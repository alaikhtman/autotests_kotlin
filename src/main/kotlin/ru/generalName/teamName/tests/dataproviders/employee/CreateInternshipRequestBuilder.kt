package ru.samokat.mysamokat.tests.dataproviders.employee

import ru.samokat.employeeprofiles.api.darkstoreusers.internships.create.CreateInternshipRequest
import java.time.Instant
import java.util.*

class CreateInternshipRequestBuilder {

    private lateinit var darkstoreId: UUID
    fun darkstoreId(darkstoreId: UUID) = apply { this.darkstoreId = darkstoreId }
    fun getDarkstoreId(): UUID {
        return darkstoreId
    }

    private lateinit var plannedDate: Instant
    fun plannedDate(plannedDate: Instant) = apply { this.plannedDate = plannedDate }
    fun getPlannedDate(): Instant {
        return plannedDate
    }

    private lateinit var issuerProfileId: UUID
    fun issuerProfileId(issuerProfileId: UUID) = apply { this.issuerProfileId = issuerProfileId }
    fun getIssuerProfileId(): UUID {
        return issuerProfileId
    }

    fun build(): CreateInternshipRequest {
        return CreateInternshipRequest(
            darkstoreId = darkstoreId,
            plannedDate = plannedDate,
            issuerProfileId = issuerProfileId
        )
    }
}