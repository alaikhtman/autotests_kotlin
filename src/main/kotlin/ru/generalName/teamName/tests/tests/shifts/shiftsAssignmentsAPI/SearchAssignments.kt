package ru.samokat.mysamokat.tests.tests.shifts.shiftsAssignmentsAPI

import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.samokat.employeeprofiles.api.common.domain.Vehicle
import ru.samokat.my.domain.employee.EmployeeRole
import ru.samokat.employeeprofiles.api.common.domain.EmployeeVehicleType
import ru.samokat.my.domain.shifts.ShiftAssignmentCancellationReason
import ru.samokat.my.domain.shifts.TimeRange
import ru.samokat.my.rest.api.enum.ApiEnum
import ru.samokat.mysamokat.tests.checkers.ShiftAssertion
import ru.samokat.mysamokat.tests.dataproviders.Constants
import ru.samokat.mysamokat.tests.dataproviders.preconditions.CommonPreconditions
import ru.samokat.mysamokat.tests.dataproviders.preconditions.EmployeePreconditions
import ru.samokat.mysamokat.tests.dataproviders.preconditions.ShiftsPreconditions
import ru.samokat.mysamokat.tests.helpers.actions.EmployeeActions
import ru.samokat.mysamokat.tests.helpers.actions.ShiftsActions
import ru.samokat.shifts.api.assignments.ShiftAssignmentStatus
import ru.samokat.shifts.api.assignments.search.SearchShiftAssignmentsRequest
import ru.samokat.shifts.api.common.domain.AssigneeRole
import ru.samokat.shifts.api.common.domain.ShiftUserRole
import java.time.LocalDate

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Tag("shifts")
class SearchAssignments {

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
        this.date = commonPreconditions.getTomorrowsDate()
        this.range = commonPreconditions.getTomorrowsFullDayRange()
        shiftsPreconditions = ShiftsPreconditions()
        employeePreconditions = EmployeePreconditions()
        shiftsAssertion = ShiftAssertion()
        employeeActions.deleteProfile(Constants.mobile1)
        employeeActions.deleteProfile(Constants.mobile2)
        commonPreconditions.clearAssignmentsFromDatabase(range)
    }

    @AfterEach
    fun release() {
        shiftsAssertion.assertAll()
        employeeActions.deleteProfile(Constants.mobile1)
        employeeActions.deleteProfile(Constants.mobile2)
        commonPreconditions.clearAssignmentsFromDatabase(range)
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
    @Tags (Tag("smoke"))
    @DisplayName("Search assignments: for 1 deliveryman")
    fun searchAssignmentsForOneDeliveryman() {

        val profileId = commonPreconditions.createProfileDeliveryman().profileId

        val shiftRange = commonPreconditions.getFormattedTimeRange(date, "09:00", "17:00")
        val assignmentId = commonPreconditions.createAssignment(
            profileId, range, shiftRange
        )

        val searchAssignmentsRequest = shiftsPreconditions.fillSearchAssignmentsRequest(
            timeRange = range, statuses = setOf(ShiftAssignmentStatus.ASSIGNED)
        )
        val assignments = shiftsActions.searchAssignments(searchAssignmentsRequest).assignments

        shiftsAssertion
            .checkAssignmentPresentInList(assignments, assignmentId)
            .checkAssignmentFieldsInList(assignments, assignmentId, shiftRange, profileId)
    }

    @Test
    @DisplayName("Search assignments: for 1 picker")
    fun searchAssignmentsForOnePicker() {

        val profileId = commonPreconditions.createProfilePicker().profileId

        val shiftRange = commonPreconditions.getFormattedTimeRange(date, "09:00", "17:00")
        val assignmentId = commonPreconditions.createAssignment(
            profileId, range, shiftRange, role = AssigneeRole.PICKER
        )

        val searchAssignmentsRequest = shiftsPreconditions.fillSearchAssignmentsRequest(
            timeRange = range, statuses = setOf(ShiftAssignmentStatus.ASSIGNED),
            assigneeRoles = setOf(AssigneeRole.PICKER)
        )
        val assignments = shiftsActions.searchAssignments(searchAssignmentsRequest).assignments

        shiftsAssertion
            .checkAssignmentPresentInList(assignments, assignmentId)
            .checkAssignmentFieldsInList(
                assignments,
                assignmentId,
                shiftRange,
                profileId,
                userRole = ShiftUserRole.PICKER
            )
    }

    @Test
    @DisplayName("Search assignments: for 1 employee by two roles")
    fun searchAssignmentsForOneEmployeeByTwoRoles() {

        val profileId = commonPreconditions.createProfileDeliverymanPicker().profileId

        val shiftRange1 = commonPreconditions.getFormattedTimeRange(date, "09:00", "15:00")
        val shiftRange2 = commonPreconditions.getFormattedTimeRange(date, "16:00", "17:00")

        val assignmentId1 = commonPreconditions.createAssignment(
            profileId, range, shiftRange1
        )
        val assignmentId2 = commonPreconditions.createAssignment(
            profileId, range, shiftRange2, role = AssigneeRole.PICKER
        )

        val searchAssignmentsRequest = shiftsPreconditions.fillSearchAssignmentsRequest(
            timeRange = range, statuses = setOf(ShiftAssignmentStatus.ASSIGNED),
            assigneeRoles = setOf(AssigneeRole.DELIVERYMAN, AssigneeRole.PICKER)
        )
        val assignments = shiftsActions.searchAssignments(searchAssignmentsRequest).assignments

        shiftsAssertion
            .checkAssignmentPresentInList(assignments, assignmentId1)
            .checkAssignmentPresentInList(assignments, assignmentId2)
    }

    @Test
    @Tags (Tag("smoke"))
    @DisplayName("Search assignments: for several employees")
    fun searchAssignmentsForSeveralEmployess() {

        val profileId1 = commonPreconditions.createProfilePicker().profileId
        val profileId2 = commonPreconditions.createProfileDeliveryman(Constants.mobile2).profileId

        val shiftRange1 = commonPreconditions.getFormattedTimeRange(date, "09:00", "15:00")
        val shiftRange2 = commonPreconditions.getFormattedTimeRange(date, "16:00", "17:00")

        val assignmentId1 = commonPreconditions.createAssignment(
            profileId1, range, shiftRange1
        )
        val assignmentId2 = commonPreconditions.createAssignment(
            profileId2, range, shiftRange2, role = AssigneeRole.PICKER
        )

        val searchAssignmentsRequest = shiftsPreconditions.fillSearchAssignmentsRequest(
            timeRange = range, statuses = setOf(ShiftAssignmentStatus.ASSIGNED),
            assigneeRoles = setOf(AssigneeRole.DELIVERYMAN, AssigneeRole.PICKER)
        )
        val assignments = shiftsActions.searchAssignments(searchAssignmentsRequest).assignments

        shiftsAssertion
            .checkAssignmentPresentInList(assignments, assignmentId1)
            .checkAssignmentPresentInList(assignments, assignmentId2)
    }

    @Test
    @Tags (Tag("smoke"))
    @DisplayName("Search assignments: cancelled assignments")
    fun searchCancelledAssignments() {

        val profileId = commonPreconditions.createProfileDeliveryman().profileId

        val shiftRange = commonPreconditions.getFormattedTimeRange(date, "09:00", "17:00")
        val assignmentId = commonPreconditions.createAssignment(
            profileId, range, shiftRange
        )

        commonPreconditions.cancelAssignment(assignmentId, range)

        val searchAssignmentsRequest = shiftsPreconditions.fillSearchAssignmentsRequest(
            timeRange = range, statuses = setOf(ShiftAssignmentStatus.CANCELED)
        )
        val assignments = shiftsActions.searchAssignments(searchAssignmentsRequest).assignments

        shiftsAssertion
            .checkAssignmentPresentInList(assignments, assignmentId)
            .checkAssignmentFieldsInList(
                assignments,
                assignmentId,
                shiftRange,
                profileId,
                status = "canceled",
                version = 2L,
                cancellationReason = ShiftAssignmentCancellationReason.MISTAKEN_ASSIGNMENT
            )
    }

    @Test
    @Tags (Tag("smoke"))
    @DisplayName("Search assignments: different statuses")
    fun searchAssignmentsDifferentStatuses() {

        val profileId = commonPreconditions.createProfileDeliveryman().profileId

        val creation = shiftsPreconditions.fillCreationBuilder(
            "1", profileId,
            commonPreconditions.getFormattedTimeRange(date, "09:00", "17:00")
        )
        val batchAssignmentsRequest = shiftsPreconditions.fillBatchAssignmentsRequest(
            assignmentsRole = ApiEnum(AssigneeRole.DELIVERYMAN), editingTimeRange = range, creations = listOf(creation),
            cancellations = null
        )

        val assignmentId1 =
            shiftsActions.batchAssignments(batchAssignmentsRequest).assignments[0].assignment.assignmentId


        val creation2 = shiftsPreconditions.fillCreationBuilder(
            "1", profileId,
            commonPreconditions.getFormattedTimeRange(date, "18:00", "21:00")
        )
        val deletion = shiftsPreconditions.fillCancellationBuilder(
            assignmentId1
        )
        val batchAssignmentsRequest2 = shiftsPreconditions.fillBatchAssignmentsRequest(
            assignmentsRole = ApiEnum(AssigneeRole.DELIVERYMAN),
            editingTimeRange = range,
            creations = listOf(creation2),
            updates = null,
            cancellations = listOf(deletion)
        )

        val assignmentId2 =
            shiftsActions.batchAssignments(batchAssignmentsRequest2).assignments.filter { it.assignment.assignmentId != assignmentId1 }[0].assignment.assignmentId


        val searchAssignmentsRequest = shiftsPreconditions.fillSearchAssignmentsRequest(
            timeRange = range, statuses = setOf(ShiftAssignmentStatus.CANCELED, ShiftAssignmentStatus.ASSIGNED)
        )
        val assignments = shiftsActions.searchAssignments(searchAssignmentsRequest).assignments

        shiftsAssertion
            .checkAssignmentPresentInList(assignments, assignmentId1)
            .checkAssignmentPresentInList(assignments, assignmentId2)
    }

    @Test
    @DisplayName("Search assignments: ending after range")
    fun searchAssignmentsEndAfterRange() {

        val profileId = commonPreconditions.createProfileDeliveryman().profileId

        val searchRange = commonPreconditions.getFormattedTimeRange(date, "09:00", "17:00")
        val shiftRange = commonPreconditions.getFormattedTimeRange(date, "11:00", "21:00")
        val assignmentId = commonPreconditions.createAssignment(
            profileId, range, shiftRange
        )

        val searchAssignmentsRequest = shiftsPreconditions.fillSearchAssignmentsRequest(
            timeRange = searchRange, statuses = setOf(ShiftAssignmentStatus.ASSIGNED)
        )
        val assignments = shiftsActions.searchAssignments(searchAssignmentsRequest).assignments

        shiftsAssertion
            .checkAssignmentPresentInList(assignments, assignmentId)
            .checkAssignmentFieldsInList(assignments, assignmentId, shiftRange, profileId)
    }

    @Test
    @DisplayName("Search assignments: full range")
    fun searchAssignmentsFullRange() {

        val profileId = commonPreconditions.createProfileDeliveryman().profileId

        val searchRange = commonPreconditions.getFormattedTimeRange(date, "09:00", "17:00")
        val shiftRange = commonPreconditions.getFormattedTimeRange(date, "11:00", "21:00")
        val assignmentId = commonPreconditions.createAssignment(
            profileId, range, shiftRange
        )

        val searchAssignmentsRequest = shiftsPreconditions.fillSearchAssignmentsRequest(
            timeRange = searchRange, statuses = setOf(ShiftAssignmentStatus.ASSIGNED)
        )
        val assignments = shiftsActions.searchAssignments(searchAssignmentsRequest).assignments

        shiftsAssertion
            .checkAssignmentPresentInList(assignments, assignmentId)
            .checkAssignmentFieldsInList(assignments, assignmentId, shiftRange, profileId)
    }

    @Test
    @DisplayName("Search assignments: started before range")
    fun searchAssignmentsStartedBeforeRange() {

        val profileId = commonPreconditions.createProfileDeliveryman().profileId

        val searchRange = commonPreconditions.getFormattedTimeRange(date, "09:00", "17:00")
        val shiftRange = commonPreconditions.getFormattedTimeRange(date, "08:00", "13:00")
        val assignmentId = commonPreconditions.createAssignment(
            profileId, range, shiftRange
        )

        val searchAssignmentsRequest = shiftsPreconditions.fillSearchAssignmentsRequest(
            timeRange = searchRange, statuses = setOf(ShiftAssignmentStatus.ASSIGNED)
        )
        val assignments = shiftsActions.searchAssignments(searchAssignmentsRequest).assignments

        shiftsAssertion
            .checkAssignmentNotPresentInList(assignments, assignmentId)
    }

    @Test
    @DisplayName("Search assignments: out of range")
    fun searchAssignmentsOutOfRange() {

        val profileId = commonPreconditions.createProfileDeliveryman().profileId

        val searchRange = commonPreconditions.getFormattedTimeRange(date, "09:00", "17:00")
        val shiftRange = commonPreconditions.getFormattedTimeRange(date, "18:00", "19:00")
        val assignmentId = commonPreconditions.createAssignment(
            profileId, range, shiftRange
        )

        val searchAssignmentsRequest = shiftsPreconditions.fillSearchAssignmentsRequest(
            timeRange = searchRange, statuses = setOf(ShiftAssignmentStatus.ASSIGNED)
        )
        val assignments = shiftsActions.searchAssignments(searchAssignmentsRequest).assignments

        shiftsAssertion
            .checkAssignmentNotPresentInList(assignments, assignmentId)
    }

    @Test
    @DisplayName("Search assignments: pagination")
    fun searchAssignmentsPagination() {

        val profileId1 = commonPreconditions.createProfileDeliveryman().profileId
        val profileId2 = commonPreconditions.createProfileDeliveryman(Constants.mobile2).profileId

        val creation1 = shiftsPreconditions.fillCreationBuilder(
            "1", profileId1,
            commonPreconditions.getFormattedTimeRange(date, "08:00", "09:00")
        )

        val creation2 = shiftsPreconditions.fillCreationBuilder(
            "1", profileId1,
            commonPreconditions.getFormattedTimeRange(date, "12:00", "13:00")
        )

        val creation3 = shiftsPreconditions.fillCreationBuilder(
            "1", profileId1,
            commonPreconditions.getFormattedTimeRange(date, "15:00", "18:00")
        )

        val creation4 = shiftsPreconditions.fillCreationBuilder(
            "1", profileId2,
            commonPreconditions.getFormattedTimeRange(date, "10:00", "11:00")
        )

        val creation5 = shiftsPreconditions.fillCreationBuilder(
            "1", profileId2,
            commonPreconditions.getFormattedTimeRange(date, "12:00", "13:00")
        )

        val creation6 = shiftsPreconditions.fillCreationBuilder(
            "1", profileId2,
            commonPreconditions.getFormattedTimeRange(date, "16:00", "18:00")
        )

        val batchAssignmentsRequest = shiftsPreconditions.fillBatchAssignmentsRequest(
            assignmentsRole = ApiEnum(AssigneeRole.DELIVERYMAN),
            editingTimeRange = range,
            creations = listOf(creation1, creation2, creation3, creation4, creation5, creation6),
            cancellations = null
        )

        val assignments =
            shiftsActions.batchAssignments(batchAssignmentsRequest)

        val assignmentId1 = assignments.assignments.filter { (it.assignment.userId == profileId1) && (it.assignment.timeRange == creation1.timeRange)}[0].assignment.assignmentId
        val assignmentId2 = assignments.assignments.filter { (it.assignment.userId == profileId1) && (it.assignment.timeRange == creation2.timeRange)}[0].assignment.assignmentId
        val assignmentId3 = assignments.assignments.filter { (it.assignment.userId == profileId1) && (it.assignment.timeRange == creation3.timeRange)}[0].assignment.assignmentId
        val assignmentId4 = assignments.assignments.filter { (it.assignment.userId == profileId2) && (it.assignment.timeRange == creation4.timeRange)}[0].assignment.assignmentId
        val assignmentId5 = assignments.assignments.filter { (it.assignment.userId == profileId2) && (it.assignment.timeRange == creation5.timeRange)}[0].assignment.assignmentId
        val assignmentId6 = assignments.assignments.filter { (it.assignment.userId == profileId2) && (it.assignment.timeRange == creation6.timeRange)}[0].assignment.assignmentId


        val searchAssignmentsRequestPage1 = shiftsPreconditions.fillSearchAssignmentsRequest(
            timeRange = range, statuses = setOf(ShiftAssignmentStatus.ASSIGNED),
            assigneeRoles = setOf(AssigneeRole.DELIVERYMAN, AssigneeRole.PICKER),
            paging = SearchShiftAssignmentsRequest.PagingData(2, null)
        )
        val searchResultsPage1 = shiftsActions.searchAssignments(searchAssignmentsRequestPage1)

        val searchAssignmentsRequestPage2 = shiftsPreconditions.fillSearchAssignmentsRequest(
            timeRange = range, statuses = setOf(ShiftAssignmentStatus.ASSIGNED),
            assigneeRoles = setOf(AssigneeRole.DELIVERYMAN, AssigneeRole.PICKER),
            paging = SearchShiftAssignmentsRequest.PagingData(2, searchResultsPage1.paging.nextPageMark)
        )
        val searchResultsPage2 = shiftsActions.searchAssignments(searchAssignmentsRequestPage2)

        val searchAssignmentsRequestPage3 = shiftsPreconditions.fillSearchAssignmentsRequest(
            timeRange = range, statuses = setOf(ShiftAssignmentStatus.ASSIGNED),
            assigneeRoles = setOf(AssigneeRole.DELIVERYMAN, AssigneeRole.PICKER),
            paging = SearchShiftAssignmentsRequest.PagingData(2, searchResultsPage2.paging.nextPageMark)
        )
        val searchResultsPage3 = shiftsActions.searchAssignments(searchAssignmentsRequestPage3)


        shiftsAssertion
            .checkAssignmentListCount(searchResultsPage1.assignments, 2)
            .checkAssignmentListCount(searchResultsPage2.assignments, 2)
            .checkAssignmentListCount(searchResultsPage3.assignments, 2)

            .checkAssignmentPresentInList(searchResultsPage1.assignments, assignmentId1)
            .checkAssignmentPresentInList(searchResultsPage1.assignments, assignmentId4)
            .checkAssignmentPresentInList(searchResultsPage2.assignments, assignmentId2)
            .checkAssignmentPresentInList(searchResultsPage2.assignments, assignmentId5)
            .checkAssignmentPresentInList(searchResultsPage3.assignments, assignmentId3)
            .checkAssignmentPresentInList(searchResultsPage3.assignments, assignmentId6)




    }

    @Test
    @DisplayName("Search assignments: empty results (darkstore)")
    fun searchAssignmentsEmptyResultsDarkstore() {

        val profileId = commonPreconditions.createProfileDeliveryman().profileId

        val shiftRange = commonPreconditions.getFormattedTimeRange(date, "09:00", "17:00")
        val assignmentId = commonPreconditions.createAssignment(
            profileId, range, shiftRange, darkstoreId = Constants.updatedDarkstoreId
        )

        val searchAssignmentsRequest = shiftsPreconditions.fillSearchAssignmentsRequest(
            timeRange = range, statuses = setOf(ShiftAssignmentStatus.ASSIGNED)
        )
        val assignments = shiftsActions.searchAssignments(searchAssignmentsRequest).assignments

        shiftsAssertion
            .checkAssignmentNotPresentInList(assignments, assignmentId)
            .checkAssignmentListCount(assignments, 0)
    }

    @Test
    @DisplayName("Search assignments: empty results (role)")
    fun searchAssignmentsEmptyResultsRole() {

        val profileId = commonPreconditions.createProfileDeliveryman().profileId

        val shiftRange = commonPreconditions.getFormattedTimeRange(date, "09:00", "17:00")
        val assignmentId = commonPreconditions.createAssignment(
            profileId, range, shiftRange, role = AssigneeRole.DELIVERYMAN
        )

        val searchAssignmentsRequest = shiftsPreconditions.fillSearchAssignmentsRequest(
            timeRange = range, statuses = setOf(ShiftAssignmentStatus.ASSIGNED), assigneeRoles = setOf(AssigneeRole.PICKER)
        )
        val assignments = shiftsActions.searchAssignments(searchAssignmentsRequest).assignments

        shiftsAssertion
            .checkAssignmentNotPresentInList(assignments, assignmentId)
            .checkAssignmentListCount(assignments, 0)
    }

    @Test
    @DisplayName("Search assignments: empty results (status)")
    fun searchAssignmentsEmptyResultsStatus() {

        val profileId = commonPreconditions.createProfileDeliveryman().profileId

        val shiftRange = commonPreconditions.getFormattedTimeRange(date, "09:00", "17:00")
        val assignmentId = commonPreconditions.createAssignment(
            profileId, range, shiftRange
        )

        val searchAssignmentsRequest = shiftsPreconditions.fillSearchAssignmentsRequest(
            timeRange = range, statuses = setOf(ShiftAssignmentStatus.CANCELED)
        )
        val assignments = shiftsActions.searchAssignments(searchAssignmentsRequest).assignments

        shiftsAssertion
            .checkAssignmentNotPresentInList(assignments, assignmentId)
            .checkAssignmentListCount(assignments, 0)
    }
}





