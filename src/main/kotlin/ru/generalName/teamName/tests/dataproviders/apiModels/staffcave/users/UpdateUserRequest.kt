package ru.samokat.mysamokat.tests.dataproviders.apiModels.staffcave.users

import ru.samokat.my.domain.Email
import ru.samokat.my.domain.PhoneNumber
import ru.samokat.my.domain.employee.EmployeeName
import ru.samokat.my.domain.employee.EmployeeRole
import ru.samokat.my.rest.api.enum.ApiEnum

import java.util.*


data class UpdateUserRequest (
    val mobile: PhoneNumber,
    val email: Email?,
    val name: EmployeeName,
    val roles: List<ApiEnum<EmployeeRole, String>>,
    val vehicle: Vehicle?,
    val darkstoreId: UUID?,
    val supervisedDarkstores: List<UUID>? = null,
    val staffPartnerId: UUID?,
    val version: Long,
    val accountingProfileId: String? = null
)