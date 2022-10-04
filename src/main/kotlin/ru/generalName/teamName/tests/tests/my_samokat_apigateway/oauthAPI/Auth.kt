package ru.samokat.mysamokat.tests.tests.my_samokat_apigateway.oauthAPI

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
import ru.samokat.mysamokat.tests.dataproviders.Constants
import ru.samokat.mysamokat.tests.dataproviders.StringAndPhoneNumberGenerator
import ru.samokat.mysamokat.tests.dataproviders.preconditions.CommonPreconditions
import ru.samokat.mysamokat.tests.dataproviders.preconditions.MySamokatApiGWPreconditions
import ru.samokat.mysamokat.tests.helpers.actions.EmployeeActions
import ru.samokat.mysamokat.tests.helpers.actions.MySamokatApiGWActions

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Tags(Tag("msmkt-apigateway"), Tag("Auth"))
class Auth {

    private lateinit var msmktPreconditions: MySamokatApiGWPreconditions

    @Autowired
    private lateinit var employeeActions: EmployeeActions

    @Autowired
    private lateinit var msmktActions: MySamokatApiGWActions

    private lateinit var msmktAssertion: MySamokatApiGWAssertions

    @Autowired
    private lateinit var commonPreconditions: CommonPreconditions

    private lateinit var commonAssertions: CommonAssertion

    @BeforeEach
    fun before() {
        msmktPreconditions = MySamokatApiGWPreconditions()
        msmktAssertion = MySamokatApiGWAssertions()
        commonAssertions = CommonAssertion()
        employeeActions.deleteProfile(Constants.mobile1)
    }

    @AfterEach
    fun release() {
        msmktAssertion.assertAll()
        commonAssertions.assertAll()
        employeeActions.deleteProfile(Constants.mobile1)
    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Auth by password: deliveryman")
    fun authByPasswordDeliveryman() {

        val profile = commonPreconditions.createProfileDeliveryman()

        val tokens =
            msmktActions.authProfilePassword(msmktPreconditions.fillAuthRequest(password = profile.generatedPassword!!))

        msmktAssertion.checkOauthTokensExists(tokens!!)
    }

    @Test
    @DisplayName("Auth by password: picker")
    fun authByPasswordPicker() {

        val profile = commonPreconditions.createProfilePicker()
        val authRequest = msmktPreconditions.fillAuthRequest(password = profile.generatedPassword!!)

        val tokens =
            msmktActions.authProfilePassword(authRequest)

        msmktAssertion.checkOauthTokensExists(tokens!!)
    }

    @Test
    @DisplayName("Auth by password: darkstore admin")
    fun authByPasswordDarkstoreAdmin() {

        val profile = commonPreconditions.createProfileDarkstoreAdmin()
        val authRequest = msmktPreconditions.fillAuthRequest(password = profile.generatedPassword!!)

        val tokens = msmktActions.authProfilePassword(authRequest)

        msmktAssertion.checkOauthTokensExists(tokens!!)
    }

    @Test
    @DisplayName("Auth by password: forbidden role")
    fun authByPasswordForbiddenRole() {

        val profile = commonPreconditions.createProfile(
            roles = listOf(ApiEnum(EmployeeRole.SUPERVISOR))
        )
        val authRequest = msmktPreconditions.fillAuthRequest(
            password = profile.generatedPassword!!)

        val tokensError = msmktActions.authProfilePasswordError(authRequest, HttpStatus.SC_FORBIDDEN)

        commonAssertions.checkErrorMessage(tokensError!!.code.toString(), "Forbidden")
        commonAssertions.checkErrorMessage(tokensError.message.toString(), "User is forbidden")
    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Auth by password: with invalid password")
    fun authByWrongPassword() {

        commonPreconditions.createProfileDeliveryman()
        val authRequest =  msmktPreconditions.fillAuthRequest(
            password = StringAndPhoneNumberGenerator.generateRandomString(6).toCharArray()
        )
        val tokensError = msmktActions.authProfilePasswordError(authRequest, HttpStatus.SC_UNAUTHORIZED)

        commonAssertions.checkErrorMessage(tokensError!!.code.toString(), "IncorrectCredentials")
        commonAssertions.checkErrorMessage(tokensError.message.toString(), "Given credentials are invalid")
    }

    @Test
    @DisplayName("Auth by password: user not exists")
    fun authByPasswordUserNotExists() {

        val authRequest = msmktPreconditions.fillAuthRequest(
            password = StringAndPhoneNumberGenerator.generateRandomString(6).toCharArray()
        )
        val tokensError = msmktActions.authProfilePasswordError(authRequest, HttpStatus.SC_UNAUTHORIZED)

        commonAssertions.checkErrorMessage(tokensError!!.code.toString(), "IncorrectCredentials")
        commonAssertions.checkErrorMessage(tokensError.message.toString(), "Given credentials are invalid")
    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Auth by password: user is blocked")
    fun authByPasswordUserIsBlocked() {

        val profile = commonPreconditions.createProfileDeliveryman()
        val authRequest = msmktPreconditions.fillAuthRequest(
            password = StringAndPhoneNumberGenerator.generateRandomString(6).toCharArray())
        employeeActions.deleteProfile(profile.profileId)

        val tokensError = msmktActions.authProfilePasswordError(authRequest, HttpStatus.SC_UNAUTHORIZED)
        commonAssertions.checkErrorMessage(tokensError!!.code.toString(), "IncorrectCredentials")
        commonAssertions.checkErrorMessage(tokensError.message.toString(), "Given credentials are invalid")
    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Auth by password: refresh token")
    fun authByPasswordTokenRefresh() {

        val profile = commonPreconditions.createProfileDeliveryman()

        val authRequest = msmktPreconditions.fillAuthRequest(password = profile.generatedPassword!!)
        val tokens = msmktActions.authProfilePassword(authRequest)

        val refresh = msmktActions.refreshToken(msmktPreconditions.fillRefreshTokenRequest(tokens!!.refreshToken!!))

        msmktAssertion.checkOauthTokensExists(refresh!!)
    }

    @Test
    @DisplayName("Auth by password: delete token")
    fun authByPasswordDeleteToken() {
        val profile = commonPreconditions.createProfileDeliveryman()
        val authRequest = msmktPreconditions.fillAuthRequest(password = profile.generatedPassword!!)
        val tokens = msmktActions.authProfilePassword(authRequest)!!

        msmktActions.deleteToken(tokens.accessToken, HttpStatus.SC_NO_CONTENT)
    }

    @Test
    @DisplayName("Auth by otp")
    fun authByOtp() {
        commonPreconditions.createProfileDeliveryman()
        val otpRequest = msmktPreconditions.fillOtpRequest()

        msmktActions.authProfileOtp(otpRequest)
        val otp = msmktActions.getOtp(Constants.mobile1)
        val authRequest = msmktPreconditions.fillAuthRequestWithOtp(otp = otp.toCharArray())

        val tokens = msmktActions.authProfilePassword(authRequest)

        msmktAssertion.checkOauthTokensExists(tokens!!)


    }
}