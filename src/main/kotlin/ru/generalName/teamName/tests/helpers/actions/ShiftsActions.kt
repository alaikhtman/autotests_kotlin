package ru.samokat.mysamokat.tests.helpers.actions

import io.qameta.allure.Step
import org.jetbrains.exposed.sql.ResultRow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import ru.samokat.mysamokat.tests.SuiteBase
import ru.samokat.mysamokat.tests.helpers.controllers.KafkaController
import ru.samokat.mysamokat.tests.helpers.controllers.asClientError
import ru.samokat.mysamokat.tests.helpers.controllers.asSuccess
import ru.samokat.mysamokat.tests.helpers.controllers.database.ShiftsDatabaseController
import ru.samokat.mysamokat.tests.helpers.controllers.events.shifts.ActiveShiftsLog
import ru.samokat.mysamokat.tests.helpers.controllers.events.shifts.FirstShiftSchedule
import ru.samokat.mysamokat.tests.helpers.controllers.events.shifts.ShiftAssignmentsLog
import ru.samokat.mysamokat.tests.helpers.controllers.shifts.*
import ru.samokat.shifts.api.activeshifts.ActiveShiftView
import ru.samokat.shifts.api.activeshifts.get.GetActiveShiftError
import ru.samokat.shifts.api.activeshifts.getlist.ActiveShiftsListView
import ru.samokat.shifts.api.activeshifts.getlist.GetActiveShiftsListRequest
import ru.samokat.shifts.api.activeshifts.start.StartShiftError
import ru.samokat.shifts.api.activeshifts.start.StartShiftRequest
import ru.samokat.shifts.api.activeshifts.stop.StopShiftRequest
import ru.samokat.shifts.api.aggregates.statistics.GetShiftAggregatedStatisticRequest
import ru.samokat.shifts.api.aggregates.statistics.ShiftAggregatedStatisticView
import ru.samokat.shifts.api.assignments.ShiftAssignmentView
import ru.samokat.shifts.api.assignments.search.SearchShiftAssignmentsRequest
import ru.samokat.shifts.api.assignments.search.ShiftAssignmentsListView
import ru.samokat.shifts.api.assignments.storebatch.StoreShiftAssignmentsBatchError
import ru.samokat.shifts.api.assignments.storebatch.StoreShiftAssignmentsBatchRequest
import ru.samokat.shifts.api.assignments.storebatch.StoredShiftAssignmentsBatchView
import ru.samokat.shifts.api.schedules.search.SearchShiftSchedulesRequest
import ru.samokat.shifts.api.schedules.search.ShiftSchedulesListView
import ru.samokat.shifts.api.schedules.store.StoreScheduleError
import ru.samokat.shifts.api.schedules.store.StoreScheduleRequest
import ru.samokat.shifts.api.workedout.WorkedOutShiftView
import ru.samokat.shifts.api.workedout.search.SearchWorkedOutShiftsRequest
import ru.samokat.shifts.api.workedout.search.WorkedOutShiftsListView
import java.util.*


@Component
@Scope("prototype")
class ShiftsActions(
    private val kafkaActiveShiftsLog: KafkaController,
    private val kafkaShiftAssignmentsLog: KafkaController,
    private val kafkaFirstShiftsSchedule: KafkaController

) {

    @Autowired
    lateinit var schedulesController: ShiftSchedulesController

    @Autowired
    lateinit var activeShiftController: ActiveShiftsController

    @Autowired
    private lateinit var shiftDatabaseController: ShiftsDatabaseController

    @Autowired
    private lateinit var workedOutShiftsController: WorkedOutShiftsController

    @Autowired
    private lateinit var shiftsAssignmentsController: ShiftAssignmentsController

    @Autowired
    private lateinit var shiftsStatisticsController: ShiftAggregatesController

    // active shifts
    @Step("Start new shift")
    fun startShift(request: StartShiftRequest): ActiveShiftView {
        return activeShiftController.openShift(request)!!.asSuccess()
    }

    @Step("Start new shift with error")
    fun startShiftWithError(request: StartShiftRequest): StartShiftError {
        return activeShiftController.openShift(request)!!.asClientError()
    }

    @Step("Get active shift by profile id")
    fun getActiveShiftByProfileId(profileId: UUID): ActiveShiftView {
        return activeShiftController.getActiveShiftByProfileId(profileId)!!.asSuccess()
    }

    @Step("Get active shift by profile id")
    fun getActiveShiftByProfileIdWithError(profileId: UUID): GetActiveShiftError {
        return activeShiftController.getActiveShiftByProfileId(profileId)!!.asClientError()
    }

    @Step("Get active shifts by darkstore")
    fun getActiveShiftsByDarkstore(request: GetActiveShiftsListRequest): ActiveShiftsListView {
        return activeShiftController.getActiveShiftsByDarkstore(request)!!.asSuccess()
    }

    @Step("Stop shift")
    fun stopActiveShift(request: StopShiftRequest){
        return activeShiftController.stopActiveShift(request)!!.asSuccess()
    }

    @Step("Search workerd out shifts")
    fun searchWorkedOutShifts(request: SearchWorkedOutShiftsRequest): WorkedOutShiftsListView {
        return workedOutShiftsController.searchWorkedOutShifts(request)!!.asSuccess()
    }

    @Step("Get worked out shift by id")
    fun getWorkedOutShiftById(shiftId: UUID): WorkedOutShiftView {
        return workedOutShiftsController.getWorkedOutShiftById(shiftId)!!.asSuccess()
    }

    @Step("Get worked out shift by id with error")
    fun getWorkedOutShiftByIdWithError(shiftId: UUID): String {
        return workedOutShiftsController.getWorkedOutShiftById(shiftId)!!.asClientError().message
    }

    /*
    @Step("set shift timerange in database")
    fun setWorkedOutShiftTimeRange(shiftId: UUID, timerange: String){
        shiftDatabaseController.setWorkedOutShiftTimeRange(shiftId, timerange)
    }

     */

    // schedules

    @Step("Post schedules")
    fun postSchedules(request: StoreScheduleRequest){
        return schedulesController.postSchedules(request)!!.asSuccess()
    }

    @Step("Get schedules")
    fun getSchedules(request: SearchShiftSchedulesRequest): ShiftSchedulesListView {
        return schedulesController.searchSchedules(request)!!.asSuccess()
    }

    @Step("Post schedules with error")
    fun postSchedulesWithError(request: StoreScheduleRequest): StoreScheduleError {
        return schedulesController.postSchedules(request)!!.asClientError()
    }

    // assignments

    @Step("Search assignments")
    fun searchAssignments(request: SearchShiftAssignmentsRequest): ShiftAssignmentsListView {
        return shiftsAssignmentsController.searchAssignments(request)!!.asSuccess()
    }

    @Step("Batch assignments")
    fun batchAssignments(request: StoreShiftAssignmentsBatchRequest): StoredShiftAssignmentsBatchView {
        return shiftsAssignmentsController.storeBatchAssignments(request)!!.asSuccess()
    }

    @Step("Batch assignments with error")
    fun batchAssignmentsWithError(request: StoreShiftAssignmentsBatchRequest): StoreShiftAssignmentsBatchError {
        return shiftsAssignmentsController.storeBatchAssignments(request)!!.asClientError()
    }

    @Step("Get assignments by id")
    fun getAssignmentsById(assignmentId: UUID): ShiftAssignmentView {
        return shiftsAssignmentsController.getAssignmentsById(assignmentId)!!.asSuccess()
    }

    @Step("Get shifts statistics")
    fun getStatistics(request: GetShiftAggregatedStatisticRequest): ShiftAggregatedStatisticView {
        return shiftsStatisticsController.getAgregateStatistics(request)!!.asSuccess()
    }

    // DB
    @Step("Get schedules from database")
    fun getSchedulesFromDatabase(profileId: UUID): MutableList<ResultRow>  {
        return shiftDatabaseController.getSchedules(profileId)
    }

    @Step ("Get assignment_log from database")
    fun getAssignmentLogFromDatabaseByType(assignmentId: UUID, eventType: String): ResultRow? {
        return  shiftDatabaseController.getAssignmentLogByType(assignmentId, eventType)
    }

    @Step("Get assignment from database")
    fun getAssignmentFromDatabase(assignmentId: UUID):ResultRow{
        return shiftDatabaseController.getAssignmentById(assignmentId)
    }

    // Kafka
    @Step("Get message from active_shifts_log")
    fun getMessageFromKafkaActiveShiftsLogLog(shiftId: UUID): ActiveShiftsLog {
        val answer = kafkaActiveShiftsLog.consume(shiftId.toString())!!.value()
        return SuiteBase.jacksonObjectMapper.convertValue(answer, ActiveShiftsLog::class.java)
    }

    @Step("Get message from active_shifts_log")
    fun getMessageFromKafkaActiveShiftsLogByParam(shiftId: UUID, filterKey: String, filterValue: String): ActiveShiftsLog {
        val answer = kafkaActiveShiftsLog.consumeByIdAndParam(shiftId.toString(), filterKey,filterValue)!!.value()
        return SuiteBase.jacksonObjectMapper.convertValue(answer, ActiveShiftsLog::class.java)
    }

    @Step("Get message from shift_assignments_log")
    fun getMessageFromKafkaShiftAssignmentsLog(assignmentId: UUID): ShiftAssignmentsLog {
        val answer = kafkaShiftAssignmentsLog.consume(assignmentId.toString())!!.value()
        return SuiteBase.jacksonObjectMapper.convertValue(answer, ShiftAssignmentsLog::class.java)
    }

    @Step("Get message from shift_assignments_log")
    fun getMessageFromKafkaShiftAssignmentsLogByParam(assignmentId: UUID, filterKey: String, filterValue: Any): ShiftAssignmentsLog {
        val answer = kafkaShiftAssignmentsLog.consumeByIdAndParam(assignmentId.toString(), filterKey,filterValue)!!.value()
        return SuiteBase.jacksonObjectMapper.convertValue(answer, ShiftAssignmentsLog::class.java)
    }

    @Step("Get message from shift_assignments_log")
    fun getMessageFromkafkaFirstShiftsSchedule(userId: UUID): FirstShiftSchedule? {
        val answer = kafkaFirstShiftsSchedule.consume(userId.toString())!!.value()
        return SuiteBase.jacksonObjectMapper.convertValue(answer, FirstShiftSchedule::class.java)
    }

    @Step("Get all message from shift_assignments_log")
    fun getMessageCountFromKafkaFirstShiftsSchedule(userId: UUID): Int {
        val answer = kafkaFirstShiftsSchedule.consumeAll()

        var count = 0
        for (record in answer!!) {
            if (
                record.key().toString() == userId.toString()) {
                count += 1
            }
        }
        return count
    }
}