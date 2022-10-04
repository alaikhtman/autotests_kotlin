package ru.generalName.teamName.tests.helpers.controllers.database

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.stereotype.Service
import ru.generalName.teamName.tests.dataproviders.helpers.database.schedule.Task
import ru.generalName.teamName.tests.dataproviders.helpers.database.schedule.Timesheet
import ru.generalName.teamName.tests.dataproviders.helpers.database.schedule.WorkedOutTimeSlot
import ru.generalName.teamName.tests.dataproviders.helpers.database.schedule.WorkedOutTimeSlotLog
import java.time.LocalDate
import java.util.*

@Service
class ScheduleDatabaseController(private val employeeScheduleDatabase: Database) {


    fun getWorkedOutTimeSlot(workedOutTimeSlotId: UUID): ResultRow {
        var workedOutTimeSlot: ResultRow? = null
        transaction(employeeScheduleDatabase) {
            addLogger(StdOutSqlLogger)
            workedOutTimeSlot =
                WorkedOutTimeSlot.select { WorkedOutTimeSlot.workedOutTimeSlotId eq workedOutTimeSlotId }.single()
        }
        return workedOutTimeSlot!!
    }

    fun getWorkedOutTimeSlotByShift(workedOutShiftId: UUID): UUID {
        var workedOutTimeSlot: ResultRow? = null
        transaction(employeeScheduleDatabase) {
            addLogger(StdOutSqlLogger)
            workedOutTimeSlot =
                WorkedOutTimeSlot.select { WorkedOutTimeSlot.workedOutShiftId eq workedOutShiftId }.single()
        }
        return workedOutTimeSlot!![WorkedOutTimeSlot.workedOutTimeSlotId]
    }


    fun getListOfWorkedOutTimeSlot(listOfWorkedOutTimeSlotId: MutableList<UUID>): MutableList<ResultRow> {
        val listOfWorkedOutTimeSlot: MutableList<ResultRow> = mutableListOf()
        transaction(employeeScheduleDatabase) {
            addLogger(StdOutSqlLogger)
            for (i in 0..listOfWorkedOutTimeSlot.count()) {
                listOfWorkedOutTimeSlot.add(WorkedOutTimeSlot.select { WorkedOutTimeSlot.workedOutTimeSlotId eq listOfWorkedOutTimeSlotId[i] }
                    .single())
            }
        }
        return listOfWorkedOutTimeSlot
    }


    fun getWorkedOutTimeSlotLog(workedOutTimeSlotId: UUID): MutableList<ResultRow> {
        val workedOutTimeSlotLog: MutableList<ResultRow> = mutableListOf()
        transaction(employeeScheduleDatabase) {
            addLogger(StdOutSqlLogger)
            val query = WorkedOutTimeSlotLog.select { WorkedOutTimeSlotLog.workedOutTimeSlotId eq workedOutTimeSlotId }
            query.forEach {
                workedOutTimeSlotLog.add(it)
            }
        }

        return workedOutTimeSlotLog
    }

    fun getTimesheet(timesheetId: UUID): ResultRow? {
        var timesheet: ResultRow? = null
        transaction(employeeScheduleDatabase) {
            addLogger(StdOutSqlLogger)
            timesheet = Timesheet.select { Timesheet.timesheetId eq timesheetId }.single()
        }
        return timesheet
    }

    fun checkTimeslotExist(profileId: UUID): Boolean {
        var exists = false
        transaction(employeeScheduleDatabase) {
            addLogger(StdOutSqlLogger)
            if (WorkedOutTimeSlot.select { (WorkedOutTimeSlot.profileId eq profileId) }
                    .count() > 0) {
                exists = true
            }
        }
        return exists
    }

    fun checkTimesheetExist(darkstoreId: UUID, date: LocalDate): Boolean {
        var exists = false
        transaction(employeeScheduleDatabase) {
            addLogger(StdOutSqlLogger)
            if (Timesheet.select { (Timesheet.darkstoreId eq darkstoreId) and (Timesheet.timesheetDate eq date) }
                    .count() > 0) {
                exists = true
            }
        }
        return exists
    }

    fun deleteTimesheetById(timesheetId: UUID) {
        transaction(employeeScheduleDatabase) {
            addLogger(StdOutSqlLogger)
            Timesheet.deleteWhere { Timesheet.timesheetId eq timesheetId }

        }
    }

    fun deleteTimesheetByDarkstore(darkstoreId: UUID, date: LocalDate) {
        transaction(employeeScheduleDatabase) {
            addLogger(StdOutSqlLogger)
            Timesheet.deleteWhere { (Timesheet.darkstoreId eq darkstoreId) and (Timesheet.timesheetDate eq date) }

        }
    }

    fun updateTimesheetInDB(
        darkstoreId: UUID,
        date: LocalDate,
        newDate: LocalDate
    ) {
        transaction(employeeScheduleDatabase) {
            addLogger(StdOutSqlLogger)
            Timesheet.update({ (Timesheet.darkstoreId eq darkstoreId) and (Timesheet.timesheetDate eq date) }) {
                it[timesheetDate] = newDate
            }
        }
    }

    fun getTask(type: String, correlationId: String): String {
        var task: ResultRow? = null
        transaction(employeeScheduleDatabase) {
            addLogger(StdOutSqlLogger)
            task = Task.select { (Task.type eq type) and (Task.correlationId eq correlationId) }.single()
        }
        return task!![Task.scheduledAt]
    }

    fun checkTaskExist(type: String, correlationId: String): Boolean {
        var exists = false
        transaction(employeeScheduleDatabase) {
            addLogger(StdOutSqlLogger)
            if (Task.select { (Task.type eq type) and (Task.correlationId eq correlationId) }.count() > 0) {
                exists = true
            }
        }
        return exists
    }

    fun getBilledTimeSlotId(timesheetId: UUID, accountingContractId: UUID): UUID {
        var billedTimeSlot: ResultRow? = null
        transaction(employeeScheduleDatabase) {
            addLogger(StdOutSqlLogger)
            billedTimeSlot =
                BilledTimeSlot.select { (BilledTimeSlot.accountingContractId eq accountingContractId) and (BilledTimeSlot.timesheetId eq timesheetId) }
                    .single()
        }
        return billedTimeSlot!![BilledTimeSlot.billedTimeSlotId]
    }

    fun getBilledTimeSlotByTimesheet(timesheetId: UUID): MutableList<ResultRow> {
        var timesheetBilledTimeSlot: MutableList<ResultRow> = mutableListOf()
        transaction(employeeScheduleDatabase) {
            addLogger(StdOutSqlLogger)
            var query =
                BilledTimeSlot.select { (BilledTimeSlot.timesheetId eq timesheetId) }
            query.forEach {
                timesheetBilledTimeSlot.add(it)
            }

        }
        return timesheetBilledTimeSlot
    }

    fun checkBilledTimeSlotExist(timesheetId: UUID, accountingContractId: UUID): Boolean {
        var exists = false
        transaction(employeeScheduleDatabase) {
            addLogger(StdOutSqlLogger)
            if (BilledTimeSlot.select { (BilledTimeSlot.accountingContractId eq accountingContractId) and (BilledTimeSlot.timesheetId eq timesheetId) }
                    .count() > 0) {
                exists = true
            }
        }
        return exists
    }

    fun getBilledTimeSlotError(billedTimeSlotId: UUID): ResultRow? {
        var billedTimeSlotError: ResultRow? = null
        transaction(employeeScheduleDatabase) {
            addLogger(StdOutSqlLogger)
            billedTimeSlotError =
                BilledTimeSlotError.select { (BilledTimeSlotError.billedTimeSlotId eq billedTimeSlotId) }
                    .single()
        }
        return billedTimeSlotError
    }

    fun checkBilledTimeSlotErrorExist(billedTimeSlotId: UUID): Boolean {
        var exists = false
        transaction(employeeScheduleDatabase) {
            addLogger(StdOutSqlLogger)
            if (BilledTimeSlotError.select { (BilledTimeSlotError.billedTimeSlotId eq billedTimeSlotId) }
                    .count() > 0) {
                exists = true
            }
        }
        return exists
    }

}