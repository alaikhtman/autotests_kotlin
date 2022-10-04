package ru.samokat.mysamokat.tests.helpers.controllers.employeeprofiles

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.samokat.employeeprofiles.api.profiles.authenticate.AuthenticateProfileError
import ru.samokat.employeeprofiles.api.profiles.authenticate.AuthenticateProfileRequest
import ru.samokat.employeeprofiles.api.profiles.changepassword.ChangePasswordError
import ru.samokat.employeeprofiles.api.profiles.changepassword.ChangedPasswordView
import ru.samokat.employeeprofiles.api.common.domain.EmployeeProfileView
import ru.samokat.employeeprofiles.api.profiles.create.CreateProfileError
import ru.samokat.employeeprofiles.api.profiles.create.CreateProfileRequest
import ru.samokat.employeeprofiles.api.profiles.create.CreatedProfileView
import ru.samokat.employeeprofiles.api.profiles.disable.DisableProfileError
import ru.samokat.employeeprofiles.api.profiles.getbyid.GetProfileByIdError
import ru.samokat.employeeprofiles.api.profiles.getprofiles.GetProfilesError
import ru.samokat.employeeprofiles.api.profiles.getprofiles.GetProfilesRequest
import ru.samokat.employeeprofiles.api.profiles.getprofiles.GetProfilesView
import ru.samokat.employeeprofiles.api.profiles.search.GetProfilesByIdsError
import ru.samokat.employeeprofiles.api.profiles.search.GetProfilesByIdsRequest
import ru.samokat.employeeprofiles.api.profiles.search.GetProfilesByIdsView
import ru.samokat.employeeprofiles.api.profiles.update.UpdateProfileError
import ru.samokat.employeeprofiles.api.profiles.update.UpdateProfileRequest
import ru.samokat.employeeprofiles.client.EmployeeProfilesClient
import ru.samokat.my.domain.PhoneNumber
import ru.samokat.my.rest.client.RestResult
import ru.samokat.mysamokat.tests.dataproviders.employee.ChangePasswordRequestBuilder
import ru.samokat.mysamokat.tests.dataproviders.employee.DisableProfileRequestBuilder
import ru.samokat.mysamokat.tests.helpers.controllers.database.EmployeeProfilesDatabaseController
import java.util.*

@Service
class EmployeeProfileController {

    @Autowired
    lateinit var emproFeign : EmployeeProfilesClient

    @Autowired
    private lateinit var databaseController: EmployeeProfilesDatabaseController



    fun createProfile(request: CreateProfileRequest): RestResult<CreatedProfileView, CreateProfileError>? {
        return try {
            emproFeign.createProfile(request).get()
        } catch (e: InterruptedException) {
            e.printStackTrace()
            null
        }
    }

    fun getProfileById(profileId: UUID): RestResult<EmployeeProfileView, GetProfileByIdError>? {
        return try {
            emproFeign.getProfileById(profileId).get()
        } catch (e: InterruptedException) {
            e.printStackTrace()
            null
        }
    }

    fun getProfiles(request: GetProfilesRequest): RestResult<GetProfilesView, GetProfilesError>? {
        return try {
            emproFeign.getProfiles(request).get()
        } catch (e: InterruptedException) {
            e.printStackTrace()
            null
        }
    }

    fun deleteProfile(profileId: UUID): RestResult<Unit, DisableProfileError>? {
        val disableProfileRequest = DisableProfileRequestBuilder()
            .randomIssuerProfileId()
            .build()
        return try {
            emproFeign.disableProfile(profileId = profileId, request = disableProfileRequest).join()
        } catch (e: InterruptedException) {
            e.printStackTrace()
            null
        }
    }

    fun deleteProfileByMobileIfExists(mobile: PhoneNumber) {
        val profileId = databaseController.getProfileIdByMobile(mobile)
        if (profileId != null)
            deleteProfile(profileId)
    }

    fun deleteProfileByAccountingProfileIdIfExists(accountingProfileId: String) {
        val profileId = databaseController.getProfileIdByAccountingProfileId(accountingProfileId)
        if (profileId != null)
            deleteProfile(profileId)
    }

    fun updateProfilePassword(profileId: UUID): RestResult<ChangedPasswordView, ChangePasswordError>? {
        val changePasswordRequest = ChangePasswordRequestBuilder()
            .randomIssuerProfileId()
            .build()
        return try {
            emproFeign.changePassword(profileId, changePasswordRequest).get()
        } catch (e: InterruptedException) {
            e.printStackTrace()
            null
        }
    }

    fun updateProfile(profileId: UUID, request: UpdateProfileRequest): RestResult<Unit, UpdateProfileError>? {
        return try {
            emproFeign.updateProfile(profileId, request).get()
        } catch (e: InterruptedException) {
            e.printStackTrace()
            null
        }
    }

    fun authenticateProfile(request: AuthenticateProfileRequest): RestResult<EmployeeProfileView, AuthenticateProfileError>?{
        return try {
            emproFeign.authenticateProfile(request).get()
        } catch (e: InterruptedException) {
            e.printStackTrace()
            null
        }
    }

    fun getProfilesByIdsList(request: GetProfilesByIdsRequest): RestResult<GetProfilesByIdsView, GetProfilesByIdsError>?{
        return try {
            emproFeign.getProfilesByIds(request).get()
        } catch (e: InterruptedException) {
            e.printStackTrace()
            null
        }
    }
}