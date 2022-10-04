package ru.samokat.mysamokat.tests.tests.shifts.activeShiftsAPI

import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.samokat.my.domain.employee.EmployeeRole
import ru.samokat.my.rest.api.enum.ApiEnum
import ru.samokat.mysamokat.tests.checkers.ShiftAssertion
import ru.samokat.mysamokat.tests.dataproviders.Constants
import ru.samokat.mysamokat.tests.dataproviders.preconditions.CommonPreconditions
import ru.samokat.mysamokat.tests.dataproviders.preconditions.EmployeePreconditions
import ru.samokat.mysamokat.tests.dataproviders.preconditions.ShiftsPreconditions
import ru.samokat.mysamokat.tests.helpers.actions.EmployeeActions
import ru.samokat.mysamokat.tests.helpers.actions.ShiftsActions
import ru.samokat.shifts.api.common.domain.ShiftUserPermission
import ru.samokat.shifts.api.common.domain.ShiftUserRole
import java.time.Instant

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Tag("shifts")
class GetActiveShiftsByDarkstore {

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
        employeeActions.deleteProfile(Constants.mobile3)
        employeeActions.deleteProfile(Constants.mobile4)
        employeeActions.deleteProfile(Constants.mobile5)
    }

    @AfterEach
    fun release() {
        shiftsAssertion.assertAll()
        employeeActions.deleteProfile(Constants.mobile1)
        employeeActions.deleteProfile(Constants.mobile2)
        employeeActions.deleteProfile(Constants.mobile3)
        employeeActions.deleteProfile(Constants.mobile4)
        employeeActions.deleteProfile(Constants.mobile5)
    }

    @Test
    @Tags (Tag("smoke"), Tag("darkstore_integration"))
    @DisplayName("Get open shifts")
    fun getOpenShift() {

        val profileId = commonPreconditions.createProfileDeliveryman().profileId
        val openShiftRequest = shiftsPreconditions.fillStartShiftRequest(
            userId = profileId,
            deliveryMethod = null
        )
        val getShiftsRequest = shiftsPreconditions.fillGetActiveShiftListRequest()
        val openShift = shiftsActions.startShift(openShiftRequest)
        val darkstoreShifts = shiftsActions.getActiveShiftsByDarkstore(getShiftsRequest)

        shiftsAssertion
            .checkDarkstoreShiftInfo(openShiftRequest, darkstoreShifts, null, Instant.now())
    }

    @Test
    @DisplayName("Get open shifts (foreign darkstore)")
    fun getOpenShiftForeignDarkstore() {

        val profileId = commonPreconditions.createProfileDeliveryman().profileId
        val openShiftRequest = shiftsPreconditions.fillStartShiftRequest(
            userId = profileId,
            deliveryMethod = null,
            darkstoreId = Constants.updatedDarkstoreId
        )
        val getShiftsRequest = shiftsPreconditions.fillGetActiveShiftListRequest(darkstoreId = Constants.updatedDarkstoreId)
        val openShift = shiftsActions.startShift(openShiftRequest)
        val darkstoreShifts = shiftsActions.getActiveShiftsByDarkstore(getShiftsRequest)

        shiftsAssertion
            .checkDarkstoreShiftInfo(openShiftRequest, darkstoreShifts, null, Instant.now())
    }

    @Test
    @Tags (Tag("smoke"))
    @DisplayName("Get open shifts (several roles)")
    fun getOpenShiftSeveralRoles() {

        val profileIdDeliveryman = commonPreconditions.createProfileDeliveryman().profileId
        val profileIdPicker = commonPreconditions.createProfilePicker(Constants.mobile2).profileId
        val openShiftDeliverymanRequest = shiftsPreconditions.fillStartShiftRequest(
            userId = profileIdDeliveryman,
            deliveryMethod = null
        )
        val openShiftPickerRequest = shiftsPreconditions.fillStartShiftRequest(
            userId = profileIdPicker,
            userRole = ShiftUserRole.PICKER,
            deliveryMethod = null
        )
        val getShiftsRequest = shiftsPreconditions.fillGetActiveShiftListRequest()
        val openShiftDeliveryman = shiftsActions.startShift(openShiftDeliverymanRequest)
        val openShiftPicker = shiftsActions.startShift(openShiftPickerRequest)
        val darkstoreShifts = shiftsActions.getActiveShiftsByDarkstore(getShiftsRequest)

        shiftsAssertion
            .checkDarkstoreShiftInfo(openShiftDeliverymanRequest, darkstoreShifts, null, Instant.now())
            .checkDarkstoreShiftInfo(openShiftPickerRequest, darkstoreShifts, null, Instant.now())
    }

    @Test
    @Tags (Tag("smoke"))
    @DisplayName("Get open shifts by permissions")
    fun getOpenShiftsByPermissions(){

        val profileIdDeliveryman = commonPreconditions.createProfileDeliveryman().profileId
        val profileIdPicker = commonPreconditions.createProfilePicker(Constants.mobile2).profileId
        val profileIdReceivingClerk = commonPreconditions.createProfile(
            roles = listOf(ApiEnum(EmployeeRole.RECEIVING_CLERK)),
            mobile = Constants.mobile3, darkstoreId = Constants.darkstoreId, cityId = Constants.cityId).profileId
        val profileIdGoodsManager = commonPreconditions.createProfileGoodsManager(
            mobile = Constants.mobile4).profileId
        val profileIdAdmin = commonPreconditions.createProfileDarkstoreAdmin(
            mobile = Constants.mobile5).profileId

        val openShiftDeliverymanRequest = shiftsPreconditions.fillStartShiftRequest(
            userId = profileIdDeliveryman,
            deliveryMethod = null)
        val openShiftRequestPicker = shiftsPreconditions.fillStartShiftRequest(
            userId = profileIdPicker, userRole = ShiftUserRole.PICKER)
        val openShiftReceivingClerkRequest = shiftsPreconditions.fillStartShiftRequest(
            userId = profileIdReceivingClerk,
            userRole = ShiftUserRole.RECEIVING_CLERK)
        val openShiftGoodsManagerRequest = shiftsPreconditions.fillStartShiftRequest(
            userId = profileIdGoodsManager,
            userRole = ShiftUserRole.GOODS_MANAGER)
        val openShiftAdminRequest = shiftsPreconditions.fillStartShiftRequest(
            userId = profileIdAdmin,
            userRole = ShiftUserRole.DARKSTORE_ADMIN)

        val shiftDeliveryman = shiftsActions.startShift(openShiftDeliverymanRequest)
        val shiftPicker = shiftsActions.startShift(openShiftRequestPicker)
        val shiftReceivingClerk = shiftsActions.startShift(openShiftReceivingClerkRequest)
        val shiftGoodsManager = shiftsActions.startShift(openShiftGoodsManagerRequest)
        val shiftAdmin = shiftsActions.startShift(openShiftAdminRequest)


        // дернуть по пермиссиям

        val getShiftsDeliveryPermissionsRequest = shiftsPreconditions.fillGetActiveShiftListRequest(
            userPermission = ShiftUserPermission.DELIVERY
        )
        val getShiftsPickingPermissionsRequest = shiftsPreconditions.fillGetActiveShiftListRequest(
            userPermission = ShiftUserPermission.PICKING
        )
        val getShiftsManagementPermissionsRequest = shiftsPreconditions.fillGetActiveShiftListRequest(
            userPermission = ShiftUserPermission.MANAGEMENT
        )
        val getShiftsReceivingPermissionsRequest = shiftsPreconditions.fillGetActiveShiftListRequest(
            userPermission = ShiftUserPermission.RECEIVING
        )
        val getShiftsHubDeliveryPermissionsRequest = shiftsPreconditions.fillGetActiveShiftListRequest(
            userPermission = ShiftUserPermission.HUB_DELIVERY
        )

        val darkstoreShiftsDelivery = shiftsActions.getActiveShiftsByDarkstore(getShiftsDeliveryPermissionsRequest)
        val darkstoreShiftsPicking = shiftsActions.getActiveShiftsByDarkstore(getShiftsPickingPermissionsRequest)
        val darkstoreShiftsManagement = shiftsActions.getActiveShiftsByDarkstore(getShiftsManagementPermissionsRequest)
        val darkstoreShiftsReceiving = shiftsActions.getActiveShiftsByDarkstore(getShiftsReceivingPermissionsRequest)
        val darkstoreShiftsHubDelivery = shiftsActions.getActiveShiftsByDarkstore(getShiftsHubDeliveryPermissionsRequest)

        shiftsAssertion

            .checkShiftIsPresentInList(darkstoreShiftsDelivery, profileIdDeliveryman)
            .checkShiftIsNotPresentInList(darkstoreShiftsDelivery, profileIdPicker)
            .checkShiftIsPresentInList(darkstoreShiftsDelivery, profileIdAdmin)
            .checkShiftIsPresentInList(darkstoreShiftsDelivery, profileIdGoodsManager)
            .checkShiftIsNotPresentInList(darkstoreShiftsDelivery, profileIdReceivingClerk)

            .checkShiftIsNotPresentInList(darkstoreShiftsPicking, profileIdDeliveryman)
            .checkShiftIsPresentInList(darkstoreShiftsPicking, profileIdPicker)
            .checkShiftIsPresentInList(darkstoreShiftsPicking, profileIdAdmin)
            .checkShiftIsPresentInList(darkstoreShiftsPicking, profileIdGoodsManager)
            .checkShiftIsNotPresentInList(darkstoreShiftsPicking, profileIdReceivingClerk)

            .checkShiftIsNotPresentInList(darkstoreShiftsManagement, profileIdDeliveryman)
            .checkShiftIsNotPresentInList(darkstoreShiftsManagement, profileIdPicker)
            .checkShiftIsPresentInList(darkstoreShiftsManagement, profileIdAdmin)
            .checkShiftIsPresentInList(darkstoreShiftsManagement, profileIdGoodsManager)
            .checkShiftIsNotPresentInList(darkstoreShiftsManagement, profileIdReceivingClerk)

            .checkShiftIsNotPresentInList(darkstoreShiftsReceiving, profileIdDeliveryman)
            .checkShiftIsNotPresentInList(darkstoreShiftsReceiving, profileIdPicker)
            .checkShiftIsPresentInList(darkstoreShiftsReceiving, profileIdAdmin)
            .checkShiftIsPresentInList(darkstoreShiftsReceiving, profileIdGoodsManager)
            .checkShiftIsPresentInList(darkstoreShiftsReceiving, profileIdReceivingClerk)

            .checkShiftIsNotPresentInList(darkstoreShiftsHubDelivery, profileIdDeliveryman)
            .checkShiftIsNotPresentInList(darkstoreShiftsHubDelivery, profileIdPicker)
            .checkShiftIsPresentInList(darkstoreShiftsHubDelivery, profileIdAdmin)
            .checkShiftIsPresentInList(darkstoreShiftsHubDelivery, profileIdGoodsManager)
            .checkShiftIsNotPresentInList(darkstoreShiftsHubDelivery, profileIdReceivingClerk)


    }

    @Test
    @DisplayName("Get open shifts by permissions (empty result)")
    fun getOpenShiftsByPermissionEmptyResults() {
        val profileId = commonPreconditions.createProfileDeliveryman().profileId
        val openShiftRequest = shiftsPreconditions.fillStartShiftRequest(
            userId = profileId,
            deliveryMethod = null
        )
        val getShiftsRequest = shiftsPreconditions.fillGetActiveShiftListRequest(userPermission = ShiftUserPermission.PICKING)
        val openShift = shiftsActions.startShift(openShiftRequest)
        val darkstoreShifts = shiftsActions.getActiveShiftsByDarkstore(getShiftsRequest)

        shiftsAssertion
            .checkShiftsListCount(darkstoreShifts, 0)
    }

}