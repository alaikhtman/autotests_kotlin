package ru.samokat.mysamokat.tests.tests.employee_profiles.profilesAPI

import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.samokat.employeeprofiles.api.common.domain.EmployeeVehicleType
import ru.samokat.employeeprofiles.api.common.domain.Vehicle
import ru.samokat.employeeprofiles.api.darkstoreusers.domain.DarkstoreUserRole
import ru.samokat.my.domain.employee.EmployeeRole

import ru.samokat.my.rest.api.enum.ApiEnum
import ru.samokat.mysamokat.tests.checkers.EmployeeAssertion
import ru.samokat.mysamokat.tests.dataproviders.Constants
import ru.samokat.mysamokat.tests.dataproviders.helpers.database.employee_profiles_backend.Profile
import ru.samokat.mysamokat.tests.dataproviders.preconditions.EmployeePreconditions
import ru.samokat.mysamokat.tests.helpers.actions.EmployeeActions
import ru.samokat.mysamokat.tests.helpers.controllers.database.EmployeeProfilesDatabaseController
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*


@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Tag("empro")
class DisableProfile {

    private lateinit var employeeAssertion: EmployeeAssertion

    @Autowired
    lateinit var employeeActions: EmployeeActions

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
    }

    @Test
    @Tags(Tag("smoke"), Tag("kafka_consume"))
    @DisplayName("Disable profile")
    fun deleteProfile() {
        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
                email = null
            )

        val createdProfileId = employeeActions.createProfileId(createRequest)

        employeeActions.deleteProfile(createdProfileId)

        val profileFromApi = employeeActions.getApiProfileById(createdProfileId)
        val profileFromDB = employeeActions.getProfileFromDB(createdProfileId)

        employeeAssertion
            .checkProfileStatusDisable(profileFromDB[Profile.status])
            .checkDisabledUserInProfileLogTable(
                employeeActions.getProfilesFromProfilesLogTable(createdProfileId)
            )
            .checkEmployeeProfileDisableKafka(employeeActions.getMessageFromKafkaDisable(createdProfileId))
            .checkProfilePasswordLogExistByType(
                employeeActions.getProfilePasswordLogRowsCount(
                    createdProfileId,
                    "deletion"
                )
            )
            .checkTwoDatesAreEqual(profileFromApi.createdAt, profileFromDB[Profile.createdAt])
            .checkTwoDatesAreEqual(profileFromApi.updatedAt, profileFromDB[Profile.updatedAt])
            .checkUpdatedDatesLaterThanCreate(profileFromDB[Profile.createdAt], profileFromDB[Profile.updatedAt])
    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Disable profile with internship")
    fun deleteProfileWithInternshipTest() {
        employeePreconditions
            .setCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.PERSONAL_BICYCLE))
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
            .deleteProfile( employeeActions.createdProfileResponse().profileId)

        employeeAssertion
            .checkInternshipNotInDatabase(databaseController.checkInternshipExists(employeeActions.createdProfileResponse().profileId))
            .checkInternshipLogFromDB(
                databaseController.getInternshipLogArray(employeeActions.createdProfileResponse().profileId),
                2,
                1,
                "deleted"
            )

    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Disable profile (invalid profileID)")
    fun disableProfileWithInvalidProfileId() {
        val errorMessage = employeeActions.deleteProfileError(UUID.randomUUID())
        employeeAssertion.checkErrorMessage(errorMessage, "Profile was not found")
    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Disable profile (profile already disabled)")
    fun disableProfileAlreadyDisabled() {
        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
                email = null
            )
        val createdProfileId = employeeActions.createProfileId(createRequest)
        employeeActions.deleteProfile(createdProfileId)

        employeeActions.deleteProfile(createdProfileId)
        val profileFromDB = employeeActions.getProfileFromDB(createdProfileId)

        employeeAssertion
            .checkProfileStatusDisable(profileFromDB[Profile.status])
    }
}
