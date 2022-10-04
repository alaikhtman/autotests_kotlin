package ru.samokat.mysamokat.tests.helpers.controllers.msmkt_apigw

import io.restassured.RestAssured
import io.restassured.builder.RequestSpecBuilder
import io.restassured.filter.log.LogDetail
import io.restassured.http.ContentType
import org.apache.http.HttpStatus
import org.springframework.stereotype.Service
import ru.samokat.mysamokat.apigateway.api.reviews.ReviewRequestsView
import ru.samokat.mysamokat.apigateway.api.shifts.getassignment.ShiftAssignmentView
import ru.samokat.mysamokat.apigateway.api.shifts.getlist.ShiftListView
import ru.samokat.mysamokat.apigateway.api.shifts.getworkedoutshift.WorkedOutShiftView
import ru.samokat.mysamokat.apigateway.api.shifts.reviews.SubmitShiftReviewRequest
import ru.samokat.mysamokat.apigateway.api.shifts.storeschedule.StoreScheduleRequest
import ru.samokat.mysamokat.tests.SuiteBase
import ru.samokat.mysamokat.tests.dataproviders.ErrorView
import ru.samokat.mysamokat.tests.endpoints.MsmktApiGatewayEndPoints
import ru.samokat.mysamokat.tests.endpoints.configuration.NetConfig
import java.util.*


@Service
class ShiftsApiController(
    restApigateway: NetConfig
) {

    private val requestSpecification = RequestSpecBuilder()
        .setBaseUri(restApigateway.url)
        .setPort(restApigateway.port)
        .setAccept(ContentType.JSON)
        .setContentType(ContentType.JSON)
        .log(LogDetail.ALL)
        .build()

    fun putShiftsSchedule(accessToken: String, requestBody: StoreScheduleRequest, from: String, to: String){

        val request = RestAssured.given(this.requestSpecification)
            .header("Authorization", "Bearer $accessToken")
            ?.queryParam("from", from)
            ?.queryParam("to", to)
            ?.body(requestBody)
            ?.`when`()
            ?.put(MsmktApiGatewayEndPoints.SHIFTS_SCHEDULE)
            ?.then()!!.statusCode(HttpStatus.SC_NO_CONTENT)
    }

    fun putShiftsScheduleError(accessToken: String, requestBody: StoreScheduleRequest, from: String, to: String, sc: Int): ErrorView? {

        val request = RestAssured.given(this.requestSpecification)
            .header("Authorization", "Bearer $accessToken")
            ?.queryParam("from", from)
            ?.queryParam("to", to)
            ?.body(requestBody)
            ?.`when`()
            ?.put(MsmktApiGatewayEndPoints.SHIFTS_SCHEDULE)
            ?.then()!!.statusCode(sc).extract().body().path<Any>("")

        return SuiteBase.jacksonObjectMapper.convertValue(request, ErrorView::class.java)
    }

    fun getShifts(accessToken: String, from: String, to: String): ShiftListView? {

        val request = RestAssured.given(this.requestSpecification)
            .header("Authorization", "Bearer $accessToken")
            ?.queryParam("from", from)
            ?.queryParam("to", to)
            ?.`when`()
            ?.get(MsmktApiGatewayEndPoints.SHIFTS)
            ?.then()!!.spec(SuiteBase.commonSpecification).extract().body().path<Any>("")

        return SuiteBase.jacksonObjectMapper.convertValue(request, ShiftListView::class.java)
    }

    fun getWorkedOutShift(accessToken: String, shiftId: UUID): WorkedOutShiftView{
        val request = RestAssured.given(this.requestSpecification)
            .header("Authorization", "Bearer $accessToken")
            ?.`when`()
            ?.get(MsmktApiGatewayEndPoints.SHIFT.replace("{shiftId}", shiftId.toString()))
            ?.then()!!.spec(SuiteBase.commonSpecification).extract().body().path<Any>("")

        return SuiteBase.jacksonObjectMapper.convertValue(request, WorkedOutShiftView::class.java)

    }

    fun getWorkedOutShiftError(accessToken: String, shiftId: UUID, sc: Int){
        val request = RestAssured.given(this.requestSpecification)
            .header("Authorization", "Bearer $accessToken")
            ?.`when`()
            ?.get(MsmktApiGatewayEndPoints.SHIFT.replace("{shiftId}", shiftId.toString()))
            ?.then()!!.statusCode(sc)

    }

    fun getAssignmentById(accessToken: String, assignmentId: UUID): ShiftAssignmentView{
        val request = RestAssured.given(this.requestSpecification)
            .header("Authorization", "Bearer $accessToken")
            ?.`when`()
            ?.get(MsmktApiGatewayEndPoints.SHIFTS_ASSIGNMENT.replace("{assignmentId}", assignmentId.toString()))
            ?.then()!!.spec(SuiteBase.commonSpecification).extract().body().path<Any>("")

        return SuiteBase.jacksonObjectMapper.convertValue(request, ShiftAssignmentView::class.java)
    }

    fun getAssignmentByIdError(accessToken: String, assignmentId: UUID, sc: Int){
        val request = RestAssured.given(this.requestSpecification)
            .header("Authorization", "Bearer $accessToken")
            ?.`when`()
            ?.get(MsmktApiGatewayEndPoints.SHIFTS_ASSIGNMENT.replace("{assignmentId}", assignmentId.toString()))
            ?.then()!!.statusCode(sc)
    }

    fun getReviewPending(accessToken: String): ReviewRequestsView{
        val request = RestAssured.given(this.requestSpecification)
            .header("Authorization", "Bearer $accessToken")
            ?.`when`()
            ?.get(MsmktApiGatewayEndPoints.ME_REVIEWS_PENDING)
            ?.then()!!.spec(SuiteBase.commonSpecification).extract().body().path<Any>("")

        return SuiteBase.jacksonObjectMapper.convertValue(request, ReviewRequestsView::class.java)
    }

    fun deleteReviewPending(accessToken: String, shiftId: UUID){
        val request = RestAssured.given(this.requestSpecification)
            .header("Authorization", "Bearer $accessToken")
            ?.`when`()
            ?.delete(MsmktApiGatewayEndPoints.SHIFTS_REVIEW_PENDING.replace("{shiftId}", shiftId.toString()))
            ?.then()!!.statusCode(HttpStatus.SC_NO_CONTENT)
    }


    fun submitReview(accessToken: String, shiftId: UUID, requestBody: SubmitShiftReviewRequest){
        val request = RestAssured.given(this.requestSpecification)
            .header("Authorization", "Bearer $accessToken")
            ?.body(requestBody)
            ?.`when`()
            ?.post(MsmktApiGatewayEndPoints.SHIFTS_REVIEW.replace("{shiftId}", shiftId.toString()))
            ?.then()!!.statusCode(HttpStatus.SC_NO_CONTENT)
    }
}