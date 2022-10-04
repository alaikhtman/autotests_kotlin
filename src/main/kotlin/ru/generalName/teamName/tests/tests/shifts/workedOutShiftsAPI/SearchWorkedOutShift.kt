package ru.samokat.mysamokat.tests.tests.shifts.workedOutShiftsAPI

import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.samokat.employeeprofiles.api.common.domain.Vehicle
import ru.samokat.my.domain.employee.EmployeeRole
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

import ru.samokat.shifts.api.common.domain.ShiftUserRole
import ru.samokat.shifts.api.workedout.search.SearchWorkedOutShiftsRequest
import java.time.Instant



@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Tag("shifts")
class SearchWorkedOutShift {

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
        employeeActions.deleteProfile(Constants.mobile3)
        employeeActions.deleteProfile(Constants.mobile4)
        employeeActions.deleteProfile(Constants.mobile5)
    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Search workedOut shifts by role")
    fun searchWorkedOutShiftsByRole() {

        val profileIdDeliveryman = commonPreconditions.createProfileDeliveryman().profileId
        val profileIdPicker = commonPreconditions.createProfilePicker(Constants.mobile2).profileId

        val deliverymanShift = commonPreconditions.startAndStopShift(profileIdDeliveryman)
        val pickerShift = commonPreconditions.startAndStopShift(profileIdPicker, userRole = ShiftUserRole.PICKER)

        val getWorkedOutShiftsDeliverymanRequest = shiftsPreconditions.fillSearchWorkedOutShiftsRequests(
            darkstoreId = deliverymanShift.darkstoreId,
            userIds = setOf(profileIdDeliveryman, profileIdPicker),
            userRoles = setOf(ShiftUserRole.DELIVERYMAN),
            timeRange = TimeRange(Instant.now().minusSeconds(120), Instant.now().plusSeconds(120))
        )

        val getWorkedOutShiftsPickerRequest = shiftsPreconditions.fillSearchWorkedOutShiftsRequests(
            darkstoreId = pickerShift.darkstoreId,
            userIds = setOf(profileIdDeliveryman, profileIdPicker),
            userRoles = setOf(ShiftUserRole.PICKER),
            timeRange = TimeRange(Instant.now().minusSeconds(120), Instant.now().plusSeconds(120))
        )

        val workedOutDeliveryman = shiftsActions.searchWorkedOutShifts(getWorkedOutShiftsDeliverymanRequest)
        val workedOutPicker = shiftsActions.searchWorkedOutShifts(getWorkedOutShiftsPickerRequest)

        shiftsAssertion
            .checkShiftIsPresentInList(workedOutDeliveryman, deliverymanShift.shiftId)
            .checkShiftIsNotPresentInList(workedOutDeliveryman, pickerShift.shiftId)
            .checkShiftIsPresentInList(workedOutPicker, pickerShift.shiftId)
            .checkShiftIsNotPresentInList(workedOutPicker, deliverymanShift.shiftId)
            .checkWorkedOutShiftInfoInList(deliverymanShift, workedOutDeliveryman)
            .checkWorkedOutShiftInfoInList(pickerShift, workedOutPicker)
    }

    @Test
    @DisplayName("Search workedOut shifts (multirole)")
    fun searchWorkedOutShiftsMultirole() {
        val profileId = commonPreconditions.createProfileDeliverymanPicker().profileId

        val deliverymanShift = commonPreconditions.startAndStopShift(profileId, userRole = ShiftUserRole.DELIVERYMAN)
        val pickerShift = commonPreconditions.startAndStopShift(profileId, userRole = ShiftUserRole.PICKER)

        val getWorkedOutShiftsRequest = shiftsPreconditions.fillSearchWorkedOutShiftsRequests(
            darkstoreId = deliverymanShift.darkstoreId,
            userIds = setOf(profileId),
            userRoles = setOf(ShiftUserRole.DELIVERYMAN, ShiftUserRole.PICKER),
            timeRange = TimeRange(Instant.now().minusSeconds(120), Instant.now().plusSeconds(120))
        )

        val workedOutShifts = shiftsActions.searchWorkedOutShifts(getWorkedOutShiftsRequest)

        shiftsAssertion
            .checkShiftIsPresentInList(workedOutShifts, deliverymanShift.shiftId)
            .checkShiftIsPresentInList(workedOutShifts, pickerShift.shiftId)
            .checkWorkedOutShiftInfoInList(deliverymanShift, workedOutShifts)
            .checkWorkedOutShiftInfoInList(pickerShift, workedOutShifts)
    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Search workedOut shifts (timerange)")
    fun searchWorkedOutShiftsTimeRange() {
        val profileId1 = commonPreconditions.createProfileDeliveryman().profileId
        val profileId2 = commonPreconditions.createProfileDeliveryman(Constants.mobile2).profileId

        val shift1 = commonPreconditions.startShift(profileId1)
        Thread.sleep(60_000)
        val rangeStart = Instant.now()

        Thread.sleep(1_000)
        commonPreconditions.stopShift(profileId1)
        val shift2 = commonPreconditions.startShift(profileId2)

        Thread.sleep(60_000)
        val rangeStop = Instant.now()
        Thread.sleep(1_000)
        commonPreconditions.stopShift(profileId2)

        val getWorkedOutShiftsRequest = shiftsPreconditions.fillSearchWorkedOutShiftsRequests(
            darkstoreId = shift1.darkstoreId,
            userIds = setOf(profileId1, profileId2),
            userRoles = setOf(ShiftUserRole.DELIVERYMAN),
            timeRange = TimeRange(
                rangeStart, rangeStop
            )
        )

        val workedOut = shiftsActions.searchWorkedOutShifts(getWorkedOutShiftsRequest)

        shiftsAssertion
            .checkShiftIsPresentInList(workedOut, shift2.shiftId)
            .checkShiftIsNotPresentInList(workedOut, shift1.shiftId)
            .checkWorkedOutShiftInfoInList(shift2, workedOut)
    }

    @Test
    @DisplayName("Search workedOut shifts by role: empty shifts")
    fun searchWorkedOutShiftsNoShifts() {
        val profileId = commonPreconditions.createProfileDeliveryman().profileId

        val getWorkedOutShiftsRequest = shiftsPreconditions.fillSearchWorkedOutShiftsRequests(
            userIds = setOf(profileId),
            userRoles = setOf(ShiftUserRole.DELIVERYMAN),
            timeRange = TimeRange(Instant.now().minusSeconds(120), Instant.now().plusSeconds(120))
        )

        val workedOut = shiftsActions.searchWorkedOutShifts(getWorkedOutShiftsRequest)

        shiftsAssertion
            .checkShiftsListCount(workedOut, 0)
    }

    @Test
    @DisplayName("Search workedOut shifts by role: several employees")
    fun searchWorkedOutShiftsNoShiftsSeveralEmployees() {
        val profileIdDeliveryman = commonPreconditions.createProfileDeliveryman().profileId
        val profileIdPicker = commonPreconditions.createProfilePicker(Constants.mobile2).profileId

        val deliverymanShift = commonPreconditions.startAndStopShift(profileIdDeliveryman)

        val getWorkedOutShiftsRequest = shiftsPreconditions.fillSearchWorkedOutShiftsRequests(
            darkstoreId = deliverymanShift.darkstoreId,
            userIds = setOf(profileIdDeliveryman, profileIdPicker),
            userRoles = setOf(ShiftUserRole.DELIVERYMAN, ShiftUserRole.PICKER),
            timeRange = TimeRange(Instant.now().minusSeconds(120), Instant.now().plusSeconds(120))
        )

        val workedOut = shiftsActions.searchWorkedOutShifts(getWorkedOutShiftsRequest)

        shiftsAssertion
            .checkShiftIsPresentInList(workedOut, deliverymanShift.shiftId)
            .checkShiftsListCount(workedOut, 1)
    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Search workedOut shifts (pagination)")
    fun searchWorkedOutShiftsPagination() {

        val profileId1 = commonPreconditions.createProfileDeliveryman().profileId
        val profileId2 = commonPreconditions.createProfileDeliveryman(Constants.mobile2).profileId
        val profileId3 = commonPreconditions.createProfileDeliveryman(Constants.mobile3).profileId
        val profileId4 = commonPreconditions.createProfileDeliveryman(Constants.mobile4).profileId
        val profileId5 = commonPreconditions.createProfileDeliveryman(Constants.mobile5).profileId

        val shift1 = commonPreconditions.startAndStopShift(profileId1)
        val shift2 = commonPreconditions.startAndStopShift(profileId2)
        val shift3 = commonPreconditions.startAndStopShift(profileId3)
        val shift4 = commonPreconditions.startAndStopShift(profileId4)
        val shift5 = commonPreconditions.startAndStopShift(profileId5)

        val getWorkedOutShiftsRequestPage1 = shiftsPreconditions.fillSearchWorkedOutShiftsRequests(
            darkstoreId = shift1.darkstoreId,
            userIds = setOf(profileId1, profileId2, profileId3, profileId4, profileId5),
            userRoles = setOf(ShiftUserRole.DELIVERYMAN),
            timeRange = TimeRange(Instant.now().minusSeconds(120), Instant.now().plusSeconds(120)),
            paging = SearchWorkedOutShiftsRequest.PagingData(pageSize = 3, pageMark = null)
        )
        val workedOutShiftsPage1 = shiftsActions.searchWorkedOutShifts(getWorkedOutShiftsRequestPage1)


        val getWorkedOutShiftsRequestPage2 = shiftsPreconditions.fillSearchWorkedOutShiftsRequests(
            darkstoreId = shift1.darkstoreId,
            userIds = setOf(profileId1, profileId2, profileId3, profileId4, profileId5),
            userRoles = setOf(ShiftUserRole.DELIVERYMAN),
            timeRange = TimeRange(Instant.now().minusSeconds(120), Instant.now().plusSeconds(120)),
            paging = SearchWorkedOutShiftsRequest.PagingData(pageSize = 3, pageMark = workedOutShiftsPage1.paging.nextPageMark)
        )
        val workedOutShiftsPage2 = shiftsActions.searchWorkedOutShifts(getWorkedOutShiftsRequestPage2)

        shiftsAssertion
            .checkShiftIsPresentInList(workedOutShiftsPage1, shift1.shiftId)
            .checkShiftIsPresentInList(workedOutShiftsPage1, shift2.shiftId)
            .checkShiftIsPresentInList(workedOutShiftsPage1, shift3.shiftId)
            .checkShiftIsNotPresentInList(workedOutShiftsPage1, shift4.shiftId)
            .checkShiftIsNotPresentInList(workedOutShiftsPage1, shift5.shiftId)

            .checkShiftIsNotPresentInList(workedOutShiftsPage2, shift1.shiftId)
            .checkShiftIsNotPresentInList(workedOutShiftsPage2, shift2.shiftId)
            .checkShiftIsNotPresentInList(workedOutShiftsPage2, shift3.shiftId)
            .checkShiftIsPresentInList(workedOutShiftsPage2, shift4.shiftId)
            .checkShiftIsPresentInList(workedOutShiftsPage2, shift5.shiftId)

    }

    @Test
    @DisplayName("Search workedOut shifts (foreign employee)")
    fun searchWorkedOutShiftsForeignEmployees() {
        val profileId = commonPreconditions.createProfileDeliveryman(darkstoreId = Constants.updatedDarkstoreId).profileId

        val deliverymanShift = commonPreconditions.startAndStopShift(profileId)

        val getWorkedOutShiftsRequest = shiftsPreconditions.fillSearchWorkedOutShiftsRequests(
            darkstoreId = deliverymanShift.darkstoreId,
            userIds = setOf(profileId),
            userRoles = setOf(ShiftUserRole.DELIVERYMAN),
            timeRange = TimeRange(Instant.now().minusSeconds(120), Instant.now().plusSeconds(120))
        )

        val workedOut = shiftsActions.searchWorkedOutShifts(getWorkedOutShiftsRequest)

        shiftsAssertion
            .checkShiftIsPresentInList(workedOut, deliverymanShift.shiftId)
            .checkShiftsListCount(workedOut, 1)
    }

}