package ru.samokat.mysamokat.tests.checkers

import io.qameta.allure.Step
import org.assertj.core.api.SoftAssertions
import org.jetbrains.exposed.sql.ResultRow
import org.springframework.stereotype.Service
import ru.samokat.my.domain.shifts.ShiftAssignmentCancellationReason
import ru.samokat.my.domain.shifts.TimeRange
import ru.samokat.my.rest.api.enum.ApiEnum
import ru.samokat.mysamokat.tests.SuiteBase
import ru.samokat.mysamokat.tests.dataproviders.Constants
import ru.samokat.mysamokat.tests.dataproviders.helpers.database.shifts_backend.ShiftsAssignment
import ru.samokat.mysamokat.tests.dataproviders.helpers.database.shifts_backend.ShiftsAssignmentLog
import ru.samokat.mysamokat.tests.dataproviders.helpers.database.shifts_backend.ShiftsAssignmentsLogData
import ru.samokat.mysamokat.tests.helpers.controllers.events.shifts.ActiveShiftsLog
import ru.samokat.mysamokat.tests.helpers.controllers.events.shifts.FirstShiftSchedule
import ru.samokat.mysamokat.tests.helpers.controllers.events.shifts.ShiftAssignmentsLog
import ru.samokat.shifts.api.activeshifts.ActiveShiftView
import ru.samokat.shifts.api.activeshifts.getlist.ActiveShiftsListView
import ru.samokat.shifts.api.activeshifts.start.StartShiftRequest
import ru.samokat.shifts.api.aggregates.statistics.ShiftAggregatedStatisticView
import ru.samokat.shifts.api.assignments.ShiftAssignmentStatus
import ru.samokat.shifts.api.assignments.ShiftAssignmentView
import ru.samokat.shifts.api.assignments.storebatch.StoreShiftAssignmentsBatchRequest
import ru.samokat.shifts.api.common.domain.DeliveryMethod
import ru.samokat.shifts.api.common.domain.ShiftUserPermission
import ru.samokat.shifts.api.common.domain.ShiftUserRole
import ru.samokat.shifts.api.schedules.ShiftScheduleView
import ru.samokat.shifts.api.schedules.search.ShiftSchedulesListView
import ru.samokat.shifts.api.schedules.store.StoreScheduleRequest
import ru.samokat.shifts.api.workedout.ShiftStopType
import ru.samokat.shifts.api.workedout.WorkedOutShiftView
import ru.samokat.shifts.api.workedout.search.WorkedOutShiftsListView
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.*

@Service
class ShiftAssertion {

    private val softAssertion = SoftAssertions()
    fun getSoftAssertion(): SoftAssertions {
        return softAssertion
    }

    fun assertAll() {
        getSoftAssertion().assertAll()
    }

    // active shifts
    @Step("check start shift info")
    fun checkStartShiftInfo(
        request: StartShiftRequest,
        openShift: ActiveShiftView,
        deliveryMethod: ApiEnum<DeliveryMethod, String>? = null,
        startDateTime: Instant
    ): ShiftAssertion {

        getSoftAssertion().assertThat(openShift.userId).isEqualTo(request.userId)
        getSoftAssertion().assertThat(openShift.userRole).isEqualTo(request.userRole)
        getSoftAssertion().assertThat(openShift.darkstoreId).isEqualTo(request.darkstoreId)
        getSoftAssertion().assertThat(openShift.deliveryMethod).isEqualTo(deliveryMethod)
        getSoftAssertion().assertThat((Duration.between(startDateTime, openShift.startedAt).toMinutes()) <= 1).isTrue

        return this
    }

    @Step("check user permissions")
    fun checkUserPermissions(
        actual: List<ApiEnum<ShiftUserPermission, String>>,
        expected: List<ApiEnum<ShiftUserPermission, String>>
    ): ShiftAssertion {

        getSoftAssertion().assertThat(expected.count()).isEqualTo(actual.count())
        getSoftAssertion().assertThat(expected.containsAll(actual)).isTrue

        return this
    }

    @Step("check darkstore shift info")
    fun checkDarkstoreShiftInfo(
        request: StartShiftRequest,
        openShifts: ActiveShiftsListView,
        deliveryMethod: ApiEnum<DeliveryMethod, String>? = null,
        startDateTime: Instant
    ): ShiftAssertion {

        val filtered = openShifts.shifts.filter { it.userId == request.userId }.first()

        getSoftAssertion().assertThat(filtered.userId).isEqualTo(request.userId)
        getSoftAssertion().assertThat(filtered.userRole).isEqualTo(request.userRole)
        getSoftAssertion().assertThat(filtered.darkstoreId).isEqualTo(request.darkstoreId)
        getSoftAssertion().assertThat(filtered.deliveryMethod).isEqualTo(deliveryMethod)
        getSoftAssertion().assertThat((Duration.between(startDateTime, filtered.startedAt).toMinutes()) <= 1).isTrue

        return this
    }

    @Step("check workedOut shift info")
    fun checkWorkedOutShiftInfoInList(
        shift: ActiveShiftView,
        shifts: WorkedOutShiftsListView,
        deliveryMethod: ApiEnum<DeliveryMethod, String>? = null
    ): ShiftAssertion {

        val filtered = shifts.shifts.filter { it.shiftId == shift.shiftId }.first()

        getSoftAssertion().assertThat(filtered.userId).isEqualTo(shift.userId)
        getSoftAssertion().assertThat(filtered.userRole).isEqualTo(shift.userRole)
        getSoftAssertion().assertThat(filtered.darkstoreId).isEqualTo(shift.darkstoreId)
        getSoftAssertion().assertThat(filtered.deliveryMethod).isEqualTo(deliveryMethod)

        return this
    }

    @Step("check workedOut shift info")
    fun checkWorkedOutShiftInfo(
        activeShift: ActiveShiftView,
        workedOutShift: WorkedOutShiftView,
        deliveryMethod: ApiEnum<DeliveryMethod, String>? = null,
        stopType: ShiftStopType = ShiftStopType.MANUAL
    ): ShiftAssertion {

        getSoftAssertion().assertThat(workedOutShift.userId).isEqualTo(activeShift.userId)
        getSoftAssertion().assertThat(workedOutShift.userRole).isEqualTo(activeShift.userRole)
        getSoftAssertion().assertThat(workedOutShift.darkstoreId).isEqualTo(activeShift.darkstoreId)
        getSoftAssertion().assertThat(workedOutShift.deliveryMethod).isEqualTo(deliveryMethod)
        getSoftAssertion().assertThat(workedOutShift.stopType.enumValue).isEqualTo(stopType)

        return this
    }

    @Step("Check shift present in darkstore shifts list")
    fun checkShiftIsPresentInList(openShifts: ActiveShiftsListView, profileId: UUID): ShiftAssertion {
        val filtered = openShifts.shifts.filter { it.userId == profileId }
        getSoftAssertion().assertThat(filtered.size).isNotEqualTo(0)
        return this
    }

    @Step("Check shift present in workedOut shifts list")
    fun checkShiftIsPresentInList(workedOut: WorkedOutShiftsListView, shiftId: UUID): ShiftAssertion {
        val filtered = workedOut.shifts.filter { it.shiftId == shiftId }
        getSoftAssertion().assertThat(filtered.size).isNotEqualTo(0)
        return this
    }

    @Step("Check shift not present in darkstore shifts list")
    fun checkShiftIsNotPresentInList(openShifts: ActiveShiftsListView, profileId: UUID): ShiftAssertion {
        val filtered = openShifts.shifts.filter { it.userId == profileId }
        getSoftAssertion().assertThat(filtered.size).isEqualTo(0)
        return this
    }

    @Step("Check shift not present in workedOut shifts list")
    fun checkShiftIsNotPresentInList(workedOut: WorkedOutShiftsListView, shiftId: UUID): ShiftAssertion {
        val filtered = workedOut.shifts.filter { it.shiftId == shiftId }
        getSoftAssertion().assertThat(filtered.size).isEqualTo(0)
        return this
    }

    @Step("Check shifts list count")
    fun checkShiftsListCount(openShifts: ActiveShiftsListView, expectedCount: Int): ShiftAssertion {
        getSoftAssertion().assertThat(openShifts.shifts.count()).isEqualTo(expectedCount)
        return this
    }

    @Step("Check shifts list count")
    fun checkShiftsListCount(workedOut: WorkedOutShiftsListView, expectedCount: Int): ShiftAssertion {
        getSoftAssertion().assertThat(workedOut.shifts.count()).isEqualTo(expectedCount)
        return this
    }

    @Step("Check error message")
    fun checkErrorMessage(actual: String, expected: String): ShiftAssertion {
        getSoftAssertion().assertThat(actual).isEqualTo(expected)
        return this
    }

    // Schedule
    @Step("Check schedule count")
    fun checkScheduleListCount(schedules: ShiftSchedulesListView, expectedCount: Int): ShiftAssertion {
        getSoftAssertion().assertThat(schedules.schedules.count()).isEqualTo(expectedCount)
        return this
    }

    @Step("Check schedule info")
    fun checkScheduleInfo(schedules: ShiftSchedulesListView, expectedShifts: StoreScheduleRequest): ShiftAssertion {
        expectedShifts.schedule.forEach {
            getSoftAssertion().assertThat(
                schedules.schedules.contains(
                    ShiftScheduleView(
                        expectedShifts.userId,
                        TimeRange(it.startingAt, it.endingAt)
                    )
                )
            ).isTrue
        }
        return this
    }

    @Step("Check schedule info")
    fun checkScheduleInfo(schedules: ShiftSchedulesListView, expectedShift: ShiftScheduleView): ShiftAssertion {
        getSoftAssertion().assertThat(schedules.schedules.contains(expectedShift)).isTrue
        return this
    }

    // assignments
    @Step("Check assignments info - creation")
    fun checkAssignmentsInfo(
        assignment: ShiftAssignmentView,
        batch: StoreShiftAssignmentsBatchRequest,
        creation: StoreShiftAssignmentsBatchRequest.Creation,
        version: Long = 1L,
        status: ShiftAssignmentStatus = ShiftAssignmentStatus.ASSIGNED
    ): ShiftAssertion {

        getSoftAssertion().assertThat(assignment.userId).isEqualTo(creation.assigneeId)
        getSoftAssertion().assertThat(assignment.darkstoreId).isEqualTo(batch.darkstoreId)
        getSoftAssertion().assertThat(assignment.timeRange).isEqualTo(creation.timeRange)
        getSoftAssertion().assertThat(assignment.status.value).isEqualTo(status.value)
        getSoftAssertion().assertThat(assignment.version).isEqualTo(version)

        return this
    }

    @Step("Check assignments info - updation")
    fun checkAssignmentsInfo(
        profileId: UUID,
        assignment: ShiftAssignmentView,
        batch: StoreShiftAssignmentsBatchRequest,
        updation: StoreShiftAssignmentsBatchRequest.Update,
        version: Long = 2L,
        status: ShiftAssignmentStatus = ShiftAssignmentStatus.ASSIGNED
    ): ShiftAssertion {

        getSoftAssertion().assertThat(assignment.userId).isEqualTo(profileId)
        getSoftAssertion().assertThat(assignment.darkstoreId).isEqualTo(batch.darkstoreId)
        getSoftAssertion().assertThat(assignment.timeRange).isEqualTo(updation.timeRange)
        getSoftAssertion().assertThat(assignment.status.value).isEqualTo(status.value)
        getSoftAssertion().assertThat(assignment.version).isEqualTo(version)

        return this
    }

    @Step("Check assignments info - deleted")
    fun checkCancelledAssignmentsInfo(
        profileId: UUID,
        assignment: ShiftAssignmentView,
        batch: StoreShiftAssignmentsBatchRequest,
        cancellation: StoreShiftAssignmentsBatchRequest.Cancellation,
        version: Long = 2L,
        status: ShiftAssignmentStatus = ShiftAssignmentStatus.CANCELED
    ): ShiftAssertion {

        getSoftAssertion().assertThat(assignment.userId).isEqualTo(profileId)
        getSoftAssertion().assertThat(assignment.darkstoreId).isEqualTo(batch.darkstoreId)
        getSoftAssertion().assertThat(assignment.status.value).isEqualTo(status.value)
        getSoftAssertion().assertThat(assignment.cancellationReason).isEqualTo(cancellation.reason)
        getSoftAssertion().assertThat(assignment.version).isEqualTo(version)

        return this
    }

    @Step("Check assignment from db")
    fun checkAssignmentFromDB(
        assignmentFromDB: ResultRow,
        batch: StoreShiftAssignmentsBatchRequest,
        creation: StoreShiftAssignmentsBatchRequest.Creation,
        cancellationReason: String? = null,
        status: String = "assigned",
        version: Long = 1L,
    ): ShiftAssertion {

        getSoftAssertion().assertThat(assignmentFromDB[ShiftsAssignment.darkstoreId]).isEqualTo(batch.darkstoreId)
        getSoftAssertion().assertThat(assignmentFromDB[ShiftsAssignment.userId]).isEqualTo(creation.assigneeId)
        getSoftAssertion().assertThat(assignmentFromDB[ShiftsAssignment.cancellationReason])
            .isEqualTo(cancellationReason)
        getSoftAssertion().assertThat(assignmentFromDB[ShiftsAssignment.status]).isEqualTo(status)
        getSoftAssertion().assertThat(assignmentFromDB[ShiftsAssignment.version]).isEqualTo(version)
        return this
    }

    @Step("Check assignment from db")
    fun checkAssignmentFromDB(
        profileId: UUID,
        assignmentFromDB: ResultRow,
        batch: StoreShiftAssignmentsBatchRequest,
        cancellationReason: String? = null,
        status: String = "assigned",
        version: Long = 2L,
    ): ShiftAssertion {

        getSoftAssertion().assertThat(assignmentFromDB[ShiftsAssignment.darkstoreId]).isEqualTo(batch.darkstoreId)
        getSoftAssertion().assertThat(assignmentFromDB[ShiftsAssignment.userId]).isEqualTo(profileId)
        getSoftAssertion().assertThat(assignmentFromDB[ShiftsAssignment.cancellationReason])
            .isEqualTo(cancellationReason)
        getSoftAssertion().assertThat(assignmentFromDB[ShiftsAssignment.status]).isEqualTo(status)
        getSoftAssertion().assertThat(assignmentFromDB[ShiftsAssignment.version]).isEqualTo(version)
        return this
    }

    @Step("Check assignment from db")
    fun checkCancelledAssignmentFromDB(
        profileId: UUID,
        assignmentFromDB: ResultRow,
        batch: StoreShiftAssignmentsBatchRequest,
        cancellation: StoreShiftAssignmentsBatchRequest.Cancellation,
        status: String = "canceled",
        version: Long = 2L,
    ): ShiftAssertion {

        getSoftAssertion().assertThat(assignmentFromDB[ShiftsAssignment.darkstoreId]).isEqualTo(batch.darkstoreId)
        getSoftAssertion().assertThat(assignmentFromDB[ShiftsAssignment.userId]).isEqualTo(profileId)
        getSoftAssertion().assertThat(assignmentFromDB[ShiftsAssignment.cancellationReason])
            .isEqualTo(cancellation.reason.value)
        getSoftAssertion().assertThat(assignmentFromDB[ShiftsAssignment.status]).isEqualTo(status)
        getSoftAssertion().assertThat(assignmentFromDB[ShiftsAssignment.version]).isEqualTo(version)
        return this
    }

    @Step("Check assignment log from db")
    fun checkAssignmentLogFromDB(
        assignmentLog: ResultRow,
        batch: StoreShiftAssignmentsBatchRequest,
        creation: StoreShiftAssignmentsBatchRequest.Creation,
        version: Long = 1L,
        type: String = "assignment"
    ): ShiftAssertion {
        getSoftAssertion().assertThat(assignmentLog[ShiftsAssignmentLog.version]).isEqualTo(version)
        getSoftAssertion().assertThat(assignmentLog[ShiftsAssignmentLog.eventType]).isEqualTo(type)
        getSoftAssertion().assertThat(assignmentLog[ShiftsAssignmentLog.issuerId]).isEqualTo(batch.issuerId)

        val data = SuiteBase.jacksonObjectMapper.readValue(
            assignmentLog[ShiftsAssignmentLog.data],
            ShiftsAssignmentsLogData::class.java
        )
        getSoftAssertion().assertThat(data.darkstoreId).isEqualTo(batch.darkstoreId)
        getSoftAssertion().assertThat(data.userId).isEqualTo(creation.assigneeId)
        getSoftAssertion().assertThat(data.timeRangeStart).isEqualTo(creation.timeRange.startingAt.toString())
        getSoftAssertion().assertThat(data.timeRangeEnd).isEqualTo(creation.timeRange.endingAt.toString())

        return this
    }

    @Step("Check assignment log from db")
    fun checkAssignmentLogFromDB(
        profileId: UUID,
        assignmentLog: ResultRow,
        batch: StoreShiftAssignmentsBatchRequest,
        updation: StoreShiftAssignmentsBatchRequest.Update,
        version: Long = 2L,
        type: String = "update"
    ): ShiftAssertion {
        getSoftAssertion().assertThat(assignmentLog[ShiftsAssignmentLog.version]).isEqualTo(version)
        getSoftAssertion().assertThat(assignmentLog[ShiftsAssignmentLog.eventType]).isEqualTo(type)
        getSoftAssertion().assertThat(assignmentLog[ShiftsAssignmentLog.issuerId]).isEqualTo(batch.issuerId)

        val data = SuiteBase.jacksonObjectMapper.readValue(
            assignmentLog[ShiftsAssignmentLog.data],
            ShiftsAssignmentsLogData::class.java
        )
        getSoftAssertion().assertThat(data.timeRangeStart).isEqualTo(updation.timeRange.startingAt.toString())
        getSoftAssertion().assertThat(data.timeRangeEnd).isEqualTo(updation.timeRange.endingAt.toString())

        return this
    }

    @Step("Check assignment log from db")
    fun checkCancelledAssignmentLogFromDB(
        profileId: UUID,
        assignmentLog: ResultRow,
        batch: StoreShiftAssignmentsBatchRequest,
        version: Long = 2L,
        type: String = "cancellation"
    ): ShiftAssertion {
        getSoftAssertion().assertThat(assignmentLog[ShiftsAssignmentLog.version]).isEqualTo(version)
        getSoftAssertion().assertThat(assignmentLog[ShiftsAssignmentLog.eventType]).isEqualTo(type)
        getSoftAssertion().assertThat(assignmentLog[ShiftsAssignmentLog.issuerId]).isEqualTo(batch.issuerId)

        return this
    }

    @Step("Check auto cancelled assignments")
    fun checkAutoCancelledAssignments(
        assignmentFromDB: ResultRow,
        status: String = "canceled",
        cancellationReason: String? = "assignee_retirement",
    ): ShiftAssertion {

        getSoftAssertion().assertThat(assignmentFromDB[ShiftsAssignment.cancellationReason])
            .isEqualTo(cancellationReason)
        getSoftAssertion().assertThat(assignmentFromDB[ShiftsAssignment.status]).isEqualTo(status)
        return this
    }

    @Step("Check assignment present in assignments list")
    fun checkAssignmentPresentInList(assignments: List<ShiftAssignmentView>, assignmentId: UUID): ShiftAssertion {
        val filtered = assignments.filter { it.assignmentId == assignmentId }
        getSoftAssertion().assertThat(filtered.size).isNotEqualTo(0)
        return this
    }

    @Step("Check assignment present in assignments list")
    fun checkAssignmentNotPresentInList(assignments: List<ShiftAssignmentView>, assignmentId: UUID): ShiftAssertion {
        val filtered = assignments.filter { it.assignmentId == assignmentId }
        getSoftAssertion().assertThat(filtered.size).isEqualTo(0)
        return this
    }

    @Step("assert assignment list count")
    fun checkAssignmentListCount(
        assignments: List<ShiftAssignmentView>,
        expectedCount: Int
    ): ShiftAssertion {
        getSoftAssertion().assertThat(assignments.count()).isEqualTo(expectedCount)
        return this
    }

    @Step("Check list element")
    fun checkAssignmentFieldsInList(
        assignments: List<ShiftAssignmentView>,
        assignmentId: UUID,
        timeRange: TimeRange,
        profileId: UUID,
        darkstoreId: UUID = Constants.darkstoreId,
        version: Long = 1L,
        cancellationReason: ShiftAssignmentCancellationReason? = null,
        status: String = "assigned",
        userRole: ShiftUserRole = ShiftUserRole.DELIVERYMAN
    ): ShiftAssertion {

        val filtered = assignments.filter { it.assignmentId == assignmentId }.firstOrNull()

        getSoftAssertion().assertThat(filtered!!.version).isEqualTo(version)
        getSoftAssertion().assertThat(filtered.cancellationReason?.value).isEqualTo(cancellationReason?.value)
        getSoftAssertion().assertThat(filtered.status.value).isEqualTo(status)
        getSoftAssertion().assertThat(filtered.timeRange).isEqualTo(timeRange)
        getSoftAssertion().assertThat(filtered.darkstoreId).isEqualTo(darkstoreId)
        getSoftAssertion().assertThat(filtered.userId).isEqualTo(profileId)
        getSoftAssertion().assertThat(filtered.userRole.value).isEqualTo(userRole.value)

        return this
    }

    @Step("Check statistics data")
    fun checkStats(
        stats: ShiftAggregatedStatisticView.UserStatistics,
        scheduleDuration: Long = 0,
        assignmentDuration: Long = 0,
        assignmentCount: Int = 0,
        workedOutCount: Int = 0
    ): ShiftAssertion {

        getSoftAssertion().assertThat(stats?.schedule?.duration.toHours()).isEqualTo(scheduleDuration)
        getSoftAssertion().assertThat(stats?.assignments?.duration.toHours()).isEqualTo(assignmentDuration)
        getSoftAssertion().assertThat(stats?.assignments?.count.assigned).isEqualTo(assignmentCount)
        getSoftAssertion().assertThat(stats?.workedOutShifts?.count).isEqualTo(workedOutCount)


        return this
    }


    // Kafka
    @Step("Check active_shifts_log message")
    fun checkActiveShiftsLogMessage(
        shift: ActiveShiftView,
        event: ActiveShiftsLog,
        status: String = "started",
        stopType: String? = null
    ) {
        getSoftAssertion().assertThat(shift.shiftId).isEqualTo(event.shiftId)
        getSoftAssertion().assertThat(shift.userId).isEqualTo(event.userId)
        getSoftAssertion().assertThat(shift.userRole.value).isEqualTo(event.userRole)
        getSoftAssertion().assertThat(shift.darkstoreId).isEqualTo(event.darkstoreId)
        getSoftAssertion().assertThat(event.status).isEqualTo(status)

        if (status == "stopped")
            getSoftAssertion().assertThat(event.stopType).isEqualTo(stopType)

        getSoftAssertion().assertThat(convertDateTimeFromStringToInstant(event.startedAt))
            .isEqualTo(shift.startedAt)
    }

    @Step("Check active_shifts_log message")
    fun checkActiveShiftsLogStopMessage(
        shift: ActiveShiftView,
        events: List<ActiveShiftsLog>,
        stopType: String = "manual"
    ) {
        val filteredEvents = events.filter { it.status == "stopped" }.first()
        getSoftAssertion().assertThat(shift.shiftId).isEqualTo(filteredEvents.shiftId)
        getSoftAssertion().assertThat(shift.userId).isEqualTo(filteredEvents.userId)
        getSoftAssertion().assertThat(shift.userRole.value).isEqualTo(filteredEvents.userRole)
        getSoftAssertion().assertThat(shift.darkstoreId).isEqualTo(filteredEvents.darkstoreId)
        getSoftAssertion().assertThat(filteredEvents.stopType).isEqualTo(stopType)

        getSoftAssertion().assertThat(convertDateTimeFromStringToInstant(filteredEvents.startedAt))
            .isEqualTo(shift.startedAt)
    }

    @Step("Check ShiftAssignmentsLog message")
    fun checkShiftAssignmentsLogMessage(
        event: ShiftAssignmentsLog,
        batch: StoreShiftAssignmentsBatchRequest,
        creation: StoreShiftAssignmentsBatchRequest.Creation,
        version: Long = 1L,
        status: String = "assigned",
        cancellationReason: String? = null
    ) {
        getSoftAssertion().assertThat(event.userId).isEqualTo(creation.assigneeId.toString())
        getSoftAssertion().assertThat(event.darkstoreId).isEqualTo(batch.darkstoreId.toString())
        getSoftAssertion().assertThat(event.status).isEqualTo(status)
        getSoftAssertion().assertThat(event.version).isEqualTo(version)

        getSoftAssertion().assertThat(convertDateTimeFromStringToInstant(event.timeRange.startingAt))
            .isEqualTo(creation.timeRange.startingAt)
        getSoftAssertion().assertThat(convertDateTimeFromStringToInstant(event.timeRange.endingAt))
            .isEqualTo(creation.timeRange.endingAt)

        if (status == "canceled")
            getSoftAssertion().assertThat(event.cancellationReason).isEqualTo(cancellationReason)


    }

    @Step("Check ShiftAssignmentsLog message")
    fun checkShiftAssignmentsLogMessage(
        event: ShiftAssignmentsLog,
        batch: StoreShiftAssignmentsBatchRequest,
        creation: StoreShiftAssignmentsBatchRequest.Creation,
        updation: StoreShiftAssignmentsBatchRequest.Update,
        version: Long = 1L,
        status: String = "assigned"
    ) {
        getSoftAssertion().assertThat(event.userId).isEqualTo(creation.assigneeId.toString())
        getSoftAssertion().assertThat(event.darkstoreId).isEqualTo(batch.darkstoreId.toString())
        getSoftAssertion().assertThat(event.status).isEqualTo(status)
        getSoftAssertion().assertThat(event.version).isEqualTo(version)

        getSoftAssertion().assertThat(convertDateTimeFromStringToInstant(event.timeRange.startingAt))
            .isEqualTo(updation.timeRange.startingAt)
        getSoftAssertion().assertThat(convertDateTimeFromStringToInstant(event.timeRange.endingAt))
            .isEqualTo(updation.timeRange.endingAt)
    }

    fun convertDateTimeFromStringToInstant(dateTimeStr: String): Instant? {
        return Instant.parse(
            LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                .atZone(ZoneId.of("GMT")).toInstant().toString()
        )
    }

    @Step("Check message exists")
    fun checkFirstTimeSlotEvent(event: FirstShiftSchedule?, profileId: UUID){
        getSoftAssertion().assertThat(event!!.userId).isEqualTo(profileId)
    }


    @Step("Check messages count")
    fun checkMessagesCount(actual: Int, expectedCount: Int){
        getSoftAssertion().assertThat(actual).isEqualTo(expectedCount)
    }

}