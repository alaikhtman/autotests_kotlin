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
class AuthByPassword {

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
    @DisplayName("Auth by password: darkstore_admin")
    fun authByPasswordDarkstoreAdmin() {

        val profile = commonPreconditions.createProfileDarkstoreAdmin()
        staffApiGWPreconditions.fillAuthRequest(password = profile.generatedPassword!!)

        val tokens =
            apiGWActions.authProfilePassword(staffApiGWPreconditions.oAuthTokenRequest())

        apiGWAssertions.checkOauthTokensExists(tokens!!)
    }


    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Auth by password: invalid role")
    fun authByPasswordInvalidRole() {
        val profile = commonPreconditions.createProfileDeliveryman()
        staffApiGWPreconditions.fillAuthRequest(password = profile.generatedPassword!!)

        val tokensError =
            apiGWActions.authProfilePasswordError(
                staffApiGWPreconditions.oAuthTokenRequest(),
                HttpStatus.SC_FORBIDDEN
            )

        commonAssertions.checkErrorMessage(tokensError!!.code.toString(), "Forbidden")
        commonAssertions.checkErrorMessage(tokensError.message.toString(), "User is forbidden")
    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Auth by password: invalid password")
    fun authByPasswordInvalidPassword() {

        commonPreconditions.createProfile(
            roles = listOf(ApiEnum(EmployeeRole.SUPERVISOR))
        )
        staffApiGWPreconditions.fillAuthRequest(
            password = StringAndPhoneNumberGenerator.generateRandomString(6).toCharArray()
        )

        val tokensError =
            apiGWActions.authProfilePasswordError(
                staffApiGWPreconditions.oAuthTokenRequest(),
                HttpStatus.SC_UNAUTHORIZED
            )

        commonAssertions.checkErrorMessage(tokensError!!.code.toString(), "IncorrectCredentials")
        commonAssertions.checkErrorMessage(tokensError.message.toString(), "Given credentials are invalid")
    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Auth by password: user is not existed")
    fun authByPasswordNotExistedUser() {
        staffApiGWPreconditions.fillAuthRequest(
            mobile = StringAndPhoneNumberGenerator.generateRandomPhoneNumber(),
            password = StringAndPhoneNumberGenerator.generateRandomString(6).toCharArray()
        )

        val tokensError =
            apiGWActions.authProfilePasswordError(
                staffApiGWPreconditions.oAuthTokenRequest(),
                HttpStatus.SC_UNAUTHORIZED
            )

        commonAssertions.checkErrorMessage(tokensError!!.code.toString(), "IncorrectCredentials")
        commonAssertions.checkErrorMessage(tokensError.message.toString(), "Given credentials are invalid")
    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Auth by password: user is disabled")
    fun authByPasswordDisabledUser() {
        val profile = commonPreconditions.createProfile(
            roles = listOf(ApiEnum(EmployeeRole.SUPERVISOR))
        )

        profileActions.deleteProfile(profile.profileId)

        staffApiGWPreconditions.fillAuthRequest(
            password = profile.generatedPassword!!
        )

        val tokensError =
            apiGWActions.authProfilePasswordError(
                staffApiGWPreconditions.oAuthTokenRequest(),
                HttpStatus.SC_UNAUTHORIZED
            )

        commonAssertions.checkErrorMessage(tokensError!!.code.toString(), "IncorrectCredentials")
        commonAssertions.checkErrorMessage(tokensError.message.toString(), "Given credentials are invalid")
    }

}