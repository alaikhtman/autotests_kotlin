package ru.samokat.mysamokat.tests.helpers.controllers.msmkt_apigw

import io.restassured.RestAssured
import io.restassured.builder.RequestSpecBuilder
import io.restassured.filter.log.LogDetail
import io.restassured.http.ContentType
import org.springframework.stereotype.Service
import ru.samokat.mysamokat.apigateway.api.common.domain.OAuthTokenView
import ru.samokat.mysamokat.apigateway.api.oauth.otp.SendOtpRequest
import ru.samokat.mysamokat.apigateway.api.oauth.otp.SendOtpResponseView
import ru.samokat.mysamokat.apigateway.api.oauth.token.GetOAuthTokenRequest
import ru.samokat.mysamokat.apigateway.api.pushtokens.register.RegisterPushTokenRequest
import ru.samokat.mysamokat.tests.SuiteBase.Companion.commonSpecification
import ru.samokat.mysamokat.tests.SuiteBase.Companion.jacksonObjectMapper
import ru.samokat.mysamokat.tests.dataproviders.ErrorView
import ru.samokat.mysamokat.tests.dataproviders.apiModels.staffcave.oauth.RefreshAccessTokenRequest
import ru.samokat.mysamokat.tests.endpoints.MsmktApiGatewayEndPoints
import ru.samokat.mysamokat.tests.endpoints.configuration.NetConfig

@Service
class AuthController(
    restApigateway: NetConfig
) {

    private val requestSpecification = RequestSpecBuilder()
        .setBaseUri(restApigateway.url)
        .setPort(restApigateway.port)
        .setAccept(ContentType.JSON)
        .setContentType(ContentType.JSON)
        .log(LogDetail.ALL)
        .build()

    fun authenticateProfile(requestBody: GetOAuthTokenRequest): OAuthTokenView? {

        val request = RestAssured.given(this.requestSpecification)
            ?.body(requestBody)
            ?.`when`()
            ?.post(MsmktApiGatewayEndPoints.AUTH_PASSWORD)
            ?.then()!!.spec(commonSpecification).extract().body().path<Any>("")

        return jacksonObjectMapper.convertValue(request, OAuthTokenView::class.java)
    }

    fun authenticateProfileWithError(requestBody: GetOAuthTokenRequest, sc: Int): ErrorView? {

        val request = RestAssured.given(this.requestSpecification)
            ?.body(requestBody)
            ?.`when`()
            ?.post(MsmktApiGatewayEndPoints.AUTH_PASSWORD)
            ?.then()!!.statusCode(sc).extract().body().path<Any>("")

        return jacksonObjectMapper.convertValue(request, ErrorView::class.java)
    }

    fun refreshToken(requestBody: RefreshAccessTokenRequest): OAuthTokenView? {

        val request = RestAssured.given(this.requestSpecification)
            ?.body(requestBody)
            ?.`when`()
            ?.post(MsmktApiGatewayEndPoints.TOKEN_REFRESH)
            ?.then()!!.spec(commonSpecification).extract().body().path<Any>("")

        return jacksonObjectMapper.convertValue(request, OAuthTokenView::class.java)

    }

    fun deleteToken(accessToken: String, sc: Int){
        val request = RestAssured.given(this.requestSpecification)
            .header("Authorization", "Bearer $accessToken")
            ?.`when`()
            ?.delete(MsmktApiGatewayEndPoints.AUTH_PASSWORD)
            ?.then()!!.statusCode(sc)
    }

    fun postOtp(requestBody: SendOtpRequest): SendOtpResponseView {
        val request = RestAssured.given(this.requestSpecification)
            ?.body(requestBody)
            ?.`when`()
            ?.post(MsmktApiGatewayEndPoints.OTP)
            ?.then()!!.spec(commonSpecification).extract().body().path<Any>("")

        return jacksonObjectMapper.convertValue(request, SendOtpResponseView::class.java)

    }

    fun postOtpError(requestBody: SendOtpRequest): ErrorView? {
        val request = RestAssured.given(this.requestSpecification)
            ?.body(requestBody)
            ?.`when`()
            ?.post(MsmktApiGatewayEndPoints.OTP)
            ?.then()!!.spec(commonSpecification).extract().body().path<Any>("")

        return jacksonObjectMapper.convertValue(request, ErrorView::class.java)

    }

    fun postPushToken(requestBody: RegisterPushTokenRequest) {
        val request = RestAssured.given(this.requestSpecification)
            ?.body(requestBody)
            ?.`when`()
            ?.post(MsmktApiGatewayEndPoints.PUSH_TOKENS)
            ?.then()!!.spec(commonSpecification)

    }

    fun postPushTokenError(requestBody: RegisterPushTokenRequest, sc: Int): ErrorView? {
        val request = RestAssured.given(this.requestSpecification)
            ?.body(requestBody)
            ?.`when`()
            ?.post(MsmktApiGatewayEndPoints.PUSH_TOKENS)
            ?.then()!!.statusCode(sc).extract().body().path<Any>("")

        return jacksonObjectMapper.convertValue(request, ErrorView::class.java)
    }
}