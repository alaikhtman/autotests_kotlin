package ru.samokat.mysamokat.tests.helpers.controllers.hrp_map

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.samokat.hrp.map.api.CounterpartyPreferenceClient
import ru.samokat.hrp.map.api.model.city.CityView
import ru.samokat.hrp.map.api.model.counterparty.preferences.CounterpartiesAuthorizedCitiesView
import ru.samokat.hrp.map.api.model.counterparty.preferences.CounterpartyPreferencesError
import ru.samokat.hrp.map.api.model.counterparty.preferences.UpdateCounterpartiesAuthorizedCityRequest
import ru.samokat.platform.utils.Result
import java.util.*

@Service
class CounterpartyPreferencesAPIController {

    @Autowired
    lateinit var cpPreferencesFeign: CounterpartyPreferenceClient

    fun submitPreferences(request: UpdateCounterpartiesAuthorizedCityRequest): Result<CounterpartyPreferencesError, Unit>? {
        return try {
            cpPreferencesFeign.submitAuthorizedCities(request).get()
        } catch (e: InterruptedException) {
            e.printStackTrace()
            null
        }
    }

    fun fetchPreferencesByCounterpartyId(counterPartyId: UUID): Result<CounterpartyPreferencesError, List<CityView>>? {
        return try {
            cpPreferencesFeign.fetchAuthorizedCities(counterPartyId).get()
        } catch (e: InterruptedException) {
            e.printStackTrace()
            null
        }
    }


    fun fetchPreferences(): Result<CounterpartyPreferencesError, CounterpartiesAuthorizedCitiesView>? {
        return try{
            cpPreferencesFeign.fetchAuthorizedCities().get()
        }
        catch (e: InterruptedException) {
            e.printStackTrace()
            null
        }
    }
}