package com.devicehive.decoder

import com.devicehive.model.DeviceMessageEntity
import kafka.serializer.Decoder
import kafka.utils.VerifiableProperties
import org.json4s.DefaultFormats
import org.json4s.jackson.JsonMethods

class DeviceMessageDecoder(props: VerifiableProperties = null) extends Decoder[DeviceMessageEntity] {

  def fromBytes(bytes: Array[Byte]): DeviceMessageEntity = {
    implicit val formats = DefaultFormats
    JsonMethods.parse(new String(bytes, "UTF8")).extract[DeviceMessageEntity]
  }
}
