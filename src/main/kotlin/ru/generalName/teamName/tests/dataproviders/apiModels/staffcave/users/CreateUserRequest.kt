package ru.samokat.mysamokat.tests.dataproviders.apiModels.staffcave.users

import ru.samokat.employeeprofiles.api.common.domain.EmployeeVehicleType
import ru.samokat.my.domain.Email
import ru.samokat.my.domain.PhoneNumber
import ru.samokat.my.domain.employee.EmployeeName
import ru.samokat.my.domain.employee.EmployeeRole
import ru.samokat.my.rest.api.enum.ApiEnum
import java.util.*

data class CreateUserRequest(
    val mobile: PhoneNumber,
    val email: Email?,
    val name: EmployeeName,
    val roles: List<ApiEnum<EmployeeRole, String>>,
    val vehicle: Vehicle? = null,
    val darkstoreId: UUID? = null,
    val supervisedDarkstores: List<UUID>? = null,
    val staffPartnerId: UUID? = null,
    val accountingProfileId: String? = null,
    val requisitionId: UUID? = null,
)

data class Vehicle(
    val type: ApiEnum<EmployeeVehicleType, String>
)