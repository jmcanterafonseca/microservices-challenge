package main

import kafka.{AlarmRecordProducer, ClockEventConsumer, KafkaParams}

/**
  *
  * Server launcher
  *
  *
  *
  */
object Launcher extends Configuration  {
  def main(args:Array[String]) = {
    val kafkaBroker = System.getenv.getOrDefault(KafkaBroker, DefaultKafkaBroker)
    val kafkaParams = KafkaParams(kafkaBroker)

    AlarmRecordProducer.setParams(kafkaParams)

    ClockEventConsumer.setParams(kafkaParams)
    ClockEventConsumer.consume
  }
}
