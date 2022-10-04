package ru.samokat.mysamokat.tests.tests.hr_platform.userPreferencesAPI

import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.samokat.my.domain.employee.EmployeeRole
import ru.samokat.my.rest.api.enum.ApiEnum
import ru.samokat.mysamokat.tests.checkers.HrpMapAssertions
import ru.samokat.mysamokat.tests.dataproviders.Constants
import ru.samokat.mysamokat.tests.dataproviders.preconditions.CommonPreconditions
import ru.samokat.mysamokat.tests.dataproviders.preconditions.HrpMapPreconditions
import ru.samokat.mysamokat.tests.helpers.actions.EmployeeActions
import ru.samokat.mysamokat.tests.helpers.actions.HrpMapActions

@SpringBootTest
@Tag("hrp_map")
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class FetchPreferredCities {

    @Autowired
    private lateinit var commonPreconditions: CommonPreconditions
    private lateinit var hrpPreconditions: HrpMapPreconditions

    @Autowired
    private lateinit var hrpActions: HrpMapActions

    @Autowired
    private lateinit var employeeActions: EmployeeActions

    private lateinit var hrpAssertion: HrpMapAssertions

    @BeforeEach
    fun before() {
        hrpActions.deleteDarkstoreFromDB(Constants.hrpTestDarktoreId1)
        hrpActions.deleteDarkstoreFromDB(Constants.hrpTestDarktoreId2)
        hrpActions.deleteCityFromDB(Constants.hrpTestCityId)
        hrpActions.deleteCityFromDB(Constants.hrpTestCityId1)
        employeeActions.deleteProfile(Constants.mobile1)
        employeeActions.deletePartner("HRP-Map Partner")
        employeeActions.deletePartner("HRP-Map Partner2")
        hrpPreconditions = HrpMapPreconditions()
        hrpAssertion = HrpMapAssertions()
    }

    @AfterEach
    fun release() {
        hrpActions.deleteDarkstoreFromDB(Constants.hrpTestDarktoreId1)
        hrpActions.deleteDarkstoreFromDB(Constants.hrpTestDarktoreId2)
        hrpActions.deleteCityFromDB(Constants.hrpTestCityId)
        hrpActions.deleteCityFromDB(Constants.hrpTestCityId1)
        employeeActions.deleteProfile(Constants.mobile1)
        employeeActions.deletePartner("HRP-Map Partner")
        employeeActions.deletePartner("HRP-Map Partner2")
        hrpAssertion.assertAll()

    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Fetch Preferred Cities: get city with planned darkstore")
    fun fetchPreferredCitiesPlannedDarkstoreTest() {

        hrpActions.createCityInDatabase()
        hrpActions.createDarkstoreInDatabase()
        val counterparty = commonPreconditions.createStaffPartner()
        val submitRequest = hrpPreconditions.fillSubmitAuthorizedCitiesRequest(
            listOf(counterparty), listOf(Constants.hrpTestCityId))
        hrpActions.submitCounterpartyPreferencesCities(submitRequest)

        val employee = commonPreconditions.createProfile(
            roles = listOf(ApiEnum(EmployeeRole.COUNTERPARTY)),
            staffPartnerId = counterparty,
            darkstoreId = null,
            email = "test@test.ru"
        ).profileId

        val userPreferencesRequest = hrpPreconditions.fillUpdateUserPreferredCityRequest(employee, listOf(Constants.hrpTestCityId))
        hrpActions.submitUserPreferredCities(userPreferencesRequest)

        val cities = hrpActions.fetchUserPreferredCities(employee)

        hrpAssertion.checkUserPreferences(cities, listOf(Constants.hrpTestCityId))
    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Fetch Preferred Cities: get city with operating hub")
    fun fetchPreferredCitiesOperatingHubTest(){
        hrpActions.createCityInDatabase()
        hrpActions.createDarkstoreInDatabase(type = "HUB", status = "PLANNED")
        val counterparty = commonPreconditions.createStaffPartner()
        val submitRequest = hrpPreconditions.fillSubmitAuthorizedCitiesRequest(
            listOf(counterparty), listOf(Constants.hrpTestCityId))
        hrpActions.submitCounterpartyPreferencesCities(submitRequest)

        val employee = commonPreconditions.createProfile(
            roles = listOf(ApiEnum(EmployeeRole.COUNTERPARTY)),
            staffPartnerId = counterparty,
            darkstoreId = null,
            email = "test@test.ru"
        ).profileId

        val userPreferencesRequest = hrpPreconditions.fillUpdateUserPreferredCityRequest(employee, listOf(Constants.hrpTestCityId))
        hrpActions.submitUserPreferredCities(userPreferencesRequest)

        val cities = hrpActions.fetchUserPreferredCities(employee)

        hrpAssertion.checkUserPreferences(cities, listOf(Constants.hrpTestCityId))
    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Fetch Preferred Cities: list of cities")
    fun fetchPreferredCitiesListTest(){
        hrpActions.createCityInDatabase()
        hrpActions.createCityInDatabase(Constants.hrpTestCityId1)
        hrpActions.createDarkstoreInDatabase()
        hrpActions.createDarkstoreInDatabase(cityId = Constants.hrpTestCityId1, darkstoreId = Constants.hrpTestDarktoreId2)
        val counterparty = commonPreconditions.createStaffPartner()
        val submitRequest = hrpPreconditions.fillSubmitAuthorizedCitiesRequest(
            listOf(counterparty), listOf(Constants.hrpTestCityId, Constants.hrpTestCityId1))
        hrpActions.submitCounterpartyPreferencesCities(submitRequest)

        val employee = commonPreconditions.createProfile(
            roles = listOf(ApiEnum(EmployeeRole.COUNTERPARTY)),
            staffPartnerId = counterparty,
            darkstoreId = null,
            email = "test@test.ru"
        ).profileId

        val userPreferencesRequest = hrpPreconditions.fillUpdateUserPreferredCityRequest(employee, listOf(Constants.hrpTestCityId, Constants.hrpTestCityId1))
        hrpActions.submitUserPreferredCities(userPreferencesRequest)

        val cities = hrpActions.fetchUserPreferredCities(employee)

        hrpAssertion.checkUserPreferences(cities, listOf(Constants.hrpTestCityId, Constants.hrpTestCityId1))
    }

    @Test
    @DisplayName("Fetch Preferred Cities: user does not has preferred cities")
    fun fetchPreferredCitiesEmptyTest(){
        hrpActions.createCityInDatabase()
        hrpActions.createDarkstoreInDatabase()
        val counterparty = commonPreconditions.createStaffPartner()
        val submitRequest = hrpPreconditions.fillSubmitAuthorizedCitiesRequest(
            listOf(counterparty), listOf(Constants.hrpTestCityId))
        hrpActions.submitCounterpartyPreferencesCities(submitRequest)

        val employee = commonPreconditions.createProfile(
            roles = listOf(ApiEnum(EmployeeRole.COUNTERPARTY)),
            staffPartnerId = counterparty,
            darkstoreId = null,
            email = "test@test.ru"
        ).profileId

        val cities = hrpActions.fetchUserPreferredCities(employee)

        hrpAssertion.checkUserPreferences(cities, listOf())
    }

    @Test
    @DisplayName("Fetch Preferred Cities: city not exists in cities table")
    fun fetchPreferredCitiesNotExistsTest(){

        hrpActions.createCityInDatabase()
        hrpActions.createDarkstoreInDatabase()
        val counterparty = commonPreconditions.createStaffPartner()
        val submitRequest = hrpPreconditions.fillSubmitAuthorizedCitiesRequest(
            listOf(counterparty), listOf(Constants.hrpTestCityId))
        hrpActions.submitCounterpartyPreferencesCities(submitRequest)

        val employee = commonPreconditions.createProfile(
            roles = listOf(ApiEnum(EmployeeRole.COUNTERPARTY)),
            staffPartnerId = counterparty,
            darkstoreId = null,
            email = "test@test.ru"
        ).profileId

        val userPreferencesRequest = hrpPreconditions.fillUpdateUserPreferredCityRequest(employee, listOf(Constants.hrpTestCityId))
        hrpActions.submitUserPreferredCities(userPreferencesRequest)
        hrpActions.deleteDarkstoreFromDB(Constants.hrpTestDarktoreId1)
        hrpActions.deleteCityFromDB(Constants.hrpTestCityId)

        val cities = hrpActions.fetchUserPreferredCities(employee)

        hrpAssertion.checkUserPreferences(cities, listOf())
    }

}