package ru.samokat.mysamokat.tests.tests.my_samokat_apigateway.shiftsAPI

import org.apache.http.HttpStatus
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
import java.time.LocalDate

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Tag("msmkt-apigateway")
class GetAssignmentById {

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
        commonPreconditions.clearAssignmentsFromDatabase(range)
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

    @Test
    @DisplayName("Get assignment: deliveryman")
    fun getAssignmentDeliveryman() {

        val assignmentRange = commonPreconditions.getFormattedTimeRange(date, "09:00", "10:00")
        val profile = commonPreconditions.createProfileDeliveryman()
        val authRequest = msmktPreconditions.fillAuthRequest(password = profile.generatedPassword!!)
        val assignmentId = commonPreconditions.createAssignment(
            profile.profileId, range, assignmentRange
        )

        val token = msmktActions.authProfilePassword(authRequest)!!.accessToken
        val assignment = msmktActions.getAssignmentById(token, assignmentId)

        msmktAssertion.checkAssignmentInfo(
            assignment,
            assignmentRange,
            AssignmentStatus.ASSIGNED
        )
            .checkAssignmentsReplacementsRoles(assignment)
    }

    @Test
    @DisplayName("Get assignment: check replacements")
    fun getAssignmentCheckReplacements() {

        val assignmentRange = commonPreconditions.getFormattedTimeRange(date, "09:00", "10:00")
        val profile = commonPreconditions.createProfileDeliveryman()

        val profileIdDeliverymanBisy = commonPreconditions.createProfileDeliveryman(mobile = Constants.mobile2)
        val profileIdDeliverymanFree = commonPreconditions.createProfileDeliveryman(mobile = Constants.mobile3)
        val profileIdPickerFree = commonPreconditions.createProfilePicker(mobile = Constants.mobile4)

        val authRequest = msmktPreconditions.fillAuthRequest(password = profile.generatedPassword!!)
        val assignmentId = commonPreconditions.createAssignment(
            profile.profileId, range, assignmentRange
        )
        val assignmentIdBisy = commonPreconditions.createAssignment(
            profileIdDeliverymanBisy.profileId, range, assignmentRange, darkstoreId = Constants.searchContactsDarkstore
        )

        val token = msmktActions.authProfilePassword(authRequest)!!.accessToken
        val assignment = msmktActions.getAssignmentById(token, assignmentId)

        msmktAssertion.checkAssignmentInfo(
            assignment,
            assignmentRange,
            AssignmentStatus.ASSIGNED
        )
            .checkAssignmentsReplacementsRoles(assignment)
            .checkProfileInReplacementsList(assignment, profileIdDeliverymanFree.profileId)
            .checkProfileNotInReplacementsList(assignment, profileIdDeliverymanBisy.profileId)
            .checkProfileNotInReplacementsList(assignment, profileIdPickerFree.profileId)
    }

    @Test
    @DisplayName("Get assignment: picker")
    fun getAssignmentPicker() {

        val assignmentRange = commonPreconditions.getFormattedTimeRange(date, "09:00", "10:00")
        val profile = commonPreconditions.createProfilePicker()
        val authRequest = msmktPreconditions.fillAuthRequest(password = profile.generatedPassword!!)
        val assignmentId = commonPreconditions.createAssignment(
            profile.profileId, range, assignmentRange, role = AssigneeRole.PICKER,
        )

        val token = msmktActions.authProfilePassword(authRequest)!!.accessToken
        val assignment = msmktActions.getAssignmentById(token, assignmentId)

        msmktAssertion.checkAssignmentInfo(
            assignment,
            assignmentRange,
            AssignmentStatus.ASSIGNED,
            role = EmployeeRole.PICKER
        )
    }

    @Test
    @DisplayName("Get assignment: changed")
    fun getAssignmentChanged() {

        val assignmentRange = commonPreconditions.getFormattedTimeRange(date, "09:00", "10:00")
        val assignmentRangeNew = commonPreconditions.getFormattedTimeRange(date, "11:00", "12:00")

        val profile = commonPreconditions.createProfileDeliverymanPicker()
        val authRequest = msmktPreconditions.fillAuthRequest(password = profile.generatedPassword!!)
        val assignmentId = commonPreconditions.createAssignment(
            profile.profileId, range, assignmentRange
        )
        commonPreconditions.updateAssignment(assignmentId, range, assignmentRangeNew)

        val token = msmktActions.authProfilePassword(authRequest)!!.accessToken
        val assignment = msmktActions.getAssignmentById(token, assignmentId)

        msmktAssertion.checkAssignmentInfo(
            assignment,
            assignmentRangeNew,
            AssignmentStatus.ASSIGNED,
            role = EmployeeRole.DELIVERYMAN
        )
    }

    @Test
    @DisplayName("Get assignment: deleted")
    fun getAssignmentDeleted() {

        val assignmentRange = commonPreconditions.getFormattedTimeRange(date, "09:00", "10:00")

        val profile = commonPreconditions.createProfileDeliverymanPicker()

        val authRequest = msmktPreconditions.fillAuthRequest(password = profile.generatedPassword!!)
        val assignmentId = commonPreconditions.createAssignment(
            profile.profileId, range, assignmentRange
        )
        commonPreconditions.cancelAssignment(assignmentId, range)

        val token = msmktActions.authProfilePassword(authRequest)!!.accessToken
        val assignment = msmktActions.getAssignmentById(token, assignmentId)

        msmktAssertion.checkAssignmentInfo(
            assignment,
            assignmentRange,
            AssignmentStatus.CANCELED,
            role = EmployeeRole.DELIVERYMAN,
            cancellationReason = ShiftAssignmentCancellationReason.MISTAKEN_ASSIGNMENT
        )
    }

    @Test
    @DisplayName("Get assignment: workedOut Shift")
    fun getAssignmentWorkedOutShift() {

        val profile = commonPreconditions.createProfileDeliveryman()
        val shiftId = commonPreconditions.startAndStopShift(profile.profileId).shiftId
        val authRequest = msmktPreconditions.fillAuthRequest(password = profile.generatedPassword!!)

        val token = msmktActions.authProfilePassword(authRequest)!!.accessToken
        val assignment = msmktActions.getAssignmentByIdError(token, shiftId, HttpStatus.SC_NOT_FOUND)
    }
}

