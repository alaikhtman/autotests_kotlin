package ru.samokat.mysamokat.tests.dataproviders.preconditions

import com.fasterxml.jackson.databind.JsonNode
import io.qameta.allure.Step
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.samokat.employeeprofiles.api.common.domain.Vehicle
import ru.samokat.my.domain.PhoneNumber
import ru.samokat.my.domain.employee.EmployeeRole
import ru.samokat.my.domain.shifts.ShiftAssignmentCancellationReason
import ru.samokat.my.domain.shifts.TimeRange
import ru.samokat.my.rest.api.enum.ApiEnum
import ru.samokat.mysamokat.tests.dataproviders.Constants
import ru.samokat.mysamokat.tests.dataproviders.shifts.*
import ru.samokat.mysamokat.tests.helpers.actions.EmployeeActions
import ru.samokat.shifts.api.activeshifts.getlist.GetActiveShiftsListRequest
import ru.samokat.shifts.api.activeshifts.start.StartShiftRequest
import ru.samokat.shifts.api.activeshifts.stop.StopShiftRequest
import ru.samokat.shifts.api.aggregates.statistics.GetShiftAggregatedStatisticRequest
import ru.samokat.shifts.api.assignments.ShiftAssignmentStatus
import ru.samokat.shifts.api.assignments.search.SearchShiftAssignmentsRequest
import ru.samokat.shifts.api.assignments.storebatch.StoreShiftAssignmentsBatchRequest
import ru.samokat.shifts.api.common.domain.AssigneeRole
import ru.samokat.shifts.api.common.domain.DeliveryMethod
import ru.samokat.shifts.api.common.domain.ShiftUserPermission
import ru.samokat.shifts.api.common.domain.ShiftUserRole
import ru.samokat.shifts.api.schedules.search.SearchShiftSchedulesRequest
import ru.samokat.shifts.api.schedules.store.StoreScheduleRequest
import ru.samokat.shifts.api.workedout.search.SearchWorkedOutShiftsRequest
import java.util.*

@Service
class ShiftsPreconditions {

    private lateinit var employeePreconditions: EmployeePreconditions

    @Autowired
    private lateinit var employeeActions: EmployeeActions

    private lateinit var profileId: UUID
    fun profileId(): UUID {
        return profileId
    }

    private lateinit var storeScheduleRequest: StoreScheduleRequest
    fun storeScheduleRequest(): StoreScheduleRequest {
        return storeScheduleRequest
    }

    @Step("Create test profile")
    fun setCreateProfile(
        roles: List<ApiEnum<EmployeeRole, String>>,
        mobile: PhoneNumber = Constants.mobile1,
        darkstoreId: UUID? = Constants.darkstoreId,
        vehicle: Vehicle? = null,
    ): UUID {
        return employeeActions.createProfileId(
            employeePreconditions.fillCreateProfileRequest(
                roles = roles,
                mobile = mobile,
                darkstoreId = darkstoreId,
                vehicle = vehicle
            )
        )
    }

    @Step("Fill start shift request")
    fun fillStartShiftRequest(
        darkstoreId: UUID = Constants.darkstoreId,
        userId: UUID,
        userRole: ShiftUserRole = ShiftUserRole.DELIVERYMAN,
        deliveryMethod: DeliveryMethod? = null
    ): StartShiftRequest {
        return StartShiftRequestBuilder()
            .darkstoreId(darkstoreId)
            .userId(userId)
            .userRole(userRole)
            .deliveryMethod(deliveryMethod)
            .build()
    }

    @Step("Fill get active shifts list request")
    fun fillGetActiveShiftListRequest(
        darkstoreId: UUID = Constants.darkstoreId,
        userPermission: ShiftUserPermission? = null
    ): GetActiveShiftsListRequest {
        return GetActiveShiftsListRequestBuilder()
            .darkstoreId(darkstoreId)
            .userPermission(userPermission)
            .build()
    }

    @Step("Fill stop shift request")
    fun fillStopShiftRequest(
        userId: UUID
    ): StopShiftRequest {
        return StopShiftRequestBuilder()
            .userId(userId)
            .build()
    }

    @Step("Set store schedule request")
    fun fillStoreScheduleRequest(
        userId: UUID,
        schedule: List<TimeRange>,
        timeRange: TimeRange
    ): StoreScheduleRequest {
        return StoreScheduleRequestBuilder()
            .userId(userId)
            .schedule(schedule)
            .timeRange(timeRange)
            .build()
    }

    @Step("Set store schedule request")
    fun fillSearchScheduleRequest(
        timeRange: TimeRange,
        userIds: Set<UUID>,
        paging: SearchShiftSchedulesRequest.PagingData = SearchShiftSchedulesRequest.PagingData(null, null)
    ): SearchShiftSchedulesRequest {
        return SearchShiftSchedulesRequestBuilder()
            .filter(
                SearchShiftSchedulesRequestFilterBuilder()
                    .timeRange(timeRange)
                    .userIds(userIds)
                    .build()
            )
            .paging(paging)
            .build()
    }

    @Step("Fill search workerd out shifts request")
    fun fillSearchWorkedOutShiftsRequests(
        timeRange: TimeRange,
        darkstoreId: UUID = Constants.darkstoreId,
        userIds: Set<UUID>,
        userRoles: Set<ShiftUserRole>? = null,
        paging: SearchWorkedOutShiftsRequest.PagingData = SearchWorkedOutShiftsRequest.PagingData(null, null)
    ): SearchWorkedOutShiftsRequest {
        return SearchWorkedOutShiftsRequestBuilder()
            .filter(
                SearchWorkedOutShiftFilterBuilder()
                    .timeRange(timeRange)
                    .darkstoreId(darkstoreId)
                    .userIds(userIds)
                    .userRoles(userRoles)
                    .build()
            )
            .paging(paging)
            .build()
    }

    // assignments

    @Step("Fill search assignments request")
    fun fillSearchAssignmentsRequest(
        timeRange: TimeRange,
        darkstoreId: UUID = Constants.darkstoreId,
        assigneeRoles: Set<AssigneeRole>? = null,
        statuses: Set<ShiftAssignmentStatus>? = null,
        metadata: SearchShiftAssignmentsRequest.MetadataFilter? = null,
        paging: SearchShiftAssignmentsRequest.PagingData? = null
    ): SearchShiftAssignmentsRequest {
        return SearchShiftAssignmentsRequestBuilder()
            .filter(
                FilterBuilder()
                    .timeRange(timeRange)
                    .darkstoreId(darkstoreId)
                    .assigneeRoles(assigneeRoles)
                    .statuses(statuses)
                    .build()
            )
            .metadata(metadata)
            .paging(paging)
            .build()
    }

    @Step ("Fill batch assignments request")
    fun fillBatchAssignmentsRequest(
        creations: List<StoreShiftAssignmentsBatchRequest.Creation>? = null,
        updates: List<StoreShiftAssignmentsBatchRequest.Update>? = null,
        cancellations: List<StoreShiftAssignmentsBatchRequest.Cancellation>? = null,
        darkstoreId: UUID = Constants.darkstoreId,
        assignmentsRole: ApiEnum<AssigneeRole, String>,
        editingTimeRange: TimeRange
    ): StoreShiftAssignmentsBatchRequest {
        return StoreShiftAssignmentsBatchRequestBuilder()
            .batch(StoreShiftAssignmentsBatchBuilder()
                .creations(creations)
                .updates(updates)
                .cancellations(cancellations)
                .build())
            .darkstoreId(darkstoreId)
            .issuerId(UUID.randomUUID())
            .assignmentsRole(assignmentsRole)
            .editingTimeRange(editingTimeRange)
            .build()

    }

    fun fillCancellationBuilder(
        assignmentId: UUID,
        version: Long = 1,
        reason: ShiftAssignmentCancellationReason = ShiftAssignmentCancellationReason.MISTAKEN_ASSIGNMENT
    ): StoreShiftAssignmentsBatchRequest.Cancellation {

        return CancellationBuilder()
            .assignmentId(assignmentId)
            .version(version)
            .reason(ApiEnum(reason))
            .build()
    }

    fun fillCreationBuilder(
        clientAssignmentId: String = "1",
        assigneeId: UUID,
        timeRange: TimeRange,
        metadata: Map<String, JsonNode>? = null
    ): StoreShiftAssignmentsBatchRequest.Creation {

        return CreationBuilder()
            .clientAssignmentId(clientAssignmentId)
            .assigneeId(assigneeId)
            .timeRange(timeRange)
            .metadata(metadata)
            .build()
    }

    fun fillUpdationBuilder(
        assignmentId: UUID,
        timeRange: TimeRange,
        metadata: Map<String, JsonNode>? = null,
        version: Long = 1L
    ): StoreShiftAssignmentsBatchRequest.Update {
        return UpdateBuilder()
            .assignmentId(assignmentId)
            .timeRange(timeRange)
            .metadata(metadata)
            .version(version)
            .build()

    }

    fun fillGetStatisticsBuilder(timeRange: TimeRange, userIds: List<UUID>): GetShiftAggregatedStatisticRequest {
        return GetShiftAggregatedStatisticRequestBuilder()
            .timeRange(timeRange)
            .userIds(userIds)
            .build()
    }
}
