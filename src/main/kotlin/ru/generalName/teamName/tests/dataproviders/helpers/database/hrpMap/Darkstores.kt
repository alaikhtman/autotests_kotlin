package ru.samokat.mysamokat.tests.dataproviders.helpers.database.hrpMap

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.`java-time`.timestamp

object Darkstores : Table(name = "darkstores") {
    val id = integer("id")
    val darkstoreId = uuid("darkstore_id")
    val name = text("name")
    val type = text("type")
    val status = text("status")
    val address = text("address")
    val cityId = uuid("city_id")
    val lat = decimal("lat", 8, 6)
    val lon = decimal("lon", 8, 6)
    val email = text("email")
    val phone = text("phone")
    val routeDescription = text("route_description")
    val district = text("district")
    val subwayStations = text("subway_stations")
    val lastSyncronizedAt = timestamp("last_synchronized_at")
    val startedToOperate = timestamp("started_to_operate")
    val finishedToOperate = timestamp("finished_to_operate")
}