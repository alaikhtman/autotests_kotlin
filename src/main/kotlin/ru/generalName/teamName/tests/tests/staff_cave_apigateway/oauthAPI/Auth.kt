package ru.samokat.mysamokat.tests.tests.staff_cave_apigateway.oauthAPI

import org.apache.http.HttpStatus
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.samokat.employeeprofiles.api.common.domain.EmployeeVehicleType
import ru.samokat.employeeprofiles.api.common.domain.Vehicle
import ru.samokat.my.domain.employee.EmployeeRole
import ru.samokat.my.rest.api.enum.ApiEnum
import ru.samokat.mysamokat.tests.checkers.CommonAssertion
import ru.samokat.mysamokat.tests.checkers.MySamokatApiGWAssertions
import ru.samokat.mysamokat.tests.checkers.StaffCaveApiGWAssertions
import ru.samokat.mysamokat.tests.dataproviders.Constants
import ru.samokat.mysamokat.tests.dataproviders.StringAndPhoneNumberGenerator
import ru.samokat.mysamokat.tests.dataproviders.preconditions.CommonPreconditions
import ru.samokat.mysamokat.tests.dataproviders.preconditions.MySamokatApiGWPreconditions
import ru.samokat.mysamokat.tests.dataproviders.preconditions.StaffCaveApiGWPreconditions
import ru.samokat.mysamokat.tests.helpers.actions.EmployeeActions
import ru.samokat.mysamokat.tests.helpers.actions.StaffCaveApiGWActions

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Tags(Tag("staff-cave-apigateway"), Tag("Auth"))
class Auth {

    @Autowired
    private lateinit var scActions: StaffCaveApiGWActions

    @Autowired
    private lateinit var employeeActions: EmployeeActions

    @Autowired
    private lateinit var commonPreconditions: CommonPreconditions

    private lateinit var commonAssertions: CommonAssertion

    private lateinit var scPreconditions: StaffCaveApiGWPreconditions

    private lateinit var scAssertion: StaffCaveApiGWAssertions

    @BeforeEach
    fun before() {
        scPreconditions = StaffCaveApiGWPreconditions()
        scAssertion = StaffCaveApiGWAssertions()
        commonAssertions = CommonAssertion()
        employeeActions.deleteProfile(Constants.mobile1)
    }

    @AfterEach
    fun release() {
        scAssertion.assertAll()
        commonAssertions.assertAll()
        employeeActions.deleteProfile(Constants.mobile1)
    }

    @Test
    @Tags(Tag("smoke"), Tag("emproIntegration"))
    @DisplayName("Auth by password: tech-support")
    fun authByPasswordSupport() {

        val profile = commonPreconditions.createProfile(
            roles = listOf(ApiEnum(EmployeeRole.TECH_SUPPORT))
        )
        val authRequest = scPreconditions.fillAuthRequest(password = profile.generatedPassword!!)

        val tokens = scActions.authProfilePassword(authRequest)

        scAssertion.checkOauthTokensExists(tokens)
    }

    @Test
    @Tags(Tag("smoke"), Tag("emproIntegration"))
    @DisplayName("Auth by password: staff-manager")
    fun authByPasswordStaffManager() {

        val profile = commonPreconditions.createProfile(
            roles = listOf(ApiEnum(EmployeeRole.STAFF_MANAGER))
        )
        val authRequest = scPreconditions.fillAuthRequest(password = profile.generatedPassword!!)

        val tokens = scActions.authProfilePassword(authRequest)

        scAssertion.checkOauthTokensExists(tokens)
    }

    @Test
    @DisplayName("Auth by password: wrong password")
    fun authByWrongPassword() {

        commonPreconditions.createProfile(
            roles = listOf(ApiEnum(EmployeeRole.STAFF_MANAGER))
        )
        val authRequest = scPreconditions.fillAuthRequest(password = StringAndPhoneNumberGenerator.generateRandomString(6).toCharArray())

        val tokens = scActions.authProfilePasswordError(authRequest, HttpStatus.SC_UNAUTHORIZED)

        commonAssertions.checkErrorMessage(tokens!!.code.toString(), "IncorrectCredentials")
        commonAssertions.checkErrorMessage(tokens.message.toString(), "Given credentials are invalid")
    }

    @Test
    @DisplayName("Auth by password: forbidden role")
    fun authByPasswordForbiddenRole() {

        val profile = commonPreconditions.createProfile(
            roles = listOf(ApiEnum(EmployeeRole.SUPERVISOR))
        )
        val authRequest = scPreconditions.fillAuthRequest(
            password = profile.generatedPassword!!)

        val tokensError = scActions.authProfilePasswordError(authRequest, HttpStatus.SC_FORBIDDEN)

        commonAssertions.checkErrorMessage(tokensError!!.code.toString(), "Forbidden")
        commonAssertions.checkErrorMessage(tokensError.message.toString(), "User is forbidden")
    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Auth by password: user is blocked")
    fun authByPasswordUserIsBlocked() {

        val profile = commonPreconditions.createProfile(
            roles = listOf(ApiEnum(EmployeeRole.STAFF_MANAGER)))

        val authRequest = scPreconditions.fillAuthRequest(password = profile.generatedPassword!!)
        employeeActions.deleteProfile(profile.profileId)

        val tokensError = scActions.authProfilePasswordError(authRequest, HttpStatus.SC_UNAUTHORIZED)
        commonAssertions.checkErrorMessage(tokensError!!.code.toString(), "IncorrectCredentials")
        commonAssertions.checkErrorMessage(tokensError.message.toString(), "Given credentials are invalid")
    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Auth by password: refresh token")
    fun authByPasswordTokenRefresh() {

        val profile = commonPreconditions.createProfile(
            roles = listOf(ApiEnum(EmployeeRole.STAFF_MANAGER)))

        val authRequest = scPreconditions.fillAuthRequest(password = profile.generatedPassword!!)
        val tokens = scActions.authProfilePassword(authRequest)

        val refresh = scActions.refreshToken(scPreconditions.fillRefreshTokenRequest(tokens!!.refreshToken!!))

        scAssertion.checkOauthTokensExists(refresh!!)
    }

    @Test
    @DisplayName("Auth by password: delete token")
    fun authByPasswordDeleteToken() {
        val profile = commonPreconditions.createProfile(
            roles = listOf(ApiEnum(EmployeeRole.STAFF_MANAGER)))
        val authRequest = scPreconditions.fillAuthRequest(password = profile.generatedPassword!!)
        val tokens = scActions.authProfilePassword(authRequest)

        scActions.deleteToken(tokens.accessToken, HttpStatus.SC_NO_CONTENT)
    }
}