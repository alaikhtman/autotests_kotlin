package ru.samokat.mysamokat.tests.dataproviders.dataClasses.staffApiGTW.oauth

data class GetOAuthTokenRequest(

   val mobile: String,
   val otp: CharArray?,
   val password: CharArray?
)
