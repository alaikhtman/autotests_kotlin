package ru.samokat.mysamokat.tests.dataproviders.helpers.database.apigateway

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.`java-time`.timestamp

object Task: Table(name = "task") {
    val id = integer("id")
    val type = varchar("type", 30)
    val payload = text("payload")
    val attempts = integer("attempts")
    val createdAt = timestamp("created_at")
    val scheduledAt = timestamp("scheduled_at")
    val correlationId = text("correlation_id")
    val traceInfo = text("trace_info")
}