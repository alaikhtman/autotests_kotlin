package ru.samokat.mysamokat.tests.helpers.controllers.database

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.stereotype.Service
import ru.samokat.mysamokat.tests.dataproviders.helpers.database.apigateway.Task
import ru.samokat.mysamokat.tests.dataproviders.helpers.database.employee_profiles_backend.Profile
import java.util.*

@Service
class ApigatewayDatabaseController (
    private val apigatewayDatabase: Database
        )
{


    fun getTaskByAssignmentIdAndType(assignmentId: UUID, type: String): ResultRow {
        var task: ResultRow? = null
        transaction(apigatewayDatabase) {
            addLogger(StdOutSqlLogger)
            task = Task.select { (Task.correlationId eq assignmentId.toString()) and (Task.type eq type)}.single()
        }
        return task!!
    }

    fun deleteAssignmentTaskById(assignmentId: String) {
        transaction(apigatewayDatabase) {
            addLogger(StdOutSqlLogger)
            Task.deleteWhere { Task.correlationId eq assignmentId }
        }
    }
}