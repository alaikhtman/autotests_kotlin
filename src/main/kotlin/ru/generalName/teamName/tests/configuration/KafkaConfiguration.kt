package ru.samokat.mysamokat.tests.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import ru.samokat.mysamokat.tests.helpers.controllers.KafkaController

@Configuration
class KafkaConfiguration {

    @Bean
    fun kafkaEmployeeLog(integrationTestProperties: IntegrationTestProperties): KafkaController {
        val (urls, consumerTopics) = integrationTestProperties.kafka.getValue("release")
        val kafkaController = KafkaController()
        kafkaController.setProperties(urls.joinToString())
        kafkaController.setListOfTopicsForConsumer(mutableListOf(consumerTopics.employeeLog))
        return kafkaController
    }

    @Bean
    fun kafkaEmployeeCreated(integrationTestProperties: IntegrationTestProperties): KafkaController {
        val (urls, consumerTopics) = integrationTestProperties.kafka.getValue("release")
        val kafkaController = KafkaController()
        kafkaController.setProperties(urls.joinToString())
        kafkaController.setListOfTopicsForConsumer(mutableListOf(consumerTopics.employeeCreated))
        return kafkaController
    }

    @Bean
    fun kafkaEmployeeChanged(integrationTestProperties: IntegrationTestProperties): KafkaController {
        val (urls, consumerTopics) = integrationTestProperties.kafka.getValue("release")
        val kafkaController = KafkaController()
        kafkaController.setProperties(urls.joinToString())
        kafkaController.setListOfTopicsForConsumer(mutableListOf(consumerTopics.employeeChanged))
        return kafkaController
    }

    @Bean
    fun kafkaEmployeeDisabled(integrationTestProperties: IntegrationTestProperties): KafkaController {
        val (urls, consumerTopics) = integrationTestProperties.kafka.getValue("release")
        val kafkaController = KafkaController()
        kafkaController.setProperties(urls.joinToString())
        kafkaController.setListOfTopicsForConsumer(mutableListOf(consumerTopics.employeeDisabled))
        return kafkaController
    }

    @Bean
    fun kafkaEmployeePassChanged(integrationTestProperties: IntegrationTestProperties): KafkaController {
        val (urls, consumerTopics) = integrationTestProperties.kafka.getValue("release")
        val kafkaController = KafkaController()
        kafkaController.setProperties(urls.joinToString())
        kafkaController.setListOfTopicsForConsumer(mutableListOf(consumerTopics.employeePassChanged))
        return kafkaController
    }


    @Bean
    fun kafkaEmployeeCreatedProduce(integrationTestProperties: IntegrationTestProperties): KafkaController {
        val (urls,_, producerTopics) = integrationTestProperties.kafka.getValue("release")
        val kafkaController = KafkaController()
        kafkaController.setProperties(urls.joinToString())
        kafkaController.setTopicForProducer(producerTopics.employeeCreated)
        return kafkaController
    }

    @Bean
    fun kafkaPriemNaRabotuCFZ(integrationTestProperties: IntegrationTestProperties): KafkaController {
        val (urls,_, producerTopics) = integrationTestProperties.kafka.getValue("release")
        val kafkaController = KafkaController()
        kafkaController.setProperties(urls.joinToString())
        kafkaController.setTopicForProducer(producerTopics.priemNaRabotuCFZ)
        return kafkaController
    }

    @Bean
    fun kafkaPriemNaRabotuSpiskomCFZ(integrationTestProperties: IntegrationTestProperties): KafkaController {
        val (urls,_, producerTopics) = integrationTestProperties.kafka.getValue("release")
        val kafkaController = KafkaController()
        kafkaController.setProperties(urls.joinToString())
        kafkaController.setTopicForProducer(producerTopics.priemNaRabotuSpiskomCFZ)
        return kafkaController
    }

    @Bean
    fun kafkaActiveShiftsLog(integrationTestProperties: IntegrationTestProperties): KafkaController {
        val (urls, consumerTopics) = integrationTestProperties.kafka.getValue("release")
        val kafkaController = KafkaController()
        kafkaController.setProperties(urls.joinToString())
        kafkaController.setListOfTopicsForConsumer(mutableListOf(consumerTopics.activeShiftsLog))
        return kafkaController
    }

    @Bean
    fun kafkaShiftAssignmentsLog(integrationTestProperties: IntegrationTestProperties): KafkaController {
        val (urls, consumerTopics) = integrationTestProperties.kafka.getValue("release")
        val kafkaController = KafkaController()
        kafkaController.setProperties(urls.joinToString())
        kafkaController.setListOfTopicsForConsumer(mutableListOf(consumerTopics.shiftAssignmentLog))
        return kafkaController
    }

    @Bean
    fun kafkaKadrovyyPerevodCFZ(integrationTestProperties: IntegrationTestProperties): KafkaController {
        val (urls,_, producerTopics) = integrationTestProperties.kafka.getValue("release")
        val kafkaController = KafkaController()
        kafkaController.setProperties(urls.joinToString())
        kafkaController.setTopicForProducer(producerTopics.kadrovyyPerevodCFZ)
        return kafkaController
    }

    @Bean
    fun kafkaKadrovyyPerevodSpiskomCFZ(integrationTestProperties: IntegrationTestProperties): KafkaController {
        val (urls,_, producerTopics) = integrationTestProperties.kafka.getValue("release")
        val kafkaController = KafkaController()
        kafkaController.setProperties(urls.joinToString())
        kafkaController.setTopicForProducer(producerTopics.kadrovyyPerevodSpiskomCFZ)
        return kafkaController
    }

    @Bean
    fun kafkaVneshnieSotrudniki(integrationTestProperties: IntegrationTestProperties): KafkaController {
        val (urls,_, producerTopics) = integrationTestProperties.kafka.getValue("release")
        val kafkaController = KafkaController()
        kafkaController.setProperties(urls.joinToString())
        kafkaController.setTopicForProducer(producerTopics.vneshnieSotrudniki)
        return kafkaController
    }

    @Bean
    fun kafkaFirstLogin(integrationTestProperties: IntegrationTestProperties): KafkaController {
        val (urls,_, producerTopics) = integrationTestProperties.kafka.getValue("release")
        val kafkaController = KafkaController()
        kafkaController.setProperties(urls.joinToString())
        kafkaController.setTopicForProducer(producerTopics.employeeFirstLogin)
        return kafkaController
    }

    @Bean
    fun kafkaFirstShiftsSchedule(integrationTestProperties: IntegrationTestProperties): KafkaController {
        val (urls, consumerTopics) = integrationTestProperties.kafka.getValue("release")
        val kafkaController = KafkaController()
        kafkaController.setProperties(urls.joinToString())
        kafkaController.setListOfTopicsForConsumer(mutableListOf(consumerTopics.firstShiftsSchedule))
        return kafkaController
    }

    @Bean
    fun kafkaFirstLoginConsume(integrationTestProperties: IntegrationTestProperties): KafkaController {
        val (urls, consumerTopics) = integrationTestProperties.kafka.getValue("release")
        val kafkaController = KafkaController()
        kafkaController.setProperties(urls.joinToString())
        kafkaController.setListOfTopicsForConsumer(mutableListOf(consumerTopics.employeeFirstLoginConsume))
        return kafkaController
    }


    @Bean
    fun kafkaBilledTimeslot(integrationTestProperties: IntegrationTestProperties): KafkaController {
        val (urls, consumerTopics) = integrationTestProperties.kafka.getValue("release")
        val kafkaController = KafkaController()
        kafkaController.setProperties(urls.joinToString())
        kafkaController.setListOfTopicsForConsumer(mutableListOf(consumerTopics.billedTimeslot))
        return kafkaController
    }

    @Bean
    fun kafkaBilledTimeslotError(integrationTestProperties: IntegrationTestProperties): KafkaController {
        val (urls,_, producerTopics) = integrationTestProperties.kafka.getValue("release")
        val kafkaController = KafkaController()
        kafkaController.setProperties(urls.joinToString())
        kafkaController.setTopicForProducer(producerTopics.billedTimeslotError)
        return kafkaController
    }
}