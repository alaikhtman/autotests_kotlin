package ru.samokat.mysamokat.tests.dataproviders.helpers.database.employee_profiles_backend

import org.jetbrains.exposed.sql.Table

object DarkstoreUserActivity : Table(name = "darkstore_user_activity") {
    val id = integer("id")
    val profileId = uuid("profile_id")
    val darkstoreId = uuid("darkstore_id")
    val role = varchar("role", 255)
    val status = text("status")
    val activeUntil = varchar("active_until", 255)

}
