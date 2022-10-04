package ru.samokat.mysamokat.tests.tests.employee_schedule.timesheetAPI

import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.samokat.employeeprofiles.api.common.domain.EmployeeVehicleType
import ru.samokat.employeeprofiles.api.common.domain.Vehicle
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
class SubmitTimesheet {

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


    }

    @AfterEach
    fun release() {
        commonAssertion.assertAll()
        employeeScheduleAssertion.assertAll()
        employeeActions.deleteProfile(Constants.mobile1)
        employeeActions.deleteProfile(Constants.mobile2)
        employeeScheduleActions.deleteTimesheetInDB(darkstoreId = Constants.darkstoreId, date = LocalDate.now())


    }

    @Test
    @Tags(Tag("kafka_consume"), Tag("smoke"))
    @DisplayName("Submit timesheet with 1 timeslot")
    fun submitOneTimeslotTimesheetTest() {

        val profileId = employeeCommonPreconditions.createProfileDeliveryman(accountingProfileId = Constants.accountingProfileIdForTimesheet1).profileId
        employeeCommonPreconditions.startAndStopUpdatedShift(
            profileId, newDate = Instant.now().minusSeconds(10800).truncatedTo(
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


        employeeScheduleAssertion
            .checkBilledTimeslotInDatabase(
                employeeScheduleDatabase.getBilledTimeSlotByTimesheet(
                    employeeScheduleActions.timesheetByDarkstore().timesheetId
                ),
                count = 1,
                accountContractIds = mutableListOf(UUID.fromString(employeeActions.getContractIdFromDB(Constants.accountingProfileIdForTimesheet1))),
                totalWorkedHours = mutableListOf(3)
            )
            .checkBilledTimesheetKafkaTopic(
                employeeScheduleActions.getMessageFromKafkaBilledTimeslot(
                    employeeScheduleDatabase.getBilledTimeSlotId(
                        timesheetId = employeeScheduleActions.timesheetByDarkstore().timesheetId,
                        accountingContractId = UUID.fromString(employeeActions.getContractIdFromDB(Constants.accountingProfileIdForTimesheet1))
                    )

                ),
                darkstoreId = Constants.darkstoreId,
                accountingContractId = UUID.fromString(employeeActions.getContractIdFromDB(Constants.accountingProfileIdForTimesheet1)),
                workedOutHours = 3
            )


    }

    @Test
    @Tags(Tag("kafka_consume"), Tag("smoke"))
    @DisplayName("Submit timesheet with >1 timeslot for different users")
    fun submitSeveralDifferentTimeslotTimesheetTest() {

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
        employeeCommonPreconditions.startAndStopUpdatedSeveralShifts(
            listOfProfileId = listOfProfileId,
            listOfUserRole = mutableListOf(ShiftUserRole.DELIVERYMAN, ShiftUserRole.PICKER),
            listOfDeliveryMethod = mutableListOf(null, null),
            listOfDarkstore = mutableListOf(Constants.darkstoreId, Constants.darkstoreId),
            amount = 2,
            newDate = Instant.now().minusSeconds(10800).truncatedTo(
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
            .submitTimesheet(
                employeeScheduleActions.timesheetByDarkstore().timesheetId
            )

        employeeScheduleAssertion
            .checkBilledTimeslotInDatabase(
                employeeScheduleDatabase.getBilledTimeSlotByTimesheet(
                    employeeScheduleActions.timesheetByDarkstore().timesheetId
                ),
                count = 2,
                accountContractIds = mutableListOf(
                    UUID.fromString(
                        employeeActions.getContractIdFromDB(
                            Constants.accountingProfileIdForTimesheet1
                        )
                    ), UUID.fromString(
                        employeeActions.getContractIdFromDB(
                            Constants.accountingProfileIdForTimesheet2
                        )
                    )
                ),
                totalWorkedHours = mutableListOf(3, 3)
            )

            .checkBilledTimesheetKafkaTopic(
                employeeScheduleActions.getMessageFromKafkaBilledTimeslot(
                    employeeScheduleDatabase.getBilledTimeSlotId(
                        timesheetId = employeeScheduleActions.timesheetByDarkstore().timesheetId,
                        accountingContractId = UUID.fromString(employeeActions.getContractIdFromDB(Constants.accountingProfileIdForTimesheet1))
                    )

                ),
                darkstoreId = Constants.darkstoreId,
                accountingContractId = UUID.fromString(employeeActions.getContractIdFromDB(Constants.accountingProfileIdForTimesheet1)),
                workedOutHours = 3
            )

            .checkBilledTimesheetKafkaTopic(
                employeeScheduleActions.getMessageFromKafkaBilledTimeslot(
                    employeeScheduleDatabase.getBilledTimeSlotId(
                        timesheetId = employeeScheduleActions.timesheetByDarkstore().timesheetId,
                        accountingContractId = UUID.fromString(employeeActions.getContractIdFromDB(Constants.accountingProfileIdForTimesheet2))
                    )

                ),
                darkstoreId = Constants.darkstoreId,
                accountingContractId = UUID.fromString(employeeActions.getContractIdFromDB(Constants.accountingProfileIdForTimesheet2)),
                workedOutHours = 3
            )
    }


    @Test
    @Tags(Tag("kafka_consume"), Tag("smoke"))
    @DisplayName("Submit timesheet with >1 timeslot for one user with 1 contract")
    fun submitSeveralTimeslotForOneContractTimesheetTest() {
        val profileId = employeeCommonPreconditions.createProfileDeliveryman(accountingProfileId = Constants.accountingProfileIdForTimesheet1).profileId

        employeeCommonPreconditions.startAndStopUpdatedSeveralShifts(
            listOfProfileId = mutableListOf(profileId, profileId),
            listOfUserRole = mutableListOf(ShiftUserRole.DELIVERYMAN, ShiftUserRole.DELIVERYMAN),
            listOfDeliveryMethod = mutableListOf(null, null),
            listOfDarkstore = mutableListOf(Constants.darkstoreId, Constants.darkstoreId),
            amount = 2,
            newDate = Instant.now().minusSeconds(18000).truncatedTo(
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

        employeeScheduleAssertion
            .checkBilledTimeslotInDatabase(
                employeeScheduleDatabase.getBilledTimeSlotByTimesheet(
                    employeeScheduleActions.timesheetByDarkstore().timesheetId
                ),
                count = 1,
                accountContractIds = mutableListOf(
                    UUID.fromString(
                        employeeActions.getContractIdFromDB(
                            Constants.accountingProfileIdForTimesheet1
                        )
                    )
                ),
                totalWorkedHours = mutableListOf(10)
            )

            .checkBilledTimesheetKafkaTopic(
                employeeScheduleActions.getMessageFromKafkaBilledTimeslot(
                    employeeScheduleDatabase.getBilledTimeSlotId(
                        timesheetId = employeeScheduleActions.timesheetByDarkstore().timesheetId,
                        accountingContractId = UUID.fromString(employeeActions.getContractIdFromDB(Constants.accountingProfileIdForTimesheet1))
                    )

                ),
                darkstoreId = Constants.darkstoreId,
                accountingContractId = UUID.fromString(employeeActions.getContractIdFromDB(Constants.accountingProfileIdForTimesheet1)),
                workedOutHours = 10
            )

    }


    @Test
    @Tags(Tag("kafka_consume"), Tag("smoke"))
    @DisplayName("Submit timesheet with >1 timeslot for one user with >1 contract")
    fun submitSeveralTimeslotForSeveralContractsTimesheetTest() {

        val profileId = employeeCommonPreconditions.createProfileDeliveryman(
            accountingProfileId = Constants.accountingProfileIdWithTwoContracts).profileId

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
                    Constants.activeContract1,
                    Constants.activeContract2
                ),
                issuerId = UUID.randomUUID()
            )

        employeeScheduleActions
            .createSeveralWorkedOutTimeSlot(
                employeeSchedulePreconditions.listOfCreateWorkedOutTimesheetRequest()
            )
            .submitTimesheet(
                employeeScheduleActions.timesheetByDarkstore().timesheetId
            )

        employeeScheduleAssertion
            .checkBilledTimeslotInDatabase(
                employeeScheduleDatabase.getBilledTimeSlotByTimesheet(
                    employeeScheduleActions.timesheetByDarkstore().timesheetId
                ),
                count = 2,
                accountContractIds = mutableListOf(
                    Constants.activeContract1, Constants.activeContract2
                ),
                totalWorkedHours = mutableListOf(6, 7)
            )

            .checkBilledTimesheetKafkaTopic(
                employeeScheduleActions.getMessageFromKafkaBilledTimeslot(
                    employeeScheduleDatabase.getBilledTimeSlotId(
                        timesheetId = employeeScheduleActions.timesheetByDarkstore().timesheetId,
                        accountingContractId = Constants.activeContract1
                    )

                ),
                darkstoreId = Constants.darkstoreId,
                accountingContractId = Constants.activeContract1,
                workedOutHours = 6

            )
            .checkBilledTimesheetKafkaTopic(
                employeeScheduleActions.getMessageFromKafkaBilledTimeslot(
                    employeeScheduleDatabase.getBilledTimeSlotId(
                        timesheetId = employeeScheduleActions.timesheetByDarkstore().timesheetId,
                        accountingContractId = Constants.activeContract2
                    )

                ),
                darkstoreId = Constants.darkstoreId,
                accountingContractId = Constants.activeContract2,
                workedOutHours = 7
            )

    }


    @Test
    @DisplayName("Submit empty timesheet")
    fun submitEmptyTimesheetTest() {

        val profileId = employeeCommonPreconditions.createProfileDeliveryman(
            accountingProfileId = Constants.accountingProfileIdForTimesheet1).profileId

        employeeCommonPreconditions.startAndStopShift(profileId).shiftId
        employeeSchedulePreconditions
            .setTimesheetRequest(date = LocalDate.now())

        employeeScheduleActions
            .submitTimesheet(
                employeeScheduleActions.getTimesheet(
                    Constants.darkstoreId,
                    employeeSchedulePreconditions.getTimesheetRequest()
                ).timesheetByDarkstore().timesheetId
            )

        employeeScheduleAssertion
            .checkBilledTimeslotNotInDatabase(
                employeeScheduleDatabase.checkBilledTimeSlotExist(
                    employeeScheduleActions.timesheetByDarkstore().timesheetId, UUID.fromString(
                        employeeActions.getContractIdFromDB(
                            Constants.accountingProfileIdForTimesheet1
                        )
                    )
                )
            )

    }


    @Test
    @DisplayName("Submit timesheet with timeslot = 0")
    fun submitZeroTimeslotTimesheetTest() {

        val profileId1 = employeeCommonPreconditions.createProfileDeliveryman().profileId

        val workedOutShiftId = employeeCommonPreconditions.startAndStopShift(profileId1).shiftId

        val profileId2 = employeeCommonPreconditions.createProfileDeliveryman(
            accountingProfileId = Constants.accountingProfileIdForTimesheet1,
        mobile = Constants.mobile2).profileId

        employeeCommonPreconditions.startAndStopUpdatedShift(
            profileId2, newDate = Instant.now().minusSeconds(10800).truncatedTo(
                ChronoUnit.NANOS
            )
        )

        employeeSchedulePreconditions
            .setTimesheetRequest(date = LocalDate.now())
            .setCreateWorkedOutTimeslotRequest(
                timesheetId = employeeScheduleActions.getTimesheet(
                    Constants.darkstoreId,
                    employeeSchedulePreconditions.getTimesheetRequest()
                ).timesheetByDarkstore().timesheetId,
                profileId = profileId1,
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
            .submitTimesheet(
                employeeScheduleActions.getTimesheet(
                    Constants.darkstoreId,
                    employeeSchedulePreconditions.getTimesheetRequest()
                ).timesheetByDarkstore().timesheetId
            )

        employeeScheduleAssertion
            .checkBilledTimeslotNotInDatabase(
                employeeScheduleDatabase.checkBilledTimeSlotExist(
                    employeeScheduleActions.timesheetByDarkstore().timesheetId,
                    employeeSchedulePreconditions.createWorkedOutTimesheetRequest().accountingContractId

                )
            )
            .checkBilledTimeslotInDatabase(
                employeeScheduleDatabase.getBilledTimeSlotByTimesheet(
                    employeeScheduleActions.timesheetByDarkstore().timesheetId
                ),
                count = 1,
                accountContractIds = mutableListOf(
                    UUID.fromString(
                        employeeActions.getContractIdFromDB(
                            Constants.accountingProfileIdForTimesheet1
                        )
                    )
                ),
                totalWorkedHours = mutableListOf(3)
            )


    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Submit fake timesheet is impossible")
    fun submitSubmittedTimesheetTest() {

        employeeScheduleActions
            .submitTimesheetUnsuccessful(
                timesheetId = UUID.fromString("4689a1ab-24f1-11ec-9189-ec0d9ab1c881")
            )

        commonAssertion
            .checkErrorMessage(employeeScheduleActions.submitResultError().message, "Timesheet Not Found")


    }


}
