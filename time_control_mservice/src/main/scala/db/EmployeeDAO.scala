package db

import java.sql.{Connection, PreparedStatement, ResultSet, SQLException}

import timesheet.Employee

object EmployeeDAO extends Queries {
  // Null if employee is not found
  def getEmployee(employeeId:Integer):Option[Employee] = {
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
        Some(Employee(
          rs.getInt("id"),
          rs.getString("dni"),
          rs.getString("name"))
        )
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
    val ps = connection.prepareStatement(EMPLOYEE_QUERY)
    ps.setInt(1,employeeId)

    ps
  }
}
