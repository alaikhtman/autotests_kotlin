package ru.samokat.mysamokat.tests.dataproviders.employee
import ru.samokat.employeeprofiles.api.darkstoreusers.domain.DarkstoreUserState
import ru.samokat.employeeprofiles.api.darkstoreusers.update.UpdateDarkstoreUserRequest
import ru.samokat.my.rest.api.enum.ApiEnum
import java.util.*

class UpdateDSUserStatusBuilder {

    private lateinit var issuerProfileId: UUID
    fun issuerProfileId(issuerProfileId: UUID) = apply { this.issuerProfileId = issuerProfileId }
    fun getIssuerProfileId(): UUID {
        return issuerProfileId
    }

    fun randomIssuerProfileId() = apply { issuerProfileId = UUID.randomUUID() }


    private lateinit var state:
            ApiEnum<DarkstoreUserState, String>

    fun state(
        state:
        ApiEnum<DarkstoreUserState, String>
    ) = apply { this.state = state }

    fun getState():
            ApiEnum<DarkstoreUserState, String> {
        return state
    }


    private var version: Long = 1
    fun version(version: Long) = apply { this.version = version }
    fun getVersion(): Long {
        return version
    }

    private var inactivityReason: String? = null
    fun inactivityReason(inactivityReason: String?) =
        apply { this.inactivityReason = inactivityReason }

    fun inactivityReason(): String? {
        return inactivityReason
    }

    fun build(): UpdateDarkstoreUserRequest {
        return UpdateDarkstoreUserRequest(
            issuerProfileId = issuerProfileId,
            state = state,
            version = version,
            inactivityReason = inactivityReason
        )
    }
}