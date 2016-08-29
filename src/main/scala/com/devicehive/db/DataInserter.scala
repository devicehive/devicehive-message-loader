package com.devicehive.db

import com.datastax.spark.connector.cql.CassandraConnector
import com.devicehive.model.{DeviceCommand,DeviceNotification}
import org.apache.spark.rdd.RDD
import com.datastax.spark.connector._
import com.devicehive.utils.Config

abstract class DataInserter extends Config {

  def init(): Unit
  def saveNotifications(rdd: RDD[DeviceNotification]): Unit // find better way to generify saving?
  def saveCommands(rdd: RDD[DeviceCommand]): Unit
}

object CassandraInserter extends DataInserter {

  override def init(): Unit = {

    CassandraConnector(sparkConf).withSessionDo { s =>
      s.execute(
        s"""CREATE KEYSPACE IF NOT EXISTS ${config.cassKeySpace}
            WITH REPLICATION = {'class': 'SimpleStrategy', 'replication_factor': ${config.cassRepFactor} }""".stripMargin) // TODO: change strategy

      if (config.dropCommandTable) {
        s.execute( s"""DROP TABLE IF EXISTS ${config.cassKeySpace}.${config.cassCommandTable}""")
      }
      s.execute(
        s"""CREATE TABLE IF NOT EXISTS ${config.cassKeySpace}.${config.cassCommandTable}
            ( id bigint, command text, user_id text, status text, timestamp text, parameters text, PRIMARY KEY (id, timestamp))""".stripMargin) // TODO: structure of table

      if (config.dropNotificationTable) {
        s.execute( s"""DROP TABLE IF EXISTS ${config.cassKeySpace}.${config.cassNotificationTable}""")
      }
      s.execute(
        s"""CREATE TABLE IF NOT EXISTS ${config.cassKeySpace}.${config.cassNotificationTable}
            ( id bigint, notification text, device_guid text, timestamp text, parameters text, PRIMARY KEY (id, timestamp))""".stripMargin) // TODO: structure of table
    }
  }

  override def saveNotifications(rdd: RDD[DeviceNotification]): Unit = {
    rdd.saveToCassandra(config.cassKeySpace, config.cassNotificationTable)
  }

  override def saveCommands(rdd: RDD[DeviceCommand]): Unit = {
    rdd.saveToCassandra(config.cassKeySpace, config.cassCommandTable)
  }
}

