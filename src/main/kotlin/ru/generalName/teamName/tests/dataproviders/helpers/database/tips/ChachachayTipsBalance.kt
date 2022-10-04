package ru.samokat.mysamokat.tests.dataproviders.helpers.database.tips

import org.jetbrains.exposed.sql.Table

object ChachachayTipsBalance: Table(name = "chachachay_tips_balance") {
    val id = integer("id")
    val profileId = uuid("profileId")
    val registrationStatus = varchar("registrations_status", 25)
    val balance = integer("balance")
    val nextPickedAt = varchar("next_picked_at", 50)
    val updatedAt = varchar("updated_at", 50)
}