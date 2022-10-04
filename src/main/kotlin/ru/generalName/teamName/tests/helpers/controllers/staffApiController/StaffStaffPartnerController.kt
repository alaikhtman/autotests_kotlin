package ru.samokat.mysamokat.tests.helpers.controllers.staffApiController

import io.restassured.RestAssured
import io.restassured.builder.RequestSpecBuilder
import io.restassured.filter.log.LogDetail
import io.restassured.http.ContentType
import io.restassured.module.jsv.JsonSchemaValidator
import org.springframework.stereotype.Service
import ru.samokat.mysamokat.tests.SuiteBase
import ru.samokat.mysamokat.tests.dataproviders.ErrorView
import ru.samokat.mysamokat.tests.dataproviders.dataClasses.staffApiGTW.staffPartner.GetStaffPartnersView
import ru.samokat.mysamokat.tests.endpoints.StaffApiGatewayEndPoints
import ru.samokat.mysamokat.tests.endpoints.configuration.NetConfig

@Service
class StaffStaffPartnerController(restStaffApigateway: NetConfig) {

    private val requestSpecification = RequestSpecBuilder()
        .setBaseUri(restStaffApigateway.url)
        .setPort(restStaffApigateway.port)
        .setAccept(ContentType.JSON)
        .setContentType(ContentType.JSON)
        .log(LogDetail.ALL)
        .build()

    fun getStaffPartner(accessToken: String): GetStaffPartnersView {

        val request = RestAssured.given(this.requestSpecification)
            .header("Authorization", "Bearer $accessToken")
            ?.`when`()
            ?.get(StaffApiGatewayEndPoints.STAFF_PARTNERS)
            ?.then()!!.spec(SuiteBase.commonSpecification)
            .assertThat().body(
                JsonSchemaValidator.matchesJsonSchemaInClasspath(
                    "staff-api-gateway/GetStaffPartnersSchema.json"
                )
            )
            .extract().body().path<Any>("")

        return SuiteBase.jacksonObjectMapper.convertValue(
            request,
            GetStaffPartnersView::class.java
        )

    }

    fun getStaffPartnerWithError(accessToken: String, sc: Int): ErrorView {

        val request = RestAssured.given(this.requestSpecification)
            .header("Authorization", "Bearer $accessToken")
            ?.`when`()
            ?.get(StaffApiGatewayEndPoints.STAFF_PARTNERS)
            ?.then()!!.statusCode(sc).log().all()
            .assertThat().body(
                JsonSchemaValidator.matchesJsonSchemaInClasspath(
                    "staff-api-gateway/ErrorViewSchema.json"
                )
            )
            .extract().body().path<Any>("")

        return SuiteBase.jacksonObjectMapper.convertValue(request, ErrorView::class.java)
    }

}