package ru.samokat.mysamokat.tests.dataproviders.employee_schedule

import ru.samokat.employeeschedule.api.workedouttimeslot.update.UpdateWorkedOutTimeSlotRequest
import java.util.*

class UpdateWorkedOutTimeSlotRequestBuilder {

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

    private var version: Long = 1
    fun version(version: Long) = apply { this.version = version }
    fun getVersion(): Long {
        return version
    }

    fun build(): UpdateWorkedOutTimeSlotRequest {
        return UpdateWorkedOutTimeSlotRequest(
            workedOutHours = workedOutHours,
            timeEditingReason = timeEditingReason,
            accountingContractId = accountingContractId,
            issuerId = issuerId,
            version = version

        )
    }

}