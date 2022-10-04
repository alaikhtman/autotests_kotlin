package ru.generalName.teamName.tests.dataproviders.apiGW

import ru.generalName.teamName.tests.dataproviders.dataClasses.apiGTW.oauth.GetOAuthTokenRequest


class GetOAuthTokenRequestBuilder {

    private lateinit var mobile: String
    fun mobile(mobile: String) = apply {this.mobile = mobile}
    fun getMobile(): String {
        return mobile
    }

    private var password: CharArray? = null
    fun password(password: CharArray) = apply {this.password = password}
    fun getPassword(): CharArray? {
        return password
    }

    private var otp: CharArray? = null
    fun otp(otp: CharArray) = apply {this.otp = otp}
    fun getOtp(): CharArray? {
        return otp
    }

    fun build(): GetOAuthTokenRequest {
        return GetOAuthTokenRequest(
            mobile = mobile,
            password = password,
            otp = otp
        )
    }
}