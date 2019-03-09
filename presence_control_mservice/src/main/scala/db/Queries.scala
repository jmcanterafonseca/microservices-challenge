package db

trait Queries {
  val PRESENCE_QUERY = """
                          SELECT *
                          FROM presence
                          WHERE employee_id=?
                       """

  val PRESENCE_UPDATE = """
                          UPDATE presence
                          SET status=CAST(? AS Status_Type),
                          last_update=now(),
                          clock_record_id=?
                          WHERE employee_id=?
                        """

  val PRESENCE_INSERT = """
                          INSERT INTO presence(employee_id,status,clock_record_id,last_update)
                          VALUES (?,CAST(? AS Status_Type),?, now())
                        """

  val CHECKIN_UPDATE = """
                          INSERT INTO clock_record (id,employee_id,check_in_terminal,check_in_date)
                          VALUES(?,?,?,now())
                       """

  val CHECK_OUT_UPDATE = """
                          UPDATE clock_record
                          SET check_out_date=now(),check_out_terminal=?
                          WHERE id=?
                         """

  val EMPLOYEE_QUERY =
                       """
                          SELECT * FROM employee WHERE id=?
                       """

  val TERMINAL_QUERY = """
                          SELECT * FROM terminal WHERE id=?
                       """

  val HISTORY_QUERY =
                        """
                            SELECT id, employee_id, check_in_date, check_out_date
                            FROM clock_record
                            WHERE employee_id=?
                            AND date(check_in_date) >= date(?)
                            AND date(check_in_date) <= date(?)
                            ORDER BY check_in_date DESC
                        """

  val CLOCK_RECORD_QUERY =
                            """
                              SELECT id, employee_id, check_in_date, check_out_date
                              FROM clock_record
                              WHERE id=?
                            """
}
