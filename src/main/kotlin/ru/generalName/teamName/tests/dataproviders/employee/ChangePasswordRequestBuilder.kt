package ru.samokat.mysamokat.tests.dataproviders.employee

import ru.samokat.employeeprofiles.api.profiles.changepassword.ChangePasswordRequest
import java.util.*

class ChangePasswordRequestBuilder {
    private lateinit var issuerProfileId: UUID
    fun issuerProfileId(issuerProfileId: UUID) = apply { this.issuerProfileId = issuerProfileId }
    fun getIssuerProfileId(): UUID {
        return issuerProfileId
    }

    fun randomIssuerProfileId() = apply { issuerProfileId = UUID.randomUUID() }

    fun build(): ChangePasswordRequest {
        return ChangePasswordRequest(
            issuerProfileId = issuerProfileId
        )
    }
}