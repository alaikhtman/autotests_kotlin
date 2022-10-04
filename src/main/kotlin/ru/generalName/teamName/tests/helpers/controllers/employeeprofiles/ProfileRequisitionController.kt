package ru.samokat.mysamokat.tests.helpers.controllers.employeeprofiles

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.samokat.employeeprofiles.api.profilerequisitions.decline.DeclineProfileRequisitionError
import ru.samokat.employeeprofiles.api.profilerequisitions.decline.DeclineProfileRequisitionRequest
import ru.samokat.employeeprofiles.api.profilerequisitions.get.GetProfileRequisitionError
import ru.samokat.employeeprofiles.api.profilerequisitions.get.GetProfileRequisitionView
import ru.samokat.employeeprofiles.api.profilerequisitions.search.SearchProfileRequisitionsError
import ru.samokat.employeeprofiles.api.profilerequisitions.search.SearchProfileRequisitionsRequest
import ru.samokat.employeeprofiles.api.profilerequisitions.search.SearchProfileRequisitionsView
import ru.samokat.employeeprofiles.client.ProfileRequisitionsClient
import ru.samokat.my.rest.client.RestResult
import java.util.*

@Service
class ProfileRequisitionController {


    @Autowired
    lateinit var requisitionsFeign : ProfileRequisitionsClient

    fun deleteRequisition(requisitionId: UUID, request: DeclineProfileRequisitionRequest): RestResult<Unit, DeclineProfileRequisitionError>? {
        return try {
            requisitionsFeign.declineProfileRequisition(requisitionId, request).get()
        } catch (e: InterruptedException) {
            e.printStackTrace()
            null
        }
    }

    fun getRequisitionById(requisitionId: UUID): RestResult<GetProfileRequisitionView, GetProfileRequisitionError>? {
        return try {
            requisitionsFeign.getProfileRequisition(requisitionId).get()
        } catch (e: InterruptedException) {
            e.printStackTrace()
            null
        }
    }

    fun searchRequisitions(request: SearchProfileRequisitionsRequest): RestResult<SearchProfileRequisitionsView, SearchProfileRequisitionsError>? {
        return try {
            requisitionsFeign.searchProfileRequisitions(request).get()
        } catch (e: InterruptedException) {
            e.printStackTrace()
            null
        }
    }
}