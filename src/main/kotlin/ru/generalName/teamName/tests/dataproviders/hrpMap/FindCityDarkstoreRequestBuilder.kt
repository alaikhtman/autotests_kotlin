package ru.samokat.mysamokat.tests.dataproviders.hrpMap

import ru.samokat.hrp.map.api.model.CityId
import ru.samokat.hrp.map.api.model.PagingFilter
import ru.samokat.hrp.map.api.model.darkstore.city.FindCityDarkstoreRequest

class FindCityDarkstoreRequestBuilder {

    private lateinit var cityId: CityId
    fun cityId(cityId: CityId) = apply { this.cityId = cityId }
    fun getCityId(): CityId { return this.cityId }

    private lateinit var pagingFilter: PagingFilter
    fun pagingFilter(pagingFilter: PagingFilter) = apply { this.pagingFilter = pagingFilter }
    fun getPagingFilter(): PagingFilter { return this.pagingFilter }

    fun build(): FindCityDarkstoreRequest {
        return FindCityDarkstoreRequest(
            cityId = cityId,
            pagingFilter = pagingFilter
        )
    }

}