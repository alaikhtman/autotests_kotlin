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
class UpdateWorkedOutTimeslot {

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
    @DisplayName("Update worked-out timeslot deliveryman")
    fun updateDeliverymanWorkedOutTimeSlotTest() {
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
                issuerId = UUID.randomUUID(),

                )
            .setUpdateWorkedOutTimeslotRequest(
                workedOutHours = 5,
                timeEditingReason = "Ошибся",
                accountingContractId = employeeSchedulePreconditions.createWorkedOutTimesheetRequest().accountingContractId
            )
        employeeScheduleActions
            .createWorkedOutTimeSlot(
                employeeSchedulePreconditions.createWorkedOutTimesheetRequest()
            )
            .updateWorkedOutTimeSlot(
                employeeScheduleActions.workedOutTimeSlot().workedOutTimeSlotId,
                employeeSchedulePreconditions.updateWorkedOutTimeSlotRequest()
            )
            .getTimeSlotById(employeeScheduleActions.workedOutTimeSlot().workedOutTimeSlotId)

        employeeScheduleAssertion
            .checkTimeSlotInDatabase(
                employeeScheduleDatabase.getWorkedOutTimeSlot(
                    employeeScheduleActions.workedOutTimeSlot().workedOutTimeSlotId
                ),
                accountContractId = employeeSchedulePreconditions.updateWorkedOutTimeSlotRequest().accountingContractId,
                profileId = employeeSchedulePreconditions.createWorkedOutTimesheetRequest().profileId,
                timesheetId = employeeSchedulePreconditions.createWorkedOutTimesheetRequest().timesheetId,
                workedOutHours = employeeSchedulePreconditions.updateWorkedOutTimeSlotRequest().workedOutHours,
                workedOutShiftId = employeeSchedulePreconditions.createWorkedOutTimesheetRequest().workedOutShiftId
            )
            .checkTimeSlotLogInDatabase(
                employeeScheduleDatabase.getWorkedOutTimeSlotLog(employeeScheduleActions.workedOutTimeSlot().workedOutTimeSlotId),
                2,
                index = 1,
                timeEditingReason = employeeSchedulePreconditions.updateWorkedOutTimeSlotRequest().timeEditingReason,
                issuerId = employeeSchedulePreconditions.updateWorkedOutTimeSlotRequest().issuerId,
                version = 2
            )
            .checkUpdatedTimeSlotAPI(
                actualResponse = employeeScheduleActions.timeSlot(),
                expectedRequest = employeeSchedulePreconditions.updateWorkedOutTimeSlotRequest(),
                version = 2
            )
    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Update worked-out timeslot picker")
    fun updatePickerWorkedOutTimeSlotTest() {
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
                workedOutHours = 2,
                timeEditingReason = "Случайно закрыл",
                accountingContractId = UUID.randomUUID(),
                issuerId = UUID.randomUUID(),

                )
            .setUpdateWorkedOutTimeslotRequest(
                workedOutHours = 5,
                timeEditingReason = "Ошибся",
                accountingContractId = employeeSchedulePreconditions.createWorkedOutTimesheetRequest().accountingContractId
            )
        employeeScheduleActions
            .createWorkedOutTimeSlot(
                employeeSchedulePreconditions.createWorkedOutTimesheetRequest()
            )
            .updateWorkedOutTimeSlot(
                employeeScheduleActions.workedOutTimeSlot().workedOutTimeSlotId,
                employeeSchedulePreconditions.updateWorkedOutTimeSlotRequest()
            )
            .getTimeSlotById(employeeScheduleActions.workedOutTimeSlot().workedOutTimeSlotId)

        employeeScheduleAssertion
            .checkTimeSlotInDatabase(
                employeeScheduleDatabase.getWorkedOutTimeSlot(
                    employeeScheduleActions.workedOutTimeSlot().workedOutTimeSlotId
                ),
                accountContractId = employeeSchedulePreconditions.updateWorkedOutTimeSlotRequest().accountingContractId,
                profileId = employeeSchedulePreconditions.createWorkedOutTimesheetRequest().profileId,
                timesheetId = employeeSchedulePreconditions.createWorkedOutTimesheetRequest().timesheetId,
                workedOutHours = employeeSchedulePreconditions.updateWorkedOutTimeSlotRequest().workedOutHours,
                workedOutShiftId = employeeSchedulePreconditions.createWorkedOutTimesheetRequest().workedOutShiftId
            )
            .checkTimeSlotLogInDatabase(
                employeeScheduleDatabase.getWorkedOutTimeSlotLog(employeeScheduleActions.workedOutTimeSlot().workedOutTimeSlotId),
                2,
                index = 1,
                timeEditingReason = employeeSchedulePreconditions.updateWorkedOutTimeSlotRequest().timeEditingReason,
                issuerId = employeeSchedulePreconditions.updateWorkedOutTimeSlotRequest().issuerId,
                version = 2
            )
            .checkUpdatedTimeSlotAPI(
                actualResponse = employeeScheduleActions.timeSlot(),
                expectedRequest = employeeSchedulePreconditions.updateWorkedOutTimeSlotRequest(),
                version = 2
            )
    }

    @Test
    @DisplayName("Update worked-out timeslot several times")
    fun updateSeveralTimesWorkedOutTimeSlotTest() {
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
                issuerId = UUID.randomUUID(),

                )
            .setListOfUpdateWorkedOutTimeslotRequest(
                listOfWorkedOutHours = mutableListOf(5, 7),
                listOfTimeEditingReason = mutableListOf("Ошибся", "Не учел переработок"),
                listOfAccountingContractId = mutableListOf(
                    employeeSchedulePreconditions.createWorkedOutTimesheetRequest().accountingContractId,
                    employeeSchedulePreconditions.createWorkedOutTimesheetRequest().accountingContractId
                ),
                amount = 2
            )
        employeeScheduleActions
            .createWorkedOutTimeSlot(
                employeeSchedulePreconditions.createWorkedOutTimesheetRequest()
            )
            .updateSeveralWorkedOutTimeSlot(
                employeeScheduleActions.workedOutTimeSlot().workedOutTimeSlotId,
                employeeSchedulePreconditions.listOfUpdateWorkedOutTimesheetRequest()
            )
            .getTimeSlotById(employeeScheduleActions.workedOutTimeSlot().workedOutTimeSlotId)

        employeeScheduleAssertion
            .checkTimeSlotInDatabase(
                employeeScheduleDatabase.getWorkedOutTimeSlot(
                    employeeScheduleActions.workedOutTimeSlot().workedOutTimeSlotId
                ),
                accountContractId = employeeSchedulePreconditions.listOfUpdateWorkedOutTimesheetRequest()
                    .last().accountingContractId,
                profileId = employeeSchedulePreconditions.createWorkedOutTimesheetRequest().profileId,
                timesheetId = employeeSchedulePreconditions.createWorkedOutTimesheetRequest().timesheetId,
                workedOutHours = employeeSchedulePreconditions.listOfUpdateWorkedOutTimesheetRequest()
                    .last().workedOutHours,
                workedOutShiftId = employeeSchedulePreconditions.createWorkedOutTimesheetRequest().workedOutShiftId
            )
            .checkTimeSlotLogInDatabase(
                employeeScheduleDatabase.getWorkedOutTimeSlotLog(employeeScheduleActions.workedOutTimeSlot().workedOutTimeSlotId),
                3,
                index = 2,
                timeEditingReason = employeeSchedulePreconditions.listOfUpdateWorkedOutTimesheetRequest()
                    .last().timeEditingReason,
                issuerId = employeeSchedulePreconditions.listOfUpdateWorkedOutTimesheetRequest().last().issuerId,
                version = 3
            )
            .checkUpdatedTimeSlotAPI(
                actualResponse = employeeScheduleActions.timeSlot(),
                expectedRequest = employeeSchedulePreconditions.listOfUpdateWorkedOutTimesheetRequest().last(),
                version = 3
            )
    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Update worked-out timeslot min time = 0")
    fun updateTimeNullWorkedOutTimeSlotTest() {
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
                issuerId = UUID.randomUUID(),

                )
            .setUpdateWorkedOutTimeslotRequest(
                workedOutHours = 0,
                timeEditingReason = "Ошибся",
                accountingContractId = employeeSchedulePreconditions.createWorkedOutTimesheetRequest().accountingContractId
            )
        employeeScheduleActions
            .createWorkedOutTimeSlot(
                employeeSchedulePreconditions.createWorkedOutTimesheetRequest()
            )
            .updateWorkedOutTimeSlot(
                employeeScheduleActions.workedOutTimeSlot().workedOutTimeSlotId,
                employeeSchedulePreconditions.updateWorkedOutTimeSlotRequest()
            )
            .getTimeSlotById(employeeScheduleActions.workedOutTimeSlot().workedOutTimeSlotId)

        employeeScheduleAssertion
            .checkTimeSlotInDatabase(
                employeeScheduleDatabase.getWorkedOutTimeSlot(
                    employeeScheduleActions.workedOutTimeSlot().workedOutTimeSlotId
                ),
                accountContractId = employeeSchedulePreconditions.updateWorkedOutTimeSlotRequest().accountingContractId,
                profileId = employeeSchedulePreconditions.createWorkedOutTimesheetRequest().profileId,
                timesheetId = employeeSchedulePreconditions.createWorkedOutTimesheetRequest().timesheetId,
                workedOutHours = employeeSchedulePreconditions.updateWorkedOutTimeSlotRequest().workedOutHours,
                workedOutShiftId = employeeSchedulePreconditions.createWorkedOutTimesheetRequest().workedOutShiftId
            )
            .checkTimeSlotLogInDatabase(
                employeeScheduleDatabase.getWorkedOutTimeSlotLog(employeeScheduleActions.workedOutTimeSlot().workedOutTimeSlotId),
                2,
                index = 1,
                timeEditingReason = employeeSchedulePreconditions.updateWorkedOutTimeSlotRequest().timeEditingReason,
                issuerId = employeeSchedulePreconditions.updateWorkedOutTimeSlotRequest().issuerId,
                version = 2
            )
            .checkUpdatedTimeSlotAPI(
                actualResponse = employeeScheduleActions.timeSlot(),
                expectedRequest = employeeSchedulePreconditions.updateWorkedOutTimeSlotRequest(),
                version = 2
            )
    }

    @Test
    @DisplayName("Update worked-out timeslot max time = 16")
    fun updateTimeMaxWorkedOutTimeSlotTest() {
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
                issuerId = UUID.randomUUID(),

                )
            .setUpdateWorkedOutTimeslotRequest(
                workedOutHours = 16,
                timeEditingReason = "Ошибся",
                accountingContractId = employeeSchedulePreconditions.createWorkedOutTimesheetRequest().accountingContractId
            )
        employeeScheduleActions
            .createWorkedOutTimeSlot(
                employeeSchedulePreconditions.createWorkedOutTimesheetRequest()
            )
            .updateWorkedOutTimeSlot(
                employeeScheduleActions.workedOutTimeSlot().workedOutTimeSlotId,
                employeeSchedulePreconditions.updateWorkedOutTimeSlotRequest()
            )
            .getTimeSlotById(employeeScheduleActions.workedOutTimeSlot().workedOutTimeSlotId)

        employeeScheduleAssertion
            .checkTimeSlotInDatabase(
                employeeScheduleDatabase.getWorkedOutTimeSlot(
                    employeeScheduleActions.workedOutTimeSlot().workedOutTimeSlotId
                ),
                accountContractId = employeeSchedulePreconditions.updateWorkedOutTimeSlotRequest().accountingContractId,
                profileId = employeeSchedulePreconditions.createWorkedOutTimesheetRequest().profileId,
                timesheetId = employeeSchedulePreconditions.createWorkedOutTimesheetRequest().timesheetId,
                workedOutHours = employeeSchedulePreconditions.updateWorkedOutTimeSlotRequest().workedOutHours,
                workedOutShiftId = employeeSchedulePreconditions.createWorkedOutTimesheetRequest().workedOutShiftId
            )
            .checkTimeSlotLogInDatabase(
                employeeScheduleDatabase.getWorkedOutTimeSlotLog(employeeScheduleActions.workedOutTimeSlot().workedOutTimeSlotId),
                2,
                index = 1,
                timeEditingReason = employeeSchedulePreconditions.updateWorkedOutTimeSlotRequest().timeEditingReason,
                issuerId = employeeSchedulePreconditions.updateWorkedOutTimeSlotRequest().issuerId,
                version = 2
            )
            .checkUpdatedTimeSlotAPI(
                actualResponse = employeeScheduleActions.timeSlot(),
                expectedRequest = employeeSchedulePreconditions.updateWorkedOutTimeSlotRequest(),
                version = 2
            )
    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Update worked-out timeslot contract")
    fun updateContractWorkedOutTimeSlotTest() {
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
                issuerId = UUID.randomUUID(),

                )
            .setUpdateWorkedOutTimeslotRequest(
                workedOutHours = 2,
                timeEditingReason = "Ошибся",
                accountingContractId = UUID.randomUUID()
            )
        employeeScheduleActions
            .createWorkedOutTimeSlot(
                employeeSchedulePreconditions.createWorkedOutTimesheetRequest()
            )
            .updateWorkedOutTimeSlot(
                employeeScheduleActions.workedOutTimeSlot().workedOutTimeSlotId,
                employeeSchedulePreconditions.updateWorkedOutTimeSlotRequest()
            )
            .getTimeSlotById(employeeScheduleActions.workedOutTimeSlot().workedOutTimeSlotId)

        employeeScheduleAssertion
            .checkTimeSlotInDatabase(
                employeeScheduleDatabase.getWorkedOutTimeSlot(
                    employeeScheduleActions.workedOutTimeSlot().workedOutTimeSlotId
                ),
                accountContractId = employeeSchedulePreconditions.updateWorkedOutTimeSlotRequest().accountingContractId,
                profileId = employeeSchedulePreconditions.createWorkedOutTimesheetRequest().profileId,
                timesheetId = employeeSchedulePreconditions.createWorkedOutTimesheetRequest().timesheetId,
                workedOutHours = employeeSchedulePreconditions.updateWorkedOutTimeSlotRequest().workedOutHours,
                workedOutShiftId = employeeSchedulePreconditions.createWorkedOutTimesheetRequest().workedOutShiftId
            )
            .checkTimeSlotLogInDatabase(
                employeeScheduleDatabase.getWorkedOutTimeSlotLog(employeeScheduleActions.workedOutTimeSlot().workedOutTimeSlotId),
                2,
                index = 1,
                timeEditingReason = employeeSchedulePreconditions.updateWorkedOutTimeSlotRequest().timeEditingReason,
                issuerId = employeeSchedulePreconditions.updateWorkedOutTimeSlotRequest().issuerId,
                version = 2
            )
            .checkUpdatedTimeSlotAPI(
                actualResponse = employeeScheduleActions.timeSlot(),
                expectedRequest = employeeSchedulePreconditions.updateWorkedOutTimeSlotRequest(),
                version = 2
            )
    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Update worked-out timeslot contract w/t reason")
    fun updateContractWithoutReasonWorkedOutTimeSlotTest() {
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
                timeEditingReason = "Случайно",
                accountingContractId = UUID.randomUUID(),
                issuerId = UUID.randomUUID(),

                )
            .setUpdateWorkedOutTimeslotRequest(
                workedOutHours = 2,
                timeEditingReason = null,
                accountingContractId = UUID.randomUUID()
            )
        employeeScheduleActions
            .createWorkedOutTimeSlot(
                employeeSchedulePreconditions.createWorkedOutTimesheetRequest()
            )
            .updateWorkedOutTimeSlot(
                employeeScheduleActions.workedOutTimeSlot().workedOutTimeSlotId,
                employeeSchedulePreconditions.updateWorkedOutTimeSlotRequest()
            )
            .getTimeSlotById(employeeScheduleActions.workedOutTimeSlot().workedOutTimeSlotId)

        employeeScheduleAssertion
            .checkTimeSlotInDatabase(
                employeeScheduleDatabase.getWorkedOutTimeSlot(
                    employeeScheduleActions.workedOutTimeSlot().workedOutTimeSlotId
                ),
                accountContractId = employeeSchedulePreconditions.updateWorkedOutTimeSlotRequest().accountingContractId,
                profileId = employeeSchedulePreconditions.createWorkedOutTimesheetRequest().profileId,
                timesheetId = employeeSchedulePreconditions.createWorkedOutTimesheetRequest().timesheetId,
                workedOutHours = employeeSchedulePreconditions.updateWorkedOutTimeSlotRequest().workedOutHours,
                workedOutShiftId = employeeSchedulePreconditions.createWorkedOutTimesheetRequest().workedOutShiftId
            )
            .checkTimeSlotLogInDatabase(
                employeeScheduleDatabase.getWorkedOutTimeSlotLog(employeeScheduleActions.workedOutTimeSlot().workedOutTimeSlotId),
                2,
                index = 1,
                timeEditingReason = employeeSchedulePreconditions.updateWorkedOutTimeSlotRequest().timeEditingReason,
                issuerId = employeeSchedulePreconditions.updateWorkedOutTimeSlotRequest().issuerId,
                version = 2
            )
            .checkUpdatedTimeSlotAPI(
                actualResponse = employeeScheduleActions.timeSlot(),
                expectedRequest = employeeSchedulePreconditions.updateWorkedOutTimeSlotRequest(),
                version = 2
            )
    }

    @Test
    @DisplayName("Update worked-out timeslot time and contract")
    fun updateTimeAndContractWorkedOutTimeSlotTest() {
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
                issuerId = UUID.randomUUID(),

                )
            .setUpdateWorkedOutTimeslotRequest(
                workedOutHours = 12,
                timeEditingReason = "Ошибся",
                accountingContractId = UUID.randomUUID()
            )
        employeeScheduleActions
            .createWorkedOutTimeSlot(
                employeeSchedulePreconditions.createWorkedOutTimesheetRequest()
            )
            .updateWorkedOutTimeSlot(
                employeeScheduleActions.workedOutTimeSlot().workedOutTimeSlotId,
                employeeSchedulePreconditions.updateWorkedOutTimeSlotRequest()
            )
            .getTimeSlotById(employeeScheduleActions.workedOutTimeSlot().workedOutTimeSlotId)

        employeeScheduleAssertion
            .checkTimeSlotInDatabase(
                employeeScheduleDatabase.getWorkedOutTimeSlot(
                    employeeScheduleActions.workedOutTimeSlot().workedOutTimeSlotId
                ),
                accountContractId = employeeSchedulePreconditions.updateWorkedOutTimeSlotRequest().accountingContractId,
                profileId = employeeSchedulePreconditions.createWorkedOutTimesheetRequest().profileId,
                timesheetId = employeeSchedulePreconditions.createWorkedOutTimesheetRequest().timesheetId,
                workedOutHours = employeeSchedulePreconditions.updateWorkedOutTimeSlotRequest().workedOutHours,
                workedOutShiftId = employeeSchedulePreconditions.createWorkedOutTimesheetRequest().workedOutShiftId
            )
            .checkTimeSlotLogInDatabase(
                employeeScheduleDatabase.getWorkedOutTimeSlotLog(employeeScheduleActions.workedOutTimeSlot().workedOutTimeSlotId),
                2,
                index = 1,
                timeEditingReason = employeeSchedulePreconditions.updateWorkedOutTimeSlotRequest().timeEditingReason,
                issuerId = employeeSchedulePreconditions.updateWorkedOutTimeSlotRequest().issuerId,
                version = 2
            )
            .checkUpdatedTimeSlotAPI(
                actualResponse = employeeScheduleActions.timeSlot(),
                expectedRequest = employeeSchedulePreconditions.updateWorkedOutTimeSlotRequest(),
                version = 2
            )
    }

    @Test
    @DisplayName("Update worked-out timeslot max time > 16 impossible")
    fun updateTimeInvalidWorkedOutTimeSlotTest() {

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
                workedOutHours = 7,
                timeEditingReason = "Случайно закрыл",
                accountingContractId = UUID.randomUUID(),
                issuerId = UUID.randomUUID()
                )
            .setUpdateWorkedOutTimeslotRequest(
                workedOutHours = 17,
                timeEditingReason = "Ошибся",
                accountingContractId = employeeSchedulePreconditions.createWorkedOutTimesheetRequest().accountingContractId
            )

        employeeScheduleActions
            .createWorkedOutTimeSlot(
                employeeSchedulePreconditions.createWorkedOutTimesheetRequest()
            )
            .updateWorkedOutTimeSlotUnsuccessful(
                employeeScheduleActions.workedOutTimeSlot().workedOutTimeSlotId,
                employeeSchedulePreconditions.updateWorkedOutTimeSlotRequest()
            )

        commonAssertion
            .checkErrorMessage(
                employeeScheduleActions.updateWorkedOutTimeSlotError().message,
                "must be less than or equal to 16"
            )
    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Update time in worked-out timeslot without reason impossible")
    fun updateWithoutReasonWorkedOutTimeSlotTest() {

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
                workedOutHours = 7,
                timeEditingReason = "Случайно закрыл",
                accountingContractId = UUID.randomUUID(),
                issuerId = UUID.randomUUID(),

                )
            .setUpdateWorkedOutTimeslotRequest(
                workedOutHours = 3,
                timeEditingReason = null,
                accountingContractId = employeeSchedulePreconditions.createWorkedOutTimesheetRequest().accountingContractId
            )

        employeeScheduleActions
            .createWorkedOutTimeSlot(
                employeeSchedulePreconditions.createWorkedOutTimesheetRequest()
            )
            .updateWorkedOutTimeSlotUnsuccessful(
                employeeScheduleActions.workedOutTimeSlot().workedOutTimeSlotId,
                employeeSchedulePreconditions.updateWorkedOutTimeSlotRequest()
            )

        commonAssertion
            .checkErrorMessage(
                employeeScheduleActions.updateWorkedOutTimeSlotError().message,
                "Reason Mandatory For Worked Hours Changes"
            )
    }

    @Test
    @DisplayName("Update worked-out timeslot in submitted timesheet impossible")
    fun updateSubmittedTimesheetWorkedOutTimeSlotTest() {
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
            .setUpdateWorkedOutTimeslotRequest(
                workedOutHours = 5,
                timeEditingReason = "Ошибся",
                accountingContractId = employeeSchedulePreconditions.createWorkedOutTimesheetRequest().accountingContractId
            )
        employeeScheduleActions
            .createWorkedOutTimeSlot(employeeSchedulePreconditions.createWorkedOutTimesheetRequest())
            .submitTimesheet(employeeSchedulePreconditions.createWorkedOutTimesheetRequest().timesheetId)
            .updateWorkedOutTimeSlotUnsuccessful(
                employeeScheduleActions.workedOutTimeSlot().workedOutTimeSlotId,
                employeeSchedulePreconditions.updateWorkedOutTimeSlotRequest()
            )

        commonAssertion
            .checkStatusBadRequest(employeeScheduleActions.updateWorkedOutTimeSlotResult().statusCode)
            .checkErrorMessage(
                employeeScheduleActions.updateWorkedOutTimeSlotError().message,
                "Timesheet Already Submitted"
            )

        employeeScheduleDatabase.deleteTimesheetById(employeeSchedulePreconditions.createWorkedOutTimesheetRequest().timesheetId)
    }


}