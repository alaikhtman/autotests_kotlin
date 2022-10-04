package ru.generalName.teamName.tests.dataproviders.dataClasses.apiGTW.oauth

data class GetOAuthTokenRequest(

   val mobile: String,
   val otp: CharArray?,
   val password: CharArray?
)
