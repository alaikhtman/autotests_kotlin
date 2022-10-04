package ru.generalName.teamName.tests.dataproviders

import java.sql.Timestamp

data class SendConfirmationCodeRequest(val phoneNumber: String)

data class ConfirmationCodeView(val retryTimeout: Int)

data class SmscStubMessage(
    val messageDateTime: Timestamp,
    val phone: String,
    val text: String,
    val id: String,
    val provider: String
)

