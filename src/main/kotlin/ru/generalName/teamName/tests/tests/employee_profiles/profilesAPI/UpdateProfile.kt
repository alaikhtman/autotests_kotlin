package ru.samokat.mysamokat.tests.tests.employee_profiles.profilesAPI

import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.samokat.employeeprofiles.api.common.domain.EmployeeVehicleType
import ru.samokat.employeeprofiles.api.common.domain.Vehicle
import ru.samokat.employeeprofiles.api.darkstoreusers.domain.DarkstoreUserRole
import ru.samokat.employeeprofiles.api.darkstoreusers.domain.DarkstoreUserState
import ru.samokat.employeeprofiles.api.darkstoreusers.internships.domain.FailureCode
import ru.samokat.employeeprofiles.api.darkstoreusers.internships.domain.InternshipStatus
import ru.samokat.employeeprofiles.api.darkstoreusers.internships.domain.RejectionCode
import ru.samokat.my.domain.employee.EmployeeName
import ru.samokat.my.domain.employee.EmployeeRole
import ru.samokat.my.rest.api.enum.ApiEnum
import ru.samokat.mysamokat.tests.checkers.EmployeeAssertion
import ru.samokat.mysamokat.tests.dataproviders.Constants
import ru.samokat.mysamokat.tests.dataproviders.StringAndPhoneNumberGenerator
import ru.samokat.mysamokat.tests.dataproviders.employee.UpdateProfileRequestBuilder
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
class UpdateProfile {

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
        employeeActions.deleteProfile(Constants.mobile2)
    }

    @AfterEach
    fun release() {
        employeeAssertion.assertAll()
        employeeActions.deleteProfile(Constants.mobile1)
        employeeActions.deleteProfile(Constants.mobile2)
    }

    @Test
    @Tags(Tag("smoke"), Tag("kafka_consume"))
    @DisplayName("Update profile: name")
    fun updateProfileNameTest() {
        employeePreconditions
            .setCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.PERSONAL_BICYCLE))
            )
            .setUpdateProfileRequest(
                employeePreconditions.createProfileRequest(),
                name = EmployeeName("Sasha", "Petrova", "Alekseevna")
            )

        employeeActions
            .createProfile(employeePreconditions.createProfileRequest())
            .updateProfile(
                employeeActions.createdProfileResponse().profileId,
                employeePreconditions.updateProfileRequest()
            )
            .getProfileById(employeeActions.createdProfileResponse().profileId)
            .setProfile(databaseController.getProfile(employeeActions.createdProfileResponse().profileId))

        employeeAssertion
            .checkFirstNameInDatabase(
                employeePreconditions.updateProfileRequest().name.firstName,
                employeeActions.getProfile()
            )
            .checkLastNameInDatabase(
                employeePreconditions.updateProfileRequest().name.lastName,
                employeeActions.getProfile()
            )
            .checkMiddleNameInDatabase(
                employeePreconditions.updateProfileRequest().name.middleName!!,
                employeeActions.getProfile()
            )
            .checkProfileVersion(
                2,
                employeeActions.getProfile()

            )
            .checkProfileLogInDatabase(
                databaseController.getProfileLogArray(employeeActions.createdProfileResponse().profileId),
                2,
                1,
                "update",
                employeePreconditions.updateProfileRequest().issuerProfileId,
                2
            )
            .checkUpdatedProfilesAreEquals(
                employeeActions.getProfileFromApi(),
                employeePreconditions.updateProfileRequest()
            )
            .checkEmployeeProfileChangedKafka(
                employeeActions.getMessageFromKafkaUpdate(employeeActions.createdProfileResponse().profileId),
                employeePreconditions.updateProfileRequest(),
                2
            )
            .checkEmployeeProfileChangedKafkaUpdatedName(
                employeeActions.getMessageFromKafkaUpdate(employeeActions.createdProfileResponse().profileId),
                employeePreconditions.updateProfileRequest()
            )

            .checkTwoDatesAreEqual(
                employeeActions.getProfileFromApi().createdAt,
                employeeActions.getProfile()[Profile.createdAt]
            )
            .checkTwoDatesAreEqual(
                employeeActions.getProfileFromApi().updatedAt,
                employeeActions.getProfile()[Profile.updatedAt]
            )
            .checkUpdatedDatesLaterThanCreate(
                employeeActions.getProfile()[Profile.createdAt],
                employeeActions.getProfile()[Profile.updatedAt]
            )

    }

    @Test
    @DisplayName("Update profile: twice updates")
    fun updateProfileSeveralTimesTest() {
        employeePreconditions
            .setCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.PERSONAL_BICYCLE))
            )
            .setListUpdateNameProfileRequest(2, employeePreconditions.createProfileRequest())

        employeeActions
            .createProfile(employeePreconditions.createProfileRequest())
            .updateProfileSeveralTimes(
                employeeActions.createdProfileResponse().profileId,
                employeePreconditions.listUpdateProfileRequest()
            )
            .getProfileById(employeeActions.createdProfileResponse().profileId)
            .setProfile(databaseController.getProfile(employeeActions.createdProfileResponse().profileId))

        employeeAssertion
            .checkFirstNameInDatabase(
                employeePreconditions.listUpdateProfileRequest().last().name.firstName,
                employeeActions.getProfile()
            )
            .checkLastNameInDatabase(
                employeePreconditions.listUpdateProfileRequest().last().name.lastName,
                employeeActions.getProfile()
            )
            .checkProfileVersion(
                3,
                employeeActions.getProfile()
            )
            .checkProfileLogInDatabase(
                databaseController.getProfileLogArray(employeeActions.createdProfileResponse().profileId),
                3,
                2,
                "update",
                employeePreconditions.listUpdateProfileRequest().last().issuerProfileId,
                3
            )
            .checkUpdatedProfilesAreEquals(
                employeeActions.getProfileFromApi(),
                employeePreconditions.listUpdateProfileRequest().last()
            )
//            .checkSeveralTimesChangedEmployeeProfilesKafka(
//                employeeActions.getAllMessagesFromKafkaChangeLogById(employeeActions.createdProfileResponse().profileId),
//                employeePreconditions.listUpdateProfileRequest().last(),
//                3,
//                2
//            )

    }

    @Test
    @Tags(Tag("smoke"), Tag("kafka_consume"))
    @DisplayName("Update profile: mobile")
    fun updateProfileMobileTest() {

        employeePreconditions
            .setCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.PERSONAL_BICYCLE)),
                cityId = Constants.cityId
            )
            .setUpdateProfileRequest(
                employeePreconditions.createProfileRequest(),
                mobile = Constants.mobile2,
                cityId = Constants.cityId
            )

        employeeActions
            .createProfile(employeePreconditions.createProfileRequest())
            .updateProfile(
                employeeActions.createdProfileResponse().profileId,
                employeePreconditions.updateProfileRequest()
            )
            .getProfileById(employeeActions.createdProfileResponse().profileId)
            .setProfile(databaseController.getProfile(employeeActions.createdProfileResponse().profileId))


        employeeAssertion
            .checkMobileInDatabase(
                employeePreconditions.updateProfileRequest().mobile.asStringWithoutPlus(),
                employeeActions.getProfile()
            )
            .checkCityIdInDatabase(employeePreconditions.updateProfileRequest().cityId!!, employeeActions.getProfile())
            .checkProfileVersion(
                2,
                employeeActions.getProfile()
            )
            .checkProfileLogInDatabase(
                databaseController.getProfileLogArray(employeeActions.createdProfileResponse().profileId),
                2,
                1,
                "update",
                employeePreconditions.updateProfileRequest().issuerProfileId,
                2
            )
            .checkUpdatedProfilesAreEquals(
                employeeActions.getProfileFromApi(),
                employeePreconditions.updateProfileRequest()
            )

            .checkEmployeeProfileChangedKafka(
                employeeActions.getMessageFromKafkaUpdate(employeeActions.createdProfileResponse().profileId),
                employeePreconditions.updateProfileRequest(),
                2
            )
            .checkEmployeeProfileChangedKafkaUpdatedMobile(
                employeeActions.getMessageFromKafkaUpdate(employeeActions.createdProfileResponse().profileId),
                employeePreconditions.updateProfileRequest()
            )
    }

    @Test
    @Tag("kafka_consume")
    @DisplayName("Update profile: darkstore and city")
    fun updateProfileDarkstoreTest() {
        employeePreconditions
            .setCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.PERSONAL_BICYCLE)),
                cityId = Constants.cityId
            )
            .setUpdateProfileRequest(
                employeePreconditions.createProfileRequest(),
                darkstoreId = Constants.updatedDarkstoreId,
                cityId = Constants.updatedCityId

            )

        employeeActions
            .createProfile(employeePreconditions.createProfileRequest())
            .updateProfile(
                employeeActions.createdProfileResponse().profileId,
                employeePreconditions.updateProfileRequest()
            )
            .getProfileById(employeeActions.createdProfileResponse().profileId)
            .setProfile(databaseController.getProfile(employeeActions.createdProfileResponse().profileId))


        employeeAssertion
            .checkDarkstoreIdInDatabase(
                employeePreconditions.updateProfileRequest().darkstoreId!!,
                employeeActions.getProfile()
            )
            .checkCityIdInDatabase(employeePreconditions.updateProfileRequest().cityId!!, employeeActions.getProfile())
            .checkProfileVersion(
                2,
                employeeActions.getProfile()
            )
            .checkProfileLogInDatabase(
                databaseController.getProfileLogArray(employeeActions.createdProfileResponse().profileId),
                2,
                1,
                "update",
                employeePreconditions.updateProfileRequest().issuerProfileId,
                2
            )
            .checkDSUserInDatabase(
                databaseController.getDSUserProfile(
                    employeeActions.createdProfileResponse().profileId,
                    employeePreconditions.updateProfileRequest().roles[0].value
                ),
                employeePreconditions.updateProfileRequest().darkstoreId!!,
                4
            )
            .checkDSUserActivityInDatabase(
                databaseController.getDSUserActivity(
                    employeeActions.createdProfileResponse().profileId,
                    employeePreconditions.updateProfileRequest().roles[0].value
                ),
                employeePreconditions.updateProfileRequest().darkstoreId!!,
                "waiting_for_fetch"
            )
            .checkDSUserLogInDatabase(
                databaseController.getDSUserLogArray(
                    employeeActions.createdProfileResponse().profileId,
                    employeePreconditions.updateProfileRequest().roles[0].value
                ),
                3,
                2,
                "new"
            )
            .checkUpdatedProfilesAreEquals(
                employeeActions.getProfileFromApi(),
                employeePreconditions.updateProfileRequest()
            )

            .checkEmployeeProfileChangedKafka(
                employeeActions.getMessageFromKafkaUpdate(employeeActions.createdProfileResponse().profileId),
                employeePreconditions.updateProfileRequest(),
                2
            )
            .checkEmployeeProfileChangedKafkaUpdatedDarkstoreAndCity(
                employeeActions.getMessageFromKafkaUpdate(employeeActions.createdProfileResponse().profileId),
                employeePreconditions.updateProfileRequest()
            )
    }

    @Test
    @Tags(Tag("debug"))
    @DisplayName("Update working profile:only darkstore")
    fun updateWorkingProfileDarkstoreTest() {
        employeePreconditions
            .setCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.PERSONAL_BICYCLE)),
                cityId = Constants.cityId
            )
            .setUpdateDSUserProfile(state = ApiEnum(DarkstoreUserState.WORKING))
            .setUpdateProfileRequest(
                employeePreconditions.createProfileRequest(),
                darkstoreId = Constants.updatedDarkstoreId,
                cityId = Constants.cityId
            )

        employeeActions
            .createProfile(employeePreconditions.createProfileRequest())
            .updateDSUserStatus(
                employeeActions.createdProfileResponse().profileId,
                employeePreconditions.createProfileRequest().darkstoreId!!,
                DarkstoreUserRole.DELIVERYMAN,
                employeePreconditions.updateDSUserStatusRequest()
            )
            .updateProfile(
                employeeActions.createdProfileResponse().profileId,
                employeePreconditions.updateProfileRequest()
            )
            .getProfileById(employeeActions.createdProfileResponse().profileId)
            .setProfile(databaseController.getProfile(employeeActions.createdProfileResponse().profileId))

        employeeAssertion
            .checkDarkstoreIdInDatabase(
                employeePreconditions.updateProfileRequest().darkstoreId!!,
                employeeActions.getProfile()
            )
            .checkCityIdInDatabase(employeePreconditions.updateProfileRequest().cityId!!, employeeActions.getProfile())

            .checkProfileVersion(
                2,
                employeeActions.getProfile()
            )
            .checkProfileLogInDatabase(
                databaseController.getProfileLogArray(employeeActions.createdProfileResponse().profileId),
                2,
                1,
                "update",
                employeePreconditions.updateProfileRequest().issuerProfileId,
                2
            )
            .checkDSUserInDatabase(
                databaseController.getDSUserProfile(
                    employeeActions.createdProfileResponse().profileId,
                    employeePreconditions.updateProfileRequest().roles[0].value
                ),
                employeePreconditions.updateProfileRequest().darkstoreId!!,
                6
            )
            .checkDSUserActivityInDatabase(
                databaseController.getDSUserActivity(
                    employeeActions.createdProfileResponse().profileId,
                    employeePreconditions.updateProfileRequest().roles[0].value
                ),
                employeePreconditions.updateProfileRequest().darkstoreId!!,
                "waiting_for_fetch"
            )
            .checkDSUserLogInDatabase(
                databaseController.getDSUserLogArray(
                    employeeActions.createdProfileResponse().profileId,
                    employeePreconditions.updateProfileRequest().roles[0].value
                ), 4, 3, "new"
            )
            .checkUpdatedProfilesAreEquals(
                employeeActions.getProfileFromApi(),
                employeePreconditions.updateProfileRequest()
            )

    }

    @Test
    @DisplayName("Update not_working profile: darkstore and city")
    fun updateNotWorkingProfileDarkstoreTest() {
        employeePreconditions
            .setCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.PERSONAL_BICYCLE)),
                cityId = Constants.cityId
            )
            .setUpdateDSUserProfile(state = ApiEnum(DarkstoreUserState.NOT_WORKING))
            .setUpdateProfileRequest(
                employeePreconditions.createProfileRequest(),
                darkstoreId = Constants.updatedDarkstoreId,
                cityId = Constants.updatedCityId
            )

        employeeActions
            .createProfile(employeePreconditions.createProfileRequest())
            .updateDSUserStatus(
                employeeActions.createdProfileResponse().profileId,
                employeePreconditions.createProfileRequest().darkstoreId!!,
                DarkstoreUserRole.DELIVERYMAN,
                employeePreconditions.updateDSUserStatusRequest()
            )
            .updateProfile(
                employeeActions.createdProfileResponse().profileId,
                employeePreconditions.updateProfileRequest()
            )
            .getProfileById(employeeActions.createdProfileResponse().profileId)
            .setProfile(databaseController.getProfile(employeeActions.createdProfileResponse().profileId))

        employeeAssertion
            .checkDarkstoreIdInDatabase(
                employeePreconditions.updateProfileRequest().darkstoreId!!,
                employeeActions.getProfile()
            )
            .checkCityIdInDatabase(employeePreconditions.updateProfileRequest().cityId!!, employeeActions.getProfile())

            .checkProfileVersion(
                2,
                employeeActions.getProfile()
            )
            .checkProfileLogInDatabase(
                databaseController.getProfileLogArray(employeeActions.createdProfileResponse().profileId),
                2,
                1,
                "update",
                employeePreconditions.updateProfileRequest().issuerProfileId,
                2
            )
            .checkDSUserInDatabase(
                databaseController.getDSUserProfile(
                    employeeActions.createdProfileResponse().profileId,
                    employeePreconditions.updateProfileRequest().roles[0].value
                ),
                employeePreconditions.updateProfileRequest().darkstoreId!!,
                4
            )

            .checkDSUserActivityInDatabase(
                databaseController.getDSUserActivity(
                    employeeActions.createdProfileResponse().profileId,
                    employeePreconditions.updateProfileRequest().roles[0].value
                ),
                employeePreconditions.updateProfileRequest().darkstoreId!!,
                "waiting_for_fetch"
            )
            .checkDSUserLogInDatabase(
                databaseController.getDSUserLogArray(
                    employeeActions.createdProfileResponse().profileId,
                    employeePreconditions.updateProfileRequest().roles[0].value
                ), 4, 3, "new"
            )
            .checkUpdatedProfilesAreEquals(
                employeeActions.getProfileFromApi(),
                employeePreconditions.updateProfileRequest()
            )

    }

    @Test
    @Tag("kafka_consume")
    @DisplayName("Update profile: staff_partner")
    fun updateProfileStaffPartnerTest() {
        employeePreconditions
            .setCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.PERSONAL_BICYCLE))
            )
            .setUpdateProfileRequest(
                employeePreconditions.createProfileRequest(),
                staffPartnerId = databaseController.getStaffPartnerId()
            )

        employeeActions
            .createProfile(employeePreconditions.createProfileRequest())
            .updateProfile(
                employeeActions.createdProfileResponse().profileId,
                employeePreconditions.updateProfileRequest()
            )
            .getProfileById(employeeActions.createdProfileResponse().profileId)
            .setProfile(databaseController.getProfile(employeeActions.createdProfileResponse().profileId))

        employeeAssertion
            .checkPartnerIdInDatabase(
                employeePreconditions.updateProfileRequest().staffPartnerId!!,
                employeeActions.getProfile()
            )
            .checkProfileVersion(
                2,
                employeeActions.getProfile()

            )
            .checkProfileLogInDatabase(
                databaseController.getProfileLogArray(employeeActions.createdProfileResponse().profileId),
                2,
                1,
                "update",
                employeePreconditions.updateProfileRequest().issuerProfileId,
                2
            )
            .checkUpdatedProfilesAreEquals(
                employeeActions.getProfileFromApi(),
                employeePreconditions.updateProfileRequest()
            )
            .checkEmployeeProfileChangedKafka(
                employeeActions.getMessageFromKafkaUpdate(employeeActions.createdProfileResponse().profileId),
                employeePreconditions.updateProfileRequest(),
                2
            )
            .checkEmployeeProfileChangedKafkaUpdatedStaffPartner(
                employeeActions.getMessageFromKafkaUpdate(employeeActions.createdProfileResponse().profileId),
                employeePreconditions.updateProfileRequest()
            )
    }

    @Test
    @Tag("kafka_consume")
    @DisplayName("Update profile: vehicle")
    fun updateProfileVehicleTest() {
        employeePreconditions
            .setCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.PERSONAL_BICYCLE))
            )
            .setUpdateProfileRequest(
                employeePreconditions.createProfileRequest(),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR))
            )

        employeeActions
            .createProfile(employeePreconditions.createProfileRequest())
            .updateProfile(
                employeeActions.createdProfileResponse().profileId,
                employeePreconditions.updateProfileRequest()
            )
            .getProfileById(employeeActions.createdProfileResponse().profileId)
            .setProfile(databaseController.getProfile(employeeActions.createdProfileResponse().profileId))

        employeeAssertion
            .checkVehicleInDatabase(
                employeePreconditions.updateProfileRequest().vehicle!!.type.value,
                databaseController.getProfileVehicle(employeeActions.createdProfileResponse().profileId)

            )
            .checkProfileVersion(
                2,
                employeeActions.getProfile()

            )
            .checkProfileLogInDatabase(
                databaseController.getProfileLogArray(employeeActions.createdProfileResponse().profileId),
                2,
                1,
                "update",
                employeePreconditions.updateProfileRequest().issuerProfileId,
                2
            )
            .checkUpdatedProfilesAreEquals(
                employeeActions.getProfileFromApi(),
                employeePreconditions.updateProfileRequest()
            )
            .checkEmployeeProfileChangedKafka(
                employeeActions.getMessageFromKafkaUpdate(employeeActions.createdProfileResponse().profileId),
                employeePreconditions.updateProfileRequest(),
                2
            )
            .checkEmployeeProfileChangedKafkaUpdatedVehicle(
                employeeActions.getMessageFromKafkaUpdate(employeeActions.createdProfileResponse().profileId),
                employeePreconditions.updateProfileRequest()
            )

    }

    @Test
    @Tag("kafka_consume")
    @DisplayName("Update profile: role (deliveryman-picker)")
    fun updateProfileRoleWithDarkstoreTest() {
        employeePreconditions
            .setCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.PERSONAL_BICYCLE))
            )
            .setUpdateProfileRequest(
                employeePreconditions.createProfileRequest(),
                roles = listOf(ApiEnum(EmployeeRole.PICKER)),
                vehicle = null
            )

        employeeActions
            .createProfile(employeePreconditions.createProfileRequest())
            .updateProfile(
                employeeActions.createdProfileResponse().profileId,
                employeePreconditions.updateProfileRequest()
            )
            .getProfileById(employeeActions.createdProfileResponse().profileId)
            .setProfile(databaseController.getProfile(employeeActions.createdProfileResponse().profileId))

        employeeAssertion
            .checkProfileRolesInDatabase(
                employeeActions.getProfile(),
                employeePreconditions.updateProfileRequest().roles
            )

            .checkProfileVersion(
                2,
                employeeActions.getProfile()

            )
            .checkProfileLogInDatabase(
                databaseController.getProfileLogArray(employeeActions.createdProfileResponse().profileId),
                2,
                1,
                "update",
                employeePreconditions.updateProfileRequest().issuerProfileId,
                2
            )
            .checkDsUserNotInDatabase(
                databaseController.checkDarkstoreUserWithRoleExists(
                    employeeActions.createdProfileResponse().profileId,
                    employeePreconditions.createProfileRequest().roles[0].value
                )
            )
            .checkDSUserInDatabase(
                databaseController.getDSUserProfile(
                    employeeActions.createdProfileResponse().profileId,
                    employeePreconditions.updateProfileRequest().roles[0].value
                ),
                employeePreconditions.updateProfileRequest().darkstoreId!!,
                4
            )
            .checkDsUserActivityNotInDatabase(
                databaseController.checkDarkstoreUserWithRoleActivityExists(
                    employeeActions.createdProfileResponse().profileId,
                    employeePreconditions.createProfileRequest().roles[0].value
                )
            )
            .checkDSUserActivityInDatabase(
                databaseController.getDSUserActivity(
                    employeeActions.createdProfileResponse().profileId,
                    employeePreconditions.updateProfileRequest().roles[0].value
                ),
                employeePreconditions.updateProfileRequest().darkstoreId!!,
                "waiting_for_fetch"
            )
            .checkDSUserLogInDatabase(
                databaseController.getDSUserLogArray(
                    employeeActions.createdProfileResponse().profileId,
                    employeePreconditions.updateProfileRequest().roles[0].value
                ), 1, 0, "new"
            )
            .checkUpdatedProfilesAreEquals(
                employeeActions.getProfileFromApi(),
                employeePreconditions.updateProfileRequest()
            )
            .checkEmployeeProfileChangedKafka(
                employeeActions.getMessageFromKafkaUpdate(employeeActions.createdProfileResponse().profileId),
                employeePreconditions.updateProfileRequest(),
                2
            )
            .checkEmployeeProfileChangedKafkaUpdatedRole(
                employeeActions.getMessageFromKafkaUpdate(employeeActions.createdProfileResponse().profileId),
                employeePreconditions.updateProfileRequest()
            )
    }

    @Test
    @Tag("kafka_consume")
    @DisplayName("Update profile: role without darkstore and city (deliveryman-activation manager)")
    fun updateProfileRoleWithoutDarkstoreTest() {
        employeePreconditions
            .setCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.PERSONAL_BICYCLE)),
                cityId = Constants.cityId
            )
            .setUpdateProfileRequest(
                employeePreconditions.createProfileRequest(),
                staffPartnerId = null,
                darkstoreId = null,
                roles = listOf(ApiEnum(EmployeeRole.ACTIVATION_MANAGER)),
                vehicle = null,
                cityId = null
            )
        employeeActions
            .createProfile(employeePreconditions.createProfileRequest())
            .updateProfile(
                employeeActions.createdProfileResponse().profileId,
                employeePreconditions.updateProfileRequest()
            )
            .getProfileById(employeeActions.createdProfileResponse().profileId)
            .setProfile(databaseController.getProfile(employeeActions.createdProfileResponse().profileId))
        employeeAssertion
            .checkProfileRolesInDatabase(
                employeeActions.getProfile(),
                employeePreconditions.updateProfileRequest().roles
            )
            .checkCityIdInDatabase(profileFromDB = employeeActions.getProfile())

            .checkProfileVersion(
                2,
                employeeActions.getProfile()

            )
            .checkProfileLogInDatabase(
                databaseController.getProfileLogArray(employeeActions.createdProfileResponse().profileId),
                2,
                1,
                "update",
                employeePreconditions.updateProfileRequest().issuerProfileId,
                2
            )
            .checkDsUserNotInDatabase(
                databaseController.checkDarkstoreUserWithRoleExists(
                    employeeActions.createdProfileResponse().profileId,
                    employeePreconditions.createProfileRequest().roles[0].value
                )
            )
            .checkDsUserActivityNotInDatabase(
                databaseController.checkDarkstoreUserWithRoleActivityExists(
                    employeeActions.createdProfileResponse().profileId,
                    employeePreconditions.createProfileRequest().roles[0].value
                )
            )
            .checkDSUserLogInDatabase(
                databaseController.getDSUserLogArray(
                    employeeActions.createdProfileResponse().profileId,
                    employeePreconditions.createProfileRequest().roles[0].value
                ), 2, 1, "deleted"
            )
            .checkUpdatedProfilesAreEquals(
                employeeActions.getProfileFromApi(),
                employeePreconditions.updateProfileRequest()
            )
            .checkEmployeeProfileChangedKafka(
                employeeActions.getMessageFromKafkaUpdate(employeeActions.createdProfileResponse().profileId),
                employeePreconditions.updateProfileRequest(),
                2
            )
            .checkEmployeeProfileChangedKafkaUpdatedRole(
                employeeActions.getMessageFromKafkaUpdate(employeeActions.createdProfileResponse().profileId),
                employeePreconditions.updateProfileRequest()
            )

    }

    @Test
    @Tags(Tag("smoke"), Tag("kafka_consume"))
    @DisplayName("Update profile: role with darkstore and city (supervisor-deliveryman)")
    fun updateProfileRoleWithAddingDarkstoreTest() {
        employeePreconditions
            .setCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.SUPERVISOR)),
                darkstoreId = null,
                staffPartnerId = null,
                vehicle = null,
                cityId = null
            )
            .setUpdateProfileRequest(
                employeePreconditions.createProfileRequest(),
                staffPartnerId = Constants.defaultStaffPartnerId,
                darkstoreId = Constants.darkstoreId,
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.PERSONAL_BICYCLE)),
                cityId = Constants.cityId
            )

        employeeActions
            .createProfile(employeePreconditions.createProfileRequest())
            .updateProfile(
                employeeActions.createdProfileResponse().profileId,
                employeePreconditions.updateProfileRequest()
            )
            .getProfileById(employeeActions.createdProfileResponse().profileId)
            .setProfile(databaseController.getProfile(employeeActions.createdProfileResponse().profileId))

        employeeAssertion
            .checkProfileRolesInDatabase(
                employeeActions.getProfile(),
                employeePreconditions.updateProfileRequest().roles
            )
            .checkCityIdInDatabase(employeePreconditions.updateProfileRequest().cityId!!, employeeActions.getProfile())

            .checkProfileVersion(
                2,
                employeeActions.getProfile()

            )
            .checkProfileLogInDatabase(
                databaseController.getProfileLogArray(employeeActions.createdProfileResponse().profileId),
                2,
                1,
                "update",
                employeePreconditions.updateProfileRequest().issuerProfileId,
                2
            )
            .checkDSUserInDatabase(
                databaseController.getDSUserProfile(
                    employeeActions.createdProfileResponse().profileId,
                    employeePreconditions.updateProfileRequest().roles[0].value
                ),
                employeePreconditions.updateProfileRequest().darkstoreId!!,
                4
            )
            .checkDSUserActivityInDatabase(
                databaseController.getDSUserActivity(
                    employeeActions.createdProfileResponse().profileId,
                    employeePreconditions.updateProfileRequest().roles[0].value
                ),
                employeePreconditions.updateProfileRequest().darkstoreId!!,
                "waiting_for_fetch"
            )
            .checkDSUserLogInDatabase(
                databaseController.getDSUserLogArray(
                    employeeActions.createdProfileResponse().profileId,
                    employeePreconditions.updateProfileRequest().roles[0].value
                ), 1, 0, "new"
            )
            .checkUpdatedProfilesAreEquals(
                employeeActions.getProfileFromApi(),
                employeePreconditions.updateProfileRequest()
            )
            .checkEmployeeProfileChangedKafka(
                employeeActions.getMessageFromKafkaUpdate(employeeActions.createdProfileResponse().profileId),
                employeePreconditions.updateProfileRequest(),
                2
            )
            .checkEmployeeProfileChangedKafkaUpdatedRole(
                employeeActions.getMessageFromKafkaUpdate(employeeActions.createdProfileResponse().profileId),
                employeePreconditions.updateProfileRequest()
            )
    }

    @Test
    @Tags(Tag("smoke"), Tag("kafka_consume"))
    @DisplayName("Update profile: role with staff-partner and vehicle (goods_manager - deliveryman)")
    fun updateProfileRoleWithVehicleAndPartnerTest() {
        employeePreconditions
            .setCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.GOODS_MANAGER)),
                staffPartnerId = null,
                vehicle = null
            )
            .setUpdateProfileRequest(
                employeePreconditions.createProfileRequest(),
                staffPartnerId = Constants.defaultStaffPartnerId,
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.PERSONAL_BICYCLE))
            )

        employeeActions
            .createProfile(employeePreconditions.createProfileRequest())
            .updateProfile(
                employeeActions.createdProfileResponse().profileId,
                employeePreconditions.updateProfileRequest()
            )
            .getProfileById(employeeActions.createdProfileResponse().profileId)
            .setProfile(databaseController.getProfile(employeeActions.createdProfileResponse().profileId))

        employeeAssertion
            .checkProfileRolesInDatabase(
                employeeActions.getProfile(),
                employeePreconditions.updateProfileRequest().roles
            )
            .checkVehicleInDatabase(
                employeePreconditions.updateProfileRequest().vehicle!!.type.value,
                databaseController.getProfileVehicle(employeeActions.createdProfileResponse().profileId)
            )
            .checkPartnerIdInDatabase(
                employeePreconditions.updateProfileRequest().staffPartnerId!!,
                employeeActions.getProfile()
            )
            .checkProfileVersion(
                2,
                employeeActions.getProfile()

            )
            .checkProfileLogInDatabase(
                databaseController.getProfileLogArray(employeeActions.createdProfileResponse().profileId),
                2,
                1,
                "update",
                employeePreconditions.updateProfileRequest().issuerProfileId,
                2
            )
            .checkDSUserInDatabase(
                databaseController.getDSUserProfile(
                    employeeActions.createdProfileResponse().profileId,
                    employeePreconditions.updateProfileRequest().roles[0].value
                ),
                employeePreconditions.updateProfileRequest().darkstoreId!!,
                4
            )
            .checkDSUserActivityInDatabase(
                databaseController.getDSUserActivity(
                    employeeActions.createdProfileResponse().profileId,
                    employeePreconditions.updateProfileRequest().roles[0].value
                ),
                employeePreconditions.updateProfileRequest().darkstoreId!!,
                "waiting_for_fetch"
            )
            .checkDSUserLogInDatabase(
                databaseController.getDSUserLogArray(
                    employeeActions.createdProfileResponse().profileId,
                    employeePreconditions.updateProfileRequest().roles[0].value
                ), 1, 0, "new"
            )
            .checkUpdatedProfilesAreEquals(
                employeeActions.getProfileFromApi(),
                employeePreconditions.updateProfileRequest()
            )
            .checkEmployeeProfileChangedKafka(
                employeeActions.getMessageFromKafkaUpdate(employeeActions.createdProfileResponse().profileId),
                employeePreconditions.updateProfileRequest(),
                2
            )
            .checkEmployeeProfileChangedKafkaUpdatedRole(
                employeeActions.getMessageFromKafkaUpdate(employeeActions.createdProfileResponse().profileId),
                employeePreconditions.updateProfileRequest()
            )
            .checkEmployeeProfileChangedKafkaUpdatedStaffPartner(
                employeeActions.getMessageFromKafkaUpdate(employeeActions.createdProfileResponse().profileId),
                employeePreconditions.updateProfileRequest()
            )
            .checkEmployeeProfileChangedKafkaUpdatedVehicle(
                employeeActions.getMessageFromKafkaUpdate(employeeActions.createdProfileResponse().profileId),
                employeePreconditions.updateProfileRequest()
            )
    }

    @Test
    @Tag("kafka_consume")
    @DisplayName("Update profile: role without staff-partner and vehicle(deliveryman-goods_manager)")
    fun updateProfileRoleWithOutVehicleAndPartnerTest() {
        employeePreconditions
            .setCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.PERSONAL_BICYCLE))
            )
            .setUpdateProfileRequest(
                employeePreconditions.createProfileRequest(),
                staffPartnerId = null,
                roles = listOf(ApiEnum(EmployeeRole.GOODS_MANAGER)),
                vehicle = null
            )

        employeeActions
            .createProfile(employeePreconditions.createProfileRequest())
            .updateProfile(
                employeeActions.createdProfileResponse().profileId,
                employeePreconditions.updateProfileRequest()
            )
            .getProfileById(employeeActions.createdProfileResponse().profileId)
            .setProfile(databaseController.getProfile(employeeActions.createdProfileResponse().profileId))

        employeeAssertion
            .checkProfileRolesInDatabase(
                employeeActions.getProfile(),
                employeePreconditions.updateProfileRequest().roles
            )
            .checkProfileVersion(
                2,
                employeeActions.getProfile()

            )
            .checkProfileLogInDatabase(
                databaseController.getProfileLogArray(employeeActions.createdProfileResponse().profileId),
                2,
                1,
                "update",
                employeePreconditions.updateProfileRequest().issuerProfileId,
                2
            )
            .checkDsUserNotInDatabase(
                databaseController.checkDarkstoreUserWithRoleExists(
                    employeeActions.createdProfileResponse().profileId,
                    employeePreconditions.createProfileRequest().roles[0].value
                )
            )
            .checkDsUserActivityNotInDatabase(
                databaseController.checkDarkstoreUserWithRoleActivityExists(
                    employeeActions.createdProfileResponse().profileId,
                    employeePreconditions.createProfileRequest().roles[0].value
                )
            )
            .checkDSUserLogInDatabase(
                databaseController.getDSUserLogArray(
                    employeeActions.createdProfileResponse().profileId,
                    employeePreconditions.createProfileRequest().roles[0].value
                ), 2, 1, "deleted"
            )

            .checkUpdatedProfilesAreEquals(
                employeeActions.getProfileFromApi(),
                employeePreconditions.updateProfileRequest()
            )
            .checkEmployeeProfileChangedKafka(
                employeeActions.getMessageFromKafkaUpdate(employeeActions.createdProfileResponse().profileId),
                employeePreconditions.updateProfileRequest(),
                2
            )
            .checkEmployeeProfileChangedKafkaUpdatedRole(
                employeeActions.getMessageFromKafkaUpdate(employeeActions.createdProfileResponse().profileId),
                employeePreconditions.updateProfileRequest()
            )
    }

    @Test
    @Tag("kafka_consume")
    @DisplayName("Update profile: role with supervised darkstores(darkstore_admin - coordinator)")
    fun updateProfileRoleWithSupervisedDarkstoreTest() {
        employeePreconditions
            .setCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DARKSTORE_ADMIN)),
                staffPartnerId = null,
                vehicle = null
            )
            .setUpdateProfileRequest(
                employeePreconditions.createProfileRequest(),
                staffPartnerId = null,
                darkstoreId = null,
                roles = listOf(ApiEnum(EmployeeRole.COORDINATOR)),
                vehicle = null,
                supervisedDarkstores = UpdateProfileRequestBuilder().randomSupervisedDarkstore(2)
                    .getSupervisedDarkstore(),
                cityId = null
            )

        employeeActions
            .createProfile(employeePreconditions.createProfileRequest())
            .updateProfile(
                employeeActions.createdProfileResponse().profileId,
                employeePreconditions.updateProfileRequest()
            )
            .getProfileById(employeeActions.createdProfileResponse().profileId)
            .setProfile(databaseController.getProfile(employeeActions.createdProfileResponse().profileId))

        employeeAssertion
            .checkProfileRolesInDatabase(
                employeeActions.getProfile(),
                employeePreconditions.updateProfileRequest().roles
            )
            .checkProfileSupervisedDarkstoreInDatabase(
                employeePreconditions.updateProfileRequest().supervisedDarkstores,
                databaseController.getProfileSupervisedDarkstores(employeeActions.createdProfileResponse().profileId)
            )
            .checkProfileVersion(
                2,
                employeeActions.getProfile()

            )
            .checkProfileLogInDatabase(
                databaseController.getProfileLogArray(employeeActions.createdProfileResponse().profileId),
                2,
                1,
                "update",
                employeePreconditions.updateProfileRequest().issuerProfileId,
                2
            )
            .checkUpdatedProfilesAreEquals(
                employeeActions.getProfileFromApi(),
                employeePreconditions.updateProfileRequest()
            )
            .checkEmployeeProfileChangedKafka(
                employeeActions.getMessageFromKafkaUpdate(employeeActions.createdProfileResponse().profileId),
                employeePreconditions.updateProfileRequest(),
                2
            )

            .checkEmployeeProfileChangedKafkaUpdatedSupervisedDS(
                employeeActions.getMessageFromKafkaUpdate(employeeActions.createdProfileResponse().profileId),
                employeePreconditions.updateProfileRequest()
            )
    }

    @Test
    @DisplayName("Update profile: role without supervised darkstores(coordinator-darkstore_admin)")
    fun updateProfileRoleWithoutSupervisedDarkstoreTest() {
        employeePreconditions
            .setCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.COORDINATOR)),
                staffPartnerId = null,
                darkstoreId = null,
                vehicle = null,
                supervisedDarkstores = UpdateProfileRequestBuilder().randomSupervisedDarkstore(16)
                    .getSupervisedDarkstore(),
                cityId = null
            )
            .setUpdateProfileRequest(
                employeePreconditions.createProfileRequest(),
                darkstoreId = Constants.darkstoreId,
                roles = listOf(ApiEnum(EmployeeRole.DARKSTORE_ADMIN))
            )

        employeeActions
            .createProfile(employeePreconditions.createProfileRequest())
            .updateProfile(
                employeeActions.createdProfileResponse().profileId,
                employeePreconditions.updateProfileRequest()
            )
            .getProfileById(employeeActions.createdProfileResponse().profileId)
            .setProfile(databaseController.getProfile(employeeActions.createdProfileResponse().profileId))

        employeeAssertion
            .checkProfileRolesInDatabase(
                employeeActions.getProfile(),
                employeePreconditions.updateProfileRequest().roles
            )
            .checkProfileSupervisedDarkstoreNotInDatabase(
                employeePreconditions.createProfileRequest().supervisedDarkstores,
                databaseController.getProfileSupervisedDarkstores(employeeActions.createdProfileResponse().profileId)
            )
            .checkProfileVersion(
                2,
                employeeActions.getProfile()

            )
            .checkProfileLogInDatabase(
                databaseController.getProfileLogArray(employeeActions.createdProfileResponse().profileId),
                2,
                1,
                "update",
                employeePreconditions.updateProfileRequest().issuerProfileId,
                2
            )
            .checkUpdatedProfilesAreEquals(
                employeeActions.getProfileFromApi(),
                employeePreconditions.updateProfileRequest()
            )

    }

    @Test
    @Tag("kafka_consume")
    @DisplayName("Update profile: role with email (staff_manager- counterparty)")
    fun updateProfileRoleWithEmailTest() {
        employeePreconditions
            .setCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.STAFF_MANAGER)),
                staffPartnerId = null,
                darkstoreId = null,
                vehicle = null,
                cityId = null
            )
            .setUpdateProfileRequest(
                employeePreconditions.createProfileRequest(),
                staffPartnerId = Constants.defaultStaffPartnerId,
                roles = listOf(ApiEnum(EmployeeRole.COUNTERPARTY)),
                email = Constants.defaultEmail,
                cityId = null
            )

        employeeActions
            .createProfile(employeePreconditions.createProfileRequest())
            .updateProfile(
                employeeActions.createdProfileResponse().profileId,
                employeePreconditions.updateProfileRequest()
            )
            .getProfileById(employeeActions.createdProfileResponse().profileId)
            .setProfile(databaseController.getProfile(employeeActions.createdProfileResponse().profileId))

        employeeAssertion
            .checkProfileRolesInDatabase(
                employeeActions.getProfile(),
                employeePreconditions.updateProfileRequest().roles
            )
            .checkEmailInDatabase(
                employeePreconditions.updateProfileRequest().email,
                employeeActions.getProfile()
            )
            .checkProfileVersion(
                2,
                employeeActions.getProfile()

            )
            .checkProfileLogInDatabase(
                databaseController.getProfileLogArray(employeeActions.createdProfileResponse().profileId),
                2,
                1,
                "update",
                employeePreconditions.updateProfileRequest().issuerProfileId,
                2
            )
            .checkUpdatedProfilesAreEquals(
                employeeActions.getProfileFromApi(),
                employeePreconditions.updateProfileRequest()
            )
            .checkEmployeeProfileChangedKafka(
                employeeActions.getMessageFromKafkaUpdate(employeeActions.createdProfileResponse().profileId),
                employeePreconditions.updateProfileRequest(),
                2
            )
            .checkEmployeeProfileChangedKafkaUpdatedRole(
                employeeActions.getMessageFromKafkaUpdate(employeeActions.createdProfileResponse().profileId),
                employeePreconditions.updateProfileRequest()
            )

    }

    @Test
    @DisplayName("Update profile: role without email (counterparty-staff_manager)")
    fun updateProfileRoleWithoutEmailTest() {
        employeePreconditions
            .setCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.COUNTERPARTY)),
                email = Constants.defaultEmail,
                staffPartnerId = Constants.defaultStaffPartnerId,
                darkstoreId = null,
                vehicle = null,
                cityId = null

            )
            .setUpdateProfileRequest(
                employeePreconditions.createProfileRequest(),
                staffPartnerId = null,
                roles = listOf(ApiEnum(EmployeeRole.STAFF_MANAGER)),
                email = null,
                cityId = null

            )

        employeeActions
            .createProfile(employeePreconditions.createProfileRequest())
            .updateProfile(
                employeeActions.createdProfileResponse().profileId,
                employeePreconditions.updateProfileRequest()
            )
            .getProfileById(employeeActions.createdProfileResponse().profileId)
            .setProfile(databaseController.getProfile(employeeActions.createdProfileResponse().profileId))

        employeeAssertion
            .checkProfileRolesInDatabase(
                employeeActions.getProfile(),
                employeePreconditions.updateProfileRequest().roles
            )

            .checkProfileVersion(
                2,
                employeeActions.getProfile()

            )
            .checkProfileLogInDatabase(
                databaseController.getProfileLogArray(employeeActions.createdProfileResponse().profileId),
                2,
                1,
                "update",
                employeePreconditions.updateProfileRequest().issuerProfileId,
                2
            )
            .checkUpdatedProfilesAreEquals(
                employeeActions.getProfileFromApi(),
                employeePreconditions.updateProfileRequest()
            )

    }

    @Test
    @Tag("kafka_consume")
    @DisplayName("Update profile: adding new role  (deliveryman+picker)")
    fun updateNewProfileAddingRoleTest() {
        employeePreconditions
            .setCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.PERSONAL_BICYCLE))
            )
            .setUpdateProfileRequest(
                employeePreconditions.createProfileRequest(),
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN), ApiEnum(EmployeeRole.PICKER))
            )

        employeeActions
            .createProfile(employeePreconditions.createProfileRequest())
            .updateProfile(
                employeeActions.createdProfileResponse().profileId,
                employeePreconditions.updateProfileRequest()
            )
            .getProfileById(employeeActions.createdProfileResponse().profileId)
            .setProfile(databaseController.getProfile(employeeActions.createdProfileResponse().profileId))

        employeeAssertion
            .checkProfileRolesInDatabase(
                employeeActions.getProfile(),
                employeePreconditions.updateProfileRequest().roles
            )
            .checkProfileVersion(
                2,
                employeeActions.getProfile()

            )
            .checkProfileLogInDatabase(
                databaseController.getProfileLogArray(employeeActions.createdProfileResponse().profileId),
                2,
                1,
                "update",
                employeePreconditions.updateProfileRequest().issuerProfileId,
                2
            )

            .checkDSUserInDatabase(
                databaseController.getDSUserProfile(
                    employeeActions.createdProfileResponse().profileId,
                    employeePreconditions.updateProfileRequest().roles[0].value
                ),
                employeePreconditions.updateProfileRequest().darkstoreId!!,
                4
            )
            .checkDSUserInDatabase(
                databaseController.getDSUserProfile(
                    employeeActions.createdProfileResponse().profileId,
                    employeePreconditions.updateProfileRequest().roles[1].value
                ),
                employeePreconditions.updateProfileRequest().darkstoreId!!,
                4
            )

            .checkDSUserActivityInDatabase(
                databaseController.getDSUserActivity(
                    employeeActions.createdProfileResponse().profileId,
                    employeePreconditions.updateProfileRequest().roles[0].value
                ),
                employeePreconditions.updateProfileRequest().darkstoreId!!,
                "waiting_for_fetch"
            )
            .checkDSUserActivityInDatabase(
                databaseController.getDSUserActivity(
                    employeeActions.createdProfileResponse().profileId,
                    employeePreconditions.updateProfileRequest().roles[1].value
                ),
                employeePreconditions.updateProfileRequest().darkstoreId!!,
                "waiting_for_fetch"
            )
            .checkDSUserLogInDatabase(
                databaseController.getDSUserLogArray(
                    employeeActions.createdProfileResponse().profileId,
                    employeePreconditions.updateProfileRequest().roles[0].value
                ), 1, 0, "new"
            )
            .checkDSUserLogInDatabase(
                databaseController.getDSUserLogArray(
                    employeeActions.createdProfileResponse().profileId,
                    employeePreconditions.updateProfileRequest().roles[1].value
                ), 1, 0, "new"
            )
            .checkUpdatedProfilesAreEquals(
                employeeActions.getProfileFromApi(),
                employeePreconditions.updateProfileRequest()
            )
            .checkEmployeeProfileChangedKafka(
                employeeActions.getMessageFromKafkaUpdate(employeeActions.createdProfileResponse().profileId),
                employeePreconditions.updateProfileRequest(),
                2
            )
            .checkEmployeeProfileChangedKafkaUpdatedRole(
                employeeActions.getMessageFromKafkaUpdate(employeeActions.createdProfileResponse().profileId),
                employeePreconditions.updateProfileRequest()
            )
    }

    @Test
    @DisplayName("Update working profile: adding new role  (deliveryman+picker)")
    fun updateWorkingProfileAddingRoleTest() {
        employeePreconditions
            .setCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.PERSONAL_BICYCLE))
            )
            .setUpdateDSUserProfile(state = ApiEnum(DarkstoreUserState.WORKING))
            .setUpdateProfileRequest(
                employeePreconditions.createProfileRequest(),
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN), ApiEnum(EmployeeRole.PICKER))
            )

        employeeActions
            .createProfile(employeePreconditions.createProfileRequest())
            .updateDSUserStatus(
                employeeActions.createdProfileResponse().profileId,
                employeePreconditions.createProfileRequest().darkstoreId!!,
                DarkstoreUserRole.DELIVERYMAN,
                employeePreconditions.updateDSUserStatusRequest()
            )
            .updateProfile(
                employeeActions.createdProfileResponse().profileId,
                employeePreconditions.updateProfileRequest()
            )
            .getProfileById(employeeActions.createdProfileResponse().profileId)
            .setProfile(databaseController.getProfile(employeeActions.createdProfileResponse().profileId))

        employeeAssertion
            .checkProfileRolesInDatabase(
                employeeActions.getProfile(),
                employeePreconditions.updateProfileRequest().roles

            )
            .checkProfileVersion(
                2,
                employeeActions.getProfile()

            )
            .checkProfileLogInDatabase(
                databaseController.getProfileLogArray(employeeActions.createdProfileResponse().profileId),
                2,
                1,
                "update",
                employeePreconditions.updateProfileRequest().issuerProfileId,
                2
            )

            .checkDSUserInDatabase(
                databaseController.getDSUserProfile(
                    employeeActions.createdProfileResponse().profileId,
                    employeePreconditions.updateProfileRequest().roles[0].value
                ),
                employeePreconditions.updateProfileRequest().darkstoreId!!,
                6
            )
            .checkDSUserInDatabase(
                databaseController.getDSUserProfile(
                    employeeActions.createdProfileResponse().profileId,
                    employeePreconditions.updateProfileRequest().roles[1].value
                ),
                employeePreconditions.updateProfileRequest().darkstoreId!!,
                4
            )

            .checkDSUserActivityInDatabase(
                databaseController.getDSUserActivity(
                    employeeActions.createdProfileResponse().profileId,
                    employeePreconditions.updateProfileRequest().roles[0].value
                ),
                employeePreconditions.updateProfileRequest().darkstoreId!!,
                "waiting_for_fetch"
            )
            .checkDSUserActivityInDatabase(
                databaseController.getDSUserActivity(
                    employeeActions.createdProfileResponse().profileId,
                    employeePreconditions.updateProfileRequest().roles[1].value
                ),
                employeePreconditions.updateProfileRequest().darkstoreId!!,
                "waiting_for_fetch"
            )
            .checkDSUserLogInDatabase(
                databaseController.getDSUserLogArray(
                    employeeActions.createdProfileResponse().profileId,
                    employeePreconditions.updateProfileRequest().roles[0].value
                ), 2, 1, "updated"
            )
            .checkDSUserLogInDatabase(
                databaseController.getDSUserLogArray(
                    employeeActions.createdProfileResponse().profileId,
                    employeePreconditions.updateProfileRequest().roles[1].value
                ), 1, 0, "new"
            )
            .checkUpdatedProfilesAreEquals(
                employeeActions.getProfileFromApi(),
                employeePreconditions.updateProfileRequest()
            )
        // TODO Kafka
    }

    @Test
    @DisplayName("Update not_working profile: adding new role  (deliveryman+picker)")
    fun updateNotWorkingProfileAddingRoleTest() {
        employeePreconditions
            .setCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.PERSONAL_BICYCLE))
            )
            .setUpdateDSUserProfile(state = ApiEnum(DarkstoreUserState.NOT_WORKING))
            .setUpdateProfileRequest(
                employeePreconditions.createProfileRequest(),
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN), ApiEnum(EmployeeRole.PICKER))
            )

        employeeActions
            .createProfile(employeePreconditions.createProfileRequest())
            .updateDSUserStatus(
                employeeActions.createdProfileResponse().profileId,
                employeePreconditions.createProfileRequest().darkstoreId!!,
                DarkstoreUserRole.DELIVERYMAN,
                employeePreconditions.updateDSUserStatusRequest()
            )
            .updateProfile(
                employeeActions.createdProfileResponse().profileId,
                employeePreconditions.updateProfileRequest()
            )
            .getProfileById(employeeActions.createdProfileResponse().profileId)
            .setProfile(databaseController.getProfile(employeeActions.createdProfileResponse().profileId))

        employeeAssertion
            .checkProfileRolesInDatabase(
                employeeActions.getProfile(),
                employeePreconditions.updateProfileRequest().roles
            )
            .checkProfileVersion(
                2,
                employeeActions.getProfile()

            )
            .checkProfileLogInDatabase(
                databaseController.getProfileLogArray(employeeActions.createdProfileResponse().profileId),
                2,
                1,
                "update",
                employeePreconditions.updateProfileRequest().issuerProfileId,
                2
            )

            .checkDSUserInDatabase(
                databaseController.getDSUserProfile(
                    employeeActions.createdProfileResponse().profileId,
                    employeePreconditions.updateProfileRequest().roles[0].value
                ),
                employeePreconditions.updateProfileRequest().darkstoreId!!,
                5
            )
            .checkDSUserInDatabase(
                databaseController.getDSUserProfile(
                    employeeActions.createdProfileResponse().profileId,
                    employeePreconditions.updateProfileRequest().roles[1].value
                ),
                employeePreconditions.updateProfileRequest().darkstoreId!!,
                4
            )

            .checkDSUserActivityInDatabase(
                databaseController.getDSUserActivity(
                    employeeActions.createdProfileResponse().profileId,
                    employeePreconditions.updateProfileRequest().roles[0].value
                ),
                employeePreconditions.updateProfileRequest().darkstoreId!!,
                "waiting_for_fetch"
            )
            .checkDSUserActivityInDatabase(
                databaseController.getDSUserActivity(
                    employeeActions.createdProfileResponse().profileId,
                    employeePreconditions.updateProfileRequest().roles[1].value
                ),
                employeePreconditions.updateProfileRequest().darkstoreId!!,
                "waiting_for_fetch"
            )
            .checkDSUserLogInDatabase(
                databaseController.getDSUserLogArray(
                    employeeActions.createdProfileResponse().profileId,
                    employeePreconditions.updateProfileRequest().roles[0].value
                ), 2, 1, "updated"
            )
            .checkDSUserLogInDatabase(
                databaseController.getDSUserLogArray(
                    employeeActions.createdProfileResponse().profileId,
                    employeePreconditions.updateProfileRequest().roles[1].value
                ), 1, 0, "new"
            )
            .checkUpdatedProfilesAreEquals(
                employeeActions.getProfileFromApi(),
                employeePreconditions.updateProfileRequest()
            )
        // TODO Kafka
    }

    @Test
    @Tag("kafka_consume")
    @DisplayName("Update profile: deleting new role  (deliveryman+picker - picker)")
    fun updateProfileDeletingRoleTest() {
        employeePreconditions
            .setCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN), ApiEnum(EmployeeRole.PICKER)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.PERSONAL_BICYCLE))
            )
            .setUpdateProfileRequest(
                employeePreconditions.createProfileRequest(),
                roles = listOf(ApiEnum(EmployeeRole.PICKER)),
                vehicle = null
            )

        employeeActions
            .createProfile(employeePreconditions.createProfileRequest())
            .updateProfile(
                employeeActions.createdProfileResponse().profileId,
                employeePreconditions.updateProfileRequest()
            )
            .getProfileById(employeeActions.createdProfileResponse().profileId)
            .setProfile(databaseController.getProfile(employeeActions.createdProfileResponse().profileId))

        employeeAssertion
            .checkProfileRolesInDatabase(
                employeeActions.getProfile(),
                employeePreconditions.updateProfileRequest().roles
            )
            .checkProfileVersion(
                2,
                employeeActions.getProfile()

            )
            .checkProfileLogInDatabase(
                databaseController.getProfileLogArray(employeeActions.createdProfileResponse().profileId),
                2,
                1,
                "update",
                employeePreconditions.updateProfileRequest().issuerProfileId,
                2
            )

            .checkDSUserInDatabase(
                databaseController.getDSUserProfile(
                    employeeActions.createdProfileResponse().profileId,
                    employeePreconditions.updateProfileRequest().roles[0].value
                ),
                employeePreconditions.updateProfileRequest().darkstoreId!!,
                4
            )
            .checkDsUserNotInDatabase(
                databaseController.checkDarkstoreUserWithRoleExists(
                    employeeActions.createdProfileResponse().profileId,
                    employeePreconditions.createProfileRequest().roles[0].value
                )
            )

            .checkDSUserActivityInDatabase(
                databaseController.getDSUserActivity(
                    employeeActions.createdProfileResponse().profileId,
                    employeePreconditions.updateProfileRequest().roles[0].value
                ),
                employeePreconditions.updateProfileRequest().darkstoreId!!,
                "waiting_for_fetch"
            )
            .checkDsUserActivityNotInDatabase(
                databaseController.checkDarkstoreUserWithRoleActivityExists(
                    employeeActions.createdProfileResponse().profileId,
                    employeePreconditions.createProfileRequest().roles[0].value
                )
            )
            .checkDSUserLogInDatabase(
                databaseController.getDSUserLogArray(
                    employeeActions.createdProfileResponse().profileId,
                    employeePreconditions.updateProfileRequest().roles[0].value
                ), 1, 0, "new"
            )
            .checkUpdatedProfilesAreEquals(
                employeeActions.getProfileFromApi(),
                employeePreconditions.updateProfileRequest()
            )
            .checkEmployeeProfileChangedKafka(
                employeeActions.getMessageFromKafkaUpdate(employeeActions.createdProfileResponse().profileId),
                employeePreconditions.updateProfileRequest(),
                2
            )
            .checkEmployeeProfileChangedKafkaUpdatedRole(
                employeeActions.getMessageFromKafkaUpdate(employeeActions.createdProfileResponse().profileId),
                employeePreconditions.updateProfileRequest()
            )
    }

    @Test
    @Tag("kafka_consume")
    @DisplayName("Update profile: edit supervised darkstores")
    fun updateProfileAddingSupervisedDarkstoreTest() {
        employeePreconditions
            .setCreateProfileRequest(
                darkstoreId = null,
                roles = listOf(ApiEnum(EmployeeRole.COORDINATOR)),
                staffPartnerId = null,
                supervisedDarkstores = UpdateProfileRequestBuilder().randomSupervisedDarkstore(5)
                    .getSupervisedDarkstore(),
                vehicle = null,
                cityId = null
            )
            .setUpdateProfileRequest(
                employeePreconditions.createProfileRequest(),
                supervisedDarkstores = UpdateProfileRequestBuilder().randomSupervisedDarkstore(2)
                    .getSupervisedDarkstore()
            )

        employeeActions
            .createProfile(employeePreconditions.createProfileRequest())
            .updateProfile(
                employeeActions.createdProfileResponse().profileId,
                employeePreconditions.updateProfileRequest()
            )
            .getProfileById(employeeActions.createdProfileResponse().profileId)
            .setProfile(databaseController.getProfile(employeeActions.createdProfileResponse().profileId))

        employeeAssertion
            .checkProfileSupervisedDarkstoreInDatabase(
                employeePreconditions.updateProfileRequest().supervisedDarkstores,
                databaseController.getProfileSupervisedDarkstores(employeeActions.createdProfileResponse().profileId)
            )
            .checkProfileVersion(
                2,
                employeeActions.getProfile()

            )
            .checkProfileLogInDatabase(
                databaseController.getProfileLogArray(employeeActions.createdProfileResponse().profileId),
                2,
                1,
                "update",
                employeePreconditions.updateProfileRequest().issuerProfileId,
                2
            )
            .checkUpdatedProfilesAreEquals(
                employeeActions.getProfileFromApi(),
                employeePreconditions.updateProfileRequest()
            )
            .checkEmployeeProfileChangedKafka(
                employeeActions.getMessageFromKafkaUpdate(employeeActions.createdProfileResponse().profileId),
                employeePreconditions.updateProfileRequest(),
                2
            )
            .checkEmployeeProfileChangedKafkaUpdatedSupervisedDS(
                employeeActions.getMessageFromKafkaUpdate(employeeActions.createdProfileResponse().profileId),
                employeePreconditions.updateProfileRequest()
            )
    }

    @Test
    @Tags(Tag("smoke"), Tag("kafka_consume"))
    @DisplayName("Update profile: edit cp email")
    fun updateProfileCpEmailTest() {
        employeePreconditions
            .setCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.COUNTERPARTY)),
                darkstoreId = null,
                staffPartnerId = Constants.defaultStaffPartnerId,
                email = Constants.defaultEmail,
                vehicle = null,
                cityId = null
            )
            .setUpdateProfileRequest(
                employeePreconditions.createProfileRequest(),
                staffPartnerId = Constants.defaultStaffPartnerId,
                email = "supermail@mail.ru"
            )

        employeeActions
            .createProfile(employeePreconditions.createProfileRequest())
            .updateProfile(
                employeeActions.createdProfileResponse().profileId,
                employeePreconditions.updateProfileRequest()
            )
            .getProfileById(employeeActions.createdProfileResponse().profileId)
            .setProfile(databaseController.getProfile(employeeActions.createdProfileResponse().profileId))

        employeeAssertion
            .checkProfileRolesInDatabase(
                employeeActions.getProfile(),
                employeePreconditions.updateProfileRequest().roles
            )
            .checkEmailInDatabase(
                employeePreconditions.updateProfileRequest().email,
                employeeActions.getProfile()
            )
            .checkProfileVersion(
                2,
                employeeActions.getProfile()

            )
            .checkProfileLogInDatabase(
                databaseController.getProfileLogArray(employeeActions.createdProfileResponse().profileId),
                2,
                1,
                "update",
                employeePreconditions.updateProfileRequest().issuerProfileId,
                2
            )
            .checkUpdatedProfilesAreEquals(
                employeeActions.getProfileFromApi(),
                employeePreconditions.updateProfileRequest()
            )
            .checkEmployeeProfileChangedKafka(
                employeeActions.getMessageFromKafkaUpdate(employeeActions.createdProfileResponse().profileId),
                employeePreconditions.updateProfileRequest(),
                2
            )
            .checkEmployeeProfileChangedKafkaUpdatedEmail(
                employeeActions.getMessageFromKafkaUpdate(employeeActions.createdProfileResponse().profileId),
                employeePreconditions.updateProfileRequest()
            )

    }

    @Test
    @DisplayName("Update profile: update with the same data")
    fun updateProfileNoChangesTest() {
        employeePreconditions
            .setCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.PERSONAL_BICYCLE))
            )
            .setUpdateProfileRequest(
                employeePreconditions.createProfileRequest()
            )

        employeeActions
            .createProfile(employeePreconditions.createProfileRequest())
            .updateProfile(
                employeeActions.createdProfileResponse().profileId,
                employeePreconditions.updateProfileRequest()
            )
            .getProfileById(employeeActions.createdProfileResponse().profileId)
            .setProfile(databaseController.getProfile(employeeActions.createdProfileResponse().profileId))

        employeeAssertion
            .checkProfileVersion(
                1,
                employeeActions.getProfile()
            )
            .checkProfileLogInDatabase(
                databaseController.getProfileLogArray(employeeActions.createdProfileResponse().profileId),
                1,
                0,
                "creation",
                employeePreconditions.updateProfileRequest().issuerProfileId,
                1
            )
            .checkUpdatedProfilesAreEquals(
                employeeActions.getProfileFromApi(),
                employeePreconditions.updateProfileRequest()
            )
    }

    @Test
    @DisplayName("Update profile with the existing mobile is impossible")
    fun updateProfileExistingMobileTest() {
        employeePreconditions
            .setCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.PERSONAL_BICYCLE))
            )
            .setUpdateProfileRequest(
                employeePreconditions.createProfileRequest(),
                mobile = Constants.existedMobile
            )

        employeeActions
            .createProfile(employeePreconditions.createProfileRequest())
            .updateProfileUnsuccessful(
                employeeActions.createdProfileResponse().profileId,
                employeePreconditions.updateProfileRequest()
            )
            .setProfile(databaseController.getProfile(employeeActions.createdProfileResponse().profileId))

        employeeAssertion
            .checkStatusCodeConflict(employeeActions.getUpdateResponse().statusCode)
            .checkMobileInDatabase(
                employeePreconditions.createProfileRequest().mobile.asStringWithoutPlus(),
                employeeActions.getProfile()
            )
            .checkProfileVersion(
                1,
                employeeActions.getProfile()
            )
            .checkProfileLogInDatabase(
                databaseController.getProfileLogArray(employeeActions.createdProfileResponse().profileId),
                1,
                0,
                "creation",
                employeePreconditions.updateProfileRequest().issuerProfileId,
                1
            )
    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Update disabled profile is impossible")
    fun updateDeletedProfileTest() {
        employeePreconditions
            .setCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.PERSONAL_BICYCLE))
            )
            .setUpdateProfileRequest(
                employeePreconditions.createProfileRequest(),
                mobile = Constants.mobile2
            )

        employeeActions
            .createProfile(employeePreconditions.createProfileRequest())
            .deleteProfile(employeeActions.createdProfileResponse().profileId)
            .updateProfileUnsuccessful(
                employeeActions.createdProfileResponse().profileId,
                employeePreconditions.updateProfileRequest()
            )

        employeeAssertion
            .checkStatusNotFound(employeeActions.getUpdateResponse().statusCode)

    }

    @Test
    @DisplayName("Update CP profile with adding role is impossible")
    fun updateProfileCpMultipleRoleTest() {
        employeePreconditions
            .setCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.COUNTERPARTY)),
                darkstoreId = null,
                staffPartnerId = Constants.defaultStaffPartnerId,
                email = Constants.defaultEmail,
                vehicle = null,
                cityId = null
            )
            .setUpdateProfileRequest(
                employeePreconditions.createProfileRequest(),
                roles = listOf(ApiEnum(EmployeeRole.COUNTERPARTY), ApiEnum(EmployeeRole.PICKER))
            )

        employeeActions
            .createProfile(employeePreconditions.createProfileRequest())
            .updateProfileUnsuccessful(
                employeeActions.createdProfileResponse().profileId,
                employeePreconditions.updateProfileRequest()
            )

            .setProfile(databaseController.getProfile(employeeActions.createdProfileResponse().profileId))

        employeeAssertion
            .checkStatusBadRequest(employeeActions.getUpdateResponse().statusCode)
            .checkProfileRolesInDatabase(
                employeeActions.getProfile(),
                employeePreconditions.createProfileRequest().roles
            )

            .checkProfileVersion(
                1,
                employeeActions.getProfile()

            )
            .checkProfileLogInDatabase(
                databaseController.getProfileLogArray(employeeActions.createdProfileResponse().profileId),
                1,
                0,
                "creation",
                employeePreconditions.updateProfileRequest().issuerProfileId,
                1
            )
    }

    @Test
    @DisplayName("Update forwarder profile with vehicle != car is impossible")
    fun updateProfileForwarderVehicleTest() {
        employeePreconditions
            .setCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.FORWARDER)),
                staffPartnerId = null,
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR))
            )
            .setUpdateProfileRequest(
                employeePreconditions.createProfileRequest(),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.PERSONAL_BICYCLE))
            )

        employeeActions
            .createProfile(employeePreconditions.createProfileRequest())
            .updateProfileUnsuccessful(
                employeeActions.createdProfileResponse().profileId,
                employeePreconditions.updateProfileRequest()
            )

            .setProfile(databaseController.getProfile(employeeActions.createdProfileResponse().profileId))

        employeeAssertion
            .checkStatusBadRequest(employeeActions.getUpdateResponse().statusCode)
            .checkProfileRolesInDatabase(
                employeeActions.getProfile(),
                employeePreconditions.createProfileRequest().roles
            )

            .checkProfileVersion(
                1,
                employeeActions.getProfile()

            )
            .checkProfileLogInDatabase(
                databaseController.getProfileLogArray(employeeActions.createdProfileResponse().profileId),
                1,
                0,
                "creation",
                employeePreconditions.updateProfileRequest().issuerProfileId,
                1
            )

    }

    @Test
    @DisplayName("Update profile w/t mandatory field is impossible")
    fun updateProfileWithoutMandatoryFieldTest() {
        employeePreconditions
            .setCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.GOODS_MANAGER)),
                staffPartnerId = null,
                vehicle = null
            )
            .setUpdateProfileRequest(
                employeePreconditions.createProfileRequest(),
                roles = listOf(ApiEnum(EmployeeRole.PICKER))
            )

        employeeActions
            .createProfile(employeePreconditions.createProfileRequest())
            .updateProfileUnsuccessful(
                employeeActions.createdProfileResponse().profileId,
                employeePreconditions.updateProfileRequest()
            )

            .setProfile(databaseController.getProfile(employeeActions.createdProfileResponse().profileId))

        employeeAssertion
            .checkStatusBadRequest(employeeActions.getUpdateResponse().statusCode)
            .checkProfileRolesInDatabase(
                employeeActions.getProfile(),
                employeePreconditions.createProfileRequest().roles
            )

            .checkProfileVersion(
                1,
                employeeActions.getProfile()

            )
            .checkProfileLogInDatabase(
                databaseController.getProfileLogArray(employeeActions.createdProfileResponse().profileId),
                1,
                0,
                "creation",
                employeePreconditions.updateProfileRequest().issuerProfileId,
                1
            )


    }

    @Test
    @DisplayName("Update profile: accountingProfileId to darkstore admin")
    fun updateContractForDarkstoreAdminTest() {
        employeePreconditions
            .setCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DARKSTORE_ADMIN)),
                vehicle = null,
                staffPartnerId = null,
                accountingProfileId = UUID.randomUUID().toString()
            )
            .setUpdateProfileRequest(
                employeePreconditions.createProfileRequest(),
                accountingProfileId = UUID.randomUUID().toString()
            )

        employeeActions
            .createProfile(employeePreconditions.createProfileRequest())
            .updateProfile(
                employeeActions.createdProfileResponse().profileId,
                employeePreconditions.updateProfileRequest()
            )
            .getProfileById(employeeActions.createdProfileResponse().profileId)
            .setProfile(databaseController.getProfile(employeeActions.createdProfileResponse().profileId))

        employeeAssertion
            .checkProfileAccountingIdInDatabase(
                employeePreconditions.updateProfileRequest().accountingProfileId!!,
                employeeActions.getProfile()
            )
    }

    @Test
    @DisplayName("Update profile: accountingProfileId to goods manager")
    fun updateContractForGoodsManagerTest() {
        employeePreconditions
            .setCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.GOODS_MANAGER)),
                vehicle = null,
                staffPartnerId = null,
                accountingProfileId = UUID.randomUUID().toString()
            )
            .setUpdateProfileRequest(
                employeePreconditions.createProfileRequest(),
                accountingProfileId = UUID.randomUUID().toString()
            )

        employeeActions
            .createProfile(employeePreconditions.createProfileRequest())
            .updateProfile(
                employeeActions.createdProfileResponse().profileId,
                employeePreconditions.updateProfileRequest()
            )
            .getProfileById(employeeActions.createdProfileResponse().profileId)
            .setProfile(databaseController.getProfile(employeeActions.createdProfileResponse().profileId))

        employeeAssertion
            .checkProfileAccountingIdInDatabase(
                employeePreconditions.updateProfileRequest().accountingProfileId!!,
                employeeActions.getProfile()
            )
    }

    @Test
    @DisplayName("Update profile role: from admin to supervisor with accountingProfileId")
    fun updateRoleFromAdminToSupervisorWithAccountingProfileIdTest() {
        employeePreconditions
            .setCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DARKSTORE_ADMIN)),
                vehicle = null,
                staffPartnerId = null,
                accountingProfileId = StringAndPhoneNumberGenerator.generateRandomInn(),
            )
            .setUpdateProfileRequest(
                employeePreconditions.createProfileRequest(),
                darkstoreId = null,
                roles = listOf(ApiEnum(EmployeeRole.SUPERVISOR)),
                cityId = null
            )

        employeeActions
            .createProfile(employeePreconditions.createProfileRequest())
            .updateProfile(
                employeeActions.createdProfileResponse().profileId,
                employeePreconditions.updateProfileRequest()
            )
            .getProfileById(employeeActions.createdProfileResponse().profileId)
            .setProfile(databaseController.getProfile(employeeActions.createdProfileResponse().profileId))

        employeeAssertion
            .checkProfileAccountingIdInDatabase(
                employeePreconditions.updateProfileRequest().accountingProfileId!!,
                employeeActions.getProfile()
            )
    }

    @Test
    @DisplayName("Update profile: from supervisor to goods manager with accountingProfileId")
    fun updateRoleFromSupervisorToGoodsManagerWithAccountingProfileIdTest() {

        employeePreconditions
            .setCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.SUPERVISOR)),
                vehicle = null,
                staffPartnerId = null,
                accountingProfileId = StringAndPhoneNumberGenerator.generateRandomInn(),
                darkstoreId = null,
                cityId = null
            )
            .setUpdateProfileRequest(
                employeePreconditions.createProfileRequest(),
                darkstoreId = Constants.darkstoreId,
                roles = listOf(ApiEnum(EmployeeRole.GOODS_MANAGER))
            )

        employeeActions
            .createProfile(employeePreconditions.createProfileRequest())
            .updateProfile(
                employeeActions.createdProfileResponse().profileId,
                employeePreconditions.updateProfileRequest()
            )
            .getProfileById(employeeActions.createdProfileResponse().profileId)
            .setProfile(databaseController.getProfile(employeeActions.createdProfileResponse().profileId))

        employeeAssertion
            .checkProfileAccountingIdInDatabase(
                employeePreconditions.updateProfileRequest().accountingProfileId!!,
                employeeActions.getProfile()
            )
    }

    @Test
    @DisplayName("Update name+mobile for profile with internship")
    fun updateProfileWithInternshipTest() {
        employeePreconditions
            .setCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.PERSONAL_BICYCLE))
            )
            .setCreateInternshipRequest(
                employeePreconditions.createProfileRequest().darkstoreId!!,
                plannedDate = Instant.now().plusSeconds(60).truncatedTo(ChronoUnit.SECONDS)
            )
            .setUpdateProfileRequest(
                employeePreconditions.createProfileRequest(),
                name = EmployeeName("Sasha", "Petrova", "Alekseevna"),
                mobile = Constants.mobile2
            )

        employeeActions
            .createProfile(employeePreconditions.createProfileRequest())
            .createInternship(
                employeeActions.createdProfileResponse().profileId,
                DarkstoreUserRole.DELIVERYMAN,
                employeePreconditions.createInternshipRequest()
            )
            .updateProfile(
                employeeActions.createdProfileResponse().profileId,
                employeePreconditions.updateProfileRequest()
            )
            .getProfileById(employeeActions.createdProfileResponse().profileId)
            .setProfile(databaseController.getProfile(employeeActions.createdProfileResponse().profileId))
            .getInternship(employeeActions.createdProfileResponse().profileId)
            .setInternship(databaseController.getInternship(employeeActions.createdProfileResponse().profileId))

        employeeAssertion
            .checkUpdatedProfilesAreEquals(
                employeeActions.getProfileFromApi(),
                employeePreconditions.updateProfileRequest()
            )
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


    }

    @Test
    @DisplayName("Update darkstore profile with planned internship")
    fun updateProfileWithPlannedInternshipTest() {
        employeePreconditions
            .setCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.PERSONAL_BICYCLE))
            )
            .setCreateInternshipRequest(
                employeePreconditions.createProfileRequest().darkstoreId!!,
                plannedDate = Instant.now().plusSeconds(60).truncatedTo(ChronoUnit.SECONDS)
            )
            .setUpdateProfileRequest(
                employeePreconditions.createProfileRequest(),
                darkstoreId = Constants.updatedDarkstoreId
            )

        employeeActions
            .createProfile(employeePreconditions.createProfileRequest())
            .createInternship(
                employeeActions.createdProfileResponse().profileId,
                DarkstoreUserRole.DELIVERYMAN,
                employeePreconditions.createInternshipRequest()
            )
            .updateProfile(
                employeeActions.createdProfileResponse().profileId,
                employeePreconditions.updateProfileRequest()
            )
            .getProfileById(employeeActions.createdProfileResponse().profileId)
            .setProfile(databaseController.getProfile(employeeActions.createdProfileResponse().profileId))
            .getInternship(employeeActions.createdProfileResponse().profileId)
            .setInternship(databaseController.getInternship(employeeActions.createdProfileResponse().profileId))

        employeeAssertion
            .checkUpdatedProfilesAreEquals(
                employeeActions.getProfileFromApi(),
                employeePreconditions.updateProfileRequest()
            )
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
                employeePreconditions.updateProfileRequest().darkstoreId!!,
                4
            )


    }

    @Test
    @DisplayName("Update darkstore profile with cancelled internship")
    fun updateProfileWithCancelledInternshipTest() {
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
            .setUpdateProfileRequest(
                employeePreconditions.createProfileRequest(),
                darkstoreId = Constants.updatedDarkstoreId
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
            .updateProfile(
                employeeActions.createdProfileResponse().profileId,
                employeePreconditions.updateProfileRequest()
            )
            .getProfileById(employeeActions.createdProfileResponse().profileId)
            .setProfile(databaseController.getProfile(employeeActions.createdProfileResponse().profileId))
            .getInternship(employeeActions.createdProfileResponse().profileId)
            .setInternship(databaseController.getInternship(employeeActions.createdProfileResponse().profileId))

        employeeAssertion
            .checkUpdatedProfilesAreEquals(
                employeeActions.getProfileFromApi(),
                employeePreconditions.updateProfileRequest()
            )
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
            .checkDSUserInDatabase(
                databaseController.getDSUserProfile(
                    employeeActions.createdProfileResponse().profileId,
                    employeePreconditions.createProfileRequest().roles[0].value
                ),
                employeePreconditions.updateProfileRequest().darkstoreId!!,
                5
            )


    }

    @Test
    @DisplayName("Update darkstore profile with rejected internship")
    fun updateProfileWithRejectedInternshipTest() {
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
            .setUpdateProfileRequest(
                employeePreconditions.createProfileRequest(),
                darkstoreId = Constants.updatedDarkstoreId
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
            .updateProfile(
                employeeActions.createdProfileResponse().profileId,
                employeePreconditions.updateProfileRequest()
            )
            .getProfileById(employeeActions.createdProfileResponse().profileId)
            .setProfile(databaseController.getProfile(employeeActions.createdProfileResponse().profileId))
            .getInternship(employeeActions.createdProfileResponse().profileId)
            .setInternship(databaseController.getInternship(employeeActions.createdProfileResponse().profileId))

        employeeAssertion
            .checkUpdatedProfilesAreEquals(
                employeeActions.getProfileFromApi(),
                employeePreconditions.updateProfileRequest()
            )
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
            .checkDSUserInDatabase(
                databaseController.getDSUserProfile(
                    employeeActions.createdProfileResponse().profileId,
                    employeePreconditions.createProfileRequest().roles[0].value
                ),
                employeePreconditions.updateProfileRequest().darkstoreId!!,
                5
            )


    }

    @Test
    @DisplayName("Update darkstore profile with done internship")
    fun updateProfileWithDoneInternshipTest() {

        employeePreconditions
            .setCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.PERSONAL_BICYCLE))
            )
            .setCreateInternshipRequest(
                employeePreconditions.createProfileRequest().darkstoreId!!,
                plannedDate = Instant.now().plusSeconds(3).truncatedTo(ChronoUnit.SECONDS)
            )
            .setCloseInternshipRequest(status = ApiEnum(InternshipStatus.DONE))
            .setUpdateProfileRequest(
                employeePreconditions.createProfileRequest(),
                darkstoreId = Constants.updatedDarkstoreId
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
            .updateProfile(
                employeeActions.createdProfileResponse().profileId,
                employeePreconditions.updateProfileRequest()
            )
            .getProfileById(employeeActions.createdProfileResponse().profileId)
            .setProfile(databaseController.getProfile(employeeActions.createdProfileResponse().profileId))
            .getInternship(employeeActions.createdProfileResponse().profileId)
            .setInternship(databaseController.getInternship(employeeActions.createdProfileResponse().profileId))

        employeeAssertion
            .checkUpdatedProfilesAreEquals(
                employeeActions.getProfileFromApi(),
                employeePreconditions.updateProfileRequest()
            )
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
            .checkDSUserInDatabase(
                databaseController.getDSUserProfile(
                    employeeActions.createdProfileResponse().profileId,
                    employeePreconditions.createProfileRequest().roles[0].value
                ),
                employeePreconditions.updateProfileRequest().darkstoreId!!,
                6
            )


    }

    @Test
    @DisplayName("Update darkstore profile with failed internship")
    fun updateProfileWithFailedInternshipTest() {

        employeePreconditions
            .setCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.PERSONAL_BICYCLE))
            )
            .setCreateInternshipRequest(
                employeePreconditions.createProfileRequest().darkstoreId!!,
                plannedDate = Instant.now().plusSeconds(3).truncatedTo(ChronoUnit.SECONDS)
            )
            .setCloseInternshipRequest((ApiEnum(FailureCode.F001)), status = ApiEnum(InternshipStatus.FAILED))
            .setUpdateProfileRequest(
                employeePreconditions.createProfileRequest(),
                darkstoreId = Constants.updatedDarkstoreId
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
            .updateProfile(
                employeeActions.createdProfileResponse().profileId,
                employeePreconditions.updateProfileRequest()
            )
            .getProfileById(employeeActions.createdProfileResponse().profileId)
            .setProfile(databaseController.getProfile(employeeActions.createdProfileResponse().profileId))
            .getInternship(employeeActions.createdProfileResponse().profileId)
            .setInternship(databaseController.getInternship(employeeActions.createdProfileResponse().profileId))

        employeeAssertion
            .checkUpdatedProfilesAreEquals(
                employeeActions.getProfileFromApi(),
                employeePreconditions.updateProfileRequest()
            )
            .checkInternshipFromDB(
                employeePreconditions.createInternshipRequest().darkstoreId,
                "deliveryman",
                "done",
                employeeActions.getInternship()
            )
            .checkInternshipLogFromDB(
                databaseController.getInternshipLogArray(employeeActions.createdProfileResponse().profileId),
                3,
                2,
                "done"
            )
            .checkInternshipAPIResponse(
                employeeActions.getCreatedInternship(),
                employeePreconditions.createInternshipRequest(),
                ApiEnum(InternshipStatus.DONE)
            )
            .checkDSUserInDatabase(
                databaseController.getDSUserProfile(
                    employeeActions.createdProfileResponse().profileId,
                    employeePreconditions.createProfileRequest().roles[0].value
                ),
                employeePreconditions.updateProfileRequest().darkstoreId!!,
                6
            )


    }

    @Test
    @DisplayName("Update role profile with internship: delete role with internship")
    fun updateProfileDeleteRoleWithInternshipTest() {

        employeePreconditions
            .setCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.PERSONAL_BICYCLE))
            )
            .setCreateInternshipRequest(
                employeePreconditions.createProfileRequest().darkstoreId!!,
                plannedDate = Instant.now().plusSeconds(3).truncatedTo(ChronoUnit.SECONDS)
            )

            .setUpdateProfileRequest(
                employeePreconditions.createProfileRequest(),
                roles = listOf(ApiEnum(EmployeeRole.PICKER)),
                vehicle = null
            )


        employeeActions
            .createProfile(employeePreconditions.createProfileRequest())
            .createInternship(
                employeeActions.createdProfileResponse().profileId,
                DarkstoreUserRole.DELIVERYMAN,
                employeePreconditions.createInternshipRequest()
            )
            .updateProfile(
                employeeActions.createdProfileResponse().profileId,
                employeePreconditions.updateProfileRequest()
            )
            .getProfileById(employeeActions.createdProfileResponse().profileId)
            .setProfile(databaseController.getProfile(employeeActions.createdProfileResponse().profileId))


        employeeAssertion
            .checkInternshipNotInDatabase(databaseController.checkInternshipExists(employeeActions.createdProfileResponse().profileId))
            .checkInternStatusForDSUser(
                employeeActions.getDarkstoreUserById(
                    employeeActions.createdProfileResponse().profileId,
                    employeePreconditions.createProfileRequest().darkstoreId!!,
                    DarkstoreUserRole.PICKER,
                ), false
            )
    }

    @Test
    @DisplayName("Update role profile with internship: add role with internship")
    fun updateProfileAddRoleWithInternshipTest() {

        employeePreconditions
            .setCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.PERSONAL_BICYCLE))
            )
            .setCreateInternshipRequest(
                employeePreconditions.createProfileRequest().darkstoreId!!,
                plannedDate = Instant.now().plusSeconds(3).truncatedTo(ChronoUnit.SECONDS)
            )
            .setUpdateProfileRequest(
                employeePreconditions.createProfileRequest(),
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN), ApiEnum(EmployeeRole.PICKER))
            )

        employeeActions
            .createProfile(employeePreconditions.createProfileRequest())
            .createInternship(
                employeeActions.createdProfileResponse().profileId,
                DarkstoreUserRole.DELIVERYMAN,
                employeePreconditions.createInternshipRequest()
            )
            .updateProfile(
                employeeActions.createdProfileResponse().profileId,
                employeePreconditions.updateProfileRequest()
            )
            .createInternshipUnsuccessful(
                employeeActions.createdProfileResponse().profileId,
                DarkstoreUserRole.PICKER,
                employeePreconditions.createInternshipRequest()
            )
            .getProfileById(employeeActions.createdProfileResponse().profileId)
            .setProfile(databaseController.getProfile(employeeActions.createdProfileResponse().profileId))
            .getInternship(employeeActions.createdProfileResponse().profileId)
            .setInternship(databaseController.getInternship(employeeActions.createdProfileResponse().profileId))


        employeeAssertion
            .checkStatusBadRequest(employeeActions.getCreateInternshipResponse().statusCode)
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
    @Tag("kafka_consume")
    @DisplayName("Update profile: darkstore without city impossible")
    fun updateProfileDarkstoreWithoutCityTest() {
        employeePreconditions
            .setCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.PERSONAL_BICYCLE)),
                cityId = Constants.cityId
            )
            .setUpdateProfileRequest(
                employeePreconditions.createProfileRequest(),
                darkstoreId = Constants.updatedDarkstoreId,
                cityId = null

            )

        employeeActions
            .createProfile(employeePreconditions.createProfileRequest())
            .updateProfileUnsuccessful(
                employeeActions.createdProfileResponse().profileId,
                employeePreconditions.updateProfileRequest()
            )
            .getProfileById(employeeActions.createdProfileResponse().profileId)
            .setProfile(databaseController.getProfile(employeeActions.createdProfileResponse().profileId))


        employeeAssertion
            .checkStatusBadRequest(employeeActions.getUpdateResponse().statusCode)
            .checkDarkstoreIdInDatabase(
                employeePreconditions.createProfileRequest().darkstoreId!!,
                employeeActions.getProfile()
            )
            .checkProfileVersion(
                1,
                employeeActions.getProfile()
            )
            .checkProfileLogInDatabase(
                databaseController.getProfileLogArray(employeeActions.createdProfileResponse().profileId),
                1,
                0,
                "creation",
                employeePreconditions.updateProfileRequest().issuerProfileId,
                1
            )
    }
}