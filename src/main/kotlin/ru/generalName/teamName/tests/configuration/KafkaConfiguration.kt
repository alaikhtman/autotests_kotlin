package ru.generalName.teamName.tests.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class KafkaConfiguration {

    @Bean
    fun kafkaLog(integrationTestProperties: IntegrationTestProperties): KafkaController {
        val (urls, consumerTopics) = integrationTestProperties.kafka.getValue("test")
        val kafkaController = KafkaController()
        kafkaController.setProperties(urls.joinToString())
        kafkaController.setListOfTopicsForConsumer(mutableListOf(consumerTopics.test))
        return kafkaController
    }


}