package timesheet

case class TimesheetRecord(employeeId:Integer,
                           workDate:String,
                           workedHours:Float,
                           lastUpdate:java.sql.Timestamp=null
                          )
