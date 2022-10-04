package ru.samokat.mysamokat.tests.tests.my_samokat_apigateway.UsersAPI

import org.apache.http.HttpStatus
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.samokat.employeeprofiles.api.common.domain.EmployeeVehicleType
import ru.samokat.employeeprofiles.api.common.domain.Vehicle
import ru.samokat.my.domain.employee.EmployeeName
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
@Tag("msmkt-apigateway")
class ProfileData {

    private lateinit var msmktPreconditions: MySamokatApiGWPreconditions


    @Autowired
    private lateinit var employeeActions: EmployeeActions

    @Autowired
    private lateinit var msmktActions: MySamokatApiGWActions

    private lateinit var msmktAssertion: MySamokatApiGWAssertions

    private lateinit var commonAssertion: CommonAssertion

    @Autowired
    private lateinit var commonPreconditions: CommonPreconditions


    @BeforeEach
    fun before() {
        msmktPreconditions = MySamokatApiGWPreconditions()
        msmktAssertion = MySamokatApiGWAssertions()
        commonAssertion = CommonAssertion()
        employeeActions.deleteProfile(Constants.mobile1)
    }

    @AfterEach
    fun release() {
        msmktAssertion.assertAll()
        commonAssertion.assertAll()
        employeeActions.deleteProfile(Constants.mobile1)
    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Get profile data: deliveryman")
    fun getProfileDataDeliveryman() {

        val name = EmployeeName(
            StringAndPhoneNumberGenerator.generateRandomString(10),
            StringAndPhoneNumberGenerator.generateRandomString(10),
            StringAndPhoneNumberGenerator.generateRandomString(10)
        )
        val profile = commonPreconditions.createProfileDeliveryman(name = name)
        val authToken =
            msmktActions.authProfilePassword(msmktPreconditions.fillAuthRequest(password = profile.generatedPassword!!))!!.accessToken

        val profileData = msmktActions.getProfileData(authToken)!!
        val messageFirstLogin = msmktActions.getFirstLoginEventFromKafka(profile.profileId)!!

        msmktAssertion.checkUserProfileWithName(profileData, profile.profileId, name = name)
        msmktAssertion.checkFirstLoginEventMessage(messageFirstLogin)
    }

    @Test
    @DisplayName("Get profile data: picker")
    fun getProfileDataPicker() {

        val profile = commonPreconditions.createProfilePicker()
        val authToken =
            msmktActions.authProfilePassword(msmktPreconditions.fillAuthRequest(password = profile.generatedPassword!!))!!.accessToken

        val profileData = msmktActions.getProfileData(authToken)!!

        msmktAssertion.checkUserProfile(profileData, profile.profileId, roles = listOf(ApiEnum(EmployeeRole.PICKER)))
    }

    @Test
    @DisplayName("Get profile data: darkstore admin")
    fun getProfileDataDarkstoreAdmin() {

        val profile = commonPreconditions.createProfileDarkstoreAdmin()
        val authToken =
            msmktActions.authProfilePassword(msmktPreconditions.fillAuthRequest(password = profile.generatedPassword!!))!!.accessToken

        val profileData = msmktActions.getProfileData(authToken)!!

        msmktAssertion.checkUserProfile(profileData, profile.profileId, roles = listOf(ApiEnum(EmployeeRole.DARKSTORE_ADMIN)))
    }

    @Test
    @DisplayName("Get profile data: deliveryman-picker")
    fun getProfileDataDeliverymanPicker() {

        val profile = commonPreconditions.createProfileDeliverymanPicker()
        val authToken =
            msmktActions.authProfilePassword(msmktPreconditions.fillAuthRequest(password = profile.generatedPassword!!))!!.accessToken

        val profileData = msmktActions.getProfileData(authToken)!!

        msmktAssertion.checkUserProfile(profileData, profile.profileId, roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN), ApiEnum(EmployeeRole.PICKER)))
    }

    @Test
    @DisplayName("Get profile data: profile disabled")
    fun getProfileDataDisabled() {

        val profile = commonPreconditions.createProfileDeliveryman()
        val authToken =
            msmktActions.authProfilePassword(msmktPreconditions.fillAuthRequest(password = profile.generatedPassword!!))!!.accessToken
        employeeActions.deleteProfile(profile.profileId)

        val profileDataError = msmktActions.getProfileDataError(authToken, HttpStatus.SC_UNAUTHORIZED)!!

        commonAssertion.checkErrorMessage(profileDataError!!.code.toString(), "IncorrectJwt")
        commonAssertion.checkErrorMessage(profileDataError.message.toString(), "Failed to find user profile")
    }

    @Test
    @DisplayName("Get profile data: inactive darkstore")
    fun getProfileDataInactiveDarkstore() {

        val profile = commonPreconditions.createProfileDeliveryman(darkstoreId = Constants.inactiveDarkstore)
        val authToken =
            msmktActions.authProfilePassword(msmktPreconditions.fillAuthRequest(password = profile.generatedPassword!!))!!.accessToken

        val profileData = msmktActions.getProfileData(authToken)!!

        msmktAssertion.checkUserProfile(profileData, profile.profileId, darkstoreId = Constants.inactiveDarkstore)
    }

    @Test
    @DisplayName("Get profile data: with refreshed token")
    fun getProfileDataWithRefreshedToken() {

        val profile = commonPreconditions.createProfileDeliveryman()
        val authToken =
            msmktActions.authProfilePassword(msmktPreconditions.fillAuthRequest(password = profile.generatedPassword!!))!!
        val refresh = msmktActions.refreshToken(msmktPreconditions.fillRefreshTokenRequest(authToken.refreshToken!!))!!


        val profileData = msmktActions.getProfileData(refresh.accessToken)!!

        msmktAssertion.checkUserProfile(profileData, profile.profileId)
    }

    @Test
    @DisplayName("Get profile data: check first login is not empty for not new user")
    fun checkFirstLoginIsNotEmptyForNotNewUser() {

        val name = EmployeeName(
            StringAndPhoneNumberGenerator.generateRandomString(10),
            StringAndPhoneNumberGenerator.generateRandomString(10),
            StringAndPhoneNumberGenerator.generateRandomString(10)
        )
        val profile = commonPreconditions.createProfileDeliveryman(name = name)
        val authToken =
            msmktActions.authProfilePassword(msmktPreconditions.fillAuthRequest(password = profile.generatedPassword!!))!!.accessToken

        val profileDataFirst = msmktActions.getProfileData(authToken)!!
        val profileDataSecond = msmktActions.getProfileData(authToken)!!

        msmktAssertion.checkUserProfileWithName(profileDataFirst, profile.profileId, name = name)
        msmktAssertion.checkUserProfileFirstLoginIsNotEmpty(profileDataSecond)
    }
}


