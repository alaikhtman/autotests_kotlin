package ru.samokat.mysamokat.tests.dataproviders.msmktApiGW

import ru.samokat.employeeprofiles.api.profiles.authenticate.AuthenticateProfileRequest
import ru.samokat.my.domain.PhoneNumber
import ru.samokat.mysamokat.apigateway.api.oauth.token.GetOAuthTokenRequest

class GetOAuthTokenRequestBuilder {

    private lateinit var mobile: PhoneNumber
    fun mobile(mobile: PhoneNumber) = apply {this.mobile = mobile}
    fun getMobile(): PhoneNumber {
        return mobile
    }

    private var otp: CharArray? = null
    fun otp(otp: CharArray) = apply {this.otp = otp}
    fun getOtp(): CharArray? {
        return otp
    }

    private var password: CharArray? = null
    fun password(password: CharArray) = apply {this.password = password}
    fun getPassword(): CharArray? {
        return password
    }

    private lateinit var deviceId: String
    fun deviceId(deviceId: String) = apply {this.deviceId = deviceId}
    fun getDeviceId(): String {
        return deviceId
    }

    fun build(): GetOAuthTokenRequest {
        return GetOAuthTokenRequest(
            mobile = mobile,
            password = password,
            otp = otp,
            deviceId = deviceId
        )
    }
}