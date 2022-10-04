package ru.samokat.mysamokat.tests.tests.staff_cave_apigateway.usersAPI

import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.samokat.my.domain.employee.EmployeeRole
import ru.samokat.my.rest.api.enum.ApiEnum
import ru.samokat.mysamokat.tests.checkers.CommonAssertion
import ru.samokat.mysamokat.tests.checkers.StaffCaveApiGWAssertions
import ru.samokat.mysamokat.tests.dataproviders.Constants
import ru.samokat.mysamokat.tests.dataproviders.preconditions.CommonPreconditions
import ru.samokat.mysamokat.tests.dataproviders.preconditions.EmployeePreconditions
import ru.samokat.mysamokat.tests.dataproviders.preconditions.StaffCaveApiGWPreconditions
import ru.samokat.mysamokat.tests.helpers.actions.EmployeeActions
import ru.samokat.mysamokat.tests.helpers.actions.StaffCaveApiGWActions
import java.util.*

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Tags(Tag("staff-cave-apigateway"))
class GetProfileByID {
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

    private lateinit var employeePreconditions: EmployeePreconditions

    @BeforeEach
    fun before() {
        scPreconditions = StaffCaveApiGWPreconditions()
        employeePreconditions = EmployeePreconditions()
        scAssertion = StaffCaveApiGWAssertions()
        commonAssertions = CommonAssertion()
        employeeActions.deleteProfile(Constants.mobile1)
        employeeActions.deleteProfile(Constants.mobile2)
        getAuthToken()
    }

    @AfterEach
    fun release() {
        scAssertion.assertAll()
        commonAssertions.assertAll()
        employeeActions.deleteProfile(Constants.mobile1)
        employeeActions.deleteProfile(Constants.mobile2)
    }

    fun getAuthToken() {
        val profile = commonPreconditions.createProfile(
            roles = listOf(ApiEnum(EmployeeRole.TECH_SUPPORT))
        )
        val authRequest = scPreconditions.fillAuthRequest(password = profile.generatedPassword!!)
        token = scActions.authProfilePassword(authRequest).accessToken

    }

    @Test
    @Tags(Tag("kafka_produce"), Tag("smoke"))
    @DisplayName("Check signature for darkstore admin with contract")
    fun getProfileCheckSignatureForDarkstoreAdminWithContractTest() {

        val event = employeePreconditions.fillPriemNaRabotyEvent()
        employeeActions.produceToPriemNaRabotuCFZ(event)

        val createUserRequest = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.DARKSTORE_ADMIN)),
            darkstoreId = Constants.darkstoreId,
            accountingProfileId = event.payload[0].fizicheskoeLitso.inn
        )
        val createdUser = scActions.createUser(token, createUserRequest)!!.user.userId


        val user = scActions.getUserByProfileId(token, createdUser)!!

        scAssertion.checkUserSignature(user, true)
    }

    @Test
    @Tags(Tag("kafka_produce"), Tag("smoke"))
    @DisplayName("Check signature for goods manager with contract")
    fun getProfileCheckSignatureForGoodsManagerWithContractTest() {

        val event = employeePreconditions.fillPriemNaRabotyEvent()
        employeeActions.produceToPriemNaRabotuCFZ(event)

        val createUserRequest = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.GOODS_MANAGER)),
            darkstoreId = Constants.darkstoreId,
            accountingProfileId = event.payload[0].fizicheskoeLitso.inn
        )
        val createdUser = scActions.createUser(token, createUserRequest)!!.user.userId


        val user = scActions.getUserByProfileId(token, createdUser)!!

        scAssertion.checkUserSignature(user, true)
    }

    @Test
    @Tags(Tag("kafka_produce"), Tag("smoke"))
    @DisplayName("Check signature for darkstore admin without contract")
    fun getProfileCheckSignatureForDarkstoreAdminWithoutContractTest() {

        val createUserRequest = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.DARKSTORE_ADMIN)),
            darkstoreId = Constants.darkstoreId
        )
        val createdUser = scActions.createUser(token, createUserRequest)!!.user.userId


        val user = scActions.getUserByProfileId(token, createdUser)!!

        scAssertion.checkUserSignature(user, false)
    }

    @Test
    @Tags(Tag("kafka_produce"), Tag("smoke"))
    @DisplayName("Check signature for goods manager with two contract")
    fun getProfileCheckSignatureForGoodsManagerWithTwoContractTest() {

        val event1 = employeePreconditions.fillPriemNaRabotyEvent()
        val event2 = employeePreconditions.fillPriemNaRabotyEvent(inn = event1.payload[0].fizicheskoeLitso.inn!!)
        employeeActions.produceToPriemNaRabotuCFZ(event1)
        employeeActions.produceToPriemNaRabotuCFZ(event2)

        val createUserRequest = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.DARKSTORE_ADMIN)),
            darkstoreId = Constants.darkstoreId,
            accountingProfileId = event1.payload[0].fizicheskoeLitso.inn
        )
        val createdUser = scActions.createUser(token, createUserRequest)!!.user.userId


        val user = scActions.getUserByProfileId(token, createdUser)!!

        scAssertion.checkUserSignature(user, false)
    }

    @Test
    @DisplayName("Get user by id - profile not exists")
    fun getProfileNotExistTest() {

        scActions.getUserByProfileIdError(token, UUID.randomUUID())!!
    }
}