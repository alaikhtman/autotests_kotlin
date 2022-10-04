package ru.samokat.mysamokat.tests.dataproviders.helpers.database.staff_metadata

import org.jetbrains.exposed.sql.Table

object UserMetadata : Table(name = "user_metadata") {
    val id = integer("id")
    val userId = uuid("user_id")
    val commentary = text("commentary")
}
