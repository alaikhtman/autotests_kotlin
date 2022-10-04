package ru.samokat.mysamokat.tests.dataproviders.helpers.database.shifts_backend

import org.jetbrains.exposed.sql.Table

object ShiftAssignmentMetadata: Table(name = "shift_assignment_metadata") {
    val id = integer("id")
    val assignmentId = uuid("assignment_id")
    val metadata = text("metadata")
    val version = integer("version")
    val modifiedAt = varchar("modified_at", 50)
}