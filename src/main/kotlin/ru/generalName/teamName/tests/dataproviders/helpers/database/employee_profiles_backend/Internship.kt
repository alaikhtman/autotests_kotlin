package ru.samokat.mysamokat.tests.dataproviders.helpers.database.employee_profiles_backend

import org.jetbrains.exposed.sql.Table

object Internship : Table(name = "internship") {
    val id = integer("id")
    val profileId = uuid("profile_id")
    val role = varchar("role", 255)
    val darkstoreId = uuid("darkstore_id")
    val date = text ("date")
    val status = text("status")
    val failureReason = text("failure_reason")
    val rejectedReason = text("rejection_reason")
    val version = integer("version")
    val modifiedAt = varchar("modified_at", 255)

}