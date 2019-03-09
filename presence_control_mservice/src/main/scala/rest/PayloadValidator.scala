package rest

object PayloadValidator {
  def validateCheckInOut(data:Map[String,Any]) = {
    val employeeId = data.get("employeeId")

    employeeId match {
      case None => throw new BadPayloadException("Employee Id not present")
      case Some(a) => {
        if (!a.isInstanceOf[Long])
          throw new BadPayloadException("Employee Id shall be an integer number")
      }
    }

    val terminalId = data.get("terminalId")

    terminalId match {
      case None => throw new BadPayloadException("Terminal Id not present")
      case Some(a) => {
        if (!a.isInstanceOf[String])
        throw new BadPayloadException("Terminal Id shall be a string")
      }
    }
  }

  def validateEmployeeId(employeeId:String) = {
    if (employeeId == null || employeeId.trim.length == 0) {
      throw new BadPayloadException("Employee Id shall be a string")
    }
  }

  def validateHistoryParams(employeeId: String, startDate:String, endDate:String) = {
    validateEmployeeId(employeeId)

    if (startDate == null || endDate == null) {
      throw new BadPayloadException("Start date and end date shall be present")
    }

    if (startDate.trim.length == 0 || endDate.trim.length == 0) {
      throw new BadPayloadException("Start date and end date shall be a non empty string")
    }
  }
}
