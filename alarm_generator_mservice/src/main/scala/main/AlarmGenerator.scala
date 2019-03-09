package main

import java.time.Duration

import kafka.AlarmRecordProducer
import utils.DateUtil

/**
  *
  *   Very basic alarm generator based on hardcoded rules
  *
  */
object AlarmGenerator {

  // Absence threshold in minutes
  private val ABSENCE_THRESHOLD = 30
  // Late checkout is considered from 20:00 hours
  private val LATE_CHECK_OUT = 2
  // Late checkin is considered from 11:00 hours
  private val LATE_CHECK_IN = 11

  def processEvent(clockEvent: ClockEvent) = {
    clockEvent.eventType match {
      case "checkOut" => processCheckOutEvent(clockEvent)
      case "checkIn" => processCheckInEvent(clockEvent)
      case _ => None
    }

    true
  }

  private def processCheckOutEvent(clockEvent: ClockEvent) = {
    val checkOut = DateUtil.fromISO8601(clockEvent.checkOutDate)
    val checkOutTime = checkOut.toLocalTime

    // Late checkouts generate an alarm
    // TODO: This could depend on time shifts, etc
    if (checkOutTime.getHour >= LATE_CHECK_OUT) {
      val alarm = Alarm(clockEvent.employeeId,
        DateUtil.dateToISO8601(checkOut.toLocalDate),
        "late_checkout",
        s"Employee has checked out at ${checkOutTime}"
      )
      AlarmRecordProducer.sendAlarmRecord(alarm)
    }

  }

  private def processCheckInEvent(clockEvent: ClockEvent) = {
    // If the checkIn is done the same day as the previous check Out
    // Check number of minutes absent
    // And if it is bigger than 30 minutes generate an alarm of temporal absence
    val previousCheckOut = DateUtil.fromISO8601(clockEvent.checkOutDate)
    val checkIn = DateUtil.fromISO8601(clockEvent.checkInDate)

    // TODO: Check this against shifts, vacations, etc.
    if (previousCheckOut.toLocalDate.isEqual(checkIn.toLocalDate)) {
      val difference = Duration.between(previousCheckOut, checkIn)

      val minutes = difference.toHours * 60 + difference.toMinutes

      if (minutes > ABSENCE_THRESHOLD) {
        val alarm = Alarm(
          clockEvent.employeeId,
          DateUtil.dateToISO8601(checkIn.toLocalDate),
          "temporal_absence",
          s"Employee has been absent for ${minutes} minutes"
        )

        AlarmRecordProducer.sendAlarmRecord(alarm)
      }
    }
    else {
      // We assume this is the first check in of the day
      val checkInTime = checkIn.toLocalTime

      if (checkInTime.getHour >= LATE_CHECK_IN) {
        val alarm = Alarm(
          clockEvent.employeeId,
          DateUtil.dateToISO8601(checkIn.toLocalDate),
          "late_check_in",
          s"Employee has checked in first at ${checkInTime}"
        )

        AlarmRecordProducer.sendAlarmRecord(alarm)
      }

      // Now generating alarms for day absence
      var currentDate = previousCheckOut.toLocalDate.plusDays(1)
      val endDate = checkIn.toLocalDate
      while (currentDate.isBefore(endDate)) {
        // TODO: Check this against time shifts, vacations, etc.
        if (currentDate.getDayOfWeek.getValue <= 6) {
          val alarm = Alarm(
            clockEvent.employeeId,
            DateUtil.dateToISO8601(currentDate),
            "day_absence",
            s"Employee was absent on ${currentDate}"
          )

          AlarmRecordProducer.sendAlarmRecord(alarm)

          currentDate = currentDate.plusDays(1)
        }
      }
    }
  }
}
