package kafka

import java.time.Duration
import java.util
import java.util.Properties

import org.apache.kafka.clients.consumer.{ConsumerRecords, KafkaConsumer, OffsetAndMetadata, OffsetCommitCallback}
import org.apache.kafka.common.TopicPartition
import org.apache.kafka.common.errors.WakeupException
import timesheet.TimeControlManager

import scala.util.control.NonFatal

import utils.ParserUtil

import timesheet.ClockRecord

object ClockRecordConsumer {
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
    props.put("group.id", "Timesheet")
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
          val clockRecord = parseRecord(data)

          if (!clockRecord.isEmpty) {
            Console.println("Checkout Record")

            if (TimeControlManager.accumulate(clockRecord.get)) {
              offsetsToCommit.put(
                new TopicPartition(r.topic, r.partition),
                new OffsetAndMetadata(r.offset + 1, "")
              )
            }
          }
          else {
            Console.println("Record is not of interest. Committing")
            offsetsToCommit.put(
              new TopicPartition(r.topic, r.partition),
              new OffsetAndMetadata(r.offset + 1, "")
            )
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
        case NonFatal(t) =>
          t.printStackTrace(System.out)
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

  private def parseRecord(data: String): Option[ClockRecord] = {
    try {
      val record = ParserUtil.parse(data).asInstanceOf[Map[String, Any]]

      if (record("event_type") == "checkOut") {
        Some(ClockRecord(
          record("employee_id").asInstanceOf[Long].intValue(),
          record("check_in_date").asInstanceOf[String],
          record("check_out_date").asInstanceOf[String]
        ))
      }
      else
        None
    }
    catch {
      case NonFatal(t) => {
        Console.println("Wrong clock record")
        t.printStackTrace(System.out)
        None
      }
    }
  }
}
