package ru.samokat.mysamokat.tests.helpers.controllers.employeeprofiles

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.samokat.employeeprofiles.api.contacts.DarkstoreContactsView
import ru.samokat.employeeprofiles.client.EmployeeContactsClient
import ru.samokat.mysamokat.tests.helpers.controllers.asSuccess
import java.util.*

@Service
class EmployeeContactsController {

    @Autowired
    lateinit var darkstoreContactsFeign: EmployeeContactsClient

    fun getDarkstoreContacts(darkstoreId: UUID): DarkstoreContactsView? {
        return try {
            darkstoreContactsFeign.getDarkstoreContacts(darkstoreId).get().asSuccess()
        } catch (e: InterruptedException) {
            e.printStackTrace()
            null
        }
    }

}