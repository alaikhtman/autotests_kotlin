package ru.samokat.mysamokat.tests.tests.hr_platform.cityAPI

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
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Tag("hrp_map")
class FetchAvailableCities {

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
    @DisplayName("Fetch Available: city with operating darkstore returns")
    fun fetchAvailableOperatingDsOnlyTest() {

        hrpActions.createCityInDatabase()
        hrpActions.createDarkstoreInDatabase()
        val counterparty = commonPreconditions.createStaffPartner()
        val employee = commonPreconditions.createProfile(
            roles = listOf(ApiEnum(EmployeeRole.COUNTERPARTY)),
            staffPartnerId = counterparty,
            darkstoreId = null,
            email = "test@test.ru"
        ).profileId
        val submitRequest = hrpPreconditions.fillSubmitAuthorizedCitiesRequest(
            listOf(counterparty), listOf(Constants.hrpTestCityId))
        val fetchRequest = hrpPreconditions.fillFetchAvailableCitiesRequest(employee)

        hrpActions.submitCounterpartyPreferencesCities(submitRequest)


        val cities = hrpActions.getAvailableCities(fetchRequest)!!

        hrpAssertion
            .checkCityDataInList(cities.value, Constants.hrpTestCityId)
    }

    @Test
    @DisplayName("Fetch Available: city with planned hub returns")
    fun fetchAvailablePlannedHubOnlyTest() {

        hrpActions.createCityInDatabase()
        hrpActions.createDarkstoreInDatabase(status = "PLANNED", type = "HUB")
        val counterparty = commonPreconditions.createStaffPartner()
        val employee = commonPreconditions.createProfile(
            roles = listOf(ApiEnum(EmployeeRole.COUNTERPARTY)),
            staffPartnerId = counterparty,
            darkstoreId = null,
            email = "test@test.ru"
        ).profileId
        val submitRequest = hrpPreconditions.fillSubmitAuthorizedCitiesRequest(
            listOf(counterparty), listOf(Constants.hrpTestCityId))
        val fetchRequest = hrpPreconditions.fillFetchAvailableCitiesRequest(employee)

        hrpActions.submitCounterpartyPreferencesCities(submitRequest)


        val cities = hrpActions.getAvailableCities(fetchRequest)!!

        hrpAssertion
            .checkCityDataInList(cities.value, Constants.hrpTestCityId)
    }

    @Test
    @DisplayName("Fetch Available: city without darkstores")
    fun fetchAvailableCityWithoutDarkstoreTest() {

        hrpActions.createCityInDatabase()
        hrpActions.createCityInDatabase(Constants.hrpTestCityId1)
        hrpActions.createDarkstoreInDatabase()
        val counterparty = commonPreconditions.createStaffPartner()
        val employee = commonPreconditions.createProfile(
            roles = listOf(ApiEnum(EmployeeRole.COUNTERPARTY)),
            staffPartnerId = counterparty,
            darkstoreId = null,
            email = "test@test.ru"
        ).profileId
        val submitRequest = hrpPreconditions.fillSubmitAuthorizedCitiesRequest(
            listOf(counterparty), listOf(Constants.hrpTestCityId))
        val fetchRequest = hrpPreconditions.fillFetchAvailableCitiesRequest(employee)

        hrpActions.submitCounterpartyPreferencesCities(submitRequest)


        val cities = hrpActions.getAvailableCities(fetchRequest)!!

        hrpAssertion
            .checkCityPresentInList(cities.value, Constants.hrpTestCityId)
            .checkCityNotPresentInList(cities.value, Constants.hrpTestCityId1)
    }

    @Test
    @DisplayName("Fetch Available: city without coordinates")
    fun fetchAvailableCityWithoutCoordinatesTest() {

        hrpActions.createCityInDatabase()
        hrpActions.createCityInDatabaseWithoutCoordinates(Constants.hrpTestCityId1)
        hrpActions.createDarkstoreInDatabase()
        val counterparty = commonPreconditions.createStaffPartner()
        val employee = commonPreconditions.createProfile(
            roles = listOf(ApiEnum(EmployeeRole.COUNTERPARTY)),
            staffPartnerId = counterparty,
            darkstoreId = null,
            email = "test@test.ru"
        ).profileId
        val submitRequest = hrpPreconditions.fillSubmitAuthorizedCitiesRequest(
            listOf(counterparty), listOf(Constants.hrpTestCityId))
        val fetchRequest = hrpPreconditions.fillFetchAvailableCitiesRequest(employee)

        hrpActions.submitCounterpartyPreferencesCities(submitRequest)


        val cities = hrpActions.getAvailableCities(fetchRequest)!!

        hrpAssertion
            .checkCityPresentInList(cities.value, Constants.hrpTestCityId)
            .checkCityNotPresentInList(cities.value, Constants.hrpTestCityId1)
    }

    @Test
    @DisplayName("Fetch Available: empty cities list")
    fun fetchAvailableEmptyCitiesListTest() {

        hrpActions.createCityInDatabase()
        val counterparty = commonPreconditions.createStaffPartner()
        val employee = commonPreconditions.createProfile(
            roles = listOf(ApiEnum(EmployeeRole.COUNTERPARTY)),
            staffPartnerId = counterparty,
            darkstoreId = null,
            email = "test@test.ru"
        ).profileId
        val submitRequest = hrpPreconditions.fillSubmitAuthorizedCitiesRequest(
            listOf(counterparty), listOf())
        val fetchRequest = hrpPreconditions.fillFetchAvailableCitiesRequest(employee)

        hrpActions.submitCounterpartyPreferencesCities(submitRequest)


        val cities = hrpActions.getAvailableCities(fetchRequest)!!

        hrpAssertion
            .checkCitiesListCount(cities.value, 0)
    }

    @Test
    @DisplayName("Fetch Available: patner is absent")
    fun fetchAvailablePartnerIsAbsentTest() {

        val counterparty = commonPreconditions.createStaffPartner()
        val employee = commonPreconditions.createProfile(
            roles = listOf(ApiEnum(EmployeeRole.COUNTERPARTY)),
            staffPartnerId = counterparty,
            darkstoreId = null,
            email = "test@test.ru"
        ).profileId
        val fetchRequest = hrpPreconditions.fillFetchAvailableCitiesRequest(employee)

        val cities = hrpActions.getAvailableCities(fetchRequest)!!

        hrpAssertion
            .checkCitiesListCount(cities.value, 0)
    }

    @Test
    @DisplayName("Fetch Available: user not exist")
    fun fetchAvailableUserNotExistsTest() {

        val fetchRequest = hrpPreconditions.fillFetchAvailableCitiesRequest(UUID.randomUUID())

        val error = hrpActions.getAvailableCities(fetchRequest)!!

        hrpAssertion
            .checkCitiesNotFetchedError(error.error.toString())
    }

    @Test
    @DisplayName("Fetch Available: user with other role")
    fun fetchAvailableOtherUserRoleTest() {

        val employee = commonPreconditions.createProfile(
            roles = listOf(ApiEnum(EmployeeRole.STAFF_MANAGER)),
            darkstoreId = null, staffPartnerId = null
        ).profileId
        val fetchRequest = hrpPreconditions.fillFetchAvailableCitiesRequest(employee)

        val error = hrpActions.getAvailableCities(fetchRequest)!!

        hrpAssertion
            .checkCitiesNotFetchedError(error.error.toString())
    }
}
