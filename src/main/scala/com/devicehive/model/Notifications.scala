package com.devicehive.model

case class DeviceNotification(id: Int, notification: String, deviceGuid: String, timestamp: String, parameters: Map[String, String] = Map())
case class Body(deviceNotification: DeviceNotification, action: String)
case class DeviceMessageEntity(body: Body, correlationId: String, partitionKey: String, singleReplyExpected: Boolean, replyTo: String)
//  case class Parameters(mac: String, uuid: String, value: Double)
