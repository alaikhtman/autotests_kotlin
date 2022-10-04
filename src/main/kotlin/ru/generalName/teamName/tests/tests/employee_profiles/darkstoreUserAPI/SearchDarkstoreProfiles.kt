package ru.samokat.mysamokat.tests.tests.employee_profiles.darkstoreUserAPI

import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.samokat.employeeprofiles.api.common.domain.Vehicle
import ru.samokat.employeeprofiles.api.common.paging.PagingFilter
import ru.samokat.employeeprofiles.api.profiles.create.CreateProfileRequest
import ru.samokat.employeeprofiles.api.darkstoreusers.domain.DarkstoreUserRole
import ru.samokat.employeeprofiles.api.darkstoreusers.domain.DarkstoreUserState
import ru.samokat.employeeprofiles.api.darkstoreusers.search.SearchDarkstoreUsersSortingMode
import ru.samokat.my.domain.PhoneNumber
import ru.samokat.my.domain.employee.EmployeeName
import ru.samokat.my.domain.employee.EmployeeRole
import ru.samokat.employeeprofiles.api.common.domain.EmployeeVehicleType
import ru.samokat.my.rest.api.enum.ApiEnum
import ru.samokat.mysamokat.tests.checkers.EmployeeAssertion
import ru.samokat.mysamokat.tests.dataproviders.Constants
import ru.samokat.mysamokat.tests.dataproviders.StringAndPhoneNumberGenerator
import ru.samokat.mysamokat.tests.dataproviders.preconditions.EmployeePreconditions
import ru.samokat.mysamokat.tests.helpers.actions.EmployeeActions

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Tag("empro")
class SearchDarkstoreProfiles {

    private lateinit var employeeAssertion: EmployeeAssertion

    @Autowired
    private lateinit var employeeActions: EmployeeActions

    private lateinit var employeePreconditions: EmployeePreconditions

    @BeforeEach
    fun before() {
        employeePreconditions = EmployeePreconditions()
        employeeAssertion = EmployeeAssertion()
        createTestUsers()
    }

    @AfterEach
    fun release() {
        employeeAssertion.assertAll()
        employeeActions.deleteProfile(Constants.mobile1)
        deleteTestUsers()
    }

    var testUsers: MutableMap<PhoneNumber, CreateProfileRequest> = mutableMapOf()

    private fun createTestUsers() {
        deleteTestUsers()

        val createRequest1 = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.BICYCLE)),
                staffPartnerId = Constants.staffPartnerId,
                darkstoreId = Constants.searchContactsDarkstore,
                email = null,
                mobile = Constants.mobile1,
                name = EmployeeName("Maria", "Petrova")
            )
        val createdProfileId1 = employeeActions.createProfileId(createRequest1)
        testUsers[Constants.mobile1] = createRequest1

        val createRequest2 = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.PICKER)),
                staffPartnerId = Constants.defaultStaffPartnerId,
                email = null,
                darkstoreId = Constants.searchContactsDarkstore,
                vehicle = null,
                mobile = Constants.mobile2,
                name = EmployeeName("Anna", "Sidorova")
            )
        val createdProfileId2 = employeeActions.createProfileId(createRequest2)
        testUsers[Constants.mobile2] = createRequest2
        val updateDSBuilder1 = employeePreconditions.fillUpdateProfileStatusOnDarkstore(
            createdProfileId2, ApiEnum(DarkstoreUserState.WORKING), null
        )
        employeeActions.updateProfileStatusOnDarkstore(
            createdProfileId2,
            createRequest2.darkstoreId!!,
            DarkstoreUserRole.PICKER,
            updateDSBuilder1
        )

        val createRequest3 = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.PICKER), ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.ELECTRIC_BICYCLE)),
                staffPartnerId = Constants.staffPartnerId,
                email = null,
                darkstoreId = Constants.searchContactsDarkstore,
                mobile = Constants.mobile3,
                name = EmployeeName("Ivan", "Ivanov", "Ivanovich")
            )
        val createdProfileId3 = employeeActions.createProfileId(createRequest3)
        testUsers[Constants.mobile3] = createRequest3
        val updateDSBuilder3 = employeePreconditions.fillUpdateProfileStatusOnDarkstore(
            createdProfileId3,
            ApiEnum(DarkstoreUserState.NOT_WORKING),
            "employee_request"
        )
        employeeActions.updateProfileStatusOnDarkstore(
            createdProfileId3,
            createRequest3.darkstoreId!!,
            DarkstoreUserRole.DELIVERYMAN,
            updateDSBuilder3
        )

        val createRequest4 = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                staffPartnerId = Constants.defaultStaffPartnerId,
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
                email = null,
                darkstoreId = Constants.searchContactsDarkstore,
                mobile = Constants.mobile4,
                name = EmployeeName("Сергей", "Соколов", "курьерНомер1")
            )
        val createdProfileId4 = employeeActions.createProfileId(createRequest4)
        testUsers[Constants.mobile4] = createRequest4
        val updateDSBuilder4 = employeePreconditions.fillUpdateProfileStatusOnDarkstore(
            createdProfileId4,
            ApiEnum(DarkstoreUserState.NOT_WORKING),
            "darkstore_admin_request"
        )
        employeeActions.updateProfileStatusOnDarkstore(
            createdProfileId4,
            createRequest4.darkstoreId!!,
            DarkstoreUserRole.DELIVERYMAN,
            updateDSBuilder4
        )

        val createRequest5 = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                staffPartnerId = Constants.defaultStaffPartnerId,
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.MOTOCYCLE)),
                email = null,
                darkstoreId = Constants.searchContactsDarkstore,
                mobile = Constants.mobile5,
                name = EmployeeName("Михаил", "Сергеев", "дефис-отчество")
            )
        val createdProfileId5 = employeeActions.createProfileId(createRequest5)
        testUsers[Constants.mobile5] = createRequest5
        val updateDSBuilder5 = employeePreconditions.fillUpdateProfileStatusOnDarkstore(
            createdProfileId5,
            ApiEnum(DarkstoreUserState.NOT_WORKING),
            "violation"
        )
        employeeActions.updateProfileStatusOnDarkstore(
            createdProfileId5,
            createRequest5.darkstoreId!!,
            DarkstoreUserRole.DELIVERYMAN,
            updateDSBuilder5
        )

        val createRequest6 = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
                staffPartnerId = Constants.staffPartnerId,
                darkstoreId = Constants.searchContactsDarkstore,
                email = null,
                mobile = Constants.mobile6,
                name = EmployeeName("Андрей", "Соколов", "Сергеевич")
            )
        val createdProfileId6 = employeeActions.createProfileId(createRequest6)
        testUsers[Constants.mobile6] = createRequest6

        val createRequest7 = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
                staffPartnerId = Constants.staffPartnerId,
                email = null,
                darkstoreId = Constants.searchProfileDarkstore,
                mobile = Constants.mobile7
            )
        val createdProfileId7 = employeeActions.createProfileId(createRequest7)
        testUsers[Constants.mobile7] = createRequest7
    }

    private fun deleteTestUsers() {
        employeeActions.deleteProfile(Constants.mobile1)
        employeeActions.deleteProfile(Constants.mobile2)
        employeeActions.deleteProfile(Constants.mobile3)
        employeeActions.deleteProfile(Constants.mobile4)
        employeeActions.deleteProfile(Constants.mobile5)
        employeeActions.deleteProfile(Constants.mobile6)
        employeeActions.deleteProfile(Constants.mobile7)

    }

    @Test
    @DisplayName("Get DS profiles: get profiles by one darkstore")
    fun getProfilesByOneDarkstore() {
        val searchRequest = employeePreconditions.fillSearchDSProfilesRequest(
            darkstoreIds = listOf(Constants.searchContactsDarkstore)
        )

        val searchResults = employeeActions.searchDarkstoreProfiles(searchRequest).darkstoreUsers

        employeeAssertion
            .checkProfilePresentInDSUsersList(searchResults, Constants.mobile1)
            .checkProfilePresentInDSUsersList(searchResults, Constants.mobile2)
            .checkProfilePresentInDSUsersList(searchResults, Constants.mobile3)
            .checkProfilePresentInDSUsersList(searchResults, Constants.mobile4)
            .checkProfilePresentInDSUsersList(searchResults, Constants.mobile5)
            .checkProfileNotPresentInDSUsersList(searchResults, Constants.mobile7)
            .checkDSUserSatisfyConditions(searchResults, "darkstoreId", darkstoreId = Constants.searchContactsDarkstore)
    }

    @Test
    @DisplayName("Get DS profiles: get profiles by several darkstore")
    fun getProfilesBySeveralDarkstore() {
        val searchRequest = employeePreconditions.fillSearchDSProfilesRequest(
            darkstoreIds = listOf(Constants.searchContactsDarkstore, Constants.searchProfileDarkstore)
        )

        val searchResults = employeeActions.searchDarkstoreProfiles(searchRequest).darkstoreUsers

        employeeAssertion
            .checkProfilePresentInDSUsersList(searchResults, Constants.mobile1)
            .checkProfilePresentInDSUsersList(searchResults, Constants.mobile2)
            .checkProfilePresentInDSUsersList(searchResults, Constants.mobile3)
            .checkProfilePresentInDSUsersList(searchResults, Constants.mobile4)
            .checkProfilePresentInDSUsersList(searchResults, Constants.mobile5)
            .checkProfilePresentInDSUsersList(searchResults, Constants.mobile7)
    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Get DS profiles: new state")
    fun getProfilesByNewState() {
        val searchRequest = employeePreconditions.fillSearchDSProfilesRequest(
            darkstoreIds = listOf(Constants.searchContactsDarkstore),
            darkstoreUserStates = listOf(ApiEnum(DarkstoreUserState.NEW))
        )

        val searchResults = employeeActions.searchDarkstoreProfiles(searchRequest).darkstoreUsers

        employeeAssertion
            .checkProfilePresentInDSUsersList(searchResults, Constants.mobile1)
            .checkProfileNotPresentInDSUsersList(searchResults, Constants.mobile2)
            .checkProfilePresentInDSUsersList(searchResults, Constants.mobile3)
            .checkProfileNotPresentInDSUsersList(searchResults, Constants.mobile4)
            .checkProfileNotPresentInDSUsersList(searchResults, Constants.mobile5)
            .checkDSUserSatisfyConditions(searchResults, "state", state = DarkstoreUserState.NEW)
    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Get DS profiles: working status")
    fun getProfilesByWorkingStatus() {
        val searchRequest = employeePreconditions.fillSearchDSProfilesRequest(
            darkstoreIds = listOf(Constants.searchContactsDarkstore),
            darkstoreUserStates = listOf(ApiEnum(DarkstoreUserState.WORKING))
        )

        val searchResults = employeeActions.searchDarkstoreProfiles(searchRequest).darkstoreUsers

        employeeAssertion
            .checkProfileNotPresentInDSUsersList(searchResults, Constants.mobile1)
            .checkProfilePresentInDSUsersList(searchResults, Constants.mobile2)
            .checkProfileNotPresentInDSUsersList(searchResults, Constants.mobile3)
            .checkProfileNotPresentInDSUsersList(searchResults, Constants.mobile4)
            .checkProfileNotPresentInDSUsersList(searchResults, Constants.mobile5)
            .checkDSUserSatisfyConditions(searchResults, "state", state = DarkstoreUserState.WORKING)
    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Get DS profiles: not_working status")
    fun getProfilesByNotWorkingStatus() {
        val searchRequest = employeePreconditions.fillSearchDSProfilesRequest(
            darkstoreIds = listOf(Constants.searchContactsDarkstore),
            darkstoreUserStates = listOf(ApiEnum(DarkstoreUserState.NOT_WORKING))
        )

        val searchResults = employeeActions.searchDarkstoreProfiles(searchRequest).darkstoreUsers

        employeeAssertion
            .checkProfileNotPresentInDSUsersList(searchResults, Constants.mobile1)
            .checkProfileNotPresentInDSUsersList(searchResults, Constants.mobile2)
            .checkProfilePresentInDSUsersList(searchResults, Constants.mobile3)
            .checkProfilePresentInDSUsersList(searchResults, Constants.mobile4)
            .checkProfilePresentInDSUsersList(searchResults, Constants.mobile5)
            .checkDSUserSatisfyConditions(searchResults, "state", state = DarkstoreUserState.NOT_WORKING)
    }

    @Test
    @DisplayName("Get DS profiles: several status (new, working)")
    fun getProfilesByNewWorkingStatus() {
        val searchRequest = employeePreconditions.fillSearchDSProfilesRequest(
            darkstoreIds = listOf(Constants.searchContactsDarkstore),
            darkstoreUserStates = listOf(ApiEnum(DarkstoreUserState.NEW), ApiEnum(DarkstoreUserState.WORKING))
        )

        val searchResults = employeeActions.searchDarkstoreProfiles(searchRequest)

        employeeAssertion
            .checkProfilePresentInDSUsersList(searchResults.darkstoreUsers, Constants.mobile1)
            .checkProfilePresentInDSUsersList(searchResults.darkstoreUsers, Constants.mobile2)
            .checkProfilePresentInDSUsersList(searchResults.darkstoreUsers, Constants.mobile3)
            .checkProfileNotPresentInDSUsersList(searchResults.darkstoreUsers, Constants.mobile4)
            .checkProfileNotPresentInDSUsersList(searchResults.darkstoreUsers, Constants.mobile5)
    }

    @Test
    @DisplayName("Get DS profiles: by role - deliveryman")
    fun getProfilesByDeliverymanRole() {
        val searchRequest = employeePreconditions.fillSearchDSProfilesRequest(
            darkstoreIds = listOf(Constants.searchContactsDarkstore),
            darkstoreUserRoles = listOf(ApiEnum(DarkstoreUserRole.DELIVERYMAN))
        )

        val searchResults = employeeActions.searchDarkstoreProfiles(searchRequest).darkstoreUsers

        employeeAssertion
            .checkProfilePresentInDSUsersList(searchResults, Constants.mobile1)
            .checkProfileNotPresentInDSUsersList(searchResults, Constants.mobile2)
            .checkProfilePresentInDSUsersList(searchResults, Constants.mobile3)
            .checkProfilePresentInDSUsersList(searchResults, Constants.mobile4)
            .checkProfilePresentInDSUsersList(searchResults, Constants.mobile5)
            .checkDSUserSatisfyConditions(searchResults, "role", role = ApiEnum(DarkstoreUserRole.DELIVERYMAN))

    }

    @Test
    @DisplayName("Get DS profiles: by role - picker")
    fun getProfilesByPickerRole() {
        val searchRequest = employeePreconditions.fillSearchDSProfilesRequest(
            darkstoreIds = listOf(Constants.searchContactsDarkstore),
            darkstoreUserRoles = listOf(ApiEnum(DarkstoreUserRole.PICKER))
        )

        val searchResults = employeeActions.searchDarkstoreProfiles(searchRequest).darkstoreUsers

        employeeAssertion
            .checkProfileNotPresentInDSUsersList(searchResults, Constants.mobile1)
            .checkProfilePresentInDSUsersList(searchResults, Constants.mobile2)
            .checkProfilePresentInDSUsersList(searchResults, Constants.mobile3)
            .checkProfileNotPresentInDSUsersList(searchResults, Constants.mobile4)
            .checkProfileNotPresentInDSUsersList(searchResults, Constants.mobile5)
            .checkDSUserSatisfyConditions(searchResults, "role", role = ApiEnum(DarkstoreUserRole.PICKER))

    }

    @Test
    @DisplayName("Get DS profiles: by role - deliveryman&picker")
    fun getProfilesByTwoRoles() {
        val searchRequest = employeePreconditions.fillSearchDSProfilesRequest(
            darkstoreIds = listOf(Constants.searchContactsDarkstore),
            darkstoreUserRoles = listOf(ApiEnum(DarkstoreUserRole.DELIVERYMAN), ApiEnum(DarkstoreUserRole.PICKER))
        )

        val searchResults = employeeActions.searchDarkstoreProfiles(searchRequest).darkstoreUsers

        employeeAssertion
            .checkProfilePresentInDSUsersList(searchResults, Constants.mobile1)
            .checkProfilePresentInDSUsersList(searchResults, Constants.mobile2)
            .checkProfilePresentInDSUsersList(searchResults, Constants.mobile3)
            .checkProfilePresentInDSUsersList(searchResults, Constants.mobile4)
            .checkProfilePresentInDSUsersList(searchResults, Constants.mobile5)
    }

    @Test
    @DisplayName("Get DS profiles: filter by vehicle - bicycle")
    fun getProfilesByVehicleNone() {
        val searchRequest = employeePreconditions.fillSearchDSProfilesRequest(
            darkstoreIds = listOf(Constants.searchContactsDarkstore),
            vehicleTypes = listOf(ApiEnum(EmployeeVehicleType.BICYCLE))
        )

        val searchResults = employeeActions.searchDarkstoreProfiles(searchRequest).darkstoreUsers

        employeeAssertion
            .checkProfilePresentInDSUsersList(searchResults, Constants.mobile1)
            .checkProfileNotPresentInDSUsersList(searchResults, Constants.mobile2)
            .checkProfileNotPresentInDSUsersList(searchResults, Constants.mobile3)
            .checkProfileNotPresentInDSUsersList(searchResults, Constants.mobile4)
            .checkProfileNotPresentInDSUsersList(searchResults, Constants.mobile5)
            .checkDSUserSatisfyConditions(
                searchResults,
                "vehicle",
                vehicle = listOf(ApiEnum(EmployeeVehicleType.BICYCLE))
            )

    }

    @Test
    @DisplayName("Get DS profiles: filter by vehicle - car")
    fun getProfilesByVehicleCar() {
        val searchRequest = employeePreconditions.fillSearchDSProfilesRequest(
            darkstoreIds = listOf(Constants.searchContactsDarkstore),
            vehicleTypes = listOf(ApiEnum(EmployeeVehicleType.CAR))
        )

        val searchResults = employeeActions.searchDarkstoreProfiles(searchRequest).darkstoreUsers

        employeeAssertion
            .checkProfileNotPresentInDSUsersList(searchResults, Constants.mobile1)
            .checkProfileNotPresentInDSUsersList(searchResults, Constants.mobile2)
            .checkProfileNotPresentInDSUsersList(searchResults, Constants.mobile3)
            .checkProfilePresentInDSUsersList(searchResults, Constants.mobile4)
            .checkProfileNotPresentInDSUsersList(searchResults, Constants.mobile5)
            .checkDSUserSatisfyConditions(searchResults, "vehicle", vehicle = listOf(ApiEnum(EmployeeVehicleType.CAR)))

    }

    @Test
    @DisplayName("Get DS profiles: filter by vehicle - moto")
    fun getProfilesByVehiclePersonalBicycle() {
        val searchRequest = employeePreconditions.fillSearchDSProfilesRequest(
            darkstoreIds = listOf(Constants.searchContactsDarkstore),
            vehicleTypes = listOf(ApiEnum(EmployeeVehicleType.MOTOCYCLE))
        )

        val searchResults = employeeActions.searchDarkstoreProfiles(searchRequest).darkstoreUsers

        employeeAssertion
            .checkProfileNotPresentInDSUsersList(searchResults, Constants.mobile1)
            .checkProfileNotPresentInDSUsersList(searchResults, Constants.mobile2)
            .checkProfileNotPresentInDSUsersList(searchResults, Constants.mobile3)
            .checkProfileNotPresentInDSUsersList(searchResults, Constants.mobile4)
            .checkProfilePresentInDSUsersList(searchResults, Constants.mobile5)
            .checkDSUserSatisfyConditions(
                searchResults,
                "vehicle",
                vehicle = listOf(ApiEnum(EmployeeVehicleType.MOTOCYCLE))
            )

    }

    @Test
    @DisplayName("Get DS profiles: filter by vehicle - electric bicycle")
    fun getProfilesByVehicleCompanyBicycle() {
        val searchRequest = employeePreconditions.fillSearchDSProfilesRequest(
            darkstoreIds = listOf(Constants.searchContactsDarkstore),
            vehicleTypes = listOf(ApiEnum(EmployeeVehicleType.ELECTRIC_BICYCLE))
        )

        val searchResults = employeeActions.searchDarkstoreProfiles(searchRequest).darkstoreUsers

        employeeAssertion
            .checkProfileNotPresentInDSUsersList(searchResults, Constants.mobile1)
            .checkProfileNotPresentInDSUsersList(searchResults, Constants.mobile2)
            .checkProfilePresentInDSUsersList(searchResults, Constants.mobile3)
            .checkProfileNotPresentInDSUsersList(searchResults, Constants.mobile4)
            .checkProfileNotPresentInDSUsersList(searchResults, Constants.mobile5)
            .checkDSUserSatisfyConditions(
                searchResults,
                "vehicle",
                vehicle = listOf(ApiEnum(EmployeeVehicleType.ELECTRIC_BICYCLE))
            )
    }

    @Test
    @DisplayName("Get DS profiles: filter by vehicle - several types")
    fun getProfilesByVehicleSeveraltypes() {
        val searchRequest = employeePreconditions.fillSearchDSProfilesRequest(
            darkstoreIds = listOf(Constants.searchContactsDarkstore),
            vehicleTypes = listOf(ApiEnum(EmployeeVehicleType.BICYCLE), ApiEnum(EmployeeVehicleType.CAR))
        )

        val searchResults = employeeActions.searchDarkstoreProfiles(searchRequest).darkstoreUsers

        employeeAssertion
            .checkProfilePresentInDSUsersList(searchResults, Constants.mobile1)
            .checkProfileNotPresentInDSUsersList(searchResults, Constants.mobile2)
            .checkProfileNotPresentInDSUsersList(searchResults, Constants.mobile3)
            .checkProfilePresentInDSUsersList(searchResults, Constants.mobile4)
            .checkProfileNotPresentInDSUsersList(searchResults, Constants.mobile5)
            .checkDSUserSatisfyConditions(
                searchResults,
                "vehicle",
                vehicle = listOf(ApiEnum(EmployeeVehicleType.BICYCLE), ApiEnum(EmployeeVehicleType.CAR))
            )
    }

    @Test
    @DisplayName("Get DS profiles: filter by staff partner")
    fun getProfilesByStaffPartner() {
        val searchRequest = employeePreconditions.fillSearchDSProfilesRequest(
            darkstoreIds = listOf(Constants.searchContactsDarkstore),
            staffPartnerIds = listOf(Constants.staffPartnerId)
        )

        val searchResults = employeeActions.searchDarkstoreProfiles(searchRequest).darkstoreUsers

        employeeAssertion
            .checkProfilePresentInDSUsersList(searchResults, Constants.mobile1)
            .checkProfileNotPresentInDSUsersList(searchResults, Constants.mobile2)
            .checkProfilePresentInDSUsersList(searchResults, Constants.mobile3)
            .checkProfileNotPresentInDSUsersList(searchResults, Constants.mobile4)
            .checkProfileNotPresentInDSUsersList(searchResults, Constants.mobile5)
            .checkDSUserSatisfyConditions(
                searchResults,
                "staffPartner",
                staffPartners = listOf(Constants.staffPartnerId)
            )
    }

    @Test
    @DisplayName("Get DS profiles: filter by status and vehicle")
    fun getProfilesByStatusAndVehicle() {
        val searchRequest = employeePreconditions.fillSearchDSProfilesRequest(
            darkstoreIds = listOf(Constants.searchContactsDarkstore),
            darkstoreUserStates = listOf(ApiEnum(DarkstoreUserState.NOT_WORKING)),
            vehicleTypes = listOf(ApiEnum(EmployeeVehicleType.CAR))
        )

        val searchResults = employeeActions.searchDarkstoreProfiles(searchRequest).darkstoreUsers

        employeeAssertion
            .checkProfilePresentInDSUsersList(searchResults, Constants.mobile4)
            .checkProfileNotPresentInDSUsersList(searchResults, Constants.mobile6)
            .checkDSUserSatisfyConditions(searchResults, "vehicle", vehicle = listOf(ApiEnum(EmployeeVehicleType.CAR)))
            .checkDSUserSatisfyConditions(searchResults, "state", state = DarkstoreUserState.NOT_WORKING)
    }

    @Test
    @DisplayName("Get DS profiles: filter by status and role")
    fun getProfilesByStatusAndRole() {
        val searchRequest = employeePreconditions.fillSearchDSProfilesRequest(
            darkstoreIds = listOf(Constants.searchContactsDarkstore),
            darkstoreUserStates = listOf(ApiEnum(DarkstoreUserState.WORKING)),
            darkstoreUserRoles = listOf(ApiEnum(DarkstoreUserRole.PICKER))
        )

        val searchResults = employeeActions.searchDarkstoreProfiles(searchRequest).darkstoreUsers

        employeeAssertion
            .checkProfilePresentInDSUsersList(searchResults, Constants.mobile2)
            .checkProfileNotPresentInDSUsersList(searchResults, Constants.mobile3)
            .checkDSUserSatisfyConditions(searchResults, "state", state = DarkstoreUserState.WORKING)
            .checkDSUserSatisfyConditions(searchResults, "role", role = ApiEnum(DarkstoreUserRole.PICKER))
    }

    @Test
    @DisplayName("Get DS profiles: filter by status and staff partner")
    fun getProfilesByStatusAndStaffPartner() {
        val searchRequest = employeePreconditions.fillSearchDSProfilesRequest(
            darkstoreIds = listOf(Constants.searchContactsDarkstore),
            darkstoreUserStates = listOf(ApiEnum(DarkstoreUserState.NOT_WORKING)),
            staffPartnerIds = listOf(Constants.staffPartnerId)
        )

        val searchResults = employeeActions.searchDarkstoreProfiles(searchRequest).darkstoreUsers

        employeeAssertion
            .checkProfileNotPresentInDSUsersList(searchResults, Constants.mobile1)
            .checkProfilePresentInDSUsersList(searchResults, Constants.mobile3)
            .checkDSUserSatisfyConditions(searchResults, "state", state = DarkstoreUserState.NOT_WORKING)
            .checkDSUserSatisfyConditions(
                searchResults,
                "staffPartner",
                staffPartners = listOf(Constants.staffPartnerId)
            )
    }

    @Test
    @DisplayName("Get DS profiles: filter by  vehicle and staff partner")
    fun getProfilesByVehicleAndStaffPartner() {
        val searchRequest = employeePreconditions.fillSearchDSProfilesRequest(
            darkstoreIds = listOf(Constants.searchContactsDarkstore),
            vehicleTypes = listOf(ApiEnum(EmployeeVehicleType.CAR)),
            staffPartnerIds = listOf(Constants.staffPartnerId)
        )

        val searchResults = employeeActions.searchDarkstoreProfiles(searchRequest).darkstoreUsers

        employeeAssertion
            .checkProfileNotPresentInDSUsersList(searchResults, Constants.mobile1)
            .checkProfileNotPresentInDSUsersList(searchResults, Constants.mobile2)
            .checkProfileNotPresentInDSUsersList(searchResults, Constants.mobile3)
            .checkProfileNotPresentInDSUsersList(searchResults, Constants.mobile4)
            .checkProfileNotPresentInDSUsersList(searchResults, Constants.mobile5)
            .checkProfilePresentInDSUsersList(searchResults, Constants.mobile6)
            .checkDSUserSatisfyConditions(searchResults, "vehicle", vehicle = listOf(ApiEnum(EmployeeVehicleType.CAR)))
            .checkDSUserSatisfyConditions(
                searchResults,
                "staffPartner",
                staffPartners = listOf(Constants.staffPartnerId)
            )
    }

    @Test
    @DisplayName("Get DS profiles: filter by role and staffPartner")
    fun getProfilesByRoleAndStaffPartner() {
        val searchRequest = employeePreconditions.fillSearchDSProfilesRequest(
            darkstoreIds = listOf(Constants.searchContactsDarkstore),
            staffPartnerIds = listOf(Constants.staffPartnerId),
            darkstoreUserRoles = listOf(ApiEnum(DarkstoreUserRole.DELIVERYMAN))
        )

        val searchResults = employeeActions.searchDarkstoreProfiles(searchRequest).darkstoreUsers

        employeeAssertion
            .checkProfilePresentInDSUsersList(searchResults, Constants.mobile1)
            .checkProfileNotPresentInDSUsersList(searchResults, Constants.mobile2)
            .checkProfilePresentInDSUsersList(searchResults, Constants.mobile3)
            .checkProfileNotPresentInDSUsersList(searchResults, Constants.mobile4)
            .checkProfileNotPresentInDSUsersList(searchResults, Constants.mobile5)
            .checkProfilePresentInDSUsersList(searchResults, Constants.mobile6)
            .checkDSUserSatisfyConditions(
                searchResults,
                "staffPartner",
                staffPartners = listOf(Constants.staffPartnerId)
            )
            .checkDSUserSatisfyConditions(searchResults, "role", role = ApiEnum(DarkstoreUserRole.DELIVERYMAN))
    }


    @Test
    @DisplayName("Get DS profiles: filter by all params")
    fun getProfilesByAllParams() {
        val searchRequest = employeePreconditions.fillSearchDSProfilesRequest(
            darkstoreIds = listOf(Constants.searchContactsDarkstore),
            darkstoreUserStates = listOf(ApiEnum(DarkstoreUserState.NEW)),
            staffPartnerIds = listOf(Constants.staffPartnerId),
            darkstoreUserRoles = listOf(ApiEnum(DarkstoreUserRole.DELIVERYMAN)),
            vehicleTypes = listOf(ApiEnum(EmployeeVehicleType.CAR))
        )

        val searchResults = employeeActions.searchDarkstoreProfiles(searchRequest).darkstoreUsers

        employeeAssertion
            .checkProfileNotPresentInDSUsersList(searchResults, Constants.mobile1)
            .checkProfileNotPresentInDSUsersList(searchResults, Constants.mobile2)
            .checkProfileNotPresentInDSUsersList(searchResults, Constants.mobile3)
            .checkProfileNotPresentInDSUsersList(searchResults, Constants.mobile4)
            .checkProfileNotPresentInDSUsersList(searchResults, Constants.mobile5)
            .checkProfilePresentInDSUsersList(searchResults, Constants.mobile6)
    }

    @Test
    @DisplayName("Get DS profiles: search by full name")
    fun getProfilesByFullName() {
        val searchRequest = employeePreconditions.fillSearchDSProfilesRequest(
            darkstoreIds = listOf(Constants.searchContactsDarkstore),
            nameLike = "Ivan Ivanov Ivanovich"
        )

        val searchResults = employeeActions.searchDarkstoreProfiles(searchRequest).darkstoreUsers

        employeeAssertion
            .checkProfileNotPresentInDSUsersList(searchResults, Constants.mobile1)
            .checkProfileNotPresentInDSUsersList(searchResults, Constants.mobile2)
            .checkProfilePresentInDSUsersList(searchResults, Constants.mobile3)
            .checkProfileNotPresentInDSUsersList(searchResults, Constants.mobile4)
            .checkProfileNotPresentInDSUsersList(searchResults, Constants.mobile5)
            .checkProfileNotPresentInDSUsersList(searchResults, Constants.mobile6)
    }

    @Test
    @DisplayName("Get DS profiles: search by name")
    fun getProfilesByName() {
        val searchRequest = employeePreconditions.fillSearchDSProfilesRequest(
            darkstoreIds = listOf(Constants.searchContactsDarkstore),
            nameLike = "Maria Petrova"
        )

        val searchResults = employeeActions.searchDarkstoreProfiles(searchRequest).darkstoreUsers

        employeeAssertion
            .checkProfilePresentInDSUsersList(searchResults, Constants.mobile1)
            .checkProfileNotPresentInDSUsersList(searchResults, Constants.mobile2)
            .checkProfileNotPresentInDSUsersList(searchResults, Constants.mobile3)
            .checkProfileNotPresentInDSUsersList(searchResults, Constants.mobile4)
            .checkProfileNotPresentInDSUsersList(searchResults, Constants.mobile5)
            .checkProfileNotPresentInDSUsersList(searchResults, Constants.mobile6)
    }

    @Test
    @DisplayName("Get DS profiles: search by last name")
    fun getProfilesByLastName() {
        val searchRequest = employeePreconditions.fillSearchDSProfilesRequest(
            darkstoreIds = listOf(Constants.searchContactsDarkstore),
            nameLike = "Соколов"
        )

        val searchResults = employeeActions.searchDarkstoreProfiles(searchRequest).darkstoreUsers

        employeeAssertion
            .checkProfileNotPresentInDSUsersList(searchResults, Constants.mobile1)
            .checkProfileNotPresentInDSUsersList(searchResults, Constants.mobile2)
            .checkProfileNotPresentInDSUsersList(searchResults, Constants.mobile3)
            .checkProfilePresentInDSUsersList(searchResults, Constants.mobile4)
            .checkProfileNotPresentInDSUsersList(searchResults, Constants.mobile5)
            .checkProfilePresentInDSUsersList(searchResults, Constants.mobile6)
    }

    @Test
    @DisplayName("Get DS profiles: search by first name")
    fun getProfilesByFirstName() {
        val searchRequest = employeePreconditions.fillSearchDSProfilesRequest(
            darkstoreIds = listOf(Constants.searchContactsDarkstore),
            nameLike = "Anna"
        )

        val searchResults = employeeActions.searchDarkstoreProfiles(searchRequest).darkstoreUsers

        employeeAssertion
            .checkProfileNotPresentInDSUsersList(searchResults, Constants.mobile1)
            .checkProfilePresentInDSUsersList(searchResults, Constants.mobile2)
            .checkProfileNotPresentInDSUsersList(searchResults, Constants.mobile3)
            .checkProfileNotPresentInDSUsersList(searchResults, Constants.mobile4)
            .checkProfileNotPresentInDSUsersList(searchResults, Constants.mobile5)
            .checkProfileNotPresentInDSUsersList(searchResults, Constants.mobile6)
    }

    @Test
    @DisplayName("Get DS profiles: search by middle name")
    fun getProfilesByModdleName() {
        val searchRequest = employeePreconditions.fillSearchDSProfilesRequest(
            darkstoreIds = listOf(Constants.searchContactsDarkstore),
            nameLike = "Сергеевич"
        )

        val searchResults = employeeActions.searchDarkstoreProfiles(searchRequest).darkstoreUsers

        employeeAssertion
            .checkProfileNotPresentInDSUsersList(searchResults, Constants.mobile1)
            .checkProfileNotPresentInDSUsersList(searchResults, Constants.mobile2)
            .checkProfileNotPresentInDSUsersList(searchResults, Constants.mobile3)
            .checkProfileNotPresentInDSUsersList(searchResults, Constants.mobile4)
            .checkProfileNotPresentInDSUsersList(searchResults, Constants.mobile5)
            .checkProfilePresentInDSUsersList(searchResults, Constants.mobile6)
    }

    @Test
    @DisplayName("Get DS profiles: search by part name")
    fun getProfilesByPartName() {
        val searchRequest = employeePreconditions.fillSearchDSProfilesRequest(
            darkstoreIds = listOf(Constants.searchContactsDarkstore),
            nameLike = "Серге"
        )

        val searchResults = employeeActions.searchDarkstoreProfiles(searchRequest).darkstoreUsers

        employeeAssertion
            .checkProfileNotPresentInDSUsersList(searchResults, Constants.mobile1)
            .checkProfileNotPresentInDSUsersList(searchResults, Constants.mobile2)
            .checkProfileNotPresentInDSUsersList(searchResults, Constants.mobile3)
            .checkProfilePresentInDSUsersList(searchResults, Constants.mobile4)
            .checkProfilePresentInDSUsersList(searchResults, Constants.mobile5)
            .checkProfilePresentInDSUsersList(searchResults, Constants.mobile6)
    }

    @Test
    @DisplayName("Get DS profiles: search by name with space")
    fun getProfilesByNameWithSpace() {
        val searchRequest = employeePreconditions.fillSearchDSProfilesRequest(
            darkstoreIds = listOf(Constants.searchContactsDarkstore),
            nameLike = " Сергей "
        )

        val searchResults = employeeActions.searchDarkstoreProfiles(searchRequest).darkstoreUsers

        employeeAssertion
            .checkProfileNotPresentInDSUsersList(searchResults, Constants.mobile1)
            .checkProfileNotPresentInDSUsersList(searchResults, Constants.mobile2)
            .checkProfileNotPresentInDSUsersList(searchResults, Constants.mobile3)
            .checkProfilePresentInDSUsersList(searchResults, Constants.mobile4)
            .checkProfileNotPresentInDSUsersList(searchResults, Constants.mobile5)
            .checkProfileNotPresentInDSUsersList(searchResults, Constants.mobile6)
    }

    @Test
    @DisplayName("Get DS profiles: search by name with nubmers")
    fun getProfilesByNameWithNumbers() {
        val searchRequest = employeePreconditions.fillSearchDSProfilesRequest(
            darkstoreIds = listOf(Constants.searchContactsDarkstore),
            nameLike = "курьерНомер1"
        )

        val searchResults = employeeActions.searchDarkstoreProfiles(searchRequest).darkstoreUsers

        employeeAssertion
            .checkProfileNotPresentInDSUsersList(searchResults, Constants.mobile1)
            .checkProfileNotPresentInDSUsersList(searchResults, Constants.mobile2)
            .checkProfileNotPresentInDSUsersList(searchResults, Constants.mobile3)
            .checkProfilePresentInDSUsersList(searchResults, Constants.mobile4)
            .checkProfileNotPresentInDSUsersList(searchResults, Constants.mobile5)
            .checkProfileNotPresentInDSUsersList(searchResults, Constants.mobile6)
    }

    @Test
    @DisplayName("Get DS profiles: search by name with symbols")
    fun getProfilesByNameWithSymbols() {
        val searchRequest = employeePreconditions.fillSearchDSProfilesRequest(
            darkstoreIds = listOf(Constants.searchContactsDarkstore),
            nameLike = "дефис-отчество"
        )

        val searchResults = employeeActions.searchDarkstoreProfiles(searchRequest).darkstoreUsers

        employeeAssertion
            .checkProfileNotPresentInDSUsersList(searchResults, Constants.mobile1)
            .checkProfileNotPresentInDSUsersList(searchResults, Constants.mobile2)
            .checkProfileNotPresentInDSUsersList(searchResults, Constants.mobile3)
            .checkProfileNotPresentInDSUsersList(searchResults, Constants.mobile4)
            .checkProfilePresentInDSUsersList(searchResults, Constants.mobile5)
            .checkProfileNotPresentInDSUsersList(searchResults, Constants.mobile6)
    }

    @Test
    @DisplayName("Get DS profiles: search by mobile part")
    fun getProfilesByMobilePartBegin() {
        val searchRequest = employeePreconditions.fillSearchDSProfilesRequest(
            darkstoreIds = listOf(Constants.searchContactsDarkstore),
            mobileLike = "7000999"
        )

        val searchResults = employeeActions.searchDarkstoreProfiles(searchRequest).darkstoreUsers

        employeeAssertion
            .checkProfilePresentInDSUsersList(searchResults, Constants.mobile1)
            .checkProfilePresentInDSUsersList(searchResults, Constants.mobile2)
            .checkProfilePresentInDSUsersList(searchResults, Constants.mobile3)
            .checkProfilePresentInDSUsersList(searchResults, Constants.mobile4)
            .checkProfilePresentInDSUsersList(searchResults, Constants.mobile5)
            .checkProfilePresentInDSUsersList(searchResults, Constants.mobile6)
    }

    @Test
    @DisplayName("Get DS profiles: search by mobile part")
    fun getProfilesByMobilePartEnd() {
        val searchRequest = employeePreconditions.fillSearchDSProfilesRequest(
            darkstoreIds = listOf(Constants.searchContactsDarkstore),
            mobileLike = "7755"
        )

        val searchResults = employeeActions.searchDarkstoreProfiles(searchRequest).darkstoreUsers

        employeeAssertion
            .checkProfileNotPresentInDSUsersList(searchResults, Constants.mobile1)
            .checkProfileNotPresentInDSUsersList(searchResults, Constants.mobile2)
            .checkProfileNotPresentInDSUsersList(searchResults, Constants.mobile3)
            .checkProfileNotPresentInDSUsersList(searchResults, Constants.mobile4)
            .checkProfilePresentInDSUsersList(searchResults, Constants.mobile5)
            .checkProfileNotPresentInDSUsersList(searchResults, Constants.mobile6)
    }

    @Test
    @DisplayName("Get DS profiles: search by mobile full")
    fun getProfilesByMobileFull() {
        val searchRequest = employeePreconditions.fillSearchDSProfilesRequest(
            darkstoreIds = listOf(Constants.searchContactsDarkstore),
            mobileLike = "70009997733"
        )

        val searchResults = employeeActions.searchDarkstoreProfiles(searchRequest).darkstoreUsers

        employeeAssertion
            .checkProfileNotPresentInDSUsersList(searchResults, Constants.mobile1)
            .checkProfileNotPresentInDSUsersList(searchResults, Constants.mobile2)
            .checkProfilePresentInDSUsersList(searchResults, Constants.mobile3)
            .checkProfileNotPresentInDSUsersList(searchResults, Constants.mobile4)
            .checkProfileNotPresentInDSUsersList(searchResults, Constants.mobile5)
            .checkProfileNotPresentInDSUsersList(searchResults, Constants.mobile6)
    }

    @Test
    @DisplayName("Get DS profiles: search by name with register")
    fun getProfilesByNameWithRegister() {
        val searchRequest = employeePreconditions.fillSearchDSProfilesRequest(
            darkstoreIds = listOf(Constants.searchContactsDarkstore),
            nameLike = "СоКоЛоВ"
        )

        val searchResults = employeeActions.searchDarkstoreProfiles(searchRequest).darkstoreUsers

        employeeAssertion
            .checkProfileNotPresentInDSUsersList(searchResults, Constants.mobile1)
            .checkProfileNotPresentInDSUsersList(searchResults, Constants.mobile2)
            .checkProfileNotPresentInDSUsersList(searchResults, Constants.mobile3)
            .checkProfilePresentInDSUsersList(searchResults, Constants.mobile4)
            .checkProfileNotPresentInDSUsersList(searchResults, Constants.mobile5)
            .checkProfilePresentInDSUsersList(searchResults, Constants.mobile6)
    }

    @Test
    @DisplayName("Get DS profiles: empty result")
    fun getProfilesEmptyResult() {
        val searchRequest = employeePreconditions.fillSearchDSProfilesRequest(
            darkstoreIds = listOf(Constants.searchContactsDarkstore),
            nameLike = StringAndPhoneNumberGenerator.generateRandomString(10)
        )

        val searchResults = employeeActions.searchDarkstoreProfiles(searchRequest).darkstoreUsers

        employeeAssertion
            .checkListCount(searchResults.count(), 0)
    }

    @Test
    @DisplayName("Get DS profiles: filer by status and search")
    fun getProfilesByStatusAndName() {
        val searchRequest = employeePreconditions.fillSearchDSProfilesRequest(
            darkstoreIds = listOf(Constants.searchContactsDarkstore),
            nameLike = "Соколов",
            darkstoreUserStates = listOf(ApiEnum(DarkstoreUserState.NOT_WORKING)),
        )

        val searchResults = employeeActions.searchDarkstoreProfiles(searchRequest).darkstoreUsers

        employeeAssertion
            .checkProfilePresentInDSUsersList(searchResults, Constants.mobile4)
            .checkProfileNotPresentInDSUsersList(searchResults, Constants.mobile6)
    }

    @Test
    @DisplayName("Get DS profiles: filer by role and search")
    fun getProfilesByRoleAndName() {
        val searchRequest = employeePreconditions.fillSearchDSProfilesRequest(
            darkstoreIds = listOf(Constants.searchContactsDarkstore),
            nameLike = "rova",
            darkstoreUserRoles = listOf(ApiEnum(DarkstoreUserRole.PICKER))
        )

        val searchResults = employeeActions.searchDarkstoreProfiles(searchRequest).darkstoreUsers

        employeeAssertion
            .checkProfileNotPresentInDSUsersList(searchResults, Constants.mobile1)
            .checkProfilePresentInDSUsersList(searchResults, Constants.mobile2)
    }

    @Test
    @DisplayName("Get DS profiles: filer by vehicle and search")
    fun getProfilesByVehicleAndName() {
        val searchRequest = employeePreconditions.fillSearchDSProfilesRequest(
            darkstoreIds = listOf(Constants.searchContactsDarkstore),
            nameLike = "серг",
            vehicleTypes = listOf(ApiEnum(EmployeeVehicleType.CAR))
        )

        val searchResults = employeeActions.searchDarkstoreProfiles(searchRequest).darkstoreUsers

        employeeAssertion
            .checkProfilePresentInDSUsersList(searchResults, Constants.mobile4)
            .checkProfileNotPresentInDSUsersList(searchResults, Constants.mobile5)
    }

    @Test
    @DisplayName("Get DS profiles: filer by staffPartner and search")
    fun getProfilesByStaffPartnerAndName() {
        val searchRequest = employeePreconditions.fillSearchDSProfilesRequest(
            darkstoreIds = listOf(Constants.searchContactsDarkstore),
            nameLike = "серге",
            staffPartnerIds = listOf(Constants.staffPartnerId)
        )

        val searchResults = employeeActions.searchDarkstoreProfiles(searchRequest).darkstoreUsers

        employeeAssertion
            .checkProfileNotPresentInDSUsersList(searchResults, Constants.mobile5)
            .checkProfilePresentInDSUsersList(searchResults, Constants.mobile6)
    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Get DS profile: sorting - default")
    fun getProfileDefaultSorting() {
        val searchRequest = employeePreconditions.fillSearchDSProfilesRequest(
            darkstoreIds = listOf(Constants.searchContactsDarkstore),
            mobileLike = "700099977",
            sortingMode = ApiEnum(SearchDarkstoreUsersSortingMode.DEFAULT),
            darkstoreUserRoles = listOf(ApiEnum(DarkstoreUserRole.DELIVERYMAN))
        )

        val searchResults = employeeActions.searchDarkstoreProfiles(searchRequest).darkstoreUsers
        val expectedSort = listOf(
            Constants.mobile1,
            Constants.mobile6,
            Constants.mobile3,
            Constants.mobile5,
            Constants.mobile4
        )

        employeeAssertion
            .checkDSUserListSorting(searchResults, expectedSort)
    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Get DS profile: sorting - glide")
    fun getProfileGlideSorting() {
        val searchRequest = employeePreconditions.fillSearchDSProfilesRequest(
            darkstoreIds = listOf(Constants.searchContactsDarkstore),
            mobileLike = "700099977",
            sortingMode = ApiEnum(SearchDarkstoreUsersSortingMode.GLIDE),
            darkstoreUserRoles = listOf(ApiEnum(DarkstoreUserRole.DELIVERYMAN))
        )

        val searchResults = employeeActions.searchDarkstoreProfiles(searchRequest).darkstoreUsers
        val expectedSort = listOf(
            Constants.mobile1,
            Constants.mobile3,
            Constants.mobile4,
            Constants.mobile5,
            Constants.mobile6
        )

        employeeAssertion
            .checkDSUserListSorting(searchResults, expectedSort)
    }

    @Test
    @DisplayName("Get DS profile: check result count")
    fun getProfileCheckResultCount() {
        val searchRequest = employeePreconditions.fillSearchDSProfilesRequest(
            darkstoreIds = listOf(Constants.searchContactsDarkstore),
            mobileLike = "700099977"
        )

        val searchResultsCount = employeeActions.searchDarkstoreProfiles(searchRequest).darkstoreUsersCount

        employeeAssertion
            .checkListCount(searchResultsCount, 7)
    }

    @Test
    @DisplayName("Get DS profile: check result fields")
    fun getProfileCheckResultFields() {
        val searchRequest = employeePreconditions.fillSearchDSProfilesRequest(
            darkstoreIds = listOf(Constants.searchContactsDarkstore),
            mobileLike = "700099977",
        )

        val searchResults = employeeActions.searchDarkstoreProfiles(searchRequest).darkstoreUsers

        employeeAssertion
            .checkDSProfileFieldsInList(
                searchResults,
                testUsers.get(Constants.mobile1)!!,
                DarkstoreUserState.NEW,
                version = 1,
                isIntern = true,
                role = DarkstoreUserRole.DELIVERYMAN
            )
            .checkDSProfileFieldsInList(
                searchResults,
                testUsers.get(Constants.mobile2)!!,
                DarkstoreUserState.WORKING,
                version = 2,
                isIntern = false,
                role = DarkstoreUserRole.PICKER
            )
            .checkDSProfileFieldsInList(
                searchResults,
                testUsers.get(Constants.mobile3)!!,
                DarkstoreUserState.NOT_WORKING,
                version = 2,
                isIntern = true,
                role = DarkstoreUserRole.DELIVERYMAN
            )
            .checkDSProfileFieldsInList(
                searchResults,
                testUsers.get(Constants.mobile3)!!,
                DarkstoreUserState.NEW,
                version = 1,
                isIntern = true,
                role = DarkstoreUserRole.PICKER
            )
            .checkDSProfileFieldsInList(
                searchResults,
                testUsers.get(Constants.mobile4)!!,
                DarkstoreUserState.NOT_WORKING,
                version = 2,
                isIntern = true,
                role = DarkstoreUserRole.DELIVERYMAN
            )
            .checkDSProfileFieldsInList(
                searchResults,
                testUsers.get(Constants.mobile5)!!,
                DarkstoreUserState.NOT_WORKING,
                version = 2,
                isIntern = true,
                role = DarkstoreUserRole.DELIVERYMAN
            )
            .checkDSProfileFieldsInList(
                searchResults,
                testUsers.get(Constants.mobile6)!!,
                DarkstoreUserState.NEW,
                version = 1,
                isIntern = true,
                role = DarkstoreUserRole.DELIVERYMAN
            )
    }

    @Test
    @DisplayName("Get DS profile: pagination")
    fun getProfilePagination() {
        val searchRequest1 = employeePreconditions.fillSearchDSProfilesRequest(
            darkstoreIds = listOf(Constants.searchContactsDarkstore),
            mobileLike = "700099977",
            sortingMode = ApiEnum(SearchDarkstoreUsersSortingMode.GLIDE),
            darkstoreUserRoles = listOf(ApiEnum(DarkstoreUserRole.DELIVERYMAN)),
            pagingFilter = PagingFilter(pageSize = 3, pageMark = null)
        )

        val searchResults1 = employeeActions.searchDarkstoreProfiles(searchRequest1)

        val searchRequest2 = employeePreconditions.fillSearchDSProfilesRequest(
            darkstoreIds = listOf(Constants.searchContactsDarkstore),
            mobileLike = "700099977",
            sortingMode = ApiEnum(SearchDarkstoreUsersSortingMode.GLIDE),
            darkstoreUserRoles = listOf(ApiEnum(DarkstoreUserRole.DELIVERYMAN)),
            pagingFilter = PagingFilter(pageSize = 3, pageMark = searchResults1.paging.nextPageMark)
        )

        val searchResults2 = employeeActions.searchDarkstoreProfiles(searchRequest2)
        val expectedSortPage1 = listOf(Constants.mobile1, Constants.mobile3, Constants.mobile4)
        val expectedSortPage2 = listOf(Constants.mobile5, Constants.mobile6)

        employeeAssertion
            .checkDSUserListSorting(searchResults1.darkstoreUsers, expectedSortPage1)
            .checkDSUserListSorting(searchResults2.darkstoreUsers, expectedSortPage2)
    }
}

