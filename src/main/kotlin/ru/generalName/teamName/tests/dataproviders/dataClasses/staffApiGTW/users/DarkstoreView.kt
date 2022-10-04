package ru.samokat.mysamokat.tests.dataproviders.dataClasses.staffApiGTW.users

import java.time.ZoneId
import java.util.*

data class DarkstoreView(
    val id: UUID,
    val title: String,
    val timezone: ZoneId,
    val cityCode: String
)
