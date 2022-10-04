package ru.samokat.mysamokat.tests.dataproviders.shifts

import ru.samokat.my.domain.shifts.TimeRange
import ru.samokat.shifts.api.schedules.search.SearchShiftSchedulesRequest

import java.util.*

class SearchShiftSchedulesRequestFilterBuilder {

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


    fun build(): SearchShiftSchedulesRequest.Filter {
        return SearchShiftSchedulesRequest.Filter(
            timeRange = timeRange,
            userIds = userIds
        )
    }
}