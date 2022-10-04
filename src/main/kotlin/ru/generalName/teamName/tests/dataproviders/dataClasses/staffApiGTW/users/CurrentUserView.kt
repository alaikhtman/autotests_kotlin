package ru.samokat.mysamokat.tests.dataproviders.dataClasses.staffApiGTW.users

import ru.samokat.my.rest.api.enum.ApiEnum
import ru.samokat.mysamokat.tests.dataproviders.dataClasses.staffApiGTW.enum.*
import java.util.*

data class CurrentUserView(
    val id: UUID,
    val mobile: String,
    val name: ProfileName,
    val darkstore: DarkstoreView?,
    val roles: List<ApiEnum<CurrentUserRole, String>>,
    val supervisedDarkstores: List<DarkstoreView>?
)
