package ru.samokat.mysamokat.tests.helpers.controllers.database

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.springframework.stereotype.Service
import ru.samokat.mysamokat.tests.dataproviders.helpers.database.employee_profiles_statistics.MonthlyShiftStatistics
import ru.samokat.mysamokat.tests.dataproviders.helpers.database.employee_profiles_statistics.ShiftStatistics
import ru.samokat.mysamokat.tests.dataproviders.helpers.database.employee_profiles_statistics.WeeklyShiftStatistics
import ru.samokat.mysamokat.tests.dataproviders.helpers.database.shifts_backend.Shift
import java.time.Instant
import java.util.*

@Service
class StatisticsDatabaseController ( private val statisticsDatabase: Database)
{
    fun updateWeeklyStatOrdersCount(userId: UUID, count: Int) {
        transaction(statisticsDatabase) {
            addLogger(StdOutSqlLogger)
            WeeklyShiftStatistics.update({ (WeeklyShiftStatistics.userId eq userId) }) {
                it[deliveredOrdersCount] = count
            }
        }
    }

    fun updateMonthlyStatOrdersCount(userId: UUID, count: Int) {
        transaction(statisticsDatabase) {
            addLogger(StdOutSqlLogger)
            MonthlyShiftStatistics.update({ (MonthlyShiftStatistics.userId eq userId) }) {
                it[deliveredOrdersCount] = count
            }
        }
    }

    fun updateShiftsOrdersCount(shiftId: UUID, count: Int) {
        transaction(statisticsDatabase) {
            addLogger(StdOutSqlLogger)
            ShiftStatistics.update({ (ShiftStatistics.shiftId eq shiftId) }) {
                it[deliveredOrdersCount] = count
            }
        }
    }

}