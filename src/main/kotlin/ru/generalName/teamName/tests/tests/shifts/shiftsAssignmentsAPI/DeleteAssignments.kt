package ru.samokat.mysamokat.tests.tests.shifts.shiftsAssignmentsAPI

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
import ru.samokat.shifts.api.common.domain.AssigneeRole
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneOffset

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Tag("shifts")
class DeleteAssignments {

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
    @DisplayName("Batch assignments: delete 1 assignment for deliveryman")
    fun deleteAssignmentDeliveryman() {

        val profileId = commonPreconditions.createProfileDeliveryman().profileId

        val creation = shiftsPreconditions.fillCreationBuilder(
            "1", profileId,
            commonPreconditions.getFormattedTimeRange(date, "09:00", "17:00")
        )
        val batchAssignmentsRequest = shiftsPreconditions.fillBatchAssignmentsRequest(
            assignmentsRole = ApiEnum(AssigneeRole.DELIVERYMAN), editingTimeRange = range, creations = listOf(creation),
            cancellations = null
        )

        val assignmentId =
            shiftsActions.batchAssignments(batchAssignmentsRequest).assignments[0].assignment.assignmentId


        val deletion = shiftsPreconditions.fillCancellationBuilder(
            assignmentId
        )
        val batchAssignmentsRequest2 = shiftsPreconditions.fillBatchAssignmentsRequest(
            assignmentsRole = ApiEnum(AssigneeRole.DELIVERYMAN), editingTimeRange = range, creations = null,
            updates = null,
            cancellations = listOf(deletion)
        )
        shiftsActions.batchAssignments(batchAssignmentsRequest2)

        val assignmentsFromApi = shiftsActions.getAssignmentsById(assignmentId)
        val assignmentLogDB = shiftsActions.getAssignmentLogFromDatabaseByType(assignmentId, "cancellation")!!
        val assignmentDB = shiftsActions.getAssignmentFromDatabase(assignmentId)

        val assignmentKafka = shiftsActions.getMessageFromKafkaShiftAssignmentsLogByParam(assignmentId, "status", "canceled")

        shiftsAssertion
            .checkCancelledAssignmentsInfo(profileId, assignmentsFromApi, batchAssignmentsRequest2, deletion)
            .checkCancelledAssignmentLogFromDB(profileId, assignmentLogDB, batchAssignmentsRequest2)
            .checkCancelledAssignmentFromDB(profileId, assignmentDB, batchAssignmentsRequest, deletion)
            .checkShiftAssignmentsLogMessage(assignmentKafka, batchAssignmentsRequest, creation, version = 2L, status = "canceled", cancellationReason = "mistaken_assignment")
    }

    @Test
    @DisplayName("Batch assignments: delete 1 assignment for picker")
    fun deleteAssignmentPicker() {

        val profileId = commonPreconditions.createProfilePicker().profileId

        val creation = shiftsPreconditions.fillCreationBuilder(
            "1", profileId,
            commonPreconditions.getFormattedTimeRange(date, "09:00", "17:00")
        )
        val batchAssignmentsRequest = shiftsPreconditions.fillBatchAssignmentsRequest(
            assignmentsRole = ApiEnum(AssigneeRole.PICKER), editingTimeRange = range, creations = listOf(creation),
            cancellations = null
        )

        val assignmentId =
            shiftsActions.batchAssignments(batchAssignmentsRequest).assignments[0].assignment.assignmentId


        val deletion = shiftsPreconditions.fillCancellationBuilder(
            assignmentId
        )
        val batchAssignmentsRequest2 = shiftsPreconditions.fillBatchAssignmentsRequest(
            assignmentsRole = ApiEnum(AssigneeRole.PICKER), editingTimeRange = range, creations = null,
            updates = null,
            cancellations = listOf(deletion)
        )
        shiftsActions.batchAssignments(batchAssignmentsRequest2)

        val assignmentsFromApi = shiftsActions.getAssignmentsById(assignmentId)
        val assignmentLogDB = shiftsActions.getAssignmentLogFromDatabaseByType(assignmentId, "cancellation")!!
        val assignmentDB = shiftsActions.getAssignmentFromDatabase(assignmentId)

        // todo - check kafka message

        shiftsAssertion
            .checkCancelledAssignmentsInfo(profileId, assignmentsFromApi, batchAssignmentsRequest2, deletion)
            .checkCancelledAssignmentLogFromDB(profileId, assignmentLogDB, batchAssignmentsRequest2)
            .checkCancelledAssignmentFromDB(profileId, assignmentDB, batchAssignmentsRequest, deletion)
    }

    @Test
    @DisplayName("Batch assignments: delete 2 assignment for one employee")
    fun deleteAssignment2AssignmentsFor1Employee() {

        val profileId = commonPreconditions.createProfilePicker().profileId

        val creation1 = shiftsPreconditions.fillCreationBuilder(
            "1", profileId,
            commonPreconditions.getFormattedTimeRange(date, "09:00", "11:00")
        )
        val creation2 = shiftsPreconditions.fillCreationBuilder(
            "2", profileId,
            commonPreconditions.getFormattedTimeRange(date, "13:00", "15:00")
        )
        val batchAssignmentsRequest = shiftsPreconditions.fillBatchAssignmentsRequest(
            assignmentsRole = ApiEnum(AssigneeRole.PICKER),
            editingTimeRange = range,
            creations = listOf(creation1, creation2),
            cancellations = null
        )

        val assignmentId1 =
            shiftsActions.batchAssignments(batchAssignmentsRequest).assignments[0].assignment.assignmentId
        val assignmentId2 =
            shiftsActions.batchAssignments(batchAssignmentsRequest).assignments[1].assignment.assignmentId


        val deletion1 = shiftsPreconditions.fillCancellationBuilder(
            assignmentId1
        )
        val deletion2 = shiftsPreconditions.fillCancellationBuilder(
            assignmentId2
        )
        val batchAssignmentsRequest2 = shiftsPreconditions.fillBatchAssignmentsRequest(
            assignmentsRole = ApiEnum(AssigneeRole.PICKER), editingTimeRange = range, creations = null,
            updates = null,
            cancellations = listOf(deletion1, deletion2)
        )
        shiftsActions.batchAssignments(batchAssignmentsRequest2)

        val assignmentsFromApi1 = shiftsActions.getAssignmentsById(assignmentId1)
        val assignmentLogDB1 = shiftsActions.getAssignmentLogFromDatabaseByType(assignmentId1, "cancellation")!!
        val assignmentDB1 = shiftsActions.getAssignmentFromDatabase(assignmentId1)

        val assignmentsFromApi2 = shiftsActions.getAssignmentsById(assignmentId2)
        val assignmentLogDB2 = shiftsActions.getAssignmentLogFromDatabaseByType(assignmentId2, "cancellation")!!
        val assignmentDB2 = shiftsActions.getAssignmentFromDatabase(assignmentId2)

        shiftsAssertion
            .checkCancelledAssignmentsInfo(profileId, assignmentsFromApi1, batchAssignmentsRequest2, deletion1)
            .checkCancelledAssignmentLogFromDB(profileId, assignmentLogDB1, batchAssignmentsRequest2)
            .checkCancelledAssignmentFromDB(profileId, assignmentDB1, batchAssignmentsRequest, deletion1)
            .checkCancelledAssignmentsInfo(profileId, assignmentsFromApi2, batchAssignmentsRequest2, deletion2)
            .checkCancelledAssignmentLogFromDB(profileId, assignmentLogDB2, batchAssignmentsRequest2)
            .checkCancelledAssignmentFromDB(profileId, assignmentDB2, batchAssignmentsRequest, deletion2)
    }

    @Test
    @DisplayName("Batch assignments: delete 2 assignment for two employee")
    fun deleteAssignment2AssignmentsFor2Employees() {

        val profileId1 = commonPreconditions.createProfilePicker().profileId
        val profileId2 = commonPreconditions.createProfilePicker(Constants.mobile2).profileId

        val creation1 = shiftsPreconditions.fillCreationBuilder(
            "1", profileId1,
            commonPreconditions.getFormattedTimeRange(date, "09:00", "11:00")
        )
        val creation2 = shiftsPreconditions.fillCreationBuilder(
            "2", profileId2,
            commonPreconditions.getFormattedTimeRange(date, "13:00", "15:00")
        )
        val batchAssignmentsRequest = shiftsPreconditions.fillBatchAssignmentsRequest(
            assignmentsRole = ApiEnum(AssigneeRole.PICKER),
            editingTimeRange = range,
            creations = listOf(creation1, creation2),
            cancellations = null
        )

        val assignmentId1 =
            shiftsActions.batchAssignments(batchAssignmentsRequest).assignments[0].assignment.assignmentId
        val assignmentId2 =
            shiftsActions.batchAssignments(batchAssignmentsRequest).assignments[1].assignment.assignmentId


        val deletion1 = shiftsPreconditions.fillCancellationBuilder(
            assignmentId1
        )
        val deletion2 = shiftsPreconditions.fillCancellationBuilder(
            assignmentId2
        )
        val batchAssignmentsRequest2 = shiftsPreconditions.fillBatchAssignmentsRequest(
            assignmentsRole = ApiEnum(AssigneeRole.PICKER), editingTimeRange = range, creations = null,
            updates = null,
            cancellations = listOf(deletion1, deletion2)
        )
        shiftsActions.batchAssignments(batchAssignmentsRequest2)

        val assignmentsFromApi1 = shiftsActions.getAssignmentsById(assignmentId1)
        val assignmentLogDB1 = shiftsActions.getAssignmentLogFromDatabaseByType(assignmentId1, "cancellation")!!
        val assignmentDB1 = shiftsActions.getAssignmentFromDatabase(assignmentId1)

        val assignmentsFromApi2 = shiftsActions.getAssignmentsById(assignmentId2)
        val assignmentLogDB2 = shiftsActions.getAssignmentLogFromDatabaseByType(assignmentId2, "cancellation")!!
        val assignmentDB2 = shiftsActions.getAssignmentFromDatabase(assignmentId2)

        shiftsAssertion
            .checkCancelledAssignmentsInfo(profileId1, assignmentsFromApi1, batchAssignmentsRequest2, deletion1)
            .checkCancelledAssignmentLogFromDB(profileId1, assignmentLogDB1, batchAssignmentsRequest2)
            .checkCancelledAssignmentFromDB(profileId1, assignmentDB1, batchAssignmentsRequest, deletion1)
            .checkCancelledAssignmentsInfo(profileId2, assignmentsFromApi2, batchAssignmentsRequest2, deletion2)
            .checkCancelledAssignmentLogFromDB(profileId2, assignmentLogDB2, batchAssignmentsRequest2)
            .checkCancelledAssignmentFromDB(profileId2, assignmentDB2, batchAssignmentsRequest, deletion2)
    }

    @Test
    @DisplayName("Batch assignments: create and delete assignments")
    fun createAndDeleteAssignments() {

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
            TimeRange(
                startingAt = date.atTime(LocalTime.parse("18:00")).toInstant(ZoneOffset.UTC),
                endingAt = date.atTime(LocalTime.parse("21:00")).toInstant(ZoneOffset.UTC)
            )
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

        val assignmentsFromApi1 = shiftsActions.getAssignmentsById(assignmentId1)
        val assignmentLogDB1 = shiftsActions.getAssignmentLogFromDatabaseByType(assignmentId1, "cancellation")!!
        val assignmentDB1 = shiftsActions.getAssignmentFromDatabase(assignmentId1)

        val assignmentsFromApi2 = shiftsActions.getAssignmentsById(assignmentId2)
        val assignmentLogDB2 = shiftsActions.getAssignmentLogFromDatabaseByType(assignmentId2, "assignment")!!
        val assignmentDB2 = shiftsActions.getAssignmentFromDatabase(assignmentId2)

        shiftsAssertion
            .checkCancelledAssignmentsInfo(profileId, assignmentsFromApi1, batchAssignmentsRequest2, deletion)
            .checkCancelledAssignmentLogFromDB(profileId, assignmentLogDB1, batchAssignmentsRequest2)
            .checkCancelledAssignmentFromDB(profileId, assignmentDB1, batchAssignmentsRequest, deletion)
            .checkAssignmentsInfo(assignmentsFromApi2, batchAssignmentsRequest2, creation2)
            .checkAssignmentLogFromDB(assignmentLogDB2, batchAssignmentsRequest2, creation2)
            .checkAssignmentFromDB(assignmentDB2, batchAssignmentsRequest2, creation2)
    }

    @Test
    @DisplayName("Batch assignments: create and delete assignments same time")
    fun createAndDeleteAssignmentsSameTime() {

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
            TimeRange(
                startingAt = date.atTime(LocalTime.parse("09:00")).toInstant(ZoneOffset.UTC),
                endingAt = date.atTime(LocalTime.parse("17:00")).toInstant(ZoneOffset.UTC)
            )
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

        val assignmentsFromApi1 = shiftsActions.getAssignmentsById(assignmentId1)
        val assignmentLogDB1 = shiftsActions.getAssignmentLogFromDatabaseByType(assignmentId1, "cancellation")!!
        val assignmentDB1 = shiftsActions.getAssignmentFromDatabase(assignmentId1)

        val assignmentsFromApi2 = shiftsActions.getAssignmentsById(assignmentId2)
        val assignmentLogDB2 = shiftsActions.getAssignmentLogFromDatabaseByType(assignmentId2, "assignment")!!
        val assignmentDB2 = shiftsActions.getAssignmentFromDatabase(assignmentId2)

        shiftsAssertion
            .checkCancelledAssignmentsInfo(profileId, assignmentsFromApi1, batchAssignmentsRequest2, deletion)
            .checkCancelledAssignmentLogFromDB(profileId, assignmentLogDB1, batchAssignmentsRequest2)
            .checkCancelledAssignmentFromDB(profileId, assignmentDB1, batchAssignmentsRequest, deletion)
            .checkAssignmentsInfo(assignmentsFromApi2, batchAssignmentsRequest2, creation2)
            .checkAssignmentLogFromDB(assignmentLogDB2, batchAssignmentsRequest2, creation2)
            .checkAssignmentFromDB(assignmentDB2, batchAssignmentsRequest2, creation2)
    }

    @Test
    @DisplayName("Batch assignments: create and delete assignments")
    fun createUpdateAndDeleteAssignments() {

        val profileId = commonPreconditions.createProfileDeliveryman().profileId

        val creation1 = shiftsPreconditions.fillCreationBuilder(
            "1", profileId,
            commonPreconditions.getFormattedTimeRange(date, "09:00", "15:00")
        )
        val creation2 = shiftsPreconditions.fillCreationBuilder(
            "1", profileId,
            commonPreconditions.getFormattedTimeRange(date, "16:00", "17:00")
        )
        val batchAssignmentsRequest = shiftsPreconditions.fillBatchAssignmentsRequest(
            assignmentsRole = ApiEnum(AssigneeRole.DELIVERYMAN),
            editingTimeRange = range,
            creations = listOf(creation1, creation2),
            cancellations = null
        )

        val assignmentId1 =
            shiftsActions.batchAssignments(batchAssignmentsRequest).assignments[0].assignment.assignmentId
        val assignmentId2 =
            shiftsActions.batchAssignments(batchAssignmentsRequest).assignments[1].assignment.assignmentId


        val creation = shiftsPreconditions.fillCreationBuilder(
            "1", profileId,
            TimeRange(
                startingAt = date.atTime(LocalTime.parse("18:00")).toInstant(ZoneOffset.UTC),
                endingAt = date.atTime(LocalTime.parse("21:00")).toInstant(ZoneOffset.UTC)
            )
        )
        val updation = shiftsPreconditions.fillUpdationBuilder(
            assignmentId1, TimeRange(
                startingAt = date.atTime(LocalTime.parse("11:00")).toInstant(ZoneOffset.UTC),
                endingAt = date.atTime(LocalTime.parse("15:00")).toInstant(ZoneOffset.UTC)
            )
        )
        val deletion = shiftsPreconditions.fillCancellationBuilder(
            assignmentId2
        )
        val batchAssignmentsRequest2 = shiftsPreconditions.fillBatchAssignmentsRequest(
            assignmentsRole = ApiEnum(AssigneeRole.DELIVERYMAN), editingTimeRange = range, creations = listOf(creation),
            updates = listOf(updation),
            cancellations = listOf(deletion)
        )

        val assignmentId3 =
            shiftsActions.batchAssignments(batchAssignmentsRequest2).assignments.filter { it.assignment.version == 1L }[0].assignment.assignmentId

        val assignmentsFromApi1 = shiftsActions.getAssignmentsById(assignmentId1)
        val assignmentLogDB1 = shiftsActions.getAssignmentLogFromDatabaseByType(assignmentId1, "update")!!
        val assignmentDB1 = shiftsActions.getAssignmentFromDatabase(assignmentId1)

        val assignmentsFromApi2 = shiftsActions.getAssignmentsById(assignmentId2)
        val assignmentLogDB2 = shiftsActions.getAssignmentLogFromDatabaseByType(assignmentId2, "cancellation")!!
        val assignmentDB2 = shiftsActions.getAssignmentFromDatabase(assignmentId2)

        val assignmentsFromApi3 = shiftsActions.getAssignmentsById(assignmentId3)
        val assignmentLogDB3 = shiftsActions.getAssignmentLogFromDatabaseByType(assignmentId3, "assignment")!!
        val assignmentDB3 = shiftsActions.getAssignmentFromDatabase(assignmentId3)

        shiftsAssertion
            .checkCancelledAssignmentsInfo(profileId, assignmentsFromApi2, batchAssignmentsRequest2, deletion)
            .checkCancelledAssignmentLogFromDB(profileId, assignmentLogDB2, batchAssignmentsRequest2)
            .checkCancelledAssignmentFromDB(profileId, assignmentDB2, batchAssignmentsRequest, deletion)

            .checkAssignmentsInfo(assignmentsFromApi3, batchAssignmentsRequest2, creation)
            .checkAssignmentLogFromDB(assignmentLogDB3, batchAssignmentsRequest2, creation)
            .checkAssignmentFromDB(assignmentDB3, batchAssignmentsRequest2, creation)

            .checkAssignmentsInfo(profileId, assignmentsFromApi1, batchAssignmentsRequest2, updation)
            .checkAssignmentLogFromDB(profileId, assignmentLogDB1, batchAssignmentsRequest2, updation)
            .checkAssignmentFromDB(profileId, assignmentDB1, batchAssignmentsRequest)
    }

    @Test
    @DisplayName("Batch assignments: not specify the shift in timerange")
    fun deleteAssignmentNotSpecifiedShiftInRange() {

        val profileId = commonPreconditions.createProfileDeliveryman().profileId

        val creation1 = shiftsPreconditions.fillCreationBuilder(
            "1", profileId,
            commonPreconditions.getFormattedTimeRange(date, "09:00", "17:00")
        )
        val creation2 = shiftsPreconditions.fillCreationBuilder(
            "1", profileId,
            commonPreconditions.getFormattedTimeRange(date, "18:00", "19:00")
        )
        val batchAssignmentsRequest = shiftsPreconditions.fillBatchAssignmentsRequest(
            assignmentsRole = ApiEnum(AssigneeRole.DELIVERYMAN), editingTimeRange = range, creations = listOf(creation1, creation2),
            cancellations = null
        )

        val assignmentId =
            shiftsActions.batchAssignments(batchAssignmentsRequest).assignments[0].assignment.assignmentId


        val deletion = shiftsPreconditions.fillCancellationBuilder(
            assignmentId
        )
        val batchAssignmentsRequest2 = shiftsPreconditions.fillBatchAssignmentsRequest(
            assignmentsRole = ApiEnum(AssigneeRole.DELIVERYMAN), editingTimeRange = range, creations = null,
            updates = null,
            cancellations = listOf(deletion)
        )
        val errorMessage = shiftsActions.batchAssignmentsWithError(batchAssignmentsRequest2).message

        shiftsAssertion.checkErrorMessage(errorMessage, "Concurrent modification has been detected")
    }

    @Test
    @DisplayName("Batch assignments: delete 1 assignment not in range")
    fun deleteAssignmentNotInRange() {

        val profileId = commonPreconditions.createProfileDeliveryman().profileId

        val creation = shiftsPreconditions.fillCreationBuilder(
            "1", profileId,
            commonPreconditions.getFormattedTimeRange(date, "09:00", "17:00")
        )
        val batchAssignmentsRequest = shiftsPreconditions.fillBatchAssignmentsRequest(
            assignmentsRole = ApiEnum(AssigneeRole.DELIVERYMAN), editingTimeRange = range, creations = listOf(creation),
            cancellations = null
        )

        val assignmentId =
            shiftsActions.batchAssignments(batchAssignmentsRequest).assignments[0].assignment.assignmentId


        val deletion = shiftsPreconditions.fillCancellationBuilder(
            assignmentId, 1L
        )
        val batchAssignmentsRequest2 = shiftsPreconditions.fillBatchAssignmentsRequest(
            assignmentsRole = ApiEnum(AssigneeRole.DELIVERYMAN),
            editingTimeRange = TimeRange(
                startingAt = date.plusDays(1L).atStartOfDay().toInstant(ZoneOffset.UTC),
                endingAt = date.plusDays(2L).atStartOfDay().toInstant(ZoneOffset.UTC)
            ),
            creations = null,
            updates = null,
            cancellations = listOf(deletion)
        )
        val errorMessage = shiftsActions.batchAssignmentsWithError(batchAssignmentsRequest2).message

        shiftsAssertion.checkErrorMessage(errorMessage, "Shift assignment is not included in editing time range")
    }

    @Test
    @Tags (Tag("smoke"))
    @DisplayName("Batch assignments: delete 1 assignment with wrong version")
    fun deleteAssignmentWrongVersion() {

        val profileId = commonPreconditions.createProfileDeliveryman().profileId

        val creation = shiftsPreconditions.fillCreationBuilder(
            "1", profileId,
            commonPreconditions.getFormattedTimeRange(date, "09:00", "17:00")
        )
        val batchAssignmentsRequest = shiftsPreconditions.fillBatchAssignmentsRequest(
            assignmentsRole = ApiEnum(AssigneeRole.DELIVERYMAN), editingTimeRange = range, creations = listOf(creation),
            cancellations = null
        )

        val assignmentId =
            shiftsActions.batchAssignments(batchAssignmentsRequest).assignments[0].assignment.assignmentId


        val deletion = shiftsPreconditions.fillCancellationBuilder(
            assignmentId, 10L
        )
        val batchAssignmentsRequest2 = shiftsPreconditions.fillBatchAssignmentsRequest(
            assignmentsRole = ApiEnum(AssigneeRole.DELIVERYMAN), editingTimeRange = range, creations = null,
            updates = null,
            cancellations = listOf(deletion)
        )
        val errorMessage = shiftsActions.batchAssignmentsWithError(batchAssignmentsRequest2).message

        shiftsAssertion.checkErrorMessage(errorMessage, "Shift assignment was modified since last request")
    }

    @Test
    @Tag("empro_integration")
    @DisplayName("Delete Assignments when user is blocked")
    fun deleteAssignmentsWhenUserIsBlocked(){
        val profileId = commonPreconditions.createProfileDeliveryman().profileId

        val creation = shiftsPreconditions.fillCreationBuilder(
            "1", profileId,
            commonPreconditions.getFormattedTimeRange(date, "09:00", "17:00")
        )
        val batchAssignmentsRequest = shiftsPreconditions.fillBatchAssignmentsRequest(
            assignmentsRole = ApiEnum(AssigneeRole.DELIVERYMAN), editingTimeRange = range, creations = listOf(creation),
            cancellations = null
        )

        val assignmentId =
            shiftsActions.batchAssignments(batchAssignmentsRequest).assignments[0].assignment.assignmentId

        employeeActions.deleteProfile(profileId)

        val assignmentDB = shiftsActions.getAssignmentFromDatabase(assignmentId)

        shiftsAssertion
            .checkAutoCancelledAssignments(assignmentDB)
    }

    @Test
    @Tag("empro_integration")
    @DisplayName("Delete Assignments when user role change")
    fun deleteAssifnmentsWhenRoleChanged(){
        val profileId = commonPreconditions.createProfileDeliveryman().profileId

        employeePreconditions.setUpdateProfileRequest(
            employeePreconditions
                .fillCreateProfileRequest(
                    roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                    vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
                    email = null,
                    staffPartnerId = Constants.defaultStaffPartnerId,
                    darkstoreId = Constants.darkstoreId,
                    mobile = Constants.mobile1
                ),
            roles = listOf(ApiEnum(EmployeeRole.PICKER)),
            vehicle = null
        )
        val creation = shiftsPreconditions.fillCreationBuilder(
            "1", profileId,
            commonPreconditions.getFormattedTimeRange(date, "09:00", "17:00")
        )
        val batchAssignmentsRequest = shiftsPreconditions.fillBatchAssignmentsRequest(
            assignmentsRole = ApiEnum(AssigneeRole.DELIVERYMAN), editingTimeRange = range, creations = listOf(creation),
            cancellations = null
        )

        val assignmentId =
            shiftsActions.batchAssignments(batchAssignmentsRequest).assignments[0].assignment.assignmentId

        employeeActions.updateProfile(
            profileId,
            employeePreconditions.updateProfileRequest()
        )

        val assignmentDB = shiftsActions.getAssignmentFromDatabase(assignmentId)

        shiftsAssertion
            .checkAutoCancelledAssignments(assignmentDB, cancellationReason = "assignee_roles_loss")
    }

    @Test
    @Tag("empro_integration")
    @DisplayName("Assignments with actual role not deleted when user role change")
    fun deleteAssignmentsWhenRoleChangedActualRole(){
        val profileId = commonPreconditions.createProfileDeliveryman().profileId

        employeePreconditions.setUpdateProfileRequest(
            employeePreconditions
                .fillCreateProfileRequest(
                    roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                    vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
                    email = null,
                    staffPartnerId = Constants.defaultStaffPartnerId,
                    darkstoreId = Constants.darkstoreId,
                    mobile = Constants.mobile1
                ),
            roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN), ApiEnum(EmployeeRole.PICKER))
        )
        val creation = shiftsPreconditions.fillCreationBuilder(
            "1", profileId,
            commonPreconditions.getFormattedTimeRange(date, "09:00", "17:00")
        )
        val batchAssignmentsRequest = shiftsPreconditions.fillBatchAssignmentsRequest(
            assignmentsRole = ApiEnum(AssigneeRole.DELIVERYMAN), editingTimeRange = range, creations = listOf(creation),
            cancellations = null
        )

        val assignmentId =
            shiftsActions.batchAssignments(batchAssignmentsRequest).assignments[0].assignment.assignmentId

        employeeActions.updateProfile(
            profileId,
            employeePreconditions.updateProfileRequest()
        )

        val assignmentDB = shiftsActions.getAssignmentFromDatabase(assignmentId)

        shiftsAssertion
            .checkAutoCancelledAssignments(assignmentDB, cancellationReason = null, status = "assigned")
    }
}