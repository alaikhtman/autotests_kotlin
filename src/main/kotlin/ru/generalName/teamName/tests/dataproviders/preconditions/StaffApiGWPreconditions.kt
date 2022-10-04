package ru.samokat.mysamokat.tests.dataproviders.preconditions

import io.qameta.allure.Step
import ru.samokat.employeeprofiles.api.profiles.create.CreateProfileRequest
import ru.samokat.my.domain.shifts.TimeRange
import ru.samokat.mysamokat.tests.dataproviders.Constants
import ru.samokat.mysamokat.tests.dataproviders.dataClasses.staffApiGTW.oauth.*
import ru.samokat.mysamokat.tests.dataproviders.dataClasses.staffApiGTW.users.*
import ru.samokat.mysamokat.tests.dataproviders.staffApiGW.*
import java.time.Instant
import java.util.*


class StaffApiGWPreconditions {


    private lateinit var oAuthTokenRequest: GetOAuthTokenRequest
    fun oAuthTokenRequest(): GetOAuthTokenRequest {
        return oAuthTokenRequest
    }

    private lateinit var sendOtpRequest: SendOtpRequest
    fun sendOtpRequest(): SendOtpRequest {
        return sendOtpRequest
    }

    private lateinit var refreshAccessTokenRequest: RefreshAccessTokenRequest
    fun refreshAccessTokenRequest(): RefreshAccessTokenRequest {
        return refreshAccessTokenRequest
    }

    private lateinit var createRequest: CreateProfileRequest
    fun createRequest(): CreateProfileRequest {
        return createRequest
    }

    private lateinit var commentRequest: StoreUserMetadataRequest
    fun commentRequest(): StoreUserMetadataRequest {
        return commentRequest
    }


    private lateinit var contractRequest: SearchUsersContractsRequest
    fun contractRequest(): SearchUsersContractsRequest {
        return contractRequest
    }

    private lateinit var assigneeRequest: GetAssigneeListRequest
    fun assigneeRequest(): GetAssigneeListRequest {
        return assigneeRequest
    }


    @Step("Fill auth request")
    fun fillAuthRequest(
        mobile: String = Constants.mobile1.asStringWithoutPlus(),
        password: CharArray
    ) = apply {
        this.oAuthTokenRequest = GetOAuthTokenRequestBuilder()
            .mobile(mobile)
            .password(password)
            .build()
    }

    @Step("Fill auth request")
    fun fillAuthRequestWithOtp(
        mobile: String = Constants.mobile1.asStringWithoutPlus(),
        otp: CharArray,
    ) = apply {
        this.oAuthTokenRequest = GetOAuthTokenRequestBuilder()
            .mobile(mobile)
            .otp(otp)
            .build()
    }

    @Step("Fill otp request")
    fun fillOtpRequest(
        mobile: String = Constants.mobile1.asStringWithoutPlus()
    ) = apply {
        this.sendOtpRequest =
            SendOtpRequestBuilder()
                .mobile(mobile)
                .build()
    }

    @Step("Fill refresh token request")
    fun fillRefreshTokenRequest(refreshToken: String) = apply {
        this.refreshAccessTokenRequest =
            RefreshAccessTokenRequestBuilder()
                .refreshToken(refreshToken)
                .build()
    }

    @Step("Fill comment request")
    fun fillCommentRequest(comment: String?) = apply {
        this.commentRequest = StoreUserMetadataRequestBuilder()
            .comment(comment)
            .build()
    }

    @Step("Fill contracts search request")
    fun fillContractsSearchRequest(userIds: MutableList<UUID>, activeUntil: Instant? = null) = apply {
        this.contractRequest = SearchUsersContractsRequestBuilder()
            .userIds(userIds)
            .activeUntil(activeUntil)
            .build()
    }

    @Step("Fill assignee search request")
    fun fillAssigneeSearchRequestFor24TimeRange12HShift(
        name: String,
        userRoles: List<String>,
        searchFrom: Long = Instant.now().minusSeconds(86400).toEpochMilli(),
        searchTo: Long = Instant.now().toEpochMilli(),
        assignFrom: Long = Instant.now().minusSeconds(43200).toEpochMilli(),
        assignTo: Long = Instant.now().toEpochMilli()
    ) = apply {
        this.assigneeRequest = GetAssigneeListRequestBuilder()
            .name(name)
            .userRoles(userRoles)
            .searchFrom(searchFrom)
            .searchTo(searchTo)
            .assignFrom(assignFrom)
            .assignTo(assignTo)
            .build()

    }

    fun get24TimeRange(): TimeRange {
        return TimeRange(
            startingAt = Instant.now().minusSeconds(86400),
            endingAt = Instant.now()
        )
    }

    fun get12TimeRange(): TimeRange {
        return TimeRange(
            startingAt = Instant.now().minusSeconds(43200),
            endingAt = Instant.now()
        )
    }

    fun get2TimeRange(): TimeRange {
        return TimeRange(
            startingAt = Instant.now().minusSeconds(7200),
            endingAt = Instant.now()
        )
    }

    fun get3TimeRange(): TimeRange {
        return TimeRange(
            startingAt = Instant.now().minusSeconds(21600),
            endingAt = Instant.now().minusSeconds(10800)
        )
    }


}