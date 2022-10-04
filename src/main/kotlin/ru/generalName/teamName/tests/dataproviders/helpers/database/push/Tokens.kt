package ru.samokat.mysamokat.tests.dataproviders.helpers.database.push

import org.jetbrains.exposed.sql.Table

object Tokens : Table(name = "tokens") {
    val userId = uuid("user_id")
    val applicationId = integer("application_id")
    val token = text("token")
    val providerId = integer("provider_id")
    val registrationDate = varchar("registration_date", 255)
}