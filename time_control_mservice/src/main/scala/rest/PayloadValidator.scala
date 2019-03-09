package rest

object PayloadValidator {
  def validate(employeeId: String, startDate: String, endDate: String) = {
    if (employeeId == null || employeeId.trim.length == 0) {
      throw new BadPayloadException("Employee Id shall be a string")
    }

    if (startDate == null || endDate == null) {
      throw new BadPayloadException("Start date and end date shall be present")
    }

    if (startDate.trim.length == 0 || endDate.trim.length == 0) {
      throw new BadPayloadException("Start date and end date shall be a non empty string")
    }
  }
}
