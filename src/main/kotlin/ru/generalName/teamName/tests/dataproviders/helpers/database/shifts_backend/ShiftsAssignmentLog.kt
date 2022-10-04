package ru.samokat.mysamokat.tests.dataproviders.helpers.database.shifts_backend
import org.jetbrains.exposed.sql.Table
import java.util.*

object ShiftsAssignmentLog: Table (name = "shift_assignment_log") {
    val id = integer("id")
    val assignmentId = uuid("assignment_id")
    val eventType = varchar("event_type", 30)
    val version = integer("version")
    val created_at = varchar("created_at", 50)
    val issuerId = uuid("issuer_id")
    val data = text("data")
}

data class ShiftsAssignmentsLogData(
    val userId: UUID?,
    val darkstoreId: UUID?,
    val timeRangeStart: String,
    val timeRangeEnd: String,
    val comment: String?,
    val userRole: String?
)

