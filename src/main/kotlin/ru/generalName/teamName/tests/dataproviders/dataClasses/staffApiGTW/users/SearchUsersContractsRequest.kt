package ru.samokat.mysamokat.tests.dataproviders.dataClasses.staffApiGTW.users

import java.time.Instant
import java.util.*

data class SearchUsersContractsRequest(

    val userIds: List<UUID>,
    val activeUntil: Instant?

)
