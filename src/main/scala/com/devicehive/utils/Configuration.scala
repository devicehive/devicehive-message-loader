package com.devicehive.utils

import java.util.Properties

case class Configuration (
    kafkaBrokers: String,
    kafkaTopic: String,
    cassHost: String,
    cassUser: String,
    cassPassword: String,
    cassRepFactor: String,
    cassKeySpace: String,
    cassTable: String,
    batchDuration: Long,
    dropTable: Boolean,
    producerProps: Properties
)

object Configuration {
  def apply(vars: Map[String, String]): Configuration = {
    val kafkaBrokers = vars.getOrElse("KAFKA_BROKERS", "localhost:9092")
    val kafkaTopic = vars.getOrElse("KAFKA_TOPIC", "devices")

    val cassHost = vars.getOrElse("CASSANDRA_HOST", "localhost")
    val cassUser = vars.getOrElse("CASSANDRA_USERNAME", "cassandra")
    val cassPassword = vars.getOrElse("CASSANDRA_PASSWORD", "cassandra")
    val cassRepFactor = vars.getOrElse("CASSANDRA_REP_FACTOR", "3")
    val cassKeySpace = vars.getOrElse("CASSANDRA_KEYSPACE_NAME", "devicehive")
    val cassTable = vars.getOrElse("CASSANDRA_TABLE_NAME", "device_messages")
    val batchDuration = vars.getOrElse("BATCH_DURATION", "10")
    val dropTable = vars.getOrElse("DROP_TABLE", "false")
    val producerProps = new Properties()
    producerProps.setProperty("bootstrap.servers", kafkaBrokers)
    producerProps.setProperty("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    producerProps.setProperty("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

    new Configuration(
      kafkaBrokers,
      kafkaTopic,
      cassHost,
      cassUser,
      cassPassword,
      cassRepFactor,
      cassKeySpace,
      cassTable,
      batchDuration.toLong,
      dropTable.toBoolean,
      producerProps
    )
  }
}