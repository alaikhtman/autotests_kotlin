package ru.samokat.mysamokat.tests.tests.hr_platform.darkstoresAPI

import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.samokat.hrp.map.api.model.PagingFilter
import ru.samokat.hrp.map.api.model.PointView
import ru.samokat.mysamokat.tests.checkers.HrpMapAssertions
import ru.samokat.mysamokat.tests.dataproviders.Constants
import ru.samokat.mysamokat.tests.dataproviders.preconditions.HrpMapPreconditions
import ru.samokat.mysamokat.tests.helpers.actions.HrpMapActions
import java.math.BigDecimal

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Tag("hrp_map")
class FindAllNearest {

    private lateinit var hrpPreconditions: HrpMapPreconditions

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
        hrpAssertion.assertAll()

    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Find All Nearest: several ds all types and status")
    fun findAllNearestTest() {

        hrpActions.createCityInDatabase()
        hrpActions.createDarkstoreInDatabase(
            name = "Хаб тест",
            type = "HUB",
            status = "PLANNED",
            darkstoreId = Constants.hrpTestDarktoreId2,
            lat = BigDecimal(54.329538),
            lon = BigDecimal(48.357696)
        )
        hrpActions.createDarkstoreInDatabase(
            name = "Даркстор тест", darkstoreId = Constants.hrpTestDarktoreId1,
            lat = BigDecimal(54.342449),
            lon = BigDecimal(48.315982)
        )
        hrpActions.createDarkstoreInDatabase(
            name = "АААА тест",
            status = "PLANNED",
            darkstoreId = Constants.hrpTestDarktoreId3,
            lat = BigDecimal(54.337545),
            lon = BigDecimal(48.408851)
        )

        val findRequest = hrpPreconditions.fillFindNearestRequest(
            Constants.hrpTestCityId, PointView(
                lat = 54.331840, lon = 48.366279
            )
        )
        val darkstores = hrpActions.getDarkstoresNearestPoint(findRequest)!!

        hrpAssertion.checkDistanceDarkstoreData(
            darkstores.value.items[0],
            name = "Хаб тест",
            darkstoreId = Constants.hrpTestDarktoreId2,
            status = "PLANNED",
            type = "HUB",
            meters = 612.554997,
            lat = 54.329538,
            lon = 48.357696
        )
            .checkDistanceDarkstoreData(
                darkstores.value.items[1],
                name = "АААА тест",
                status = "PLANNED",
                darkstoreId = Constants.hrpTestDarktoreId3,
                meters = 2832.00086636,
                lat = 54.337545,
                lon = 48.408851
            )
            .checkDistanceDarkstoreData(
                darkstores.value.items[2],
                name = "Даркстор тест",
                darkstoreId = Constants.hrpTestDarktoreId1,
                meters = 3467.50608647,
                lat = 54.342449,
                lon = 48.315982
            )
    }

    @Test
    @DisplayName("Find All Nearest: ds from other city not returned")
    fun findAllNearestDSFromOtherCityNotReturnedTest() {

        hrpActions.createCityInDatabase()
        hrpActions.createCityInDatabase(cityId = Constants.hrpTestCityId1)
        hrpActions.createDarkstoreInDatabase(
            name = "Хаб тест",
            type = "HUB",
            status = "PLANNED",
            darkstoreId = Constants.hrpTestDarktoreId2,
            lat = BigDecimal(54.329538),
            lon = BigDecimal(48.357696)
        )
        hrpActions.createDarkstoreInDatabase(
            name = "Даркстор тест", darkstoreId = Constants.hrpTestDarktoreId1,
            lat = BigDecimal(54.342449),
            lon = BigDecimal(48.315982)
        )
        hrpActions.createDarkstoreInDatabase(
            name = "АААА тест",
            status = "PLANNED",
            darkstoreId = Constants.hrpTestDarktoreId3,
            lat = BigDecimal(54.337545),
            lon = BigDecimal(48.408851),
            cityId = Constants.hrpTestCityId1
        )

        val findRequest = hrpPreconditions.fillFindNearestRequest(
            Constants.hrpTestCityId, PointView(
                lat = 54.331840, lon = 48.366279
            )
        )
        val darkstores = hrpActions.getDarkstoresNearestPoint(findRequest)!!

        hrpAssertion
            .checkDarkstoreNotInList(darkstores.value.items, Constants.hrpTestDarktoreId3)
            .checkDarkstoreInList(darkstores.value.items, Constants.hrpTestDarktoreId2)
            .checkDarkstoreInList(darkstores.value.items, Constants.hrpTestDarktoreId1)
    }

    @Test
    @DisplayName("Find All Nearest: paging")
    fun findAllNearestPagingTest() {

        hrpActions.createCityInDatabase()
        hrpActions.createDarkstoreInDatabase(
            name = "Хаб тест",
            type = "HUB",
            status = "PLANNED",
            darkstoreId = Constants.hrpTestDarktoreId2,
            lat = BigDecimal(54.329538),
            lon = BigDecimal(48.357696)
        )
        hrpActions.createDarkstoreInDatabase(
            name = "Даркстор тест", darkstoreId = Constants.hrpTestDarktoreId1,
            lat = BigDecimal(54.342449),
            lon = BigDecimal(48.315982)
        )
        hrpActions.createDarkstoreInDatabase(
            name = "АААА тест",
            status = "PLANNED",
            darkstoreId = Constants.hrpTestDarktoreId3,
            lat = BigDecimal(54.337545),
            lon = BigDecimal(48.408851)
        )

        val findRequestPage1 = hrpPreconditions.fillFindNearestRequest(
            Constants.hrpTestCityId, PointView(
                lat = 54.331840, lon = 48.366279
            ), PagingFilter(1, 1)
        )
        val darkstoresPage1 = hrpActions.getDarkstoresNearestPoint(findRequestPage1)!!

        val findRequestPage2 = hrpPreconditions.fillFindNearestRequest(
            Constants.hrpTestCityId, PointView(
                lat = 54.331840, lon = 48.366279
            ), PagingFilter(1, 2)
        )
        val darkstoresPage2 = hrpActions.getDarkstoresNearestPoint(findRequestPage2)!!

        val findRequestPage3 = hrpPreconditions.fillFindNearestRequest(
            Constants.hrpTestCityId, PointView(
                lat = 54.331840, lon = 48.366279
            ), PagingFilter(1, 3)
        )
        val darkstoresPage3 = hrpActions.getDarkstoresNearestPoint(findRequestPage3)!!

        hrpAssertion
            .checkDistanceDarkstoreData(
                darkstoresPage1.value.items[0],
                name = "Хаб тест",
                darkstoreId = Constants.hrpTestDarktoreId2,
                status = "PLANNED",
                type = "HUB",
                meters = 612.554997,
                lat = 54.329538,
                lon = 48.357696
            )
            .checkDistanceDarkstoreData(
                darkstoresPage2.value.items[0],
                name = "АААА тест",
                status = "PLANNED",
                darkstoreId = Constants.hrpTestDarktoreId3,
                meters = 2832.00086636,
                lat = 54.337545,
                lon = 48.408851
            )
            .checkDistanceDarkstoreData(
                darkstoresPage3.value.items[0],
                name = "Даркстор тест",
                darkstoreId = Constants.hrpTestDarktoreId1,
                meters = 3467.50608647,
                lat = 54.342449,
                lon = 48.315982
            )
            .checkDarstoresListPaging(darkstoresPage1.value.paging, 1, 3)
            .checkDarstoresListPaging(darkstoresPage2.value.paging, 2, 3)
            .checkDarstoresListPaging(darkstoresPage3.value.paging, 3, 3)
            .checkDarstoresDistanceListCount(darkstoresPage1, 1)
            .checkDarstoresDistanceListCount(darkstoresPage2, 1)
            .checkDarstoresDistanceListCount(darkstoresPage3, 1)
    }

    @Test
    @DisplayName("Find All Nearest: city without darkstores")
    fun findAllNearestCityWithoutDarkstoresTest() {
        hrpActions.createCityInDatabase()
        val findRequest = hrpPreconditions.fillFindNearestRequest(
            Constants.hrpTestCityId, PointView(
                lat = 54.331840, lon = 48.366279
            )
        )
        val darkstores = hrpActions.getDarkstoresNearestPoint(findRequest)!!
        hrpAssertion.checkDarstoresDistanceListCount(darkstores, 0)

    }

    @Test
    @DisplayName("Find All Nearest: city not exists")
    fun findAllNearestCityNotExistTest() {
        val findRequest = hrpPreconditions.fillFindNearestRequest(
            Constants.hrpTestCityId, PointView(
                lat = 54.331840, lon = 48.366279
            )
        )
        val error = hrpActions.getDarkstoresNearestPoint(findRequest)!!
        hrpAssertion.checkValidationError(error!!.error.toString())
    }

}