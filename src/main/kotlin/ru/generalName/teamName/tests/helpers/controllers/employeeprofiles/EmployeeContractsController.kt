package ru.samokat.mysamokat.tests.helpers.controllers.employeeprofiles

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.samokat.employeeprofiles.api.contracts.search.SearchUsersContractsError
import ru.samokat.employeeprofiles.api.contracts.search.SearchUsersContractsRequest
import ru.samokat.employeeprofiles.api.contracts.search.SearchUsersContractsView
import ru.samokat.employeeprofiles.api.contracts.search.*
import ru.samokat.employeeprofiles.client.EmployeeContractsClient
import ru.samokat.my.rest.client.RestResult

@Service
class EmployeeContractsController {
    @Autowired
    lateinit var contractsFeign: EmployeeContractsClient

    fun getContracts(request: SearchUsersContractsRequest): RestResult<SearchUsersContractsView, SearchUsersContractsError>? {
        return try {
            contractsFeign.searchUsersContracts(request).get()
        } catch (e: InterruptedException) {
            e.printStackTrace()
            null
        }
    }

    fun getContractsByAccountingProfileIds(request: SearchAccountingProfileContractsRequest): RestResult<SearchAccountingProfileContractsView, SearchAccountingProfileContractsError>? {
        return try {
            contractsFeign.searchAccountingProfileContracts(request).get()
        } catch (e: InterruptedException) {
            e.printStackTrace()
            null
        }
    }
}