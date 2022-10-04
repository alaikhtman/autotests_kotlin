package ru.generalName.teamName.tests.dataproviders.dataClasses.apiGTW.oauth

import java.time.Duration

data class OAuthTokenView(

    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Duration

)
