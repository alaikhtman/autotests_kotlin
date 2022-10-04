package ru.samokat.mysamokat.tests.dataproviders.staffMetadata

import ru.samokat.staffmetadata.api.users.search.SearchUsersMetadataRequest
import java.util.*

class SearchUsersRequestBuilder {

    private var userIds: MutableList<UUID>?= null
    fun userIds(userId: MutableList<UUID>?) = apply {this.userIds = userId}
    fun getUserId(): MutableList<UUID> {
        return userIds!!
    }

    fun build(): SearchUsersMetadataRequest {
        return SearchUsersMetadataRequest(
            userIds = userIds!!
        )
    }

    fun randomUserIds(count: Int) = apply {
        userIds = mutableListOf()
        for (i in 0 until count) {
            userIds!!.add(UUID.randomUUID())
        }
    }
}