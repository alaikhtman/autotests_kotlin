package ru.samokat.mysamokat.tests.checkers

import io.qameta.allure.Step
import org.assertj.core.api.SoftAssertions
import org.jetbrains.exposed.sql.ResultRow
import org.springframework.stereotype.Service
import ru.samokat.employeeprofiles.api.contacts.EmployeeProfileContactView
import ru.samokat.employeeprofiles.api.profiles.create.CreateProfileRequest
import ru.samokat.my.domain.PhoneNumber
import ru.samokat.my.domain.employee.EmployeeName
import ru.samokat.my.domain.employee.EmployeeRole
import ru.samokat.my.domain.shifts.ShiftAssignmentCancellationReason
import ru.samokat.my.domain.shifts.TimeRange
import ru.samokat.my.rest.api.enum.ApiEnum
import ru.samokat.mysamokat.apigateway.api.common.domain.ContactView

import ru.samokat.mysamokat.apigateway.api.common.domain.OAuthTokenView
import ru.samokat.mysamokat.apigateway.api.common.domain.UserView
import ru.samokat.mysamokat.apigateway.api.reviews.ReviewRequestsView
import ru.samokat.mysamokat.apigateway.api.shifts.AssignmentStatus
import ru.samokat.mysamokat.apigateway.api.shifts.getassignment.ShiftAssignmentView
import ru.samokat.mysamokat.apigateway.api.shifts.getlist.ShiftListView
import ru.samokat.mysamokat.apigateway.api.shifts.getworkedoutshift.WorkedOutShiftBriefView
import ru.samokat.mysamokat.apigateway.api.shifts.getworkedoutshift.WorkedOutShiftView
import ru.samokat.mysamokat.apigateway.api.shifts.reviews.SubmitShiftReviewRequest
import ru.samokat.mysamokat.apigateway.api.shifts.storeschedule.StoreScheduleRequest
import ru.samokat.mysamokat.apigateway.api.user.getcontacts.UserContactsView
import ru.samokat.mysamokat.apigateway.api.user.getexpectations.schedule.ScheduleExpectationsView
import ru.samokat.mysamokat.apigateway.api.user.getexpectations.schedule.submit.SubmitScheduleExpectationsRequest
import ru.samokat.mysamokat.apigateway.api.user.getstatistics.actual.UserActualStatisticsView
import ru.samokat.mysamokat.apigateway.api.user.getstatistics.aggregated.AggregatedUserStatisticsListView
import ru.samokat.mysamokat.apigateway.api.user.getstatistics.shifts.WorkedOutShiftType
import ru.samokat.mysamokat.apigateway.api.user.getstatistics.shifts.getlist.ShiftsStatisticsListView
import ru.samokat.mysamokat.apigateway.api.user.getstatistics.shifts.getlist.WorkedOutShiftStatistics
import ru.samokat.mysamokat.apigateway.api.user.gettips.UserTipsRegistrationStatus
import ru.samokat.mysamokat.apigateway.api.user.gettips.UserTipsView
import ru.samokat.mysamokat.tests.SuiteBase
import ru.samokat.mysamokat.tests.configuration.KafkaConfiguration
import ru.samokat.mysamokat.tests.dataproviders.Constants
import ru.samokat.mysamokat.tests.dataproviders.MSMKTConfigView
import ru.samokat.mysamokat.tests.dataproviders.helpers.database.apigateway.Task
import ru.samokat.mysamokat.tests.dataproviders.helpers.database.push.Tokens
import ru.samokat.mysamokat.tests.helpers.controllers.KafkaController
import ru.samokat.mysamokat.tests.helpers.controllers.events.employee.EmployeeProfileLog
import ru.samokat.mysamokat.tests.helpers.controllers.events.employee.FirstLoginEvent
import ru.samokat.shifts.api.activeshifts.ActiveShiftView
import ru.samokat.shifts.api.common.domain.DeliveryMethod
import ru.samokat.shifts.api.schedules.ShiftScheduleView
import ru.samokat.shifts.api.workedout.ShiftStopType


import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.*
import kotlin.collections.ArrayList


@Service
class MySamokatApiGWAssertions {

    private val softAssertion = SoftAssertions()
    fun getSoftAssertion(): SoftAssertions {
        return softAssertion
    }

    fun assertAll() {
        getSoftAssertion().assertAll()
    }

    // authorization
    @Step("check oauth tokens exists")
    fun checkOauthTokensExists(tokens: OAuthTokenView) {
        getSoftAssertion().assertThat(tokens.accessToken).isNotNull
        getSoftAssertion().assertThat(tokens.refreshToken).isNotNull
    }

    // me
    @Step("check user profile")
    fun checkUserProfileWithName(
        profileData: UserView,
        profileId: UUID,
        mobile: PhoneNumber = Constants.mobile1,
        name: EmployeeName,
        roles: List<ApiEnum<EmployeeRole, String>> = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
        darkstoreId: UUID = Constants.darkstoreId,
        firstLoginAt: Instant? = null
    ) {
        getSoftAssertion().assertThat(profileData.userId).isEqualTo(profileId)
        getSoftAssertion().assertThat(profileData.mobile).isEqualTo(mobile)
        getSoftAssertion().assertThat(profileData.name).isEqualTo(name)
        getSoftAssertion().assertThat(profileData.roles).isEqualTo(roles)
        getSoftAssertion().assertThat(profileData.darkstore!!.darkstoreId).isEqualTo(darkstoreId)
        getSoftAssertion().assertThat(profileData.firstLoginAt).isEqualTo(firstLoginAt)
    }

    @Step("check user profile: empty first login")
    fun checkUserProfileFirstLoginIsNotEmpty(
        profileData: UserView
    ) {
        getSoftAssertion().assertThat(profileData.firstLoginAt).isNotNull
    }



    @Step("check user profile")
    fun checkUserProfile(
        profileData: UserView,
        profileId: UUID,
        mobile: PhoneNumber = Constants.mobile1,
        roles: List<ApiEnum<EmployeeRole, String>> = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
        darkstoreId: UUID = Constants.darkstoreId,
        firstLoginAt: Instant? = null
    ): MySamokatApiGWAssertions {
        getSoftAssertion().assertThat(profileData.userId).isEqualTo(profileId)
        getSoftAssertion().assertThat(profileData.mobile).isEqualTo(mobile)
        getSoftAssertion().assertThat(profileData.roles).isEqualTo(roles)
        getSoftAssertion().assertThat(profileData.darkstore!!.darkstoreId).isEqualTo(darkstoreId)
        getSoftAssertion().assertThat(profileData.firstLoginAt).isEqualTo(firstLoginAt)

        return this
    }

    @Step("Check schedule count")
    fun checkScheduleListCount(shifts: ShiftListView?, expectedCount: Int): MySamokatApiGWAssertions {
        getSoftAssertion().assertThat(shifts!!.shifts.schedule?.count()).isEqualTo(expectedCount)
        return this
    }

    @Step("Check assignments count")
    fun checkScheduleListNull(shifts: ShiftListView?): MySamokatApiGWAssertions {
        getSoftAssertion().assertThat(shifts!!.shifts.schedule).isNull()
        return this
    }

    @Step("Check assignments count")
    fun checkAssignmentsListCount(shifts: ShiftListView?, expectedCount: Int): MySamokatApiGWAssertions {
        getSoftAssertion().assertThat(shifts!!.shifts.assignments?.count()).isEqualTo(expectedCount)
        return this
    }

    @Step("Check assignments count")
    fun checkAssignmentsListNull(shifts: ShiftListView?): MySamokatApiGWAssertions {
        getSoftAssertion().assertThat(shifts!!.shifts.assignments).isNull()
        return this
    }

    @Step("Check assignments count")
    fun checkWorkedOutListCount(shifts: ShiftListView?, expectedCount: Int): MySamokatApiGWAssertions {
        getSoftAssertion().assertThat(shifts!!.shifts.workedOutShifts?.count()).isEqualTo(expectedCount)
        return this
    }

    @Step("Check assignments count")
    fun checkWorkedOutListNull(shifts: ShiftListView?): MySamokatApiGWAssertions {
        getSoftAssertion().assertThat(shifts!!.shifts.workedOutShifts).isNull()
        return this
    }

    @Step("Check schedule not exists")
    fun checkScheduleNotExists(shifts: ShiftListView): MySamokatApiGWAssertions {
        getSoftAssertion().assertThat(shifts!!.shifts.schedule).isNull()
        return this
    }

    @Step("Check schedule info")
    fun checkScheduleInfo(shifts: ShiftListView?, expected: StoreScheduleRequest): MySamokatApiGWAssertions {
        expected.schedules.forEach {
            getSoftAssertion().assertThat(
                shifts!!.shifts.schedule!!.contains(
                    ShiftListView.Schedule(it.startingAt, it.endingAt)
                )
            ).isTrue
        }
        return this
    }

    @Step("Check assignments info")
    fun checkAssignmentInfo(
        assignment: ShiftListView.ShiftAssignmentBriefView,
        timerange: TimeRange,
        status: AssignmentStatus,
        role: EmployeeRole = EmployeeRole.DELIVERYMAN,
        darkstoreId: UUID = Constants.darkstoreId,
        cancellationReason: ShiftAssignmentCancellationReason? = null
    ): MySamokatApiGWAssertions {

        getSoftAssertion().assertThat(assignment.role).isEqualTo(ApiEnum(role))
        getSoftAssertion().assertThat(assignment.startingAt).isEqualTo(timerange.startingAt)
        getSoftAssertion().assertThat(assignment.endingAt).isEqualTo(timerange.endingAt)
        getSoftAssertion().assertThat(assignment.status).isEqualTo(ApiEnum(status))
        getSoftAssertion().assertThat(assignment.darkstore.darkstoreId).isEqualTo(darkstoreId)

        if (status == AssignmentStatus.CANCELED)
            getSoftAssertion().assertThat(assignment.cancellationReason!!.enumValue).isEqualTo(cancellationReason)

        return this
    }

    @Step("Check assignments info")
    fun checkAssignmentInfo(
        assignment: ShiftAssignmentView,
        timerange: TimeRange,
        status: AssignmentStatus,
        role: EmployeeRole = EmployeeRole.DELIVERYMAN,
        darkstoreId: UUID = Constants.darkstoreId,
        cancellationReason: ShiftAssignmentCancellationReason? = null
    ): MySamokatApiGWAssertions {

        getSoftAssertion().assertThat(assignment.role).isEqualTo(ApiEnum(role))
        getSoftAssertion().assertThat(assignment.startingAt).isEqualTo(timerange.startingAt)
        getSoftAssertion().assertThat(assignment.endingAt).isEqualTo(timerange.endingAt)
        getSoftAssertion().assertThat(assignment.status).isEqualTo(ApiEnum(status))
        getSoftAssertion().assertThat(assignment.darkstore.darkstoreId).isEqualTo(darkstoreId)

        if (status == AssignmentStatus.CANCELED)
            getSoftAssertion().assertThat(assignment.cancellationReason!!.enumValue).isEqualTo(cancellationReason)

        return this
    }

    @Step("Check assignments replacements")
    fun checkAssignmentsReplacementsRoles(assignment: ShiftAssignmentView): MySamokatApiGWAssertions {
        assignment.replacements.forEach {
            getSoftAssertion().assertThat(
                it.contact.roles.contains(ApiEnum(EmployeeRole.PICKER)) or it.contact.roles.contains(
                    ApiEnum(EmployeeRole.DELIVERYMAN)
                )
            ).isTrue
        }
        return this
    }

    fun checkProfileInReplacementsList(assignment: ShiftAssignmentView, profileId: UUID): MySamokatApiGWAssertions{
        getSoftAssertion().assertThat(assignment.replacements.filter { it.contact.userId == profileId }.first()).isNotNull
        return this
    }

    fun checkProfileNotInReplacementsList(assignment: ShiftAssignmentView, profileId: UUID): MySamokatApiGWAssertions{
        getSoftAssertion().assertThat(assignment.replacements.filter { it.contact.userId == profileId }).isEmpty()
        return this
    }

    @Step("Check workedOut shift info")
    fun checkWorkedOutShiftInfo(
        workedOutShift: WorkedOutShiftBriefView,
        role: EmployeeRole = EmployeeRole.DELIVERYMAN,
        darkstoreId: UUID = Constants.darkstoreId
    ): MySamokatApiGWAssertions {

        getSoftAssertion().assertThat(workedOutShift.role).isEqualTo(ApiEnum(role))
        getSoftAssertion().assertThat(workedOutShift.darkstore.darkstoreId).isEqualTo(darkstoreId)

        return this
    }


    @Step("Check workedOut shift info")
    fun checkWorkedOutShiftInfoById(
        workedOutShift: ru.samokat.mysamokat.apigateway.api.shifts.getworkedoutshift.WorkedOutShiftView,
        role: EmployeeRole = EmployeeRole.DELIVERYMAN,
        darkstoreId: UUID = Constants.darkstoreId,
        deliveredOrdersCount: Int = 0
    ): MySamokatApiGWAssertions {

        getSoftAssertion().assertThat(workedOutShift.role).isEqualTo(ApiEnum(role))
        getSoftAssertion().assertThat(workedOutShift.darkstore.darkstoreId).isEqualTo(darkstoreId)

        if (role != EmployeeRole.DARKSTORE_ADMIN)
            getSoftAssertion().assertThat(workedOutShift.statistics!!.deliveredOrdersCount).isEqualTo(deliveredOrdersCount)

        return this
    }

    @Step("Check expectations")
    fun checkExpectations(
        userExpectations: ScheduleExpectationsView?,
        storeExpectationsRequest: SubmitScheduleExpectationsRequest
    ): MySamokatApiGWAssertions {
        getSoftAssertion().assertThat(userExpectations!!.weeklyWorkingDaysCount)
            .isEqualTo(storeExpectationsRequest.weeklyWorkingDaysCount)
        getSoftAssertion().assertThat(userExpectations.workingDayDuration)
            .isEqualTo(storeExpectationsRequest.workingDayDuration)
        return this
    }

    @Step("assert darkstore contacts list count")
    fun checkDarkstoreContactsListCount(
        contacts: UserContactsView,
        expectedCount: Int
    ): MySamokatApiGWAssertions {
        getSoftAssertion().assertThat(contacts.contacts.count()).isEqualTo(expectedCount)
        return this
    }

    @Step("assert darkstore contacts element in list")
    fun checkDarkstoreContactInList(
        contacts: UserContactsView,
        profileId: UUID,
        mobile: PhoneNumber,
        name: EmployeeName,
        roles: List<ApiEnum<EmployeeRole, String>>
    ): MySamokatApiGWAssertions {
        val listElement = contacts.contacts.filter { it.userId == profileId }[0]
        getSoftAssertion().assertThat(listElement.mobile).isEqualTo(mobile)
        getSoftAssertion().assertThat(listElement.name).isEqualTo(name)
        getSoftAssertion().assertThat(listElement.roles.containsAll(roles)).isTrue
        return this
    }

    @Step("assert darkstore contacts list count")
    fun checkDarkstoreContactsPresentInList(
        contacts: UserContactsView,
        profileId: UUID
    ): MySamokatApiGWAssertions {
        getSoftAssertion().assertThat(contacts.contacts.filter { it.userId == profileId }).isNotNull
        return this
    }

    @Step("Check darkstore contacts sort")
    fun checkDarkstoreContactsRole(contact: ContactView, role: EmployeeRole): MySamokatApiGWAssertions{
        getSoftAssertion().assertThat(contact.roles[0].enumValue).isEqualTo(role)
        return this
    }

    @Step("Check shift in reviews pending list")
    fun checkShiftsInReviewsPendingList(shiftId: UUID, reviews: ReviewRequestsView): MySamokatApiGWAssertions {
        getSoftAssertion().assertThat(reviews.shifts!!.shift.id).isEqualTo(shiftId)
        return this
    }

    @Step("Check reviews pending list is empty")
    fun checkReviewsPendingListEmpty(reviews: ReviewRequestsView): MySamokatApiGWAssertions {
        getSoftAssertion().assertThat(reviews.shifts).isNull()
        return this
    }

    @Step("Check shift review")
    fun checkShiftReview(shift: WorkedOutShiftView, request: SubmitShiftReviewRequest): MySamokatApiGWAssertions {
        getSoftAssertion().assertThat(shift.review!!.rating).isEqualTo(request.rating)
        return this
    }

    @Step("Check actual statistics")
    fun checkActualStatistics(
        stat: UserActualStatisticsView,
        monthlyDeliverymanShiftsCount: Int,
        monthlyPickerShiftsCount: Int,
        weeklyDeliverymanShiftsCount: Int,
        weeklyPickerShiftsCount: Int,
        deliveredOrdersCount: Int = 0
    ): MySamokatApiGWAssertions {
        getSoftAssertion().assertThat(stat.monthly.shifts.deliveryman.shiftsCount).isEqualTo(monthlyDeliverymanShiftsCount)
        getSoftAssertion().assertThat(stat.monthly.shifts.picker.shiftsCount).isEqualTo(monthlyPickerShiftsCount)
        getSoftAssertion().assertThat(stat.weekly.shifts.deliveryman.shiftsCount).isEqualTo(weeklyDeliverymanShiftsCount)
        getSoftAssertion().assertThat(stat.weekly.shifts.picker.shiftsCount).isEqualTo(weeklyPickerShiftsCount)
        getSoftAssertion().assertThat(stat.weekly.shifts.deliveryman.deliveredOrdersCount).isEqualTo(deliveredOrdersCount)
        getSoftAssertion().assertThat(stat.monthly.shifts.deliveryman.deliveredOrdersCount).isEqualTo(deliveredOrdersCount)
        return this
    }

    @Step("Check aggregated statistics")
    fun checkAggregatedStatistics(
        stat: AggregatedUserStatisticsListView,
        deliverymanShiftsCount: Int,
        pickerShiftsCount: Int
    ): MySamokatApiGWAssertions {
        getSoftAssertion().assertThat(stat.statistics[0].stats.shifts.picker.shiftsCount).isEqualTo(pickerShiftsCount)
        getSoftAssertion().assertThat(stat.statistics[0].stats.shifts.deliveryman.shiftsCount).isEqualTo(deliverymanShiftsCount)
        return this
    }

    @Step("Check aggregated statistics")
    fun checkAggregatedStatisticsEmpty(
        stat: AggregatedUserStatisticsListView
    ): MySamokatApiGWAssertions {
        getSoftAssertion().assertThat(stat.statistics.size).isEqualTo(0)
        return this
    }

    @Step("Check shifts statistics")
    fun checkShiftsStatisticsEmpty(
        stat: ShiftsStatisticsListView
    ): MySamokatApiGWAssertions {
        getSoftAssertion().assertThat(stat.shifts.size).isEqualTo(0)
        return this
    }

    @Step("Check shifts statistics")
    fun checkShiftsStatistics(
        stat: WorkedOutShiftStatistics,
        shiftType: WorkedOutShiftType,
    ): MySamokatApiGWAssertions {
        getSoftAssertion().assertThat(stat.shiftType.enumValue).isEqualTo(shiftType)
        return this
    }

    // tips
    @Step("Check tips data")
    fun checkTipsData(tips: UserTipsView, balance: Long, status: UserTipsRegistrationStatus): MySamokatApiGWAssertions {
        getSoftAssertion().assertThat(tips.balance).isEqualTo(balance)
        getSoftAssertion().assertThat(tips.status.enumValue).isEqualTo(status)

        return this
    }

    @Step("Check tips data")
    fun checkTipsDataWOBalance(tips: UserTipsView, status: UserTipsRegistrationStatus): MySamokatApiGWAssertions {
        getSoftAssertion().assertThat(tips.balance).isNull()
        getSoftAssertion().assertThat(tips.status.enumValue).isEqualTo(status)

        return this
    }

    @Step("Check first login event message")
    fun checkFirstLoginEventMessage(message: FirstLoginEvent): MySamokatApiGWAssertions {

        getSoftAssertion().assertThat(message).isNotNull
        return this
    }

    // push tokens
    @Step("Check push token")
    fun checkPushToken(
        tokenRow: ResultRow,
        userId: UUID,
        token: String
    ): MySamokatApiGWAssertions {
        getSoftAssertion().assertThat(tokenRow[Tokens.applicationId]).isEqualTo(3)
        getSoftAssertion().assertThat(tokenRow[Tokens.providerId]).isEqualTo(1)
        getSoftAssertion().assertThat(tokenRow[Tokens.userId]).isEqualTo(userId)
        getSoftAssertion().assertThat(tokenRow[Tokens.token]).isEqualTo(token)
        return this
    }

    @Step("Check push token not exists")
    fun checkPushTokenNotExists(token: Boolean): MySamokatApiGWAssertions {
        getSoftAssertion().assertThat(token).isFalse
        return this
    }

    @Step("Check config")
    fun checkConfig(config: MSMKTConfigView): MySamokatApiGWAssertions{
        getSoftAssertion().assertThat(config.config.tech_support_faq.links.filter { it.title == Constants.techSupportFaqMobileTitle }.count()).isEqualTo(1)
        getSoftAssertion().assertThat(config.config.tech_support_faq.links.filter { it.title == Constants.techSupportFaqMobileTitle }[0].url).isEqualTo(Constants.techSupportFaqMobile)
        getSoftAssertion().assertThat(config.config.list_schedule_max_days).isEqualTo(Constants.listScheduleMaxDays)
        getSoftAssertion().assertThat(config.config.shifts_schedule_min_hours).isEqualTo(Constants.shiftScheduleMinHours)

        return this
    }

    @Step("Check shiftAssignmentInstantPush data")
    fun checkShiftAssignmentInstantPushData(task: ResultRow, payload: String){
        getSoftAssertion().assertThat(task[Task.payload]).isEqualTo(payload)
        getSoftAssertion().assertThat(task[Task.scheduledAt] > Instant.now()).isTrue
        getSoftAssertion().assertThat(task[Task.scheduledAt] < Instant.now().plusSeconds(660)).isTrue
    }

    @Step("Check shiftAssignmentInstantPush data")
    fun checkShiftAssignmentReminderPushData(task: ResultRow, date: LocalDate ){
        getSoftAssertion().assertThat(task[Task.scheduledAt] > date.atStartOfDay().toInstant(ZoneOffset.UTC)).isTrue
        getSoftAssertion().assertThat(task[Task.scheduledAt] < date.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC)).isTrue
    }

}