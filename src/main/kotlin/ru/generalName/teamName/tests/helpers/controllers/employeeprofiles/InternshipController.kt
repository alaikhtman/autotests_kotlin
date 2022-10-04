package ru.samokat.mysamokat.tests.helpers.controllers.employeeprofiles

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.samokat.employeeprofiles.api.darkstoreusers.domain.DarkstoreUserRole
import ru.samokat.employeeprofiles.api.darkstoreusers.internships.cancel.CancelInternshipError
import ru.samokat.employeeprofiles.api.darkstoreusers.internships.cancel.CancelInternshipRequest
import ru.samokat.employeeprofiles.api.darkstoreusers.internships.close.CloseInternshipError
import ru.samokat.employeeprofiles.api.darkstoreusers.internships.close.CloseInternshipRequest
import ru.samokat.employeeprofiles.api.darkstoreusers.internships.create.CreateInternshipError
import ru.samokat.employeeprofiles.api.darkstoreusers.internships.create.CreateInternshipRequest
import ru.samokat.employeeprofiles.api.darkstoreusers.internships.get.GetDarkstoreInternshipsError
import ru.samokat.employeeprofiles.api.darkstoreusers.internships.get.GetDarkstoreInternshipsRequest
import ru.samokat.employeeprofiles.api.darkstoreusers.internships.get.InternshipsView
import ru.samokat.employeeprofiles.api.darkstoreusers.internships.reject.RejectInternshipError
import ru.samokat.employeeprofiles.api.darkstoreusers.internships.reject.RejectInternshipRequest
import ru.samokat.employeeprofiles.api.darkstoreusers.internships.update.UpdateInternshipError
import ru.samokat.employeeprofiles.api.darkstoreusers.internships.update.UpdateInternshipRequest
import ru.samokat.employeeprofiles.client.DarkstoreUsersInternshipsClient
import ru.samokat.my.rest.api.error.GeneralError
import ru.samokat.my.rest.client.RestResult
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