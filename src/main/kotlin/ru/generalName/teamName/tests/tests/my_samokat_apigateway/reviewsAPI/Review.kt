package ru.samokat.mysamokat.tests.tests.my_samokat_apigateway.reviewsAPI

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

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Tag("msmkt-apigateway")
class Review {

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
    @DisplayName("Get review pending")
    fun getReviewPending() {

        val profile = commonPreconditions.createProfileDeliveryman()
        val shiftId = commonPreconditions.startAndStopShift(profile.profileId).shiftId
        val authRequest = msmktPreconditions.fillAuthRequest(password = profile.generatedPassword!!)

        val token = msmktActions.authProfilePassword(authRequest)!!.accessToken
        val reviews = msmktActions.getReviewPending(token)

        msmktAssertion.checkShiftsInReviewsPendingList(shiftId, reviews)
    }

    @Test
    @DisplayName("Delete review pending")
    fun deleteReviewPending(){

        val profile = commonPreconditions.createProfileDeliveryman()
        val shiftId = commonPreconditions.startAndStopShift(profile.profileId).shiftId
        val authRequest = msmktPreconditions.fillAuthRequest(password = profile.generatedPassword!!)

        val token = msmktActions.authProfilePassword(authRequest)!!.accessToken
        val reviews1 = msmktActions.getReviewPending(token)
        msmktActions.deleteReviewPending(token, shiftId)
        val reviews2 = msmktActions.getReviewPending(token)

        msmktAssertion.checkShiftsInReviewsPendingList(shiftId, reviews1)
            .checkReviewsPendingListEmpty(reviews2)
    }

    @Test
    @DisplayName("Rate shift")
    fun rateShift(){

        val profile = commonPreconditions.createProfileDeliveryman()
        val shiftId = commonPreconditions.startAndStopShift(profile.profileId).shiftId
        val authRequest = msmktPreconditions.fillAuthRequest(password = profile.generatedPassword!!)
        val submitReviewRequest = msmktPreconditions.fillSubmitReviewRequest(3)

        val token = msmktActions.authProfilePassword(authRequest)!!.accessToken
        msmktActions.submitReview(token, shiftId, submitReviewRequest)
        val workedOutShift = msmktActions.getWorkedOutShiftById(token, shiftId)
        val reviews = msmktActions.getReviewPending(token)

        msmktAssertion
            .checkReviewsPendingListEmpty(reviews)
            .checkShiftReview(workedOutShift, submitReviewRequest)
    }

}