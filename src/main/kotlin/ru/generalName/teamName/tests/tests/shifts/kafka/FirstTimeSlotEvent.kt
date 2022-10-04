package ru.samokat.mysamokat.tests.tests.shifts.kafka

import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.samokat.employeeprofiles.api.common.domain.EmployeeVehicleType
import ru.samokat.employeeprofiles.api.common.domain.Vehicle
import ru.samokat.my.domain.shifts.TimeRange
import ru.samokat.my.rest.api.enum.ApiEnum
import ru.samokat.mysamokat.tests.checkers.ShiftAssertion
import ru.samokat.mysamokat.tests.dataproviders.Constants
import ru.samokat.mysamokat.tests.dataproviders.preconditions.CommonPreconditions
import ru.samokat.mysamokat.tests.dataproviders.preconditions.EmployeePreconditions
import ru.samokat.mysamokat.tests.dataproviders.preconditions.ShiftsPreconditions
import ru.samokat.mysamokat.tests.helpers.actions.EmployeeActions
import ru.samokat.mysamokat.tests.helpers.actions.ShiftsActions
import java.time.LocalDate

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Tags(Tag("shifts"), Tag("kafka_consume"))
class FirstTimeSlotEvent {

    private lateinit var shiftsPreconditions: ShiftsPreconditions

    @Autowired
    private lateinit var employeeActions: EmployeeActions

    private lateinit var employeePreconditions: EmployeePreconditions

    @Autowired
    private lateinit var shiftsActions: ShiftsActions

    private lateinit var shiftsAssertion: ShiftAssertion

    @Autowired
    private lateinit var commonPreconditions: CommonPreconditions

    @BeforeEach
    fun before() {
        this.date = commonPreconditions.getTomorrowsDate()
        this.range = commonPreconditions.getTomorrowsFullDayRange()
        shiftsPreconditions = ShiftsPreconditions()
        employeePreconditions = EmployeePreconditions()
        shiftsAssertion = ShiftAssertion()
        employeeActions.deleteProfile(Constants.mobile1)
    }

    @AfterEach
    fun release() {
        shiftsAssertion.assertAll()
        employeeActions.deleteProfile(Constants.mobile1)
    }

    private lateinit var range: TimeRange
    fun range(range: TimeRange) = apply { this.range = range }
    fun getRange(): TimeRange {
        return range
    }

    private lateinit var date: LocalDate
    fun date(date: LocalDate) = apply { this.date = date }
    fun getDate(): LocalDate {
        return date
    }

    @Test
    @Tags (Tag("smoke"))
    @DisplayName("Send first timeslot event for one timeslot")
    fun sendFirstTimeSlotEventForOneSlotTest() {

        val profileId = commonPreconditions.createProfileDeliveryman().profileId

        val storeRequest = shiftsPreconditions.fillStoreScheduleRequest(
            profileId,
            timeRange = range,
            schedule = listOf(
                commonPreconditions.getFormattedTimeRange(date, "09:00", "17:00")
            )
        )

        shiftsActions.postSchedules(storeRequest)
        val kafkaEvent = shiftsActions.getMessageFromkafkaFirstShiftsSchedule(profileId)

        shiftsAssertion.checkFirstTimeSlotEvent(kafkaEvent, profileId)
    }

    @Test
    @Tags (Tag("smoke"))
    @DisplayName("Send first timeslot event for several timeslot")
    fun sendFirstTimeSlotEventForSeveralSlotTest() {

        val profileId = commonPreconditions.createProfileDeliveryman().profileId

        val storeRequest = shiftsPreconditions.fillStoreScheduleRequest(
            profileId,
            timeRange = range,
            schedule = listOf(
                commonPreconditions.getFormattedTimeRange(date, "09:00", "17:00"),
                commonPreconditions.getFormattedTimeRange(date, "18:00", "19:00")
            )
        )

        shiftsActions.postSchedules(storeRequest)
        val kafkaEvent = shiftsActions.getMessageFromkafkaFirstShiftsSchedule(profileId)

        shiftsAssertion.checkFirstTimeSlotEvent(kafkaEvent, profileId)
    }

    @Test
    @Tags (Tag("smoke"))
    @DisplayName("First timeslot event not send for not first timeslot")
    fun firstTimeslotEventNotSendForNotFirstTimeslotTest() {

        val storeRequest = shiftsPreconditions.fillStoreScheduleRequest(
            Constants.profileWithTimeSlots,
            timeRange = range,
            schedule = listOf(
                commonPreconditions.getFormattedTimeRange(date, "09:00", "17:00")
            )
        )
        shiftsActions.postSchedules(storeRequest)

        val kafkaEventCount = shiftsActions.getMessageCountFromKafkaFirstShiftsSchedule(Constants.profileWithTimeSlots)

        shiftsAssertion.checkMessagesCount(kafkaEventCount, 0)
    }

    // TODO
    //добавить кейс: не отправлять событие при создании второго таймслота
    //добавить кейс: был таймслот, удалил, создал заново => отправилось событие

}
