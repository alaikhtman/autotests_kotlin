package ru.generalName.teamName.tests.helpers.controllers.schedule

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*


@Service
class TimesheetController {

    @Autowired
    lateinit var timesheetFeign: TimesheetClient

    fun getTimesheet(darkstoreId: UUID, request: GetTimesheetRequest): RestResult<TimesheetView, GetTimesheetError>? {
        return try {
            timesheetFeign.getTimesheet(darkstoreId, request).get()
        } catch (e: InterruptedException) {
            e.printStackTrace()
            null
        }

    }


    fun getTimesheetById(timesheetId: UUID): RestResult<TimesheetView, GetTimesheetByIdError>? {
        return try {
            timesheetFeign.getTimesheetById(timesheetId).get()
        } catch (e: InterruptedException) {
            e.printStackTrace()
            null
        }
    }

    fun submitTimesheet(timesheetId: UUID): RestResult<Unit, SubmitTimesheetError>? {
        return try {
            timesheetFeign.submitTimesheet(timesheetId).get()
        } catch (e: InterruptedException) {
            e.printStackTrace()
            null
        }
    }


}