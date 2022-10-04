package ru.samokat.mysamokat.tests.dataproviders.shifts

import ru.samokat.my.domain.shifts.TimeRange
import ru.samokat.shifts.api.common.domain.ShiftUserRole
import ru.samokat.shifts.api.workedout.search.SearchWorkedOutShiftsRequest.Filter
import java.util.*

class SearchWorkedOutShiftFilterBuilder {

    private lateinit var timeRange: TimeRange
    fun timeRange(timeRange: TimeRange) = apply {this.timeRange = timeRange}
    fun getTimeRange(): TimeRange {
        return timeRange
    }

    private lateinit var userIds: Set<UUID>
    fun userIds(userIds: Set<UUID>) = apply { this.userIds = userIds }
    fun getUserIds(): Set<UUID> {
        return userIds!!
    }

    private var userRoles: Set<ShiftUserRole>? = null
    fun userRoles(userRoles: Set<ShiftUserRole>?) = apply { this.userRoles = userRoles }
    fun getUserRoles(): Set<ShiftUserRole>? {
        return userRoles!!
    }

    private var darkstoreId: UUID? = null
    fun darkstoreId(darkstoreId: UUID?) = apply { this.darkstoreId = darkstoreId }
    fun getDarkstoreId(): UUID? {
        return darkstoreId!!
    }

    fun build(): Filter{
        return Filter(
            timeRange = timeRange,
            userIds = userIds,
            userRoles = userRoles,
            darkstoreId = darkstoreId
        )
    }
}