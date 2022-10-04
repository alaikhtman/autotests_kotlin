package ru.samokat.mysamokat.tests.helpers.controllers.events.employee

import java.util.*

data class EmployeeProfileLog(
    val profileId: UUID?,
    val mobile: String,
    val darkstoreId: UUID?,
    val name: Name,
    val roles: List<String>,
    val staffPartnerId: UUID?,
    val vehicle: Vehicle?,
    val status: String?,
    val passwordChanged: Boolean,
    val test: Boolean,
    val version: Int,
    val supervisedDarkstores: List<UUID>?,
    val email: String?,
    val hasPassword: Boolean?,
    val accountingProfileId: String?,
    val cityId: UUID?
)

data class Vehicle(
    val type: String
)

data class Name(
    val firstName: String,
    val middleName: String?,
    val lastName: String
)
