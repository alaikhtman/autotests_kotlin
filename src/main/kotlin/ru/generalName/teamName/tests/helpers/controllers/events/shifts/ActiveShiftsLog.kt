package ru.samokat.mysamokat.tests.helpers.controllers.events.shifts

import java.util.*

data class ActiveShiftsLog (
    val shiftId: UUID,
    val userId: UUID,
    val userRole: String,
    val userRoles: List<String>?,
    val darkstoreId: UUID,
    val status: String,
    val stopType: String?,
    val startedAt: String,
    val stoppedAt: String?
    )
