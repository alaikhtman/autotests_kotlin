package ru.samokat.mysamokat.tests.dataproviders.apiModels.staffcave.users

import ru.samokat.my.domain.Email
import ru.samokat.my.domain.PhoneNumber
import ru.samokat.my.domain.employee.EmployeeName
import ru.samokat.my.domain.employee.EmployeeRole
import ru.samokat.my.rest.api.enum.ApiEnum
import ru.samokat.mysamokat.tests.dataproviders.apiModels.staffcave.DarkstoreView
import ru.samokat.mysamokat.tests.dataproviders.apiModels.staffcave.staffpartners.StaffPartnerView
import java.util.*

data class BriefUserView (
    val userId: UUID,
    val mobile: PhoneNumber,
    val email: Email?,
    val name: EmployeeName,
    val roles: List<ApiEnum<EmployeeRole, String>>,
    val vehicle: Vehicle? = null,
    val darkstore: DarkstoreView?,
    val supervisedDarkstores: List<DarkstoreView>?,
    val staffPartner: StaffPartnerView?
    )