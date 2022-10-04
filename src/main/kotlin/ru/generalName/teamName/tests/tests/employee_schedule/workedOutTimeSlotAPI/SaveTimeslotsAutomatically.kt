package ru.samokat.mysamokat.tests.tests.employee_schedule.workedOutTimeSlotAPI

import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.samokat.employeeprofiles.api.common.domain.EmployeeVehicleType
import ru.samokat.employeeprofiles.api.common.domain.Vehicle
import ru.samokat.employeeschedule.api.timesheet.TimesheetStatus
import ru.samokat.my.domain.employee.EmployeeRole
import ru.samokat.my.rest.api.enum.ApiEnum
import ru.samokat.mysamokat.tests.checkers.CommonAssertion
import ru.samokat.mysamokat.tests.checkers.EmployeeScheduleAssertion
import ru.samokat.mysamokat.tests.dataproviders.Constants
import ru.samokat.mysamokat.tests.dataproviders.preconditions.CommonPreconditions
import ru.samokat.mysamokat.tests.dataproviders.preconditions.EmployeePreconditions
import ru.samokat.mysamokat.tests.dataproviders.preconditions.EmployeeSchedulePreconditions
import ru.samokat.mysamokat.tests.helpers.actions.EmployeeActions
import ru.samokat.mysamokat.tests.helpers.actions.EmployeeScheduleActions
import ru.samokat.mysamokat.tests.helpers.controllers.database.EmployeeScheduleDatabaseController
import ru.samokat.shifts.api.common.domain.ShiftUserRole
import java.time.Instant
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.*


@SpringBootTest
@Tags(Tag("emproSchedule"), Tag("shifts_integration"))
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class SaveTimeslotsAutomatically {

    private lateinit var employeeScheduleAssertion: EmployeeScheduleAssertion
    private lateinit var commonAssertion: CommonAssertion

    @Autowired
    private lateinit var employeeActions: EmployeeActions

    @Autowired
    private lateinit var employeeCommonPreconditions: CommonPreconditions


    private lateinit var employeePreconditions: EmployeePreconditions

    private lateinit var employeeSchedulePreconditions: EmployeeSchedulePreconditions

    @Autowired
    private lateinit var employeeScheduleActions: EmployeeScheduleActions

    @Autowired
    private lateinit var employeeScheduleDatabase: EmployeeScheduleDatabaseController


    @BeforeEach
    fun before() {
        employeeSchedulePreconditions = EmployeeSchedulePreconditions()
        employeePreconditions = EmployeePreconditions()
        employeeScheduleAssertion = EmployeeScheduleAssertion()
        commonAssertion = CommonAssertion()
        employeeActions.deleteProfile(Constants.mobile1)
        employeeScheduleActions.deleteTimesheetInDB(darkstoreId = Constants.darkstoreId, date = LocalDate.now())
        employeeScheduleActions.deleteTimesheetInDB(
            darkstoreId = Constants.darkstoreForTimesheet,
            date = LocalDate.now()
        )

    }

    @AfterEach
    fun release() {
        commonAssertion.assertAll()
        employeeScheduleAssertion.assertAll()
        employeeActions.deleteProfile(Constants.mobile1)
        employeeScheduleActions.deleteTimesheetInDB(darkstoreId = Constants.darkstoreId, date = LocalDate.now())
        employeeScheduleActions.deleteTimesheetInDB(
            darkstoreId = Constants.darkstoreForTimesheet,
            date = LocalDate.now()
        )
    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Save deliveryman worked-out timeslot automatically")
    fun saveWorkedOutTimeslotDeliverymanAutomaticallyTest() {

        val profileId =
            employeeCommonPreconditions.createProfileDeliveryman(accountingProfileId = Constants.accountingProfileIdForTimesheet1).profileId
        val workedOutShiftId = employeeCommonPreconditions.startAndStopUpdatedShift(
            profileId, newDate = Instant.now().minusSeconds(10800).truncatedTo(
                ChronoUnit.NANOS
            )
        ).shiftId

        employeeSchedulePreconditions
            .setTimesheetRequest(date = LocalDate.now())

        employeeScheduleActions
            .getTimeSlotById(employeeScheduleActions.getTimeSlotInBD(workedOutShiftId))
            .getTimesheet(
                employeeCommonPreconditions.createProfileRequest().darkstoreId!!,
                employeeSchedulePreconditions.getTimesheetRequest()
            )

        employeeScheduleAssertion
            .checkTimeSlotInDatabase(
                employeeScheduleDatabase.getWorkedOutTimeSlot(
                    employeeScheduleActions.timesheetByDarkstore().workedOutTimeSlots.first().workedOutTimeSlotId
                ),
                accountContractId = UUID.fromString(employeeActions.getContractIdFromDB(Constants.accountingProfileIdForTimesheet1)),
                profileId = profileId,
                timesheetId = employeeScheduleActions.timesheetByDarkstore().timesheetId,
                workedOutHours = 3,
                workedOutShiftId = workedOutShiftId
            )
            .checkTimeSlotLogInDatabase(
                employeeScheduleDatabase.getWorkedOutTimeSlotLog(
                    employeeScheduleActions.timesheetByDarkstore().workedOutTimeSlots.first().workedOutTimeSlotId
                ),
                1,
                index = 0,
                timeEditingReason = null,
                issuerId = Constants.absentIssuerId,
                version = 1
            )
            .checkAutoCreatedTimeSlotAPI(
                actualResponse = employeeScheduleActions.timeSlot(),
                accountContractId = UUID.fromString(employeeActions.getContractIdFromDB(Constants.accountingProfileIdForTimesheet1)),
                profileId = profileId,
                timesheetId = employeeScheduleActions.timesheetByDarkstore().timesheetId,
                workedOutHours = 3,
                workedOutShiftId = workedOutShiftId,
                version = 1
            )

    }

    @Test
    @DisplayName("Save picker worked-out timeslot automatically")
    fun saveWorkedOutTimeslotPickerAutomaticallyTest() {
        val profileId =
            employeeCommonPreconditions.createProfilePicker(accountingProfileId = Constants.accountingProfileIdForTimesheet1).profileId

        val workedOutShiftId = employeeCommonPreconditions.startAndStopUpdatedShift(
            profileId, userRole = ShiftUserRole.PICKER, newDate = Instant.now().minusSeconds(10800).truncatedTo(
                ChronoUnit.NANOS
            )
        ).shiftId

        employeeSchedulePreconditions
            .setTimesheetRequest(date = LocalDate.now())

        employeeScheduleActions
            .getTimeSlotById(employeeScheduleActions.getTimeSlotInBD(workedOutShiftId))
            .getTimesheet(
                employeeCommonPreconditions.createProfileRequest().darkstoreId!!,
                employeeSchedulePreconditions.getTimesheetRequest()
            )

        employeeScheduleAssertion
            .checkTimeSlotInDatabase(
                employeeScheduleDatabase.getWorkedOutTimeSlot(
                    employeeScheduleActions.timesheetByDarkstore().workedOutTimeSlots.first().workedOutTimeSlotId
                ),
                accountContractId = UUID.fromString(employeeActions.getContractIdFromDB(Constants.accountingProfileIdForTimesheet1)),
                profileId = profileId,
                timesheetId = employeeScheduleActions.timesheetByDarkstore().timesheetId,
                workedOutHours = 3,
                workedOutShiftId = workedOutShiftId
            )
            .checkTimeSlotLogInDatabase(
                employeeScheduleDatabase.getWorkedOutTimeSlotLog(
                    employeeScheduleActions.timesheetByDarkstore().workedOutTimeSlots.first().workedOutTimeSlotId
                ),
                1,
                index = 0,
                timeEditingReason = null,
                issuerId = Constants.absentIssuerId,
                version = 1
            )
            .checkAutoCreatedTimeSlotAPI(
                actualResponse = employeeScheduleActions.timeSlot(),
                accountContractId = UUID.fromString(employeeActions.getContractIdFromDB(Constants.accountingProfileIdForTimesheet1)),
                profileId = profileId,
                timesheetId = employeeScheduleActions.timesheetByDarkstore().timesheetId,
                workedOutHours = 3,
                workedOutShiftId = workedOutShiftId,
                version = 1
            )

    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Save worked-out timeslot at other darkstore automatically")
    fun saveWorkedOutTimeslotDarkstoreAutomaticallyTest() {
        val profileId =
            employeeCommonPreconditions.createProfileDeliveryman(accountingProfileId = Constants.accountingProfileIdForTimesheet1).profileId
        val workedOutShiftId = employeeCommonPreconditions.startAndStopUpdatedShift(
            profileId,
            darkstoreId = Constants.darkstoreForTimesheet,
            newDate = Instant.now().minusSeconds(10800).truncatedTo(
                ChronoUnit.NANOS
            )
        ).shiftId

        employeeSchedulePreconditions
            .setTimesheetRequest(date = LocalDate.now())

        employeeScheduleActions
            .getTimeSlotById(employeeScheduleActions.getTimeSlotInBD(workedOutShiftId))
            .getTimesheet(
                Constants.darkstoreForTimesheet,
                employeeSchedulePreconditions.getTimesheetRequest()
            )
        employeeScheduleAssertion
            .checkTimeSlotInDatabase(
                employeeScheduleDatabase.getWorkedOutTimeSlot(
                    employeeScheduleActions.timesheetByDarkstore().workedOutTimeSlots.first().workedOutTimeSlotId
                ),
                accountContractId = UUID.fromString(employeeActions.getContractIdFromDB(Constants.accountingProfileIdForTimesheet1)),
                profileId = profileId,
                timesheetId = employeeScheduleActions.timesheetByDarkstore().timesheetId,
                workedOutHours = 3,
                workedOutShiftId = workedOutShiftId
            )
            .checkTimeSlotLogInDatabase(
                employeeScheduleDatabase.getWorkedOutTimeSlotLog(
                    employeeScheduleActions.timesheetByDarkstore().workedOutTimeSlots.first().workedOutTimeSlotId
                ),
                1,
                index = 0,
                timeEditingReason = null,
                issuerId = Constants.absentIssuerId,
                version = 1
            )
            .checkAutoCreatedTimeSlotAPI(
                actualResponse = employeeScheduleActions.timeSlot(),
                accountContractId = UUID.fromString(employeeActions.getContractIdFromDB(Constants.accountingProfileIdForTimesheet1)),
                profileId = profileId,
                timesheetId = employeeScheduleActions.timesheetByDarkstore().timesheetId,
                workedOutHours = 3,
                workedOutShiftId = workedOutShiftId,
                version = 1
            )
    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Save worked-out timeslot deliveryman-picker as picker automatically")
    fun saveWorkedOutTimeslotAsPickerAutomaticallyTest() {
        val profileId =
            employeeCommonPreconditions.createProfileDeliverymanPicker(accountingProfileId = Constants.accountingProfileIdForTimesheet1).profileId

        val workedOutShiftId = employeeCommonPreconditions.startAndStopUpdatedShift(
            profileId, userRole = ShiftUserRole.PICKER, newDate = Instant.now().minusSeconds(10800).truncatedTo(
                ChronoUnit.NANOS
            )
        ).shiftId

        employeeSchedulePreconditions
            .setTimesheetRequest(date = LocalDate.now())

        employeeScheduleActions
            .getTimeSlotById(employeeScheduleActions.getTimeSlotInBD(workedOutShiftId))
            .getTimesheet(
                employeeCommonPreconditions.createProfileRequest().darkstoreId!!,
                employeeSchedulePreconditions.getTimesheetRequest()
            )

        employeeScheduleAssertion
            .checkTimeSlotInDatabase(
                employeeScheduleDatabase.getWorkedOutTimeSlot(
                    employeeScheduleActions.timesheetByDarkstore().workedOutTimeSlots.first().workedOutTimeSlotId
                ),
                accountContractId = UUID.fromString(employeeActions.getContractIdFromDB(Constants.accountingProfileIdForTimesheet1)),
                profileId = profileId,
                timesheetId = employeeScheduleActions.timesheetByDarkstore().timesheetId,
                workedOutHours = 3,
                workedOutShiftId = workedOutShiftId
            )
            .checkTimeSlotLogInDatabase(
                employeeScheduleDatabase.getWorkedOutTimeSlotLog(
                    employeeScheduleActions.timesheetByDarkstore().workedOutTimeSlots.first().workedOutTimeSlotId
                ),
                1,
                index = 0,
                timeEditingReason = null,
                issuerId = Constants.absentIssuerId,
                version = 1
            )
            .checkAutoCreatedTimeSlotAPI(
                actualResponse = employeeScheduleActions.timeSlot(),
                accountContractId = UUID.fromString(employeeActions.getContractIdFromDB(Constants.accountingProfileIdForTimesheet1)),
                profileId = profileId,
                timesheetId = employeeScheduleActions.timesheetByDarkstore().timesheetId,
                workedOutHours = 3,
                workedOutShiftId = workedOutShiftId,
                version = 1
            )

    }

    @Test
    @DisplayName("Save worked-out timeslot deliveryman-picker as deliveryman automatically")
    fun saveWorkedOutTimeslotAsDeliverymanAutomaticallyTest() {
        val profileId =
            employeeCommonPreconditions.createProfileDeliverymanPicker(accountingProfileId = Constants.accountingProfileIdForTimesheet1).profileId

        val workedOutShiftId = employeeCommonPreconditions.startAndStopUpdatedShift(
            profileId, userRole = ShiftUserRole.DELIVERYMAN, newDate = Instant.now().minusSeconds(10800).truncatedTo(
                ChronoUnit.NANOS
            )
        ).shiftId

        employeeSchedulePreconditions
            .setTimesheetRequest(date = LocalDate.now())

        employeeScheduleActions
            .getTimeSlotById(employeeScheduleActions.getTimeSlotInBD(workedOutShiftId))
            .getTimesheet(
                employeeCommonPreconditions.createProfileRequest().darkstoreId!!,
                employeeSchedulePreconditions.getTimesheetRequest()
            )

        employeeScheduleAssertion
            .checkTimeSlotInDatabase(
                employeeScheduleDatabase.getWorkedOutTimeSlot(
                    employeeScheduleActions.timesheetByDarkstore().workedOutTimeSlots.first().workedOutTimeSlotId
                ),
                accountContractId = UUID.fromString(employeeActions.getContractIdFromDB(Constants.accountingProfileIdForTimesheet1)),
                profileId = profileId,
                timesheetId = employeeScheduleActions.timesheetByDarkstore().timesheetId,
                workedOutHours = 3,
                workedOutShiftId = workedOutShiftId
            )
            .checkTimeSlotLogInDatabase(
                employeeScheduleDatabase.getWorkedOutTimeSlotLog(
                    employeeScheduleActions.timesheetByDarkstore().workedOutTimeSlots.first().workedOutTimeSlotId
                ),
                1,
                index = 0,
                timeEditingReason = null,
                issuerId = Constants.absentIssuerId,
                version = 1
            )
            .checkAutoCreatedTimeSlotAPI(
                actualResponse = employeeScheduleActions.timeSlot(),
                accountContractId = UUID.fromString(employeeActions.getContractIdFromDB(Constants.accountingProfileIdForTimesheet1)),
                profileId = profileId,
                timesheetId = employeeScheduleActions.timesheetByDarkstore().timesheetId,
                workedOutHours = 3,
                workedOutShiftId = workedOutShiftId,
                version = 1
            )

    }

    @Test
    @Tags(Tag("ignore"))
    @DisplayName("Save worked-out timeslot = 16 automatically")
    fun saveMaxTimeWorkedOutTimeslotAutomaticallyTest() {
        val profileId =
            employeeCommonPreconditions.createProfileDeliveryman(accountingProfileId = Constants.accountingProfileIdForTimesheet1).profileId

        val workedOutShiftId = employeeCommonPreconditions.startAndStopUpdatedShift(
            profileId, userRole = ShiftUserRole.DELIVERYMAN, newDate = Instant.now().minusSeconds(57600).truncatedTo(
                ChronoUnit.NANOS
            )
        ).shiftId

        employeeSchedulePreconditions
            .setTimesheetRequest(date = LocalDate.now())

        employeeScheduleActions
            .getTimeSlotById(employeeScheduleActions.getTimeSlotInBD(workedOutShiftId))
            .getTimesheet(
                employeeCommonPreconditions.createProfileRequest().darkstoreId!!,
                employeeSchedulePreconditions.getTimesheetRequest()
            )

        employeeScheduleAssertion
            .checkTimeSlotInDatabase(
                employeeScheduleDatabase.getWorkedOutTimeSlot(
                    employeeScheduleActions.timesheetByDarkstore().workedOutTimeSlots.first().workedOutTimeSlotId
                ),
                accountContractId = UUID.fromString(employeeActions.getContractIdFromDB(Constants.accountingProfileIdForTimesheet1)),
                profileId = profileId,
                timesheetId = employeeScheduleActions.timesheetByDarkstore().timesheetId,
                workedOutHours = 16,
                workedOutShiftId = workedOutShiftId
            )
            .checkTimeSlotLogInDatabase(
                employeeScheduleDatabase.getWorkedOutTimeSlotLog(
                    employeeScheduleActions.timesheetByDarkstore().workedOutTimeSlots.first().workedOutTimeSlotId
                ),
                1,
                index = 0,
                timeEditingReason = null,
                issuerId = Constants.absentIssuerId,
                version = 1
            )
            .checkAutoCreatedTimeSlotAPI(
                actualResponse = employeeScheduleActions.timeSlot(),
                accountContractId = UUID.fromString(employeeActions.getContractIdFromDB(Constants.accountingProfileIdForTimesheet1)),
                profileId = profileId,
                timesheetId = employeeScheduleActions.timesheetByDarkstore().timesheetId,
                workedOutHours = 16,
                workedOutShiftId = workedOutShiftId,
                version = 1
            )

    }

    @Test
    @Tags(Tag("ignore"))
    @DisplayName("Save worked-out timeslot > 16 automatically")
    fun saveMoreThanMaxTimeWorkedOutTimeslotAutomaticallyTest() {
        val profileId =
            employeeCommonPreconditions.createProfileDeliveryman(accountingProfileId = Constants.accountingProfileIdForTimesheet1).profileId

        val workedOutShiftId = employeeCommonPreconditions.startAndStopUpdatedShift(
            profileId, userRole = ShiftUserRole.DELIVERYMAN, newDate = Instant.now().minusSeconds(57660).truncatedTo(
                ChronoUnit.NANOS
            )
        ).shiftId

        employeeSchedulePreconditions
            .setTimesheetRequest(date = LocalDate.now())

        employeeScheduleActions
            .getTimeSlotById(employeeScheduleActions.getTimeSlotInBD(workedOutShiftId))
            .getTimesheet(
                employeeCommonPreconditions.createProfileRequest().darkstoreId!!,
                employeeSchedulePreconditions.getTimesheetRequest()
            )

        employeeScheduleAssertion
            .checkTimeSlotInDatabase(
                employeeScheduleDatabase.getWorkedOutTimeSlot(
                    employeeScheduleActions.timesheetByDarkstore().workedOutTimeSlots.first().workedOutTimeSlotId
                ),
                accountContractId = UUID.fromString(employeeActions.getContractIdFromDB(Constants.accountingProfileIdForTimesheet1)),
                profileId = profileId,
                timesheetId = employeeScheduleActions.timesheetByDarkstore().timesheetId,
                workedOutHours = 16,
                workedOutShiftId = workedOutShiftId
            )
            .checkTimeSlotLogInDatabase(
                employeeScheduleDatabase.getWorkedOutTimeSlotLog(
                    employeeScheduleActions.timesheetByDarkstore().workedOutTimeSlots.first().workedOutTimeSlotId
                ),
                1,
                index = 0,
                timeEditingReason = null,
                issuerId = Constants.absentIssuerId,
                version = 1
            )
            .checkAutoCreatedTimeSlotAPI(
                actualResponse = employeeScheduleActions.timeSlot(),
                accountContractId = UUID.fromString(employeeActions.getContractIdFromDB(Constants.accountingProfileIdForTimesheet1)),
                profileId = profileId,
                timesheetId = employeeScheduleActions.timesheetByDarkstore().timesheetId,
                workedOutHours = 16,
                workedOutShiftId = workedOutShiftId,
                version = 1
            )
    }


    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Save worked-out timeslot = 1 hour 29 min automatically")
    fun saveRoundingWorkedOutTimeslotAutomaticallyTest() {
        val profileId =
            employeeCommonPreconditions.createProfileDeliveryman(accountingProfileId = Constants.accountingProfileIdForTimesheet1).profileId

        val workedOutShiftId = employeeCommonPreconditions.startAndStopUpdatedShift(
            profileId, userRole = ShiftUserRole.DELIVERYMAN, newDate = Instant.now().minusSeconds(5340).truncatedTo(
                ChronoUnit.NANOS
            )
        ).shiftId

        employeeSchedulePreconditions
            .setTimesheetRequest(date = LocalDate.now())

        employeeScheduleActions
            .getTimeSlotById(employeeScheduleActions.getTimeSlotInBD(workedOutShiftId))
            .getTimesheet(
                darkstoreId = employeeCommonPreconditions.createProfileRequest().darkstoreId!!,
                employeeSchedulePreconditions.getTimesheetRequest()
            )

        employeeScheduleAssertion
            .checkTimeSlotInDatabase(
                employeeScheduleDatabase.getWorkedOutTimeSlot(
                    employeeScheduleActions.timesheetByDarkstore().workedOutTimeSlots.first().workedOutTimeSlotId
                ),
                accountContractId = UUID.fromString(employeeActions.getContractIdFromDB(Constants.accountingProfileIdForTimesheet1)),
                profileId = profileId,
                timesheetId = employeeScheduleActions.timesheetByDarkstore().timesheetId,
                workedOutHours = 1,
                workedOutShiftId = workedOutShiftId
            )
            .checkTimeSlotLogInDatabase(
                employeeScheduleDatabase.getWorkedOutTimeSlotLog(
                    employeeScheduleActions.timesheetByDarkstore().workedOutTimeSlots.first().workedOutTimeSlotId
                ),
                1,
                index = 0,
                timeEditingReason = null,
                issuerId = Constants.absentIssuerId,
                version = 1
            )
            .checkAutoCreatedTimeSlotAPI(
                actualResponse = employeeScheduleActions.timeSlot(),
                accountContractId = UUID.fromString(employeeActions.getContractIdFromDB(Constants.accountingProfileIdForTimesheet1)),
                profileId = profileId,
                timesheetId = employeeScheduleActions.timesheetByDarkstore().timesheetId,
                workedOutHours = 1,
                workedOutShiftId = workedOutShiftId,
                version = 1
            )
    }

    @Test
    @DisplayName("Save worked-out timeslot =30 min automatically")
    fun saveMinTimeWorkedOutTimeslotAutomaticallyTest() {
        val profileId =
            employeeCommonPreconditions.createProfileDeliveryman(accountingProfileId = Constants.accountingProfileIdForTimesheet1).profileId

        val workedOutShiftId = employeeCommonPreconditions.startAndStopUpdatedShift(
            profileId, userRole = ShiftUserRole.DELIVERYMAN, newDate = Instant.now().minusSeconds(1805).truncatedTo(
                ChronoUnit.NANOS
            )
        ).shiftId

        employeeSchedulePreconditions
            .setTimesheetRequest(date = LocalDate.now())

        employeeScheduleActions
            .getTimeSlotById(employeeScheduleActions.getTimeSlotInBD(workedOutShiftId))
            .getTimesheet(
                employeeCommonPreconditions.createProfileRequest().darkstoreId!!,
                employeeSchedulePreconditions.getTimesheetRequest()
            )

        employeeScheduleAssertion
            .checkTimeSlotInDatabase(
                employeeScheduleDatabase.getWorkedOutTimeSlot(
                    employeeScheduleActions.timesheetByDarkstore().workedOutTimeSlots.first().workedOutTimeSlotId
                ),
                accountContractId = UUID.fromString(employeeActions.getContractIdFromDB(Constants.accountingProfileIdForTimesheet1)),
                profileId = profileId,
                timesheetId = employeeScheduleActions.timesheetByDarkstore().timesheetId,
                workedOutHours = 1,
                workedOutShiftId = workedOutShiftId
            )
            .checkTimeSlotLogInDatabase(
                employeeScheduleDatabase.getWorkedOutTimeSlotLog(
                    employeeScheduleActions.timesheetByDarkstore().workedOutTimeSlots.first().workedOutTimeSlotId
                ),
                1,
                index = 0,
                timeEditingReason = null,
                issuerId = Constants.absentIssuerId,
                version = 1
            )
            .checkAutoCreatedTimeSlotAPI(
                actualResponse = employeeScheduleActions.timeSlot(),
                accountContractId = UUID.fromString(employeeActions.getContractIdFromDB(Constants.accountingProfileIdForTimesheet1)),
                profileId = profileId,
                timesheetId = employeeScheduleActions.timesheetByDarkstore().timesheetId,
                workedOutHours = 1,
                workedOutShiftId = workedOutShiftId,
                version = 1
            )
    }

    @Test
    @DisplayName("Save worked-out timeslot >30 min automatically")
    fun saveOneHourWorkedOutTimeslotAutomaticallyTest() {
        val profileId =
            employeeCommonPreconditions.createProfileDeliveryman(accountingProfileId = Constants.accountingProfileIdForTimesheet1).profileId

        val workedOutShiftId = employeeCommonPreconditions.startAndStopUpdatedShift(
            profileId, userRole = ShiftUserRole.DELIVERYMAN, newDate = Instant.now().minusSeconds(1920).truncatedTo(
                ChronoUnit.NANOS
            )
        ).shiftId

        employeeSchedulePreconditions
            .setTimesheetRequest(date = LocalDate.now())

        employeeScheduleActions
            .getTimeSlotById(employeeScheduleActions.getTimeSlotInBD(workedOutShiftId))
            .getTimesheet(
                employeeCommonPreconditions.createProfileRequest().darkstoreId!!,
                employeeSchedulePreconditions.getTimesheetRequest()
            )


        employeeScheduleAssertion
            .checkTimeSlotInDatabase(
                employeeScheduleDatabase.getWorkedOutTimeSlot(
                    employeeScheduleActions.timesheetByDarkstore().workedOutTimeSlots.first().workedOutTimeSlotId
                ),
                accountContractId = UUID.fromString(employeeActions.getContractIdFromDB(Constants.accountingProfileIdForTimesheet1)),
                profileId = profileId,
                timesheetId = employeeScheduleActions.timesheetByDarkstore().timesheetId,
                workedOutHours = 1,
                workedOutShiftId = workedOutShiftId
            )
            .checkTimeSlotLogInDatabase(
                employeeScheduleDatabase.getWorkedOutTimeSlotLog(
                    employeeScheduleActions.timesheetByDarkstore().workedOutTimeSlots.first().workedOutTimeSlotId
                ),
                1,
                index = 0,
                timeEditingReason = null,
                issuerId = Constants.absentIssuerId,
                version = 1
            )
            .checkAutoCreatedTimeSlotAPI(
                actualResponse = employeeScheduleActions.timeSlot(),
                accountContractId = UUID.fromString(employeeActions.getContractIdFromDB(Constants.accountingProfileIdForTimesheet1)),
                profileId = profileId,
                timesheetId = employeeScheduleActions.timesheetByDarkstore().timesheetId,
                workedOutHours = 1,
                workedOutShiftId = workedOutShiftId,
                version = 1
            )
    }

    @Test
    @DisplayName("Save worked-out timeslot = 1 hour 30 min automatically")
    fun saveTwoHourWorkedOutTimeslotAutomaticallyTest() {
        val profileId = employeeCommonPreconditions.createProfileDeliveryman(accountingProfileId = Constants.accountingProfileIdForTimesheet1).profileId
        val workedOutShiftId = employeeCommonPreconditions.startAndStopUpdatedShift(
            profileId, userRole = ShiftUserRole.DELIVERYMAN, newDate = Instant.now().minusSeconds(5405).truncatedTo(
                ChronoUnit.NANOS
            )
        ).shiftId

        employeeSchedulePreconditions
            .setTimesheetRequest(date = LocalDate.now())

        employeeScheduleActions
            .getTimeSlotById(employeeScheduleActions.getTimeSlotInBD(workedOutShiftId))
            .getTimesheet(
                employeeCommonPreconditions.createProfileRequest().darkstoreId!!,
                employeeSchedulePreconditions.getTimesheetRequest()
            )
        employeeScheduleAssertion
            .checkTimeSlotInDatabase(
                employeeScheduleDatabase.getWorkedOutTimeSlot(
                    employeeScheduleActions.timesheetByDarkstore().workedOutTimeSlots.first().workedOutTimeSlotId
                ),
                accountContractId = UUID.fromString(employeeActions.getContractIdFromDB(Constants.accountingProfileIdForTimesheet1)),
                profileId = profileId,
                timesheetId = employeeScheduleActions.timesheetByDarkstore().timesheetId,
                workedOutHours = 2,
                workedOutShiftId = workedOutShiftId
            )
            .checkTimeSlotLogInDatabase(
                employeeScheduleDatabase.getWorkedOutTimeSlotLog(
                    employeeScheduleActions.timesheetByDarkstore().workedOutTimeSlots.first().workedOutTimeSlotId
                ),
                1,
                index = 0,
                timeEditingReason = null,
                issuerId = Constants.absentIssuerId,
                version = 1
            )
            .checkAutoCreatedTimeSlotAPI(
                actualResponse = employeeScheduleActions.timeSlot(),
                accountContractId = UUID.fromString(employeeActions.getContractIdFromDB(Constants.accountingProfileIdForTimesheet1)),
                profileId = profileId,
                timesheetId = employeeScheduleActions.timesheetByDarkstore().timesheetId,
                workedOutHours = 2,
                workedOutShiftId = workedOutShiftId,
                version = 1
            )
    }

    @Test
    @DisplayName("Worked-out timeslot 0 min is not created")
    fun createNullWorkedOutTimeslotAutomaticallyTest() {
        val profileId = employeeCommonPreconditions.createProfileDeliveryman(accountingProfileId = Constants.accountingProfileIdForTimesheet1).profileId
        employeeCommonPreconditions.startAndStopShift(profileId)
        employeeSchedulePreconditions
            .setTimesheetRequest(date = LocalDate.now())

        employeeScheduleActions
            .getTimesheet(
                employeeCommonPreconditions.createProfileRequest().darkstoreId!!,
                employeeSchedulePreconditions.getTimesheetRequest()
            )
        employeeScheduleAssertion
            .checkTimeSlotNotInDatabase(employeeScheduleDatabase.checkTimeslotExist(profileId))
            .checkTimesheetWithoutTimeslotsAPI(
                employeeScheduleActions.timesheetByDarkstore(),
                employeeCommonPreconditions.createProfileRequest().darkstoreId!!,
                ApiEnum(TimesheetStatus.NEW),
                modifiedAt = Instant.now().truncatedTo(ChronoUnit.MINUTES)

            )
    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Worked-out timeslot <30 min is not created")
    fun createShortWorkedOutTimeslotAutomaticallyTest() {
        val profileId = employeeCommonPreconditions.createProfileDeliveryman(accountingProfileId = Constants.accountingProfileIdForTimesheet1).profileId

        employeeCommonPreconditions.startAndStopUpdatedShift(
            profileId, userRole = ShiftUserRole.DELIVERYMAN, newDate = Instant.now().minusSeconds(1740).truncatedTo(
                ChronoUnit.NANOS
            )
        )

        employeeSchedulePreconditions
            .setTimesheetRequest(date = LocalDate.now())

        employeeScheduleActions
            .getTimesheet(
                employeeCommonPreconditions.createProfileRequest().darkstoreId!!,
                employeeSchedulePreconditions.getTimesheetRequest()
            )
        employeeScheduleAssertion
            .checkTimeSlotNotInDatabase(employeeScheduleDatabase.checkTimeslotExist(profileId))
            .checkTimesheetWithoutTimeslotsAPI(
                employeeScheduleActions.timesheetByDarkstore(),
                employeeCommonPreconditions.createProfileRequest().darkstoreId!!,
                ApiEnum(TimesheetStatus.NEW),
                modifiedAt = Instant.now().truncatedTo(ChronoUnit.MINUTES)

            )
    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Worked-out timeslot without contract is not created")
    fun createWorkedOutTimeslotWithoutContractAutomaticallyTest() {
        val profileId = employeeCommonPreconditions.createProfileDeliveryman(accountingProfileId = Constants.accountingProfileIdForTimesheet1).profileId

        employeeCommonPreconditions.startAndStopUpdatedShift(
            profileId, userRole = ShiftUserRole.DELIVERYMAN, newDate = Instant.now().minusSeconds(3600).truncatedTo(
                ChronoUnit.NANOS
            )
        )

        employeeSchedulePreconditions
            .setTimesheetRequest(date = LocalDate.now())

        employeeScheduleActions
            .getTimesheet(
                employeeCommonPreconditions.createProfileRequest().darkstoreId!!,
                employeeSchedulePreconditions.getTimesheetRequest()
            )
        employeeScheduleAssertion
            .checkTimeSlotNotInDatabase(employeeScheduleDatabase.checkTimeslotExist(profileId))
            .checkTimesheetWithoutTimeslotsAPI(
                employeeScheduleActions.timesheetByDarkstore(),
                employeeCommonPreconditions.createProfileRequest().darkstoreId!!,
                ApiEnum(TimesheetStatus.NEW),
                modifiedAt = Instant.now().truncatedTo(ChronoUnit.MINUTES)

            )
    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Worked-out timeslot with 2 contracts is not created")
    fun createWorkedOutTimeslotWithTwoContractAutomaticallyTest() {
        val profileId = employeeCommonPreconditions.createProfileDeliveryman(accountingProfileId = Constants.accountingProfileIdWithTwoContracts).profileId

        employeeCommonPreconditions.startAndStopUpdatedShift(
            profileId, userRole = ShiftUserRole.DELIVERYMAN, newDate = Instant.now().minusSeconds(5740).truncatedTo(
                ChronoUnit.NANOS
            )
        )

        employeeSchedulePreconditions
            .setTimesheetRequest(date = LocalDate.now())

        employeeScheduleActions
            .getTimesheet(
                employeeCommonPreconditions.createProfileRequest().darkstoreId!!,
                employeeSchedulePreconditions.getTimesheetRequest()
            )
        employeeScheduleAssertion
            .checkTimeSlotNotInDatabase(employeeScheduleDatabase.checkTimeslotExist(profileId))
            .checkTimesheetWithoutTimeslotsAPI(
                employeeScheduleActions.timesheetByDarkstore(),
                employeeCommonPreconditions.createProfileRequest().darkstoreId!!,
                ApiEnum(TimesheetStatus.NEW),
                modifiedAt = Instant.now().truncatedTo(ChronoUnit.MINUTES)

            )
    }

    @Test
    @DisplayName("Worked-out timeslot with not deliveryman or picker role is not created")
    fun createWorkedOutTimeslotWithDarkstoreAdminRoleAutomaticallyTest() {
        val profileId = employeeCommonPreconditions.createProfileGoodsManager().profileId


        employeeCommonPreconditions.startAndStopUpdatedShift(
            profileId, userRole = ShiftUserRole.GOODS_MANAGER, newDate = Instant.now().minusSeconds(5405).truncatedTo(
                ChronoUnit.NANOS
            )
        ).shiftId

        employeeScheduleAssertion
            .checkTimeSlotNotInDatabase(employeeScheduleDatabase.checkTimeslotExist(profileId))

    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Worked-out timeslot in submitted timesheet is not created")
    fun createWorkedOutTimeslotInSubmittedTimesheetAutomaticallyTest() {
        val profileId = employeeCommonPreconditions.createProfileDeliveryman(accountingProfileId = Constants.accountingProfileIdForTimesheet1).profileId
        val workedOutShiftId = employeeCommonPreconditions.startShift(profileId).shiftId
        employeeCommonPreconditions.updateShiftInDB(
            workedOutShiftId,
            Instant.now().minusSeconds(5740).truncatedTo(
                ChronoUnit.NANOS
            )
        )

        employeeSchedulePreconditions
            .setTimesheetRequest(date = LocalDate.now())

        employeeScheduleActions
            .submitTimesheet(
                employeeScheduleActions.getTimesheet(
                    employeeCommonPreconditions.createProfileRequest().darkstoreId!!,
                    employeeSchedulePreconditions.getTimesheetRequest()
                ).timesheetByDarkstore().timesheetId
            )
            .getTimesheet(
                employeeCommonPreconditions.createProfileRequest().darkstoreId!!,
                employeeSchedulePreconditions.getTimesheetRequest()
            )

        employeeCommonPreconditions.stopShift(profileId)
        employeeScheduleAssertion
            .checkTimeSlotNotInDatabase(employeeScheduleDatabase.checkTimeslotExist(profileId))
            .checkTimesheetWithoutTimeslotsAPI(
                employeeScheduleActions.timesheetByDarkstore(),
                employeeCommonPreconditions.createProfileRequest().darkstoreId!!,
                ApiEnum(TimesheetStatus.NOTHING_TO_SUBMIT),
                modifiedAt = Instant.now().truncatedTo(ChronoUnit.MINUTES)

            )
    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Worked-out timeslot with inactive contract is not created")
    fun createWorkedOutTimeslotWithInactiveContractAutomaticallyTest() {
        val profileId = employeeCommonPreconditions.createProfileDeliveryman(accountingProfileId = Constants.accountingProfileIdWithInactiveContract).profileId

        employeeCommonPreconditions.startAndStopUpdatedShift(
            profileId, userRole = ShiftUserRole.DELIVERYMAN, newDate = Instant.now().minusSeconds(5740).truncatedTo(
                ChronoUnit.NANOS
            )
        )

        employeeSchedulePreconditions
            .setTimesheetRequest(date = LocalDate.now())

        employeeScheduleActions
            .getTimesheet(
                employeeCommonPreconditions.createProfileRequest().darkstoreId!!,
                employeeSchedulePreconditions.getTimesheetRequest()
            )
        employeeScheduleAssertion
            .checkTimeSlotNotInDatabase(employeeScheduleDatabase.checkTimeslotExist(profileId))
            .checkTimesheetWithoutTimeslotsAPI(
                employeeScheduleActions.timesheetByDarkstore(),
                employeeCommonPreconditions.createProfileRequest().darkstoreId!!,
                ApiEnum(TimesheetStatus.NEW),
                modifiedAt = Instant.now().truncatedTo(ChronoUnit.MINUTES)

            )
    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Worked-out timeslot with 2 inactive contracts is not created")
    fun createWorkedOutTimeslotWithTwoInactiveContractAutomaticallyTest() {
        val profileId = employeeCommonPreconditions.createProfileDeliveryman(accountingProfileId = Constants.accountingProfileIdWith2InactiveContracts).profileId

        employeeCommonPreconditions.startAndStopUpdatedShift(
            profileId, userRole = ShiftUserRole.DELIVERYMAN, newDate = Instant.now().minusSeconds(5740).truncatedTo(
                ChronoUnit.NANOS
            )
        )

        employeeSchedulePreconditions
            .setTimesheetRequest(date = LocalDate.now())

        employeeScheduleActions
            .getTimesheet(
                employeeCommonPreconditions.createProfileRequest().darkstoreId!!,
                employeeSchedulePreconditions.getTimesheetRequest()
            )
        employeeScheduleAssertion
            .checkTimeSlotNotInDatabase(employeeScheduleDatabase.checkTimeslotExist(profileId))
            .checkTimesheetWithoutTimeslotsAPI(
                employeeScheduleActions.timesheetByDarkstore(),
                employeeCommonPreconditions.createProfileRequest().darkstoreId!!,
                ApiEnum(TimesheetStatus.NEW),
                modifiedAt = Instant.now().truncatedTo(ChronoUnit.MINUTES)
            )
    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Worked-out timeslot with inactive and active contract is created")
    fun createWorkedOutTimeslotWithInactiveAndActiveContractAutomaticallyTest() {
        val profileId = employeeCommonPreconditions.createProfileDeliveryman(accountingProfileId = Constants.accountingProfileIdWithActiveAndInactiveContracts).profileId

        val workedOutShiftId = employeeCommonPreconditions.startAndStopUpdatedShift(
            profileId, newDate = Instant.now().minusSeconds(10800).truncatedTo(
                ChronoUnit.NANOS
            )
        ).shiftId

        employeeSchedulePreconditions
            .setTimesheetRequest(date = LocalDate.now())

        employeeScheduleActions
            .getTimeSlotById(employeeScheduleActions.getTimeSlotInBD(workedOutShiftId))
            .getTimesheet(
                employeeCommonPreconditions.createProfileRequest().darkstoreId!!,
                employeeSchedulePreconditions.getTimesheetRequest()
            )

        employeeScheduleAssertion
            .checkTimeSlotInDatabase(
                employeeScheduleDatabase.getWorkedOutTimeSlot(
                    employeeScheduleActions.timesheetByDarkstore().workedOutTimeSlots.first().workedOutTimeSlotId
                ),
                accountContractId = UUID.fromString(
                    employeeActions.getActiveContractIdWithoutRetirementDateFromDB(
                        Constants.accountingProfileIdWithActiveAndInactiveContracts
                    ).first()
                ),
                profileId = profileId,
                timesheetId = employeeScheduleActions.timesheetByDarkstore().timesheetId,
                workedOutHours = 3,
                workedOutShiftId = workedOutShiftId
            )
            .checkTimeSlotLogInDatabase(
                employeeScheduleDatabase.getWorkedOutTimeSlotLog(
                    employeeScheduleActions.timesheetByDarkstore().workedOutTimeSlots.first().workedOutTimeSlotId
                ),
                1,
                index = 0,
                timeEditingReason = null,
                issuerId = Constants.absentIssuerId,
                version = 1
            )
            .checkAutoCreatedTimeSlotAPI(
                actualResponse = employeeScheduleActions.timeSlot(),
                accountContractId = UUID.fromString(
                    employeeActions.getActiveContractIdWithoutRetirementDateFromDB(
                        Constants.accountingProfileIdWithActiveAndInactiveContracts
                    ).first()
                ),
                profileId = profileId,
                timesheetId = employeeScheduleActions.timesheetByDarkstore().timesheetId,
                workedOutHours = 3,
                workedOutShiftId = workedOutShiftId,
                version = 1
            )

    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Worked-out timeslot with inactive from today contract is created")
    fun createWorkedOutTimeslotWithInactiveFromTodayContractAutomaticallyTest() {


        val event = employeePreconditions.fillOutsourseContractEvent(
            accountingProfileId = UUID.randomUUID(),
            dataUvolneniya = Instant.now().toString()
        )
        employeeActions.produceToVneshnieSotrudniki(event)

        val profileId = employeeCommonPreconditions.createProfileDeliveryman(accountingProfileId = event.payload[0].fizicheskoeLitso.guid.toString()).profileId

        val workedOutShiftId = employeeCommonPreconditions.startAndStopUpdatedShift(
            profileId, newDate = Instant.now().minusSeconds(10800).truncatedTo(
                ChronoUnit.NANOS
            )
        ).shiftId

        employeeSchedulePreconditions
            .setTimesheetRequest(date = LocalDate.now())

        employeeScheduleActions
            .getTimeSlotById(employeeScheduleActions.getTimeSlotInBD(workedOutShiftId))
            .getTimesheet(
                employeeCommonPreconditions.createProfileRequest().darkstoreId!!,
                employeeSchedulePreconditions.getTimesheetRequest()
            )

        employeeScheduleAssertion
            .checkTimeSlotInDatabase(
                employeeScheduleDatabase.getWorkedOutTimeSlot(
                    employeeScheduleActions.timesheetByDarkstore().workedOutTimeSlots.first().workedOutTimeSlotId
                ),
                accountContractId = UUID.fromString(employeeActions.getContractIdFromDB(event.payload[0].fizicheskoeLitso.guid.toString())),
                profileId = profileId,
                timesheetId = employeeScheduleActions.timesheetByDarkstore().timesheetId,
                workedOutHours = 3,
                workedOutShiftId = workedOutShiftId
            )
            .checkTimeSlotLogInDatabase(
                employeeScheduleDatabase.getWorkedOutTimeSlotLog(
                    employeeScheduleActions.timesheetByDarkstore().workedOutTimeSlots.first().workedOutTimeSlotId
                ),
                1,
                index = 0,
                timeEditingReason = null,
                issuerId = Constants.absentIssuerId,
                version = 1
            )
            .checkAutoCreatedTimeSlotAPI(
                actualResponse = employeeScheduleActions.timeSlot(),
                accountContractId = UUID.fromString(employeeActions.getContractIdFromDB(event.payload[0].fizicheskoeLitso.guid.toString())),
                profileId = profileId,
                timesheetId = employeeScheduleActions.timesheetByDarkstore().timesheetId,
                workedOutHours = 3,
                workedOutShiftId = workedOutShiftId,
                version = 1
            )


    }

}