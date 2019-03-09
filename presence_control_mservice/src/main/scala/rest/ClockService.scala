package rest

import java.sql.SQLException

import clock.{ClockManager, NotFoundException, StatusException}
import javax.servlet.ServletConfig
import json.JSONSerializer.serialize
import main.Configuration
import org.scalatra._
import utils.{DateUtil, ParserUtil}

import scala.collection.mutable.ListBuffer
import scala.util.control.NonFatal


/**
  *
  * Root class that offers access to the different resources
  *
  *
  */
class ClockService extends ScalatraServlet with Configuration {
  private val Base = DefaultApiPath

  private val JSON_MIME_TYPE = "application/json"

  override def init(servletConfig: ServletConfig) = {
    super.init(servletConfig)
  }

  private def handleSQLException(ex: SQLException) = {
    Console.println(s"SQL Exception: ${ex.getMessage}")
    ServiceUnavailable(serialize(Map("error" -> s"DB Error: ${ex.getSQLState}")))
  }

  private def handleBadPayload(ex: Exception) = {
    BadRequest(serialize(Map("error" -> ex.getMessage)))
  }

  private def handleNonFatal(t: Throwable) = {
    Console.println("Non Fatal Exception")
    t.printStackTrace(System.out)
    BadRequest(serialize(Map("error" -> t.getClass.getName)))
  }

  before() {
    val requestContentType = request.header("Content-Type")
    if (!requestContentType.isEmpty && requestContentType.get != JSON_MIME_TYPE)
      halt(415)
  }

  after() {
    contentType = JSON_MIME_TYPE
  }

  get(s"${Base}/presence/:employeeId") {
    try {
      val employeeId = params("employeeId")

      PayloadValidator.validateEmployeeId(employeeId)

      val empNumber = employeeId.toInt

      val presenceInfo = ClockManager.getPresence(empNumber)

      val presenceMap = Map(
        "employeeId" -> presenceInfo.employeeId,
        "status" -> presenceInfo.status,
        "lastUpdate" -> DateUtil.toISO8601(presenceInfo.lastUpdate)
      )

      Ok(serialize(presenceMap))

    }
    catch {
      case ex: SQLException => handleSQLException(ex)
      case _: NotFoundException => NotFound(serialize(Map("error" -> "Employee not found")))
      case ex: NumberFormatException => handleBadPayload(ex)
      case ex: BadPayloadException => handleBadPayload(ex)
      case NonFatal(t) => handleNonFatal(t)
    }
  }


  get(s"${Base}/history/:employeeId") {
    try {
      val employeeId = params("employeeId")

      val startDate = request.getParameter("start_date")
      val endDate = request.getParameter("end_date")

      PayloadValidator.validateHistoryParams(employeeId, startDate, endDate)

      val empNumber = employeeId.toInt

      val historyData = ClockManager.getHistory(empNumber, startDate, endDate)

      val payLoadOut = ListBuffer[Map[String, Any]]()
      for (record <- historyData) {
        payLoadOut += Map(
          "checkInDate" -> DateUtil.toISO8601(record.checkInDate),
          "checkOutDate" -> DateUtil.toISO8601(record.checkOutDate)
        )
      }
      Ok(serialize(Map(
        "history" -> payLoadOut.toList,
        "employeeId" -> employeeId
      )))
    }
    catch {
      case ex: SQLException => handleSQLException(ex)
      case _: NotFoundException => NotFound(serialize(Map("error" -> "Employee not found")))
      case ex: NumberFormatException => handleBadPayload(ex)
      case ex: BadPayloadException => handleBadPayload(ex)
      case NonFatal(t) => handleNonFatal(t)
    }
  }


  post(s"${Base}/check_in") {
    try {
      val data = ParserUtil.parse(request.body).asInstanceOf[Map[String, Any]]

      PayloadValidator.validateCheckInOut(data)

      val employeeId = new Integer(data("employeeId").asInstanceOf[Long].intValue())
      val terminalId = data("terminalId").asInstanceOf[String]

      NoContent()

      if (ClockManager.checkIn(employeeId, terminalId))
        NoContent()
      else
        Conflict()
    }
    catch {
      case ex: StatusException => Conflict(serialize(Map("error" -> ex.getMessage)))
      case ex: SQLException => handleSQLException(ex)
      case ex: BadPayloadException => handleBadPayload(ex)
      case ex: NotFoundException => NotFound(serialize(Map("error" -> "Employee or terminal not found")))
      case NonFatal(t) => handleNonFatal(t)
    }
  }


  post(s"${Base}/check_out") {
    try {
      val data = ParserUtil.parse(request.body).asInstanceOf[Map[String, Any]]

      PayloadValidator.validateCheckInOut(data)

      val employeeId = new Integer(data("employeeId").asInstanceOf[Long].intValue())
      val terminalId = data("terminalId").asInstanceOf[String]

      if (ClockManager.checkOut(employeeId, terminalId))
        NoContent()
      else
        Conflict()
    }
    catch {
      case ex: StatusException => Conflict(serialize(Map("error" -> ex.getMessage)))
      case ex: SQLException => handleSQLException(ex)
      case ex: BadPayloadException => handleBadPayload(ex)
      case ex: NotFoundException => NotFound(serialize(Map("error" -> "Employee or terminal not found")))
      case NonFatal(t) => handleNonFatal(t)
    }
  }

}
