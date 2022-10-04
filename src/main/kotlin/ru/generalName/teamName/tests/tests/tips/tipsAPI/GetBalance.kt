package ru.samokat.mysamokat.tests.tests.tips.tipsAPI

import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.samokat.mysamokat.tests.checkers.TipsAssertion
import ru.samokat.mysamokat.tests.dataproviders.Constants
import ru.samokat.mysamokat.tests.dataproviders.preconditions.CommonPreconditions
import ru.samokat.mysamokat.tests.helpers.actions.EmployeeActions
import ru.samokat.mysamokat.tests.helpers.actions.TipsActions
import ru.samokat.tips.api.user.get.UserTipsView
import ru.samokat.tips.api.user.get.chachachay.ChaChaChayTipsRegistrationStatus
import java.util.*

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Tag("tips")
class GetBalance {

    @Autowired
    private lateinit var tipsActions: TipsActions

    private lateinit var tipsAssertion: TipsAssertion

    @Autowired
    private lateinit var employeeActions: EmployeeActions

    @Autowired
    private lateinit var commonPreconditions: CommonPreconditions

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
    @DisplayName("Get tips balance: deliveryman not exists in worker table")
    fun getTipsBalanceDeliverymanNotExistsInWorkerTable() {
        val errMessage = tipsActions.getBalanceWithError(UUID.randomUUID()).message
        tipsAssertion.checkErrorMessage(errMessage, "User tips data was not found")
    }

    @Test
    @Tag("smoke")
    @DisplayName("Get tips balance: status is completed")
    fun getTipsBalanceCompletedStatus() {

        val balances = mutableMapOf<UUID, UserTipsView>()

        Constants.chachachayUsersCompleted.forEach{
            balances[it.key] = tipsActions.getBalance(it.key)
        }

        balances.forEach{
            tipsAssertion.checkUserBalance(it.value, Constants.chachachayUsersCompleted.get(it.key)!!, ChaChaChayTipsRegistrationStatus.COMPLETED)
        }
    }

    @Test
    @Tag("smoke")
    @DisplayName("Get tips balance: status is in_progress")
    fun getTipsBalanceInProgressStatus(){
        val balance = tipsActions.getBalance(Constants.chachachayInProgress)
        tipsAssertion.checkUserBalance(balance, null, ChaChaChayTipsRegistrationStatus.IN_PROGRESS)
    }

    @Test
    @DisplayName("Get tips balance: status is failed_to_sync")
    fun getTipsBalanceFailedToSync(){

        val balance = tipsActions.getBalance(Constants.chachachayFailedToSync)
        tipsAssertion.checkUserBalance(balance, null, ChaChaChayTipsRegistrationStatus.SYNC_FAILED)
    }
}