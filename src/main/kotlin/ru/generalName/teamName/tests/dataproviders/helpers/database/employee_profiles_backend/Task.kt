package ru.samokat.mysamokat.tests.dataproviders.helpers.database.employee_profiles_backend

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.`java-time`.timestamp

object Task : Table(name = "task") {
    val id = integer("id")
    val type = varchar("type", 64)
    val payload = text("payload")
    val attempts = integer("attempts")
    val createdAt = varchar("created_at", 30)
    val scheduledAt = timestamp("scheduled_at")
    val correlationId = varchar("correlation_id", 64)
    val traceInfo = text("trace_info")
}
