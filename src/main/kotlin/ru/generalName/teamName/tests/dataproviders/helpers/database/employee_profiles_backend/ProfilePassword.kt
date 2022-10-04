package ru.samokat.mysamokat.tests.dataproviders.helpers.database.employee_profiles_backend

import org.jetbrains.exposed.sql.Table

object ProfilePassword : Table(name = "profile_password") {
    val id = integer("id")
    val profileId = uuid("profile_id")
    val passwordHash = varchar("password_hash", 100)
    val createdAt = varchar("modified_at", 30)
}
