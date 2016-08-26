package com.devicehive.producer

import java.time.LocalDateTime
import java.util.concurrent.atomic.AtomicInteger
import com.devicehive.model.{Body, DeviceMessageEntity, DeviceNotification}
import com.devicehive.utils.Configuration
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.apache.spark.Logging
import org.json4s.DefaultFormats
import org.json4s.jackson.Serialization

object NotificationProducer extends Logging {

  private val id = new AtomicInteger()

  def main(args: Array[String]) {
    val config = Configuration(sys.env)
    val producer = new KafkaProducer[AnyRef, String](config.producerProps)

    val thread = new Thread(new Runnable() {
      def run(): Unit = {
        try {
          while (!Thread.currentThread().isInterrupted) {
            producer.send(new ProducerRecord(config.kafkaTopic,  convertToJSON(generateMessage())))
            Thread.sleep(1000)
          }
        }
        catch {
          case ex: InterruptedException => log.error("Interrupted exception occurred!")
        }
      }
    })

    thread.start()
  }

  def generateMessage(): DeviceMessageEntity = {
    id.getAndIncrement()
    val notification = DeviceNotification(id.get(), "some test data", "089a5342-cc07-4e92-a209-25888ac7ea98", LocalDateTime.now().toString)
    val body = Body(notification, "notification_insert")
    DeviceMessageEntity(body, "", "", singleReplyExpected = false, "")
  }

  private def convertToJSON(message: DeviceMessageEntity): String = {
    implicit val formats = DefaultFormats
    Serialization.write(message)
  }
}
