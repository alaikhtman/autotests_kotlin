package ru.samokat.mysamokat.tests.helpers.controllers.msmkt_apigw

import io.restassured.RestAssured
import io.restassured.builder.RequestSpecBuilder
import io.restassured.filter.log.LogDetail
import io.restassured.http.ContentType
import org.springframework.stereotype.Service
import ru.samokat.mysamokat.apigateway.api.common.domain.OAuthTokenView
import ru.samokat.mysamokat.apigateway.api.oauth.token.GetOAuthTokenRequest
import ru.samokat.mysamokat.tests.SuiteBase
import ru.samokat.mysamokat.tests.dataproviders.ErrorView
import ru.samokat.mysamokat.tests.dataproviders.MSMKTConfigView
import ru.samokat.mysamokat.tests.endpoints.MsmktApiGatewayEndPoints
import ru.samokat.mysamokat.tests.endpoints.configuration.NetConfig

@Service
class ConfigController(
    restApigateway: NetConfig
) {

    private val requestSpecification = RequestSpecBuilder()
        .setBaseUri(restApigateway.url)
        .setPort(restApigateway.port)
        .setAccept(ContentType.JSON)
        .setContentType(ContentType.JSON)
        .log(LogDetail.ALL)
        .build()


    fun getConfig(type: String): MSMKTConfigView? {
        val request = RestAssured.given(this.requestSpecification)
            ?.`when`()
            ?.get(MsmktApiGatewayEndPoints.CONFIG.replace("{type}", type))
            ?.then()!!.spec(SuiteBase.commonSpecification).extract().body().path<Any>("")

        return SuiteBase.jacksonObjectMapper.convertValue(request, MSMKTConfigView::class.java)
    }

    fun getConfigWithError(type: String, sc: Int) {

        val request = RestAssured.given(this.requestSpecification)
            ?.`when`()
            ?.get(MsmktApiGatewayEndPoints.CONFIG.replace("{type}", type))
            ?.then()!!.statusCode(sc)
    }
}