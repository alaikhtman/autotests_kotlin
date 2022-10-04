package ru.generalName.teamName.tests.dataproviders.helpers.database.schedule

import org.jetbrains.exposed.sql.Table

object WorkedOutTimeSlotLog : Table(name = "worked_out_time_slot_log") {
    val id = integer("id")
    val workedOutTimeSlotId = uuid("worked_out_time_slot_id")
    val editingReason = text("editing_reason")
    val data = text("data")
    val version = integer("version")
    val createdAt = varchar("created_at", 255)
    val issuerId = uuid("issuer_id")
}