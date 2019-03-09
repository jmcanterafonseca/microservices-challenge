package kafka

import java.util.Properties

import clock.HistoryClockRecord
import org.apache.kafka.clients.producer.{Callback, KafkaProducer, ProducerRecord, RecordMetadata}

import scala.util.control.NonFatal

object ClockRecordProducer {
  private lazy val producer = new KafkaProducer[String, String](props)
  private val props: Properties = new Properties
  private val KAFKA_TOPIC_NAME = "clockevents2"
  private var kafkaParams: KafkaParams = null

  def setParams(params: KafkaParams) = {
    kafkaParams = params

    props.put("bootstrap.servers", kafkaParams.brokerEndpoint)
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  }

  def sendClockRecord(record: HistoryClockRecord) = {
    val kafkaRecord = new ProducerRecord[String, String](KAFKA_TOPIC_NAME, record.id, record.toJson())

    try {
      producer.send(kafkaRecord, new Callback {
        override def onCompletion(metadata: RecordMetadata, exception: Exception): Unit = {
          if (exception == null) {
            Console.println(s"Record sent to Kafka topic: ${record.eventType}")
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
