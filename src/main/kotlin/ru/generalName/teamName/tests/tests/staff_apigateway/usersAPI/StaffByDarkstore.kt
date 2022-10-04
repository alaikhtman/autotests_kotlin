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
import ru.samokat.mysamokat.tests.dataproviders.dataClasses.staffApiGTW.enum.*
import java.util.*


@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Tags(Tag("staff_apigateway"))
class StaffByDarkstore {

    private lateinit var staffApiGWPreconditions: StaffApiGWPreconditions

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
        staffApiGWPreconditions = StaffApiGWPreconditions()
        employeePreconditions = EmployeePreconditions()
        staffApiGWAssertions = StaffApiGWAssertions()
        commonAssertions = CommonAssertion()
        employeeActions.deleteProfile(Constants.mobile1)
        employeeActions.deleteProfile(Constants.mobile2)
        employeeActions.deleteProfile(Constants.mobile3)
        employeeActions.deleteProfile(Constants.mobile3)
        employeeActions.deleteProfile(Constants.mobile4)
        employeeActions.deleteProfile(Constants.mobile5)
        employeeActions.deleteProfile(Constants.mobile6)
    }

    @AfterEach
    fun release() {
        staffApiGWAssertions.assertAll()
        commonAssertions.assertAll()
        employeeActions.deleteProfile(Constants.mobile1)
        employeeActions.deleteProfile(Constants.mobile2)
        employeeActions.deleteProfile(Constants.mobile3)
        employeeActions.deleteProfile(Constants.mobile3)
        employeeActions.deleteProfile(Constants.mobile4)
        employeeActions.deleteProfile(Constants.mobile5)
        employeeActions.deleteProfile(Constants.mobile6)

    }

    @Test
    @Tags(Tag("smoke"), Tag("emproIntegration"))
    @DisplayName("Get active user with role = deliveryman with different status (new, work, not_working) by darkstore_admin")
    fun getDeliverymanByDarkstoreAdmin() {
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

        val createPicker = commonPreconditions.createListOfProfiles(
            listOfRoles = mutableListOf(
                listOf(ApiEnum(EmployeeRole.PICKER))

            ),
            listOfVehicle = mutableListOf(null),
            listOfStaffPartner = mutableListOf(
                Constants.defaultStaffPartnerId
            ),
            listOfDarkstore = mutableListOf(
                Constants.darkstoreId
            ),
            listOfMobile = mutableListOf(
                Constants.mobile6
            ),
            listOfName = mutableListOf(
                EmployeeName(
                    (StringAndPhoneNumberGenerator.generateRandomString(10)),
                    (StringAndPhoneNumberGenerator.generateRandomString(10)),
                    (StringAndPhoneNumberGenerator.generateRandomString(10))
                )
            ),
            listOfAccountingProfileIds = mutableListOf(
                UUID.randomUUID().toString()
            ),
            listOfRequisitionsId = mutableListOf(null),
            amount = 1
        )

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
            createDeliverymen[1],
            Constants.darkstoreId,
            DarkstoreUserRole.DELIVERYMAN,
            updateDSBuilderNotWorking
        )

        val users = staffApiGWActions.getUsersByDarkstore(
            tokens!!.accessToken,
            commonPreconditions.createProfileRequest().darkstoreId.toString(),
            mutableListOf(EmployeeRole.DELIVERYMAN.value)
        )

        staffApiGWAssertions
            .checkStaff(
                staff = users,
                profileIds = createDeliverymen,
                darkstoreIds = listOf(
                    Constants.darkstoreId,
                    Constants.darkstoreId,
                    Constants.darkstoreId,
                    Constants.darkstoreId
                ),
                roles = listOf(
                    listOf(ApiEnum(EmployeeUserRole.DELIVERYMAN)),
                    listOf(ApiEnum(EmployeeUserRole.DELIVERYMAN)),
                    listOf(ApiEnum(EmployeeUserRole.DELIVERYMAN)),
                    listOf(ApiEnum(EmployeeUserRole.DELIVERYMAN), ApiEnum(EmployeeUserRole.PICKER))
                ),
                vehicle = listOf(
                    Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
                    Vehicle(ApiEnum(EmployeeVehicleType.BICYCLE)),
                    Vehicle(ApiEnum(EmployeeVehicleType.MOTOCYCLE)),
                    Vehicle(ApiEnum(EmployeeVehicleType.ELECTRIC_BICYCLE))
                ),
                status = listOf(
                    listOf(ApiEnum(StafferState.WORKING)),
                    listOf(ApiEnum(StafferState.NOT_WORKING)),
                    listOf(ApiEnum(StafferState.NEW)),
                    listOf(ApiEnum(StafferState.NEW), ApiEnum(StafferState.NEW))
                ),
                stafferRoles = listOf(
                    listOf(ApiEnum(StafferRole.DELIVERYMAN)),
                    listOf(ApiEnum(StafferRole.DELIVERYMAN)),
                    listOf(ApiEnum(StafferRole.DELIVERYMAN)),
                    listOf(ApiEnum(StafferRole.DELIVERYMAN), ApiEnum(StafferRole.PICKER))
                )
            )
            .checkGroupOfUserIsAbsent(users, createPicker)

    }


    @Test
    @Tags(Tag("smoke"), Tag("emproIntegration"))
    @DisplayName("Get active user with role = picker (new, work, not_working) by goods_manager")
    fun getPickerByGoodsManager() {
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
                listOf(ApiEnum(EmployeeRole.PICKER), ApiEnum(EmployeeRole.DELIVERYMAN))
            ),
            listOfVehicle = mutableListOf(
                null, null, null, Vehicle(ApiEnum(EmployeeVehicleType.BICYCLE))
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

        val createDeliveryman = commonPreconditions.createListOfProfiles(
            listOfRoles = mutableListOf(
                listOf(ApiEnum(EmployeeRole.DELIVERYMAN))

            ),
            listOfVehicle = mutableListOf(Vehicle(ApiEnum(EmployeeVehicleType.BICYCLE))),
            listOfStaffPartner = mutableListOf(
                Constants.defaultStaffPartnerId
            ),
            listOfDarkstore = mutableListOf(
                Constants.darkstoreId
            ),
            listOfMobile = mutableListOf(
                Constants.mobile6
            ),
            listOfName = mutableListOf(
                EmployeeName(
                    (StringAndPhoneNumberGenerator.generateRandomString(10)),
                    (StringAndPhoneNumberGenerator.generateRandomString(10)),
                    (StringAndPhoneNumberGenerator.generateRandomString(10))
                )
            ),
            listOfAccountingProfileIds = mutableListOf(
                UUID.randomUUID().toString()
            ),
            listOfRequisitionsId = mutableListOf(null),
            amount = 1
        )

        val updateDSBuilder = employeePreconditions.fillUpdateProfileStatusOnDarkstore(
            UUID.randomUUID(), ApiEnum(DarkstoreUserState.WORKING), null
        )

        employeeActions.updateProfileStatusOnDarkstore(
            createPickers[0],
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
            createPickers[1],
            Constants.darkstoreId,
            DarkstoreUserRole.PICKER,
            updateDSBuilderNotWorking
        )

        val users = staffApiGWActions.getUsersByDarkstore(
            tokens!!.accessToken,
            commonPreconditions.createProfileRequest().darkstoreId.toString(),
            mutableListOf(EmployeeRole.PICKER.value)
        )

        staffApiGWAssertions
            .checkStaff(
                staff = users,
                profileIds = createPickers,
                darkstoreIds = listOf(
                    Constants.darkstoreId,
                    Constants.darkstoreId,
                    Constants.darkstoreId,
                    Constants.darkstoreId
                ),
                roles = listOf(
                    listOf(ApiEnum(EmployeeUserRole.PICKER)),
                    listOf(ApiEnum(EmployeeUserRole.PICKER)),
                    listOf(ApiEnum(EmployeeUserRole.PICKER)),
                    listOf(ApiEnum(EmployeeUserRole.PICKER), ApiEnum(EmployeeUserRole.DELIVERYMAN))
                ),
                vehicle = null,
                status = listOf(
                    listOf(ApiEnum(StafferState.WORKING)),
                    listOf(ApiEnum(StafferState.NOT_WORKING)),
                    listOf(ApiEnum(StafferState.NEW)),
                    listOf(ApiEnum(StafferState.NEW), ApiEnum(StafferState.NEW))
                ),
                stafferRoles = listOf(
                    listOf(ApiEnum(StafferRole.PICKER)),
                    listOf(ApiEnum(StafferRole.PICKER)),
                    listOf(ApiEnum(StafferRole.PICKER)),
                    listOf(ApiEnum(StafferRole.PICKER), ApiEnum(StafferRole.DELIVERYMAN))
                )
            )
            .checkGroupOfUserIsAbsent(users, createDeliveryman)

    }


    @Test
    @Tags(Tag("smoke"), Tag("emproIntegration"))
    @DisplayName("Get active deliveryman-picker (new, work, not_working) by coordinator")
    fun getDeliverymanPickerByCoordinator() {
        val tokens = commonPreconditions
            .createAndAuthorizeStaffUser(
                roles = listOf(ApiEnum(EmployeeRole.COORDINATOR)),
                vehicle = null,
                staffPartnerId = null,
                darkstoreId = null,
                supervisedDarkstores = Constants.supervisedDarkstores,
                cityId = null
            )
        val createPickersAndDeliverymen = commonPreconditions.createListOfProfiles(
            listOfRoles = mutableListOf(
                listOf(ApiEnum(EmployeeRole.PICKER)),
                listOf(ApiEnum(EmployeeRole.PICKER), ApiEnum(EmployeeRole.DELIVERYMAN)),
                listOf(ApiEnum(EmployeeRole.DELIVERYMAN))
            ),
            listOfVehicle = mutableListOf(
                null,
                Vehicle(ApiEnum(EmployeeVehicleType.BICYCLE)),
                Vehicle(ApiEnum(EmployeeVehicleType.BICYCLE))
            ),
            listOfStaffPartner = mutableListOf(
                Constants.defaultStaffPartnerId,
                Constants.defaultStaffPartnerId,
                Constants.defaultStaffPartnerId

            ),
            listOfDarkstore = mutableListOf(
                Constants.darkstoreId,
                Constants.darkstoreId,
                Constants.darkstoreId
            ),
            listOfMobile = mutableListOf(
                Constants.mobile2,
                Constants.mobile3,
                Constants.mobile4
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
            createPickersAndDeliverymen[0],
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
            createPickersAndDeliverymen[1],
            Constants.darkstoreId,
            DarkstoreUserRole.DELIVERYMAN,
            updateDSBuilderNotWorking
        )

        val users = staffApiGWActions.getUsersByDarkstore(
            tokens!!.accessToken,
            commonPreconditions.createProfileRequest().darkstoreId.toString(),
            mutableListOf(EmployeeRole.PICKER.value, EmployeeRole.DELIVERYMAN.value)
        )

        staffApiGWAssertions
            .checkStaff(
                staff = users,
                profileIds = createPickersAndDeliverymen,
                darkstoreIds = listOf(
                    Constants.darkstoreId,
                    Constants.darkstoreId,
                    Constants.darkstoreId
                ),
                roles = listOf(
                    listOf(ApiEnum(EmployeeUserRole.PICKER)),
                    listOf(ApiEnum(EmployeeUserRole.PICKER), ApiEnum(EmployeeUserRole.DELIVERYMAN)),
                    listOf(ApiEnum(EmployeeUserRole.DELIVERYMAN)),

                    ),
                vehicle = listOf(
                    null,
                    Vehicle(ApiEnum(EmployeeVehicleType.BICYCLE)),
                    Vehicle(ApiEnum(EmployeeVehicleType.BICYCLE))
                ),
                status = listOf(
                    listOf(ApiEnum(StafferState.WORKING)),
                    listOf(ApiEnum(StafferState.NEW), ApiEnum(StafferState.NOT_WORKING)),
                    listOf(ApiEnum(StafferState.NEW))
                ),
                stafferRoles = listOf(
                    listOf(ApiEnum(StafferRole.PICKER)),
                    listOf(ApiEnum(StafferRole.PICKER), (ApiEnum(StafferRole.DELIVERYMAN))),
                    listOf(ApiEnum(StafferRole.DELIVERYMAN))
                )
            )
    }


    @Test
    @Tags(Tag("smoke"), Tag("emproIntegration"))
    @DisplayName("Deleted users are not shown")
    fun getDeletedUsers() {
        val tokens = commonPreconditions
            .createAndAuthorizeStaffUser(
                roles = listOf(ApiEnum(EmployeeRole.GOODS_MANAGER)),
                vehicle = null,
                staffPartnerId = null
            )

        val createDeliveryman = commonPreconditions.createListOfProfiles(
            listOfRoles = mutableListOf(
                listOf(ApiEnum(EmployeeRole.DELIVERYMAN))

            ),
            listOfVehicle = mutableListOf(Vehicle(ApiEnum(EmployeeVehicleType.BICYCLE))),
            listOfStaffPartner = mutableListOf(
                Constants.defaultStaffPartnerId
            ),
            listOfDarkstore = mutableListOf(
                Constants.darkstoreId
            ),
            listOfMobile = mutableListOf(
                Constants.mobile6
            ),
            listOfName = mutableListOf(
                EmployeeName(
                    (StringAndPhoneNumberGenerator.generateRandomString(10)),
                    (StringAndPhoneNumberGenerator.generateRandomString(10)),
                    (StringAndPhoneNumberGenerator.generateRandomString(10))
                )
            ),
            listOfAccountingProfileIds = mutableListOf(
                UUID.randomUUID().toString()
            ),
            listOfRequisitionsId = mutableListOf(null),
            amount = 1
        )

        employeeActions.deleteProfile(createDeliveryman.first())

        val users = staffApiGWActions.getUsersByDarkstore(
            tokens!!.accessToken,
            commonPreconditions.createProfileRequest().darkstoreId.toString(),
            mutableListOf(EmployeeRole.DELIVERYMAN.value)
        )
        staffApiGWAssertions
            .checkGroupOfUserIsAbsent(users, createDeliveryman)

    }


    @Test
    @DisplayName("Get staff by supervisor is impossible")
    fun getDeliverymanBySupervisor() {
        val tokens = commonPreconditions
            .createAndAuthorizeStaffUser(
                roles = listOf(ApiEnum(EmployeeRole.SUPERVISOR)),
                vehicle = null,
                staffPartnerId = null,
                darkstoreId = null,
                cityId = null
            )

        staffApiGWActions.getUsersByDarkstoreWithError(
            tokens!!.accessToken,
            Constants.darkstoreId.toString(),
            mutableListOf(EmployeeRole.DELIVERYMAN.value),
            HttpStatus.SC_FORBIDDEN
        )

    }

    @Test
    @Tags(Tag("emproIntegration"))
    @DisplayName("Get staff from foreign darkstore is impossible")
    fun getForeignStaff() {
        val tokens = commonPreconditions
            .createAndAuthorizeStaffUser(
                roles = listOf(ApiEnum(EmployeeRole.GOODS_MANAGER)),
                vehicle = null,
                staffPartnerId = null
            )

        staffApiGWActions.getUsersByDarkstoreWithError(
            tokens!!.accessToken,
            Constants.darkstoreIdWithNotMoscowTimezone.toString(),
            mutableListOf(EmployeeRole.DELIVERYMAN.value),
            HttpStatus.SC_FORBIDDEN
        )
    }

    @Test
    @Tags(Tag("smoke"), Tag("emproIntegration"))
    @DisplayName("Get staff by disabled user is impossible")
    fun getStaffByDisabledUser() {
        val tokens = commonPreconditions
            .createAndAuthorizeStaffUser(
                roles = listOf(ApiEnum(EmployeeRole.GOODS_MANAGER)),
                vehicle = null,
                staffPartnerId = null
            )

        employeeActions.deleteProfile(Constants.mobile1)

        staffApiGWActions.getUsersByDarkstoreWithError(
            tokens!!.accessToken,
            commonPreconditions.createProfileRequest().darkstoreId.toString(),
            mutableListOf(EmployeeRole.DELIVERYMAN.value),
            HttpStatus.SC_FORBIDDEN
        )

    }

}