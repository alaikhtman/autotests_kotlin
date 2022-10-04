package ru.samokat.mysamokat.tests.checkers

import io.qameta.allure.Step
import org.assertj.core.api.SoftAssertions
import org.jetbrains.exposed.sql.ResultRow
import org.springframework.stereotype.Service
import ru.samokat.mysamokat.tests.dataproviders.helpers.database.staff_metadata.UserMetadata
import ru.samokat.staffmetadata.api.users.search.UsersMetadataView
import ru.samokat.staffmetadata.api.users.store.StoreUserMetadataRequest


@Service
class StaffMetadataAssertion {

    private val softAssertion = SoftAssertions()
    fun getSoftAssertion(): SoftAssertions {
        return softAssertion
    }

    fun assertAll() {
        getSoftAssertion().assertAll()
    }


    @Step("checkCommentInDatabase")
    fun checkCommentInDatabase(actual: ResultRow, expected: StoreUserMetadataRequest): StaffMetadataAssertion {
        getSoftAssertion().assertThat(actual[UserMetadata.commentary]).isEqualTo(expected.commentary)
        return this
    }

    @Step("Check metadata list count")
    fun checkMetadataListCount(userIdsCount: Int, expectedCount: Int): StaffMetadataAssertion {
        getSoftAssertion().assertThat(userIdsCount).isEqualTo(expectedCount)
        return this
    }

    @Step("checkEmptyMetadata")
    fun checkEmptyMetadata(commentInResponse: UsersMetadataView): StaffMetadataAssertion {
        getSoftAssertion().assertThat(commentInResponse.metadata.size).isEqualTo(0)
        return this
    }

    @Step("checkCommentInSearchResponse")
    fun checkCommentInSearchResponse(commentInResponse: String?, commentInDb: String): StaffMetadataAssertion {
        getSoftAssertion().assertThat(commentInResponse).isEqualTo(commentInDb)
        return this
    }

    @Step("checkCommentsInSearchResponse")
    fun checkCommentsInSearchResponse(commentsInResponse: MutableList<String?>, commentsInDb: MutableList<String>): StaffMetadataAssertion {
    for (i in 0 until commentsInResponse.count()) {
        getSoftAssertion().assertThat(commentsInResponse[i]).isEqualTo(commentsInDb[i])
        }
    return this
    }
}