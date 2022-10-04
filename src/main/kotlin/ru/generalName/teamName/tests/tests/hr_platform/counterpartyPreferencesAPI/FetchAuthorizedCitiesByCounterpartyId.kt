package ru.samokat.mysamokat.tests.tests.hr_platform.counterpartyPreferencesAPI

import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.samokat.mysamokat.tests.checkers.HrpMapAssertions
import ru.samokat.mysamokat.tests.dataproviders.Constants
import ru.samokat.mysamokat.tests.dataproviders.preconditions.CommonPreconditions
import ru.samokat.mysamokat.tests.dataproviders.preconditions.HrpMapPreconditions
import ru.samokat.mysamokat.tests.helpers.actions.EmployeeActions
import ru.samokat.mysamokat.tests.helpers.actions.HrpMapActions
import java.util.*

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Tag("hrp_map")
class FetchAuthorizedCitiesByCounterpartyId {

    @Autowired
    private lateinit var commonPreconditions: CommonPreconditions
    private lateinit var hrpPreconditions: HrpMapPreconditions

    @Autowired
    private lateinit var employeeActions: EmployeeActions
    @Autowired
    private lateinit var hrpActions: HrpMapActions

    private lateinit var hrpAssertion: HrpMapAssertions

    @BeforeEach
    fun before() {
        hrpActions.deleteDarkstoreFromDB(Constants.hrpTestDarktoreId1)
        hrpActions.deleteDarkstoreFromDB(Constants.hrpTestDarktoreId2)
        hrpActions.deleteCityFromDB(Constants.hrpTestCityId)
        hrpActions.deleteCityFromDB(Constants.hrpTestCityId1)
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
        employeeActions.deletePartner("HRP-Map Partner")
        employeeActions.deletePartner("HRP-Map Partner2")
        hrpAssertion.assertAll()
    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Fetch Authorized Cities By Counterparty ID: one city")
    fun fetchAuthorizedCitiesByCounterpartyIdOneCityTest() {

        hrpActions.createCityInDatabase()
        hrpActions.createDarkstoreInDatabase()
        val counterparty = commonPreconditions.createStaffPartner()
        val submitRequest = hrpPreconditions.fillSubmitAuthorizedCitiesRequest(
            listOf(counterparty), listOf(Constants.hrpTestCityId))
        hrpActions.submitCounterpartyPreferencesCities(submitRequest)

        val city = hrpActions.getCityByIdFromDB(Constants.hrpTestCityId)

        val counterpartyPreferences = hrpActions.fetchAuthorizedCitiesByCounterpartyId(counterparty)

        hrpAssertion.checkCounterpartyPreferences(counterpartyPreferences, listOf(city))

    }

    @Test
    @DisplayName("Fetch Authorized Cities By Counterparty ID: several cities")
    fun fetchAuthorizedCitiesByCounterpartyIdSeveralCitiesTest() {

        hrpActions.createCityInDatabase()
        hrpActions.createCityInDatabase(Constants.hrpTestCityId1)
        hrpActions.createDarkstoreInDatabase()
        hrpActions.createDarkstoreInDatabase(cityId = Constants.hrpTestCityId1, darkstoreId = Constants.hrpTestDarktoreId2)

        val counterparty = commonPreconditions.createStaffPartner()
        val submitRequest = hrpPreconditions.fillSubmitAuthorizedCitiesRequest(
            listOf(counterparty), listOf(Constants.hrpTestCityId, Constants.hrpTestCityId1))
        hrpActions.submitCounterpartyPreferencesCities(submitRequest)

        val city1 = hrpActions.getCityByIdFromDB(Constants.hrpTestCityId)
        val city2 = hrpActions.getCityByIdFromDB(Constants.hrpTestCityId1)

        val counterpartyPreferences = hrpActions.fetchAuthorizedCitiesByCounterpartyId(counterparty)

        hrpAssertion.checkCounterpartyPreferences(counterpartyPreferences, listOf(city1, city2))
    }

    @Test
    @DisplayName("Fetch Authorized Cities By Counterparty ID: empty list")
    fun fetchAuthorizedCitiesByCounterpartyIdEmptyListTest() {

        val counterparty = commonPreconditions.createStaffPartner()
        val submitRequest = hrpPreconditions.fillSubmitAuthorizedCitiesRequest(
            listOf(counterparty), listOf())
        hrpActions.submitCounterpartyPreferencesCities(submitRequest)

        val counterpartyPreferences = hrpActions.fetchAuthorizedCitiesByCounterpartyId(counterparty)

        hrpAssertion.checkCounterpartyPreferencesListIsEmpty(counterpartyPreferences)
    }

    @Test
    @DisplayName("Fetch Authorized Cities By Counterparty ID: counterparty not exists")
    fun fetchAuthorizedCitiesByCounterpartyIdNotExistTest() {

        val counterpartyPreferences = hrpActions.fetchAuthorizedCitiesByCounterpartyId(UUID.randomUUID())

        hrpAssertion.checkCounterpartyPreferencesListIsEmpty(counterpartyPreferences)
    }

    @Test
    @DisplayName("Fetch Authorized Cities By Counterparty ID: city without coordinates doesn't show")
    fun fetchAuthorizedCitiesByCounterpartyIdCityWithoutCoordinates() {

        hrpActions.createCityInDatabaseWithoutCoordinates()
        hrpActions.createDarkstoreInDatabase()
        val counterparty = commonPreconditions.createStaffPartner()
        val submitRequest = hrpPreconditions.fillSubmitAuthorizedCitiesRequest(
            listOf(counterparty), listOf(Constants.hrpTestCityId))
        hrpActions.submitCounterpartyPreferencesCities(submitRequest)

        val counterpartyPreferences = hrpActions.fetchAuthorizedCitiesByCounterpartyId(counterparty)

        hrpAssertion.checkCounterpartyPreferencesListIsEmpty(counterpartyPreferences)

    }

    @Test
    @DisplayName("Fetch Authorized Cities By Counterparty ID: city without darkstores doesn't show")
    fun fetchAuthorizedCitiesByCounterpartyIdCityWithoutDarkstores() {

        hrpActions.createCityInDatabase()
        val counterparty = commonPreconditions.createStaffPartner()
        val submitRequest = hrpPreconditions.fillSubmitAuthorizedCitiesRequest(
            listOf(counterparty), listOf(Constants.hrpTestCityId))
        hrpActions.submitCounterpartyPreferencesCities(submitRequest)

        val counterpartyPreferences = hrpActions.fetchAuthorizedCitiesByCounterpartyId(counterparty)

        hrpAssertion.checkCounterpartyPreferencesListIsEmpty(counterpartyPreferences)

    }
}