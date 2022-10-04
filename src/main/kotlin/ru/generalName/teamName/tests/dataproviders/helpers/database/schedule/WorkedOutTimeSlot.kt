package ru.generalName.teamName.tests.dataproviders.helpers.database.schedule

import org.jetbrains.exposed.sql.Table

object WorkedOutTimeSlot : Table(name = "worked_out_time_slot") {
    val id = integer("id")
    val workedOutTimeSlotId = uuid("worked_out_time_slot_id")
    val workedOutShiftId = uuid("worked_out_shift_id")
    val accountingContractId = uuid("accounting_contract_id")
    val profileId = uuid("profile_id")
    val timesheetId = uuid("timesheet_id")
    val workedOutHours = integer("worked_out_hours")
    val version = integer("version")
    val modifiedAt = varchar("modified_at", 255)
}