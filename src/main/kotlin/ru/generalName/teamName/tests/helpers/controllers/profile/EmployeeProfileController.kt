package ru.generalName.teamName.tests.helpers.controllers.profile

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
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