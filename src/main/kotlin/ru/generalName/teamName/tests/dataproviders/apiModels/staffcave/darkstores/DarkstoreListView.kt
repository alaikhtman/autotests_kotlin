package ru.samokat.mysamokat.tests.dataproviders.apiModels.staffcave

import ru.samokat.my.domain.Email
import java.time.ZoneId
import java.util.*


data class DarkstoreListView (
    val darkstores: List<DarkstoreView>
        )

data class DarkstoreView(
    val darkstoreId: UUID,
    val title: String,
    val email: Email,
    val timezone: ZoneId?,
    val cityCode: String
)
