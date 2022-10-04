package ru.samokat.mysamokat.tests.dataproviders.employee

import ru.samokat.employeeprofiles.api.darkstoreusers.violations.domain.ViolationCode
import ru.samokat.employeeprofiles.api.darkstoreusers.violations.store.StoreDarkstoreUserViolationRequest
import ru.samokat.my.rest.api.enum.ApiEnum
import java.util.*

class StoreDarkstoreUserViolationRequestBuilder {

    private lateinit var violationCode: ViolationCode
    fun violationCode(violationCode: ViolationCode) = apply {this.violationCode = violationCode}
    fun getViolationCode(): ViolationCode {
        return violationCode
    }

    private var violationComment: String? = null
    fun violationComment(violationComment: String?) = apply {this.violationComment = violationComment}
    fun getViolationComment(): String {
        return violationComment!!
    }

    private lateinit var issuerProfileId: UUID
    fun issuerProfileId(issuerProfileId: UUID) = apply {this.issuerProfileId = issuerProfileId}
    fun getIssuerProfileId(): UUID {
        return issuerProfileId
    }

    fun build(): StoreDarkstoreUserViolationRequest {
        return StoreDarkstoreUserViolationRequest(
            violationCode = ApiEnum(violationCode),
            violationComment = violationComment,
            issuerProfileId = issuerProfileId
        )
    }

}