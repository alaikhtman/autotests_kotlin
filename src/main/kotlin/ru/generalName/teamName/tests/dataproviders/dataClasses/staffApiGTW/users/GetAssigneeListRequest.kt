package ru.samokat.mysamokat.tests.dataproviders.dataClasses.staffApiGTW.users

data class GetAssigneeListRequest(
    val name: String,
    val userRoles: List<String>,
    val searchFrom: Long,
    val searchTo: Long,
    val assignFrom: Long,
    val assignTo: Long
)
