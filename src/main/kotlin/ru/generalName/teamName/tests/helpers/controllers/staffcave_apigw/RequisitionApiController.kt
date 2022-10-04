package ru.samokat.mysamokat.tests.helpers.controllers.staffcave_apigw

import io.restassured.RestAssured
import io.restassured.builder.RequestSpecBuilder
import io.restassured.filter.log.LogDetail
import io.restassured.http.ContentType
import io.restassured.module.jsv.JsonSchemaValidator
import org.springframework.stereotype.Service
import ru.samokat.mysamokat.tests.SuiteBase
import ru.samokat.mysamokat.tests.dataproviders.ErrorView
import ru.samokat.mysamokat.tests.dataproviders.apiModels.staffcave.requisitions.GetUserRequisitionView
import ru.samokat.mysamokat.tests.dataproviders.apiModels.staffcave.requisitions.SearchUserRequisitionsRequest
import ru.samokat.mysamokat.tests.dataproviders.apiModels.staffcave.requisitions.SearchUserRequisitionsView
import ru.samokat.mysamokat.tests.endpoints.StaffCaveApiGatewayEndPoints
import ru.samokat.mysamokat.tests.endpoints.configuration.NetConfig
import java.util.*

@Service
class RequisitionApiController (restStaffCaveApigateway: NetConfig) {

    private val requestSpecification = RequestSpecBuilder()
        .setBaseUri(restStaffCaveApigateway.url)
        .setPort(restStaffCaveApigateway.port)
        .setAccept(ContentType.JSON)
        .setContentType(ContentType.JSON)
        .log(LogDetail.ALL)
        .build()

    fun getRequisitionById(accessToken: String, requestId: UUID): GetUserRequisitionView? {

        return  RestAssured.given(this.requestSpecification)
            .header("Authorization", "Bearer $accessToken")
            ?.`when`()
            ?.get(StaffCaveApiGatewayEndPoints.REQUEST.replace("{requestId}", requestId.toString()))
            ?.then()!!.spec(SuiteBase.commonSpecification)
            .assertThat().body(
                JsonSchemaValidator.matchesJsonSchemaInClasspath(
                    "staff-cave-api-gateway/GetUserRequisitionViewSchema.json"))
            .extract().`as`(GetUserRequisitionView::class.java)
    }

    fun getRequisitionByIdWithError(accessToken: String, requestId: UUID, sc: Int) {

          RestAssured.given(this.requestSpecification)
            .header("Authorization", "Bearer $accessToken")
            ?.`when`()
            ?.get(StaffCaveApiGatewayEndPoints.REQUEST.replace("{requestId}", requestId.toString()))
            ?.then()!!.log().all().statusCode(sc)
    }

    fun declineRequisition(accessToken: String, requestId: UUID, sc: Int) {

        RestAssured.given(this.requestSpecification)
            .header("Authorization", "Bearer $accessToken")
            ?.`when`()
            ?.delete(StaffCaveApiGatewayEndPoints.REQUEST.replace("{requestId}", requestId.toString()))
            ?.then()!!.log().all().statusCode(sc)
    }

    fun declineRequisitionWithError(accessToken: String, requestId: UUID, sc: Int): ErrorView {

        return RestAssured.given(this.requestSpecification)
            .header("Authorization", "Bearer $accessToken")
            ?.`when`()
            ?.delete(StaffCaveApiGatewayEndPoints.REQUEST.replace("{requestId}", requestId.toString()))
            ?.then()!!.log().all().statusCode(sc)
            .assertThat().body(
                JsonSchemaValidator.matchesJsonSchemaInClasspath(
                    "staff-cave-api-gateway/ErrorViewSchema.json"))
            .extract().`as`(ErrorView::class.java)
    }

    fun searchRequisitions(accessToken: String, requestBody: SearchUserRequisitionsRequest): SearchUserRequisitionsView? {

        return  RestAssured.given(this.requestSpecification)
            .header("Authorization", "Bearer $accessToken")
            ?.body(requestBody)
            ?.`when`()
            ?.post(StaffCaveApiGatewayEndPoints.REQUESTS)
            ?.then()!!.spec(SuiteBase.commonSpecification)
            .assertThat().body(
                JsonSchemaValidator.matchesJsonSchemaInClasspath(
                    "staff-cave-api-gateway/SearchUserRequisitionsViewSchema.json"))
            .extract().`as`(SearchUserRequisitionsView::class.java)
    }

}