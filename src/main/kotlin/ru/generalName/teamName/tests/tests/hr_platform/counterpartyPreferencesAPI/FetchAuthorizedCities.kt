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

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Tag("hrp_map")
class FetchAuthorizedCities {

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
        hrpActions.deleteCityFromDB(Constants.hrpTestCityId)
        employeeActions.deletePartner("HRP-Map Partner")
        employeeActions.deletePartner("HRP-Map Partner2")
        hrpPreconditions = HrpMapPreconditions()
        hrpAssertion = HrpMapAssertions()
    }

    @AfterEach
    fun release() {
        employeeActions.deletePartner("HRP-Map Partner")
        employeeActions.deletePartner("HRP-Map Partner2")
        hrpActions.deleteDarkstoreFromDB(Constants.hrpTestDarktoreId1)
        hrpActions.deleteCityFromDB(Constants.hrpTestCityId)
        hrpAssertion.assertAll()
    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Fetch Authorized Cities")
    fun fetchAuthorizedCitiesTest() {

        hrpActions.createCityInDatabase()
        hrpActions.createDarkstoreInDatabase()
        val counterparty = commonPreconditions.createStaffPartner()
        val submitRequest = hrpPreconditions.fillSubmitAuthorizedCitiesRequest(
            listOf(counterparty), listOf(Constants.hrpTestCityId))
        hrpActions.submitCounterpartyPreferencesCities(submitRequest)

        val city = hrpActions.getCityByIdFromDB(Constants.hrpTestCityId)

        val counterpartyPreferencesList = hrpActions.fetchAuthorizedCities()

        hrpAssertion.checkCounterpartyPreferencesInList(counterpartyPreferencesList, counterparty, listOf(city))

    }
}