package com.devicehive.decoder

import com.devicehive.model.DeviceMessageEntity
import kafka.serializer.Decoder
import kafka.utils.VerifiableProperties
import org.json4s.DefaultFormats
import org.json4s.jackson.JsonMethods

class DeviceMessageDecoder(props: VerifiableProperties = null) extends Decoder[DeviceMessageEntity] {
  val encoding =
    if(props == null)
      "UTF8"
    else
      props.getString("serializer.encoding", "UTF8")

  def fromBytes(bytes: Array[Byte]): DeviceMessageEntity = {
    implicit val formats = DefaultFormats
    JsonMethods.parse(new String(bytes, encoding)).extract[DeviceMessageEntity]
  }
}
