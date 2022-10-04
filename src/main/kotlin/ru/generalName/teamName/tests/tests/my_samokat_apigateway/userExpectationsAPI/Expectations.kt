package ru.samokat.mysamokat.tests.tests.my_samokat_apigateway.userExpectationsAPI

import org.apache.http.HttpStatus
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.samokat.employeeprofiles.api.common.domain.EmployeeVehicleType
import ru.samokat.employeeprofiles.api.common.domain.Vehicle
import ru.samokat.my.rest.api.enum.ApiEnum
import ru.samokat.mysamokat.tests.checkers.CommonAssertion
import ru.samokat.mysamokat.tests.checkers.MySamokatApiGWAssertions
import ru.samokat.mysamokat.tests.dataproviders.Constants
import ru.samokat.mysamokat.tests.dataproviders.preconditions.CommonPreconditions
import ru.samokat.mysamokat.tests.dataproviders.preconditions.MySamokatApiGWPreconditions
import ru.samokat.mysamokat.tests.helpers.actions.EmployeeActions
import ru.samokat.mysamokat.tests.helpers.actions.MySamokatApiGWActions
import java.time.Duration

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Tag("msmkt-apigateway")
class Expectations {

    private lateinit var msmktPreconditions: MySamokatApiGWPreconditions

    private lateinit var commonAssertion: CommonAssertion

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
        commonAssertion = CommonAssertion()
        employeeActions.deleteProfile(Constants.mobile1)
    }

    @AfterEach
    fun release() {
        msmktAssertion.assertAll()
        employeeActions.deleteProfile(Constants.mobile1)
    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Expectations: add expectations")
    fun addExpectations() {

        val profile = commonPreconditions.createProfileDeliveryman()
        val authRequest = msmktPreconditions.fillAuthRequest(password = profile.generatedPassword!!)
        val storeExpectationsRequest = msmktPreconditions.fillPutExpectationsRequest(3, Duration.ofHours(5))

        val token = msmktActions.authProfilePassword(authRequest)!!.accessToken
        msmktActions.putExpectations(token, storeExpectationsRequest)
        val userExpectations = msmktActions.getExpectations(token)

        msmktAssertion.checkExpectations(userExpectations, storeExpectationsRequest)
    }

    @Test
    @DisplayName("Expectations: update expectations")
    fun updateExpectations() {

        val profile = commonPreconditions.createProfileDeliveryman()
        val authRequest = msmktPreconditions.fillAuthRequest(password = profile.generatedPassword!!)
        val storeExpectationsRequest1 = msmktPreconditions.fillPutExpectationsRequest(3, Duration.ofHours(5))
        val storeExpectationsRequest2 = msmktPreconditions.fillPutExpectationsRequest(4, Duration.ofHours(7))

        val token = msmktActions.authProfilePassword(authRequest)!!.accessToken
        msmktActions.putExpectations(token, storeExpectationsRequest1)
        msmktActions.putExpectations(token, storeExpectationsRequest2)
        val userExpectations = msmktActions.getExpectations(token)

        msmktAssertion.checkExpectations(userExpectations, storeExpectationsRequest2)
    }

    @Test
    @DisplayName("Expectations: set expectations to maх")
    fun updateExpectationsToMax() {

        val profile = commonPreconditions.createProfileDeliveryman()
        val authRequest = msmktPreconditions.fillAuthRequest(password = profile.generatedPassword!!)
        val storeExpectationsRequest = msmktPreconditions.fillPutExpectationsRequest(7, Duration.ofHours(16))

        val token = msmktActions.authProfilePassword(authRequest)!!.accessToken
        msmktActions.putExpectations(token, storeExpectationsRequest)
        val userExpectations = msmktActions.getExpectations(token)

        msmktAssertion.checkExpectations(userExpectations, storeExpectationsRequest)
    }

    @Test
    @DisplayName("Expectations: set expectations to min")
    fun updateExpectationsToMin() {

        val profile = commonPreconditions.createProfileDeliveryman()
        val authRequest = msmktPreconditions.fillAuthRequest(password = profile.generatedPassword!!)
        val storeExpectationsRequest = msmktPreconditions.fillPutExpectationsRequest(1, Duration.ofHours(2))

        val token = msmktActions.authProfilePassword(authRequest)!!.accessToken
        msmktActions.putExpectations(token, storeExpectationsRequest)
        val userExpectations = msmktActions.getExpectations(token)

        msmktAssertion.checkExpectations(userExpectations, storeExpectationsRequest)
    }

    @Test
    @DisplayName("Expectations: get empty expectations")
    fun getEmptyExpectations() {

        val profile = commonPreconditions.createProfileDeliveryman()
        val authRequest = msmktPreconditions.fillAuthRequest(password = profile.generatedPassword!!)

        val token = msmktActions.authProfilePassword(authRequest)!!.accessToken
        msmktActions.getExpectationsError(token, HttpStatus.SC_NOT_FOUND)
    }

    @Test
    @DisplayName("Expectations: add expectations (less than min)")
    fun addExpectationsLessThanMin() {

        val profile = commonPreconditions.createProfileDeliveryman()
        val authRequest = msmktPreconditions.fillAuthRequest(password = profile.generatedPassword!!)
        val storeExpectationsRequest = msmktPreconditions.fillPutExpectationsRequest(0, Duration.ofHours(1))

        val token = msmktActions.authProfilePassword(authRequest)!!.accessToken
        val error = msmktActions.putExpectationsError(token, storeExpectationsRequest, HttpStatus.SC_BAD_REQUEST)

        commonAssertion.checkErrorMessage(error!!.code.toString(), "InvalidRequest")
        commonAssertion.checkErrorMessage(
            error!!.message.toString(),
            "Working day duration must be lower or equal than 16 hours"
        )
        commonAssertion.checkErrorMessage(error!!.parameter.toString(), "workingDayDuration")
    }

    @Test
    @DisplayName("Expectations: add expectations (more than max)")
    fun addExpectationsMoreThanMax() {

        val profile = commonPreconditions.createProfileDeliveryman()
        val authRequest = msmktPreconditions.fillAuthRequest(password = profile.generatedPassword!!)
        val storeExpectationsRequest = msmktPreconditions.fillPutExpectationsRequest(10, Duration.ofHours(24))

        val token = msmktActions.authProfilePassword(authRequest)!!.accessToken
        val error = msmktActions.putExpectationsError(token, storeExpectationsRequest, HttpStatus.SC_BAD_REQUEST)

        commonAssertion.checkErrorMessage(error!!.code.toString(), "InvalidRequest")
        commonAssertion.checkErrorMessage(
            error!!.message.toString(),
            "Working day duration must be lower or equal than 16 hours"
        )
        commonAssertion.checkErrorMessage(error!!.parameter.toString(), "workingDayDuration")
    }

    @Test
    @DisplayName("Expectations: get expectations by disabled profile")
    fun addExpectationsByDisabledProfile() {

        val profile = commonPreconditions.createProfileDeliveryman()
        val authRequest = msmktPreconditions.fillAuthRequest(password = profile.generatedPassword!!)
        val storeExpectationsRequest = msmktPreconditions.fillPutExpectationsRequest(5, Duration.ofHours(5))

        val token = msmktActions.authProfilePassword(authRequest)!!.accessToken
        msmktActions.putExpectations(token, storeExpectationsRequest)
        employeeActions.deleteProfile(profile.profileId)
        msmktActions.getExpectationsError(token, HttpStatus.SC_NOT_FOUND)
    }
}