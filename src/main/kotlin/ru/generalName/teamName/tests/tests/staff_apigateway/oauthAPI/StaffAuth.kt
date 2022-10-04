package ru.samokat.mysamokat.tests.tests.staff_apigateway.oauthAPI

import org.apache.http.HttpStatus
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.samokat.employeeprofiles.api.common.domain.EmployeeVehicleType
import ru.samokat.employeeprofiles.api.common.domain.Vehicle
import ru.samokat.my.domain.employee.EmployeeRole
import ru.samokat.my.rest.api.enum.ApiEnum
import ru.samokat.mysamokat.tests.checkers.CommonAssertion
import ru.samokat.mysamokat.tests.checkers.StaffApiGWAssertions
import ru.samokat.mysamokat.tests.dataproviders.Constants
import ru.samokat.mysamokat.tests.dataproviders.StringAndPhoneNumberGenerator
import ru.samokat.mysamokat.tests.dataproviders.preconditions.CommonPreconditions
import ru.samokat.mysamokat.tests.dataproviders.preconditions.StaffApiGWPreconditions
import ru.samokat.mysamokat.tests.helpers.actions.EmployeeActions
import ru.samokat.mysamokat.tests.helpers.actions.StaffApiGWActions

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Tags(Tag("staff_apigateway"), Tag("Auth"))
class StaffAuth {

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
    @DisplayName("Auth by password: darkstore_admin")
    fun authByPasswordDarkstoreAdmin() {

        val profile = commonPreconditions.createProfileDarkstoreAdmin()
        staffApiGWPreconditions.fillAuthRequest(password = profile.generatedPassword!!)

        val tokens =
            staffApiGWActions.authProfilePassword(staffApiGWPreconditions.oAuthTokenRequest())

        staffApiGWAssertions.checkOauthTokensExists(tokens!!)
    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Auth by password: goods_manager")
    fun authByPasswordGoodsManager() {

        val profile = commonPreconditions.createProfileGoodsManager()
        staffApiGWPreconditions.fillAuthRequest(password = profile.generatedPassword!!)

        val tokens =
            staffApiGWActions.authProfilePassword(staffApiGWPreconditions.oAuthTokenRequest())
        staffApiGWAssertions.checkOauthTokensExists(tokens!!)
    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Auth by password: coordinator")
    fun authByPasswordCoordinator() {

        val profile = commonPreconditions.createProfile(
            roles = listOf(ApiEnum(EmployeeRole.COORDINATOR)),
            supervisedDarkstores = Constants.supervisedDarkstores
        )
        staffApiGWPreconditions.fillAuthRequest(password = profile.generatedPassword!!)

        val tokens =
            staffApiGWActions.authProfilePassword(staffApiGWPreconditions.oAuthTokenRequest())

        staffApiGWAssertions.checkOauthTokensExists(tokens!!)
    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Auth by password: supervisor")
    fun authByPasswordSupervisor() {

        val profile = commonPreconditions.createProfile(
            roles = listOf(ApiEnum(EmployeeRole.SUPERVISOR))
        )
        staffApiGWPreconditions.fillAuthRequest(password = profile.generatedPassword!!)

        val tokens =
            staffApiGWActions.authProfilePassword(staffApiGWPreconditions.oAuthTokenRequest())

        staffApiGWAssertions.checkOauthTokensExists(tokens!!)
    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Auth by password: invalid role")
    fun authByPasswordInvalidRole() {
        val profile = commonPreconditions.createProfileDeliveryman()
        staffApiGWPreconditions.fillAuthRequest(password = profile.generatedPassword!!)

        val tokensError =
            staffApiGWActions.authProfilePasswordError(
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
            staffApiGWActions.authProfilePasswordError(
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
            staffApiGWActions.authProfilePasswordError(
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

        employeeActions.deleteProfile(profile.profileId)

        staffApiGWPreconditions.fillAuthRequest(
            password = profile.generatedPassword!!
        )

        val tokensError =
            staffApiGWActions.authProfilePasswordError(
                staffApiGWPreconditions.oAuthTokenRequest(),
                HttpStatus.SC_UNAUTHORIZED
            )

        commonAssertions.checkErrorMessage(tokensError!!.code.toString(), "IncorrectCredentials")
        commonAssertions.checkErrorMessage(tokensError.message.toString(), "Given credentials are invalid")
    }

}