package ru.samokat.mysamokat.tests.helpers.controllers.employeeprofiles

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.samokat.employeeprofiles.api.common.domain.StaffPartnerView
import ru.samokat.employeeprofiles.api.staffpartners.createpartners.CreatePartnerError
import ru.samokat.employeeprofiles.api.staffpartners.createpartners.CreatePartnerRequest
import ru.samokat.employeeprofiles.api.staffpartners.getpartners.GetPartnersError
import ru.samokat.employeeprofiles.api.staffpartners.getpartners.GetPartnersRequest
import ru.samokat.employeeprofiles.api.staffpartners.getpartners.GetPartnersView
import ru.samokat.employeeprofiles.client.StaffPartnersClient
import ru.samokat.my.rest.client.RestResult

@Service
class StaffPartnersController {

    @Autowired
    lateinit var staffPartnersFeign : StaffPartnersClient

    fun getStaffPartners(request: GetPartnersRequest): RestResult<GetPartnersView, GetPartnersError>? {
        return try {
            staffPartnersFeign.getPartners(request).get()
        } catch (e: InterruptedException) {
            e.printStackTrace()
            null
        }
    }

    fun createStaffPartner(request: CreatePartnerRequest): RestResult<StaffPartnerView, CreatePartnerError>? {
        return try {
            staffPartnersFeign.createPartner(request).get()
        } catch (e: InterruptedException) {
            e.printStackTrace()
            null
        }
    }

}