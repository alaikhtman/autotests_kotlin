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
class UpdateAssignments {

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
    @DisplayName("Batch assignments: change 1 assignment for deliveryman")
    fun changeAssignmentDeliveryman() {

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


        val updation = shiftsPreconditions.fillUpdationBuilder(
            assignmentId, commonPreconditions.getFormattedTimeRange(date, "11:00", "15:00")
        )
        val batchAssignmentsRequest2 = shiftsPreconditions.fillBatchAssignmentsRequest(
            assignmentsRole = ApiEnum(AssigneeRole.DELIVERYMAN), editingTimeRange = range, creations = null,
            updates = listOf(updation),
            cancellations = null
        )
        shiftsActions.batchAssignments(batchAssignmentsRequest2)

        val assignmentsFromApi = shiftsActions.getAssignmentsById(assignmentId)
        val assignmentLogDB = shiftsActions.getAssignmentLogFromDatabaseByType(assignmentId, "update")!!
        val assignmentDB = shiftsActions.getAssignmentFromDatabase(assignmentId)

        val assignmentKafka = shiftsActions.getMessageFromKafkaShiftAssignmentsLogByParam(assignmentId, "edited", "true")

        shiftsAssertion
            .checkAssignmentsInfo(profileId, assignmentsFromApi, batchAssignmentsRequest2, updation)
            .checkAssignmentLogFromDB(profileId, assignmentLogDB, batchAssignmentsRequest2, updation)
            .checkAssignmentFromDB(profileId, assignmentDB, batchAssignmentsRequest)
            .checkShiftAssignmentsLogMessage(assignmentKafka, batchAssignmentsRequest, creation, updation, 2L)
    }

    @Test
    @DisplayName("Batch assignments: change 1 assignment for picker")
    fun changeAssignmentPicker() {

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


        val updation = shiftsPreconditions.fillUpdationBuilder(
            assignmentId, commonPreconditions.getFormattedTimeRange(date, "11:00", "15:00")
        )
        val batchAssignmentsRequest2 = shiftsPreconditions.fillBatchAssignmentsRequest(
            assignmentsRole = ApiEnum(AssigneeRole.PICKER), editingTimeRange = range, creations = null,
            updates = listOf(updation),
            cancellations = null
        )
        shiftsActions.batchAssignments(batchAssignmentsRequest2)

        val assignmentsFromApi = shiftsActions.getAssignmentsById(assignmentId)
        val assignmentLogDB = shiftsActions.getAssignmentLogFromDatabaseByType(assignmentId, "update")!!
        val assignmentDB = shiftsActions.getAssignmentFromDatabase(assignmentId)

        // todo - check kafka message

        shiftsAssertion
            .checkAssignmentsInfo(profileId, assignmentsFromApi, batchAssignmentsRequest2, updation)
            .checkAssignmentLogFromDB(profileId, assignmentLogDB, batchAssignmentsRequest2, updation)
            .checkAssignmentFromDB(profileId, assignmentDB, batchAssignmentsRequest)
    }

    @Test
    @DisplayName("Batch assignments: change 2 assignment for 1 employee")
    fun change2AssignmentFor1Employee() {

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

        val assignmentId1 =
            shiftsActions.batchAssignments(batchAssignmentsRequest).assignments[0].assignment.assignmentId
        val assignmentId2 =
            shiftsActions.batchAssignments(batchAssignmentsRequest).assignments[1].assignment.assignmentId


        val updation1 = shiftsPreconditions.fillUpdationBuilder(
            assignmentId1, TimeRange(
                startingAt = date.atTime(LocalTime.parse("11:00")).toInstant(ZoneOffset.UTC),
                endingAt = date.atTime(LocalTime.parse("14:00")).toInstant(ZoneOffset.UTC)
            )
        )
        val updation2 = shiftsPreconditions.fillUpdationBuilder(
            assignmentId2, TimeRange(
                startingAt = date.atTime(LocalTime.parse("17:00")).toInstant(ZoneOffset.UTC),
                endingAt = date.atTime(LocalTime.parse("18:00")).toInstant(ZoneOffset.UTC)
            )
        )
        val batchAssignmentsRequest2 = shiftsPreconditions.fillBatchAssignmentsRequest(
            assignmentsRole = ApiEnum(AssigneeRole.DELIVERYMAN), editingTimeRange = range, creations = null,
            updates = listOf(updation1, updation2),
            cancellations = null
        )
        shiftsActions.batchAssignments(batchAssignmentsRequest2)

        val assignmentsFromApi1 = shiftsActions.getAssignmentsById(assignmentId1)
        val assignmentLogDB1 = shiftsActions.getAssignmentLogFromDatabaseByType(assignmentId1, "update")!!
        val assignmentDB1 = shiftsActions.getAssignmentFromDatabase(assignmentId1)

        val assignmentsFromApi2 = shiftsActions.getAssignmentsById(assignmentId2)
        val assignmentLogDB2 = shiftsActions.getAssignmentLogFromDatabaseByType(assignmentId2, "update")!!
        val assignmentDB2 = shiftsActions.getAssignmentFromDatabase(assignmentId2)

        shiftsAssertion
            .checkAssignmentsInfo(profileId, assignmentsFromApi1, batchAssignmentsRequest2, updation1)
            .checkAssignmentLogFromDB(profileId, assignmentLogDB1, batchAssignmentsRequest2, updation1)
            .checkAssignmentFromDB(profileId, assignmentDB1, batchAssignmentsRequest)
            .checkAssignmentsInfo(profileId, assignmentsFromApi2, batchAssignmentsRequest2, updation2)
            .checkAssignmentLogFromDB(profileId, assignmentLogDB2, batchAssignmentsRequest2, updation2)
            .checkAssignmentFromDB(profileId, assignmentDB2, batchAssignmentsRequest)
    }

    @Test
    @DisplayName("Batch assignments: change 2 assignment for 2 employee")
    fun change2AssignmentFor2Employee() {

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

        val assignmentId1 =
            shiftsActions.batchAssignments(batchAssignmentsRequest).assignments[0].assignment.assignmentId
        val assignmentId2 =
            shiftsActions.batchAssignments(batchAssignmentsRequest).assignments[1].assignment.assignmentId


        val updation1 = shiftsPreconditions.fillUpdationBuilder(
            assignmentId1, TimeRange(
                startingAt = date.atTime(LocalTime.parse("11:00")).toInstant(ZoneOffset.UTC),
                endingAt = date.atTime(LocalTime.parse("14:00")).toInstant(ZoneOffset.UTC)
            )
        )
        val updation2 = shiftsPreconditions.fillUpdationBuilder(
            assignmentId2, TimeRange(
                startingAt = date.atTime(LocalTime.parse("17:00")).toInstant(ZoneOffset.UTC),
                endingAt = date.atTime(LocalTime.parse("18:00")).toInstant(ZoneOffset.UTC)
            )
        )
        val batchAssignmentsRequest2 = shiftsPreconditions.fillBatchAssignmentsRequest(
            assignmentsRole = ApiEnum(AssigneeRole.DELIVERYMAN), editingTimeRange = range, creations = null,
            updates = listOf(updation1, updation2),
            cancellations = null
        )
        shiftsActions.batchAssignments(batchAssignmentsRequest2)

        val assignmentsFromApi1 = shiftsActions.getAssignmentsById(assignmentId1)
        val assignmentLogDB1 = shiftsActions.getAssignmentLogFromDatabaseByType(assignmentId1, "update")!!
        val assignmentDB1 = shiftsActions.getAssignmentFromDatabase(assignmentId1)

        val assignmentsFromApi2 = shiftsActions.getAssignmentsById(assignmentId2)
        val assignmentLogDB2 = shiftsActions.getAssignmentLogFromDatabaseByType(assignmentId2, "update")!!
        val assignmentDB2 = shiftsActions.getAssignmentFromDatabase(assignmentId2)

        shiftsAssertion
            .checkAssignmentsInfo(profileId1, assignmentsFromApi1, batchAssignmentsRequest2, updation1)
            .checkAssignmentLogFromDB(profileId1, assignmentLogDB1, batchAssignmentsRequest2, updation1)
            .checkAssignmentFromDB(profileId1, assignmentDB1, batchAssignmentsRequest)
            .checkAssignmentsInfo(profileId2, assignmentsFromApi2, batchAssignmentsRequest2, updation2)
            .checkAssignmentLogFromDB(profileId2, assignmentLogDB2, batchAssignmentsRequest2, updation2)
            .checkAssignmentFromDB(profileId2, assignmentDB2, batchAssignmentsRequest)
    }

    @Test
    @Tags (Tag("smoke"))
    @DisplayName("Batch assignments: change 2 assignment for 2 employee (crossing)")
    fun change2AssignmentFor2EmployeeCrossing() {

        val profileId1 = commonPreconditions.createProfileDeliveryman().profileId
        val profileId2 = commonPreconditions.createProfileDeliveryman(Constants.mobile2).profileId

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

        val assignmentId1 =
            shiftsActions.batchAssignments(batchAssignmentsRequest).assignments[0].assignment.assignmentId
        val assignmentId2 =
            shiftsActions.batchAssignments(batchAssignmentsRequest).assignments[1].assignment.assignmentId


        val updation1 = shiftsPreconditions.fillUpdationBuilder(
            assignmentId1, TimeRange(
                startingAt = date.atTime(LocalTime.parse("11:00")).toInstant(ZoneOffset.UTC),
                endingAt = date.atTime(LocalTime.parse("17:00")).toInstant(ZoneOffset.UTC)
            )
        )
        val updation2 = shiftsPreconditions.fillUpdationBuilder(
            assignmentId2, TimeRange(
                startingAt = date.atTime(LocalTime.parse("16:00")).toInstant(ZoneOffset.UTC),
                endingAt = date.atTime(LocalTime.parse("18:00")).toInstant(ZoneOffset.UTC)
            )
        )
        val batchAssignmentsRequest2 = shiftsPreconditions.fillBatchAssignmentsRequest(
            assignmentsRole = ApiEnum(AssigneeRole.DELIVERYMAN), editingTimeRange = range, creations = null,
            updates = listOf(updation1, updation2),
            cancellations = null
        )
        shiftsActions.batchAssignments(batchAssignmentsRequest2)

        val assignmentsFromApi1 = shiftsActions.getAssignmentsById(assignmentId1)
        val assignmentLogDB1 = shiftsActions.getAssignmentLogFromDatabaseByType(assignmentId1, "update")!!
        val assignmentDB1 = shiftsActions.getAssignmentFromDatabase(assignmentId1)

        val assignmentsFromApi2 = shiftsActions.getAssignmentsById(assignmentId2)
        val assignmentLogDB2 = shiftsActions.getAssignmentLogFromDatabaseByType(assignmentId2, "update")!!
        val assignmentDB2 = shiftsActions.getAssignmentFromDatabase(assignmentId2)

        shiftsAssertion
            .checkAssignmentsInfo(profileId1, assignmentsFromApi1, batchAssignmentsRequest2, updation1)
            .checkAssignmentLogFromDB(profileId1, assignmentLogDB1, batchAssignmentsRequest2, updation1)
            .checkAssignmentFromDB(profileId1, assignmentDB1, batchAssignmentsRequest)
            .checkAssignmentsInfo(profileId2, assignmentsFromApi2, batchAssignmentsRequest2, updation2)
            .checkAssignmentLogFromDB(profileId2, assignmentLogDB2, batchAssignmentsRequest2, updation2)
            .checkAssignmentFromDB(profileId2, assignmentDB2, batchAssignmentsRequest)
    }

    @Test
    @DisplayName("Batch assignments: change 2 assignment for 2 employee (equal)")
    fun change2AssignmentFor2EmployeeEqual() {

        val profileId1 = commonPreconditions.createProfileDeliveryman().profileId
        val profileId2 = commonPreconditions.createProfileDeliveryman(Constants.mobile2).profileId

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

        val assignmentId1 =
            shiftsActions.batchAssignments(batchAssignmentsRequest).assignments[0].assignment.assignmentId
        val assignmentId2 =
            shiftsActions.batchAssignments(batchAssignmentsRequest).assignments[1].assignment.assignmentId


        val updation1 = shiftsPreconditions.fillUpdationBuilder(
            assignmentId1, TimeRange(
                startingAt = date.atTime(LocalTime.parse("11:00")).toInstant(ZoneOffset.UTC),
                endingAt = date.atTime(LocalTime.parse("17:00")).toInstant(ZoneOffset.UTC)
            )
        )
        val updation2 = shiftsPreconditions.fillUpdationBuilder(
            assignmentId2, TimeRange(
                startingAt = date.atTime(LocalTime.parse("11:00")).toInstant(ZoneOffset.UTC),
                endingAt = date.atTime(LocalTime.parse("17:00")).toInstant(ZoneOffset.UTC)
            )
        )
        val batchAssignmentsRequest2 = shiftsPreconditions.fillBatchAssignmentsRequest(
            assignmentsRole = ApiEnum(AssigneeRole.DELIVERYMAN), editingTimeRange = range, creations = null,
            updates = listOf(updation1, updation2),
            cancellations = null
        )
        shiftsActions.batchAssignments(batchAssignmentsRequest2)

        val assignmentsFromApi1 = shiftsActions.getAssignmentsById(assignmentId1)
        val assignmentLogDB1 = shiftsActions.getAssignmentLogFromDatabaseByType(assignmentId1, "update")!!
        val assignmentDB1 = shiftsActions.getAssignmentFromDatabase(assignmentId1)

        val assignmentsFromApi2 = shiftsActions.getAssignmentsById(assignmentId2)
        val assignmentLogDB2 = shiftsActions.getAssignmentLogFromDatabaseByType(assignmentId2, "update")!!
        val assignmentDB2 = shiftsActions.getAssignmentFromDatabase(assignmentId2)

        shiftsAssertion
            .checkAssignmentsInfo(profileId1, assignmentsFromApi1, batchAssignmentsRequest2, updation1)
            .checkAssignmentLogFromDB(profileId1, assignmentLogDB1, batchAssignmentsRequest2, updation1)
            .checkAssignmentFromDB(profileId1, assignmentDB1, batchAssignmentsRequest)
            .checkAssignmentsInfo(profileId2, assignmentsFromApi2, batchAssignmentsRequest2, updation2)
            .checkAssignmentLogFromDB(profileId2, assignmentLogDB2, batchAssignmentsRequest2, updation2)
            .checkAssignmentFromDB(profileId2, assignmentDB2, batchAssignmentsRequest)
    }

    @Test
    @Tags (Tag("smoke"))
    @DisplayName("Batch assignments: change 2 assignment for 1 employee (swap)")
    fun change2AssignmentFor1EmployeeSwap() {

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

        val assignmentId1 =
            shiftsActions.batchAssignments(batchAssignmentsRequest).assignments[0].assignment.assignmentId
        val assignmentId2 =
            shiftsActions.batchAssignments(batchAssignmentsRequest).assignments[1].assignment.assignmentId


        val updation1 = shiftsPreconditions.fillUpdationBuilder(
            assignmentId1, TimeRange(
                startingAt = date.atTime(LocalTime.parse("16:00")).toInstant(ZoneOffset.UTC),
                endingAt = date.atTime(LocalTime.parse("17:00")).toInstant(ZoneOffset.UTC)
            )
        )
        val updation2 = shiftsPreconditions.fillUpdationBuilder(
            assignmentId2, TimeRange(
                startingAt = date.atTime(LocalTime.parse("09:00")).toInstant(ZoneOffset.UTC),
                endingAt = date.atTime(LocalTime.parse("15:00")).toInstant(ZoneOffset.UTC)
            )
        )
        val batchAssignmentsRequest2 = shiftsPreconditions.fillBatchAssignmentsRequest(
            assignmentsRole = ApiEnum(AssigneeRole.DELIVERYMAN), editingTimeRange = range, creations = null,
            updates = listOf(updation1, updation2),
            cancellations = null
        )
        shiftsActions.batchAssignments(batchAssignmentsRequest2)

        val assignmentsFromApi1 = shiftsActions.getAssignmentsById(assignmentId1)
        val assignmentLogDB1 = shiftsActions.getAssignmentLogFromDatabaseByType(assignmentId1, "update")!!
        val assignmentDB1 = shiftsActions.getAssignmentFromDatabase(assignmentId1)

        val assignmentsFromApi2 = shiftsActions.getAssignmentsById(assignmentId2)
        val assignmentLogDB2 = shiftsActions.getAssignmentLogFromDatabaseByType(assignmentId2, "update")!!
        val assignmentDB2 = shiftsActions.getAssignmentFromDatabase(assignmentId2)

        shiftsAssertion
            .checkAssignmentsInfo(profileId, assignmentsFromApi1, batchAssignmentsRequest2, updation1)
            .checkAssignmentLogFromDB(profileId, assignmentLogDB1, batchAssignmentsRequest2, updation1)
            .checkAssignmentFromDB(profileId, assignmentDB1, batchAssignmentsRequest)
            .checkAssignmentsInfo(profileId, assignmentsFromApi2, batchAssignmentsRequest2, updation2)
            .checkAssignmentLogFromDB(profileId, assignmentLogDB2, batchAssignmentsRequest2, updation2)
            .checkAssignmentFromDB(profileId, assignmentDB2, batchAssignmentsRequest)
    }

    @Test
    @DisplayName("Batch assignments: change and create 1 assignment for deliveryman")
    fun changeAndCreateAssignmentForOneEmployee() {

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
            "2", profileId,
            commonPreconditions.getFormattedTimeRange(date, "08:00", "10:00")
        )
        val updation = shiftsPreconditions.fillUpdationBuilder(
            assignmentId1, commonPreconditions.getFormattedTimeRange(date, "11:00", "15:00")
        )
        val batchAssignmentsRequest2 = shiftsPreconditions.fillBatchAssignmentsRequest(
            assignmentsRole = ApiEnum(AssigneeRole.DELIVERYMAN),
            editingTimeRange = range,
            creations = listOf(creation2),
            updates = listOf(updation),
            cancellations = null
        )
        val assignmentId2 =
            shiftsActions.batchAssignments(batchAssignmentsRequest2).assignments.filter { it.assignment.assignmentId != assignmentId1 }[0].assignment.assignmentId

        val assignmentsFromApi1 = shiftsActions.getAssignmentsById(assignmentId1)
        val assignmentLogDB1 = shiftsActions.getAssignmentLogFromDatabaseByType(assignmentId1, "update")!!
        val assignmentDB1 = shiftsActions.getAssignmentFromDatabase(assignmentId1)

        val assignmentsFromApi2 = shiftsActions.getAssignmentsById(assignmentId2)
        val assignmentLogDB2 = shiftsActions.getAssignmentLogFromDatabaseByType(assignmentId2, "assignment")!!
        val assignmentDB2 = shiftsActions.getAssignmentFromDatabase(assignmentId2)

        shiftsAssertion
            .checkAssignmentsInfo(profileId, assignmentsFromApi1, batchAssignmentsRequest2, updation)
            .checkAssignmentLogFromDB(profileId, assignmentLogDB1, batchAssignmentsRequest2, updation)
            .checkAssignmentFromDB(profileId, assignmentDB1, batchAssignmentsRequest2)

            .checkAssignmentsInfo(assignmentsFromApi2, batchAssignmentsRequest2, creation2)
            .checkAssignmentLogFromDB(assignmentLogDB2, batchAssignmentsRequest2, creation2)
            .checkAssignmentFromDB(assignmentDB2, batchAssignmentsRequest2, creation2)
    }

    @Test
    @DisplayName("Batch assignments: change and create 1 assignment for 2 employees")
    fun changeAndCreateAssignmentForTwoEmployee() {

        val profileId1 = commonPreconditions.createProfileDeliveryman().profileId
        val profileId2 = commonPreconditions.createProfileDeliveryman(Constants.mobile2).profileId

        val creation = shiftsPreconditions.fillCreationBuilder(
            "1", profileId1,
            commonPreconditions.getFormattedTimeRange(date, "09:00", "17:00")
        )
        val batchAssignmentsRequest = shiftsPreconditions.fillBatchAssignmentsRequest(
            assignmentsRole = ApiEnum(AssigneeRole.DELIVERYMAN), editingTimeRange = range, creations = listOf(creation),
            cancellations = null
        )

        val assignmentId1 =
            shiftsActions.batchAssignments(batchAssignmentsRequest).assignments[0].assignment.assignmentId

        val creation2 = shiftsPreconditions.fillCreationBuilder(
            "2", profileId2,
            commonPreconditions.getFormattedTimeRange(date, "08:00", "10:00")
        )
        val updation = shiftsPreconditions.fillUpdationBuilder(
            assignmentId1, commonPreconditions.getFormattedTimeRange(date, "08:00", "10:00")
        )
        val batchAssignmentsRequest2 = shiftsPreconditions.fillBatchAssignmentsRequest(
            assignmentsRole = ApiEnum(AssigneeRole.DELIVERYMAN),
            editingTimeRange = range,
            creations = listOf(creation2),
            updates = listOf(updation),
            cancellations = null
        )
        val assignmentId2 =
            shiftsActions.batchAssignments(batchAssignmentsRequest2).assignments.filter { it.assignment.assignmentId != assignmentId1 }[0].assignment.assignmentId

        val assignmentsFromApi1 = shiftsActions.getAssignmentsById(assignmentId1)
        val assignmentLogDB1 = shiftsActions.getAssignmentLogFromDatabaseByType(assignmentId1, "update")!!
        val assignmentDB1 = shiftsActions.getAssignmentFromDatabase(assignmentId1)

        val assignmentsFromApi2 = shiftsActions.getAssignmentsById(assignmentId2)
        val assignmentLogDB2 = shiftsActions.getAssignmentLogFromDatabaseByType(assignmentId2, "assignment")!!
        val assignmentDB2 = shiftsActions.getAssignmentFromDatabase(assignmentId2)

        shiftsAssertion
            .checkAssignmentsInfo(profileId1, assignmentsFromApi1, batchAssignmentsRequest2, updation)
            .checkAssignmentLogFromDB(profileId1, assignmentLogDB1, batchAssignmentsRequest2, updation)
            .checkAssignmentFromDB(profileId1, assignmentDB1, batchAssignmentsRequest2)

            .checkAssignmentsInfo(assignmentsFromApi2, batchAssignmentsRequest2, creation2)
            .checkAssignmentLogFromDB(assignmentLogDB2, batchAssignmentsRequest2, creation2)
            .checkAssignmentFromDB(assignmentDB2, batchAssignmentsRequest2, creation2)
    }

    @Test
    @DisplayName("Batch assignments: change 2 assignment for 1 employee (crossing)")
    fun change2AssignmentFor1EmployeeCrossing() {

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

        val assignmentId1 =
            shiftsActions.batchAssignments(batchAssignmentsRequest).assignments[0].assignment.assignmentId
        val assignmentId2 =
            shiftsActions.batchAssignments(batchAssignmentsRequest).assignments[1].assignment.assignmentId


        val updation1 = shiftsPreconditions.fillUpdationBuilder(
            assignmentId1, commonPreconditions.getFormattedTimeRange(date, "11:00", "17:00")
        )
        val updation2 = shiftsPreconditions.fillUpdationBuilder(
            assignmentId2, commonPreconditions.getFormattedTimeRange(date, "15:00", "18:00")
        )
        val batchAssignmentsRequest2 = shiftsPreconditions.fillBatchAssignmentsRequest(
            assignmentsRole = ApiEnum(AssigneeRole.DELIVERYMAN), editingTimeRange = range, creations = null,
            updates = listOf(updation1, updation2),
            cancellations = null
        )

        val errorMessage = shiftsActions.batchAssignmentsWithError(batchAssignmentsRequest2).message

        shiftsAssertion.checkErrorMessage(
            errorMessage,
            "Given batch violates constraints"
        )
    }

    @Test
    @DisplayName("Batch assignments: change 2 assignment for 1 employee (equal)")
    fun change2AssignmentFor1EmployeeEqual() {

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

        val assignmentId1 =
            shiftsActions.batchAssignments(batchAssignmentsRequest).assignments[0].assignment.assignmentId
        val assignmentId2 =
            shiftsActions.batchAssignments(batchAssignmentsRequest).assignments[1].assignment.assignmentId


        val updation1 = shiftsPreconditions.fillUpdationBuilder(
            assignmentId1, commonPreconditions.getFormattedTimeRange(date, "11:00", "18:00")
        )
        val updation2 = shiftsPreconditions.fillUpdationBuilder(
            assignmentId2, commonPreconditions.getFormattedTimeRange(date, "11:00", "18:00")
        )
        val batchAssignmentsRequest2 = shiftsPreconditions.fillBatchAssignmentsRequest(
            assignmentsRole = ApiEnum(AssigneeRole.DELIVERYMAN), editingTimeRange = range, creations = null,
            updates = listOf(updation1, updation2),
            cancellations = null
        )

        val errorMessage = shiftsActions.batchAssignmentsWithError(batchAssignmentsRequest2).message

        shiftsAssertion.checkErrorMessage(errorMessage, "Given batch violates constraints")
    }

    @Test
    @DisplayName("Batch assignments: update deleted assignment")
    fun updateDeletedAssignment() {

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

        val deletion = shiftsPreconditions.fillCancellationBuilder(assignmentId)
        val batchAssignmentsRequest2 = shiftsPreconditions.fillBatchAssignmentsRequest(
            assignmentsRole = ApiEnum(AssigneeRole.DELIVERYMAN), editingTimeRange = range, creations = null,
            cancellations = listOf(deletion)
        )
        shiftsActions.batchAssignments(batchAssignmentsRequest2)

        val updation = shiftsPreconditions.fillUpdationBuilder(
            assignmentId, commonPreconditions.getFormattedTimeRange(date, "09:00", "18:00"), version = 2L
        )
        val batchAssignmentsRequest3 = shiftsPreconditions.fillBatchAssignmentsRequest(
            assignmentsRole = ApiEnum(AssigneeRole.DELIVERYMAN), editingTimeRange = range, creations = null,
            updates = listOf(updation),
            cancellations = null
        )

        val errorMessage = shiftsActions.batchAssignmentsWithError(batchAssignmentsRequest3).message

        shiftsAssertion.checkErrorMessage(errorMessage, "Shift assignment is not included in editing time range")

    }

    @Test
    @DisplayName("Batch assignments: create and change 2 assignment for 1 employee (equal)")
    fun createAndChange2AssignmentFor1EmployeeEqual() {

        val profileId = commonPreconditions.createProfileDeliveryman().profileId

        val creation1 = shiftsPreconditions.fillCreationBuilder(
            "1", profileId,
            commonPreconditions.getFormattedTimeRange(date, "09:00", "15:00")
        )
        val creation2 = shiftsPreconditions.fillCreationBuilder(
            "2", profileId,
            commonPreconditions.getFormattedTimeRange(date, "16:00", "18:00")
        )
        val batchAssignmentsRequest = shiftsPreconditions.fillBatchAssignmentsRequest(
            assignmentsRole = ApiEnum(AssigneeRole.DELIVERYMAN),
            editingTimeRange = range,
            creations = listOf(creation1, creation2),
            cancellations = null
        )

        val assignmentId1 =
            shiftsActions.batchAssignments(batchAssignmentsRequest).assignments[0].assignment.assignmentId

        val updation1 = shiftsPreconditions.fillUpdationBuilder(
            assignmentId1, commonPreconditions.getFormattedTimeRange(date, "16:00", "18:00")
        )

        val batchAssignmentsRequest2 = shiftsPreconditions.fillBatchAssignmentsRequest(
            assignmentsRole = ApiEnum(AssigneeRole.DELIVERYMAN),
            editingTimeRange = range,
            creations = listOf(creation2),
            updates = listOf(updation1),
            cancellations = null
        )

        val errorMessage = shiftsActions.batchAssignmentsWithError(batchAssignmentsRequest2).message

        shiftsAssertion.checkErrorMessage(errorMessage, "Given batch violates constraints")
    }

    @Test
    @DisplayName("Batch assignments: update assignment not in range")
    fun updateAssignmentNotInRange() {

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

        val updation = shiftsPreconditions.fillUpdationBuilder(
            assignmentId, commonPreconditions.getFormattedTimeRange(date.plusDays(1), "11:00", "18:00"), version = 1L
        )
        val batchAssignmentsRequest3 = shiftsPreconditions.fillBatchAssignmentsRequest(
            assignmentsRole = ApiEnum(AssigneeRole.DELIVERYMAN), editingTimeRange = range, creations = null,
            updates = listOf(updation),
            cancellations = null
        )

        val errorMessage = shiftsActions.batchAssignmentsWithError(batchAssignmentsRequest3).message

        shiftsAssertion.checkErrorMessage(
            errorMessage,
            "Invalid request: update's time range at index 0 is not included in editing time range"
        )

    }

    @Test
    @Tags (Tag("smoke"))
    @DisplayName("Batch assignments: update assignment with wrong version")
    fun updateAssignmentWithWrongVersion() {

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

        val updation = shiftsPreconditions.fillUpdationBuilder(
            assignmentId, commonPreconditions.getFormattedTimeRange(date, "11:00", "18:00"), version = 20L
        )
        val batchAssignmentsRequest3 = shiftsPreconditions.fillBatchAssignmentsRequest(
            assignmentsRole = ApiEnum(AssigneeRole.DELIVERYMAN), editingTimeRange = range, creations = null,
            updates = listOf(updation),
            cancellations = null
        )

        val errorMessage = shiftsActions.batchAssignmentsWithError(batchAssignmentsRequest3).message

        shiftsAssertion.checkErrorMessage(errorMessage, "Shift assignment was modified since last request")

    }
}