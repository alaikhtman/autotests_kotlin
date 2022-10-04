package ru.samokat.mysamokat.tests.dataproviders.shifts

import ru.samokat.my.domain.shifts.TimeRange
import ru.samokat.shifts.api.aggregates.statistics.GetShiftAggregatedStatisticRequest
import java.util.*

class GetShiftAggregatedStatisticRequestBuilder {

    private lateinit var timeRange: TimeRange
    fun timeRange(timeRange: TimeRange) = apply { this.timeRange = timeRange }
    fun getTimeRange(): TimeRange {
        return timeRange
    }

    private lateinit var userIds: List<UUID>
    fun userIds(userIds: List<UUID>) = apply { this.userIds = userIds }
    fun getUserIds(): List<UUID> {
        return userIds
    }

    fun build(): GetShiftAggregatedStatisticRequest {
        return GetShiftAggregatedStatisticRequest(
            timeRange = timeRange,
            userIds = userIds
        )

    }

}