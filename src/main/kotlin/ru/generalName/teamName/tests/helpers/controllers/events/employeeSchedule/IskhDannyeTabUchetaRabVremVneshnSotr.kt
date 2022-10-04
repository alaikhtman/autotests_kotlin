package ru.samokat.mysamokat.tests.helpers.controllers.events.employeeSchedule

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

data class IskhDannyeTabUchetaRabVremVneshnSotr(
    val headers: Headers,
    val payload: List<Payload>
)

data class Headers(
    @JsonProperty("Id")
    val id: UUID,
    @JsonProperty("Date")
    val date: String?,
    @JsonProperty("Event")
    val event: String?,
    @JsonProperty("DateInMilliseconds")
    val dateInMilliseconds: Long?
)

data class Payload(
    @JsonProperty("ObjectName")
    val objectName: String,
    @JsonProperty("Podrazdelenie")
    val podrazdelenie: Podrazdelenie,
    @JsonProperty("Sotrudnik")
    val sotrudnik: Sotrudnik,
    @JsonProperty("PeriodRegistratsii")
    val periodRegistrasii: String,
    @JsonProperty("Chasy")
    val chasy: Int

)

data class Podrazdelenie(
    @JsonProperty("ObjectName")
    val objectName: String,
    @JsonProperty("GUID")
    val guid: String,
    @JsonProperty("Naimenovanie")
    val naimenovanie: String
)

data class Sotrudnik(
    @JsonProperty("ObjectName")
    val objectName: String,
    @JsonProperty("GUID")
    val guid: String,
    @JsonProperty("Naimenovanie")
    val naimenovanie: String?
)

