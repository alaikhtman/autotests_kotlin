package ru.samokat.mysamokat.tests.dataproviders.apiModels.staffcave.staffpartners

import ru.samokat.my.enum.ScalarRepresentation
import ru.samokat.my.rest.api.enum.ApiEnum

data class CreatePartnerRequest (
    val title: String,
    val shortTitle: String,
    val type: ApiEnum<StaffPartnerType, String>
        )


enum class StaffPartnerType(override val value: String) : ScalarRepresentation<String> {

    OUT_SOURCE("outsource"),
    OUT_STAFF("outstaff");

    override fun toString(): String {
        return value
    }
}