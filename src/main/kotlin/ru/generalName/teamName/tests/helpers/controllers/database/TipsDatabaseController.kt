package ru.samokat.mysamokat.tests.helpers.controllers.database

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.stereotype.Service
import ru.samokat.mysamokat.tests.dataproviders.helpers.database.tips.ChachachayTipsBalance
import ru.samokat.mysamokat.tests.dataproviders.helpers.database.tips.ChachachayWorker
import java.util.*

@Service
class TipsDatabaseController (
    private val tipsDatabase: Database
){


    fun getChachachayWorkerByProfileId(profileId: UUID): ResultRow {
        var chachachayWorker: ResultRow? = null
        transaction(tipsDatabase) {
            addLogger(StdOutSqlLogger)
            chachachayWorker = ChachachayWorker.select { ChachachayWorker.profileId eq profileId }.single()
        }
        return chachachayWorker!!
    }

    fun getChachachayTipsBalanceByProfileId(profileId: UUID): ResultRow {
        var chachachayTipsBalance: ResultRow? = null
        transaction(tipsDatabase) {
            addLogger(StdOutSqlLogger)
            chachachayTipsBalance = ChachachayTipsBalance.select { ChachachayTipsBalance.profileId eq profileId }.single()
        }
        return chachachayTipsBalance!!
    }

    fun checkChachachayWorkerExistsByProfileId(profileId: UUID): Boolean {
        var exists = false
        transaction(tipsDatabase) {
            addLogger(StdOutSqlLogger)
            if (ChachachayWorker.select { ChachachayWorker.profileId eq profileId }.count() > 0) {
                exists = true
            }
        }
        return exists
    }

    fun checkChachachayWorkerExistsByMobile(mobile: String): Boolean {
        var exists = false
        transaction(tipsDatabase) {
            addLogger(StdOutSqlLogger)
            if (ChachachayWorker.select { ChachachayWorker.mobile eq mobile }.count() > 0) {
                exists = true
            }
        }
        return exists
    }

    fun deleteProfileFromTipsByMobile(mobile: String) {
        transaction(tipsDatabase) {
            addLogger(StdOutSqlLogger)
            ChachachayWorker.deleteWhere { ChachachayWorker.mobile eq mobile }
        }
    }
}