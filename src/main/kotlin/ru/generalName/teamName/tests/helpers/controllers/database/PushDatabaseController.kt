package ru.samokat.mysamokat.tests.helpers.controllers.database

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.stereotype.Service
import ru.samokat.mysamokat.tests.dataproviders.helpers.database.employee_profiles_backend.Task
import ru.samokat.mysamokat.tests.dataproviders.helpers.database.push.Tokens
import ru.samokat.mysamokat.tests.dataproviders.helpers.database.shifts_backend.ShiftsAssignment
import java.util.*

@Service
class PushDatabaseController(
    private val pushDatabase: Database
) {

    fun getToken(token: UUID): ResultRow {
        var tokenRow: ResultRow? = null
        transaction(pushDatabase) {
            addLogger(StdOutSqlLogger)
            tokenRow = Tokens.select { Tokens.token eq token.toString() }.single()
        }
        return tokenRow!!
    }

    fun checkTokenExists(token: UUID): Boolean {
        var exists = false
        transaction(pushDatabase) {
            addLogger(StdOutSqlLogger)
            if (Tokens.select { Tokens.token eq token.toString() }.count() > 0) {
                exists = true
            }
        }
        return exists
    }
}