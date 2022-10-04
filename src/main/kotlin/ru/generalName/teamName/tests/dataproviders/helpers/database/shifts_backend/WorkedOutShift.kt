package ru.samokat.mysamokat.tests.dataproviders.helpers.database.shifts_backend

import org.jetbrains.exposed.sql.Table

object WorkedOutShift: Table(name = "worked_out_shift") {
    val id = integer("id")
    val shiftId = uuid("shift_id")
    val userId = uuid ("user_id")
    val userRole = varchar("user_role", 15)
    val darkstoreId = uuid("darkstore_id")
    val timeSlot = text("time_slot")
    val stopType = varchar("stop_type", 15)
    val deliveryMethod = varchar("delivery_method", 15)

}