package ru.samokat.mysamokat.tests.tests.my_samokat_apigateway.UsersAPI

import org.apache.http.HttpStatus
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.samokat.employeeprofiles.api.common.domain.EmployeeVehicleType
import ru.samokat.employeeprofiles.api.common.domain.Vehicle
import ru.samokat.my.domain.employee.EmployeeRole
import ru.samokat.my.domain.shifts.TimeRange
import ru.samokat.my.rest.api.enum.ApiEnum
import ru.samokat.mysamokat.apigateway.api.user.getstatistics.shifts.WorkedOutShiftType
import ru.samokat.mysamokat.tests.checkers.CommonAssertion
import ru.samokat.mysamokat.tests.checkers.MySamokatApiGWAssertions
import ru.samokat.mysamokat.tests.dataproviders.Constants
import ru.samokat.mysamokat.tests.dataproviders.preconditions.CommonPreconditions
import ru.samokat.mysamokat.tests.dataproviders.preconditions.MySamokatApiGWPreconditions
import ru.samokat.mysamokat.tests.helpers.actions.EmployeeActions
import ru.samokat.mysamokat.tests.helpers.actions.MySamokatApiGWActions
import ru.samokat.shifts.api.common.domain.ShiftUserRole

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Tag("msmkt-apigateway")
class ShiftsStatistics {

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
        this.range = commonPreconditions.getTodayFullDayRange()
    }

    @AfterEach
    fun release() {
        msmktAssertion.assertAll()
        employeeActions.deleteProfile(Constants.mobile1)
    }


    private lateinit var range: TimeRange
    fun range(range: TimeRange) = apply { this.range = range }
    fun getRange(): TimeRange {
        return range
    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Shifts Statistics: get deliveryman shifts statistics")
    fun getDeliverymanShiftsStatsTest() {

        val profile = commonPreconditions.createProfileDeliveryman()
        val authRequest = msmktPreconditions.fillAuthRequest(password = profile.generatedPassword!!)
        val token = msmktActions.authProfilePassword(authRequest)!!.accessToken

        val emptyStat = msmktActions.getShiftsStatistics(token, range)!!
        commonPreconditions.startAndStopShift(profile.profileId)
        val stat = msmktActions.getShiftsStatistics(token, range)

        msmktAssertion.checkShiftsStatisticsEmpty(emptyStat)
        msmktAssertion.checkShiftsStatistics(stat!!.shifts[0], WorkedOutShiftType.DELIVERY)
    }

    @Test
    @DisplayName("Shifts Statistics: get picker shifts statistics")
    fun getPickerShiftsStatsTest() {

        val profile = commonPreconditions.createProfilePicker()
        val authRequest = msmktPreconditions.fillAuthRequest(password = profile.generatedPassword!!)
        val token = msmktActions.authProfilePassword(authRequest)!!.accessToken

        val emptyStat = msmktActions.getShiftsStatistics(token, range)!!
        commonPreconditions.startAndStopShift(profile.profileId, userRole = ShiftUserRole.PICKER)
        val stat = msmktActions.getShiftsStatistics(token, range)

        msmktAssertion.checkShiftsStatisticsEmpty(emptyStat)
        msmktAssertion.checkShiftsStatistics(stat!!.shifts[0], WorkedOutShiftType.PICKING)
    }

    @Test
    @DisplayName("Shifts Statistics: get deliveryman-picker shifts statistics")
    fun getDeliverymanPickerShiftsStatsTest() {

        val profile = commonPreconditions.createProfileDeliverymanPicker()
        val authRequest = msmktPreconditions.fillAuthRequest(password = profile.generatedPassword!!)
        val token = msmktActions.authProfilePassword(authRequest)!!.accessToken

        val emptyStat = msmktActions.getShiftsStatistics(token, range)!!
        commonPreconditions.startAndStopShift(profile.profileId)
        commonPreconditions.startAndStopShift(profile.profileId, userRole = ShiftUserRole.PICKER)
        val stat = msmktActions.getShiftsStatistics(token, range)!!

        msmktAssertion.checkShiftsStatisticsEmpty(emptyStat)
        msmktAssertion.checkShiftsStatistics(stat.shifts[0], WorkedOutShiftType.PICKING)
        msmktAssertion.checkShiftsStatistics(stat.shifts[1], WorkedOutShiftType.DELIVERY)
    }

    @Test
    @DisplayName("Shifts Statistics: get admin shifts statistics")
    fun getAdminShiftsStatsTest() {

        val profile = commonPreconditions.createProfileDarkstoreAdmin()
        val authRequest = msmktPreconditions.fillAuthRequest(password = profile.generatedPassword!!)
        val token = msmktActions.authProfilePassword(authRequest)!!.accessToken

        val emptyStat = msmktActions.getShiftsStatistics(token, range)!!
        commonPreconditions.startAndStopShift(profile.profileId, userRole = ShiftUserRole.DARKSTORE_ADMIN)
        val stat = msmktActions.getShiftsStatistics(token, range)!!

        msmktAssertion.checkShiftsStatisticsEmpty(emptyStat)
        msmktAssertion.checkShiftsStatisticsEmpty(stat)
    }

    @Test
    @DisplayName("Shifts Statistics: open shifts")
    fun getOpenShiftsStatsTest() {

        val profile = commonPreconditions.createProfileDeliveryman()
        val authRequest = msmktPreconditions.fillAuthRequest(password = profile.generatedPassword!!)
        val token = msmktActions.authProfilePassword(authRequest)!!.accessToken

        commonPreconditions.startShift(profile.profileId)
        val emptyStat = msmktActions.getShiftsStatistics(token, range)!!
        commonPreconditions.stopShift(profile.profileId)
        val stat = msmktActions.getShiftsStatistics(token, range)

        msmktAssertion.checkShiftsStatisticsEmpty(emptyStat)
        msmktAssertion.checkShiftsStatistics(stat!!.shifts[0], WorkedOutShiftType.DELIVERY)
    }

    @Test
    @DisplayName("Shifts Statistics: with paging")
    fun getDeliverymanPickerShiftsStatsWithPagingTest() {

        val profile = commonPreconditions.createProfileDeliverymanPicker()
        val authRequest = msmktPreconditions.fillAuthRequest(password = profile.generatedPassword!!)
        val token = msmktActions.authProfilePassword(authRequest)!!.accessToken

        commonPreconditions.startAndStopShift(profile.profileId)
        commonPreconditions.startAndStopShift(profile.profileId, userRole = ShiftUserRole.PICKER)
        val statPage1 = msmktActions.getShiftsStatisticsPageSize(token, range, "1")!!
        val statPage2 = msmktActions.getShiftsStatisticsPaging(token, range, "2", statPage1.paging.nextPageMark!!)!!

        msmktAssertion.checkShiftsStatistics(statPage1.shifts[0], WorkedOutShiftType.PICKING)
        msmktAssertion.checkShiftsStatistics(statPage2.shifts[0], WorkedOutShiftType.DELIVERY)
    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Shifts Statistics: wrong range")
    fun getShiftsStatsWrongRangeTest() {

        val profile = commonPreconditions.createProfileDeliveryman()
        val authRequest = msmktPreconditions.fillAuthRequest(password = profile.generatedPassword!!)
        val token = msmktActions.authProfilePassword(authRequest)!!.accessToken

        commonPreconditions.startAndStopShift(profile.profileId)
        val stat = msmktActions.getShiftsStatisticsError(token, range, HttpStatus.SC_BAD_REQUEST)

        commonAssertion.checkErrorMessage(stat.message!!, "'from' should start before than 'to'")
    }
}
