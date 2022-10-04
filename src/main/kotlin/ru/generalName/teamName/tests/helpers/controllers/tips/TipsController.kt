package ru.samokat.mysamokat.tests.helpers.controllers.tips

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.samokat.my.rest.api.error.GeneralError
import ru.samokat.my.rest.client.RestResult
import ru.samokat.tips.api.redirecturl.GetRedirectUrlRequest
import ru.samokat.tips.api.redirecturl.RedirectUrlView
import ru.samokat.tips.api.user.get.GetUserTipsError
import ru.samokat.tips.api.user.get.UserTipsView
import ru.samokat.tips.client.TipsClient
import java.util.*

@Service
class TipsController {

    @Autowired
    lateinit var tipsFeign: TipsClient

    fun getTipsBalance(profileId: UUID): RestResult<UserTipsView, GetUserTipsError>? {
        return try {
            tipsFeign.getUserTips(profileId).get()
        } catch (e: InterruptedException) {
            e.printStackTrace()
            null
        }
    }

    fun getRedirectUrl(request: GetRedirectUrlRequest): RestResult<RedirectUrlView, GeneralError>? {
        return try {
            tipsFeign.getRedirectUrl(request).get()
        } catch (e: InterruptedException) {
            e.printStackTrace()
            null
        }
    }
}