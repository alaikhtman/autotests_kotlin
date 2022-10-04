package ru.samokat.mysamokat.tests.dataproviders.hrpMap

import ru.samokat.hrp.map.api.model.CityId
import ru.samokat.hrp.map.api.model.PagingFilter
import ru.samokat.hrp.map.api.model.PointView
import ru.samokat.hrp.map.api.model.darkstore.city.FindCityDarkstoreRequest
import ru.samokat.hrp.map.api.model.darkstore.nearest.FindNearestDarkstoresRequest

class FindNearestDarkstoresRequestBuilder {
    private lateinit var cityId: CityId
    fun cityId(cityId: CityId) = apply { this.cityId = cityId }
    fun getCityId(): CityId { return this.cityId }

    private lateinit var point: PointView
    fun point(point: PointView) = apply { this.point = point }
    fun getPoint(): PointView { return this.point }

    private lateinit var pagingFilter: PagingFilter
    fun pagingFilter(pagingFilter: PagingFilter) = apply { this.pagingFilter = pagingFilter }
    fun getPagingFilter(): PagingFilter { return this.pagingFilter }

    fun build(): FindNearestDarkstoresRequest {
        return FindNearestDarkstoresRequest(
            cityId = cityId,
            pagingFilter = pagingFilter,
            point = point
        )
    }
}