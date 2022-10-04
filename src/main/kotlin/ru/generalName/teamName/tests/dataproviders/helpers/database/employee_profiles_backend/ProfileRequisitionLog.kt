package ru.samokat.mysamokat.tests.dataproviders.helpers.database.employee_profiles_backend

import org.jetbrains.exposed.sql.Table

object ProfileRequisitionLog: Table(name = "profile_requisition_log") {
    val id = integer("id")
    val requestId = uuid("request_id")
    val issuerId = uuid("issuer_id")
    val data = text("data")
    val version = integer("version")
    val createdAt = varchar("created_at", 50)
}