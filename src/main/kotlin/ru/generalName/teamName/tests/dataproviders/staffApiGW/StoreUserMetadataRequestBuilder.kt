package ru.samokat.mysamokat.tests.dataproviders.staffApiGW

import ru.samokat.mysamokat.tests.dataproviders.dataClasses.staffApiGTW.users.StoreUserMetadataRequest


class StoreUserMetadataRequestBuilder {

    private var comment: String? = null
    fun comment(comment: String?) = apply {this.comment = comment}
    fun getComment(): String? {
        return comment
    }

    fun build(): StoreUserMetadataRequest {
        return StoreUserMetadataRequest(
            comment = comment
        )
    }
}