package ru.samokat.mysamokat.tests.dataproviders.employee

import ru.samokat.employeeprofiles.api.profiles.disable.DisableProfileRequest
import java.util.*

class DisableProfileRequestBuilder {

    private lateinit var issuerProfileId: UUID
    fun issuerProfileId(darkstoreId: UUID) = apply { this.issuerProfileId = issuerProfileId }
    fun getIssuerProfileId(): UUID {
        return issuerProfileId
    }

    fun randomIssuerProfileId() = apply { issuerProfileId = UUID.randomUUID() }

    fun build(): DisableProfileRequest {
        return DisableProfileRequest(
            issuerProfileId = issuerProfileId
        )
    }
}