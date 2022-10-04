package ru.samokat.mysamokat.tests.dataproviders.employee

import ru.samokat.employeeprofiles.api.darkstoreusers.internships.get.GetDarkstoreInternshipsRequest
import java.time.Instant

class GetDSInternshipRequestBuilder {

    private lateinit var from: Instant
    fun from (from: Instant) = apply { this.from = from }
    fun getFrom(): Instant {
        return from
    }

    private lateinit var to: Instant
    fun to (to: Instant) = apply { this.to = to }
    fun to(): Instant {
        return to
    }

    fun build(): GetDarkstoreInternshipsRequest {
        return GetDarkstoreInternshipsRequest(
            from = from,
            to = to
        )
    }
}