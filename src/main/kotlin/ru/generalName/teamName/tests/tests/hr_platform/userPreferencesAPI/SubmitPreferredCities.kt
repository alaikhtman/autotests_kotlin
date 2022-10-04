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
import java.util.*

@SpringBootTest
@Tag("hrp_map")
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class SubmitPreferredCities {


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
    @DisplayName("Submit Preferred Cities: city with operating darkstore")
    fun submitPreferredCitiesOperatingDarkstoreTest() {

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

        val savedPreferences = hrpActions.getUserPreferredCitiesFromDatabase(employee)

        hrpAssertion.checkUserPreferencesInDatabase(savedPreferences, listOf(Constants.hrpTestCityId))
    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Submit Preferred Cities: city with planned hub returns")
    fun submitPreferredCitiesPlannedHubTest() {
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

        val savedPreferences = hrpActions.getUserPreferredCitiesFromDatabase(employee)

        hrpAssertion.checkUserPreferencesInDatabase(savedPreferences, listOf(Constants.hrpTestCityId))
    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Submit Preferred Cities: list of cities")
    fun submitPreferredCitiesListOfCitiesTest() {
        hrpActions.createCityInDatabase()
        hrpActions.createDarkstoreInDatabase()
        hrpActions.createCityInDatabase(Constants.hrpTestCityId1)
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

        val savedPreferences = hrpActions.getUserPreferredCitiesFromDatabase(employee)

        hrpAssertion.checkUserPreferencesInDatabase(savedPreferences, listOf(Constants.hrpTestCityId, Constants.hrpTestCityId1))
    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Submit Preferred Cities: update cities list")
    fun submitPreferredCitiesUpdateCitiesTest() {
        hrpActions.createCityInDatabase()
        hrpActions.createDarkstoreInDatabase()
        hrpActions.createCityInDatabase(Constants.hrpTestCityId1)
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

        val userPreferencesRequest1 = hrpPreconditions.fillUpdateUserPreferredCityRequest(employee, listOf(Constants.hrpTestCityId))
        val userPreferencesRequest2 = hrpPreconditions.fillUpdateUserPreferredCityRequest(employee, listOf(Constants.hrpTestCityId1))
        hrpActions.submitUserPreferredCities(userPreferencesRequest1)
        val savedPreferences1 = hrpActions.getUserPreferredCitiesFromDatabase(employee)

        hrpActions.submitUserPreferredCities(userPreferencesRequest2)
        val savedPreferences2 = hrpActions.getUserPreferredCitiesFromDatabase(employee)

        hrpAssertion
            .checkUserPreferencesInDatabase(savedPreferences1, listOf(Constants.hrpTestCityId))
            .checkUserPreferencesInDatabase(savedPreferences2, listOf(Constants.hrpTestCityId1))
    }

    @Test
    @DisplayName("Submit Preferred Cities:  city without darkstores")
    fun submitPreferredCitiesWithoutDarkstoresTest() {
        hrpActions.createCityInDatabase()
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
        val error = hrpActions.submitUserPreferredCities(userPreferencesRequest)!!

        val existence = hrpActions.getUserPreferredCitiesExistenceFromDatabase(employee)

        hrpAssertion
            .checkUserPreferencesNotExists(existence)
            .checkValidationError(error.error.toString())
    }

    @Test
    @DisplayName("Submit Preferred Cities:  city without coordinates")
    fun submitPreferredCitiesWithoutCoordinatesTest() {
        hrpActions.createCityInDatabaseWithoutCoordinates()
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
        val error = hrpActions.submitUserPreferredCities(userPreferencesRequest)!!

        val existence = hrpActions.getUserPreferredCitiesExistenceFromDatabase(employee)

        hrpAssertion
            .checkUserPreferencesNotExists(existence)
            .checkValidationError(error.error.toString())
    }

    @Test
    @DisplayName("Submit Preferred Cities: empty cities list")
    fun submitPreferredCitiesEmptyCitiesTest() {

        val counterparty = commonPreconditions.createStaffPartner()
        val employee = commonPreconditions.createProfile(
            roles = listOf(ApiEnum(EmployeeRole.COUNTERPARTY)),
            staffPartnerId = counterparty,
            darkstoreId = null,
            email = "test@test.ru"
        ).profileId

        val userPreferencesRequest = hrpPreconditions.fillUpdateUserPreferredCityRequest(employee, listOf())
        val error = hrpActions.submitUserPreferredCities(userPreferencesRequest)!!

        val existence = hrpActions.getUserPreferredCitiesExistenceFromDatabase(employee)

        hrpAssertion
            .checkUserPreferencesNotExists(existence)
            .checkValidationError(error.error.toString())
    }

    @Test
    @DisplayName("Submit Preferred Cities: city not exists")
    fun submitPreferredCitiesNotExistsTest() {
        val counterparty = commonPreconditions.createStaffPartner()
        val employee = commonPreconditions.createProfile(
            roles = listOf(ApiEnum(EmployeeRole.COUNTERPARTY)),
            staffPartnerId = counterparty,
            darkstoreId = null,
            email = "test@test.ru"
        ).profileId

        val userPreferencesRequest = hrpPreconditions.fillUpdateUserPreferredCityRequest(employee, listOf(UUID.randomUUID()))
        val error = hrpActions.submitUserPreferredCities(userPreferencesRequest)!!

        val existence = hrpActions.getUserPreferredCitiesExistenceFromDatabase(employee)

        hrpAssertion
            .checkUserPreferencesNotExists(existence)
            .checkValidationError(error.error.toString())
    }

    @Test
    @DisplayName("Submit Preferred Cities: city not available to counterparty")
    fun submitPreferredCitiesNotAvailableTest() {
        hrpActions.createCityInDatabase()
        hrpActions.createCityInDatabase(Constants.hrpTestCityId1)
        hrpActions.createDarkstoreInDatabase()
        hrpActions.createDarkstoreInDatabase(cityId = Constants.hrpTestCityId1, darkstoreId = Constants.hrpTestDarktoreId2)
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

        val userPreferencesRequest = hrpPreconditions.fillUpdateUserPreferredCityRequest(employee, listOf(Constants.hrpTestCityId1))
        hrpActions.submitUserPreferredCities(userPreferencesRequest)!!

        val savedPreferences = hrpActions.getUserPreferredCitiesFromDatabase(employee)

        hrpAssertion.checkUserPreferencesInDatabase(savedPreferences, listOf(Constants.hrpTestCityId1))
    }

    @Test
    @DisplayName("Submit Preferred Cities: user not exists")
    fun submitPreferredCitiesUserNotExistsTest() {
        val userPreferencesRequest = hrpPreconditions.fillUpdateUserPreferredCityRequest(UUID.randomUUID(), listOf(Constants.hrpTestCityId1))
        val error = hrpActions.submitUserPreferredCities(userPreferencesRequest)!!

        hrpAssertion
            .checkValidationError(error.error.toString())
    }

    @Test
    @DisplayName("Submit Preferred Cities: user with wrong role")
    fun submitPreferredCitiesWrongUserRoleTest() {
        val employee = commonPreconditions.createProfile(
            roles = listOf(ApiEnum(EmployeeRole.SUPERVISOR)),
            staffPartnerId = null,
            darkstoreId = null,
        ).profileId

        val userPreferencesRequest = hrpPreconditions.fillUpdateUserPreferredCityRequest(employee, listOf(Constants.hrpTestCityId1))
        val error = hrpActions.submitUserPreferredCities(userPreferencesRequest)!!

        hrpAssertion
            .checkValidationError(error.error.toString())
    }
}
