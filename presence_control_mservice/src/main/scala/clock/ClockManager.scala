package clock

import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

import db.{ClockRecordDAO, EmployeeDAO, PresenceDAO, TerminalDAO}
import kafka.ClockRecordProducer


object ClockManager {

  def checkIn(employeeId: Integer, terminalId: String): Boolean = {
    val employee = EmployeeDAO.getEmployee(employeeId)
    if (employee.isEmpty) {
      throw new NotFoundException();
    }

    val terminal = TerminalDAO.getTerminal(terminalId)
    if (terminal.isEmpty) {
      throw new NotFoundException();
    }

    val presence = PresenceDAO.getPresence(employeeId)
    val isOut = presence.isEmpty || (!presence.isEmpty && presence.get.status == "out")

    if (isOut) {
      val recordId = generateId(employeeId)

      val record = ClockRecord(
        recordId,
        employeeId,
        terminalId,
        presence.isEmpty
      )

      val result = ClockRecordDAO.addCheckIn(record)

      // Generating the Kafka event record concerning check in
      if (!presence.isEmpty) {
        val previousCheckout = presence.get.lastUpdate
        val finalClockRecord = ClockRecordDAO.get(recordId)
        val kafkaRecord = HistoryClockRecord(recordId,
                                              employeeId,
                                              finalClockRecord.checkInDate,
                                              previousCheckout,
                                              "checkIn")

        ClockRecordProducer.sendClockRecord(kafkaRecord)
      }

      result
    }
    else {
      throw new StatusException("Employee is already in")
    }
  }

  private def generateId(employeeId: Integer) = {
    val date = OffsetDateTime.now

    val formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")

    s"${employeeId}${date.format(formatter)}"
  }

  def checkOut(employeeId: Integer, terminalId: String):Boolean = {
    val employee = EmployeeDAO.getEmployee(employeeId)
    if (employee.isEmpty) {
      throw new NotFoundException();
    }

    val terminal = TerminalDAO.getTerminal(terminalId)
    if (terminal.isEmpty) {
      throw new NotFoundException();
    }

    val presence = PresenceDAO.getPresence(employeeId)
    val isIn = (!presence.isEmpty && presence.get.status == "in")

    if (isIn) {
      val recordId = presence.get.clockRecordId

      val record = ClockRecord(recordId, employeeId, terminalId)

      // TODO: Are we doing here a query that could be saved?
      if (ClockRecordDAO.addCheckOut(record)) {
        val finalRecord = ClockRecordDAO.get(recordId)
        ClockRecordProducer.sendClockRecord(finalRecord)
        true
      }
      else
        false
    }
    else {
      throw new StatusException("Employee is already out")
    }
  }

  def getPresence(employeeId: Integer): PresenceInfo = {
    val employeeInfo = EmployeeDAO.getEmployee(employeeId)

    if (!employeeInfo.isEmpty) {
      PresenceDAO.getPresence(employeeId).get
    }
    else {
      throw new NotFoundException();
    }
  }

  def getHistory(employeeId:Integer,startDate:String,endDate:String) = {
    val employeeInfo = EmployeeDAO.getEmployee(employeeId)

    if (!employeeInfo.isEmpty) {
      ClockRecordDAO.find(employeeId,startDate,endDate)
    }
    else {
      throw new NotFoundException();
    }
  }
}
