package ru.samokat.mysamokat.tests.tests.tips.tipsAPI

import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.samokat.mysamokat.tests.checkers.TipsAssertion
import ru.samokat.mysamokat.tests.dataproviders.Constants
import ru.samokat.mysamokat.tests.dataproviders.preconditions.CommonPreconditions
import ru.samokat.mysamokat.tests.dataproviders.preconditions.TipsPreconditions
import ru.samokat.mysamokat.tests.helpers.actions.EmployeeActions
import ru.samokat.mysamokat.tests.helpers.actions.TipsActions
import java.util.*

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Tag("tips")
class RedirectUrl {

    @Autowired
    private lateinit var tipsActions: TipsActions

    private lateinit var tipsAssertion: TipsAssertion

    @Autowired
    private lateinit var employeeActions: EmployeeActions

    @Autowired
    private lateinit var commonPreconditions: CommonPreconditions

    @Autowired
    private lateinit var tipsPreconditions: TipsPreconditions

    @BeforeEach
    fun before() {
        tipsAssertion = TipsAssertion()
        employeeActions.deleteProfile(Constants.mobile1)
        employeeActions.deleteProfile(Constants.mobile8)
    }

    @AfterEach
    fun release() {
        tipsAssertion.assertAll()
        employeeActions.deleteProfile(Constants.mobile1)
        employeeActions.deleteProfile(Constants.mobile8)
    }

    @Test
    @Tag("smoke")
    @DisplayName("Get tips url")
    fun getTipsURL() {

        val request = tipsPreconditions.fillGetRedirectUrlRequest(Constants.orderId)
        val url = tipsActions.getRedirectUrl(request).redirectUrl

        val dercyptedData = tipsActions.decryptData(url.substringAfter("td="))!!

        tipsAssertion.checkRedirectUrl(url, dercyptedData)
    }

    @Test
    @Tag("smoke")
    @DisplayName("Get tips url: order not exists")
    fun getTipsURLOrderNotExists() {
        val request = tipsPreconditions.fillGetRedirectUrlRequest(UUID.randomUUID())
        val errMsg = tipsActions.getRedirectUrlWithError(request)!!
        tipsAssertion.checkErrorMessage(errMsg, "Failed to create redirect url")
    }

}