package ru.samokat.mysamokat.tests.dataproviders.staffMetadata
import ru.samokat.staffmetadata.api.users.store.StoreUserMetadataRequest
import java.util.*

class CreateCommentRequestBuilder {

    private var commentary: String? = null
    fun commentary(commentary: String?) = apply {this.commentary = commentary}
    fun getCommentary(): String? {
        return commentary
    }

    private lateinit var userId: UUID
    fun userId(userId: UUID) = apply {this.userId = userId}
    fun getUserId(): UUID {
        return userId
    }

    fun build(): StoreUserMetadataRequest {
        return StoreUserMetadataRequest(
            userId = userId,
            commentary = commentary
            )
    }
}