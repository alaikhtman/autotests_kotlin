package ru.generalName.teamName.tests.helpers.controllers


import com.fasterxml.jackson.databind.node.ObjectNode
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.ConsumerRecords
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.ByteArraySerializer
import org.apache.kafka.connect.json.JsonDeserializer
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import java.time.Duration
import java.util.*


@Component
@Scope("prototype")
class KafkaController {
    private val properties: Properties = Properties()
    private var topicForProducer: String = ""
    private var listOfTopics = mutableListOf<String>()

    fun setProperties(connections: String) {
        properties[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = connections
        properties[ProducerConfig.ACKS_CONFIG] = "all"
        properties[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] =
            org.apache.kafka.common.serialization.UUIDSerializer::class.java
        properties[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = ByteArraySerializer::class.java
        properties[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] =
            org.apache.kafka.common.serialization.UUIDDeserializer::class.java
        properties[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = JsonDeserializer::class.java
        properties[ConsumerConfig.GROUP_ID_CONFIG] = "0"
        properties[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "earliest"
    }

    fun setTopicForProducer(topic: String) {
        this.topicForProducer = topic
    }

    fun setListOfTopicsForConsumer(listOfTopics: MutableList<String>) {
        this.listOfTopics = listOfTopics
    }

    fun sendMessage(record: Any, messageCount: Int, key: UUID) {
        KafkaProducer<Any, Any>(properties).use { producer ->
            repeat(messageCount) {
                producer.send(ProducerRecord(topicForProducer, key, record))
            }
            producer.flush()
        }
    }

    fun consume(messageOrKeyToFind: String): ConsumerRecord<Any, Any>? {
        val props = properties
        props[ConsumerConfig.GROUP_ID_CONFIG] = UUID.randomUUID().toString()
        val consumer = KafkaConsumer<Any, Any>(props)
        consumer.subscribe(listOfTopics)
        var counter = 0
        consumer.use {
            while (true) {
                var records = consumer.poll(Duration.ofSeconds(10))
                for (record in records) {
                    val key = record.key()
                    if (key.toString() == messageOrKeyToFind) {
                        return record
                    }
                    if (record == records.last()) {
                        counter++
                    }
                    if (counter > 30) {
                        throw Exception("Consumer cannot find record")
                    }
                }
            }
        }
    }

    fun consumeByIdAndParam(
        messageOrKeyToFind: String,
        filterKey: String,
        filterValue: Any
    ): ConsumerRecord<Any, Any>? {
        val props = properties
        props[ConsumerConfig.GROUP_ID_CONFIG] = UUID.randomUUID().toString()
        val consumer = KafkaConsumer<Any, Any>(props)
        consumer.subscribe(listOfTopics)
        var counter = 0
        consumer.use {
            while (true) {
                var records = consumer.poll(Duration.ofSeconds(10))
                for (record in records) {
                    val key = record.key()
                    if (key.toString() == messageOrKeyToFind &&
                        (record.value() as ObjectNode).findValue(filterKey).toString().trim('"') == filterValue

                    ) {
                        return record
                    }
                    if (record == records.last()) {
                        counter++
                    }
                    if (counter > 30) {
                        throw Exception("Consumer cannot find record")
                    }
                }
            }
        }
    }

    fun consumeAll(): ConsumerRecords<Any?, Any>? {
        val props = properties
        props[ConsumerConfig.GROUP_ID_CONFIG] = UUID.randomUUID().toString()
        val consumer = KafkaConsumer<Any?, Any>(props)
        consumer.subscribe(listOfTopics)
        consumer.use {
            var records: ConsumerRecords<Any?, Any>? = null
            while (records == null || records.isEmpty) {
                records = consumer.poll(Duration.ofSeconds(30))
            }
            return records
        }
    }
}

