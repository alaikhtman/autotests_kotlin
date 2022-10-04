package ru.samokat.mysamokat.tests.dataproviders.helpers.database.tips

import org.jetbrains.exposed.sql.Table

object ChachachayWorker: Table(name = "chachachay_worker") {
    val id = integer("id")
    val mobile = varchar("mobile", 11)
    val firstName = varchar("first_name", 30)
    val lastName = varchar("last_name", 30)
    val middleName = varchar("middle_name", 30)
    val chachachayWorkerId = integer("chachachay_worker_id")
    val status = varchar("status", 15)
    val createdAt = varchar("created_at", 50)
    val updatedAt = varchar("updated_at", 50)
    val profileId = uuid("profile_id")
}