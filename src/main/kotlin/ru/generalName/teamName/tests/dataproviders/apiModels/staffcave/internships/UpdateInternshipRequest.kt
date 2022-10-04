package ru.samokat.mysamokat.tests.dataproviders.apiModels.staffcave.internships

import java.time.Instant
import java.util.*

data class UpdateInternshipRequest (
    val darkstoreId: UUID,
    val plannedDate: Instant,
    val version: Long
        )