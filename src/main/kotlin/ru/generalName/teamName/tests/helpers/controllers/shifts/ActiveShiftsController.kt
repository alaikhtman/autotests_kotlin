package ru.samokat.mysamokat.tests.helpers.controllers.shifts

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.samokat.my.rest.api.error.GeneralError
import ru.samokat.my.rest.client.RestResult
import ru.samokat.shifts.api.activeshifts.ActiveShiftView
import ru.samokat.shifts.api.activeshifts.get.GetActiveShiftError
import ru.samokat.shifts.api.activeshifts.getlist.ActiveShiftsListView
import ru.samokat.shifts.api.activeshifts.getlist.GetActiveShiftsListError
import ru.samokat.shifts.api.activeshifts.getlist.GetActiveShiftsListRequest
import ru.samokat.shifts.api.activeshifts.start.StartShiftError
import ru.samokat.shifts.api.activeshifts.start.StartShiftRequest
import ru.samokat.shifts.api.activeshifts.stop.StopShiftRequest
import ru.samokat.shifts.client.ActiveShiftsClient
import java.util.*

@Service
class ActiveShiftsController {
    @Autowired
    lateinit var activeShiftsFeign: ActiveShiftsClient

    fun openShift(request: StartShiftRequest): RestResult<ActiveShiftView, StartShiftError>? {
        return try {
            activeShiftsFeign.startShift(request).get()
        } catch (e: InterruptedException) {
            e.printStackTrace()
            null
        }
    }

    fun getActiveShiftByProfileId(profileId: UUID): RestResult<ActiveShiftView, GetActiveShiftError>? {
        return try {
            activeShiftsFeign.getActiveShift(profileId).get()
        } catch (e: InterruptedException) {
            e.printStackTrace()
            null
        }
    }

    fun getActiveShiftsByDarkstore(request: GetActiveShiftsListRequest): RestResult<ActiveShiftsListView, GetActiveShiftsListError>? {
        return try {
            activeShiftsFeign.getActiveShifts(request).get()
        } catch (e: InterruptedException) {
            e.printStackTrace()
            null
        }
    }

    fun stopActiveShift(request: StopShiftRequest): RestResult<Unit, GeneralError>? {
        return try {
            activeShiftsFeign.stopShift(request).get()
        } catch (e: InterruptedException) {
            e.printStackTrace()
            null
        }
    }


}