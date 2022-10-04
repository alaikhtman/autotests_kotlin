package ru.samokat.mysamokat.tests.dataproviders.helpers.database.hrpMap

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.`java-time`.timestamp

object Cities : Table(name = "cities") {
    val id = integer("id")
    val cityId = uuid("city_id")
    val name = text("name")
    val lat = decimal("lat", 8, 6)
    val lon = decimal("lon", 8, 6)
    val lastSyncronizedAt = timestamp("last_synchronized_at")
    val externalId = text("external_id")
}