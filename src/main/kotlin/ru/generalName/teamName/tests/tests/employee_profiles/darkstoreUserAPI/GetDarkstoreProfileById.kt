package ru.samokat.mysamokat.tests.tests.employee_profiles.darkstoreUserAPI

import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.samokat.employeeprofiles.api.common.domain.Vehicle
import ru.samokat.employeeprofiles.api.darkstoreusers.domain.DarkstoreUserRole
import ru.samokat.employeeprofiles.api.darkstoreusers.domain.DarkstoreUserState
import ru.samokat.employeeprofiles.api.darkstoreusers.internships.domain.FailureCode
import ru.samokat.employeeprofiles.api.darkstoreusers.internships.domain.InternshipStatus
import ru.samokat.employeeprofiles.api.darkstoreusers.internships.domain.RejectionCode
import ru.samokat.my.domain.employee.EmployeeRole
import ru.samokat.employeeprofiles.api.common.domain.EmployeeVehicleType
import ru.samokat.my.rest.api.enum.ApiEnum
import ru.samokat.mysamokat.tests.checkers.EmployeeAssertion
import ru.samokat.mysamokat.tests.dataproviders.Constants
import ru.samokat.mysamokat.tests.dataproviders.preconditions.EmployeePreconditions
import ru.samokat.mysamokat.tests.helpers.actions.EmployeeActions
import java.time.Instant
import java.time.temporal.ChronoUnit

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Tag("empro")
class GetDarkstoreProfileById {

    private lateinit var employeeAssertion: EmployeeAssertion

    @Autowired
    private lateinit var employeeActions: EmployeeActions

    private lateinit var employeePreconditions: EmployeePreconditions

    @BeforeEach
    fun before() {
        employeePreconditions = EmployeePreconditions()
        employeeAssertion = EmployeeAssertion()
        employeeActions.deleteProfile(Constants.mobile1)
    }

    @AfterEach
    fun release() {
        employeeAssertion.assertAll()
        employeeActions.deleteProfile(Constants.mobile1)
    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Get darkstore profile info: deliveryman (new)")
    fun profileDeliverymanOnDarkstore() {

        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
                email = null
            )

        val createdProfileId = employeeActions.createProfileId(createRequest)

        val darkstoreProfile = employeeActions.getDarkstoreUserById(
            createdProfileId,
            createRequest.darkstoreId!!,
            DarkstoreUserRole.DELIVERYMAN
        )

        employeeAssertion.checkDarkstoreUserProfile(
            dsProfile = darkstoreProfile,
            createRequest = createRequest,
            DarkstoreUserState.NEW,
            version = 1
        )
    }

    @Test
    @DisplayName("Get darkstore profile info: picker (new)")
    fun profilePickerOnDarkstore() {
        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.PICKER)),
                vehicle = null,
                email = null
            )

        val createdProfileId = employeeActions.createProfileId(createRequest)

        val darkstoreProfile = employeeActions.getDarkstoreUserById(
            createdProfileId,
            createRequest.darkstoreId!!,
            DarkstoreUserRole.PICKER
        )

        employeeAssertion.checkDarkstoreUserProfile(
            dsProfile = darkstoreProfile,
            createRequest = createRequest,
            DarkstoreUserState.NEW,
            version = 1
        )
    }

    @Test
    @DisplayName("Get darkstore profile info: daliveryman-picker")
    fun profileDeliverymanPickerOnDarkstore() {
        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN), ApiEnum(EmployeeRole.PICKER)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
                email = null
            )

        val createdProfileId = employeeActions.createProfileId(createRequest)

        val darkstoreProfileDeliveryman = employeeActions.getDarkstoreUserById(
            createdProfileId,
            createRequest.darkstoreId!!,
            DarkstoreUserRole.DELIVERYMAN
        )
        val darkstoreProfilePicker = employeeActions.getDarkstoreUserById(
            createdProfileId,
            createRequest.darkstoreId!!,
            DarkstoreUserRole.PICKER
        )

        employeeAssertion.checkDarkstoreUserProfile(
            dsProfile = darkstoreProfileDeliveryman,
            createRequest = createRequest,
            DarkstoreUserState.NEW,
            version = 1
        )
            .checkDarkstoreUserProfile(
                dsProfile = darkstoreProfilePicker,
                createRequest = createRequest,
                DarkstoreUserState.NEW,
                version = 1
            )
    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Get darkstore profile info: working status")
    fun profileOnDarkstoreInWorkingStatus() {
        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
                email = null
            )

        val createdProfileId = employeeActions.createProfileId(createRequest)

        val updateDSBuilder = employeePreconditions.fillUpdateProfileStatusOnDarkstore(
            createdProfileId, ApiEnum(DarkstoreUserState.WORKING), null
        )

        employeeActions.updateProfileStatusOnDarkstore(
            createdProfileId,
            createRequest.darkstoreId!!,
            DarkstoreUserRole.DELIVERYMAN,
            updateDSBuilder
        )


        val darkstoreProfile = employeeActions.getDarkstoreUserById(
            createdProfileId,
            createRequest.darkstoreId!!,
            DarkstoreUserRole.DELIVERYMAN
        )

        employeeAssertion.checkDarkstoreUserProfile(
            dsProfile = darkstoreProfile,
            createRequest = createRequest,
            DarkstoreUserState.WORKING,
            version = 2
        )
    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Get darkstore profile info: not_working status")
    fun profileOnDarkstoreInNotWorkingStatus() {

        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
                email = null
            )

        val createdProfileId = employeeActions.createProfileId(createRequest)

        val updateDSBuilder = employeePreconditions.fillUpdateProfileStatusOnDarkstore(
            createdProfileId,
            ApiEnum(DarkstoreUserState.NOT_WORKING),
            "employee_request"
        )

        employeeActions.updateProfileStatusOnDarkstore(
            createdProfileId,
            createRequest.darkstoreId!!,
            DarkstoreUserRole.DELIVERYMAN,
            updateDSBuilder
        )

        val darkstoreProfile = employeeActions.getDarkstoreUserById(
            createdProfileId,
            createRequest.darkstoreId!!,
            DarkstoreUserRole.DELIVERYMAN
        )

        employeeAssertion.checkDarkstoreUserProfile(
            dsProfile = darkstoreProfile,
            createRequest = createRequest,
            DarkstoreUserState.NOT_WORKING,
            version = 2,
            inactivityReason = "employee_request"
        )
    }

    @Test
    @DisplayName("Get darkstore profile info: profile does not exist on darkstore")
    fun profileDoesNotExistsOnDarkstore() {

        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
                email = null
            )

        val createdProfileId = employeeActions.createProfileId(createRequest)

        val errorMessage = employeeActions.getDarkstoreUserByIdError(
            createdProfileId,
            Constants.updatedDarkstoreId,
            DarkstoreUserRole.DELIVERYMAN
        )

        employeeAssertion.checkErrorMessage(errorMessage, "Darkstore user was not found")
    }

    @Test
    @DisplayName("Get darkstore profile info: profile does not exist in this role on darkstore")
    fun profileDoesNotExistsInThisRoleOnDarkstore() {

        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
                email = null
            )

        val createdProfileId = employeeActions.createProfileId(createRequest)

        val errorMessage = employeeActions.getDarkstoreUserByIdError(
            createdProfileId,
            createRequest.darkstoreId!!,
            DarkstoreUserRole.PICKER
        )

        employeeAssertion.checkErrorMessage(errorMessage, "Darkstore user was not found")

    }

    @Test
    @DisplayName("Get darkstore profile info: profile disabled")
    fun profileIsDisable() {

        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
                email = null
            )

        val createdProfileId = employeeActions.createProfileId(createRequest)
        employeeActions.deleteProfile(createdProfileId)

        val errorMessage = employeeActions.getDarkstoreUserByIdError(
            createdProfileId,
            createRequest.darkstoreId!!,
            DarkstoreUserRole.DELIVERYMAN
        )

        employeeAssertion.checkErrorMessage(errorMessage, "Darkstore user was not found")
    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Get darkstore profile info: check isIntern flag for new with planned internship")
    fun checkIsInternFlagWithForNewPlannedInternship() {
        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
                email = null,
                mobile = Constants.mobile1,
            )
        employeePreconditions.setCreateInternshipRequest(
            createRequest.darkstoreId!!,
            plannedDate = Instant.now().plusSeconds(60).truncatedTo(ChronoUnit.SECONDS),
            issuerProfileId = createRequest.issuerProfileId
        )

        val createdProfileId = employeeActions.createProfileId(createRequest)
        employeeActions.createInternship(
            createdProfileId,
            DarkstoreUserRole.DELIVERYMAN,
            employeePreconditions.createInternshipRequest()
        )

        val darkstoreProfile = employeeActions.getDarkstoreUserById(
            createdProfileId,
            createRequest.darkstoreId!!,
            DarkstoreUserRole.DELIVERYMAN
        )

        employeeAssertion.checkDarkstoreUserProfile(
            dsProfile = darkstoreProfile,
            createRequest = createRequest,
            state = DarkstoreUserState.NEW,
            version = 2
        )
            .checkIsInternFlag(darkstoreProfile.isIntern, true)

    }

    @Test
    @DisplayName("Get darkstore profile info: check isIntern flag for not_working with canceled internship")
    fun checkIsInternFlagWithForNotWorkingCancelledInternship() {
        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
                email = null,
                mobile = Constants.mobile1,
            )
        employeePreconditions.setCreateInternshipRequest(
            createRequest.darkstoreId!!,
            plannedDate = Instant.now().plusSeconds(60).truncatedTo(ChronoUnit.SECONDS),
            issuerProfileId = createRequest.issuerProfileId
        )
            .setCancelInternshipRequest(issuerProfileId = createRequest.issuerProfileId)

        val createdProfileId = employeeActions.createProfileId(createRequest)
        employeeActions.createInternship(
            createdProfileId,
            DarkstoreUserRole.DELIVERYMAN,
            employeePreconditions.createInternshipRequest()
        )
            .changeInternshipDateInDatabase(createdProfileId, "2021-08-20 13:08:30+03")
            .cancelInternshipWithoutWaiting(
                employeePreconditions.createInternshipRequest().plannedDate,
                createdProfileId,
                DarkstoreUserRole.DELIVERYMAN,
                employeePreconditions.cancelInternshipRequest()
            )

        val darkstoreProfile = employeeActions.getDarkstoreUserById(
            createdProfileId,
            createRequest.darkstoreId!!,
            DarkstoreUserRole.DELIVERYMAN
        )

        employeeAssertion.checkDarkstoreUserProfile(
            dsProfile = darkstoreProfile,
            createRequest = createRequest,
            state = DarkstoreUserState.NOT_WORKING,
            inactivityReason = "internship_failure",
            version = 3
        )
            .checkIsInternFlag(darkstoreProfile.isIntern, true)

    }

    @Test
    @DisplayName("Get darkstore profile info: check isIntern flag for not_working with rejected internship")
    fun checkIsInternFlagWithForNotWorkingRejectedInternship() {
        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
                email = null,
                mobile = Constants.mobile1,
            )
        employeePreconditions.setCreateInternshipRequest(
            createRequest.darkstoreId!!,
            plannedDate = Instant.now().plusSeconds(30).truncatedTo(ChronoUnit.SECONDS),
            issuerProfileId = createRequest.issuerProfileId
        )
            .setRejectInternshipRequest((ApiEnum(RejectionCode.R001)), issuerProfileId = createRequest.issuerProfileId)

        val createdProfileId = employeeActions.createProfileId(createRequest)
        employeeActions.createInternship(
            createdProfileId,
            DarkstoreUserRole.DELIVERYMAN,
            employeePreconditions.createInternshipRequest()
        )
            .changeInternshipDateInDatabase(createdProfileId, "2021-08-20 13:08:30+03")
            .rejectInternshipWithoutWaiting(
                employeePreconditions.createInternshipRequest().plannedDate,
                createdProfileId,
                DarkstoreUserRole.DELIVERYMAN,
                employeePreconditions.rejectInternshipRequest()
            )

        val darkstoreProfile = employeeActions.getDarkstoreUserById(
            createdProfileId,
            createRequest.darkstoreId!!,
            DarkstoreUserRole.DELIVERYMAN
        )

        employeeAssertion.checkDarkstoreUserProfile(
            dsProfile = darkstoreProfile,
            createRequest = createRequest,
            state = DarkstoreUserState.NOT_WORKING,
            version = 3,
            inactivityReason = "internship_failure"
        )
            .checkIsInternFlag(darkstoreProfile.isIntern, true)

    }

    @Test
    @DisplayName("Get darkstore profile info: check isIntern flag for not_working with failed internship")
    fun checkIsInternFlagWithForNotWorkingFailedInternship() {
        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
                email = null,
                mobile = Constants.mobile1,
            )
        employeePreconditions.setCreateInternshipRequest(
            createRequest.darkstoreId!!,
            plannedDate = Instant.now().plusSeconds(30).truncatedTo(ChronoUnit.SECONDS),
            issuerProfileId = createRequest.issuerProfileId
        )
            .setCloseInternshipRequest(
                failureCode = (ApiEnum(FailureCode.F006)),
                status = (ApiEnum(InternshipStatus.FAILED)),
                issuerProfileId = createRequest.issuerProfileId
            )
        val createdProfileId = employeeActions.createProfileId(createRequest)
        employeeActions.createInternship(
            createdProfileId,
            DarkstoreUserRole.DELIVERYMAN,
            employeePreconditions.createInternshipRequest()
        )
            .changeInternshipDateInDatabase(createdProfileId, "2021-08-20 13:08:30+03")
            .closeInternshipWithoutWaiting(
                employeePreconditions.createInternshipRequest().plannedDate,
                createdProfileId,
                DarkstoreUserRole.DELIVERYMAN,
                employeePreconditions.closeInternshipRequest()
            )

        val darkstoreProfile = employeeActions.getDarkstoreUserById(
            createdProfileId,
            createRequest.darkstoreId!!,
            DarkstoreUserRole.DELIVERYMAN
        )

        employeeAssertion.checkDarkstoreUserProfile(
            dsProfile = darkstoreProfile,
            createRequest = createRequest,
            state = DarkstoreUserState.NOT_WORKING,
            version = 3,
            inactivityReason = "internship_failure"
        )
            .checkIsInternFlag(darkstoreProfile.isIntern, false)

    }

    @Test
    @DisplayName("Get darkstore profile info: check isIntern flag for working with done internship")
    fun checkIsInternFlagWithForWorkingDoneInternship() {
        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
                email = null,
                mobile = Constants.mobile1,
            )
        employeePreconditions.setCreateInternshipRequest(
            createRequest.darkstoreId!!,
            plannedDate = Instant.now().plusSeconds(30).truncatedTo(ChronoUnit.SECONDS),
            issuerProfileId = createRequest.issuerProfileId
        )
            .setCloseInternshipRequest(
                status = (ApiEnum(InternshipStatus.DONE)),
                issuerProfileId = createRequest.issuerProfileId
            )
        val createdProfileId = employeeActions.createProfileId(createRequest)
        employeeActions.createInternship(
            createdProfileId,
            DarkstoreUserRole.DELIVERYMAN,
            employeePreconditions.createInternshipRequest()
        )
            .changeInternshipDateInDatabase(createdProfileId, "2021-08-20 13:08:30+03")
            .closeInternshipWithoutWaiting(
                employeePreconditions.createInternshipRequest().plannedDate,
                createdProfileId,
                DarkstoreUserRole.DELIVERYMAN,
                employeePreconditions.closeInternshipRequest()
            )

        val darkstoreProfile = employeeActions.getDarkstoreUserById(
            createdProfileId,
            createRequest.darkstoreId!!,
            DarkstoreUserRole.DELIVERYMAN
        )

        employeeAssertion.checkDarkstoreUserProfile(
            dsProfile = darkstoreProfile,
            createRequest = createRequest,
            state = DarkstoreUserState.WORKING,
            version = 3
        )
            .checkIsInternFlag(darkstoreProfile.isIntern, false)
    }

    @Test
    @DisplayName("Get darkstore profile info: check isIntern flag for not_working with done internship")
    fun checkIsInternFlagWithForNotWorkingDoneInternship() {
        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
                email = null,
                mobile = Constants.mobile1,
            )
        employeePreconditions.setCreateInternshipRequest(
            createRequest.darkstoreId!!,
            plannedDate = Instant.now().plusSeconds(30).truncatedTo(ChronoUnit.SECONDS),
            issuerProfileId = createRequest.issuerProfileId
        )
            .setCloseInternshipRequest(
                status = (ApiEnum(InternshipStatus.DONE)),
                issuerProfileId = createRequest.issuerProfileId
            )

        val createdProfileId = employeeActions.createProfileId(createRequest)
        val updateDSBuilderNotWorking = employeePreconditions.fillUpdateProfileStatusOnDarkstore(
            createdProfileId,
            ApiEnum(DarkstoreUserState.NOT_WORKING),
            "employee_request", 3
        )
        employeeActions.createInternship(
            createdProfileId,
            DarkstoreUserRole.DELIVERYMAN,
            employeePreconditions.createInternshipRequest()
        )
            .changeInternshipDateInDatabase(createdProfileId, "2021-08-20 13:08:30+03")
            .closeInternshipWithoutWaiting(
                employeePreconditions.createInternshipRequest().plannedDate,
                createdProfileId,
                DarkstoreUserRole.DELIVERYMAN,
                employeePreconditions.closeInternshipRequest()
            )
        employeeActions.updateProfileStatusOnDarkstore(
            createdProfileId,
            createRequest.darkstoreId!!,
            DarkstoreUserRole.DELIVERYMAN,
            updateDSBuilderNotWorking
        )

        val darkstoreProfile = employeeActions.getDarkstoreUserById(
            createdProfileId,
            createRequest.darkstoreId!!,
            DarkstoreUserRole.DELIVERYMAN
        )

        employeeAssertion.checkDarkstoreUserProfile(
            dsProfile = darkstoreProfile,
            createRequest = createRequest,
            state = DarkstoreUserState.NOT_WORKING,
            version = 4,
            inactivityReason = "employee_request",
        )
            .checkIsInternFlag(darkstoreProfile.isIntern, false)
    }

}
