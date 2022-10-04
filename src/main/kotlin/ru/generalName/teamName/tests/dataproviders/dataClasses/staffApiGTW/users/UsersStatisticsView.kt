package ru.samokat.mysamokat.tests.dataproviders.dataClasses.staffApiGTW.users

import java.time.Duration
import java.util.*

data class UsersStatisticsView(

    val statistics: Map<UUID, UserStatistics>
)


data class UserStatistics(
    val schedule: ScheduleStatistics,
    val assignments: ShiftAssignmentsStatistics,
    val workedOutShifts: WorkedOutShiftsStatistics
)


data class ScheduleStatistics(
    val duration: Duration
)


data class ShiftAssignmentsStatistics(
    val duration: Duration,
    val count: Count
)


data class Count(
    val assigned: Int,
    val canceled: CanceledCount
)


data class CanceledCount(
    val mistaken: Int,
    val byIssuer: Int,
    val byAssignee: Int,
    val absence: Int
)


data class WorkedOutShiftsStatistics(

    val duration: Duration,
    val count: Int
)
