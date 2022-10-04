package ru.samokat.mysamokat.tests.tests.shifts.activeShiftsAPI

import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.samokat.employeeprofiles.api.common.domain.Vehicle
import ru.samokat.employeeprofiles.api.common.domain.EmployeeVehicleType
import ru.samokat.my.rest.api.enum.ApiEnum
import ru.samokat.mysamokat.tests.checkers.ShiftAssertion
import ru.samokat.mysamokat.tests.dataproviders.Constants
import ru.samokat.mysamokat.tests.dataproviders.preconditions.CommonPreconditions
import ru.samokat.mysamokat.tests.dataproviders.preconditions.EmployeePreconditions
import ru.samokat.mysamokat.tests.dataproviders.preconditions.ShiftsPreconditions
import ru.samokat.mysamokat.tests.helpers.actions.EmployeeActions
import ru.samokat.mysamokat.tests.helpers.actions.ShiftsActions
import ru.samokat.shifts.api.common.domain.ShiftUserPermission
import java.time.Instant

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Tag("shifts")
class GetActiveShiftByProfile {

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
    }

    @AfterEach
    fun release() {
        shiftsAssertion.assertAll()
        employeeActions.deleteProfile(Constants.mobile1)
    }

    @Test
    @Tags (Tag("smoke"))
    @DisplayName("Get open shift")
    fun getOpenShift() {

        val profileId = commonPreconditions.createProfileDeliveryman().profileId
        val openShiftRequest = shiftsPreconditions.fillStartShiftRequest(
            userId = profileId,
            deliveryMethod = null
        )
        val openShift = shiftsActions.startShift(openShiftRequest)
        val activeShift = shiftsActions.getActiveShiftByProfileId(profileId)

        shiftsAssertion
            .checkStartShiftInfo(openShiftRequest, activeShift, null, Instant.now())
            .checkUserPermissions(activeShift.userPermissions, listOf(ApiEnum(ShiftUserPermission.DELIVERY)))
    }

    @Test
    @Tags (Tag("smoke"))
    @DisplayName("Get open shift on foreign darkstore")
    fun getOpenShiftOnForeignDarkstore() {

        val profileId = commonPreconditions.createProfileDeliveryman().profileId
        val openShiftRequest = shiftsPreconditions.fillStartShiftRequest(
            userId = profileId,
            deliveryMethod = null,
            darkstoreId = Constants.updatedDarkstoreId
        )
        val openShift = shiftsActions.startShift(openShiftRequest)
        val activeShift = shiftsActions.getActiveShiftByProfileId(profileId)

        shiftsAssertion
            .checkStartShiftInfo(openShiftRequest, activeShift, null, Instant.now())
            .checkUserPermissions(activeShift.userPermissions, listOf(ApiEnum(ShiftUserPermission.DELIVERY)))
    }

    @Test
    @Tags (Tag("smoke"))
    @DisplayName("Open shift not exists")
    fun openShiftNotExists() {

        val profileId = commonPreconditions.createProfileDeliveryman().profileId
        val errorMessage = shiftsActions.getActiveShiftByProfileIdWithError(profileId).message

        shiftsAssertion
            .checkErrorMessage(errorMessage, "Active shift was not found")
    }
}