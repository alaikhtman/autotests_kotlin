package ru.samokat.mysamokat.tests.helpers.actions

import io.qameta.allure.Step
import org.jetbrains.exposed.sql.ResultRow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import ru.samokat.my.domain.PhoneNumber
import ru.samokat.my.domain.shifts.TimeRange
import ru.samokat.mysamokat.apigateway.api.common.domain.OAuthTokenView
import ru.samokat.mysamokat.apigateway.api.common.domain.UserView
import ru.samokat.mysamokat.apigateway.api.oauth.otp.SendOtpRequest
import ru.samokat.mysamokat.apigateway.api.oauth.otp.SendOtpResponseView

import ru.samokat.mysamokat.apigateway.api.oauth.token.GetOAuthTokenRequest
import ru.samokat.mysamokat.apigateway.api.pushtokens.register.RegisterPushTokenRequest
import ru.samokat.mysamokat.apigateway.api.reviews.ReviewRequestsView
import ru.samokat.mysamokat.apigateway.api.shifts.getassignment.ShiftAssignmentView
import ru.samokat.mysamokat.apigateway.api.shifts.getlist.ShiftListView
import ru.samokat.mysamokat.apigateway.api.shifts.getworkedoutshift.WorkedOutShiftView
import ru.samokat.mysamokat.apigateway.api.shifts.reviews.SubmitShiftReviewRequest
import ru.samokat.mysamokat.apigateway.api.shifts.storeschedule.StoreScheduleRequest
import ru.samokat.mysamokat.apigateway.api.user.getcontacts.UserContactsView
import ru.samokat.mysamokat.apigateway.api.user.getexpectations.schedule.ScheduleExpectationsView
import ru.samokat.mysamokat.apigateway.api.user.getexpectations.schedule.submit.SubmitScheduleExpectationsRequest
import ru.samokat.mysamokat.apigateway.api.user.getstatistics.actual.UserActualStatisticsView
import ru.samokat.mysamokat.apigateway.api.user.getstatistics.aggregated.AggregatedUserStatisticsListView
import ru.samokat.mysamokat.apigateway.api.user.getstatistics.shifts.getlist.ShiftsStatisticsListView
import ru.samokat.mysamokat.apigateway.api.user.gettips.UserTipsView
import ru.samokat.mysamokat.tests.SuiteBase
import ru.samokat.mysamokat.tests.dataproviders.ErrorView

import ru.samokat.mysamokat.tests.dataproviders.apiModels.staffcave.oauth.RefreshAccessTokenRequest

import ru.samokat.mysamokat.tests.dataproviders.MSMKTConfigView

import ru.samokat.mysamokat.tests.helpers.controllers.KafkaController
import ru.samokat.mysamokat.tests.helpers.controllers.SmsStubController
import ru.samokat.mysamokat.tests.helpers.controllers.database.ApigatewayDatabaseController
import ru.samokat.mysamokat.tests.helpers.controllers.database.PushDatabaseController
import ru.samokat.mysamokat.tests.helpers.controllers.database.StatisticsDatabaseController
import ru.samokat.mysamokat.tests.helpers.controllers.events.employee.FirstLoginEvent
import ru.samokat.mysamokat.tests.helpers.controllers.msmkt_apigw.*
import java.time.ZoneId
import java.util.*

@Component
@Scope("prototype")
class MySamokatApiGWActions(private val kafkaFirstLoginConsume: KafkaController) {

    @Autowired
    private lateinit var authController: AuthController

    @Autowired
    private lateinit var stubSmsController: SmsStubController

    @Autowired
    private lateinit var userApiController: UserApiController

    @Autowired
    private lateinit var shiftsApiController: ShiftsApiController

    @Autowired
    private lateinit var pushTokenApiController: PushTokensController

    @Autowired
    private lateinit var configController: ConfigController

    @Autowired
    private lateinit var pushDatabaseController: PushDatabaseController

    @Autowired
    private lateinit var statDatabaseController: StatisticsDatabaseController

    @Autowired
    private lateinit var apigatewayDatabaseController: ApigatewayDatabaseController

    @Step("Authenticate profile with password")
    fun authProfilePassword(request: GetOAuthTokenRequest): OAuthTokenView? {
        return authController.authenticateProfile(request)
    }

    @Step("Authenticate profile with otp")
    fun authProfileOtp(request: SendOtpRequest): SendOtpResponseView {
        return authController.postOtp(request)
    }

    @Step("Authenticate profile with password with error")
    fun authProfilePasswordError(request: GetOAuthTokenRequest, expectedSC: Int): ErrorView? {
        return authController.authenticateProfileWithError(request, expectedSC)
    }

    @Step("Get Otp Code")
    fun getOtp(mobile: PhoneNumber): String {
        return stubSmsController.getOtp(mobile)
    }

    @Step("Refresh token")
    fun refreshToken(request: RefreshAccessTokenRequest): OAuthTokenView? {
        return authController.refreshToken(request)
    }

    @Step("Delete token")
    fun deleteToken(accessToken: String, sc: Int) {
        authController.deleteToken(accessToken, sc)
    }

    @Step("Register push token")
    fun registerPushToken(accessToken: String, body: RegisterPushTokenRequest) {
        pushTokenApiController.registerPushToken(accessToken, body)
    }

    @Step("delete push token")
    fun deletePushToken(accessToken: String, pushToken: String) {
        pushTokenApiController.deletePushToken(accessToken, pushToken)
    }

    @Step("Get profile data")
    fun getProfileData(accessToken: String): UserView? {
        return userApiController.getUserData(accessToken)
    }

    @Step("Get profile data error")
    fun getProfileDataError(accessToken: String?, sc: Int): ErrorView? {
        return userApiController.getUserDataWithError(accessToken, sc)
    }

    @Step("Store schedule")
    fun storeSchedule(accessToken: String, request: StoreScheduleRequest, range: TimeRange) {

        shiftsApiController.putShiftsSchedule(
            accessToken,
            request,
            range.startingAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli().toString(),
            range.endingAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli().toString()
        )
    }

    @Step("Store schedule")
    fun storeScheduleError(accessToken: String, request: StoreScheduleRequest, range: TimeRange, sc: Int): ErrorView? {
        return shiftsApiController.putShiftsScheduleError(
            accessToken,
            request,
            range.startingAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli().toString(),
            range.endingAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli().toString(), sc
        )
    }

    @Step("Get all shifts")
    fun getAllShifts(accessToken: String, range: TimeRange): ShiftListView? {
        return shiftsApiController.getShifts(
            accessToken, range.startingAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli().toString(),
            range.endingAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli().toString()
        )
    }

    @Step("Put expectations")
    fun putExpectations(accessToken: String, request: SubmitScheduleExpectationsRequest) {
        userApiController.putExpectations(accessToken, request)
    }

    @Step("Put expectations error")
    fun putExpectationsError(accessToken: String, request: SubmitScheduleExpectationsRequest, sc: Int): ErrorView? {
        return userApiController.putExpectationsError(accessToken, request, sc)
    }

    @Step("Get expectations")
    fun getExpectations(accessToken: String): ScheduleExpectationsView? {
        return userApiController.getExpectations(accessToken)
    }

    @Step("Get expectations error")
    fun getExpectationsError(accessToken: String, sc: Int) {
        return userApiController.getExpectationsError(accessToken, sc)
    }

    @Step("Get contacts")
    fun getContacts(accessToken: String): UserContactsView {
        return userApiController.getContacts(accessToken)
    }

    @Step("Get contacts")
    fun getContactsError(accessToken: String, sc: Int) {
        return userApiController.getContactsError(accessToken, sc)
    }

    @Step("Get worked out shift by id")
    fun getWorkedOutShiftById(accessToken: String, shiftId: UUID): WorkedOutShiftView {
        return shiftsApiController.getWorkedOutShift(accessToken, shiftId)
    }

    @Step("Get worked out shift by id")
    fun getWorkedOutShiftByIdError(accessToken: String, shiftId: UUID, sc: Int) {
        shiftsApiController.getWorkedOutShiftError(accessToken, shiftId, sc)
    }

    @Step("Get worked out shift by id")
    fun getAssignmentById(accessToken: String, shiftId: UUID): ShiftAssignmentView {
        return shiftsApiController.getAssignmentById(accessToken, shiftId)
    }

    @Step("Get worked out shift by id")
    fun getAssignmentByIdError(accessToken: String, shiftId: UUID, sc: Int) {
        shiftsApiController.getAssignmentByIdError(accessToken, shiftId, sc)
    }

    @Step("Get review pending")
    fun getReviewPending(accessToken: String): ReviewRequestsView {
        return shiftsApiController.getReviewPending(accessToken)
    }

    @Step("Delete review pending")
    fun deleteReviewPending(accessToken: String, shiftId: UUID) {
        shiftsApiController.deleteReviewPending(accessToken, shiftId)
    }

    @Step("Submit Review")
    fun submitReview(accessToken: String, shiftId: UUID, requestBody: SubmitShiftReviewRequest) {
        shiftsApiController.submitReview(accessToken, shiftId, requestBody)
    }

    // Statistics

    @Step("Get actual statistics")
    fun getActualStatistics(accessToken: String): UserActualStatisticsView? {
        return userApiController.getActualStatistics(accessToken)
    }

    @Step("Get aggregated statistics")
    fun getAggregatedStatistics(accessToken: String, type: String): AggregatedUserStatisticsListView? {
        return userApiController.getAggregatedStatistics(accessToken, type)
    }

    @Step("Get aggregated statistics")
    fun getShiftsStatistics(accessToken: String, range: TimeRange): ShiftsStatisticsListView? {
        return userApiController.getShiftsStatistics(
            accessToken, range.startingAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli().toString(),
            range.endingAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli().toString()
        )
    }

    @Step("Get aggregated statistics with error")
    fun getShiftsStatisticsError(accessToken: String, range: TimeRange, sc: Int): ErrorView {
        return userApiController.getShiftsStatisticsError(
            accessToken,
            range.endingAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli().toString(),
            range.startingAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli().toString(),
            sc
        )
    }

    @Step("Get aggregated statistics")
    fun getShiftsStatisticsPaging(
        accessToken: String,
        range: TimeRange,
        pageSize: String,
        pageMark: String
    ): ShiftsStatisticsListView? {
        return userApiController.getShiftsStatisticsPaging(
            accessToken, range.startingAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli().toString(),
            range.endingAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli().toString(), pageSize, pageMark
        )
    }

    @Step("Get aggregated statistics")
    fun getShiftsStatisticsPageSize(
        accessToken: String,
        range: TimeRange,
        pageSize: String
    ): ShiftsStatisticsListView? {
        return userApiController.getShiftsStatisticsPageSize(
            accessToken, range.startingAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli().toString(),
            range.endingAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli().toString(), pageSize
        )
    }

    // Tips
    @Step("Get tips data")
    fun getTipsData(accessToken: String): UserTipsView? {
        return userApiController.getTipsData(accessToken)
    }

    // kafka

    @Step("Get first login event from kafka")
    fun getFirstLoginEventFromKafka(profileId: UUID): FirstLoginEvent? {
        val answer = kafkaFirstLoginConsume.consume(profileId.toString())!!.value()
        val result = SuiteBase.jacksonObjectMapper.convertValue(answer, FirstLoginEvent::class.java)
        return result
    }

    // Push
    @Step("Get push token from DB")
    fun getPushTokenFromDB(token: UUID): ResultRow {
        return pushDatabaseController.getToken(token)
    }

    @Step("Check token exists in database")
    fun checkTokenExists(token: UUID): Boolean {
        return pushDatabaseController.checkTokenExists(token)
    }

    @Step("Update deliveryd order count in db")
    fun updateDeliveredOrdersCount(userId: UUID, orders: Int) {
        statDatabaseController.updateMonthlyStatOrdersCount(userId, orders)
        statDatabaseController.updateWeeklyStatOrdersCount(userId, orders)
    }

    @Step("Update shift orders count")
    fun updateShiftOrdersCount(shiftId: UUID, orders: Int) {
        statDatabaseController.updateShiftsOrdersCount(shiftId, orders)
    }

    @Step("get config")
    fun getConfig(type: String): MSMKTConfigView? {
        return configController.getConfig(type)
    }

    @Step("get config with error")
    fun getConfigWithError(type: String, expectedSC: Int) {
        return configController.getConfigWithError(type, expectedSC)
    }

    @Step("Get push task from db")
    fun getPushTaskFromDBByassignmentIdAndType(assignmentId: UUID, type: String): ResultRow {
        return apigatewayDatabaseController.getTaskByAssignmentIdAndType(assignmentId, type)

    }

    @Step("delete task by assignmentId")
    fun deleteTaskByAssignmentId(assignmentId: UUID) {
        apigatewayDatabaseController.deleteAssignmentTaskById(assignmentId.toString())
    }
}