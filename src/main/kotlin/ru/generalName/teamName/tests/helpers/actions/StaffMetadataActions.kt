package ru.samokat.mysamokat.tests.helpers.actions

import io.qameta.allure.Step
import org.jetbrains.exposed.sql.ResultRow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import ru.samokat.mysamokat.tests.dataproviders.staffMetadata.SearchUsersRequestBuilder
import ru.samokat.mysamokat.tests.helpers.controllers.asClientError
import ru.samokat.mysamokat.tests.helpers.controllers.asSuccess
import ru.samokat.mysamokat.tests.helpers.controllers.database.StaffMetadataDatabaseController
import ru.samokat.mysamokat.tests.helpers.controllers.staffmetadata.StaffMetadataController
import ru.samokat.staffmetadata.api.users.search.SearchUsersMetadataError
import ru.samokat.staffmetadata.api.users.search.UsersMetadataView
import ru.samokat.staffmetadata.api.users.store.StoreUserMetadataError
import ru.samokat.staffmetadata.api.users.store.StoreUserMetadataRequest
import java.util.*

@Component
@Scope("prototype")
class StaffMetadataActions {

    @Autowired
    lateinit var staffMetadataController: StaffMetadataController

    @Autowired
    private lateinit var databaseController: StaffMetadataDatabaseController

    @Step("Send new comment")
    fun sendComment(request: StoreUserMetadataRequest){
        staffMetadataController.sendComment(request)!!.asSuccess()
    }

    @Step("Send comment with error")
    fun sendCommentWithError(request: StoreUserMetadataRequest): StoreUserMetadataError {
        return staffMetadataController.sendComment(request)!!.asClientError()
    }

    fun getCommentFromDB(userId: UUID): ResultRow {
        return databaseController.getMetadataById(userId)
    }

    fun deleteUserMetadataInDb(userId: UUID) {
        databaseController.deleteUserMetadataById(userId)
    }

    fun setMetadataById(userId: UUID, commentary: String) {
        databaseController.setMetadataById(userId, commentary)
    }

    fun setManyMetadataById(userId: MutableList<UUID>, commentary: MutableList<String>) {
        for (i in 0 until userId.count()) {
            databaseController.setMetadataById(userId[i], commentary[i])
        }
    }


    @Step("get users metadata by list of ids")
    fun getMetadataByListOfIds(getBuilder: SearchUsersRequestBuilder): UsersMetadataView {
        return staffMetadataController.searchMetadata(getBuilder.build())!!.asSuccess()!!
    }

    @Step("get comment in response by uuid")
    fun getResponseCommentById(getBuilder: SearchUsersRequestBuilder, userId: UUID): String? {
        val response = getMetadataByListOfIds(getBuilder)
        return response.metadata.getValue(userId).commentary
    }

    @Step("get users metadata by list of ids")
    fun getMetadataByListOfIdsWithError(getBuilder: SearchUsersRequestBuilder): SearchUsersMetadataError {
        return staffMetadataController.searchMetadata(getBuilder.build())!!.asClientError()
    }

}