package ru.generalName.teamName.tests.dataproviders.preconditions

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import ru.generalName.teamName.tests.dataproviders.dataClasses.apiGTW.oauth.OAuthTokenView
import ru.generalName.teamName.tests.helpers.actions.ProfileActions
import ru.generalName.teamName.tests.helpers.actions.ApiGWActions
import ru.generalName.teamName.tests.helpers.controllers.database.ApigatewayDatabaseController
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneOffset
import java.util.*

@Component
@Scope("prototype")
class CommonPreconditions {

    private var employeePreconditions: ProfilePreconditions = ProfilePreconditions()
    private var shiftsPreconditions: ShiftsPreconditions = ShiftsPreconditions()
    private var apiGWPreconditions: ApiGWPreconditions = ApiGWPreconditions()

    @Autowired
    private lateinit var profileActions: ProfileActions

    @Autowired
    private lateinit var shiftsActions: ShiftsActions

    @Autowired
    private lateinit var apiGWActions: ApiGWActions

    @Autowired
    private lateinit var shiftDatabaseController: ShiftsDatabaseController

    @Autowired
    private lateinit var apigatewayDatabaseController: ApigatewayDatabaseController

    private lateinit var createRequest: CreateProfileRequest
    fun createProfileRequest(): CreateProfileRequest {
        return createRequest
    }

    private var listOfProfileRequest: MutableList<CreateProfileRequest> = mutableListOf()
    fun listOfProfileRequest(): MutableList<CreateProfileRequest> {
        return listOfProfileRequest
    }

    private lateinit var profileResult: CreatedProfileView
    fun profileResult(): CreatedProfileView {
        return profileResult
    }

    private var listOfProfiles: MutableList<CreatedProfileView> = mutableListOf()
    fun listOfProfiles(): MutableList<CreatedProfileView> {
        return listOfProfiles
    }

    fun createProfile(
        roles: List<ApiEnum<EmployeeRole, String>>,
        vehicle: Vehicle? = null,
        staffPartnerId: UUID? = null,
        darkstoreId: UUID? = null,
        mobile: PhoneNumber = Constants.mobile1,
        name: EmployeeName = EmployeeName(
            StringAndPhoneNumberGenerator.generateRandomString(10),
            StringAndPhoneNumberGenerator.generateRandomString(10),
            StringAndPhoneNumberGenerator.generateRandomString(10)
        ),
        supervisedDarkstores: MutableList<UUID>? = null,
        accountingProfileId: String? = null,
        requisitionId: UUID? = null,
        cityId: UUID? = null,
        email: String? = null
    ): CreatedProfileView {
        createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = roles,
                vehicle = vehicle,
                email = email,
                staffPartnerId = staffPartnerId,
                darkstoreId = darkstoreId,
                mobile = mobile,
                name = name,
                supervisedDarkstores = supervisedDarkstores,
                accountingProfileId = accountingProfileId,
                requisitionId = requisitionId,
                cityId = cityId
            )
        profileResult = profileActions.createProfileFullResult(createRequest)
        return profileResult
    }

    fun createProfileDeliveryman(
        mobile: PhoneNumber = Constants.mobile1,
        roles: List<ApiEnum<EmployeeRole, String>> = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
        vehicle: Vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
        staffPartnerId: UUID = Constants.staffPartnerId,
        darkstoreId: UUID = Constants.darkstoreId,
        accountingProfileId: String? = null,
        cityId: UUID? = Constants.cityId,
        requisitionId: UUID? = null,
        name: EmployeeName = EmployeeName(
            StringAndPhoneNumberGenerator.generateRandomString(10),
            StringAndPhoneNumberGenerator.generateRandomString(10),
            StringAndPhoneNumberGenerator.generateRandomString(10)
        )
    ): CreatedProfileView {
        return createProfile(
            roles = roles,
            vehicle = vehicle,
            staffPartnerId = staffPartnerId,
            darkstoreId = darkstoreId,
            accountingProfileId = accountingProfileId,
            cityId = cityId,
            mobile = mobile,
            requisitionId = requisitionId,
            name = name
        )
    }

    fun createProfilePicker(
        mobile: PhoneNumber = Constants.mobile1,
        roles: List<ApiEnum<EmployeeRole, String>> = listOf(ApiEnum(EmployeeRole.PICKER)),
        staffPartnerId: UUID = Constants.staffPartnerId,
        darkstoreId: UUID = Constants.darkstoreId,
        accountingProfileId: String? = null,
        cityId: UUID? = Constants.cityId,
        name: EmployeeName = EmployeeName(
            StringAndPhoneNumberGenerator.generateRandomString(10),
            StringAndPhoneNumberGenerator.generateRandomString(10),
            StringAndPhoneNumberGenerator.generateRandomString(10)
        )
    ): CreatedProfileView {
        return createProfile(
            roles = roles,
            staffPartnerId = staffPartnerId,
            darkstoreId = darkstoreId,
            accountingProfileId = accountingProfileId,
            cityId = cityId,
            mobile = mobile,
            name = name
        )
    }

    fun createProfileDeliverymanPicker(
        mobile: PhoneNumber = Constants.mobile1,
        roles: List<ApiEnum<EmployeeRole, String>> = listOf(ApiEnum(EmployeeRole.DELIVERYMAN),ApiEnum(EmployeeRole.PICKER)),
        vehicle: Vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
        staffPartnerId: UUID = Constants.staffPartnerId,
        darkstoreId: UUID = Constants.darkstoreId,
        accountingProfileId: String? = null,
        cityId: UUID? = Constants.cityId
    ): CreatedProfileView {
        return createProfile(
            roles = roles,
            vehicle = vehicle,
            staffPartnerId = staffPartnerId,
            darkstoreId = darkstoreId,
            accountingProfileId = accountingProfileId,
            cityId = cityId,
            mobile = mobile
        )
    }

    fun createProfileGoodsManager(
        mobile: PhoneNumber = Constants.mobile1,
        roles: List<ApiEnum<EmployeeRole, String>> = listOf(ApiEnum(EmployeeRole.GOODS_MANAGER)),
        darkstoreId: UUID = Constants.darkstoreId,
        cityId: UUID? = Constants.cityId
    ): CreatedProfileView {
        return createProfile(
            roles = roles,
            darkstoreId = darkstoreId,
            cityId = cityId,
            mobile = mobile
        )
    }

    fun createProfileDarkstoreAdmin(
        mobile: PhoneNumber = Constants.mobile1,
        roles: List<ApiEnum<EmployeeRole, String>> = listOf(ApiEnum(EmployeeRole.DARKSTORE_ADMIN)),
        darkstoreId: UUID = Constants.darkstoreId,
        cityId: UUID? = Constants.cityId,
        accountingProfileId: String? = null,
        requisitionId: UUID? = null,
        name: EmployeeName = EmployeeName(
            StringAndPhoneNumberGenerator.generateRandomString(10),
            StringAndPhoneNumberGenerator.generateRandomString(10),
            StringAndPhoneNumberGenerator.generateRandomString(10)
        ),
    ): CreatedProfileView {
        return createProfile(
            roles = roles,
            darkstoreId = darkstoreId,
            cityId = cityId,
            mobile = mobile,
            accountingProfileId = accountingProfileId,
            requisitionId = requisitionId,
            name = name
        )
    }

    fun createListOfProfiles(
        listOfRoles: MutableList<List<ApiEnum<EmployeeRole, String>>>,
        listOfVehicle: MutableList<Vehicle?>,
        listOfStaffPartner: MutableList<UUID>,
        listOfDarkstore: MutableList<UUID>,
        listOfMobile: MutableList<PhoneNumber>,
        listOfName: MutableList<EmployeeName>,
        listOfSupervisedDarkstores: MutableList<UUID>? = null,
        listOfAccountingProfileIds: MutableList<String> = mutableListOf(),
        listOfRequisitionsId: MutableList<UUID?>,
        cityId: UUID? = Constants.cityId,
        amount: Int
    ): MutableList<UUID> {
        val listOfProfileId: MutableList<UUID> = mutableListOf()
        for (i in 0 until amount) {
            this.listOfProfileRequest.add(
                employeePreconditions
                    .fillCreateProfileRequest(
                        roles = listOfRoles[i],
                        vehicle = listOfVehicle[i],
                        email = null,
                        staffPartnerId = listOfStaffPartner[i],
                        darkstoreId = listOfDarkstore[i],
                        mobile = listOfMobile[i],
                        name = listOfName[i],
                        supervisedDarkstores = listOfSupervisedDarkstores,
                        accountingProfileId = listOfAccountingProfileIds[i],
                        requisitionId = listOfRequisitionsId[i],
                        cityId = cityId
                    )
            )

            listOfProfileId.add(
                createProfile(
                    listOfRoles[i],
                    listOfVehicle[i],
                    listOfStaffPartner[i],
                    listOfDarkstore[i],
                    listOfMobile[i],
                    listOfName[i],
                    listOfSupervisedDarkstores,
                    listOfAccountingProfileIds[i],
                    listOfRequisitionsId[i],
                    cityId
                ).profileId
            )


        }
        return listOfProfileId

    }

    fun startAndStopShift(
        profileId: UUID,
        userRole: ShiftUserRole = ShiftUserRole.DELIVERYMAN,
        deliveryMethod: DeliveryMethod? = null,
        darkstoreId: UUID = Constants.darkstoreId
    ): ActiveShiftView {
        val shift = startShift(profileId, userRole, deliveryMethod, darkstoreId)
        stopShift(profileId)
        return shift
    }

    fun startAndStopShiftForSeveralProfiles(
        listOfProfileId: MutableList<UUID>,
        listOfUserRole: MutableList<ShiftUserRole>,
        listOfDeliveryMethod: MutableList<DeliveryMethod?>,
        listOfDarkstore: MutableList<UUID>,
        amount: Int
    ): MutableList<ActiveShiftView> {
        val listOfShifts: MutableList<ActiveShiftView> = mutableListOf()
        for (i in 0 until amount) {
            listOfShifts.add(
                startAndStopShift(
                    listOfProfileId[i],
                    listOfUserRole[i],
                    listOfDeliveryMethod[i],
                    listOfDarkstore[i]
                )
            )
        }
        return listOfShifts
    }

    fun startShift(
        profileId: UUID,
        userRole: ShiftUserRole = ShiftUserRole.DELIVERYMAN,
        deliveryMethod: DeliveryMethod? = null,
        darkstoreId: UUID = Constants.darkstoreId
    ): ActiveShiftView {
        val openShiftRequest = shiftsPreconditions.fillStartShiftRequest(
            userId = profileId, userRole = userRole, deliveryMethod = deliveryMethod, darkstoreId = darkstoreId
        )
        val shift = shiftsActions.startShift(openShiftRequest)
        return shift
    }

    fun stopShift(profileId: UUID) {
        val stopShiftRequest = shiftsPreconditions.fillStopShiftRequest(profileId)
        shiftsActions.stopActiveShift(stopShiftRequest)
    }


    fun startAndStopUpdatedShift(
        profileId: UUID,
        userRole: ShiftUserRole = ShiftUserRole.DELIVERYMAN,
        deliveryMethod: DeliveryMethod? = null,
        darkstoreId: UUID = Constants.darkstoreId,
        newDate: Instant
    ): ActiveShiftView {
        val shift = startShift(profileId, userRole, deliveryMethod, darkstoreId)
        updateShiftInDB(shift.shiftId, newDate)
        stopShift(profileId)
        return shift
    }

    fun startAndStopUpdatedSeveralShifts(
        listOfProfileId: MutableList<UUID>,
        listOfUserRole: MutableList<ShiftUserRole>,
        listOfDeliveryMethod: MutableList<DeliveryMethod?>,
        listOfDarkstore: MutableList<UUID>,
        amount: Int,
        newDate: Instant
    ): MutableList<ActiveShiftView> {
        val listOfShifts: MutableList<ActiveShiftView> = mutableListOf()
        for (i in 0 until amount) {
            listOfShifts.add(
                startAndStopUpdatedShift(
                    listOfProfileId[i],
                    listOfUserRole[i],
                    listOfDeliveryMethod[i],
                    listOfDarkstore[i],
                    newDate
                )
            )
        }
        return listOfShifts
    }

    fun getTomorrowsFullDayRange(): TimeRange {
        val date = LocalDate.now().plusDays(1L)
        val range = TimeRange(
            startingAt = date.atStartOfDay().toInstant(ZoneOffset.UTC),
            endingAt = date.plusDays(1L).atStartOfDay().toInstant(ZoneOffset.UTC)
        )
        return range
    }

    fun getTodayFullDayRange(): TimeRange {
        val date = LocalDate.now()
        val range = TimeRange(
            startingAt = date.atStartOfDay().toInstant(ZoneOffset.UTC),
            endingAt = date.plusDays(1L).atStartOfDay().toInstant(ZoneOffset.UTC)
        )
        return range
    }


    fun getTheDayAfterTomorrowFullDayRange(): TimeRange {
        val date = LocalDate.now()
        val range = TimeRange(
            startingAt = date.plusDays(2L).atStartOfDay().toInstant(ZoneOffset.UTC),
            endingAt = date.plusDays(3L).atStartOfDay().toInstant(ZoneOffset.UTC)
        )
        return range
    }

    fun getFormattedTime(date: LocalDate, timeStr: String): Instant {
        return date.atTime(LocalTime.parse(timeStr)).toInstant(ZoneOffset.UTC)
    }

    fun getFormattedTimeRange(date: LocalDate, timeStart: String, timeEnd: String): TimeRange {
        return TimeRange(
            startingAt = getFormattedTime(date, timeStart),
            endingAt = getFormattedTime(date, timeEnd),
        )
    }

    fun getTomorrowsDate(): LocalDate {
        return LocalDate.now().plusDays(1L)
    }

    fun getTheDayAfterTomorrow(): LocalDate {
        return LocalDate.now().plusDays(2L)
    }

    fun clearAssignmentsFromDatabase(range: TimeRange, darkstoreId: UUID = Constants.darkstoreId) {

        val searchAssignmentsRequest = shiftsPreconditions.fillSearchAssignmentsRequest(
            timeRange = range, darkstoreId = darkstoreId
        )

        val assignments = shiftsActions.searchAssignments(searchAssignmentsRequest)

        assignments.assignments.forEach {
            shiftDatabaseController.deleteAssignmentMetadataLogById(it.assignmentId)
            shiftDatabaseController.deleteAssignmentMetadataById(it.assignmentId)
            shiftDatabaseController.deleteAssignmentLogById(it.assignmentId)
            shiftDatabaseController.deleteAssignmentById(it.assignmentId)
            apigatewayDatabaseController.deleteAssignmentTaskById(it.assignmentId.toString())
        }
    }

    fun createAssignment(
        profileId: UUID,
        range: TimeRange,
        assignmentRange: TimeRange,
        role: AssigneeRole = AssigneeRole.DELIVERYMAN,
        darkstoreId: UUID = Constants.darkstoreId
    ): UUID {
        val creation = shiftsPreconditions.fillCreationBuilder(
            "1", profileId,
            assignmentRange
        )
        val batchAssignmentsRequest = shiftsPreconditions.fillBatchAssignmentsRequest(
            assignmentsRole = ApiEnum(role), editingTimeRange = range, creations = listOf(creation),
            cancellations = null, darkstoreId = darkstoreId
        )
        return shiftsActions.batchAssignments(batchAssignmentsRequest).assignments[0].assignment.assignmentId
    }

    fun createAssignmentForSeveralProfiles(
        profileId: MutableList<UUID>,
        range: TimeRange,
        assignmentRange: TimeRange,
        role: MutableList<AssigneeRole> = mutableListOf(AssigneeRole.DELIVERYMAN),
        darkstoreId: MutableList<UUID> = mutableListOf(Constants.darkstoreId)
    ): MutableList<UUID> {
        val assignments: MutableList<UUID> = mutableListOf()
        for (i in 0 until profileId.count()) {
            assignments.add(createAssignment(profileId[i], range, assignmentRange, role[i], darkstoreId[i]))
        }

        return assignments
    }

    fun createSeveralAssignment(
        profileId: UUID,
        range: TimeRange,
        assignmentRanges: List<TimeRange>,
        role: AssigneeRole = AssigneeRole.DELIVERYMAN,
        darkstoreId: UUID = Constants.darkstoreId
    ): List<StoredShiftAssignmentsBatchView.ShiftAssignmentViewWithClientId> {
        val creations =
            mutableListOf<ru.samokat.shifts.api.assignments.storebatch.StoreShiftAssignmentsBatchRequest.Creation>()

        assignmentRanges.forEach {
            creations.add(
                shiftsPreconditions.fillCreationBuilder(
                    Random(10).toString(), profileId, it
                )
            )
        }
        val batchAssignmentsRequest = shiftsPreconditions.fillBatchAssignmentsRequest(
            assignmentsRole = ApiEnum(role), editingTimeRange = range, creations = creations,
            cancellations = null, darkstoreId = darkstoreId
        )
        return shiftsActions.batchAssignments(batchAssignmentsRequest).assignments
    }

    fun cancelAssignment(
        assignmentId: UUID,
        range: TimeRange,
        role: AssigneeRole = AssigneeRole.DELIVERYMAN,
        reason: ShiftAssignmentCancellationReason = ShiftAssignmentCancellationReason.MISTAKEN_ASSIGNMENT,
        darkstoreId: UUID = Constants.darkstoreId
    ) {

        val cancellation = shiftsPreconditions.fillCancellationBuilder(assignmentId, 1L, reason)
        val batchAssignmentsRequest = shiftsPreconditions.fillBatchAssignmentsRequest(
            assignmentsRole = ApiEnum(role), editingTimeRange = range, creations = null,
            cancellations = listOf(cancellation), darkstoreId = darkstoreId
        )

        shiftsActions.batchAssignments(batchAssignmentsRequest)
    }

    fun updateAssignment(
        assignmentId: UUID,
        range: TimeRange,
        newRange: TimeRange,
        role: AssigneeRole = AssigneeRole.DELIVERYMAN
    ) {

        val updation = shiftsPreconditions.fillUpdationBuilder(
            assignmentId, newRange
        )
        val batchAssignmentsRequest = shiftsPreconditions.fillBatchAssignmentsRequest(
            assignmentsRole = ApiEnum(role), editingTimeRange = range, creations = null,
            updates = listOf(updation)
        )
        shiftsActions.batchAssignments(batchAssignmentsRequest)
    }

    fun updateShiftInDB(shiftId: UUID, newDate: Instant) {
        shiftDatabaseController.updateShiftInDB(shiftId, newDate)

    }


    fun postSchedulesForSeveralProfiles(
        profileIds: MutableList<UUID>,
        schedule: List<TimeRange>,
        timeRange: TimeRange
    ): MutableList<Unit> {
        val schedules: MutableList<Unit> = mutableListOf()

        for (i in 0 until profileIds.count()) {
            val request = shiftsPreconditions.fillStoreScheduleRequest(profileIds[i], schedule, timeRange)
            schedules.add(shiftsActions.postSchedules(request))

        }
        return schedules
    }

    fun createAndAuthorizeStaffUser(
        roles: List<ApiEnum<EmployeeRole, String>> = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
        vehicle: Vehicle? = null,
        staffPartnerId: UUID? = Constants.defaultStaffPartnerId,
        darkstoreId: UUID? = Constants.darkstoreId,
        mobile: PhoneNumber = Constants.mobile1,
        name: EmployeeName = EmployeeName(
            StringAndPhoneNumberGenerator.generateRandomString(10),
            StringAndPhoneNumberGenerator.generateRandomString(10),
            StringAndPhoneNumberGenerator.generateRandomString(10)
        ),
        supervisedDarkstores: MutableList<UUID>? = null,
        cityId: UUID? = Constants.cityId
    ): OAuthTokenView? {
        val profile =
            createProfile(
                roles = roles,
                vehicle = vehicle,
                staffPartnerId = staffPartnerId,
                darkstoreId = darkstoreId,
                mobile = mobile,
                name = name,
                supervisedDarkstores = supervisedDarkstores,
                cityId = cityId
            )
        apiGWPreconditions.fillAuthRequest(
            mobile = Constants.mobile1.asStringWithPlus(),
            password = profile.generatedPassword!!
        )
        return apiGWActions.authProfilePassword(apiGWPreconditions.oAuthTokenRequest())

    }


    fun createStaffPartner(
        title: String = "HRP-Map Partner",
        shortTitle: String = "TestPartnerShortTitle",
        type: StaffPartnerType = StaffPartnerType.OUT_STAFF
    ): UUID {
        val request = employeePreconditions.fillCreatePartnerRequest(title, shortTitle, ApiEnum(type))
        return profileActions.createStaffPartner(request).partnerId
    }

}