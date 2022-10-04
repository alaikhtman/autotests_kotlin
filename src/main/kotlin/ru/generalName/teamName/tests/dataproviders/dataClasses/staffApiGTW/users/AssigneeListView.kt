package ru.samokat.mysamokat.tests.dataproviders.dataClasses.staffApiGTW.users


import ru.samokat.my.rest.api.enum.ApiEnum
import ru.samokat.mysamokat.tests.dataproviders.dataClasses.staffApiGTW.enum.EmployeeUserRole


import java.time.ZonedDateTime
import java.util.*


data class AssigneeListView(

    val assignees: List<AssigneeView>
)

data class AssigneeView(

    val assignee: EmployeeView,
    val availability: AssigneeAvailability
)

data class AssigneeAvailability(
    val available: Boolean,
    val conflictingShiftAssignments: List<ConflictingShiftAssignmentView>?
)

data class ConflictingShiftAssignmentView(

    val id: UUID,
    val assigneeRole: ApiEnum<EmployeeUserRole, String>,
    val assigneeRoles: List<ApiEnum<EmployeeUserRole, String>>?,
    val darkstore: DarkstoreView,
    val startingAt: ZonedDateTime,
    val endingAt: ZonedDateTime,
    val version: Long
)


