package ru.samokat.mysamokat.tests.dataproviders.employee

import ru.samokat.employeeprofiles.api.darkstoreusers.getbyprofileids.GetDarkstoreUsersByProfileIdsRequest
import java.util.*

class GetDarkstoreUsersByProfileIdsRequestBuilder {


    private lateinit var profileIds: List<UUID>
    fun profileIds(profileIds: List<UUID>) = apply { this.profileIds = profileIds }
    fun getProfileIds(): List<UUID> {
        return profileIds
    }

    fun build(): GetDarkstoreUsersByProfileIdsRequest {
        return GetDarkstoreUsersByProfileIdsRequest(
            profileIds = profileIds
        )
    }
}