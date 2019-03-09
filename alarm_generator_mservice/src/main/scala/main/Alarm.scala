package main

import json.JSONSerializer.serialize

case class Alarm(employeeId:Integer, date:String, category:String, description:String) {
  def toJson() = {
    val m = Map(
      "employee_id" -> employeeId,
      "date" -> date,
      "category" -> category,
      "description" -> description
    )

    serialize(m)
  }
}
