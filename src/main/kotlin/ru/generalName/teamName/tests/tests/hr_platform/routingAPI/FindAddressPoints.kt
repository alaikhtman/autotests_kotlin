package ru.samokat.mysamokat.tests.tests.hr_platform.routingAPI

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
class FindAddressPoints {

    private lateinit var hrpPreconditions: HrpMapPreconditions

    @Autowired
    private lateinit var hrpActions: HrpMapActions

    private lateinit var hrpAssertion: HrpMapAssertions

    @BeforeEach
    fun before() {
        hrpActions.deleteCityFromDB(Constants.hrpTestCityId)

        hrpPreconditions = HrpMapPreconditions()
        hrpAssertion = HrpMapAssertions()
    }

    @AfterEach
    fun release() {
        hrpActions.deleteCityFromDB(Constants.hrpTestCityId)
        hrpAssertion.assertAll()

    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Find Address Points: empty result")
    fun findAddressPointsEmptyTest() {

        hrpActions.createCityInDatabase()

        val findAddressPointsRequest = hrpPreconditions.fillFindAddressPointsRequest(
            cityId = Constants.hrpTestCityId,
            query = "11312223113"
        )

        val addresses = hrpActions.getAddresses(findAddressPointsRequest)!!

        hrpAssertion
            .checkAddressListCount(addresses=addresses, count = 0)
    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Find Address Points: city not exist")
    fun findAddressPointsCityNotExistTest() {

        hrpActions.createCityInDatabase()

        val findAddressPointsRequest = hrpPreconditions.fillFindAddressPointsRequest(
            cityId = Constants.hrpTestCityId1,
            query = "ленина"
        )

        val error = hrpActions.getAddresses(findAddressPointsRequest)!!
        hrpAssertion.checkValidationError(error!!.error.toString())
    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Find Address Points: many results")
    fun findAddressPointsManyResultsTest() {

        hrpActions.createCityInDatabase()

        val findAddressPointsRequest = hrpPreconditions.fillFindAddressPointsRequest(
            cityId = Constants.hrpTestCityId,
            query = "ленина, 2"
        )

        val addresses = hrpActions.getAddresses(findAddressPointsRequest)!!

        hrpAssertion
            .checkAddressListCount(addresses=addresses, count = 10)
    }

}
