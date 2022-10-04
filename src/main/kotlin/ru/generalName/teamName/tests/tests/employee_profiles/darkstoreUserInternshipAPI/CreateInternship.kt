package ru.samokat.mysamokat.tests.tests.employee_profiles.darkstoreUserInternshipAPI


import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.samokat.employeeprofiles.api.common.domain.EmployeeVehicleType
import ru.samokat.employeeprofiles.api.common.domain.Vehicle
import ru.samokat.employeeprofiles.api.darkstoreusers.domain.DarkstoreUserRole
import ru.samokat.employeeprofiles.api.darkstoreusers.domain.DarkstoreUserState
import ru.samokat.employeeprofiles.api.darkstoreusers.internships.domain.InternshipStatus
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
class CreateInternship {
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
    @DisplayName("Create Internship: deliveryman")
    fun createInternshipDeliverymanTest() {
        employeePreconditions
            .setCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.PERSONAL_BICYCLE))
            )
            .setCreateInternshipRequest(
                employeePreconditions.createProfileRequest().darkstoreId!!,
                plannedDate = Instant.now().plusSeconds(60).truncatedTo(ChronoUnit.SECONDS)
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
            .getInternship(employeeActions.createdProfileResponse().profileId)
            .setProfile(databaseController.getProfile(employeeActions.createdProfileResponse().profileId))
            .setInternship(databaseController.getInternship(employeeActions.createdProfileResponse().profileId))
            .getDSInternship(
                employeePreconditions.createProfileRequest().darkstoreId!!,
                employeePreconditions.getInternshipDSRequest()
            )

        employeeAssertion
            .checkInternshipFromDB(
                employeePreconditions.createInternshipRequest().darkstoreId,
                "deliveryman",
                "planned",
                employeeActions.getInternship()
            )
            .checkInternshipLogFromDB(
                databaseController.getInternshipLogArray(employeeActions.createdProfileResponse().profileId),
                1,
                0,
                "created"
            )
            .checkInternshipAPIResponse(
                employeeActions.getCreatedInternship(),
                employeePreconditions.createInternshipRequest(),
                ApiEnum(InternshipStatus.PLANNED)
            )

            .checkInternshipAPIResponse(
                employeeActions.getCreatedDsInternship(),
                employeePreconditions.createInternshipRequest(),
                ApiEnum(InternshipStatus.PLANNED)
            )
            .checkDSUserInDatabase(
                databaseController.getDSUserProfile(
                    employeeActions.createdProfileResponse().profileId,
                    employeePreconditions.createProfileRequest().roles[0].value
                ),
                employeePreconditions.createProfileRequest().darkstoreId!!,
                4
            )
            .checkInternStatusForDSUser(
                employeeActions.getDarkstoreUserById(
                    employeeActions.createdProfileResponse().profileId,
                    employeePreconditions.createProfileRequest().darkstoreId!!,
                    DarkstoreUserRole.DELIVERYMAN,
                ), true
            )

    }

    @Test
    @DisplayName("Create Internship: picker")
    fun createInternshipPickerTest() {
        employeePreconditions
            .setCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.PICKER)),
                vehicle = null
            )
            .setCreateInternshipRequest(
                employeePreconditions.createProfileRequest().darkstoreId!!,
                plannedDate = Instant.now().plusSeconds(60).truncatedTo(ChronoUnit.SECONDS)
            )
            .setGetInternshipRequest(
                from = employeePreconditions.createInternshipRequest().plannedDate,
                to = employeePreconditions.createInternshipRequest().plannedDate.plusSeconds(120)
            )

        employeeActions
            .createProfile(employeePreconditions.createProfileRequest())
            .createInternship(
                employeeActions.createdProfileResponse().profileId,
                DarkstoreUserRole.PICKER,
                employeePreconditions.createInternshipRequest()
            )
            .getInternship(employeeActions.createdProfileResponse().profileId)
            .setProfile(databaseController.getProfile(employeeActions.createdProfileResponse().profileId))
            .setInternship(databaseController.getInternship(employeeActions.createdProfileResponse().profileId))
            .getDSInternship(
                employeePreconditions.createProfileRequest().darkstoreId!!,
                employeePreconditions.getInternshipDSRequest()
            )

        employeeAssertion
            .checkInternshipFromDB(
                employeePreconditions.createInternshipRequest().darkstoreId,
                "picker",
                "planned",
                employeeActions.getInternship()
            )
            .checkInternshipLogFromDB(
                databaseController.getInternshipLogArray(employeeActions.createdProfileResponse().profileId),
                1,
                0,
                "created"
            )
            .checkInternshipAPIResponse(
                employeeActions.getCreatedInternship(),
                employeePreconditions.createInternshipRequest(),
                ApiEnum(InternshipStatus.PLANNED)
            )

            .checkInternshipAPIResponse(
                employeeActions.getCreatedDsInternship(),
                employeePreconditions.createInternshipRequest(),
                ApiEnum(InternshipStatus.PLANNED)
            )
            .checkDSUserInDatabase(
                databaseController.getDSUserProfile(
                    employeeActions.createdProfileResponse().profileId,
                    employeePreconditions.createProfileRequest().roles[0].value
                ),
                employeePreconditions.createProfileRequest().darkstoreId!!,
                4
            )
            .checkInternStatusForDSUser(
                employeeActions.getDarkstoreUserById(
                    employeeActions.createdProfileResponse().profileId,
                    employeePreconditions.createProfileRequest().darkstoreId!!,
                    DarkstoreUserRole.PICKER,
                ), true
            )
    }

    @Test
    @DisplayName("Create several Internship")
    fun createSeveralInternshipDeliverymanTest() {
        employeePreconditions
            .setListOfCreatedProfileRequest(amount = 50)
            .setListInternshipOfCreatedProfileRequest(
                3,
                plannedDate = Instant.now().plusSeconds(60).truncatedTo(ChronoUnit.SECONDS),

                )
            .setGetInternshipRequest(
                from = employeePreconditions.listCreateInternshipRequest()[1].plannedDate.minusSeconds(60),
                to = employeePreconditions.listCreateInternshipRequest()[1].plannedDate.plusSeconds(120)
            )

        employeeActions
            .createSeveralProfiles(employeePreconditions.listCreateProfileRequest())
            .createSeveralInternships(
                employeeActions.listCreatedProfileResponses(),
                DarkstoreUserRole.DELIVERYMAN,
                employeePreconditions.listCreateInternshipRequest()
            )
            .getDSInternship(
                employeePreconditions.listCreateProfileRequest()[1].darkstoreId!!,
                employeePreconditions.getInternshipDSRequest()
            )

        employeeAssertion
            .checkSeveralInternshipAPIResponse(
                employeeActions.getCreatedDsInternship(),
                employeePreconditions.listCreateInternshipRequest(),
                ApiEnum(InternshipStatus.PLANNED)
            )
        employeeActions.deleteSeveralProfileByProfileId(employeeActions.listCreatedProfileResponses())
    }

    @Test
    @DisplayName("Create Internship: deliveryman-picker for deliveryman")
    fun createInternshipDeliverymanPickerTest() {
        employeePreconditions
            .setCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.PICKER), (ApiEnum(EmployeeRole.DELIVERYMAN)))
            )
            .setCreateInternshipRequest(
                employeePreconditions.createProfileRequest().darkstoreId!!,
                plannedDate = Instant.now().plusSeconds(60).truncatedTo(ChronoUnit.SECONDS)
            )
            .setGetInternshipRequest(
                from = employeePreconditions.createInternshipRequest().plannedDate.minusSeconds(60),
                to = employeePreconditions.createInternshipRequest().plannedDate
            )

        employeeActions
            .createProfile(employeePreconditions.createProfileRequest())
            .createInternship(
                employeeActions.createdProfileResponse().profileId,
                DarkstoreUserRole.DELIVERYMAN,
                employeePreconditions.createInternshipRequest()
            )
            .getInternship(employeeActions.createdProfileResponse().profileId)
            .setProfile(databaseController.getProfile(employeeActions.createdProfileResponse().profileId))
            .setInternship(databaseController.getInternship(employeeActions.createdProfileResponse().profileId))
            .getDSInternship(
                employeePreconditions.createProfileRequest().darkstoreId!!,
                employeePreconditions.getInternshipDSRequest()
            )

        employeeAssertion
            .checkInternshipFromDB(
                employeePreconditions.createInternshipRequest().darkstoreId,
                "deliveryman",
                "planned",
                employeeActions.getInternship()
            )

            .checkInternshipLogFromDB(
                databaseController.getInternshipLogArray(employeeActions.createdProfileResponse().profileId),
                1,
                0,
                "created"
            )
            .checkInternshipAPIResponse(
                employeeActions.getCreatedInternship(),
                employeePreconditions.createInternshipRequest(),
                ApiEnum(InternshipStatus.PLANNED)
            )

            .checkInternshipAPIResponse(
                employeeActions.getCreatedDsInternship(),
                employeePreconditions.createInternshipRequest(),
                ApiEnum(InternshipStatus.PLANNED)
            )
            .checkDSUserInDatabase(
                databaseController.getDSUserProfile(
                    employeeActions.createdProfileResponse().profileId,
                    employeePreconditions.createProfileRequest().roles[0].value
                ),
                employeePreconditions.createProfileRequest().darkstoreId!!,
                4
            )

            .checkInternStatusForDSUser(
                employeeActions.getDarkstoreUserById(
                    employeeActions.createdProfileResponse().profileId,
                    employeePreconditions.createProfileRequest().darkstoreId!!,
                    DarkstoreUserRole.DELIVERYMAN,
                ), true
            )

            .checkInternStatusForDSUser(
                employeeActions.getDarkstoreUserById(
                    employeeActions.createdProfileResponse().profileId,
                    employeePreconditions.createProfileRequest().darkstoreId!!,
                    DarkstoreUserRole.PICKER,
                ), false
            )


    }

    @Test
    @DisplayName("Create Internship: deliveryman-picker for picker")
    fun createInternshipPickerDeliverymanTest() {
        employeePreconditions
            .setCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.PICKER), (ApiEnum(EmployeeRole.DELIVERYMAN)))
            )
            .setCreateInternshipRequest(
                employeePreconditions.createProfileRequest().darkstoreId!!,
                plannedDate = Instant.now().plusSeconds(60).truncatedTo(ChronoUnit.SECONDS)
            )

        employeeActions
            .createProfile(employeePreconditions.createProfileRequest())
            .createInternship(
                employeeActions.createdProfileResponse().profileId,
                DarkstoreUserRole.PICKER,
                employeePreconditions.createInternshipRequest()
            )
            .getInternship(employeeActions.createdProfileResponse().profileId)
            .setProfile(databaseController.getProfile(employeeActions.createdProfileResponse().profileId))
            .setInternship(databaseController.getInternship(employeeActions.createdProfileResponse().profileId))

        employeeAssertion
            .checkInternshipFromDB(
                employeePreconditions.createInternshipRequest().darkstoreId,
                "picker",
                "planned",
                employeeActions.getInternship()
            )

            .checkInternshipLogFromDB(
                databaseController.getInternshipLogArray(employeeActions.createdProfileResponse().profileId),
                1,
                0,
                "created"
            )
            .checkInternshipAPIResponse(
                employeeActions.getCreatedInternship(),
                employeePreconditions.createInternshipRequest(),
                ApiEnum(InternshipStatus.PLANNED)
            )
            .checkDSUserInDatabase(
                databaseController.getDSUserProfile(
                    employeeActions.createdProfileResponse().profileId,
                    employeePreconditions.createProfileRequest().roles[0].value
                ),
                employeePreconditions.createProfileRequest().darkstoreId!!,
                4
            )

            .checkInternStatusForDSUser(
                employeeActions.getDarkstoreUserById(
                    employeeActions.createdProfileResponse().profileId,
                    employeePreconditions.createProfileRequest().darkstoreId!!,
                    DarkstoreUserRole.DELIVERYMAN,
                ), false
            )

            .checkInternStatusForDSUser(
                employeeActions.getDarkstoreUserById(
                    employeeActions.createdProfileResponse().profileId,
                    employeePreconditions.createProfileRequest().darkstoreId!!,
                    DarkstoreUserRole.PICKER,
                ), true
            )


    }

    @Test
    @DisplayName("Create Internship: not moscow timezone")
    fun createInternshipTimezoneTest() {
        employeePreconditions
            .setCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                darkstoreId = Constants.darkstoreIdWithNotMoscowTimezone,
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.PERSONAL_BICYCLE))

            )
            .setCreateInternshipRequest(
                employeePreconditions.createProfileRequest().darkstoreId!!,
                plannedDate = Instant.now().plusSeconds(60).truncatedTo(ChronoUnit.SECONDS)
            )

        employeeActions
            .createProfile(employeePreconditions.createProfileRequest())
            .createInternship(
                employeeActions.createdProfileResponse().profileId,
                DarkstoreUserRole.DELIVERYMAN,
                employeePreconditions.createInternshipRequest()
            )
            .getInternship(employeeActions.createdProfileResponse().profileId)
            .setProfile(databaseController.getProfile(employeeActions.createdProfileResponse().profileId))
            .setInternship(databaseController.getInternship(employeeActions.createdProfileResponse().profileId))

        employeeAssertion
            .checkInternshipFromDB(
                employeePreconditions.createInternshipRequest().darkstoreId,
                "deliveryman",
                "planned",
                employeeActions.getInternship()
            )
            .checkInternshipLogFromDB(
                databaseController.getInternshipLogArray(employeeActions.createdProfileResponse().profileId),
                1,
                0,
                "created"
            )
            .checkInternshipAPIResponse(
                employeeActions.getCreatedInternship(),
                employeePreconditions.createInternshipRequest(),
                ApiEnum(InternshipStatus.PLANNED)
            )
            .checkDSUserInDatabase(
                databaseController.getDSUserProfile(
                    employeeActions.createdProfileResponse().profileId,
                    employeePreconditions.createProfileRequest().roles[0].value
                ),
                employeePreconditions.createProfileRequest().darkstoreId!!,
                4
            )
            .checkInternStatusForDSUser(
                employeeActions.getDarkstoreUserById(
                    employeeActions.createdProfileResponse().profileId,
                    employeePreconditions.createProfileRequest().darkstoreId!!,
                    DarkstoreUserRole.DELIVERYMAN,
                ), true
            )


    }

    @Test
    @DisplayName("Create Internship: not profile's darkstore")
    fun createInternshipDarkstoreTest() {
        employeePreconditions
            .setCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.PERSONAL_BICYCLE))
            )
            .setCreateInternshipRequest(
                Constants.darkstoreIdWithNotMoscowTimezone,
                plannedDate = Instant.now().plusSeconds(60).truncatedTo(ChronoUnit.SECONDS)
            )

        employeeActions
            .createProfile(employeePreconditions.createProfileRequest())
            .createInternship(
                employeeActions.createdProfileResponse().profileId,
                DarkstoreUserRole.DELIVERYMAN,
                employeePreconditions.createInternshipRequest()
            )
            .getInternship(employeeActions.createdProfileResponse().profileId)
            .setProfile(databaseController.getProfile(employeeActions.createdProfileResponse().profileId))
            .setInternship(databaseController.getInternship(employeeActions.createdProfileResponse().profileId))

        employeeAssertion
            .checkInternshipFromDB(
                employeePreconditions.createInternshipRequest().darkstoreId,
                "deliveryman",
                "planned",
                employeeActions.getInternship()
            )
            .checkInternshipLogFromDB(
                databaseController.getInternshipLogArray(employeeActions.createdProfileResponse().profileId),
                1,
                0,
                "created"
            )
            .checkInternshipAPIResponse(
                employeeActions.getCreatedInternship(),
                employeePreconditions.createInternshipRequest(),
                ApiEnum(InternshipStatus.PLANNED)
            )
            .checkDSUserInDatabase(
                databaseController.getDSUserProfile(
                    employeeActions.createdProfileResponse().profileId,
                    employeePreconditions.createProfileRequest().roles[0].value
                ),
                employeePreconditions.createProfileRequest().darkstoreId!!,
                4
            )
            .checkInternStatusForDSUser(
                employeeActions.getDarkstoreUserById(
                    employeeActions.createdProfileResponse().profileId,
                    employeePreconditions.createProfileRequest().darkstoreId!!,
                    DarkstoreUserRole.DELIVERYMAN,
                ), true
            )


    }

    @Test
    @DisplayName("Create Internship with past date (date = today) is impossible")
    fun createInternshipTodayTest() {
        employeePreconditions
            .setCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.PERSONAL_BICYCLE))
            )
            .setCreateInternshipRequest(
                employeePreconditions.createProfileRequest().darkstoreId!!,
                plannedDate = Instant.now().minusSeconds(60).truncatedTo(ChronoUnit.SECONDS)
            )

        employeeActions
            .createProfile(employeePreconditions.createProfileRequest())
            .createInternshipUnsuccessful(
                employeeActions.createdProfileResponse().profileId,
                DarkstoreUserRole.DELIVERYMAN,
                employeePreconditions.createInternshipRequest()
            )

        employeeAssertion
            .checkStatusBadRequest(employeeActions.getCreateInternshipResponse().statusCode)
            .checkInternshipNotInDatabase(databaseController.checkInternshipExists(employeeActions.createdProfileResponse().profileId))
            .checkInternStatusForDSUser(
                employeeActions.getDarkstoreUserById(
                    employeeActions.createdProfileResponse().profileId,
                    employeePreconditions.createProfileRequest().darkstoreId!!,
                    DarkstoreUserRole.DELIVERYMAN,
                ), false
            )


    }

    @Test
    @DisplayName("Create Internship with invalid role of intern is impossible")
    fun createInternshipInvalidRoleInternTest() {
        employeePreconditions
            .setCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DARKSTORE_ADMIN)),
                vehicle = null,
                staffPartnerId = null
            )
            .setCreateInternshipRequest(
                employeePreconditions.createProfileRequest().darkstoreId!!,
                plannedDate = Instant.now().plusSeconds(60).truncatedTo(ChronoUnit.SECONDS)
            )

        employeeActions
            .createProfile(employeePreconditions.createProfileRequest())
            .createInternshipUnsuccessful(
                employeeActions.createdProfileResponse().profileId,
                DarkstoreUserRole.DELIVERYMAN,
                employeePreconditions.createInternshipRequest()
            )

        employeeAssertion
            .checkStatusNotFound(employeeActions.getCreateInternshipResponse().statusCode)
            .checkInternshipNotInDatabase(databaseController.checkInternshipExists(employeeActions.createdProfileResponse().profileId))

    }

    @Test
    @DisplayName("Create Internship with past date (yesterday) is impossible")
    fun createInternshipPastDateTest() {
        employeePreconditions
            .setCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.PERSONAL_BICYCLE))
            )
            .setCreateInternshipRequest(
                employeePreconditions.createProfileRequest().darkstoreId!!,
                plannedDate = Instant.now().minusSeconds(86400).truncatedTo(ChronoUnit.SECONDS)
            )

        employeeActions
            .createProfile(employeePreconditions.createProfileRequest())
            .createInternshipUnsuccessful(
                employeeActions.createdProfileResponse().profileId,
                DarkstoreUserRole.DELIVERYMAN,
                employeePreconditions.createInternshipRequest()
            )

        employeeAssertion
            .checkStatusBadRequest(employeeActions.getCreateInternshipResponse().statusCode)
            .checkInternshipNotInDatabase(databaseController.checkInternshipExists(employeeActions.createdProfileResponse().profileId))
            .checkInternStatusForDSUser(
                employeeActions.getDarkstoreUserById(
                    employeeActions.createdProfileResponse().profileId,
                    employeePreconditions.createProfileRequest().darkstoreId!!,
                    DarkstoreUserRole.DELIVERYMAN,
                ), false
            )
    }

    @Test
    @DisplayName("Create Internship for profile with already created internship is impossible")
    fun createInternshipCreatedInternshipTest() {
        employeePreconditions
            .setCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.PERSONAL_BICYCLE))
            )
            .setCreateInternshipRequest(
                employeePreconditions.createProfileRequest().darkstoreId!!,
                plannedDate = Instant.now().plusSeconds(60).truncatedTo(ChronoUnit.SECONDS)
            )
            .setCreateInternshipTwiceRequest(
                employeePreconditions.createProfileRequest().darkstoreId!!,
                plannedDate = Instant.now().plusSeconds(120).truncatedTo(ChronoUnit.SECONDS)

            )

        employeeActions
            .createProfile(employeePreconditions.createProfileRequest())
            .createInternship(
                employeeActions.createdProfileResponse().profileId,
                DarkstoreUserRole.DELIVERYMAN,
                employeePreconditions.createInternshipRequest()
            )
            .createInternshipUnsuccessful(
                employeeActions.createdProfileResponse().profileId,
                DarkstoreUserRole.DELIVERYMAN,
                employeePreconditions.createInternshipTwiceRequest()
            )

        employeeAssertion
            .checkStatusCodeConflict(employeeActions.getCreateInternshipResponse().statusCode)
    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Create Internship for profile with already created internship for 1 role is impossible")
    fun createInternshipCreatedInternshipOneRoleTest() {
        employeePreconditions
            .setCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN), ApiEnum(EmployeeRole.PICKER)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.PERSONAL_BICYCLE))
            )
            .setCreateInternshipRequest(
                employeePreconditions.createProfileRequest().darkstoreId!!,
                plannedDate = Instant.now().plusSeconds(60).truncatedTo(ChronoUnit.SECONDS)
            )
            .setCreateInternshipTwiceRequest(
                employeePreconditions.createProfileRequest().darkstoreId!!,
                plannedDate = Instant.now().plusSeconds(120).truncatedTo(ChronoUnit.SECONDS)

            )

        employeeActions
            .createProfile(employeePreconditions.createProfileRequest())
            .createInternship(
                employeeActions.createdProfileResponse().profileId,
                DarkstoreUserRole.DELIVERYMAN,
                employeePreconditions.createInternshipRequest()
            )
            .createInternshipUnsuccessful(
                employeeActions.createdProfileResponse().profileId,
                DarkstoreUserRole.PICKER,
                employeePreconditions.createInternshipTwiceRequest()
            )

        employeeAssertion
            .checkStatusBadRequest(employeeActions.getCreateInternshipResponse().statusCode)
            .checkInternStatusForDSUser(
                employeeActions.getDarkstoreUserById(
                    employeeActions.createdProfileResponse().profileId,
                    employeePreconditions.createProfileRequest().darkstoreId!!,
                    DarkstoreUserRole.DELIVERYMAN,
                ), true
            )
            .checkInternStatusForDSUser(
                employeeActions.getDarkstoreUserById(
                    employeeActions.createdProfileResponse().profileId,
                    employeePreconditions.createProfileRequest().darkstoreId!!,
                    DarkstoreUserRole.PICKER,
                ), false
            )
    }

    @Test
    @DisplayName("Create Internship for profile with status is working is impossible")
    fun createInternshipWorkingStatusTest() {
        employeePreconditions
            .setCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.PERSONAL_BICYCLE))
            )
            .setUpdateDSUserProfile(state = ApiEnum(DarkstoreUserState.WORKING))
            .setCreateInternshipRequest(
                employeePreconditions.createProfileRequest().darkstoreId!!,
                plannedDate = Instant.now().plusSeconds(60).truncatedTo(ChronoUnit.SECONDS)
            )

        employeeActions
            .createProfile(employeePreconditions.createProfileRequest())
            .updateDSUserStatus(
                employeeActions.createdProfileResponse().profileId,
                employeePreconditions.createProfileRequest().darkstoreId!!,
                DarkstoreUserRole.DELIVERYMAN,
                employeePreconditions.updateDSUserStatusRequest()
            )
            .createInternshipUnsuccessful(
                employeeActions.createdProfileResponse().profileId,
                DarkstoreUserRole.DELIVERYMAN,
                employeePreconditions.createInternshipRequest()
            )

        employeeAssertion
            .checkStatusBadRequest(employeeActions.getCreateInternshipResponse().statusCode)
            .checkInternshipNotInDatabase(databaseController.checkInternshipExists(employeeActions.createdProfileResponse().profileId))
            .checkInternStatusForDSUser(
                employeeActions.getDarkstoreUserById(
                    employeeActions.createdProfileResponse().profileId,
                    employeePreconditions.createProfileRequest().darkstoreId!!,
                    DarkstoreUserRole.DELIVERYMAN,
                ), false
            )


    }

    @Test
    @DisplayName("Create Internship for profile with status is not_working is impossible")
    fun createInternshipNotWorkingStatusTest() {

        employeePreconditions
            .setCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.PERSONAL_BICYCLE))
            )
            .setUpdateDSUserProfile(state = ApiEnum(DarkstoreUserState.NOT_WORKING))
            .setCreateInternshipRequest(
                employeePreconditions.createProfileRequest().darkstoreId!!,
                plannedDate = Instant.now().plusSeconds(60).truncatedTo(ChronoUnit.SECONDS)
            )

        employeeActions
            .createProfile(employeePreconditions.createProfileRequest())
            .updateDSUserStatus(
                employeeActions.createdProfileResponse().profileId,
                employeePreconditions.createProfileRequest().darkstoreId!!,
                DarkstoreUserRole.DELIVERYMAN,
                employeePreconditions.updateDSUserStatusRequest()
            )
            .createInternshipUnsuccessful(
                employeeActions.createdProfileResponse().profileId,
                DarkstoreUserRole.DELIVERYMAN,
                employeePreconditions.createInternshipRequest()
            )

        employeeAssertion
            .checkStatusBadRequest(employeeActions.getCreateInternshipResponse().statusCode)
            .checkInternshipNotInDatabase(databaseController.checkInternshipExists(employeeActions.createdProfileResponse().profileId))
            .checkInternStatusForDSUser(
                employeeActions.getDarkstoreUserById(
                    employeeActions.createdProfileResponse().profileId,
                    employeePreconditions.createProfileRequest().darkstoreId!!,
                    DarkstoreUserRole.DELIVERYMAN,
                ), false
            )


    }

    @Test
    @DisplayName("Create Internship for profile with status is not new for 1 role is impossible")
    fun createInternshipNotNewOneRoleStatusTest() {
        employeePreconditions
            .setCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN), ApiEnum(EmployeeRole.PICKER)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.PERSONAL_BICYCLE))
            )
            .setUpdateDSUserProfile(state = ApiEnum(DarkstoreUserState.NOT_WORKING))
            .setCreateInternshipRequest(
                employeePreconditions.createProfileRequest().darkstoreId!!,
                plannedDate = Instant.now().plusSeconds(60).truncatedTo(ChronoUnit.SECONDS)
            )

        employeeActions
            .createProfile(employeePreconditions.createProfileRequest())
            .updateDSUserStatus(
                employeeActions.createdProfileResponse().profileId,
                employeePreconditions.createProfileRequest().darkstoreId!!,
                DarkstoreUserRole.PICKER,
                employeePreconditions.updateDSUserStatusRequest()
            )
            .createInternshipUnsuccessful(
                employeeActions.createdProfileResponse().profileId,
                DarkstoreUserRole.DELIVERYMAN,
                employeePreconditions.createInternshipRequest()
            )

        employeeAssertion
            .checkStatusBadRequest(employeeActions.getCreateInternshipResponse().statusCode)
            .checkInternshipNotInDatabase(databaseController.checkInternshipExists(employeeActions.createdProfileResponse().profileId))
            .checkInternStatusForDSUser(
                employeeActions.getDarkstoreUserById(
                    employeeActions.createdProfileResponse().profileId,
                    employeePreconditions.createProfileRequest().darkstoreId!!,
                    DarkstoreUserRole.DELIVERYMAN,
                ), false
            )


    }

    @Test
    @DisplayName("Create Internship for disabled profile is impossible")
    fun createInternshipDisabledRoleTest() {

        employeePreconditions
            .setCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.PERSONAL_BICYCLE))
            )
            .setCreateInternshipRequest(
                employeePreconditions.createProfileRequest().darkstoreId!!,
                plannedDate = Instant.now().plusSeconds(60).truncatedTo(ChronoUnit.SECONDS)
            )
            .setGetInternshipRequest(
                from = employeePreconditions.createInternshipRequest().plannedDate.minusSeconds(6000),
                to = employeePreconditions.createInternshipRequest().plannedDate.plusSeconds(6000)
            )

        employeeActions
            .createProfile(employeePreconditions.createProfileRequest())
            .deleteProfile(employeeActions.createdProfileResponse().profileId)
            .createInternshipUnsuccessful(
                employeeActions.createdProfileResponse().profileId,
                DarkstoreUserRole.DELIVERYMAN,
                employeePreconditions.createInternshipRequest()
            )
            .getDSInternship(
                employeePreconditions.createProfileRequest().darkstoreId!!,
                employeePreconditions.getInternshipDSRequest()
            )

        employeeAssertion
            .checkStatusNotFound(employeeActions.getCreateInternshipResponse().statusCode)
            .checkInternshipAPIisEmptyResponse(employeeActions.getCreatedDsInternship())

    }
}


