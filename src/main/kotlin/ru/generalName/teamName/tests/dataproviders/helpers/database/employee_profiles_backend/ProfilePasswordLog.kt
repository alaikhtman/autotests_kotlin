package ru.samokat.mysamokat.tests.dataproviders.helpers.database.employee_profiles_backend

import org.jetbrains.exposed.sql.Table

object ProfilePasswordLog : Table(name = "profile_password_log") {
    val id = integer("id")
    val profileId = uuid("profile_id")
    val type = varchar("type", 10)
    val issuerProfileId = uuid("issuer_profile_id")
    val createdAt = varchar("created_at", 30)
}