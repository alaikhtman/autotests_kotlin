package ru.samokat.mysamokat.tests.dataproviders.staffApiGW


import ru.samokat.mysamokat.tests.dataproviders.dataClasses.staffApiGTW.users.SearchUsersContractsRequest
import java.time.Instant
import java.util.*

class SearchUsersContractsRequestBuilder {

    private lateinit var userIds: List<UUID>
    fun userIds(userIds: List<UUID>) = apply { this.userIds = userIds }
    fun getUserIds(): List<UUID> {
        return userIds
    }

    private var activeUntil: Instant? = null
    fun activeUntil(activeUntil: Instant?) = apply { this.activeUntil = activeUntil }
    fun getActiveUntil(): Instant? {
        return activeUntil
    }

    fun build(): SearchUsersContractsRequest {
        return SearchUsersContractsRequest(
            userIds = userIds,
            activeUntil = activeUntil

        )
    }
}