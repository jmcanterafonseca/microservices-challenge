package test

import java.time.LocalDate

import json.JSONSerializer.serialize
import main.Configuration
import org.scalatest.FunSuiteLike
import org.scalatra.test.scalatest.ScalatraSuite
import rest.ClockService
import utils.{DateUtil, ParserUtil}

class PresenceControlTest extends ScalatraSuite with FunSuiteLike with Configuration {

  addServlet(classOf[ClockService], "/*")

  // We need to do this here as the Scalatra Bootstrap will not execute when running tests
  applyConf()

  val JSON_MIME_TYPE =  Map("Content-Type" -> "application/json")

  val checkData = Map(
    "employeeId" -> 4,
    "terminalId" -> "A2378KJH"
  )

  test("Check in") {
    post("/clock/v1/check_in",
          serialize(checkData),
          JSON_MIME_TYPE) {

      status should equal(204)

    }
  }

  test("Double Check in. Error") {
    post("/clock/v1/check_in",
          serialize(checkData),
          JSON_MIME_TYPE) {

      status should equal(409)

    }
  }

  test("Check out") {
    post("/clock/v1/check_out",
          serialize(checkData),
          JSON_MIME_TYPE) {

      status should equal(204)

    }
  }

  test("Double Check out. Error") {
    post("/clock/v1/check_out",
          serialize(checkData),
          JSON_MIME_TYPE) {

      status should equal(409)

    }
  }

  test("Check in employee does not exist") {
    val checkData = Map(
      "employeeId" -> 9999,
      "terminalId" -> "A2378KJH"
    )

    post("/clock/v1/check_in",
          serialize(checkData),
          JSON_MIME_TYPE) {

      status should equal(404)

    }
  }

  test("Check out employee does not exist") {
    val checkData = Map(
      "employeeId" -> 9999,
      "terminalId" -> "A2378KJH"
    )

    post("/clock/v1/check_out",
      serialize(checkData),
      JSON_MIME_TYPE) {

      status should equal(404)

    }
  }

  test("Presence employee") {
    get("/clock/v1/presence/4") {
      status should equal(200)
      ParserUtil.parseObj(body)("status") should equal("out")
    }
  }

  test("Presence employee does not exist") {
    get("/clock/v1/presence/9999") {
      status should equal(404)
    }
  }

  test("History employee") {
    val today = DateUtil.dateToISO8601(LocalDate.now())

    get(s"/clock/v1/history/4?start_date=${today}&end_date=${today}") {
      status should equal(200)

      val out = ParserUtil.parseObj(body)
      val history = out("history").asInstanceOf[List[Map[String,Any]]]

      val checkout = DateUtil.fromISO8601(
                      history(history.size - 1)("checkOutDate").asInstanceOf[String])

      DateUtil.dateToISO8601(checkout.toLocalDate) should equal (today)
    }
  }

  test("History employee does not exist") {
    val today = DateUtil.dateToISO8601(LocalDate.now())

    get(s"/clock/v1/history/9999?start_date=${today}&end_date=${today}") {
      status should equal(404)
    }
  }
}
