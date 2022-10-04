package ru.generalName.teamName.tests.dataproviders.schedule


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