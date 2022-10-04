package ru.samokat.mysamokat.tests.dataproviders.shifts

import ru.samokat.shifts.api.activeshifts.getlist.GetActiveShiftsListRequest
import ru.samokat.shifts.api.common.domain.ShiftUserPermission
import java.util.*

class GetActiveShiftsListRequestBuilder {

    private lateinit var darkstoreId: UUID
    fun darkstoreId(darkstoreId: UUID) = apply { this.darkstoreId = darkstoreId }
    fun getDarkstoreId(): UUID {
        return darkstoreId
    }

    private var userPermission: ShiftUserPermission? = null
    fun userPermission(userPermission: ShiftUserPermission?) = apply { this.userPermission = userPermission }
    fun getUserPermission(): ShiftUserPermission? {
        return userPermission!!
    }

    fun build(): GetActiveShiftsListRequest {
        return GetActiveShiftsListRequest(
            darkstoreId = darkstoreId,
            userPermission = userPermission
        )

    }
}