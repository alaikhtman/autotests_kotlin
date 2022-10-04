package ru.samokat.mysamokat.tests.dataproviders.helpers.database.employee_profiles_backend

import org.jetbrains.exposed.sql.Table

object Vehicle : Table(name = "vehicle") {
    val id = integer("id")
    val type = varchar("type", 30)
    val profileId = uuid("profile_id")
    val createdAt = varchar("created_at", 30)
}
