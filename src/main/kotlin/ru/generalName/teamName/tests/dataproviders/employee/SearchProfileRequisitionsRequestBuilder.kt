package ru.samokat.mysamokat.tests.dataproviders.employee

import ru.samokat.employeeprofiles.api.common.filtering.MobileQueryFilter
import ru.samokat.employeeprofiles.api.common.paging.PagingFilter
import ru.samokat.employeeprofiles.api.darkstoreusers.violations.domain.ViolationCode
import ru.samokat.employeeprofiles.api.profilerequisitions.domain.ProfileRequisitionStatus
import ru.samokat.employeeprofiles.api.profilerequisitions.domain.ProfileRequisitionType
import ru.samokat.employeeprofiles.api.profilerequisitions.search.SearchProfileRequisitionsRequest
import ru.samokat.my.rest.api.enum.ApiEnum

class SearchProfileRequisitionsRequestBuilder {

    private var mobile: MobileQueryFilter? = null
    fun mobile(mobile: MobileQueryFilter?) = apply {this.mobile = mobile}
    fun getMobile(): MobileQueryFilter? {
        return mobile
    }

    private var nameLike: String? = null
    fun nameLike(nameLike: String?) = apply {this.nameLike = nameLike}
    fun getNameLike(): String? {
        return nameLike
    }

    private var types: List<ApiEnum<ProfileRequisitionType, String>>? = null
    fun types(types: List<ApiEnum<ProfileRequisitionType, String>>?) = apply {this.types = types}
    fun getTypes(): List<ApiEnum<ProfileRequisitionType, String>>? {
        return types
    }

    private var statuses: List<ApiEnum<ProfileRequisitionStatus, String>>? = null
    fun statuses(statuses: List<ApiEnum<ProfileRequisitionStatus, String>>?) = apply {this.statuses = statuses}
    fun getStatuses(): List<ApiEnum<ProfileRequisitionStatus, String>>? {
        return statuses
    }

    private lateinit var pagingFilter: PagingFilter
    fun pagingFilter(pagingFilter: PagingFilter) = apply {this.pagingFilter = pagingFilter}
    fun getPagingFilter(): PagingFilter { return pagingFilter}

    fun build(): SearchProfileRequisitionsRequest {
        return SearchProfileRequisitionsRequest(
            mobile = mobile,
            nameLike = nameLike,
            statuses = statuses,
            paging = pagingFilter,
            types = types
        )
    }
}