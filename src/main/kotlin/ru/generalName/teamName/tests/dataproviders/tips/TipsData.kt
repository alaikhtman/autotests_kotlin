package ru.samokat.mysamokat.tests.dataproviders.tips

import com.fasterxml.jackson.annotation.JsonProperty

data class TipsData (
    @JsonProperty("client_phone")
    val clientPhone: String,

    @JsonProperty("tips_list")
    val tipsList:  List<TipsList>,
        )

data class TipsList(
    val phone: String,
    val percent: Int
)