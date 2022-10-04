package ru.generalName.teamName.tests.dataproviders.profile

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