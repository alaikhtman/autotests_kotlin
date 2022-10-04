package ru.samokat.mysamokat.tests.dataproviders.helpers.database.hrpMap

import org.jetbrains.exposed.sql.Table

object CounterpartyPreferences : Table(name = "counterparty_preferences") {
    val id = integer("id")
    val counterpartyId = uuid("counterparty_id")
    val authorizedCities = text("authorized_cities")
}