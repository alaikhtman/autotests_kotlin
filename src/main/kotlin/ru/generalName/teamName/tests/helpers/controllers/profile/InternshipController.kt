package ru.generalName.teamName.tests.helpers.controllers.profile

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*


@Service
class InternshipController {

    @Autowired
    lateinit var internshipFeign: DarkstoreUsersInternshipsClient

    fun createInternship(
        profileId: UUID, darkstoreUserRole: DarkstoreUserRole, request: CreateInternshipRequest
    ): RestResult<Unit, CreateInternshipError>? {
        return try {
            internshipFeign.createDarkstoreUserInternship(profileId, darkstoreUserRole, request).get()
        } catch (e: InterruptedException) {
            e.printStackTrace()
            null
        }
    }

    fun getInternshipByProfileId(
        profileId: UUID
    ): RestResult<InternshipsView, GeneralError>? {
        return try {
            internshipFeign.getProfileInternships(profileId).get()
        } catch (e: InterruptedException) {
            e.printStackTrace()
            null
        }
    }

    fun getInternshipByDarkstoreId(
        darkstoreId: UUID,
        request: GetDarkstoreInternshipsRequest
    ):RestResult<InternshipsView, GetDarkstoreInternshipsError>? {
        return try {
            internshipFeign.getDarkstoreInternships(darkstoreId, request).get()
        } catch (e: InterruptedException) {
            e.printStackTrace()
            null
        }
    }

    fun updateInternship(
        profileId: UUID, darkstoreUserRole: DarkstoreUserRole, request: UpdateInternshipRequest
    ):RestResult<Unit, UpdateInternshipError>? {
        return try {
            internshipFeign.updateDarkstoreUserInternship(profileId, darkstoreUserRole, request).get()
        } catch (e: InterruptedException) {
            e.printStackTrace()
            null
        }
    }

    fun rejectInternship(
        profileId: UUID, darkstoreUserRole: DarkstoreUserRole, request: RejectInternshipRequest
    ):RestResult<Unit, RejectInternshipError>? {
        return try {
            internshipFeign.rejectDarkstoreUserInternship(profileId, darkstoreUserRole, request).get()
        } catch (e: InterruptedException) {
            e.printStackTrace()
            null
        }
    }

    fun cancelInternship(
        profileId: UUID, darkstoreUserRole: DarkstoreUserRole, request: CancelInternshipRequest
    ):RestResult<Unit, CancelInternshipError>? {
        return try {
            internshipFeign.cancelDarkstoreUserInternship(profileId, darkstoreUserRole, request).get()
        } catch (e: InterruptedException) {
            e.printStackTrace()
            null
        }
    }

    fun closeInternship(
        profileId: UUID, darkstoreUserRole: DarkstoreUserRole, request: CloseInternshipRequest
    ):RestResult<Unit, CloseInternshipError>?{
        return try {
            internshipFeign.closeDarkstoreUserInternship(profileId, darkstoreUserRole, request).get()
        } catch (e: InterruptedException) {
            e.printStackTrace()
            null
        }
    }
}