package ru.samokat.mysamokat.tests.tests.employee_schedule.timesheetAPI

import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.samokat.employeeprofiles.api.common.domain.EmployeeVehicleType
import ru.samokat.employeeprofiles.api.common.domain.Vehicle
import ru.samokat.employeeschedule.api.timesheet.TimesheetStatus
import ru.samokat.my.domain.employee.EmployeeName
import ru.samokat.my.domain.employee.EmployeeRole
import ru.samokat.my.rest.api.enum.ApiEnum
import ru.samokat.mysamokat.tests.checkers.CommonAssertion
import ru.samokat.mysamokat.tests.checkers.EmployeeScheduleAssertion
import ru.samokat.mysamokat.tests.dataproviders.Constants
import ru.samokat.mysamokat.tests.dataproviders.StringAndPhoneNumberGenerator
import ru.samokat.mysamokat.tests.dataproviders.preconditions.CommonPreconditions
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
@Tag("emproSchedule")
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class CheckTimesheet {

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
        employeeActions.deleteProfile(Constants.mobile2)
        employeeScheduleActions.deleteTimesheetInDB(darkstoreId = Constants.darkstoreId, date = LocalDate.now())
        employeeScheduleActions.deleteTimesheetInDB(
            darkstoreId = Constants.darkstoreId,
            date = LocalDate.parse("2021-11-11")
        )
    }

    @AfterEach
    fun release() {
        commonAssertion.assertAll()
        employeeScheduleAssertion.assertAll()
        employeeActions.deleteProfile(Constants.mobile1)
        employeeActions.deleteProfile(Constants.mobile2)
        employeeScheduleActions.deleteTimesheetInDB(darkstoreId = Constants.darkstoreId, date = LocalDate.now())
        employeeScheduleActions.deleteTimesheetInDB(
            darkstoreId = Constants.darkstoreId,
            date = LocalDate.parse("2021-11-11")
        )

    }


    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Check new timesheet with timeSlots for different roles")
    fun checkNewTimesheetTest() {

        val listOfProfileId: MutableList<UUID> = employeeCommonPreconditions.createListOfProfiles(
            listOfRoles = mutableListOf(
                listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                listOf(ApiEnum(EmployeeRole.PICKER))
            ),
            listOfVehicle = mutableListOf(Vehicle(ApiEnum(EmployeeVehicleType.CAR)), null),
            listOfStaffPartner = mutableListOf(Constants.defaultStaffPartnerId, Constants.defaultStaffPartnerId),
            listOfDarkstore = mutableListOf(Constants.darkstoreId, Constants.darkstoreId),
            listOfMobile = mutableListOf(Constants.mobile1, Constants.mobile2),
            listOfName = mutableListOf(
                EmployeeName(
                    (StringAndPhoneNumberGenerator.generateRandomString(10)),
                    (StringAndPhoneNumberGenerator.generateRandomString(10)),
                    (StringAndPhoneNumberGenerator.generateRandomString(10))
                ),
                EmployeeName(
                    (StringAndPhoneNumberGenerator.generateRandomString(10)),
                    (StringAndPhoneNumberGenerator.generateRandomString(10)),
                    (StringAndPhoneNumberGenerator.generateRandomString(10))
                )
            ),
            listOfAccountingProfileIds = mutableListOf(
                Constants.accountingProfileIdForTimesheet1,
                Constants.accountingProfileIdForTimesheet2
            ),
            listOfRequisitionsId = mutableListOf(null, null),
            amount = 2
        )

        val listOfShifts: List<UUID> = employeeCommonPreconditions.startAndStopShiftForSeveralProfiles(
            listOfProfileId = listOfProfileId,
            listOfUserRole = mutableListOf(ShiftUserRole.DELIVERYMAN, ShiftUserRole.PICKER),
            listOfDeliveryMethod = mutableListOf(null, null),
            listOfDarkstore = mutableListOf(Constants.darkstoreId, Constants.darkstoreId),
            amount = 2
        ).map { it.shiftId }


        employeeSchedulePreconditions
            .setTimesheetRequest(date = LocalDate.now())
            .setListCreateWorkedOutTimeslotRequest(
                2,
                timesheetId = employeeScheduleActions.getTimesheet(
                    Constants.darkstoreId,
                    employeeSchedulePreconditions.getTimesheetRequest()
                ).timesheetByDarkstore().timesheetId,
                listOfProfileId = listOfProfileId,
                listOfWorkedOutShiftId = listOfShifts,
                listOfWorkedOutHours = mutableListOf(6, 7),
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
            .getTimesheetById(employeeScheduleActions.timesheetByDarkstore().timesheetId)


        employeeScheduleAssertion
            .checkTimesheetAPI(
                employeeScheduleActions.timesheetByDarkstore(),
                modifiedAt = Instant.now().truncatedTo(ChronoUnit.MINUTES),
                listOfAccountContractId = employeeSchedulePreconditions.listOfCreateWorkedOutTimesheetRequest()
                    .map { it.accountingContractId },
                listOfWorkedOutHours = employeeSchedulePreconditions.listOfCreateWorkedOutTimesheetRequest()
                    .map { it.workedOutHours },
                listOfWorkedOutShiftId = employeeSchedulePreconditions.listOfCreateWorkedOutTimesheetRequest()
                    .map { it.workedOutShiftId },
                status = ApiEnum(TimesheetStatus.NEW),
                darkstoreId = Constants.darkstoreId,
                listOfProfileId = employeeSchedulePreconditions.listOfCreateWorkedOutTimesheetRequest()
                    .map { it.profileId }
            )

            .checkTimesheetAPI(
                employeeScheduleActions.timesheetById(),
                modifiedAt = Instant.now().truncatedTo(ChronoUnit.MINUTES),
                listOfAccountContractId = employeeSchedulePreconditions.listOfCreateWorkedOutTimesheetRequest()
                    .map { it.accountingContractId },
                listOfWorkedOutHours = employeeSchedulePreconditions.listOfCreateWorkedOutTimesheetRequest()
                    .map { it.workedOutHours },
                listOfWorkedOutShiftId = employeeSchedulePreconditions.listOfCreateWorkedOutTimesheetRequest()
                    .map { it.workedOutShiftId },
                status = ApiEnum(TimesheetStatus.NEW),
                darkstoreId = Constants.darkstoreId,
                listOfProfileId = employeeSchedulePreconditions.listOfCreateWorkedOutTimesheetRequest()
                    .map { it.profileId }
            )
            .checkTimesheetInDatabase(
                employeeScheduleDatabase.getTimesheet(employeeScheduleActions.timesheetByDarkstore().timesheetId),
                Constants.darkstoreId,
                TimesheetStatus.NEW.toString()
            )
            .checkTimesheetAutoTask(
                employeeScheduleDatabase.checkTaskExist(
                    "timesheetAutoSubmit",
                    employeeScheduleActions.timesheetByDarkstore().timesheetId.toString()
                )
            )


    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Check submitted timesheet")
    fun checkSubmittedTimesheetTest() {
        val profileId = employeeCommonPreconditions.createProfileDeliveryman().profileId
        val workedOutShiftId = employeeCommonPreconditions.startAndStopShift(
            profileId,
            darkstoreId = Constants.darkstoreId
        ).shiftId

        employeeSchedulePreconditions
            .setTimesheetRequest(date = LocalDate.now())
            .setCreateWorkedOutTimeslotRequest(
                timesheetId = employeeScheduleActions.getTimesheet(
                    darkstoreId = Constants.darkstoreId,
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
            .createWorkedOutTimeSlot(
                employeeSchedulePreconditions.createWorkedOutTimesheetRequest()
            )
            .submitTimesheet(employeeSchedulePreconditions.createWorkedOutTimesheetRequest().timesheetId)
            .getTimesheet(
                darkstoreId = Constants.darkstoreId,
                employeeSchedulePreconditions.getTimesheetRequest()
            )
            .getTimesheetById(employeeSchedulePreconditions.createWorkedOutTimesheetRequest().timesheetId)

        employeeScheduleAssertion
            .checkTimesheetAPI(
                employeeScheduleActions.timesheetByDarkstore(),
                modifiedAt = Instant.now().truncatedTo(ChronoUnit.MINUTES),
                listOfAccountContractId = listOf(employeeSchedulePreconditions.createWorkedOutTimesheetRequest().accountingContractId),
                listOfWorkedOutHours = listOf(employeeSchedulePreconditions.createWorkedOutTimesheetRequest().workedOutHours),
                listOfWorkedOutShiftId = listOf(employeeSchedulePreconditions.createWorkedOutTimesheetRequest().workedOutShiftId),
                status = ApiEnum(TimesheetStatus.SUBMITTED),
                darkstoreId = Constants.darkstoreId,
                listOfProfileId = listOf(employeeSchedulePreconditions.createWorkedOutTimesheetRequest().profileId)
            )

            .checkTimesheetAPI(
                employeeScheduleActions.timesheetById(),
                modifiedAt = Instant.now().truncatedTo(ChronoUnit.MINUTES),
                listOfAccountContractId = listOf(employeeSchedulePreconditions.createWorkedOutTimesheetRequest().accountingContractId),
                listOfWorkedOutHours = listOf(employeeSchedulePreconditions.createWorkedOutTimesheetRequest().workedOutHours),
                listOfWorkedOutShiftId = listOf(employeeSchedulePreconditions.createWorkedOutTimesheetRequest().workedOutShiftId),
                status = ApiEnum(TimesheetStatus.SUBMITTED),
                darkstoreId = Constants.darkstoreId,
                listOfProfileId = listOf(employeeSchedulePreconditions.createWorkedOutTimesheetRequest().profileId)

            )
            .checkTimesheetInDatabase(
                employeeScheduleDatabase.getTimesheet(employeeScheduleActions.timesheetByDarkstore().timesheetId),
                darkstoreId = Constants.darkstoreId,
                TimesheetStatus.SUBMITTED.toString()
            )


    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Check nothing_to_submit timesheet")
    fun checkNothingToSubmitTimesheetTest() {
        val profileId = employeeCommonPreconditions.createProfileDeliveryman().profileId
        employeeCommonPreconditions.startAndStopShift(profileId)
        employeeSchedulePreconditions
            .setTimesheetRequest(date = LocalDate.now())

        employeeScheduleActions
            .submitTimesheet(
                timesheetId = employeeScheduleActions.getTimesheet(
                    darkstoreId = Constants.darkstoreId,
                    employeeSchedulePreconditions.getTimesheetRequest()
                ).timesheetByDarkstore().timesheetId
            )
            .getTimesheet(
                darkstoreId = Constants.darkstoreId,
                employeeSchedulePreconditions.getTimesheetRequest()
            )
            .getTimesheetById(employeeScheduleActions.timesheetByDarkstore().timesheetId)

        employeeScheduleAssertion
            .checkTimesheetWithoutTimeslotsAPI(
                employeeScheduleActions.timesheetByDarkstore(),
                status = ApiEnum(TimesheetStatus.NOTHING_TO_SUBMIT),
                darkstoreId = Constants.darkstoreId,
                modifiedAt = Instant.now().truncatedTo(ChronoUnit.MINUTES)
            )
            .checkTimesheetWithoutTimeslotsAPI(
                employeeScheduleActions.timesheetById(),
                status = ApiEnum(TimesheetStatus.NOTHING_TO_SUBMIT),
                darkstoreId = Constants.darkstoreId,
                modifiedAt = Instant.now().truncatedTo(ChronoUnit.MINUTES)
            )
            .checkTimesheetInDatabase(
                employeeScheduleDatabase.getTimesheet(employeeScheduleActions.timesheetByDarkstore().timesheetId),
                Constants.darkstoreId,
                TimesheetStatus.NOTHING_TO_SUBMIT.toString()
            )


    }

    @Test
    @DisplayName("Check not-today darkstore's timesheet")
    fun checkNotTodayTimesheetTest() {

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
            .setTimesheetRequest(date = LocalDate.parse("2021-11-11"))

        employeeScheduleActions
            .createWorkedOutTimeSlot(
                employeeSchedulePreconditions.createWorkedOutTimesheetRequest()
            )
            .updateTimesheetInDB(
                Constants.darkstoreId,
                date = LocalDate.now(),
                newDate = employeeSchedulePreconditions.getTimesheetRequest().date
            )
            .getTimesheet(
                Constants.darkstoreId,
                employeeSchedulePreconditions.getTimesheetRequest()
            )
            .getTimesheetById(employeeSchedulePreconditions.createWorkedOutTimesheetRequest().timesheetId)

        employeeScheduleAssertion
            .checkTimesheetAPI(
                employeeScheduleActions.timesheetByDarkstore(),
                modifiedAt = Instant.now().truncatedTo(ChronoUnit.MINUTES),
                listOfAccountContractId = listOf(employeeSchedulePreconditions.createWorkedOutTimesheetRequest().accountingContractId),
                listOfWorkedOutHours = listOf(employeeSchedulePreconditions.createWorkedOutTimesheetRequest().workedOutHours),
                listOfWorkedOutShiftId = listOf(employeeSchedulePreconditions.createWorkedOutTimesheetRequest().workedOutShiftId),
                status = ApiEnum(TimesheetStatus.NEW),
                darkstoreId = Constants.darkstoreId,
                listOfProfileId = listOf(employeeSchedulePreconditions.createWorkedOutTimesheetRequest().profileId)
            )

            .checkTimesheetAPI(
                employeeScheduleActions.timesheetById(),
                modifiedAt = Instant.now().truncatedTo(ChronoUnit.MINUTES),
                listOfAccountContractId = listOf(employeeSchedulePreconditions.createWorkedOutTimesheetRequest().accountingContractId),
                listOfWorkedOutHours = listOf(employeeSchedulePreconditions.createWorkedOutTimesheetRequest().workedOutHours),
                listOfWorkedOutShiftId = listOf(employeeSchedulePreconditions.createWorkedOutTimesheetRequest().workedOutShiftId),
                status = ApiEnum(TimesheetStatus.NEW),
                darkstoreId = Constants.darkstoreId,
                listOfProfileId = listOf(employeeSchedulePreconditions.createWorkedOutTimesheetRequest().profileId)

            )
            .checkTimesheetInDatabase(
                employeeScheduleDatabase.getTimesheet(employeeScheduleActions.timesheetByDarkstore().timesheetId),
                Constants.darkstoreId,
                TimesheetStatus.NEW.toString()
            )

    }

    @Test
    @DisplayName("Check darkstore without timesheet")
    fun checkDarkstoreWithoutTimesheetTest() {

        employeeSchedulePreconditions
            .setTimesheetRequest(date = LocalDate.now())

        employeeScheduleActions
            .getTimesheetUnsuccessfully(
                darkstoreId = Constants.darkstoreId,
                employeeSchedulePreconditions.getTimesheetRequest()
            )

        commonAssertion
            .checkErrorMessage(employeeScheduleActions.timesheetByDarkstoreError().message, "Timesheet Not Found")
            .checkStatusNotFound(employeeScheduleActions.timesheetByDarkstoreResult().statusCode)

        employeeScheduleAssertion.checkTimesheetNotInDatabase(
            employeeScheduleDatabase.checkTimesheetExist(
                darkstoreId = Constants.darkstoreId,
                date = LocalDate.now()
            )
        )

    }

    @Test
    @DisplayName("Check timesheet with timeslot for user from other darkstore")
    fun checkTimesheetWithUserFromOtherDarkstoreTest() {
        val listOfProfileId: MutableList<UUID> = employeeCommonPreconditions.createListOfProfiles(
            listOfRoles = mutableListOf(
                listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                listOf(ApiEnum(EmployeeRole.PICKER))
            ),
            listOfVehicle = mutableListOf(Vehicle(ApiEnum(EmployeeVehicleType.CAR)), null),
            listOfStaffPartner = mutableListOf(Constants.defaultStaffPartnerId, Constants.defaultStaffPartnerId),
            listOfDarkstore = mutableListOf(
                Constants.darkstoreIdWithNotMoscowTimezone,
                Constants.darkstoreIdWithNotMoscowTimezone
            ),
            listOfMobile = mutableListOf(Constants.mobile1, Constants.mobile2),
            listOfName = mutableListOf(
                EmployeeName(
                    (StringAndPhoneNumberGenerator.generateRandomString(10)),
                    (StringAndPhoneNumberGenerator.generateRandomString(10)),
                    (StringAndPhoneNumberGenerator.generateRandomString(10))
                ),
                EmployeeName(
                    (StringAndPhoneNumberGenerator.generateRandomString(10)),
                    (StringAndPhoneNumberGenerator.generateRandomString(10)),
                    (StringAndPhoneNumberGenerator.generateRandomString(10))
                )
            ),
            listOfAccountingProfileIds = mutableListOf(
                Constants.accountingProfileIdForTimesheet1,
                Constants.accountingProfileIdForTimesheet2
            ),
            listOfRequisitionsId = mutableListOf(null, null),
            amount = 2
        )

        val listOfShifts: List<UUID> = employeeCommonPreconditions.startAndStopShiftForSeveralProfiles(
            listOfProfileId = listOfProfileId,
            listOfUserRole = mutableListOf(ShiftUserRole.DELIVERYMAN, ShiftUserRole.PICKER),
            listOfDeliveryMethod = mutableListOf(null, null),
            listOfDarkstore = mutableListOf(Constants.darkstoreId, Constants.darkstoreId),
            amount = 2
        ).map { it.shiftId }


        employeeSchedulePreconditions
            .setTimesheetRequest(date = LocalDate.now())
            .setListCreateWorkedOutTimeslotRequest(
                2,
                timesheetId = employeeScheduleActions.getTimesheet(
                    Constants.darkstoreId,
                    employeeSchedulePreconditions.getTimesheetRequest()
                ).timesheetByDarkstore().timesheetId,
                listOfProfileId = listOfProfileId,
                listOfWorkedOutShiftId = listOfShifts,
                listOfWorkedOutHours = mutableListOf(6, 7),
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
            .getTimesheetById(employeeScheduleActions.timesheetByDarkstore().timesheetId)


        employeeScheduleAssertion
            .checkTimesheetAPI(
                employeeScheduleActions.timesheetByDarkstore(),
                modifiedAt = Instant.now().truncatedTo(ChronoUnit.MINUTES),
                listOfAccountContractId = employeeSchedulePreconditions.listOfCreateWorkedOutTimesheetRequest()
                    .map { it.accountingContractId },
                listOfWorkedOutHours = employeeSchedulePreconditions.listOfCreateWorkedOutTimesheetRequest()
                    .map { it.workedOutHours },
                listOfWorkedOutShiftId = employeeSchedulePreconditions.listOfCreateWorkedOutTimesheetRequest()
                    .map { it.workedOutShiftId },
                status = ApiEnum(TimesheetStatus.NEW),
                darkstoreId = Constants.darkstoreId,
                listOfProfileId = employeeSchedulePreconditions.listOfCreateWorkedOutTimesheetRequest()
                    .map { it.profileId }
            )

            .checkTimesheetAPI(
                employeeScheduleActions.timesheetById(),
                modifiedAt = Instant.now().truncatedTo(ChronoUnit.MINUTES),
                listOfAccountContractId = employeeSchedulePreconditions.listOfCreateWorkedOutTimesheetRequest()
                    .map { it.accountingContractId },
                listOfWorkedOutHours = employeeSchedulePreconditions.listOfCreateWorkedOutTimesheetRequest()
                    .map { it.workedOutHours },
                listOfWorkedOutShiftId = employeeSchedulePreconditions.listOfCreateWorkedOutTimesheetRequest()
                    .map { it.workedOutShiftId },
                status = ApiEnum(TimesheetStatus.NEW),
                darkstoreId = Constants.darkstoreId,
                listOfProfileId = employeeSchedulePreconditions.listOfCreateWorkedOutTimesheetRequest()
                    .map { it.profileId }
            )
            .checkTimesheetInDatabase(
                employeeScheduleDatabase.getTimesheet(employeeScheduleActions.timesheetByDarkstore().timesheetId),
                Constants.darkstoreId,
                TimesheetStatus.NEW.toString()
            )
            .checkTimesheetAutoTask(
                employeeScheduleDatabase.checkTaskExist(
                    "timesheetAutoSubmit",
                    employeeScheduleActions.timesheetByDarkstore().timesheetId.toString()
                )
            )


    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Check timesheet with cancelled timeslot (=0)")
    fun checkTimesheetWithCancelledTimeslotTest() {
        val listOfProfileId: MutableList<UUID> = employeeCommonPreconditions.createListOfProfiles(
            listOfRoles = mutableListOf(
                listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                listOf(ApiEnum(EmployeeRole.PICKER))
            ),
            listOfVehicle = mutableListOf(Vehicle(ApiEnum(EmployeeVehicleType.CAR)), null),
            listOfStaffPartner = mutableListOf(Constants.defaultStaffPartnerId, Constants.defaultStaffPartnerId),
            listOfDarkstore = mutableListOf(Constants.darkstoreId, Constants.darkstoreId),
            listOfMobile = mutableListOf(Constants.mobile1, Constants.mobile2),
            listOfName = mutableListOf(
                EmployeeName(
                    (StringAndPhoneNumberGenerator.generateRandomString(10)),
                    (StringAndPhoneNumberGenerator.generateRandomString(10)),
                    (StringAndPhoneNumberGenerator.generateRandomString(10))
                ),
                EmployeeName(
                    (StringAndPhoneNumberGenerator.generateRandomString(10)),
                    (StringAndPhoneNumberGenerator.generateRandomString(10)),
                    (StringAndPhoneNumberGenerator.generateRandomString(10))
                )
            ),
            listOfAccountingProfileIds = mutableListOf(
                Constants.accountingProfileIdForTimesheet1,
                Constants.accountingProfileIdForTimesheet2
            ),
            listOfRequisitionsId = mutableListOf(null, null),
            amount = 2
        )

        val listOfShifts: List<UUID> = employeeCommonPreconditions.startAndStopShiftForSeveralProfiles(
            listOfProfileId = listOfProfileId,
            listOfUserRole = mutableListOf(ShiftUserRole.DELIVERYMAN, ShiftUserRole.PICKER),
            listOfDeliveryMethod = mutableListOf(null, null),
            listOfDarkstore = mutableListOf(Constants.darkstoreId, Constants.darkstoreId),
            amount = 2
        ).map { it.shiftId }


        employeeSchedulePreconditions
            .setTimesheetRequest(date = LocalDate.now())
            .setListCreateWorkedOutTimeslotRequest(
                2,
                timesheetId = employeeScheduleActions.getTimesheet(
                    Constants.darkstoreId,
                    employeeSchedulePreconditions.getTimesheetRequest()
                ).timesheetByDarkstore().timesheetId,
                listOfProfileId = listOfProfileId,
                listOfWorkedOutShiftId = listOfShifts,
                listOfWorkedOutHours = mutableListOf(0, 0),
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
            .getTimesheetById(employeeScheduleActions.timesheetByDarkstore().timesheetId)


        employeeScheduleAssertion
            .checkTimesheetAPI(
                employeeScheduleActions.timesheetByDarkstore(),
                modifiedAt = Instant.now().truncatedTo(ChronoUnit.MINUTES),
                listOfAccountContractId = employeeSchedulePreconditions.listOfCreateWorkedOutTimesheetRequest()
                    .map { it.accountingContractId },
                listOfWorkedOutHours = employeeSchedulePreconditions.listOfCreateWorkedOutTimesheetRequest()
                    .map { it.workedOutHours },
                listOfWorkedOutShiftId = employeeSchedulePreconditions.listOfCreateWorkedOutTimesheetRequest()
                    .map { it.workedOutShiftId },
                status = ApiEnum(TimesheetStatus.NEW),
                darkstoreId = Constants.darkstoreId,
                listOfProfileId = employeeSchedulePreconditions.listOfCreateWorkedOutTimesheetRequest()
                    .map { it.profileId }
            )

            .checkTimesheetAPI(
                employeeScheduleActions.timesheetById(),
                modifiedAt = Instant.now().truncatedTo(ChronoUnit.MINUTES),
                listOfAccountContractId = employeeSchedulePreconditions.listOfCreateWorkedOutTimesheetRequest()
                    .map { it.accountingContractId },
                listOfWorkedOutHours = employeeSchedulePreconditions.listOfCreateWorkedOutTimesheetRequest()
                    .map { it.workedOutHours },
                listOfWorkedOutShiftId = employeeSchedulePreconditions.listOfCreateWorkedOutTimesheetRequest()
                    .map { it.workedOutShiftId },
                status = ApiEnum(TimesheetStatus.NEW),
                darkstoreId = Constants.darkstoreId,
                listOfProfileId = employeeSchedulePreconditions.listOfCreateWorkedOutTimesheetRequest()
                    .map { it.profileId }
            )
            .checkTimesheetInDatabase(
                employeeScheduleDatabase.getTimesheet(employeeScheduleActions.timesheetByDarkstore().timesheetId),
                Constants.darkstoreId,
                TimesheetStatus.NEW.toString()
            )
            .checkTimesheetAutoTask(
                employeeScheduleDatabase.checkTaskExist(
                    "timesheetAutoSubmit",
                    employeeScheduleActions.timesheetByDarkstore().timesheetId.toString()
                )
            )


    }

    @Test
    @DisplayName("Check timesheet with 1 changed timeslot")
    fun checkTimesheetWithChangedTimeslotTest() {
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
            .getTimesheetById(employeeSchedulePreconditions.createWorkedOutTimesheetRequest().timesheetId)

        employeeScheduleAssertion
            .checkTimesheetAPI(
                employeeScheduleActions.timesheetByDarkstore(),
                modifiedAt = Instant.now().truncatedTo(ChronoUnit.MINUTES),
                listOfAccountContractId = listOf(employeeSchedulePreconditions.updateWorkedOutTimeSlotRequest().accountingContractId),
                listOfWorkedOutHours = listOf(employeeSchedulePreconditions.updateWorkedOutTimeSlotRequest().workedOutHours),
                listOfWorkedOutShiftId = listOf(employeeSchedulePreconditions.createWorkedOutTimesheetRequest().workedOutShiftId),
                status = ApiEnum(TimesheetStatus.NEW),
                darkstoreId = Constants.darkstoreId,
                listOfProfileId = listOf(employeeSchedulePreconditions.createWorkedOutTimesheetRequest().profileId)
            )

            .checkTimesheetAPI(
                employeeScheduleActions.timesheetById(),
                modifiedAt = Instant.now().truncatedTo(ChronoUnit.MINUTES),
                listOfAccountContractId = listOf(employeeSchedulePreconditions.updateWorkedOutTimeSlotRequest().accountingContractId),
                listOfWorkedOutHours = listOf(employeeSchedulePreconditions.updateWorkedOutTimeSlotRequest().workedOutHours),
                listOfWorkedOutShiftId = listOf(employeeSchedulePreconditions.createWorkedOutTimesheetRequest().workedOutShiftId),
                status = ApiEnum(TimesheetStatus.NEW),
                darkstoreId = Constants.darkstoreId,
                listOfProfileId = listOf(employeeSchedulePreconditions.createWorkedOutTimesheetRequest().profileId)

            )
            .checkTimesheetInDatabase(
                employeeScheduleDatabase.getTimesheet(employeeScheduleActions.timesheetByDarkstore().timesheetId),
                Constants.darkstoreId,
                TimesheetStatus.NEW.toString()
            )
            .checkTimesheetAutoTask(
                employeeScheduleDatabase.checkTaskExist(
                    "timesheetAutoSubmit",
                    employeeScheduleActions.timesheetByDarkstore().timesheetId.toString()
                )
            )


    }

    @Test
    @DisplayName("Check timesheet with max time timeslot")
    fun checkTimesheetWithMaxTimeTimeslotTest() {
        val listOfProfileId: MutableList<UUID> = employeeCommonPreconditions.createListOfProfiles(
            listOfRoles = mutableListOf(
                listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                listOf(ApiEnum(EmployeeRole.PICKER))
            ),
            listOfVehicle = mutableListOf(Vehicle(ApiEnum(EmployeeVehicleType.CAR)), null),
            listOfStaffPartner = mutableListOf(Constants.defaultStaffPartnerId, Constants.defaultStaffPartnerId),
            listOfDarkstore = mutableListOf(Constants.darkstoreId, Constants.darkstoreId),
            listOfMobile = mutableListOf(Constants.mobile1, Constants.mobile2),
            listOfName = mutableListOf(
                EmployeeName(
                    (StringAndPhoneNumberGenerator.generateRandomString(10)),
                    (StringAndPhoneNumberGenerator.generateRandomString(10)),
                    (StringAndPhoneNumberGenerator.generateRandomString(10))
                ),
                EmployeeName(
                    (StringAndPhoneNumberGenerator.generateRandomString(10)),
                    (StringAndPhoneNumberGenerator.generateRandomString(10)),
                    (StringAndPhoneNumberGenerator.generateRandomString(10))
                )
            ),
            listOfAccountingProfileIds = mutableListOf(
                Constants.accountingProfileIdForTimesheet1,
                Constants.accountingProfileIdForTimesheet2
            ),
            listOfRequisitionsId = mutableListOf(null, null),
            amount = 2
        )

        val listOfShifts: List<UUID> = employeeCommonPreconditions.startAndStopShiftForSeveralProfiles(
            listOfProfileId = listOfProfileId,
            listOfUserRole = mutableListOf(ShiftUserRole.DELIVERYMAN, ShiftUserRole.PICKER),
            listOfDeliveryMethod = mutableListOf(null, null),
            listOfDarkstore = mutableListOf(Constants.darkstoreId, Constants.darkstoreId),
            amount = 2
        ).map { it.shiftId }


        employeeSchedulePreconditions
            .setTimesheetRequest(date = LocalDate.now())
            .setListCreateWorkedOutTimeslotRequest(
                2,
                timesheetId = employeeScheduleActions.getTimesheet(
                    Constants.darkstoreId,
                    employeeSchedulePreconditions.getTimesheetRequest()
                ).timesheetByDarkstore().timesheetId,
                listOfProfileId = listOfProfileId,
                listOfWorkedOutShiftId = listOfShifts,
                listOfWorkedOutHours = mutableListOf(16, 16),
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
            .getTimesheetById(employeeScheduleActions.timesheetByDarkstore().timesheetId)


        employeeScheduleAssertion
            .checkTimesheetAPI(
                employeeScheduleActions.timesheetByDarkstore(),
                modifiedAt = Instant.now().truncatedTo(ChronoUnit.MINUTES),
                listOfAccountContractId = employeeSchedulePreconditions.listOfCreateWorkedOutTimesheetRequest()
                    .map { it.accountingContractId },
                listOfWorkedOutHours = employeeSchedulePreconditions.listOfCreateWorkedOutTimesheetRequest()
                    .map { it.workedOutHours },
                listOfWorkedOutShiftId = employeeSchedulePreconditions.listOfCreateWorkedOutTimesheetRequest()
                    .map { it.workedOutShiftId },
                status = ApiEnum(TimesheetStatus.NEW),
                darkstoreId = Constants.darkstoreId,
                listOfProfileId = employeeSchedulePreconditions.listOfCreateWorkedOutTimesheetRequest()
                    .map { it.profileId }
            )

            .checkTimesheetAPI(
                employeeScheduleActions.timesheetById(),
                modifiedAt = Instant.now().truncatedTo(ChronoUnit.MINUTES),
                listOfAccountContractId = employeeSchedulePreconditions.listOfCreateWorkedOutTimesheetRequest()
                    .map { it.accountingContractId },
                listOfWorkedOutHours = employeeSchedulePreconditions.listOfCreateWorkedOutTimesheetRequest()
                    .map { it.workedOutHours },
                listOfWorkedOutShiftId = employeeSchedulePreconditions.listOfCreateWorkedOutTimesheetRequest()
                    .map { it.workedOutShiftId },
                status = ApiEnum(TimesheetStatus.NEW),
                darkstoreId = Constants.darkstoreId,
                listOfProfileId = employeeSchedulePreconditions.listOfCreateWorkedOutTimesheetRequest()
                    .map { it.profileId }
            )
            .checkTimesheetInDatabase(
                employeeScheduleDatabase.getTimesheet(employeeScheduleActions.timesheetByDarkstore().timesheetId),
                Constants.darkstoreId,
                TimesheetStatus.NEW.toString()
            )

    }

    @Test
    @DisplayName("Check timesheet is not created when shift is not opened by deliveryman or picker ")
    fun checkTimesheetIsNotCreatedForDarkstoreAdminTest() {
        val profileId = employeeCommonPreconditions.createProfileGoodsManager().profileId
        employeeCommonPreconditions.startAndStopShift(profileId, userRole = ShiftUserRole.GOODS_MANAGER)
        employeeSchedulePreconditions
            .setTimesheetRequest(date = LocalDate.now())

        employeeScheduleActions
            .getTimesheetUnsuccessfully(
                darkstoreId = Constants.darkstoreId,
                employeeSchedulePreconditions.getTimesheetRequest()
            )

        commonAssertion
            .checkErrorMessage(employeeScheduleActions.timesheetByDarkstoreError().message, "Timesheet Not Found")
            .checkStatusNotFound(employeeScheduleActions.timesheetByDarkstoreResult().statusCode)

        employeeScheduleAssertion.checkTimesheetNotInDatabase(
            employeeScheduleDatabase.checkTimesheetExist(
                darkstoreId = Constants.darkstoreId,
                date = LocalDate.now()
            )
        )

    }

    @Test
    @DisplayName("Check empty timesheet")
    fun checkEmptyTimesheetTest() {
        val profileId = employeeCommonPreconditions.createProfileDeliveryman().profileId
        employeeCommonPreconditions.startShift(profileId)
        employeeSchedulePreconditions
            .setTimesheetRequest(date = LocalDate.now())

        employeeScheduleActions
            .getTimesheet(
                darkstoreId = Constants.darkstoreId,
                employeeSchedulePreconditions.getTimesheetRequest()
            )
            .getTimesheetById(employeeScheduleActions.timesheetByDarkstore().timesheetId)

        employeeScheduleAssertion
            .checkTimesheetWithoutTimeslotsAPI(
                employeeScheduleActions.timesheetByDarkstore(),
                status = ApiEnum(TimesheetStatus.NEW),
                darkstoreId = Constants.darkstoreId,
                modifiedAt = Instant.now().truncatedTo(ChronoUnit.MINUTES)
            )
            .checkTimesheetWithoutTimeslotsAPI(
                employeeScheduleActions.timesheetById(),
                status = ApiEnum(TimesheetStatus.NEW),
                darkstoreId = Constants.darkstoreId,
                modifiedAt = Instant.now().truncatedTo(ChronoUnit.MINUTES)
            )
            .checkTimesheetInDatabase(
                employeeScheduleDatabase.getTimesheet(employeeScheduleActions.timesheetByDarkstore().timesheetId),
                Constants.darkstoreId,
                TimesheetStatus.NEW.toString()
            )

    }

    @Test
    @DisplayName("Check not-existed timesheet")
    fun checkNotExistedTimesheetTest() {
        employeeSchedulePreconditions
            .setTimesheetRequest(date = LocalDate.now())

        employeeScheduleActions
            .getTimesheetByIdUnsuccessfully(
                timesheetId = UUID.randomUUID()
            )

        commonAssertion
            .checkErrorMessage(employeeScheduleActions.timesheetByIdError().message, "Timesheet Not Found")
            .checkStatusNotFound(employeeScheduleActions.timesheetByIdResult().statusCode)


    }


}