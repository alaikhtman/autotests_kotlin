package ru.samokat.mysamokat.tests.configuration

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
    val employeeLog: String,
    val employeeCreated: String,
    val employeeChanged: String,
    val employeeDisabled: String,
    val employeePassChanged: String,
    val activeShiftsLog: String,
    val shiftAssignmentLog: String,
    val firstShiftsSchedule: String,
    val employeeFirstLoginConsume: String,
    val billedTimeslot: String
)

data class ProducerTopics(
    val employeeCreated: String,
    val priemNaRabotuCFZ: String,
    val priemNaRabotuSpiskomCFZ: String,
    val kadrovyyPerevodCFZ: String,
    val kadrovyyPerevodSpiskomCFZ: String,
    val vneshnieSotrudniki: String,
    val employeeFirstLogin: String,
    val billedTimeslotError: String

)
