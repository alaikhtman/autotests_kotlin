package ru.samokat.mysamokat.tests.dataproviders.helpers.database.employee_profiles_backend

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ColumnType
import org.jetbrains.exposed.sql.Table
import java.util.logging.Level.parse


object Contract : Table(name = "contract") {
    val id = integer("id")
    val accountingContractId = varchar("accounting_contract_id", 50)
    val accountingProfileId = varchar("accounting_profile_id", 50)
    val data = text ("data")
    val modifiedAt = varchar("modified_at", 255)
}


