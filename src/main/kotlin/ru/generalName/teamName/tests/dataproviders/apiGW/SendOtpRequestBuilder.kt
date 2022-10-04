package ru.generalName.teamName.tests.dataproviders.apiGW

import ru.generalName.teamName.tests.dataproviders.dataClasses.apiGTW.oauth.SendOtpRequest


class SendOtpRequestBuilder {

    private lateinit var mobile: String
    fun mobile(mobile: String) = apply {this.mobile = mobile}
    fun getMobile(): String {
        return mobile
    }

    fun build(): SendOtpRequest {
        return SendOtpRequest(
            mobile = mobile
        )
    }
}