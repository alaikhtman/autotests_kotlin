package ru.samokat.mysamokat.tests.checkers

import io.qameta.allure.Step
import org.assertj.core.api.SoftAssertions
import org.jetbrains.exposed.sql.ResultRow
import org.springframework.stereotype.Service
import ru.samokat.hrp.map.api.model.PageInfoView
import ru.samokat.hrp.map.api.model.PagedView
import ru.samokat.hrp.map.api.model.city.CityView
import ru.samokat.hrp.map.api.model.counterparty.preferences.CounterpartiesAuthorizedCitiesView
import ru.samokat.hrp.map.api.model.counterparty.preferences.UpdateCounterpartiesAuthorizedCityRequest
import ru.samokat.hrp.map.api.model.darkstore.DarkstoreError
import ru.samokat.hrp.map.api.model.darkstore.DarkstoreView
import ru.samokat.hrp.map.api.model.darkstore.RoutingError
import ru.samokat.hrp.map.api.model.darkstore.nearest.DistancedDarkstoreView
import ru.samokat.hrp.map.api.model.routing.AddressPointView
import ru.samokat.hrp.map.api.model.routing.PrecisionDistanceView
import ru.samokat.hrp.map.api.model.user.preferences.UserPreferencesError
import ru.samokat.mysamokat.tests.dataproviders.Constants
import ru.samokat.mysamokat.tests.dataproviders.helpers.database.hrpMap.Cities
import ru.samokat.mysamokat.tests.dataproviders.helpers.database.hrpMap.CounterpartyPreferences
import ru.samokat.mysamokat.tests.dataproviders.helpers.database.hrpMap.UserPreferences
import ru.samokat.platform.utils.Result
import java.util.*

@Service
class HrpMapAssertions {

    private val softAssertion = SoftAssertions()
    fun getSoftAssertion(): SoftAssertions {
        return softAssertion
    }

    fun assertAll() {
        getSoftAssertion().assertAll()
    }

    // Counterparty Preferences API
    @Step("Check Counterparty Preferences")
    fun checkCounterpartyPreferencesInDB(
        counterpartyPreferences: ResultRow,
        submitRequest: UpdateCounterpartiesAuthorizedCityRequest,
        counterpartyId: UUID = submitRequest.counterparties[0].counterpartyId
    ): HrpMapAssertions {
        val cpRequest = submitRequest.counterparties.filter { it.counterpartyId == counterpartyId }.first()
        getSoftAssertion().assertThat(counterpartyPreferences[CounterpartyPreferences.authorizedCities])
            .isEqualTo(cpRequest.cities.joinToString())
        return this
    }

    @Step("Check Counterparty Preferences")
    fun checkCounterpartyPreferences(
        counterpartyPreferences: List<CityView>,
        cities: List<ResultRow>
    ): HrpMapAssertions {

        for (i in 0 until cities.count()) {
            val cpCity = counterpartyPreferences.filter { it.id == cities[i][Cities.cityId] }.first()

            getSoftAssertion().assertThat(cpCity.name).isEqualTo(cities[i][Cities.name])
            getSoftAssertion().assertThat(cpCity.point!!.lat).isEqualTo(cities[i][Cities.lat].toDouble())
            getSoftAssertion().assertThat(cpCity.point!!.lon).isEqualTo(cities[i][Cities.lon].toDouble())
        }
        return this
    }

    @Step("Check Counterparty Preferences")
    fun checkCounterpartyPreferencesInList(
        counterpartyPreferencesList: CounterpartiesAuthorizedCitiesView,
        counterparty: UUID,
        cities: List<ResultRow>
    ): HrpMapAssertions {

        val counterpartyPreferences =
            counterpartyPreferencesList.counterparties.filter { it.counterpartyId == counterparty }.first()

        for (i in 0 until cities.count()) {
            getSoftAssertion().assertThat(counterpartyPreferences.cities.contains(cities[i][Cities.cityId])).isTrue
        }
        return this
    }

    @Step("Check Counterparty Preferences In List Is Empty")
    fun checkCounterpartyPreferencesInListIsEmpty(
        counterpartyPreferencesList: CounterpartiesAuthorizedCitiesView,
        counterparty: UUID
    ): HrpMapAssertions {
        val a = 0

        val counterpartyPreferences =
            counterpartyPreferencesList.counterparties.filter { it.counterpartyId == counterparty }.first()
        getSoftAssertion().assertThat(counterpartyPreferences.cities.size).isEqualTo(0)
        return this
    }

    @Step("Check Counterparty Preferences")
    fun checkCounterpartyPreferencesListIsEmpty(
        counterpartyPreferences: List<CityView>
    ): HrpMapAssertions {

        getSoftAssertion().assertThat(counterpartyPreferences.size).isEqualTo(0)
        return this
    }

    // Roiting API

    @Step("Check Routing Time Data")
    fun checkRoutingTimeData(
        distance: PrecisionDistanceView,
        bicycleTime: Int,
        pedestrianTime: Int
        ): HrpMapAssertions {
        getSoftAssertion().assertThat(distance.bicycleTime).isEqualTo(bicycleTime)
        getSoftAssertion().assertThat(distance.pedestrianTime).isEqualTo(pedestrianTime)

        return this
    }

    @Step("Check Empty Time Data")
    fun checkEmptyTimeData(
        distance: PrecisionDistanceView
    ): HrpMapAssertions {
        getSoftAssertion().assertThat(distance.pedestrianTime).isNull()
        getSoftAssertion().assertThat(distance.bicycleTime).isNull()

        return this
    }

    @Step("Check Empty pedestrianTime")
    fun checkEmptyPedestrianTime(
        distance: PrecisionDistanceView
    ): HrpMapAssertions {
        getSoftAssertion().assertThat(distance.pedestrianTime).isNull()
        return this
    }

    @Step("Check Empty bicycleTime")
    fun checkEmptyBicycleTime(
        distance: PrecisionDistanceView
    ): HrpMapAssertions {
        getSoftAssertion().assertThat(distance.bicycleTime).isNull()
        return this
    }


    @Step("Check Addresses List Count")
    fun checkAddressListCount(
        addresses: Result<RoutingError, List<AddressPointView>>?,
        count: Int
    ): HrpMapAssertions {
        getSoftAssertion().assertThat(addresses!!.value.count()).isEqualTo(count)

        return this
    }

    // Darkstores API

    @Step("Check Darktore Data")
    fun checkDarkstoreData(
        darkstore: DarkstoreView,
        darkstoreId: UUID = Constants.hrpTestDarktoreId1,
        cityId: UUID = Constants.hrpTestCityId,
        lat: Double = 53.305636,
        lon: Double = 50.287672,
        type: String = "DARKSTORE",
        status: String = "OPERATING",
        name: String = "Тестовый Даркстор Для Автотестов",
        address: String = "адрес тестового дарктора",
        email: String = "email.test@email.ru",
        phone: String = "80008908909",
        district: String = "Засвияжский"
    ): HrpMapAssertions {
        getSoftAssertion().assertThat(darkstore.id).isEqualTo(darkstoreId)
        getSoftAssertion().assertThat(darkstore.coordinates.lat).isEqualTo(lat)
        getSoftAssertion().assertThat(darkstore.coordinates.lon).isEqualTo(lon)
        getSoftAssertion().assertThat(darkstore.type.toString()).isEqualTo(type)
        getSoftAssertion().assertThat(darkstore.status.toString()).isEqualTo(status)
        getSoftAssertion().assertThat(darkstore.name).isEqualTo(name)
        getSoftAssertion().assertThat(darkstore.address).isEqualTo(address)
        getSoftAssertion().assertThat(darkstore.district).isEqualTo(district)
        return this
    }


    @Step("Check Darkstore Data")
    fun checkDistanceDarkstoreData(
        darkstore: DistancedDarkstoreView,
        name: String,
        darkstoreId: UUID,
        status: String = "OPERATING",
        type: String = "DARKSTORE",
        meters: Double,
        address: String = "адрес тестового дарктора",
        district: String = "Засвияжский",
        lat: Double = 53.305636,
        lon: Double = 50.287672,
    ): HrpMapAssertions {
        getSoftAssertion().assertThat(darkstore.id).isEqualTo(darkstoreId)
        getSoftAssertion().assertThat(darkstore.distanceToPoint.meters).isEqualTo(meters)
        getSoftAssertion().assertThat(darkstore.name).isEqualTo(name)
        getSoftAssertion().assertThat(darkstore.status.toString()).isEqualTo(status)
        getSoftAssertion().assertThat(darkstore.type.toString()).isEqualTo(type)
        getSoftAssertion().assertThat(darkstore.address).isEqualTo(address)
        getSoftAssertion().assertThat(darkstore.district).isEqualTo(district)
        getSoftAssertion().assertThat(darkstore.coordinates.lon).isEqualTo(lon)
        getSoftAssertion().assertThat(darkstore.coordinates.lat).isEqualTo(lat)
        return this
    }


    @Step("Check Darktores List Count")
    fun checkDarkstoresPageListCount(
        darkstores: Result<DarkstoreError, PagedView<DarkstoreView>>?,
        count: Int
    ): HrpMapAssertions {
        getSoftAssertion().assertThat(darkstores!!.value.items.count()).isEqualTo(count)
        return this
    }

    @Step("Check Darktores List Count")
    fun checkDarkstoresListCount(
        darkstores: Result<DarkstoreError, List<DarkstoreView>>,
        count: Int
    ): HrpMapAssertions {
        getSoftAssertion().assertThat(darkstores!!.value.count()).isEqualTo(count)
        return this
    }

    @Step("Check Darktores List Count")
    fun checkDarstoresDistanceListCount(
        darkstores: Result<DarkstoreError, PagedView<DistancedDarkstoreView>>,
        count: Int
    ): HrpMapAssertions {
        getSoftAssertion().assertThat(darkstores!!.value.items.count()).isEqualTo(count)
        return this
    }


    @Step("Check Darktores List Paging")
    fun checkDarstoresListPaging(
        darkstores: PageInfoView,
        currentPage: Int,
        totalPages: Int
    ): HrpMapAssertions {
        getSoftAssertion().assertThat(darkstores!!.currentPage).isEqualTo(currentPage)
        getSoftAssertion().assertThat(darkstores!!.totalPages).isEqualTo(totalPages)
        return this
    }


    @Step("Check Darktores Not In List")
    fun checkDarkstoreNotInList(darkstores: List<DistancedDarkstoreView>, darkstoreId: UUID): HrpMapAssertions {
        val darkstore = darkstores.filter { it.id == darkstoreId }
        getSoftAssertion().assertThat(darkstore.count()).isEqualTo(0)
        return this
    }


    @Step("Check Darktores In List")
    fun checkDarkstoreInList(darkstores: List<DistancedDarkstoreView>, darkstoreId: UUID): HrpMapAssertions {
        val darkstore = darkstores.filter { it.id == darkstoreId }
        getSoftAssertion().assertThat(darkstore.count()).isEqualTo(1)
        return this
    }


    @Step("Check Darktores In List")
    fun checkDarkstoreInDSList(darkstores: List<DarkstoreView>, darkstoreId: UUID): HrpMapAssertions {
        val darkstore = darkstores.filter { it.id == darkstoreId }
        getSoftAssertion().assertThat(darkstore.count()).isEqualTo(1)
        return this
    }


    @Step("Check Darktores Not In List")
    fun checkDarkstoreNotInDSList(darkstores: List<DarkstoreView>, darkstoreId: UUID): HrpMapAssertions {
        val darkstore = darkstores.filter { it.id == darkstoreId }
        getSoftAssertion().assertThat(darkstore.count()).isEqualTo(0)
        return this
    }

    //City API

    @Step("Check City Data In List")
    fun checkCityDataInList(
        cities: List<CityView>,
        cityId: UUID,
        name: String =  "Тестовый город для автотестов",
        lat: Double = 53.305636,
        lon: Double = 50.287672,
    ): HrpMapAssertions {
        val city = cities.filter { it.id == cityId }.first()
        getSoftAssertion().assertThat(city.name).isEqualTo(name)
        getSoftAssertion().assertThat(city.point!!.lat).isEqualTo(lat)
        getSoftAssertion().assertThat(city.point!!.lon).isEqualTo(lon)
        return this
    }

    @Step("Check City Present In List")
    fun checkCityPresentInList(cities: List<CityView>, cityId: UUID): HrpMapAssertions {
        val city = cities.filter { it.id == cityId }
        getSoftAssertion().assertThat(city.count()).isEqualTo(1)
        return this
    }

    @Step("Check City Not Present In List")
    fun checkCityNotPresentInList(cities: List<CityView>, cityId: UUID): HrpMapAssertions {
        val city = cities.filter { it.id == cityId }
        getSoftAssertion().assertThat(city.count()).isEqualTo(0)
        return this
    }

    @Step("Check Cities List Count")
    fun checkCitiesListCount(cities: List<CityView>, count: Int): HrpMapAssertions{
        getSoftAssertion().assertThat(cities.count()).isEqualTo(count)
        return this
    }

    // User Preferences API

    @Step("Check User Preferences In Database")
    fun checkUserPreferencesInDatabase(savedPreferences: ResultRow, cities: List<UUID>): HrpMapAssertions {
        getSoftAssertion().assertThat(savedPreferences[UserPreferences.preferredCities]).isEqualTo(cities.joinToString ())
        return this
    }

    @Step("Check User Preferences In Database")
    fun checkUserPreferences(savedPreferences: Result<UserPreferencesError, List<CityView>>?, cities: List<UUID>): HrpMapAssertions {
        getSoftAssertion().assertThat(savedPreferences!!.value.map { it.id }).isEqualTo(cities)
        return this
    }

    @Step("Check User Preference Existence")
    fun checkUserPreferencesNotExists(existence: Boolean): HrpMapAssertions {
        getSoftAssertion().assertThat(existence).isFalse
        return this
    }

    // common
    @Step("Check Validation Error")
    fun checkValidationError(error: String): HrpMapAssertions {
        getSoftAssertion().assertThat(error).isEqualTo("VALIDATION_ERROR")
        return this
    }

    @Step("Check Darkstore Not Found Error")
    fun checkDarkstoreNotFoundError(error: String): HrpMapAssertions {
        getSoftAssertion().assertThat(error).isEqualTo("DARKSTORE_NOT_FOUND")
        return this
    }

    @Step("Check Cities Not Fetched Error Error")
    fun checkCitiesNotFetchedError(error: String): HrpMapAssertions {
        getSoftAssertion().assertThat(error).isEqualTo("CITIES_NOT_FETCHED_COUNTERPARTY_NOT_FETCHED")
        return this
    }

}