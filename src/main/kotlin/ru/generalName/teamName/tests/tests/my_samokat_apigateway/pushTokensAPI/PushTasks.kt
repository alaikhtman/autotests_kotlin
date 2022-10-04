package ru.samokat.mysamokat.tests.tests.my_samokat_apigateway.pushTokensAPI

import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.samokat.employeeprofiles.api.common.domain.EmployeeVehicleType
import ru.samokat.employeeprofiles.api.common.domain.Vehicle
import ru.samokat.my.domain.employee.EmployeeRole
import ru.samokat.my.domain.shifts.TimeRange
import ru.samokat.my.rest.api.enum.ApiEnum
import ru.samokat.mysamokat.tests.checkers.CommonAssertion
import ru.samokat.mysamokat.tests.checkers.MySamokatApiGWAssertions
import ru.samokat.mysamokat.tests.dataproviders.Constants
import ru.samokat.mysamokat.tests.dataproviders.preconditions.CommonPreconditions
import ru.samokat.mysamokat.tests.dataproviders.preconditions.MySamokatApiGWPreconditions
import ru.samokat.mysamokat.tests.helpers.actions.EmployeeActions
import ru.samokat.mysamokat.tests.helpers.actions.MySamokatApiGWActions
import java.time.LocalDate

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Tag("msmkt-apigateway")
class PushTasks {

    private lateinit var msmktPreconditions: MySamokatApiGWPreconditions

    private lateinit var commonAssertion: CommonAssertion

    @Autowired
    private lateinit var employeeActions: EmployeeActions

    @Autowired
    private lateinit var msmktActions: MySamokatApiGWActions

    private lateinit var msmktAssertion: MySamokatApiGWAssertions

    @Autowired
    private lateinit var commonPreconditions: CommonPreconditions


    @BeforeEach
    fun before() {
        msmktPreconditions = MySamokatApiGWPreconditions()
        msmktAssertion = MySamokatApiGWAssertions()
        commonAssertion = CommonAssertion()
        employeeActions.deleteProfile(Constants.mobile1)
        employeeActions.deleteProfile(Constants.mobile2)
        employeeActions.deleteProfile(Constants.mobile3)
        employeeActions.deleteProfile(Constants.mobile4)
        this.date = commonPreconditions.getTomorrowsDate()
        this.range = commonPreconditions.getTomorrowsFullDayRange()
        this.theDayAfterTomorrowDate = commonPreconditions.getTheDayAfterTomorrow()
        this.theDayAfterTomorrowRange = commonPreconditions.getTheDayAfterTomorrowFullDayRange()
        commonPreconditions.clearAssignmentsFromDatabase(range)
        commonPreconditions.clearAssignmentsFromDatabase(theDayAfterTomorrowRange)
        commonPreconditions.clearAssignmentsFromDatabase(range, darkstoreId = Constants.searchContactsDarkstore)

    }

    @AfterEach
    fun release() {
        msmktAssertion.assertAll()
        employeeActions.deleteProfile(Constants.mobile1)
        employeeActions.deleteProfile(Constants.mobile2)
        employeeActions.deleteProfile(Constants.mobile3)
        employeeActions.deleteProfile(Constants.mobile4)
        commonPreconditions.clearAssignmentsFromDatabase(range)
        commonPreconditions.clearAssignmentsFromDatabase(theDayAfterTomorrowRange)
        commonPreconditions.clearAssignmentsFromDatabase(range, darkstoreId = Constants.searchContactsDarkstore)

    }

    private lateinit var range: TimeRange
    fun range(range: TimeRange) = apply { this.range = range }
    fun getRange(): TimeRange {
        return range
    }

    private lateinit var date: LocalDate
    fun date(date: LocalDate) = apply { this.date = date }
    fun getDate(): LocalDate {
        return date
    }

    private lateinit var theDayAfterTomorrowRange: TimeRange
    fun theDayAfterTomorrowRange(todayRange: TimeRange) = apply { this.theDayAfterTomorrowRange = todayRange }
    fun getTheDayAfterTomorrowRange(): TimeRange {
        return theDayAfterTomorrowRange
    }

    private lateinit var theDayAfterTomorrowDate: LocalDate
    fun theDayAfterTomorrowDate(todayDate: LocalDate) = apply { this.theDayAfterTomorrowDate = todayDate }
    fun getTheDayAfterTomorrowDate(): LocalDate {
        return theDayAfterTomorrowDate
    }


    @Test
    @DisplayName("shiftAssignmentInstantPush: assigned")
    fun shiftAssignmentInstantPushAssignedTest() {

        val assignmentRange = commonPreconditions.getFormattedTimeRange(date, "09:00", "10:00")
        val profile = commonPreconditions.createProfileDeliveryman()
        val assignmentId = commonPreconditions.createAssignment(
            profile.profileId, range, assignmentRange
        )

        Thread.sleep(5_000)

        val task = msmktActions.getPushTaskFromDBByassignmentIdAndType(assignmentId, "shiftAssignmentInstantPush")

        msmktAssertion.checkShiftAssignmentInstantPushData(task, "{\"created\":true}")
    }

    @Test
    @DisplayName("shiftAssignmentInstantPush: changed")
    fun shiftAssignmentInstantPushChangedTest() {

        val assignmentRange = commonPreconditions.getFormattedTimeRange(date, "09:00", "10:00")
        val assignmentRangeNew = commonPreconditions.getFormattedTimeRange(date, "11:00", "12:00")

        val profile = commonPreconditions.createProfileDeliveryman()
        val assignmentId = commonPreconditions.createAssignment(
            profile.profileId, range, assignmentRange
        )
        msmktActions.deleteTaskByAssignmentId(assignmentId)
        commonPreconditions.updateAssignment(assignmentId, range, assignmentRangeNew)

        Thread.sleep(5_000)

        val task = msmktActions.getPushTaskFromDBByassignmentIdAndType(assignmentId, "shiftAssignmentInstantPush")

        msmktAssertion.checkShiftAssignmentInstantPushData(task, "{\"created\":false}")
    }

    @Test
    @DisplayName("shiftAssignmentInstantPush: deleted")
    fun shiftAssignmentInstantPushCancelledTest() {

        val assignmentRange = commonPreconditions.getFormattedTimeRange(date, "09:00", "10:00")

        val profile = commonPreconditions.createProfileDeliveryman()
        val assignmentId = commonPreconditions.createAssignment(
            profile.profileId, range, assignmentRange
        )
        msmktActions.deleteTaskByAssignmentId(assignmentId)
        commonPreconditions.cancelAssignment(assignmentId, range)

        Thread.sleep(5_000)

        val task = msmktActions.getPushTaskFromDBByassignmentIdAndType(assignmentId, "shiftAssignmentInstantPush")

        msmktAssertion.checkShiftAssignmentInstantPushData(task, "{\"created\":false}")
    }

    @Test
    @DisplayName("shiftAssignmentReminderPush: deleted")
    fun shiftAssignmentReminderPushTest() {

        val assignmentRange = commonPreconditions.getFormattedTimeRange(theDayAfterTomorrowDate, "09:00", "10:00")

        val profile = commonPreconditions.createProfileDeliveryman()
        val assignmentId = commonPreconditions.createAssignment(
            profile.profileId, theDayAfterTomorrowRange, assignmentRange
        )


        val task = msmktActions.getPushTaskFromDBByassignmentIdAndType(assignmentId, "shiftAssignmentReminderPush")
        
        msmktAssertion.checkShiftAssignmentReminderPushData(task, date)
    }

}