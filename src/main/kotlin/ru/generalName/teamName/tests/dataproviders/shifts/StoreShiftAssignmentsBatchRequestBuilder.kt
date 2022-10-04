package ru.samokat.mysamokat.tests.dataproviders.shifts

import ru.samokat.my.domain.shifts.TimeRange
import ru.samokat.my.rest.api.enum.ApiEnum
import ru.samokat.shifts.api.assignments.storebatch.StoreShiftAssignmentsBatchRequest
import ru.samokat.shifts.api.common.domain.AssigneeRole
import java.util.*

class StoreShiftAssignmentsBatchRequestBuilder {

    private lateinit var darkstoreId: UUID
    fun darkstoreId(darkstoreId: UUID) = apply { this.darkstoreId = darkstoreId }
    fun getDarkstoreId(): UUID {
        return darkstoreId
    }

    private lateinit var issuerId: UUID
    fun issuerId(issuerId: UUID) = apply { this.issuerId = issuerId }
    fun getIssuerId(): UUID {
        return issuerId
    }

    private lateinit var assignmentsRole: ApiEnum<AssigneeRole, String>
    fun assignmentsRole(assignmentsRole: ApiEnum<AssigneeRole, String>) = apply { this.assignmentsRole = assignmentsRole }
    fun getAssignmentsRole(): ApiEnum<AssigneeRole, String> {
        return assignmentsRole
    }

    private lateinit var editingTimeRange: TimeRange
    fun editingTimeRange(editingTimeRange: TimeRange) = apply { this.editingTimeRange = editingTimeRange }
    fun getEditingTimeRange(): TimeRange {
        return editingTimeRange
    }

    private lateinit var batch: StoreShiftAssignmentsBatchRequest.Batch
    fun batch(batch: StoreShiftAssignmentsBatchRequest.Batch) = apply { this.batch = batch }
    fun getBatch(): StoreShiftAssignmentsBatchRequest.Batch {
        return batch
    }

    fun build(): StoreShiftAssignmentsBatchRequest {
        return StoreShiftAssignmentsBatchRequest(
            darkstoreId = darkstoreId,
            issuerId = issuerId,
            assignmentsRole = assignmentsRole,
            editingTimeRange = editingTimeRange,
            batch = batch
        )

    }
}