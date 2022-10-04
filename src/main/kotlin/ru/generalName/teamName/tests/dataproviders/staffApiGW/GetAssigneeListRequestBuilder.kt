package ru.samokat.mysamokat.tests.dataproviders.staffApiGW


import ru.samokat.mysamokat.tests.dataproviders.dataClasses.staffApiGTW.users.GetAssigneeListRequest
import java.time.LocalDate
import java.time.ZoneOffset


class GetAssigneeListRequestBuilder {


    private lateinit var name: String
    fun name(name: String) = apply { this.name = name }
    fun getName(): String {
        return name
    }

    private lateinit var userRoles: List<String>
    fun userRoles(userRoles: List<String>) = apply { this.userRoles = userRoles }
    fun getUserRoles(): List<String> {
        return userRoles
    }

    private var searchFrom: Long  = LocalDate.now().atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()
    fun searchFrom(searchFrom: Long) = apply { this.searchFrom = searchFrom }
    fun getSearchFrom(): Long {
        return searchFrom
    }

    private var searchTo: Long =  LocalDate.now().atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()
    fun searchTo(searchTo: Long) = apply { this.searchTo = searchTo }
    fun getSearchTo(): Long {
        return searchTo
    }


    private var assignFrom: Long =  LocalDate.now().atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()
    fun assignFrom(assignFrom: Long) = apply { this.assignFrom = assignFrom }
    fun getAssignFrom(): Long {
        return assignFrom
    }

    private var assignTo: Long =  LocalDate.now().atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()
    fun assignTo(assignTo: Long) = apply { this.assignTo = searchTo }
    fun getAssignTo(): Long {
        return assignTo
    }


    fun build(): GetAssigneeListRequest {
        return GetAssigneeListRequest(
            name = name,
            userRoles = userRoles,
            searchFrom = searchFrom,
            searchTo = searchTo,
            assignFrom = assignFrom,
            assignTo = assignTo
        )
    }
}