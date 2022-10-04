package ru.samokat.mysamokat.tests.helpers.controllers.hrp_map

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.samokat.hrp.map.api.RoutingClient
import ru.samokat.hrp.map.api.model.darkstore.RoutingError
import ru.samokat.hrp.map.api.model.routing.AddressPointView
import ru.samokat.hrp.map.api.model.routing.FindAddressPointsRequest
import ru.samokat.hrp.map.api.model.routing.FindPrecisionDistanceRequest
import ru.samokat.hrp.map.api.model.routing.PrecisionDistanceView
import ru.samokat.platform.utils.Result

@Service
class RoutingApiController {

    @Autowired
    lateinit var dsFeign: RoutingClient

    fun findPrecisionDistance(request: FindPrecisionDistanceRequest): Result<RoutingError, PrecisionDistanceView>? {
        return try {
            dsFeign.findPrecisionDistance(request).get()
        } catch (e: InterruptedException) {
            e.printStackTrace()
            null
        }
    }

    fun findAddressPoints(request: FindAddressPointsRequest): Result<RoutingError, List<AddressPointView>>? {
        return try {
            dsFeign.findAddressPoints(request).get()
        } catch (e: InterruptedException) {
            e.printStackTrace()
            null
        }
    }

}