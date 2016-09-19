package com.devicehive.db

import java.sql.Timestamp
import java.text.SimpleDateFormat
import com.basho.riak.spark._
import com.basho.riak.spark.rdd.connector.RiakConnector
import com.devicehive.model.{DeviceCommand, DeviceNotification}
import org.apache.spark.Logging
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.Row
import scala.util.Try

object RiakInserter extends DataInserter {

  private val timestampFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")

  override def init(): Unit = {
    val connector = RiakConnector(sparkConf)
    connector.withSessionDo { session =>
      Try(session.execute(new com.basho.riak.client.api.commands.timeseries.Query.Builder(
        s"""
           |CREATE TABLE ${config.riakCommandTable} (
           |  id SINT64 NOT NULL,
           |  command VARCHAR NOT NULL,
           |  user_id SINT64 NOT NULL,
           |  gevice_guid VARCHAR NOT NULL,
           |  time TIMESTAMP NOT NULL,
           |  parameters VARCHAR,
           |  PRIMARY KEY (
           |    (QUANTUM(time, 60, 'm')),
           |    time
           |  )
           |)
         """.stripMargin
      ).build()))
      Try(session.execute(new com.basho.riak.client.api.commands.timeseries.Query.Builder(
        s"""
           |CREATE TABLE ${config.riakNotificationTable} (
           |  id SINT64 NOT NULL,
           |  notification VARCHAR NOT NULL,
           |  device_guid VARCHAR NOT NULL,
           |  time TIMESTAMP NOT NULL,
           |  parameters VARCHAR,
           |  PRIMARY KEY (
           |    (QUANTUM(time, 60, 'm')),
           |    time
           |  )
           |)
         """.stripMargin
      ).build()))
    }
  }

  override def saveNotifications(rdd: RDD[DeviceNotification]): Unit = {
    rdd.map { deviceNotification =>
      val timestamp = new Timestamp(timestampFormat.parse(deviceNotification.timestamp).getTime)
      Row(deviceNotification.id, deviceNotification.notification, deviceNotification.deviceGuid,
        timestamp, deviceNotification.parameters)
    }.saveToRiakTS(config.riakNotificationTable)
  }

  override def saveCommands(rdd: RDD[DeviceCommand]): Unit = {
    rdd.map { deviceCommand =>
      val timestamp = new Timestamp(timestampFormat.parse(deviceCommand.timestamp).getTime)
      Row(deviceCommand.id, deviceCommand.command, deviceCommand.userId, deviceCommand.deviceGuid,
        timestamp, deviceCommand.parameters)
    }.saveToRiakTS(config.riakCommandTable)
  }
}
