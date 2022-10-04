package ru.samokat.mysamokat.tests.helpers.controllers.staffcave_apigw

import io.restassured.RestAssured
import io.restassured.builder.RequestSpecBuilder
import io.restassured.filter.log.LogDetail
import io.restassured.http.ContentType
import io.restassured.module.jsv.JsonSchemaValidator

import org.springframework.stereotype.Service
import ru.samokat.mysamokat.tests.SuiteBase
import ru.samokat.mysamokat.tests.dataproviders.ErrorView
import ru.samokat.mysamokat.tests.dataproviders.apiModels.staffcave.DarkstoreListView
import ru.samokat.mysamokat.tests.endpoints.StaffCaveApiGatewayEndPoints
import ru.samokat.mysamokat.tests.endpoints.configuration.NetConfig


@Service
class DarkstoresApiController (restStaffCaveApigateway: NetConfig){


    private val requestSpecification = RequestSpecBuilder()
        .setBaseUri(restStaffCaveApigateway.url)
        .setPort(restStaffCaveApigateway.port)
        .setAccept(ContentType.JSON)
        .setContentType(ContentType.JSON)
        .log(LogDetail.ALL)
        .build()

    fun getDarkStoresList(accessToken: String): DarkstoreListView? {

        return  RestAssured.given(this.requestSpecification)
            .header("Authorization", "Bearer $accessToken")
            ?.`when`()
            ?.get(StaffCaveApiGatewayEndPoints.DARKSTORES)
            ?.then()!!.spec(SuiteBase.commonSpecification)
            .assertThat().body(
                JsonSchemaValidator.matchesJsonSchemaInClasspath(
                "staff-cave-api-gateway/DarkstoresListShema.json"))
            .extract().`as`(DarkstoreListView::class.java)
    }
}