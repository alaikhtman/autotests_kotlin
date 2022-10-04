package ru.samokat.mysamokat.tests.tests.my_samokat_apigateway.shiftsAPI

import org.apache.http.HttpStatus
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.samokat.employeeprofiles.api.common.domain.EmployeeVehicleType
import ru.samokat.employeeprofiles.api.common.domain.Vehicle
import ru.samokat.my.domain.employee.EmployeeRole
import ru.samokat.my.domain.shifts.TimeRange
import ru.samokat.my.rest.api.enum.ApiEnum
import ru.samokat.mysamokat.tests.checkers.CommonAssertion
import ru.samokat.mysamokat.tests.checkers.MySamokatApiGWAssertions
import ru.samokat.mysamokat.tests.dataproviders.Constants
import ru.samokat.mysamokat.tests.dataproviders.preconditions.CommonPreconditions
import ru.samokat.mysamokat.tests.dataproviders.preconditions.MySamokatApiGWPreconditions
import ru.samokat.mysamokat.tests.helpers.actions.EmployeeActions
import ru.samokat.mysamokat.tests.helpers.actions.MySamokatApiGWActions
import java.time.LocalDate
import java.time.ZoneOffset

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Tag("msmkt-apigateway")
class StoreSchedule {

    private lateinit var msmktPreconditions: MySamokatApiGWPreconditions

    private lateinit var commonAssertion: CommonAssertion

    @Autowired
    private lateinit var employeeActions: EmployeeActions

    @Autowired
    private lateinit var msmktActions: MySamokatApiGWActions

    private lateinit var msmktAssertion: MySamokatApiGWAssertions

    @Autowired
    private lateinit var commonPreconditions: CommonPreconditions


    @BeforeEach
    fun before() {
        msmktPreconditions = MySamokatApiGWPreconditions()
        msmktAssertion = MySamokatApiGWAssertions()
        commonAssertion = CommonAssertion()
        employeeActions.deleteProfile(Constants.mobile1)
        this.date = commonPreconditions.getTomorrowsDate()
        this.range = commonPreconditions.getTomorrowsFullDayRange()
    }

    @AfterEach
    fun release() {
        msmktAssertion.assertAll()
        commonAssertion.assertAll()
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
    @Tags(Tag("smoke"))
    @DisplayName("Store schedule: several timeslots")
    fun storeScheduleSeveralTimeslots() {

        val profile = commonPreconditions.createProfileDeliveryman()
        val authRequest = msmktPreconditions.fillAuthRequest(password = profile.generatedPassword!!)
        val storeScheduleRequest = msmktPreconditions.fillStoreScheduleRequestBuilder(
            listOf(
                msmktPreconditions.getFormattedTimeRange(date, "09:00", "17:00"),
                msmktPreconditions.getFormattedTimeRange(date, "18:00", "22:00")
            )
        )

        val token = msmktActions.authProfilePassword(authRequest)!!.accessToken
        msmktActions.storeSchedule(token, storeScheduleRequest, range)
        val allShifts = msmktActions.getAllShifts(token, range)

        msmktAssertion.checkScheduleListCount(allShifts, 2)
        msmktAssertion.checkScheduleInfo(allShifts, storeScheduleRequest)

    }

    @Test
    @DisplayName("Store schedule: change timeslot")
    fun storeScheduleChangeTimeslot() {

        val profile = commonPreconditions.createProfileDeliveryman()
        val authRequest = msmktPreconditions.fillAuthRequest(password = profile.generatedPassword!!)
        val storeScheduleRequest1 = msmktPreconditions.fillStoreScheduleRequestBuilder(
            listOf(
                msmktPreconditions.getFormattedTimeRange(date, "09:00", "17:00"),
                msmktPreconditions.getFormattedTimeRange(date, "18:00", "22:00")
            )
        )
        val storeScheduleRequest2 = msmktPreconditions.fillStoreScheduleRequestBuilder(
            listOf(
                msmktPreconditions.getFormattedTimeRange(date, "11:00", "18:00"),
                msmktPreconditions.getFormattedTimeRange(date, "19:00", "21:00")
            )
        )

        val token = msmktActions.authProfilePassword(authRequest)!!.accessToken
        msmktActions.storeSchedule(token, storeScheduleRequest1, range)
        msmktActions.storeSchedule(token, storeScheduleRequest2, range)
        val allShifts = msmktActions.getAllShifts(token, range)

        msmktAssertion.checkScheduleListCount(allShifts, 2)
        msmktAssertion.checkScheduleInfo(allShifts, storeScheduleRequest2)

    }

    @Test
    @DisplayName("Store schedule: full day timeslot (tomorrow, deliveryman)")
    fun storeScheduleFullDayTimeslotTomorrow() {

        val profile = commonPreconditions.createProfileDeliveryman()
        val authRequest = msmktPreconditions.fillAuthRequest(password = profile.generatedPassword!!)
        val storeScheduleRequest = msmktPreconditions.fillStoreScheduleRequestBuilder(
            listOf(
                msmktPreconditions.getFormattedTimeRange(date, "00:00", "23:59"),
            )
        )

        val token = msmktActions.authProfilePassword(authRequest)!!.accessToken
        msmktActions.storeSchedule(token, storeScheduleRequest, range)
        val allShifts = msmktActions.getAllShifts(token, range)

        msmktAssertion.checkScheduleListCount(allShifts, 1)
        msmktAssertion.checkScheduleInfo(allShifts, storeScheduleRequest)

    }

    @Test
    @DisplayName("Store schedule: full day timeslot (today, picker)")
    fun storeScheduleFullDayTimeslotToday() {

        val profile =
            commonPreconditions.createProfilePicker()
        val authRequest = msmktPreconditions.fillAuthRequest(password = profile.generatedPassword!!)
        val storeScheduleRequest = msmktPreconditions.fillStoreScheduleRequestBuilder(
            listOf(
                msmktPreconditions.getFormattedTimeRange(LocalDate.now(), "00:00", "23:59"),
            )
        )

        val token = msmktActions.authProfilePassword(authRequest)!!.accessToken
        msmktActions.storeSchedule(
            token, storeScheduleRequest, TimeRange(
                startingAt = LocalDate.now().atStartOfDay().toInstant(ZoneOffset.UTC),
                endingAt = LocalDate.now().plusDays(1L).atStartOfDay().toInstant(ZoneOffset.UTC)
            )
        )
        val allShifts = msmktActions.getAllShifts(
            token, TimeRange(
                startingAt = LocalDate.now().atStartOfDay().toInstant(ZoneOffset.UTC),
                endingAt = LocalDate.now().plusDays(1L).atStartOfDay().toInstant(ZoneOffset.UTC)
            )
        )

        msmktAssertion.checkScheduleListCount(allShifts, 1)
        msmktAssertion.checkScheduleInfo(allShifts, storeScheduleRequest)

    }

    @Test
    @DisplayName("Store schedule: delete all timeslots")
    fun storeScheduleDeleteAllTimeslots() {

        val profile = commonPreconditions.createProfileDeliveryman()
        val authRequest = msmktPreconditions.fillAuthRequest(password = profile.generatedPassword!!)
        val storeScheduleRequest1 = msmktPreconditions.fillStoreScheduleRequestBuilder(
            listOf(
                msmktPreconditions.getFormattedTimeRange(date, "09:00", "17:00"),
                msmktPreconditions.getFormattedTimeRange(date, "18:00", "22:00")
            )
        )
        val storeScheduleRequest2 = msmktPreconditions.fillStoreScheduleRequestBuilder(listOf())

        val token = msmktActions.authProfilePassword(authRequest)!!.accessToken
        msmktActions.storeSchedule(token, storeScheduleRequest1, range)
        val allShifts1 = msmktActions.getAllShifts(token, range)
        msmktActions.storeSchedule(token, storeScheduleRequest2, range)
        val allShifts2 = msmktActions.getAllShifts(token, range)!!

        msmktAssertion.checkScheduleListCount(allShifts1, 2)
        msmktAssertion.checkScheduleInfo(allShifts1, storeScheduleRequest1)
        msmktAssertion.checkScheduleNotExists(allShifts2)
    }

    @Test
    @DisplayName("Store schedule: delete timeslot")
    fun storeScheduleDeleteTimeslot() {

        val profile = commonPreconditions.createProfileDeliveryman()
        val authRequest = msmktPreconditions.fillAuthRequest(password = profile.generatedPassword!!)
        val storeScheduleRequest1 = msmktPreconditions.fillStoreScheduleRequestBuilder(
            listOf(
                msmktPreconditions.getFormattedTimeRange(date, "09:00", "17:00"),
                msmktPreconditions.getFormattedTimeRange(date, "18:00", "22:00")
            )
        )
        val storeScheduleRequest2 = msmktPreconditions.fillStoreScheduleRequestBuilder(
            listOf(
                msmktPreconditions.getFormattedTimeRange(date, "09:00", "17:00")
            )
        )

        val token = msmktActions.authProfilePassword(authRequest)!!.accessToken
        msmktActions.storeSchedule(token, storeScheduleRequest1, range)
        val allShifts1 = msmktActions.getAllShifts(token, range)
        msmktActions.storeSchedule(token, storeScheduleRequest2, range)
        val allShifts2 = msmktActions.getAllShifts(token, range)!!

        msmktAssertion.checkScheduleListCount(allShifts1, 2)
        msmktAssertion.checkScheduleInfo(allShifts1, storeScheduleRequest1)
        msmktAssertion.checkScheduleListCount(allShifts2, 1)
        msmktAssertion.checkScheduleInfo(allShifts2, storeScheduleRequest2)
    }

    @Test
    @DisplayName("Store schedule: disabled profile")
    fun storeScheduleDisabledProfile() {

        val profile = commonPreconditions.createProfileDeliveryman()
        val authRequest = msmktPreconditions.fillAuthRequest(password = profile.generatedPassword!!)
        val storeScheduleRequest = msmktPreconditions.fillStoreScheduleRequestBuilder(
            listOf(
                msmktPreconditions.getFormattedTimeRange(date, "09:00", "17:00"),
                msmktPreconditions.getFormattedTimeRange(date, "18:00", "22:00")
            )
        )

        val token = msmktActions.authProfilePassword(authRequest)!!.accessToken
        employeeActions.deleteProfile(profile.profileId)
        msmktActions.storeSchedule(token, storeScheduleRequest, range)
        val allShifts = msmktActions.getAllShifts(token, range)!!

        msmktAssertion.checkScheduleListCount(allShifts, 2)
        msmktAssertion.checkScheduleInfo(allShifts, storeScheduleRequest)
    }

    @Test
    @DisplayName("Store schedule: crossing slots")
    fun storeScheduleCrossingSlots() {

        val profile = commonPreconditions.createProfileDeliveryman()
        val authRequest = msmktPreconditions.fillAuthRequest(password = profile.generatedPassword!!)
        val storeScheduleRequest = msmktPreconditions.fillStoreScheduleRequestBuilder(
            listOf(
                msmktPreconditions.getFormattedTimeRange(date, "09:00", "17:00"),
                msmktPreconditions.getFormattedTimeRange(date, "15:00", "22:00")
            )
        )

        val token = msmktActions.authProfilePassword(authRequest)!!.accessToken
        employeeActions.deleteProfile(profile.profileId)
        val profileDataError = msmktActions.storeScheduleError(token, storeScheduleRequest, range, HttpStatus.SC_BAD_REQUEST)

        commonAssertion.checkErrorMessage(profileDataError!!.code.toString(), "InvalidRequest")
        commonAssertion.checkErrorMessage(profileDataError.message.toString(), "Invalid time ranges: time range at index 1 overlaps time range at index 0")
        commonAssertion.checkErrorMessage(profileDataError.parameter.toString(), "schedules")
    }
}