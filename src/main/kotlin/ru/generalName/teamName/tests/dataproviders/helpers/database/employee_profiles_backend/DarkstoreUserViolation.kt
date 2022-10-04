package ru.samokat.mysamokat.tests.dataproviders.helpers.database.employee_profiles_backend

import org.jetbrains.exposed.sql.Table

object DarkstoreUserViolation : Table(name = "darkstore_user_violation") {
    val id = integer("id")
    val violationId = uuid("violation_id")
    val darkstoreId = uuid("darkstore_id")
    val profileId = uuid("profile_id")
    val role = varchar("role", 255)
    val violation_code = varchar("violation_code", 30)
    val createdAt = varchar("created_at", 30)
    val issuerId = text("issuer")
    val version = long("version")
    val comment = text("comment")
}
