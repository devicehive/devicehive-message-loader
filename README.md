# DeviceHive Message Loader

Provides persisting `notification_insert_request` notifications and `command_insert_request` commands going through [DeviceHive Java Server](https://github.com/devicehive/devicehive-java-server) with the help of [Spark Streaming](http://spark.apache.org/streaming/), [Apache Kafka](http://kafka.apache.org/) and [Apache Cassandra](http://cassandra.apache.org/) or [Riak TS](http://basho.com/products/riak-ts/).
 
# Configuration
 
All configuration properties are set up within environment variables. The list of available properties is set up in `com.devicehive.utils.Config` class. Every property has a default value. You can change Spark configs, Kafka configs, database configs etc there.

# Usage

- Start [DeviceHive Java Server](https://github.com/devicehive/devicehive-java-server/blob/development/README.md).

- Choose the database and start it. Available options are `cassandra` and `riak`. By default the `riak` database is chosen.

- Start Kafka and check if the topic configured in `com.devicehive.utils.Config` class exists. By default the `request_topic` is chosen, as DeviceHive Java Server uses it.

- Start `com.devicehive.application.NotificationProcessor` class. In the beginning necessary tables in the chosen database will be created. After that Spark will read new commands/notifications from topic every configured `batch duration`, filter `notification_insert_request` and `command_insert_request` and save them to the chosen storage.

- Insert DeviceCommand/DeviceNotification. Examples of requests:

Insert DeviceNotification: `curl -XPOST -H 'Content-type: application/json' -H 'Authorization: Bearer 1jwKgLYi/CdfBTI9KByfYxwyQ6HUIEfnGSgakdpFjgk=' localhost:8080/dh/rest/device/e50d6085-2aba-48e9-b1c3-73c673e414be/notification -d '{"notification":"test"}'`

Insert DeviceCommand:  `curl -XPOST -H 'Content-type: application/json' -H 'Authorization: Bearer 1jwKgLYi/CdfBTI9KByfYxwyQ6HUIEfnGSgakdpFjgk=' localhost:8080/dh/rest/device/e50d6085-2aba-48e9-b1c3-73c673e414be/command -d '{"command":"test"}'`

If everything is configured properly you can select command/notifications from corresponding tables and see messages inserted.

# Improvements

You can change `com.devicehive.model.DeviceCommand` and `com.devicehive.model.DeviceNotification` and write corresponding serializer/deserializer. You can find the examples of serializers/deserializers for current entities in `com.devicehive.decoder.EntityDecoder` class. 

You can manage the structure of tables in `com.devicehive.db.CassandraInserter#init()` and `com.devicehive.db.RiakInserter#init()` methods.

You can add new entities for persisting notifications/commands with different actions. You must also add serializer/deserializer of the new entities in order to be able to read this entities from Kafka, add tables and methods for saving messages to the database.

# License

[DeviceHive](http://devicehive.com) is developed by [DataArt Apps](http://dataart.com) and distributed under [Open Source MIT](https://en.wikipedia.org/wiki/MIT_License) license. This basically means you can do whatever you want with the software as long as the copyright notice is included. This also means you don't have to contribute the end product or modified sources back to Open Source, but if you feel like sharing, you are highly encouraged to do so!

© Copyright 2016 DataArt Apps © All Rights Reserved