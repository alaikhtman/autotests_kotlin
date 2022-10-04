package ru.samokat.mysamokat.tests.dataproviders.apiModels.staffcave.requisitions

import ru.samokat.my.rest.api.enum.ApiEnum
import java.time.Instant
import java.util.*

data class UserRequisitionView (

    val requisitionId: UUID,
    val type: ApiEnum<UserRequisitionType, String>,
    val accountingProfileId: String,
    val fullName: String,
    val mobile: String,
    val status: ApiEnum<UserRequisitionStatus, String>,
    val modifiedAt: Instant
)