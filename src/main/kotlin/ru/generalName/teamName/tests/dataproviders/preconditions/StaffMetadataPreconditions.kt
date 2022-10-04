package ru.samokat.mysamokat.tests.dataproviders.preconditions

import io.qameta.allure.Step
import ru.samokat.mysamokat.tests.dataproviders.Constants
import ru.samokat.mysamokat.tests.dataproviders.staffMetadata.CreateCommentRequestBuilder
import ru.samokat.mysamokat.tests.dataproviders.staffMetadata.SearchUsersRequestBuilder
import ru.samokat.staffmetadata.api.users.store.StoreUserMetadataRequest
import java.util.*

class StaffMetadataPreconditions {


    @Step("Set create comment request")
    fun setCreateCommentRequest(
        userId: UUID = Constants.uuidForComment1,
        commentary: String?
    ): StoreUserMetadataRequest {
        return CreateCommentRequestBuilder()
                .userId(userId)
                .commentary(commentary)
            .build()
        }


    @Step("Set search comment builder")
    fun setSearchCommentBuilder(
        userIds: MutableList<UUID>?,
        usersCount: Int? = null
    ): SearchUsersRequestBuilder {
        val builder = SearchUsersRequestBuilder()

        if (usersCount != null)
            builder
                .randomUserIds(usersCount)
        if (userIds != null)
            builder.userIds(userIds!!)

        return builder
    }

}