package ru.samokat.mysamokat.tests.dataproviders.helpers.database.shifts_backend

import com.impossibl.postgres.jdbc.PGBuffersArray
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ColumnType
import org.jetbrains.exposed.sql.Table

object ShiftsAssignment: Table(name = "shift_assignment") {
    val id = integer("id")
    val assignmentId = uuid("assignment_id")
    val userId = uuid("user_id")
    val darkstoreId = uuid("darkstore_id")
    val status = varchar("status", 15)
    val created_at = varchar("created_at", 50)
    val updatedAt = varchar("updated_at", 50)
    val version = integer("version")
    val cancellationReason = varchar("cancellation_reason", 50)
    val user_roles = shiftsRoleArray("user_roles")
}

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
