package ru.samokat.mysamokat.tests.dataproviders.helpers.database.employee_profiles_backend

import org.jetbrains.exposed.sql.Table

object DarkstoreUserLog : Table(name = "darkstore_user_log") {
    val id = integer("id")
    val profileId = uuid("profile_id")
    val darkstoreId = uuid("darkstore_id")
    val role = varchar("role", 255)
    val type = text("type")
    val issuerId = text("issuer_id")
    val data = varchar("data", 255)
    val version = long("version")
    val createdAt = varchar("created_at", 30)
}
