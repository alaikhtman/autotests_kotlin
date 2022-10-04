package ru.samokat.mysamokat.tests.dataproviders.hrpMap

import ru.samokat.hrp.map.api.model.CityId
import ru.samokat.hrp.map.api.model.UserId
import ru.samokat.hrp.map.api.model.counterparty.preferences.UpdateCounterpartiesAuthorizedCityRequest
import ru.samokat.hrp.map.api.model.counterparty.preferences.UpdateCounterpartyAuthorizedCityRequest
import ru.samokat.hrp.map.api.model.user.preferences.UpdateUserPreferredCityRequest

class UpdateUserPreferredCityRequestBuilder {

    private lateinit var cities: List<CityId>
    fun cities(cities: List<CityId>) = apply { this.cities = cities }
    fun getCities(): List<CityId> { return this.cities }

    private lateinit var userId: UserId
    fun userId(userId: UserId) = apply { this.userId = userId }
    fun getUserId(): UserId { return this.userId }

    fun build(): UpdateUserPreferredCityRequest {
        return UpdateUserPreferredCityRequest(
            cities = cities,
            userId = userId
        )
    }
}