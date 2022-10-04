package ru.samokat.mysamokat.tests.helpers.controllers.events.employee

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

data class PriemNaRabotuCFZ(
    val headers: Headers,
    val payload: List<Payload>
)

data class Headers(
    @JsonProperty("Id")
    var id: UUID,
    @JsonProperty("Date")
    val date: String?,
    @JsonProperty("Event")
    val event: String?,
    @JsonProperty("DateInMilliseconds")
    var dateInMilliseconds: Long?,
    @JsonProperty("MultithreadingAnalytics")
    val multithreadingAnalytics: String?,
    @JsonProperty("MultithreadingDate")
    val multithreadingDate: String?,
    @JsonProperty("SingleThreaded")
    val singleThreaded: Boolean?,
    @JsonProperty("AddressForResult")
    val addressForResult: String?
)

data class Payload(
    @JsonProperty("ObjectName")
    val objectName: String,
    @JsonProperty("Proveden")
    var proveden: Boolean,
    @JsonProperty("GUID")
    val guid: UUID,
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

data class Sotrudnik(
    @JsonProperty("ObjectName")
    val objectName: String?,
    @JsonProperty("GUID")
    val guid: UUID
)

data class FizicheskoeLitso(
    @JsonProperty("ObjectName")
    val objectName: String?,
    @JsonProperty("GUID")
    val guid: UUID,
    @JsonProperty("INN")
    val inn: String?,
    @JsonProperty("Naimenovanie")
    val naimenovanie: String
)

data class Dolzhnost(
    @JsonProperty("ObjectName")
    val objectName: String?,
    @JsonProperty("GUID")
    val guid : UUID,
    @JsonProperty("Naimenovanie")
    val naimenovanie: String
)

data class Data(
    val individualId: String,
    val approved: Boolean,
    val fullName: String,
    val jobFunction: JobFunction,
    val employmentDate: String
)

data class JobFunction(
    val title: String,
    val jobFunctionId: String
)
