package com.devicehive.utils

import java.util.Properties
import org.apache.spark.SparkConf

trait Config {

  lazy val env = sys.env

  lazy val sparkConf = new SparkConf(true)
    .setAppName("DeviceHiveMessageLoader")
    .set("spark.cassandra.connection.host", config.cassHost)
    .set("spark.cassandra.auth.username", config.cassUser)
    .set("spark.cassandra.auth.password", config.cassPassword)
    .set("spark.riak.connection.host", config.riakSocket)

  object config {
    lazy val kafkaBrokers = env.getOrElse("KAFKA_BROKERS", "localhost:9092")
    lazy val kafkaNotificationTopic = env.getOrElse("KAFKA_NOTIFICATION_TOPIC", "request_topic")
    lazy val kafkaCommandTopic = env.getOrElse("KAFKA_COMMAND_TOPIC", "request_topic")
    lazy val producerProps = new Properties()
    producerProps.setProperty("bootstrap.servers", kafkaBrokers)
    producerProps.setProperty("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    producerProps.setProperty("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

    lazy val cassHost = env.getOrElse("CASSANDRA_HOST", "localhost")
    lazy val cassUser = env.getOrElse("CASSANDRA_USERNAME", "cassandra")
    lazy val cassPassword = env.getOrElse("CASSANDRA_PASSWORD", "cassandra")
    lazy val cassRepFactor = env.getOrElse("CASSANDRA_REP_FACTOR", "1")
    lazy val cassKeySpace = env.getOrElse("CASSANDRA_KEYSPACE_NAME", "devicehive")
    lazy val cassCommandTable = env.getOrElse("CASSANDRA_COMMAND_TABLE_NAME", "device_command")
    lazy val cassNotificationTable = env.getOrElse("CASSANDRA_NOTIFICATION_TABLE_NAME", "device_notification")

    lazy val riakSocket = env.getOrElse("RIAK_SOCKET", "localhost:8087")
    lazy val riakCommandTable = env.getOrElse("RIAK_COMMAND_TABLE_NAME", "device_command")
    lazy val riakNotificationTable = env.getOrElse("RIAK_NOTIFICATION_TABLE_NAME", "device_notification")

    lazy val batchDuration = env.getOrElse("BATCH_DURATION", "10").toInt
    lazy val dropCommandTable = env.getOrElse("DROP_COMMAND_TABLE", "true").toBoolean
    lazy val dropNotificationTable = env.getOrElse("DROP_NOTIFICATION_TABLE", "true").toBoolean

    lazy val storage = env.getOrElse("STORAGE", "riak")
  }
}
