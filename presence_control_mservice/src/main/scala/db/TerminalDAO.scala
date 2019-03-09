package db

import java.sql.{Connection, PreparedStatement, ResultSet, SQLException}

import clock.Terminal

object TerminalDAO extends Queries {
  // None if employee is not found
  def getTerminal(terminalId:String):Option[Terminal] = {
    var connection:Connection = null
    var ps:PreparedStatement = null
    var rs:ResultSet = null

    try {
      connection = Database.getConnection

      ps = prepareQuery(connection,terminalId)
      rs = ps.executeQuery()

      if (!rs.next)
        None
      else
        Some(Terminal(
          rs.getString("id")
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

  private def prepareQuery(connection:Connection, terminalId:String) = {
    val ps = connection.prepareStatement(TERMINAL_QUERY)
    ps.setString(1,terminalId)

    ps
  }
}
