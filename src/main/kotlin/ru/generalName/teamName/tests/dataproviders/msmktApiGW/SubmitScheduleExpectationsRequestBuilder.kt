package ru.samokat.mysamokat.tests.dataproviders.msmktApiGW

import ru.samokat.mysamokat.apigateway.api.shifts.storeschedule.StoreScheduleRequest
import ru.samokat.mysamokat.apigateway.api.user.getexpectations.schedule.submit.SubmitScheduleExpectationsRequest
import java.time.Duration

class SubmitScheduleExpectationsRequestBuilder {

    private var weeklyWorkingDaysCount: Int = 1
    fun weeklyWorkingDaysCount(weeklyWorkingDaysCount: Int) =
        apply { this.weeklyWorkingDaysCount = weeklyWorkingDaysCount }

    fun getWeeklyWorkingDaysCount(): Int {
        return weeklyWorkingDaysCount
    }

    private lateinit var workingDayDuration: Duration
    fun workingDayDuration(workingDayDuration: Duration) =
        apply { this.workingDayDuration = workingDayDuration }

    fun getWorkingDayDuration(): Duration {
        return workingDayDuration
    }

    fun build(): SubmitScheduleExpectationsRequest {
        return SubmitScheduleExpectationsRequest(
            weeklyWorkingDaysCount = weeklyWorkingDaysCount,
            workingDayDuration = workingDayDuration
        )
    }
}