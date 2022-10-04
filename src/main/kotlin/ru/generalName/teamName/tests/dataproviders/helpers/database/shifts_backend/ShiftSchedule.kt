package ru.samokat.mysamokat.tests.dataproviders.helpers.database.shifts_backend

import com.impossibl.postgres.jdbc.PGBuffersArray
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ColumnType
import org.jetbrains.exposed.sql.Table

object ShiftSchedule: Table(name = "shift_schedule") {
    val id = integer("id")
    val userId = uuid("user_id")
    val timeSlot = text("time_slot")
}

fun Table.timerange(name: String): Column<List<String>> = registerColumn(name, TimeRangeColumnType())

private class TimeRangeColumnType : ColumnType() {
    override fun sqlType() = "tstzrange"
    override fun valueFromDB(value: Any): List<String> {
        return if (value is PGBuffersArray) {
            val array = value.array
            if (array is Array<*>) {
                array.map {
                    it as String
                }
            } else {
                throw Exception("Values returned from database is not of type kotlin Array<*>")
            }
        } else
            throw Exception("Values returned from database is not of type PgArray")
    }
}
