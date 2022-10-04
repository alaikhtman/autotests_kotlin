package ru.generalName.teamName.tests.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class RestConfiguration {


    @Bean
    fun restProfile(integrationTestProperties: IntegrationTestProperties): NetConfig {
        val (url, port) = integrationTestProperties.rest.getValue("profiles")
        return NetConfig(url, port)
    }


}