package main

import db.{Database, DbParams}
import kafka.{ClockRecordProducer, KafkaParams}

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
  val DefaultEndpoint = "http://localhost:5000"
  val DefaultPort = "5000"

  val DefaultApiPath = "/clock/v1"

  val DbUrl = "Db_Url"
  val DbUser = "Db_User"
  var DbPwd = "Db_Pwd"

  val DefaultDbUrl = "jdbc:postgresql://localhost:5432/presence_control"
  val DefaultDbUser = "challenge"
  var DefaultDbPwd = "challenge"

  val KafkaBroker = "Kafka_Broker"
  val DefaultKafkaBroker = "localhost:9092"

  def applyConf() = {
    val dbUrl = System.getenv.getOrDefault(DbUrl, DefaultDbUrl)
    val dbUser = System.getenv.getOrDefault(DbUser, DefaultDbUser)
    val dbPwd = System.getenv.getOrDefault(DbPwd, DefaultDbUser)

    Database.init(DbParams(dbUrl, dbUser, dbPwd))

    val kafkaBroker = System.getenv.getOrDefault(KafkaBroker, DefaultKafkaBroker)
    ClockRecordProducer.setParams(KafkaParams(kafkaBroker))
  }
}
