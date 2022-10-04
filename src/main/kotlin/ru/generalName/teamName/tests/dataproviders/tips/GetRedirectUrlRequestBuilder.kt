package ru.samokat.mysamokat.tests.dataproviders.tips

import ru.samokat.tips.api.redirecturl.GetRedirectUrlRequest
import java.util.*

class GetRedirectUrlRequestBuilder {

    private lateinit var orderId: UUID
    fun orderId(orderId: UUID) = apply { this.orderId = orderId }
    fun getOrderId(): UUID {
        return orderId
    }

    fun build(): GetRedirectUrlRequest {
        return GetRedirectUrlRequest(
            orderId = orderId
        )

    }
}