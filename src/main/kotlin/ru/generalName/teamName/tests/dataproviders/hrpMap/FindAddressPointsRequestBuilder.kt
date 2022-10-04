package ru.samokat.mysamokat.tests.dataproviders.hrpMap

import ru.samokat.hrp.map.api.model.CityId
import ru.samokat.hrp.map.api.model.routing.FindAddressPointsRequest

class FindAddressPointsRequestBuilder {

    private lateinit var cityId: CityId
    fun cityId(cityId: CityId) = apply { this.cityId = cityId }
    fun getCityId(): CityId { return this.cityId }

    private lateinit var query: String
    fun query(query: String) = apply { this.query = query }
    fun getQuery(): String { return this.query }

    fun build(): FindAddressPointsRequest {
        return FindAddressPointsRequest(
            cityId = cityId,
            query = query
        )
    }

}