package ru.samokat.mysamokat.tests.helpers.controllers.employeeprofiles

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.samokat.employeeprofiles.api.darkstoreusers.domain.DarkstoreUserRole
import ru.samokat.employeeprofiles.api.darkstoreusers.violations.dictionary.get.ViolationDictionaryView
import ru.samokat.employeeprofiles.api.darkstoreusers.violations.get.DarkstoreUserViolationListView
import ru.samokat.employeeprofiles.api.darkstoreusers.violations.get.GetDarkstoreUserViolationsError
import ru.samokat.employeeprofiles.api.darkstoreusers.violations.search.SearchDarkstoreUsersViolationsError
import ru.samokat.employeeprofiles.api.darkstoreusers.violations.search.SearchDarkstoreUsersViolationsRequest
import ru.samokat.employeeprofiles.api.darkstoreusers.violations.search.SearchDarkstoreUsersViolationsView
import ru.samokat.employeeprofiles.api.darkstoreusers.violations.store.StoreDarkstoreUserViolationError
import ru.samokat.employeeprofiles.api.darkstoreusers.violations.store.StoreDarkstoreUserViolationRequest
import ru.samokat.employeeprofiles.api.darkstoreusers.violations.store.StoreDarkstoreUserViolationView
import ru.samokat.employeeprofiles.client.DarkstoreUserViolationsClient
import ru.samokat.my.rest.api.error.GeneralError
import ru.samokat.my.rest.client.RestResult
import java.util.*

@Service
class DarkstoreUserViolationsController {

    @Autowired
    lateinit var darkstoreUserViolationsClient: DarkstoreUserViolationsClient

    fun getViolationsDictionary(): RestResult<ViolationDictionaryView, GeneralError>? {
        return try {
            darkstoreUserViolationsClient.getDarkstoreViolationsDictionary().get()
        } catch (e: InterruptedException) {
            e.printStackTrace()
            null
        }
    }

    fun addViolation(profileId: UUID, darkstoreId: UUID, role: DarkstoreUserRole, request: StoreDarkstoreUserViolationRequest): RestResult<StoreDarkstoreUserViolationView, StoreDarkstoreUserViolationError>? {

        return try {
            darkstoreUserViolationsClient.storeDarkstoreUserViolation(profileId, darkstoreId, role, request).get()
        } catch (e: InterruptedException) {
            e.printStackTrace()
            null
        }
    }

    fun getViolation(profileId: UUID, darkstoreUserRole: DarkstoreUserRole): RestResult<DarkstoreUserViolationListView, GetDarkstoreUserViolationsError>? {
        return try {
            darkstoreUserViolationsClient.getDarkstoreUserViolationsByIdAndRole(profileId, darkstoreUserRole).get()
        } catch (e: InterruptedException) {
            e.printStackTrace()
            null
        }
    }

    fun searchViolation(request: SearchDarkstoreUsersViolationsRequest): RestResult<SearchDarkstoreUsersViolationsView, SearchDarkstoreUsersViolationsError>? {
        return try {
            darkstoreUserViolationsClient.searchDarkstoreUsersViolations(request).get()
        } catch (e: InterruptedException) {
            e.printStackTrace()
            null
        }
    }

}