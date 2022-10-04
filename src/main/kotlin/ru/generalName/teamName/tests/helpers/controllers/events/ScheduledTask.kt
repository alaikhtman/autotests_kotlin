package ru.samokat.mysamokat.tests.helpers.controllers.events

import ru.samokat.mysamokat.tests.helpers.controllers.events.employee.Data

data class ScheduledTask(
    val contract: Contract,
    val endDateTime: String?)

data class Contract(
    val accountingProfileId: String,
    val accountingContractId: String,
    val data: Data,
    val lastModifiedAt: String,
    val type: String

)
