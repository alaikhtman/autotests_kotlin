package ru.generalName.teamName.tests.tests.profile_service.internshipAPI


import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.generalName.teamName.tests.checkers.ProfileAssertions
import ru.generalName.teamName.tests.dataproviders.Constants
import ru.generalName.teamName.tests.dataproviders.preconditions.ProfilePreconditions
import ru.generalName.teamName.tests.helpers.actions.ProfileActions
import ru.generalName.teamName.tests.helpers.controllers.database.ProfilesDatabaseController
import java.time.Instant
import java.time.temporal.ChronoUnit

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Tag("profile")
class CreateInternship {
    private lateinit var profileAssertions: ProfileAssertions

    @Autowired
    private lateinit var profileActions: ProfileActions

    private lateinit var profilePreconditions: ProfilePreconditions

    @Autowired
    private lateinit var databaseController: ProfilesDatabaseController


    @BeforeEach
    fun setUp() {
        profilePreconditions = ProfilePreconditions()
        profileAssertions = ProfileAssertions()
        profileActions.deleteProfile(Constants.mobile1)
    }

    @AfterEach
    fun release() {
        profileAssertions.assertAll()
        profileActions.deleteProfile(Constants.mobile1)

    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Create Internship: deliveryman")
    fun createInternshipDeliverymanTest() {
        profilePreconditions
            .setCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.PERSONAL_BICYCLE))
            )
            .setCreateInternshipRequest(
                profilePreconditions.createProfileRequest().darkstoreId!!,
                plannedDate = Instant.now().plusSeconds(60).truncatedTo(ChronoUnit.SECONDS)
            )
            .setGetInternshipRequest(
                from = profilePreconditions.createInternshipRequest().plannedDate.minusSeconds(60),
                to = profilePreconditions.createInternshipRequest().plannedDate.plusSeconds(120)
            )

        profileActions
            .createProfile(profilePreconditions.createProfileRequest())
            .createInternship(
                profileActions.createdProfileResponse().profileId,
                DarkstoreUserRole.DELIVERYMAN,
                profilePreconditions.createInternshipRequest()
            )
            .getInternship(profileActions.createdProfileResponse().profileId)
            .setProfile(databaseController.getProfile(profileActions.createdProfileResponse().profileId))
            .setInternship(databaseController.getInternship(profileActions.createdProfileResponse().profileId))
            .getDSInternship(
                profilePreconditions.createProfileRequest().darkstoreId!!,
                profilePreconditions.getInternshipDSRequest()
            )

        profileAssertions
            .checkInternshipFromDB(
                profilePreconditions.createInternshipRequest().darkstoreId,
                "deliveryman",
                "planned",
                profileActions.getInternship()
            )
            .checkInternshipLogFromDB(
                databaseController.getInternshipLogArray(profileActions.createdProfileResponse().profileId),
                1,
                0,
                "created"
            )
            .checkInternshipAPIResponse(
                profileActions.getCreatedInternship(),
                profilePreconditions.createInternshipRequest(),
                ApiEnum(InternshipStatus.PLANNED)
            )

            .checkInternshipAPIResponse(
                profileActions.getCreatedDsInternship(),
                profilePreconditions.createInternshipRequest(),
                ApiEnum(InternshipStatus.PLANNED)
            )
            .checkDSUserInDatabase(
                databaseController.getDSUserProfile(
                    profileActions.createdProfileResponse().profileId,
                    profilePreconditions.createProfileRequest().roles[0].value
                ),
                profilePreconditions.createProfileRequest().darkstoreId!!,
                4
            )
            .checkInternStatusForDSUser(
                profileActions.getDarkstoreUserById(
                    profileActions.createdProfileResponse().profileId,
                    profilePreconditions.createProfileRequest().darkstoreId!!,
                    DarkstoreUserRole.DELIVERYMAN,
                ), true
            )

    }


    @Test
    @DisplayName("Create Internship with invalid role of intern is impossible")
    fun createInternshipInvalidRoleInternTest() {
        profilePreconditions
            .setCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DARKSTORE_ADMIN)),
                vehicle = null,
                staffPartnerId = null
            )
            .setCreateInternshipRequest(
                profilePreconditions.createProfileRequest().darkstoreId!!,
                plannedDate = Instant.now().plusSeconds(60).truncatedTo(ChronoUnit.SECONDS)
            )

        profileActions
            .createProfile(profilePreconditions.createProfileRequest())
            .createInternshipUnsuccessful(
                profileActions.createdProfileResponse().profileId,
                DarkstoreUserRole.DELIVERYMAN,
                profilePreconditions.createInternshipRequest()
            )

        profileAssertions
            .checkStatusNotFound(profileActions.getCreateInternshipResponse().statusCode)
            .checkInternshipNotInDatabase(databaseController.checkInternshipExists(profileActions.createdProfileResponse().profileId))

    }

    @Test
    @DisplayName("Create Internship with past date (yesterday) is impossible")
    fun createInternshipPastDateTest() {
        profilePreconditions
            .setCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.PERSONAL_BICYCLE))
            )
            .setCreateInternshipRequest(
                profilePreconditions.createProfileRequest().darkstoreId!!,
                plannedDate = Instant.now().minusSeconds(86400).truncatedTo(ChronoUnit.SECONDS)
            )

        profileActions
            .createProfile(profilePreconditions.createProfileRequest())
            .createInternshipUnsuccessful(
                profileActions.createdProfileResponse().profileId,
                DarkstoreUserRole.DELIVERYMAN,
                profilePreconditions.createInternshipRequest()
            )

        profileAssertions
            .checkStatusBadRequest(profileActions.getCreateInternshipResponse().statusCode)
            .checkInternshipNotInDatabase(databaseController.checkInternshipExists(profileActions.createdProfileResponse().profileId))
            .checkInternStatusForDSUser(
                profileActions.getDarkstoreUserById(
                    profileActions.createdProfileResponse().profileId,
                    profilePreconditions.createProfileRequest().darkstoreId!!,
                    DarkstoreUserRole.DELIVERYMAN,
                ), false
            )
    }


}


