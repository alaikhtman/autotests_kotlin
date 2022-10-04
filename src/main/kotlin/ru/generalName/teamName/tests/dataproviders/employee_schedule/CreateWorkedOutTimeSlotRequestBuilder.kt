package ru.samokat.mysamokat.tests.dataproviders.employee_schedule

import ru.samokat.employeeschedule.api.workedouttimeslot.create.CreateWorkedOutTimeSlotRequest
import java.util.*

class CreateWorkedOutTimeSlotRequestBuilder {

    private lateinit var timesheetId: UUID
    fun timesheetId(timesheetId: UUID) = apply { this.timesheetId = timesheetId }
    fun getTimesheetId(): UUID {
        return timesheetId
    }

    private lateinit var profileId: UUID
    fun profileId(profileId: UUID) = apply { this.profileId = profileId }
    fun getProfileId(): UUID {
        return profileId
    }

    private lateinit var workedOutShiftId: UUID
    fun workedOutShiftId(workedOutShiftId: UUID) = apply { this.workedOutShiftId = workedOutShiftId }
    fun getWorkedOutShiftId(): UUID {
        return workedOutShiftId
    }

    private var timeEditingReason: String? = null
    fun timeEditingReason(timeEditingReason: String?) = apply { this.timeEditingReason = timeEditingReason }
    fun getTimeEditingReason(): String? {
        return timeEditingReason
    }

    private var workedOutHours: Short = 1
    fun workedOutHours(workedOutHours: Short) = apply { this.workedOutHours = workedOutHours }
    fun getWorkedOutHours(): Short {
        return workedOutHours
    }

    private lateinit var accountingContractId: UUID
    fun accountingContractId(accountingContractId: UUID) = apply { this.accountingContractId = accountingContractId }
    fun getAccountingContractId(): UUID {
        return accountingContractId
    }

    private lateinit var issuerId: UUID
    fun issuerId(issuerId: UUID) = apply { this.issuerId = issuerId }
    fun getIssuerId(): UUID {
        return issuerId
    }

    fun build(): CreateWorkedOutTimeSlotRequest {
        return CreateWorkedOutTimeSlotRequest(
            timesheetId = timesheetId,
            profileId = profileId,
            workedOutShiftId = workedOutShiftId,
            workedOutHours = workedOutHours,
            timeEditingReason = timeEditingReason,
            accountingContractId = accountingContractId,
            issuerId = issuerId


        )
    }
}