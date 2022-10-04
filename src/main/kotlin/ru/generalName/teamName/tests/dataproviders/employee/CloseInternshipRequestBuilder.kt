package ru.samokat.mysamokat.tests.dataproviders.employee

import ru.samokat.employeeprofiles.api.darkstoreusers.internships.close.CloseInternshipRequest
import ru.samokat.employeeprofiles.api.darkstoreusers.internships.domain.FailureCode
import ru.samokat.employeeprofiles.api.darkstoreusers.internships.domain.InternshipStatus
import ru.samokat.my.rest.api.enum.ApiEnum
import java.util.*

class CloseInternshipRequestBuilder {
    private lateinit var issuerProfileId: UUID
    fun issuerProfileId(issuerProfileId: UUID) = apply { this.issuerProfileId = issuerProfileId }
    fun getIssuerProfileId(): UUID {
        return issuerProfileId

    }

    private  var failureCode:
            ApiEnum<FailureCode, String>? = null

    fun failureCode(failureCode: ApiEnum<FailureCode, String>?) = apply { this.failureCode = failureCode }
    fun getFailureCode(): ApiEnum<FailureCode, String> {
        return failureCode!!

    }

    private lateinit var status:
            ApiEnum<InternshipStatus, String>

    fun status(status: ApiEnum<InternshipStatus, String>) = apply { this.status = status }
    fun status(): ApiEnum<InternshipStatus, String> {
        return status

    }


    fun build(): CloseInternshipRequest {
        return CloseInternshipRequest(
            status = status,
            failureCode = failureCode,
            issuerProfileId = issuerProfileId
        )
    }
}