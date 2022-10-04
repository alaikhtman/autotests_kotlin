package ru.samokat.mysamokat.tests.helpers.controllers.events

import com.fasterxml.jackson.annotation.JsonProperty
import ru.samokat.mysamokat.tests.helpers.controllers.events.employee.Dolzhnost
import ru.samokat.mysamokat.tests.helpers.controllers.events.employee.FizicheskoeLitso
import ru.samokat.mysamokat.tests.helpers.controllers.events.employee.Headers
import ru.samokat.mysamokat.tests.helpers.controllers.events.employee.Sotrudnik
import java.util.*

data class KadrovyyPerevodSpiskomCFZ (
    val headers: Headers,
    val payload: List<PayloadPerevodList>
)
data class PayloadPerevodList(
    @JsonProperty("ObjectName")
    val objectName: String,
    @JsonProperty("Proveden")
    val proveden: Boolean,
    @JsonProperty("GUID")
    val guid: UUID,
    @JsonProperty("Sotrudniki")
    val sotrudniki: List<SotrudnikiPerevod>,
)

data class SotrudnikiPerevod(
    @JsonProperty("Sotrudnik")
    val sotrudnik: Sotrudnik,
    @JsonProperty("FizicheskoeLitso")
    val fizicheskoeLitso: FizicheskoeLitso,
    @JsonProperty("Dolzhnost")
    val dolzhnost: Dolzhnost,
    @JsonProperty("DataNachala")
    val dataNachala: String,
    @JsonProperty("DataOkonchaniya")
    val dataOkonchaniya: String
)

