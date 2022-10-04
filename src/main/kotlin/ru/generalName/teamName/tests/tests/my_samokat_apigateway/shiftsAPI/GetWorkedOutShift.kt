package ru.samokat.mysamokat.tests.tests.my_samokat_apigateway.shiftsAPI

import org.apache.http.HttpStatus
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
import java.time.LocalDate
import java.util.*

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Tag("msmkt-apigateway")
class GetWorkedOutShift {

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
    @DisplayName("Get workedOut shift: deliveryman shift on foreign  darkstore")
    fun getWorkedOutShiftOnForeignDarkstore() {
        val profile = commonPreconditions.createProfileDeliveryman()
        val shiftId = commonPreconditions.startAndStopShift(profile.profileId, darkstoreId = Constants.updatedDarkstoreId).shiftId
        val authRequest = msmktPreconditions.fillAuthRequest(password = profile.generatedPassword!!)

        val token = msmktActions.authProfilePassword(authRequest)!!.accessToken

        val workedOutShift = msmktActions.getWorkedOutShiftById(token, shiftId)

        msmktAssertion.checkWorkedOutShiftInfoById(workedOutShift, darkstoreId = Constants.updatedDarkstoreId)

    }

    @Test
    @DisplayName("Get workedOut shift: with orders")
    fun getWorkedOutShiftWithOrders() {
        val profile = commonPreconditions.createProfileDeliveryman()
        val shiftId = commonPreconditions.startAndStopShift(profile.profileId).shiftId
        val authRequest = msmktPreconditions.fillAuthRequest(password = profile.generatedPassword!!)

        val token = msmktActions.authProfilePassword(authRequest)!!.accessToken
        msmktActions.updateShiftOrdersCount(shiftId, 10)

        val workedOutShift = msmktActions.getWorkedOutShiftById(token, shiftId)

        msmktAssertion.checkWorkedOutShiftInfoById(workedOutShift, deliveredOrdersCount = 10)

    }

    @Test
    @DisplayName("Get workedOut shift: picker shift")
    fun getWorkedOutShiftPicker() {
        val profile = commonPreconditions.createProfileDeliverymanPicker()
        val shiftId = commonPreconditions.startAndStopShift(profile.profileId, userRole = ShiftUserRole.PICKER).shiftId
        val authRequest = msmktPreconditions.fillAuthRequest(password = profile.generatedPassword!!)

        val token = msmktActions.authProfilePassword(authRequest)!!.accessToken

        val workedOutShift = msmktActions.getWorkedOutShiftById(token, shiftId)

        msmktAssertion.checkWorkedOutShiftInfoById(workedOutShift, role = EmployeeRole.PICKER)

    }

    @Test
    @DisplayName("Get workedOut shift: admin shift")
    fun getWorkedOutShiftAdmin() {
        val profile = commonPreconditions.createProfileDarkstoreAdmin()
        val shiftId = commonPreconditions.startAndStopShift(profile.profileId, userRole = ShiftUserRole.DARKSTORE_ADMIN).shiftId
        val authRequest = msmktPreconditions.fillAuthRequest(password = profile.generatedPassword!!)

        val token = msmktActions.authProfilePassword(authRequest)!!.accessToken

        val workedOutShift = msmktActions.getWorkedOutShiftById(token, shiftId)

        msmktAssertion.checkWorkedOutShiftInfoById(workedOutShift, role = EmployeeRole.DARKSTORE_ADMIN)
    }

    @Test
    @DisplayName("Get workedOut shift: open shift")
    fun getWorkedOutShiftOpenShift() {
        val profile = commonPreconditions.createProfileDeliveryman()
        val shiftId = commonPreconditions.startShift(profile.profileId).shiftId
        val authRequest = msmktPreconditions.fillAuthRequest(password = profile.generatedPassword!!)

        val token = msmktActions.authProfilePassword(authRequest)!!.accessToken

        val workedOutShift = msmktActions.getWorkedOutShiftByIdError(token, shiftId, HttpStatus.SC_NOT_FOUND)
    }

    @Test
    @DisplayName("Get workedOut shift: shift not exists")
    fun getWorkedOutShiftNotExist() {
        val profile = commonPreconditions.createProfileDeliveryman()
        val authRequest = msmktPreconditions.fillAuthRequest(password = profile.generatedPassword!!)

        val token = msmktActions.authProfilePassword(authRequest)!!.accessToken

        val workedOutShift = msmktActions.getWorkedOutShiftByIdError(token, UUID.randomUUID(), HttpStatus.SC_NOT_FOUND)
    }

    @Test
    @DisplayName("Get workedOut shift: profile disabled")
    fun getWorkedOutShiftProfileDisabled() {
        val profile = commonPreconditions.createProfileDeliveryman()
        val authRequest = msmktPreconditions.fillAuthRequest(password = profile.generatedPassword!!)
        val shiftId = commonPreconditions.startAndStopShift(profile.profileId).shiftId

        val token = msmktActions.authProfilePassword(authRequest)!!.accessToken
        employeeActions.deleteProfile(profile.profileId)

        val workedOutShift = msmktActions.getWorkedOutShiftById(token, shiftId)

        msmktAssertion.checkWorkedOutShiftInfoById(workedOutShift)
    }

    @Test
    @DisplayName("Get workedOut: get assignment")
    fun getWorkedOutAssignment(){

        val dayRange = commonPreconditions.getFormattedTimeRange(LocalDate.now().plusDays(1L), "00:00", "23:00")
        val assignmentRange = commonPreconditions.getFormattedTimeRange(LocalDate.now().plusDays(1L), "09:00", "10:00")

        val profile = commonPreconditions.createProfileDeliveryman()
        val authRequest = msmktPreconditions.fillAuthRequest(password = profile.generatedPassword!!)
        val assignmentId = commonPreconditions.createAssignment(
            profile.profileId, dayRange, assignmentRange
        )

        val token = msmktActions.authProfilePassword(authRequest)!!.accessToken
        val workedOutShift = msmktActions.getWorkedOutShiftByIdError(token, assignmentId, HttpStatus.SC_NOT_FOUND)

    }

}