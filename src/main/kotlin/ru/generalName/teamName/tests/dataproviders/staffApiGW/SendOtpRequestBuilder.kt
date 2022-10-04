package ru.samokat.mysamokat.tests.dataproviders.staffApiGW

import ru.samokat.mysamokat.tests.dataproviders.dataClasses.staffApiGTW.oauth.SendOtpRequest


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