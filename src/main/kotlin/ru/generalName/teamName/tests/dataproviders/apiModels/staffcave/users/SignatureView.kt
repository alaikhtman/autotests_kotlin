package ru.samokat.mysamokat.tests.dataproviders.apiModels.staffcave.users

data class SignatureView(
    val canSign: Boolean,
    val reason: SignForbidReason?
)

enum class SignForbidReason {
    /**
     * Отсутствует поле accounting profile id
     */
    MISSING_ACCOUNTING_PROFILE_ID,

    /**
     * Отсутствует договор с сотрудником
     */
    MISSING_CONTRACT,

    /**
     * Неизвестная ошибка
     */
    UNKNOWN_ERROR
}
