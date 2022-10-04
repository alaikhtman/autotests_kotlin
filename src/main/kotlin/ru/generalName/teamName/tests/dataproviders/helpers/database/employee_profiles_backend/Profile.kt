package ru.samokat.mysamokat.tests.dataproviders.helpers.database.employee_profiles_backend


import com.impossibl.postgres.jdbc.PGBuffersArray
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ColumnType
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.`java-time`.timestamp

object Profile : IntIdTable(name = "profile") {

    val profileId = uuid("profile_id")
    val mobile = varchar("mobile", 15)
    val firstName = varchar("first_name", 80)
    val lastName = varchar("last_name", 80)
    val middleName = varchar("middle_name", 80)
    val roles = uuidArray("roles")
    val darkstoreId = uuid("darkstore_id")
    val status = varchar("status", 15)
    val version = long("version")
    val createdAt = timestamp("created_at")
    val updatedAt = timestamp("updated_at")
    val partnerId = uuid("partner_id")
    val passwordHash = varchar("password_hash", 256)
    val test = bool("test")
    val fullName = varchar("full_name", 256)
    val email = varchar ("email", 256)
    val accountingProfileId = varchar("accounting_profile_id", 40)
    val firstLoginAt = timestamp("first_login_at")
    val cityId = uuid("city_id")
}

fun Table.uuidArray(name: String): Column<List<String>> = registerColumn(name, UUIDArrayColumnType())

private class UUIDArrayColumnType : ColumnType() {
    override fun sqlType() = "profile_role[]"
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
