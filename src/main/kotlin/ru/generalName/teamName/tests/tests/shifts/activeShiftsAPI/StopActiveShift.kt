package ru.samokat.mysamokat.tests.tests.shifts.activeShiftsAPI

import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.samokat.employeeprofiles.api.common.domain.Vehicle
import ru.samokat.my.domain.employee.EmployeeRole
import ru.samokat.employeeprofiles.api.common.domain.EmployeeVehicleType
import ru.samokat.my.rest.api.enum.ApiEnum
import ru.samokat.mysamokat.tests.checkers.ShiftAssertion
import ru.samokat.mysamokat.tests.dataproviders.Constants
import ru.samokat.mysamokat.tests.dataproviders.preconditions.CommonPreconditions
import ru.samokat.mysamokat.tests.dataproviders.preconditions.EmployeePreconditions
import ru.samokat.mysamokat.tests.dataproviders.preconditions.ShiftsPreconditions
import ru.samokat.mysamokat.tests.helpers.actions.EmployeeActions
import ru.samokat.mysamokat.tests.helpers.actions.ShiftsActions
import ru.samokat.shifts.api.common.domain.ShiftUserRole
import ru.samokat.shifts.api.workedout.ShiftStopType

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Tag("shifts")
class StopActiveShift {

    private lateinit var shiftsPreconditions: ShiftsPreconditions

    @Autowired
    private lateinit var employeeActions: EmployeeActions

    private lateinit var employeePreconditions: EmployeePreconditions

    @Autowired
    private lateinit var commonPreconditions: CommonPreconditions

    @Autowired
    private lateinit var shiftsActions: ShiftsActions

    private lateinit var shiftsAssertion: ShiftAssertion
    @BeforeEach
    fun before() {
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

    @Test
    @Tags (Tag("smoke"), Tag("kafka_consume"))
    @DisplayName("Stop deliveryman shift")
    fun stopDeliverymanShift() {

        val profileId = commonPreconditions.createProfileDeliveryman().profileId
        val openShiftRequest = shiftsPreconditions.fillStartShiftRequest(
            userId = profileId,
            deliveryMethod = null)
        val stopShiftRequest = shiftsPreconditions.fillStopShiftRequest(profileId)
        val shift = shiftsActions.startShift(openShiftRequest)
        shiftsActions.stopActiveShift(stopShiftRequest)

        val kafkaEvent = shiftsActions.getMessageFromKafkaActiveShiftsLogByParam(shift.shiftId, "status", "stopped")

        val errorMessage = shiftsActions.getActiveShiftByProfileIdWithError(profileId).message

        shiftsAssertion
            .checkErrorMessage(errorMessage, "Active shift was not found")
            .checkActiveShiftsLogMessage(shift, kafkaEvent, "stopped", "manual")
    }

    @Test
    @Tags (Tag("smoke"))
    @DisplayName("Stop picker shift")
    fun stopPickerShift() {

        val profileId = commonPreconditions.createProfilePicker().profileId
        val openShiftRequest = shiftsPreconditions.fillStartShiftRequest(
            userId = profileId, userRole = ShiftUserRole.PICKER)
        val stopShiftRequest = shiftsPreconditions.fillStopShiftRequest(profileId)
        val shift = shiftsActions.startShift(openShiftRequest)
        shiftsActions.stopActiveShift(stopShiftRequest)

        val errorMessage = shiftsActions.getActiveShiftByProfileIdWithError(profileId).message

        shiftsAssertion
            .checkErrorMessage(errorMessage, "Active shift was not found")
    }

    @Test
    @Tags (Tag("smoke"))
    @DisplayName("Stop admin shift")
    fun stopAdminShift() {

        val profileId = commonPreconditions.createProfileDarkstoreAdmin().profileId
        val openShiftRequest = shiftsPreconditions.fillStartShiftRequest(
            userId = profileId,
            userRole = ShiftUserRole.DARKSTORE_ADMIN)
        val stopShiftRequest = shiftsPreconditions.fillStopShiftRequest(profileId)
        val shift = shiftsActions.startShift(openShiftRequest)
        shiftsActions.stopActiveShift(stopShiftRequest)

        val errorMessage = shiftsActions.getActiveShiftByProfileIdWithError(profileId).message

        shiftsAssertion
            .checkErrorMessage(errorMessage, "Active shift was not found")
    }

    @Test
    @Tags (Tag("smoke"))
    @DisplayName("Stop shift not exist")
    fun stopShiftNotExists() {

        val profileId = commonPreconditions.createProfileDeliveryman().profileId
        val stopShiftRequest = shiftsPreconditions.fillStopShiftRequest(profileId)
        shiftsActions.stopActiveShift(stopShiftRequest)

        val errorMessage = shiftsActions.getActiveShiftByProfileIdWithError(profileId).message

        shiftsAssertion
            .checkErrorMessage(errorMessage, "Active shift was not found")
    }

    @Test
    @Tag("empro_integration")
    @DisplayName("User is blocked")
    fun stopShiftUserIsBlocked() {

        val profileId = commonPreconditions.createProfileDeliveryman().profileId
        val openShiftRequest = shiftsPreconditions.fillStartShiftRequest(
            userId = profileId,
            deliveryMethod = null)
        val stopShiftRequest = shiftsPreconditions.fillStopShiftRequest(profileId)

        val shift = shiftsActions.startShift(openShiftRequest)
        employeeActions.deleteProfile(profileId)
        Thread.sleep(3_000)
        shiftsActions.stopActiveShift(stopShiftRequest)

        val errorMessage = shiftsActions.getActiveShiftByProfileIdWithError(profileId).message
        val workedOutShift = shiftsActions.getWorkedOutShiftById(shift.shiftId)

        shiftsAssertion
            .checkWorkedOutShiftInfo(shift, workedOutShift, stopType = ShiftStopType.AUTO)
            .checkErrorMessage(errorMessage, "Active shift was not found")
    }



}