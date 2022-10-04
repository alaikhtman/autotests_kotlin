package ru.samokat.mysamokat.tests.helpers.controllers.employee_schedule

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.samokat.employeeschedule.api.timesheet.TimesheetView
import ru.samokat.employeeschedule.api.timesheet.get.GetTimesheetError
import ru.samokat.employeeschedule.api.timesheet.get.GetTimesheetRequest
import ru.samokat.employeeschedule.api.timesheet.getbyid.GetTimesheetByIdError
import ru.samokat.employeeschedule.api.timesheet.submit.SubmitTimesheetError
import ru.samokat.employeeschedule.client.TimesheetClient
import ru.samokat.my.rest.client.RestResult
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