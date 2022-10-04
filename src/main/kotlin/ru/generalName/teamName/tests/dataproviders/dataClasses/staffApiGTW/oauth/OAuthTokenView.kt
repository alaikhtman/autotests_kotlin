package ru.samokat.mysamokat.tests.dataproviders.dataClasses.staffApiGTW.oauth

import java.time.Duration

data class OAuthTokenView(

    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Duration

)
