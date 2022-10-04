package ru.generalName.teamName.tests.dataproviders.helpers.database.profiles_backend

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.`java-time`.datetime

object InternshipLog : Table(name = "internship_log") {
    val id = integer("id")
    val profileId = uuid("profile_id")
    val role = varchar("role", 255)
    val issuerId = uuid("issuer")
    val data = datetime("data")
    val type = text("type")
    val createdAt = varchar("created_at", 255)
    val version = integer("version")

}