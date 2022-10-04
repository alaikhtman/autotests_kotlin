package ru.samokat.mysamokat.tests.dataproviders.employee_schedule

import ru.samokat.employeeschedule.api.timesheet.get.GetTimesheetRequest
import java.time.LocalDate

class GetTimesheetRequestBuilder {

    private lateinit var date: LocalDate
    fun date(date: LocalDate) = apply { this.date = date }
    fun getDate(): LocalDate {
        return date
    }

    fun build(): GetTimesheetRequest {
        return GetTimesheetRequest(date = date)
    }
}