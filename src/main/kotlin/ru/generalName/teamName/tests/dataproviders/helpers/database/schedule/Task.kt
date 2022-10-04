package ru.generalName.teamName.tests.dataproviders.helpers.database.schedule

import org.jetbrains.exposed.sql.Table

object Task : Table(name = "Task") {
    val id = integer("id")
    val type = varchar("type", 255)
    val payload = text ("payload")
    val attempts = integer("attempts")
    val createdAt =  varchar("created_at", 255)
    val scheduledAt = varchar("scheduled_at", 255)
    val correlationId = varchar("correlation_id", 255)
    val traceInfo = text ("trace_info")
}