package ru.samokat.mysamokat.tests.helpers.controllers.database

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.stereotype.Service
import ru.samokat.mysamokat.tests.dataproviders.helpers.database.hrpMap.Cities
import ru.samokat.mysamokat.tests.dataproviders.helpers.database.hrpMap.CounterpartyPreferences
import ru.samokat.mysamokat.tests.dataproviders.helpers.database.hrpMap.Darkstores
import ru.samokat.mysamokat.tests.dataproviders.helpers.database.hrpMap.UserPreferences
import java.math.BigDecimal
import java.time.Instant
import java.util.*

@Service
class HrpMapDatabaseController (
    private val hrpMapDatabase: Database
){

    fun getCounterpartyPreferencesByCounterpartyId(counterpartyId: UUID): ResultRow {
        var counterpartyPreferences: ResultRow? = null
        transaction(hrpMapDatabase) {
            addLogger(StdOutSqlLogger)
            counterpartyPreferences = CounterpartyPreferences.select { CounterpartyPreferences.counterpartyId eq counterpartyId }.single()
        }
        return counterpartyPreferences!!
    }

    fun getCityById(cityId: UUID):ResultRow{
        var city: ResultRow? = null
        transaction(hrpMapDatabase) {
            addLogger(StdOutSqlLogger)
            city = Cities.select { Cities.cityId eq cityId }.single()
        }
        return city!!
    }

    fun createCity(
        cityIdParam: UUID,
        nameParam: String,
        latParam: BigDecimal,
        lonParam: BigDecimal
    ){
        transaction(hrpMapDatabase) {
            addLogger(StdOutSqlLogger)
            Cities.insert {
                it[cityId] = cityIdParam
                it[name] = nameParam
                it[lat] = latParam
                it[lon] = lonParam
                it[lastSyncronizedAt] = Instant.now()
            }
        }
    }

    fun createCityWithoutCoordinates(
        cityIdParam: UUID,
        nameParam: String
    ){
        transaction(hrpMapDatabase) {
            addLogger(StdOutSqlLogger)
            Cities.insert {
                it[cityId] = cityIdParam
                it[name] = nameParam
                it[lastSyncronizedAt] = Instant.now()
            }
        }
    }

    fun deleteCityById(cityId: UUID){
        transaction(hrpMapDatabase) {
            addLogger(StdOutSqlLogger)
            Cities.deleteWhere { Cities.cityId eq cityId }
        }
    }

    fun getDarkstoreById(darkstoreId: UUID):ResultRow{
        var darkstore: ResultRow? = null
        transaction(hrpMapDatabase) {
            addLogger(StdOutSqlLogger)
            darkstore = Darkstores.select { Darkstores.darkstoreId eq darkstoreId }.single()
        }
        return darkstore!!
    }

    fun createDarkstore(
        darkstoreIdParam: UUID,
        nameParam: String,
        typeParam: String,
        statusParam: String,
        addressParam: String,
        cityIdParam: UUID,
        latParam: BigDecimal,
        lonParam: BigDecimal,
        emailParam: String,
        phoneParam: String,
        districtParam: String
    ){
        transaction(hrpMapDatabase) {
            addLogger(StdOutSqlLogger)
            Darkstores.insert {
                it[darkstoreId] = darkstoreIdParam
                it[name] = nameParam
                it[type] = typeParam
                it[status] = statusParam
                it[address] = addressParam
                it[cityId] = cityIdParam
                it[lat] = latParam
                it[lon] = lonParam
                it[email] = emailParam
                it[phone] = phoneParam
                it[district] = districtParam
                it[startedToOperate] = Instant.now()
                //it[finishedToOperate] = Instant.now().plusSeconds(86400)
                it[lastSyncronizedAt] = Instant.now()
                it[subwayStations] = "[]"

            }
        }

    }

    fun createDarkstoreWithEndDate(
        darkstoreIdParam: UUID,
        nameParam: String,
        typeParam: String,
        statusParam: String,
        addressParam: String,
        cityIdParam: UUID,
        latParam: BigDecimal,
        lonParam: BigDecimal,
        emailParam: String,
        phoneParam: String,
        districtParam: String
    ){
        transaction(hrpMapDatabase) {
            addLogger(StdOutSqlLogger)
            Darkstores.insert {
                it[darkstoreId] = darkstoreIdParam
                it[name] = nameParam
                it[type] = typeParam
                it[status] = statusParam
                it[address] = addressParam
                it[cityId] = cityIdParam
                it[lat] = latParam
                it[lon] = lonParam
                it[email] = emailParam
                it[phone] = phoneParam
                it[district] = districtParam
                it[startedToOperate] = Instant.now()
                it[finishedToOperate] = Instant.now().plusSeconds(86400)
                it[lastSyncronizedAt] = Instant.now()
                it[subwayStations] = "[]"

            }
        }

    }

    fun deleteDarkstoreById(darkstoreId: UUID){
        transaction(hrpMapDatabase) {
            addLogger(StdOutSqlLogger)
            Darkstores.deleteWhere { Darkstores.darkstoreId eq darkstoreId }
        }
    }

    fun getUserPreferencesByUserId(userId: UUID): ResultRow {
        var userPreferences: ResultRow? = null
        transaction(hrpMapDatabase) {
            addLogger(StdOutSqlLogger)
            userPreferences = UserPreferences.select { UserPreferences.userId eq userId }.single()
        }
        return userPreferences!!
    }

    fun getUserPreferencesExistenceByUserId(userId: UUID): Boolean {
        var exists = false
        transaction(hrpMapDatabase) {
            addLogger(StdOutSqlLogger)
            if (UserPreferences.select { (UserPreferences.userId eq userId) }
                    .count() > 0) {
                exists = true
            }
        }
        return exists
    }
}