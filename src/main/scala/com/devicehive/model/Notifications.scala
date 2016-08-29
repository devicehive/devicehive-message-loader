package com.devicehive.model

sealed trait DeviceMessage
case class DeviceNotification(id: Int, notification: String, deviceGuid: String, timestamp: String, parameters: String = null) extends DeviceMessage // how to parse parameters?
case class DeviceCommand(id: Int, command: String, userId: Int, status: String, timestamp: String, parameters: String = null) extends DeviceMessage // TODO: subject to change
//case class Parameters(mac: String, uuid: String, value: Double)

case class Body(notification: DeviceMessage, action: String)
case class Entity(body: Body, correlationId: String, partitionKey: String, singleReplyExpected: Boolean, replyTo: String)