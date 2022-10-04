package ru.generalName.teamName.tests.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties("configuration")
data class IntegrationTestProperties(
    val database: Map<String, DatabaseProperties>,
    val rest: Map<String, ServiceConfigurationProperties>,
    val kafka: Map<String, KafkaConfigurationProperties>
)

@ConstructorBinding
data class DatabaseProperties(
    val url: String,
    val user: String,
    val password: String
)

@ConstructorBinding
data class ServiceConfigurationProperties(
    val url: String,
    val port: Int
)

@ConstructorBinding
data class KafkaConfigurationProperties(
    val urls: List<String>,
    val consumerTopics: ConsumerTopics,
    val producerTopics: ProducerTopics
)

data class ConsumerTopics(
    val test: String
)

data class ProducerTopics(
    val test: String

)
