package ru.samokat.mysamokat.tests.dataproviders.shifts

import ru.samokat.my.domain.shifts.TimeRange
import ru.samokat.shifts.api.schedules.store.StoreScheduleRequest
import java.util.*

class StoreScheduleRequestBuilder {

    private lateinit var userId: UUID
    fun userId(userId: UUID) = apply { this.userId = userId }
    fun getUserId(): UUID {
        return userId
    }

    private lateinit var timeRange: TimeRange
    fun timeRange(timeRange: TimeRange) = apply { this.timeRange = timeRange }
    fun getTimeRange(): TimeRange {
        return timeRange
    }

    private lateinit var schedule: List<TimeRange>
    fun schedule(schedule: List<TimeRange>) = apply { this.schedule = schedule }
    fun getSchedule(): List<TimeRange> {
        return schedule
    }

    fun build(): StoreScheduleRequest {
        return StoreScheduleRequest(
            userId = userId,
            timeRange = timeRange,
            schedule = schedule)
    }

}

