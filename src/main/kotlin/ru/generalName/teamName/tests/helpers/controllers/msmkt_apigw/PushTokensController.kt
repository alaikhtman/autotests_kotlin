package ru.samokat.mysamokat.tests.helpers.controllers.msmkt_apigw

import io.restassured.RestAssured
import io.restassured.builder.RequestSpecBuilder
import io.restassured.filter.log.LogDetail
import io.restassured.http.ContentType
import org.apache.http.HttpStatus
import org.springframework.stereotype.Service
import ru.samokat.mysamokat.apigateway.api.pushtokens.register.RegisterPushTokenRequest
import ru.samokat.mysamokat.tests.SuiteBase
import ru.samokat.mysamokat.tests.endpoints.MsmktApiGatewayEndPoints
import ru.samokat.mysamokat.tests.endpoints.configuration.NetConfig

@Service
class PushTokensController(
    restApigateway: NetConfig
) {

    private val requestSpecification = RequestSpecBuilder()
        .setBaseUri(restApigateway.url)
        .setPort(restApigateway.port)
        .setAccept(ContentType.JSON)
        .setContentType(ContentType.JSON)
        .log(LogDetail.ALL)
        .build()

    fun registerPushToken(accessToken: String, requestBody: RegisterPushTokenRequest){
        RestAssured.given(this.requestSpecification)
            .header("Authorization", "Bearer $accessToken")
            ?.body(requestBody)
            ?.`when`()
            ?.post(MsmktApiGatewayEndPoints.PUSH_TOKENS)
            ?.then()!!.statusCode(HttpStatus.SC_NO_CONTENT)
    }

    fun deletePushToken(accessToken: String, token: String){
        RestAssured.given(this.requestSpecification)
            .header("Authorization", "Bearer $accessToken")
            ?.`when`()
            ?.delete(MsmktApiGatewayEndPoints.PUSH_TOKENS_DEL.replace("{token}", token))
            ?.then()!!.statusCode(HttpStatus.SC_NO_CONTENT)
    }

}