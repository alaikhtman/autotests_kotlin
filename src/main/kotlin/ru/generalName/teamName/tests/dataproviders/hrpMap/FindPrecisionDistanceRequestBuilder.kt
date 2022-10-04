package ru.samokat.mysamokat.tests.dataproviders.hrpMap

import ru.samokat.hrp.map.api.model.DarkstoreId
import ru.samokat.hrp.map.api.model.PointView
import ru.samokat.hrp.map.api.model.routing.FindPrecisionDistanceRequest

class FindPrecisionDistanceRequestBuilder {

    private lateinit var darkstoreId: DarkstoreId
    fun darkstoreId(darkstoreId: DarkstoreId) = apply { this.darkstoreId = darkstoreId }
    fun getDarkstoreId(): DarkstoreId { return this.darkstoreId }

    private lateinit var point: PointView
    fun point(point: PointView) = apply { this.point = point }
    fun getPoint(): PointView { return this.point }

    fun build(): FindPrecisionDistanceRequest {
        return FindPrecisionDistanceRequest(
            darkstoreId = darkstoreId,
            point = point
        )
    }
}