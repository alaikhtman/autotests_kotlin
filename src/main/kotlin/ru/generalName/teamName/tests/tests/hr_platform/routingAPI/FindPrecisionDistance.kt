package ru.samokat.mysamokat.tests.tests.hr_platform.routingAPI

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
class FindPrecisionDistance {

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
    @DisplayName("Find Precision Distance: 0 min pedestrian and 0 min bike time")
    fun findDistance0minPedestrian0minBikeTest() {

        hrpActions.createCityInDatabase()
        hrpActions.createDarkstoreInDatabase(
            darkstoreId = Constants.hrpTestDarktoreId1,
            lat = BigDecimal(53.249801),
            lon = BigDecimal(50.208638)
        )

        val findDistanceRequest = hrpPreconditions.fillFindPrecisionDistanceRequest (
            Constants.hrpTestDarktoreId1, PointView(
                lat = 53.249801, lon = 50.208638
            )
        )
        val distance = hrpActions.getPrecisionTime(findDistanceRequest)!!.value

        hrpAssertion
            .checkRoutingTimeData(
                bicycleTime = 0, pedestrianTime = 0, distance = distance
            )
    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Find Precision Distance: darkstore not found")
    fun findDistanceDarkstoreNotFoundTest() {


        val findDistanceRequest = hrpPreconditions.fillFindPrecisionDistanceRequest (
            darkstoreId = Constants.hrpTestDarktoreId4, PointView(
                lat = 53.249801, lon = 50.208638
            )
        )

        val error = hrpActions.getPrecisionTime(findDistanceRequest)!!
        hrpAssertion.checkValidationError(error!!.error.toString())
    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Find Precision Distance: 204 code both time")
    fun findDistanceNoTimePedestrianAndBikeTest() {

        hrpActions.createCityInDatabase()
        hrpActions.createDarkstoreInDatabase(
            darkstoreId = Constants.hrpTestDarktoreId1,
            lat = BigDecimal(53.249801),
            lon = BigDecimal(50.208638)
        )

        val findDistanceRequest = hrpPreconditions.fillFindPrecisionDistanceRequest (
            Constants.hrpTestDarktoreId1, PointView(
                lat = 56.265164, lon = 43.883053
            )
        )
        val distance = hrpActions.getPrecisionTime(findDistanceRequest)!!.value

        hrpAssertion
            .checkEmptyTimeData(distance)
    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Find Precision Distance: >0 pedestrian and 0 bike time")
    fun findDistancePedestrianAnd0BikeTest() {

        hrpActions.createCityInDatabase()
        hrpActions.createDarkstoreInDatabase(
            darkstoreId = Constants.hrpTestDarktoreId1,
            lat = BigDecimal(55.666206),
            lon = BigDecimal(37.914063)
        )

        val findDistanceRequest = hrpPreconditions.fillFindPrecisionDistanceRequest (
            Constants.hrpTestDarktoreId1, PointView(
                lat = 55.666206, lon = 37.914063
            )
        )
        val distance = hrpActions.getPrecisionTime(findDistanceRequest)!!.value

        hrpAssertion
            .checkRoutingTimeData(
                bicycleTime = 0, pedestrianTime = 12, distance = distance
            )
    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Find Precision Distance: >0 pedestrian and >0 bike time")
    fun findDistancePedestrianAndBikeTest() {

        hrpActions.createCityInDatabase()
        hrpActions.createDarkstoreInDatabase(
            darkstoreId = Constants.hrpTestDarktoreId1,
            lat = BigDecimal(55.856693),
            lon = BigDecimal(37.515308)
        )

        val findDistanceRequest = hrpPreconditions.fillFindPrecisionDistanceRequest (
            Constants.hrpTestDarktoreId1, PointView(
                lat = 55.852886, lon = 37.518244
            )
        )
        val distance = hrpActions.getPrecisionTime(findDistanceRequest)!!.value

        hrpAssertion
            .checkRoutingTimeData(
                bicycleTime = 206, pedestrianTime = 433, distance = distance
            )
    }

    @Test //не подключается мок((
    @Tags(Tag("smoke"))
    @DisplayName("Find Precision Distance: >0 pedestrian and 204 bike time")
    fun findDistancePedestrianAnd204BikeTest() {

        hrpActions.createCityInDatabase()
        hrpActions.createDarkstoreInDatabase(
            darkstoreId = Constants.hrpTestDarktoreId1,
            lat = BigDecimal(59.831306),
            lon = BigDecimal(30.532991)
        )

        val findDistanceRequest = hrpPreconditions.fillFindPrecisionDistanceRequest (
            Constants.hrpTestDarktoreId1, PointView(
                lat = 60.1, lon = 30.6
            )
        )
        val distance = hrpActions.getPrecisionTime(findDistanceRequest)!!.value

        hrpAssertion
            .checkRoutingTimeData(
                bicycleTime = 206, pedestrianTime = 433, distance = distance
            )
    }

}
