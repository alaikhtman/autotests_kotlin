package ru.samokat.mysamokat.tests.tests.staff_cave_apigateway.usersAPI

import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.samokat.employeeprofiles.api.common.domain.EmployeeVehicleType
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
class DeleteUser {

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
        getAuthToken()
    }

    @AfterEach
    fun release() {
        scAssertion.assertAll()
        commonAssertions.assertAll()
        employeeActions.deleteProfile(Constants.mobile1)
        employeeActions.deleteProfile(Constants.mobile2)
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
    @DisplayName("Delete profile by tech support")
    fun deleteActiveProfileTest(){

        val createUserRequest = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
            darkstoreId = Constants.darkstoreId,
            vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
            staffPartnerId = Constants.staffPartnerId
        )
        val createdUser = scActions.createUser(token, createUserRequest)!!.user.userId

        scActions.deleteUser(token, createdUser)


        val user = scActions.getUserByProfileId(token, createdUser)!!

        scAssertion.checkUserData(user, createUserRequest, 2)
    }

    @Test
    @Tags(Tag("smoke"), Tag("emproIntegration"))
    @DisplayName("Delete profile by staff manager")
    fun deleteActiveProfileByStaffManagerTest(){
        getAuthToken(EmployeeRole.STAFF_MANAGER)
        val createUserRequest = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
            darkstoreId = Constants.darkstoreId,
            vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
            staffPartnerId = Constants.staffPartnerId
        )
        val createdUser = scActions.createUser(token, createUserRequest)!!.user.userId

        scActions.deleteUser(token, createdUser)


        val user = scActions.getUserByProfileId(token, createdUser)!!

        scAssertion.checkUserData(user, createUserRequest, 2)
    }

    @Test
    @DisplayName("Delete inactive profile")
    fun deleteInactiveProfileTest(){

        val createUserRequest = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
            darkstoreId = Constants.darkstoreId,
            vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
            staffPartnerId = Constants.staffPartnerId
        )
        val createdUser = scActions.createUser(token, createUserRequest)!!.user.userId

        employeeActions.deleteProfile(createdUser)
        scActions.deleteUser(token, createdUser)


        val user = scActions.getUserByProfileId(token, createdUser)!!

        scAssertion.checkUserData(user, createUserRequest, 2)
    }

    @Test
    @DisplayName("Delete profile (user not exist)")
    fun deleteNotExistedProfileTest(){

        scActions.deleteUserWithErrorEmptyResult(token, UUID.randomUUID())
    }



}