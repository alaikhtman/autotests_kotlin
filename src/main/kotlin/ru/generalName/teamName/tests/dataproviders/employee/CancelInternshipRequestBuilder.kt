package ru.samokat.mysamokat.tests.dataproviders.employee

import ru.samokat.employeeprofiles.api.darkstoreusers.internships.cancel.CancelInternshipRequest
import java.util.*

class CancelInternshipRequestBuilder {


    private lateinit var issuerProfileId: UUID
    fun issuerProfileId(issuerProfileId: UUID) = apply { this.issuerProfileId = issuerProfileId }
    fun getIssuerProfileId(): UUID {
        return issuerProfileId

    }


    fun build(): CancelInternshipRequest {
        return CancelInternshipRequest(
            issuerProfileId = issuerProfileId
        )
    }
}