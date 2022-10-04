package ru.samokat.mysamokat.tests.dataproviders.shifts

import ru.samokat.shifts.api.activeshifts.start.StartShiftRequest
import ru.samokat.shifts.api.common.domain.DeliveryMethod
import ru.samokat.shifts.api.common.domain.ShiftUserRole
import java.util.*

class StartShiftRequestBuilder {

    private lateinit var userId: UUID
    fun userId(userId: UUID) = apply { this.userId = userId }
    fun getUserId(): UUID {
        return userId
    }

    private lateinit var darkstoreId: UUID
    fun darkstoreId(darkstoreId: UUID) = apply { this.darkstoreId = darkstoreId }
    fun getDarkstoreId(): UUID {
        return darkstoreId
    }

    private lateinit var userRole: ShiftUserRole
    fun userRole(userRole: ShiftUserRole) = apply { this.userRole = userRole }
    fun getUserRole(): ShiftUserRole {
        return userRole
    }

    private var deliveryMethod: DeliveryMethod? = null
    fun deliveryMethod(deliveryMethod: DeliveryMethod?) = apply { this.deliveryMethod = deliveryMethod }
    fun getDeliveryMethod(): DeliveryMethod? {
        return deliveryMethod!!
    }

    fun build(): StartShiftRequest {
        return StartShiftRequest(
            darkstoreId = darkstoreId,
            userId = userId,
            userRole = userRole,
            deliveryMethod = deliveryMethod
        )

    }
}

