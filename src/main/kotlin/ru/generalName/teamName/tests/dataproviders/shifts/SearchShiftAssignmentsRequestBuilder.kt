package ru.samokat.mysamokat.tests.dataproviders.shifts

import ru.samokat.my.domain.shifts.TimeRange
import ru.samokat.shifts.api.assignments.ShiftAssignmentStatus
import ru.samokat.shifts.api.assignments.search.SearchShiftAssignmentsRequest
import ru.samokat.shifts.api.common.domain.AssigneeRole
import java.util.*

class SearchShiftAssignmentsRequestBuilder {

    private lateinit var filter: SearchShiftAssignmentsRequest.Filter
    fun filter(filter: SearchShiftAssignmentsRequest.Filter) = apply { this.filter = filter }
    fun getFilter(): SearchShiftAssignmentsRequest.Filter {
        return filter
    }

    private var metadata: SearchShiftAssignmentsRequest.MetadataFilter? = null
    fun metadata(metadata: SearchShiftAssignmentsRequest.MetadataFilter?) = apply { this.metadata = metadata }
    fun getMetadata(): SearchShiftAssignmentsRequest.MetadataFilter? {
        return metadata
    }

    private var paging: SearchShiftAssignmentsRequest.PagingData? = null
    fun paging(paging: SearchShiftAssignmentsRequest.PagingData?) = apply { this.paging = paging }
    fun getPaging(): SearchShiftAssignmentsRequest.PagingData? {
        return paging
    }

    fun build(): SearchShiftAssignmentsRequest {
        return SearchShiftAssignmentsRequest(
            filter = filter,
            metadata = metadata,
            paging = paging
        )
    }
}

class FilterBuilder{

    private lateinit var timeRange: TimeRange
    fun timeRange(timeRange: TimeRange) = apply { this.timeRange = timeRange }
    fun getTimeRange(): TimeRange {
        return timeRange
    }

    private lateinit var assigneeIds: Set<UUID>
    fun assigneeIds(assigneeIds: Set<UUID>) = apply { this.assigneeIds = assigneeIds }
    fun getAssigneeIds(): Set<UUID> {
        return assigneeIds
    }

    private lateinit var darkstoreId: UUID
    fun darkstoreId(darkstoreId: UUID) = apply { this.darkstoreId = darkstoreId }
    fun getDarkstoreId(): UUID {
        return darkstoreId
    }


    private var assigneeRoles: Set<AssigneeRole>? = null
    fun assigneeRoles(assigneeRoles: Set<AssigneeRole>? ) = apply { this.assigneeRoles = assigneeRoles }
    fun getAssigneeRoles(): Set<AssigneeRole>?  {
        return assigneeRoles
    }

    private var statuses: Set<ShiftAssignmentStatus>? = null
    fun statuses(statuses: Set<ShiftAssignmentStatus>? ) = apply { this.statuses = statuses }
    fun getStatuses(): Set<ShiftAssignmentStatus>? {
        return statuses
    }

    fun build(): SearchShiftAssignmentsRequest.Filter {
        return SearchShiftAssignmentsRequest.Filter(
            timeRange = timeRange,
            darkstoreId = darkstoreId,
            assigneeRoles = assigneeRoles,
            statuses = statuses
        )
    }

}

