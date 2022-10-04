package ru.samokat.mysamokat.tests.dataproviders.preconditions

import io.qameta.allure.Step
import ru.samokat.employeeschedule.api.timesheet.get.GetTimesheetRequest
import ru.samokat.employeeschedule.api.workedouttimeslot.create.CreateWorkedOutTimeSlotRequest
import ru.samokat.employeeschedule.api.workedouttimeslot.update.UpdateWorkedOutTimeSlotRequest
import ru.samokat.mysamokat.tests.dataproviders.employee_schedule.CreateWorkedOutTimeSlotRequestBuilder
import ru.samokat.mysamokat.tests.dataproviders.employee_schedule.GetTimesheetRequestBuilder
import ru.samokat.mysamokat.tests.dataproviders.employee_schedule.UpdateWorkedOutTimeSlotRequestBuilder
import ru.samokat.mysamokat.tests.helpers.controllers.events.employee.Headers
import ru.samokat.mysamokat.tests.helpers.controllers.events.employeeSchedule.ErrorPayload
import ru.samokat.mysamokat.tests.helpers.controllers.events.employeeSchedule.IskhDannyeTabUchetaError
import java.time.LocalDate
import java.util.*


class EmployeeSchedulePreconditions {


    private lateinit var createWorkedOutTimesheetRequest: CreateWorkedOutTimeSlotRequest
    fun createWorkedOutTimesheetRequest(): CreateWorkedOutTimeSlotRequest {
        return createWorkedOutTimesheetRequest
    }

    private var listOfCreateWorkedOutTimesheetRequest: MutableList<CreateWorkedOutTimeSlotRequest> = mutableListOf()
    fun listOfCreateWorkedOutTimesheetRequest(): MutableList<CreateWorkedOutTimeSlotRequest> {
        return listOfCreateWorkedOutTimesheetRequest
    }

    private lateinit var updateWorkedOutTimeSlotRequest: UpdateWorkedOutTimeSlotRequest
    fun updateWorkedOutTimeSlotRequest(): UpdateWorkedOutTimeSlotRequest {
        return updateWorkedOutTimeSlotRequest
    }

    private var listOfUpdateWorkedOutTimesheetRequest: MutableList<UpdateWorkedOutTimeSlotRequest> = mutableListOf()
    fun listOfUpdateWorkedOutTimesheetRequest(): MutableList<UpdateWorkedOutTimeSlotRequest> {
        return listOfUpdateWorkedOutTimesheetRequest
    }

    private lateinit var getTimesheetRequest: GetTimesheetRequest
    fun getTimesheetRequest(): GetTimesheetRequest {
        return getTimesheetRequest
    }

    @Step("Set create workedOut timeslot request")
    fun setCreateWorkedOutTimeslotRequest(
        timesheetId: UUID,
        profileId: UUID,
        workedOutShiftId: UUID,
        workedOutHours: Short,
        timeEditingReason: String? = null,
        accountingContractId: UUID,
        issuerId: UUID
    ) = apply {
        this.createWorkedOutTimesheetRequest = CreateWorkedOutTimeSlotRequestBuilder()
            .timesheetId(timesheetId)
            .profileId(profileId)
            .workedOutShiftId(workedOutShiftId)
            .workedOutHours(workedOutHours)
            .timeEditingReason(timeEditingReason)
            .accountingContractId(accountingContractId)
            .issuerId(issuerId)
            .build()
    }

    @Step("Set list of create workedOut timeslot request")
    fun setListCreateWorkedOutTimeslotRequest(
        amount: Int,
        timesheetId: UUID,
        listOfProfileId: MutableList<UUID>,
        listOfWorkedOutShiftId: List<UUID>,
        listOfWorkedOutHours: MutableList<Short>,
        timeEditingReason: String? = null,
        listOfAccountingContractId: MutableList<UUID>,
        issuerId: UUID
    ): EmployeeSchedulePreconditions {
        for (i in 0 until amount) {
            this.listOfCreateWorkedOutTimesheetRequest.add(
                CreateWorkedOutTimeSlotRequestBuilder()
                    .timesheetId(timesheetId)
                    .profileId(listOfProfileId[i])
                    .workedOutShiftId(listOfWorkedOutShiftId[i])
                    .workedOutHours(listOfWorkedOutHours[i])
                    .timeEditingReason(timeEditingReason)
                    .accountingContractId(listOfAccountingContractId[i])
                    .issuerId(issuerId)
                    .build()
            )
        }
        return this
    }

    @Step("Set update workedOut timeslot request")
    fun setUpdateWorkedOutTimeslotRequest(
        workedOutHours: Short,
        timeEditingReason: String? = null,
        accountingContractId: UUID,
        issuerId: UUID = UUID.randomUUID(),
        version: Long = 1
    ) = apply {
        this.updateWorkedOutTimeSlotRequest = UpdateWorkedOutTimeSlotRequestBuilder()
            .workedOutHours(workedOutHours)
            .timeEditingReason(timeEditingReason)
            .accountingContractId(accountingContractId)
            .issuerId(issuerId)
            .version(version)
            .build()
    }

    @Step("Set list of update workedOut timeslot request")
    fun setListOfUpdateWorkedOutTimeslotRequest(
        listOfWorkedOutHours: MutableList<Short>,
        listOfTimeEditingReason: MutableList<String>? = null,
        listOfAccountingContractId: List<UUID>,
        issuerId: UUID = UUID.randomUUID(),
        amount: Int
    ) {
        for (i in 0 until amount) {
            var version = i.toLong()
            version++
            this.listOfUpdateWorkedOutTimesheetRequest.add(
                UpdateWorkedOutTimeSlotRequestBuilder()
                    .workedOutHours(listOfWorkedOutHours[i])
                    .timeEditingReason(listOfTimeEditingReason?.get(i))
                    .accountingContractId(listOfAccountingContractId[i])
                    .issuerId(issuerId)
                    .version(version)
                    .build()
            )
        }
    }


    @Step("Set timesheet request")
    fun setTimesheetRequest(
        date: LocalDate
    ) = apply {
        this.getTimesheetRequest = GetTimesheetRequestBuilder()
            .date(date)
            .build()

    }

    @Step("Fill billedTimeslot error event")
    fun fillBilledTimeslotErrorEvent(error: Boolean, errorText: String? = null): IskhDannyeTabUchetaError {
        return IskhDannyeTabUchetaError(
            headers = set1CEventHeaders(), payload = listOf(
                ErrorPayload(error = error, errorText = errorText)
            )
        )

    }

    fun set1CEventHeaders(
        dateInMilliseconds: Long? = System.currentTimeMillis(),
        event: String = "ПриемНаРаботу"
    ): Headers {
        return Headers(
            id = UUID.randomUUID(),
            date = "2021-10-13T14:53:50Z",
            event = event,
            dateInMilliseconds = dateInMilliseconds,
            multithreadingAnalytics = "",
            multithreadingDate = "2021-10-05T13:38:47Z",
            singleThreaded = false,
            addressForResult = "http://hw-011011005.samokat.io/base4_sobolev_01/hs/RequestAPI"
        )
    }


}