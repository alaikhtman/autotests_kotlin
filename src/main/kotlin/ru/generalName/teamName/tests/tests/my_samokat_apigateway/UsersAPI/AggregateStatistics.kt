package ru.samokat.mysamokat.tests.tests.my_samokat_apigateway.UsersAPI

import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.samokat.employeeprofiles.api.common.domain.EmployeeVehicleType
import ru.samokat.employeeprofiles.api.common.domain.Vehicle
import ru.samokat.my.domain.employee.EmployeeRole
import ru.samokat.my.rest.api.enum.ApiEnum
import ru.samokat.mysamokat.apigateway.api.user.getstatistics.aggregated.StatisticsAggregationType
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
class AggregateStatistics {

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
    }

    @AfterEach
    fun release() {
        msmktAssertion.assertAll()
        employeeActions.deleteProfile(Constants.mobile1)
    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Aggregate Statistics: get deliveryman weekly statistics")
    fun getDeliverymanAggregateWeeklyStatsTest() {

        val profile = commonPreconditions.createProfileDeliveryman()
        val authRequest = msmktPreconditions.fillAuthRequest(password = profile.generatedPassword!!)
        val token = msmktActions.authProfilePassword(authRequest)!!.accessToken

        val emptyStat = msmktActions.getAggregatedStatistics(token, StatisticsAggregationType.WEEKLY.toString())!!
        commonPreconditions.startAndStopShift(profile.profileId)
        val stat = msmktActions.getAggregatedStatistics(token, StatisticsAggregationType.WEEKLY.toString())!!

        msmktAssertion.checkAggregatedStatisticsEmpty(emptyStat)
        msmktAssertion.checkAggregatedStatistics(stat, 1, 0)
    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Aggregate Statistics: get deliveryman monthly statistics")
    fun getDeliverymanAggregateMonthlyStatsTest() {

        val profile = commonPreconditions.createProfileDeliveryman()
        val authRequest = msmktPreconditions.fillAuthRequest(password = profile.generatedPassword!!)
        val token = msmktActions.authProfilePassword(authRequest)!!.accessToken

        val emptyStat = msmktActions.getAggregatedStatistics(token, StatisticsAggregationType.MONTHLY.toString())!!
        commonPreconditions.startAndStopShift(profile.profileId)
        val stat = msmktActions.getAggregatedStatistics(token, StatisticsAggregationType.MONTHLY.toString())!!

        msmktAssertion.checkAggregatedStatisticsEmpty(emptyStat)
        msmktAssertion.checkAggregatedStatistics(stat, 1, 0)
    }

    @Test
    @DisplayName("Aggregate Statistics: get picker weekly statistics")
    fun getPickerAggregateWeeklyStatsTest() {

        val profile = commonPreconditions.createProfilePicker()
        val authRequest = msmktPreconditions.fillAuthRequest(password = profile.generatedPassword!!)
        val token = msmktActions.authProfilePassword(authRequest)!!.accessToken

        val emptyStat = msmktActions.getAggregatedStatistics(token, StatisticsAggregationType.WEEKLY.toString())!!
        commonPreconditions.startAndStopShift(profile.profileId, ShiftUserRole.PICKER)
        val stat = msmktActions.getAggregatedStatistics(token, StatisticsAggregationType.WEEKLY.toString())!!

        msmktAssertion.checkAggregatedStatisticsEmpty(emptyStat)
        msmktAssertion.checkAggregatedStatistics(stat, 0, 1)
    }

    @Test
    @DisplayName("Aggregate Statistics: get picker monthly statistics")
    fun getPickerAggregateMonthlyStatsTest() {

        val profile = commonPreconditions.createProfilePicker()
        val authRequest = msmktPreconditions.fillAuthRequest(password = profile.generatedPassword!!)
        val token = msmktActions.authProfilePassword(authRequest)!!.accessToken

        val emptyStat = msmktActions.getAggregatedStatistics(token, StatisticsAggregationType.MONTHLY.toString())!!
        commonPreconditions.startAndStopShift(profile.profileId, ShiftUserRole.PICKER)
        val stat = msmktActions.getAggregatedStatistics(token, StatisticsAggregationType.MONTHLY.toString())!!

        msmktAssertion.checkAggregatedStatisticsEmpty(emptyStat)
        msmktAssertion.checkAggregatedStatistics(stat, 0, 1)
    }

    @Test
    @DisplayName("Aggregate Statistics: get picker monthly statistics")
    fun getAdminAggregateMonthlyStatsTest() {

        val profile = commonPreconditions.createProfileDarkstoreAdmin()
        val authRequest = msmktPreconditions.fillAuthRequest(password = profile.generatedPassword!!)
        val token = msmktActions.authProfilePassword(authRequest)!!.accessToken

        val emptyStat = msmktActions.getAggregatedStatistics(token, StatisticsAggregationType.MONTHLY.toString())!!
        commonPreconditions.startAndStopShift(profile.profileId, ShiftUserRole.DARKSTORE_ADMIN)
        val stat = msmktActions.getAggregatedStatistics(token, StatisticsAggregationType.MONTHLY.toString())!!

        msmktAssertion.checkAggregatedStatisticsEmpty(emptyStat)
        msmktAssertion.checkAggregatedStatisticsEmpty(stat)
    }

    @Test
    @DisplayName("Aggregate Statistics: get picker weekly statistics")
    fun getAdminAggregateWeeklyStatsTest() {

        val profile = commonPreconditions.createProfileDarkstoreAdmin()
        val authRequest = msmktPreconditions.fillAuthRequest(password = profile.generatedPassword!!)
        val token = msmktActions.authProfilePassword(authRequest)!!.accessToken

        val emptyStat = msmktActions.getAggregatedStatistics(token, StatisticsAggregationType.WEEKLY.toString())!!
        commonPreconditions.startAndStopShift(profile.profileId, ShiftUserRole.DARKSTORE_ADMIN)
        val stat = msmktActions.getAggregatedStatistics(token, StatisticsAggregationType.WEEKLY.toString())!!

        msmktAssertion.checkAggregatedStatisticsEmpty(emptyStat)
        msmktAssertion.checkAggregatedStatisticsEmpty(stat)
    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Aggregate Statistics: get deliveryman weekly statistics with open shifts")
    fun getDeliverymanAggregateWeeklyStatsOpenShiftTest() {

        val profile = commonPreconditions.createProfileDeliveryman()
        val authRequest = msmktPreconditions.fillAuthRequest(password = profile.generatedPassword!!)
        val token = msmktActions.authProfilePassword(authRequest)!!.accessToken

        val emptyStat = msmktActions.getAggregatedStatistics(token, StatisticsAggregationType.WEEKLY.toString())!!
        commonPreconditions.startShift(profile.profileId)
        val statOpenShift = msmktActions.getAggregatedStatistics(token, StatisticsAggregationType.WEEKLY.toString())!!
        commonPreconditions.stopShift(profile.profileId)
        val stat = msmktActions.getAggregatedStatistics(token, StatisticsAggregationType.WEEKLY.toString())!!

        msmktAssertion.checkAggregatedStatisticsEmpty(emptyStat)
        msmktAssertion.checkAggregatedStatisticsEmpty(statOpenShift)
        msmktAssertion.checkAggregatedStatistics(stat, 1, 0)
    }
}