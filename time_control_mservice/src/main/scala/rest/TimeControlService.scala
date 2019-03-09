package rest

import java.sql.SQLException

import javax.servlet.ServletConfig
import json.JSONSerializer.serialize
import main.Configuration
import org.scalatra._
import timesheet.{NotFoundException, TimeControlManager}

import scala.collection.mutable.ListBuffer
import scala.util.control.NonFatal


/**
  *
  * Root class that offers access to the different resources
  *
  *
  */
class TimeControlService extends ScalatraServlet with Configuration {
  private val Base = DefaultApiPath

  private val JSON_MIME_TYPE = "application/json"

  override def init(servletConfig: ServletConfig) = {
    super.init(servletConfig)
  }

  private def handleBadPayload(ex: Exception) = {
    BadRequest(serialize(Map("error" -> ex.getMessage)))
  }

  private def handleSQLException(ex: SQLException) = {
    Console.println(ex.getMessage)
    ServiceUnavailable(serialize(Map("error" -> s"DB Error: ${ex.getSQLState}")))
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

  get(s"${Base}/timesheet/:employeeId") {
    try {
      val employeeId = params("employeeId")
      val startDate = request.getParameter("start_date")
      val endDate = request.getParameter("end_date")

      PayloadValidator.validate(employeeId, startDate, endDate)

      val empNumber = employeeId.toInt

      val timesheetData = TimeControlManager.getTimesheet(empNumber, startDate, endDate)

      var outPayload = ListBuffer[Map[String, Any]]()

      for (record <- timesheetData) {
        val recordMap = Map(
          "workDate" -> record.workDate,
          "workedHours" -> record.workedHours
        )

        outPayload += recordMap
      }

      Ok(serialize(
        Map(
          "employeeId" -> empNumber,
          "timesheet" -> outPayload.toList)
      ))

    }
    catch {
      case ex: SQLException => handleSQLException(ex)
      case _: NotFoundException => NotFound(serialize(Map("error" -> "Employee not found")))
      case ex: NumberFormatException => handleBadPayload(ex)
      case ex: BadPayloadException => handleBadPayload(ex)
      case NonFatal(t) => handleNonFatal(t)
    }
  }
}
