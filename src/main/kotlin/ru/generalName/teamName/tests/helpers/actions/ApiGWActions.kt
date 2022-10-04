package ru.generalName.teamName.tests.helpers.actions

import io.qameta.allure.Step
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import ru.generalName.teamName.tests.dataproviders.dataClasses.apiGTW.oauth.GetOAuthTokenRequest
import ru.generalName.teamName.tests.dataproviders.dataClasses.apiGTW.oauth.OAuthTokenView
import ru.generalName.teamName.tests.dataproviders.dataClasses.apiGTW.oauth.SendOtpRequest
import ru.generalName.teamName.tests.dataproviders.dataClasses.apiGTW.oauth.SendOtpResponseView
import ru.generalName.teamName.tests.dataproviders.dataClasses.apiGTW.users.CurrentUserView



@Component
@Scope("prototype")
class ApiGWActions {

    private var apiGWPreconditions: ApiGWPreconditions = ApiGWPreconditions()

    @Autowired
    private lateinit var staffAuthController: StaffAuthController

    @Autowired
    private lateinit var stubSmsController: SmsStubController

    @Autowired
    private lateinit var staffUserController: StaffUsersApiController

    @Autowired
    private lateinit var staffPartnerController: StaffStaffPartnerController


    @Step("Authenticate profile with password")
    fun authProfilePassword(request: GetOAuthTokenRequest): OAuthTokenView? {
        return staffAuthController.authenticateProfile(request)
    }

    @Step("Authenticate profile with otp")
    fun authProfileOtp(request: SendOtpRequest): SendOtpResponseView {
        return staffAuthController.postOtp(request)
    }

    @Step("Authenticate profile with password with error")
    fun authProfilePasswordError(request: GetOAuthTokenRequest, expectedSC: Int): ErrorView? {
        return staffAuthController.authenticateProfileWithError(request, expectedSC)
    }

    @Step("Get Otp Code")
    fun getOtp(mobile: PhoneNumber): String {
        Thread.sleep(3000)
        return stubSmsController.getOtp(mobile)
    }

    @Step("Refresh token")
    fun refreshToken(request: RefreshAccessTokenRequest): OAuthTokenView? {
        return staffAuthController.refreshToken(request)
    }

    @Step("Refresh token with error")
    fun refreshTokenError(request: RefreshAccessTokenRequest, expectedSC: Int): ErrorView? {
        return staffAuthController.refreshTokenWithError(request, expectedSC)
    }

    @Step("Delete token")
    fun deleteToken(accessToken: String, sc: Int) {
        return staffAuthController.deleteToken(accessToken, sc)
    }

    @Step("Get user data")
    fun getMe(accessToken: String): CurrentUserView {
        return staffUserController.getMe(accessToken)
    }


    @Step("Get user data with error")
    fun getMeWithError(accessToken: String, expectedSC: Int): ErrorView? {
        return staffUserController.getMeError(accessToken, expectedSC)
    }


    @Step("Get staff by darkstore")
    fun getUsersByDarkstore(
        accessToken: String,
        darkstoreId: String,
        userRoles: MutableList<String>
    ): EmployeeListView {
        return staffUserController.getUsers(accessToken, darkstoreId, userRoles)
    }

    @Step("Get staff by darkstore")
    fun getUsersByDarkstoreWithError(
        accessToken: String,
        darkstoreId: String,
        userRoles: MutableList<String>,
        expectedSC: Int
    ): ErrorView {
        return staffUserController.getUsersWithError(accessToken, darkstoreId, userRoles, expectedSC)
    }

    @Step("Add comment")
    fun addComment(accessToken: String, requestBody: StoreUserMetadataRequest, userId: String) {
        return staffUserController.addMetadata(accessToken, requestBody, userId)
    }

    @Step("Add comment")
    fun addCommentWithError(
        accessToken: String,
        requestBody: StoreUserMetadataRequest,
        userId: String,
        expectedSC: Int
    ) {
        return staffUserController.addMetadataWithError(accessToken, requestBody, userId, expectedSC)
    }

    @Step("Delete comment")
    fun deleteComment(accessToken: String, userId: String) {
        apiGWPreconditions.fillCommentRequest(comment = null)
        addComment(accessToken, apiGWPreconditions.commentRequest(), userId)
    }

    @Step("Get contracts")
    fun getContracts(accessToken: String, requestBody: SearchUsersContractsRequest): SearchUsersContractsView {
        return staffUserController.searchContract(accessToken, requestBody)
    }

    @Step("Get contracts with error")
    fun getContractsWithError(
        accessToken: String,
        requestBody: SearchUsersContractsRequest,
        expectedSC: Int
    ): ErrorView {
        return staffUserController.searchContractWithError(accessToken, requestBody, expectedSC)
    }

    @Step("Get contracts")
    fun getAssignee(
        accessToken: String,
        assigneeRequest: GetAssigneeListRequest
    ): AssigneeListView {
        return staffUserController.getAssignees(
            accessToken = accessToken,
            name = assigneeRequest.name,
            userRoles = assigneeRequest.userRoles.toMutableList(),
            searchFrom = assigneeRequest.searchFrom,
            searchTo = assigneeRequest.searchTo,
            assignFrom = assigneeRequest.assignFrom,
            assignTo = assigneeRequest.assignTo
        )
    }

    @Step("Get contracts with Error")
    fun getAssigneeWithError(
        accessToken: String,
        assigneeRequest: GetAssigneeListRequest,
        expectedSC: Int

    ): ErrorView {
        return staffUserController.getAssigneesWithError(
            accessToken = accessToken,
            name = assigneeRequest.name,
            userRoles = assigneeRequest.userRoles.toMutableList(),
            searchFrom = assigneeRequest.searchFrom,
            searchTo = assigneeRequest.searchTo,
            assignFrom = assigneeRequest.assignFrom,
            assignTo = assigneeRequest.assignTo,
            sc = expectedSC
        )
    }


    @Step("Get statistic")
    fun getStatistic(
        accessToken: String,
        darkstoreId: String,
        userRoles: MutableList<String>,
        from: Long,
        to: Long
    ): UsersStatisticsView {
        return staffUserController.getStatistics(accessToken, darkstoreId, userRoles, from, to)
    }

    @Step("Get statistic")
    fun getStatisticWithError(
        accessToken: String,
        darkstoreId: String,
        userRoles: MutableList<String>,
        from: Long,
        to: Long,
        expectedSC: Int
    ): ErrorView {
        return staffUserController.getStatisticsWithError(accessToken, darkstoreId, userRoles, from, to, expectedSC)
    }

    @Step("Get staff partners")
    fun getStaffPartners(
        accessToken: String
    ): GetStaffPartnersView {
        return staffPartnerController.getStaffPartner(accessToken)
    }


    @Step("Get staff partners with error")
    fun getStaffPartnersWithError(
        accessToken: String,
        expectedSC: Int
    ): ErrorView {
        return staffPartnerController.getStaffPartnerWithError(accessToken, expectedSC)
    }


}