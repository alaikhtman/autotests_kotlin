package ru.samokat.mysamokat.tests.configuration

import org.jetbrains.exposed.sql.Database
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class DatabaseConfiguration {


    @Bean
    fun employeeDatabase(integrationTestProperties: IntegrationTestProperties): Database {
        val (url, username, password) = integrationTestProperties.database.getValue("employee")
        return Database.connect(url = url, user = username, password = password)
    }

    @Bean
    fun employeeScheduleDatabase(integrationTestProperties: IntegrationTestProperties): Database {
        val (url, username, password) = integrationTestProperties.database.getValue("employee-schedule")
        return Database.connect(url = url, user = username, password = password)
    }

    @Bean
    fun shiftsDatabase(integrationTestProperties: IntegrationTestProperties): Database {
        val (url, username, password) = integrationTestProperties.database.getValue("shifts")
        return Database.connect(url = url, user = username, password = password)
    }

    @Bean
    fun apigatewayDatabase(integrationTestProperties: IntegrationTestProperties): Database {
        val (url, username, password) = integrationTestProperties.database.getValue("apigateway")
        return Database.connect(url = url, user = username, password = password)
    }

    @Bean
    fun tipsDatabase(integrationTestProperties: IntegrationTestProperties): Database {
        val (url, username, password) = integrationTestProperties.database.getValue("tips")
        return Database.connect(url = url, user = username, password = password)
    }

    @Bean
    fun staffMetadataDatabase(integrationTestProperties: IntegrationTestProperties): Database {
        val (url, username, password) = integrationTestProperties.database.getValue("staff-metadata")
        return Database.connect(url = url, user = username, password = password)
    }

    @Bean
    fun pushDatabase(integrationTestProperties: IntegrationTestProperties): Database {
        val (url, username, password) = integrationTestProperties.database.getValue("push")
        return Database.connect(url = url, user = username, password = password)
    }

    @Bean
    fun statisticsDatabase(integrationTestProperties: IntegrationTestProperties): Database {
        val (url, username, password) = integrationTestProperties.database.getValue("employee-statistics")
        return Database.connect(url = url, user = username, password = password)
    }

    @Bean
    fun hrpMapDatabase(integrationTestProperties: IntegrationTestProperties): Database {
        val (url, username, password) = integrationTestProperties.database.getValue("hrp-map")
        return Database.connect(url = url, user = username, password = password)
    }


}