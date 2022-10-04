package ru.samokat.mysamokat.tests.helpers.controllers.employeeprofiles


import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.samokat.employeeprofiles.api.darkstoreusers.domain.DarkstoreUserRole
import ru.samokat.employeeprofiles.api.darkstoreusers.domain.DarkstoreUserView
import ru.samokat.employeeprofiles.api.darkstoreusers.getbyid.GetDarkstoreUserByIdError
import ru.samokat.employeeprofiles.api.darkstoreusers.getbyprofileids.GetDarkstoreUsersByProfileIdsError
import ru.samokat.employeeprofiles.api.darkstoreusers.getbyprofileids.GetDarkstoreUsersByProfileIdsRequest
import ru.samokat.employeeprofiles.api.darkstoreusers.getbyprofileids.GetDarkstoreUsersByProfileIdsView
import ru.samokat.employeeprofiles.api.darkstoreusers.search.SearchDarkstoreUsersError
import ru.samokat.employeeprofiles.api.darkstoreusers.search.SearchDarkstoreUsersRequest
import ru.samokat.employeeprofiles.api.darkstoreusers.search.SearchDarkstoreUsersView
import ru.samokat.employeeprofiles.api.darkstoreusers.update.UpdateDarkstoreUserError
import ru.samokat.employeeprofiles.api.darkstoreusers.update.UpdateDarkstoreUserRequest
import ru.samokat.employeeprofiles.client.DarkstoreUsersClient
import ru.samokat.my.rest.client.RestResult
import java.util.*

@Service
class DarkstoreUserController {

    @Autowired
    lateinit var dsUsersFeign: DarkstoreUsersClient

    fun getDarkstoreUserById( profileId: UUID, darkstoreId: UUID, darkstoreUserRole: DarkstoreUserRole): RestResult<DarkstoreUserView, GetDarkstoreUserByIdError>?{
        return try {
            dsUsersFeign.getDarkstoreUserById(profileId, darkstoreId, darkstoreUserRole).get()
        } catch (e: InterruptedException) {
            e.printStackTrace()
            null
        }
    }

    fun searchDarkstoreProfiles(request: SearchDarkstoreUsersRequest): RestResult<SearchDarkstoreUsersView, SearchDarkstoreUsersError>? {
        return try {
            dsUsersFeign.searchDarkstoreUsers(request).get()
        } catch (e: InterruptedException) {
            e.printStackTrace()
            null
        }
    }

    fun updateProfileStatus(profileId: UUID, darkstoreId: UUID, role: DarkstoreUserRole, request: UpdateDarkstoreUserRequest): RestResult<Unit, UpdateDarkstoreUserError>? {
        return try {
            dsUsersFeign.updateDarkstoreUserState(profileId, darkstoreId, role, request).get()
        } catch (e: InterruptedException) {
            e.printStackTrace()
            null
        }
    }

    fun findProfiles(request: GetDarkstoreUsersByProfileIdsRequest): RestResult<GetDarkstoreUsersByProfileIdsView, GetDarkstoreUsersByProfileIdsError>? {
        return try {
            dsUsersFeign.getDarkstoreUsersByProfileIds(request).get()
        } catch (e: InterruptedException) {
            e.printStackTrace()
            null
        }
    }

}
