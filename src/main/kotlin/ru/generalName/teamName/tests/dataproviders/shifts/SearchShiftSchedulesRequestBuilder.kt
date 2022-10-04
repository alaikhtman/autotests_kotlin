package ru.samokat.mysamokat.tests.dataproviders.shifts

import ru.samokat.shifts.api.schedules.search.SearchShiftSchedulesRequest

class SearchShiftSchedulesRequestBuilder {

    private lateinit var filter: SearchShiftSchedulesRequest.Filter
    fun filter(filter: SearchShiftSchedulesRequest.Filter) = apply { this.filter = filter }
    fun getFilter(): SearchShiftSchedulesRequest.Filter {
        return filter
    }

    private var paging: SearchShiftSchedulesRequest.PagingData? = null
    fun paging(paging: SearchShiftSchedulesRequest.PagingData?) = apply { this.paging = paging }
    fun getPaging(): SearchShiftSchedulesRequest.PagingData? {
        return paging!!
    }

    fun build(): SearchShiftSchedulesRequest {
        return SearchShiftSchedulesRequest(
            filter = filter,
            paging = paging)
    }
}