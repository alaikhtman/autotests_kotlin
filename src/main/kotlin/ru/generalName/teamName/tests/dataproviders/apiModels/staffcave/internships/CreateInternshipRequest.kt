package ru.samokat.mysamokat.tests.dataproviders.apiModels.staffcave.internships

import java.time.Instant
import java.util.*

data class CreateInternshipRequest (
    val darkstoreId: UUID,
    val plannedDate: Instant
    )