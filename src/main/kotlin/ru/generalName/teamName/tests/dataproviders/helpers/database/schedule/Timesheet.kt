package ru.generalName.teamName.tests.dataproviders.helpers.database.schedule

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.`java-time`.date

object Timesheet : Table(name = "timesheet") {
    val id = integer("id")
    val timesheetId = uuid("timesheet_id")
    val timesheetDate = date("timesheet_date")
    val darkstoreId = uuid("darkstore_id")
    val status = text("status")
    val modifiedAt = varchar("modified_at", 255)
}