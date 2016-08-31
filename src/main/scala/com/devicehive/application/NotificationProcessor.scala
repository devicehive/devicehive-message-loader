package com.devicehive.application

import com.devicehive.utils.Config
import org.apache.spark.streaming.{Seconds, StreamingContext}
import com.devicehive.db.{CassandraInserter, DataInserter, RiakInserter}
import com.devicehive.loader.DataLoader
import com.devicehive.model._
import org.apache.spark.Logging

object NotificationProcessor extends Logging with Config {

  def main(args: Array[String]) {
    logDebug(config.toString)
    val dataInserter: DataInserter = config.storage match {
      case "riak" => RiakInserter
      case "cassandra" => CassandraInserter
      case storage => throw new ClassNotFoundException(s"Invalid storage: $storage")
    }
    dataInserter.init()

    val ssc = new StreamingContext(sparkConf, Seconds(config.batchDuration))

    DataLoader.load[DeviceNotification](ssc, config.kafkaBrokers, config.kafkaNotificationTopic, "notification_insert")
      .foreachRDD(rdd => dataInserter.saveNotifications(rdd))

    DataLoader.load[DeviceCommand](ssc, config.kafkaBrokers, config.kafkaCommandTopic, "command_insert")
      .foreachRDD(rdd => dataInserter.saveCommands(rdd))

    ssc.start()
    ssc.awaitTermination()
  }
}