package ru.samokat.mysamokat.tests.dataproviders.hrpMap

import ru.samokat.hrp.map.api.model.CityId
import ru.samokat.hrp.map.api.model.CounterpartyId
import ru.samokat.hrp.map.api.model.counterparty.preferences.UpdateCounterpartiesAuthorizedCityRequest
import ru.samokat.hrp.map.api.model.counterparty.preferences.UpdateCounterpartyAuthorizedCityRequest

class UpdateCounterpartiesAuthorizedCityRequestBuilder {

    private lateinit var counterparties: List<UpdateCounterpartyAuthorizedCityRequest>
    fun counterparties(counterparties: List<UpdateCounterpartyAuthorizedCityRequest>) = apply { this.counterparties = counterparties }
    fun getCounterparties(): List<UpdateCounterpartyAuthorizedCityRequest> { return this.counterparties }

    fun build(): UpdateCounterpartiesAuthorizedCityRequest {
        return UpdateCounterpartiesAuthorizedCityRequest(
            counterparties = counterparties
        )
    }
}

class UpdateCounterpartyAuthorizedCityRequestBuilder{

    private lateinit var  cities: List<CityId>
    fun cities(cities: List<CityId>) = apply { this.cities = cities }
    fun getCities(): List<CityId> { return this.cities }

    private lateinit  var counterpartyId: CounterpartyId
    fun counterpartyId(counterpartyId: CounterpartyId) = apply { this.counterpartyId = counterpartyId }
    fun getCounterpartyId(): CounterpartyId { return this.counterpartyId }

    fun build(): UpdateCounterpartyAuthorizedCityRequest {
        return UpdateCounterpartyAuthorizedCityRequest(
            cities = cities,
            counterpartyId = counterpartyId
        )
    }
}