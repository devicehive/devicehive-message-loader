package com.devicehive.model

sealed trait DeviceMessage
case class DeviceNotification(id: Long, notification: String, deviceGuid: String, timestamp: String, parameters: String = null) extends DeviceMessage
case class DeviceCommand(id: Long, command: String, userId: Long, deviceGuid: String, timestamp: String, parameters: String = null) extends DeviceMessage // more fields
case class DeviceMessageDefault() extends DeviceMessage
//case class Parameters(mac: String, uuid: String, value: Double)

case class Body(notification: DeviceMessage, action: String)
case class Entity(body: Body, correlationId: String, partitionKey: String, singleReplyExpected: Boolean, replyTo: String)