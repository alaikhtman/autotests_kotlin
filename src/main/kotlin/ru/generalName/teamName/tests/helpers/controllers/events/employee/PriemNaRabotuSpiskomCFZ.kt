package ru.samokat.mysamokat.tests.helpers.controllers.events.employee

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

data class PriemNaRabotuSpiskomCFZ (
    val headers: Headers,
    val payload: List<PayloadList>
        )

    data class PayloadList(
        @JsonProperty("ObjectName")
        val objectName: String,
        @JsonProperty("Proveden")
        val proveden: Boolean,
        @JsonProperty("GUID")
        val guid: UUID,
        @JsonProperty("Sotrudniki")
        val sotrudniki: List<Sotrudniki>,
)

data class Sotrudniki(
    @JsonProperty("Sotrudnik")
    val sotrudnik: Sotrudnik,
    @JsonProperty("FizicheskoeLitso")
    val fizicheskoeLitso: FizicheskoeLitso,
    @JsonProperty("Dolzhnost")
    val dolzhnost: Dolzhnost,
    @JsonProperty("DataPriema")
    val dataPriema: String,
    @JsonProperty("DataZaversheniyaTrudovogoDogovora")
    val dataZaversheniyaTrudovogoDogovora: String
)