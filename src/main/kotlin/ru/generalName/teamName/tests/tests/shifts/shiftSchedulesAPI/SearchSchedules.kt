package ru.samokat.mysamokat.tests.tests.shifts.shiftSchedulesAPI

import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.samokat.employeeprofiles.api.common.domain.Vehicle
import ru.samokat.employeeprofiles.api.common.domain.EmployeeVehicleType
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
import ru.samokat.shifts.api.schedules.search.SearchShiftSchedulesRequest
import java.time.*


@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Tag("shifts")
class SearchSchedules {

    private lateinit var shiftsPreconditions: ShiftsPreconditions

    @Autowired
    private lateinit var employeeActions: EmployeeActions

    private lateinit var employeePreconditions: EmployeePreconditions

    @Autowired
    private lateinit var shiftsActions: ShiftsActions

    private lateinit var shiftsAssertion: ShiftAssertion

    @Autowired
    private lateinit var commonPreconditions: CommonPreconditions

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

    @BeforeEach
    fun before() {
        this.date = commonPreconditions.getTomorrowsDate()
        this.range = commonPreconditions.getTomorrowsFullDayRange()
        shiftsPreconditions = ShiftsPreconditions()
        employeePreconditions = EmployeePreconditions()
        shiftsAssertion = ShiftAssertion()
        employeeActions.deleteProfile(Constants.mobile1)
        employeeActions.deleteProfile(Constants.mobile2)
    }

    @AfterEach
    fun release() {
        shiftsAssertion.assertAll()
        employeeActions.deleteProfile(Constants.mobile1)
        employeeActions.deleteProfile(Constants.mobile2)
    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Search schedules: >1 timeslot for >1 employee")
    fun searchScheduledSeveralResultsForSeveralemployees() {

        val profileId1 = commonPreconditions.createProfileDeliveryman().profileId
        val profileId2 = commonPreconditions.createProfileDeliveryman(Constants.mobile2).profileId

        val storeRequest1 = shiftsPreconditions.fillStoreScheduleRequest(
            profileId1,
            timeRange = range,
            schedule = listOf(
                commonPreconditions.getFormattedTimeRange(date, "09:00", "17:00"),
                commonPreconditions.getFormattedTimeRange(date, "18:00", "22:00")
            )
        )

        val storeRequest2 = shiftsPreconditions.fillStoreScheduleRequest(
            profileId2,
            timeRange = range,
            schedule = listOf(
                commonPreconditions.getFormattedTimeRange(date, "10:00", "11:00"),
                commonPreconditions.getFormattedTimeRange(date, "12:00", "13:00")
            )
        )

        val getScheduleRequest = shiftsPreconditions.fillSearchScheduleRequest(
            timeRange = range,
            userIds = setOf(profileId1, profileId2),
        )

        shiftsActions.postSchedules(storeRequest1)
        shiftsActions.postSchedules(storeRequest2)
        val schedules = shiftsActions.getSchedules(getScheduleRequest)

        shiftsAssertion.checkScheduleListCount(schedules, 4)
        shiftsAssertion.checkScheduleInfo(schedules, storeRequest1)
        shiftsAssertion.checkScheduleInfo(schedules, storeRequest2)
    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Timeslot start inside timerange")
    fun searchSchedulesTimeslotStartInsideRange() {

        val profileId = commonPreconditions.createProfileDeliveryman().profileId

        val storeRequest = shiftsPreconditions.fillStoreScheduleRequest(
            profileId,
            timeRange = range,
            schedule = listOf(
                commonPreconditions.getFormattedTimeRange(date, "09:00", "22:00")
            )
        )

        val getScheduleRequest = shiftsPreconditions.fillSearchScheduleRequest(
            timeRange = TimeRange(range.startingAt, date.atStartOfDay().plusHours(12).toInstant(ZoneOffset.UTC)),
            userIds = setOf(profileId),
        )

        shiftsActions.postSchedules(storeRequest)
        val schedules = shiftsActions.getSchedules(getScheduleRequest)

        shiftsAssertion.checkScheduleListCount(schedules, 1)
        shiftsAssertion.checkScheduleInfo(schedules, storeRequest)
    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Timeslot length for the entire time range")
    fun searchSchedulesTimeslotLengthFoTheEntireTimeRange() {

        val profileId = commonPreconditions.createProfileDeliveryman().profileId

        val storeRequest = shiftsPreconditions.fillStoreScheduleRequest(
            profileId,
            timeRange = range,
            schedule = listOf(
                commonPreconditions.getFormattedTimeRange(date, "08:00", "16:00")
            )
        )

        val getScheduleRequest = shiftsPreconditions.fillSearchScheduleRequest(
            timeRange = TimeRange(
                startingAt = date.atStartOfDay().plusHours(8).toInstant(ZoneOffset.UTC),
                endingAt = date.atStartOfDay().plusHours(16).toInstant(ZoneOffset.UTC)
            ),
            userIds = setOf(profileId),
        )

        shiftsActions.postSchedules(storeRequest)
        val schedules = shiftsActions.getSchedules(getScheduleRequest)

        shiftsAssertion.checkScheduleListCount(schedules, 1)
        shiftsAssertion.checkScheduleInfo(schedules, storeRequest)
    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Timeslot starts before timerange")
    fun searchSchedulesTimeslotStartsBeforeTimeRange() {

        val profileId = commonPreconditions.createProfileDeliveryman().profileId

        val storeRequest = shiftsPreconditions.fillStoreScheduleRequest(
            profileId,
            timeRange = range,
            schedule = listOf(
                commonPreconditions.getFormattedTimeRange(date, "09:00", "22:00")
            )
        )

        val getScheduleRequest = shiftsPreconditions.fillSearchScheduleRequest(
            timeRange = TimeRange(
                date.atStartOfDay().plusHours(12).toInstant(ZoneOffset.UTC),
                date.atStartOfDay().plusHours(23).toInstant(ZoneOffset.UTC)
            ),
            userIds = setOf(profileId),
        )

        shiftsActions.postSchedules(storeRequest)
        val schedules = shiftsActions.getSchedules(getScheduleRequest)

        shiftsAssertion.checkScheduleListCount(schedules, 0)
    }

    @Test
    @DisplayName("Some of the requested employees do not have timeslots")
    fun searchSchedulesSomeEmployeesDoNotHaveTimeslots() {

        val profileId1 = commonPreconditions.createProfileDeliveryman().profileId
        val profileId2 = commonPreconditions.createProfileDeliveryman(Constants.mobile2).profileId

        val storeRequest = shiftsPreconditions.fillStoreScheduleRequest(
            profileId1,
            timeRange = range,
            schedule = listOf(
                commonPreconditions.getFormattedTimeRange(date, "09:00", "17:00"),
                commonPreconditions.getFormattedTimeRange(date, "18:00", "22:00")
            )
        )

        val getScheduleRequest = shiftsPreconditions.fillSearchScheduleRequest(
            timeRange = range,
            userIds = setOf(profileId1, profileId2),
        )

        shiftsActions.postSchedules(storeRequest)
        val schedules = shiftsActions.getSchedules(getScheduleRequest)

        shiftsAssertion.checkScheduleListCount(schedules, 2)
        shiftsAssertion.checkScheduleInfo(schedules, storeRequest)
    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("There are no timeslots at all")
    fun searchSchedulesNoTimeslots() {

        val profileId = commonPreconditions.createProfileDeliveryman().profileId

        val getScheduleRequest = shiftsPreconditions.fillSearchScheduleRequest(
            timeRange = range,
            userIds = setOf(profileId),
        )

        val schedules = shiftsActions.getSchedules(getScheduleRequest)

        shiftsAssertion.checkScheduleListCount(schedules, 0)
    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Search schedules: pagination")
    fun searchSchedulesPagination() {

        val profileId1 = commonPreconditions.createProfileDeliveryman().profileId
        val profileId2 = commonPreconditions.createProfileDeliveryman(Constants.mobile2).profileId

        val storeRequest1 = shiftsPreconditions.fillStoreScheduleRequest(
            profileId1,
            timeRange = range,
            schedule = listOf(
                commonPreconditions.getFormattedTimeRange(date, "05:00", "07:00"),
                commonPreconditions.getFormattedTimeRange(date, "08:00", "10:00"),
                commonPreconditions.getFormattedTimeRange(date, "11:00", "12:00")
            )
        )
        val storeRequest2 = shiftsPreconditions.fillStoreScheduleRequest(
            profileId2,
            timeRange = range,
            schedule = listOf(
                commonPreconditions.getFormattedTimeRange(date, "05:00", "07:00"),
                commonPreconditions.getFormattedTimeRange(date, "09:00", "11:00"),
                commonPreconditions.getFormattedTimeRange(date, "15:00", "18:00")
            )
        )

        shiftsActions.postSchedules(storeRequest1)
        shiftsActions.postSchedules(storeRequest2)

        val getScheduleRequestPage1 = shiftsPreconditions.fillSearchScheduleRequest(
            timeRange = range,
            userIds = setOf(profileId1, profileId2),
            paging = SearchShiftSchedulesRequest.PagingData(pageSize = 3, null)
        )

        val schedulesPage1 = shiftsActions.getSchedules(getScheduleRequestPage1)

        val getScheduleRequestPage2 = shiftsPreconditions.fillSearchScheduleRequest(
            timeRange = range,
            userIds = setOf(profileId1, profileId2),
            paging = SearchShiftSchedulesRequest.PagingData(pageSize = 3, schedulesPage1.paging.nextPageMark)
        )
        val schedulesPage2 = shiftsActions.getSchedules(getScheduleRequestPage2)

        shiftsAssertion.checkScheduleListCount(schedulesPage1, 3)
        shiftsAssertion.checkScheduleListCount(schedulesPage2, 3)

        shiftsAssertion
            .checkScheduleInfo(
                schedulesPage1, ShiftScheduleView(
                    userId = profileId1,
                    timeRange = commonPreconditions.getFormattedTimeRange(date, "05:00", "07:00")
                )
            )
            .checkScheduleInfo(
                schedulesPage1, ShiftScheduleView(
                    userId = profileId2,
                    timeRange = commonPreconditions.getFormattedTimeRange(date, "05:00", "07:00")
                )
            )
            .checkScheduleInfo(
                schedulesPage1, ShiftScheduleView(
                    userId = profileId1,
                    timeRange = commonPreconditions.getFormattedTimeRange(date, "08:00", "10:00")
                )
            )
            .checkScheduleInfo(
                schedulesPage2, ShiftScheduleView(
                    userId = profileId2,
                    timeRange = commonPreconditions.getFormattedTimeRange(date, "09:00", "11:00")
                )
            )
            .checkScheduleInfo(
                schedulesPage2, ShiftScheduleView(
                    userId = profileId1,
                    timeRange = commonPreconditions.getFormattedTimeRange(date, "11:00", "12:00")
                )
            )
            .checkScheduleInfo(
                schedulesPage2, ShiftScheduleView(
                    userId = profileId2,
                    timeRange = commonPreconditions.getFormattedTimeRange(date, "15:00", "18:00")
                )
            )

    }


}