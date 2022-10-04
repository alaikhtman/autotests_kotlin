package ru.samokat.mysamokat.tests.tests.employee_schedule.timesheetAPI

import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.samokat.mysamokat.tests.checkers.EmployeeScheduleAssertion
import ru.samokat.mysamokat.tests.dataproviders.preconditions.EmployeeSchedulePreconditions
import ru.samokat.mysamokat.tests.helpers.actions.EmployeeScheduleActions
import ru.samokat.mysamokat.tests.helpers.controllers.database.EmployeeScheduleDatabaseController


@SpringBootTest
@Tag("emproSchedule")
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class BilledTimeslotError {


    private lateinit var employeeScheduleAssertion: EmployeeScheduleAssertion

    private lateinit var employeeSchedulePreconditions: EmployeeSchedulePreconditions

    @Autowired
    private lateinit var employeeScheduleActions: EmployeeScheduleActions

    @Autowired
    private lateinit var employeeScheduleDatabase: EmployeeScheduleDatabaseController

    @BeforeEach
    fun before() {
        employeeSchedulePreconditions = EmployeeSchedulePreconditions()
        employeeScheduleAssertion = EmployeeScheduleAssertion()


    }

    @AfterEach
    fun release() {
        employeeScheduleAssertion.assertAll()

    }

    @Test
    @Tags(Tag("kafka_produce"), Tag("smoke"))
    @DisplayName("Check billedTimeslot errors events with flag = true in DB")
    fun checkBilledTimeslotErrorWithTrueFlagTest() {
        val event = employeeSchedulePreconditions.fillBilledTimeslotErrorEvent(
            error = true,
            errorText = "Количествоотработанныхчасовбольше 16"
        )

        employeeScheduleActions.sendMessageToIskhDannyeTabUchetaErrorKafka(event)
        employeeScheduleAssertion.checkErrorBilledTimeslotInDatabase(
            employeeScheduleDatabase.getBilledTimeSlotError(
                event.headers.id
            ), event.payload[0].errorText
        )

    }

    @Test
    @Tags(Tag("kafka_produce"), Tag("smoke"))
    @DisplayName("Check billedTimeslot errors events with flag = false in DB")
    fun checkBilledTimeslotErrorWithFalseFlagTest() {
        val event = employeeSchedulePreconditions.fillBilledTimeslotErrorEvent(
            error = false,
            errorText = "API: Не удалось типизировать значение для Сотрудник Ссылка f004cddb-fde2-4cda-9f30-7e0b2a10cd48 - количество найденных 0"
        )

        employeeScheduleActions.sendMessageToIskhDannyeTabUchetaErrorKafka(event)
        employeeScheduleAssertion.checkErrorBilledTimeslotInDatabase(
            employeeScheduleDatabase.getBilledTimeSlotError(
                event.headers.id
            ), event.payload[0].errorText
        )
    }

    @Test
    @Tags(Tag("kafka_produce"), Tag("smoke"))
    @DisplayName("Check billedTimeslot errors events with empty text is in DB")
    fun checkEmptyBilledTimeslotErrorTest() {
        val event = employeeSchedulePreconditions.fillBilledTimeslotErrorEvent(
            error = false,
            errorText = ""
        )

        employeeScheduleActions.sendMessageToIskhDannyeTabUchetaErrorKafka(event)
        employeeScheduleAssertion.checkErrorBilledTimeslotInDatabase(
            employeeScheduleDatabase.getBilledTimeSlotError(
                event.headers.id
            ), event.payload[0].errorText
        )
    }
}