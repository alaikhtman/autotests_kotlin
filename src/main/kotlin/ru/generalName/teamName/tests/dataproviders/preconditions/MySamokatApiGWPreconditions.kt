package ru.samokat.mysamokat.tests.dataproviders.preconditions


import io.qameta.allure.Step
import org.springframework.stereotype.Service
import ru.samokat.my.domain.PhoneNumber
import ru.samokat.mysamokat.apigateway.api.oauth.otp.SendOtpRequest
import ru.samokat.mysamokat.apigateway.api.oauth.token.GetOAuthTokenRequest
import ru.samokat.mysamokat.apigateway.api.pushtokens.register.RegisterPushTokenRequest
import ru.samokat.mysamokat.apigateway.api.shifts.reviews.SubmitShiftReviewRequest
import ru.samokat.mysamokat.apigateway.api.shifts.storeschedule.StoreScheduleRequest
import ru.samokat.mysamokat.apigateway.api.shifts.storeschedule.StoreScheduleRequest.TimeRange
import ru.samokat.mysamokat.apigateway.api.user.getexpectations.schedule.submit.SubmitScheduleExpectationsRequest
import ru.samokat.mysamokat.tests.dataproviders.Constants
import ru.samokat.mysamokat.tests.dataproviders.StringAndPhoneNumberGenerator
import ru.samokat.mysamokat.tests.dataproviders.apiModels.staffcave.oauth.RefreshAccessTokenRequest
import ru.samokat.mysamokat.tests.dataproviders.msmktApiGW.*
import java.time.*
import java.util.*

@Service
class MySamokatApiGWPreconditions {

    fun getFormattedTimeRange(date: LocalDate, timeStart: String, timeEnd: String): TimeRange {
        return TimeRange(
            startingAt = getFormattedTime(date, timeStart),
            endingAt = getFormattedTime(date, timeEnd),
        )
    }

    fun getFormattedTime(date: LocalDate, timeStr: String): Instant {
        return date.atTime(LocalTime.parse(timeStr)).toInstant(ZoneOffset.UTC)
    }

    @Step("Fill auth request")
    fun fillAuthRequest(
        mobile: PhoneNumber = Constants.mobile1,
        password: CharArray,
        deviceId: UUID = UUID.randomUUID()
    ): GetOAuthTokenRequest {
        return GetOAuthTokenRequestBuilder()
            .mobile(mobile)
            .password(password)
            .deviceId(deviceId.toString())
            .build()
    }

    @Step("Fill auth request")
    fun fillAuthRequestWithOtp(
        mobile: PhoneNumber = Constants.mobile1,
        otp: CharArray,
        deviceId: UUID = UUID.randomUUID()
    ): GetOAuthTokenRequest {
        return GetOAuthTokenRequestBuilder()
            .mobile(mobile)
            .otp(otp)
            .deviceId(deviceId.toString())
            .build()
    }

    @Step("Fill otp request")
    fun fillOtpRequest(
        mobile: PhoneNumber = Constants.mobile1
    ): SendOtpRequest {
        return SendOtpRequestBuilder()
            .mobile(mobile)
            .build()
    }

    @Step("Fill refresh token request")
    fun fillRefreshTokenRequest(refreshToken: String): RefreshAccessTokenRequest {
        return RefreshAccessTokenRequestBuilder()
            .refreshToken(refreshToken)
            .build()
    }

    @Step("Fill store schedule request builder")
    fun fillStoreScheduleRequestBuilder(
        schedules: List<StoreScheduleRequest.TimeRange>
    ): StoreScheduleRequest {
        return StoreScheduleRequestBuilder()
            .schedules(schedules)
            .build()
    }

    @Step("Fill put expectations request")
    fun fillPutExpectationsRequest(
        weeklyWorkingDaysCount: Int,
        workingDayDuration: Duration
    ): SubmitScheduleExpectationsRequest {
        return SubmitScheduleExpectationsRequestBuilder()
            .weeklyWorkingDaysCount(weeklyWorkingDaysCount)
            .workingDayDuration(workingDayDuration)
            .build()

    }

    @Step("Fill Submit review request")
    fun fillSubmitReviewRequest(
        rating: Int, comment: String = StringAndPhoneNumberGenerator.generateRandomString(10)
    ): SubmitShiftReviewRequest {
        return SubmitShiftReviewRequestBuilder()
            .comment(comment)
            .rating(rating)
            .build()
    }

    @Step("Fill push token register request")
    fun fillPushTokenRegisterRequest(
        sessionId: UUID = UUID.randomUUID(),
        pushToken: UUID = UUID.randomUUID()
    ): RegisterPushTokenRequest {
        return RegisterPushTokenRequest(sessionId = sessionId.toString(), pushToken = pushToken.toString())
    }



}
