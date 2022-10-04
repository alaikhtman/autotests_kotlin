package ru.samokat.logistics.tests.dataproviders.employee

import ru.samokat.employeeprofiles.api.profiles.search.GetProfilesByIdsRequest
import java.util.*

class GetProfilesByIdsRequestBuilder {
    private lateinit var profileIds: MutableList<UUID>
    fun profileIds(profileIds: MutableList<UUID>) = apply {this.profileIds = profileIds}
    fun getProfileIds(): MutableList<UUID> {
        return profileIds!!
    }

    fun randomProfilesIds(count: Int?) = apply {
        profileIds = mutableListOf()
        for (i in 0 until count!!) {
            profileIds!!.add(UUID.randomUUID())
        }
    }

    fun build(): GetProfilesByIdsRequest {
        return GetProfilesByIdsRequest(
            profileIds = profileIds
        )
    }
}
