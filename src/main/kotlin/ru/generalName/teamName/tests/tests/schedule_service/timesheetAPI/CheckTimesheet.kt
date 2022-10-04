package ru.generalName.teamName.tests.tests.schedule_service.timesheetAPI

import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.generalName.teamName.tests.checkers.CommonAssertion
import ru.generalName.teamName.tests.checkers.ScheduleAssertions
import ru.generalName.teamName.tests.dataproviders.Constants
import ru.generalName.teamName.tests.dataproviders.preconditions.CommonPreconditions
import ru.generalName.teamName.tests.dataproviders.preconditions.SchedulePreconditions
import ru.generalName.teamName.tests.helpers.actions.ProfileActions
import ru.generalName.teamName.tests.helpers.actions.ScheduleActions
import ru.generalName.teamName.tests.helpers.controllers.database.ScheduleDatabaseController
import java.time.Instant
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.*


@SpringBootTest
@Tag("schedule")
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class CheckTimesheet {

    private lateinit var scheduleAssertions: ScheduleAssertions
    private lateinit var commonAssertion: CommonAssertion

    @Autowired
    private lateinit var profileActions: ProfileActions

    @Autowired
    private lateinit var commonPreconditions: CommonPreconditions

    private lateinit var schedulePreconditions: SchedulePreconditions

    @Autowired
    private lateinit var scheduleActions: ScheduleActions

    @Autowired
    private lateinit var scheduleDatabase: ScheduleDatabaseController

    @BeforeEach
    fun before() {
        schedulePreconditions = SchedulePreconditions()
        scheduleAssertions = ScheduleAssertions()
        commonAssertion = CommonAssertion()
        profileActions.deleteProfile(Constants.mobile1)
        profileActions.deleteProfile(Constants.mobile2)
        scheduleActions.deleteTimesheetInDB(darkstoreId = Constants.darkstoreId, date = LocalDate.now())
        scheduleActions.deleteTimesheetInDB(
            darkstoreId = Constants.darkstoreId,
            date = LocalDate.parse("2021-11-11")
        )
    }

    @AfterEach
    fun release() {
        commonAssertion.assertAll()
        scheduleAssertions.assertAll()
        profileActions.deleteProfile(Constants.mobile1)
        profileActions.deleteProfile(Constants.mobile2)
        scheduleActions.deleteTimesheetInDB(darkstoreId = Constants.darkstoreId, date = LocalDate.now())
        scheduleActions.deleteTimesheetInDB(
            darkstoreId = Constants.darkstoreId,
            date = LocalDate.parse("2021-11-11")
        )

    }


    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Check new timesheet with timeSlots for different roles")
    fun checkNewTimesheetTest() {

        val listOfProfileId: MutableList<UUID> = commonPreconditions.createListOfProfiles(
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

        val listOfShifts: List<UUID> = commonPreconditions.startAndStopShiftForSeveralProfiles(
            listOfProfileId = listOfProfileId,
            listOfUserRole = mutableListOf(ShiftUserRole.DELIVERYMAN, ShiftUserRole.PICKER),
            listOfDeliveryMethod = mutableListOf(null, null),
            listOfDarkstore = mutableListOf(Constants.darkstoreId, Constants.darkstoreId),
            amount = 2
        ).map { it.shiftId }


        schedulePreconditions
            .setTimesheetRequest(date = LocalDate.now())
            .setListCreateWorkedOutTimeslotRequest(
                2,
                timesheetId = scheduleActions.getTimesheet(
                    Constants.darkstoreId,
                    schedulePreconditions.getTimesheetRequest()
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

        scheduleActions
            .createSeveralWorkedOutTimeSlot(
                schedulePreconditions.listOfCreateWorkedOutTimesheetRequest()
            )
            .getTimesheetById(scheduleActions.timesheetByDarkstore().timesheetId)


        scheduleAssertions
            .checkTimesheetAPI(
                scheduleActions.timesheetByDarkstore(),
                modifiedAt = Instant.now().truncatedTo(ChronoUnit.MINUTES),
                listOfAccountContractId = schedulePreconditions.listOfCreateWorkedOutTimesheetRequest()
                    .map { it.accountingContractId },
                listOfWorkedOutHours = schedulePreconditions.listOfCreateWorkedOutTimesheetRequest()
                    .map { it.workedOutHours },
                listOfWorkedOutShiftId = schedulePreconditions.listOfCreateWorkedOutTimesheetRequest()
                    .map { it.workedOutShiftId },
                status = ApiEnum(TimesheetStatus.NEW),
                darkstoreId = Constants.darkstoreId,
                listOfProfileId = schedulePreconditions.listOfCreateWorkedOutTimesheetRequest()
                    .map { it.profileId }
            )

            .checkTimesheetAPI(
                scheduleActions.timesheetById(),
                modifiedAt = Instant.now().truncatedTo(ChronoUnit.MINUTES),
                listOfAccountContractId = schedulePreconditions.listOfCreateWorkedOutTimesheetRequest()
                    .map { it.accountingContractId },
                listOfWorkedOutHours = schedulePreconditions.listOfCreateWorkedOutTimesheetRequest()
                    .map { it.workedOutHours },
                listOfWorkedOutShiftId = schedulePreconditions.listOfCreateWorkedOutTimesheetRequest()
                    .map { it.workedOutShiftId },
                status = ApiEnum(TimesheetStatus.NEW),
                darkstoreId = Constants.darkstoreId,
                listOfProfileId = schedulePreconditions.listOfCreateWorkedOutTimesheetRequest()
                    .map { it.profileId }
            )
            .checkTimesheetInDatabase(
                scheduleDatabase.getTimesheet(scheduleActions.timesheetByDarkstore().timesheetId),
                Constants.darkstoreId,
                TimesheetStatus.NEW.toString()
            )
            .checkTimesheetAutoTask(
                scheduleDatabase.checkTaskExist(
                    "timesheetAutoSubmit",
                    scheduleActions.timesheetByDarkstore().timesheetId.toString()
                )
            )


    }


    @Test
    @DisplayName("Check not-existed timesheet")
    fun checkNotExistedTimesheetTest() {
        schedulePreconditions
            .setTimesheetRequest(date = LocalDate.now())

        scheduleActions
            .getTimesheetByIdUnsuccessfully(
                timesheetId = UUID.randomUUID()
            )

        commonAssertion
            .checkErrorMessage(scheduleActions.timesheetByIdError().message, "Timesheet Not Found")
            .checkStatusNotFound(scheduleActions.timesheetByIdResult().statusCode)


    }


}