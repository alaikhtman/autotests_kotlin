package ru.samokat.mysamokat.tests.dataproviders.shifts

import com.fasterxml.jackson.databind.JsonNode
import ru.samokat.my.domain.shifts.ShiftAssignmentCancellationReason
import ru.samokat.my.domain.shifts.TimeRange
import ru.samokat.my.rest.api.enum.ApiEnum
import ru.samokat.shifts.api.assignments.storebatch.StoreShiftAssignmentsBatchRequest
import java.util.*

class StoreShiftAssignmentsBatchBuilder {

    private var creations: List<StoreShiftAssignmentsBatchRequest.Creation>? = null
    fun creations(creations: List<StoreShiftAssignmentsBatchRequest.Creation>?) =
        apply { this.creations = creations }
    fun creations(): List<StoreShiftAssignmentsBatchRequest.Creation>? {
        return creations
    }

    private var updates: List<StoreShiftAssignmentsBatchRequest.Update>? = null
    fun updates(updates: List<StoreShiftAssignmentsBatchRequest.Update>?) = apply { this.updates = updates }
    fun updates(): List<StoreShiftAssignmentsBatchRequest.Update>? {
        return updates
    }

    private var cancellations: List<StoreShiftAssignmentsBatchRequest.Cancellation>? = null
    fun cancellations(cancellations: List<StoreShiftAssignmentsBatchRequest.Cancellation>?) =
        apply { this.cancellations = cancellations }
    fun cancellations(): List<StoreShiftAssignmentsBatchRequest.Cancellation>? {
        return cancellations
    }

    fun build(): StoreShiftAssignmentsBatchRequest.Batch {
        return StoreShiftAssignmentsBatchRequest.Batch(
            creations = creations,
            updates = updates,
            cancellations = cancellations
        )
    }
}

class CreationBuilder {

    private var clientAssignmentId: String? = null
    fun clientAssignmentId(editingTimeRange: String?) = apply { this.clientAssignmentId = clientAssignmentId }
    fun getClientAssignmentId(): String? {
        return clientAssignmentId
    }

    private lateinit var assigneeId: UUID
    fun assigneeId(assigneeId: UUID) = apply { this.assigneeId = assigneeId }
    fun getAssigneeId(): UUID {
        return assigneeId
    }

    private lateinit var timeRange: TimeRange
    fun timeRange(timeRange: TimeRange) = apply { this.timeRange = timeRange }
    fun getTimeRange(): TimeRange {
        return timeRange
    }

    private var metadata: Map<String, JsonNode>? = null
    fun metadata(metadata: Map<String, JsonNode>? = null) = apply { this.metadata = metadata }
    fun getMetadata(): Map<String, JsonNode>? {
        return metadata
    }

    fun build(): StoreShiftAssignmentsBatchRequest.Creation {
        return StoreShiftAssignmentsBatchRequest.Creation(
            clientAssignmentId = clientAssignmentId,
            assigneeId = assigneeId,
            timeRange = timeRange,
            metadata = metadata
        )
    }
}

class UpdateBuilder {

    private lateinit var assignmentId: UUID
    fun assignmentId(assignmentId: UUID) = apply { this.assignmentId = assignmentId }
    fun getAssignmentId(): UUID {
        return assignmentId
    }

    private lateinit var timeRange: TimeRange
    fun timeRange(timeRange: TimeRange) = apply { this.timeRange = timeRange }
    fun getTimeRange(): TimeRange {
        return timeRange
    }

    private var metadata: Map<String, JsonNode>? = null
    fun metadata(metadata: Map<String, JsonNode>? = null) = apply { this.metadata = metadata }
    fun getMetadata(): Map<String, JsonNode>? {
        return metadata
    }

    private var version: Long = 1L
    fun version(version: Long) = apply { this.version = version }
    fun getVersion(): Long {
        return version
    }

    fun build(): StoreShiftAssignmentsBatchRequest.Update {
        return StoreShiftAssignmentsBatchRequest.Update(
            assignmentId = assignmentId,
            timeRange = timeRange,
            metadata = metadata,
            version = version
        )
    }
}

class CancellationBuilder {
    private lateinit var assignmentId: UUID
    fun assignmentId(assignmentId: UUID) = apply { this.assignmentId = assignmentId }
    fun getAssignmentId(): UUID {
        return assignmentId
    }

    private var version: Long = 1
    fun version(version: Long) = apply { this.version = version }
    fun getVersion(): Long {
        return version
    }

    private lateinit var reason: ApiEnum<ShiftAssignmentCancellationReason, String>
    fun reason(reason: ApiEnum<ShiftAssignmentCancellationReason, String> ) = apply { this.reason = reason }
    fun getReason(): ApiEnum<ShiftAssignmentCancellationReason, String>  {
        return reason
    }

    fun build(): StoreShiftAssignmentsBatchRequest.Cancellation {
        return StoreShiftAssignmentsBatchRequest.Cancellation(
            assignmentId = assignmentId,
            version = version,
            reason = reason
        )
    }
}