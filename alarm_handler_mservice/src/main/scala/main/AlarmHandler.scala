package main

import db.AlarmDAO
import mail._

import scala.util.control.NonFatal

object AlarmHandler {

  private var notifyParams:NotifyParams = null

  def handleAlarm(alarm:Alarm) = {
    sendAlarm(alarm)

    insertNewAlarm(alarm)
  }

  def insertNewAlarm(alarm: Alarm) = {
    AlarmDAO.add(alarm)
  }

  def sendAlarm(alarm:Alarm) = {
    try {
      val message = s"Employee: ${alarm.employeeId}" +
        s"\nAlarm Category: ${alarm.category}" +
        s"\nDate: ${alarm.date}" +
        s"\nDescription: ${alarm.description}"

      send a new Mail(
        from = (notifyParams.emailFrom, "Alarms"),
        to = notifyParams.emailRecipient,
        subject = "Time Control Alarm",
        message = message
      )

      true
    }
    catch {
      case NonFatal(e) => {
        e.printStackTrace(System.out)
        false
      }
    }
  }

  def init(notifyParams:NotifyParams) = {
    this.notifyParams = notifyParams
  }
}
