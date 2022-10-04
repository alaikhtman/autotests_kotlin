package ru.generalName.teamName.tests.checkers

import io.qameta.allure.Step
import org.assertj.core.api.SoftAssertions
import org.jetbrains.exposed.sql.ResultRow
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*


@Service
class ScheduleAssertions {

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
    ): ScheduleAssertions {
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


}