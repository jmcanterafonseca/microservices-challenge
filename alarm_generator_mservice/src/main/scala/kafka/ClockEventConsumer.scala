package kafka

import java.time.Duration
import java.util
import java.util.Properties

import main.{AlarmGenerator, ClockEvent}
import org.apache.kafka.clients.consumer.{ConsumerRecords, KafkaConsumer, OffsetAndMetadata, OffsetCommitCallback}
import org.apache.kafka.common.TopicPartition
import org.apache.kafka.common.errors.WakeupException
import utils.ParserUtil

import scala.util.control.NonFatal

object ClockEventConsumer {
  private lazy val consumer = new KafkaConsumer[String, String](props)
  private val props: Properties = new Properties

  private val KAFKA_TOPIC_NAME = "clockevents2"

  private var kafkaParams: KafkaParams = null

  def setParams(params: KafkaParams) = {
    kafkaParams = params

    props.put("bootstrap.servers", kafkaParams.brokerEndpoint)
    props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    props.put("enable.auto.commit", "false")
    props.put("group.id", "Alarms")
  }


  def consume() = {
    consumer.subscribe(java.util.Collections.singletonList(KAFKA_TOPIC_NAME))
    var loop = true

    val offsetsToCommit = new java.util.HashMap[TopicPartition, OffsetAndMetadata]()

    while (loop) {
      try {
        val records: ConsumerRecords[String, String] = consumer.poll(Duration.ofMillis(100))

        val it = records.iterator

        offsetsToCommit.clear()

        while (it.hasNext) {
          Console.println("Processing record from Kafka")

          val r = it.next
          val data = r.value
          val clockEvent = parseRecord(data)

          if (clockEvent != null) {
            if (AlarmGenerator.processEvent(clockEvent)) {
              offsetsToCommit.put(
                new TopicPartition(r.topic, r.partition),
                new OffsetAndMetadata(r.offset + 1, "")
              )
            }
          }
        }

        if (offsetsToCommit.size > 0)
          consumer.commitAsync(offsetsToCommit, new OffsetCommitCallback {
            override def onComplete(offsets: util.Map[TopicPartition, OffsetAndMetadata], exception: Exception): Unit = {
              if (exception == null) {
                Console.println("Record data consumed")
              }
              else {
                exception.printStackTrace(System.out)
              }
            }
          })

      }
      catch {
        case NonFatal(t) => t.printStackTrace(System.out)
        case ex: WakeupException => {
          loop = false
          try {
            consumer.commitSync(offsetsToCommit)
          }
          finally {
            consumer.close()
          }
        }
      }
    }
  }

  private def parseRecord(data: String) = {
    try {
      val record = ParserUtil.parse(data).asInstanceOf[Map[String, Any]]

      ClockEvent(
        record("employee_id").asInstanceOf[Long].intValue(),
        record("event_type").asInstanceOf[String],
        record("check_in_date").asInstanceOf[String],
        record("check_out_date").asInstanceOf[String]
      )
    }
    catch {
      case NonFatal(t) => {
        Console.println("Wrong clock record")
        t.printStackTrace(System.out)
        null
      }
    }
  }

}
