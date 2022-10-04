package ru.samokat.mysamokat.tests.tests.employee_schedule.workedOutTimeSlotAPI

import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.samokat.employeeprofiles.api.common.domain.EmployeeVehicleType
import ru.samokat.employeeprofiles.api.common.domain.Vehicle
import ru.samokat.my.domain.employee.EmployeeRole
import ru.samokat.my.rest.api.enum.ApiEnum
import ru.samokat.mysamokat.tests.checkers.CommonAssertion
import ru.samokat.mysamokat.tests.checkers.EmployeeScheduleAssertion
import ru.samokat.mysamokat.tests.dataproviders.Constants
import ru.samokat.mysamokat.tests.dataproviders.preconditions.CommonPreconditions
import ru.samokat.mysamokat.tests.dataproviders.preconditions.EmployeeSchedulePreconditions
import ru.samokat.mysamokat.tests.helpers.actions.EmployeeActions
import ru.samokat.mysamokat.tests.helpers.actions.EmployeeScheduleActions
import ru.samokat.mysamokat.tests.helpers.controllers.database.EmployeeScheduleDatabaseController
import ru.samokat.shifts.api.common.domain.ShiftUserRole
import java.time.LocalDate
import java.util.*

@SpringBootTest
@Tag("emproSchedule")
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class CreateWorkedOutTimeslot {
    private lateinit var employeeScheduleAssertion: EmployeeScheduleAssertion
    private lateinit var commonAssertion: CommonAssertion

    @Autowired
    private lateinit var employeeActions: EmployeeActions

    @Autowired
    private lateinit var employeeCommonPreconditions: CommonPreconditions

    private lateinit var employeeSchedulePreconditions: EmployeeSchedulePreconditions

    @Autowired
    private lateinit var employeeScheduleActions: EmployeeScheduleActions

    @Autowired
    private lateinit var employeeScheduleDatabase: EmployeeScheduleDatabaseController


    @BeforeEach
    fun before() {
        employeeSchedulePreconditions = EmployeeSchedulePreconditions()
        employeeScheduleAssertion = EmployeeScheduleAssertion()
        commonAssertion = CommonAssertion()
        employeeActions.deleteProfile(Constants.mobile1)
    }

    @AfterEach
    fun release() {
        commonAssertion.assertAll()
        employeeScheduleAssertion.assertAll()
        employeeActions.deleteProfile(Constants.mobile1)
    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Create worked-out timeslot deliveryman")
    fun createDeliverymanWorkedOutTimeslotTest() {

        val profileId = employeeCommonPreconditions.createProfileDeliveryman().profileId
        val workedOutShiftId = employeeCommonPreconditions.startAndStopShift(profileId).shiftId

        employeeSchedulePreconditions
            .setTimesheetRequest(date = LocalDate.now())
            .setCreateWorkedOutTimeslotRequest(
                timesheetId = employeeScheduleActions.getTimesheet(
                    Constants.darkstoreId,
                    employeeSchedulePreconditions.getTimesheetRequest()
                ).timesheetByDarkstore().timesheetId,
                profileId = profileId,
                workedOutShiftId = workedOutShiftId,
                workedOutHours = 2,
                timeEditingReason = "Случайно закрыл",
                accountingContractId = UUID.randomUUID(),
                issuerId = UUID.randomUUID()

            )
        employeeScheduleActions
            .createWorkedOutTimeSlot(
                employeeSchedulePreconditions.createWorkedOutTimesheetRequest()
            )
            .getTimeSlotById(employeeScheduleActions.workedOutTimeSlot().workedOutTimeSlotId)

        employeeScheduleAssertion
            .checkTimeSlotInDatabase(
                employeeScheduleDatabase.getWorkedOutTimeSlot(
                    employeeScheduleActions.workedOutTimeSlot().workedOutTimeSlotId
                ),
                accountContractId = employeeSchedulePreconditions.createWorkedOutTimesheetRequest().accountingContractId,
                profileId = employeeSchedulePreconditions.createWorkedOutTimesheetRequest().profileId,
                timesheetId = employeeSchedulePreconditions.createWorkedOutTimesheetRequest().timesheetId,
                workedOutHours = employeeSchedulePreconditions.createWorkedOutTimesheetRequest().workedOutHours,
                workedOutShiftId = employeeSchedulePreconditions.createWorkedOutTimesheetRequest().workedOutShiftId
            )
            .checkTimeSlotLogInDatabase(
                employeeScheduleDatabase.getWorkedOutTimeSlotLog(employeeScheduleActions.workedOutTimeSlot().workedOutTimeSlotId),
                1,
                index = 0,
                timeEditingReason = employeeSchedulePreconditions.createWorkedOutTimesheetRequest().timeEditingReason,
                issuerId = employeeSchedulePreconditions.createWorkedOutTimesheetRequest().issuerId,
                version = 1
            )
            .checkCreatedTimeSlotAPI(
                actualResponse = employeeScheduleActions.timeSlot(),
                expectedRequest = employeeSchedulePreconditions.createWorkedOutTimesheetRequest(),
                version = 1
            )


    }

    @Test
    @DisplayName("Create worked-out timeslot picker")
    fun createPickerWorkedOutTimeslotTest() {

        val profileId = employeeCommonPreconditions.createProfilePicker().profileId
        val workedOutShiftId =
            employeeCommonPreconditions.startAndStopShift(profileId, userRole = ShiftUserRole.PICKER).shiftId

        employeeSchedulePreconditions
            .setTimesheetRequest(date = LocalDate.now())
            .setCreateWorkedOutTimeslotRequest(
                timesheetId = employeeScheduleActions.getTimesheet(
                    Constants.darkstoreId,
                    employeeSchedulePreconditions.getTimesheetRequest()
                ).timesheetByDarkstore().timesheetId,
                profileId = profileId,
                workedOutShiftId = workedOutShiftId,
                workedOutHours = 13,
                timeEditingReason = "Случайно закрыл",
                accountingContractId = UUID.randomUUID(),
                issuerId = UUID.randomUUID(),

                )
        employeeScheduleActions
            .createWorkedOutTimeSlot(
                employeeSchedulePreconditions.createWorkedOutTimesheetRequest()
            )
            .getTimeSlotById(employeeScheduleActions.workedOutTimeSlot().workedOutTimeSlotId)

        employeeScheduleAssertion
            .checkTimeSlotInDatabase(
                employeeScheduleDatabase.getWorkedOutTimeSlot(
                    employeeScheduleActions.workedOutTimeSlot().workedOutTimeSlotId
                ),
                accountContractId = employeeSchedulePreconditions.createWorkedOutTimesheetRequest().accountingContractId,
                profileId = employeeSchedulePreconditions.createWorkedOutTimesheetRequest().profileId,
                timesheetId = employeeSchedulePreconditions.createWorkedOutTimesheetRequest().timesheetId,
                workedOutHours = employeeSchedulePreconditions.createWorkedOutTimesheetRequest().workedOutHours,
                workedOutShiftId = employeeSchedulePreconditions.createWorkedOutTimesheetRequest().workedOutShiftId
            )
            .checkTimeSlotLogInDatabase(
                employeeScheduleDatabase.getWorkedOutTimeSlotLog(employeeScheduleActions.workedOutTimeSlot().workedOutTimeSlotId),
                1,
                index = 0,
                timeEditingReason = employeeSchedulePreconditions.createWorkedOutTimesheetRequest().timeEditingReason,
                issuerId = employeeSchedulePreconditions.createWorkedOutTimesheetRequest().issuerId,
                version = 1
            )
            .checkCreatedTimeSlotAPI(
                actualResponse = employeeScheduleActions.timeSlot(),
                expectedRequest = employeeSchedulePreconditions.createWorkedOutTimesheetRequest(),
                version = 1
            )


    }

    @Test
    @DisplayName("Create worked-out timeslot twice for 1 user with 1 contract")
    fun createTwiceWorkedOutTimeslotTest() {
        val profileId = employeeCommonPreconditions.createProfileDeliveryman().profileId
        val workedOutShiftId1 = employeeCommonPreconditions.startAndStopShift(profileId).shiftId
        val workedOutShiftId2 = employeeCommonPreconditions.startAndStopShift(profileId).shiftId

        employeeSchedulePreconditions
            .setTimesheetRequest(date = LocalDate.now())
            .setListCreateWorkedOutTimeslotRequest(
                2,
                timesheetId = employeeScheduleActions.getTimesheet(
                    Constants.darkstoreId,
                    employeeSchedulePreconditions.getTimesheetRequest()
                ).timesheetByDarkstore().timesheetId,
                listOfProfileId = mutableListOf(profileId, profileId),
                listOfWorkedOutShiftId = mutableListOf(workedOutShiftId1, workedOutShiftId2),
                listOfWorkedOutHours = mutableListOf(6, 7),
                timeEditingReason = "Случайно закрыл",
                listOfAccountingContractId = mutableListOf(
                    Constants.darkstoreForTimesheet,
                    Constants.darkstoreForTimesheet
                ),
                issuerId = UUID.randomUUID()
            )
        employeeScheduleActions
            .createSeveralWorkedOutTimeSlot(
                employeeSchedulePreconditions.listOfCreateWorkedOutTimesheetRequest()
            )

        employeeScheduleAssertion
            .checkSeveralTimeSlotInDatabase(
                employeeScheduleDatabase.getListOfWorkedOutTimeSlot(
                    listOfWorkedOutTimeSlotId = employeeScheduleActions.listOfWorkedOutTimeSlotId()
                ),
                listOfWorkedOutShiftId = employeeSchedulePreconditions.listOfCreateWorkedOutTimesheetRequest()
                    .map { it.workedOutShiftId },
                listOfAccountContractId = employeeSchedulePreconditions.listOfCreateWorkedOutTimesheetRequest()
                    .map { it.accountingContractId },
                listOfPofileId = employeeSchedulePreconditions.listOfCreateWorkedOutTimesheetRequest()
                    .map { it.profileId },
                timesheetId = employeeSchedulePreconditions.listOfCreateWorkedOutTimesheetRequest()[1].timesheetId,
                listOfWorkedOutHours = employeeSchedulePreconditions.listOfCreateWorkedOutTimesheetRequest()
                    .map { it.workedOutHours },

                )

            .checkTimeSlotLogInDatabase(
                employeeScheduleDatabase.getWorkedOutTimeSlotLog(employeeScheduleActions.listOfWorkedOutTimeSlot()[0].workedOutTimeSlotId),
                1,
                index = 0,
                timeEditingReason = employeeSchedulePreconditions.listOfCreateWorkedOutTimesheetRequest()[0].timeEditingReason,
                issuerId = employeeSchedulePreconditions.listOfCreateWorkedOutTimesheetRequest()[0].issuerId,
                version = 1
            )
            .checkTimeSlotLogInDatabase(
                employeeScheduleDatabase.getWorkedOutTimeSlotLog(employeeScheduleActions.listOfWorkedOutTimeSlot()[1].workedOutTimeSlotId),
                1,
                index = 0,
                timeEditingReason = employeeSchedulePreconditions.listOfCreateWorkedOutTimesheetRequest()[1].timeEditingReason,
                issuerId = employeeSchedulePreconditions.listOfCreateWorkedOutTimesheetRequest()[1].issuerId,
                version = 1
            )
    }

    @Test
    @DisplayName("Create worked-out timeslot twice for 1 user with 2 contract")
    fun createForTwoContactsWorkedOutTimeslotTest() {
        val profileId = employeeCommonPreconditions.createProfileDeliveryman().profileId
        val workedOutShiftId1 = employeeCommonPreconditions.startAndStopShift(profileId).shiftId
        val workedOutShiftId2 = employeeCommonPreconditions.startAndStopShift(profileId).shiftId

        employeeSchedulePreconditions
            .setTimesheetRequest(date = LocalDate.now())
            .setListCreateWorkedOutTimeslotRequest(
                2,
                timesheetId = employeeScheduleActions.getTimesheet(
                    Constants.darkstoreId,
                    employeeSchedulePreconditions.getTimesheetRequest()
                ).timesheetByDarkstore().timesheetId,
                listOfProfileId = mutableListOf(profileId, profileId),
                listOfWorkedOutShiftId = mutableListOf(workedOutShiftId1, workedOutShiftId2),
                listOfWorkedOutHours = mutableListOf(6, 3),
                timeEditingReason = "Случайно закрыл",
                listOfAccountingContractId = mutableListOf(
                    UUID.randomUUID(),
                    UUID.randomUUID()
                ),
                issuerId = UUID.randomUUID()
            )
        employeeScheduleActions
            .createSeveralWorkedOutTimeSlot(
                employeeSchedulePreconditions.listOfCreateWorkedOutTimesheetRequest()
            )
        employeeScheduleAssertion

            .checkSeveralTimeSlotInDatabase(
                employeeScheduleDatabase.getListOfWorkedOutTimeSlot(
                    listOfWorkedOutTimeSlotId = employeeScheduleActions.listOfWorkedOutTimeSlotId()
                ),
                listOfWorkedOutShiftId = employeeSchedulePreconditions.listOfCreateWorkedOutTimesheetRequest()
                    .map { it.workedOutShiftId },
                listOfAccountContractId = employeeSchedulePreconditions.listOfCreateWorkedOutTimesheetRequest()
                    .map { it.accountingContractId },
                listOfPofileId = employeeSchedulePreconditions.listOfCreateWorkedOutTimesheetRequest()
                    .map { it.profileId },
                timesheetId = employeeSchedulePreconditions.listOfCreateWorkedOutTimesheetRequest()[1].timesheetId,
                listOfWorkedOutHours = employeeSchedulePreconditions.listOfCreateWorkedOutTimesheetRequest()
                    .map { it.workedOutHours },

                )

            .checkTimeSlotLogInDatabase(
                employeeScheduleDatabase.getWorkedOutTimeSlotLog(employeeScheduleActions.listOfWorkedOutTimeSlot()[0].workedOutTimeSlotId),
                1,
                index = 0,
                timeEditingReason = employeeSchedulePreconditions.listOfCreateWorkedOutTimesheetRequest()[0].timeEditingReason,
                issuerId = employeeSchedulePreconditions.listOfCreateWorkedOutTimesheetRequest()[0].issuerId,
                version = 1
            )
            .checkTimeSlotLogInDatabase(
                employeeScheduleDatabase.getWorkedOutTimeSlotLog(employeeScheduleActions.listOfWorkedOutTimeSlot()[1].workedOutTimeSlotId),
                1,
                index = 0,
                timeEditingReason = employeeSchedulePreconditions.listOfCreateWorkedOutTimesheetRequest()[1].timeEditingReason,
                issuerId = employeeSchedulePreconditions.listOfCreateWorkedOutTimesheetRequest()[1].issuerId,
                version = 1
            )


    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Create worked-out timeslot without reason")
    fun createWithoutReasonWorkedOutTimeslotTest() {
        val profileId = employeeCommonPreconditions.createProfileDeliveryman(vehicle = Vehicle(ApiEnum(EmployeeVehicleType.BICYCLE))).profileId
        val workedOutShiftId = employeeCommonPreconditions.startAndStopShift(profileId).shiftId

        employeeSchedulePreconditions
            .setTimesheetRequest(date = LocalDate.now())
            .setCreateWorkedOutTimeslotRequest(
                timesheetId = employeeScheduleActions.getTimesheet(
                    Constants.darkstoreId,
                    employeeSchedulePreconditions.getTimesheetRequest()
                ).timesheetByDarkstore().timesheetId,
                profileId = profileId,
                workedOutShiftId = workedOutShiftId,
                workedOutHours = 3,
                accountingContractId = UUID.randomUUID(),
                issuerId = UUID.randomUUID(),

                )
        employeeScheduleActions
            .createWorkedOutTimeSlot(
                employeeSchedulePreconditions.createWorkedOutTimesheetRequest()
            )
        employeeScheduleAssertion
            .checkTimeSlotInDatabase(
                employeeScheduleDatabase.getWorkedOutTimeSlot(
                    employeeScheduleActions.workedOutTimeSlot().workedOutTimeSlotId
                ),
                accountContractId = employeeSchedulePreconditions.createWorkedOutTimesheetRequest().accountingContractId,
                profileId = employeeSchedulePreconditions.createWorkedOutTimesheetRequest().profileId,
                timesheetId = employeeSchedulePreconditions.createWorkedOutTimesheetRequest().timesheetId,
                workedOutHours = employeeSchedulePreconditions.createWorkedOutTimesheetRequest().workedOutHours,
                workedOutShiftId = employeeSchedulePreconditions.createWorkedOutTimesheetRequest().workedOutShiftId
            )
            .checkTimeSlotLogInDatabase(
                employeeScheduleDatabase.getWorkedOutTimeSlotLog(employeeScheduleActions.workedOutTimeSlot().workedOutTimeSlotId),
                1,
                index = 0,
                timeEditingReason = null,
                issuerId = employeeSchedulePreconditions.createWorkedOutTimesheetRequest().issuerId,
                version = 1
            )


    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Create worked-out timeslot = 0")
    fun createNullWorkedOutTimeslotTest() {
        val profileId = employeeCommonPreconditions.createProfileDeliveryman().profileId
        val workedOutShiftId = employeeCommonPreconditions.startAndStopShift(profileId).shiftId

        employeeSchedulePreconditions
            .setTimesheetRequest(date = LocalDate.now())
            .setCreateWorkedOutTimeslotRequest(
                timesheetId = employeeScheduleActions.getTimesheet(
                    Constants.darkstoreId,
                    employeeSchedulePreconditions.getTimesheetRequest()
                ).timesheetByDarkstore().timesheetId,
                profileId = profileId,
                workedOutShiftId = workedOutShiftId,
                workedOutHours = 0,
                timeEditingReason = "Случайно закрыл",
                accountingContractId = UUID.randomUUID(),
                issuerId = UUID.randomUUID(),

                )
        employeeScheduleActions
            .createWorkedOutTimeSlot(
                employeeSchedulePreconditions.createWorkedOutTimesheetRequest()
            )
            .getTimeSlotById(employeeScheduleActions.workedOutTimeSlot().workedOutTimeSlotId)

        employeeScheduleAssertion
            .checkTimeSlotInDatabase(
                employeeScheduleDatabase.getWorkedOutTimeSlot(
                    employeeScheduleActions.workedOutTimeSlot().workedOutTimeSlotId
                ),
                accountContractId = employeeSchedulePreconditions.createWorkedOutTimesheetRequest().accountingContractId,
                profileId = employeeSchedulePreconditions.createWorkedOutTimesheetRequest().profileId,
                timesheetId = employeeSchedulePreconditions.createWorkedOutTimesheetRequest().timesheetId,
                workedOutHours = employeeSchedulePreconditions.createWorkedOutTimesheetRequest().workedOutHours,
                workedOutShiftId = employeeSchedulePreconditions.createWorkedOutTimesheetRequest().workedOutShiftId
            )
            .checkTimeSlotLogInDatabase(
                employeeScheduleDatabase.getWorkedOutTimeSlotLog(employeeScheduleActions.workedOutTimeSlot().workedOutTimeSlotId),
                1,
                index = 0,
                timeEditingReason = employeeSchedulePreconditions.createWorkedOutTimesheetRequest().timeEditingReason,
                issuerId = employeeSchedulePreconditions.createWorkedOutTimesheetRequest().issuerId,
                version = 1
            )
            .checkCreatedTimeSlotAPI(
                actualResponse = employeeScheduleActions.timeSlot(),
                expectedRequest = employeeSchedulePreconditions.createWorkedOutTimesheetRequest(),
                version = 1
            )


    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Create worked-out timeslot = 16")
    fun createMaxWorkedOutTimeslotTest() {
        val profileId = employeeCommonPreconditions.createProfileDeliveryman().profileId

        val workedOutShiftId = employeeCommonPreconditions.startAndStopShift(profileId).shiftId

        employeeSchedulePreconditions
            .setTimesheetRequest(date = LocalDate.now())
            .setCreateWorkedOutTimeslotRequest(
                timesheetId = employeeScheduleActions.getTimesheet(
                    Constants.darkstoreId,
                    employeeSchedulePreconditions.getTimesheetRequest()
                ).timesheetByDarkstore().timesheetId,
                profileId = profileId,
                workedOutShiftId = workedOutShiftId,
                workedOutHours = 16,
                timeEditingReason = "Случайно закрыл",
                accountingContractId = UUID.randomUUID(),
                issuerId = UUID.randomUUID(),

                )
        employeeScheduleActions
            .createWorkedOutTimeSlot(
                employeeSchedulePreconditions.createWorkedOutTimesheetRequest()
            )
            .getTimeSlotById(employeeScheduleActions.workedOutTimeSlot().workedOutTimeSlotId)

        employeeScheduleAssertion
            .checkTimeSlotInDatabase(
                employeeScheduleDatabase.getWorkedOutTimeSlot(
                    employeeScheduleActions.workedOutTimeSlot().workedOutTimeSlotId
                ),
                accountContractId = employeeSchedulePreconditions.createWorkedOutTimesheetRequest().accountingContractId,
                profileId = employeeSchedulePreconditions.createWorkedOutTimesheetRequest().profileId,
                timesheetId = employeeSchedulePreconditions.createWorkedOutTimesheetRequest().timesheetId,
                workedOutHours = employeeSchedulePreconditions.createWorkedOutTimesheetRequest().workedOutHours,
                workedOutShiftId = employeeSchedulePreconditions.createWorkedOutTimesheetRequest().workedOutShiftId
            )
            .checkTimeSlotLogInDatabase(
                employeeScheduleDatabase.getWorkedOutTimeSlotLog(employeeScheduleActions.workedOutTimeSlot().workedOutTimeSlotId),
                1,
                index = 0,
                timeEditingReason = employeeSchedulePreconditions.createWorkedOutTimesheetRequest().timeEditingReason,
                issuerId = employeeSchedulePreconditions.createWorkedOutTimesheetRequest().issuerId,
                version = 1
            )

            .checkCreatedTimeSlotAPI(
                actualResponse = employeeScheduleActions.timeSlot(),
                expectedRequest = employeeSchedulePreconditions.createWorkedOutTimesheetRequest(),
                version = 1
            )

    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Create worked-out timeslot > 16 is impossible")
    fun createMoreMaximumWorkedOutTimeslotTest() {

        val profileId = employeeCommonPreconditions.createProfileDeliveryman(vehicle = Vehicle(ApiEnum(EmployeeVehicleType.BICYCLE))).profileId

        val workedOutShiftId = employeeCommonPreconditions.startAndStopShift(profileId).shiftId

        employeeSchedulePreconditions
            .setTimesheetRequest(date = LocalDate.now())
            .setCreateWorkedOutTimeslotRequest(
                timesheetId = employeeScheduleActions.getTimesheet(
                    Constants.darkstoreId,
                    employeeSchedulePreconditions.getTimesheetRequest()
                ).timesheetByDarkstore().timesheetId,
                profileId = profileId,
                workedOutShiftId = workedOutShiftId,
                workedOutHours = 17,
                timeEditingReason = "Случайно закрыл",
                accountingContractId = UUID.randomUUID(),
                issuerId = UUID.randomUUID(),

                )
        employeeScheduleActions
            .createWorkedOutTimeSlotUnsuccessful(
                employeeSchedulePreconditions.createWorkedOutTimesheetRequest()
            )

        employeeScheduleAssertion
            .checkTimeSlotNotInDatabase(employeeScheduleDatabase.checkTimeslotExist(employeeSchedulePreconditions.createWorkedOutTimesheetRequest().profileId))

        commonAssertion
            .checkErrorMessage(
                employeeScheduleActions.createWorkedOutTimeSlotError().message,
                "must be less than or equal to 16"
            )
    }

    @Test
    @DisplayName("Create worked-out timeslot for shift with timeslot is impossible")
    fun createExistedWorkedOutTimeslotTest() {
        val profileId = employeeCommonPreconditions.createProfileDeliveryman(vehicle = Vehicle(ApiEnum(EmployeeVehicleType.ELECTRIC_BICYCLE))).profileId
        val workedOutShiftId1 = employeeCommonPreconditions.startAndStopShift(profileId).shiftId

        employeeSchedulePreconditions
            .setTimesheetRequest(date = LocalDate.now())
            .setListCreateWorkedOutTimeslotRequest(
                2,
                timesheetId = employeeScheduleActions.getTimesheet(
                    Constants.darkstoreId,
                    employeeSchedulePreconditions.getTimesheetRequest()
                ).timesheetByDarkstore().timesheetId,
                listOfProfileId = mutableListOf(profileId, profileId),
                listOfWorkedOutShiftId = mutableListOf(workedOutShiftId1, workedOutShiftId1),
                listOfWorkedOutHours = mutableListOf(6, 5),
                timeEditingReason = "Случайно закрыл",
                listOfAccountingContractId = mutableListOf(
                    UUID.randomUUID(),
                    UUID.randomUUID()
                ),
                issuerId = UUID.randomUUID()
            )
        employeeScheduleActions
            .createWorkedOutTimeSlot(employeeSchedulePreconditions.listOfCreateWorkedOutTimesheetRequest().first())
            .createWorkedOutTimeSlotUnsuccessful(
                employeeSchedulePreconditions.listOfCreateWorkedOutTimesheetRequest().last()
            )

        commonAssertion
            .checkStatusCodeConflict(employeeScheduleActions.createWorkedOutTimeSlotResult().statusCode)
            .checkErrorMessage(
                employeeScheduleActions.createWorkedOutTimeSlotError().message,
                "Concurrent modification has been detected"
            )

    }

    @Test
    @DisplayName("Create worked-out timeslot in submitted timesheet is impossible")
    fun createSubmittedTimesheetWorkedOutTimeslotTest() {
        val profileId = employeeCommonPreconditions.createProfileDeliveryman(darkstoreId = Constants.darkstoreForTimesheet).profileId

        val workedOutShiftId = employeeCommonPreconditions.startAndStopShift(
            profileId,
            darkstoreId = Constants.darkstoreForTimesheet
        ).shiftId

        employeeSchedulePreconditions
            .setTimesheetRequest(date = LocalDate.now())
            .setCreateWorkedOutTimeslotRequest(
                timesheetId = employeeScheduleActions.getTimesheet(
                    darkstoreId = Constants.darkstoreForTimesheet,
                    employeeSchedulePreconditions.getTimesheetRequest()
                ).timesheetByDarkstore().timesheetId,
                profileId = profileId,
                workedOutShiftId = workedOutShiftId,
                workedOutHours = 2,
                timeEditingReason = "Случайно закрыл",
                accountingContractId = UUID.randomUUID(),
                issuerId = UUID.randomUUID(),

                )
        employeeScheduleActions
            .submitTimesheet(employeeSchedulePreconditions.createWorkedOutTimesheetRequest().timesheetId)
            .createWorkedOutTimeSlotUnsuccessful(employeeSchedulePreconditions.createWorkedOutTimesheetRequest())

        employeeScheduleAssertion
            .checkTimeSlotNotInDatabase(employeeScheduleDatabase.checkTimeslotExist(employeeSchedulePreconditions.createWorkedOutTimesheetRequest().profileId))

        commonAssertion
            .checkStatusBadRequest(employeeScheduleActions.createWorkedOutTimeSlotResult().statusCode)
            .checkErrorMessage(
                employeeScheduleActions.createWorkedOutTimeSlotError().message,
                "Timesheet Already Submitted"
            )

        employeeScheduleDatabase.deleteTimesheetById(employeeSchedulePreconditions.createWorkedOutTimesheetRequest().timesheetId)

    }
}