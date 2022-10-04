package ru.samokat.mysamokat.tests.tests.shifts.workedOutShiftsAPI

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
import java.util.*

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Tag("shifts")
class GetWorkedOutShiftById {

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
    @DisplayName("Get workedOut shift by id (deliveryman)")
    fun getWorkedOutShiftDeliveryman() {

        val profileId = commonPreconditions.createProfileDeliveryman().profileId
        val shift = commonPreconditions.startAndStopShift(profileId)

        val workedOutShift = shiftsActions.getWorkedOutShiftById(shift.shiftId)

        shiftsAssertion
            .checkWorkedOutShiftInfo(shift, workedOutShift)
    }

    @Test
    @DisplayName("Get workedOut shift by id (picker)")
    fun getWorkedOutShiftPicker() {

        val profileId = commonPreconditions.createProfilePicker().profileId
        val shift = commonPreconditions.startAndStopShift(profileId, userRole = ShiftUserRole.PICKER)

        val workedOutShift = shiftsActions.getWorkedOutShiftById(shift.shiftId)

        shiftsAssertion
            .checkWorkedOutShiftInfo(shift, workedOutShift)
    }

    @Test
    @DisplayName("Get workedOut shift by id (shift not exists)")
    fun getWorkedOutShiftNotExists() {

        val errMessage = shiftsActions.getWorkedOutShiftByIdWithError(UUID.randomUUID())

        shiftsAssertion
            .checkErrorMessage(errMessage, "Worked-out shift was not found")
    }

    @Test
    @DisplayName("Get workedOut shift by id (shift is active)")
    fun getWorkedOutShiftIsActive() {
        val profileId = commonPreconditions.createProfileDeliveryman().profileId
        val openShiftRequest = shiftsPreconditions.fillStartShiftRequest(userId = profileId)

        val shift = shiftsActions.startShift(openShiftRequest)

        val errMessage = shiftsActions.getWorkedOutShiftByIdWithError(UUID.randomUUID())

        shiftsAssertion
            .checkErrorMessage(errMessage, "Worked-out shift was not found")
    }

    @Test
    @DisplayName("Get workedOut shift by id (user is disabled)")
    fun getWorkedOutShiftUserIsDisabled() {
        val profileId = commonPreconditions.createProfileDeliveryman().profileId
        val shift = commonPreconditions.startAndStopShift(profileId)

        employeeActions.deleteProfile(profileId)

        val workedOutShift = shiftsActions.getWorkedOutShiftById(shift.shiftId)

        shiftsAssertion
            .checkWorkedOutShiftInfo(shift, workedOutShift)
    }

}