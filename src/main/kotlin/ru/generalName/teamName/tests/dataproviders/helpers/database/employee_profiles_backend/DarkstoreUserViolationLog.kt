package ru.samokat.mysamokat.tests.dataproviders.helpers.database.employee_profiles_backend

import org.jetbrains.exposed.sql.Table

object DarkstoreUserViolationLog : Table(name = "darkstore_user_violation_log") {
    val id = integer("id")
    val violationId = uuid("violation_id")
    val darkstoreId = uuid("darkstore_id")
    val profileId = uuid("profile_id")
    val role = varchar("role", 255)
    val issuerId = text("issuer")
    val createdAt = varchar("created_at", 30)
    val version = long("version")
    val data = text("data")
    val type = text("type")
}
