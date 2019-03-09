package db

trait Queries {

  val ALARM_INSERT =
                        """
                          INSERT INTO alarm
                          (employee_id, alarm_date, status, category, description, last_update)
                          VALUES   (?, date(?),
                                   CAST(? AS Alarm_Status_Type),
                                   CAST(? AS Alarm_Category_Type),?,now()
                          )
                        """
}
