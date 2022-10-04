package ru.samokat.mysamokat.tests.tests.employee_profiles.darkstoreUserInternshipAPI

import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.samokat.employeeprofiles.api.common.domain.EmployeeVehicleType
import ru.samokat.employeeprofiles.api.common.domain.Vehicle
import ru.samokat.employeeprofiles.api.darkstoreusers.domain.DarkstoreUserRole
import ru.samokat.employeeprofiles.api.darkstoreusers.internships.domain.FailureCode
import ru.samokat.employeeprofiles.api.darkstoreusers.internships.domain.InternshipStatus
import ru.samokat.employeeprofiles.api.darkstoreusers.internships.domain.RejectionCode
import ru.samokat.my.domain.employee.EmployeeRole

import ru.samokat.my.rest.api.enum.ApiEnum
import ru.samokat.mysamokat.tests.checkers.EmployeeAssertion
import ru.samokat.mysamokat.tests.dataproviders.Constants
import ru.samokat.mysamokat.tests.dataproviders.preconditions.EmployeePreconditions
import ru.samokat.mysamokat.tests.helpers.actions.EmployeeActions
import ru.samokat.mysamokat.tests.helpers.controllers.database.EmployeeProfilesDatabaseController
import java.time.Instant
import java.time.temporal.ChronoUnit

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Tag("empro")
class ChangeStateInternship {

    private lateinit var employeeAssertion: EmployeeAssertion

    @Autowired
    private lateinit var employeeActions: EmployeeActions

    private lateinit var employeePreconditions: EmployeePreconditions

    @Autowired
    private lateinit var databaseController: EmployeeProfilesDatabaseController


    @BeforeEach
    fun setUp() {
        employeePreconditions = EmployeePreconditions()
        employeeAssertion = EmployeeAssertion()
        employeeActions.deleteProfile(Constants.mobile1)
    }

    @AfterEach
    fun release() {
        employeeAssertion.assertAll()
        employeeActions.deleteProfile(Constants.mobile1)
        employeeActions.deleteProfile(Constants.mobile2)
    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Cancel deliveryman Internship")
    fun cancelDeliverymanInternshipsTest() {
        employeePreconditions
            .setCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.PERSONAL_BICYCLE))
            )
            .setCreateInternshipRequest(
                employeePreconditions.createProfileRequest().darkstoreId!!,
                plannedDate = Instant.now().plusSeconds(3).truncatedTo(ChronoUnit.SECONDS)
            )
            .setCancelInternshipRequest()
            .setGetInternshipRequest(
                from = employeePreconditions.createInternshipRequest().plannedDate.minusSeconds(60),
                to = employeePreconditions.createInternshipRequest().plannedDate.plusSeconds(120)
            )


        employeeActions
            .createProfile(employeePreconditions.createProfileRequest())
            .createInternship(
                employeeActions.createdProfileResponse().profileId,
                DarkstoreUserRole.DELIVERYMAN,
                employeePreconditions.createInternshipRequest()
            )
            .cancelInternship(
                employeePreconditions.createInternshipRequest().plannedDate,
                employeeActions.createdProfileResponse().profileId,
                DarkstoreUserRole.DELIVERYMAN,
                employeePreconditions.cancelInternshipRequest()
            )
            .getInternship(employeeActions.createdProfileResponse().profileId)
            .setInternship(databaseController.getInternship(employeeActions.createdProfileResponse().profileId))
            .getDSInternship(
                employeePreconditions.createProfileRequest().darkstoreId!!,
                employeePreconditions.getInternshipDSRequest()
            )
        employeeAssertion
            .checkInternshipFromDB(
                employeePreconditions.createInternshipRequest().darkstoreId,
                "deliveryman",
                "canceled",
                employeeActions.getInternship()
            )
            .checkInternshipLogFromDB(
                databaseController.getInternshipLogArray(employeeActions.createdProfileResponse().profileId),
                2,
                1,
                "canceled"
            )

            .checkInternshipAPIResponse(
                employeeActions.getCreatedInternship(),
                employeePreconditions.createInternshipRequest(),
                ApiEnum(InternshipStatus.CANCELED)
            )

            .checkInternshipAPIResponse(
                employeeActions.getCreatedDsInternship(),
                employeePreconditions.createInternshipRequest(),
                ApiEnum(InternshipStatus.CANCELED)
            )
            .checkInternStatusForDSUser(
                employeeActions.getDarkstoreUserById(
                    employeeActions.createdProfileResponse().profileId,
                    employeePreconditions.createProfileRequest().darkstoreId!!,
                    DarkstoreUserRole.DELIVERYMAN,
                ), true
            )
            .checkDSUserInDatabase(
                databaseController.getDSUserProfile(
                    employeeActions.createdProfileResponse().profileId,
                    employeePreconditions.createProfileRequest().roles[0].value
                ),
                employeePreconditions.createProfileRequest().darkstoreId!!,
                5
            )

    }

    @Test
    @DisplayName("Cancel picker Internship")
    fun cancelPickerInternshipsTest() {

        employeePreconditions
            .setCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.PICKER)),
                vehicle = null
            )
            .setCreateInternshipRequest(
                employeePreconditions.createProfileRequest().darkstoreId!!,
                plannedDate = Instant.now().plusSeconds(3).truncatedTo(ChronoUnit.SECONDS)
            )
            .setCancelInternshipRequest()
            .setGetInternshipRequest(
                from = employeePreconditions.createInternshipRequest().plannedDate.minusSeconds(60),
                to = employeePreconditions.createInternshipRequest().plannedDate.plusSeconds(120)
            )


        employeeActions
            .createProfile(employeePreconditions.createProfileRequest())
            .createInternship(
                employeeActions.createdProfileResponse().profileId,
                DarkstoreUserRole.PICKER,
                employeePreconditions.createInternshipRequest()
            )
            .cancelInternship(
                employeePreconditions.createInternshipRequest().plannedDate,
                employeeActions.createdProfileResponse().profileId,
                DarkstoreUserRole.PICKER,
                employeePreconditions.cancelInternshipRequest()
            )
            .getInternship(employeeActions.createdProfileResponse().profileId)
            .setInternship(databaseController.getInternship(employeeActions.createdProfileResponse().profileId))
            .getDSInternship(
                employeePreconditions.createProfileRequest().darkstoreId!!,
                employeePreconditions.getInternshipDSRequest()
            )
        employeeAssertion
            .checkInternshipFromDB(
                employeePreconditions.createInternshipRequest().darkstoreId,
                "picker",
                "canceled",
                employeeActions.getInternship()
            )
            .checkInternshipLogFromDB(
                databaseController.getInternshipLogArray(employeeActions.createdProfileResponse().profileId),
                2,
                1,
                "canceled"
            )

            .checkInternshipAPIResponse(
                employeeActions.getCreatedInternship(),
                employeePreconditions.createInternshipRequest(),
                ApiEnum(InternshipStatus.CANCELED)
            )

            .checkInternshipAPIResponse(
                employeeActions.getCreatedDsInternship(),
                employeePreconditions.createInternshipRequest(),
                ApiEnum(InternshipStatus.CANCELED)
            )
            .checkInternStatusForDSUser(
                employeeActions.getDarkstoreUserById(
                    employeeActions.createdProfileResponse().profileId,
                    employeePreconditions.createProfileRequest().darkstoreId!!,
                    DarkstoreUserRole.PICKER,
                ), true
            )
            .checkDSUserInDatabase(
                databaseController.getDSUserProfile(
                    employeeActions.createdProfileResponse().profileId,
                    employeePreconditions.createProfileRequest().roles[0].value
                ),
                employeePreconditions.createProfileRequest().darkstoreId!!,
                5
            )

    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Reject deliveryman Internship")
    fun rejectDeliverymanInternshipsTest() {

        employeePreconditions
            .setCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.PERSONAL_BICYCLE))
            )
            .setCreateInternshipRequest(
                employeePreconditions.createProfileRequest().darkstoreId!!,
                plannedDate = Instant.now().plusSeconds(3).truncatedTo(ChronoUnit.SECONDS)
            )
            .setRejectInternshipRequest((ApiEnum(RejectionCode.R008)))
            .setGetInternshipRequest(
                from = employeePreconditions.createInternshipRequest().plannedDate.minusSeconds(60),
                to = employeePreconditions.createInternshipRequest().plannedDate.plusSeconds(120)
            )


        employeeActions
            .createProfile(employeePreconditions.createProfileRequest())
            .createInternship(
                employeeActions.createdProfileResponse().profileId,
                DarkstoreUserRole.DELIVERYMAN,
                employeePreconditions.createInternshipRequest()
            )

            .rejectInternship(
                employeePreconditions.createInternshipRequest().plannedDate,
                employeeActions.createdProfileResponse().profileId,
                DarkstoreUserRole.DELIVERYMAN,
                employeePreconditions.rejectInternshipRequest()
            )
            .getInternship(employeeActions.createdProfileResponse().profileId)
            .setInternship(databaseController.getInternship(employeeActions.createdProfileResponse().profileId))
            .getDSInternship(
                employeePreconditions.createProfileRequest().darkstoreId!!,
                employeePreconditions.getInternshipDSRequest()
            )
        employeeAssertion
            .checkInternshipFromDB(
                employeePreconditions.createInternshipRequest().darkstoreId,
                "deliveryman",
                "rejected",
                employeeActions.getInternship()
            )
            .checkInternshipLogFromDB(
                databaseController.getInternshipLogArray(employeeActions.createdProfileResponse().profileId),
                2,
                1,
                "rejected"
            )

            .checkInternshipAPIResponse(
                employeeActions.getCreatedInternship(),
                employeePreconditions.createInternshipRequest(),
                ApiEnum(InternshipStatus.REJECTED)
            )
            .checkInternshipAPIResponse(
                employeeActions.getCreatedDsInternship(),
                employeePreconditions.createInternshipRequest(),
                ApiEnum(InternshipStatus.REJECTED)

            )
            .checkInternStatusForDSUser(
                employeeActions.getDarkstoreUserById(
                    employeeActions.createdProfileResponse().profileId,
                    employeePreconditions.createProfileRequest().darkstoreId!!,
                    DarkstoreUserRole.DELIVERYMAN,
                ), true
            )
            .checkDSUserInDatabase(
                databaseController.getDSUserProfile(
                    employeeActions.createdProfileResponse().profileId,
                    employeePreconditions.createProfileRequest().roles[0].value
                ),
                employeePreconditions.createProfileRequest().darkstoreId!!,
                5
            )
    }

    @Test
    @DisplayName("Reject picker Internship")
    fun rejectPickerInternshipsTest() {

        employeePreconditions
            .setCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.PICKER)),
                vehicle = null
            )
            .setCreateInternshipRequest(
                employeePreconditions.createProfileRequest().darkstoreId!!,
                plannedDate = Instant.now().plusSeconds(3).truncatedTo(ChronoUnit.SECONDS)
            )
            .setRejectInternshipRequest((ApiEnum(RejectionCode.R001)))
            .setGetInternshipRequest(
                from = employeePreconditions.createInternshipRequest().plannedDate.minusSeconds(60),
                to = employeePreconditions.createInternshipRequest().plannedDate.plusSeconds(120)
            )


        employeeActions
            .createProfile(employeePreconditions.createProfileRequest())
            .createInternship(
                employeeActions.createdProfileResponse().profileId,
                DarkstoreUserRole.PICKER,
                employeePreconditions.createInternshipRequest()
            )

            .rejectInternship(
                employeePreconditions.createInternshipRequest().plannedDate,
                employeeActions.createdProfileResponse().profileId,
                DarkstoreUserRole.PICKER,
                employeePreconditions.rejectInternshipRequest()
            )
            .getInternship(employeeActions.createdProfileResponse().profileId)
            .setInternship(databaseController.getInternship(employeeActions.createdProfileResponse().profileId))
            .getDSInternship(
                employeePreconditions.createProfileRequest().darkstoreId!!,
                employeePreconditions.getInternshipDSRequest()
            )
        employeeAssertion
            .checkInternshipFromDB(
                employeePreconditions.createInternshipRequest().darkstoreId,
                "picker",
                "rejected",
                employeeActions.getInternship()
            )
            .checkInternshipLogFromDB(
                databaseController.getInternshipLogArray(employeeActions.createdProfileResponse().profileId),
                2,
                1,
                "rejected"
            )

            .checkInternshipAPIResponse(
                employeeActions.getCreatedInternship(),
                employeePreconditions.createInternshipRequest(),
                ApiEnum(InternshipStatus.REJECTED)
            )
            .checkInternshipAPIResponse(
                employeeActions.getCreatedDsInternship(),
                employeePreconditions.createInternshipRequest(),
                ApiEnum(InternshipStatus.REJECTED)

            )
            .checkInternStatusForDSUser(
                employeeActions.getDarkstoreUserById(
                    employeeActions.createdProfileResponse().profileId,
                    employeePreconditions.createProfileRequest().darkstoreId!!,
                    DarkstoreUserRole.PICKER,
                ), true
            )
            .checkDSUserInDatabase(
                databaseController.getDSUserProfile(
                    employeeActions.createdProfileResponse().profileId,
                    employeePreconditions.createProfileRequest().roles[0].value
                ),
                employeePreconditions.createProfileRequest().darkstoreId!!,
                5
            )
    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Fail deliveryman Internship")
    fun failDeliverymanInternshipsTest() {
        employeePreconditions
            .setCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.PERSONAL_BICYCLE))
            )
            .setCreateInternshipRequest(
                employeePreconditions.createProfileRequest().darkstoreId!!,
                plannedDate = Instant.now().plusSeconds(3).truncatedTo(ChronoUnit.SECONDS)
            )
            .setCloseInternshipRequest(
                failureCode = (ApiEnum(FailureCode.F009)),
                status = (ApiEnum(InternshipStatus.FAILED))
            )

            .setGetInternshipRequest(
                from = employeePreconditions.createInternshipRequest().plannedDate.minusSeconds(60),
                to = employeePreconditions.createInternshipRequest().plannedDate.plusSeconds(120)
            )


        employeeActions
            .createProfile(employeePreconditions.createProfileRequest())
            .createInternship(
                employeeActions.createdProfileResponse().profileId,
                DarkstoreUserRole.DELIVERYMAN,
                employeePreconditions.createInternshipRequest()
            )
            .closeInternship(
                employeePreconditions.createInternshipRequest().plannedDate,
                employeeActions.createdProfileResponse().profileId,
                DarkstoreUserRole.DELIVERYMAN,
                employeePreconditions.closeInternshipRequest()
            )
            .getInternship(employeeActions.createdProfileResponse().profileId)
            .setInternship(databaseController.getInternship(employeeActions.createdProfileResponse().profileId))
            .getDSInternship(
                employeePreconditions.createProfileRequest().darkstoreId!!,
                employeePreconditions.getInternshipDSRequest()
            )
        employeeAssertion
            .checkInternshipFromDB(
                employeePreconditions.createInternshipRequest().darkstoreId,
                "deliveryman",
                "failed",
                employeeActions.getInternship()
            )
            .checkInternshipLogFromDB(
                databaseController.getInternshipLogArray(employeeActions.createdProfileResponse().profileId),
                2,
                1,
                "failed"
            )

            .checkInternshipAPIResponse(
                employeeActions.getCreatedInternship(),
                employeePreconditions.createInternshipRequest(),
                ApiEnum(InternshipStatus.FAILED)
            )
            .checkInternshipAPIResponse(
                employeeActions.getCreatedDsInternship(),
                employeePreconditions.createInternshipRequest(),
                ApiEnum(InternshipStatus.FAILED)

            )
            .checkInternStatusForDSUser(
                employeeActions.getDarkstoreUserById(
                    employeeActions.createdProfileResponse().profileId,
                    employeePreconditions.createProfileRequest().darkstoreId!!,
                    DarkstoreUserRole.DELIVERYMAN,
                ), false
            )
            .checkDSUserInDatabase(
                databaseController.getDSUserProfile(
                    employeeActions.createdProfileResponse().profileId,
                    employeePreconditions.createProfileRequest().roles[0].value
                ),
                employeePreconditions.createProfileRequest().darkstoreId!!,
                5
            )
    }

    @Test
    @DisplayName("Fail picker Internship")
    fun failPickerInternshipsTest() {
        employeePreconditions
            .setCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.PICKER)),
                vehicle = null
            )
            .setCreateInternshipRequest(
                employeePreconditions.createProfileRequest().darkstoreId!!,
                plannedDate = Instant.now().plusSeconds(3).truncatedTo(ChronoUnit.SECONDS)
            )
            .setCloseInternshipRequest(
                failureCode = (ApiEnum(FailureCode.F007)),
                status = (ApiEnum(InternshipStatus.FAILED))
            )

            .setGetInternshipRequest(
                from = employeePreconditions.createInternshipRequest().plannedDate.minusSeconds(60),
                to = employeePreconditions.createInternshipRequest().plannedDate.plusSeconds(120)
            )


        employeeActions
            .createProfile(employeePreconditions.createProfileRequest())
            .createInternship(
                employeeActions.createdProfileResponse().profileId,
                DarkstoreUserRole.PICKER,
                employeePreconditions.createInternshipRequest()
            )
            .closeInternship(
                employeePreconditions.createInternshipRequest().plannedDate,
                employeeActions.createdProfileResponse().profileId,
                DarkstoreUserRole.PICKER,
                employeePreconditions.closeInternshipRequest()
            )
            .getInternship(employeeActions.createdProfileResponse().profileId)
            .setInternship(databaseController.getInternship(employeeActions.createdProfileResponse().profileId))
            .getDSInternship(
                employeePreconditions.createProfileRequest().darkstoreId!!,
                employeePreconditions.getInternshipDSRequest()
            )
        employeeAssertion
            .checkInternshipFromDB(
                employeePreconditions.createInternshipRequest().darkstoreId,
                "picker",
                "failed",
                employeeActions.getInternship()
            )
            .checkInternshipLogFromDB(
                databaseController.getInternshipLogArray(employeeActions.createdProfileResponse().profileId),
                2,
                1,
                "failed"
            )

            .checkInternshipAPIResponse(
                employeeActions.getCreatedInternship(),
                employeePreconditions.createInternshipRequest(),
                ApiEnum(InternshipStatus.FAILED)
            )
            .checkInternshipAPIResponse(
                employeeActions.getCreatedDsInternship(),
                employeePreconditions.createInternshipRequest(),
                ApiEnum(InternshipStatus.FAILED)

            )
            .checkInternStatusForDSUser(
                employeeActions.getDarkstoreUserById(
                    employeeActions.createdProfileResponse().profileId,
                    employeePreconditions.createProfileRequest().darkstoreId!!,
                    DarkstoreUserRole.PICKER,
                ), false
            )
            .checkDSUserInDatabase(
                databaseController.getDSUserProfile(
                    employeeActions.createdProfileResponse().profileId,
                    employeePreconditions.createProfileRequest().roles[0].value
                ),
                employeePreconditions.createProfileRequest().darkstoreId!!,
                5
            )
    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Done deliveryman Internship")
    fun doneDeliverymanInternshipsTest() {
        employeePreconditions
            .setCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.PERSONAL_BICYCLE))
            )
            .setCreateInternshipRequest(
                employeePreconditions.createProfileRequest().darkstoreId!!,
                plannedDate = Instant.now().plusSeconds(3).truncatedTo(ChronoUnit.SECONDS)
            )
            .setCloseInternshipRequest(
                status = (ApiEnum(InternshipStatus.DONE))
            )

            .setGetInternshipRequest(
                from = employeePreconditions.createInternshipRequest().plannedDate.minusSeconds(60),
                to = employeePreconditions.createInternshipRequest().plannedDate.plusSeconds(120)
            )


        employeeActions
            .createProfile(employeePreconditions.createProfileRequest())
            .createInternship(
                employeeActions.createdProfileResponse().profileId,
                DarkstoreUserRole.DELIVERYMAN,
                employeePreconditions.createInternshipRequest()
            )
            .closeInternship(
                employeePreconditions.createInternshipRequest().plannedDate,
                employeeActions.createdProfileResponse().profileId,
                DarkstoreUserRole.DELIVERYMAN,
                employeePreconditions.closeInternshipRequest()
            )
            .getInternship(employeeActions.createdProfileResponse().profileId)
            .setInternship(databaseController.getInternship(employeeActions.createdProfileResponse().profileId))
            .getDSInternship(
                employeePreconditions.createProfileRequest().darkstoreId!!,
                employeePreconditions.getInternshipDSRequest()
            )
        employeeAssertion
            .checkInternshipFromDB(
                employeePreconditions.createInternshipRequest().darkstoreId,
                "deliveryman",
                "done",
                employeeActions.getInternship()
            )
            .checkInternshipLogFromDB(
                databaseController.getInternshipLogArray(employeeActions.createdProfileResponse().profileId),
                2,
                1,
                "done"
            )

            .checkInternshipAPIResponse(
                employeeActions.getCreatedInternship(),
                employeePreconditions.createInternshipRequest(),
                ApiEnum(InternshipStatus.DONE)
            )
            .checkInternshipAPIResponse(
                employeeActions.getCreatedDsInternship(),
                employeePreconditions.createInternshipRequest(),
                ApiEnum(InternshipStatus.DONE)

            )
            .checkInternStatusForDSUser(
                employeeActions.getDarkstoreUserById(
                    employeeActions.createdProfileResponse().profileId,
                    employeePreconditions.createProfileRequest().darkstoreId!!,
                    DarkstoreUserRole.DELIVERYMAN,
                ), false
            )
            .checkDSUserInDatabase(
                databaseController.getDSUserProfile(
                    employeeActions.createdProfileResponse().profileId,
                    employeePreconditions.createProfileRequest().roles[0].value
                ),
                employeePreconditions.createProfileRequest().darkstoreId!!,
                6
            )
    }

    @Test
    @DisplayName("Done picker Internship")
    fun donePickerInternshipsTest() {

        employeePreconditions
            .setCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.PICKER)),
                vehicle = null
            )
            .setCreateInternshipRequest(
                employeePreconditions.createProfileRequest().darkstoreId!!,
                plannedDate = Instant.now().plusSeconds(3).truncatedTo(ChronoUnit.SECONDS)
            )
            .setCloseInternshipRequest(
                status = (ApiEnum(InternshipStatus.DONE))
            )

            .setGetInternshipRequest(
                from = employeePreconditions.createInternshipRequest().plannedDate.minusSeconds(60),
                to = employeePreconditions.createInternshipRequest().plannedDate.plusSeconds(120)
            )


        employeeActions
            .createProfile(employeePreconditions.createProfileRequest())
            .createInternship(
                employeeActions.createdProfileResponse().profileId,
                DarkstoreUserRole.PICKER,
                employeePreconditions.createInternshipRequest()
            )
            .closeInternship(
                employeePreconditions.createInternshipRequest().plannedDate,
                employeeActions.createdProfileResponse().profileId,
                DarkstoreUserRole.PICKER,
                employeePreconditions.closeInternshipRequest()
            )
            .getInternship(employeeActions.createdProfileResponse().profileId)
            .setInternship(databaseController.getInternship(employeeActions.createdProfileResponse().profileId))
            .getDSInternship(
                employeePreconditions.createProfileRequest().darkstoreId!!,
                employeePreconditions.getInternshipDSRequest()
            )
        employeeAssertion
            .checkInternshipFromDB(
                employeePreconditions.createInternshipRequest().darkstoreId,
                "picker",
                "done",
                employeeActions.getInternship()
            )
            .checkInternshipLogFromDB(
                databaseController.getInternshipLogArray(employeeActions.createdProfileResponse().profileId),
                2,
                1,
                "done"
            )

            .checkInternshipAPIResponse(
                employeeActions.getCreatedInternship(),
                employeePreconditions.createInternshipRequest(),
                ApiEnum(InternshipStatus.DONE)
            )
            .checkInternshipAPIResponse(
                employeeActions.getCreatedDsInternship(),
                employeePreconditions.createInternshipRequest(),
                ApiEnum(InternshipStatus.DONE)

            )
            .checkInternStatusForDSUser(
                employeeActions.getDarkstoreUserById(
                    employeeActions.createdProfileResponse().profileId,
                    employeePreconditions.createProfileRequest().darkstoreId!!,
                    DarkstoreUserRole.PICKER,
                ), false
            )
            .checkDSUserInDatabase(
                databaseController.getDSUserProfile(
                    employeeActions.createdProfileResponse().profileId,
                    employeePreconditions.createProfileRequest().roles[0].value
                ),
                employeePreconditions.createProfileRequest().darkstoreId!!,
                6
            )
    }

    @Test
    @DisplayName("Reject picker Internship with deliveryman's reason is impossible")
    fun rejectPickerInternshipsWithInvalidReasonTest() {
        employeePreconditions
            .setCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.PICKER)),
                vehicle = null
            )
            .setCreateInternshipRequest(
                employeePreconditions.createProfileRequest().darkstoreId!!,
                plannedDate = Instant.now().plusSeconds(3).truncatedTo(ChronoUnit.SECONDS)
            )
            .setRejectInternshipRequest((ApiEnum(RejectionCode.R008)))



        employeeActions
            .createProfile(employeePreconditions.createProfileRequest())
            .createInternship(
                employeeActions.createdProfileResponse().profileId,
                DarkstoreUserRole.PICKER,
                employeePreconditions.createInternshipRequest()
            )
            .rejectInternshipUnsuccessful(
                employeePreconditions.createInternshipRequest().plannedDate,
                employeeActions.createdProfileResponse().profileId,
                DarkstoreUserRole.PICKER,
                employeePreconditions.rejectInternshipRequest()
            )
            .getInternship(employeeActions.createdProfileResponse().profileId)
            .setInternship(databaseController.getInternship(employeeActions.createdProfileResponse().profileId))

        employeeAssertion
            .checkStatusBadRequest(employeeActions.getRejectedInternship().statusCode)
            .checkInternshipFromDB(
                employeePreconditions.createInternshipRequest().darkstoreId,
                "picker",
                "planned",
                employeeActions.getInternship()
            )
            .checkInternshipAPIResponse(
                employeeActions.getCreatedInternship(),
                employeePreconditions.createInternshipRequest(),
                ApiEnum(InternshipStatus.PLANNED)
            )

    }

    @Test
    @DisplayName("Fail picker Internship with deliveryman's reason is impossible")
    fun failPickerInternshipsWithInvalidReasonTest() {

        employeePreconditions
            .setCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.PICKER)),
                vehicle = null
            )
            .setCreateInternshipRequest(
                employeePreconditions.createProfileRequest().darkstoreId!!,
                plannedDate = Instant.now().plusSeconds(3).truncatedTo(ChronoUnit.SECONDS)
            )
            .setCloseInternshipRequest(
                failureCode = (ApiEnum(FailureCode.F009)),
                status = (ApiEnum(InternshipStatus.FAILED))
            )

        employeeActions
            .createProfile(employeePreconditions.createProfileRequest())
            .createInternship(
                employeeActions.createdProfileResponse().profileId,
                DarkstoreUserRole.PICKER,
                employeePreconditions.createInternshipRequest()
            )
            .closeInternshipUnsuccessful(
                employeePreconditions.createInternshipRequest().plannedDate,
                employeeActions.createdProfileResponse().profileId,
                DarkstoreUserRole.PICKER,
                employeePreconditions.closeInternshipRequest()
            )
            .getInternship(employeeActions.createdProfileResponse().profileId)
            .setInternship(databaseController.getInternship(employeeActions.createdProfileResponse().profileId))

        employeeAssertion
            .checkStatusBadRequest(employeeActions.getClosedInternship().statusCode)
            .checkInternshipFromDB(
                employeePreconditions.createInternshipRequest().darkstoreId,
                "picker",
                "planned",
                employeeActions.getInternship()
            )
            .checkInternshipAPIResponse(
                employeeActions.getCreatedInternship(),
                employeePreconditions.createInternshipRequest(),
                ApiEnum(InternshipStatus.PLANNED)
            )

    }

    @Test
    @DisplayName("Reject failed Internship is impossible")
    fun rejectFailedInternshipsTest() {
        employeePreconditions
            .setCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.PERSONAL_BICYCLE))
            )
            .setCreateInternshipRequest(
                employeePreconditions.createProfileRequest().darkstoreId!!,
                plannedDate = Instant.now().plusSeconds(3).truncatedTo(ChronoUnit.SECONDS)
            )
            .setCloseInternshipRequest(
                failureCode = (ApiEnum(FailureCode.F007)),
                status = (ApiEnum(InternshipStatus.FAILED))
            )
            .setRejectInternshipRequest((ApiEnum(RejectionCode.R008)))


        employeeActions
            .createProfile(employeePreconditions.createProfileRequest())
            .createInternship(
                employeeActions.createdProfileResponse().profileId,
                DarkstoreUserRole.DELIVERYMAN,
                employeePreconditions.createInternshipRequest()
            )
            .closeInternship(
                employeePreconditions.createInternshipRequest().plannedDate,
                employeeActions.createdProfileResponse().profileId,
                DarkstoreUserRole.DELIVERYMAN,
                employeePreconditions.closeInternshipRequest()
            )
            .rejectInternshipUnsuccessful(
                employeePreconditions.createInternshipRequest().plannedDate,
                employeeActions.createdProfileResponse().profileId,
                DarkstoreUserRole.DELIVERYMAN,
                employeePreconditions.rejectInternshipRequest()
            )
            .getInternship(employeeActions.createdProfileResponse().profileId)
            .setInternship(databaseController.getInternship(employeeActions.createdProfileResponse().profileId))

        employeeAssertion
            .checkStatusBadRequest(employeeActions.getRejectedInternship().statusCode)
            .checkInternshipFromDB(
                employeePreconditions.createInternshipRequest().darkstoreId,
                "deliveryman",
                "failed",
                employeeActions.getInternship()
            )
            .checkInternshipAPIResponse(
                employeeActions.getCreatedInternship(),
                employeePreconditions.createInternshipRequest(),
                ApiEnum(InternshipStatus.FAILED)
            )

    }

    @Test
    @DisplayName("Reject cancelled Internship is impossible")
    fun rejectCancelledInternshipsTest() {
        employeePreconditions
            .setCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.PERSONAL_BICYCLE))
            )
            .setCreateInternshipRequest(
                employeePreconditions.createProfileRequest().darkstoreId!!,
                plannedDate = Instant.now().plusSeconds(3).truncatedTo(ChronoUnit.SECONDS)
            )
            .setCancelInternshipRequest()
            .setRejectInternshipRequest((ApiEnum(RejectionCode.R008)))


        employeeActions
            .createProfile(employeePreconditions.createProfileRequest())
            .createInternship(
                employeeActions.createdProfileResponse().profileId,
                DarkstoreUserRole.DELIVERYMAN,
                employeePreconditions.createInternshipRequest()
            )
            .cancelInternship(
                employeePreconditions.createInternshipRequest().plannedDate,
                employeeActions.createdProfileResponse().profileId,
                DarkstoreUserRole.DELIVERYMAN,
                employeePreconditions.cancelInternshipRequest()
            )
            .rejectInternshipUnsuccessful(
                employeePreconditions.createInternshipRequest().plannedDate,
                employeeActions.createdProfileResponse().profileId,
                DarkstoreUserRole.DELIVERYMAN,
                employeePreconditions.rejectInternshipRequest()
            )
            .getInternship(employeeActions.createdProfileResponse().profileId)
            .setInternship(databaseController.getInternship(employeeActions.createdProfileResponse().profileId))

        employeeAssertion
            .checkStatusBadRequest(employeeActions.getRejectedInternship().statusCode)
            .checkInternshipFromDB(
                employeePreconditions.createInternshipRequest().darkstoreId,
                "deliveryman",
                "canceled",
                employeeActions.getInternship()
            )
            .checkInternshipAPIResponse(
                employeeActions.getCreatedInternship(),
                employeePreconditions.createInternshipRequest(),
                ApiEnum(InternshipStatus.CANCELED)
            )

    }

    @Test
    @DisplayName("Reject done Internship is impossible")
    fun rejectDoneInternshipsTest() {

        employeePreconditions
            .setCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.PERSONAL_BICYCLE))
            )
            .setCreateInternshipRequest(
                employeePreconditions.createProfileRequest().darkstoreId!!,
                plannedDate = Instant.now().plusSeconds(3).truncatedTo(ChronoUnit.SECONDS)
            )
            .setCloseInternshipRequest(
                status = (ApiEnum(InternshipStatus.DONE))
            )
            .setRejectInternshipRequest((ApiEnum(RejectionCode.R008)))


        employeeActions
            .createProfile(employeePreconditions.createProfileRequest())
            .createInternship(
                employeeActions.createdProfileResponse().profileId,
                DarkstoreUserRole.DELIVERYMAN,
                employeePreconditions.createInternshipRequest()
            )
            .closeInternship(
                employeePreconditions.createInternshipRequest().plannedDate,
                employeeActions.createdProfileResponse().profileId,
                DarkstoreUserRole.DELIVERYMAN,
                employeePreconditions.closeInternshipRequest()
            )
            .rejectInternshipUnsuccessful(
                employeePreconditions.createInternshipRequest().plannedDate,
                employeeActions.createdProfileResponse().profileId,
                DarkstoreUserRole.DELIVERYMAN,
                employeePreconditions.rejectInternshipRequest()
            )
            .getInternship(employeeActions.createdProfileResponse().profileId)
            .setInternship(databaseController.getInternship(employeeActions.createdProfileResponse().profileId))

        employeeAssertion
            .checkStatusBadRequest(employeeActions.getRejectedInternship().statusCode)
            .checkInternshipFromDB(
                employeePreconditions.createInternshipRequest().darkstoreId,
                "deliveryman",
                "done",
                employeeActions.getInternship()
            )
            .checkInternshipAPIResponse(
                employeeActions.getCreatedInternship(),
                employeePreconditions.createInternshipRequest(),
                ApiEnum(InternshipStatus.DONE)
            )

    }

    @Test
    @DisplayName("Cancel failed Internship is impossible")
    fun cancelFailedInternshipsTest() {

        employeePreconditions
            .setCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.PERSONAL_BICYCLE))
            )
            .setCreateInternshipRequest(
                employeePreconditions.createProfileRequest().darkstoreId!!,
                plannedDate = Instant.now().plusSeconds(3).truncatedTo(ChronoUnit.SECONDS)
            )
            .setCloseInternshipRequest(
                failureCode = (ApiEnum(FailureCode.F007)),
                status = (ApiEnum(InternshipStatus.FAILED))
            )
            .setCancelInternshipRequest()


        employeeActions
            .createProfile(employeePreconditions.createProfileRequest())
            .createInternship(
                employeeActions.createdProfileResponse().profileId,
                DarkstoreUserRole.DELIVERYMAN,
                employeePreconditions.createInternshipRequest()
            )
            .closeInternship(
                employeePreconditions.createInternshipRequest().plannedDate,
                employeeActions.createdProfileResponse().profileId,
                DarkstoreUserRole.DELIVERYMAN,
                employeePreconditions.closeInternshipRequest()
            )
            .cancelInternshipUnsuccessful(
                employeePreconditions.createInternshipRequest().plannedDate,
                employeeActions.createdProfileResponse().profileId,
                DarkstoreUserRole.DELIVERYMAN,
                employeePreconditions.cancelInternshipRequest()
            )
            .getInternship(employeeActions.createdProfileResponse().profileId)
            .setInternship(databaseController.getInternship(employeeActions.createdProfileResponse().profileId))

        employeeAssertion
            .checkStatusBadRequest(employeeActions.getCancelledInternship().statusCode)
            .checkInternshipFromDB(
                employeePreconditions.createInternshipRequest().darkstoreId,
                "deliveryman",
                "failed",
                employeeActions.getInternship()
            )
            .checkInternshipAPIResponse(
                employeeActions.getCreatedInternship(),
                employeePreconditions.createInternshipRequest(),
                ApiEnum(InternshipStatus.FAILED)
            )

    }

    @Test
    @DisplayName("Cancel rejected Internship is impossible")
    fun cancelRejectedInternshipsTest() {
        employeePreconditions
            .setCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.PERSONAL_BICYCLE))
            )
            .setCreateInternshipRequest(
                employeePreconditions.createProfileRequest().darkstoreId!!,
                plannedDate = Instant.now().plusSeconds(3).truncatedTo(ChronoUnit.SECONDS)
            )
            .setRejectInternshipRequest(
                rejectionCode = (ApiEnum(RejectionCode.R002)),

                )
            .setCancelInternshipRequest()


        employeeActions
            .createProfile(employeePreconditions.createProfileRequest())
            .createInternship(
                employeeActions.createdProfileResponse().profileId,
                DarkstoreUserRole.DELIVERYMAN,
                employeePreconditions.createInternshipRequest()
            )
            .rejectInternship(
                employeePreconditions.createInternshipRequest().plannedDate,
                employeeActions.createdProfileResponse().profileId,
                DarkstoreUserRole.DELIVERYMAN,
                employeePreconditions.rejectInternshipRequest()
            )
            .cancelInternshipUnsuccessful(
                employeePreconditions.createInternshipRequest().plannedDate,
                employeeActions.createdProfileResponse().profileId,
                DarkstoreUserRole.DELIVERYMAN,
                employeePreconditions.cancelInternshipRequest()
            )
            .getInternship(employeeActions.createdProfileResponse().profileId)
            .setInternship(databaseController.getInternship(employeeActions.createdProfileResponse().profileId))

        employeeAssertion
            .checkStatusBadRequest(employeeActions.getCancelledInternship().statusCode)
            .checkInternshipFromDB(
                employeePreconditions.createInternshipRequest().darkstoreId,
                "deliveryman",
                "rejected",
                employeeActions.getInternship()
            )
            .checkInternshipAPIResponse(
                employeeActions.getCreatedInternship(),
                employeePreconditions.createInternshipRequest(),
                ApiEnum(InternshipStatus.REJECTED)
            )

    }

    @Test
    @DisplayName("Cancel done Internship is impossible")
    fun cancelDoneInternshipsTest() {

        employeePreconditions
            .setCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.PERSONAL_BICYCLE))
            )
            .setCreateInternshipRequest(
                employeePreconditions.createProfileRequest().darkstoreId!!,
                plannedDate = Instant.now().plusSeconds(3).truncatedTo(ChronoUnit.SECONDS)
            )
            .setCloseInternshipRequest(
                status = (ApiEnum(InternshipStatus.DONE))
            )
            .setCancelInternshipRequest()


        employeeActions
            .createProfile(employeePreconditions.createProfileRequest())
            .createInternship(
                employeeActions.createdProfileResponse().profileId,
                DarkstoreUserRole.DELIVERYMAN,
                employeePreconditions.createInternshipRequest()
            )
            .closeInternship(
                employeePreconditions.createInternshipRequest().plannedDate,
                employeeActions.createdProfileResponse().profileId,
                DarkstoreUserRole.DELIVERYMAN,
                employeePreconditions.closeInternshipRequest()
            )
            .cancelInternshipUnsuccessful(
                employeePreconditions.createInternshipRequest().plannedDate,
                employeeActions.createdProfileResponse().profileId,
                DarkstoreUserRole.DELIVERYMAN,
                employeePreconditions.cancelInternshipRequest()
            )
            .getInternship(employeeActions.createdProfileResponse().profileId)
            .setInternship(databaseController.getInternship(employeeActions.createdProfileResponse().profileId))

        employeeAssertion
            .checkStatusBadRequest(employeeActions.getCancelledInternship().statusCode)
            .checkInternshipFromDB(
                employeePreconditions.createInternshipRequest().darkstoreId,
                "deliveryman",
                "done",
                employeeActions.getInternship()
            )
            .checkInternshipAPIResponse(
                employeeActions.getCreatedInternship(),
                employeePreconditions.createInternshipRequest(),
                ApiEnum(InternshipStatus.DONE)
            )

    }

    @Test
    @DisplayName("Done cancelled Internship is impossible")
    fun doneCancelledInternshipsTest() {
        employeePreconditions
            .setCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.PERSONAL_BICYCLE))
            )
            .setCreateInternshipRequest(
                employeePreconditions.createProfileRequest().darkstoreId!!,
                plannedDate = Instant.now().plusSeconds(3).truncatedTo(ChronoUnit.SECONDS)
            )
            .setCancelInternshipRequest()
            .setCloseInternshipRequest(
                status = (ApiEnum(InternshipStatus.DONE))
            )


        employeeActions
            .createProfile(employeePreconditions.createProfileRequest())
            .createInternship(
                employeeActions.createdProfileResponse().profileId,
                DarkstoreUserRole.DELIVERYMAN,
                employeePreconditions.createInternshipRequest()
            )
            .cancelInternship(
                employeePreconditions.createInternshipRequest().plannedDate,
                employeeActions.createdProfileResponse().profileId,
                DarkstoreUserRole.DELIVERYMAN,
                employeePreconditions.cancelInternshipRequest()
            )
            .closeInternshipUnsuccessful(
                employeePreconditions.createInternshipRequest().plannedDate,
                employeeActions.createdProfileResponse().profileId,
                DarkstoreUserRole.DELIVERYMAN,
                employeePreconditions.closeInternshipRequest()
            )

            .getInternship(employeeActions.createdProfileResponse().profileId)
            .setInternship(databaseController.getInternship(employeeActions.createdProfileResponse().profileId))

        employeeAssertion
            .checkStatusBadRequest(employeeActions.getClosedInternship().statusCode)
            .checkInternshipFromDB(
                employeePreconditions.createInternshipRequest().darkstoreId,
                "deliveryman",
                "canceled",
                employeeActions.getInternship()
            )
            .checkInternshipAPIResponse(
                employeeActions.getCreatedInternship(),
                employeePreconditions.createInternshipRequest(),
                ApiEnum(InternshipStatus.CANCELED)
            )

    }

    @Test
    @DisplayName("Done rejected Internship is impossible")
    fun doneRejectedInternshipsTest() {
        employeePreconditions
            .setCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.PERSONAL_BICYCLE))
            )
            .setCreateInternshipRequest(
                employeePreconditions.createProfileRequest().darkstoreId!!,
                plannedDate = Instant.now().plusSeconds(3).truncatedTo(ChronoUnit.SECONDS)
            )
            .setRejectInternshipRequest(
                rejectionCode = (ApiEnum(RejectionCode.R003))
            )
            .setCloseInternshipRequest(
                status = (ApiEnum(InternshipStatus.DONE))
            )


        employeeActions
            .createProfile(employeePreconditions.createProfileRequest())
            .createInternship(
                employeeActions.createdProfileResponse().profileId,
                DarkstoreUserRole.DELIVERYMAN,
                employeePreconditions.createInternshipRequest()
            )

            .rejectInternship(
                employeePreconditions.createInternshipRequest().plannedDate,
                employeeActions.createdProfileResponse().profileId,
                DarkstoreUserRole.DELIVERYMAN,
                employeePreconditions.rejectInternshipRequest()
            )
            .closeInternshipUnsuccessful(
                employeePreconditions.createInternshipRequest().plannedDate,
                employeeActions.createdProfileResponse().profileId,
                DarkstoreUserRole.DELIVERYMAN,
                employeePreconditions.closeInternshipRequest()
            )

            .getInternship(employeeActions.createdProfileResponse().profileId)
            .setInternship(databaseController.getInternship(employeeActions.createdProfileResponse().profileId))

        employeeAssertion
            .checkStatusBadRequest(employeeActions.getClosedInternship().statusCode)
            .checkInternshipFromDB(
                employeePreconditions.createInternshipRequest().darkstoreId,
                "deliveryman",
                "rejected",
                employeeActions.getInternship()
            )
            .checkInternshipAPIResponse(
                employeeActions.getCreatedInternship(),
                employeePreconditions.createInternshipRequest(),
                ApiEnum(InternshipStatus.REJECTED)
            )

    }

    @Test
    @DisplayName("Fail cancelled Internship is impossible")
    fun failCancelledInternshipsTest() {
        employeePreconditions
            .setCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.PERSONAL_BICYCLE))
            )
            .setCreateInternshipRequest(
                employeePreconditions.createProfileRequest().darkstoreId!!,
                plannedDate = Instant.now().plusSeconds(3).truncatedTo(ChronoUnit.SECONDS)
            )
            .setCancelInternshipRequest()
            .setCloseInternshipRequest(
                failureCode = (ApiEnum(FailureCode.F007)),
                status = (ApiEnum(InternshipStatus.FAILED))
            )



        employeeActions
            .createProfile(employeePreconditions.createProfileRequest())
            .createInternship(
                employeeActions.createdProfileResponse().profileId,
                DarkstoreUserRole.DELIVERYMAN,
                employeePreconditions.createInternshipRequest()
            )
            .cancelInternship(
                employeePreconditions.createInternshipRequest().plannedDate,
                employeeActions.createdProfileResponse().profileId,
                DarkstoreUserRole.DELIVERYMAN,
                employeePreconditions.cancelInternshipRequest()
            )
            .closeInternshipUnsuccessful(
                employeePreconditions.createInternshipRequest().plannedDate,
                employeeActions.createdProfileResponse().profileId,
                DarkstoreUserRole.DELIVERYMAN,
                employeePreconditions.closeInternshipRequest()
            )

            .getInternship(employeeActions.createdProfileResponse().profileId)
            .setInternship(databaseController.getInternship(employeeActions.createdProfileResponse().profileId))

        employeeAssertion
            .checkStatusBadRequest(employeeActions.getClosedInternship().statusCode)
            .checkInternshipFromDB(
                employeePreconditions.createInternshipRequest().darkstoreId,
                "deliveryman",
                "canceled",
                employeeActions.getInternship()
            )
            .checkInternshipAPIResponse(
                employeeActions.getCreatedInternship(),
                employeePreconditions.createInternshipRequest(),
                ApiEnum(InternshipStatus.CANCELED)
            )

    }

    @Test
    @DisplayName("Fail rejected Internship is impossible")
    fun failRejectedInternshipsTest() {
        employeePreconditions
            .setCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.PERSONAL_BICYCLE))
            )
            .setCreateInternshipRequest(
                employeePreconditions.createProfileRequest().darkstoreId!!,
                plannedDate = Instant.now().plusSeconds(3).truncatedTo(ChronoUnit.SECONDS)
            )
            .setRejectInternshipRequest((ApiEnum(RejectionCode.R008)))
            .setCloseInternshipRequest(
                failureCode = (ApiEnum(FailureCode.F007)),
                status = (ApiEnum(InternshipStatus.FAILED))
            )



        employeeActions
            .createProfile(employeePreconditions.createProfileRequest())
            .createInternship(
                employeeActions.createdProfileResponse().profileId,
                DarkstoreUserRole.DELIVERYMAN,
                employeePreconditions.createInternshipRequest()
            )
            .rejectInternship(
                employeePreconditions.createInternshipRequest().plannedDate,
                employeeActions.createdProfileResponse().profileId,
                DarkstoreUserRole.DELIVERYMAN,
                employeePreconditions.rejectInternshipRequest()
            )
            .closeInternshipUnsuccessful(
                employeePreconditions.createInternshipRequest().plannedDate,
                employeeActions.createdProfileResponse().profileId,
                DarkstoreUserRole.DELIVERYMAN,
                employeePreconditions.closeInternshipRequest()
            )

            .getInternship(employeeActions.createdProfileResponse().profileId)
            .setInternship(databaseController.getInternship(employeeActions.createdProfileResponse().profileId))

        employeeAssertion
            .checkStatusBadRequest(employeeActions.getClosedInternship().statusCode)
            .checkInternshipFromDB(
                employeePreconditions.createInternshipRequest().darkstoreId,
                "deliveryman",
                "rejected",
                employeeActions.getInternship()
            )
            .checkInternshipAPIResponse(
                employeeActions.getCreatedInternship(),
                employeePreconditions.createInternshipRequest(),
                ApiEnum(InternshipStatus.REJECTED)
            )

    }

    @Test
    @DisplayName("Fail internship for deactivated profile is impossible")
    fun failDeactivatedInternshipsTest() {
        employeePreconditions
            .setCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.PERSONAL_BICYCLE))
            )
            .setCreateInternshipRequest(
                employeePreconditions.createProfileRequest().darkstoreId!!,
                plannedDate = Instant.now().plusSeconds(3).truncatedTo(ChronoUnit.SECONDS)
            )

            .setCloseInternshipRequest(
                failureCode = (ApiEnum(FailureCode.F007)),
                status = (ApiEnum(InternshipStatus.FAILED))
            )

        employeeActions
            .createProfile(employeePreconditions.createProfileRequest())
            .createInternship(
                employeeActions.createdProfileResponse().profileId,
                DarkstoreUserRole.DELIVERYMAN,
                employeePreconditions.createInternshipRequest()
            )
            .deleteProfile(employeeActions.createdProfileResponse().profileId)
            .closeInternshipUnsuccessful(
                employeePreconditions.createInternshipRequest().plannedDate,
                employeeActions.createdProfileResponse().profileId,
                DarkstoreUserRole.DELIVERYMAN,
                employeePreconditions.closeInternshipRequest()
            )

            .getInternship(employeeActions.createdProfileResponse().profileId)

        employeeAssertion
            .checkStatusNotFound(employeeActions.getClosedInternship().statusCode)
            .checkInternshipNotInDatabase(databaseController.checkInternshipExists(employeeActions.createdProfileResponse().profileId))
            .checkInternshipAPIisEmptyResponse(employeeActions.getCreatedInternship())
    }

    @Test
    @DisplayName("Fail not-existed internship is impossible")
    fun failNotExistedInternshipTest() {
        employeePreconditions
            .setCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.PERSONAL_BICYCLE))
            )

            .setCloseInternshipRequest(
                failureCode = (ApiEnum(FailureCode.F007)),
                status = (ApiEnum(InternshipStatus.FAILED))
            )

        employeeActions
            .createProfile(employeePreconditions.createProfileRequest())
            .closeInternshipUnsuccessful(
                plannedDate = Instant.now().minusSeconds(60),
                employeeActions.createdProfileResponse().profileId,
                DarkstoreUserRole.DELIVERYMAN,
                employeePreconditions.closeInternshipRequest()
            )
        employeeAssertion
            .checkStatusBadRequest(employeeActions.getClosedInternship().statusCode)
    }

    @Test
    @DisplayName("Fail Internship for invalid role is impossible")
    fun failProfileInvalidRoleInternshipsTest() {
        employeePreconditions
            .setCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.PERSONAL_BICYCLE))
            )
            .setCreateInternshipRequest(
                employeePreconditions.createProfileRequest().darkstoreId!!,
                plannedDate = Instant.now().plusSeconds(3).truncatedTo(ChronoUnit.SECONDS)
            )

            .setCloseInternshipRequest(
                failureCode = (ApiEnum(FailureCode.F007)),
                status = (ApiEnum(InternshipStatus.FAILED))
            )

        employeeActions
            .createProfile(employeePreconditions.createProfileRequest())
            .createInternship(
                employeeActions.createdProfileResponse().profileId,
                DarkstoreUserRole.DELIVERYMAN,
                employeePreconditions.createInternshipRequest()
            )

            .closeInternshipUnsuccessful(
                employeePreconditions.createInternshipRequest().plannedDate,
                employeeActions.createdProfileResponse().profileId,
                DarkstoreUserRole.PICKER,
                employeePreconditions.closeInternshipRequest()
            )
        employeeAssertion
            .checkStatusNotFound(employeeActions.getClosedInternship().statusCode)

    }

}
