package ru.samokat.mysamokat.tests.tests.my_samokat_apigateway.shiftsAPI

import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.samokat.employeeprofiles.api.common.domain.EmployeeVehicleType
import ru.samokat.employeeprofiles.api.common.domain.Vehicle
import ru.samokat.my.domain.employee.EmployeeRole
import ru.samokat.my.domain.shifts.ShiftAssignmentCancellationReason
import ru.samokat.my.domain.shifts.TimeRange
import ru.samokat.my.rest.api.enum.ApiEnum
import ru.samokat.mysamokat.apigateway.api.shifts.AssignmentStatus
import ru.samokat.mysamokat.tests.checkers.CommonAssertion
import ru.samokat.mysamokat.tests.checkers.MySamokatApiGWAssertions
import ru.samokat.mysamokat.tests.dataproviders.Constants
import ru.samokat.mysamokat.tests.dataproviders.preconditions.CommonPreconditions
import ru.samokat.mysamokat.tests.dataproviders.preconditions.MySamokatApiGWPreconditions
import ru.samokat.mysamokat.tests.helpers.actions.EmployeeActions
import ru.samokat.mysamokat.tests.helpers.actions.MySamokatApiGWActions
import ru.samokat.shifts.api.common.domain.AssigneeRole
import ru.samokat.shifts.api.common.domain.ShiftUserRole
import java.time.LocalDate
import java.time.ZoneOffset

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Tag("msmkt-apigateway")
class GetShifts {
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
        this.tomorrowDate = commonPreconditions.getTomorrowsDate()
        this.theDayAfterTomorrowDate = commonPreconditions.getTheDayAfterTomorrow()
        this.tomorrowRange = commonPreconditions.getTomorrowsFullDayRange()
        this.theDayAfterTomorrowRange = commonPreconditions.getTheDayAfterTomorrowFullDayRange()
        commonPreconditions.clearAssignmentsFromDatabase(tomorrowRange)
        commonPreconditions.clearAssignmentsFromDatabase(theDayAfterTomorrowRange)
        commonPreconditions.clearAssignmentsFromDatabase(tomorrowRange, Constants.inactiveDarkstore)
    }

    @AfterEach
    fun release() {
        msmktAssertion.assertAll()
        employeeActions.deleteProfile(Constants.mobile1)
        commonPreconditions.clearAssignmentsFromDatabase(tomorrowRange)
        commonPreconditions.clearAssignmentsFromDatabase(tomorrowRange, Constants.inactiveDarkstore)
    }

    private lateinit var tomorrowRange: TimeRange
    fun tomorrowRange(tomorrowRange: TimeRange) = apply { this.tomorrowRange = tomorrowRange }
    fun getTomorrowRange(): TimeRange {
        return tomorrowRange
    }

    private lateinit var theDayAfterTomorrowRange: TimeRange
    fun theDayAfterTomorrowRange(todayRange: TimeRange) = apply { this.theDayAfterTomorrowRange = todayRange }
    fun getTheDayAfterTomorrowRange(): TimeRange {
        return theDayAfterTomorrowRange
    }

    private lateinit var tomorrowDate: LocalDate
    fun tomorrowDate(tomorrowDate: LocalDate) = apply { this.tomorrowDate = tomorrowDate }
    fun getTomorrowDate(): LocalDate {
        return tomorrowDate
    }

    private lateinit var theDayAfterTomorrowDate: LocalDate
    fun theDayAfterTomorrowDate(todayDate: LocalDate) = apply { this.theDayAfterTomorrowDate = todayDate }
    fun getTheDayAfterTomorrowDate(): LocalDate {
        return theDayAfterTomorrowDate
    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Get shifts: assignment for deliveryman")
    fun getShiftsDeliverymanAssignment() {

        val assignmentRange = commonPreconditions.getFormattedTimeRange(tomorrowDate, "09:00", "10:00")
        val profile = commonPreconditions.createProfileDeliveryman()
        val authRequest = msmktPreconditions.fillAuthRequest(password = profile.generatedPassword!!)
        val assignmentId = commonPreconditions.createAssignment(
            profile.profileId, tomorrowRange, assignmentRange
        )

        val token = msmktActions.authProfilePassword(authRequest)!!.accessToken
        val allShifts = msmktActions.getAllShifts(token, tomorrowRange)

        msmktAssertion.checkAssignmentsListCount(allShifts, 1)
        msmktAssertion.checkAssignmentInfo(
            allShifts!!.shifts.assignments?.get(0)!!,
            assignmentRange,
            AssignmentStatus.ASSIGNED
        )
    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Get shifts: assignment on foreign darkstore")
    fun getShiftsForeignAssignment() {

        val assignmentRange = commonPreconditions.getFormattedTimeRange(tomorrowDate, "09:00", "10:00")
        val profile = commonPreconditions.createProfileDeliveryman(
            darkstoreId = Constants.updatedDarkstoreId
        )
        val authRequest = msmktPreconditions.fillAuthRequest(password = profile.generatedPassword!!)
        val assignmentId = commonPreconditions.createAssignment(
            profile.profileId, tomorrowRange, assignmentRange
        )

        val token = msmktActions.authProfilePassword(authRequest)!!.accessToken
        val allShifts = msmktActions.getAllShifts(token, tomorrowRange)

        msmktAssertion.checkAssignmentsListCount(allShifts, 1)
        msmktAssertion.checkAssignmentInfo(
            allShifts!!.shifts.assignments?.get(0)!!,
            assignmentRange,
            AssignmentStatus.ASSIGNED
        )
    }

    @Test()
    @Tags(Tag("smoke"))
    @DisplayName("Get shifts: assignment for picker")
    fun getShiftsPickerAssignment() {

        val assignmentRange = commonPreconditions.getFormattedTimeRange(tomorrowDate, "09:00", "10:00")
        val profile = commonPreconditions.createProfileDeliverymanPicker()
        val authRequest = msmktPreconditions.fillAuthRequest(password = profile.generatedPassword!!)
        val assignmentId = commonPreconditions.createAssignment(
            profile.profileId, tomorrowRange, assignmentRange, role = AssigneeRole.PICKER,
        )

        val token = msmktActions.authProfilePassword(authRequest)!!.accessToken
        val allShifts = msmktActions.getAllShifts(token, tomorrowRange)

        msmktAssertion.checkAssignmentsListCount(allShifts, 1)
        msmktAssertion.checkAssignmentInfo(
            allShifts!!.shifts.assignments?.get(0)!!,
            assignmentRange,
            AssignmentStatus.ASSIGNED, EmployeeRole.PICKER
        )
    }

    @Test
    @DisplayName("Get shifts: cancelled assignment")
    fun getShiftsCancelledAssignment() {

        val assignmentRange = commonPreconditions.getFormattedTimeRange(tomorrowDate, "09:00", "10:00")

        val profile = commonPreconditions.createProfileDeliverymanPicker()
        val authRequest = msmktPreconditions.fillAuthRequest(password = profile.generatedPassword!!)
        val assignmentId = commonPreconditions.createAssignment(
            profile.profileId, tomorrowRange, assignmentRange
        )
        commonPreconditions.cancelAssignment(assignmentId, tomorrowRange)

        val token = msmktActions.authProfilePassword(authRequest)!!.accessToken
        val allShifts = msmktActions.getAllShifts(token, tomorrowRange)

        msmktAssertion.checkAssignmentsListCount(allShifts, 1)
        msmktAssertion.checkAssignmentInfo(
            allShifts!!.shifts.assignments?.get(0)!!,
            assignmentRange,
            AssignmentStatus.CANCELED,
            EmployeeRole.DELIVERYMAN,
            cancellationReason = ShiftAssignmentCancellationReason.MISTAKEN_ASSIGNMENT
        )
    }

    @Test
    @Tags(Tag("darkstore_integration"))
    @DisplayName("Get shifts: with inactive darkstore")
    fun getShiftsWithInactivaDarkstore() {

        val assignmentRange = commonPreconditions.getFormattedTimeRange(tomorrowDate, "09:00", "10:00")
        val profile = commonPreconditions.createProfileDeliveryman(
            darkstoreId = Constants.inactiveDarkstore
        )
        val authRequest = msmktPreconditions.fillAuthRequest(password = profile.generatedPassword!!)
        val assignmentId = commonPreconditions.createAssignment(
            profile.profileId, tomorrowRange, assignmentRange, darkstoreId = Constants.inactiveDarkstore
        )

        val token = msmktActions.authProfilePassword(authRequest)!!.accessToken
        val allShifts = msmktActions.getAllShifts(token, tomorrowRange)

        msmktAssertion.checkAssignmentsListCount(allShifts, 1)
        msmktAssertion.checkAssignmentInfo(
            allShifts!!.shifts.assignments?.get(0)!!,
            assignmentRange,
            AssignmentStatus.ASSIGNED,
            darkstoreId = Constants.inactiveDarkstore
        )
    }

    @Test
    @DisplayName("Get shifts: updated assignment")
    fun getShiftsUpdatedAssignment() {

        val assignmentRange = commonPreconditions.getFormattedTimeRange(tomorrowDate, "09:00", "10:00")
        val assignmentRangeNew = commonPreconditions.getFormattedTimeRange(tomorrowDate, "11:00", "12:00")

        val profile = commonPreconditions.createProfileDeliverymanPicker()
        val authRequest = msmktPreconditions.fillAuthRequest(password = profile.generatedPassword!!)
        val assignmentId = commonPreconditions.createAssignment(
            profile.profileId, tomorrowRange, assignmentRange
        )
        commonPreconditions.updateAssignment(assignmentId, tomorrowRange, assignmentRangeNew)

        val token = msmktActions.authProfilePassword(authRequest)!!.accessToken
        val allShifts = msmktActions.getAllShifts(token, tomorrowRange)

        msmktAssertion.checkAssignmentsListCount(allShifts, 1)
        msmktAssertion.checkAssignmentInfo(
            allShifts!!.shifts.assignments?.get(0)!!,
            assignmentRangeNew,
            AssignmentStatus.ASSIGNED,
            EmployeeRole.DELIVERYMAN
        )
    }

    @Test
    @DisplayName("Get shifts: no shifts")
    fun getShiftsEmpty() {

        val profile = commonPreconditions.createProfileDeliveryman()
        val authRequest = msmktPreconditions.fillAuthRequest(password = profile.generatedPassword!!)

        val token = msmktActions.authProfilePassword(authRequest)!!.accessToken
        val allShifts = msmktActions.getAllShifts(token, tomorrowRange)

        msmktAssertion.checkAssignmentsListNull(allShifts)
        msmktAssertion.checkScheduleListNull(allShifts)
        msmktAssertion.checkWorkedOutListNull(allShifts)

    }

    @Test
    @DisplayName("Get shifts: workedOut, deliveryman")
    fun getShiftsWorkedOutDeliveryman() {
        val profile = commonPreconditions.createProfileDeliveryman()
        commonPreconditions.startAndStopShift(profile.profileId)
        val authRequest = msmktPreconditions.fillAuthRequest(password = profile.generatedPassword!!)

        val token = msmktActions.authProfilePassword(authRequest)!!.accessToken
        val allShifts = msmktActions.getAllShifts(
            token, TimeRange(
                LocalDate.now().atStartOfDay().toInstant(ZoneOffset.UTC),
                endingAt = LocalDate.now().plusDays(1L).atStartOfDay().toInstant(ZoneOffset.UTC)
            )
        )

        msmktAssertion.checkWorkedOutListCount(allShifts, 1)
        msmktAssertion.checkWorkedOutShiftInfo(allShifts!!.shifts.workedOutShifts!!.get(0))

    }

    @Test
    @DisplayName("Get shifts: workedOut, picker")
    fun getShiftsWorkedOutPicker() {
        val profile = commonPreconditions.createProfileDeliverymanPicker()
        commonPreconditions.startAndStopShift(profile.profileId, userRole = ShiftUserRole.PICKER)
        val authRequest = msmktPreconditions.fillAuthRequest(password = profile.generatedPassword!!)

        val token = msmktActions.authProfilePassword(authRequest)!!.accessToken
        val allShifts = msmktActions.getAllShifts(
            token, TimeRange(
                LocalDate.now().atStartOfDay().toInstant(ZoneOffset.UTC),
                endingAt = LocalDate.now().plusDays(1L).atStartOfDay().toInstant(ZoneOffset.UTC)
            )
        )

        msmktAssertion.checkWorkedOutListCount(allShifts, 1)
        msmktAssertion.checkWorkedOutShiftInfo(allShifts!!.shifts.workedOutShifts!!.get(0), role = EmployeeRole.PICKER)
    }

    @Test
    @DisplayName("Get shifts: workedOut, deliveryman, foreign  darkstore")
    fun getShiftsWorkedOutDeliverymanForeign() {
        val profile = commonPreconditions.createProfileDeliveryman()
        commonPreconditions.startAndStopShift(profile.profileId, darkstoreId = Constants.updatedDarkstoreId)
        val authRequest = msmktPreconditions.fillAuthRequest(password = profile.generatedPassword!!)

        val token = msmktActions.authProfilePassword(authRequest)!!.accessToken
        val allShifts = msmktActions.getAllShifts(
            token, TimeRange(
                LocalDate.now().atStartOfDay().toInstant(ZoneOffset.UTC),
                endingAt = LocalDate.now().plusDays(1L).atStartOfDay().toInstant(ZoneOffset.UTC)
            )
        )

        msmktAssertion.checkWorkedOutListCount(allShifts, 1)
        msmktAssertion.checkWorkedOutShiftInfo(
            allShifts!!.shifts.workedOutShifts!!.get(0),
            darkstoreId = Constants.updatedDarkstoreId
        )

    }

    @Test
    @DisplayName("Get shifts: several shifts different type")
    fun getSeveralShiftsDifferentType() {

        val assignmentRange1 = commonPreconditions.getFormattedTimeRange(tomorrowDate, "09:00", "10:00")
        val assignmentRange2 = commonPreconditions.getFormattedTimeRange(tomorrowDate, "22:00", "23:00")

        val profile = commonPreconditions.createProfileDeliveryman()
        val authRequest = msmktPreconditions.fillAuthRequest(password = profile.generatedPassword!!)

        val assignments = commonPreconditions.createSeveralAssignment(
            profile.profileId,
            tomorrowRange,
            listOf(assignmentRange1, assignmentRange2)
        )
        commonPreconditions.startAndStopShift(profile.profileId, darkstoreId = Constants.updatedDarkstoreId)
        commonPreconditions.startAndStopShift(profile.profileId, darkstoreId = Constants.updatedDarkstoreId)


        val token = msmktActions.authProfilePassword(authRequest)!!.accessToken
        val allShifts = msmktActions.getAllShifts(
            token, TimeRange(
                LocalDate.now().atStartOfDay().toInstant(ZoneOffset.UTC),
                endingAt = LocalDate.now().plusDays(2L).atStartOfDay().toInstant(ZoneOffset.UTC)
            )
        )

        msmktAssertion.checkAssignmentsListCount(allShifts, 2)
            .checkWorkedOutListCount(allShifts, 2)
            .checkAssignmentInfo(
            allShifts!!.shifts.assignments?.get(0)!!,
            assignmentRange1,
            AssignmentStatus.ASSIGNED
        )
            .checkAssignmentInfo(
            allShifts!!.shifts.assignments?.get(1)!!,
            assignmentRange2,
            AssignmentStatus.ASSIGNED
        )
            .checkWorkedOutShiftInfo(
            allShifts!!.shifts.workedOutShifts!!.get(0),
            darkstoreId = Constants.updatedDarkstoreId
        )
            .checkWorkedOutShiftInfo(
            allShifts!!.shifts.workedOutShifts!!.get(1),
            darkstoreId = Constants.updatedDarkstoreId
        )

    }
}
