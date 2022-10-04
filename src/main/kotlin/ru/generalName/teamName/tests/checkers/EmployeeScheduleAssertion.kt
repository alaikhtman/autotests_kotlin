package ru.samokat.mysamokat.tests.checkers

import io.qameta.allure.Step
import org.assertj.core.api.SoftAssertions
import org.jetbrains.exposed.sql.ResultRow
import org.springframework.stereotype.Service
import ru.samokat.employeeschedule.api.timesheet.TimesheetStatus
import ru.samokat.employeeschedule.api.timesheet.TimesheetView
import ru.samokat.employeeschedule.api.workedouttimeslot.WorkedOutTimeSlotView
import ru.samokat.employeeschedule.api.workedouttimeslot.create.CreateWorkedOutTimeSlotRequest
import ru.samokat.employeeschedule.api.workedouttimeslot.update.UpdateWorkedOutTimeSlotRequest
import ru.samokat.my.rest.api.enum.ApiEnum
import ru.samokat.mysamokat.tests.dataproviders.helpers.database.employee_profiles_schedule.*
import ru.samokat.mysamokat.tests.helpers.controllers.events.employeeSchedule.IskhDannyeTabUchetaRabVremVneshnSotr
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*


@Service
class EmployeeScheduleAssertion {

    private val softAssertion = SoftAssertions()
    fun getSoftAssertion(): SoftAssertions {
        return softAssertion
    }

    fun assertAll() {
        getSoftAssertion().assertAll()
    }


    @Step("Check timeslot in BD")
    fun checkTimeSlotInDatabase(
        workedOutTimeslotFromDB: ResultRow,
        accountContractId: UUID,
        profileId: UUID,
        timesheetId: UUID,
        workedOutHours: Short,
        workedOutShiftId: UUID
    ): EmployeeScheduleAssertion {
        getSoftAssertion().assertThat(workedOutTimeslotFromDB[WorkedOutTimeSlot.accountingContractId])
            .isEqualTo(accountContractId)
        getSoftAssertion().assertThat(workedOutTimeslotFromDB[WorkedOutTimeSlot.profileId])
            .isEqualTo(profileId)
        getSoftAssertion().assertThat(workedOutTimeslotFromDB[WorkedOutTimeSlot.timesheetId])
            .isEqualTo(timesheetId)
        getSoftAssertion().assertThat(workedOutTimeslotFromDB[WorkedOutTimeSlot.workedOutHours].toShort())
            .isEqualTo(workedOutHours)
        getSoftAssertion().assertThat(workedOutTimeslotFromDB[WorkedOutTimeSlot.workedOutShiftId])
            .isEqualTo(workedOutShiftId)

        return this
    }

    @Step("Check several timeslots in DB")
    fun checkSeveralTimeSlotInDatabase(
        listOfWorkedOutTimeSlot: MutableList<ResultRow>,
        listOfAccountContractId: List<UUID>,
        listOfPofileId: List<UUID>,
        timesheetId: UUID,
        listOfWorkedOutHours: List<Short>,
        listOfWorkedOutShiftId: List<UUID>
    ): EmployeeScheduleAssertion {
        for (i in 0 until listOfWorkedOutTimeSlot.count()) {
            checkTimeSlotInDatabase(
                listOfWorkedOutTimeSlot[i],
                listOfAccountContractId[i],
                listOfPofileId[i],
                timesheetId,
                listOfWorkedOutHours[i],
                listOfWorkedOutShiftId[i]
            )
        }
        return this
    }


    @Step("Check timeslot is not in BD")
    fun checkTimeSlotNotInDatabase(
        timeslotExists: Boolean
    ): EmployeeScheduleAssertion {
        getSoftAssertion().assertThat(timeslotExists)
            .isFalse

        return this
    }


    @Step("Check timeslot log in BD")
    fun checkTimeSlotLogInDatabase(
        workedOutTimeSlotLog: MutableList<ResultRow>,
        count: Int,
        index: Int,
        timeEditingReason: String?,
        issuerId: UUID?,
        version: Long
    ): EmployeeScheduleAssertion {
        getSoftAssertion().assertThat(workedOutTimeSlotLog.count())
            .isEqualTo(count)
        getSoftAssertion().assertThat(workedOutTimeSlotLog[index][WorkedOutTimeSlotLog.editingReason])
            .isEqualTo(timeEditingReason)
        getSoftAssertion().assertThat(workedOutTimeSlotLog[index][WorkedOutTimeSlotLog.issuerId])
            .isEqualTo(issuerId)
        getSoftAssertion().assertThat(workedOutTimeSlotLog[index][WorkedOutTimeSlotLog.version])
            .isEqualTo(version)

        return this
    }

    @Step("Check timesheet in API")
    fun checkCreatedTimeSlotAPI(
        actualResponse: WorkedOutTimeSlotView,
        expectedRequest: CreateWorkedOutTimeSlotRequest,
        version: Long
    ): EmployeeScheduleAssertion {
        getSoftAssertion().assertThat(actualResponse.accountingContractId)
            .isEqualTo(expectedRequest.accountingContractId)
        getSoftAssertion().assertThat(actualResponse.profileId)
            .isEqualTo(expectedRequest.profileId)
        getSoftAssertion().assertThat(actualResponse.timesheetId)
            .isEqualTo(expectedRequest.timesheetId)
        getSoftAssertion().assertThat(actualResponse.workedOutHours)
            .isEqualTo(expectedRequest.workedOutHours)
        getSoftAssertion().assertThat(actualResponse.workedOutShiftId)
            .isEqualTo(expectedRequest.workedOutShiftId)
        getSoftAssertion().assertThat(actualResponse.version)
            .isEqualTo(version)

        return this

    }

    @Step("Check timesheet in API")
    fun checkAutoCreatedTimeSlotAPI(
        actualResponse: WorkedOutTimeSlotView,
        accountContractId: UUID,
        profileId: UUID,
        timesheetId: UUID,
        workedOutShiftId: UUID,
        workedOutHours: Short,
        version: Long
    ): EmployeeScheduleAssertion {
        getSoftAssertion().assertThat(actualResponse.accountingContractId)
            .isEqualTo(accountContractId)
        getSoftAssertion().assertThat(actualResponse.profileId)
            .isEqualTo(profileId)
        getSoftAssertion().assertThat(actualResponse.timesheetId)
            .isEqualTo(timesheetId)
        getSoftAssertion().assertThat(actualResponse.workedOutHours)
            .isEqualTo(workedOutHours)
        getSoftAssertion().assertThat(actualResponse.workedOutShiftId)
            .isEqualTo(workedOutShiftId)
        getSoftAssertion().assertThat(actualResponse.version)
            .isEqualTo(version)

        return this

    }

    @Step("Check timesheet in API")
    fun checkUpdatedTimeSlotAPI(
        actualResponse: WorkedOutTimeSlotView,
        expectedRequest: UpdateWorkedOutTimeSlotRequest,
        version: Long
    ): EmployeeScheduleAssertion {
        getSoftAssertion().assertThat(actualResponse.accountingContractId)
            .isEqualTo(expectedRequest.accountingContractId)
        getSoftAssertion().assertThat(actualResponse.workedOutHours)
            .isEqualTo(expectedRequest.workedOutHours)
        getSoftAssertion().assertThat(actualResponse.version)
            .isEqualTo(version)

        return this

    }

    @Step("Check timesheet not in DB")
    fun checkTimesheetNotInDatabase(timesheetExists: Boolean): EmployeeScheduleAssertion {
        getSoftAssertion().assertThat(timesheetExists)
            .isFalse
        return this
    }

    @Step("Check timesheet in DB")
    fun checkTimesheetInDatabase(
        timesheet: ResultRow?,
        darkstoreId: UUID,
        status: String
    ): EmployeeScheduleAssertion {
        getSoftAssertion().assertThat(timesheet!![Timesheet.darkstoreId])
            .isEqualTo(darkstoreId)
        getSoftAssertion().assertThat(timesheet[Timesheet.status])
            .isEqualTo(status)
        return this
    }

    @Step("Check timesheet API")
    fun checkTimesheetAPI(
        actualResponse: TimesheetView,
        darkstoreId: UUID,
        status: ApiEnum<TimesheetStatus, String>,
        modifiedAt: Instant,
        listOfWorkedOutShiftId: List<UUID>,
        listOfWorkedOutHours: List<Short>,
        listOfAccountContractId: List<UUID>,
        listOfProfileId: List<UUID>
    ): EmployeeScheduleAssertion {
        getSoftAssertion().assertThat(actualResponse.darkstoreId)
            .isEqualTo(darkstoreId)
        getSoftAssertion().assertThat(actualResponse.status)
            .isEqualTo(status)
        getSoftAssertion().assertThat(actualResponse.modifiedAt.truncatedTo(ChronoUnit.MINUTES))
            .isEqualTo(modifiedAt)

        for (i in 0 until actualResponse.workedOutTimeSlots.count()) {
            getSoftAssertion().assertThat(actualResponse.workedOutTimeSlots[i].workedOutShiftId)
                .isEqualTo(listOfWorkedOutShiftId[i])
            getSoftAssertion().assertThat(actualResponse.workedOutTimeSlots[i].workedOutHours)
                .isEqualTo(listOfWorkedOutHours[i])
            getSoftAssertion().assertThat(actualResponse.workedOutTimeSlots[i].accountingContractId)
                .isEqualTo(listOfAccountContractId[i])
            getSoftAssertion().assertThat(actualResponse.workedOutTimeSlots[i].profileId)
                .isEqualTo(listOfProfileId[i])
        }

        return this
    }

    @Step("Check timesheet API")
    fun checkTimesheetWithoutTimeslotsAPI(
        actualResponse: TimesheetView,
        darkstoreId: UUID,
        status: ApiEnum<TimesheetStatus, String>,
        modifiedAt: Instant,
    ): EmployeeScheduleAssertion {
        getSoftAssertion().assertThat(actualResponse.darkstoreId)
            .isEqualTo(darkstoreId)
        getSoftAssertion().assertThat(actualResponse.status)
            .isEqualTo(status)
        getSoftAssertion().assertThat(actualResponse.modifiedAt.truncatedTo(ChronoUnit.MINUTES))
            .isEqualTo(modifiedAt)
        getSoftAssertion().assertThat(actualResponse.workedOutTimeSlots.count())
            .isEqualTo(0)


        return this
    }


    @Step("Check timesheet auto submit task")
    fun checkTimesheetAutoTaskTime(taskScheduleTime: String, expectedScheduleTime: String): EmployeeScheduleAssertion {
        getSoftAssertion().assertThat(taskScheduleTime).isEqualTo(expectedScheduleTime)

        return this
    }

    @Step("Check timesheet auto submit task is existed")
    fun checkTimesheetAutoTask(taskExist: Boolean): EmployeeScheduleAssertion {
        getSoftAssertion().assertThat(taskExist).isTrue

        return this
    }

    @Step("Check billed timeslot in DB")
    fun checkBilledTimeslotInDatabase(
        billedTimeslots: MutableList<ResultRow>,
        count: Int,
        accountContractIds: MutableList<UUID>,
        totalWorkedHours: MutableList<Int>
    ): EmployeeScheduleAssertion {
        getSoftAssertion().assertThat(billedTimeslots.count())
            .isEqualTo(count)
        for (i in 0 until billedTimeslots.count()) {
            getSoftAssertion().assertThat(billedTimeslots[i][BilledTimeSlot.accountingContractId])
                .isEqualTo(accountContractIds[i])
            getSoftAssertion().assertThat(billedTimeslots[i][BilledTimeSlot.totalWorkedOutHours])
                .isEqualTo(totalWorkedHours[i])

        }
        return this
    }

    @Step("Check billed timeslot is absent in DB")
    fun checkBilledTimeslotNotInDatabase(billedTimeslotExist: Boolean): EmployeeScheduleAssertion {
        getSoftAssertion().assertThat(billedTimeslotExist)
            .isFalse
        return this

    }

    @Step("Check billed timeslot error in DB")
    fun checkErrorBilledTimeslotInDatabase(
        billedTimeslotError: ResultRow?,
        errorText: String?
    ): EmployeeScheduleAssertion {
        getSoftAssertion().assertThat(billedTimeslotError!![BilledTimeSlotError.error]).isEqualTo(errorText)
        return this
    }

    @Step("Check billed timeslot is absent in DB")
    fun checkErrorBilledTimeslotNotInDatabase(billedTimeslotErrorExist: Boolean): EmployeeScheduleAssertion {
        getSoftAssertion().assertThat(billedTimeslotErrorExist)
            .isFalse
        return this

    }

    @Step("Check billed timesheet topic")
    fun checkBilledTimesheetKafkaTopic(
        message: IskhDannyeTabUchetaRabVremVneshnSotr,
        darkstoreId: UUID,
        accountingContractId: UUID,
        workedOutHours: Int

    ): EmployeeScheduleAssertion {
        getSoftAssertion().assertThat(message.payload[0].podrazdelenie.guid).isEqualTo(darkstoreId.toString())
        getSoftAssertion().assertThat(message.payload[0].sotrudnik.guid).isEqualTo(accountingContractId.toString())
        getSoftAssertion().assertThat(message.payload[0].chasy).isEqualTo(workedOutHours)

        return this
    }
}