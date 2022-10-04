package ru.samokat.mysamokat.tests.tests.staff_cave_apigateway.usersAPI

import org.apache.http.HttpStatus
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

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Tags(Tag("staff-cave-apigateway"))
class RefreshedPassword {

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
    @DisplayName("Update profile password for active user")
    fun updatePasswordForActiveProfileTest() {

        val createUserRequest = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.DARKSTORE_ADMIN)),
            darkstoreId = Constants.darkstoreId
        )
        val createdUser = scActions.createUser(token, createUserRequest)!!


        val newPass = scActions.updateProfilePassword(token, createdUser.user.userId)!!

        scAssertion.checkPassIsNew(newPass.generatedPassword, createdUser.generatedPassword.concatToString())
    }


    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Update profile password for inactive user")
    fun updatePasswordForInactiveProfileTest() {

        val createUserRequest = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.DARKSTORE_ADMIN)),
            darkstoreId = Constants.darkstoreId,
            mobile = Constants.mobile3
        )
        val createdUser = scActions.createUser(token, createUserRequest)!!
        employeeActions.deleteProfile(createdUser.user.userId)


        scActions.updateProfilePasswordWithError(token, createdUser.user.userId)!!
    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Update profile password by staff-manager is impossible")
    fun updatePasswordByStaffManagerTest() {

        getAuthToken(EmployeeRole.STAFF_MANAGER)

        val createUserRequest = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.DARKSTORE_ADMIN)),
            darkstoreId = Constants.darkstoreId
        )
        val createdUser = scActions.createUser(token, createUserRequest)!!
        employeeActions.deleteProfile(createdUser.user.userId)


        val errors = scActions.updateProfilePasswordWithErrorAndMessage(token, createdUser.user.userId, HttpStatus.SC_FORBIDDEN)!!

        commonAssertions.checkErrorMessage(errors!!.code.toString(), "Forbidden")
            .checkErrorMessage(errors!!.message.toString(), "Only tech supports can change password")
    }

}