package ru.samokat.mysamokat.tests.helpers.controllers.database

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.stereotype.Service
import ru.samokat.mysamokat.tests.dataproviders.helpers.database.staff_metadata.UserMetadata
import java.util.*

@Service
class StaffMetadataDatabaseController(
    private val staffMetadataDatabase: Database
    ) {

    fun getMetadataById(userId: UUID): ResultRow {
        var comment: ResultRow? = null
        transaction (staffMetadataDatabase) {
            addLogger(StdOutSqlLogger)
            comment = UserMetadata.select { UserMetadata.userId eq userId }.single()
        }
        return comment!!
    }

    fun deleteUserMetadataById(userId: UUID) {
        transaction(staffMetadataDatabase) {
            addLogger(StdOutSqlLogger)
            UserMetadata.deleteWhere { UserMetadata.userId eq userId }
        }
    }

    fun checkMetadataExistById(userId: UUID): Boolean {
        var exists = false
        transaction(staffMetadataDatabase) {
            addLogger(StdOutSqlLogger)
            if (UserMetadata.select { UserMetadata.userId eq userId}.count() > 0) {
                exists = true
            }
        }
        return exists
    }

    fun setMetadataById(userId: UUID, commentary: String){
        transaction (staffMetadataDatabase) {
            addLogger(StdOutSqlLogger)
            UserMetadata.insert {
                it[UserMetadata.userId] = userId
                it[UserMetadata.commentary] = commentary}
        }
    }
}