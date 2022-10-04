package ru.samokat.mysamokat.tests.dataproviders.helpers.database.employee_profiles_backend

import org.jetbrains.exposed.sql.Table

object ProfileRequisition: Table(name = "profile_requisition") {
    val id = integer("id")
    val requestId = uuid("request_id")
    val accountingProfileId = text("accounting_profile_id")
    val fullName = varchar("full_name", 100).nullable()
    val mobile = varchar("mobile", 11).nullable()
    val status = varchar("status", 10)
    val version = integer("version")
    val modifiedAt = varchar("modified_at", 50)
    val type = varchar("type", 15)
}