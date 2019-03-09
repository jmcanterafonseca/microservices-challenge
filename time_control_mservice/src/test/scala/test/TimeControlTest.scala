package test

import java.time.LocalDate

import main.Configuration
import org.scalatest.FunSuiteLike
import org.scalatra.test.scalatest.ScalatraSuite
import rest.TimeControlService
import utils.{DateUtil, ParserUtil}


class TimeControlTest extends ScalatraSuite with FunSuiteLike with Configuration {

  addServlet(classOf[TimeControlService], "/*")

  // We need to do this here as the Scalatra Bootstrap will not execute when running tests
  applyConf()

  val JSON_MIME_TYPE =  Map("Content-Type" -> "application/json")

  test("Timesheet employee") {
    val today = DateUtil.dateToISO8601(LocalDate.now())

    get(s"/timecontrol/v1/timesheet/4?start_date=${today}&end_date=${today}") {
      status should equal(200)

      Console.println (body)

      val out = ParserUtil.parseObj(body)
      val employeeId = out("employeeId").asInstanceOf[Long]

      employeeId should equal (4)
    }
  }

  test("Timesheet employee does not exist") {
    val today = DateUtil.dateToISO8601(LocalDate.now())

    get(s"/timecontrol/v1/timesheet/9999?start_date=${today}&end_date=${today}") {
      status should equal(404)
    }
  }

}
