package ru.samokat.mysamokat.tests.dataproviders.preconditions

import io.qameta.allure.Step
import org.springframework.stereotype.Service
import ru.samokat.mysamokat.tests.dataproviders.tips.GetRedirectUrlRequestBuilder
import ru.samokat.tips.api.redirecturl.GetRedirectUrlRequest
import java.util.*

@Service
class TipsPreconditions {

    @Step("Fill get redirect url request")
    fun fillGetRedirectUrlRequest(
        orderId: UUID
    ): GetRedirectUrlRequest {
        return GetRedirectUrlRequestBuilder()
            .orderId(orderId)
            .build()
    }
}