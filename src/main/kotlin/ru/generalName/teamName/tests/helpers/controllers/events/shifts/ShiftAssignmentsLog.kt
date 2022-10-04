package ru.samokat.mysamokat.tests.helpers.controllers.events.shifts

data class ShiftAssignmentsLog (
    val assignmentId: String,
    val userId: String,
    val userRole: String,
    val darkstoreId: String,
    val timeRange: TimeRangeEvent,
    val status: String,
    val edited: Boolean,
    val version: Int,
    val cancellationReason: String?
    )

data class TimeRangeEvent (
    val startingAt: String,
    val endingAt: String
        )
