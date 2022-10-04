package ru.samokat.mysamokat.tests.tests.staff_cave_apigateway.usersAPI

import org.apache.http.HttpStatus
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.samokat.employeeprofiles.api.common.domain.EmployeeVehicleType
import ru.samokat.my.domain.Email
import ru.samokat.my.domain.employee.EmployeeRole
import ru.samokat.my.rest.api.enum.ApiEnum
import ru.samokat.mysamokat.tests.checkers.CommonAssertion
import ru.samokat.mysamokat.tests.checkers.StaffCaveApiGWAssertions
import ru.samokat.mysamokat.tests.dataproviders.Constants
import ru.samokat.mysamokat.tests.dataproviders.StringAndPhoneNumberGenerator
import ru.samokat.mysamokat.tests.dataproviders.apiModels.staffcave.users.Vehicle
import ru.samokat.mysamokat.tests.dataproviders.preconditions.CommonPreconditions
import ru.samokat.mysamokat.tests.dataproviders.preconditions.StaffCaveApiGWPreconditions
import ru.samokat.mysamokat.tests.helpers.actions.EmployeeActions
import ru.samokat.mysamokat.tests.helpers.actions.StaffCaveApiGWActions
import java.util.*

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Tags(Tag("staff-cave-apigateway"))
class CreateProfile {

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
    @DisplayName("Create profile - deliveryman")
    fun createProfileDeliverymanTest() {

        val createUserRequest = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
            darkstoreId = Constants.darkstoreId,
            vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
            staffPartnerId = Constants.staffPartnerId
        )

        val createdUser = scActions.createUser(token, createUserRequest)

        val user = scActions.getUserByProfileId(token, createdUser!!.user.userId)!!

        scAssertion.checkUserData(user, createUserRequest)
    }

    @Test
    @DisplayName("Create profile - picker (by staff-manager)")
    fun createProfilePickerTest() {

        getAuthToken(EmployeeRole.STAFF_MANAGER)

        val createUserRequest = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.PICKER)),
            darkstoreId = Constants.darkstoreId,
            staffPartnerId = Constants.staffPartnerId
        )

        val createdUser = scActions.createUser(token, createUserRequest)

        val user = scActions.getUserByProfileId(token, createdUser!!.user.userId)!!

        scAssertion.checkUserData(user, createUserRequest)
    }

    @Test
    @Tags(Tag("smoke"), Tag("emproIntegration"))
    @DisplayName("Create profile - darkstore_admin")
    fun createProfileDarkstoreAdminTest() {

        val createUserRequest = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.DARKSTORE_ADMIN)),
            darkstoreId = Constants.darkstoreId
        )

        val createdUser = scActions.createUser(token, createUserRequest)

        val user = scActions.getUserByProfileId(token, createdUser!!.user.userId)!!

        scAssertion.checkUserData(user, createUserRequest)
    }

    @Test
    @DisplayName("Create profile - goods_manager")
    fun createProfileGoodsManagerTest() {

        val createUserRequest = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.GOODS_MANAGER)),
            darkstoreId = Constants.darkstoreId
        )

        val createdUser = scActions.createUser(token, createUserRequest)

        val user = scActions.getUserByProfileId(token, createdUser!!.user.userId)!!

        scAssertion.checkUserData(user, createUserRequest)
    }

    @Test
    @DisplayName("Create profile - supervisor")
    fun createProfileSuperVisorTest() {

        val createUserRequest = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.SUPERVISOR))
        )

        val createdUser = scActions.createUser(token, createUserRequest)

        val user = scActions.getUserByProfileId(token, createdUser!!.user.userId)!!

        scAssertion.checkUserData(user, createUserRequest)
    }

    @Test
    @DisplayName("Create profile - auditor")
    fun createProfileAuditorTest() {

        val createUserRequest = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.AUDITOR))
        )

        val createdUser = scActions.createUser(token, createUserRequest)

        val user = scActions.getUserByProfileId(token, createdUser!!.user.userId)!!

        scAssertion.checkUserData(user, createUserRequest)
    }

    @Test
    @DisplayName("Create profile - staff_manager")
    fun createProfileStaffManagerTest() {

        val createUserRequest = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.STAFF_MANAGER))
        )
        val createdUser = scActions.createUser(token, createUserRequest)

        val user = scActions.getUserByProfileId(token, createdUser!!.user.userId)!!

        scAssertion.checkUserData(user, createUserRequest)
    }

    @Test
    @DisplayName("Create profile - forwarder")
    fun createProfileForwarderTest() {

        val createUserRequest = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.FORWARDER)),
            darkstoreId = Constants.darkstoreId,
            vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR))
        )

        val createdUser = scActions.createUser(token, createUserRequest)

        val user = scActions.getUserByProfileId(token, createdUser!!.user.userId)!!

        scAssertion.checkUserData(user, createUserRequest)
    }

    @Test
    @Tags(Tag("smoke"), Tag("emproIntegration"))
    @DisplayName("Create profile - counterparty")
    fun createProfileCounterPartyTest() {

        val createUserRequest = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.COUNTERPARTY)),
            email = Email(Constants.defaultEmail),
            staffPartnerId = Constants.staffPartnerId
        )

        val createdUser = scActions.createUser(token, createUserRequest)

        val user = scActions.getUserByProfileId(token, createdUser!!.user.userId)!!

        scAssertion.checkUserData(user, createUserRequest)
    }

    @Test
    @Tags(Tag("smoke"), Tag("emproIntegration"))
    @DisplayName("Create profile - coordinator")
    fun createProfileCoordinatorTest() {

        val createUserRequest = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.COORDINATOR)),
            supervisedDarkstores = mutableListOf(Constants.darkstoreId, Constants.updatedDarkstoreId)
        )

        val createdUser = scActions.createUser(token, createUserRequest)

        val user = scActions.getUserByProfileId(token, createdUser!!.user.userId)!!

        scAssertion.checkUserData(user, createUserRequest)
    }

    @Test
    @DisplayName("Create profile - counterparty (not unique email)")
    fun createProfileCounterPartyWithNotUniqueEmailTest() {

        val createUserRequest1 = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.COUNTERPARTY)),
            email = Email(Constants.defaultEmail),
            staffPartnerId = Constants.staffPartnerId
        )

        val createUserRequest2 = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.COUNTERPARTY)),
            mobile = Constants.mobile3,
            email = Email(Constants.defaultEmail),
            staffPartnerId = Constants.staffPartnerId
        )

        val createdUser1 = scActions.createUser(token, createUserRequest1)
        val createdUser2 = scActions.createUser(token, createUserRequest2)

        val user = scActions.getUserByProfileId(token, createdUser2!!.user.userId)!!

        scAssertion.checkUserData(user, createUserRequest2)
    }

    @Test
    @DisplayName("Create profile - counterparty (not valid email)")
    fun createProfileCounterPartyNotValidEmailTest() {

        val createUserRequest = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.COUNTERPARTY)),
            email = Email("test@test"),
            staffPartnerId = Constants.staffPartnerId
        )

        val errors = scActions.createUserError(token, createUserRequest, HttpStatus.SC_BAD_REQUEST)

        commonAssertions.checkErrorMessage(errors!!.code.toString(), "Incorrect")
            .checkErrorMessage(errors!!.message.toString(), "Invalid email address")
            .checkErrorMessage(errors!!.parameter.toString(), "email")
    }

    @Test
    @Tag("smoke")
    @DisplayName("Create profile - counterparty (with multirole)")
    fun createProfileCounterPartyWithMultiroleTest() {

        val createUserRequest = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.COUNTERPARTY), ApiEnum(EmployeeRole.SUPERVISOR)),
            email = Email(Constants.defaultEmail),
            staffPartnerId = Constants.staffPartnerId
        )

        val errors = scActions.createUserError(token, createUserRequest, HttpStatus.SC_BAD_REQUEST)

        commonAssertions.checkErrorMessage(errors!!.code.toString(), "Incorrect")
            .checkErrorMessage(errors!!.message.toString(), "Incompatible roles")
            .checkErrorMessage(errors!!.parameter.toString(), "roles")
    }

    @Test
    @DisplayName("Create profile - counterparty (without email)")
    fun createProfileCounterPartyWithoutEmailTest() {

        val createUserRequest = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.COUNTERPARTY)),
            staffPartnerId = Constants.staffPartnerId
        )

        val errors = scActions.createUserError(token, createUserRequest, HttpStatus.SC_BAD_REQUEST)

        commonAssertions.checkErrorMessage(errors!!.code.toString(), "Incorrect")
            .checkErrorMessage(errors!!.message.toString(), "Email is required")
            .checkErrorMessage(errors!!.parameter.toString(), "email")
    }

    @Test
    @Tag("darkstore_integration")
    @DisplayName("Create profile - with planning darkstore")
    fun createProfileWithPlanningDarkstoreTest() {

        val createUserRequest = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
            darkstoreId = Constants.planningDarkstore,
            vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
            staffPartnerId = Constants.staffPartnerId
        )


        val createdUser = scActions.createUser(token, createUserRequest)

        val user = scActions.getUserByProfileId(token, createdUser!!.user.userId)!!

        scAssertion.checkUserData(user, createUserRequest)
    }

    @Test
    @Tag("darkstore_integration")
    @DisplayName("Create profile - with inactive darkstore")
    fun createProfileWithInactiveDarkstoreTest() {

        val createUserRequest = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
            darkstoreId = Constants.inactiveDarkstore,
            vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
            staffPartnerId = Constants.staffPartnerId
        )

        val errors = scActions.createUserError(token, createUserRequest, HttpStatus.SC_BAD_REQUEST)

        commonAssertions.checkErrorMessage(errors!!.code.toString(), "Incorrect")
            .checkErrorMessage(errors!!.message.toString(), "Darkstore is inactive")
            .checkErrorMessage(errors!!.parameter.toString(), "darkstoreId")
    }

    @Test
    @Tag("darkstore_integration")
    @DisplayName("Create profile - coordinator (with planning and active ds)")
    fun createProfileCoordinatorWithPlanningAndActiveDSTest() {

        val createUserRequest = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.COORDINATOR)),
            supervisedDarkstores = mutableListOf(Constants.darkstoreId, Constants.planningDarkstore)
        )

        val createdUser = scActions.createUser(token, createUserRequest)

        val user = scActions.getUserByProfileId(token, createdUser!!.user.userId)!!

        scAssertion.checkUserData(user, createUserRequest)
    }

    @Test
    @Tag("darkstore_integration")
    @DisplayName("Create profile - coordinator (with inactive and active ds)")
    fun createProfileCoordinatorWithInactiveAndActiveDSTest() {

        val createUserRequest = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.COORDINATOR)),
            supervisedDarkstores = mutableListOf(Constants.inactiveDarkstore, Constants.planningDarkstore)
        )

        val errors = scActions.createUserError(token, createUserRequest, HttpStatus.SC_BAD_REQUEST)
        commonAssertions.checkErrorMessage(errors!!.code.toString(), "InvalidListOfDarkstores")
            .checkErrorMessage(errors!!.message.toString(), "Invalid list of darkstores")
            .checkErrorMessage(errors!!.parameter.toString(), "supervisedDarkstores")
    }

    @Test
    @Tag("darkstore_integration")
    @DisplayName("Create profile - with one darkstore")
    fun createProfileWithOneDarkstoreTest() {

        val createUserRequest = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.COORDINATOR)),
            supervisedDarkstores = mutableListOf(Constants.darkstoreId)
        )

        val createdUser = scActions.createUser(token, createUserRequest)

        val user = scActions.getUserByProfileId(token, createdUser!!.user.userId)!!

        scAssertion.checkUserData(user, createUserRequest)
    }

    @Test
    @Tag("darkstore_integration")
    @DisplayName("Create profile - with not existed darkstore")
    fun createProfileWithNotExistedDarkstoreTest() {

        val createUserRequest = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.COORDINATOR)),
            supervisedDarkstores = mutableListOf(UUID.randomUUID())
        )

        val errors = scActions.createUserError(token, createUserRequest, HttpStatus.SC_BAD_REQUEST)
        commonAssertions.checkErrorMessage(errors!!.code.toString(), "InvalidListOfDarkstores")
            .checkErrorMessage(errors!!.message.toString(), "Invalid list of darkstores")
            .checkErrorMessage(errors!!.parameter.toString(), "supervisedDarkstores")
    }

    @Test
    @Tag("darkstore_integration")
    @DisplayName("Create profile - with repeated darkstore")
    fun createProfileWithRepeatedDarkstoreTest() {

        val createUserRequest = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.COORDINATOR)),
            supervisedDarkstores = mutableListOf(Constants.darkstoreId, Constants.darkstoreId)
        )

        val createdUser = scActions.createUser(token, createUserRequest)

        val user = scActions.getUserByProfileId(token, createdUser!!.user.userId)!!

        scAssertion.checkUserData(user, createUserRequest)
    }

    @Test
    @Tag("darkstore_integration")
    @DisplayName("Create profile - without darkstore")
    fun createProfileWithoutDarkstoreTest() {

        val createUserRequest = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.COORDINATOR))
        )

        val errors = scActions.createUserError(token, createUserRequest, HttpStatus.SC_BAD_REQUEST)
        commonAssertions.checkErrorMessage(errors!!.code.toString(), "Incorrect")
            .checkErrorMessage(errors!!.message.toString(), "List of supervised darkstores is required")
            .checkErrorMessage(errors!!.parameter.toString(), "supervisedDarkstores")
    }

    @Test
    @DisplayName("Create profile - multirole")
    fun createProfileMultiroleTest() {

        val createUserRequest = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN), ApiEnum(EmployeeRole.PICKER)),
            darkstoreId = Constants.darkstoreId,
            vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
            staffPartnerId = Constants.staffPartnerId
        )

        val createdUser = scActions.createUser(token, createUserRequest)

        val user = scActions.getUserByProfileId(token, createdUser!!.user.userId)!!

        scAssertion.checkUserData(user, createUserRequest)
    }

    @Test
    @Tag("darkstore_integration")
    @DisplayName("Create profile - with extra fields")
    fun createProfileWihtExtraFieldsTest() {

        val createUserRequest = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.COORDINATOR)),
            staffPartnerId = Constants.staffPartnerId
        )

        val errors = scActions.createUserError(token, createUserRequest, HttpStatus.SC_BAD_REQUEST)
        commonAssertions.checkErrorMessage(errors!!.code.toString(), "Incorrect")
            .checkErrorMessage(errors!!.message.toString(), "List of supervised darkstores is required")
            .checkErrorMessage(errors!!.parameter.toString(), "supervisedDarkstores")
    }

    @Test
    @DisplayName("Create profile - mobile already exists")
    fun createProfileDeliverymanMobileExistsTest() {

        val createUserRequest1 = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
            darkstoreId = Constants.darkstoreId,
            vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
            staffPartnerId = Constants.staffPartnerId
        )
        val createUserRequest2 = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
            darkstoreId = Constants.darkstoreId,
            vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
            staffPartnerId = Constants.staffPartnerId
        )

        val createdUser = scActions.createUser(token, createUserRequest1)

        val errors = scActions.createUserError(token, createUserRequest2, HttpStatus.SC_CONFLICT)
        commonAssertions.checkErrorMessage(errors!!.code.toString(), "InconsistentRequest")
            .checkErrorMessage(errors!!.message.toString(), "Request is inconsistent")
    }

    // accounting profile id
    @Test
    @Tag("smoke")
    @DisplayName("Create profile - deliveryman with accounting profile id")
    fun createProfileDeliverymanWithAccountingProfileIdTest() {

        val createUserRequest = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
            darkstoreId = Constants.darkstoreId,
            vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
            staffPartnerId = Constants.staffPartnerId,
            accountingProfileId = Constants.accountingProfileIdForRequisitions.toString()
        )

        val createdUser = scActions.createUser(token, createUserRequest)

        val user = scActions.getUserByProfileId(token, createdUser!!.user.userId)!!

        scAssertion.checkUserData(user, createUserRequest)
            .checkAccountingProfileId(user, Constants.accountingProfileIdForRequisitions.toString())
    }

    @Test
    @DisplayName("Create profile - picker with accounting profile id")
    fun createProfilePickerWithAccountingProfileIdTest() {

        val createUserRequest = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.PICKER)),
            darkstoreId = Constants.darkstoreId,
            staffPartnerId = Constants.staffPartnerId,
            accountingProfileId = Constants.accountingProfileIdForRequisitions.toString()
        )

        val createdUser = scActions.createUser(token, createUserRequest)

        val user = scActions.getUserByProfileId(token, createdUser!!.user.userId)!!

        scAssertion.checkUserData(user, createUserRequest)
            .checkAccountingProfileId(user, Constants.accountingProfileIdForRequisitions.toString())
    }

    @Test
    @DisplayName("Create profile - with existing accounting profile id")
    fun createProfileWithExistingAccountingProfileIdTest() {

        val createUserRequest1 = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
            darkstoreId = Constants.darkstoreId,
            vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
            staffPartnerId = Constants.staffPartnerId,
            accountingProfileId = Constants.accountingProfileIdForRequisitions.toString()
        )

        val createUserRequest2 = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
            darkstoreId = Constants.darkstoreId,
            mobile = Constants.mobile3,
            vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
            staffPartnerId = Constants.staffPartnerId,
            accountingProfileId = Constants.accountingProfileIdForRequisitions.toString()
        )

        val createdUser = scActions.createUser(token, createUserRequest1)
        val errors = scActions.createUserError(token, createUserRequest2, HttpStatus.SC_BAD_REQUEST)
        commonAssertions.checkErrorMessage(errors!!.code.toString(), "Incorrect")
            .checkErrorMessage(errors!!.message.toString(), "User with provided accountingProfileId already exists")
            .checkErrorMessage(errors!!.parameter.toString(), "accountingProfileId")
    }

    @Test
    @DisplayName("Create profile - darkstore_admin with accounting profile id")
    fun createProfileDarkstoreAdminWithAccountingProfileIdTest() {

        val createUserRequest = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.DARKSTORE_ADMIN)),
            darkstoreId = Constants.darkstoreId,
            accountingProfileId = Constants.innFL
        )

        val createdUser = scActions.createUser(token, createUserRequest)

        val user = scActions.getUserByProfileId(token, createdUser!!.user.userId)!!

        scAssertion.checkUserData(user, createUserRequest)
            .checkAccountingProfileId(user, Constants.innFL)
    }

    @Test
    @DisplayName("Create profile - goods manager with accounting profile id")
    fun createProfileGoodsManagerWithAccountingProfileIdTest() {

        val createUserRequest = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.GOODS_MANAGER)),
            darkstoreId = Constants.darkstoreId,
            accountingProfileId = Constants.innFL
        )

        val createdUser = scActions.createUser(token, createUserRequest)

        val user = scActions.getUserByProfileId(token, createdUser!!.user.userId)!!

        scAssertion.checkUserData(user, createUserRequest)
            .checkAccountingProfileId(user, Constants.innFL)
    }

    @Test
    @DisplayName("Create profile - with wrong token is impossible")
    fun createProfileDeliverymanWithWrongTokenTest() {

        val createUserRequest = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
            darkstoreId = Constants.darkstoreId,
            vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
            staffPartnerId = Constants.staffPartnerId
        )

        val errors = scActions.createUserError(
            StringAndPhoneNumberGenerator.generateRandomString(15),
            createUserRequest,
            HttpStatus.SC_UNAUTHORIZED
        )
        commonAssertions.checkErrorMessage(errors!!.code.toString(), "IncorrectCredentials")
            .checkErrorMessage(errors!!.message.toString(), "Provided JWT has incorrect format")

    }
}