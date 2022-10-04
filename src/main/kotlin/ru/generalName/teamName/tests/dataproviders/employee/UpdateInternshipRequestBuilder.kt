package ru.samokat.mysamokat.tests.dataproviders.employee

import ru.samokat.employeeprofiles.api.darkstoreusers.internships.update.UpdateInternshipRequest
import java.time.Instant
import java.util.*

class UpdateInternshipRequestBuilder {

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

    private var version: Long = 1
    fun version(version: Long) = apply { this.version = version }
    fun getVersion(): Long {
        return version
    }

    fun build(): UpdateInternshipRequest {
        return UpdateInternshipRequest(
            darkstoreId = darkstoreId,
            plannedDate = plannedDate,
            issuerProfileId = issuerProfileId,
            version = version
        )
    }
}