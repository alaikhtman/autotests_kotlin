package ru.generalName.teamName.tests.helpers.controllers

import io.restassured.RestAssured
import io.restassured.builder.RequestSpecBuilder
import io.restassured.filter.log.LogDetail
import io.restassured.http.ContentType
import org.springframework.stereotype.Service


import com.fasterxml.jackson.module.kotlin.*

@Service
class SmsStubController(
    restStubSms: NetConfig
) {

    private val requestSpecification = RequestSpecBuilder()
        .setBaseUri(restStubSms.url)
        .setPort(restStubSms.port)
        .setAccept(ContentType.JSON)
        .setContentType(ContentType.JSON)
        .log(LogDetail.ALL)
        .build()

    fun getOtp(mobile: PhoneNumber): String {

        Thread.sleep(2_000)
        val request = RestAssured.given(this.requestSpecification)
            ?.queryParam("phone", mobile.asStringWithoutPlus())
            ?.queryParam("limit", 1)
            ?.`when`()
            ?.get(StubSmsEndPoint.STUB_SMS)
            ?.then()!!.spec(SuiteBase.commonSpecification).extract().body().path<Any>("")

        return ((request as ArrayList<Any>)[0] as LinkedHashMap<String, String>).get("text")!!.substringAfter(": ")
    }
}