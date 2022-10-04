package ru.samokat.mysamokat.tests.tests.employee_profiles.profilesAPI

import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.samokat.employeeprofiles.api.common.domain.EmployeeVehicleType
import ru.samokat.employeeprofiles.api.common.domain.Vehicle
import ru.samokat.my.domain.employee.EmployeeName
import ru.samokat.my.domain.employee.EmployeeRole

import ru.samokat.my.rest.api.enum.ApiEnum
import ru.samokat.mysamokat.tests.checkers.CommonAssertion
import ru.samokat.mysamokat.tests.checkers.EmployeeAssertion
import ru.samokat.mysamokat.tests.dataproviders.Constants
import ru.samokat.mysamokat.tests.dataproviders.StringAndPhoneNumberGenerator
import ru.samokat.mysamokat.tests.dataproviders.helpers.database.employee_profiles_backend.Profile
import ru.samokat.mysamokat.tests.dataproviders.helpers.database.employee_profiles_backend.ProfileRequisition
import ru.samokat.mysamokat.tests.dataproviders.preconditions.CommonPreconditions
import ru.samokat.mysamokat.tests.dataproviders.preconditions.EmployeePreconditions
import ru.samokat.mysamokat.tests.helpers.actions.EmployeeActions
import ru.samokat.mysamokat.tests.helpers.controllers.asClientError
import ru.samokat.mysamokat.tests.helpers.controllers.database.EmployeeProfilesDatabaseController
import java.util.*

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Tag("empro")
class CreateProfile {


    private lateinit var employeeAssertion: EmployeeAssertion
    private lateinit var commonAssertion: CommonAssertion

    @Autowired
    private lateinit var employeeActions: EmployeeActions

    private lateinit var employeePreconditions: EmployeePreconditions

    @Autowired
    private lateinit var databaseController: EmployeeProfilesDatabaseController

    private var staffPartnerId: UUID? = null

    @Autowired
    private lateinit var commonPreconditions: CommonPreconditions

    @BeforeEach
    fun before() {
        employeePreconditions = EmployeePreconditions()
        employeeAssertion = EmployeeAssertion()
        commonAssertion = CommonAssertion()
        employeeActions.deleteProfile(Constants.mobile1)
        employeeActions.deleteProfile(Constants.mobile2)
        employeeActions.deleteProfileByAccountingProfileId(Constants.accountingProfileIdForRequisitions.toString())
        employeeActions.deleteRequisitionFromDatabase(Constants.accountingProfileIdForRequisitions.toString())
        employeeActions.deleteRequisitionFromDatabase(Constants.innForRequisitions)
    }

    @AfterEach
    fun release() {
        employeeAssertion.assertAll()
        employeeActions.deleteProfile(Constants.mobile1)
        employeeActions.deleteProfile(Constants.mobile2)
        employeeActions.deleteProfileByAccountingProfileId(Constants.accountingProfileIdForRequisitions.toString())
        employeeActions.deleteRequisitionFromDatabase(Constants.accountingProfileIdForRequisitions.toString())
        employeeActions.deleteRequisitionFromDatabase(Constants.innForRequisitions)
    }

    fun getStaffPartner() {
        val getPartnersRequest = employeePreconditions.fillGetStaffPartnersRequest()
        staffPartnerId = employeeActions.getStaffPartners(getPartnersRequest)[0].partnerId
    }

    @Test
    @Tags(Tag("smoke"), Tag("kafka_consume"))
    @DisplayName("Create deliveryman without vehicle (OLD)")
    fun createProfileDeliverymanWithoutVehicle() {

        getStaffPartner()
        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.NONE)),
                email = null,
                name = EmployeeName("Test", "Test", "Test"),
                staffPartnerId = staffPartnerId
            )

        val createdProfileId = employeeActions.createProfileId(createRequest)

        val profileFromApi = employeeActions.getApiProfileById(createdProfileId)
        val profileFromDB = employeeActions.getProfileFromDB(createdProfileId)
        val vehicleFromDB = employeeActions.getVehicleFromDatabase(createdProfileId)
        val profilesFromProfileLogDB = employeeActions.getProfilesFromProfilesLogTable(createdProfileId)

        val profileFromDarkstoreDB =
            employeeActions.getProfilesFromDSUserTable(createdProfileId, EmployeeRole.DELIVERYMAN)
        val profileFromDarkstoreLogDB =
            employeeActions.getProfilesFromDSUserLogTable(createdProfileId, EmployeeRole.DELIVERYMAN)[0]
        val profileFromDarkstoreActivityDB =
            employeeActions.getProfilesFromDSActivityTable(createdProfileId, EmployeeRole.DELIVERYMAN)

        val messageInProfileCreated = employeeActions.getMessageFromKafkaCreated(createdProfileId)

        employeeAssertion
            .checkProfilesAreEquals(profileFromApi, createRequest)

            .checkProfileStatusIsEnabled(profileFromApi)

            .checkProfileFromProfileDB(createRequest, profileFromDB)
            .checkVehicleIsEmpty(vehicleFromDB)

            .checkTwoDatesAreEqual(profileFromApi.createdAt, profileFromDB[Profile.createdAt])
            .checkTwoDatesAreEqual(profileFromApi.updatedAt, profileFromDB[Profile.updatedAt])
            .checkTwoDatesAreEqual(profileFromApi.updatedAt, profileFromApi.createdAt)

            .checkNewUserInProfileLogTable(profilesFromProfileLogDB)

            .checkNewProfileFromDSUserTable(createRequest, profileFromDarkstoreDB, EmployeeRole.DELIVERYMAN)
            .checkNewProfileFromDSUserLogTable(createRequest, profileFromDarkstoreLogDB, EmployeeRole.DELIVERYMAN)
            .checkNewProfileFromDSUserActivityTable(
                createRequest,
                profileFromDarkstoreActivityDB,
                EmployeeRole.DELIVERYMAN
            )

            .checkEmployeeProfileCreatedKafka(messageInProfileCreated, createRequest)
    }

    @Test
    @DisplayName("Create deliveryman with company bicycle (OLD)")
    fun createProfileDeliverymanWithCompanyBicycle() {

        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.COMPANY_BICYCLE)),
                email = null
            )

        val createdProfileId = employeeActions.createProfileId(createRequest)
        val vehicleFromDB = employeeActions.getVehicleFromDatabase(createdProfileId)

        employeeAssertion
            .checkDeliverymanVehicleInDB(EmployeeVehicleType.COMPANY_BICYCLE.toString(), vehicleFromDB)
    }

    @Test
    @DisplayName("Create deliveryman with personal bicycle (OLD)")
    fun createProfileDeliverymanWithPersonalBicycle() {

        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.PERSONAL_BICYCLE)),
                email = null
            )

        val createdProfileId = employeeActions.createProfileId(createRequest)
        val vehicleFromDB = employeeActions.getVehicleFromDatabase(createdProfileId)

        employeeAssertion
            .checkDeliverymanVehicleInDB(EmployeeVehicleType.PERSONAL_BICYCLE.toString(), vehicleFromDB)
    }

    @Test
    @DisplayName("Create deliveryman with auto (NEW)")
    fun createProfileDeliverymanWithCar() {
        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
                email = null
            )

        val createdProfileId = employeeActions.createProfileId(createRequest)
        val vehicleFromDB = employeeActions.getVehicleFromDatabase(createdProfileId)

        employeeAssertion
            .checkDeliverymanVehicleInDB(EmployeeVehicleType.CAR.toString(), vehicleFromDB)
    }

    @Test
    @Tags(Tag("smoke"), Tag("kafka_consume"))
    @DisplayName("Create deliveryman with bicycle (NEW)")
    fun createProfileDeliverymanWithBicycle() {

        getStaffPartner()
        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.BICYCLE)),
                email = null,
                name = EmployeeName("Test", "Test", "Test"),
                staffPartnerId = staffPartnerId
            )

        val createdProfileId = employeeActions.createProfileId(createRequest)

        val profileFromApi = employeeActions.getApiProfileById(createdProfileId)
        val profileFromDB = employeeActions.getProfileFromDB(createdProfileId)
        val vehicleFromDB = employeeActions.getVehicleFromDatabase(createdProfileId)
        val profilesFromProfileLogDB = employeeActions.getProfilesFromProfilesLogTable(createdProfileId)

        val profileFromDarkstoreDB =
            employeeActions.getProfilesFromDSUserTable(createdProfileId, EmployeeRole.DELIVERYMAN)
        val profileFromDarkstoreLogDB =
            employeeActions.getProfilesFromDSUserLogTable(createdProfileId, EmployeeRole.DELIVERYMAN)[0]
        val profileFromDarkstoreActivityDB =
            employeeActions.getProfilesFromDSActivityTable(createdProfileId, EmployeeRole.DELIVERYMAN)

        val messageInProfileCreated = employeeActions.getMessageFromKafkaCreated(createdProfileId)

        employeeAssertion
            .checkProfilesAreEquals(profileFromApi, createRequest)

            .checkProfileStatusIsEnabled(profileFromApi)

            .checkProfileFromProfileDB(createRequest, profileFromDB)
            .checkDeliverymanVehicleInDB(EmployeeVehicleType.BICYCLE.toString(), vehicleFromDB)

            .checkTwoDatesAreEqual(profileFromApi.createdAt, profileFromDB[Profile.createdAt])
            .checkTwoDatesAreEqual(profileFromApi.updatedAt, profileFromDB[Profile.updatedAt])
            .checkTwoDatesAreEqual(profileFromApi.updatedAt, profileFromApi.createdAt)

            .checkNewUserInProfileLogTable(profilesFromProfileLogDB)

            .checkNewProfileFromDSUserTable(createRequest, profileFromDarkstoreDB, EmployeeRole.DELIVERYMAN)
            .checkNewProfileFromDSUserLogTable(createRequest, profileFromDarkstoreLogDB, EmployeeRole.DELIVERYMAN)
            .checkNewProfileFromDSUserActivityTable(
                createRequest,
                profileFromDarkstoreActivityDB,
                EmployeeRole.DELIVERYMAN
            )

            .checkEmployeeProfileCreatedKafka(messageInProfileCreated, createRequest)
    }

    @Test
    @DisplayName("Create deliveryman with moto (NEW)")
    fun createProfileDeliverymanWithMoto() {
        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.MOTOCYCLE)),
                email = null
            )

        val createdProfileId = employeeActions.createProfileId(createRequest)
        val vehicleFromDB = employeeActions.getVehicleFromDatabase(createdProfileId)

        employeeAssertion
            .checkDeliverymanVehicleInDB(EmployeeVehicleType.MOTOCYCLE.toString(), vehicleFromDB)
    }

    @Test
    @DisplayName("Create deliveryman with electric bicycle (NEW)")
    fun createProfileDeliverymanWithElectricBicycle() {
        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.ELECTRIC_BICYCLE)),
                email = null
            )

        val createdProfileId = employeeActions.createProfileId(createRequest)
        val vehicleFromDB = employeeActions.getVehicleFromDatabase(createdProfileId)

        employeeAssertion
            .checkDeliverymanVehicleInDB(EmployeeVehicleType.ELECTRIC_BICYCLE.toString(), vehicleFromDB)
    }


    @Test
    @DisplayName("Create deliveryman with empty vehicle")
    fun createProfileDeliverymanWithEmptyVehicle() {

        val createBuilder = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = null,
                email = null
            )

        employeeActions.createProfileWithError(createBuilder)

        employeeAssertion.getSoftAssertion()
            .assertThat(
                databaseController.checkActiveProfileExistsByMobile(
                    createBuilder.mobile.asStringWithoutPlus()
                )
            )
            .isFalse
    }

    @Test
    @DisplayName("Create deliveryman without darkstore")
    fun createProfileDeliverymanWithoutDarkstore() {

        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                email = null,
                darkstoreId = null
            )

        employeeActions.createProfileWithError(createRequest)

        employeeAssertion.getSoftAssertion()
            .assertThat(
                databaseController.checkActiveProfileExistsByMobile(
                    createRequest.mobile.asStringWithoutPlus()
                )
            )
            .isFalse

    }

    @Test
    @DisplayName("Create deliveryman without city")
    fun createProfileDeliverymanWithoutCity() {

        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                email = null,
                cityId = null
            )

        employeeActions.createProfileWithError(createRequest)

        employeeAssertion.getSoftAssertion()
            .assertThat(
                databaseController.checkActiveProfileExistsByMobile(
                    createRequest.mobile.asStringWithoutPlus()
                )
            )
            .isFalse

    }

    @Test
    @DisplayName("Create deliveryman without middle name")
    fun createProfileDeliverymanWithoutMiddleName() {

        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
                email = null,
                name = EmployeeName(
                    firstName = StringAndPhoneNumberGenerator.generateRandomString(10),
                    lastName = StringAndPhoneNumberGenerator.generateRandomString(10)
                )
            )

        val createdProfileId = employeeActions.createProfileId(createRequest)
        val profileFromDB = employeeActions.getProfileFromDB(createdProfileId)

        employeeAssertion
            .checkMiddleNameIsNull(profileFromDB)
    }

    @Test
    @Tags(Tag("smoke"), Tag("kafka_consume"))
    @DisplayName("Create picker")
    fun createProfilePicker() {

        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.PICKER)),
                vehicle = null,
                email = null
            )

        val createdProfileId = employeeActions.createProfileId(createRequest)
        val profileFromApi = employeeActions.getApiProfileById(createdProfileId)

        val profileFromDB = employeeActions.getProfileFromDB(createdProfileId)
        val vehicleFromDB = employeeActions.getVehicleFromDatabase(createdProfileId)
        val profilesFromProfileLogDB = employeeActions.getProfilesFromProfilesLogTable(createdProfileId)

        val profileFromDarkstoreDB = employeeActions.getProfilesFromDSUserTable(createdProfileId, EmployeeRole.PICKER)
        val profileFromDarkstoreLogDB =
            employeeActions.getProfilesFromDSUserLogTable(createdProfileId, EmployeeRole.PICKER)[0]
        val profileFromDarkstoreActivityDB =
            employeeActions.getProfilesFromDSActivityTable(createdProfileId, EmployeeRole.PICKER)

        val messageInProfileCreated = employeeActions.getMessageFromKafkaCreated(createdProfileId)

        employeeAssertion
            .checkProfilesAreEquals(profileFromApi, createRequest)
            .checkProfileStatusIsEnabled(profileFromApi)

            .checkProfileFromProfileDB(createRequest, profileFromDB)
            .checkVehicleIsEmpty(vehicleFromDB)

            .checkNewUserInProfileLogTable(profilesFromProfileLogDB)

            .checkNewProfileFromDSUserTable(createRequest, profileFromDarkstoreDB, EmployeeRole.PICKER)
            .checkNewProfileFromDSUserLogTable(createRequest, profileFromDarkstoreLogDB, EmployeeRole.PICKER)
            .checkNewProfileFromDSUserActivityTable(createRequest, profileFromDarkstoreActivityDB, EmployeeRole.PICKER)

            .checkEmployeeProfileCreatedKafka(messageInProfileCreated, createRequest)

    }

    @Test
    @DisplayName("Create piker without darkstore")
    fun createProfilePickerWithoutDarkstore() {
        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.PICKER)),
                vehicle = null,
                email = null,
                darkstoreId = null
            )

        employeeActions.createProfileWithError(createRequest)

        employeeAssertion.getSoftAssertion()
            .assertThat(
                databaseController.checkActiveProfileExistsByMobile(
                    createRequest.mobile.asStringWithoutPlus()
                )
            )
            .isFalse

    }

    @Test
    @DisplayName("Create picker without city")
    fun createProfilePickerWithoutCity() {

        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.PICKER)),
                email = null,
                vehicle = null,
                cityId = null
            )

        employeeActions.createProfileWithError(createRequest)

        employeeAssertion.getSoftAssertion()
            .assertThat(
                databaseController.checkActiveProfileExistsByMobile(
                    createRequest.mobile.asStringWithoutPlus()
                )
            )
            .isFalse

    }

    @Test
    @Tags(Tag("smoke"), Tag("kafka_consume"))
    @DisplayName("Create goods manager")
    fun createProfileGoodsManager() {

        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.GOODS_MANAGER)),
                vehicle = null,
                email = null,
                staffPartnerId = null
            )

        val createdProfileId = employeeActions.createProfileId(createRequest)
        val profileFromApi = employeeActions.getApiProfileById(createdProfileId)
        val profileFromDB = employeeActions.getProfileFromDB(createdProfileId)
        val profilesFromProfileLogDB = employeeActions.getProfilesFromProfilesLogTable(createdProfileId)

        val messageInProfileCreated = employeeActions.getMessageFromKafkaCreated(createdProfileId)

        employeeAssertion
            .checkProfilesAreEquals(profileFromApi, createRequest)
            .checkProfileStatusIsEnabled(profileFromApi)

            .checkProfileFromProfileDB(createRequest, profileFromDB)
            .checkNewUserInProfileLogTable(profilesFromProfileLogDB)

            .checkEmployeeProfileCreatedKafka(messageInProfileCreated, createRequest)

        employeeAssertion.getSoftAssertion().assertThat(databaseController.checkDarkstoreUserExists(createdProfileId))
            .isFalse
        employeeAssertion.getSoftAssertion()
            .assertThat(databaseController.checkDarkstoreUserLogExists(createdProfileId))
            .isFalse
        employeeAssertion.getSoftAssertion()
            .assertThat(databaseController.checkDarkstoreUserActivityExists(createdProfileId))
            .isFalse
    }

    @Test
    @DisplayName("Create goods manager without darkstore")
    fun createProfileGoodsManagerWithoutDarkstore() {

        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.PICKER)),
                vehicle = null,
                email = null,
                darkstoreId = null,
                staffPartnerId = null
            )

        employeeActions.createProfileWithError(createRequest)

        employeeAssertion.getSoftAssertion()
            .assertThat(
                databaseController.checkActiveProfileExistsByMobile(
                    createRequest.mobile.asStringWithoutPlus()
                )
            )
            .isFalse

    }

    @Test
    @DisplayName("Create goods manager without city")
    fun createProfileGoodsManagerWithoutCity() {

        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.PICKER)),
                vehicle = null,
                email = null,
                cityId = null,
                staffPartnerId = null
            )

        employeeActions.createProfileWithError(createRequest)

        employeeAssertion.getSoftAssertion()
            .assertThat(
                databaseController.checkActiveProfileExistsByMobile(
                    createRequest.mobile.asStringWithoutPlus()
                )
            )
            .isFalse

    }
    @Test
    @Tags(Tag("smoke"), Tag("kafka_consume"))
    @DisplayName("Create forwarder")
    fun createProfileForwarder() {

        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.FORWARDER)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
                email = null,
                staffPartnerId = null
            )

        val createdProfileId = employeeActions.createProfileId(createRequest)
        val profileFromApi = employeeActions.getApiProfileById(createdProfileId)
        val profileFromDB = employeeActions.getProfileFromDB(createdProfileId)
        val profilesFromProfileLogDB = employeeActions.getProfilesFromProfilesLogTable(createdProfileId)
        val vehicleFromDB = employeeActions.getVehicleFromDatabase(createdProfileId)

        val messageInProfileCreated = employeeActions.getMessageFromKafkaCreated(createdProfileId)

        employeeAssertion
            .checkProfilesAreEquals(profileFromApi, createRequest)
            .checkProfileStatusIsEnabled(profileFromApi)

            .checkProfileFromProfileDB(createRequest, profileFromDB)
            .checkDeliverymanVehicleInDB(EmployeeVehicleType.CAR.toString(), vehicleFromDB)
            .checkNewUserInProfileLogTable(profilesFromProfileLogDB)

            .checkEmployeeProfileCreatedKafka(messageInProfileCreated, createRequest)

        employeeAssertion.getSoftAssertion().assertThat(databaseController.checkDarkstoreUserExists(createdProfileId))
            .isFalse
        employeeAssertion.getSoftAssertion()
            .assertThat(databaseController.checkDarkstoreUserLogExists(createdProfileId))
            .isFalse
        employeeAssertion.getSoftAssertion()
            .assertThat(databaseController.checkDarkstoreUserActivityExists(createdProfileId))
            .isFalse

    }

    @Test
    @DisplayName("Create forwarder without car")
    fun createProfileForwarderWithoutCar() {
        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.FORWARDER)),
                vehicle = null,
                email = null,
                staffPartnerId = null
            )

        employeeActions.createProfileWithError(createRequest)

        employeeAssertion.getSoftAssertion()
            .assertThat(
                databaseController.checkActiveProfileExistsByMobile(
                    createRequest.mobile.asStringWithoutPlus()
                )
            )
            .isFalse

    }

    @Test
    @DisplayName("Create forwarder without darkstore")
    fun createProfileForwarderWithoutDarkstore() {
        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.FORWARDER)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
                email = null,
                staffPartnerId = null,
                darkstoreId = null
            )

        employeeActions.createProfileWithError(createRequest)

        employeeAssertion.getSoftAssertion()
            .assertThat(
                databaseController.checkActiveProfileExistsByMobile(
                    createRequest.mobile.asStringWithoutPlus()
                )
            )
            .isFalse

    }

    @Test
    @DisplayName("Create forwarder without city")
    fun createProfileForwarderWithoutCity() {
        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.FORWARDER)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
                email = null,
                staffPartnerId = null,
                cityId = null
            )

        employeeActions.createProfileWithError(createRequest)

        employeeAssertion.getSoftAssertion()
            .assertThat(
                databaseController.checkActiveProfileExistsByMobile(
                    createRequest.mobile.asStringWithoutPlus()
                )
            )
            .isFalse

    }


    @Test
    @Tags(Tag("smoke"), Tag("kafka_consume"))
    @DisplayName("Create darkstore admin")
    fun createProfileDarkstoreAdmin() {

        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DARKSTORE_ADMIN)),
                vehicle = null,
                email = null,
                staffPartnerId = null
            )

        val createdProfileId = employeeActions.createProfileId(createRequest)
        val profileFromApi = employeeActions.getApiProfileById(createdProfileId)
        val profileFromDB = employeeActions.getProfileFromDB(createdProfileId)
        val profilesFromProfileLogDB = employeeActions.getProfilesFromProfilesLogTable(createdProfileId)

        val messageInProfileCreated = employeeActions.getMessageFromKafkaCreated(createdProfileId)

        employeeAssertion
            .checkProfilesAreEquals(profileFromApi, createRequest)
            .checkProfileStatusIsEnabled(profileFromApi)

            .checkProfileFromProfileDB(createRequest, profileFromDB)

            .checkNewUserInProfileLogTable(profilesFromProfileLogDB)

            .checkEmployeeProfileCreatedKafka(messageInProfileCreated, createRequest)

        employeeAssertion.getSoftAssertion().assertThat(databaseController.checkDarkstoreUserExists(createdProfileId))
            .isFalse
        employeeAssertion.getSoftAssertion()
            .assertThat(databaseController.checkDarkstoreUserLogExists(createdProfileId))
            .isFalse
        employeeAssertion.getSoftAssertion()
            .assertThat(databaseController.checkDarkstoreUserActivityExists(createdProfileId))
            .isFalse
    }

    @Test
    @DisplayName("Create darkstore admin without darkstore")
    fun createProfileAdminWithoutDarkstore() {
        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DARKSTORE_ADMIN)),
                vehicle = null,
                email = null,
                staffPartnerId = null,
                darkstoreId = null
            )

        employeeActions.createProfileWithError(createRequest)

        employeeAssertion.getSoftAssertion()
            .assertThat(
                databaseController.checkActiveProfileExistsByMobile(
                    createRequest.mobile.asStringWithoutPlus()
                )
            )
            .isFalse

    }

    @Test
    @DisplayName("Create darkstore admin without city")
    fun createProfileAdminWithoutCity() {
        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DARKSTORE_ADMIN)),
                vehicle = null,
                email = null,
                staffPartnerId = null,
                cityId = null
            )

        employeeActions.createProfileWithError(createRequest)

        employeeAssertion.getSoftAssertion()
            .assertThat(
                databaseController.checkActiveProfileExistsByMobile(
                    createRequest.mobile.asStringWithoutPlus()
                )
            )
            .isFalse

    }

    @Test
    @Tags(Tag("smoke"), Tag("kafka_consume"))
    @DisplayName("Create supervisor")
    fun createProfileSupervisor() {

        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.SUPERVISOR)),
                vehicle = null,
                email = null,
                staffPartnerId = null,
                darkstoreId = null,
                cityId = null
            )

        val createdProfileId = employeeActions.createProfileId(createRequest)
        val profileFromApi = employeeActions.getApiProfileById(createdProfileId)
        val profileFromDB = employeeActions.getProfileFromDB(createdProfileId)
        val profilesFromProfileLogDB = employeeActions.getProfilesFromProfilesLogTable(createdProfileId)

        val messageInProfileCreated = employeeActions.getMessageFromKafkaCreated(createdProfileId)

        employeeAssertion
            .checkProfilesAreEquals(profileFromApi, createRequest)
            .checkProfileStatusIsEnabled(profileFromApi)

            .checkProfileFromProfileDB(createRequest, profileFromDB)
            .checkNewUserInProfileLogTable(profilesFromProfileLogDB)
            .checkEmployeeProfileCreatedKafka(messageInProfileCreated, createRequest)

        employeeAssertion.getSoftAssertion().assertThat(databaseController.checkDarkstoreUserExists(createdProfileId))
            .isFalse
        employeeAssertion.getSoftAssertion()
            .assertThat(databaseController.checkDarkstoreUserLogExists(createdProfileId))
            .isFalse
        employeeAssertion.getSoftAssertion()
            .assertThat(databaseController.checkDarkstoreUserActivityExists(createdProfileId))
            .isFalse
    }

    @Test
    @Tags(Tag("smoke"), Tag("kafka_consume"))
    @DisplayName("Create territorial manager")
    fun createProfileTerritorialManager() {

        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.TERRITORIAL_MANAGER)),
                vehicle = null,
                email = null,
                staffPartnerId = null,
                darkstoreId = null,
                cityId = null
            )

        val createdProfileId = employeeActions.createProfileId(createRequest)
        val profileFromApi = employeeActions.getApiProfileById(createdProfileId)
        val profileFromDB = employeeActions.getProfileFromDB(createdProfileId)
        val profilesFromProfileLogDB = employeeActions.getProfilesFromProfilesLogTable(createdProfileId)

        val messageInProfileCreated = employeeActions.getMessageFromKafkaCreated(createdProfileId)

        employeeAssertion
            .checkProfilesAreEquals(profileFromApi, createRequest)
            .checkProfileStatusIsEnabled(profileFromApi)

            .checkProfileFromProfileDB(createRequest, profileFromDB)

            .checkNewUserInProfileLogTable(profilesFromProfileLogDB)
            .checkEmployeeProfileCreatedKafka(messageInProfileCreated, createRequest)

        employeeAssertion.getSoftAssertion().assertThat(databaseController.checkDarkstoreUserExists(createdProfileId))
            .isFalse
        employeeAssertion.getSoftAssertion()
            .assertThat(databaseController.checkDarkstoreUserLogExists(createdProfileId))
            .isFalse
        employeeAssertion.getSoftAssertion()
            .assertThat(databaseController.checkDarkstoreUserActivityExists(createdProfileId))
            .isFalse

    }

    @Test
    @Tags(Tag("smoke"), Tag("kafka_consume"))
    @DisplayName("Create activation manager")
    fun createProfileActivationManager() {

        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.ACTIVATION_MANAGER)),
                vehicle = null,
                email = null,
                staffPartnerId = null,
                darkstoreId = null,
                cityId = null
            )

        val createdProfileId = employeeActions.createProfileId(createRequest)
        val profileFromApi = employeeActions.getApiProfileById(createdProfileId)
        val profileFromDB = employeeActions.getProfileFromDB(createdProfileId)
        val profilesFromProfileLogDB = employeeActions.getProfilesFromProfilesLogTable(createdProfileId)

        val messageInProfileCreated = employeeActions.getMessageFromKafkaCreated(createdProfileId)

        employeeAssertion
            .checkProfilesAreEquals(profileFromApi, createRequest)
            .checkProfileStatusIsEnabled(profileFromApi)

            .checkProfileFromProfileDB(createRequest, profileFromDB)
            .checkNewUserInProfileLogTable(profilesFromProfileLogDB)
            .checkEmployeeProfileCreatedKafka(messageInProfileCreated, createRequest)

        employeeAssertion.getSoftAssertion().assertThat(databaseController.checkDarkstoreUserExists(createdProfileId))
            .isFalse
        employeeAssertion.getSoftAssertion()
            .assertThat(databaseController.checkDarkstoreUserLogExists(createdProfileId))
            .isFalse
        employeeAssertion.getSoftAssertion()
            .assertThat(databaseController.checkDarkstoreUserActivityExists(createdProfileId))
            .isFalse
    }

    @Test
    @Tags(Tag("smoke"), Tag("kafka_consume"))
    @DisplayName("Create staff manager")
    fun createProfileStaffManager() {

        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.STAFF_MANAGER)),
                vehicle = null,
                email = null,
                staffPartnerId = null,
                darkstoreId = null,
                cityId = null
            )

        val createdProfileId = employeeActions.createProfileId(createRequest)
        val profileFromApi = employeeActions.getApiProfileById(createdProfileId)
        val profileFromDB = employeeActions.getProfileFromDB(createdProfileId)
        val profilesFromProfileLogDB = employeeActions.getProfilesFromProfilesLogTable(createdProfileId)

        val messageInProfileCreated = employeeActions.getMessageFromKafkaCreated(createdProfileId)

        employeeAssertion
            .checkProfilesAreEquals(profileFromApi, createRequest)
            .checkProfileStatusIsEnabled(profileFromApi)

            .checkProfileFromProfileDB(createRequest, profileFromDB)
            .checkNewUserInProfileLogTable(profilesFromProfileLogDB)
            .checkEmployeeProfileCreatedKafka(messageInProfileCreated, createRequest)

        employeeAssertion.getSoftAssertion().assertThat(databaseController.checkDarkstoreUserExists(createdProfileId))
            .isFalse
        employeeAssertion.getSoftAssertion()
            .assertThat(databaseController.checkDarkstoreUserLogExists(createdProfileId))
            .isFalse
        employeeAssertion.getSoftAssertion()
            .assertThat(databaseController.checkDarkstoreUserActivityExists(createdProfileId))
            .isFalse
    }

    @Test
    @Tags(Tag("smoke"), Tag("kafka_consume"))
    @DisplayName("Create tech support")
    fun createProfileTechSupport() {

        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.TECH_SUPPORT)),
                vehicle = null,
                email = null,
                staffPartnerId = null,
                darkstoreId = null,
                cityId = null
            )

        val createdProfileId = employeeActions.createProfileId(createRequest)
        val profileFromApi = employeeActions.getApiProfileById(createdProfileId)
        val profileFromDB = employeeActions.getProfileFromDB(createdProfileId)
        val profilesFromProfileLogDB = employeeActions.getProfilesFromProfilesLogTable(createdProfileId)

        val messageInProfileCreated = employeeActions.getMessageFromKafkaCreated(createdProfileId)

        employeeAssertion
            .checkProfilesAreEquals(profileFromApi, createRequest)
            .checkProfileStatusIsEnabled(profileFromApi)

            .checkProfileFromProfileDB(createRequest, profileFromDB)
            .checkNewUserInProfileLogTable(profilesFromProfileLogDB)
            .checkEmployeeProfileCreatedKafka(messageInProfileCreated, createRequest)

        employeeAssertion.getSoftAssertion().assertThat(databaseController.checkDarkstoreUserExists(createdProfileId))
            .isFalse
        employeeAssertion.getSoftAssertion()
            .assertThat(databaseController.checkDarkstoreUserLogExists(createdProfileId))
            .isFalse
        employeeAssertion.getSoftAssertion()
            .assertThat(databaseController.checkDarkstoreUserActivityExists(createdProfileId))
            .isFalse
    }

    @Test
    @Tags(Tag("smoke"), Tag("kafka_consume"))
    @DisplayName("Create auditor")
    fun createProfileAuditor() {

        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.AUDITOR)),
                vehicle = null,
                email = null,
                staffPartnerId = null,
                darkstoreId = null,
                cityId = null
            )

        val createdProfileId = employeeActions.createProfileId(createRequest)
        val profileFromApi = employeeActions.getApiProfileById(createdProfileId)
        val profileFromDB = employeeActions.getProfileFromDB(createdProfileId)
        val profilesFromProfileLogDB = employeeActions.getProfilesFromProfilesLogTable(createdProfileId)
        val messageInProfileCreated = employeeActions.getMessageFromKafkaCreated(createdProfileId)

        employeeAssertion
            .checkProfilesAreEquals(profileFromApi, createRequest)
            .checkProfileStatusIsEnabled(profileFromApi)

            .checkProfileFromProfileDB(createRequest, profileFromDB)
            .checkNewUserInProfileLogTable(profilesFromProfileLogDB)
            .checkEmployeeProfileCreatedKafka(messageInProfileCreated, createRequest)

        employeeAssertion.getSoftAssertion().assertThat(databaseController.checkDarkstoreUserExists(createdProfileId))
            .isFalse
        employeeAssertion.getSoftAssertion()
            .assertThat(databaseController.checkDarkstoreUserLogExists(createdProfileId))
            .isFalse
        employeeAssertion.getSoftAssertion()
            .assertThat(databaseController.checkDarkstoreUserActivityExists(createdProfileId))
            .isFalse
    }

    @Test
    @Tags(Tag("smoke"), Tag("kafka_consume"))
    @DisplayName("Create coordinator with one darkstore")
    fun createProfileCoordinator() {

        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.COORDINATOR)),
                vehicle = null,
                email = null,
                supervisedDarkstoresCount = 1,
                staffPartnerId = null,
                darkstoreId = null,
                cityId = null
            )

        val createdProfileId = employeeActions.createProfileId(createRequest)
        val profileFromApi = employeeActions.getApiProfileById(createdProfileId)
        val profileFromDB = employeeActions.getProfileFromDB(createdProfileId)
        val profilesFromProfileLogDB = employeeActions.getProfilesFromProfilesLogTable(createdProfileId)
        val supervisedDarkstoresFromDB = employeeActions.getSupervisedDarkstoresFromDB(createdProfileId)
        val messageInProfileCreated = employeeActions.getMessageFromKafkaCreated(createdProfileId)

        employeeAssertion
            .checkProfilesAreEquals(profileFromApi, createRequest)
            .checkProfileStatusIsEnabled(profileFromApi)

            .checkProfileFromProfileDB(createRequest, profileFromDB)
            .checkSupervisedDarkstoresInDatabase(createRequest, supervisedDarkstoresFromDB)
            .checkNewUserInProfileLogTable(profilesFromProfileLogDB)
            .checkEmployeeProfileCreatedKafka(messageInProfileCreated, createRequest)

        employeeAssertion.getSoftAssertion().assertThat(databaseController.checkDarkstoreUserExists(createdProfileId))
            .isFalse
        employeeAssertion.getSoftAssertion()
            .assertThat(databaseController.checkDarkstoreUserLogExists(createdProfileId))
            .isFalse
        employeeAssertion.getSoftAssertion()
            .assertThat(databaseController.checkDarkstoreUserActivityExists(createdProfileId))
            .isFalse
    }

    @Test
    @Tags(Tag("smoke"), Tag("kafka_consume"))
    @DisplayName("Create coordinator with several darkstores")
    fun createProfileCoordinatorWithSeveralDarkstore() {

        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.COORDINATOR)),
                vehicle = null,
                email = null,
                supervisedDarkstoresCount = 5,
                staffPartnerId = null,
                darkstoreId = null,
                cityId = null
            )

        val createdProfileId = employeeActions.createProfileId(createRequest)
        val profileFromApi = employeeActions.getApiProfileById(createdProfileId)
        val supervisedDarkstoresFromDB = employeeActions.getSupervisedDarkstoresFromDB(createdProfileId)
        val messageInProfileCreated = employeeActions.getMessageFromKafkaCreated(createdProfileId)

        employeeAssertion
            .checkSupervisedDarkstoresAreEquals(profileFromApi, createRequest)
            .checkSupervisedDarkstoresInDatabase(createRequest, supervisedDarkstoresFromDB)
            .checkEmployeeProfileCreatedKafka(messageInProfileCreated, createRequest)
    }

    @Test
    @Tags(Tag("smoke"), Tag("kafka_consume"))
    @DisplayName("Create coordinator with 64  darkstores")
    fun createProfileCoordinatorWith64Darkstore() {

        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.COORDINATOR)),
                vehicle = null,
                email = null,
                supervisedDarkstoresCount = 64,
                staffPartnerId = null,
                darkstoreId = null,
                cityId = null
            )

        val createdProfileId = employeeActions.createProfileId(createRequest)
        val profileFromApi = employeeActions.getApiProfileById(createdProfileId)
        val supervisedDarkstoresFromDB = employeeActions.getSupervisedDarkstoresFromDB(createdProfileId)
        val messageInProfileCreated = employeeActions.getMessageFromKafkaCreated(createdProfileId)

        employeeAssertion
            .checkSupervisedDarkstoresAreEquals(profileFromApi, createRequest)
            .checkSupervisedDarkstoresInDatabase(createRequest, supervisedDarkstoresFromDB)
            .checkEmployeeProfileCreatedKafka(messageInProfileCreated, createRequest)

    }

    @Test
    @Tags(Tag("smoke"), Tag("kafka_consume"))
    @DisplayName("Create counterparty")
    fun createProfileCounterParty() {

        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.COUNTERPARTY)),
                vehicle = null,
                darkstoreId = null,
                cityId = null
            )

        val createdProfileId = employeeActions.createProfileId(createRequest)
        val profileFromApi = employeeActions.getApiProfileById(createdProfileId)

        val profileFromDB = employeeActions.getProfileFromDB(createdProfileId)
        val profilesFromProfileLogDB = employeeActions.getProfilesFromProfilesLogTable(createdProfileId)

        val messageInProfileCreated = employeeActions.getMessageFromKafkaCreated(createdProfileId)

        employeeAssertion
            .checkProfilesAreEquals(profileFromApi, createRequest)
            .checkProfileStatusIsEnabled(profileFromApi)

            .checkProfileFromProfileDB(createRequest, profileFromDB)

            .checkNewUserInProfileLogTable(profilesFromProfileLogDB)
            .checkEmployeeProfileCreatedKafka(messageInProfileCreated, createRequest)

        employeeAssertion.getSoftAssertion().assertThat(databaseController.checkDarkstoreUserExists(createdProfileId))
            .isFalse
        employeeAssertion.getSoftAssertion()
            .assertThat(databaseController.checkDarkstoreUserLogExists(createdProfileId))
            .isFalse
        employeeAssertion.getSoftAssertion()
            .assertThat(databaseController.checkDarkstoreUserActivityExists(createdProfileId))
            .isFalse
    }

    @Test
    @DisplayName("Create counterparty without email")
    fun createProfileCounterPartyWithoutEmail() {

        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.COUNTERPARTY)),
                vehicle = null,
                darkstoreId = null,
                email = null,
                cityId = null
            )

        employeeActions.createProfileWithError(createRequest)

        employeeAssertion.getSoftAssertion()
            .assertThat(
                databaseController.checkActiveProfileExistsByMobile(
                    createRequest.mobile.asStringWithoutPlus()
                )
            )
            .isFalse

    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Create counterparty with invalid email")
    fun createProfileCounterPartyInvalidEmail() {
        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.COUNTERPARTY)),
                vehicle = null,
                darkstoreId = null,
                email = "test@test",
                cityId = null
            )

        employeeActions.createProfileWithError(createRequest)

        employeeAssertion.getSoftAssertion()
            .assertThat(
                databaseController.checkActiveProfileExistsByMobile(
                    createRequest.mobile.asStringWithoutPlus()
                )
            )
            .isFalse

    }

    @Test
    @Tags(Tag("smoke"), Tag("kafka_consume"))
    @DisplayName("Create deliveryman-picker")
    fun createMultiroleProfileDeliverymanPicker() {

        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN), ApiEnum(EmployeeRole.PICKER)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
                email = null
            )

        val createdProfileId = employeeActions.createProfileId(createRequest)
        val profileFromApi = employeeActions.getApiProfileById(createdProfileId)
        val profileFromDB = employeeActions.getProfileFromDB(createdProfileId)
        val vehicleFromDB = employeeActions.getVehicleFromDatabase(createdProfileId)
        val profilesFromProfileLogDB = employeeActions.getProfilesFromProfilesLogTable(createdProfileId)

        val profileFromDarkstoreDBDeliveryman =
            employeeActions.getProfilesFromDSUserTable(createdProfileId, EmployeeRole.DELIVERYMAN)
        val profileFromDarkstoreLogDBDeliveryman =
            employeeActions.getProfilesFromDSUserLogTable(createdProfileId, EmployeeRole.DELIVERYMAN)[0]
        val profileFromDarkstoreActivityDBDeliveryman =
            employeeActions.getProfilesFromDSActivityTable(createdProfileId, EmployeeRole.DELIVERYMAN)

        val profileFromDarkstoreDBPicker =
            employeeActions.getProfilesFromDSUserTable(createdProfileId, EmployeeRole.PICKER)
        val profileFromDarkstoreLogDBPicker =
            employeeActions.getProfilesFromDSUserLogTable(createdProfileId, EmployeeRole.PICKER)[0]
        val profileFromDarkstoreActivityDBPicker =
            employeeActions.getProfilesFromDSActivityTable(createdProfileId, EmployeeRole.PICKER)

        val messageInProfileCreated = employeeActions.getMessageFromKafkaCreated(createdProfileId)

        employeeAssertion
            .checkProfilesAreEquals(profileFromApi, createRequest)
            .checkProfileStatusIsEnabled(profileFromApi)

            .checkProfileFromProfileDB(createRequest, profileFromDB)
            .checkDeliverymanVehicleInDB(EmployeeVehicleType.CAR.toString(), vehicleFromDB)

            .checkNewUserInProfileLogTable(profilesFromProfileLogDB)

            .checkNewProfileFromDSUserTable(createRequest, profileFromDarkstoreDBDeliveryman, EmployeeRole.DELIVERYMAN)
            .checkNewProfileFromDSUserLogTable(
                createRequest,
                profileFromDarkstoreLogDBDeliveryman,
                EmployeeRole.DELIVERYMAN
            )
            .checkNewProfileFromDSUserActivityTable(
                createRequest,
                profileFromDarkstoreActivityDBDeliveryman,
                EmployeeRole.DELIVERYMAN
            )
            .checkNewProfileFromDSUserTable(createRequest, profileFromDarkstoreDBPicker, EmployeeRole.PICKER)
            .checkNewProfileFromDSUserLogTable(createRequest, profileFromDarkstoreLogDBPicker, EmployeeRole.PICKER)
            .checkNewProfileFromDSUserActivityTable(
                createRequest,
                profileFromDarkstoreActivityDBPicker,
                EmployeeRole.PICKER
            )
            .checkEmployeeProfileCreatedKafka(messageInProfileCreated, createRequest)
    }

    @Test
    @Tags(Tag("smoke"), Tag("kafka_consume"))
    @DisplayName("Create deliveryman-coordinator")
    fun createMultiroleProfileDeliverymanCoordinator() {

        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN), ApiEnum(EmployeeRole.COORDINATOR)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.COMPANY_BICYCLE)),
                supervisedDarkstoresCount = 5,
                email = null
            )

        val createdProfileId = employeeActions.createProfileId(createRequest)
        val profileFromApi = employeeActions.getApiProfileById(createdProfileId)

        val profileFromDB = employeeActions.getProfileFromDB(createdProfileId)
        val vehicleFromDB = employeeActions.getVehicleFromDatabase(createdProfileId)
        val profilesFromProfileLogDB = employeeActions.getProfilesFromProfilesLogTable(createdProfileId)

        val profileFromDarkstoreDB =
            employeeActions.getProfilesFromDSUserTable(createdProfileId, EmployeeRole.DELIVERYMAN)
        val profileFromDarkstoreLogDB =
            employeeActions.getProfilesFromDSUserLogTable(createdProfileId, EmployeeRole.DELIVERYMAN)[0]
        val profileFromDarkstoreActivityDB =
            employeeActions.getProfilesFromDSActivityTable(createdProfileId, EmployeeRole.DELIVERYMAN)

        val supervisedDarkstoresfromDB = employeeActions.getSupervisedDarkstoresFromDB(createdProfileId)
        val messageInProfileCreated = employeeActions.getMessageFromKafkaCreated(createdProfileId)

        employeeAssertion
            .checkProfilesAreEquals(profileFromApi, createRequest)
            .checkProfileStatusIsEnabled(profileFromApi)

            .checkSupervisedDarkstoresInDatabase(createRequest, supervisedDarkstoresfromDB)
            .checkProfileFromProfileDB(createRequest, profileFromDB)
            .checkDeliverymanVehicleInDB(EmployeeVehicleType.COMPANY_BICYCLE.toString(), vehicleFromDB)

            .checkNewUserInProfileLogTable(profilesFromProfileLogDB)

            .checkNewProfileFromDSUserTable(createRequest, profileFromDarkstoreDB, EmployeeRole.DELIVERYMAN)
            .checkNewProfileFromDSUserLogTable(createRequest, profileFromDarkstoreLogDB, EmployeeRole.DELIVERYMAN)
            .checkNewProfileFromDSUserActivityTable(
                createRequest,
                profileFromDarkstoreActivityDB,
                EmployeeRole.DELIVERYMAN
            )
            .checkEmployeeProfileCreatedKafka(messageInProfileCreated, createRequest)
    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Create deliveryman-counterparty")
    fun createMultiroleProfileDeliverymanCounterparty() {

        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN), ApiEnum(EmployeeRole.COUNTERPARTY)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
                supervisedDarkstoresCount = 5
            )

        employeeActions.createProfileWithError(createRequest)

        employeeAssertion
            .getSoftAssertion()
            .assertThat(
                databaseController.checkActiveProfileExistsByMobile(
                    createRequest.mobile.asStringWithoutPlus()
                )
            )
            .isFalse


    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Create profile with existing number")
    fun createProfileWithExistMobileTest() {

        val createRequest1 = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.STAFF_MANAGER)),
                vehicle = null,
                email = null,
                staffPartnerId = null,
                darkstoreId = null,
                cityId = null
            )
        val createRequest2 = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.TECH_SUPPORT)),
                vehicle = null,
                email = null,
                staffPartnerId = null,
                darkstoreId = null,
                cityId = null
            )

        employeeActions.createProfileId(createRequest1)
        employeeActions.createProfileWithExistNumber(createRequest2)

        employeeAssertion.getSoftAssertion().assertThat(employeeActions.getConflictProfile().roles[0].value)
            .isEqualTo(EmployeeRole.STAFF_MANAGER.toString())

    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Create profile without password")
    fun createProfileWithoutPassword() {

        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
                email = null,
                generatePassword = false
            )

        val createdProfileId = employeeActions.createProfileId(createRequest)

        employeeAssertion.getSoftAssertion().assertThat(databaseController.checkProfilePassExists(createdProfileId))
            .isFalse
    }

    @Test
    @DisplayName("Create goods manager with contractId")
    fun createProfileGoodsManagerWithContract() {

        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.GOODS_MANAGER)),
                vehicle = null,
                email = null,
                staffPartnerId = null,
                accountingProfileId = StringAndPhoneNumberGenerator.generateRandomInn(),
            )

        val createdProfileId = employeeActions.createProfileId(createRequest)
        val profileFromApi = employeeActions.getApiProfileById(createdProfileId)
        val profileFromDB = employeeActions.getProfileFromDB(createdProfileId)

        employeeAssertion
            .checkProfileAccountingId(
                createRequest.accountingProfileId.toString(),
                profileFromApi.accountingProfileId.toString()
            )
            .checkProfileAccountingId(
                createRequest.accountingProfileId.toString(),
                profileFromDB[Profile.accountingProfileId]
            )
    }

    @Test
    @DisplayName("Create darkstoreAdmin with contractId")
    fun createProfileDarkstoreAdminWithContract() {

        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DARKSTORE_ADMIN)),
                vehicle = null,
                email = null,
                staffPartnerId = null,
                accountingProfileId = StringAndPhoneNumberGenerator.generateRandomInn(),
            )

        val createdProfileId = employeeActions.createProfileId(createRequest)
        val profileFromApi = employeeActions.getApiProfileById(createdProfileId)
        val profileFromDB = employeeActions.getProfileFromDB(createdProfileId)

        employeeAssertion
            .checkProfileAccountingId(
                createRequest.accountingProfileId.toString(),
                profileFromApi.accountingProfileId.toString()
            )
            .checkProfileAccountingId(
                createRequest.accountingProfileId.toString(),
                profileFromDB[Profile.accountingProfileId]
            )
    }

    @Test
    @DisplayName("Create other role with contractId")
    fun createProfileSupervisorWithContract() {

        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.SUPERVISOR)),
                vehicle = null,
                email = null,
                staffPartnerId = null,
                darkstoreId = null,
                accountingProfileId = StringAndPhoneNumberGenerator.generateRandomInn(),
            )

        val createdProfileId = employeeActions.createProfileId(createRequest)
        val profileFromApi = employeeActions.getApiProfileById(createdProfileId)
        val profileFromDB = employeeActions.getProfileFromDB(createdProfileId)

        employeeAssertion
            .checkProfileAccountingId(
                createRequest.accountingProfileId.toString(),
                profileFromApi.accountingProfileId.toString()
            )
            .checkProfileAccountingId(
                createRequest.accountingProfileId.toString(),
                profileFromDB[Profile.accountingProfileId]
            )
    }

    @Test
    @DisplayName("Create profile with duplicated contractId")
    fun createProfileWithDuplicatedContract() {

        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.GOODS_MANAGER)),
                vehicle = null,
                email = null,
                staffPartnerId = null,
                accountingProfileId = StringAndPhoneNumberGenerator.generateRandomInn(),
            )

        val createRequest2 = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.GOODS_MANAGER)),
                vehicle = null,
                email = null,
                staffPartnerId = null,
                accountingProfileId = createRequest.accountingProfileId,
                mobile = Constants.mobile2
            )

        val createdProfileId = employeeActions.createProfileId(createRequest)
        employeeActions.createProfileWithError(createRequest2)

        employeeAssertion.getSoftAssertion()
            .assertThat(
                databaseController.checkActiveProfileExistsByMobile(
                    createRequest2.mobile.asStringWithoutPlus()
                )
            )
            .isFalse
    }

    // create profile from requisitions

    @Test
    @Tags(Tag("smoke"), Tag("kafka_produce"))
    @DisplayName("Create profile from requisition - deliveryman")
    fun createProfileFromRequisitionDeliveryman() {

        val event = employeePreconditions.fillOutsourseContractEvent(
            accountingProfileId = Constants.accountingProfileIdForRequisitions,
        )
        employeeActions.produceToVneshnieSotrudniki(event)

        val dbRequisition =
            employeeActions.getRequisitionFromDBByAccountingProfileId(Constants.accountingProfileIdForRequisitions.toString())!!

        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
                email = null,
                name = EmployeeName("Test", "Test", "Test"),
                requisitionId = dbRequisition[ProfileRequisition.requestId]
            )

        val profileId = employeeActions.createProfileId(createRequest)

        val profileFromDB = employeeActions.getProfileFromDB(profileId)
        val vehicleFromDB = employeeActions.getVehicleFromDatabase(profileId)

        employeeAssertion
            .checkProfileFromProfileDB(createRequest, profileFromDB)
    }

    @Test
    @Tags(Tag("kafka_produce"))
    @DisplayName("Create profile from requisition - picker")
    fun createProfileFromRequisitionPicker() {

        val event = employeePreconditions.fillOutsourseContractEvent(
            accountingProfileId = Constants.accountingProfileIdForRequisitions,
        )
        employeeActions.produceToVneshnieSotrudniki(event)

        val dbRequisition =
            employeeActions.getRequisitionFromDBByAccountingProfileId(Constants.accountingProfileIdForRequisitions.toString())!!

        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.PICKER)),
                vehicle = null,
                email = null,
                name = EmployeeName("Test", "Test", "Test"),
                requisitionId = dbRequisition[ProfileRequisition.requestId]
            )

        val profileId = employeeActions.createProfileId(createRequest)

        val profileFromDB = employeeActions.getProfileFromDB(profileId)
        val vehicleFromDB = employeeActions.getVehicleFromDatabase(profileId)

        employeeAssertion
            .checkProfileFromProfileDB(createRequest, profileFromDB)
            .checkVehicleIsEmpty(vehicleFromDB)
    }

    @Test
    @Tags(Tag("kafka_produce"))
    @DisplayName("Create profile from requisition - deliveryman-goods_manager")
    fun createProfileFromRequisitionDeliverymanGoodsManager() {

        val event = employeePreconditions.fillOutsourseContractEvent(
            accountingProfileId = Constants.accountingProfileIdForRequisitions,
        )
        employeeActions.produceToVneshnieSotrudniki(event)

        val dbRequisition =
            employeeActions.getRequisitionFromDBByAccountingProfileId(Constants.accountingProfileIdForRequisitions.toString())!!

        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN), ApiEnum(EmployeeRole.GOODS_MANAGER)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
                email = null,
                name = EmployeeName("Test", "Test", "Test"),
                requisitionId = dbRequisition[ProfileRequisition.requestId]
            )

        val profileId = employeeActions.createProfileId(createRequest)

        val profileFromDB = employeeActions.getProfileFromDB(profileId)
        val vehicleFromDB = employeeActions.getVehicleFromDatabase(profileId)

        employeeAssertion
            .checkProfileFromProfileDB(createRequest, profileFromDB)
    }

    @Test
    @Tags(Tag("kafka_produce"))
    @DisplayName("Create profile from requisition - deliveryman-picker")
    fun createProfileFromRequisitionDeliverymanPicker() {

        val event = employeePreconditions.fillOutsourseContractEvent(
            accountingProfileId = Constants.accountingProfileIdForRequisitions,
        )
        employeeActions.produceToVneshnieSotrudniki(event)

        val dbRequisition =
            employeeActions.getRequisitionFromDBByAccountingProfileId(Constants.accountingProfileIdForRequisitions.toString())!!

        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN), ApiEnum(EmployeeRole.PICKER)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
                email = null,
                name = EmployeeName("Test", "Test", "Test"),
                requisitionId = dbRequisition[ProfileRequisition.requestId]
            )

        val profileId = employeeActions.createProfileId(createRequest)

        val profileFromDB = employeeActions.getProfileFromDB(profileId)
        val vehicleFromDB = employeeActions.getVehicleFromDatabase(profileId)

        employeeAssertion
            .checkProfileFromProfileDB(createRequest, profileFromDB)
    }

    @Test
    @Tags(Tag("kafka_produce"))
    @DisplayName("Create profile from requisition - with wrong role - darkstore_admin")
    fun createProfileFromRequisitionDarkstoreAdminWrongRole() {

        val event = employeePreconditions.fillOutsourseContractEvent(
            accountingProfileId = Constants.accountingProfileIdForRequisitions,
        )
        employeeActions.produceToVneshnieSotrudniki(event)

        val dbRequisition =
            employeeActions.getRequisitionFromDBByAccountingProfileId(Constants.accountingProfileIdForRequisitions.toString())!!

        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DARKSTORE_ADMIN)),
                vehicle = null,
                staffPartnerId = null,
                email = null,
                name = EmployeeName("Test", "Test", "Test"),
                requisitionId = dbRequisition[ProfileRequisition.requestId]
            )

        employeeActions.createProfileWithError(createRequest)

        employeeAssertion
            .checkErrorMessage(
                employeeActions.getResponseCreateProfile().asClientError().message,
                "Invalid profile requisition roles"
            )
            .getSoftAssertion()
            .assertThat(
                databaseController.checkActiveProfileExistsByMobile(
                    createRequest.mobile.asStringWithoutPlus()
                )
            )
            .isFalse

    }

    @Test
    @Tags(Tag("smoke"), Tag("kafka_produce"))
    @DisplayName("Create profile from requisition - deliveryman with guid")
    fun createProfileFromRequisitionDeliverymanWithGuid() {

        val event = employeePreconditions.fillOutsourseContractEvent(
            accountingProfileId = Constants.accountingProfileIdForRequisitions,
        )
        employeeActions.produceToVneshnieSotrudniki(event)

        val dbRequisition =
            employeeActions.getRequisitionFromDBByAccountingProfileId(Constants.accountingProfileIdForRequisitions.toString())!!

        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
                email = null,
                name = EmployeeName("Test", "Test", "Test"),
                requisitionId = dbRequisition[ProfileRequisition.requestId],
                accountingProfileId = UUID.randomUUID().toString()
            )

        employeeActions.createProfileWithError(createRequest)

        employeeAssertion
            .checkErrorMessage(
                employeeActions.getResponseCreateProfile().asClientError().message,
                "Profile requisition can't be used simultaneously with accounting profile id"
            )
            .getSoftAssertion()
            .assertThat(
                databaseController.checkActiveProfileExistsByMobile(
                    createRequest.mobile.asStringWithoutPlus()
                )
            )
            .isFalse
    }

    @Test
    @Tags(Tag("smoke"), Tag("kafka_produce"))
    @DisplayName("Create profile from declined requisition")
    fun createProfileFromDeclinedRequisition() {

        val event = employeePreconditions.fillOutsourseContractEvent(
            accountingProfileId = Constants.accountingProfileIdForRequisitions,
        )
        val declineRequest = employeePreconditions.fillDeclineRequisitionRequest()
        employeeActions.produceToVneshnieSotrudniki(event)

        val dbRequisition =
            employeeActions.getRequisitionFromDBByAccountingProfileId(Constants.accountingProfileIdForRequisitions.toString())!!
        employeeActions.declineProfileRequisition(dbRequisition[ProfileRequisition.requestId], declineRequest)

        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
                email = null,
                name = EmployeeName("Test", "Test", "Test"),
                requisitionId = dbRequisition[ProfileRequisition.requestId]
            )

        employeeActions.createProfileWithError(createRequest)

        employeeAssertion
            .checkErrorMessage(
                employeeActions.getResponseCreateProfile().asClientError().message,
                "Invalid profile requisition status"
            )
            .getSoftAssertion()
            .assertThat(
                databaseController.checkActiveProfileExistsByMobile(
                    createRequest.mobile.asStringWithoutPlus()
                )
            )
            .isFalse
    }

    @Test
    @Tags(Tag("smoke"), Tag("kafka_produce"))
    @DisplayName("Create profile from processed requisition")
    fun createProfileFromProcessedRequisition() {

        val event = employeePreconditions.fillOutsourseContractEvent(
            accountingProfileId = Constants.accountingProfileIdForRequisitions,
        )
        employeeActions.produceToVneshnieSotrudniki(event)

        val dbRequisition =
            employeeActions.getRequisitionFromDBByAccountingProfileId(Constants.accountingProfileIdForRequisitions.toString())!!
        commonPreconditions.createProfileDeliveryman(
            requisitionId = dbRequisition[ProfileRequisition.requestId]
        )

        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
                email = null,
                name = EmployeeName("Test", "Test", "Test"),
                requisitionId = dbRequisition[ProfileRequisition.requestId],
                mobile = Constants.mobile2
            )

        employeeActions.createProfileWithError(createRequest)

        val profileExistance = employeeActions.getProfileExistanceByMobile(Constants.mobile2.asStringWithoutPlus())

        commonAssertion.checkErrorMessage(
            employeeActions.getResponseCreateProfile().asClientError().message,
            "Invalid profile requisition status"
        )
        employeeAssertion
            .checkProfileNotExists(profileExistance)
    }

    @Test
    @Tags(Tag("smoke"), Tag("kafka_produce"))
    @DisplayName("Create profile from requisition - darkstore admin")
    fun createProfileFromRequisitionDarkstoreAdmin() {

        val event = employeePreconditions.fillPriemNaRabotyEvent(
            accountingProfileId = Constants.accountingProfileIdForRequisitions,
            inn = Constants.innForRequisitions
        )
        employeeActions.produceToPriemNaRabotuCFZ(event)

        val dbRequisition = employeeActions.getRequisitionFromDBByAccountingProfileId(Constants.innForRequisitions)!!

        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DARKSTORE_ADMIN)),
                vehicle = null,
                staffPartnerId = null,
                email = null,
                name = EmployeeName("Test", "Test", "Test"),
                requisitionId = dbRequisition[ProfileRequisition.requestId]
            )

        val profileId = employeeActions.createProfileId(createRequest)

        val profileFromDB = employeeActions.getProfileFromDB(profileId)

        employeeAssertion
            .checkProfileFromProfileDB(createRequest, profileFromDB)
    }

    @Test
    @Tags(Tag("smoke"), Tag("kafka_produce"))
    @DisplayName("Create profile from requisition - goods manager")
    fun createProfileFromRequisitionGoodsManager() {

        val event = employeePreconditions.fillPriemNaRabotyEvent(
            accountingProfileId = Constants.accountingProfileIdForRequisitions,
            inn = Constants.innForRequisitions
        )
        employeeActions.produceToPriemNaRabotuCFZ(event)

        val dbRequisition = employeeActions.getRequisitionFromDBByAccountingProfileId(Constants.innForRequisitions)!!

        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.GOODS_MANAGER)),
                vehicle = null,
                staffPartnerId = null,
                email = null,
                name = EmployeeName("Test", "Test", "Test"),
                requisitionId = dbRequisition[ProfileRequisition.requestId]
            )

        val profileId = employeeActions.createProfileId(createRequest)

        val profileFromDB = employeeActions.getProfileFromDB(profileId)

        employeeAssertion
            .checkProfileFromProfileDB(createRequest, profileFromDB)
    }

}
