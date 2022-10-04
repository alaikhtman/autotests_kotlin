package ru.samokat.mysamokat.tests.dataproviders.helpers.database.shifts_backend

import org.jetbrains.exposed.sql.Table

object ShiftAssignmentMetadataLog: Table(name = "shift_assignment_metadata_log") {
    val id = integer("id")
    val assignmentId = uuid("assignment_id")
    val metadata = text("metadata")
    val version = integer("version")
    val createdAt = varchar("created_at", 50)
    val issuerId = uuid("issuer_id")
}