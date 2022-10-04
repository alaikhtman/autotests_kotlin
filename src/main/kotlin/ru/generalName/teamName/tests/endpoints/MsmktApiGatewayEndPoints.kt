package ru.samokat.mysamokat.tests.endpoints

internal interface MsmktApiGatewayEndPoints {
    companion object {
        const val AUTH_PASSWORD = "/oauth/token"
        const val TOKEN_REFRESH = "/oauth/token/refresh"
        const val OTP = "/oauth/otp"

        const val PUSH_TOKENS = "/me/push-tokens"
        const val PUSH_TOKENS_DEL = "/me/push-tokens/{token}"

        const val SHIFTS_SCHEDULE = "/shifts/schedule"
        const val SHIFTS_REVIEW = "/shifts/{shiftId}/review"
        const val SHIFT = "/shifts/{shiftId}"
        const val SHIFTS_ASSIGNMENT = "/shifts/assignments/{assignmentId}"
        const val SHIFTS = "/shifts"
        const val SHIFTS_REVIEW_PENDING = "/shifts/{shiftId}/review/pending"

        const val ME = "/me"
        const val ME_TIPS = "/me/tips"
        const val ME_STATS_SHIFTS = "/me/statistics/shifts"
        const val ME_STATS_AGREGATE = "/me/statistics/aggregated"
        const val ME_STATS_ACTUAL = "/me/statistics/actual"
        const val ME_CONTACTS = "/me/contacts"

        const val EXPECTATION_SCHEDULE = "/me/expectations/schedule"
        const val ME_REVIEWS_PENDING = "/me/reviews/pending"

        const val CONFIG = "/config/{type}"

    }
}
