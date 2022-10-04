package ru.samokat.mysamokat.tests.dataproviders.employee

import ru.samokat.employeeprofiles.api.darkstoreusers.internships.domain.RejectionCode
import ru.samokat.employeeprofiles.api.darkstoreusers.internships.reject.RejectInternshipRequest
import ru.samokat.my.rest.api.enum.ApiEnum
import java.util.*

class RejectInternshipRequestBuilder {
    private lateinit var issuerProfileId: UUID
    fun issuerProfileId(issuerProfileId: UUID) = apply { this.issuerProfileId = issuerProfileId }
    fun getIssuerProfileId(): UUID {
        return issuerProfileId

    }

    private lateinit var rejectionCode:
            ApiEnum<RejectionCode, String>

    fun rejectionCode(rejectionCode: ApiEnum<RejectionCode, String>) = apply { this.rejectionCode = rejectionCode }
    fun getRejectionCode(): ApiEnum<RejectionCode, String> {
        return rejectionCode

    }


    fun build(): RejectInternshipRequest {
        return RejectInternshipRequest(
            rejectionCode = rejectionCode,
            issuerProfileId = issuerProfileId
        )
    }
}