package ru.samokat.mysamokat.tests.tests.hr_platform.cityAPI

import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.samokat.mysamokat.tests.checkers.HrpMapAssertions
import ru.samokat.mysamokat.tests.dataproviders.Constants
import ru.samokat.mysamokat.tests.dataproviders.preconditions.HrpMapPreconditions
import ru.samokat.mysamokat.tests.helpers.actions.HrpMapActions

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Tag("hrp_map")
class FetchAll {

    private lateinit var hrpPreconditions: HrpMapPreconditions

    @Autowired
    private lateinit var hrpActions: HrpMapActions

    private lateinit var hrpAssertion: HrpMapAssertions

    @BeforeEach
    fun before() {
        hrpActions.deleteDarkstoreFromDB(Constants.hrpTestDarktoreId1)
        hrpActions.deleteCityFromDB(Constants.hrpTestCityId)

        hrpPreconditions = HrpMapPreconditions()
        hrpAssertion = HrpMapAssertions()
    }

    @AfterEach
    fun release() {
        hrpActions.deleteDarkstoreFromDB(Constants.hrpTestDarktoreId1)
        hrpActions.deleteCityFromDB(Constants.hrpTestCityId)
        hrpAssertion.assertAll()

    }


    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Fetch All Cities: city with operating darkstore returns")
    fun fetchAllOperatingDsOnlyTest() {

        hrpActions.createCityInDatabase()
        hrpActions.createDarkstoreInDatabase()

        val cities = hrpActions.getAllCities()!!

        hrpAssertion
            .checkCityDataInList(cities.value, Constants.hrpTestCityId)
    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Fetch All Cities: city with planned hub returns")
    fun fetchAllPlanningHubOnlyTest() {

        hrpActions.createCityInDatabase()
        hrpActions.createDarkstoreInDatabase(type = "HUB", status = "PLANNED")

        val cities = hrpActions.getAllCities()!!

        hrpAssertion
            .checkCityDataInList(cities.value, Constants.hrpTestCityId)
    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Fetch All Cities: city without darkstore")
    fun fetchAllCityWithoutDarkstoreTest() {

        hrpActions.createCityInDatabase()

        val cities = hrpActions.getAllCities()!!

        hrpAssertion
            .checkCityNotPresentInList(cities.value, Constants.hrpTestCityId)
    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Fetch All Cities: city without coordinates")
    fun fetchAllCityWithoutCoordinatesTest() {

        hrpActions.createCityInDatabaseWithoutCoordinates()
        hrpActions.createDarkstoreInDatabase()

        val cities = hrpActions.getAllCities()!!

        hrpAssertion
            .checkCityNotPresentInList(cities.value, Constants.hrpTestCityId)
    }
}