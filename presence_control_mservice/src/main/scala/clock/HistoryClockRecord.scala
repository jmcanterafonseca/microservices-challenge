package clock

import utils.DateUtil

import json.JSONSerializer.serialize

case class HistoryClockRecord(
                              id:String,
                              employeeId:Integer,
                              checkInDate:java.util.Date,
                              checkOutDate:java.util.Date,
                              eventType: String = "checkOut"
                             )
{
  def toJson() = {
    val out = Map(
      "employee_id" -> this.employeeId,
      "check_in_date" -> DateUtil.toISO8601(this.checkInDate),
      "check_out_date" -> DateUtil.toISO8601(this.checkOutDate),
      "event_type" -> eventType
    )

    serialize(out)
  }
}