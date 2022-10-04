package ru.samokat.mysamokat.tests.dataproviders.dataClasses.staffApiGTW.users

import ru.samokat.my.rest.api.enum.ApiEnum

import ru.samokat.mysamokat.tests.dataproviders.dataClasses.staffApiGTW.enum.*
import java.util.*

data class EmployeeListView(

    val users: List<EmployeeView>
)

data class Staffer(

    val userId: UUID,
    val role: ApiEnum<StafferRole, String>,
    val state: ApiEnum<StafferState, String>,
    val inactivityReason: String?,
    val isIntern: Boolean,
    val version: Long
)