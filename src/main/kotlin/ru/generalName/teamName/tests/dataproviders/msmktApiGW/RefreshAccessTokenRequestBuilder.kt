package ru.samokat.mysamokat.tests.dataproviders.msmktApiGW

import ru.samokat.mysamokat.tests.dataproviders.apiModels.staffcave.oauth.RefreshAccessTokenRequest


class RefreshAccessTokenRequestBuilder {

    private lateinit var refreshToken: String
    fun refreshToken(refreshToken: String) = apply {this.refreshToken = refreshToken}
    fun getRefreshToken(): String {
        return refreshToken
    }

    fun build(): RefreshAccessTokenRequest {
        return RefreshAccessTokenRequest(
            refreshToken = refreshToken
        )
    }
}