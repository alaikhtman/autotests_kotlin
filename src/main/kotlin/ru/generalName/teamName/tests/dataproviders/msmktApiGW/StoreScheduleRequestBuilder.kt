package ru.samokat.mysamokat.tests.dataproviders.msmktApiGW

import ru.samokat.my.domain.shifts.TimeRange
import ru.samokat.mysamokat.apigateway.api.shifts.storeschedule.StoreScheduleRequest

class StoreScheduleRequestBuilder {

    private lateinit var schedules: List<StoreScheduleRequest.TimeRange>
    fun schedules(schedules: List<StoreScheduleRequest.TimeRange>) = apply {this.schedules = schedules}
    fun getSchedules(): List<StoreScheduleRequest.TimeRange> {
        return schedules
    }

    fun build(): StoreScheduleRequest {
        return StoreScheduleRequest(
            schedules = schedules
        )
    }
}