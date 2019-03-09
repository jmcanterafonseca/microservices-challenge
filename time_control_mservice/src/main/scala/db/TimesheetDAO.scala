package db

import java.sql.{Connection, PreparedStatement, ResultSet, SQLException}

import timesheet.TimesheetRecord
import utils.DateUtil

import scala.collection.mutable.ListBuffer


object TimesheetDAO extends Queries {

  def getTimesheet(employeeId: Integer, startDate: String, endDate: String): List[TimesheetRecord] = {
    var connection: Connection = null
    var ps: PreparedStatement = null
    var rs: ResultSet = null

    try {
      connection = Database.getConnection

      ps = prepareQuery(connection, employeeId, startDate, endDate)
      rs = ps.executeQuery()

      val out = ListBuffer[TimesheetRecord]()
      while (rs.next) {
        val record = TimesheetRecord(rs.getInt("employee_Id"),
          DateUtil.dateToISO8601(rs.getDate("work_date")),
          rs.getFloat("worked_hours")
        )

        out += record
      }

      out.toList
    }
    catch {
      case ex: SQLException => {
        Console.println(ex.getMessage)
        throw ex
      }
    }
    finally {
      if (connection != null)
        connection.close
      if (ps != null)
        ps.close
      if (rs != null)
        rs.close
    }
  }

  private def prepareQuery(connection: Connection,
                           employeeId: Integer,
                           startDate: String,
                           endDate: String) = {
    val ps = connection.prepareStatement(TIMESHEET_QUERY)
    ps.setInt(1, employeeId)

    ps.setString(2, startDate)
    ps.setString(3, endDate)

    ps
  }

  // hours and minutes contain the new values
  def updateWorkTime(employeeId: Integer, workDay: String, hours: Float) = {
    var connection: Connection = null
    var ps: PreparedStatement = null
    var rs: ResultSet = null

    try {
      connection = Database.getConnection

      val newRecord = TimesheetRecord(employeeId, workDay, hours)

      ps = prepareUpsert(connection, s"${employeeId}${workDay}", newRecord)

      ps.executeUpdate()

      true
    }
    catch {
      case ex: SQLException => {
        Console.println(ex.getMessage)
        throw ex
      }
    }
    finally {
      if (connection != null)
        connection.close
      if (ps != null)
        ps.close
      if (rs != null)
        rs.close
    }
  }

  private def prepareUpsert(connection: Connection,
                            id: String,
                            timesheetRecord: TimesheetRecord) = {

    val ps = connection.prepareStatement(TIMESHEET_UPSERT)

    ps.setString(1, id)
    ps.setInt(2, timesheetRecord.employeeId)
    ps.setString(3, timesheetRecord.workDate)
    ps.setFloat(4, timesheetRecord.workedHours)

    ps.setFloat(5, timesheetRecord.workedHours)
    ps.setInt(6, timesheetRecord.employeeId)
    ps.setString(7, timesheetRecord.workDate)

    ps
  }

  private def getTimesheetRecord(employeeId: Integer, workDay: String): Option[TimesheetRecord] = {
    var connection: Connection = null
    var ps: PreparedStatement = null
    var rs: ResultSet = null

    try {
      connection = Database.getConnection

      ps = prepareRecordQuery(connection, employeeId, workDay)

      rs = ps.executeQuery()

      if (rs.next)
        Some(TimesheetRecord(
          rs.getInt("employee_id"),
          rs.getString("work_date"),
          rs.getFloat("worked_hours"),
          rs.getTimestamp("last_update")
        ))
      else
        None
    }
    catch {
      case ex: SQLException => {
        Console.println(ex.getMessage)
        throw ex
      }
    }
    finally {
      if (connection != null)
        connection.close
      if (ps != null)
        ps.close
      if (rs != null)
        rs.close
    }
  }

  private def prepareRecordQuery(connection: Connection,
                                 employeeId: Integer,
                                 workDay: String) = {
    val ps = connection.prepareStatement(TIMESHEET_QUERY_ONE)
    ps.setInt(1, employeeId)

    ps.setString(2, workDay)

    ps
  }

}
