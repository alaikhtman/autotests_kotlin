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
import java.util.*

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Tag("empro")
class UpdateInternship {
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
    @DisplayName("Update Internship's date: deliveryman")
    fun updateInternshipsDateDeliverymanTest() {
        employeePreconditions
            .setCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.PERSONAL_BICYCLE))
            )
            .setCreateInternshipRequest(
                employeePreconditions.createProfileRequest().darkstoreId!!,
                plannedDate = Instant.now().plusSeconds(60).truncatedTo(ChronoUnit.SECONDS)
            )
            .setUpdateInternshipRequest(
                employeePreconditions.createProfileRequest().darkstoreId!!,
                plannedDate = Instant.now().plusSeconds(1200).truncatedTo(ChronoUnit.SECONDS)

            )
            .setGetInternshipRequest(
                from = employeePreconditions.updateInternshipRequest().plannedDate.minusSeconds(60),
                to = employeePreconditions.updateInternshipRequest().plannedDate.plusSeconds(120)
            )

        employeeActions
            .createProfile(employeePreconditions.createProfileRequest())
            .createInternship(
                employeeActions.createdProfileResponse().profileId,
                DarkstoreUserRole.DELIVERYMAN,
                employeePreconditions.createInternshipRequest()
            )
            .updateInternship(
                employeeActions.createdProfileResponse().profileId,
                DarkstoreUserRole.DELIVERYMAN,
                employeePreconditions.updateInternshipRequest()
            )
            .getInternship(employeeActions.createdProfileResponse().profileId)
            .setInternship(databaseController.getInternship(employeeActions.createdProfileResponse().profileId))
            .getDSInternship(
                employeePreconditions.updateInternshipRequest().darkstoreId,
                employeePreconditions.getInternshipDSRequest()
            )
        employeeAssertion
            .checkInternshipFromDB(
                employeePreconditions.updateInternshipRequest().darkstoreId,
                "deliveryman",
                "planned",
                employeeActions.getInternship()
            )
            .checkInternshipLogFromDB(
                databaseController.getInternshipLogArray(employeeActions.createdProfileResponse().profileId),
                2,
                1,
                "updated"
            )

            .checkUpdatedInternshipAPIResponse(
                employeeActions.getCreatedInternship(),
                employeePreconditions.updateInternshipRequest()

            )
            .checkUpdatedInternshipAPIResponse(
                employeeActions.getCreatedDsInternship(),
                employeePreconditions.updateInternshipRequest()

            )
    }

    @Test
    @DisplayName("Update Internship's date: picker")
    fun updateInternshipsDatePickerTest() {
        employeePreconditions
            .setCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.PICKER)),
                vehicle = null
            )
            .setCreateInternshipRequest(
                employeePreconditions.createProfileRequest().darkstoreId!!,
                plannedDate = Instant.now().plusSeconds(60).truncatedTo(ChronoUnit.SECONDS)
            )
            .setUpdateInternshipRequest(
                employeePreconditions.createProfileRequest().darkstoreId!!,
                plannedDate = Instant.now().plusSeconds(1200).truncatedTo(ChronoUnit.SECONDS)

            )
            .setGetInternshipRequest(
                from = employeePreconditions.updateInternshipRequest().plannedDate.minusSeconds(60),
                to = employeePreconditions.updateInternshipRequest().plannedDate.plusSeconds(120)
            )

        employeeActions
            .createProfile(employeePreconditions.createProfileRequest())
            .createInternship(
                employeeActions.createdProfileResponse().profileId,
                DarkstoreUserRole.PICKER,
                employeePreconditions.createInternshipRequest()
            )
            .updateInternship(
                employeeActions.createdProfileResponse().profileId,
                DarkstoreUserRole.PICKER,
                employeePreconditions.updateInternshipRequest()
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
                "planned",
                employeeActions.getInternship()
            )
            .checkInternshipLogFromDB(
                databaseController.getInternshipLogArray(employeeActions.createdProfileResponse().profileId),
                2,
                1,
                "updated"
            )

            .checkUpdatedInternshipAPIResponse(
                employeeActions.getCreatedInternship(),
                employeePreconditions.updateInternshipRequest()

                )
            .checkUpdatedInternshipAPIResponse(
                employeeActions.getCreatedDsInternship(),
                employeePreconditions.updateInternshipRequest()

                )
    }

    @Test
    @DisplayName("Update Internship's date: deliveryman twice")
    fun updateInternshipsTwiceTest() {
        employeePreconditions
            .setCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.PERSONAL_BICYCLE))
            )
            .setCreateInternshipRequest(
                employeePreconditions.createProfileRequest().darkstoreId!!,
                plannedDate = Instant.now().plusSeconds(60).truncatedTo(ChronoUnit.SECONDS)
            )
            .setListUpdateInternshipRequest(
                2,
                employeePreconditions.createProfileRequest(),
                darkstoreId = employeePreconditions.createProfileRequest().darkstoreId!!
            )

            .setGetInternshipRequest(
                from = employeePreconditions.listUpdateInternshipRequest()[0].plannedDate.minusSeconds(60),
                to = employeePreconditions.listUpdateInternshipRequest()[1].plannedDate.plusSeconds(3000)
            )

        employeeActions
            .createProfile(employeePreconditions.createProfileRequest())
            .createInternship(
                employeeActions.createdProfileResponse().profileId,
                DarkstoreUserRole.DELIVERYMAN,
                employeePreconditions.createInternshipRequest()
            )
            .updateInternshipSeveralTimes(
                employeeActions.createdProfileResponse().profileId,
                DarkstoreUserRole.DELIVERYMAN,
                employeePreconditions.listUpdateInternshipRequest()
            )
            .getInternship(employeeActions.createdProfileResponse().profileId)
            .setInternship(databaseController.getInternship(employeeActions.createdProfileResponse().profileId))
            .getDSInternship(
                employeePreconditions.listUpdateInternshipRequest().last().darkstoreId,
                employeePreconditions.getInternshipDSRequest()
            )
        employeeAssertion
            .checkInternshipFromDB(
                employeePreconditions.listUpdateInternshipRequest().last().darkstoreId,
                "deliveryman",
                "planned",
                employeeActions.getInternship()
            )
            .checkInternshipLogFromDB(
                databaseController.getInternshipLogArray(employeeActions.createdProfileResponse().profileId),
                3,
                2,
                "updated"
            )

            .checkUpdatedInternshipAPIResponse(
                employeeActions.getCreatedInternship(),
                employeePreconditions.listUpdateInternshipRequest().last()
            )
            .checkUpdatedInternshipAPIResponse(
                employeeActions.getCreatedDsInternship(),
                employeePreconditions.listUpdateInternshipRequest().last()

            )
    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Update Internship's darkstore: deliveryman")
    fun updateInternshipsDarkstoreDeliverymanTest() {
        employeePreconditions
            .setCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.PERSONAL_BICYCLE))
            )
            .setCreateInternshipRequest(
                employeePreconditions.createProfileRequest().darkstoreId!!,
                plannedDate = Instant.now().plusSeconds(172800).truncatedTo(ChronoUnit.SECONDS)
            )
            .setUpdateInternshipRequest(
                Constants.updatedDarkstoreId,
                employeePreconditions.createInternshipRequest().plannedDate

            )
            .setGetInternshipRequest(
                from = employeePreconditions.updateInternshipRequest().plannedDate.minusSeconds(60),
                to = employeePreconditions.updateInternshipRequest().plannedDate.plusSeconds(120)
            )

        employeeActions
            .createProfile(employeePreconditions.createProfileRequest())
            .createInternship(
                employeeActions.createdProfileResponse().profileId,
                DarkstoreUserRole.DELIVERYMAN,
                employeePreconditions.createInternshipRequest()
            )
            .updateInternship(
                employeeActions.createdProfileResponse().profileId,
                DarkstoreUserRole.DELIVERYMAN,
                employeePreconditions.updateInternshipRequest()
            )
            .getInternship(employeeActions.createdProfileResponse().profileId)
            .setInternship(databaseController.getInternship(employeeActions.createdProfileResponse().profileId))
            .getDSInternship(
                employeePreconditions.updateInternshipRequest().darkstoreId,
                employeePreconditions.getInternshipDSRequest()
            )
        employeeAssertion
            .checkInternshipFromDB(
                employeePreconditions.updateInternshipRequest().darkstoreId,
                "deliveryman",
                "planned",
                employeeActions.getInternship()
            )
            .checkInternshipLogFromDB(
                databaseController.getInternshipLogArray(employeeActions.createdProfileResponse().profileId),
                2,
                1,
                "updated"
            )

            .checkUpdatedInternshipAPIResponse(
                employeeActions.getCreatedInternship(),
                employeePreconditions.updateInternshipRequest()

            )
            .checkUpdatedInternshipAPIResponse(
                employeeActions.getCreatedDsInternship(),
                employeePreconditions.updateInternshipRequest()

            )
    }

    @Test
    @DisplayName("Update Internship's darkstore: picker")
    fun updateInternshipsDarkstorePickerTest() {
        employeePreconditions
            .setCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.PICKER)),
                vehicle = null
            )
            .setCreateInternshipRequest(
                employeePreconditions.createProfileRequest().darkstoreId!!,
                plannedDate = Instant.now().plusSeconds(172800).truncatedTo(ChronoUnit.SECONDS)
            )
            .setUpdateInternshipRequest(
                Constants.updatedDarkstoreId,
                employeePreconditions.createInternshipRequest().plannedDate

            )
            .setGetInternshipRequest(
                from = employeePreconditions.updateInternshipRequest().plannedDate.minusSeconds(60),
                to = employeePreconditions.updateInternshipRequest().plannedDate.plusSeconds(120)
            )

        employeeActions
            .createProfile(employeePreconditions.createProfileRequest())
            .createInternship(
                employeeActions.createdProfileResponse().profileId,
                DarkstoreUserRole.PICKER,
                employeePreconditions.createInternshipRequest()
            )
            .updateInternship(
                employeeActions.createdProfileResponse().profileId,
                DarkstoreUserRole.PICKER,
                employeePreconditions.updateInternshipRequest()
            )
            .getInternship(employeeActions.createdProfileResponse().profileId)
            .setInternship(databaseController.getInternship(employeeActions.createdProfileResponse().profileId))
            .getDSInternship(
                employeePreconditions.updateInternshipRequest().darkstoreId,
                employeePreconditions.getInternshipDSRequest()
            )
        employeeAssertion
            .checkInternshipFromDB(
                employeePreconditions.updateInternshipRequest().darkstoreId,
                "picker",
                "planned",
                employeeActions.getInternship()
            )
            .checkInternshipLogFromDB(
                databaseController.getInternshipLogArray(employeeActions.createdProfileResponse().profileId),
                2,
                1,
                "updated"
            )

            .checkUpdatedInternshipAPIResponse(
                employeeActions.getCreatedInternship(),
                employeePreconditions.updateInternshipRequest()

            )
            .checkUpdatedInternshipAPIResponse(
                employeeActions.getCreatedDsInternship(),
                employeePreconditions.updateInternshipRequest()

            )
    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Update Internship's date for cancelled internship")
    fun updateCancelledInternshipWithDateTest() {
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
            .setUpdateInternshipRequest(
                employeePreconditions.createProfileRequest().darkstoreId!!,
                plannedDate = Instant.now().plusSeconds(1200).truncatedTo(ChronoUnit.SECONDS),
                version = 2

            )
            .setGetInternshipRequest(
                from = employeePreconditions.updateInternshipRequest().plannedDate.minusSeconds(60),
                to = employeePreconditions.updateInternshipRequest().plannedDate.plusSeconds(120)
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
            .updateInternship(
                employeeActions.createdProfileResponse().profileId,
                DarkstoreUserRole.PICKER,
                employeePreconditions.updateInternshipRequest()
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
                "planned",
                employeeActions.getInternship()
            )
            .checkInternshipLogFromDB(
                databaseController.getInternshipLogArray(employeeActions.createdProfileResponse().profileId),
                3,
                2,
                "updated"
            )

            .checkUpdatedInternshipAPIResponse(
                employeeActions.getCreatedInternship(),
                employeePreconditions.updateInternshipRequest()

            )
            .checkUpdatedInternshipAPIResponse(
                employeeActions.getCreatedDsInternship(),
                employeePreconditions.updateInternshipRequest()

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
                4
            )
    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Update Internship's date for rejected internship")
    fun updateRejectedInternshipWithDateTest() {
        employeePreconditions
            .setCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.PERSONAL_BICYCLE))
            )
            .setCreateInternshipRequest(
                employeePreconditions.createProfileRequest().darkstoreId!!,
                plannedDate = Instant.now().plusSeconds(3).truncatedTo(ChronoUnit.SECONDS)
            )
            .setRejectInternshipRequest((ApiEnum(RejectionCode.R001)))
            .setUpdateInternshipRequest(
                employeePreconditions.createProfileRequest().darkstoreId!!,
                plannedDate = Instant.now().plusSeconds(1200).truncatedTo(ChronoUnit.SECONDS),
                version = 2

            )
            .setGetInternshipRequest(
                from = employeePreconditions.updateInternshipRequest().plannedDate.minusSeconds(60),
                to = employeePreconditions.updateInternshipRequest().plannedDate.plusSeconds(120)
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
            .updateInternship(
                employeeActions.createdProfileResponse().profileId,
                DarkstoreUserRole.DELIVERYMAN,
                employeePreconditions.updateInternshipRequest()
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
                "planned",
                employeeActions.getInternship()
            )
            .checkInternshipLogFromDB(
                databaseController.getInternshipLogArray(employeeActions.createdProfileResponse().profileId),
                3,
                2,
                "updated"
            )

            .checkUpdatedInternshipAPIResponse(
                employeeActions.getCreatedInternship(),
                employeePreconditions.updateInternshipRequest()

            )
            .checkUpdatedInternshipAPIResponse(
                employeeActions.getCreatedDsInternship(),
                employeePreconditions.updateInternshipRequest()

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
                4
            )
    }

    @Test
    @DisplayName("Update Internship's date and darkstore for cancelled internship")
    fun updateCancelledInternshipWithDarkstoreTest() {
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
            .setUpdateInternshipRequest(
                Constants.updatedDarkstoreId,
                plannedDate = Instant.now().plusSeconds(1200).truncatedTo(ChronoUnit.SECONDS),
                version = 2

            )
            .setGetInternshipRequest(
                from = employeePreconditions.updateInternshipRequest().plannedDate.minusSeconds(60),
                to = employeePreconditions.updateInternshipRequest().plannedDate.plusSeconds(120)
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
            .updateInternship(
                employeeActions.createdProfileResponse().profileId,
                DarkstoreUserRole.PICKER,
                employeePreconditions.updateInternshipRequest()
            )
            .getInternship(employeeActions.createdProfileResponse().profileId)
            .setInternship(databaseController.getInternship(employeeActions.createdProfileResponse().profileId))
            .getDSInternship(
                employeePreconditions.updateInternshipRequest().darkstoreId,
                employeePreconditions.getInternshipDSRequest()
            )
        employeeAssertion
            .checkInternshipFromDB(
                employeePreconditions.updateInternshipRequest().darkstoreId,
                "picker",
                "planned",
                employeeActions.getInternship()
            )
            .checkInternshipLogFromDB(
                databaseController.getInternshipLogArray(employeeActions.createdProfileResponse().profileId),
                3,
                2,
                "updated"
            )

            .checkUpdatedInternshipAPIResponse(
                employeeActions.getCreatedInternship(),
                employeePreconditions.updateInternshipRequest()

            )
            .checkUpdatedInternshipAPIResponse(
                employeeActions.getCreatedDsInternship(),
                employeePreconditions.updateInternshipRequest()

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
                4
            )
    }

    @Test
    @DisplayName("Update Internship's date and darkstore for rejected internship")
    fun updateRejectedInternshipWithDarkstoreTest() {
        employeePreconditions
            .setCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.PERSONAL_BICYCLE))
            )
            .setCreateInternshipRequest(
                employeePreconditions.createProfileRequest().darkstoreId!!,
                plannedDate = Instant.now().plusSeconds(3).truncatedTo(ChronoUnit.SECONDS)
            )
            .setRejectInternshipRequest((ApiEnum(RejectionCode.R001)))
            .setUpdateInternshipRequest(
                Constants.updatedDarkstoreId,
                plannedDate = Instant.now().plusSeconds(1200).truncatedTo(ChronoUnit.SECONDS),
                version = 2

            )
            .setGetInternshipRequest(
                from = employeePreconditions.updateInternshipRequest().plannedDate.minusSeconds(60),
                to = employeePreconditions.updateInternshipRequest().plannedDate.plusSeconds(120)
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
            .updateInternship(
                employeeActions.createdProfileResponse().profileId,
                DarkstoreUserRole.DELIVERYMAN,
                employeePreconditions.updateInternshipRequest()
            )
            .getInternship(employeeActions.createdProfileResponse().profileId)
            .setInternship(databaseController.getInternship(employeeActions.createdProfileResponse().profileId))
            .getDSInternship(
                employeePreconditions.updateInternshipRequest().darkstoreId,
                employeePreconditions.getInternshipDSRequest()
            )
        employeeAssertion
            .checkInternshipFromDB(
                employeePreconditions.updateInternshipRequest().darkstoreId,
                "deliveryman",
                "planned",
                employeeActions.getInternship()
            )
            .checkInternshipLogFromDB(
                databaseController.getInternshipLogArray(employeeActions.createdProfileResponse().profileId),
                3,
                2,
                "updated"
            )

            .checkUpdatedInternshipAPIResponse(
                employeeActions.getCreatedInternship(),
                employeePreconditions.updateInternshipRequest()

            )
            .checkUpdatedInternshipAPIResponse(
                employeeActions.getCreatedDsInternship(),
                employeePreconditions.updateInternshipRequest()

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
                4
            )
    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Update Internship's darkstore without date for cancelled internship is impossible")
    fun updateCancelledInternshipWithoutDateTest() {
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
            .setUpdateInternshipRequest(
                Constants.updatedDarkstoreId,
                plannedDate = employeePreconditions.createInternshipRequest().plannedDate,
                version = 2

            )
            .setGetInternshipRequest(
                from = employeePreconditions.updateInternshipRequest().plannedDate.minusSeconds(60),
                to = employeePreconditions.updateInternshipRequest().plannedDate.plusSeconds(120)
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
            .updateInternshipUnsuccessful(
                employeeActions.createdProfileResponse().profileId,
                DarkstoreUserRole.PICKER,
                employeePreconditions.updateInternshipRequest()
            )
            .setInternship(databaseController.getInternship(employeeActions.createdProfileResponse().profileId))

        employeeAssertion
            .checkStatusBadRequest(employeeActions.getUpdatedInternship().statusCode)
            .checkInternshipFromDB(
                employeePreconditions.createInternshipRequest().darkstoreId,
                "picker",
                "canceled",
                employeeActions.getInternship()
            )
    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Update Internship's darkstore without date for rejected internship is impossible")
    fun updateRejectedInternshipWithoutDateTest() {
        employeePreconditions
            .setCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.PERSONAL_BICYCLE))
            )
            .setCreateInternshipRequest(
                employeePreconditions.createProfileRequest().darkstoreId!!,
                plannedDate = Instant.now().plusSeconds(3).truncatedTo(ChronoUnit.SECONDS)
            )
            .setRejectInternshipRequest((ApiEnum(RejectionCode.R001)))
            .setUpdateInternshipRequest(
                Constants.updatedDarkstoreId,
                plannedDate = employeePreconditions.createInternshipRequest().plannedDate,
                version = 2

            )
            .setGetInternshipRequest(
                from = employeePreconditions.updateInternshipRequest().plannedDate.minusSeconds(60),
                to = employeePreconditions.updateInternshipRequest().plannedDate.plusSeconds(120)
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
            .updateInternshipUnsuccessful(
                employeeActions.createdProfileResponse().profileId,
                DarkstoreUserRole.DELIVERYMAN,
                employeePreconditions.updateInternshipRequest()
            )
            .setInternship(databaseController.getInternship(employeeActions.createdProfileResponse().profileId))

        employeeAssertion
            .checkInternshipFromDB(
                employeePreconditions.createInternshipRequest().darkstoreId,
                "deliveryman",
                "rejected",
                employeeActions.getInternship()
            )
            .checkStatusBadRequest(employeeActions.getUpdatedInternship().statusCode)

    }

    @Test
    @DisplayName("Update Internship for failed internship is impossible")
    fun updateFailedInternshipTest() {
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
                failureCode = (ApiEnum(FailureCode.F006)),
                status = (ApiEnum(InternshipStatus.FAILED))
            )
            .setUpdateInternshipRequest(
                Constants.updatedDarkstoreId,
                plannedDate = Instant.now().plusSeconds(2100).truncatedTo(ChronoUnit.SECONDS),
                version = 2

            )
            .setGetInternshipRequest(
                from = employeePreconditions.updateInternshipRequest().plannedDate.minusSeconds(60),
                to = employeePreconditions.updateInternshipRequest().plannedDate.plusSeconds(120)
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
            .updateInternshipUnsuccessful(
                employeeActions.createdProfileResponse().profileId,
                DarkstoreUserRole.PICKER,
                employeePreconditions.updateInternshipRequest()
            )
            .setInternship(databaseController.getInternship(employeeActions.createdProfileResponse().profileId))

        employeeAssertion
            .checkStatusBadRequest(employeeActions.getUpdatedInternship().statusCode)
            .checkInternshipFromDB(
                employeePreconditions.createInternshipRequest().darkstoreId,
                "picker",
                "failed",
                employeeActions.getInternship()
            )
    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Update Internship for done internship is impossible")
    fun updateDoneInternshipTest() {
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
            .setUpdateInternshipRequest(
                Constants.updatedDarkstoreId,
                plannedDate = Instant.now().plusSeconds(2100).truncatedTo(ChronoUnit.SECONDS),
                version = 2

            )
            .setGetInternshipRequest(
                from = employeePreconditions.updateInternshipRequest().plannedDate.minusSeconds(60),
                to = employeePreconditions.updateInternshipRequest().plannedDate.plusSeconds(120)
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
            .updateInternshipUnsuccessful(
                employeeActions.createdProfileResponse().profileId,
                DarkstoreUserRole.DELIVERYMAN,
                employeePreconditions.updateInternshipRequest()
            )
            .setInternship(databaseController.getInternship(employeeActions.createdProfileResponse().profileId))

        employeeAssertion
            .checkStatusBadRequest(employeeActions.getUpdatedInternship().statusCode)
            .checkInternshipFromDB(
                employeePreconditions.createInternshipRequest().darkstoreId,
                "deliveryman",
                "done",
                employeeActions.getInternship()
            )
    }

    @Test
    @DisplayName("Update Internship date in past is impossible")
    fun updatePastDateInternshipTest() {
        employeePreconditions
            .setCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.PERSONAL_BICYCLE))
            )
            .setCreateInternshipRequest(
                employeePreconditions.createProfileRequest().darkstoreId!!,
                plannedDate = Instant.now().plusSeconds(60).truncatedTo(ChronoUnit.SECONDS)
            )
            .setUpdateInternshipRequest(
                employeePreconditions.createProfileRequest().darkstoreId!!,
                plannedDate = Instant.now().minusSeconds(1200).truncatedTo(ChronoUnit.SECONDS)

            )
            .setGetInternshipRequest(
                from = employeePreconditions.updateInternshipRequest().plannedDate.minusSeconds(60),
                to = employeePreconditions.updateInternshipRequest().plannedDate.plusSeconds(120)
            )

        employeeActions
            .createProfile(employeePreconditions.createProfileRequest())
            .createInternship(
                employeeActions.createdProfileResponse().profileId,
                DarkstoreUserRole.DELIVERYMAN,
                employeePreconditions.createInternshipRequest()
            )
            .updateInternshipUnsuccessful(
                employeeActions.createdProfileResponse().profileId,
                DarkstoreUserRole.DELIVERYMAN,
                employeePreconditions.updateInternshipRequest()
            )
            .getInternship(employeeActions.createdProfileResponse().profileId)

        employeeAssertion
            .checkStatusBadRequest(employeeActions.getUpdatedInternship().statusCode)
            .checkInternshipLogFromDB(
                databaseController.getInternshipLogArray(employeeActions.createdProfileResponse().profileId),
                1,
                0,
                "created"
            )
    }

    @Test
    @DisplayName("Update Internship for deactivated profile is impossible")
    fun updateInternshipForDeactivatedProfileTest() {
        employeePreconditions
            .setCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.PERSONAL_BICYCLE))
            )
            .setCreateInternshipRequest(
                employeePreconditions.createProfileRequest().darkstoreId!!,
                plannedDate = Instant.now().plusSeconds(60).truncatedTo(ChronoUnit.SECONDS)
            )
            .setUpdateInternshipRequest(
                employeePreconditions.createProfileRequest().darkstoreId!!,
                plannedDate = Instant.now().plusSeconds(1200).truncatedTo(ChronoUnit.SECONDS)

            )

        employeeActions
            .createProfile(employeePreconditions.createProfileRequest())
            .createInternship(
                employeeActions.createdProfileResponse().profileId,
                DarkstoreUserRole.DELIVERYMAN,
                employeePreconditions.createInternshipRequest()
            )
            .deleteProfile(employeeActions.createdProfileResponse().profileId)
            .updateInternshipUnsuccessful(
                employeeActions.createdProfileResponse().profileId,
                DarkstoreUserRole.DELIVERYMAN,
                employeePreconditions.updateInternshipRequest()
            )
            .getInternship(employeeActions.createdProfileResponse().profileId)

        employeeAssertion
            .checkStatusNotFound(employeeActions.getUpdatedInternship().statusCode)
            .checkInternshipAPIisEmptyResponse(employeeActions.getCreatedInternship())
    }

    @Test
    @DisplayName("Update Internship for not valid role is impossible")
    fun updateInternshipForNotValidRoleTest() {
        employeePreconditions
            .setCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.PERSONAL_BICYCLE))
            )
            .setCreateInternshipRequest(
                employeePreconditions.createProfileRequest().darkstoreId!!,
                plannedDate = Instant.now().plusSeconds(60).truncatedTo(ChronoUnit.SECONDS)
            )
            .setUpdateInternshipRequest(
                employeePreconditions.createProfileRequest().darkstoreId!!,
                plannedDate = Instant.now().plusSeconds(1200).truncatedTo(ChronoUnit.SECONDS)

            )
            .setGetInternshipRequest(
                from = employeePreconditions.updateInternshipRequest().plannedDate.minusSeconds(60),
                to = employeePreconditions.updateInternshipRequest().plannedDate.plusSeconds(120)
            )

        employeeActions
            .createProfile(employeePreconditions.createProfileRequest())
            .createInternship(
                employeeActions.createdProfileResponse().profileId,
                DarkstoreUserRole.DELIVERYMAN,
                employeePreconditions.createInternshipRequest()
            )
            .updateInternshipUnsuccessful(
                employeeActions.createdProfileResponse().profileId,
                DarkstoreUserRole.PICKER,
                employeePreconditions.updateInternshipRequest()
            )
            .getInternship(employeeActions.createdProfileResponse().profileId)

        employeeAssertion
            .checkStatusNotFound(employeeActions.getUpdatedInternship().statusCode)
            .checkInternshipLogFromDB(
                databaseController.getInternshipLogArray(employeeActions.createdProfileResponse().profileId),
                1,
                0,
                "created"
            )
    }

    @Test
    @DisplayName("Update Internship for not existed profile is impossible")
    fun updateInternshipForNotExistedProfileTest() {
        employeePreconditions
            .setUpdateInternshipRequest(
                darkstoreId = Constants.updatedDarkstoreId,
                plannedDate = Instant.now().plusSeconds(1200).truncatedTo(ChronoUnit.SECONDS),
                issuerProfileId = UUID.fromString("c6f4f0c0-74e5-440f-b46c-a0875fdb880e")
            )

        employeeActions
            .updateInternshipUnsuccessful(
                profileId = UUID.fromString("c6f4f0c0-74e5-440f-b46c-a0875fdb880e"),
                DarkstoreUserRole.DELIVERYMAN,
                employeePreconditions.updateInternshipRequest()
            )

        employeeAssertion
            .checkStatusNotFound(employeeActions.getUpdatedInternship().statusCode)

    }

    @Test
    @DisplayName("Update Internship with incorrect version is impossible")
    fun updateInternshipWithIncorrectVersionProfileTest() {
        employeePreconditions
            .setCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.PERSONAL_BICYCLE))
            )
            .setCreateInternshipRequest(
                employeePreconditions.createProfileRequest().darkstoreId!!,
                plannedDate = Instant.now().plusSeconds(60).truncatedTo(ChronoUnit.SECONDS)
            )
            .setUpdateInternshipRequest(
                employeePreconditions.createProfileRequest().darkstoreId!!,
                plannedDate = Instant.now().plusSeconds(1200).truncatedTo(ChronoUnit.SECONDS),
                version = 3

            )
            .setGetInternshipRequest(
                from = employeePreconditions.updateInternshipRequest().plannedDate.minusSeconds(60),
                to = employeePreconditions.updateInternshipRequest().plannedDate.plusSeconds(120)
            )

        employeeActions
            .createProfile(employeePreconditions.createProfileRequest())
            .createInternship(
                employeeActions.createdProfileResponse().profileId,
                DarkstoreUserRole.DELIVERYMAN,
                employeePreconditions.createInternshipRequest()
            )
            .updateInternshipUnsuccessful(
                employeeActions.createdProfileResponse().profileId,
                DarkstoreUserRole.DELIVERYMAN,
                employeePreconditions.updateInternshipRequest()
            )
            .getInternship(employeeActions.createdProfileResponse().profileId)

        employeeAssertion
            .checkStatusCodeConflict(employeeActions.getUpdatedInternship().statusCode)
            .checkInternshipLogFromDB(
                databaseController.getInternshipLogArray(employeeActions.createdProfileResponse().profileId),
                1,
                0,
                "created"
            )
    }
}