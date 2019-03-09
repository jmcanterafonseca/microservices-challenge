package main

import db.{Database, DbParams}
import mail.MailParams
import main.Launcher.{AlarmRecipient, DefaultAlarmRecipient}

/**
  *
  * Configuration constants
  *
  *
  */
trait Configuration {
  val DbUrl = "Db_Url"
  val DbUser = "Db_User"
  var DbPwd = "Db_Pwd"

  val DefaultDbUrl = "jdbc:postgresql://localhost:5432/presence_control"
  val DefaultDbUser = "a"
  val DefaultDbPwd = "b"
  val KafkaBroker = "Kafka_Broker"
  val DefaultKafkaBroker = "localhost:9092"

  val EmailServer = "Email_Server"
  val EmailServerUser = "Email_Server_User"
  val EmailServerPass = "Email_Server_Pass"
  val AlarmRecipient = "Alarm_Recipient"

  val DefaultEmailServer = "smtp.googlemail.com"
  val DefaultEmailServerUser = "email@gmail.com"
  val DefaultEmailServerPass = "20192020"
  val DefaultAlarmRecipient = "jmcanterafonseca@gmail.com"

  def applyConf(): Unit = {
    val dbUrl = System.getenv.getOrDefault(DbUrl, DefaultDbUrl)
    val dbUser = System.getenv.getOrDefault(DbUser, DefaultDbUser)
    val dbPwd = System.getenv.getOrDefault(DbPwd, DefaultDbUser)

    Database.init(DbParams(dbUrl, dbUser, dbPwd))

    val mailServer = System.getenv.getOrDefault(EmailServer, DefaultEmailServer)
    val mailUser = System.getenv.getOrDefault(EmailServerUser, DefaultEmailServerUser)
    val mailPass = System.getenv.getOrDefault(EmailServerPass, DefaultEmailServerPass)

    mail.init(MailParams(mailServer, mailUser, mailPass))

    val emailRecipient = System.getenv.getOrDefault(AlarmRecipient, DefaultAlarmRecipient)
    AlarmHandler.init(NotifyParams(mailUser, emailRecipient))
  }
}
