package ru.samokat.mysamokat.tests.dataproviders.employee

import ru.samokat.employeeprofiles.api.common.paging.PagingFilter
import ru.samokat.employeeprofiles.api.darkstoreusers.domain.DarkstoreUserRole
import ru.samokat.employeeprofiles.api.darkstoreusers.violations.search.SearchDarkstoreUsersViolationsRequest
import ru.samokat.my.rest.api.enum.ApiEnum
import java.util.*

class SearchDarkstoreUsersViolationsRequestBuilder {

    private lateinit var darkstoreIds: List<UUID>
    fun darkstoreIds(darkstoreIds: List<UUID>) = apply {this.darkstoreIds = darkstoreIds}
    fun getDarkstoreIds(): List<UUID>{ return darkstoreIds}

    private lateinit var darkstoreUserRoles: List<ApiEnum<DarkstoreUserRole, String>>
    fun darkstoreUserRoles(darkstoreUserRoles: List<ApiEnum<DarkstoreUserRole, String>>) = apply {this.darkstoreUserRoles = darkstoreUserRoles}
    fun getDarkstoreUserRoles(): List<ApiEnum<DarkstoreUserRole, String>>{ return darkstoreUserRoles}

    private lateinit var pagingFilter: PagingFilter
    fun pagingFilter(pagingFilter: PagingFilter) = apply {this.pagingFilter = pagingFilter}
    fun getPagingFilter(): PagingFilter { return pagingFilter}

    fun build(): SearchDarkstoreUsersViolationsRequest {
        return SearchDarkstoreUsersViolationsRequest(
            darkstoreIds = darkstoreIds,
            darkstoreUserRoles = darkstoreUserRoles,
            pagingFilter = pagingFilter
        )
    }
}
