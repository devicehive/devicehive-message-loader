package com.devicehive.loader

import com.devicehive.decoder.EntityDecoder
import com.devicehive.model._
import com.devicehive.utils.Config
import kafka.serializer.StringDecoder
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.kafka.KafkaUtils

import scala.reflect.ClassTag

object DataLoader extends Config {

  def load[T <: DeviceMessage : ClassTag](ssc: StreamingContext, kafkaBrokers: String, kafkaTopic: String, action: String): DStream[T] = {
    val kafkaParams = Map[String, String]("metadata.broker.list" -> kafkaBrokers)
    KafkaUtils
      .createDirectStream[String, Entity, StringDecoder, EntityDecoder](ssc, kafkaParams, Set(kafkaTopic))
      .map { case (k, v) => v.body }
      .filter(body => body.action.equalsIgnoreCase(action))
      .map { body => body.notification }
      .map { case mes: T => mes }
  }
}
