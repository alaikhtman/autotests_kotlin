package ru.samokat.mysamokat.tests.dataproviders.shifts

import ru.samokat.shifts.api.activeshifts.stop.StopShiftRequest
import java.util.*

class StopShiftRequestBuilder {

    private lateinit var userId: UUID
    fun userId(userId: UUID) = apply { this.userId = userId }
    fun getUserId(): UUID {
        return userId
    }

    fun build(): StopShiftRequest {
        return StopShiftRequest(
            userId = userId
        )

    }
}