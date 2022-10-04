package ru.samokat.mysamokat.tests.helpers.controllers.staffcave_apigw

import io.restassured.RestAssured
import io.restassured.builder.RequestSpecBuilder
import io.restassured.filter.log.LogDetail
import io.restassured.http.ContentType
import io.restassured.module.jsv.JsonSchemaValidator
import org.springframework.stereotype.Service


import ru.samokat.mysamokat.tests.SuiteBase
import ru.samokat.mysamokat.tests.dataproviders.ErrorView
import ru.samokat.mysamokat.tests.dataproviders.apiModels.staffcave.oauth.GetOAuthTokenRequest
import ru.samokat.mysamokat.tests.dataproviders.apiModels.staffcave.oauth.OAuthTokenView
import ru.samokat.mysamokat.tests.dataproviders.apiModels.staffcave.oauth.RefreshAccessTokenRequest
import ru.samokat.mysamokat.tests.endpoints.StaffCaveApiGatewayEndPoints
import ru.samokat.mysamokat.tests.endpoints.configuration.NetConfig

@Service
class SCAuthController (restStaffCaveApigateway: NetConfig) {

    private val requestSpecification = RequestSpecBuilder()
        .setBaseUri(restStaffCaveApigateway.url)
        .setPort(restStaffCaveApigateway.port)
        .setAccept(ContentType.JSON)
        .setContentType(ContentType.JSON)
        .log(LogDetail.ALL)
        .build()

    fun authenticateProfile(requestBody: String): String {

        return  RestAssured.given(this.requestSpecification)
            ?.body(requestBody)
            ?.`when`()
            ?.post(StaffCaveApiGatewayEndPoints.TOKEN)
            ?.then()!!.spec(SuiteBase.commonSpecification).extract().body().asString()
    }

    fun authenticateProfile(requestBody: GetOAuthTokenRequest): OAuthTokenView {
        return  RestAssured.given(this.requestSpecification)
            ?.body(requestBody)
            ?.`when`()
            ?.post(StaffCaveApiGatewayEndPoints.TOKEN)
            ?.then()!!.spec(SuiteBase.commonSpecification)
            .assertThat().body(
                JsonSchemaValidator.matchesJsonSchemaInClasspath(
                    "staff-cave-api-gateway/OAuthTokenViewSchema.json"))
            .extract().`as`(OAuthTokenView::class.java)
    }

    fun authenticateProfileWithError(requestBody: GetOAuthTokenRequest, sc: Int): ErrorView? {

        val request = RestAssured.given(this.requestSpecification)
            ?.body(requestBody)
            ?.`when`()
            ?.post(StaffCaveApiGatewayEndPoints.TOKEN)
            ?.then()!!.log().all().statusCode(sc)
            .assertThat().body(
                JsonSchemaValidator.matchesJsonSchemaInClasspath(
                    "staff-cave-api-gateway/ErrorViewSchema.json"))
            .extract().body().path<Any>("")

        return SuiteBase.jacksonObjectMapper.convertValue(request, ErrorView::class.java)
    }

    fun refreshToken(requestBody: RefreshAccessTokenRequest): OAuthTokenView? {

        val request = RestAssured.given(this.requestSpecification)
            ?.body(requestBody)
            ?.`when`()
            ?.post(StaffCaveApiGatewayEndPoints.TOKEN_REFRESH)
            ?.then()!!.spec(SuiteBase.commonSpecification)
            .assertThat().body(
                JsonSchemaValidator.matchesJsonSchemaInClasspath(
                    "staff-cave-api-gateway/OAuthTokenViewSchema.json"))
            .extract().body().path<Any>("")

        return SuiteBase.jacksonObjectMapper.convertValue(request, OAuthTokenView::class.java)
    }

    fun deleteToken(accessToken: String, sc: Int){
        RestAssured.given(this.requestSpecification)
            .header("Authorization", "Bearer $accessToken")
            ?.`when`()
            ?.delete(StaffCaveApiGatewayEndPoints.TOKEN)
            ?.then()!!.log().all().statusCode(sc)
    }
}