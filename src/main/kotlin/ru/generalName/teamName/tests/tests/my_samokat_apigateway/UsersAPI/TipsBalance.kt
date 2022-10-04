package ru.samokat.mysamokat.tests.tests.my_samokat_apigateway.UsersAPI

import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.samokat.mysamokat.apigateway.api.user.gettips.UserTipsRegistrationStatus
import ru.samokat.mysamokat.tests.checkers.MySamokatApiGWAssertions
import ru.samokat.mysamokat.tests.dataproviders.Constants
import ru.samokat.mysamokat.tests.dataproviders.preconditions.MySamokatApiGWPreconditions
import ru.samokat.mysamokat.tests.helpers.actions.MySamokatApiGWActions

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Tag("msmkt-apigateway")
class TipsBalance {

    private lateinit var msmktPreconditions: MySamokatApiGWPreconditions

    @Autowired
    private lateinit var msmktActions: MySamokatApiGWActions

    private lateinit var msmktAssertion: MySamokatApiGWAssertions


    @BeforeEach
    fun before() {
        msmktPreconditions = MySamokatApiGWPreconditions()
        msmktAssertion = MySamokatApiGWAssertions()
    }

    @AfterEach
    fun release() {
        msmktAssertion.assertAll()
    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Tips: get positive balance")
    fun getPositiveTipsBalanceTest() {

        val authRequest = msmktPreconditions.fillAuthRequest(
            mobile = Constants.chachachayPositiveBalanceMobile,
            password = Constants.chachachayPositiveBalancePassword.toCharArray()
        )
        val token = msmktActions.authProfilePassword(authRequest)!!.accessToken

        val tips = msmktActions.getTipsData(token)!!

        msmktAssertion.checkTipsData(tips, 74800L, UserTipsRegistrationStatus.COMPLETED)
    }

    @Test
    @DisplayName("Tips: get negative balance")
    fun getNegativeTipsBalanceTest() {

        val authRequest = msmktPreconditions.fillAuthRequest(
            mobile = Constants.chachachayNegativeBalanceMobile,
            password = Constants.chachachayNegativeBalancePassword.toCharArray()
        )
        val token = msmktActions.authProfilePassword(authRequest)!!.accessToken

        val tips = msmktActions.getTipsData(token)!!

        msmktAssertion.checkTipsData(tips, -1000L, UserTipsRegistrationStatus.COMPLETED)
    }

    @Test
    @DisplayName("Tips: get max balance")
    fun getMaxTipsBalanceTest() {

        val authRequest = msmktPreconditions.fillAuthRequest(
            mobile = Constants.chachachayMaxBalanceMobile,
            password = Constants.chachachayMaxBalancePassword.toCharArray()
        )
        val token = msmktActions.authProfilePassword(authRequest)!!.accessToken

        val tips = msmktActions.getTipsData(token)!!

        msmktAssertion.checkTipsData(tips, 999999999L, UserTipsRegistrationStatus.COMPLETED)
    }

    @Test
    @DisplayName("Tips: get in progress balance")
    fun getInProgressTipsBalanceTest() {

        val authRequest = msmktPreconditions.fillAuthRequest(
            mobile = Constants.chachachayInProgressMobile,
            password = Constants.chachachayInProgressPassword.toCharArray()
        )
        val token = msmktActions.authProfilePassword(authRequest)!!.accessToken

        val tips = msmktActions.getTipsData(token)!!

        msmktAssertion.checkTipsDataWOBalance(tips, UserTipsRegistrationStatus.IN_PROGRESS)
    }

    @Test
    @DisplayName("Tips: get failed balance")
    fun getFailedTipsBalanceTest() {

        val authRequest = msmktPreconditions.fillAuthRequest(
            mobile = Constants.chachachayFailedToSyncMobile,
            password = Constants.chachachayFailedToSyncPassword.toCharArray()
        )
        val token = msmktActions.authProfilePassword(authRequest)!!.accessToken

        val tips = msmktActions.getTipsData(token)!!

        msmktAssertion.checkTipsDataWOBalance(tips, UserTipsRegistrationStatus.FAILED)
    }

}