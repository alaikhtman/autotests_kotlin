package ru.samokat.mysamokat.tests.dataproviders.helpers.database.employee_profiles_backend

import org.jetbrains.exposed.sql.Table

object ProfileLog : Table(name = "profile_log") {
    val id = integer("id")
    val profileId = uuid("profile_id")
    val createdAt = varchar("created_at", 30)
    val version = long("version")
    val type = varchar("type", 256)
    val data = text("data")
    val issuerId = uuid("issuer_id")

}