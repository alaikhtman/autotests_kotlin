package ru.samokat.mysamokat.tests.dataproviders.helpers.database.employee_profiles_schedule

import org.jetbrains.exposed.sql.Table

object BilledTimeSlotError : Table(name = "billed_time_slot_error") {
    val id = integer("id")
    val billedTimeSlotId = uuid("billed_time_slot_id")
    val error = text("error")
    val createdAt = varchar("created_at", 255)

}