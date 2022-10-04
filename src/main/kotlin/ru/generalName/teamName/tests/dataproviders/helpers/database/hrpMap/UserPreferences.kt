package ru.samokat.mysamokat.tests.dataproviders.helpers.database.hrpMap

import org.jetbrains.exposed.sql.Table

object UserPreferences: Table(name = "user_preferences") {
    val id = integer("id")
    val userId = uuid("user_id")
    val preferredCities = text("preferred_cities")
}