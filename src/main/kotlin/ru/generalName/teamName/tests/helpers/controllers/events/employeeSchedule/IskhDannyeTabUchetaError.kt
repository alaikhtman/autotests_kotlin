package ru.samokat.mysamokat.tests.helpers.controllers.events.employeeSchedule

import com.fasterxml.jackson.annotation.JsonProperty
import ru.samokat.mysamokat.tests.helpers.controllers.events.employee.Headers
import java.util.*

data class  IskhDannyeTabUchetaError (
    val headers: Headers ,
    val payload: List<ErrorPayload>

    )



data class ErrorPayload(
    @JsonProperty("Error")
    val error: Boolean,
    @JsonProperty("ErrorText")
    val errorText: String?

)
