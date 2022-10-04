package ru.samokat.mysamokat.tests.dataproviders.apiModels.staffcave.requisitions

import ru.samokat.my.rest.api.enum.ApiEnum

data class SearchUserRequisitionsRequest (
    val mobile: String?,
    val name: String?,
    val statuses: List<ApiEnum<UserRequisitionStatus, String>>?,
    val types: List<ApiEnum<UserRequisitionType, String>>?,
    val pageSize: Int?,
    val pageMark: String?
        )