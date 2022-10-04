package ru.samokat.mysamokat.tests.tests.staff_apigateway.usersAPI

import org.apache.http.HttpStatus
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.samokat.employeeprofiles.api.common.domain.EmployeeVehicleType
import ru.samokat.employeeprofiles.api.common.domain.Vehicle
import ru.samokat.my.domain.employee.EmployeeName
import ru.samokat.my.domain.employee.EmployeeRole
import ru.samokat.my.domain.shifts.ShiftAssignmentCancellationReason
import ru.samokat.my.domain.shifts.TimeRange
import ru.samokat.my.rest.api.enum.ApiEnum
import ru.samokat.mysamokat.tests.checkers.CommonAssertion
import ru.samokat.mysamokat.tests.checkers.StaffApiGWAssertions
import ru.samokat.mysamokat.tests.dataproviders.Constants
import ru.samokat.mysamokat.tests.dataproviders.StringAndPhoneNumberGenerator
import ru.samokat.mysamokat.tests.dataproviders.preconditions.CommonPreconditions
import ru.samokat.mysamokat.tests.dataproviders.preconditions.EmployeePreconditions
import ru.samokat.mysamokat.tests.dataproviders.preconditions.StaffApiGWPreconditions
import ru.samokat.mysamokat.tests.helpers.actions.EmployeeActions
import ru.samokat.mysamokat.tests.helpers.actions.StaffApiGWActions
import ru.samokat.shifts.api.common.domain.AssigneeRole
import ru.samokat.shifts.api.common.domain.DeliveryMethod
import ru.samokat.shifts.api.common.domain.ShiftUserRole
import java.time.Instant
import java.util.*


@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Tags(Tag("staff_apigateway"))
class Statistics {
    private var staffApiGWPreconditions: StaffApiGWPreconditions = StaffApiGWPreconditions()

    private lateinit var staffApiGWAssertions: StaffApiGWAssertions
    private lateinit var commonAssertions: CommonAssertion

    @Autowired
    private lateinit var staffApiGWActions: StaffApiGWActions

    @Autowired
    private lateinit var employeeActions: EmployeeActions

    @Autowired
    private lateinit var commonPreconditions: CommonPreconditions


    private lateinit var employeePreconditions: EmployeePreconditions

    @BeforeEach
    fun before() {
        this.oneDayRange = staffApiGWPreconditions.get24TimeRange()
        this.halfDayRange = staffApiGWPreconditions.get12TimeRange()
        this.twoHoursRange = staffApiGWPreconditions.get2TimeRange()
        this.threeHoursRange = staffApiGWPreconditions.get3TimeRange()
        employeePreconditions = EmployeePreconditions()
        staffApiGWAssertions = StaffApiGWAssertions()
        commonAssertions = CommonAssertion()
        employeeActions.deleteProfile(Constants.mobile1)
        employeeActions.deleteProfile(Constants.mobile2)
        employeeActions.deleteProfile(Constants.mobile3)
        employeeActions.deleteProfile(Constants.mobile4)
        employeeActions.deleteProfile(Constants.mobile5)
        employeeActions.deleteProfile(Constants.mobile6)
        employeeActions.deleteProfile(Constants.mobile7)
        employeeActions.deleteProfile(Constants.mobile8)
        commonPreconditions.clearAssignmentsFromDatabase(
            oneDayRange
        )
        commonPreconditions.clearAssignmentsFromDatabase(
            oneDayRange,
            Constants.updatedDarkstoreId
        )
        commonPreconditions.clearAssignmentsFromDatabase(
            oneDayRange,
            Constants.darkstoreForTimesheet
        )
        commonPreconditions.clearAssignmentsFromDatabase(
            oneDayRange,
            Constants.darkstoreIdWithNotMoscowTimezone
        )

    }

    @AfterEach
    fun release() {
        staffApiGWAssertions.assertAll()
        commonAssertions.assertAll()
        employeeActions.deleteProfile(Constants.mobile1)
        employeeActions.deleteProfile(Constants.mobile2)
        employeeActions.deleteProfile(Constants.mobile3)
        employeeActions.deleteProfile(Constants.mobile4)
        employeeActions.deleteProfile(Constants.mobile5)
        employeeActions.deleteProfile(Constants.mobile6)
        employeeActions.deleteProfile(Constants.mobile7)
        employeeActions.deleteProfile(Constants.mobile8)
        commonPreconditions.clearAssignmentsFromDatabase(
            oneDayRange
        )
        commonPreconditions.clearAssignmentsFromDatabase(
            oneDayRange,
            Constants.updatedDarkstoreId
        )
        commonPreconditions.clearAssignmentsFromDatabase(
            oneDayRange,
            Constants.darkstoreForTimesheet
        )
        commonPreconditions.clearAssignmentsFromDatabase(
            oneDayRange,
            Constants.darkstoreIdWithNotMoscowTimezone
        )
    }

    private lateinit var oneDayRange: TimeRange
    private lateinit var halfDayRange: TimeRange
    private lateinit var twoHoursRange: TimeRange
    private lateinit var threeHoursRange: TimeRange

    @Test
    @Tags(Tag("smoke"), Tag("shiftsIntegration"))
    @DisplayName("Get statistic of deliveryman by darkstore_admin: check assignments, worked_out_shifts, timeslots")
    fun getStatisticsDeliveryman() {

        val tokens = commonPreconditions
            .createAndAuthorizeStaffUser(
                roles = listOf(ApiEnum(EmployeeRole.DARKSTORE_ADMIN)),
                vehicle = null,
                staffPartnerId = null
            )

        val createDeliverymen = commonPreconditions.createListOfProfiles(
            listOfRoles = mutableListOf(
                listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                listOf(ApiEnum(EmployeeRole.DELIVERYMAN), ApiEnum(EmployeeRole.PICKER))
            ),
            listOfVehicle = mutableListOf(
                Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
                Vehicle(ApiEnum(EmployeeVehicleType.BICYCLE)),
                Vehicle(ApiEnum(EmployeeVehicleType.MOTOCYCLE)),
                Vehicle(ApiEnum(EmployeeVehicleType.ELECTRIC_BICYCLE))
            ),
            listOfStaffPartner = mutableListOf(
                Constants.defaultStaffPartnerId,
                Constants.defaultStaffPartnerId,
                Constants.defaultStaffPartnerId,
                Constants.defaultStaffPartnerId
            ),
            listOfDarkstore = mutableListOf(
                Constants.darkstoreId,
                Constants.darkstoreId,
                Constants.darkstoreId,
                Constants.darkstoreId
            ),
            listOfMobile = mutableListOf(
                Constants.mobile2,
                Constants.mobile3,
                Constants.mobile4,
                Constants.mobile5
            ),
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
                ),
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
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString()
            ),
            listOfRequisitionsId = mutableListOf(null, null, null, null),
            amount = 4
        )

        val createPicker = commonPreconditions.createProfilePicker(mobile = Constants.mobile7).profileId

        commonPreconditions.startAndStopShiftForSeveralProfiles(
            createDeliverymen,
            mutableListOf(
                ShiftUserRole.DELIVERYMAN,
                ShiftUserRole.DELIVERYMAN,
                ShiftUserRole.DELIVERYMAN,
                ShiftUserRole.PICKER
            ),
            mutableListOf(
                DeliveryMethod.WITHOUT_CAR,
                DeliveryMethod.WITHOUT_CAR,
                DeliveryMethod.WITHOUT_CAR,
                DeliveryMethod.WITHOUT_CAR
            ),
            mutableListOf(
                Constants.darkstoreId,
                Constants.darkstoreId,
                Constants.darkstoreId,
                Constants.darkstoreId
            ),
            4
        )

        commonPreconditions.createAssignmentForSeveralProfiles(
            profileId = createDeliverymen,
            range = oneDayRange,
            assignmentRange = halfDayRange,
            role = mutableListOf(
                AssigneeRole.DELIVERYMAN,
                AssigneeRole.DELIVERYMAN,
                AssigneeRole.DELIVERYMAN,
                AssigneeRole.PICKER
            ),
            darkstoreId = mutableListOf(
                Constants.darkstoreId,
                Constants.updatedDarkstoreId,
                Constants.darkstoreForTimesheet,
                Constants.darkstoreIdWithNotMoscowTimezone
            )
        )

        commonPreconditions.postSchedulesForSeveralProfiles(
            createDeliverymen,
            timeRange = TimeRange(
                startingAt = Instant.now().minusSeconds(7200),
                endingAt = Instant.now().plusSeconds(7200)
            ),
            schedule = listOf(
                TimeRange(
                    startingAt = Instant.now().minusSeconds(7200),
                    endingAt = Instant.now().plusSeconds(3600)
                )
            )
        )

        val statistic = staffApiGWActions.getStatistic(
            accessToken = tokens!!.accessToken,
            Constants.darkstoreId.toString(),
            mutableListOf("deliveryman"),
            from = Instant.now().minusSeconds(46800).toEpochMilli(),
            to = Instant.now().plusSeconds(43200).toEpochMilli(),
        )

        staffApiGWAssertions.checkStatistic(
            statistic = statistic,
            profileIds = createDeliverymen,
            scheduleDuration = 3L,
            assignmentsDuration = 12L,
            assignmentsCount = 1,
            mistakenAssignmentsCount = 0,
            cancelledByIssuerAssignmentsCount = 0,
            cancelledByAssigneeAssignmentsCount = 0,
            absenceAssignmentsCount = 0,
            workedOutShiftsCount = 1,
            workedOutShiftsDuration = 0L
        )
            .checkEmployeeAbsentInStatistic(statistic, createPicker)


    }

    @Test
    @Tags(Tag("smoke"), Tag("shiftsIntegration"))
    @DisplayName("Get statistic of deliveryman: check several assignments, several worked_out_shifts, several timeslots")
    fun getStatisticsDeliverymanWithSeveralShifts() {

        val tokens = commonPreconditions
            .createAndAuthorizeStaffUser(
                roles = listOf(ApiEnum(EmployeeRole.DARKSTORE_ADMIN)),
                vehicle = null,
                staffPartnerId = null,
                darkstoreId = Constants.updatedDarkstoreId
            )


        val createDeliveryman = commonPreconditions.createProfileDeliveryman(mobile = Constants.mobile8, darkstoreId = Constants.updatedDarkstoreId).profileId

        commonPreconditions.startAndStopUpdatedShift(
            createDeliveryman,
            ShiftUserRole.DELIVERYMAN,
            DeliveryMethod.WITHOUT_CAR,
            Constants.updatedDarkstoreId,
            Instant.now().minusSeconds(7200)

        )

        commonPreconditions.startAndStopShift(
            createDeliveryman,
            ShiftUserRole.DELIVERYMAN,
            DeliveryMethod.WITHOUT_CAR,
            Constants.darkstoreId

        )

        commonPreconditions.createSeveralAssignment(
            profileId = createDeliveryman,
            range = oneDayRange,
            assignmentRanges = listOf(threeHoursRange, twoHoursRange),
            role =
            AssigneeRole.DELIVERYMAN,
            darkstoreId = Constants.darkstoreId

        )

        commonPreconditions.postSchedulesForSeveralProfiles(
            mutableListOf(createDeliveryman),
            timeRange = TimeRange(
                startingAt = Instant.now().minusSeconds(7200),
                endingAt = Instant.now().plusSeconds(7200)
            ),
            schedule = listOf(
                TimeRange(
                    startingAt = Instant.now().minusSeconds(7200),
                    endingAt = Instant.now().plusSeconds(3540)
                ),
                TimeRange(
                    startingAt = Instant.now().plusSeconds(3600),
                    endingAt = Instant.now().plusSeconds(7200)
                )

            )
        )


        val statistic = staffApiGWActions.getStatistic(
            accessToken = tokens!!.accessToken,
            Constants.updatedDarkstoreId.toString(),
            mutableListOf("deliveryman"),
            from = Instant.now().minusSeconds(46800).toEpochMilli(),
            to = Instant.now().plusSeconds(43200).toEpochMilli(),
        )

        staffApiGWAssertions.checkStatistic(
            statistic = statistic,
            profileIds = mutableListOf(createDeliveryman),
            scheduleDuration = 3L,
            assignmentsDuration = 5L,
            assignmentsCount = 2,
            mistakenAssignmentsCount = 0,
            cancelledByIssuerAssignmentsCount = 0,
            cancelledByAssigneeAssignmentsCount = 0,
            absenceAssignmentsCount = 0,
            workedOutShiftsCount = 2,
            workedOutShiftsDuration = 2L
        )


    }


    @Test
    @Tags(Tag("smoke"), Tag("shiftsIntegration"))
    @DisplayName("Get statistic of picker by goods_manager: check assignments, worked_out_shifts, timeslots")
    fun getStatisticsPickers() {

        val tokens = commonPreconditions
            .createAndAuthorizeStaffUser(
                roles = listOf(ApiEnum(EmployeeRole.GOODS_MANAGER)),
                vehicle = null,
                staffPartnerId = null
            )

        val createPickers = commonPreconditions.createListOfProfiles(
            listOfRoles = mutableListOf(
                listOf(ApiEnum(EmployeeRole.PICKER)),
                listOf(ApiEnum(EmployeeRole.PICKER)),
                listOf(ApiEnum(EmployeeRole.PICKER)),
                listOf(ApiEnum(EmployeeRole.DELIVERYMAN), ApiEnum(EmployeeRole.PICKER))
            ),
            listOfVehicle = mutableListOf(
                null,
                null,
                null,
                Vehicle(ApiEnum(EmployeeVehicleType.ELECTRIC_BICYCLE))
            ),
            listOfStaffPartner = mutableListOf(
                Constants.defaultStaffPartnerId,
                Constants.defaultStaffPartnerId,
                Constants.defaultStaffPartnerId,
                Constants.defaultStaffPartnerId
            ),
            listOfDarkstore = mutableListOf(
                Constants.darkstoreId,
                Constants.darkstoreId,
                Constants.darkstoreId,
                Constants.darkstoreId
            ),
            listOfMobile = mutableListOf(
                Constants.mobile2,
                Constants.mobile3,
                Constants.mobile4,
                Constants.mobile5
            ),
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
                ),
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
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString()
            ),
            listOfRequisitionsId = mutableListOf(null, null, null, null),
            amount = 4
        )

        val createDeliveryman = commonPreconditions.createProfileDeliveryman(mobile = Constants.mobile7).profileId

        commonPreconditions.startAndStopShiftForSeveralProfiles(
            createPickers,
            mutableListOf(
                ShiftUserRole.PICKER,
                ShiftUserRole.PICKER,
                ShiftUserRole.PICKER,
                ShiftUserRole.DELIVERYMAN
            ),
            mutableListOf(
                DeliveryMethod.WITHOUT_CAR,
                DeliveryMethod.WITHOUT_CAR,
                DeliveryMethod.WITHOUT_CAR,
                DeliveryMethod.WITHOUT_CAR
            ),
            mutableListOf(
                Constants.darkstoreId,
                Constants.darkstoreId,
                Constants.darkstoreId,
                Constants.darkstoreId
            ),
            4
        )

        commonPreconditions.createAssignmentForSeveralProfiles(
            profileId = createPickers,
            range = oneDayRange,
            assignmentRange = halfDayRange,
            role = mutableListOf(
                AssigneeRole.PICKER,
                AssigneeRole.PICKER,
                AssigneeRole.PICKER,
                AssigneeRole.DELIVERYMAN
            ),
            darkstoreId = mutableListOf(
                Constants.darkstoreId,
                Constants.updatedDarkstoreId,
                Constants.darkstoreForTimesheet,
                Constants.darkstoreIdWithNotMoscowTimezone
            )
        )

        commonPreconditions.postSchedulesForSeveralProfiles(
            createPickers,
            timeRange = TimeRange(
                startingAt = Instant.now().minusSeconds(7200),
                endingAt = Instant.now().plusSeconds(7200)
            ),
            schedule = listOf(
                TimeRange(
                    startingAt = Instant.now().minusSeconds(7200),
                    endingAt = Instant.now().plusSeconds(3600)
                )
            )
        )

        val statistic = staffApiGWActions.getStatistic(
            accessToken = tokens!!.accessToken,
            Constants.darkstoreId.toString(),
            mutableListOf("picker"),
            from = Instant.now().minusSeconds(46800).toEpochMilli(),
            to = Instant.now().plusSeconds(43200).toEpochMilli(),
        )

        staffApiGWAssertions.checkStatistic(
            statistic = statistic,
            profileIds = createPickers,
            scheduleDuration = 3L,
            assignmentsDuration = 12L,
            assignmentsCount = 1,
            mistakenAssignmentsCount = 0,
            cancelledByIssuerAssignmentsCount = 0,
            cancelledByAssigneeAssignmentsCount = 0,
            absenceAssignmentsCount = 0,
            workedOutShiftsCount = 1,
            workedOutShiftsDuration = 0L
        )
            .checkEmployeeAbsentInStatistic(statistic, createDeliveryman)


    }

    @Test
    @Tags(Tag("smoke"), Tag("shiftsIntegration"))
    @DisplayName("Get statistic by coordinator")
    fun getStatisticsDeliverymanPickers() {

        val tokens = commonPreconditions
            .createAndAuthorizeStaffUser(
                roles = listOf(ApiEnum(EmployeeRole.COORDINATOR)),
                vehicle = null,
                staffPartnerId = null,
                darkstoreId = null,
                supervisedDarkstores = Constants.supervisedDarkstores,
                cityId = null
            )


        val createDeliverymenAndPickers = commonPreconditions.createListOfProfiles(
            listOfRoles = mutableListOf(
                listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                listOf(ApiEnum(EmployeeRole.PICKER)),
                listOf(ApiEnum(EmployeeRole.PICKER), ApiEnum(EmployeeRole.DELIVERYMAN)),
                listOf(ApiEnum(EmployeeRole.DELIVERYMAN), ApiEnum(EmployeeRole.PICKER))
            ),
            listOfVehicle = mutableListOf(
                Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
                null,
                Vehicle(ApiEnum(EmployeeVehicleType.BICYCLE)),
                Vehicle(ApiEnum(EmployeeVehicleType.ELECTRIC_BICYCLE))
            ),
            listOfStaffPartner = mutableListOf(
                Constants.defaultStaffPartnerId,
                Constants.defaultStaffPartnerId,
                Constants.defaultStaffPartnerId,
                Constants.defaultStaffPartnerId
            ),
            listOfDarkstore = mutableListOf(
                Constants.darkstoreId,
                Constants.darkstoreId,
                Constants.darkstoreId,
                Constants.darkstoreId
            ),
            listOfMobile = mutableListOf(
                Constants.mobile2,
                Constants.mobile3,
                Constants.mobile4,
                Constants.mobile5
            ),
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
                ),
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
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString()
            ),
            listOfRequisitionsId = mutableListOf(null, null, null, null),
            amount = 4
        )


        commonPreconditions.startAndStopShiftForSeveralProfiles(
            createDeliverymenAndPickers,
            mutableListOf(
                ShiftUserRole.DELIVERYMAN,
                ShiftUserRole.PICKER,
                ShiftUserRole.PICKER,
                ShiftUserRole.DELIVERYMAN
            ),
            mutableListOf(
                DeliveryMethod.WITHOUT_CAR,
                DeliveryMethod.WITHOUT_CAR,
                DeliveryMethod.WITHOUT_CAR,
                DeliveryMethod.WITHOUT_CAR
            ),
            mutableListOf(
                Constants.darkstoreId,
                Constants.darkstoreId,
                Constants.darkstoreId,
                Constants.darkstoreId
            ),
            4
        )

        commonPreconditions.createAssignmentForSeveralProfiles(
            profileId = createDeliverymenAndPickers,
            range = oneDayRange,
            assignmentRange = halfDayRange,
            role = mutableListOf(
                AssigneeRole.DELIVERYMAN,
                AssigneeRole.PICKER,
                AssigneeRole.PICKER,
                AssigneeRole.DELIVERYMAN
            ),
            darkstoreId = mutableListOf(
                Constants.darkstoreId,
                Constants.updatedDarkstoreId,
                Constants.darkstoreForTimesheet,
                Constants.darkstoreIdWithNotMoscowTimezone
            )
        )

        commonPreconditions.postSchedulesForSeveralProfiles(
            createDeliverymenAndPickers,
            timeRange = TimeRange(
                startingAt = Instant.now().minusSeconds(7200),
                endingAt = Instant.now().plusSeconds(7200)
            ),
            schedule = listOf(
                TimeRange(
                    startingAt = Instant.now().minusSeconds(7200),
                    endingAt = Instant.now().plusSeconds(3600)
                )
            )
        )

        val statistic = staffApiGWActions.getStatistic(
            accessToken = tokens!!.accessToken,
            Constants.darkstoreId.toString(),
            mutableListOf("picker", "deliveryman"),
            from = Instant.now().minusSeconds(46800).toEpochMilli(),
            to = Instant.now().plusSeconds(43200).toEpochMilli(),
        )

        staffApiGWAssertions.checkStatistic(
            statistic = statistic,
            profileIds = createDeliverymenAndPickers,
            scheduleDuration = 3L,
            assignmentsDuration = 12L,
            assignmentsCount = 1,
            mistakenAssignmentsCount = 0,
            cancelledByIssuerAssignmentsCount = 0,
            cancelledByAssigneeAssignmentsCount = 0,
            absenceAssignmentsCount = 0,
            workedOutShiftsCount = 1,
            workedOutShiftsDuration = 0L
        )


    }


    @Test
    @DisplayName("Get statistic: started in request period shifts are shown ")
    fun getStatisticsShifts() {
        val tokens = commonPreconditions
            .createAndAuthorizeStaffUser(
                roles = listOf(ApiEnum(EmployeeRole.DARKSTORE_ADMIN)),
                vehicle = null,
                staffPartnerId = null
            )


        val createDeliveryman = commonPreconditions.createProfileDeliveryman(mobile = Constants.mobile7).profileId


        commonPreconditions.createAssignment(
            profileId = createDeliveryman,
            range = TimeRange(
                startingAt = Instant.now().minusSeconds(7200),
                endingAt = Instant.now().plusSeconds(7200)
            ),
            assignmentRange = TimeRange(
                startingAt = Instant.now().minusSeconds(7200),
                endingAt = Instant.now().plusSeconds(7200)
            ),
            role =
            AssigneeRole.DELIVERYMAN,
            darkstoreId = Constants.darkstoreId

        )

        commonPreconditions.postSchedulesForSeveralProfiles(
            mutableListOf(createDeliveryman),
            timeRange = TimeRange(
                startingAt = Instant.now().minusSeconds(7200),
                endingAt = Instant.now().plusSeconds(7200)
            ),
            schedule = listOf(
                TimeRange(
                    startingAt = Instant.now().minusSeconds(7200),
                    endingAt = Instant.now().plusSeconds(7200)
                )
            )
        )

        val statistic = staffApiGWActions.getStatistic(
            accessToken = tokens!!.accessToken,
            Constants.darkstoreId.toString(),
            mutableListOf("deliveryman"),
            from = Instant.now().minusSeconds(46800).toEpochMilli(),
            to = Instant.now().plusSeconds(3600).toEpochMilli(),
        )

        staffApiGWAssertions.checkStatistic(
            statistic = statistic,
            profileIds = mutableListOf(createDeliveryman),
            scheduleDuration = 3L,
            assignmentsDuration = 3L,
            assignmentsCount = 1,
            mistakenAssignmentsCount = 0,
            cancelledByIssuerAssignmentsCount = 0,
            cancelledByAssigneeAssignmentsCount = 0,
            absenceAssignmentsCount = 0,
            workedOutShiftsCount = 0,
            workedOutShiftsDuration = 0L
        )


    }


    @Test
    @DisplayName("Get statistic: started not in request period shifts")
    fun getStatisticsShiftsNotInRequestPeriod() {
        val tokens = commonPreconditions
            .createAndAuthorizeStaffUser(
                roles = listOf(ApiEnum(EmployeeRole.DARKSTORE_ADMIN)),
                vehicle = null,
                staffPartnerId = null
            )


        val createDeliveryman = commonPreconditions.createProfileDeliveryman(mobile = Constants.mobile7).profileId


        commonPreconditions.createAssignment(
            profileId = createDeliveryman,
            range = TimeRange(
                startingAt = Instant.now().minusSeconds(7200),
                endingAt = Instant.now().plusSeconds(7200)
            ),
            assignmentRange = TimeRange(
                startingAt = Instant.now().minusSeconds(7200),
                endingAt = Instant.now().plusSeconds(7200)
            ),
            role =
            AssigneeRole.DELIVERYMAN,
            darkstoreId = Constants.darkstoreId

        )

        commonPreconditions.postSchedulesForSeveralProfiles(
            mutableListOf(createDeliveryman),
            timeRange = TimeRange(
                startingAt = Instant.now().minusSeconds(7200),
                endingAt = Instant.now().plusSeconds(7200)
            ),
            schedule = listOf(
                TimeRange(
                    startingAt = Instant.now().minusSeconds(7200),
                    endingAt = Instant.now().plusSeconds(7200)
                )
            )
        )

        val statistic = staffApiGWActions.getStatistic(
            accessToken = tokens!!.accessToken,
            Constants.darkstoreId.toString(),
            mutableListOf("deliveryman"),
            from = Instant.now().toEpochMilli(),
            to = Instant.now().plusSeconds(43200).toEpochMilli(),
        )

        staffApiGWAssertions.checkStatistic(
            statistic = statistic,
            profileIds = mutableListOf(createDeliveryman),
            scheduleDuration = 2L,
            assignmentsDuration = 2L,
            assignmentsCount = 1,
            mistakenAssignmentsCount = 0,
            cancelledByIssuerAssignmentsCount = 0,
            cancelledByAssigneeAssignmentsCount = 0,
            absenceAssignmentsCount = 0,
            workedOutShiftsCount = 0,
            workedOutShiftsDuration = 0L
        )



    }

    @Test
    @Tags(Tag("smoke"), Tag("shiftsIntegration"))
    @DisplayName("Get statistic: check cancelled assignments and reasons")
    fun getStatisticsCancelledAssignments() {
        val tokens = commonPreconditions
            .createAndAuthorizeStaffUser(
                roles = listOf(ApiEnum(EmployeeRole.DARKSTORE_ADMIN)),
                vehicle = null,
                staffPartnerId = null
            )

        val createDeliverymen = commonPreconditions.createListOfProfiles(
            listOfRoles = mutableListOf(
                listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                listOf(ApiEnum(EmployeeRole.DELIVERYMAN), ApiEnum(EmployeeRole.PICKER))
            ),
            listOfVehicle = mutableListOf(
                Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
                Vehicle(ApiEnum(EmployeeVehicleType.BICYCLE)),
                Vehicle(ApiEnum(EmployeeVehicleType.MOTOCYCLE)),
                Vehicle(ApiEnum(EmployeeVehicleType.ELECTRIC_BICYCLE))
            ),
            listOfStaffPartner = mutableListOf(
                Constants.defaultStaffPartnerId,
                Constants.defaultStaffPartnerId,
                Constants.defaultStaffPartnerId,
                Constants.defaultStaffPartnerId
            ),
            listOfDarkstore = mutableListOf(
                Constants.darkstoreId,
                Constants.darkstoreId,
                Constants.darkstoreId,
                Constants.darkstoreId
            ),
            listOfMobile = mutableListOf(
                Constants.mobile2,
                Constants.mobile3,
                Constants.mobile4,
                Constants.mobile5
            ),
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
                ),
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
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString()
            ),
            listOfRequisitionsId = mutableListOf(null, null, null, null),
            amount = 4
        )

        val assignments = commonPreconditions.createAssignmentForSeveralProfiles(
            profileId = createDeliverymen,
            range = oneDayRange,
            assignmentRange = halfDayRange,
            role = mutableListOf(
                AssigneeRole.DELIVERYMAN,
                AssigneeRole.DELIVERYMAN,
                AssigneeRole.DELIVERYMAN,
                AssigneeRole.PICKER
            ),
            darkstoreId = mutableListOf(
                Constants.darkstoreId,
                Constants.updatedDarkstoreId,
                Constants.darkstoreForTimesheet,
                Constants.darkstoreIdWithNotMoscowTimezone
            )
        )

        commonPreconditions.cancelAssignment(
            assignments[0],
            oneDayRange,
            AssigneeRole.DELIVERYMAN,
            ShiftAssignmentCancellationReason.MISTAKEN_ASSIGNMENT
        )

        commonPreconditions.cancelAssignment(
            assignments[1],
            oneDayRange,
            AssigneeRole.DELIVERYMAN,
            ShiftAssignmentCancellationReason.CANCELED_BY_ASSIGNEE,
            Constants.updatedDarkstoreId
        )

        commonPreconditions.cancelAssignment(
            assignments[2],
            oneDayRange,
            AssigneeRole.DELIVERYMAN,
            ShiftAssignmentCancellationReason.CANCELED_BY_ISSUER,
            Constants.darkstoreForTimesheet
        )

        commonPreconditions.cancelAssignment(
            assignments[3],
            oneDayRange,
            AssigneeRole.PICKER,
            ShiftAssignmentCancellationReason.ASSIGNEE_ABSENCE,
            Constants.darkstoreIdWithNotMoscowTimezone
        )


        val statistic = staffApiGWActions.getStatistic(
            accessToken = tokens!!.accessToken,
            Constants.darkstoreId.toString(),
            mutableListOf("deliveryman"),
            from = Instant.now().minusSeconds(46800).toEpochMilli(),
            to = Instant.now().plusSeconds(43200).toEpochMilli(),
        )

        staffApiGWAssertions
            .checkStatistic(
                statistic = statistic,
                profileIds = mutableListOf(createDeliverymen[0]),
                scheduleDuration = 0L,
                assignmentsDuration = 0L,
                assignmentsCount = 0,
                mistakenAssignmentsCount = 1,
                cancelledByIssuerAssignmentsCount = 0,
                cancelledByAssigneeAssignmentsCount = 0,
                absenceAssignmentsCount = 0,
                workedOutShiftsCount = 0,
                workedOutShiftsDuration = 0L
            )
            .checkStatistic(
                statistic = statistic,
                profileIds = mutableListOf(createDeliverymen[1]),
                scheduleDuration = 0L,
                assignmentsDuration = 0L,
                assignmentsCount = 0,
                mistakenAssignmentsCount = 0,
                cancelledByIssuerAssignmentsCount = 0,
                cancelledByAssigneeAssignmentsCount = 1,
                absenceAssignmentsCount = 0,
                workedOutShiftsCount = 0,
                workedOutShiftsDuration = 0L
            )
            .checkStatistic(
                statistic = statistic,
                profileIds = mutableListOf(createDeliverymen[2]),
                scheduleDuration = 0L,
                assignmentsDuration = 0L,
                assignmentsCount = 0,
                mistakenAssignmentsCount = 0,
                cancelledByIssuerAssignmentsCount = 1,
                cancelledByAssigneeAssignmentsCount = 0,
                absenceAssignmentsCount = 0,
                workedOutShiftsCount = 0,
                workedOutShiftsDuration = 0L
            )
            .checkStatistic(
                statistic = statistic,
                profileIds = mutableListOf(createDeliverymen[3]),
                scheduleDuration = 0L,
                assignmentsDuration = 0L,
                assignmentsCount = 0,
                mistakenAssignmentsCount = 0,
                cancelledByIssuerAssignmentsCount = 0,
                cancelledByAssigneeAssignmentsCount = 0,
                absenceAssignmentsCount = 1,
                workedOutShiftsCount = 0,
                workedOutShiftsDuration = 0L
            )

    }

    @Test
    @DisplayName("Get statistic: check empty statistics")
    fun getEmptyStatistics() {
        val tokens = commonPreconditions
            .createAndAuthorizeStaffUser(
                roles = listOf(ApiEnum(EmployeeRole.COORDINATOR)),
                vehicle = null,
                staffPartnerId = null,
                darkstoreId = null,
                supervisedDarkstores = Constants.supervisedDarkstores
            )


        val createDeliverymenAndPickers = commonPreconditions.createListOfProfiles(
            listOfRoles = mutableListOf(
                listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                listOf(ApiEnum(EmployeeRole.PICKER)),
                listOf(ApiEnum(EmployeeRole.PICKER), ApiEnum(EmployeeRole.DELIVERYMAN)),
                listOf(ApiEnum(EmployeeRole.DELIVERYMAN), ApiEnum(EmployeeRole.PICKER))
            ),
            listOfVehicle = mutableListOf(
                Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
                null,
                Vehicle(ApiEnum(EmployeeVehicleType.BICYCLE)),
                Vehicle(ApiEnum(EmployeeVehicleType.ELECTRIC_BICYCLE))
            ),
            listOfStaffPartner = mutableListOf(
                Constants.defaultStaffPartnerId,
                Constants.defaultStaffPartnerId,
                Constants.defaultStaffPartnerId,
                Constants.defaultStaffPartnerId
            ),
            listOfDarkstore = mutableListOf(
                Constants.darkstoreId,
                Constants.darkstoreId,
                Constants.darkstoreId,
                Constants.darkstoreId
            ),
            listOfMobile = mutableListOf(
                Constants.mobile2,
                Constants.mobile3,
                Constants.mobile4,
                Constants.mobile5
            ),
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
                ),
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
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString()
            ),
            listOfRequisitionsId = mutableListOf(null, null, null, null),
            amount = 4
        )


        val statistic = staffApiGWActions.getStatistic(
            accessToken = tokens!!.accessToken,
            Constants.darkstoreId.toString(),
            mutableListOf("picker", "deliveryman"),
            from = Instant.now().minusSeconds(46800).toEpochMilli(),
            to = Instant.now().plusSeconds(43200).toEpochMilli(),
        )

        staffApiGWAssertions.checkStatistic(
            statistic = statistic,
            profileIds = createDeliverymenAndPickers,
            scheduleDuration = 0L,
            assignmentsDuration = 0L,
            assignmentsCount = 0,
            mistakenAssignmentsCount = 0,
            cancelledByIssuerAssignmentsCount = 0,
            cancelledByAssigneeAssignmentsCount = 0,
            absenceAssignmentsCount = 0,
            workedOutShiftsCount = 0,
            workedOutShiftsDuration = 0L
        )
    }

    @Test
    @DisplayName("Get statistic for disabled employee is not shown")
    fun getStatisticsDisabledEmployee() {
        val tokens = commonPreconditions
            .createAndAuthorizeStaffUser(
                roles = listOf(ApiEnum(EmployeeRole.DARKSTORE_ADMIN)),
                vehicle = null,
                staffPartnerId = null
            )


        val createPicker = commonPreconditions.createProfilePicker(mobile = Constants.mobile7).profileId

        employeeActions.deleteProfile(Constants.mobile7)

        val statistic = staffApiGWActions.getStatistic(
            accessToken = tokens!!.accessToken,
            Constants.darkstoreId.toString(),
            mutableListOf("picker"),
            from = Instant.now().minusSeconds(46800).toEpochMilli(),
            to = Instant.now().plusSeconds(43200).toEpochMilli(),
        )

        staffApiGWAssertions
            .checkEmployeeAbsentInStatistic(statistic, createPicker)


    }

    @Test
    @DisplayName("Get statistic by disabled darkstore_admin is impossible")
    fun getStatisticsDisabledDarkstoreAdmin() {

        val tokens = commonPreconditions
            .createAndAuthorizeStaffUser(
                roles = listOf(ApiEnum(EmployeeRole.DARKSTORE_ADMIN)),
                vehicle = null,
                staffPartnerId = null
            )

        employeeActions.deleteProfile(Constants.mobile1)


        commonPreconditions.createProfilePicker(mobile = Constants.mobile7)

        staffApiGWActions.getStatisticWithError(
            accessToken = tokens!!.accessToken,
            Constants.darkstoreId.toString(),
            mutableListOf("picker"),
            from = Instant.now().minusSeconds(46800).toEpochMilli(),
            to = Instant.now().plusSeconds(43200).toEpochMilli(),
            HttpStatus.SC_FORBIDDEN
        )


    }


    @Test
    @DisplayName("Get statistic from foreign darkstore is impossible")
    fun getStatisticsForeignDarkstore() {

        val tokens = commonPreconditions
            .createAndAuthorizeStaffUser(
                roles = listOf(ApiEnum(EmployeeRole.DARKSTORE_ADMIN)),
                vehicle = null,
                staffPartnerId = null
            )


        staffApiGWActions.getStatisticWithError(
            accessToken = tokens!!.accessToken,
            Constants.updatedDarkstoreId.toString(),
            mutableListOf("picker"),
            from = Instant.now().minusSeconds(46800).toEpochMilli(),
            to = Instant.now().plusSeconds(43200).toEpochMilli(),
            HttpStatus.SC_FORBIDDEN
        )


    }


    @Test
    @DisplayName("Get statistic by supervisor is impossible")
    fun getStatisticsBySupervisor() {
        val tokens = commonPreconditions
            .createAndAuthorizeStaffUser(
                roles = listOf(ApiEnum(EmployeeRole.SUPERVISOR)),
                vehicle = null,
                staffPartnerId = null,
                darkstoreId = null,
                cityId = null
            )


        staffApiGWActions.getStatisticWithError(
            accessToken = tokens!!.accessToken,
            Constants.darkstoreId.toString(),
            mutableListOf("picker"),
            from = Instant.now().minusSeconds(46800).toEpochMilli(),
            to = Instant.now().plusSeconds(43200).toEpochMilli(),
            HttpStatus.SC_FORBIDDEN
        )


    }

}