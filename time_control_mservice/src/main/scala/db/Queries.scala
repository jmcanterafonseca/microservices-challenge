package db

trait Queries {
  val TIMESHEET_QUERY =
                       """
                          SELECT employee_id, work_date, worked_hours
                          FROM timesheet_record
                          WHERE employee_id=?
                          AND work_date>=date(?)
                          AND work_date<=date(?)
                          ORDER BY work_date DESC
                       """

  val EMPLOYEE_QUERY =
                       """
                          SELECT * FROM employee WHERE id=?
                       """

  val TIMESHEET_UPSERT =
                        """
                          INSERT INTO timesheet_record
                          (id, employee_id, work_date, worked_hours, last_update)
                          VALUES (?,?,date(?),?,now())
                          ON CONFLICT (id) DO
                              UPDATE
                                SET worked_hours   = timesheet_record.worked_hours + ?,
                                    last_update=now()
                             WHERE timesheet_record.employee_id=? AND timesheet_record.work_date=date(?)
                        """

  val TIMESHEET_QUERY_ONE =
                          """
                            SELECT employee_id,
                            to_char(work_date,'YYYY-MM-DD') as work_date,
                            worked_hours,worked_minutes,
                            last_update
                            FROM timesheet_record
                            WHERE employee_id=? AND work_date=date(?)
                        """
}
