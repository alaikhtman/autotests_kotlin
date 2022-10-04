package ru.samokat.mysamokat.tests.dataproviders.apiModels.staffcave.internships

import ru.samokat.my.domain.employee.EmployeeRole
import ru.samokat.my.rest.api.enum.ApiEnum
import ru.samokat.mysamokat.tests.dataproviders.apiModels.staffcave.DarkstoreView
import java.time.Instant

data class InternshipsView (
    val internships: List<InternshipView>,
    val isEditable: Boolean
)

data class InternshipView(
    val role: ApiEnum<EmployeeRole, String>,
    val darkstore: DarkstoreView,
    val plannedDate: Instant,
    val status: ApiEnum<InternshipStatus, String>,
    val isEditable: Boolean,
    val version: Long
)