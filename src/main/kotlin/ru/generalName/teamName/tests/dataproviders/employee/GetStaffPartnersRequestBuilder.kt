package ru.samokat.mysamokat.tests.dataproviders.employee

import ru.samokat.employeeprofiles.api.staffpartners.getpartners.GetPartnersRequest

class GetStaffPartnersRequestBuilder {

    private var pageSize: Int? = null
    fun pageSize(pageSize: Int?) = apply {this.pageSize = pageSize}
    fun getPageSize(): Int {
        return pageSize!!
    }

    private var pageMark: String? = null
    fun pageMark(pageMark: String?) = apply {this.pageMark = pageMark}
    fun getPageMark(): String {
        return pageMark!!
    }

    fun build(): GetPartnersRequest {
        return GetPartnersRequest(
            pageSize = pageSize,
            pageMark = pageMark
        )
    }
}