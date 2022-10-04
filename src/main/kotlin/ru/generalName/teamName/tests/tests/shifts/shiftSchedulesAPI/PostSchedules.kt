package ru.samokat.mysamokat.tests.tests.shifts.shiftSchedulesAPI

import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.samokat.my.domain.employee.EmployeeRole
import ru.samokat.my.domain.shifts.TimeRange
import ru.samokat.my.rest.api.enum.ApiEnum
import ru.samokat.mysamokat.tests.checkers.ShiftAssertion
import ru.samokat.mysamokat.tests.dataproviders.Constants
import ru.samokat.mysamokat.tests.dataproviders.preconditions.CommonPreconditions
import ru.samokat.mysamokat.tests.dataproviders.preconditions.EmployeePreconditions
import ru.samokat.mysamokat.tests.dataproviders.preconditions.ShiftsPreconditions
import ru.samokat.mysamokat.tests.helpers.actions.EmployeeActions
import ru.samokat.mysamokat.tests.helpers.actions.ShiftsActions
import ru.samokat.shifts.api.schedules.ShiftScheduleView
import java.time.*


@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Tag("shifts")
class PostSchedules {

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
    @DisplayName("Create schedules for deliveryman")
    fun createSchedulesForDeliveryman() {

        val profileId = commonPreconditions.createProfileDeliveryman().profileId

        val storeRequest = shiftsPreconditions.fillStoreScheduleRequest(
            profileId,
            timeRange = range,
            schedule = listOf(
                commonPreconditions.getFormattedTimeRange(date, "09:00", "17:00"),
                commonPreconditions.getFormattedTimeRange(date, "18:00", "22:00")
            )
        )

        val getScheduleRequest = shiftsPreconditions.fillSearchScheduleRequest(
            timeRange = range,
            userIds = setOf(profileId),
        )

        shiftsActions.postSchedules(storeRequest)
        val schedules = shiftsActions.getSchedules(getScheduleRequest)

        shiftsAssertion.checkScheduleListCount(schedules, 2)
        shiftsAssertion.checkScheduleInfo(schedules, storeRequest)
    }

    @Test
    @DisplayName("Create schedules for picker")
    fun createSchedulesForPicker(){
        val profileId = commonPreconditions.createProfilePicker().profileId

        val storeRequest = shiftsPreconditions.fillStoreScheduleRequest(
            profileId,
            timeRange = range,
            schedule = listOf(
                commonPreconditions.getFormattedTimeRange(date, "09:00", "17:00"),
                commonPreconditions.getFormattedTimeRange(date, "18:00", "22:00")
            )
        )

        val getScheduleRequest = shiftsPreconditions.fillSearchScheduleRequest(
            timeRange = range,
            userIds = setOf(profileId),
        )

        shiftsActions.postSchedules(storeRequest)
        val schedules = shiftsActions.getSchedules(getScheduleRequest)

        shiftsAssertion.checkScheduleListCount(schedules, 2)
        shiftsAssertion.checkScheduleInfo(schedules, storeRequest)
    }

    @Test
    @DisplayName("Create schedules for 4 hours")
    fun createSchedulesFourHours(){

        val profileId = commonPreconditions.createProfileDeliveryman().profileId

        val storeRequest = shiftsPreconditions.fillStoreScheduleRequest(
            profileId,
            timeRange = range,
            schedule = listOf(
                commonPreconditions.getFormattedTimeRange(date, "08:00", "12:00")
            )
        )

        val getScheduleRequest = shiftsPreconditions.fillSearchScheduleRequest(
            timeRange = range,
            userIds = setOf(profileId),
        )

        shiftsActions.postSchedules(storeRequest)
        val schedules = shiftsActions.getSchedules(getScheduleRequest)

        shiftsAssertion.checkScheduleListCount(schedules, 1)
        shiftsAssertion.checkScheduleInfo(schedules, storeRequest)
    }

    @Test
    @Tags (Tag("smoke"))
    @DisplayName("Create schedules for all day")
    fun createSchedulesFullDay(){

        val profileId = commonPreconditions.createProfileDeliveryman().profileId

        val storeRequest = shiftsPreconditions.fillStoreScheduleRequest(
            profileId,
            timeRange = range,
            schedule = listOf(
                commonPreconditions.getFormattedTimeRange(date, "00:00", "23:59")
            )
        )

        val getScheduleRequest = shiftsPreconditions.fillSearchScheduleRequest(
            timeRange = range,
            userIds = setOf(profileId),
        )

        shiftsActions.postSchedules(storeRequest)
        val schedules = shiftsActions.getSchedules(getScheduleRequest)

        shiftsAssertion.checkScheduleListCount(schedules, 1)
        shiftsAssertion.checkScheduleInfo(schedules, storeRequest)
    }

    @Test
    @DisplayName("Create schedules with seconds and millis")
    fun createSchedulesWithSecondsAndMillliseconds(){

        val profileId = commonPreconditions.createProfileDeliveryman().profileId

        val storeRequest = shiftsPreconditions.fillStoreScheduleRequest(
            profileId,
            timeRange = range,
            schedule = listOf(
                commonPreconditions.getFormattedTimeRange(date, "09:00:01", "17:00:02"),
                commonPreconditions.getFormattedTimeRange(date, "18:00:03", "22:00:04")
            )
        )

        val getScheduleRequest = shiftsPreconditions.fillSearchScheduleRequest(
            timeRange = range,
            userIds = setOf(profileId),
        )

        shiftsActions.postSchedules(storeRequest)
        val schedules = shiftsActions.getSchedules(getScheduleRequest)

        shiftsAssertion.checkScheduleListCount(schedules, 2)
        shiftsAssertion.checkScheduleInfo(schedules, ShiftScheduleView(userId = profileId, timeRange = TimeRange(
            startingAt = date.atTime(LocalTime.parse("09:00")).toInstant(ZoneOffset.UTC),
            endingAt = date.atTime(LocalTime.parse("17:00")).toInstant(ZoneOffset.UTC)
        )))
        shiftsAssertion.checkScheduleInfo(schedules, ShiftScheduleView(userId = profileId, timeRange = TimeRange(
            startingAt = date.atTime(LocalTime.parse("18:00")).toInstant(ZoneOffset.UTC),
            endingAt = date.atTime(LocalTime.parse("22:00")).toInstant(ZoneOffset.UTC)
        )))

    }

    @Test
    @Tags (Tag("smoke"))
    @DisplayName("Change schedule time")
    fun changeScheduleTime(){

        val profileId = commonPreconditions.createProfileDeliveryman().profileId

        val storeRequest1 = shiftsPreconditions.fillStoreScheduleRequest(
            profileId,
            timeRange = range,
            schedule = listOf(
                commonPreconditions.getFormattedTimeRange(date, "09:00", "17:00"),
                commonPreconditions.getFormattedTimeRange(date, "18:00", "22:00")
            )
        )

        val storeRequest2 = shiftsPreconditions.fillStoreScheduleRequest(
            profileId,
            timeRange = range,
            schedule = listOf(
                commonPreconditions.getFormattedTimeRange(date, "10:00", "16:00"),
                commonPreconditions.getFormattedTimeRange(date, "17:00", "21:00")
            )
        )

        val getScheduleRequest = shiftsPreconditions.fillSearchScheduleRequest(
            timeRange = range,
            userIds = setOf(profileId),
        )

        shiftsActions.postSchedules(storeRequest1)
        shiftsActions.postSchedules(storeRequest2)
        val schedules = shiftsActions.getSchedules(getScheduleRequest)

        shiftsAssertion.checkScheduleListCount(schedules, 2)
        shiftsAssertion.checkScheduleInfo(schedules, storeRequest2)
    }

    @Test
    @Tags (Tag("smoke"))
    @DisplayName("Delete all schedules")
    fun deleteAllSchedules(){

        val profileId = commonPreconditions.createProfileDeliveryman().profileId

        val storeRequest = shiftsPreconditions.fillStoreScheduleRequest(
            profileId,
            timeRange = range,
            schedule = listOf(
                commonPreconditions.getFormattedTimeRange(date, "09:00", "17:00"),
                commonPreconditions.getFormattedTimeRange(date, "18:00", "22:00")
            )
        )

        val clearSchedulesRequest = shiftsPreconditions.fillStoreScheduleRequest(
            profileId,
            timeRange = range,
            schedule = mutableListOf()
        )

        val getScheduleRequest = shiftsPreconditions.fillSearchScheduleRequest(
            timeRange = range,
            userIds = setOf(profileId),
        )

        shiftsActions.postSchedules(storeRequest)
        shiftsActions.postSchedules(clearSchedulesRequest)
        val schedules = shiftsActions.getSchedules(getScheduleRequest)

        shiftsAssertion.checkScheduleListCount(schedules, 0)
    }

    @Test
    @DisplayName("Delete one schedules")
    fun deleteOneSchedules(){

        val profileId = commonPreconditions.createProfileDeliveryman().profileId

        val storeRequest1 = shiftsPreconditions.fillStoreScheduleRequest(
            profileId,
            timeRange = range,
            schedule = listOf(
                commonPreconditions.getFormattedTimeRange(date, "09:00", "17:00"),
                commonPreconditions.getFormattedTimeRange(date, "18:00", "22:00")
            )
        )

        val storeRequest2 = shiftsPreconditions.fillStoreScheduleRequest(
            profileId,
            timeRange = range,
            schedule = listOf(
                commonPreconditions.getFormattedTimeRange(date, "09:00", "17:00")
            )
        )

        val getScheduleRequest = shiftsPreconditions.fillSearchScheduleRequest(
            timeRange = range,
            userIds = setOf(profileId),
        )

        shiftsActions.postSchedules(storeRequest1)
        shiftsActions.postSchedules(storeRequest2)
        val schedules = shiftsActions.getSchedules(getScheduleRequest)

        shiftsAssertion.checkScheduleListCount(schedules, 1)
        shiftsAssertion.checkScheduleInfo(schedules, storeRequest2)
    }

    @Test
    @DisplayName("Add another schedules")
    fun addAnotherSchedule(){

        val profileId = commonPreconditions.createProfileDeliveryman().profileId

        val storeRequest1 = shiftsPreconditions.fillStoreScheduleRequest(
            profileId,
            timeRange = range,
            schedule = listOf(
                commonPreconditions.getFormattedTimeRange(date, "09:00", "17:00")
            )
        )

        val storeRequest2 = shiftsPreconditions.fillStoreScheduleRequest(
            profileId,
            timeRange = range,
            schedule = listOf(
                commonPreconditions.getFormattedTimeRange(date, "09:00", "17:00"),
                commonPreconditions.getFormattedTimeRange(date, "18:00", "22:00")
            )
        )

        val getScheduleRequest = shiftsPreconditions.fillSearchScheduleRequest(
            timeRange = range,
            userIds = setOf(profileId),
        )

        shiftsActions.postSchedules(storeRequest1)
        shiftsActions.postSchedules(storeRequest2)
        val schedules = shiftsActions.getSchedules(getScheduleRequest)

        shiftsAssertion.checkScheduleListCount(schedules, 2)
        shiftsAssertion.checkScheduleInfo(schedules, storeRequest2)
    }

    @Test
    @Tags (Tag("smoke"))
    @DisplayName("Slots boundary values")
    fun createSchedulesSlotsBoundaryValues(){

        val profileId = commonPreconditions.createProfileDeliveryman().profileId

        val storeRequest = shiftsPreconditions.fillStoreScheduleRequest(
            profileId,
            timeRange = range,
            schedule = listOf(
                commonPreconditions.getFormattedTimeRange(date, "09:00", "17:00"),
                commonPreconditions.getFormattedTimeRange(date, "17:01", "22:00")
            )
        )

        val getScheduleRequest = shiftsPreconditions.fillSearchScheduleRequest(
            timeRange = range,
            userIds = setOf(profileId),
        )

        shiftsActions.postSchedules(storeRequest)
        val schedules = shiftsActions.getSchedules(getScheduleRequest)

        shiftsAssertion.checkScheduleListCount(schedules, 2)
        shiftsAssertion.checkScheduleInfo(schedules, storeRequest)
    }

    @Test
    @DisplayName("Slots and range boundary values")
    fun createSchedulesSlotsAndRangeBoundaryValues(){

        val profileId = commonPreconditions.createProfileDeliveryman().profileId

        val storeRequest = shiftsPreconditions.fillStoreScheduleRequest(
            profileId,
            timeRange = range,
            schedule = listOf(
                commonPreconditions.getFormattedTimeRange(date, "09:00", "17:00"),
                commonPreconditions.getFormattedTimeRange(date, "17:01", "22:00")
            )
        )

        val getScheduleRequest = shiftsPreconditions.fillSearchScheduleRequest(
            timeRange = range,
            userIds = setOf(profileId),
        )

        shiftsActions.postSchedules(storeRequest)
        val schedules = shiftsActions.getSchedules(getScheduleRequest)

        shiftsAssertion.checkScheduleListCount(schedules, 2)
        shiftsAssertion.checkScheduleInfo(schedules, storeRequest)
    }

    @Test
    @DisplayName("Create schedules: user with other role")
    fun createSchedulesUserWithOtherRole(){

        val profileId = commonPreconditions.createProfile(roles = listOf(ApiEnum(EmployeeRole.SUPERVISOR))).profileId

       val storeRequest = shiftsPreconditions.fillStoreScheduleRequest(
           profileId,
           timeRange = range,
           schedule = listOf(
               commonPreconditions.getFormattedTimeRange(date, "09:00", "17:00"),
               commonPreconditions.getFormattedTimeRange(date, "18:00", "22:00")
           )
       )

       val getScheduleRequest = shiftsPreconditions.fillSearchScheduleRequest(
           timeRange = range,
           userIds = setOf(profileId),
       )

       shiftsActions.postSchedules(storeRequest)
       val schedules = shiftsActions.getSchedules(getScheduleRequest)

       shiftsAssertion.checkScheduleListCount(schedules, 2)
       shiftsAssertion.checkScheduleInfo(schedules, storeRequest)
    }

    @Test
    @DisplayName("Create schedules: user is blocked")
    fun createSchedulesUserDoNotHavePermissions(){

        val profileId = commonPreconditions.createProfileDeliveryman().profileId

        val storeRequest = shiftsPreconditions.fillStoreScheduleRequest(
            profileId,
            timeRange = range,
            schedule = listOf(
                commonPreconditions.getFormattedTimeRange(date, "09:00", "17:00"),
                commonPreconditions.getFormattedTimeRange(date, "18:00", "22:00")
            )
        )

        val getScheduleRequest = shiftsPreconditions.fillSearchScheduleRequest(
            timeRange = range,
            userIds = setOf(profileId),
        )

        employeeActions.deleteProfile(profileId)
        shiftsActions.postSchedules(storeRequest)

        val schedules = shiftsActions.getSchedules(getScheduleRequest)

        shiftsAssertion.checkScheduleListCount(schedules, 2)
        shiftsAssertion.checkScheduleInfo(schedules, storeRequest)
    }

    @Test
    @DisplayName("Create schedules in the past")
    fun createSchedulesInThePast(){

        val profileId = commonPreconditions.createProfileDeliveryman().profileId

        val storeRequest = shiftsPreconditions.fillStoreScheduleRequest(
            profileId,
            timeRange = TimeRange(
                startingAt = date.minusDays(2).atStartOfDay().toInstant(ZoneOffset.UTC),
                endingAt = date.minusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC)
            ),
            schedule = listOf(
                commonPreconditions.getFormattedTimeRange(date.minusDays(2), "18:00", "21:00")
            )
        )

        val getScheduleRequest = shiftsPreconditions.fillSearchScheduleRequest(
            timeRange = range,
            userIds = setOf(profileId),
        )

        val errorMessage = shiftsActions.postSchedulesWithError(storeRequest).message
        val schedules = shiftsActions.getSchedules(getScheduleRequest)

        shiftsAssertion.checkScheduleListCount(schedules, 0)
        shiftsAssertion.checkErrorMessage(errorMessage, "Schedule must end in the future")

    }

    @Test
    @DisplayName("StartTime > StopTime")
    fun createSchedulesStartTimeAfterStopTime(){

        val profileId = commonPreconditions.createProfileDeliveryman().profileId

        val storeRequest = shiftsPreconditions.fillStoreScheduleRequest(
            profileId,
            timeRange = range,
            schedule = listOf(
                commonPreconditions.getFormattedTimeRange(date, "21:00", "18:00")
            )
        )

        val getScheduleRequest = shiftsPreconditions.fillSearchScheduleRequest(
            timeRange = range,
            userIds = setOf(profileId),
        )

        val errorMessage = shiftsActions.postSchedulesWithError(storeRequest).message
        val schedules = shiftsActions.getSchedules(getScheduleRequest)

        shiftsAssertion.checkScheduleListCount(schedules, 0)
        shiftsAssertion.checkErrorMessage(errorMessage, "The end of time range must be greater than it's start")
    }

    @Test
    @DisplayName("Create crossing timeslots")
    fun createCrossingTimeslots(){

        val profileId = commonPreconditions.createProfileDeliveryman().profileId

        val storeRequest = shiftsPreconditions.fillStoreScheduleRequest(
            profileId,
            timeRange = range,
            schedule = listOf(
                commonPreconditions.getFormattedTimeRange(date, "13:00", "15:00"),
                commonPreconditions.getFormattedTimeRange(date, "14:00", "18:00")
            )
        )

        val getScheduleRequest = shiftsPreconditions.fillSearchScheduleRequest(
            timeRange = range,
            userIds = setOf(profileId),
        )

        val errorMessage = shiftsActions.postSchedulesWithError(storeRequest).message
        val schedules = shiftsActions.getSchedules(getScheduleRequest)

        shiftsAssertion.checkScheduleListCount(schedules, 0)
        shiftsAssertion.checkErrorMessage(errorMessage, "All time ranges must be non-overlapped")
    }
}