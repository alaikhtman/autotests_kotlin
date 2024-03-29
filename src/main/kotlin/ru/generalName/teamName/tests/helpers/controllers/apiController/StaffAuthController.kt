package ru.generalName.teamName.tests.helpers.controllers.apiController

import io.restassured.RestAssured
import io.restassured.builder.RequestSpecBuilder
import io.restassured.filter.log.LogDetail
import io.restassured.http.ContentType
import io.restassured.module.jsv.JsonSchemaValidator
import org.springframework.stereotype.Service



@Service
class StaffAuthController(restStaffApigateway: NetConfig) {

    private val requestSpecification = RequestSpecBuilder()
        .setBaseUri(restStaffApigateway.url)
        .setPort(restStaffApigateway.port)
        .setAccept(ContentType.JSON)
        .setContentType(ContentType.JSON)
        .log(LogDetail.ALL)
        .build()

    fun authenticateProfile(requestBody: GetOAuthTokenRequest): OAuthTokenView {

        val request = RestAssured.given(this.requestSpecification)
            ?.body(requestBody)
            ?.`when`()
            ?.post(ApiGatewayEndPoints.AUTH_PASSWORD)
            ?.then()!!.spec(SuiteBase.commonSpecification)
            .assertThat().body(
                JsonSchemaValidator.matchesJsonSchemaInClasspath(
                    "api-gateway/OauthTokenSchema.json"
                )
            )
            .extract().body().path<Any>("")



        return SuiteBase.jacksonObjectMapper.convertValue(
            request,
            OAuthTokenView::class.java
        )
    }

    fun authenticateProfileWithError(requestBody: GetOAuthTokenRequest, sc: Int): ErrorView? {

        val request = RestAssured.given(this.requestSpecification)
            ?.body(requestBody)
            ?.`when`()
            ?.post(ApiGatewayEndPoints.AUTH_PASSWORD)
            ?.then()!!.statusCode(sc).log().all()
            .assertThat().body(
                JsonSchemaValidator.matchesJsonSchemaInClasspath(
                    "api-gateway/ErrorViewSchema.json"
                )
            )
            .extract().body().path<Any>("")

        return SuiteBase.jacksonObjectMapper.convertValue(request, ErrorView::class.java)
    }

    fun refreshToken(requestBody: RefreshAccessTokenRequest): OAuthTokenView? {

        val request = RestAssured.given(this.requestSpecification)
            ?.body(requestBody)
            ?.`when`()
            ?.post(ApiGatewayEndPoints.TOKEN_REFRESH)
            ?.then()!!.spec(SuiteBase.commonSpecification)
            .assertThat().body(
                JsonSchemaValidator.matchesJsonSchemaInClasspath(
                    "api-gateway/OauthTokenSchema.json"
                )
            )
            .extract().body().path<Any>("")

        return SuiteBase.jacksonObjectMapper.convertValue(request, OAuthTokenView::class.java)

    }

    fun refreshTokenWithError(requestBody: RefreshAccessTokenRequest, sc: Int): ErrorView? {

        val request = RestAssured.given(this.requestSpecification)
            ?.body(requestBody)
            ?.`when`()
            ?.post(ApiGatewayEndPoints.TOKEN_REFRESH)
            ?.then()!!.statusCode(sc).log().all()
            .assertThat().body(
                JsonSchemaValidator.matchesJsonSchemaInClasspath(
                    "api-gateway/ErrorViewSchema.json"
                )
            )
            .extract().body().path<Any>("")

        return SuiteBase.jacksonObjectMapper.convertValue(request, ErrorView::class.java)

    }

    fun deleteToken(accessToken: String, sc: Int) {
        val request = RestAssured.given(this.requestSpecification)
            .header("Authorization", "Bearer $accessToken")
            ?.`when`()
            ?.delete(ApiGatewayEndPoints.AUTH_PASSWORD)
            ?.then()!!.statusCode(sc)
    }

    fun postOtp(requestBody: SendOtpRequest): SendOtpResponseView {
        val request = RestAssured.given(this.requestSpecification)
            ?.body(requestBody)
            ?.`when`()
            ?.post(ApiGatewayEndPoints.OTP)
            ?.then()!!.spec(SuiteBase.commonSpecification)
            .assertThat().body(
                JsonSchemaValidator.matchesJsonSchemaInClasspath(
                    "api-gateway/SendOtpSchema.json"
                )
            )
            .extract().body().path<Any>("")

        return SuiteBase.jacksonObjectMapper.convertValue(request, SendOtpResponseView::class.java)

    }

}