package db

import java.sql.{Connection, PreparedStatement, ResultSet, SQLException}

import main.Alarm


object AlarmDAO extends Queries {
  def add(alarm: Alarm) = {
    var connection: Connection = null
    var ps: PreparedStatement = null
    var rs: ResultSet = null

    try {
      connection = Database.getConnection

      ps = prepareStatement(connection, alarm)

      ps.executeUpdate == 1
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

  private def prepareStatement(connection: Connection, alarm: Alarm) = {
    val ps = connection.prepareStatement(ALARM_INSERT)

    ps.setInt(1, alarm.employeeId)
    ps.setString(2, alarm.date)
    ps.setString(3, "new")
    ps.setString(4, alarm.category)
    ps.setString(5, alarm.description)

    ps
  }
}
