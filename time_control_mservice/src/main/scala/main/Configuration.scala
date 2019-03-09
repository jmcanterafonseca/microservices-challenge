package main

import db.{Database, DbParams}

/**
  *
  * Configuration constants
  *
  *
  */
trait Configuration {
  val Port = "Service_Port"
  val ApiPath = "Api_Path"
  val Endpoint = "Endpoint"

  val DefaultEndpoint = "http://localhost:6000"
  val DefaultPort = "6000"

  val DefaultApiPath = "/timecontrol/v1"

  val DbUrl = "Db_Url"
  val DbUser = "Db_User"
  var DbPwd = "Db_Pwd"

  val DefaultDbUrl = "jdbc:postgresql://localhost:5432/presence_control"
  val DefaultDbUser = "a"
  val DefaultDbPwd = "b"

  val KafkaBroker = "Kafka_Broker"
  val DefaultKafkaBroker = "localhost:9092"

  def applyConf(): Unit = {
    val dbUrl = System.getenv.getOrDefault(DbUrl,DefaultDbUrl)
    val dbUser = System.getenv.getOrDefault(DbUser,DefaultDbUser)
    val dbPwd = System.getenv.getOrDefault(DbPwd,DefaultDbUser)

    Database.init(DbParams(dbUrl,dbUser,dbPwd))
  }
}
