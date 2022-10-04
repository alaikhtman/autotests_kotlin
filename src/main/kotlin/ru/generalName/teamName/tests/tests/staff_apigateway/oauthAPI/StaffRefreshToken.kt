package ru.samokat.mysamokat.tests.tests.staff_apigateway.oauthAPI

import org.apache.http.HttpStatus
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.samokat.my.domain.employee.EmployeeRole
import ru.samokat.my.rest.api.enum.ApiEnum
import ru.samokat.mysamokat.tests.checkers.CommonAssertion
import ru.samokat.mysamokat.tests.checkers.StaffApiGWAssertions
import ru.samokat.mysamokat.tests.dataproviders.Constants
import ru.samokat.mysamokat.tests.dataproviders.preconditions.CommonPreconditions
import ru.samokat.mysamokat.tests.dataproviders.preconditions.StaffApiGWPreconditions
import ru.samokat.mysamokat.tests.helpers.actions.EmployeeActions
import ru.samokat.mysamokat.tests.helpers.actions.StaffApiGWActions


@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Tags(Tag("staff_apigateway"), Tag("Auth"))
class StaffRefreshToken {
    private lateinit var staffApiGWPreconditions: StaffApiGWPreconditions

    private lateinit var staffApiGWAssertions: StaffApiGWAssertions
    private lateinit var commonAssertions: CommonAssertion

    @Autowired
    private lateinit var staffApiGWActions: StaffApiGWActions

    @Autowired
    private lateinit var employeeActions: EmployeeActions

    @Autowired
    private lateinit var commonPreconditions: CommonPreconditions

    @BeforeEach
    fun before() {
        staffApiGWPreconditions = StaffApiGWPreconditions()
        staffApiGWAssertions = StaffApiGWAssertions()
        commonAssertions = CommonAssertion()
        employeeActions.deleteProfile(Constants.mobile1)

    }

    @AfterEach
    fun release() {
        staffApiGWAssertions.assertAll()
        commonAssertions.assertAll()
        employeeActions.deleteProfile(Constants.mobile1)

    }


    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Auth by password: refresh token by darkstoreAdmin")
    fun authByPasswordRefreshTokenDarkstoreAdmin() {
        val profile = commonPreconditions.createProfileDarkstoreAdmin()
        staffApiGWPreconditions.fillAuthRequest(password = profile.generatedPassword!!)
        val tokens =
            staffApiGWActions.authProfilePassword(staffApiGWPreconditions.oAuthTokenRequest())

        staffApiGWPreconditions.fillRefreshTokenRequest(tokens!!.refreshToken)
        val refreshToken = staffApiGWActions.refreshToken(staffApiGWPreconditions.refreshAccessTokenRequest())

        staffApiGWAssertions.checkOauthTokensExists(refreshToken!!)

    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Auth by password: refresh token by goods_manager")
    fun authByPasswordRefreshTokenGoodsManager() {
        val profile = commonPreconditions.createProfileGoodsManager()
        staffApiGWPreconditions.fillAuthRequest(password = profile.generatedPassword!!)
        val tokens =
            staffApiGWActions.authProfilePassword(staffApiGWPreconditions.oAuthTokenRequest())

        staffApiGWPreconditions.fillRefreshTokenRequest(tokens!!.refreshToken)
        val refreshToken = staffApiGWActions.refreshToken(staffApiGWPreconditions.refreshAccessTokenRequest())

        staffApiGWAssertions.checkOauthTokensExists(refreshToken!!)
    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Auth by password: refresh token by coordinator")
    fun authByPasswordRefreshTokenCoordinator() {

        val profile = commonPreconditions.createProfile(
            roles = listOf(ApiEnum(EmployeeRole.COORDINATOR)),
            supervisedDarkstores = Constants.supervisedDarkstores
        )
        staffApiGWPreconditions.fillAuthRequest(password = profile.generatedPassword!!)
        val tokens =
            staffApiGWActions.authProfilePassword(staffApiGWPreconditions.oAuthTokenRequest())

        staffApiGWPreconditions.fillRefreshTokenRequest(tokens!!.refreshToken)
        val refreshToken = staffApiGWActions.refreshToken(staffApiGWPreconditions.refreshAccessTokenRequest())

        staffApiGWAssertions.checkOauthTokensExists(refreshToken!!)
    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Auth by password: refresh token by supervisor")
    fun authByPasswordRefreshTokenSupervisor() {
        val profile = commonPreconditions.createProfile(
            roles = listOf(ApiEnum(EmployeeRole.SUPERVISOR)))
        staffApiGWPreconditions.fillAuthRequest(password = profile.generatedPassword!!)
        val tokens =
            staffApiGWActions.authProfilePassword(staffApiGWPreconditions.oAuthTokenRequest())

        staffApiGWPreconditions.fillRefreshTokenRequest(tokens!!.refreshToken)
        val refreshToken = staffApiGWActions.refreshToken(staffApiGWPreconditions.refreshAccessTokenRequest())

        staffApiGWAssertions.checkOauthTokensExists(refreshToken!!)
    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Auth by password: refresh token is impossible after delete token ")
    fun authByPasswordRefreshTokenAfterDeletedToken() {

        val profile = commonPreconditions.createProfileDarkstoreAdmin()
        staffApiGWPreconditions.fillAuthRequest(password = profile.generatedPassword!!)
        val tokens =
            staffApiGWActions.authProfilePassword(staffApiGWPreconditions.oAuthTokenRequest())

        staffApiGWActions.deleteToken(tokens!!.accessToken, HttpStatus.SC_NO_CONTENT)

        staffApiGWPreconditions.fillRefreshTokenRequest(tokens.refreshToken)
        staffApiGWActions.refreshTokenError(
            staffApiGWPreconditions.refreshAccessTokenRequest(),
            HttpStatus.SC_UNAUTHORIZED
        )
    }


}