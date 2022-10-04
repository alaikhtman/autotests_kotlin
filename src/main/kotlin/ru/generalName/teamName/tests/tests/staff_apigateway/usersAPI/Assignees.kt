package ru.samokat.mysamokat.tests.tests.staff_apigateway.usersAPI

import org.apache.http.HttpStatus
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.samokat.employeeprofiles.api.common.domain.EmployeeVehicleType
import ru.samokat.employeeprofiles.api.common.domain.Vehicle
import ru.samokat.employeeprofiles.api.darkstoreusers.domain.DarkstoreUserRole
import ru.samokat.employeeprofiles.api.darkstoreusers.domain.DarkstoreUserState
import ru.samokat.my.domain.employee.EmployeeName
import ru.samokat.my.domain.employee.EmployeeRole
import ru.samokat.my.domain.shifts.TimeRange
import ru.samokat.my.rest.api.enum.ApiEnum
import ru.samokat.mysamokat.tests.checkers.CommonAssertion
import ru.samokat.mysamokat.tests.checkers.StaffApiGWAssertions
import ru.samokat.mysamokat.tests.dataproviders.Constants
import ru.samokat.mysamokat.tests.dataproviders.StringAndPhoneNumberGenerator
import ru.samokat.mysamokat.tests.dataproviders.dataClasses.staffApiGTW.enum.StafferState
import ru.samokat.mysamokat.tests.dataproviders.preconditions.CommonPreconditions
import ru.samokat.mysamokat.tests.dataproviders.preconditions.EmployeePreconditions
import ru.samokat.mysamokat.tests.dataproviders.preconditions.StaffApiGWPreconditions
import ru.samokat.mysamokat.tests.helpers.actions.EmployeeActions
import ru.samokat.mysamokat.tests.helpers.actions.StaffApiGWActions
import ru.samokat.shifts.api.common.domain.AssigneeRole
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Tags(Tag("staff_apigateway"))
class Assignees {


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
        employeeActions.deleteProfile(Constants.mobile9)
        employeeActions.deleteProfile(Constants.mobile10)
        commonPreconditions.clearAssignmentsFromDatabase(
            oneDayRange
        )
        commonPreconditions.clearAssignmentsFromDatabase(
            oneDayRange,
            Constants.updatedDarkstoreId
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
        employeeActions.deleteProfile(Constants.mobile9)
        employeeActions.deleteProfile(Constants.mobile10)
        commonPreconditions.clearAssignmentsFromDatabase(
            oneDayRange
        )
        commonPreconditions.clearAssignmentsFromDatabase(
            oneDayRange,
            Constants.updatedDarkstoreId
        )

    }

    private lateinit var oneDayRange: TimeRange
    private lateinit var halfDayRange: TimeRange


    @Test
    @Tags(Tag("smoke"), Tag("emproIntegration"), Tag("shiftsIntegration"))
    @DisplayName("Search available deliveryman by full fio - by darkstore_admin: check profile's data, ds states")
    fun searchDeliverymanByFullFIOByDarkstoreAdmin() {

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
                Constants.updatedDarkstoreId,
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
                    ("Шишковед"),
                    ("Алексей"),
                    ("Иванович")
                ),
                EmployeeName(
                    ("Шишковед"),
                    ("Алексей"),
                    ("Иванович")
                ),
                EmployeeName(
                    ("Шишковед"),
                    ("Алексей"),
                    ("Иванович")
                ),
                EmployeeName(
                    ("Шишковед"),
                    ("Алексей"),
                    ("Иванович")
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

        val createPicker = commonPreconditions.createProfilePicker(
            mobile = Constants.mobile7,
            name = EmployeeName(
                ("Шишковед"),
                ("Алексей"),
                ("Иванович")
            )
        ).profileId

        val createDeliveryman1 = commonPreconditions.createProfileDeliveryman(
            mobile = Constants.mobile8,
            name = EmployeeName(
                ("Шишковед"),
                ("Алексей"),
                ("Петрович")
            )
        ).profileId

        val createDeliveryman2 = commonPreconditions.createProfileDeliveryman(
            mobile = Constants.mobile9,
            name = EmployeeName(
                ("Мишковед"),
                ("Алексей"),
                ("Иванович")
            )
        ).profileId

        val createDeliveryman3 = commonPreconditions.createProfileDeliveryman(
            mobile = Constants.mobile10,
            name = EmployeeName(
                ("Шишковед"),
                ("Александр"),
                ("Иванович")
            )
        ).profileId

        val updateDSBuilder = employeePreconditions.fillUpdateProfileStatusOnDarkstore(
            UUID.randomUUID(), ApiEnum(DarkstoreUserState.WORKING), null
        )

        employeeActions.updateProfileStatusOnDarkstore(
            createDeliverymen[0],
            Constants.darkstoreId,
            DarkstoreUserRole.DELIVERYMAN,
            updateDSBuilder
        )


        val updateDSBuilderNotWorking = employeePreconditions.fillUpdateProfileStatusOnDarkstore(
            UUID.randomUUID(),
            ApiEnum(DarkstoreUserState.NOT_WORKING),
            "employee_request", 1
        )

        employeeActions.updateProfileStatusOnDarkstore(
            createDeliverymen[2],
            Constants.darkstoreId,
            DarkstoreUserRole.DELIVERYMAN,
            updateDSBuilderNotWorking
        )


        employeeActions.updateProfileStatusOnDarkstore(
            createDeliverymen[3],
            Constants.darkstoreId,
            DarkstoreUserRole.DELIVERYMAN,
            updateDSBuilder
        )

        staffApiGWPreconditions.fillAssigneeSearchRequestFor24TimeRange12HShift(
            name = "Шишковед Алексей Иванович",
            userRoles = listOf("deliveryman")
        )

        val assignees = staffApiGWActions.getAssignee(
            tokens!!.accessToken,
            staffApiGWPreconditions.assigneeRequest()
        )

        staffApiGWAssertions
            .checkAssigneeResponse(
                assignees = assignees,
                profileRequest = commonPreconditions.listOfProfileRequest(),
                availability = listOf(true, true, true, true),
                stafferState = listOf(
                    listOf(StafferState.WORKING),
                    listOf(StafferState.NEW),
                    listOf(StafferState.NOT_WORKING),
                    listOf(StafferState.WORKING, StafferState.NEW),
                ),
                internList = listOf(listOf(false), listOf(false), listOf(false), listOf(false, false))
            )
            .checkEmployeeIsAbsentInAssigneeResponse(assignees, createPicker)
            .checkEmployeeIsAbsentInAssigneeResponse(assignees, createDeliveryman1)
            .checkEmployeeIsAbsentInAssigneeResponse(assignees, createDeliveryman2)
            .checkEmployeeIsAbsentInAssigneeResponse(assignees, createDeliveryman3)

    }

    @Test
    @Tags(Tag("smoke"), Tag("emproIntegration"), Tag("shiftsIntegration"))
    @DisplayName("Search available picker by name - by goods_manager: check profile's data, ds states, comments, isIntern")
    fun searchPickersByNameByGoodsManager() {
        val tokens = commonPreconditions
            .createAndAuthorizeStaffUser(
                roles = listOf(ApiEnum(EmployeeRole.GOODS_MANAGER)),
                vehicle = null,
                staffPartnerId = null
            )

        val createPicker = commonPreconditions.createListOfProfiles(
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
                Constants.updatedDarkstoreId,
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
                    ("Алексойд"),
                    (StringAndPhoneNumberGenerator.generateRandomString(10))
                ),
                EmployeeName(
                    (StringAndPhoneNumberGenerator.generateRandomString(10)),
                    ("Алексойд"),
                    (StringAndPhoneNumberGenerator.generateRandomString(10))
                ),
                EmployeeName(
                    (StringAndPhoneNumberGenerator.generateRandomString(10)),
                    ("Алексойд"),
                    (StringAndPhoneNumberGenerator.generateRandomString(10))
                ),
                EmployeeName(
                    (StringAndPhoneNumberGenerator.generateRandomString(10)),
                    ("Алексойд"),
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

        val createDeliveryman = commonPreconditions.createProfileDeliveryman(
            name = EmployeeName(
                (StringAndPhoneNumberGenerator.generateRandomString(10)),
                ("Алексойд"),
                (StringAndPhoneNumberGenerator.generateRandomString(10))
            ), mobile = Constants.mobile7
        ).profileId

        val updateDSBuilder = employeePreconditions.fillUpdateProfileStatusOnDarkstore(
            UUID.randomUUID(), ApiEnum(DarkstoreUserState.WORKING), null
        )


        employeeActions.updateProfileStatusOnDarkstore(
            createPicker[0],
            Constants.darkstoreId,
            DarkstoreUserRole.PICKER,
            updateDSBuilder
        )


        val updateDSBuilderNotWorking = employeePreconditions.fillUpdateProfileStatusOnDarkstore(
            UUID.randomUUID(),
            ApiEnum(DarkstoreUserState.NOT_WORKING),
            "employee_request", 1
        )

        employeeActions.updateProfileStatusOnDarkstore(
            createPicker[2],
            Constants.darkstoreId,
            DarkstoreUserRole.PICKER,
            updateDSBuilderNotWorking
        )


        employeeActions.updateProfileStatusOnDarkstore(
            createPicker[3],
            Constants.darkstoreId,
            DarkstoreUserRole.PICKER,
            updateDSBuilder
        )

        employeePreconditions.setCreateInternshipRequest(
            Constants.darkstoreId, Instant.now().plusSeconds(60).truncatedTo(
                ChronoUnit.SECONDS
            )
        )

        employeeActions.createInternship(
            createPicker[1],
            DarkstoreUserRole.PICKER,
            employeePreconditions.createInternshipRequest()
        )

        staffApiGWPreconditions
            .fillCommentRequest("Кто ты?")
            .fillAssigneeSearchRequestFor24TimeRange12HShift(
                name = "Алексойд",
                userRoles = listOf("picker")
            )

        staffApiGWActions.addComment(
            tokens!!.accessToken,
            staffApiGWPreconditions.commentRequest(),
            createPicker.first().toString()
        )

        val assignees = staffApiGWActions.getAssignee(
            tokens.accessToken,
            staffApiGWPreconditions.assigneeRequest()
        )

        staffApiGWAssertions.checkAssigneeResponse(
            assignees = assignees,
            profileRequest = commonPreconditions.listOfProfileRequest(),
            availability = listOf(true, true, true, true),
            commentList = listOf("Кто ты?", null, null, null),
            stafferState = listOf(
                listOf(StafferState.WORKING),
                listOf(StafferState.NEW),
                listOf(StafferState.NOT_WORKING),
                listOf(StafferState.NEW, StafferState.WORKING),
            ),
            internList = listOf(listOf(false), listOf(true), listOf(false), listOf(false, false))
        )
            .checkEmployeeIsAbsentInAssigneeResponse(assignees, createDeliveryman)

    }


    @Test
    @Tags(Tag("smoke"), Tag("emproIntegration"), Tag("shiftsIntegration"))
    @DisplayName("Search available deliveryman and picker by family name - by coordinator")
    fun searchDeliverymanPickerByFamilyName() {

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
                listOf(ApiEnum(EmployeeRole.DELIVERYMAN), ApiEnum(EmployeeRole.PICKER))
            ),
            listOfVehicle = mutableListOf(
                Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
                null,
                Vehicle(ApiEnum(EmployeeVehicleType.BICYCLE))

            ),
            listOfStaffPartner = mutableListOf(
                Constants.defaultStaffPartnerId,
                Constants.defaultStaffPartnerId,
                Constants.defaultStaffPartnerId
            ),
            listOfDarkstore = mutableListOf(
                Constants.darkstoreId,
                Constants.updatedDarkstoreId,
                Constants.darkstoreId
            ),
            listOfMobile = mutableListOf(
                Constants.mobile2,
                Constants.mobile3,
                Constants.mobile4
            ),
            listOfName = mutableListOf(
                EmployeeName(
                    ("Крокейзберген"),
                    (StringAndPhoneNumberGenerator.generateRandomString(10)),
                    (StringAndPhoneNumberGenerator.generateRandomString(10))
                ),
                EmployeeName(
                    ("Крокейзберген"),
                    (StringAndPhoneNumberGenerator.generateRandomString(10)),
                    (StringAndPhoneNumberGenerator.generateRandomString(10))
                ),
                EmployeeName(
                    ("Крокейзберген"),
                    (StringAndPhoneNumberGenerator.generateRandomString(10)),
                    (StringAndPhoneNumberGenerator.generateRandomString(10))
                )

            ),
            listOfAccountingProfileIds = mutableListOf(
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString()
            ),
            listOfRequisitionsId = mutableListOf(null, null, null),
            amount = 3
        )


        val updateDSBuilder = employeePreconditions.fillUpdateProfileStatusOnDarkstore(
            UUID.randomUUID(), ApiEnum(DarkstoreUserState.WORKING), null
        )

        employeeActions.updateProfileStatusOnDarkstore(
            createDeliverymenAndPickers[0],
            Constants.darkstoreId,
            DarkstoreUserRole.DELIVERYMAN,
            updateDSBuilder
        )


        val updateDSBuilderNotWorking = employeePreconditions.fillUpdateProfileStatusOnDarkstore(
            UUID.randomUUID(),
            ApiEnum(DarkstoreUserState.NOT_WORKING),
            "employee_request", 1
        )

        employeeActions.updateProfileStatusOnDarkstore(
            createDeliverymenAndPickers[1],
            Constants.updatedDarkstoreId,
            DarkstoreUserRole.PICKER,
            updateDSBuilderNotWorking
        )


        employeeActions.updateProfileStatusOnDarkstore(
            createDeliverymenAndPickers[2],
            Constants.darkstoreId,
            DarkstoreUserRole.DELIVERYMAN,
            updateDSBuilder
        )

        staffApiGWPreconditions.fillAssigneeSearchRequestFor24TimeRange12HShift(
            name = "Крокейзберген",
            userRoles = listOf("deliveryman", "picker")
        )

        val assignees = staffApiGWActions.getAssignee(
            tokens!!.accessToken,
            staffApiGWPreconditions.assigneeRequest()
        )

        staffApiGWAssertions
            .checkAssigneeResponse(
                assignees = assignees,
                profileRequest = commonPreconditions.listOfProfileRequest(),
                availability = listOf(true, true, true),
                stafferState = listOf(
                    listOf(StafferState.WORKING),
                    listOf(StafferState.NOT_WORKING),
                    listOf(StafferState.WORKING, StafferState.NEW),
                ),
                internList = listOf(listOf(false), listOf(false), listOf(false, false))
            )

    }

    @Test
    @DisplayName("Search deliveryman by 3 symbols")
    fun searchDeliverymanBy3Symbols() {
        val tokens = commonPreconditions
            .createAndAuthorizeStaffUser(
                roles = listOf(ApiEnum(EmployeeRole.COORDINATOR)),
                vehicle = null,
                staffPartnerId = null,
                darkstoreId = null,
                supervisedDarkstores = Constants.supervisedDarkstores
            )

        commonPreconditions.createListOfProfiles(
            listOfRoles = mutableListOf(
                listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                listOf(ApiEnum(EmployeeRole.PICKER)),
                listOf(ApiEnum(EmployeeRole.DELIVERYMAN), ApiEnum(EmployeeRole.PICKER))
            ),
            listOfVehicle = mutableListOf(
                Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
                null,
                Vehicle(ApiEnum(EmployeeVehicleType.BICYCLE))

            ),
            listOfStaffPartner = mutableListOf(
                Constants.defaultStaffPartnerId,
                Constants.defaultStaffPartnerId,
                Constants.defaultStaffPartnerId
            ),
            listOfDarkstore = mutableListOf(
                Constants.darkstoreId,
                Constants.updatedDarkstoreId,
                Constants.darkstoreId
            ),
            listOfMobile = mutableListOf(
                Constants.mobile2,
                Constants.mobile3,
                Constants.mobile4
            ),
            listOfName = mutableListOf(
                EmployeeName(
                    ("Крокейзберген"),
                    (StringAndPhoneNumberGenerator.generateRandomString(10)),
                    (StringAndPhoneNumberGenerator.generateRandomString(10))
                ),
                EmployeeName(
                    (StringAndPhoneNumberGenerator.generateRandomString(10)),
                    ("Кей-би"),
                    (StringAndPhoneNumberGenerator.generateRandomString(10))
                ),
                EmployeeName(
                    (StringAndPhoneNumberGenerator.generateRandomString(10)),
                    (StringAndPhoneNumberGenerator.generateRandomString(10)),
                    ("Стекей")
                )

            ),
            listOfAccountingProfileIds = mutableListOf(
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString()
            ),
            listOfRequisitionsId = mutableListOf(null, null, null),
            amount = 3
        )

        staffApiGWPreconditions.fillAssigneeSearchRequestFor24TimeRange12HShift(
            name = "кей",
            userRoles = listOf("deliveryman", "picker")
        )

        val assignees = staffApiGWActions.getAssignee(
            tokens!!.accessToken,
            staffApiGWPreconditions.assigneeRequest()
        )

        staffApiGWAssertions
            .checkAssigneeResponse(
                assignees = assignees,
                profileRequest = commonPreconditions.listOfProfileRequest(),
                availability = listOf(true, true, true),
                stafferState = listOf(
                    listOf(StafferState.NEW),
                    listOf(StafferState.NEW),
                    listOf(StafferState.NEW, StafferState.NEW)
                ),
                internList = listOf(listOf(false), listOf(false), listOf(false, false))
            )

    }


    @Test
    @DisplayName("Search deliveryman by more than max string is impossible")
    fun searchDeliverymanByMaxString() {
        val tokens = commonPreconditions
            .createAndAuthorizeStaffUser(
                roles = listOf(ApiEnum(EmployeeRole.COORDINATOR)),
                vehicle = null,
                staffPartnerId = null,
                darkstoreId = null,
                supervisedDarkstores = Constants.supervisedDarkstores
            )

        staffApiGWPreconditions.fillAssigneeSearchRequestFor24TimeRange12HShift(
            name = StringAndPhoneNumberGenerator.generateRandomString(243),
            userRoles = listOf("deliveryman", "picker")
        )

        staffApiGWActions.getAssigneeWithError(
            tokens!!.accessToken,
            staffApiGWPreconditions.assigneeRequest(),
            HttpStatus.SC_BAD_REQUEST
        )
    }

    @Test
    @DisplayName("Search deliveryman by less than 3 symbols is impossible")
    fun searchDeliverymanByLessThan3Symbols() {
        val tokens = commonPreconditions
            .createAndAuthorizeStaffUser(
                roles = listOf(ApiEnum(EmployeeRole.COORDINATOR)),
                vehicle = null,
                staffPartnerId = null,
                darkstoreId = null,
                supervisedDarkstores = Constants.supervisedDarkstores
            )

        staffApiGWPreconditions.fillAssigneeSearchRequestFor24TimeRange12HShift(
            name = StringAndPhoneNumberGenerator.generateRandomString(2),
            userRoles = listOf("deliveryman", "picker")
        )

        staffApiGWActions.getAssigneeWithError(
            tokens!!.accessToken,
            staffApiGWPreconditions.assigneeRequest(),
            HttpStatus.SC_BAD_REQUEST
        )


    }

    @Test
    @DisplayName("Disabled employee is not shown")
    fun searchDisabledEmployee() {
        val tokens = commonPreconditions
            .createAndAuthorizeStaffUser(
                roles = listOf(ApiEnum(EmployeeRole.DARKSTORE_ADMIN)),
                vehicle = null,
                staffPartnerId = null
            )


        commonPreconditions.createProfilePicker(
            mobile = Constants.mobile7,
            name = EmployeeName(
                ("Шишковед"),
                ("Алексей"),
                ("Иванович")
            )
        ).profileId

        employeeActions.deleteProfile(Constants.mobile7)

        staffApiGWPreconditions.fillAssigneeSearchRequestFor24TimeRange12HShift(
            name = "Шишковед Алексей Иванович",
            userRoles = listOf("picker")
        )

        val assignees = staffApiGWActions.getAssignee(
            tokens!!.accessToken,
            staffApiGWPreconditions.assigneeRequest()
        )

        staffApiGWAssertions
            .checkAssigneeResponseIsEmpty(assignees)


    }

    @Test
    @DisplayName("Search unknown deliveryman or picker")
    fun searchUnknownEmployee() {
        val tokens = commonPreconditions
            .createAndAuthorizeStaffUser(
                roles = listOf(ApiEnum(EmployeeRole.COORDINATOR)),
                vehicle = null,
                staffPartnerId = null,
                darkstoreId = null,
                supervisedDarkstores = Constants.supervisedDarkstores
            )

        staffApiGWPreconditions.fillAssigneeSearchRequestFor24TimeRange12HShift(
            name = StringAndPhoneNumberGenerator.generateRandomString(25),
            userRoles = listOf("deliveryman", "picker")
        )

        val assignees = staffApiGWActions.getAssignee(
            tokens!!.accessToken,
            staffApiGWPreconditions.assigneeRequest()
        )

        staffApiGWAssertions
            .checkAssigneeResponseIsEmpty(
                assignees = assignees
            )

    }

    @Test
    @DisplayName("Search deliveryman by supervisor is impossible")
    fun searchDeliverymanBySupervisor() {
        val tokens = commonPreconditions
            .createAndAuthorizeStaffUser(
                roles = listOf(ApiEnum(EmployeeRole.SUPERVISOR)),
                vehicle = null,
                staffPartnerId = null,
                darkstoreId = null,
                cityId = null
            )

        staffApiGWPreconditions.fillAssigneeSearchRequestFor24TimeRange12HShift(
            name = StringAndPhoneNumberGenerator.generateRandomString(3),
            userRoles = listOf("deliveryman", "picker")
        )

        staffApiGWActions.getAssigneeWithError(
            tokens!!.accessToken,
            staffApiGWPreconditions.assigneeRequest(),
            HttpStatus.SC_FORBIDDEN
        )
    }

    @Test
    @Tags(Tag("smoke"), Tag("shiftsIntegration"))
    @DisplayName("Search unavailable picker: 1 shift by picker = planned assignment time")
    fun searchUnavailablePicker() {
        val tokens = commonPreconditions
            .createAndAuthorizeStaffUser(
                roles = listOf(ApiEnum(EmployeeRole.GOODS_MANAGER)),
                vehicle = null,
                staffPartnerId = null
            )

        val createPicker = commonPreconditions.createListOfProfiles(
            listOfRoles = mutableListOf(
                listOf(ApiEnum(EmployeeRole.PICKER)),
                listOf(ApiEnum(EmployeeRole.DELIVERYMAN), ApiEnum(EmployeeRole.PICKER))
            ),
            listOfVehicle = mutableListOf(
                null,
                Vehicle(ApiEnum(EmployeeVehicleType.ELECTRIC_BICYCLE))
            ),
            listOfStaffPartner = mutableListOf(
                Constants.defaultStaffPartnerId,
                Constants.defaultStaffPartnerId
            ),
            listOfDarkstore = mutableListOf(
                Constants.darkstoreId,
                Constants.updatedDarkstoreId
            ),
            listOfMobile = mutableListOf(
                Constants.mobile2,
                Constants.mobile3
            ),
            listOfName = mutableListOf(
                EmployeeName(
                    (StringAndPhoneNumberGenerator.generateRandomString(10)),
                    ("Алексойд"),
                    (StringAndPhoneNumberGenerator.generateRandomString(10))
                ),
                EmployeeName(
                    (StringAndPhoneNumberGenerator.generateRandomString(10)),
                    ("Алексойд"),
                    (StringAndPhoneNumberGenerator.generateRandomString(10))
                )
            ),
            listOfAccountingProfileIds = mutableListOf(
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString()
            ),
            listOfRequisitionsId = mutableListOf(null, null),
            amount = 2
        )


        staffApiGWPreconditions
            .fillAssigneeSearchRequestFor24TimeRange12HShift(
                name = "Алексойд",
                userRoles = listOf("picker")
            )

        val assignment1 = commonPreconditions.createAssignment(
            profileId = createPicker.first(),
            range = oneDayRange,
            assignmentRange = halfDayRange,
            role = AssigneeRole.PICKER,
            darkstoreId = Constants.darkstoreId
        )

        val assignment2 = commonPreconditions.createAssignment(
            profileId = createPicker.last(),
            range = oneDayRange,
            assignmentRange = halfDayRange,
            role = AssigneeRole.DELIVERYMAN,
            darkstoreId = Constants.updatedDarkstoreId
        )


        val assignees = staffApiGWActions.getAssignee(
            tokens!!.accessToken,
            staffApiGWPreconditions.assigneeRequest()
        )

        staffApiGWAssertions.checkAssigneeResponse(
            assignees = assignees,
            profileRequest = commonPreconditions.listOfProfileRequest(),
            availability = listOf(false, false),
            stafferState = listOf(
                listOf(StafferState.NEW),
                listOf(StafferState.NEW, StafferState.NEW)
            ),
            internList = listOf(listOf(false), listOf(false, false)),
            assignmentsList = listOf(listOf(assignment1), listOf(assignment2))
        )


    }

    @Test
    @Tags(Tag("smoke"), Tag("shiftsIntegration"))
    @DisplayName("Search unavailable deliveryman: >1 shifts for 1 assignee = planned assignment time")
    fun searchUnavailableDeliveryman() {

        val tokens = commonPreconditions
            .createAndAuthorizeStaffUser(
                roles = listOf(ApiEnum(EmployeeRole.GOODS_MANAGER)),
                vehicle = null,
                staffPartnerId = null
            )

        val createDeliveryman = commonPreconditions.createListOfProfiles(
            listOfRoles = mutableListOf(
                listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                listOf(ApiEnum(EmployeeRole.DELIVERYMAN), ApiEnum(EmployeeRole.PICKER))
            ),
            listOfVehicle = mutableListOf(
                Vehicle(ApiEnum(EmployeeVehicleType.BICYCLE)),
                Vehicle(ApiEnum(EmployeeVehicleType.ELECTRIC_BICYCLE))
            ),
            listOfStaffPartner = mutableListOf(
                Constants.defaultStaffPartnerId,
                Constants.defaultStaffPartnerId
            ),
            listOfDarkstore = mutableListOf(
                Constants.darkstoreId,
                Constants.updatedDarkstoreId
            ),
            listOfMobile = mutableListOf(
                Constants.mobile2,
                Constants.mobile3
            ),
            listOfName = mutableListOf(
                EmployeeName(
                    (StringAndPhoneNumberGenerator.generateRandomString(10)),
                    ("Алексойд"),
                    (StringAndPhoneNumberGenerator.generateRandomString(10))
                ),
                EmployeeName(
                    (StringAndPhoneNumberGenerator.generateRandomString(10)),
                    ("Алексойд"),
                    (StringAndPhoneNumberGenerator.generateRandomString(10))
                )
            ),
            listOfAccountingProfileIds = mutableListOf(
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString()
            ),
            listOfRequisitionsId = mutableListOf(null, null),
            amount = 2
        )


        staffApiGWPreconditions
            .fillAssigneeSearchRequestFor24TimeRange12HShift(
                name = "Алексойд",
                userRoles = listOf("deliveryman")
            )

        val assignment1 = commonPreconditions.createSeveralAssignment(
            profileId = createDeliveryman.first(),
            range = oneDayRange,
            assignmentRanges = listOf(
                (TimeRange(
                    startingAt = Instant.ofEpochMilli(staffApiGWPreconditions.assigneeRequest().assignFrom),
                    endingAt = Instant.ofEpochMilli(staffApiGWPreconditions.assigneeRequest().assignTo)
                        .minusSeconds(7200)

                )),
                (TimeRange(
                    startingAt = Instant.ofEpochMilli(staffApiGWPreconditions.assigneeRequest().assignTo)
                        .minusSeconds(3600),
                    endingAt = Instant.ofEpochMilli(staffApiGWPreconditions.assigneeRequest().assignTo)

                ))
            ),
            role = AssigneeRole.DELIVERYMAN,
            darkstoreId = Constants.darkstoreId
        )


        val assignment2 = commonPreconditions.createAssignment(
            profileId = createDeliveryman.last(),
            range = oneDayRange,
            assignmentRange = halfDayRange,
            role = AssigneeRole.DELIVERYMAN,
            darkstoreId = Constants.updatedDarkstoreId
        )


        val assignees = staffApiGWActions.getAssignee(
            tokens!!.accessToken,
            staffApiGWPreconditions.assigneeRequest()
        )

        staffApiGWAssertions.checkAssigneeResponse(
            assignees = assignees,
            profileRequest = commonPreconditions.listOfProfileRequest(),
            availability = listOf(false, false),
            stafferState = listOf(
                listOf(StafferState.NEW),
                listOf(StafferState.NEW, StafferState.NEW)
            ),
            internList = listOf(listOf(false), listOf(false, false)),
            assignmentsList = listOf(
                listOf(
                    assignment1.first().assignment.assignmentId,
                    assignment1.last().assignment.assignmentId
                ), listOf(assignment2)
            )
        )


    }

    @Test
    @Tags(Tag("smoke"), Tag("shiftsIntegration"))
    @DisplayName("Search unavailable deliveryman and picker: 1 shift cross planned assignment time, 1 shift in other time")
    fun searchUnavailableDeliverymanAndPicker() {
        val tokens = commonPreconditions
            .createAndAuthorizeStaffUser(
                roles = listOf(ApiEnum(EmployeeRole.GOODS_MANAGER)),
                vehicle = null,
                staffPartnerId = null
            )

        val createDeliveryman = commonPreconditions.createListOfProfiles(
            listOfRoles = mutableListOf(
                listOf(ApiEnum(EmployeeRole.PICKER)),
                listOf(ApiEnum(EmployeeRole.DELIVERYMAN))
            ),
            listOfVehicle = mutableListOf(
                null,
                Vehicle(ApiEnum(EmployeeVehicleType.ELECTRIC_BICYCLE))
            ),
            listOfStaffPartner = mutableListOf(
                Constants.defaultStaffPartnerId,
                Constants.defaultStaffPartnerId
            ),
            listOfDarkstore = mutableListOf(
                Constants.darkstoreId,
                Constants.updatedDarkstoreId
            ),
            listOfMobile = mutableListOf(
                Constants.mobile2,
                Constants.mobile3
            ),
            listOfName = mutableListOf(
                EmployeeName(
                    (StringAndPhoneNumberGenerator.generateRandomString(10)),
                    ("Алексойд"),
                    (StringAndPhoneNumberGenerator.generateRandomString(10))
                ),
                EmployeeName(
                    (StringAndPhoneNumberGenerator.generateRandomString(10)),
                    ("Алексойд"),
                    (StringAndPhoneNumberGenerator.generateRandomString(10))
                )
            ),
            listOfAccountingProfileIds = mutableListOf(
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString()
            ),
            listOfRequisitionsId = mutableListOf(null, null),
            amount = 2
        )


        staffApiGWPreconditions
            .fillAssigneeSearchRequestFor24TimeRange12HShift(
                name = "Алексойд",
                userRoles = listOf("deliveryman", "picker")
            )

        val assignment1 = commonPreconditions.createAssignment(
            profileId = createDeliveryman.first(),
            range = oneDayRange,
            assignmentRange = TimeRange(
                startingAt = Instant.ofEpochMilli(staffApiGWPreconditions.assigneeRequest().searchFrom),
                endingAt = Instant.ofEpochMilli(staffApiGWPreconditions.assigneeRequest().assignFrom).minusSeconds(7200)
            ),
            role = AssigneeRole.PICKER,
            darkstoreId = Constants.darkstoreId
        )


        val assignment2 = commonPreconditions.createAssignment(
            profileId = createDeliveryman.last(),
            range = oneDayRange,
            assignmentRange = TimeRange(
                startingAt = Instant.ofEpochMilli(staffApiGWPreconditions.assigneeRequest().assignFrom)
                    .minusSeconds(7200),
                endingAt = Instant.ofEpochMilli(staffApiGWPreconditions.assigneeRequest().assignTo).minusSeconds(3600)
            ),
            role = AssigneeRole.DELIVERYMAN,
            darkstoreId = Constants.updatedDarkstoreId
        )


        val assignees = staffApiGWActions.getAssignee(
            tokens!!.accessToken,
            staffApiGWPreconditions.assigneeRequest()
        )

        staffApiGWAssertions.checkAssigneeResponse(
            assignees = assignees,
            profileRequest = commonPreconditions.listOfProfileRequest(),
            availability = listOf(true, false),
            stafferState = listOf(
                listOf(StafferState.NEW),
                listOf(StafferState.NEW)
            ),
            internList = listOf(listOf(false), listOf(false)),
            assignmentsList = listOf(
                listOf(null),
                listOf(assignment2)
            )
        )


    }


}