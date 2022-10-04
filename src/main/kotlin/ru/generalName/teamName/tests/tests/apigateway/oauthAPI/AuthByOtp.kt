package ru.generalName.teamName.tests.tests.apigateway.oauthAPI

import org.apache.http.HttpStatus
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.generalName.teamName.tests.checkers.ApiGWAssertions
import ru.generalName.teamName.tests.checkers.CommonAssertion
import ru.generalName.teamName.tests.dataproviders.Constants
import ru.generalName.teamName.tests.dataproviders.StringAndPhoneNumberGenerator
import ru.generalName.teamName.tests.dataproviders.preconditions.ApiGWPreconditions
import ru.generalName.teamName.tests.dataproviders.preconditions.CommonPreconditions
import ru.generalName.teamName.tests.helpers.actions.ApiGWActions
import ru.generalName.teamName.tests.helpers.actions.ProfileActions


@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Tags(Tag("apigateway"), Tag("Auth"))
class AuthByOtp {
    private lateinit var staffApiGWPreconditions: ApiGWPreconditions

    private lateinit var apiGWAssertions: ApiGWAssertions
    private lateinit var commonAssertions: CommonAssertion

    @Autowired
    private lateinit var apiGWActions: ApiGWActions

    @Autowired
    private lateinit var profileActions: ProfileActions

    @Autowired
    private lateinit var commonPreconditions: CommonPreconditions

    @BeforeEach
    fun before() {
        staffApiGWPreconditions = ApiGWPreconditions()
        apiGWAssertions = ApiGWAssertions()
        commonAssertions = CommonAssertion()
        profileActions.deleteProfile(Constants.mobile1)


    }

    @AfterEach
    fun release() {
        apiGWAssertions.assertAll()
        commonAssertions.assertAll()
        profileActions.deleteProfile(Constants.mobile1)


    }


    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Auth by password: otp by darkstoreAdmin")
    fun authByOtpDarkstoreAdmin() {
        commonPreconditions.createProfileDarkstoreAdmin()
        staffApiGWPreconditions
            .fillOtpRequest()
        apiGWActions.authProfileOtp(staffApiGWPreconditions.sendOtpRequest())

        val otp = apiGWActions.getOtp(Constants.mobile1).toCharArray()

        staffApiGWPreconditions.fillAuthRequestWithOtp(otp = otp)

        val tokens = apiGWActions.authProfilePassword(staffApiGWPreconditions.oAuthTokenRequest())

        apiGWAssertions.checkOauthTokensExists(tokens!!)


    }


    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Auth by password: otp by invalid role")
    fun authByPasswordOtpInvalidRole() {
        commonPreconditions.createProfileDeliveryman(
            mobile = Constants.mobile5
        )
        staffApiGWPreconditions
            .fillOtpRequest(Constants.mobile5.asStringWithoutPlus())
        apiGWActions.authProfileOtp(staffApiGWPreconditions.sendOtpRequest())

        val otp = apiGWActions.getOtp(Constants.mobile5).toCharArray()

        staffApiGWPreconditions.fillAuthRequestWithOtp(mobile = Constants.mobile5.asStringWithoutPlus(), otp = otp)
        val tokensError = apiGWActions.authProfilePasswordError(
            staffApiGWPreconditions.oAuthTokenRequest(),
            HttpStatus.SC_FORBIDDEN
        )
        commonAssertions.checkErrorMessage(tokensError!!.code.toString(), "Forbidden")
        commonAssertions.checkErrorMessage(tokensError.message.toString(), "User is forbidden")

    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Auth by password: otp by invalid code")
    fun authByPasswordOtpInvalidCode() {
        commonPreconditions.createProfileDarkstoreAdmin(
            roles = listOf(ApiEnum(EmployeeRole.DARKSTORE_ADMIN)),
            mobile = Constants.mobile1
        )
        staffApiGWPreconditions
            .fillOtpRequest()
        apiGWActions.authProfileOtp(staffApiGWPreconditions.sendOtpRequest())

        staffApiGWPreconditions.fillAuthRequestWithOtp(otp = "0000".toCharArray())

        val tokensError = apiGWActions.authProfilePasswordError(
            staffApiGWPreconditions.oAuthTokenRequest(),
            HttpStatus.SC_UNAUTHORIZED
        )
        commonAssertions.checkErrorMessage(tokensError!!.code.toString(), "IncorrectCredentials")
        commonAssertions.checkErrorMessage(tokensError.message.toString(), "Given credentials are invalid")

    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Auth by password: otp by disabled user")
    fun authByPasswordOtpDisabledUser() {
        val profile = commonPreconditions.createProfileDarkstoreAdmin(
            mobile = Constants.mobile1
        )

        staffApiGWPreconditions
            .fillOtpRequest()
        apiGWActions.authProfileOtp(staffApiGWPreconditions.sendOtpRequest())

        val otp = apiGWActions.getOtp(Constants.mobile1).toCharArray()

        staffApiGWPreconditions.fillAuthRequestWithOtp(otp = otp)
        profileActions.deleteProfile(profile.profileId)

        val tokensError = apiGWActions.authProfilePasswordError(
            staffApiGWPreconditions.oAuthTokenRequest(),
            HttpStatus.SC_UNAUTHORIZED
        )

        commonAssertions.checkErrorMessage(tokensError!!.code.toString(), "IncorrectCredentials")
        commonAssertions.checkErrorMessage(tokensError.message.toString(), "Given credentials are invalid")
    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Auth by password: otp by fake user")
    fun authByPasswordOtpNotExistedUser() {
        staffApiGWPreconditions.fillAuthRequestWithOtp(
            mobile = StringAndPhoneNumberGenerator.generateRandomPhoneNumber(),
            otp = "0000".toCharArray()
        )
        val tokensError = apiGWActions.authProfilePasswordError(
            staffApiGWPreconditions.oAuthTokenRequest(),
            HttpStatus.SC_UNAUTHORIZED
        )

        commonAssertions.checkErrorMessage(tokensError!!.code.toString(), "IncorrectCredentials")
        commonAssertions.checkErrorMessage(tokensError.message.toString(), "Given credentials are invalid")

    }
}
