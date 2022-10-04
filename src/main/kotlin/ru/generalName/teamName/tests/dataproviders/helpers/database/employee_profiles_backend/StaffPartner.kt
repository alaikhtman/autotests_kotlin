package ru.samokat.mysamokat.tests.dataproviders.helpers.database.employee_profiles_backend

import org.jetbrains.exposed.sql.Table

object StaffPartner : Table(name = "staff_partner") {
    val id = integer("id")
    val partnerId = uuid("partner_id")
    val partnerTitle = varchar("partner_title", 256)
    val partnerType = varchar("partner_type", 256)
    val createdAt = varchar("created_at", 30)
    val partnerShortTitle = varchar("partner_short_title", 128)

}
