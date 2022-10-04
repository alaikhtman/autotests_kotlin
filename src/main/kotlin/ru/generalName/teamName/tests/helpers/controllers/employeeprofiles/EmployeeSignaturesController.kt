package ru.samokat.mysamokat.tests.helpers.controllers.employeeprofiles

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.samokat.employeeprofiles.api.signatures.getbyprofileid.EmployeeSignatureView
import ru.samokat.employeeprofiles.api.signatures.getbyprofileid.GetSignatureByProfileIdError
import ru.samokat.employeeprofiles.client.EmployeeSignaturesClient
import ru.samokat.my.rest.client.RestResult
import java.util.*

@Service
class EmployeeSignaturesController {

    @Autowired
    lateinit var signFeign: EmployeeSignaturesClient

    fun getSignature(profileId: UUID): RestResult<EmployeeSignatureView, GetSignatureByProfileIdError>? {
        return try {
            signFeign.getSignature(profileId).get()
        } catch (e: InterruptedException) {
            e.printStackTrace()
            null
        }
    }

}