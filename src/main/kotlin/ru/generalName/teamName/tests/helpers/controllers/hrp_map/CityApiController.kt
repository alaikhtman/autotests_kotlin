package ru.samokat.mysamokat.tests.helpers.controllers.hrp_map

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.samokat.hrp.map.api.CityClient
import ru.samokat.hrp.map.api.model.city.CitiesError
import ru.samokat.hrp.map.api.model.city.CityView
import ru.samokat.hrp.map.api.model.city.FetchAvailableCitiesRequest
import ru.samokat.hrp.map.api.model.counterparty.preferences.CounterpartyPreferencesError
import ru.samokat.hrp.map.api.model.counterparty.preferences.UpdateCounterpartiesAuthorizedCityRequest
import ru.samokat.platform.utils.Result

@Service
class CityApiController {

    @Autowired
    lateinit var cityFeign: CityClient

    fun fetchAll(): Result<CitiesError, List<CityView>>? {
        return try {
            cityFeign.fetchAll().get()
        } catch (e: InterruptedException) {
            e.printStackTrace()
            null
        }
    }

    fun fetchAvailable(request: FetchAvailableCitiesRequest): Result<CitiesError, List<CityView>>? {
        return try {
            cityFeign.fetchAvailable(request).get()
        } catch (e: InterruptedException) {
            e.printStackTrace()
            null
        }
    }

}