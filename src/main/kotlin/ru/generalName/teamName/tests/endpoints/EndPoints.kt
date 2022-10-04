package ru.samokat.mysamokat.tests.endpoints

/**
 * Описывает апишные урлы
 */
internal interface EndPoints {
    companion object {
        const val AUTH_URL = "https://ds-api-integration.samokat.io/authorization/password"
        const val SHIFTS_URL = "https://ds-api-integration.samokat.io/shifts"
    }
}