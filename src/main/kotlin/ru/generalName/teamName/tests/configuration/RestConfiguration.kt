package ru.samokat.mysamokat.tests.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import ru.samokat.mysamokat.tests.endpoints.configuration.NetConfig

@Configuration
class RestConfiguration {


    @Bean
    fun restEmployee(integrationTestProperties: IntegrationTestProperties): NetConfig {
        val (url, port) = integrationTestProperties.rest.getValue("employee-profiles")
        return NetConfig(url, port)
    }

    @Bean
    fun restEmployeeSchedule(integrationTestProperties: IntegrationTestProperties): NetConfig {
        val (url, port) = integrationTestProperties.rest.getValue("employee-schedule")
        return NetConfig(url, port)
    }

    @Bean
    fun restShifts(integrationTestProperties: IntegrationTestProperties): NetConfig {
        val (url, port) = integrationTestProperties.rest.getValue("shifts")
        return NetConfig(url, port)
    }

    @Bean
    fun restTips(integrationTestProperties: IntegrationTestProperties): NetConfig {
        val (url, port) = integrationTestProperties.rest.getValue("tips")
        return NetConfig(url, port)
    }

    @Bean
    fun restApigateway(integrationTestProperties: IntegrationTestProperties): NetConfig {
        val (url, port) = integrationTestProperties.rest.getValue("my-samokat-apigw")
        return NetConfig(url, port)
    }

    @Bean
    fun restStaffMetadata(integrationTestProperties: IntegrationTestProperties): NetConfig {
        val (url, port) = integrationTestProperties.rest.getValue("staff-metadata")
        return NetConfig(url, port)
    }

    @Bean
    fun restStubSms(integrationTestProperties: IntegrationTestProperties): NetConfig {
        val (url, port) = integrationTestProperties.rest.getValue("stub-controller")
        return NetConfig(url, port)
    }

    @Bean
    fun restHrpMap(integrationTestProperties: IntegrationTestProperties): NetConfig {
        val (url, port) = integrationTestProperties.rest.getValue("hrp-map")
        return NetConfig(url, port)
    }

    @Bean
    fun restStaffCaveApigateway(integrationTestProperties: IntegrationTestProperties): NetConfig {
        val (url, port) = integrationTestProperties.rest.getValue("staff-cave-apigw")
        return NetConfig(url, port)
    }

    @Bean
    fun restStaffApigateway(integrationTestProperties: IntegrationTestProperties): NetConfig {
        val (url, port) = integrationTestProperties.rest.getValue("staff-apigw")
        return NetConfig(url, port)
    }
}