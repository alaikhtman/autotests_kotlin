package ru.samokat.mysamokat.tests.helpers.controllers.staffmetadata

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.samokat.my.rest.client.RestResult
import ru.samokat.mysamokat.tests.dataproviders.staffMetadata.SearchUsersRequestBuilder
import ru.samokat.mysamokat.tests.helpers.controllers.database.StaffMetadataDatabaseController
import ru.samokat.staffmetadata.api.users.search.SearchUsersMetadataError
import ru.samokat.staffmetadata.api.users.search.SearchUsersMetadataRequest
import ru.samokat.staffmetadata.api.users.search.UsersMetadataView
import ru.samokat.staffmetadata.api.users.store.StoreUserMetadataError
import ru.samokat.staffmetadata.api.users.store.StoreUserMetadataRequest
import ru.samokat.staffmetadata.client.UsersMetadataClient

@Service
class StaffMetadataController {

    @Autowired
    lateinit var staffMetadataFeign : UsersMetadataClient //берем из кода сервиса

    @Autowired
    private lateinit var databaseController: StaffMetadataDatabaseController

    fun sendComment(request: StoreUserMetadataRequest): RestResult<Unit, StoreUserMetadataError>? {
        return try {
            staffMetadataFeign.storeMetadata(request).get()
        } catch (e: InterruptedException) {
            e.printStackTrace()
            null
        }
    }

    fun searchMetadata(request: SearchUsersMetadataRequest): RestResult<UsersMetadataView, SearchUsersMetadataError>? {
        return try { staffMetadataFeign.searchMetadata(request).get()
        } catch (e: InterruptedException) {
            e.printStackTrace()
            null
        }
    }
}