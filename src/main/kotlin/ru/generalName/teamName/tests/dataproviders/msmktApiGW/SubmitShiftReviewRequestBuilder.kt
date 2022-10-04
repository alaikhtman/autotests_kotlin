package ru.samokat.mysamokat.tests.dataproviders.msmktApiGW

import ru.samokat.mysamokat.apigateway.api.shifts.reviews.SubmitShiftReviewRequest
import ru.samokat.mysamokat.apigateway.api.user.getexpectations.schedule.submit.SubmitScheduleExpectationsRequest
import java.time.Duration

class SubmitShiftReviewRequestBuilder {

    private var rating: Int = 1
    fun rating(rating: Int) =
        apply { this.rating = rating }

    fun getRating(): Int {
        return rating
    }

    private var comment: String? = null
    fun comment(comment: String) =
        apply { this.comment = comment }

    fun getComment(): String {
        return comment!!
    }

    fun build(): SubmitShiftReviewRequest {
        return SubmitShiftReviewRequest(
            rating = rating,
            comment = comment
        )
    }
}