package ru.samokat.mysamokat.tests.tests.my_samokat_apigateway.UsersAPI

import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.samokat.employeeprofiles.api.common.domain.EmployeeVehicleType
import ru.samokat.employeeprofiles.api.common.domain.Vehicle
import ru.samokat.my.domain.employee.EmployeeRole
import ru.samokat.my.rest.api.enum.ApiEnum
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
class ActualStatistics {

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
    @DisplayName("Actual Statistics: get deliveryman statistics")
    fun getDeliverymanActualStatsTest() {

        val profile = commonPreconditions.createProfileDeliveryman()
        val authRequest = msmktPreconditions.fillAuthRequest(password = profile.generatedPassword!!)
        val token = msmktActions.authProfilePassword(authRequest)!!.accessToken

        val emptyStat = msmktActions.getActualStatistics(token)!!
        commonPreconditions.startAndStopShift(profile.profileId)
        val stat = msmktActions.getActualStatistics(token)!!

        msmktAssertion.checkActualStatistics(emptyStat, 0, 0, 0, 0)
        msmktAssertion.checkActualStatistics(stat, 1, 0, 1, 0)
    }

    @Test
    @DisplayName("Actual Statistics: get deliveryman statistics (several shifts)")
    fun getDeliverymanActualStatsSeveralShiftsTest() {

        val profile = commonPreconditions.createProfileDeliveryman()
        val authRequest = msmktPreconditions.fillAuthRequest(password = profile.generatedPassword!!)
        val token = msmktActions.authProfilePassword(authRequest)!!.accessToken

        val emptyStat = msmktActions.getActualStatistics(token)!!
        commonPreconditions.startAndStopShift(profile.profileId)
        Thread.sleep(2_000)
        commonPreconditions.startAndStopShift(profile.profileId)
        val stat = msmktActions.getActualStatistics(token)!!

        msmktAssertion.checkActualStatistics(emptyStat, 0, 0, 0, 0)
        msmktAssertion.checkActualStatistics(stat, 2, 0, 2, 0)
    }

    @Test
    @DisplayName("Actual Statistics: get picker statistics")
    fun getPickerActualStatsTest() {

        val profile = commonPreconditions.createProfilePicker()
        val authRequest = msmktPreconditions.fillAuthRequest(password = profile.generatedPassword!!)
        val token = msmktActions.authProfilePassword(authRequest)!!.accessToken

        val emptyStat = msmktActions.getActualStatistics(token)!!
        commonPreconditions.startAndStopShift(profile.profileId, ShiftUserRole.PICKER)
        val stat = msmktActions.getActualStatistics(token)!!

        msmktAssertion.checkActualStatistics(emptyStat, 0, 0, 0, 0)
        msmktAssertion.checkActualStatistics(stat, 0, 1, 0, 1)
    }

    @Test
    @DisplayName("Actual Statistics: get admin statistics")
    fun getAdminActualStatsTest() {

        val profile = commonPreconditions.createProfileDarkstoreAdmin()
        val authRequest = msmktPreconditions.fillAuthRequest(password = profile.generatedPassword!!)
        val token = msmktActions.authProfilePassword(authRequest)!!.accessToken

        val emptyStat = msmktActions.getActualStatistics(token)!!
        commonPreconditions.startAndStopShift(profile.profileId, ShiftUserRole.DARKSTORE_ADMIN)
        val stat = msmktActions.getActualStatistics(token)!!

        msmktAssertion.checkActualStatistics(emptyStat, 0, 0, 0, 0)
        msmktAssertion.checkActualStatistics(stat, 0, 0, 0, 0)
    }

    @Test
    @DisplayName("Actual Statistics: get deliveryman-picker statistics")
    fun getDeliverymanPickerActualStatsTest() {

        val profile = commonPreconditions.createProfileDeliverymanPicker()
        val authRequest = msmktPreconditions.fillAuthRequest(password = profile.generatedPassword!!)
        val token = msmktActions.authProfilePassword(authRequest)!!.accessToken

        val emptyStat = msmktActions.getActualStatistics(token)!!
        commonPreconditions.startAndStopShift(profile.profileId)
        commonPreconditions.startAndStopShift(profile.profileId, ShiftUserRole.PICKER)
        val stat = msmktActions.getActualStatistics(token)!!

        msmktAssertion.checkActualStatistics(emptyStat, 0, 0, 0, 0)
        msmktAssertion.checkActualStatistics(stat, 1, 1, 1, 1)
    }

    @Test
    @DisplayName("Actual Statistics: open shifts")
    fun getDeliverymanActualStatsOpenShiftsTest() {

        val profile = commonPreconditions.createProfileDeliveryman()
        val authRequest = msmktPreconditions.fillAuthRequest(password = profile.generatedPassword!!)
        val token = msmktActions.authProfilePassword(authRequest)!!.accessToken

        val emptyStat = msmktActions.getActualStatistics(token)!!
        commonPreconditions.startShift(profile.profileId)
        val statActiveShifts = msmktActions.getActualStatistics(token)!!
        commonPreconditions.stopShift(profile.profileId)
        val stat = msmktActions.getActualStatistics(token)!!


        msmktAssertion.checkActualStatistics(emptyStat, 0, 0, 0, 0)
        msmktAssertion.checkActualStatistics(statActiveShifts, 0, 0, 0, 0)
        msmktAssertion.checkActualStatistics(stat, 1, 0, 1, 0)
    }

    @Test
    @DisplayName("Actual Statistics: disabled profile")
    fun getDeliverymanActualStatsDisabledTest() {

        val profile = commonPreconditions.createProfileDeliveryman()
        val authRequest = msmktPreconditions.fillAuthRequest(password = profile.generatedPassword!!)
        val token = msmktActions.authProfilePassword(authRequest)!!.accessToken

        val emptyStat = msmktActions.getActualStatistics(token)!!
        commonPreconditions.startAndStopShift(profile.profileId)
        employeeActions.deleteProfile(profile.profileId)
        val stat = msmktActions.getActualStatistics(token)!!

        msmktAssertion.checkActualStatistics(emptyStat, 0, 0, 0, 0)
        msmktAssertion.checkActualStatistics(stat, 1, 0, 1, 0)
    }

    @Test
    @DisplayName("Actual Statistics: with orderd")
    fun getDeliverymanActualStatsWithOrdersTest() {

        val profile = commonPreconditions.createProfileDeliveryman()
        val authRequest = msmktPreconditions.fillAuthRequest(password = profile.generatedPassword!!)
        val token = msmktActions.authProfilePassword(authRequest)!!.accessToken

        val emptyStat = msmktActions.getActualStatistics(token)!!
        commonPreconditions.startAndStopShift(profile.profileId)
        msmktActions.updateDeliveredOrdersCount(profile.profileId, 5)
        val stat = msmktActions.getActualStatistics(token)!!

        msmktAssertion.checkActualStatistics(emptyStat, 0, 0, 0, 0)
        msmktAssertion.checkActualStatistics(stat, 1, 0, 1, 0, 5)
    }

}