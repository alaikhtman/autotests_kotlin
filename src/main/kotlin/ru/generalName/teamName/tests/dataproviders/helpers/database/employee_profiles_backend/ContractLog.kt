package ru.samokat.mysamokat.tests.dataproviders.helpers.database.employee_profiles_backend

import org.jetbrains.exposed.sql.Table

object ContractLog  : Table(name = "contract_log") {
    val id = integer("id")
    val accountingContractId = varchar("accounting_contract_id", 50)
    val type = varchar("type", 15)
    val data = text("data")
    val modifiedAt = varchar("modified_at", 255)
    val createdAt = varchar("created_at", 255)
}
