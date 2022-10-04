package ru.samokat.mysamokat.tests.dataproviders.helpers.database.employee_profiles_statistics

import org.jetbrains.exposed.sql.Table

object ShiftStatistics: Table(name = "shift_statistics") {
    val id = integer("id")
    val shiftId = uuid("shift_id")
    val darkstoreId = uuid("darkstore_id")
    val shiftType = text("shift_type")
    val startedAt = text("started_at")
    val stoppedAt = text("stopped_at")
    val deliveredOrdersCount = integer("delivered_orders_count")
    val zoneId = text("zone_id")
}