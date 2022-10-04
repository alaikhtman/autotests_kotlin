package ru.samokat.mysamokat.tests.helpers.actions

import io.qameta.allure.Step
import org.jetbrains.exposed.sql.ResultRow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import ru.samokat.hrp.map.api.model.PagedView
import ru.samokat.hrp.map.api.model.city.CitiesError
import ru.samokat.hrp.map.api.model.city.CityView
import ru.samokat.hrp.map.api.model.city.FetchAvailableCitiesRequest
import ru.samokat.hrp.map.api.model.counterparty.preferences.CounterpartiesAuthorizedCitiesView
import ru.samokat.hrp.map.api.model.counterparty.preferences.CounterpartyPreferencesError
import ru.samokat.hrp.map.api.model.counterparty.preferences.UpdateCounterpartiesAuthorizedCityRequest
import ru.samokat.hrp.map.api.model.darkstore.DarkstoreError
import ru.samokat.hrp.map.api.model.darkstore.DarkstoreView
import ru.samokat.hrp.map.api.model.darkstore.RoutingError
import ru.samokat.hrp.map.api.model.darkstore.city.FindCityDarkstoreRequest
import ru.samokat.hrp.map.api.model.darkstore.city.FindCityPartDarkstoreRequest
import ru.samokat.hrp.map.api.model.darkstore.nearest.DistancedDarkstoreView
import ru.samokat.hrp.map.api.model.darkstore.nearest.FindNearestDarkstoresRequest
import ru.samokat.hrp.map.api.model.routing.AddressPointView
import ru.samokat.hrp.map.api.model.routing.FindAddressPointsRequest
import ru.samokat.hrp.map.api.model.routing.FindPrecisionDistanceRequest
import ru.samokat.hrp.map.api.model.routing.PrecisionDistanceView
import ru.samokat.hrp.map.api.model.user.preferences.UpdateUserPreferredCityRequest
import ru.samokat.hrp.map.api.model.user.preferences.UserPreferencesError
import ru.samokat.mysamokat.tests.dataproviders.Constants
import ru.samokat.mysamokat.tests.helpers.controllers.database.HrpMapDatabaseController
import ru.samokat.mysamokat.tests.helpers.controllers.hrp_map.*
import ru.samokat.platform.utils.Result
import java.math.BigDecimal
import java.util.*

@Component
@Scope("prototype")
class HrpMapActions {

    @Autowired
    lateinit var cpPreferencesController: CounterpartyPreferencesAPIController

    @Autowired
    lateinit var darkstoreController: HrpDarkstoresApiController

    @Autowired
    lateinit var routingController: RoutingApiController

    @Autowired
    lateinit var cityController: CityApiController

    @Autowired
    lateinit var userPreferencesController: UserPreferencesAPIController

    @Autowired
    private lateinit var databaseController: HrpMapDatabaseController

    // Counterparty Preferences API
    @Step("Submit Counterparty Preferences Cities")
    fun submitCounterpartyPreferencesCities(request: UpdateCounterpartiesAuthorizedCityRequest) {
        cpPreferencesController.submitPreferences(request)
    }

    @Step("Submit Counterparty Preferences Cities")
    fun submitCounterpartyPreferencesCitiesWithError(request: UpdateCounterpartiesAuthorizedCityRequest): Result<CounterpartyPreferencesError, Unit>? {
        return cpPreferencesController.submitPreferences(request)
    }

    @Step("Fetch Counterparty Preferences Cities By Counterparty ID")
    fun fetchAuthorizedCitiesByCounterpartyId(counterpartyId: UUID): List<CityView> {
        return cpPreferencesController.fetchPreferencesByCounterpartyId(counterpartyId)!!.value
    }

    @Step("Fetch Counterparty Preferences Cities")
    fun fetchAuthorizedCities(): CounterpartiesAuthorizedCitiesView {
        return cpPreferencesController.fetchPreferences()!!.value
    }

    // Darkstores API
    @Step("Get Darkstore By ID")
    fun getDarkstoreById(darkstoreId: UUID): Result<DarkstoreError, DarkstoreView>? {
        return darkstoreController.findById(darkstoreId)
    }

    @Step("Get All Darkstores By City")
    fun getDarkstoresByCity(request: FindCityDarkstoreRequest): Result<DarkstoreError, PagedView<DarkstoreView>>? {
        return darkstoreController.findAllByCity(request)
    }

    @Step("Get All Darkstores By City")
    fun getDarkstoresNearestPoint(request: FindNearestDarkstoresRequest): Result<DarkstoreError, PagedView<DistancedDarkstoreView>>? {
        return darkstoreController.findAllNearest(request)
    }

    @Step("Get Darkstores By City Part")
    fun getDarkstoreByCityPart(request: FindCityPartDarkstoreRequest): Result<DarkstoreError, List<DarkstoreView>>? {
        return darkstoreController.findAllByCityPart(request)
    }

    // Routing API
    @Step("Get Precision Time")
    fun getPrecisionTime(request: FindPrecisionDistanceRequest): Result<RoutingError, PrecisionDistanceView>? {
        return routingController.findPrecisionDistance(request)
    }

    @Step("Get Addresses")
    fun getAddresses(request: FindAddressPointsRequest): Result<RoutingError, List<AddressPointView>>? {
        return routingController.findAddressPoints(request)
    }

    // City API

    @Step("Get All Cities")
    fun getAllCities(): Result<CitiesError, List<CityView>>? {
        return cityController.fetchAll()
    }

    @Step("Get All Cities")
    fun getAvailableCities(request: FetchAvailableCitiesRequest): Result<CitiesError, List<CityView>>? {
        return cityController.fetchAvailable(request)
    }

    // User Preferences API

    @Step("Submit Preferred Cities")
    fun submitUserPreferredCities(request: UpdateUserPreferredCityRequest): Result<UserPreferencesError, Unit>? {
        return userPreferencesController.submitPreferredCities(request)
    }

    @Step("Fetch Preferred Cities")
    fun fetchUserPreferredCities(userId: UUID): Result<UserPreferencesError, List<CityView>>? {
        return userPreferencesController.fetchPreferredCities(userId)
    }

    // database
    @Step("Get Counterparty Preferences From Database")
    fun getCounterpartyPreferencesFromDB(counterpartyId: UUID): ResultRow {
        return databaseController.getCounterpartyPreferencesByCounterpartyId(counterpartyId)
    }

    @Step("Get City From Database")
    fun getCityByIdFromDB(cityId: UUID): ResultRow {
        return databaseController.getCityById(cityId)
    }

    @Step("Get Darkstore From Database By ID")
    fun getDarkstoreFromDatabaseById(darkstoreId: UUID): ResultRow {
        return databaseController.getDarkstoreById(darkstoreId)
    }

    @Step("Create Test Darkstore In Database")
    fun createDarkstoreInDatabase(
        darkstoreId: UUID = Constants.hrpTestDarktoreId1,
        cityId: UUID = Constants.hrpTestCityId,
        lat: BigDecimal = BigDecimal(53.305636),
        lon: BigDecimal = BigDecimal(50.287672),
        type: String = "DARKSTORE",
        status: String = "OPERATING",
        name: String = "Тестовый Даркстор Для Автотестов",
        address: String = "адрес тестового дарктора",
        email: String = "email.test@email.ru",
        phone: String = "80008908909",
        district: String = "Засвияжский"
    ) {
        return databaseController.createDarkstore(
            darkstoreId, name, type, status, address,
            cityId, lat, lon, email, phone, district
        )
    }

    @Step("Create Test Darkstore With end date In Database")
    fun createDarkstoreWithEndDateInDatabase(
        darkstoreId: UUID = Constants.hrpTestDarktoreId1,
        cityId: UUID = Constants.hrpTestCityId,
        lat: BigDecimal = BigDecimal(53.305636),
        lon: BigDecimal = BigDecimal(50.287672),
        type: String = "DARKSTORE",
        status: String = "OPERATING",
        name: String = "Тестовый Даркстор Для Автотестов",
        address: String = "адрес тестового дарктора",
        email: String = "email.test@email.ru",
        phone: String = "80008908909",
        district: String = "Засвияжский"
    ) {
        return databaseController.createDarkstoreWithEndDate(
            darkstoreId, name, type, status, address,
            cityId, lat, lon, email, phone, district
        )
    }

    @Step("Delete Darkstore In Database")
    fun deleteDarkstoreFromDB(darkstoreId: UUID) {
        databaseController.deleteDarkstoreById(darkstoreId)
    }

    @Step("Delete City In Database")
    fun deleteCityFromDB(cityId: UUID) {
        databaseController.deleteCityById(cityId)
    }

    @Step("Create City In Database")
    fun createCityInDatabase(
        cityId: UUID = Constants.hrpTestCityId,
        name: String = "Тестовый город для автотестов",
        lat: BigDecimal = BigDecimal(53.305636),
        lon: BigDecimal = BigDecimal(50.287672)
    ) {
        databaseController.createCity(cityId, name, lat, lon)
    }

    @Step("Create City In Database Without Coordinates")
    fun createCityInDatabaseWithoutCoordinates(
        cityId: UUID = Constants.hrpTestCityId,
        name: String = "Тестовый город для автотестов"
    ) {
        databaseController.createCityWithoutCoordinates(cityId, name)
    }

    @Step("Get User Preferred Cities From Database")
    fun getUserPreferredCitiesFromDatabase(userId: UUID): ResultRow {
        return databaseController.getUserPreferencesByUserId(userId)
    }

    @Step("Get User Preferred Cities Existence From Database")
    fun getUserPreferredCitiesExistenceFromDatabase(userId: UUID): Boolean {
        return databaseController.getUserPreferencesExistenceByUserId(userId)
    }
}