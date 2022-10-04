package ru.samokat.mysamokat.tests.dataproviders.helpers.database.employee_profiles_schedule

import org.jetbrains.exposed.sql.Table

object BilledTimeSlot : Table(name = "billed_time_slot") {
    val id = integer("id")
    val billedTimeSlotId = uuid("billed_time_slot_id")
    val accountingContractId = uuid("accounting_contract_id")
    val timesheetId = uuid("timesheet_id")
    val totalWorkedOutHours = integer("total_worked_out_hours")
    val createdAt = varchar("created_at", 255)

}