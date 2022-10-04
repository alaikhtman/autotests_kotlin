package ru.samokat.mysamokat.tests.helpers.controllers.events

import com.fasterxml.jackson.annotation.JsonProperty
import ru.samokat.mysamokat.tests.helpers.controllers.events.employee.Dolzhnost
import ru.samokat.mysamokat.tests.helpers.controllers.events.employee.FizicheskoeLitso
import ru.samokat.mysamokat.tests.helpers.controllers.events.employee.Headers
import ru.samokat.mysamokat.tests.helpers.controllers.events.employee.JobFunction

data class VneshnieSotrudniki (
    val headers: Headers,
    val payload: List<VneshnieSotrudnikiPayload>
)

data class VneshnieSotrudnikiPayload(
    @JsonProperty("ObjectName")
    val objectName: String,
    @JsonProperty("GUID")
    val guid: String,
    @JsonProperty("Naimenovanie")
    val naimenovanie: String,
    @JsonProperty("Dolzhnost")
    val dolzhnost: Dolzhnost,
    @JsonProperty("VidDogovora")
    val vidDogovora: VidDogovora,
    @JsonProperty("DataUvolneniya")
    val dataUvolneniya: String?,
    @JsonProperty("DataOformleniya")
    val dataOformleniya: String,
    @JsonProperty("Partner")
    val partner: Partner,
    @JsonProperty("Telefon")
    val telefon: String?,
    @JsonProperty("FizicheskoeLitso")
    val fizicheskoeLitso: FizicheskoeLitso

)

data class VidDogovora(
    @JsonProperty("ObjectName")
    val objectName: String,
    @JsonProperty("GUID")
    val guid: String,
    @JsonProperty("Naimenovanie")
    val naimenovanie: String,
)

data class Partner(
    @JsonProperty("ObjectName")
    val objectName: String,
    @JsonProperty("GUID")
    val guid: String,
    @JsonProperty("Naimenovanie")
    val naimenovanie: String,
)

data class DataVneshnieSotrudniki(
    val mobile: String,
    val partner: PartnerData,
    val fullName: String,
    val jobFunction: JobFunction,
    val individualId: String,
    val employmentDate: String,
    val retirementDate: String?,
    val outsourceContractType: OutsourceContractType
)

data class PartnerData(
    val title: String,
    val partnerId: String
)

data class OutsourceContractType(
    val title: String,
    val outsourceContractTypeId: String
)
