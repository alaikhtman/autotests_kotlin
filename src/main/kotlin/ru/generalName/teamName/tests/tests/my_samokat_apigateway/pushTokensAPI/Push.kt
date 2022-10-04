package ru.samokat.mysamokat.tests.tests.my_samokat_apigateway.pushTokensAPI

import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.samokat.employeeprofiles.api.common.domain.EmployeeVehicleType
import ru.samokat.employeeprofiles.api.common.domain.Vehicle
import ru.samokat.my.rest.api.enum.ApiEnum
import ru.samokat.mysamokat.tests.checkers.MySamokatApiGWAssertions
import ru.samokat.mysamokat.tests.dataproviders.Constants
import ru.samokat.mysamokat.tests.dataproviders.preconditions.CommonPreconditions
import ru.samokat.mysamokat.tests.dataproviders.preconditions.MySamokatApiGWPreconditions
import ru.samokat.mysamokat.tests.helpers.actions.EmployeeActions
import ru.samokat.mysamokat.tests.helpers.actions.MySamokatApiGWActions
import java.util.*

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Tag("msmkt-apigateway")
class Push {
    private lateinit var msmktPreconditions: MySamokatApiGWPreconditions

    @Autowired
    private lateinit var employeeActions: EmployeeActions

    @Autowired
    private lateinit var msmktActions: MySamokatApiGWActions

    private lateinit var msmktAssertion: MySamokatApiGWAssertions

    @Autowired
    private lateinit var commonPreconditions: CommonPreconditions


    @BeforeEach
    fun before() {
        msmktPreconditions = MySamokatApiGWPreconditions()
        msmktAssertion = MySamokatApiGWAssertions()
        employeeActions.deleteProfile(Constants.mobile1)
    }

    @AfterEach
    fun release() {
        msmktAssertion.assertAll()
        employeeActions.deleteProfile(Constants.mobile1)
    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Push token")
    fun registerAndDeletePushTokenTest() {

        val profile = commonPreconditions.createProfileDeliveryman()
        val pushRequest = msmktPreconditions.fillPushTokenRegisterRequest()
        val authRequest = msmktPreconditions.fillAuthRequest(password = profile.generatedPassword!!)

        val token = msmktActions.authProfilePassword(authRequest)!!.accessToken

        msmktActions.registerPushToken(token, pushRequest)

        val pushTokenFromDB = msmktActions.getPushTokenFromDB(UUID.fromString(pushRequest.pushToken))

        msmktActions.deletePushToken(token, pushRequest.pushToken)

        val tokenExistance = msmktActions.checkTokenExists(UUID.fromString(pushRequest.pushToken))

        msmktAssertion
            .checkPushToken(pushTokenFromDB, profile.profileId, pushRequest.pushToken)
            .checkPushTokenNotExists(tokenExistance)
    }
}