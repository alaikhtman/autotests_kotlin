package ru.samokat.mysamokat.tests.dataproviders.helpers.database.shifts_backend

import com.impossibl.postgres.jdbc.PGBuffersArray
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ColumnType
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.`java-time`.timestamp

object Shift : Table(name = "shift") {
    val id = integer("id")
    val shiftId = uuid("shift_id")
    val userId = uuid("user_id")
    val darkstoreId = uuid("darkstore_id")
    val startedAt = timestamp("started_at")
    val userRoles = shiftsRoleArray("user_roles")
    val userRole = varchar("user_role", 15)
    val deliveryMethod = varchar("delivery_method", 15)

    fun Table.shiftsRoleArray(name: String): Column<List<String>> = registerColumn(name, shiftsRoleArrayColumnType())

    private class shiftsRoleArrayColumnType : ColumnType() {
        override fun sqlType() = "shift_user_role[]"
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

}