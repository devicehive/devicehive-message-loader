package com.devicehive.decoder

import com.devicehive.model._
import kafka.serializer.Decoder
import kafka.utils.VerifiableProperties
import org.json4s.JsonAST._
import org.json4s.{CustomSerializer, DefaultFormats, NoTypeHints}
import org.json4s.jackson.Serialization

class EntityDecoder(props: VerifiableProperties = null) extends Decoder[Entity] {
  val encoding =
    if (props == null)
      "UTF8"
    else
      props.getString("serializer.encoding", "UTF8")

  def fromBytes(bytes: Array[Byte]): Entity = {
    implicit val format = Serialization.formats(NoTypeHints) + new EntitySerializer
    Serialization.read[Entity](new String(bytes, encoding))
  }
}

class EntitySerializer extends CustomSerializer[Entity](format => ( {
  case entity: JObject =>
    implicit val format = DefaultFormats + new BodySerializer
    val correlationId = (entity \ "correlationId").extract[String]
    val partitionKey = (entity \ "partitionKey").extract[String]
    val singleReplyExpected = (entity \ "singleReplyExpected").extract[Boolean]
    val replyTo = (entity \ "replyTo").extract[String]
    val body = (entity \ "body").toOption
    body match {
      case Some(b) => Entity(b.extract[Body], correlationId, partitionKey, singleReplyExpected, replyTo)
      case _ => Entity(Body(DeviceMessageDefault(), ""), correlationId, partitionKey, singleReplyExpected, replyTo) // not deserialize messages with empty body
    }
}, {
  case x: Entity => JObject() //not interested in serialization
}))

class BodySerializer extends CustomSerializer[Body](format => ( {
  case body: JObject =>
    implicit val format = DefaultFormats
    val action = (body \ "action").extract[String].toLowerCase()
    action match {
      case "notification_insert_request" =>
        val devNot = (body \ "deviceNotification").extract[DeviceNotification]
        Body(devNot, action)
      case "command_insert_request" =>
        val comNot = (body \ "deviceCommand").extract[DeviceCommand]
        Body(comNot, action)
      case _ => Body(DeviceMessageDefault(), action) // not deserialize messages with different action
    }
}, {
  case body: Body => JObject() //not interested in serialization
}))

