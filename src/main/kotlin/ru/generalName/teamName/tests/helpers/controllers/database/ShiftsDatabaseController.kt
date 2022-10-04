package ru.samokat.mysamokat.tests.helpers.controllers.database

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.stereotype.Service
import ru.samokat.mysamokat.tests.dataproviders.helpers.database.shifts_backend.*
import java.sql.Timestamp
import java.time.Instant
import java.util.*

@Service
class ShiftsDatabaseController(
    private val shiftsDatabase: Database
) {

    fun getAssignmentLog(assignmentId: UUID): MutableList<ResultRow> {
        var log: MutableList<ResultRow>? = mutableListOf()
        transaction(shiftsDatabase) {
            addLogger(StdOutSqlLogger)
            var query = ShiftsAssignmentLog.select { ShiftsAssignmentLog.assignmentId eq assignmentId }
            (query as Query).forEach {
                log?.add(it)
            }
        }
        return log!!
    }

    fun getAssignmentLogByType(assignmentId: UUID, eventType: String): ResultRow? {
        var log: ResultRow? = null
        transaction(shiftsDatabase) {
            addLogger(StdOutSqlLogger)
            log =
                ShiftsAssignmentLog.select { (ShiftsAssignmentLog.assignmentId eq assignmentId) and (ShiftsAssignmentLog.eventType eq eventType) }
                    .single()
        }
        return log!!
    }

    fun getAssignmentById(assignmentId: UUID): ResultRow {
        var assignment: ResultRow? = null
        transaction(shiftsDatabase) {
            addLogger(StdOutSqlLogger)
            assignment = ShiftsAssignment.select { ShiftsAssignment.assignmentId eq assignmentId }.single()
        }
        return assignment!!
    }


    fun getSchedules(profileId: UUID): MutableList<ResultRow> {
        var schedules: MutableList<ResultRow>? = mutableListOf()
        transaction(shiftsDatabase) {
            addLogger(StdOutSqlLogger)
            var query = ShiftSchedule.select { ShiftSchedule.userId eq profileId }
            (query as Query).forEach {
                schedules?.add(it)
            }
        }
        return schedules!!
    }

    fun deleteAssignmentMetadataLogById(assignmentId: UUID) {
        transaction(shiftsDatabase) {
            addLogger(StdOutSqlLogger)
            ShiftAssignmentMetadataLog.deleteWhere { ShiftAssignmentMetadataLog.assignmentId eq assignmentId }
        }
    }

    fun deleteAssignmentMetadataById(assignmentId: UUID) {
        transaction(shiftsDatabase) {
            addLogger(StdOutSqlLogger)
            ShiftAssignmentMetadata.deleteWhere { ShiftAssignmentMetadata.assignmentId eq assignmentId }
        }
    }

    fun deleteAssignmentById(assignmentId: UUID) {
        transaction(shiftsDatabase) {
            addLogger(StdOutSqlLogger)
            ShiftsAssignment.deleteWhere { ShiftsAssignment.assignmentId eq assignmentId }
        }
    }

    fun deleteAssignmentLogById(assignmentId: UUID) {
        transaction(shiftsDatabase) {
            addLogger(StdOutSqlLogger)
            ShiftsAssignmentLog.deleteWhere { ShiftsAssignmentLog.assignmentId eq assignmentId }
        }
    }


    fun updateShiftInDB(shiftId: UUID, newDate: Instant) {
        transaction(shiftsDatabase) {
            addLogger(StdOutSqlLogger)
            Shift.update({ (Shift.shiftId eq shiftId) }) {
                it[startedAt] = newDate
            }
        }
    }
}
