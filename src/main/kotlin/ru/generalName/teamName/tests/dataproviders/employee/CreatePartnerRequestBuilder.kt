package ru.samokat.mysamokat.tests.dataproviders.employee

import ru.samokat.employeeprofiles.api.common.domain.StaffPartnerType
import ru.samokat.employeeprofiles.api.staffpartners.createpartners.CreatePartnerRequest
import ru.samokat.my.rest.api.enum.ApiEnum

class CreatePartnerRequestBuilder {

    private lateinit var title: String
    fun title(title: String) = apply {this.title = title}
    fun getTitle(): String {
        return title
    }

    private lateinit var shortTitle: String
    fun shortTitle(shortTitle: String) = apply {this.shortTitle = shortTitle}
    fun getShortTitle(): String {
        return shortTitle
    }

    private lateinit var type: ApiEnum<StaffPartnerType, String>
    fun type(type: ApiEnum<StaffPartnerType, String>) = apply {this.type = type}
    fun getType(): ApiEnum<StaffPartnerType, String> {
        return type
    }

    fun build(): CreatePartnerRequest {
        return CreatePartnerRequest(
            title = title,
            shortTitle = shortTitle,
            type = type
        )
    }
}