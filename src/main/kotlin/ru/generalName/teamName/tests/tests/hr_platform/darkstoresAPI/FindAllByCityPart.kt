package ru.samokat.mysamokat.tests.tests.hr_platform.darkstoresAPI

import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.samokat.hrp.map.api.model.PointView
import ru.samokat.mysamokat.tests.checkers.HrpMapAssertions
import ru.samokat.mysamokat.tests.dataproviders.Constants
import ru.samokat.mysamokat.tests.dataproviders.preconditions.HrpMapPreconditions
import ru.samokat.mysamokat.tests.helpers.actions.HrpMapActions
import java.math.BigDecimal

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Tag("hrp_map")
class FindAllByCityPart {

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
    @DisplayName("Find All By City Part: several ds all types and status")
    fun findAllByCityPartTest() {

        hrpActions.createCityInDatabase()
        hrpActions.createDarkstoreInDatabase(
            name = "Хаб тест",
            type = "HUB",
            status = "PLANNED",
            darkstoreId = Constants.hrpTestDarktoreId2,
            lat = BigDecimal(54.318325),
            lon = BigDecimal(48.386535)
        )
        hrpActions.createDarkstoreInDatabase(
            name = "Даркстор тест", darkstoreId = Constants.hrpTestDarktoreId1,
            lat = BigDecimal(54.325133),
            lon = BigDecimal(48.346366)
        )
        hrpActions.createDarkstoreInDatabase(
            name = "АААА тест",
            status = "PLANNED",
            darkstoreId = Constants.hrpTestDarktoreId3,
            lat = BigDecimal(54.350955),
            lon = BigDecimal(48.329887)
        )

        val findRequest = hrpPreconditions.fillFindByCityPartRequest(
            Constants.hrpTestCityId, PointView(
                lat = 54.351954, lon = 48.281822
            ), PointView(
                lat = 54.306709, lon = 48.410224
            )
        )
        val darkstores = hrpActions.getDarkstoreByCityPart(findRequest)!!

        hrpAssertion
            .checkDarkstoreData(
                darkstores.value[2],
                name = "АААА тест",
                status = "PLANNED",
                darkstoreId = Constants.hrpTestDarktoreId3,
                lat = 54.350955,
                lon = 48.329887
            )
            .checkDarkstoreData(
                darkstores.value[1],
                name = "Даркстор тест",
                darkstoreId = Constants.hrpTestDarktoreId1,
                lat = 54.325133,
                lon = 48.346366
            )
            .checkDarkstoreData(
                darkstores.value[0],
                name = "Хаб тест",
                darkstoreId = Constants.hrpTestDarktoreId2,
                status = "PLANNED",
                type = "HUB",
                lat = 54.318325,
                lon = 48.386535
            )

        /*
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

         */
    }

    @Test
    @DisplayName("Find All By City Part: ds outside region")
    fun findAllByCityPartOutsideRegionTest() {

        hrpActions.createCityInDatabase()
        hrpActions.createDarkstoreInDatabase(
            name = "Хаб тест",
            type = "HUB",
            status = "PLANNED",
            darkstoreId = Constants.hrpTestDarktoreId2,
            lat = BigDecimal(54.318325),
            lon = BigDecimal(48.386535)
        )
        hrpActions.createDarkstoreInDatabase(
            name = "Даркстор тест", darkstoreId = Constants.hrpTestDarktoreId1,
            lat = BigDecimal(54.340547),
            lon = BigDecimal(48.473396)
        )

        val findRequest = hrpPreconditions.fillFindByCityPartRequest(
            Constants.hrpTestCityId, PointView(
                lat = 54.351954, lon = 48.281822
            ), PointView(
                lat = 54.306709, lon = 48.410224
            )
        )
        val darkstores = hrpActions.getDarkstoreByCityPart(findRequest)!!

        hrpAssertion
            .checkDarkstoreInDSList(darkstores.value, Constants.hrpTestDarktoreId2)
            .checkDarkstoreNotInDSList(darkstores.value, Constants.hrpTestDarktoreId1)
    }

    @Test
    @DisplayName("Find All By City Part: ds from other city")
    fun findAllByCityPartOtherCityTest() {

        hrpActions.createCityInDatabase()
        hrpActions.createCityInDatabase(Constants.hrpTestCityId1)
        hrpActions.createDarkstoreInDatabase(
            name = "Хаб тест",
            type = "HUB",
            status = "PLANNED",
            darkstoreId = Constants.hrpTestDarktoreId2,
            lat = BigDecimal(54.318325),
            lon = BigDecimal(48.386535)
        )
        hrpActions.createDarkstoreInDatabase(
            name = "Даркстор тест", darkstoreId = Constants.hrpTestDarktoreId1,
            cityId = Constants.hrpTestCityId1,
            lat = BigDecimal(54.325133),
            lon = BigDecimal(48.346366)
        )

        val findRequest = hrpPreconditions.fillFindByCityPartRequest(
            Constants.hrpTestCityId, PointView(
                lat = 54.351954, lon = 48.281822
            ), PointView(
                lat = 54.306709, lon = 48.410224
            )
        )
        val darkstores = hrpActions.getDarkstoreByCityPart(findRequest)!!

        hrpAssertion
            .checkDarkstoreInDSList(darkstores.value, Constants.hrpTestDarktoreId2)
            .checkDarkstoreNotInDSList(darkstores.value, Constants.hrpTestDarktoreId1)
    }

    @Test
    @DisplayName("Find All Nearest: city without darkstores")
    fun findAllByCityPartWithoutDarsktoreTest() {
        hrpActions.createCityInDatabase()
        val findRequest = hrpPreconditions.fillFindByCityPartRequest(
            Constants.hrpTestCityId, PointView(
                lat = 54.351954, lon = 48.281822
            ), PointView(
                lat = 54.306709, lon = 48.410224
            )
        )
        val darkstores = hrpActions.getDarkstoreByCityPart(findRequest)!!

        hrpAssertion.checkDarkstoresListCount(darkstores, 0)

    }

    @Test
    @DisplayName("Find All Nearest: city not exists")
    fun findAllByCityPartCityNotExistTest() {
        val findRequest = hrpPreconditions.fillFindByCityPartRequest(
            Constants.hrpTestCityId, PointView(
                lat = 54.351954, lon = 48.281822
            ), PointView(
                lat = 54.306709, lon = 48.410224
            )
        )
        val error = hrpActions.getDarkstoreByCityPart(findRequest)!!
        hrpAssertion.checkValidationError(error!!.error.toString())
    }
}