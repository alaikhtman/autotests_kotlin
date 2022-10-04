package ru.samokat.mysamokat.tests.helpers.controllers.staffcave_apigw

import io.restassured.RestAssured
import io.restassured.builder.RequestSpecBuilder
import io.restassured.filter.log.LogDetail
import io.restassured.http.ContentType
import io.restassured.module.jsv.JsonSchemaValidator
import org.apache.http.HttpStatus
import org.springframework.stereotype.Service
import ru.samokat.mysamokat.tests.SuiteBase
import ru.samokat.mysamokat.tests.dataproviders.apiModels.staffcave.staffpartners.CreatePartnerError
import ru.samokat.mysamokat.tests.dataproviders.apiModels.staffcave.staffpartners.CreatePartnerRequest
import ru.samokat.mysamokat.tests.dataproviders.apiModels.staffcave.staffpartners.StaffPartnersListView
import ru.samokat.mysamokat.tests.endpoints.StaffCaveApiGatewayEndPoints
import ru.samokat.mysamokat.tests.endpoints.configuration.NetConfig

@Service
class StaffPartnersApiController (restStaffCaveApigateway: NetConfig) {

    private val requestSpecification = RequestSpecBuilder()
        .setBaseUri(restStaffCaveApigateway.url)
        .setPort(restStaffCaveApigateway.port)
        .setAccept(ContentType.JSON)
        .setContentType(ContentType.JSON)
        .log(LogDetail.ALL)
        .build()

    fun getStaffPartnersList(accessToken: String): StaffPartnersListView? {

        return RestAssured.given(this.requestSpecification)
            .header("Authorization", "Bearer $accessToken")
            ?.`when`()
            ?.get(StaffCaveApiGatewayEndPoints.STAFF_PARTNERS)
            ?.then()!!.spec(SuiteBase.commonSpecification)
            .assertThat().body(
                JsonSchemaValidator.matchesJsonSchemaInClasspath(
                    "staff-cave-api-gateway/StaffPartnersListSchema.json"))
            .extract().`as`(StaffPartnersListView::class.java)

    }

    fun createStaffPartnersList(accessToken: String, requestBody: CreatePartnerRequest) {

        RestAssured.given(this.requestSpecification)
            .header("Authorization", "Bearer $accessToken")
            ?.`when`()
            ?.body(requestBody)
            ?.post(StaffCaveApiGatewayEndPoints.STAFF_PARTNERS)
            ?.then()!!.statusCode(HttpStatus.SC_CREATED)

    }

    fun createStaffPartnerWithError(accessToken: String, requestBody: CreatePartnerRequest, sc: Int): CreatePartnerError? {

        return RestAssured.given(this.requestSpecification)
            .header("Authorization", "Bearer $accessToken")
            ?.`when`()
            ?.body(requestBody)
            ?.post(StaffCaveApiGatewayEndPoints.STAFF_PARTNERS)
            ?.then()!!.statusCode(sc).log().all()
            .assertThat().body(
                JsonSchemaValidator.matchesJsonSchemaInClasspath(
                    "staff-cave-api-gateway/CreatePartnerErrorSchema.json"))
            .extract().`as`(CreatePartnerError::class.java)
    }

}