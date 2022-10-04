package ru.samokat.mysamokat.tests.helpers.controllers.hrp_map

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.samokat.hrp.map.api.UserPreferenceClient
import ru.samokat.hrp.map.api.model.city.CityView
import ru.samokat.hrp.map.api.model.darkstore.DarkstoreError
import ru.samokat.hrp.map.api.model.darkstore.DarkstoreView
import ru.samokat.hrp.map.api.model.user.preferences.UpdateUserPreferredCityRequest
import ru.samokat.hrp.map.api.model.user.preferences.UserPreferencesError
import ru.samokat.platform.utils.Result
import java.util.*

@Service
class UserPreferencesAPIController {


    @Autowired
    lateinit var userPreferencesFeign: UserPreferenceClient

    fun submitPreferredCities(request: UpdateUserPreferredCityRequest): Result<UserPreferencesError, Unit>? {
        return try {
            userPreferencesFeign.submitPreferredCities(request).get()
        } catch (e: InterruptedException) {
            e.printStackTrace()
            null
        }
    }

    fun fetchPreferredCities(userId: UUID): Result<UserPreferencesError, List<CityView>>? {
        return try {
            userPreferencesFeign.fetchPreferredCities(userId).get()
        } catch (e: InterruptedException) {
            e.printStackTrace()
            null
        }
    }
}