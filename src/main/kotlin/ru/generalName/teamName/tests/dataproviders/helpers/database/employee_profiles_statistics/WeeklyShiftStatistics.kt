package ru.samokat.mysamokat.tests.dataproviders.helpers.database.employee_profiles_statistics

import org.jetbrains.exposed.sql.Table

object WeeklyShiftStatistics: Table(name = "weekly_shift_statistics") {

    val id = integer("id")
    val userId = uuid("user_id")
    val deliverymanShiftsCount = integer("deliveryman_shifts_count")
    val deliverymanShiftsDuration = text("deliveryman_shifts_duration")
    val pickerShiftsCount = integer("picker_shifts_count")
    val pickerShiftsDuration = text("picker_shifts_duration")
    val deliveredOrdersCount = integer("delivered_orders_count")
}