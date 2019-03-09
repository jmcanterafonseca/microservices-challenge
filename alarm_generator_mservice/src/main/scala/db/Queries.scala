package db

trait Queries {

  val ALARM_INSERT =
                        """
                          INSERT INTO alarm
                          (employee_id, work_date, worked_hours, last_update)
                          VALUES (?,?,date(?),?,now())
                          ON CONFLICT (id) DO
                              UPDATE
                                SET worked_hours   = timesheet.worked_hours + ?,
                                    last_update=now()
                             WHERE timesheet.employee_id=? AND timesheet.work_date=date(?)
                        """
}
