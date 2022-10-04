package ru.samokat.mysamokat.tests.dataproviders.employee

import ru.samokat.employeeprofiles.api.common.paging.PagingFilter
import ru.samokat.employeeprofiles.api.darkstoreusers.domain.DarkstoreUserRole
import ru.samokat.employeeprofiles.api.darkstoreusers.domain.DarkstoreUserState
import ru.samokat.employeeprofiles.api.darkstoreusers.search.SearchDarkstoreUsersRequest
import ru.samokat.employeeprofiles.api.darkstoreusers.search.SearchDarkstoreUsersSortingMode
import ru.samokat.employeeprofiles.api.common.domain.EmployeeVehicleType
import ru.samokat.my.rest.api.enum.ApiEnum
import java.util.*

class GetDarkstoreUsersRequestBuilder {

    private lateinit var darkstoreIds: List<UUID>
    fun darkstoreIds(darkstoreIds: List<UUID>) = apply {this.darkstoreIds = darkstoreIds}
    fun getDarkstoreIds(): List<UUID>{ return darkstoreIds}

    private var darkstoreUserStates: List<ApiEnum<DarkstoreUserState, String>>? = null
    fun darkstoreUserStates(darkstoreUserStates: List<ApiEnum<DarkstoreUserState, String>>?) = apply {this.darkstoreUserStates = darkstoreUserStates}
    fun getDarkstoreUserStates(): List<ApiEnum<DarkstoreUserState, String>>? { return darkstoreUserStates}

    private var nameLike: String? = null
    fun nameLike(nameLike: String?) = apply {this.nameLike = nameLike}
    fun getNameLike(): String? { return nameLike}

    private var mobileLike: String? = null
    fun mobileLike(mobileLike: String?) = apply {this.mobileLike = mobileLike}
    fun getMobileLike(): String? { return mobileLike}

    private var darkstoreUserRoles: List<ApiEnum<DarkstoreUserRole, String>>? = null
    fun darkstoreUserRoles(darkstoreUserRoles: List<ApiEnum<DarkstoreUserRole, String>>?) = apply {this.darkstoreUserRoles = darkstoreUserRoles}
    fun getDarkstoreUserRoles(): List<ApiEnum<DarkstoreUserRole, String>>? { return darkstoreUserRoles}

    private var staffPartnerIds: List<UUID>? = null
    fun staffPartnerIds(staffPartnerIds: List<UUID>?) = apply {this.staffPartnerIds = staffPartnerIds}
    fun getStaffPartnerIds(): List<UUID>? { return staffPartnerIds}

    private var vehicleTypes: List<ApiEnum<EmployeeVehicleType, String>>? = null
    fun vehicleTypes(vehicleTypes: List<ApiEnum<EmployeeVehicleType, String>>?) = apply {this.vehicleTypes = vehicleTypes}
    fun getvVehicleTypes(): List<ApiEnum<EmployeeVehicleType, String>>? { return vehicleTypes}

    private var sortingMode: ApiEnum<SearchDarkstoreUsersSortingMode, String>? = null
    fun sortingMode(sortingMode: ApiEnum<SearchDarkstoreUsersSortingMode, String>?) = apply {this.sortingMode = sortingMode}
    fun getSortingMode(): ApiEnum<SearchDarkstoreUsersSortingMode, String>? { return sortingMode}

    private var pagingFilter: PagingFilter = PagingFilter(100, null)
    fun pagingFilter(pagingFilter: PagingFilter) = apply {this.pagingFilter = pagingFilter}
    fun getPagingFilter(): PagingFilter { return pagingFilter}


    fun build(): SearchDarkstoreUsersRequest {
        return SearchDarkstoreUsersRequest(
            darkstoreIds = darkstoreIds,
            darkstoreUserStates = darkstoreUserStates,
            nameLike = nameLike,
            mobileLike = mobileLike,
            darkstoreUserRoles = darkstoreUserRoles,
            staffPartnerIds = staffPartnerIds,
            vehicleTypes = vehicleTypes,
            sortingMode = sortingMode,
            pagingFilter = pagingFilter
        )
    }
}