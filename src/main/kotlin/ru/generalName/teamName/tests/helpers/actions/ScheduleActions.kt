package ru.generalName.teamName.tests.helpers.actions

import io.qameta.allure.Step
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.util.*

@Component
@Scope("prototype")
class ScheduleActions(
    private val kafkaBilledTimeslot: KafkaController,
    private val kafkaBilledTimeslotError: KafkaController
) {

    @Autowired
    private lateinit var employeeScheduleDatabase: ScheduleDatabaseController

    @Autowired
    lateinit var timesheetController: TimesheetController

    @Autowired
    lateinit var workedOutTimeslotController: WorkedOutTimeslotController

    private lateinit var workedOutTimeSlot: CreatedWorkedOutTimeSlotView
    fun workedOutTimeSlot(): CreatedWorkedOutTimeSlotView {
        return workedOutTimeSlot
    }

    private var listOfWorkedOutTimeSlot: MutableList<CreatedWorkedOutTimeSlotView> = mutableListOf()
    fun listOfWorkedOutTimeSlot(): MutableList<CreatedWorkedOutTimeSlotView> {
        return listOfWorkedOutTimeSlot
    }

    private var listOfWorkedOutTimeSlotId: MutableList<UUID> = mutableListOf()
    fun listOfWorkedOutTimeSlotId(): MutableList<UUID> {
        return listOfWorkedOutTimeSlotId
    }

    private lateinit var createWorkedOutTimeSlotResult: RestResult<CreatedWorkedOutTimeSlotView, CreateWorkedOutTimeSlotError>
    fun createWorkedOutTimeSlotResult(): RestResult<CreatedWorkedOutTimeSlotView, CreateWorkedOutTimeSlotError> {
        return createWorkedOutTimeSlotResult
    }

    private lateinit var updateWorkedOutTimeSlotResult:
            RestResult<Unit, UpdateWorkedOutTimeSlotError>

    fun updateWorkedOutTimeSlotResult():
            RestResult<Unit, UpdateWorkedOutTimeSlotError> {
        return updateWorkedOutTimeSlotResult
    }

    private lateinit var createWorkedOutTimeSlotError: CreateWorkedOutTimeSlotError
    fun createWorkedOutTimeSlotError(): CreateWorkedOutTimeSlotError {
        return createWorkedOutTimeSlotError
    }

    private lateinit var updateWorkedOutTimeSlotError:
            UpdateWorkedOutTimeSlotError

    fun updateWorkedOutTimeSlotError():
            UpdateWorkedOutTimeSlotError {
        return updateWorkedOutTimeSlotError
    }

    private lateinit var timeSlot: WorkedOutTimeSlotView
    fun timeSlot(): WorkedOutTimeSlotView {
        return timeSlot
    }

    private lateinit var timesheetByDarkstore: TimesheetView
    fun timesheetByDarkstore(): TimesheetView {
        return timesheetByDarkstore
    }

    private lateinit var timesheetByDarkstoreResult: RestResult<TimesheetView, GetTimesheetError>
    fun timesheetByDarkstoreResult(): RestResult<TimesheetView, GetTimesheetError> {
        return timesheetByDarkstoreResult
    }

    private lateinit var timesheetByDarkstoreError: GetTimesheetError
    fun timesheetByDarkstoreError(): GetTimesheetError {
        return timesheetByDarkstoreError
    }

    private lateinit var timesheetById: TimesheetView
    fun timesheetById(): TimesheetView {
        return timesheetById
    }

    private lateinit var timesheetByIdResult: RestResult<TimesheetView, GetTimesheetByIdError>
    fun timesheetByIdResult(): RestResult<TimesheetView, GetTimesheetByIdError> {
        return timesheetByIdResult
    }

    private lateinit var timesheetByIdError: GetTimesheetByIdError
    fun timesheetByIdError(): GetTimesheetByIdError {
        return timesheetByIdError
    }

    private lateinit var submitResultError:
            SubmitTimesheetError

    fun submitResultError():
            SubmitTimesheetError {
        return submitResultError
    }




    // timesheetByDarkstore
    @Step("Get timesheet by date and darkstore")
    fun getTimesheet(darkstoreId: UUID, request: GetTimesheetRequest): ScheduleActions {
        Thread.sleep(2_000)
        timesheetByDarkstore = timesheetController.getTimesheet(darkstoreId, request)!!.asSuccess()
        return this
    }

    @Step("Get timesheet by date and darkstore unsuccessfully")
    fun getTimesheetUnsuccessfully(darkstoreId: UUID, request: GetTimesheetRequest): ScheduleActions {
        timesheetByDarkstoreResult = timesheetController.getTimesheet(darkstoreId, request)!!
        timesheetByDarkstoreError = timesheetByDarkstoreResult.asClientError()
        return this
    }

    @Step("Get timesheet by Id")
    fun getTimesheetById(timesheetId: UUID): ScheduleActions {
        timesheetById = timesheetController.getTimesheetById(timesheetId)!!.asSuccess()
        return this
    }

    @Step("Get timesheet by date and darkstore unsuccessfully")
    fun getTimesheetByIdUnsuccessfully(timesheetId: UUID): ScheduleActions {
        timesheetByIdResult = timesheetController.getTimesheetById(timesheetId)!!
        timesheetByIdError = timesheetByIdResult.asClientError()
        return this
    }


    @Step("Submit timesheetByDarkstore")
    fun submitTimesheet(timesheetId: UUID): ScheduleActions {
        timesheetController.submitTimesheet(timesheetId)!!.asSuccess()
        return this
    }

    @Step("Submit timesheetByDarkstore unsuccessful")
    fun submitTimesheetUnsuccessful(timesheetId: UUID): ScheduleActions {
        val submitResult = timesheetController.submitTimesheet(timesheetId)!!
        submitResultError = submitResult.asClientError()
        return this
    }


    //workedOutTimeSlot

    @Step("Create workedOut timeslot")
    fun createWorkedOutTimeSlot(request: CreateWorkedOutTimeSlotRequest): ScheduleActions {
        workedOutTimeSlot = workedOutTimeslotController.createWorkedOutTimeSlot(request)!!.asSuccess()
        return this
    }

    @Step("Create workedOut timeslot unsuccessful")
    fun createWorkedOutTimeSlotUnsuccessful(request: CreateWorkedOutTimeSlotRequest): ScheduleActions {
        createWorkedOutTimeSlotResult = workedOutTimeslotController.createWorkedOutTimeSlot(request)!!
        createWorkedOutTimeSlotError = createWorkedOutTimeSlotResult.asClientError()
        return this
    }


    @Step("Create several workedOut timeslot")
    fun createSeveralWorkedOutTimeSlot(workedOutTimeSlotRequestList: MutableList<CreateWorkedOutTimeSlotRequest>): ScheduleActions {
        for (i in 0 until workedOutTimeSlotRequestList.count()) {
            listOfWorkedOutTimeSlot.add(
                workedOutTimeslotController.createWorkedOutTimeSlot(
                    workedOutTimeSlotRequestList[i]
                )
                !!.asSuccess()
            )
        }
        for (i in 0 until listOfWorkedOutTimeSlot.count()) {
            listOfWorkedOutTimeSlotId.add(listOfWorkedOutTimeSlot[i].workedOutTimeSlotId)
        }
        return this
    }


    @Step("Update workedOut timeslot")
    fun updateWorkedOutTimeSlot(
        workedOutTimeSlotId: UUID,
        request: UpdateWorkedOutTimeSlotRequest
    ): ScheduleActions {
        workedOutTimeslotController.updateWorkedOutTimeSlot(workedOutTimeSlotId, request)!!.asSuccess()
        return this
    }

    @Step("Update several workedOut timeslot")
    fun updateSeveralWorkedOutTimeSlot(
        workedOutTimeSlotId: UUID,
        updatedWorkedOutTimeSlotRequestList: MutableList<UpdateWorkedOutTimeSlotRequest>
    ): ScheduleActions {
        for (i in 0 until updatedWorkedOutTimeSlotRequestList.count()) {
            val listUpdatedWorkedOutTimeSlot: MutableList<Unit> =
                mutableListOf()
            listUpdatedWorkedOutTimeSlot.add(
                workedOutTimeslotController.updateWorkedOutTimeSlot(
                    workedOutTimeSlotId,
                    updatedWorkedOutTimeSlotRequestList[i]
                )!!.asSuccess()
            )
        }
        return this
    }

    @Step("Update workedOut timeslot unsuccessful")
    fun updateWorkedOutTimeSlotUnsuccessful(
        workedOutTimeSlotId: UUID,
        request: UpdateWorkedOutTimeSlotRequest
    ): ScheduleActions {
        updateWorkedOutTimeSlotResult =
            workedOutTimeslotController.updateWorkedOutTimeSlot(workedOutTimeSlotId, request)!!
        updateWorkedOutTimeSlotError = updateWorkedOutTimeSlotResult.asClientError()
        return this
    }

    fun getTimeSlotById(timeslotId: UUID): ScheduleActions {
        timeSlot = workedOutTimeslotController.getTimeslotById(timeslotId)!!.asSuccess()
        return this
    }

    fun getTimeSlotInBD(workedOutShiftId: UUID): UUID {
        return employeeScheduleDatabase.getWorkedOutTimeSlotByShift(workedOutShiftId)
    }

    @Step("Update timesheet in DB")
    fun updateTimesheetInDB(
        darkstoreId: UUID,
        date: LocalDate,
        newDate: LocalDate
    ): ScheduleActions {
        employeeScheduleDatabase.updateTimesheetInDB(darkstoreId, date, newDate)
        return this
    }

    @Step("Delete timesheet in DB")
    fun deleteTimesheetInDB(
        darkstoreId: UUID, date: LocalDate
    ): ScheduleActions {
        employeeScheduleDatabase.deleteTimesheetByDarkstore(darkstoreId, date)
        return this
    }


    //tasks

    fun getTaskScheduleTime(type: String, correlationId: String): String {
        return employeeScheduleDatabase.getTask(type, correlationId)

    }

    //Kafka
    @Step("Get message from IskhDannyeTabUchetaRabVremVneshnSotr")
    fun getMessageFromKafkaBilledTimeslot(billedTimeslotId: UUID): IskhDannyeTabUchetaRabVremVneshnSotr {
        val answer =
            kafkaBilledTimeslot.consume(billedTimeslotId.toString())!!.value()
        val result =
            SuiteBase.jacksonObjectMapper.convertValue(answer, IskhDannyeTabUchetaRabVremVneshnSotr::class.java)
        return result
    }

    @Step("Send message to IskhDannyeTabUchetaErrorKafka")
    fun sendMessageToIskhDannyeTabUchetaErrorKafka(event: IskhDannyeTabUchetaError): ScheduleActions {
        val byteEvent = SuiteBase.jacksonObjectMapper.writeValueAsBytes(event)
        val key = UUID.randomUUID()
        kafkaBilledTimeslotError.sendMessage(byteEvent, 1, key)
        Thread.sleep(2_000)

        return this
    }




}