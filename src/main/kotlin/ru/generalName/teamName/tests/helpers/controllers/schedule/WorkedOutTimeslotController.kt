package ru.generalName.teamName.tests.helpers.controllers.schedule

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class WorkedOutTimeslotController {

    @Autowired
    lateinit var workedOutTimeslotFeign: WorkedOutTimeSlotClient

    fun createWorkedOutTimeSlot(request: CreateWorkedOutTimeSlotRequest): RestResult<CreatedWorkedOutTimeSlotView, CreateWorkedOutTimeSlotError>? {
        return try {
            workedOutTimeslotFeign.createWorkedOutTimeSlot(request).get()
        } catch (e: InterruptedException) {
            e.printStackTrace()
            null
        }
    }

    fun updateWorkedOutTimeSlot(
        workedOutTimeSlotId: UUID,
        request: UpdateWorkedOutTimeSlotRequest
    ): RestResult<Unit, UpdateWorkedOutTimeSlotError>? {
        return try {
            workedOutTimeslotFeign.updateWorkedOutTimeSlot(workedOutTimeSlotId, request).get()
        } catch (e: InterruptedException) {
            e.printStackTrace()
            null
        }

    }

    fun getTimeslotById(timeslotId: UUID): RestResult<WorkedOutTimeSlotView, GetWorkedOutTimeSlotError>? {
        return try {
            workedOutTimeslotFeign.getTimeslotById(timeslotId).get()
        } catch (e: InterruptedException) {
            e.printStackTrace()
            null
        }
    }

}