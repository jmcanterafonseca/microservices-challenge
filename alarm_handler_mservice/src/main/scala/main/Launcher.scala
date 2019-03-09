package main

import kafka.{AlarmConsumer, KafkaParams}

/**
  *
  * Server launcher
  *
  *
  *
  */
object Launcher extends Configuration  {
  def main(args:Array[String]) = {
    applyConf

    val kafkaBroker = System.getenv.getOrDefault(KafkaBroker, DefaultKafkaBroker)
    AlarmConsumer.setParams(KafkaParams(kafkaBroker))

    AlarmConsumer.consume
  }
}
