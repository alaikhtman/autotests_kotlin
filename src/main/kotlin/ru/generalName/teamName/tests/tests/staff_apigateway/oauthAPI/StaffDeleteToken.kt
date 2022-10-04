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
class StaffDeleteToken {
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
    @DisplayName("Auth by password: delete token darkstore_admin")
    fun authByPasswordDeleteTokenDarkstoreAdmin() {
        val profile = commonPreconditions.createProfileDarkstoreAdmin()
        staffApiGWPreconditions.fillAuthRequest(password = profile.generatedPassword!!)

        val tokens =
            staffApiGWActions.authProfilePassword(staffApiGWPreconditions.oAuthTokenRequest())

        staffApiGWActions.deleteToken(tokens!!.accessToken, HttpStatus.SC_NO_CONTENT)
    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Auth by password: delete token goods_manager")
    fun authByPasswordDeleteTokenGoodsManager() {
        val profile = commonPreconditions.createProfileGoodsManager()
        staffApiGWPreconditions.fillAuthRequest(password = profile.generatedPassword!!)

        val tokens =
            staffApiGWActions.authProfilePassword(staffApiGWPreconditions.oAuthTokenRequest())

        staffApiGWActions.deleteToken(tokens!!.accessToken, HttpStatus.SC_NO_CONTENT)
    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Auth by password: delete token coordinator")
    fun authByPasswordDeleteTokenCoordinator() {
        val profile = commonPreconditions.createProfile(
            roles = listOf(ApiEnum(EmployeeRole.COORDINATOR)),
            supervisedDarkstores = Constants.supervisedDarkstores
        )
        staffApiGWPreconditions.fillAuthRequest(password = profile.generatedPassword!!)

        val tokens =
            staffApiGWActions.authProfilePassword(staffApiGWPreconditions.oAuthTokenRequest())

        staffApiGWActions.deleteToken(tokens!!.accessToken, HttpStatus.SC_NO_CONTENT)
    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Auth by password: delete token supervisor")
    fun authByPasswordDeleteTokenSupervisor() {
        val profile = commonPreconditions.createProfile(
            roles = listOf(ApiEnum(EmployeeRole.SUPERVISOR))
        )
        staffApiGWPreconditions.fillAuthRequest(password = profile.generatedPassword!!)

        val tokens =
            staffApiGWActions.authProfilePassword(staffApiGWPreconditions.oAuthTokenRequest())

        staffApiGWActions.deleteToken(tokens!!.accessToken, HttpStatus.SC_NO_CONTENT)
    }

}