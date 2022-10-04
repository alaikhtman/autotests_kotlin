package ru.generalName.teamName.tests.dataproviders.dataClasses.apiGTW.users


import ru.generalName.teamName.tests.dataproviders.dataClasses.apiGTW.enum.UserRole
import java.util.*

data class CurrentUserView(
    val id: UUID,
    val mobile: String,
    val name: ProfileName,
    val darkstore: DarkstoreView?,
    val roles: List<ApiEnum<UserRole, String>>,
    val supervisedDarkstores: List<DarkstoreView>?
)
