package ru.samokat.mysamokat.tests.dataproviders.msmktApiGW

import ru.samokat.my.domain.PhoneNumber
import ru.samokat.mysamokat.apigateway.api.oauth.otp.SendOtpRequest
import ru.samokat.mysamokat.apigateway.api.oauth.token.GetOAuthTokenRequest

class SendOtpRequestBuilder {

    private lateinit var mobile: PhoneNumber
    fun mobile(mobile: PhoneNumber) = apply {this.mobile = mobile}
    fun getMobile(): PhoneNumber {
        return mobile
    }

    fun build(): SendOtpRequest {
        return SendOtpRequest(
            mobile = mobile
        )
    }
}