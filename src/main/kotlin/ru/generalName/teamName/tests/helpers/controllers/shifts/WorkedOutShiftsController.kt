package ru.samokat.mysamokat.tests.helpers.controllers.shifts

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.samokat.my.rest.client.RestResult
import ru.samokat.shifts.api.workedout.WorkedOutShiftView
import ru.samokat.shifts.api.workedout.getbyid.GetWorkedOutShiftByIdError
import ru.samokat.shifts.api.workedout.search.SearchWorkedOutShiftsError
import ru.samokat.shifts.api.workedout.search.SearchWorkedOutShiftsRequest
import ru.samokat.shifts.api.workedout.search.WorkedOutShiftsListView
import ru.samokat.shifts.client.WorkedOutShiftsClient
import java.util.*

@Service
class WorkedOutShiftsController {

    @Autowired
    lateinit var workedOutShiftsFeign: WorkedOutShiftsClient

    fun searchWorkedOutShifts(request: SearchWorkedOutShiftsRequest): RestResult<WorkedOutShiftsListView, SearchWorkedOutShiftsError>? {
        return try {
            workedOutShiftsFeign.search(request).get()
        } catch (e: InterruptedException) {
            e.printStackTrace()
            null
        }
    }

    fun getWorkedOutShiftById(shiftId: UUID): RestResult<WorkedOutShiftView, GetWorkedOutShiftByIdError>? {
        return try {
            workedOutShiftsFeign.getShift(shiftId).get()
        } catch (e: InterruptedException) {
            e.printStackTrace()
            null
        }
    }
}