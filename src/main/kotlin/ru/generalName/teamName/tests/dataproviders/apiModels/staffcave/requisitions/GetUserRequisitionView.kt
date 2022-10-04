package ru.samokat.mysamokat.tests.dataproviders.apiModels.staffcave.requisitions

import ru.samokat.my.enum.ScalarRepresentation
import ru.samokat.my.rest.api.enum.ApiEnum
import java.time.Instant
import java.util.*

data class GetUserRequisitionView (

    val requisitionId: UUID,
    val type: ApiEnum<UserRequisitionType, String>,
    val accountingProfileId: String,
    val metadata: UserRequisitionMetadataView,
    val status: ApiEnum<UserRequisitionStatus, String>,
    val modifiedAt: Instant)

data class UserRequisitionMetadataView(
    val fullName: String,
    val mobile: String,
    val partner: String?,
    val jobTitle: String
)


enum class UserRequisitionType(override val value: String) : ScalarRepresentation<String> {
    /**
     * Запрос для внутреннего сотрудника
     */
    INNER_SOURCE("inner_source"),

    /**
     * Запрос для внешнего сотрудника
     */
    OUTSOURCE("outsource")
}

enum class UserRequisitionStatus(override val value: String) : ScalarRepresentation<String> {
    /**
     * новый запрос, который ожидает обработки
     */
    NEW("new"),

    /**
     * запрос, который был отклонен сотрудником отдела кадров
     */
    DECLINED("declined"),

    /**
     * запрос был обработан и новая учётная запись была успешно создана
     */
    PROCESSED("processed")
}