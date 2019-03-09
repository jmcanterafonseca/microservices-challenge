package db

import java.sql.{Connection, PreparedStatement, ResultSet, SQLException}

import clock.{ClockRecord, HistoryClockRecord, NotFoundException}

import scala.collection.mutable.ListBuffer

object ClockRecordDAO extends Queries {
  def addCheckIn(record: ClockRecord): Boolean = {
    var connection: Connection = null
    var presenceSt: PreparedStatement = null
    var clockRecordSt: PreparedStatement = null

    try {
      connection = Database.getConnection
      connection.setAutoCommit(false)


      clockRecordSt = prepareCheckInSt(connection, record)
      val result = clockRecordSt.executeUpdate()

      if (result == 1) {
        presenceSt = preparePresenceRecordSt(connection, record)
        val result2 = presenceSt.executeUpdate()
        if (result2 == 1) {
          connection.commit()
          true
        }
        else {
          connection.rollback()

          false
        }
      }
      else {
        connection.rollback()
        false
      }

    }
    catch {
      case ex: SQLException => {
        if (connection != null) connection.rollback()
        throw ex
      }
    }
    finally {
      if (connection != null) {
        connection.setAutoCommit(true)
        connection.close
      }

      if (presenceSt != null)
        presenceSt.close

      if (clockRecordSt != null)
        clockRecordSt.close
    }
  }

  private def preparePresenceRecordSt(connection: Connection, record: ClockRecord) = {
    if (record.isFirst)
      prepareInsertPresenceSt(connection, record)
    else
      prepareUpdatePresenceSt(connection, record, "in")
  }

  private def prepareInsertPresenceSt(connection: Connection, record: ClockRecord) = {
    val ps = connection.prepareStatement(PRESENCE_INSERT)
    ps.setInt(1, record.employeeId)
    ps.setObject(2, "in")
    ps.setString(3, record.id)

    ps
  }

  private def prepareCheckInSt(connection: Connection, record: ClockRecord): PreparedStatement = {
    val ps = connection.prepareStatement(CHECKIN_UPDATE)
    ps.setString(1, record.id)
    ps.setInt(2, record.employeeId)
    ps.setString(3, record.terminal)

    ps
  }

  def addCheckOut(record: ClockRecord): Boolean = {
    var connection: Connection = null
    var presenceSt: PreparedStatement = null
    var clockRecordSt: PreparedStatement = null

    try {
      connection = Database.getConnection
      connection.setAutoCommit(false)


      clockRecordSt = prepareCheckOutSt(connection, record)
      val result = clockRecordSt.executeUpdate()

      if (result == 1) {
        presenceSt = prepareUpdatePresenceSt(connection, record, "out")
        val result2 = presenceSt.executeUpdate()
        if (result2 == 1) {
          connection.commit()
          true
        }
        else {
          connection.rollback()

          false
        }
      }
      else {
        connection.rollback()
        false
      }

    }
    catch {
      case ex: SQLException => {
        if (connection != null) connection.rollback()
        throw ex
      }
    }
    finally {
      if (connection != null) {
        connection.setAutoCommit(true)
        connection.close
      }

      if (presenceSt != null)
        presenceSt.close

      if (clockRecordSt != null)
        clockRecordSt.close
    }
  }

  private def prepareUpdatePresenceSt(connection: Connection,
                                      record: ClockRecord,
                                      status: String) = {
    val ps = connection.prepareStatement(PRESENCE_UPDATE)
    ps.setObject(1, status)
    ps.setString(2, record.id)
    ps.setInt(3, record.employeeId)

    ps
  }

  private def prepareCheckOutSt(connection: Connection, record: ClockRecord) = {
    val ps = connection.prepareStatement(CHECK_OUT_UPDATE)
    ps.setString(1, record.terminal)
    ps.setString(2, record.id)

    ps
  }

  def find(employeeId: Integer, startDate: String, endDate: String): List[HistoryClockRecord] = {
    var connection: Connection = null
    var ps: PreparedStatement = null
    var rs: ResultSet = null

    try {
      connection = Database.getConnection

      ps = prepareHistoryQuery(connection, employeeId, startDate, endDate)
      rs = ps.executeQuery()

      val out = ListBuffer[HistoryClockRecord]()
      while (rs.next) {
        val record = HistoryClockRecord(
          rs.getString("id"),
          rs.getInt("employee_id"),
          rs.getTimestamp("check_in_date"),
          rs.getTimestamp("check_out_date")
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

  private def prepareHistoryQuery(connection: Connection,
                                  employeeId: Integer,
                                  startDate: String,
                                  endDate: String) = {

    val ps = connection.prepareStatement(HISTORY_QUERY)
    ps.setInt(1, employeeId)

    ps.setString(2, startDate)
    ps.setString(3, endDate)

    ps
  }

  def get(id: String): HistoryClockRecord = {
    var connection: Connection = null
    var ps: PreparedStatement = null
    var rs: ResultSet = null

    try {
      connection = Database.getConnection

      ps = prepareRecordQuery(connection, id)
      rs = ps.executeQuery()

      if (rs.next) {
        val record = HistoryClockRecord(
          rs.getString("id"),
          rs.getInt("employee_id"),
          rs.getTimestamp("check_in_date"),
          rs.getTimestamp("check_out_date")
        )
        record
      }
      else {
        throw new NotFoundException();
      }
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
                                 id: String
                                ) = {

    val ps = connection.prepareStatement(CLOCK_RECORD_QUERY)

    ps.setString(1, id)

    ps
  }
}
