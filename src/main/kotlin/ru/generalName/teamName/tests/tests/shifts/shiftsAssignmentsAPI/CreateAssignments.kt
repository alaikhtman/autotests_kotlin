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

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Tag("shifts")
class CreateAssignments {

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
    @Tags (Tag("smoke"), Tag("kafka_consume"))
    @DisplayName("Batch assignments: create 1 assignment for deliveryman")
    fun createAssignmentDeliveryman() {

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

        val assignmentsFromApi = shiftsActions.getAssignmentsById(assignmentId)
        val assignmentLogDB = shiftsActions.getAssignmentLogFromDatabaseByType(assignmentId, "assignment")!!
        val assignmentDB = shiftsActions.getAssignmentFromDatabase(assignmentId)
        val assignmentKafka = shiftsActions.getMessageFromKafkaShiftAssignmentsLog(assignmentId)

        shiftsAssertion
            .checkAssignmentsInfo(assignmentsFromApi, batchAssignmentsRequest, creation)
            .checkAssignmentLogFromDB(assignmentLogDB, batchAssignmentsRequest, creation)
            .checkAssignmentFromDB(assignmentDB, batchAssignmentsRequest, creation)
            .checkShiftAssignmentsLogMessage(assignmentKafka, batchAssignmentsRequest, creation)
    }

    @Test
    @Tag("kafka_consume")
    @DisplayName("Batch assignments: create 1 assignment for picker")
    fun createAssignmentPicker() {

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

        val assignmentsFromApi = shiftsActions.getAssignmentsById(assignmentId)
        val assignmentLogDB = shiftsActions.getAssignmentLogFromDatabaseByType(assignmentId, "assignment")!!
        val assignmentDB = shiftsActions.getAssignmentFromDatabase(assignmentId)
        val assignmentKafka = shiftsActions.getMessageFromKafkaShiftAssignmentsLog(assignmentId)

        shiftsAssertion
            .checkAssignmentsInfo(assignmentsFromApi, batchAssignmentsRequest, creation)
            .checkAssignmentLogFromDB(assignmentLogDB, batchAssignmentsRequest, creation)
            .checkAssignmentFromDB(assignmentDB, batchAssignmentsRequest, creation)
            .checkShiftAssignmentsLogMessage(assignmentKafka, batchAssignmentsRequest, creation)
    }

    @Test
    @Tags (Tag("smoke"), Tag("kafka_consume"))
    @DisplayName("Create an assignment: create 1 assignment (the shift coincides with the time-range boundaries)")
    fun createAssignmentShiftCoincidesBoundaries() {

        val profileId = commonPreconditions.createProfileDeliveryman().profileId

        val creation = shiftsPreconditions.fillCreationBuilder(
            "1", profileId,
            TimeRange(
                startingAt = range.startingAt,
                endingAt = range.endingAt
            )
        )
        val batchAssignmentsRequest = shiftsPreconditions.fillBatchAssignmentsRequest(
            assignmentsRole = ApiEnum(AssigneeRole.DELIVERYMAN), editingTimeRange = range, creations = listOf(creation),
            cancellations = null
        )

        val assignmentId =
            shiftsActions.batchAssignments(batchAssignmentsRequest).assignments[0].assignment.assignmentId

        val assignmentsFromApi = shiftsActions.getAssignmentsById(assignmentId)
        val assignmentLogDB = shiftsActions.getAssignmentLogFromDatabaseByType(assignmentId, "assignment")!!
        val assignmentDB = shiftsActions.getAssignmentFromDatabase(assignmentId)
        val assignmentKafka = shiftsActions.getMessageFromKafkaShiftAssignmentsLog(assignmentId)

        shiftsAssertion
            .checkAssignmentsInfo(assignmentsFromApi, batchAssignmentsRequest, creation)
            .checkAssignmentLogFromDB(assignmentLogDB, batchAssignmentsRequest, creation)
            .checkAssignmentFromDB(assignmentDB, batchAssignmentsRequest, creation)
            .checkShiftAssignmentsLogMessage(assignmentKafka, batchAssignmentsRequest, creation)
    }

    @Test
    @Tags (Tag("smoke"))
    @DisplayName("Create an assignment: create 1 assignment (for the duration of the cancelled shift of the same employee)")
    fun createAssignmentShiftForCancelledShiftTimeSlot() {

        val profileId = commonPreconditions.createProfileDeliveryman().profileId

        val creation = shiftsPreconditions.fillCreationBuilder(
            "1", profileId,
            commonPreconditions.getFormattedTimeRange(date, "09:00", "17:00")
        )
        val batchAssignmentsRequest1 = shiftsPreconditions.fillBatchAssignmentsRequest(
            assignmentsRole = ApiEnum(AssigneeRole.PICKER), editingTimeRange = range, creations = listOf(creation),
            cancellations = null
        )

        val assignmentId1 =
            shiftsActions.batchAssignments(batchAssignmentsRequest1).assignments[0].assignment.assignmentId


        val cancellation = shiftsPreconditions.fillCancellationBuilder(assignmentId1, 1)

        val batchAssignmentsRequest2 = shiftsPreconditions.fillBatchAssignmentsRequest(
            assignmentsRole = ApiEnum(AssigneeRole.PICKER), editingTimeRange = range, creations = listOf(creation),
            cancellations = listOf(cancellation)
        )

        val assignmentId2 =
            shiftsActions.batchAssignments(batchAssignmentsRequest2).assignments[0].assignment.assignmentId

        val assignmentsFromApi = shiftsActions.getAssignmentsById(assignmentId2)

        shiftsAssertion
            .checkAssignmentsInfo(assignmentsFromApi, batchAssignmentsRequest2, creation)

    }

    @Test
    @Tags (Tag("smoke"))
    @DisplayName("Create an assignment: create 1 assignment (for the duration of the cancelled shift of another employee)")
    fun createAssignmentShiftForRemoveShiftTimeSlotAnotherEmployee() {

        val profileId1 = commonPreconditions.createProfileDeliveryman().profileId

        val profileId2 = commonPreconditions.createProfileDeliveryman(mobile = Constants.mobile2).profileId

        val creation1 = shiftsPreconditions.fillCreationBuilder(
            "1", profileId1,
            commonPreconditions.getFormattedTimeRange(date, "09:00", "17:00")
        )
        val creation2 = shiftsPreconditions.fillCreationBuilder(
            "1", profileId2,
            commonPreconditions.getFormattedTimeRange(date, "09:00", "17:00")
        )
        val batchAssignmentsRequest1 = shiftsPreconditions.fillBatchAssignmentsRequest(
            assignmentsRole = ApiEnum(AssigneeRole.DELIVERYMAN),
            editingTimeRange = range,
            creations = listOf(creation1),
            cancellations = null
        )

        val assignmentId1 =
            shiftsActions.batchAssignments(batchAssignmentsRequest1).assignments[0].assignment.assignmentId


        val cancellation = shiftsPreconditions.fillCancellationBuilder(assignmentId1, 1)

        val batchAssignmentsRequest2 = shiftsPreconditions.fillBatchAssignmentsRequest(
            assignmentsRole = ApiEnum(AssigneeRole.DELIVERYMAN),
            editingTimeRange = range,
            creations = listOf(creation2),
            cancellations = listOf(cancellation)
        )

        val assignmentId2 =
            shiftsActions.batchAssignments(batchAssignmentsRequest2).assignments[0].assignment.assignmentId

        val assignmentsFromApi = shiftsActions.getAssignmentsById(assignmentId2)

        shiftsAssertion
            .checkAssignmentsInfo(assignmentsFromApi, batchAssignmentsRequest2, creation2)

    }

    @Test
    @DisplayName("Create an assignment: create 2 assignments for 1 employee")
    fun createTwoAssignmentsForOneEmployee() {
        val profileId = commonPreconditions.createProfileDeliveryman().profileId

        val creation1 = shiftsPreconditions.fillCreationBuilder(
            "1", profileId,
            commonPreconditions.getFormattedTimeRange(date, "09:00", "15:00")
        )
        val creation2 = shiftsPreconditions.fillCreationBuilder(
            "2", profileId,
            commonPreconditions.getFormattedTimeRange(date, "16:00", "17:00")
        )
        val batchAssignmentsRequest = shiftsPreconditions.fillBatchAssignmentsRequest(
            assignmentsRole = ApiEnum(AssigneeRole.DELIVERYMAN),
            editingTimeRange = range,
            creations = listOf(creation1, creation2),
            cancellations = null
        )

        val assignments =
            shiftsActions.batchAssignments(batchAssignmentsRequest)

        val assignmentsFromApi1 = shiftsActions.getAssignmentsById(assignments.assignments[0].assignment.assignmentId)
        val assignmentsFromApi2 = shiftsActions.getAssignmentsById(assignments.assignments[1].assignment.assignmentId)

        shiftsAssertion
            .checkAssignmentsInfo(assignmentsFromApi1, batchAssignmentsRequest, creation1)
            .checkAssignmentsInfo(assignmentsFromApi2, batchAssignmentsRequest, creation2)
    }

    @Test
    @Tags (Tag("smoke"))
    @DisplayName("Create an assignment: create 2 assignments for 1 employee (bordering)")
    fun createTwoAssignmentsForOneEmployeeBordering() {
        val profileId = commonPreconditions.createProfileDeliveryman().profileId

        val creation1 = shiftsPreconditions.fillCreationBuilder(
            "1", profileId,
            commonPreconditions.getFormattedTimeRange(date, "09:00", "15:00")
        )
        val creation2 = shiftsPreconditions.fillCreationBuilder(
            "2", profileId,
            commonPreconditions.getFormattedTimeRange(date, "15:01", "17:00")
        )
        val batchAssignmentsRequest = shiftsPreconditions.fillBatchAssignmentsRequest(
            assignmentsRole = ApiEnum(AssigneeRole.DELIVERYMAN),
            editingTimeRange = range,
            creations = listOf(creation1, creation2),
            cancellations = null
        )

        val assignments =
            shiftsActions.batchAssignments(batchAssignmentsRequest)

        val assignmentsFromApi1 = shiftsActions.getAssignmentsById(assignments.assignments[0].assignment.assignmentId)
        val assignmentsFromApi2 = shiftsActions.getAssignmentsById(assignments.assignments[1].assignment.assignmentId)

        shiftsAssertion
            .checkAssignmentsInfo(assignmentsFromApi1, batchAssignmentsRequest, creation1)
            .checkAssignmentsInfo(assignmentsFromApi2, batchAssignmentsRequest, creation2)
    }

    @Test
    @DisplayName("Create an assignment: create 2 assignments for 2 employees (not bordering)")
    fun createTwoAssignmentsForTwoEmployees() {
        val profileId1 = commonPreconditions.createProfileDeliveryman().profileId
        val profileId2 = commonPreconditions.createProfileDeliveryman(mobile = Constants.mobile2).profileId

        val creation1 = shiftsPreconditions.fillCreationBuilder(
            "1", profileId1,
            commonPreconditions.getFormattedTimeRange(date, "09:00", "15:00")
        )
        val creation2 = shiftsPreconditions.fillCreationBuilder(
            "2", profileId2,
            commonPreconditions.getFormattedTimeRange(date, "16:00", "17:00")
        )
        val batchAssignmentsRequest = shiftsPreconditions.fillBatchAssignmentsRequest(
            assignmentsRole = ApiEnum(AssigneeRole.DELIVERYMAN),
            editingTimeRange = range,
            creations = listOf(creation1, creation2),
            cancellations = null
        )

        val assignments =
            shiftsActions.batchAssignments(batchAssignmentsRequest)

        val assignmentsFromApi1 =
            shiftsActions.getAssignmentsById(assignments.assignments.filter { it.assignment.userId == profileId1 }[0].assignment.assignmentId)
        val assignmentsFromApi2 =
            shiftsActions.getAssignmentsById(assignments.assignments.filter { it.assignment.userId == profileId2 }[0].assignment.assignmentId)

        shiftsAssertion
            .checkAssignmentsInfo(assignmentsFromApi1, batchAssignmentsRequest, creation1)
            .checkAssignmentsInfo(assignmentsFromApi2, batchAssignmentsRequest, creation2)
    }

    @Test
    @Tags (Tag("smoke"))
    @DisplayName("Create an assignment: create 2 assignments for 2 employees (bordering)")
    fun createTwoAssignmentsForTwoEmployeesBordering() {
        val profileId1 = commonPreconditions.createProfileDeliveryman().profileId
        val profileId2 = commonPreconditions.createProfileDeliveryman(Constants.mobile2).profileId

        val creation1 = shiftsPreconditions.fillCreationBuilder(
            "1", profileId1,
            commonPreconditions.getFormattedTimeRange(date, "09:00", "15:00")
        )
        val creation2 = shiftsPreconditions.fillCreationBuilder(
            "2", profileId2,
            commonPreconditions.getFormattedTimeRange(date, "15:01", "17:00")
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

        val assignmentsFromApi1 = shiftsActions.getAssignmentsById(assignmentId1)
        val assignmentsFromApi2 = shiftsActions.getAssignmentsById(assignmentId2)

        shiftsAssertion
            .checkAssignmentsInfo(assignmentsFromApi1, batchAssignmentsRequest, creation1)
            .checkAssignmentsInfo(assignmentsFromApi2, batchAssignmentsRequest, creation2)
    }

    @Test
    @DisplayName("Create an assignment: create 2 assignments for 2 employees (crossing)")
    fun createTwoAssignmentsForTwoEmployeesCrossing() {
        val profileId1 = commonPreconditions.createProfileDeliveryman().profileId
        val profileId2 = commonPreconditions.createProfileDeliveryman(Constants.mobile2).profileId

        val creation1 = shiftsPreconditions.fillCreationBuilder(
            "1", profileId1,
            commonPreconditions.getFormattedTimeRange(date, "09:00", "15:00")
        )
        val creation2 = shiftsPreconditions.fillCreationBuilder(
            "2", profileId2,
            commonPreconditions.getFormattedTimeRange(date, "11:00", "17:00")
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

        val assignmentsFromApi1 = shiftsActions.getAssignmentsById(assignmentId1)
        val assignmentsFromApi2 = shiftsActions.getAssignmentsById(assignmentId2)

        shiftsAssertion
            .checkAssignmentsInfo(assignmentsFromApi1, batchAssignmentsRequest, creation1)
            .checkAssignmentsInfo(assignmentsFromApi2, batchAssignmentsRequest, creation2)
    }

    @Test
    @Tags (Tag("smoke"))
    @DisplayName("Create an assignment: create 2 assignments for 2 employees (equal)")
    fun createTwoAssignmentsForTwoEmployeesEqual() {
        val profileId1 = commonPreconditions.createProfileDeliveryman().profileId
        val profileId2 = commonPreconditions.createProfileDeliveryman(Constants.mobile2).profileId

        val creation1 = shiftsPreconditions.fillCreationBuilder(
            "1", profileId1,
            commonPreconditions.getFormattedTimeRange(date, "09:00", "15:00")
        )
        val creation2 = shiftsPreconditions.fillCreationBuilder(
            "2", profileId2,
            commonPreconditions.getFormattedTimeRange(date, "09:00", "15:00")
        )
        val batchAssignmentsRequest = shiftsPreconditions.fillBatchAssignmentsRequest(
            assignmentsRole = ApiEnum(AssigneeRole.DELIVERYMAN),
            editingTimeRange = range,
            creations = listOf(creation1, creation2),
            cancellations = null
        )

        val assignments =
            shiftsActions.batchAssignments(batchAssignmentsRequest).assignments
        val assignmentId1 = assignments.filter { it.assignment.userId == profileId1 }[0].assignment.assignmentId
        val assignmentId2 = assignments.filter { it.assignment.userId == profileId2 }[0].assignment.assignmentId

        val assignmentsFromApi1 = shiftsActions.getAssignmentsById(assignmentId1)
        val assignmentsFromApi2 = shiftsActions.getAssignmentsById(assignmentId2)

        shiftsAssertion
            .checkAssignmentsInfo(assignmentsFromApi1, batchAssignmentsRequest, creation1)
            .checkAssignmentsInfo(assignmentsFromApi2, batchAssignmentsRequest, creation2)
    }

    @Test
    @DisplayName("Create an assignment: create 2 crossing assignments for 1 employee")
    fun createTwoCrossingAssignmentsForOneEmployee() {
        val profileId = commonPreconditions.createProfileDeliveryman().profileId

        val creation1 = shiftsPreconditions.fillCreationBuilder(
            "1", profileId,
            commonPreconditions.getFormattedTimeRange(date, "09:00", "15:00")
        )
        val creation2 = shiftsPreconditions.fillCreationBuilder(
            "2", profileId,
            commonPreconditions.getFormattedTimeRange(date, "11:00", "17:00")
        )
        val batchAssignmentsRequest = shiftsPreconditions.fillBatchAssignmentsRequest(
            assignmentsRole = ApiEnum(AssigneeRole.DELIVERYMAN),
            editingTimeRange = range,
            creations = listOf(creation1, creation2),
            cancellations = null
        )

        val errorMessage = shiftsActions.batchAssignmentsWithError(batchAssignmentsRequest).message

        shiftsAssertion.checkErrorMessage(
            errorMessage,
            "Invalid shift assignments creations: two overlapping shifts cannot be assigned to the same user with ID: " + profileId.toString()
        )

    }

    @Test
    @DisplayName("Create an assignment: create 2 equal assignments for 1 employee")
    fun createTwoEqualAssignmentsForOneEmployee() {
        val profileId = commonPreconditions.createProfileDeliveryman().profileId

        val creation1 = shiftsPreconditions.fillCreationBuilder(
            "1", profileId,
            commonPreconditions.getFormattedTimeRange(date, "09:00", "15:00")
        )
        val creation2 = shiftsPreconditions.fillCreationBuilder(
            "2", profileId,
            commonPreconditions.getFormattedTimeRange(date, "09:00", "15:00")
        )
        val batchAssignmentsRequest = shiftsPreconditions.fillBatchAssignmentsRequest(
            assignmentsRole = ApiEnum(AssigneeRole.DELIVERYMAN),
            editingTimeRange = range,
            creations = listOf(creation1, creation2),
            cancellations = null
        )

        val errorMessage = shiftsActions.batchAssignmentsWithError(batchAssignmentsRequest).message

        shiftsAssertion.checkErrorMessage(
            errorMessage,
            "Invalid shift assignments creations: two overlapping shifts cannot be assigned to the same user with ID: " + profileId.toString()
        )

    }

    @Test
    @Tags (Tag("smoke"))
    @DisplayName("Create an assignment: out of range")
    fun createAssignmentsOutOfRange() {
        val profileId = commonPreconditions.createProfileDeliveryman().profileId

        val creation1 = shiftsPreconditions.fillCreationBuilder(
            "1", profileId,
            TimeRange(
                startingAt = commonPreconditions.getFormattedTime(date.plusDays(1), "09:00"),
                endingAt = commonPreconditions.getFormattedTime(date.plusDays(1), "15:00")
            )
        )
        val creation2 = shiftsPreconditions.fillCreationBuilder(
            "2", profileId,
            commonPreconditions.getFormattedTimeRange(date, "16:00", "19:00")
        )
        val batchAssignmentsRequest = shiftsPreconditions.fillBatchAssignmentsRequest(
            assignmentsRole = ApiEnum(AssigneeRole.DELIVERYMAN),
            editingTimeRange = range,
            creations = listOf(creation1, creation2),
            cancellations = null
        )

        val errorMessage = shiftsActions.batchAssignmentsWithError(batchAssignmentsRequest).message

        shiftsAssertion.checkErrorMessage(
            errorMessage, "Invalid request: creation's time range at index 0 is not included in editing time range")

    }

    @Test
    @DisplayName("Create an assignment: foreign darkstore")
    fun createAssignmentOnForeignDarkstore(){
        val profileId = commonPreconditions.createProfileDeliveryman(darkstoreId = Constants.updatedDarkstoreId).profileId
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

        val assignmentsFromApi = shiftsActions.getAssignmentsById(assignmentId)
        val assignmentLogDB = shiftsActions.getAssignmentLogFromDatabaseByType(assignmentId, "assignment")!!
        val assignmentDB = shiftsActions.getAssignmentFromDatabase(assignmentId)
        val assignmentKafka = shiftsActions.getMessageFromKafkaShiftAssignmentsLog(assignmentId)

        shiftsAssertion
            .checkAssignmentsInfo(assignmentsFromApi, batchAssignmentsRequest, creation)
            .checkAssignmentLogFromDB(assignmentLogDB, batchAssignmentsRequest, creation)
            .checkAssignmentFromDB(assignmentDB, batchAssignmentsRequest, creation)
            .checkShiftAssignmentsLogMessage(assignmentKafka, batchAssignmentsRequest, creation)

    }
}