package ru.samokat.mysamokat.tests.helpers.controllers.hrp_map

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.samokat.hrp.map.api.CounterpartyPreferenceClient
import ru.samokat.hrp.map.api.DarkstoreClient
import ru.samokat.hrp.map.api.model.PagedView
import ru.samokat.hrp.map.api.model.counterparty.preferences.CounterpartyPreferencesError
import ru.samokat.hrp.map.api.model.counterparty.preferences.UpdateCounterpartiesAuthorizedCityRequest
import ru.samokat.hrp.map.api.model.darkstore.DarkstoreError
import ru.samokat.hrp.map.api.model.darkstore.DarkstoreView
import ru.samokat.hrp.map.api.model.darkstore.city.FindCityDarkstoreRequest
import ru.samokat.hrp.map.api.model.darkstore.city.FindCityPartDarkstoreRequest
import ru.samokat.hrp.map.api.model.darkstore.nearest.DistancedDarkstoreView
import ru.samokat.hrp.map.api.model.darkstore.nearest.FindNearestDarkstoresRequest
import ru.samokat.platform.utils.Result
import java.util.*

@Service
class HrpDarkstoresApiController {

    @Autowired
    lateinit var dsFeign: DarkstoreClient

    fun findById(darkstoreId: UUID): Result<DarkstoreError, DarkstoreView>? {
        return try {
            dsFeign.findById(darkstoreId).get()
        } catch (e: InterruptedException) {
            e.printStackTrace()
            null
        }
    }

    fun findAllByCity(request: FindCityDarkstoreRequest): Result<DarkstoreError, PagedView<DarkstoreView>>? {
        return try {
            dsFeign.findAllByCity(request).get()
        } catch (e: InterruptedException) {
            e.printStackTrace()
            null
        }
    }

    fun findAllNearest(request: FindNearestDarkstoresRequest): Result<DarkstoreError, PagedView<DistancedDarkstoreView>>? {
        return try {
            dsFeign.findAllNearest(request).get()
        } catch (e: InterruptedException) {
            e.printStackTrace()
            null
        }
    }

    fun findAllByCityPart(request: FindCityPartDarkstoreRequest): Result<DarkstoreError, List<DarkstoreView>>? {
        return try {
            dsFeign.findAllByCityPart(request).get()
        } catch (e: InterruptedException) {
            e.printStackTrace()
            null
        }
    }
}