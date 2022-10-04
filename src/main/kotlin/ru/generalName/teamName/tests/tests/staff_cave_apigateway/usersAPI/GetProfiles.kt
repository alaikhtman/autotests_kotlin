package ru.samokat.mysamokat.tests.tests.staff_cave_apigateway.usersAPI

import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.samokat.employeeprofiles.api.common.domain.EmployeeVehicleType
import ru.samokat.my.domain.employee.EmployeeName
import ru.samokat.my.domain.employee.EmployeeRole
import ru.samokat.my.rest.api.enum.ApiEnum
import ru.samokat.mysamokat.tests.checkers.CommonAssertion
import ru.samokat.mysamokat.tests.checkers.StaffCaveApiGWAssertions
import ru.samokat.mysamokat.tests.dataproviders.Constants
import ru.samokat.mysamokat.tests.dataproviders.apiModels.staffcave.users.Vehicle
import ru.samokat.mysamokat.tests.dataproviders.preconditions.CommonPreconditions
import ru.samokat.mysamokat.tests.dataproviders.preconditions.EmployeePreconditions
import ru.samokat.mysamokat.tests.dataproviders.preconditions.StaffCaveApiGWPreconditions
import ru.samokat.mysamokat.tests.helpers.actions.EmployeeActions
import ru.samokat.mysamokat.tests.helpers.actions.StaffCaveApiGWActions

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Tags(Tag("staff-cave-apigateway"))
class GetProfiles {

    @Autowired
    private lateinit var scActions: StaffCaveApiGWActions

    @Autowired
    private lateinit var employeeActions: EmployeeActions

    @Autowired
    private lateinit var commonPreconditions: CommonPreconditions

    private lateinit var commonAssertions: CommonAssertion

    private lateinit var scPreconditions: StaffCaveApiGWPreconditions

    private lateinit var scAssertion: StaffCaveApiGWAssertions

    private lateinit var token: String

    private lateinit var employeePreconditions: EmployeePreconditions

    @BeforeEach
    fun before() {
        scPreconditions = StaffCaveApiGWPreconditions()
        employeePreconditions = EmployeePreconditions()
        scAssertion = StaffCaveApiGWAssertions()
        commonAssertions = CommonAssertion()
        employeeActions.deleteProfile(Constants.mobile1)
        employeeActions.deleteProfile(Constants.mobile2)
        employeeActions.deleteProfile(Constants.mobile3)
        employeeActions.deleteProfile(Constants.mobile4)
        getAuthToken()
    }

    @AfterEach
    fun release() {
        scAssertion.assertAll()
        commonAssertions.assertAll()
        employeeActions.deleteProfile(Constants.mobile1)
        employeeActions.deleteProfile(Constants.mobile2)
        employeeActions.deleteProfile(Constants.mobile3)
        employeeActions.deleteProfile(Constants.mobile4)
    }

    fun getAuthToken(role: EmployeeRole = EmployeeRole.TECH_SUPPORT) {
        employeeActions.deleteProfile(Constants.mobile1)
        val profile = commonPreconditions.createProfile(
            roles = listOf(ApiEnum(role))
        )
        val authRequest = scPreconditions.fillAuthRequest(password = profile.generatedPassword!!)
        token = scActions.authProfilePassword(authRequest).accessToken

    }


    @Test
    @Tags(Tag("smoke"), Tag("emproIntegration"))
    @DisplayName("Search profiles: check user data - deliveryman")
    fun searchProfileCheckUserDataDeliverymanTest() {

        val createUserRequest = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
            darkstoreId = Constants.darkstoreId,
            staffPartnerId = Constants.staffPartnerId,
            vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR))
        )
        val createdUser = scActions.createUser(token, createUserRequest)!!

        val searchResults = scActions.searchProfilesByMobile(token, mobile = Constants.mobile2.asStringWithoutPlus())!!

        scAssertion
            .checkUserDataInSearchResults(createUserRequest, createdUser, searchResults)
            .checkUsersListCount(searchResults, 1)
    }

    @Test
    @DisplayName("Search profiles: check user data - picker (by staff manager)")
    fun searchProfileCheckUserDataPickerTest() {
        getAuthToken(EmployeeRole.STAFF_MANAGER)

        val createUserRequest = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.PICKER)),
            darkstoreId = Constants.darkstoreId,
            staffPartnerId = Constants.staffPartnerId
        )
        val createdUser = scActions.createUser(token, createUserRequest)!!

        val searchResults = scActions.searchProfilesByMobile(token, mobile = Constants.mobile2.asStringWithoutPlus())!!

        scAssertion
            .checkUserDataInSearchResults(createUserRequest, createdUser, searchResults)
            .checkUsersListCount(searchResults, 1)
    }

    @Test
    @DisplayName("Search profiles: check user data - darkstore_admin")
    fun searchProfileCheckUserDataDarkstoreAdminTest() {

        val createUserRequest = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.DARKSTORE_ADMIN)),
            darkstoreId = Constants.darkstoreId
        )
        val createdUser = scActions.createUser(token, createUserRequest)!!

        val searchResults = scActions.searchProfilesByMobile(token, mobile = Constants.mobile2.asStringWithoutPlus())!!

        scAssertion
            .checkUserDataInSearchResults(createUserRequest, createdUser, searchResults)
            .checkUsersListCount(searchResults, 1)
    }

    @Test
    @DisplayName("Search profiles: check user data - goods_manager")
    fun searchProfileCheckUserDataGoodsManagerTest() {

        val createUserRequest = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.GOODS_MANAGER)),
            darkstoreId = Constants.darkstoreId
        )
        val createdUser = scActions.createUser(token, createUserRequest)!!

        val searchResults = scActions.searchProfilesByMobile(token, mobile = Constants.mobile2.asStringWithoutPlus())!!

        scAssertion
            .checkUserDataInSearchResults(createUserRequest, createdUser, searchResults)
            .checkUsersListCount(searchResults, 1)
    }

    @Test
    @DisplayName("Search profiles: check user data - forwarder")
    fun searchProfileCheckUserDataForwarderTest() {

        val createUserRequest = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.FORWARDER)),
            darkstoreId = Constants.darkstoreId,
            vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR))
        )
        val createdUser = scActions.createUser(token, createUserRequest)!!

        val searchResults = scActions.searchProfilesByMobile(token, mobile = Constants.mobile2.asStringWithoutPlus())!!

        scAssertion
            .checkUserDataInSearchResults(createUserRequest, createdUser, searchResults)
            .checkUsersListCount(searchResults, 1)
    }

    @Test
    @DisplayName("Search profiles: check user data - terr manager")
    fun searchProfileCheckUserDataTerrManagerTest() {

        val createUserRequest = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.TERRITORIAL_MANAGER))
        )
        val createdUser = scActions.createUser(token, createUserRequest)!!

        val searchResults = scActions.searchProfilesByMobile(token, mobile = Constants.mobile2.asStringWithoutPlus())!!

        scAssertion
            .checkUserDataInSearchResults(createUserRequest, createdUser, searchResults)
            .checkUsersListCount(searchResults, 1)
    }

    @Test
    @DisplayName("Search profiles: check user data - supervisor")
    fun searchProfileCheckUserDataSupervisorTest() {

        val createUserRequest = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.SUPERVISOR))
        )
        val createdUser = scActions.createUser(token, createUserRequest)!!

        val searchResults = scActions.searchProfilesByMobile(token, mobile = Constants.mobile2.asStringWithoutPlus())!!

        scAssertion
            .checkUserDataInSearchResults(createUserRequest, createdUser, searchResults)
            .checkUsersListCount(searchResults, 1)
    }

    @Test
    @DisplayName("Search profiles: check user data - tech support")
    fun searchProfileCheckUserDataTechSupportTest() {

        val createUserRequest = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.TECH_SUPPORT))
        )
        val createdUser = scActions.createUser(token, createUserRequest)!!

        val searchResults = scActions.searchProfilesByMobile(token, mobile = Constants.mobile2.asStringWithoutPlus())!!

        scAssertion
            .checkUserDataInSearchResults(createUserRequest, createdUser, searchResults)
            .checkUsersListCount(searchResults, 1)
    }

    @Test
    @DisplayName("Search profiles: check user data - auditor")
    fun searchProfileCheckUserDataAuditorTest() {

        val createUserRequest = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.AUDITOR))
        )
        val createdUser = scActions.createUser(token, createUserRequest)!!

        val searchResults = scActions.searchProfilesByMobile(token, mobile = Constants.mobile2.asStringWithoutPlus())!!

        scAssertion
            .checkUserDataInSearchResults(createUserRequest, createdUser, searchResults)
            .checkUsersListCount(searchResults, 1)
    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Search profiles: check user data - deliveryman-picker")
    fun searchProfileCheckUserDataDeliverymanPickerTest() {

        val createUserRequest = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN), ApiEnum(EmployeeRole.PICKER)),
            darkstoreId = Constants.darkstoreId,
            staffPartnerId = Constants.staffPartnerId,
            vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR))
        )
        val createdUser = scActions.createUser(token, createUserRequest)!!

        val searchResults = scActions.searchProfilesByMobile(token, mobile = Constants.mobile2.asStringWithoutPlus())!!

        scAssertion
            .checkUserDataInSearchResults(createUserRequest, createdUser, searchResults)
            .checkUsersListCount(searchResults, 1)
    }

    @Test
    @DisplayName("Search profiles: check user data - staff manager")
    fun searchProfileCheckUserDataStaffManagerTest() {

        val createUserRequest = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.STAFF_MANAGER))
        )
        val createdUser = scActions.createUser(token, createUserRequest)!!

        val searchResults = scActions.searchProfilesByMobile(token, mobile = Constants.mobile2.asStringWithoutPlus())!!

        scAssertion
            .checkUserDataInSearchResults(createUserRequest, createdUser, searchResults)
            .checkUsersListCount(searchResults, 1)
    }

    @Test
    @DisplayName("Search profiles: check user data - coordinator")
    fun searchProfileCheckUserDataCoordinatorTest() {

        val createUserRequest = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.COORDINATOR)),
            supervisedDarkstores = mutableListOf(Constants.darkstoreId, Constants.updatedDarkstoreId)
        )
        val createdUser = scActions.createUser(token, createUserRequest)!!

        val searchResults = scActions.searchProfilesByMobile(token, mobile = Constants.mobile2.asStringWithoutPlus())!!

        scAssertion
            .checkUserDataInSearchResults(createUserRequest, createdUser, searchResults)
            .checkUsersListCount(searchResults, 1)
    }

    @Test
    @DisplayName("Search profiles: by part mobile")
    fun searchProfileByPartMobileTest() {

        val createUserRequest1 = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.SUPERVISOR))
        )
        val createUserRequest2 = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.SUPERVISOR)),
            mobile = Constants.mobile3
        )
        val createdUser1 = scActions.createUser(token, createUserRequest1)!!
        val createdUser2 = scActions.createUser(token, createUserRequest2)!!

        val searchResults1 = scActions.searchProfilesByMobile(token, mobile = Constants.searchMobilePart)!!
        val searchResults2 = scActions.searchProfilesByMobile(token, mobile = "7000999773")!!

        scAssertion
            .checkUserPresentInList(searchResults1, createdUser1)
            .checkUserPresentInList(searchResults1, createdUser2)
            .checkUserPresentInList(searchResults2, createdUser2)
            .checkUserNotPresentInList(searchResults2, createdUser1)
    }

    @Test
    @DisplayName("Search profiles: by mobile with +7")
    fun searchProfileByMobileWithSevenTest() {

        val createUserRequest1 = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.SUPERVISOR))
        )
        val createUserRequest2 = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.SUPERVISOR)),
            mobile = Constants.mobile3
        )
        val createdUser1 = scActions.createUser(token, createUserRequest1)!!
        val createdUser2 = scActions.createUser(token, createUserRequest2)!!

        val searchResults = scActions.searchProfilesByMobile(token, mobile = Constants.mobile2.asStringWithPlus())!!

        scAssertion
            .checkUserPresentInList(searchResults, createdUser1)
            .checkUserNotPresentInList(searchResults, createdUser2)
    }

    @Test
    @Tag("emproIntegration")
    @DisplayName("Search profiles: by full name")
    fun searchProfileByFullNameTest() {

        val createUserRequest1 = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.SUPERVISOR)),
            name = EmployeeName("ИвановАвто", "ИванТесты", "Петрович")
        )
        val createUserRequest2 = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.SUPERVISOR)),
            mobile = Constants.mobile3,
            name = EmployeeName("ИвановАвто", "ИванТесты", "Семенович")
        )
        val createdUser1 = scActions.createUser(token, createUserRequest1)!!
        val createdUser2 = scActions.createUser(token, createUserRequest2)!!

        val searchResults = scActions.searchProfilesByName(token, "ИвановАвто ИванТесты Петрович")!!

        scAssertion
            .checkUserPresentInList(searchResults, createdUser1)
            .checkUserNotPresentInList(searchResults, createdUser2)
    }

    @Test
    @DisplayName("Search profiles: by part name")
    fun searchProfileByPartNameTest() {

        val createUserRequest1 = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.SUPERVISOR)),
            name = EmployeeName("ИвановАвто", "ИванТесты", "Петрович")
        )
        val createUserRequest2 = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.SUPERVISOR)),
            mobile = Constants.mobile3,
            name = EmployeeName("ИвановАвто", "ИванТесты", "Семенович")
        )
        val createdUser1 = scActions.createUser(token, createUserRequest1)!!
        val createdUser2 = scActions.createUser(token, createUserRequest2)!!

        val searchResults = scActions.searchProfilesByName(token, "ИвановАвто ИванТесты")!!

        scAssertion
            .checkUserPresentInList(searchResults, createdUser1)
            .checkUserPresentInList(searchResults, createdUser2)
    }

    @Test
    @DisplayName("Search profiles: by mobile and name")
    fun searchProfileByMobileAndNameTest() {

        val createUserRequest1 = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.SUPERVISOR)),
            name = EmployeeName("ИвановАвто", "ИванТесты", "Петрович")
        )
        val createUserRequest2 = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.SUPERVISOR)),
            mobile = Constants.mobile3,
            name = EmployeeName("ИвановАвто", "ИванТесты", "Семенович")
        )
        val createdUser1 = scActions.createUser(token, createUserRequest1)!!
        val createdUser2 = scActions.createUser(token, createUserRequest2)!!

        val searchResults = scActions.searchProfilesByMobileAndName(token, Constants.mobile3.asStringWithoutPlus(), "ИвановАвто ИванТесты")!!

        scAssertion
            .checkUserNotPresentInList(searchResults, createdUser1)
            .checkUserPresentInList(searchResults, createdUser2)
    }

    @Test
    @DisplayName("Search profiles: disabled profiles")
    fun searchProfileDisabledTest() {

        val createUserRequest = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.SUPERVISOR))
        )
        val createdUser = scActions.createUser(token, createUserRequest)!!
        employeeActions.deleteProfile(createdUser.user.userId)

        val searchResults = scActions.searchProfilesByMobile(token, mobile = Constants.mobile2.asStringWithoutPlus())!!

        scAssertion
            .checkUsersListCount(searchResults, 0)
    }

    @Test
    @DisplayName("Search profiles: without parameters")
    fun searchProfileWithoutParameters(){
        val searchResults = scActions.searchProfilesWithoutParameters(token)!!

        scAssertion.checkUsersListCount(searchResults, 50)

    }


    @Test
    @DisplayName("Search profiles: pagination")
    fun searchProfilePagination(){
        val createUserRequest1 = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.SUPERVISOR))
        )
        val createUserRequest2 = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.SUPERVISOR)),
            mobile = Constants.mobile3
        )
        val createUserRequest3 = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.SUPERVISOR)),
            mobile = Constants.mobile4
        )
        val createdUser1 = scActions.createUser(token, createUserRequest1)!!
        val createdUser2 = scActions.createUser(token, createUserRequest2)!!
        val createdUser3 = scActions.createUser(token, createUserRequest3)!!

        val searchPage1 = scActions.searchProfilesByMobileWithPageSize(token, mobile = Constants.searchMobilePart, 2)!!
        val searchPage2 = scActions.searchProfilesByMobileWithPageSizeAndMark(token, mobile = Constants.searchMobilePart, 2, searchPage1.paging.nextPageMark!!)!!

        scAssertion
            .checkUserNotPresentInList(searchPage1, createdUser1)
            .checkUserPresentInList(searchPage1, createdUser2)
            .checkUserPresentInList(searchPage1, createdUser3)
            .checkUserPresentInList(searchPage2, createdUser1)
            .checkUserNotPresentInList(searchPage2, createdUser2)
            .checkUserNotPresentInList(searchPage2, createdUser3)
            .checkUsersListCount(searchPage1, 2)
            .checkUsersListCount(searchPage2, 2)

    }
}