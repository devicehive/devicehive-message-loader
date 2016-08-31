package com.devicehive.db

import com.devicehive.model.{DeviceCommand, DeviceNotification}
import com.devicehive.utils.Config
import org.apache.spark.rdd.RDD

abstract class DataInserter extends Config {

  def init(): Unit
  def saveNotifications(rdd: RDD[DeviceNotification]): Unit // find better way to generify saving?
  def saveCommands(rdd: RDD[DeviceCommand]): Unit
}

