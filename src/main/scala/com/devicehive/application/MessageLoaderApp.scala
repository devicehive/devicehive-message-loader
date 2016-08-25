package com.devicehive.application

import com.datastax.spark.connector.cql.CassandraConnector
import com.devicehive.utils.Configuration
import kafka.serializer.StringDecoder
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.{Logging, SparkConf}
import org.apache.spark.rdd.RDD
import org.apache.spark.streaming.dstream.DStream
import com.datastax.spark.connector._
import com.devicehive.decoder.DeviceMessageDecoder
import com.devicehive.model.{DeviceMessageEntity, DeviceNotification}

object MessageLoaderApp extends Logging {

  def main(args: Array[String]) {
    val config = Configuration(sys.env)
    logDebug(config.toString)

    val sparkConf = new SparkConf(true)
        .setAppName("DeviceHiveMessageLoader")
        .set("spark.cassandra.connection.host", config.cassHost)
        .set("spark.cassandra.auth.username", config.cassUser)
        .set("spark.cassandra.auth.password", config.cassPassword)

    CassandraConnector(sparkConf).withSessionDo { s =>
      s.execute(
        s"""CREATE KEYSPACE IF NOT EXISTS ${config.cassKeySpace}
            WITH REPLICATION = {'class': 'SimpleStrategy', 'replication_factor': ${config.cassRepFactor} }""".stripMargin) // toDo: change strategy
      if (config.dropTable)
        s.execute( s"""DROP TABLE IF EXISTS ${config.cassKeySpace}.${config.cassTable}""")
      s.execute(
        s"""CREATE TABLE IF NOT EXISTS ${config.cassKeySpace}.${config.cassTable}
            ( id bigint, notification text, device_guid text, timestamp text, PRIMARY KEY (id, timestamp))""".stripMargin) // TODO: structure of table
    }

    val ssc = new StreamingContext(sparkConf, Seconds(config.batchDuration))

    val messagesStream = load(ssc, config)
    messagesStream.foreachRDD(rdd => store(rdd, config))

    ssc.start()
    ssc.awaitTermination()
  }

  def load(ssc: StreamingContext, config: Configuration): DStream[DeviceNotification] =  {
    val kafkaParams = Map[String, String]("metadata.broker.list" -> config.kafkaBrokers)
    KafkaUtils
        .createDirectStream[String, DeviceMessageEntity, StringDecoder, DeviceMessageDecoder](ssc, kafkaParams, Set(config.kafkaTopic))
        .map { case (k, v) => v.body }
        .filter(mes => mes.action.equalsIgnoreCase("notification_insert") || mes.action.equalsIgnoreCase("command_insert"))
        .map(mes => mes.deviceNotification)
  }

  def store(messages: RDD[DeviceNotification], config: Configuration): Unit = {
    messages.saveToCassandra(config.cassKeySpace, config.cassTable)
  }
}
