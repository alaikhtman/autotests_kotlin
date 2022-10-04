package ru.samokat.mysamokat.tests.tests.hr_platform.darkstoresAPI

import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.samokat.mysamokat.tests.checkers.HrpMapAssertions
import ru.samokat.mysamokat.tests.dataproviders.Constants
import ru.samokat.mysamokat.tests.dataproviders.preconditions.HrpMapPreconditions
import ru.samokat.mysamokat.tests.helpers.actions.HrpMapActions
import java.util.*


@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Tag("hrp_map")
class FindById {

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
    @DisplayName("Find Darkstore By ID: darkstore type, operating status")
    fun findDarkstoreByIdOperatingTest() {

        hrpActions.createCityInDatabase()
        hrpActions.createDarkstoreInDatabase()
        val darkstore = hrpActions.getDarkstoreById(Constants.hrpTestDarktoreId1)
        hrpAssertion.checkDarkstoreData(darkstore!!.value)
    }

    @Test
    @DisplayName("Find Darkstore By ID: hub type, planned")
    fun findDarkstoreByIdHubPlannedTest() {

        hrpActions.createCityInDatabase()
        hrpActions.createDarkstoreInDatabase(type = "HUB", status = "PLANNED")
        val darkstore = hrpActions.getDarkstoreById(Constants.hrpTestDarktoreId1)
        hrpAssertion.checkDarkstoreData(darkstore!!.value, type = "HUB", status = "PLANNED")
    }

    @Test
    @DisplayName("Find Darkstore By ID: darkstore not exists")
    fun findDarkstoreByIdNotExistTest() {

        val error = hrpActions.getDarkstoreById(UUID.randomUUID())
        hrpAssertion.checkDarkstoreNotFoundError(error!!.error.toString())

    }
}