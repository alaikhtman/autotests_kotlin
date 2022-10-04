package ru.samokat.mysamokat.tests.helpers.controllers.shifts

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.samokat.my.rest.client.RestResult
import ru.samokat.shifts.api.assignments.ShiftAssignmentView
import ru.samokat.shifts.api.assignments.getbyid.GetShiftAssignmentByIdError
import ru.samokat.shifts.api.assignments.search.SearchShiftAssignmentsError
import ru.samokat.shifts.api.assignments.search.SearchShiftAssignmentsRequest
import ru.samokat.shifts.api.assignments.search.ShiftAssignmentsListView
import ru.samokat.shifts.api.assignments.storebatch.StoreShiftAssignmentsBatchError
import ru.samokat.shifts.api.assignments.storebatch.StoreShiftAssignmentsBatchRequest
import ru.samokat.shifts.api.assignments.storebatch.StoredShiftAssignmentsBatchView
import ru.samokat.shifts.client.ShiftAssignmentsClient
import java.util.*

@Service
class ShiftAssignmentsController {

    @Autowired
    lateinit var shiftAssignmentsFeign: ShiftAssignmentsClient

    fun getAssignmentsById(assignmentId: UUID): RestResult<ShiftAssignmentView, GetShiftAssignmentByIdError>? {
        return try {
            shiftAssignmentsFeign.getAssignment(assignmentId).get()
        } catch (e: InterruptedException) {
            e.printStackTrace()
            null
        }
    }

    fun searchAssignments(request: SearchShiftAssignmentsRequest): RestResult<ShiftAssignmentsListView, SearchShiftAssignmentsError>? {
        return try {
            shiftAssignmentsFeign.search(request).get()
        } catch (e: InterruptedException) {
            e.printStackTrace()
            null
        }
    }

    fun storeBatchAssignments(request: StoreShiftAssignmentsBatchRequest): RestResult<StoredShiftAssignmentsBatchView, StoreShiftAssignmentsBatchError>? {
        return try {
            shiftAssignmentsFeign.storeBatch(request).get()
        } catch (e: InterruptedException) {
            e.printStackTrace()
            null
        }
    }
}
