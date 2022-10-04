package ru.samokat.mysamokat.tests.dataproviders.helpers.database

import org.jetbrains.exposed.sql.Table

object DarkstoreUser : Table(name = "darkstore_user") {
    val id = integer("id")
    val profileId = uuid("profile_id")
    val darkstoreId = uuid("darkstore_id")
    val role = varchar("role", 255)
    val state = integer("state")
    val version = integer("version")
    val modifiedAt = varchar("modified_at", 255)

}
