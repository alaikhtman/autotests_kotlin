package ru.samokat.mysamokat.tests.helpers.controllers.events.employee

import java.util.*

data class EmployeeProfilesProfileChanged(
    val profileId: UUID,
    val version: Int,
    val snapshot: EmployeeProfileLog,
    val changes: Changes
)

data class Changes(
    val mobile: StringUpdate?,
    val name: NameUpdate?,
    val roles: RolesUpdate?,
    val darkstoreId: UUIDUpdate?,
    val supervisedDarkstores: SupervisedDarkstoresUpdate?,
    val staffPartnerId: UUIDUpdate?,
    val vehicle: VehicleUpdate?,
    val test: TestUpdate?,
    val email: StringUpdate?,
    val accountingProfileId: StringUpdate?,
    val cityId: UUIDUpdate?
)

data class StringUpdate(
    val type: String,
    val oldValue: String?,
    val newValue: String?
)

data class UUIDUpdate(
    val type: String,
    val oldValue: UUID?,
    val newValue: UUID?
)

data class NameUpdate(
    val type: String,
    val oldValue: Name,
    val newValue: Name?
)

data class RolesUpdate(
    val type: String,
    val oldValue: List<String>,
    val newValue: Collection<String>,
)

data class SupervisedDarkstoresUpdate(
    val type: String,
    val oldValue: List<UUID>?,
    val newValue: List<UUID>?,
)

data class VehicleUpdate(
    val type: String,
    val oldValue: Vehicle?,
    val newValue: Vehicle?
)

data class TestUpdate(
    val type: String,
    val oldValue: Boolean,
    val newValue: Boolean?
)
