package ru.samokat.logistics.tests.dataproviders.employee

import ru.samokat.employeeprofiles.api.common.domain.EmployeeProfileStatus
import ru.samokat.employeeprofiles.api.common.filtering.MobileQueryFilter
import ru.samokat.employeeprofiles.api.profiles.getprofiles.GetProfilesRequest

import ru.samokat.employeeprofiles.api.profiles.getprofiles.PagingQueryFilter
import ru.samokat.my.domain.employee.EmployeeRole
import ru.samokat.my.rest.api.enum.ApiEnum
import java.util.*

class GetProfilesRequestBuilder {

    private var mobileLike: MobileQueryFilter? = null
    fun mobileLike(mobileLike: MobileQueryFilter?) = apply {this.mobileLike = mobileLike}
    fun getMobileLike():MobileQueryFilter {
        return mobileLike!!
    }
    private var nameLike: String? = null
    fun nameLike(nameLike: String?) = apply {this.nameLike = nameLike}
    fun getNameLike():String {
        return nameLike!!
    }

    private var darkstoreId: UUID? = null
    fun darkstoreId(darkstoreId: UUID?) = apply {this.darkstoreId = darkstoreId}
    fun getDarkstoreId():UUID {
        return darkstoreId!!
    }

    private var statuses: List<ApiEnum<EmployeeProfileStatus, String>>? = null
    fun statuses(statuses: List<ApiEnum<EmployeeProfileStatus, String>>?) = apply {this.statuses = statuses}
    fun getStatuses():List<ApiEnum<EmployeeProfileStatus, String>> {
        return statuses!!
    }

    private var roles: List<ApiEnum<EmployeeRole, String>>? = null
    fun roles(roles: List<ApiEnum<EmployeeRole, String>>?) = apply {this.roles = roles}
    fun getRoles():List<ApiEnum<EmployeeRole, String>> {
        return roles!!
    }

    private lateinit var paging: PagingQueryFilter
    fun paging(paging: PagingQueryFilter) = apply {this.paging = paging}
    fun getPaging():PagingQueryFilter {
        return paging
    }

    fun build(): GetProfilesRequest {
        return GetProfilesRequest(
            mobile = mobileLike,
            nameLike = nameLike,
            darkstoreId = darkstoreId,
            statuses = statuses,
            roles = roles,
            paging = paging
        )
    }


}
