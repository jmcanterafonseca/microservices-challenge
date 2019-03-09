package timesheet

import java.time.Duration

import db.{EmployeeDAO, TimesheetDAO}
import utils.DateUtil


object TimeControlManager {

  def getTimesheet(employeeId: Integer, startDate: String, endDate: String) = {
    if (EmployeeDAO.getEmployee(employeeId).isEmpty) {
      throw new NotFoundException
    }
    TimesheetDAO.getTimesheet(employeeId, startDate, endDate)
  }

  // TODO:
  def getMonthlyTimesheet(employeeId: Integer, year: Integer, month: Integer) = {
    null
  }

  // TODO:
  def getWeeklyTimesheet(employeeId: Integer, year: Integer, weekNumber: Integer) = {
    null
  }

  def accumulate(clockRecord: ClockRecord) = {
    val workedTimeRes = workDayAndTime(clockRecord)

    TimesheetDAO.updateWorkTime(clockRecord.employeeId,
      workedTimeRes._1,
      workedTimeRes._2
    )
  }

  private def workDayAndTime(clockRecord: ClockRecord): (String, Float) = {
    val checkIn = DateUtil.fromISO8601(clockRecord.checkInDate)
    val checkOut = DateUtil.fromISO8601(clockRecord.checkOutDate)

    if (checkIn.toLocalDate.isEqual(checkOut.toLocalDate)) {
      // Assumption is that check in and check out happens same day
      val workDay = DateUtil.dateToISO8601(checkIn.toLocalDate)

      val difference = Duration.between(checkIn, checkOut)

      val hours: Float = difference.toHours + difference.toMinutes / 60.0F

      val out = (workDay, hours)

      Console.println(out)

      out
    }
    else
      ("", 0.0F)
  }
}
