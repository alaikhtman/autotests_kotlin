package ru.samokat.mysamokat.tests.checkers

import io.qameta.allure.Step
import org.assertj.core.api.SoftAssertions
import org.jetbrains.exposed.sql.ResultRow
import org.springframework.stereotype.Service
import ru.samokat.my.domain.PhoneNumber
import ru.samokat.mysamokat.tests.dataproviders.Constants
import ru.samokat.mysamokat.tests.dataproviders.helpers.database.tips.ChachachayWorker
import ru.samokat.mysamokat.tests.dataproviders.tips.TipsData
import ru.samokat.tips.api.user.get.UserTipsView
import ru.samokat.tips.api.user.get.chachachay.ChaChaChayTipsRegistrationStatus
import java.util.*

@Service
class TipsAssertion {
    private val softAssertion = SoftAssertions()
    fun getSoftAssertion(): SoftAssertions {
        return softAssertion
    }

    fun assertAll() {
        getSoftAssertion().assertAll()
    }

    @Step("check user balance")
    fun checkUserBalance(balance: UserTipsView, expectedBalance: Long?, expectedStatus: ChaChaChayTipsRegistrationStatus): TipsAssertion {

        getSoftAssertion().assertThat(balance.providerProperties.chaChaChayProperties?.balance).isEqualTo(expectedBalance)
        getSoftAssertion().assertThat(balance.providerProperties.chaChaChayProperties?.status?.enumValue).isEqualTo(expectedStatus)
        return this
    }

    @Step("Check error message")
    fun checkErrorMessage(actual: String, expected: String): TipsAssertion {
        getSoftAssertion().assertThat(actual).isEqualTo(expected)
        return this
    }

    @Step("Check redirect url")
    fun checkRedirectUrl(url: String, decryptedData: TipsData, orderId: UUID = Constants.orderId): TipsAssertion {

        getSoftAssertion().assertThat(url.substringBefore("?rid")).isEqualTo(Constants.chachachayUrl)
        getSoftAssertion().assertThat(url.substringAfter("?rid=").substringBefore("&cid")).isEqualTo(Constants.restId)
        getSoftAssertion().assertThat(url.substringAfter("&cid=").substringBefore("&td")).isEqualTo(orderId.toString())

        getSoftAssertion().assertThat(decryptedData.clientPhone).isEqualTo(Constants.clientPhone)
        getSoftAssertion().assertThat(decryptedData.tipsList[0].phone).isEqualTo(Constants.deliverymanPhone)
        getSoftAssertion().assertThat(decryptedData.tipsList[0].percent).isEqualTo(100)
        return this
    }

    @Step("Check chachachay worker")
    fun checkChachachayWorker(worker: ResultRow, mobile: PhoneNumber = Constants.mobileChaChaCha1): TipsAssertion{

        getSoftAssertion().assertThat(worker[ChachachayWorker.status])
            .isEqualTo("waiting_for_sync")
        getSoftAssertion().assertThat(worker[ChachachayWorker.mobile])
            .isEqualTo(mobile.asStringWithoutPlus())
        return this
    }

}