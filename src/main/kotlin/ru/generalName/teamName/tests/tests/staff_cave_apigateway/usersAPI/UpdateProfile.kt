package ru.samokat.mysamokat.tests.tests.staff_cave_apigateway.usersAPI

import org.apache.http.HttpStatus
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.samokat.employeeprofiles.api.common.domain.EmployeeVehicleType
import ru.samokat.my.domain.Email
import ru.samokat.my.domain.employee.EmployeeName
import ru.samokat.my.domain.employee.EmployeeRole
import ru.samokat.my.rest.api.enum.ApiEnum
import ru.samokat.mysamokat.tests.checkers.CommonAssertion
import ru.samokat.mysamokat.tests.checkers.StaffCaveApiGWAssertions
import ru.samokat.mysamokat.tests.dataproviders.Constants
import ru.samokat.mysamokat.tests.dataproviders.apiModels.staffcave.users.Vehicle
import ru.samokat.mysamokat.tests.dataproviders.preconditions.CommonPreconditions
import ru.samokat.mysamokat.tests.dataproviders.preconditions.StaffCaveApiGWPreconditions
import ru.samokat.mysamokat.tests.helpers.actions.EmployeeActions
import ru.samokat.mysamokat.tests.helpers.actions.StaffCaveApiGWActions
import java.util.*

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Tags(Tag("staff-cave-apigateway"))
class UpdateProfile {
    @Autowired
    private lateinit var scActions: StaffCaveApiGWActions

    @Autowired
    private lateinit var employeeActions: EmployeeActions

    @Autowired
    private lateinit var commonPreconditions: CommonPreconditions

    private lateinit var commonAssertions: CommonAssertion

    private lateinit var scPreconditions: StaffCaveApiGWPreconditions

    private lateinit var scAssertion: StaffCaveApiGWAssertions

    private lateinit var token: String

    @BeforeEach
    fun before() {
        scPreconditions = StaffCaveApiGWPreconditions()
        scAssertion = StaffCaveApiGWAssertions()
        commonAssertions = CommonAssertion()
        employeeActions.deleteProfile(Constants.mobile1)
        employeeActions.deleteProfile(Constants.mobile2)
        employeeActions.deleteProfile(Constants.mobile3)
        getAuthToken()
    }

    @AfterEach
    fun release() {
        scAssertion.assertAll()
        commonAssertions.assertAll()
        employeeActions.deleteProfile(Constants.mobile1)
        employeeActions.deleteProfile(Constants.mobile2)
        employeeActions.deleteProfile(Constants.mobile3)
    }

    fun getAuthToken(role: EmployeeRole = EmployeeRole.TECH_SUPPORT) {
        employeeActions.deleteProfile(Constants.mobile1)
        val profile = commonPreconditions.createProfile(
            roles = listOf(ApiEnum(role))
        )
        val authRequest = scPreconditions.fillAuthRequest(password = profile.generatedPassword!!)
        token = scActions.authProfilePassword(authRequest).accessToken

    }

    @Test
    @Tags(Tag("smoke"), Tag("emproIntegration"))
    @DisplayName("Update profile - deliveryman (change vehicle)")
    fun updateDeliverymanVehicleTest() {

        val createUserRequest = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
            darkstoreId = Constants.darkstoreId,
            vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
            staffPartnerId = Constants.staffPartnerId
        )
        val updateUserRequest = scPreconditions.fillUpdateUserRequest(
            createUserRequest,
            vehicle = Vehicle(ApiEnum(EmployeeVehicleType.ELECTRIC_BICYCLE))
        )

        val createdUser = scActions.createUser(token, createUserRequest)!!.user.userId
        val updatedUser = scActions.updateUser(token, updateUserRequest, createdUser)

        val user = scActions.getUserByProfileId(token, createdUser)!!

        scAssertion.checkUserData(user, updateUserRequest, 2)
    }

    @Test
    @DisplayName("Update profile - forwarder (change name - delete middle name) by staff-partner")
    fun updateForwarderDeleteMiddleNameTest() {

        getAuthToken(EmployeeRole.STAFF_MANAGER)
        val createUserRequest = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.FORWARDER)),
            darkstoreId = Constants.darkstoreId,
            vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR))
        )
        val updateUserRequest =
            scPreconditions.fillUpdateUserRequest(createUserRequest, name = EmployeeName("New", "Name"))

        val createdUser = scActions.createUser(token, createUserRequest)!!.user.userId
        val updatedUser = scActions.updateUser(token, updateUserRequest, createdUser)

        val user = scActions.getUserByProfileId(token, createdUser)!!

        scAssertion.checkUserData(user, updateUserRequest, 2)
    }

    @Test
    @DisplayName("Update profile - supervisor (change name - add middle name)")
    fun updateSupervisorNameTest() {

        val createUserRequest = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.SUPERVISOR)),
            name = EmployeeName("Иванов", "Иван")
        )
        val updateUserRequest =
            scPreconditions.fillUpdateUserRequest(createUserRequest, name = EmployeeName("Иванов", "Иван", "Иванович"))

        val createdUser = scActions.createUser(token, createUserRequest)!!.user.userId
        val updatedUser = scActions.updateUser(token, updateUserRequest, createdUser)

        val user = scActions.getUserByProfileId(token, createdUser)!!

        scAssertion.checkUserData(user, updateUserRequest, 2)
    }

    @Test
    @DisplayName("Update profile - counterparty (change email)")
    fun updateCounterPartyEmailTest() {

        val createUserRequest = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.COUNTERPARTY)),
            staffPartnerId = Constants.staffPartnerId,
            email = Email(Constants.defaultEmail)
        )
        val updateUserRequest = scPreconditions.fillUpdateUserRequest(createUserRequest, email = Email("test2@tt.tt"))

        val createdUser = scActions.createUser(token, createUserRequest)!!.user.userId
        val updatedUser = scActions.updateUser(token, updateUserRequest, createdUser)

        val user = scActions.getUserByProfileId(token, createdUser)!!

        scAssertion.checkUserData(user, updateUserRequest, 2)
    }

    @Test
    @Tag("smoke")
    @DisplayName("Update profile - terr manager (change mobile)")
    fun updateTerrManagerMobileTest() {

        val createUserRequest = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.TERRITORIAL_MANAGER))
        )
        val updateUserRequest = scPreconditions.fillUpdateUserRequest(createUserRequest, mobile = Constants.mobile3)

        val createdUser = scActions.createUser(token, createUserRequest)!!.user.userId
        val updatedUser = scActions.updateUser(token, updateUserRequest, createdUser)

        val user = scActions.getUserByProfileId(token, createdUser)!!

        scAssertion.checkUserData(user, updateUserRequest, 2)
    }

    @Test
    @Tag("smoke")
    @DisplayName("Update profile - with multirole")
    fun updateProfileWithMultiroleTest() {

        val createUserRequest = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN), ApiEnum(EmployeeRole.PICKER)),
            darkstoreId = Constants.darkstoreId,
            vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
            staffPartnerId = Constants.staffPartnerId
        )
        val updateUserRequest = scPreconditions.fillUpdateUserRequest(
            createUserRequest,
            vehicle = Vehicle(ApiEnum(EmployeeVehicleType.ELECTRIC_BICYCLE))
        )

        val createdUser = scActions.createUser(token, createUserRequest)!!.user.userId
        val updatedUser = scActions.updateUser(token, updateUserRequest, createdUser)

        val user = scActions.getUserByProfileId(token, createdUser)!!

        scAssertion.checkUserData(user, updateUserRequest, 2)
    }

    @Test
    @DisplayName("Update profile - picker (change darkstore)")
    fun updatePickerDarkstoreTest() {

        val createUserRequest = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.PICKER)),
            darkstoreId = Constants.darkstoreId,
            staffPartnerId = Constants.staffPartnerId
        )
        val updateUserRequest =
            scPreconditions.fillUpdateUserRequest(createUserRequest, darkstoreId = Constants.updatedDarkstoreId)

        val createdUser = scActions.createUser(token, createUserRequest)!!.user.userId
        val updatedUser = scActions.updateUser(token, updateUserRequest, createdUser)

        val user = scActions.getUserByProfileId(token, createdUser)!!

        scAssertion.checkUserData(user, updateUserRequest, 2)
    }

    @Test
    @DisplayName("Update profile - staff manager (change name)")
    fun updateStaffManagerTest() {

        val createUserRequest = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.STAFF_MANAGER))
        )
        val updateUserRequest = scPreconditions.fillUpdateUserRequest(
            createUserRequest,
            name = EmployeeName("новый", "кадровый", "специалист")
        )

        val createdUser = scActions.createUser(token, createUserRequest)!!.user.userId
        val updatedUser = scActions.updateUser(token, updateUserRequest, createdUser)

        val user = scActions.getUserByProfileId(token, createdUser)!!

        scAssertion.checkUserData(user, updateUserRequest, 2)
    }

    @Test
    @DisplayName("Update profile - auditor (change role to counterparty)")
    fun updateAuditorRoleToCounterpartyTest() {

        val createUserRequest = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.AUDITOR))
        )
        val updateUserRequest = scPreconditions.fillUpdateUserRequest(
            createUserRequest,
            roles = listOf(ApiEnum(EmployeeRole.COUNTERPARTY)),
            email = Email(Constants.defaultEmail),
            staffPartnerId = Constants.staffPartnerId
        )

        val createdUser = scActions.createUser(token, createUserRequest)!!.user.userId
        val updatedUser = scActions.updateUser(token, updateUserRequest, createdUser)

        val user = scActions.getUserByProfileId(token, createdUser)!!

        scAssertion.checkUserData(user, updateUserRequest, 2)
    }

    @Test
    @DisplayName("Update profile - darkstore_admin (change accountingProfileId)")
    fun updateDarkstoreAdminAccountingProfileIdTest() {

        val createUserRequest = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.DARKSTORE_ADMIN)),
            accountingProfileId = Constants.innFL,
            darkstoreId = Constants.darkstoreId
        )
        val updateUserRequest = scPreconditions.fillUpdateUserRequest(
            createUserRequest,
            accountingProfileId = Constants.innUL
        )

        val createdUser = scActions.createUser(token, createUserRequest)!!.user.userId
        val updatedUser = scActions.updateUser(token, updateUserRequest, createdUser)

        val user = scActions.getUserByProfileId(token, createdUser)!!

        scAssertion.checkUserData(user, updateUserRequest, 2)
    }

    @Test
    @DisplayName("Update profile - goods_manager (change accountingProfileId)")
    fun updateGoodsManagerAccountingProfileIdTest() {

        val createUserRequest = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.GOODS_MANAGER)),
            accountingProfileId = Constants.innFL,
            darkstoreId = Constants.darkstoreId
        )
        val updateUserRequest = scPreconditions.fillUpdateUserRequest(
            createUserRequest,
            accountingProfileId = Constants.innUL
        )

        val createdUser = scActions.createUser(token, createUserRequest)!!.user.userId
        val updatedUser = scActions.updateUser(token, updateUserRequest, createdUser)

        val user = scActions.getUserByProfileId(token, createdUser)!!

        scAssertion.checkUserData(user, updateUserRequest, 2)
    }

    @Test
    @DisplayName("Update profile - change role deliveryman to terr_manager")
    fun updateDeliverymanToTerrManagerTest() {

        val createUserRequest = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
            darkstoreId = Constants.darkstoreId,
            vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
            staffPartnerId = Constants.staffPartnerId
        )
        val updateUserRequest = scPreconditions.fillUpdateUserRequest(
            createUserRequest,
            roles = listOf(ApiEnum(EmployeeRole.TERRITORIAL_MANAGER)),
            darkstoreId = null,
            vehicle = null,
            staffPartnerId = null
        )

        val createdUser = scActions.createUser(token, createUserRequest)!!.user.userId
        val updatedUser = scActions.updateUser(token, updateUserRequest, createdUser)

        val user = scActions.getUserByProfileId(token, createdUser)!!

        scAssertion.checkUserData(user, updateUserRequest, 2)
    }

    @Test
    @DisplayName("Update profile - change role goods_manager with accountingProfileId to auditor")
    fun updateGoodsManagerToAuditorTest() {

        val createUserRequest = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.GOODS_MANAGER)),
            darkstoreId = Constants.darkstoreId,
            accountingProfileId = Constants.innUL
        )
        val updateUserRequest = scPreconditions.fillUpdateUserRequest(
            createUserRequest,
            roles = listOf(ApiEnum(EmployeeRole.AUDITOR)),
            darkstoreId = null,
            vehicle = null,
            staffPartnerId = null,
            accountingProfileId = null
        )

        val createdUser = scActions.createUser(token, createUserRequest)!!.user.userId
        val updatedUser = scActions.updateUser(token, updateUserRequest, createdUser)

        val user = scActions.getUserByProfileId(token, createdUser)!!

        scAssertion.checkUserData(user, updateUserRequest, 2)
    }

    @Test
    @DisplayName("Update profile - change role admin without accountingProfileId to staff_manager")
    fun updateDarkstoreAdminToStaffManagerTest() {

        val createUserRequest = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.DARKSTORE_ADMIN)),
            darkstoreId = Constants.darkstoreId
        )
        val updateUserRequest = scPreconditions.fillUpdateUserRequest(
            createUserRequest,
            roles = listOf(ApiEnum(EmployeeRole.STAFF_MANAGER)),
            darkstoreId = null,
            accountingProfileId = null
        )

        val createdUser = scActions.createUser(token, createUserRequest)!!.user.userId
        val updatedUser = scActions.updateUser(token, updateUserRequest, createdUser)

        val user = scActions.getUserByProfileId(token, createdUser)!!

        scAssertion.checkUserData(user, updateUserRequest, 2)
    }

    @Test
    @DisplayName("Update profile - deliveryman to picker (without change darkstore)")
    fun updateDeliverymanToPickerTest() {

        val createUserRequest = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
            darkstoreId = Constants.darkstoreId,
            vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
            staffPartnerId = Constants.staffPartnerId
        )
        val updateUserRequest = scPreconditions.fillUpdateUserRequest(
            createUserRequest,
            vehicle = null,
            roles = listOf(ApiEnum(EmployeeRole.PICKER))
        )

        val createdUser = scActions.createUser(token, createUserRequest)!!.user.userId
        val updatedUser = scActions.updateUser(token, updateUserRequest, createdUser)

        val user = scActions.getUserByProfileId(token, createdUser)!!

        scAssertion.checkUserData(user, updateUserRequest, 2)
    }

    @Test
    @DisplayName("Update profile - change role deliveryman to admin with accountingProfileId (with ds changed)")
    fun updateDeliverymanToAdminTest() {

        val createUserRequest = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
            darkstoreId = Constants.darkstoreId,
            vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
            staffPartnerId = Constants.staffPartnerId,
            accountingProfileId = Constants.accountingProfileIdForTimesheet1
        )
        val updateUserRequest = scPreconditions.fillUpdateUserRequest(
            createUserRequest,
            roles = listOf(ApiEnum(EmployeeRole.DARKSTORE_ADMIN)),
            accountingProfileId = Constants.innUL,
            darkstoreId = Constants.updatedDarkstoreId,
            vehicle = null,
            staffPartnerId = null
        )

        val createdUser = scActions.createUser(token, createUserRequest)!!.user.userId
        val updatedUser = scActions.updateUser(token, updateUserRequest, createdUser)

        val user = scActions.getUserByProfileId(token, createdUser)!!

        scAssertion.checkUserData(user, updateUserRequest, 2)
    }

    @Test
    @DisplayName("Update profile - change role counterparty to picker")
    fun updateCounterPartyToPickerTest() {

        val createUserRequest = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.COUNTERPARTY)),
            staffPartnerId = Constants.staffPartnerId,
            email = Email(Constants.defaultEmail)
        )
        val updateUserRequest =
            scPreconditions.fillUpdateUserRequest(
                createUserRequest,
                roles = listOf(ApiEnum(EmployeeRole.PICKER)),
                darkstoreId = Constants.darkstoreId,
                staffPartnerId = Constants.staffPartnerId,
                email = null
            )

        val createdUser = scActions.createUser(token, createUserRequest)!!.user.userId
        val updatedUser = scActions.updateUser(token, updateUserRequest, createdUser)

        val user = scActions.getUserByProfileId(token, createdUser)!!

        scAssertion.checkUserData(user, updateUserRequest, 2)
    }

    @Test
    @DisplayName("Update profile - coordinator (delete darkstore)")
    fun updateCoordinatorDeleteDarkstoreTest() {

        val createUserRequest = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.COORDINATOR)),
            supervisedDarkstores = mutableListOf(Constants.darkstoreId, Constants.updatedDarkstoreId)
        )
        val updateUserRequest =
            scPreconditions.fillUpdateUserRequest(
                createUserRequest,
                supervisedDarkstores = mutableListOf(Constants.darkstoreId)
            )

        val createdUser = scActions.createUser(token, createUserRequest)!!.user.userId
        val updatedUser = scActions.updateUser(token, updateUserRequest, createdUser)

        val user = scActions.getUserByProfileId(token, createdUser)!!

        scAssertion.checkUserData(user, updateUserRequest, 2)
    }

    @Test
    @Tag("darkstore_integration")
    @DisplayName("Update profile - coordinator (update darkstore to planning)")
    fun updateCoordinatorUpdateDarkstoreToPlanningTest() {

        val createUserRequest = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.COORDINATOR)),
            supervisedDarkstores = mutableListOf(Constants.darkstoreId, Constants.updatedDarkstoreId)
        )
        val updateUserRequest =
            scPreconditions.fillUpdateUserRequest(
                createUserRequest,
                supervisedDarkstores = mutableListOf(Constants.darkstoreId, Constants.updatedDarkstoreId, Constants.planningDarkstore)
            )

        val createdUser = scActions.createUser(token, createUserRequest)!!.user.userId
        val updatedUser = scActions.updateUser(token, updateUserRequest, createdUser)

        val user = scActions.getUserByProfileId(token, createdUser)!!

        scAssertion.checkUserData(user, updateUserRequest, 2)
    }

    @Test
    @DisplayName("Update profile - coordinator (add unknown ds)")
    fun updateCoordinatorUnknownDSTest() {

        val createUserRequest = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.COORDINATOR)),
            supervisedDarkstores = mutableListOf(Constants.darkstoreId)
        )
        val updateUserRequest = scPreconditions.fillUpdateUserRequest(
            createUserRequest,
            supervisedDarkstores = mutableListOf(Constants.darkstoreId, UUID.randomUUID())
        )

        val createdUser = scActions.createUser(token, createUserRequest)!!.user.userId
        val errors = scActions.updateUserWithError(token, updateUserRequest, createdUser, HttpStatus.SC_BAD_REQUEST)

        commonAssertions.checkErrorMessage(errors!!.code.toString(), "InvalidListOfDarkstores")
            .checkErrorMessage(errors!!.message.toString(), "Invalid list of darkstores")
            .checkErrorMessage(errors!!.parameter.toString(), "supervisedDarkstores")
    }

    @Test
    @Tag("darkstore_integration")
    @DisplayName("Update profile - coordinator (add inactive ds)")
    fun updateCoordinatorInactiveDSTest() {

        val createUserRequest = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.COORDINATOR)),
            supervisedDarkstores = mutableListOf(Constants.darkstoreId)
        )
        val updateUserRequest = scPreconditions.fillUpdateUserRequest(
            createUserRequest,
            supervisedDarkstores = mutableListOf(Constants.darkstoreId, Constants.inactiveDarkstore)
        )

        val createdUser = scActions.createUser(token, createUserRequest)!!.user.userId
        val errors = scActions.updateUserWithError(token, updateUserRequest, createdUser, HttpStatus.SC_BAD_REQUEST)

        commonAssertions.checkErrorMessage(errors!!.code.toString(), "InvalidListOfDarkstores")
            .checkErrorMessage(errors!!.message.toString(), "Invalid list of darkstores")
            .checkErrorMessage(errors!!.parameter.toString(), "supervisedDarkstores")
    }

    @Test
    @DisplayName("Update profile - coordinator (delete all ds)")
    fun updateCoordinatorDeleteAllDSTest() {

        val createUserRequest = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.COORDINATOR)),
            supervisedDarkstores = mutableListOf(Constants.darkstoreId)
        )
        val updateUserRequest = scPreconditions.fillUpdateUserRequest(
            createUserRequest,
            supervisedDarkstores = mutableListOf()
        )

        val createdUser = scActions.createUser(token, createUserRequest)!!.user.userId
        val errors = scActions.updateUserWithError(token, updateUserRequest, createdUser, HttpStatus.SC_BAD_REQUEST)

        commonAssertions.checkErrorMessage(errors!!.code.toString(), "Incorrect")
            .checkErrorMessage(errors!!.message.toString(), "List of supervised darkstores should have size 1 to 1024 elements")
            .checkErrorMessage(errors!!.parameter.toString(), "supervisedDarkstores")
    }

    @Test
    @DisplayName("Update profile - change mobile for existing")
    fun updateMobileToExistingTest() {

        val createUserRequest = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
            darkstoreId = Constants.darkstoreId,
            vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
            staffPartnerId = Constants.staffPartnerId)

        val updateUserRequest = scPreconditions.fillUpdateUserRequest(
            createUserRequest,
            mobile = Constants.mobile1
        )

        val createdUser = scActions.createUser(token, createUserRequest)!!.user.userId
        val errors = scActions.updateUserWithError(token, updateUserRequest, createdUser, HttpStatus.SC_CONFLICT)

        commonAssertions.checkErrorMessage(errors!!.code.toString(), "InconsistentRequest")
            .checkErrorMessage(errors!!.message.toString(), "Profile is already exists")
    }

    @Test
    @DisplayName("Update profile - delete deliveryman vehicle")
    fun updateDeliverymanDeleteVehicleTest() {

        val createUserRequest = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
            darkstoreId = Constants.darkstoreId,
            vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
            staffPartnerId = Constants.staffPartnerId)

        val updateUserRequest = scPreconditions.fillUpdateUserRequest(
            createUserRequest,
            vehicle = null
        )

        val createdUser = scActions.createUser(token, createUserRequest)!!.user.userId
        val errors = scActions.updateUserWithError(token, updateUserRequest, createdUser, HttpStatus.SC_BAD_REQUEST)

        commonAssertions.checkErrorMessage(errors!!.code.toString(), "Incorrect")
            .checkErrorMessage(errors!!.message.toString(), "Vehicle is required")
            .checkErrorMessage(errors!!.parameter.toString(), "vehicle")
    }

    @Test
    @DisplayName("Update profile - update forwarder vehicle")
    fun updateForwarderVehicleTest() {

        val createUserRequest = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.FORWARDER)),
            darkstoreId = Constants.darkstoreId,
            vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR))
        )

        val updateUserRequest = scPreconditions.fillUpdateUserRequest(
            createUserRequest,
            vehicle = Vehicle(ApiEnum(EmployeeVehicleType.ELECTRIC_BICYCLE))
        )

        val createdUser = scActions.createUser(token, createUserRequest)!!.user.userId
        val errors = scActions.updateUserWithError(token, updateUserRequest, createdUser, HttpStatus.SC_BAD_REQUEST)

        commonAssertions.checkErrorMessage(errors!!.code.toString(), "Incorrect")
            .checkErrorMessage(errors!!.message.toString(), "Vehicle type is forbidden")
            .checkErrorMessage(errors!!.parameter.toString(), "vehicle.type")
    }

    @Test
    @Tag("darkstore_integration")
    @DisplayName("Update profile - update darkstore to inactive")
    fun updateDeliverymanDarkstoreToInactiveTest() {

        val createUserRequest = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
            darkstoreId = Constants.darkstoreId,
            vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
            staffPartnerId = Constants.staffPartnerId)

        val updateUserRequest = scPreconditions.fillUpdateUserRequest(
            createUserRequest,
            darkstoreId = Constants.inactiveDarkstore,
        )

        val createdUser = scActions.createUser(token, createUserRequest)!!.user.userId
        val errors = scActions.updateUserWithError(token, updateUserRequest, createdUser, HttpStatus.SC_BAD_REQUEST)

        commonAssertions.checkErrorMessage(errors!!.code.toString(), "Incorrect")
            .checkErrorMessage(errors!!.message.toString(), "Darkstore is inactive")
            .checkErrorMessage(errors!!.parameter.toString(), "darkstoreId")
    }

    @Test
    @DisplayName("Update profile - counterparty (add role)")
    fun updateCounterPartyAddRoleTest() {

        val createUserRequest = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.COUNTERPARTY)),
            staffPartnerId = Constants.staffPartnerId,
            email = Email(Constants.defaultEmail)
        )
        val updateUserRequest = scPreconditions.fillUpdateUserRequest(
            createUserRequest, roles = listOf(ApiEnum(EmployeeRole.COUNTERPARTY), ApiEnum(EmployeeRole.STAFF_MANAGER)))

        val createdUser = scActions.createUser(token, createUserRequest)!!.user.userId
        val errors = scActions.updateUserWithError(token, updateUserRequest, createdUser, HttpStatus.SC_BAD_REQUEST)

        commonAssertions.checkErrorMessage(errors!!.code.toString(), "Incorrect")
            .checkErrorMessage(errors!!.message.toString(), "Incompatible roles")
            .checkErrorMessage(errors!!.parameter.toString(), "roles")
    }

    @Test
    @DisplayName("Update profile - with wrong version")
    fun updateProfileWithWrongVersionTest() {

        val createUserRequest = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
            darkstoreId = Constants.darkstoreId,
            vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
            staffPartnerId = Constants.staffPartnerId
        )
        val updateUserRequest = scPreconditions.fillUpdateUserRequest(
            createUserRequest,
            vehicle = Vehicle(ApiEnum(EmployeeVehicleType.ELECTRIC_BICYCLE)),
            version = 10
        )

        val createdUser = scActions.createUser(token, createUserRequest)!!.user.userId
        val errors = scActions.updateUserWithError(token, updateUserRequest, createdUser, HttpStatus.SC_CONFLICT)

        commonAssertions.checkErrorMessage(errors!!.code.toString(), "ConcurrentModification")
            .checkErrorMessage(errors!!.message.toString(), "Concurrent modification has been detected")
    }

    @Test
    @DisplayName("Update profile - add accountingProfileId (already existing)")
    fun updateDarkstoreAdminAccountingProfileIdExistingTest() {

        val createUserRequest1 = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.DARKSTORE_ADMIN)),
            accountingProfileId = Constants.innFL,
            darkstoreId = Constants.darkstoreId
        )
        val createUserRequest2 = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.DARKSTORE_ADMIN)),
            darkstoreId = Constants.darkstoreId,
            mobile = Constants.mobile3
        )
        val updateUserRequest = scPreconditions.fillUpdateUserRequest(
            createUserRequest2,
            accountingProfileId = Constants.innFL
        )

        val createdUser1 = scActions.createUser(token, createUserRequest1)!!.user.userId
        val createdUser2 = scActions.createUser(token, createUserRequest2)!!.user.userId

        val errors = scActions.updateUserWithError(token, updateUserRequest, createdUser2, HttpStatus.SC_BAD_REQUEST)

        commonAssertions.checkErrorMessage(errors!!.code.toString(), "Incorrect")
            .checkErrorMessage(errors!!.message.toString(), "User with provided accountingProfileId already exists")
            .checkErrorMessage(errors!!.parameter.toString(), "accountingProfileId")
    }

    @Test
    @DisplayName("Update profile - profile deleted")
    fun updateDeletedProfileTest() {

        val createUserRequest = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
            darkstoreId = Constants.darkstoreId,
            vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
            staffPartnerId = Constants.staffPartnerId
        )
        val updateUserRequest = scPreconditions.fillUpdateUserRequest(
            createUserRequest,
            vehicle = Vehicle(ApiEnum(EmployeeVehicleType.ELECTRIC_BICYCLE))
        )

        val createdUser = scActions.createUser(token, createUserRequest)!!.user.userId
        employeeActions.deleteProfile(createdUser)

        scActions.updateUserWithErrorEmptyResult(token, updateUserRequest, createdUser, HttpStatus.SC_NOT_FOUND)
    }
}