package ru.samokat.mysamokat.tests.tests.hr_platform.darkstoresAPI

import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.samokat.hrp.map.api.model.PagingFilter
import ru.samokat.mysamokat.tests.checkers.HrpMapAssertions
import ru.samokat.mysamokat.tests.dataproviders.Constants
import ru.samokat.mysamokat.tests.dataproviders.preconditions.HrpMapPreconditions
import ru.samokat.mysamokat.tests.helpers.actions.HrpMapActions
import java.util.*

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Tag("hrp_map")
class FindAllByCity {

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

        hrpPreconditions = HrpMapPreconditions()
        hrpAssertion = HrpMapAssertions()
    }

    @AfterEach
    fun release() {
        hrpActions.deleteDarkstoreFromDB(Constants.hrpTestDarktoreId1)
        hrpActions.deleteDarkstoreFromDB(Constants.hrpTestDarktoreId2)
        hrpActions.deleteDarkstoreFromDB(Constants.hrpTestDarktoreId3)
        hrpActions.deleteCityFromDB(Constants.hrpTestCityId)
        hrpAssertion.assertAll()

    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Find All By City: several ds all types and status")
    fun findAllByCityTest() {

        hrpActions.createCityInDatabase()
        hrpActions.createDarkstoreInDatabase(
            name = "Хаб тест",
            type = "HUB",
            status = "PLANNED",
            darkstoreId = Constants.hrpTestDarktoreId2
        )
        hrpActions.createDarkstoreInDatabase(name = "Даркстор тест", darkstoreId = Constants.hrpTestDarktoreId1)
        hrpActions.createDarkstoreInDatabase(
            name = "АААА тест",
            status = "PLANNED",
            darkstoreId = Constants.hrpTestDarktoreId3
        )

        val findRequest = hrpPreconditions.fillFindAllByCityRequest(Constants.hrpTestCityId)
        val darkstores = hrpActions.getDarkstoresByCity(findRequest)

        hrpAssertion
            .checkDarkstoreData(
                darkstores!!.value.items[0],
                name = "АААА тест",
                status = "PLANNED",
                darkstoreId = Constants.hrpTestDarktoreId3
            )
            .checkDarkstoreData(
                darkstores.value.items[1],
                name = "Даркстор тест",
                darkstoreId = Constants.hrpTestDarktoreId1
            )
            .checkDarkstoreData(
                darkstores.value.items[2],
                name = "Хаб тест",
                darkstoreId = Constants.hrpTestDarktoreId2,
                status = "PLANNED",
                type = "HUB"
            )
    }

    @Test
    @DisplayName("Find All By City: empty list")
    fun findAllByCityEmptyListTest() {

        hrpActions.createCityInDatabase()

        val findRequest = hrpPreconditions.fillFindAllByCityRequest(Constants.hrpTestCityId)
        val darkstores = hrpActions.getDarkstoresByCity(findRequest)

        hrpAssertion.checkDarkstoresPageListCount(darkstores, 0)
    }

    @Test
    @DisplayName("Find All By City: paging")
    fun findAllByCityPagingTest() {

        hrpActions.createCityInDatabase()
        hrpActions.createDarkstoreInDatabase(
            name = "Хаб тест",
            type = "HUB",
            status = "PLANNED",
            darkstoreId = Constants.hrpTestDarktoreId2
        )
        hrpActions.createDarkstoreInDatabase(name = "Даркстор тест", darkstoreId = Constants.hrpTestDarktoreId1)
        hrpActions.createDarkstoreInDatabase(
            name = "АААА тест",
            status = "PLANNED",
            darkstoreId = Constants.hrpTestDarktoreId3
        )

        val findRequestPage1 = hrpPreconditions.fillFindAllByCityRequest(Constants.hrpTestCityId, PagingFilter(1, null))
        val darkstoresPage1 = hrpActions.getDarkstoresByCity(findRequestPage1)!!

        val findRequestPage2 = hrpPreconditions.fillFindAllByCityRequest(
            Constants.hrpTestCityId,
            PagingFilter(1, darkstoresPage1.value.paging.currentPage + 1)
        )
        val darkstoresPage2 = hrpActions.getDarkstoresByCity(findRequestPage2)!!

        val findRequestPage3 = hrpPreconditions.fillFindAllByCityRequest(
            Constants.hrpTestCityId,
            PagingFilter(1, darkstoresPage2.value.paging.currentPage + 1)
        )
        val darkstoresPage3 = hrpActions.getDarkstoresByCity(findRequestPage3)!!


        hrpAssertion
            .checkDarkstoreData(
                darkstoresPage1.value.items[0],
                name = "АААА тест",
                status = "PLANNED",
                darkstoreId = Constants.hrpTestDarktoreId3
            )
            .checkDarkstoreData(
                darkstoresPage2.value.items[0],
                name = "Даркстор тест",
                darkstoreId = Constants.hrpTestDarktoreId1
            )
            .checkDarkstoreData(
                darkstoresPage3.value.items[0],
                name = "Хаб тест",
                darkstoreId = Constants.hrpTestDarktoreId2,
                status = "PLANNED",
                type = "HUB"
            )
            .checkDarkstoresPageListCount(darkstoresPage1, 1)
            .checkDarkstoresPageListCount(darkstoresPage2, 1)
            .checkDarkstoresPageListCount(darkstoresPage3, 1)
            .checkDarstoresListPaging(darkstoresPage1.value.paging, 1, 3)
            .checkDarstoresListPaging(darkstoresPage2.value.paging, 2, 3)
            .checkDarstoresListPaging(darkstoresPage3.value.paging, 3, 3)
    }

    @Test
    @DisplayName("Find All By City: city not exists")
    fun findAllByCityNotExistsTest() {

        val findRequest = hrpPreconditions.fillFindAllByCityRequest(UUID.randomUUID())
        val error = hrpActions.getDarkstoresByCity(findRequest)

        hrpAssertion.checkValidationError(error!!.error.toString())
    }
}