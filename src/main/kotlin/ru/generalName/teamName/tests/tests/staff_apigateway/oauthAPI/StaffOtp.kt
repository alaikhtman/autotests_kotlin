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
class StaffOtp {
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
        employeeActions.deleteProfile(Constants.mobile2)
        employeeActions.deleteProfile(Constants.mobile3)
        employeeActions.deleteProfile(Constants.mobile4)
        employeeActions.deleteProfile(Constants.mobile5)

    }

    @AfterEach
    fun release() {
        staffApiGWAssertions.assertAll()
        commonAssertions.assertAll()
        employeeActions.deleteProfile(Constants.mobile1)
        employeeActions.deleteProfile(Constants.mobile2)
        employeeActions.deleteProfile(Constants.mobile3)
        employeeActions.deleteProfile(Constants.mobile4)
        employeeActions.deleteProfile(Constants.mobile5)

    }


    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Auth by password: otp by darkstoreAdmin")
    fun authByOtpDarkstoreAdmin() {
        commonPreconditions.createProfileDarkstoreAdmin()
        staffApiGWPreconditions
            .fillOtpRequest()
        staffApiGWActions.authProfileOtp(staffApiGWPreconditions.sendOtpRequest())

        val otp = staffApiGWActions.getOtp(Constants.mobile1).toCharArray()

        staffApiGWPreconditions.fillAuthRequestWithOtp(otp = otp)

        val tokens = staffApiGWActions.authProfilePassword(staffApiGWPreconditions.oAuthTokenRequest())

        staffApiGWAssertions.checkOauthTokensExists(tokens!!)


    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Auth by password: otp by goods_manager")
    fun authByOtpGoodsManager() {
        commonPreconditions.createProfileGoodsManager(
            mobile = Constants.mobile2
        )
        staffApiGWPreconditions
            .fillOtpRequest(Constants.mobile2.asStringWithoutPlus())
        staffApiGWActions.authProfileOtp(staffApiGWPreconditions.sendOtpRequest())

        val otp = staffApiGWActions.getOtp(Constants.mobile2).toCharArray()

        staffApiGWPreconditions.fillAuthRequestWithOtp(mobile = Constants.mobile2.asStringWithoutPlus(), otp = otp)

        val tokens = staffApiGWActions.authProfilePassword(staffApiGWPreconditions.oAuthTokenRequest())

        staffApiGWAssertions.checkOauthTokensExists(tokens!!)
    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Auth by password: otp by coordinator")
    fun authByOtpCoordinator() {
        commonPreconditions.createProfile(
            roles = listOf(ApiEnum(EmployeeRole.COORDINATOR)),
            supervisedDarkstores = Constants.supervisedDarkstores,
            mobile = Constants.mobile3
        )
        staffApiGWPreconditions
            .fillOtpRequest(Constants.mobile3.asStringWithoutPlus())

        staffApiGWActions.authProfileOtp(staffApiGWPreconditions.sendOtpRequest())

        val otp = staffApiGWActions.getOtp(Constants.mobile3).toCharArray()

        staffApiGWPreconditions.fillAuthRequestWithOtp(mobile = Constants.mobile3.asStringWithoutPlus(), otp = otp)

        val tokens = staffApiGWActions.authProfilePassword(staffApiGWPreconditions.oAuthTokenRequest())

        staffApiGWAssertions.checkOauthTokensExists(tokens!!)
    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Auth by password: otp by supervisor")
    fun authByOtpSupervisor() {
        commonPreconditions.createProfile(
            roles = listOf(ApiEnum(EmployeeRole.SUPERVISOR)),
            mobile = Constants.mobile4
        )
        staffApiGWPreconditions
            .fillOtpRequest(Constants.mobile4.asStringWithoutPlus())
        staffApiGWActions.authProfileOtp(staffApiGWPreconditions.sendOtpRequest())

        val otp = staffApiGWActions.getOtp(Constants.mobile4).toCharArray()

        staffApiGWPreconditions.fillAuthRequestWithOtp(mobile = Constants.mobile4.asStringWithoutPlus(), otp = otp)

        val tokens = staffApiGWActions.authProfilePassword(staffApiGWPreconditions.oAuthTokenRequest())

        staffApiGWAssertions.checkOauthTokensExists(tokens!!)

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
        staffApiGWActions.authProfileOtp(staffApiGWPreconditions.sendOtpRequest())

        val otp = staffApiGWActions.getOtp(Constants.mobile5).toCharArray()

        staffApiGWPreconditions.fillAuthRequestWithOtp(mobile = Constants.mobile5.asStringWithoutPlus(), otp = otp)
        val tokensError = staffApiGWActions.authProfilePasswordError(
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
        staffApiGWActions.authProfileOtp(staffApiGWPreconditions.sendOtpRequest())

        staffApiGWPreconditions.fillAuthRequestWithOtp(otp = "0000".toCharArray())

       val tokensError = staffApiGWActions.authProfilePasswordError(
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
        staffApiGWActions.authProfileOtp(staffApiGWPreconditions.sendOtpRequest())

        val otp = staffApiGWActions.getOtp(Constants.mobile1).toCharArray()

        staffApiGWPreconditions.fillAuthRequestWithOtp(otp = otp)
        employeeActions.deleteProfile(profile.profileId)

      val tokensError =  staffApiGWActions.authProfilePasswordError(
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
      val tokensError =  staffApiGWActions.authProfilePasswordError(
            staffApiGWPreconditions.oAuthTokenRequest(),
            HttpStatus.SC_UNAUTHORIZED
        )

        commonAssertions.checkErrorMessage(tokensError!!.code.toString(), "IncorrectCredentials")
        commonAssertions.checkErrorMessage(tokensError.message.toString(), "Given credentials are invalid")

    }
}
