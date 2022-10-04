package ru.samokat.mysamokat.tests.helpers.controllers.events

import com.fasterxml.jackson.annotation.JsonProperty
import ru.samokat.mysamokat.tests.helpers.controllers.events.employee.Dolzhnost
import ru.samokat.mysamokat.tests.helpers.controllers.events.employee.FizicheskoeLitso
import ru.samokat.mysamokat.tests.helpers.controllers.events.employee.Headers
import ru.samokat.mysamokat.tests.helpers.controllers.events.employee.Sotrudnik
import java.util.*

data class KadrovyyPerevodCFZ (
    val headers: Headers,
    val payload: List<PayloadPerevod>
)

data class PayloadPerevod(
    @JsonProperty("ObjectName")
    val objectName: String?,
    @JsonProperty("Proveden")
    val proveden: Boolean?,
    @JsonProperty("GUID")
    val guid: UUID?,
    @JsonProperty("Sotrudnik")
    val sotrudnik: Sotrudnik?,
    @JsonProperty("FizicheskoeLitso")
    val fizicheskoeLitso: FizicheskoeLitso?,
    @JsonProperty("Dolzhnost")
    val dolzhnost: Dolzhnost?,
    @JsonProperty("DataNachala")
    val dataNachala: String?,
    @JsonProperty("DataOkonchaniya")
    val dataOkonchaniya: String?

)
