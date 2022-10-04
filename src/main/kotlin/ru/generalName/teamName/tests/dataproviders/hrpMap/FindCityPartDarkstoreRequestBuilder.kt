package ru.samokat.mysamokat.tests.dataproviders.hrpMap

import ru.samokat.hrp.map.api.model.CityId
import ru.samokat.hrp.map.api.model.PagingFilter
import ru.samokat.hrp.map.api.model.PointView
import ru.samokat.hrp.map.api.model.darkstore.city.FindCityPartDarkstoreRequest
import ru.samokat.hrp.map.api.model.darkstore.nearest.FindNearestDarkstoresRequest

class FindCityPartDarkstoreRequestBuilder {
    private lateinit var cityId: CityId
    fun cityId(cityId: CityId) = apply { this.cityId = cityId }
    fun getCityId(): CityId { return this.cityId }

    private lateinit var from: PointView
    fun from(from: PointView) = apply { this.from = from }
    fun getFrom(): PointView { return this.from }

    private lateinit var to: PointView
    fun to(to: PointView) = apply { this.to = to }
    fun getTo(): PointView { return this.to }

    fun build(): FindCityPartDarkstoreRequest {
        return FindCityPartDarkstoreRequest(
            cityId = cityId,
            from = from,
            to = to
        )
    }
}