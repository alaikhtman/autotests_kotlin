package ru.samokat.logistics.tests.dataproviders.employee

import ru.samokat.employeeprofiles.api.profiles.authenticate.AuthenticateProfileRequest
import ru.samokat.employeeprofiles.api.profiles.changepassword.ChangePasswordRequest
import ru.samokat.my.domain.PhoneNumber
import java.util.*

class AuthenticateProfileRequestBuilder {

    private lateinit var mobile: PhoneNumber

    fun mobile(mobile: PhoneNumber) = apply {this.mobile = mobile}
    fun getMobile(): PhoneNumber {
        return mobile
    }

    private lateinit var password: CharArray
    fun password(password: CharArray) = apply {this.password = password}
    fun getPassword(): CharArray {
        return password
    }

    fun build(): AuthenticateProfileRequest {
        return AuthenticateProfileRequest(
            mobile = mobile,
            password = password
        )
    }

}
