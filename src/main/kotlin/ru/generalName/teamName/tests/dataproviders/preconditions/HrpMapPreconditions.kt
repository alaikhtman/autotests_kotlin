package ru.samokat.mysamokat.tests.dataproviders.preconditions

import ru.samokat.hrp.map.api.model.*
import ru.samokat.hrp.map.api.model.city.FetchAvailableCitiesRequest
import ru.samokat.hrp.map.api.model.counterparty.preferences.UpdateCounterpartiesAuthorizedCityRequest
import ru.samokat.hrp.map.api.model.counterparty.preferences.UpdateCounterpartyAuthorizedCityRequest
import ru.samokat.hrp.map.api.model.darkstore.city.FindCityDarkstoreRequest
import ru.samokat.hrp.map.api.model.darkstore.city.FindCityPartDarkstoreRequest
import ru.samokat.hrp.map.api.model.darkstore.nearest.FindNearestDarkstoresRequest
import ru.samokat.hrp.map.api.model.routing.FindAddressPointsRequest
import ru.samokat.hrp.map.api.model.routing.FindPrecisionDistanceRequest
import ru.samokat.hrp.map.api.model.user.preferences.UpdateUserPreferredCityRequest
import ru.samokat.mysamokat.tests.dataproviders.hrpMap.*
import java.util.*

class HrpMapPreconditions {

    // Counterparty Preferences API----------------------------------------------------------------
    fun fillSubmitAuthorizedCitiesRequest(
        counterpartiesId: List<CounterpartyId>,
        cities: List<CityId>
    ): UpdateCounterpartiesAuthorizedCityRequest {

        val counterpartiesList = mutableListOf<UpdateCounterpartyAuthorizedCityRequest>()
        counterpartiesId.forEach {
            counterpartiesList.add(fillUpdateCounterpartyAuthorizedCityRequest(it, cities))
        }
        return UpdateCounterpartiesAuthorizedCityRequestBuilder()
            .counterparties(
                counterpartiesList
            )
            .build()

    }

    fun fillUpdateCounterpartyAuthorizedCityRequest(
        counterpartyId: CounterpartyId,
        cities: List<CityId>
    ): UpdateCounterpartyAuthorizedCityRequest {
        return UpdateCounterpartyAuthorizedCityRequestBuilder()
            .cities(cities)
            .counterpartyId(counterpartyId)
            .build()
    }

    // Darkstores API----------------------------------------------------------------
    fun fillFindAllByCityRequest(
        cityId: UUID,
        pagingFilter: PagingFilter = PagingFilter(100, null)
    ): FindCityDarkstoreRequest {
        return FindCityDarkstoreRequestBuilder()
            .cityId(cityId)
            .pagingFilter(pagingFilter)
            .build()
    }

    fun fillFindNearestRequest(
        cityId: UUID,
        point: PointView,
        pagingFilter: PagingFilter = PagingFilter(100, null)
    ): FindNearestDarkstoresRequest {
        return FindNearestDarkstoresRequestBuilder()
            .cityId(cityId)
            .pagingFilter(pagingFilter)
            .point(point)
            .build()
    }

    fun fillFindByCityPartRequest(
        cityId: UUID,
        from: PointView,
        to: PointView
    ): FindCityPartDarkstoreRequest {
        return FindCityPartDarkstoreRequestBuilder()
            .cityId(cityId)
            .from(from)
            .to(to)
            .build()
    }

    // Routing API----------------------------------------------------------------

    fun fillFindPrecisionDistanceRequest(
        darkstoreId: UUID,
        point: PointView
    ): FindPrecisionDistanceRequest {
        return FindPrecisionDistanceRequestBuilder()
            .darkstoreId(darkstoreId)
            .point(point)
            .build()
    }

    fun fillFindAddressPointsRequest(
        cityId: UUID,
        query: String
    ): FindAddressPointsRequest {
        return FindAddressPointsRequestBuilder()
            .cityId(cityId)
            .query(query)
            .build()
    }

    // City API------------------------------------------------------------------
    fun fillFetchAvailableCitiesRequest(
        counterpartyId: UUID
    ): FetchAvailableCitiesRequest {
        return FetchAvailableCitiesRequest(counterpartyId)
    }

    // User Preferences API----------------------------------------------------------------
    fun fillUpdateUserPreferredCityRequest(
        userId: UserId,
        cities: List<CityId>
    ): UpdateUserPreferredCityRequest {
        return UpdateUserPreferredCityRequestBuilder()
            .cities(cities)
            .userId(userId)
            .build()
    }

}