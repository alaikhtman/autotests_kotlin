package ru.samokat.mysamokat.tests.helpers.controllers.shifts

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.samokat.my.rest.client.RestResult
import ru.samokat.shifts.api.schedules.search.SearchShiftSchedulesError
import ru.samokat.shifts.api.schedules.search.SearchShiftSchedulesRequest
import ru.samokat.shifts.api.schedules.search.ShiftSchedulesListView
import ru.samokat.shifts.api.schedules.store.StoreScheduleError
import ru.samokat.shifts.api.schedules.store.StoreScheduleRequest
import ru.samokat.shifts.client.ShiftSchedulesClient

@Service
class ShiftSchedulesController {

    @Autowired
    lateinit var shiftSchedulesFeign: ShiftSchedulesClient

    fun postSchedules(request: StoreScheduleRequest): RestResult<Unit, StoreScheduleError>? {
        return try {
            shiftSchedulesFeign.store(request).get()
        } catch (e: InterruptedException) {
            e.printStackTrace()
            null
        }
    }

    fun searchSchedules(request: SearchShiftSchedulesRequest): RestResult<ShiftSchedulesListView, SearchShiftSchedulesError>? {
        return try {
            shiftSchedulesFeign.search(request).get()
        } catch (e: InterruptedException) {
            e.printStackTrace()
            null
        }
    }

}