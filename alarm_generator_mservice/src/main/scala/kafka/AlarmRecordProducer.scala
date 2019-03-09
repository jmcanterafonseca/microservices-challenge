package kafka

import java.util.Properties

import org.apache.kafka.clients.producer.{Callback, KafkaProducer, ProducerRecord, RecordMetadata}

import main.Alarm

import scala.util.control.NonFatal

object AlarmRecordProducer {
  private lazy val producer = new KafkaProducer[String, String](props)
  private val props: Properties = new Properties
  private val KAFKA_TOPIC_NAME = "alarms"
  private var kafkaParams: KafkaParams = null

  def setParams(params: KafkaParams) = {
    kafkaParams = params

    props.put("bootstrap.servers", kafkaParams.brokerEndpoint)
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  }

  def sendAlarmRecord(record: Alarm) = {
    val kafkaRecord = new ProducerRecord[String, String](
                            KAFKA_TOPIC_NAME,
                            s"${record.employeeId}_${record.date}",
                            record.toJson()
    )

    try {
      producer.send(kafkaRecord, new Callback {
        override def onCompletion(metadata: RecordMetadata, exception: Exception): Unit = {
          if (exception == null) {
            Console.println("Alarm Record sent to Kafka topic")
          }
          else {
            exception.printStackTrace(System.out)
          }
        }
      })
    }
    catch {
      case NonFatal(t) => t.printStackTrace(System.out)
    }
  }
}
