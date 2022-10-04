package ru.samokat.mysamokat.tests.helpers.controllers.staffcave_apigw

import io.restassured.RestAssured
import io.restassured.builder.RequestSpecBuilder
import io.restassured.filter.log.LogDetail
import io.restassured.http.ContentType
import io.restassured.module.jsv.JsonSchemaValidator
import org.apache.http.HttpStatus
import org.springframework.stereotype.Service
import ru.samokat.mysamokat.tests.SuiteBase
import ru.samokat.mysamokat.tests.dataproviders.ErrorView
import ru.samokat.mysamokat.tests.dataproviders.apiModels.staffcave.internships.CreateInternshipRequest
import ru.samokat.mysamokat.tests.dataproviders.apiModels.staffcave.internships.InternshipsView
import ru.samokat.mysamokat.tests.dataproviders.apiModels.staffcave.internships.UpdateInternshipRequest
import ru.samokat.mysamokat.tests.endpoints.StaffCaveApiGatewayEndPoints
import ru.samokat.mysamokat.tests.endpoints.configuration.NetConfig
import java.util.*

@Service
class InternshipsApiController (restStaffCaveApigateway: NetConfig) {

    private val requestSpecification = RequestSpecBuilder()
        .setBaseUri(restStaffCaveApigateway.url)
        .setPort(restStaffCaveApigateway.port)
        .setAccept(ContentType.JSON)
        .setContentType(ContentType.JSON)
        .log(LogDetail.ALL)
        .build()

    fun createInternship(accessToken: String, requestBody: CreateInternshipRequest, userId: UUID, role: String) {

        RestAssured.given(this.requestSpecification)
            .header("Authorization", "Bearer $accessToken")
            ?.body(requestBody)
            ?.`when`()
            ?.post(StaffCaveApiGatewayEndPoints.INTERNSHIP.replace("{userId}", userId.toString()).replace("{role}", role))
            ?.then()!!.statusCode(HttpStatus.SC_CREATED)
    }

    fun createInternshipWithError(accessToken: String, requestBody: CreateInternshipRequest, userId: UUID, role: String, sc: Int): ErrorView? {

        return RestAssured.given(this.requestSpecification)
            .header("Authorization", "Bearer $accessToken")
            ?.body(requestBody)
            ?.`when`()
            ?.post(StaffCaveApiGatewayEndPoints.INTERNSHIP.replace("{userId}", userId.toString()).replace("{role}", role))
            ?.then()!!.log().all().statusCode(sc)
            .assertThat().body(
                JsonSchemaValidator.matchesJsonSchemaInClasspath(
                    "staff-cave-api-gateway/ErrorViewSchema.json"))
            .extract().`as`(ErrorView::class.java)
    }

    fun getInternshipByUserId(accessToken: String, userId: UUID): InternshipsView {

        return  RestAssured.given(this.requestSpecification)
            .header("Authorization", "Bearer $accessToken")
            ?.`when`()
            ?.get(StaffCaveApiGatewayEndPoints.USER_INTERNSHIP.replace("{userId}", userId.toString()))
            ?.then()!!.spec(SuiteBase.commonSpecification)
            .assertThat().body(
                JsonSchemaValidator.matchesJsonSchemaInClasspath(
                    "staff-cave-api-gateway/InternshipsViewSchema.json"))
            .extract().`as`(InternshipsView::class.java)
    }

    fun getInternshipByUserIdWithError(accessToken: String, userId: UUID, sc: Int): ErrorView {

        return  RestAssured.given(this.requestSpecification)
            .header("Authorization", "Bearer $accessToken")
            ?.`when`()
            ?.get(StaffCaveApiGatewayEndPoints.USER_INTERNSHIP.replace("{userId}", userId.toString()))
            ?.then()!!.log().all().statusCode(sc)
            .assertThat().body(
                JsonSchemaValidator.matchesJsonSchemaInClasspath(
                    "staff-cave-api-gateway/ErrorViewSchema.json"))
            .extract().`as`(ErrorView::class.java)
    }

    fun updateInternship(accessToken: String, requestBody: UpdateInternshipRequest, userId: UUID, role: String) {

        RestAssured.given(this.requestSpecification)
            .header("Authorization", "Bearer $accessToken")
            ?.body(requestBody)
            ?.`when`()
            ?.put(StaffCaveApiGatewayEndPoints.INTERNSHIP.replace("{userId}", userId.toString()).replace("{role}", role))
            ?.then()!!.log().all().statusCode(HttpStatus.SC_NO_CONTENT)
    }

    fun updateInternshipWithError(accessToken: String, requestBody: UpdateInternshipRequest, userId: UUID, role: String, sc: Int): ErrorView? {

        return RestAssured.given(this.requestSpecification)
            .header("Authorization", "Bearer $accessToken")
            ?.body(requestBody)
            ?.`when`()
            ?.put(StaffCaveApiGatewayEndPoints.INTERNSHIP.replace("{userId}", userId.toString()).replace("{role}", role))
            ?.then()!!.log().all().statusCode(sc)
            .assertThat().body(
                JsonSchemaValidator.matchesJsonSchemaInClasspath(
                    "staff-cave-api-gateway/ErrorViewSchema.json"))
            .extract().`as`(ErrorView::class.java)
    }

}