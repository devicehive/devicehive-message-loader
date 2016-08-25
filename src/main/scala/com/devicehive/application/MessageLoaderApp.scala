package com.devicehive.application

import com.datastax.spark.connector.cql.CassandraConnector
import com.devicehive.utils.Configuration
import kafka.serializer.StringDecoder
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.{SparkConf, Logging}
import com.datastax.spark.connector.streaming._

object MessageLoaderApp extends Logging {

  def main(args: Array[String]) {
    val config = Configuration(sys.env)
    logDebug(config.toString)

    val sparkConf = new SparkConf()
        .setAppName("DeviceHiveMessageLoader")
        .set("spark.cassandra.connection.host", config.cassHost)
        .set("spark.cassandra.auth.username", config.cassUser)
        .set("spark.cassandra.auth.password", config.cassPassword)

    CassandraConnector(sparkConf).withSessionDo { s =>
      s.execute(s"CREATE KEYSPACE IF NOT EXISTS ${config.cassKeySpace} " +
          s"WITH REPLICATION = {'class': 'SimpleStrategy', 'replication_factor': ${config.cassRepFactor} }") // toDo: change strategy
      s.execute(s"CREATE TABLE IF NOT EXISTS ${config.cassKeySpace}.${config.cassTable} (word TEXT PRIMARY KEY, count COUNTER)")
    }

    val ssc = new StreamingContext(sparkConf, Seconds(10))

    load(ssc, config)

    ssc.start()
    ssc.awaitTermination()
  }

  def load(ssc: StreamingContext, config: Configuration) {
    val kafkaParams = Map[String, String]("metadata.broker.list" -> config.kafkaBrokers)
    val messages = KafkaUtils
        .createDirectStream[String, String, StringDecoder, StringDecoder](ssc, kafkaParams, Set(config.kafkaTopic))

    // some filters goes here

    messages.saveToCassandra(config.cassKeySpace, config.cassTable)
  }
}
