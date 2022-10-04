package ru.samokat.mysamokat.tests.helpers.controllers.msmkt_apigw

import io.restassured.RestAssured
import io.restassured.builder.RequestSpecBuilder
import io.restassured.filter.log.LogDetail
import io.restassured.http.ContentType
import org.apache.http.HttpStatus
import org.springframework.stereotype.Service
import ru.samokat.mysamokat.apigateway.api.common.domain.OAuthTokenView
import ru.samokat.mysamokat.apigateway.api.common.domain.UserView
import ru.samokat.mysamokat.apigateway.api.oauth.token.GetOAuthTokenRequest
import ru.samokat.mysamokat.apigateway.api.user.getcontacts.UserContactsView
import ru.samokat.mysamokat.apigateway.api.user.getexpectations.schedule.ScheduleExpectationsView
import ru.samokat.mysamokat.apigateway.api.user.getexpectations.schedule.submit.SubmitScheduleExpectationsRequest
import ru.samokat.mysamokat.apigateway.api.user.getstatistics.actual.UserActualStatisticsView
import ru.samokat.mysamokat.apigateway.api.user.getstatistics.aggregated.AggregatedUserStatisticsListView
import ru.samokat.mysamokat.apigateway.api.user.getstatistics.shifts.getlist.ShiftsStatisticsListView
import ru.samokat.mysamokat.apigateway.api.user.gettips.UserTipsView
import ru.samokat.mysamokat.tests.SuiteBase
import ru.samokat.mysamokat.tests.dataproviders.ErrorView
import ru.samokat.mysamokat.tests.endpoints.MsmktApiGatewayEndPoints
import ru.samokat.mysamokat.tests.endpoints.configuration.NetConfig

@Service
class UserApiController(
    restApigateway: NetConfig
) {
    private val requestSpecification = RequestSpecBuilder()
        .setBaseUri(restApigateway.url)
        .setPort(restApigateway.port)
        .setAccept(ContentType.JSON)
        .setContentType(ContentType.JSON)
        .log(LogDetail.ALL)
        .build()

    fun getUserData(accessToken: String): UserView? {

        val request = RestAssured.given(this.requestSpecification)
            .header("Authorization", "Bearer $accessToken")
            ?.`when`()
            ?.get(MsmktApiGatewayEndPoints.ME)
            ?.then()!!.spec(SuiteBase.commonSpecification).extract().body().path<Any>("")

        return SuiteBase.jacksonObjectMapper.convertValue(request, UserView::class.java)
    }

    fun getUserDataWithError(accessToken: String?, sc: Int): ErrorView? {

        val request = RestAssured.given(this.requestSpecification)
            .header("Authorization", "Bearer $accessToken")
            ?.`when`()
            ?.get(MsmktApiGatewayEndPoints.ME)
            ?.then()!!.statusCode(sc).extract().body().path<Any>("")

        return SuiteBase.jacksonObjectMapper.convertValue(request, ErrorView::class.java)
    }

    fun putExpectations(accessToken: String, requestBody: SubmitScheduleExpectationsRequest){
        val request = RestAssured.given(this.requestSpecification)
            .header("Authorization", "Bearer $accessToken")
            ?.body(requestBody)
            ?.`when`()
            ?.put(MsmktApiGatewayEndPoints.EXPECTATION_SCHEDULE)
            ?.then()!!.statusCode(HttpStatus.SC_NO_CONTENT)
    }

    fun putExpectationsError(accessToken: String, requestBody: SubmitScheduleExpectationsRequest, sc: Int): ErrorView? {
        val request = RestAssured.given(this.requestSpecification)
            .header("Authorization", "Bearer $accessToken")
            ?.body(requestBody)
            ?.`when`()
            ?.put(MsmktApiGatewayEndPoints.EXPECTATION_SCHEDULE)
            ?.then()!!.statusCode(sc).extract().body().path<Any>("")

        return SuiteBase.jacksonObjectMapper.convertValue(request, ErrorView::class.java)
    }

    fun getExpectations(accessToken: String): ScheduleExpectationsView? {
        val request = RestAssured.given(this.requestSpecification)
            .header("Authorization", "Bearer $accessToken")
            ?.`when`()
            ?.get(MsmktApiGatewayEndPoints.EXPECTATION_SCHEDULE)
            ?.then()!!.spec(SuiteBase.commonSpecification).extract().body().path<Any>("")
        return SuiteBase.jacksonObjectMapper.convertValue(request, ScheduleExpectationsView::class.java)
    }

    fun getExpectationsError(accessToken: String, sc: Int) {
        val request = RestAssured.given(this.requestSpecification)
            .header("Authorization", "Bearer $accessToken")
            ?.`when`()
            ?.get(MsmktApiGatewayEndPoints.EXPECTATION_SCHEDULE)
            ?.then()!!.statusCode(sc)
    }

    fun getContacts(accessToken: String): UserContactsView {
        val request = RestAssured.given(this.requestSpecification)
            .header("Authorization", "Bearer $accessToken")
            ?.`when`()
            ?.get(MsmktApiGatewayEndPoints.ME_CONTACTS)
            ?.then()!!.spec(SuiteBase.commonSpecification).extract().body().path<Any>("")

        return SuiteBase.jacksonObjectMapper.convertValue(request, UserContactsView::class.java)
    }

    fun getContactsError(accessToken: String, sc: Int) {
        val request = RestAssured.given(this.requestSpecification)
            .header("Authorization", "Bearer $accessToken")
            ?.`when`()
            ?.get(MsmktApiGatewayEndPoints.ME_CONTACTS)
            ?.then()!!.statusCode(sc)
    }

    fun getShiftsStatistics(accessToken: String, from: String, to: String): ShiftsStatisticsListView? {

        val request = RestAssured.given(this.requestSpecification)
            .header("Authorization", "Bearer $accessToken")
            ?.queryParam("from", from)
            ?.queryParam("to", to)
            ?.`when`()
            ?.get(MsmktApiGatewayEndPoints.ME_STATS_SHIFTS)
            ?.then()!!.spec(SuiteBase.commonSpecification).extract().body().path<Any>("")

        return SuiteBase.jacksonObjectMapper.convertValue(request, ShiftsStatisticsListView::class.java)
    }

    fun getShiftsStatisticsError(accessToken: String, from: String, to: String, sc: Int): ErrorView {

        val request = RestAssured.given(this.requestSpecification)
            .header("Authorization", "Bearer $accessToken")
            ?.queryParam("from", from)
            ?.queryParam("to", to)
            ?.`when`()
            ?.get(MsmktApiGatewayEndPoints.ME_STATS_SHIFTS)
            ?.then()!!.statusCode(sc).extract().body().path<Any>("")

        return SuiteBase.jacksonObjectMapper.convertValue(request, ErrorView::class.java)
    }

    fun getShiftsStatisticsPageSize(accessToken: String, from: String, to: String, pageSize: String): ShiftsStatisticsListView? {

        val request = RestAssured.given(this.requestSpecification)
            .header("Authorization", "Bearer $accessToken")
            ?.queryParam("from", from)
            ?.queryParam("to", to)
            ?.queryParam("pageSize", pageSize)
            ?.`when`()
            ?.get(MsmktApiGatewayEndPoints.ME_STATS_SHIFTS)
            ?.then()!!.spec(SuiteBase.commonSpecification).extract().body().path<Any>("")

        return SuiteBase.jacksonObjectMapper.convertValue(request, ShiftsStatisticsListView::class.java)
    }

    fun getShiftsStatisticsPaging(accessToken: String, from: String, to: String, pageSize: String, pageMark: String): ShiftsStatisticsListView? {

        val request = RestAssured.given(this.requestSpecification)
            .header("Authorization", "Bearer $accessToken")
            ?.queryParam("from", from)
            ?.queryParam("to", to)
            ?.queryParam("pageSize", pageSize)
            ?.queryParam("pageMark", pageMark)
            ?.`when`()
            ?.get(MsmktApiGatewayEndPoints.ME_STATS_SHIFTS)
            ?.then()!!.spec(SuiteBase.commonSpecification).extract().body().path<Any>("")

        return SuiteBase.jacksonObjectMapper.convertValue(request, ShiftsStatisticsListView::class.java)
    }

    fun getAggregatedStatistics(accessToken: String, aggregationType: String): AggregatedUserStatisticsListView? {

        val request = RestAssured.given(this.requestSpecification)
            .header("Authorization", "Bearer $accessToken")
            ?.queryParam("aggregationType", aggregationType )
            ?.`when`()
            ?.get(MsmktApiGatewayEndPoints.ME_STATS_AGREGATE)
            ?.then()!!.spec(SuiteBase.commonSpecification).extract().body().path<Any>("")

        return SuiteBase.jacksonObjectMapper.convertValue(request, AggregatedUserStatisticsListView::class.java)
    }

    fun getActualStatistics(accessToken: String): UserActualStatisticsView? {

        val request = RestAssured.given(this.requestSpecification)
            .header("Authorization", "Bearer $accessToken")
            ?.`when`()
            ?.get(MsmktApiGatewayEndPoints.ME_STATS_ACTUAL)
            ?.then()!!.spec(SuiteBase.commonSpecification).extract().body().path<Any>("")

        return SuiteBase.jacksonObjectMapper.convertValue(request, UserActualStatisticsView::class.java)
    }

    fun getTipsData(accessToken: String): UserTipsView? {

        val request = RestAssured.given(this.requestSpecification)
            .header("Authorization", "Bearer $accessToken")
            ?.`when`()
            ?.get(MsmktApiGatewayEndPoints.ME_TIPS)
            ?.then()!!.spec(SuiteBase.commonSpecification).extract().body().path<Any>("")

        return SuiteBase.jacksonObjectMapper.convertValue(request, UserTipsView::class.java)
    }
}