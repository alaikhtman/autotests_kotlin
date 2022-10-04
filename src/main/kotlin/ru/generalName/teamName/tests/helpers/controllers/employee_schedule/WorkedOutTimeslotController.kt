package ru.samokat.mysamokat.tests.helpers.controllers.employee_schedule

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.samokat.employeeschedule.api.workedouttimeslot.WorkedOutTimeSlotView
import ru.samokat.employeeschedule.api.workedouttimeslot.create.CreateWorkedOutTimeSlotError
import ru.samokat.employeeschedule.api.workedouttimeslot.create.CreateWorkedOutTimeSlotRequest
import ru.samokat.employeeschedule.api.workedouttimeslot.create.CreatedWorkedOutTimeSlotView
import ru.samokat.employeeschedule.api.workedouttimeslot.get.GetWorkedOutTimeSlotError
import ru.samokat.employeeschedule.api.workedouttimeslot.update.UpdateWorkedOutTimeSlotError
import ru.samokat.employeeschedule.api.workedouttimeslot.update.UpdateWorkedOutTimeSlotRequest
import ru.samokat.employeeschedule.client.WorkedOutTimeSlotClient
import ru.samokat.my.rest.client.RestResult
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