package ru.samokat.mysamokat.tests.dataproviders.shifts

import ru.samokat.shifts.api.workedout.search.SearchWorkedOutShiftsRequest

class SearchWorkedOutShiftsRequestBuilder {

    private lateinit var filter: SearchWorkedOutShiftsRequest.Filter
    fun filter(filter: SearchWorkedOutShiftsRequest.Filter) = apply { this.filter = filter }
    fun getFilter(): SearchWorkedOutShiftsRequest.Filter {
        return filter
    }

    private var paging: SearchWorkedOutShiftsRequest.PagingData? = null
    fun paging(paging: SearchWorkedOutShiftsRequest.PagingData?) = apply { this.paging = paging }
    fun getPaging(): SearchWorkedOutShiftsRequest.PagingData? {
        return paging!!
    }

   fun build(): SearchWorkedOutShiftsRequest {
        return SearchWorkedOutShiftsRequest(
            filter = filter,
            paging = paging)
    }
}

