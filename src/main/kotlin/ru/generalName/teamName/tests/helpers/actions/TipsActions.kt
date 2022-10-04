package ru.samokat.mysamokat.tests.helpers.actions

import io.qameta.allure.Step
import org.jetbrains.exposed.sql.ResultRow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import ru.samokat.my.domain.PhoneNumber
import ru.samokat.my.rest.client.RestResult
import ru.samokat.mysamokat.tests.SuiteBase
import ru.samokat.mysamokat.tests.dataproviders.Constants
import ru.samokat.mysamokat.tests.dataproviders.tips.TipsData
import ru.samokat.mysamokat.tests.helpers.controllers.asClientError
import ru.samokat.mysamokat.tests.helpers.controllers.asSuccess
import ru.samokat.mysamokat.tests.helpers.controllers.database.TipsDatabaseController
import ru.samokat.mysamokat.tests.helpers.controllers.tips.TipsController
import ru.samokat.tips.api.redirecturl.GetRedirectUrlRequest
import ru.samokat.tips.api.redirecturl.RedirectUrlView
import ru.samokat.tips.api.user.get.GetUserTipsError
import ru.samokat.tips.api.user.get.UserTipsView
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import javax.xml.bind.DatatypeConverter

@Component
@Scope("prototype")
class TipsActions {

    @Autowired
    lateinit var tipsController: TipsController

    @Autowired
    private lateinit var tipsDatabaseController: TipsDatabaseController



    // tips balance
    @Step("get tips balance")
    fun getBalance(profileId: UUID): UserTipsView {
        return tipsController.getTipsBalance(profileId)!!.asSuccess()
    }

    @Step("get tips balance")
    fun getBalanceWithError(profileId: UUID): GetUserTipsError {
        return tipsController.getTipsBalance(profileId)!!.asClientError()
    }

    // redirect url
    @Step("get redirect url")
    fun getRedirectUrl(request: GetRedirectUrlRequest): RedirectUrlView {
        return tipsController.getRedirectUrl(request)!!.asSuccess()
    }

    @Step("get redirect url with error")
    fun getRedirectUrlWithError(request: GetRedirectUrlRequest): String? {
        when (val response = tipsController.getRedirectUrl(request)) {
            is RestResult.ServerError -> {
                return response.error.message
            }
            is RestResult.Success, is RestResult.ClientError ->
            {
                throw Exception("Server error was expected")
            }
        }
        return null
    }

    @Step("decrypt data")
    fun decryptData(
        data: String, // чистый td
        token: String = Constants.chachachayToken
    ): TipsData? {

        val cryptedData = Base64.getDecoder().decode(URLDecoder.decode(data, StandardCharsets.UTF_8).toByteArray())
        val cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
        val initializationVector = IvParameterSpec(
            DatatypeConverter.printHexBinary(token.md5Hash.copyOfRange(0, cipher.blockSize / 2)).toLowerCase().toByteArray()
        )
        val secretKey = SecretKeySpec(token.toByteArray(), 0, cipher.blockSize, "AES")
        cipher.init(Cipher.DECRYPT_MODE, secretKey, initializationVector)

        val decryptedData = String(cipher.doFinal(cryptedData))

        val resultString = Regex("""^\w{32}""").replace(decryptedData, "")

        return SuiteBase.jacksonObjectMapper.readValue(resultString, TipsData::class.java)
    }

    private val String.md5Hash: ByteArray
        get() {
            val digest = MessageDigest.getInstance("MD5")
            digest.update(this.toByteArray())
            return digest.digest()
        }

    // database

    @Step("get chachachay worker from database")
    fun getChachachayWorkerFromDB(profileId: UUID): ResultRow {
        return tipsDatabaseController.getChachachayWorkerByProfileId(profileId)
    }

    @Step("get chachachay worker existance from database")
    fun getChachachayWorkerExistanceFromDB(profileId: UUID): Boolean {
        return tipsDatabaseController.checkChachachayWorkerExistsByProfileId(profileId)
    }

    @Step("get chachachay worker existance from database")
    fun getChachachayWorkerExistanceFromDBByMobile(mobile: PhoneNumber): Boolean {
        return tipsDatabaseController.checkChachachayWorkerExistsByMobile(mobile.asStringWithoutPlus())
    }

    @Step("get chachachay tips balance from database")
    fun getChachachayTipsBalanceFromDB(profileId: UUID): ResultRow {
        return tipsDatabaseController.getChachachayTipsBalanceByProfileId(profileId)
    }

    @Step("clear chachachay_worker table")
    fun deleteChachachayWorker(mobile: PhoneNumber){
        tipsDatabaseController.deleteProfileFromTipsByMobile(mobile.asStringWithoutPlus())
    }
}