package ru.generalName.teamName.tests.configuration

import org.jetbrains.exposed.sql.Database
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class DatabaseConfiguration {


    @Bean
    fun profileDatabase(integrationTestProperties: IntegrationTestProperties): Database {
        val (url, username, password) = integrationTestProperties.database.getValue("profile")
        return Database.connect(url = url, user = username, password = password)
    }

    @Bean
    fun scheduleDatabase(integrationTestProperties: IntegrationTestProperties): Database {
        val (url, username, password) = integrationTestProperties.database.getValue("schedule")
        return Database.connect(url = url, user = username, password = password)
    }



}