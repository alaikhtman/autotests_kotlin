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
class SubmitAuthorizedCities {

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
        hrpActions.deleteDarkstoreFromDB(Constants.hrpTestDarktoreId3)
        hrpActions.deleteCityFromDB(Constants.hrpTestCityId)
        hrpActions.deleteCityFromDB(Constants.hrpTestCityId1)
        hrpActions.deleteCityFromDB(Constants.hrpTestCityId2)
        employeeActions.deletePartner("HRP-Map Partner")
        employeeActions.deletePartner("HRP-Map Partner2")
        hrpPreconditions = HrpMapPreconditions()
        hrpAssertion = HrpMapAssertions()
    }

    @AfterEach
    fun release() {
        hrpActions.deleteDarkstoreFromDB(Constants.hrpTestDarktoreId1)
        hrpActions.deleteDarkstoreFromDB(Constants.hrpTestDarktoreId2)
        hrpActions.deleteDarkstoreFromDB(Constants.hrpTestDarktoreId3)
        hrpActions.deleteCityFromDB(Constants.hrpTestCityId)
        hrpActions.deleteCityFromDB(Constants.hrpTestCityId1)
        hrpActions.deleteCityFromDB(Constants.hrpTestCityId2)
        employeeActions.deletePartner("HRP-Map Partner")
        employeeActions.deletePartner("HRP-Map Partner2")
        hrpAssertion.assertAll()

    }


    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Submit Authorized Cities: add one city to new counterparty")
    fun submitAuthorizedCitiesOneCityToNewCounterpartyTest() {

        hrpActions.createCityInDatabase()
        hrpActions.createDarkstoreInDatabase()
        val counterparty = commonPreconditions.createStaffPartner()
        val submitRequest = hrpPreconditions.fillSubmitAuthorizedCitiesRequest(
            listOf(counterparty), listOf(Constants.hrpTestCityId))
        hrpActions.submitCounterpartyPreferencesCities(submitRequest)

        val counterpartyPreferences = hrpActions.getCounterpartyPreferencesFromDB(counterparty)

        hrpAssertion.checkCounterpartyPreferencesInDB(counterpartyPreferences, submitRequest)

    }

    @Test
    @DisplayName("Submit Authorized Cities: add several city to new counterparty")
    fun submitAuthorizedCitiesSeveralCitiesToNewCounterpartyTest() {

        hrpActions.createCityInDatabase()
        hrpActions.createCityInDatabase(Constants.hrpTestCityId1)
        hrpActions.createCityInDatabase(Constants.hrpTestCityId2)
        hrpActions.createDarkstoreInDatabase()
        hrpActions.createDarkstoreInDatabase(cityId = Constants.hrpTestCityId1, darkstoreId = Constants.hrpTestDarktoreId2)
        hrpActions.createDarkstoreInDatabase(cityId = Constants.hrpTestCityId2, darkstoreId = Constants.hrpTestDarktoreId3)

        val counterparty = commonPreconditions.createStaffPartner()
        val submitRequest = hrpPreconditions.fillSubmitAuthorizedCitiesRequest(
            listOf(counterparty), listOf(Constants.hrpTestCityId, Constants.hrpTestCityId1, Constants.hrpTestCityId2))
        hrpActions.submitCounterpartyPreferencesCities(submitRequest)

        val counterpartyPreferences = hrpActions.getCounterpartyPreferencesFromDB(counterparty)

        hrpAssertion.checkCounterpartyPreferencesInDB(counterpartyPreferences, submitRequest)
    }

    @Test
    @DisplayName("Submit Authorized Cities: update cities list to counterparty")
    fun submitAuthorizedCitiesUpdateCitiesListTest() {

        hrpActions.createCityInDatabase()
        hrpActions.createCityInDatabase(Constants.hrpTestCityId1)
        hrpActions.createCityInDatabase(Constants.hrpTestCityId2)
        hrpActions.createDarkstoreInDatabase()
        hrpActions.createDarkstoreInDatabase(cityId = Constants.hrpTestCityId1, darkstoreId = Constants.hrpTestDarktoreId2)
        hrpActions.createDarkstoreInDatabase(cityId = Constants.hrpTestCityId2, darkstoreId = Constants.hrpTestDarktoreId3)


        val counterparty = commonPreconditions.createStaffPartner()

        val submitRequest1 = hrpPreconditions.fillSubmitAuthorizedCitiesRequest(
            listOf(counterparty), listOf(Constants.hrpTestCityId))
        val submitRequest2 = hrpPreconditions.fillSubmitAuthorizedCitiesRequest(
            listOf(counterparty), listOf(Constants.hrpTestCityId1, Constants.hrpTestCityId2))

        hrpActions.submitCounterpartyPreferencesCities(submitRequest1)
        val counterpartyPreferences1 = hrpActions.getCounterpartyPreferencesFromDB(counterparty)
        hrpActions.submitCounterpartyPreferencesCities(submitRequest2)
        val counterpartyPreferences2 = hrpActions.getCounterpartyPreferencesFromDB(counterparty)

        hrpAssertion
            .checkCounterpartyPreferencesInDB(counterpartyPreferences1, submitRequest1)
            .checkCounterpartyPreferencesInDB(counterpartyPreferences2, submitRequest2)
    }

    @Test
    @DisplayName("Submit Authorized Cities: empty cities list to counterparty")
    fun submitAuthorizedCitiesEmptyCitiesListTest() {
        hrpActions.createCityInDatabase()
        hrpActions.createDarkstoreInDatabase()

        val counterparty = commonPreconditions.createStaffPartner()

        val submitRequest1 = hrpPreconditions.fillSubmitAuthorizedCitiesRequest(
            listOf(counterparty), listOf(Constants.hrpTestCityId))
        val submitRequest2 = hrpPreconditions.fillSubmitAuthorizedCitiesRequest(
            listOf(counterparty), listOf())

        hrpActions.submitCounterpartyPreferencesCities(submitRequest1)
        val counterpartyPreferences1 = hrpActions.getCounterpartyPreferencesFromDB(counterparty)
        val error = hrpActions.submitCounterpartyPreferencesCitiesWithError(submitRequest2)
        val counterpartyPreferences2 = hrpActions.getCounterpartyPreferencesFromDB(counterparty)

        hrpAssertion
            .checkCounterpartyPreferencesInDB(counterpartyPreferences1, submitRequest1)
            .checkCounterpartyPreferencesInDB(counterpartyPreferences2, submitRequest1)
            .checkValidationError(error!!.error.toString())
    }

    @Test
    @DisplayName("Submit Authorized Cities: counterparty not exists")
    fun submitAuthorizedCounterpartyNotExistsTest() {

        hrpActions.createCityInDatabase()
        hrpActions.createDarkstoreInDatabase()
        val counterparty = UUID.randomUUID()
        val submitRequest = hrpPreconditions.fillSubmitAuthorizedCitiesRequest(
            listOf(counterparty), listOf(Constants.hrpTestCityId))
        hrpActions.submitCounterpartyPreferencesCities(submitRequest)

        val counterpartyPreferences = hrpActions.getCounterpartyPreferencesFromDB(counterparty)

        hrpAssertion.checkCounterpartyPreferencesInDB(counterpartyPreferences, submitRequest)
    }

    @Test
    @DisplayName("Submit Authorized Cities: city not exists")
    fun submitAuthorizedCityNotExistsTest() {
        val counterparty = commonPreconditions.createStaffPartner()
        val submitRequest = hrpPreconditions.fillSubmitAuthorizedCitiesRequest(
            listOf(counterparty), listOf(UUID.randomUUID()))
        val error = hrpActions.submitCounterpartyPreferencesCitiesWithError(submitRequest)

        hrpAssertion.checkValidationError(error!!.error.toString())
    }

    @Test
    @DisplayName("Submit Authorized Cities: add cities to new several counterparties")
    fun submitAuthorizedCitiesToSeveralCounterpartiesTest() {

        hrpActions.createCityInDatabase()
        hrpActions.createDarkstoreInDatabase()
        val counterparty1 = commonPreconditions.createStaffPartner()
        val counterparty2 = commonPreconditions.createStaffPartner(title = "HRP-Map Partner2")
        val submitRequest = hrpPreconditions.fillSubmitAuthorizedCitiesRequest(
            listOf(counterparty1, counterparty2), listOf(Constants.hrpTestCityId))
        hrpActions.submitCounterpartyPreferencesCities(submitRequest)

        val counterpartyPreferences1 = hrpActions.getCounterpartyPreferencesFromDB(counterparty1)
        val counterpartyPreferences2 = hrpActions.getCounterpartyPreferencesFromDB(counterparty1)

        hrpAssertion
            .checkCounterpartyPreferencesInDB(counterpartyPreferences1, submitRequest, counterparty1)
            .checkCounterpartyPreferencesInDB(counterpartyPreferences2, submitRequest, counterparty2)

    }

    @Test
    @DisplayName("Submit Authorized Cities: repeated cities")
    fun submitAuthorizedCitiesRepeatedCitiesTest() {

        hrpActions.createCityInDatabase()
        hrpActions.createDarkstoreInDatabase()
        val counterparty = commonPreconditions.createStaffPartner()
        val submitRequest = hrpPreconditions.fillSubmitAuthorizedCitiesRequest(
            listOf(counterparty), listOf(Constants.hrpTestCityId, Constants.hrpTestCityId))
        hrpActions.submitCounterpartyPreferencesCities(submitRequest)

        val counterpartyPreferences = hrpActions.getCounterpartyPreferencesFromDB(counterparty)

        hrpAssertion.checkCounterpartyPreferencesInDB(counterpartyPreferences, submitRequest)

    }
}
