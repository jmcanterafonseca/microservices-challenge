package db

import java.sql.{Connection, PreparedStatement, ResultSet, SQLException}

import clock.PresenceInfo


object PresenceDAO extends Queries {

  // Returns empty PresenceInfo object if presence info for employee not found
  def getPresence(employeeId:Integer): Option[PresenceInfo] = {
    var connection:Connection = null
    var ps:PreparedStatement = null
    var rs:ResultSet = null

    try {
      connection = Database.getConnection

      ps = prepareQuery(connection,employeeId)
      rs = ps.executeQuery()

      if (!rs.next) {
        None
      }
      else
        Some(PresenceInfo(
          rs.getInt("employee_id"),
          rs.getString("status"),
          rs.getTimestamp("last_update"),
          rs.getString("clock_record_id")
        ))
    }
    catch {
      case ex:SQLException => {
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

  private def prepareQuery(connection:Connection, employeeId:Integer) = {
    val ps = connection.prepareStatement(PRESENCE_QUERY)
    ps.setInt(1,employeeId)

    ps
  }
}
