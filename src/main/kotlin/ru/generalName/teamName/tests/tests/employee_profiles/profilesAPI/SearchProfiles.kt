package ru.samokat.mysamokat.tests.tests.employee_profiles.profilesAPI

import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.samokat.employeeprofiles.api.common.domain.EmployeeProfileStatus
import ru.samokat.employeeprofiles.api.common.domain.EmployeeVehicleType
import ru.samokat.employeeprofiles.api.common.domain.Vehicle
import ru.samokat.employeeprofiles.api.common.filtering.MobileQueryFilter
import ru.samokat.employeeprofiles.api.profiles.getprofiles.PagingQueryFilter
import ru.samokat.my.domain.employee.EmployeeRole

import ru.samokat.my.rest.api.enum.ApiEnum
import ru.samokat.mysamokat.tests.checkers.EmployeeAssertion
import ru.samokat.mysamokat.tests.dataproviders.Constants
import ru.samokat.mysamokat.tests.dataproviders.helpers.database.employee_profiles_backend.Profile
import ru.samokat.mysamokat.tests.dataproviders.preconditions.EmployeePreconditions
import ru.samokat.mysamokat.tests.helpers.actions.EmployeeActions

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Tag("empro")
class SearchProfiles {


    private lateinit var employeeAssertion: EmployeeAssertion

    @Autowired
    lateinit var employeeActions: EmployeeActions

    private lateinit var employeePreconditions: EmployeePreconditions

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
    @Tags(Tag("smoke"))
    @DisplayName("Search profiles: by mobile (full)")
    fun searchProfileByPhoneNumberFull() {

        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
                email = null
            )
        val createdProfileId = employeeActions.createProfileId(createRequest)
        val profileFromDB = employeeActions.getProfileFromDB(createdProfileId)
        val getProfilesRequest = employeePreconditions.fillGetProfilesRequest(
            mobile = MobileQueryFilter.Exact(Constants.mobile1),
            statuses = listOf(ApiEnum(EmployeeProfileStatus.ENABLED))
        )
        val searchResults = employeeActions.getProfiles(getProfilesRequest.build()).profiles

        employeeAssertion
            .checkListCount(searchResults.count(), 1)
            .checkProfilePresentInList(searchResults, createdProfileId)
            .checkProfileFieldsInList(searchResults, createRequest, createdProfileId)
            .checkTwoDatesAreEqual(
                searchResults.filter { it.profileId == createdProfileId }[0].createdAt,
                profileFromDB[Profile.createdAt]
            )
            .checkTwoDatesAreEqual(
                searchResults.filter { it.profileId == createdProfileId }[0].updatedAt,
                profileFromDB[Profile.updatedAt]
            )
    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Search profiles: by mobile (part)")
    fun searchProfileByPhoneNumberPart() {

        val firstcreateRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
                email = null
            )
        val firstCreatedProfileId = employeeActions.createProfileId(firstcreateRequest)

        val secondcreateRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
                email = null,
                mobile = Constants.mobile2
            )
        val secondCreatedProfileId = employeeActions.createProfileId(secondcreateRequest)

        val getProfilesRequest = employeePreconditions.fillGetProfilesRequest(
            mobile = MobileQueryFilter.Like(Constants.searchMobilePart),
            statuses = listOf(ApiEnum(EmployeeProfileStatus.ENABLED))
        )
        val searchResults = employeeActions.getProfiles(getProfilesRequest.build()).profiles

        employeeAssertion
            .checkProfilePresentInList(searchResults, firstCreatedProfileId)
            .checkProfilePresentInList(searchResults, secondCreatedProfileId)
    }

    @Test
    @DisplayName("Search profiles: by mobile (with +7)")
    fun searchProfileByPhoneNumberWithPlusSeven() {

        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
                email = null
            )
        val createdProfileId = employeeActions.createProfileId(createRequest)

        val getProfilesRequest = employeePreconditions.fillGetProfilesRequest(
            mobile = MobileQueryFilter.Like(Constants.mobile1.asStringWithPlus()),
            statuses = listOf(ApiEnum(EmployeeProfileStatus.ENABLED))
        )
        val searchResults = employeeActions.getProfiles(getProfilesRequest.build()).profiles

        employeeAssertion
            .checkListCount(searchResults.count(), 1)
            .checkProfilePresentInList(searchResults, createdProfileId)
    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Search profiles: by name (full)")
    fun searchProfileByFullName() {

        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
                email = null
            )
        val createdProfileId = employeeActions.createProfileId(createRequest)

        val getProfilesRequest = employeePreconditions.fillGetProfilesRequest(
            nameLike = createRequest.name.firstName + " " + createRequest.name.middleName + " " + createRequest.name.lastName,
            statuses = listOf(ApiEnum(EmployeeProfileStatus.ENABLED))
        )
        val searchResults = employeeActions.getProfiles(getProfilesRequest.build()).profiles

        employeeAssertion
            .checkProfilePresentInList(searchResults, createdProfileId)
    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Search profiles: by name (part)")
    fun searchProfileByPartName() {

        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
                email = null
            )
        val createdProfileId = employeeActions.createProfileId(createRequest)

        val getProfilesRequest = employeePreconditions.fillGetProfilesRequest(
            nameLike = createRequest.name.firstName,
            statuses = listOf(ApiEnum(EmployeeProfileStatus.ENABLED))
        )
        val searchResults = employeeActions.getProfiles(getProfilesRequest.build()).profiles

        employeeAssertion
            .checkProfilePresentInList(searchResults, createdProfileId)
    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Search profiles: by status (disabled)")
    fun searchProfileByDisableStatus() {

        val getProfilesRequest = employeePreconditions.fillGetProfilesRequest(
            statuses = listOf(ApiEnum(EmployeeProfileStatus.DISABLED))
        )
        val searchResults = employeeActions.getProfiles(getProfilesRequest.build()).profiles

        employeeAssertion
            .checkElementsSatisfyConditions(searchResults, "status", status = EmployeeProfileStatus.DISABLED)
    }

    @Test
    @DisplayName("Search profiles: by role - deliveryman")
    fun searchProfileByOneRoleDeliveryman() {

        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
                email = null,
                darkstoreId = Constants.searchProfileDarkstore
            )
        val createdProfileId = employeeActions.createProfileId(createRequest)

        val getProfilesRequest = employeePreconditions.fillGetProfilesRequest(
            roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
            darkstoreId = Constants.searchProfileDarkstore
        )
        val searchResults = employeeActions.getProfiles(getProfilesRequest.build()).profiles

        employeeAssertion
            .checkElementsSatisfyConditions(searchResults, "role", roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)))
            .checkProfilePresentInList(searchResults, createdProfileId)
    }

    @Test
    @DisplayName("Search profiles: by role - goods_manager")
    fun searchProfileByOneRoleGoodsManager() {

        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.GOODS_MANAGER)),
                vehicle = null,
                email = null,
                staffPartnerId = null,
                darkstoreId = Constants.searchProfileDarkstore
            )
        val createdProfileId = employeeActions.createProfileId(createRequest)

        val getProfilesRequest = employeePreconditions.fillGetProfilesRequest(
            roles = listOf(ApiEnum(EmployeeRole.GOODS_MANAGER)),
            darkstoreId = Constants.searchProfileDarkstore
        )
        val searchResults = employeeActions.getProfiles(getProfilesRequest.build()).profiles

        employeeAssertion
            .checkElementsSatisfyConditions(searchResults, "role", roles = listOf(ApiEnum(EmployeeRole.GOODS_MANAGER)))
            .checkProfilePresentInList(searchResults, createdProfileId)
    }

    @Test
    @DisplayName("Search profiles: by role - picker")
    fun searchProfileByOneRolePicker() {
        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.PICKER)),
                vehicle = null,
                email = null,
                darkstoreId = Constants.searchProfileDarkstore
            )
        val createdProfileId = employeeActions.createProfileId(createRequest)

        val getProfilesRequest = employeePreconditions.fillGetProfilesRequest(
            roles = listOf(ApiEnum(EmployeeRole.PICKER)),
            darkstoreId = Constants.searchProfileDarkstore
        )
        val searchResults = employeeActions.getProfiles(getProfilesRequest.build()).profiles

        employeeAssertion
            .checkElementsSatisfyConditions(searchResults, "role", roles = listOf(ApiEnum(EmployeeRole.PICKER)))
            .checkProfilePresentInList(searchResults, createdProfileId)
    }

    @Test
    @DisplayName("Search profiles: by role - darkstore_admin")
    fun searchProfileByOneRoleDarkstoreAdmin() {

        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DARKSTORE_ADMIN)),
                vehicle = null,
                email = null,
                staffPartnerId = null,
                darkstoreId = Constants.searchProfileDarkstore
            )
        val createdProfileId = employeeActions.createProfileId(createRequest)

        val getProfilesRequest = employeePreconditions.fillGetProfilesRequest(
            roles = listOf(ApiEnum(EmployeeRole.DARKSTORE_ADMIN)),
            darkstoreId = Constants.searchProfileDarkstore
        )
        val searchResults = employeeActions.getProfiles(getProfilesRequest.build()).profiles

        employeeAssertion
            .checkElementsSatisfyConditions(
                searchResults,
                "role",
                roles = listOf(ApiEnum(EmployeeRole.DARKSTORE_ADMIN))
            )
            .checkProfilePresentInList(searchResults, createdProfileId)
    }

    @Test
    @DisplayName("Search profiles: by role - coordinator")
    fun searchProfileByOneRoleCoordinator() {

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

        val getProfilesRequest = employeePreconditions.fillGetProfilesRequest(
            roles = listOf(ApiEnum(EmployeeRole.COORDINATOR))
        )
        val searchResults = employeeActions.getProfiles(getProfilesRequest.build()).profiles

        employeeAssertion
            .checkElementsSatisfyConditions(searchResults, "role", roles = listOf(ApiEnum(EmployeeRole.COORDINATOR)))
            .checkProfilePresentInList(searchResults, createdProfileId)
    }

    @Test
    @DisplayName("Search profiles: by role - forwarder")
    fun searchProfileByOneRoleForwarder() {

        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.FORWARDER)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
                email = null,
                staffPartnerId = null,
                darkstoreId = Constants.searchProfileDarkstore
            )
        val createdProfileId = employeeActions.createProfileId(createRequest)

        val getProfilesRequest = employeePreconditions.fillGetProfilesRequest(
            roles = listOf(ApiEnum(EmployeeRole.FORWARDER)),
            darkstoreId = Constants.searchProfileDarkstore
        )
        val searchResults = employeeActions.getProfiles(getProfilesRequest.build()).profiles

        employeeAssertion
            .checkElementsSatisfyConditions(searchResults, "role", roles = listOf(ApiEnum(EmployeeRole.FORWARDER)))
            .checkProfilePresentInList(searchResults, createdProfileId)
    }

    @Test
    @DisplayName("Search profiles: by role - auditor")
    fun searchProfileByOneRoleAuditor() {

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

        val getProfilesRequest = employeePreconditions.fillGetProfilesRequest(
            roles = listOf(ApiEnum(EmployeeRole.AUDITOR))
        )
        val searchResults = employeeActions.getProfiles(getProfilesRequest.build()).profiles

        employeeAssertion
            .checkElementsSatisfyConditions(searchResults, "role", roles = listOf(ApiEnum(EmployeeRole.AUDITOR)))
            .checkProfilePresentInList(searchResults, createdProfileId)
    }

    @Test
    @DisplayName("Search profiles: by role - supervisor")
    fun searchProfileByOneRoleSupervisor() {

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

        val getProfilesRequest = employeePreconditions.fillGetProfilesRequest(
            roles = listOf(ApiEnum(EmployeeRole.SUPERVISOR))
        )
        val searchResults = employeeActions.getProfiles(getProfilesRequest.build()).profiles

        employeeAssertion
            .checkElementsSatisfyConditions(searchResults, "role", roles = listOf(ApiEnum(EmployeeRole.SUPERVISOR)))
            .checkProfilePresentInList(searchResults, createdProfileId)
    }

    @Test
    @DisplayName("Search profiles: by role - staff_manager")
    fun searchProfileByOneRoleStaffManager() {

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

        val getProfilesRequest = employeePreconditions.fillGetProfilesRequest(
            roles = listOf(ApiEnum(EmployeeRole.STAFF_MANAGER))
        )
        val searchResults = employeeActions.getProfiles(getProfilesRequest.build()).profiles

        employeeAssertion
            .checkElementsSatisfyConditions(searchResults, "role", roles = listOf(ApiEnum(EmployeeRole.STAFF_MANAGER)))
            .checkProfilePresentInList(searchResults, createdProfileId)
    }

    @Test
    @DisplayName("Search profiles: by role - tech_support")
    fun searchProfileByOneRoleSupport() {

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

        val getProfilesRequest = employeePreconditions.fillGetProfilesRequest(
            roles = listOf(ApiEnum(EmployeeRole.TECH_SUPPORT))
        )
        val searchResults = employeeActions.getProfiles(getProfilesRequest.build()).profiles

        employeeAssertion
            .checkElementsSatisfyConditions(searchResults, "role", roles = listOf(ApiEnum(EmployeeRole.TECH_SUPPORT)))
            .checkProfilePresentInList(searchResults, createdProfileId)
    }

    @Test
    @DisplayName("Search profiles: by role - territorial_manager")
    fun searchProfileByOneRoleTerritorialManager() {

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

        val getProfilesRequest = employeePreconditions.fillGetProfilesRequest(
            roles = listOf(ApiEnum(EmployeeRole.TERRITORIAL_MANAGER))
        )
        val searchResults = employeeActions.getProfiles(getProfilesRequest.build()).profiles

        employeeAssertion
            .checkElementsSatisfyConditions(
                searchResults,
                "role",
                roles = listOf(ApiEnum(EmployeeRole.TERRITORIAL_MANAGER))
            )
            .checkProfilePresentInList(searchResults, createdProfileId)
    }

    @Test
    @DisplayName("Search profiles: by role - counterparty")
    fun searchProfileByOneRoleCounterparty() {

        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.COUNTERPARTY)),
                vehicle = null,
                darkstoreId = null,
                cityId = null
            )
        val createdProfileId = employeeActions.createProfileId(createRequest)

        val getProfilesRequest = employeePreconditions.fillGetProfilesRequest(
            roles = listOf(ApiEnum(EmployeeRole.COUNTERPARTY))
        )
        val searchResults = employeeActions.getProfiles(getProfilesRequest.build()).profiles

        employeeAssertion
            .checkElementsSatisfyConditions(searchResults, "role", roles = listOf(ApiEnum(EmployeeRole.COUNTERPARTY)))
            .checkProfilePresentInList(searchResults, createdProfileId)
    }

    @Test
    @DisplayName("Search profiles: by role - deliveryman-picker")
    fun searchProfileByOneRoleMultirole() {
        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN), ApiEnum(EmployeeRole.PICKER)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
                email = null,
                darkstoreId = Constants.searchProfileDarkstore
            )

        val createdProfileId = employeeActions.createProfileId(createRequest)

        val getProfilesRequestDeliverymans = employeePreconditions.fillGetProfilesRequest(
            roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
            darkstoreId = Constants.searchProfileDarkstore
        )
        val searchDeliverymansResults = employeeActions.getProfiles(getProfilesRequestDeliverymans.build()).profiles

        val getProfilesRequestPickers = employeePreconditions.fillGetProfilesRequest(
            roles = listOf(ApiEnum(EmployeeRole.PICKER)),
            darkstoreId = Constants.searchProfileDarkstore
        )
        val searchPickersResults = employeeActions.getProfiles(getProfilesRequestPickers.build()).profiles


        employeeAssertion
            .checkProfilePresentInList(searchDeliverymansResults, createdProfileId)
            .checkProfileFieldsInList(searchDeliverymansResults, createRequest, createdProfileId)
            .checkElementsSatisfyConditions(
                searchDeliverymansResults,
                "role",
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN))
            )
            .checkProfilePresentInList(searchPickersResults, createdProfileId)
            .checkProfileFieldsInList(searchPickersResults, createRequest, createdProfileId)
            .checkElementsSatisfyConditions(searchPickersResults, "role", roles = listOf(ApiEnum(EmployeeRole.PICKER)))
    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Search profiles: by role (several roles)")
    fun searchProfileBySeveralRoles() {

        val createDeliverymanBuilder = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                email = null,
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
                darkstoreId = Constants.searchProfileDarkstore,
                mobile = Constants.mobile1
            )
        val createdDeliverymanProfileId = employeeActions.createProfileId(createDeliverymanBuilder)

        val createPickerBuilder = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.PICKER)),
                vehicle = null,
                email = null,
                darkstoreId = Constants.searchProfileDarkstore,
                mobile = Constants.mobile2
            )
        val createdPickerProfileId = employeeActions.createProfileId(createPickerBuilder)

        val getProfilesRequest = employeePreconditions.fillGetProfilesRequest(
            roles = listOf(ApiEnum(EmployeeRole.PICKER), ApiEnum(EmployeeRole.DELIVERYMAN)),
            darkstoreId = Constants.searchProfileDarkstore
        )
        val searchResults = employeeActions.getProfiles(getProfilesRequest.build()).profiles

        employeeAssertion
            .checkProfilePresentInList(searchResults, createdDeliverymanProfileId)
            .checkProfilePresentInList(searchResults, createdPickerProfileId)
    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Search profiles: several params (name + mobile)")
    fun searchProfileByPhoneAndName() {

        val createRequest1 = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
                email = null,
                mobile = Constants.mobile1
            )

        val createRequest2 = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
                email = null,
                name = createRequest1.name,
                mobile = Constants.mobile2
            )
        val createdProfileId1 = employeeActions.createProfileId(createRequest1)
        val createdProfileId2 = employeeActions.createProfileId(createRequest2)

        val getProfilesRequest = employeePreconditions.fillGetProfilesRequest(
            mobile = MobileQueryFilter.Exact(Constants.mobile1),
            nameLike = createRequest1.name.firstName + " " + createRequest1.name.lastName,
            statuses = listOf(ApiEnum(EmployeeProfileStatus.ENABLED))
        )
        val searchResults = employeeActions.getProfiles(getProfilesRequest.build()).profiles

        employeeAssertion
            .checkProfilePresentInList(searchResults, createdProfileId1)
            .checkProfileIsNotPresentInList(searchResults, createdProfileId2)
    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Search profiles: pagination")
    fun searchProfilePagination() {

        val getProfilesRequestWithoutPaging = employeePreconditions.fillGetProfilesRequest(
            statuses = listOf(ApiEnum(EmployeeProfileStatus.ENABLED)),
            darkstoreId = Constants.searchProfileDarkstore
        )
        val searchResultsFull = employeeActions.getProfiles(getProfilesRequestWithoutPaging.build()).profiles

        val pageSize = Math.ceil(searchResultsFull.count().toDouble() / 3).toInt()

        val getProfilesRequestWithPagingMore = employeePreconditions.fillGetProfilesRequest(
            statuses = listOf(ApiEnum(EmployeeProfileStatus.ENABLED)),
            darkstoreId = Constants.searchProfileDarkstore,
            paging = PagingQueryFilter(pageSize = searchResultsFull.count() + 1)
        )
        val searchResultsPageMore = employeeActions.getProfiles(getProfilesRequestWithPagingMore.build())

        val getProfilesRequestWithPagingEquals = employeePreconditions.fillGetProfilesRequest(
            statuses = listOf(ApiEnum(EmployeeProfileStatus.ENABLED)),
            darkstoreId = Constants.searchProfileDarkstore,
            paging = PagingQueryFilter(pageSize = searchResultsFull.count())
        )
        val searchResultsPageEquals = employeeActions.getProfiles(getProfilesRequestWithPagingEquals.build())

        val getProfilesRequestWithPagingLess1 = employeePreconditions.fillGetProfilesRequest(
            statuses = listOf(ApiEnum(EmployeeProfileStatus.ENABLED)),
            darkstoreId = Constants.searchProfileDarkstore,
            paging = PagingQueryFilter(pageSize = pageSize)
        )
        val searchResultsPageLess1 = employeeActions.getProfiles(getProfilesRequestWithPagingLess1.build())

        val getProfilesRequestWithPagingLess2 = employeePreconditions.fillGetProfilesRequest(
            statuses = listOf(ApiEnum(EmployeeProfileStatus.ENABLED)),
            darkstoreId = Constants.searchProfileDarkstore,
            paging = PagingQueryFilter(pageSize = pageSize, pageMark = searchResultsPageLess1.paging.nextPageMark)
        )
        val searchResultsPageLess2 = employeeActions.getProfiles(getProfilesRequestWithPagingLess2.build())

        val getProfilesRequestWithPagingLess3 = employeePreconditions.fillGetProfilesRequest(
            statuses = listOf(ApiEnum(EmployeeProfileStatus.ENABLED)),
            darkstoreId = Constants.searchProfileDarkstore,
            paging = PagingQueryFilter(pageSize = pageSize, pageMark = searchResultsPageLess2.paging.nextPageMark)
        )
        val searchResultsPageLess3 = employeeActions.getProfiles(getProfilesRequestWithPagingLess3.build())


        employeeAssertion
            .checkListCount(searchResultsPageMore.profiles.count(), searchResultsFull.count())
            .checkPageMarkIsNull(searchResultsPageMore)
            .checkListCount(searchResultsPageEquals.profiles.count(), searchResultsFull.count())
            .checkPageMarkIsNull(searchResultsPageEquals)
            .checkListCount(
                searchResultsPageLess1.profiles.count() + searchResultsPageLess2.profiles.count() + searchResultsPageLess3.profiles.count(),
                searchResultsFull.count()
            )
    }

    @Test
    @DisplayName("Search profiles: search without params")
    fun searchProfileWithoutParams() {

        val getProfilesRequest = employeePreconditions.fillGetProfilesRequest()
        val searchResults = employeeActions.getProfiles(getProfilesRequest.build()).profiles

        employeeAssertion
            .checkListCount(searchResults.count(), getProfilesRequest.getPaging().pageSize!!)
    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Search profiles: no results")
    fun searchProfileNoResults() {
        val getProfilesRequest = employeePreconditions.fillGetProfilesRequest(
            statuses = listOf(ApiEnum(EmployeeProfileStatus.ENABLED)),
            mobile = MobileQueryFilter.Exact(Constants.mobile1),
        )

        employeeActions.deleteProfile(Constants.mobile1)
        val searchResults = employeeActions.getProfiles(getProfilesRequest.build()).profiles

        employeeAssertion
            .checkListCount(searchResults.count(), 0)
    }

}
