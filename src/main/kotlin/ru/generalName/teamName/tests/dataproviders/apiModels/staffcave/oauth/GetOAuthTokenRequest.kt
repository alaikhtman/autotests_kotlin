package ru.samokat.mysamokat.tests.dataproviders.apiModels.staffcave.oauth

import ru.samokat.my.domain.PhoneNumber

data class GetOAuthTokenRequest (
    val mobile: PhoneNumber,
    val otp: CharArray?,
    val password: CharArray?
    )