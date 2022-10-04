package ru.samokat.mysamokat.tests.dataproviders.preconditions

import io.qameta.allure.Step
import org.springframework.stereotype.Service
import ru.samokat.my.domain.Email
import ru.samokat.my.domain.PhoneNumber
import ru.samokat.my.domain.employee.EmployeeName
import ru.samokat.my.domain.employee.EmployeeRole
import ru.samokat.my.rest.api.enum.ApiEnum

import ru.samokat.mysamokat.tests.dataproviders.Constants
import ru.samokat.mysamokat.tests.dataproviders.StringAndPhoneNumberGenerator
import ru.samokat.mysamokat.tests.dataproviders.apiModels.staffcave.internships.CreateInternshipRequest
import ru.samokat.mysamokat.tests.dataproviders.apiModels.staffcave.internships.UpdateInternshipRequest
import ru.samokat.mysamokat.tests.dataproviders.apiModels.staffcave.oauth.GetOAuthTokenRequest
import ru.samokat.mysamokat.tests.dataproviders.apiModels.staffcave.oauth.RefreshAccessTokenRequest
import ru.samokat.mysamokat.tests.dataproviders.apiModels.staffcave.requisitions.SearchUserRequisitionsRequest
import ru.samokat.mysamokat.tests.dataproviders.apiModels.staffcave.requisitions.UserRequisitionStatus
import ru.samokat.mysamokat.tests.dataproviders.apiModels.staffcave.requisitions.UserRequisitionType
import ru.samokat.mysamokat.tests.dataproviders.apiModels.staffcave.staffpartners.CreatePartnerRequest
import ru.samokat.mysamokat.tests.dataproviders.apiModels.staffcave.staffpartners.StaffPartnerType
import ru.samokat.mysamokat.tests.dataproviders.apiModels.staffcave.users.CreateUserRequest
import ru.samokat.mysamokat.tests.dataproviders.apiModels.staffcave.users.UpdateUserRequest
import ru.samokat.mysamokat.tests.dataproviders.apiModels.staffcave.users.Vehicle
import ru.samokat.mysamokat.tests.dataproviders.msmktApiGW.RefreshAccessTokenRequestBuilder
import java.nio.file.Files
import java.nio.file.Path
import java.time.Instant
import java.util.*

@Service
class StaffCaveApiGWPreconditions {

    @Step("Fill auth request")
    fun fillAuthRequest(
        mobile: PhoneNumber = Constants.mobile1,
        password: CharArray,
        deviceId: UUID = UUID.randomUUID()
    ): GetOAuthTokenRequest {
        return GetOAuthTokenRequest(
            mobile = mobile,
            password = password,
            otp = null
        )
    }

    @Step("Fill auth request (contract)")
    fun fillAuthRequestContract(
        mobile: PhoneNumber = Constants.mobile1,
        password: String
    ): String {
        return Files
            .readString(
                Path
                    .of("src/test/kotlin/ru/samokat/mysamokat/tests/dataproviders/resources/GetOAuthTokenRequest.json")
            )
            .replace("{mobile}", mobile.asStringWithoutPlus())
            .replace("{password}", password)
    }

    @Step("Fill refresh token request")
    fun fillRefreshTokenRequest(refreshToken: String): RefreshAccessTokenRequest {
        return RefreshAccessTokenRequestBuilder()
            .refreshToken(refreshToken)
            .build()
    }

    @Step("Fill create staff partner request")
    fun fillCreateStaffPartnerRequest(
        title: String,
        shortTitle: String,
        type: ApiEnum<StaffPartnerType, String>
    ): CreatePartnerRequest {
        return CreatePartnerRequest(title = title, shortTitle = shortTitle, type = type)
    }

    @Step("Fill create profile request")
    fun fillCreateUserRequest(
        mobile: PhoneNumber = Constants.mobile2,
        roles: List<ApiEnum<EmployeeRole, String>> = (listOf(ApiEnum(EmployeeRole.DELIVERYMAN))),
        name: EmployeeName = EmployeeName(
            StringAndPhoneNumberGenerator.generateRandomString(10),
            StringAndPhoneNumberGenerator.generateRandomString(10),
            StringAndPhoneNumberGenerator.generateRandomString(10)
        ),
        darkstoreId: UUID? = null,
        email: Email? = null,
        staffPartnerId: UUID? = null,
        supervisedDarkstores: MutableList<UUID>? = null,
        vehicle: Vehicle? = null,
        accountingProfileId: String? = null,
        requisitionId: UUID? = null
    ): CreateUserRequest {
        return CreateUserRequest(
            mobile = mobile,
            darkstoreId = darkstoreId,
            email = email,
            staffPartnerId = staffPartnerId,
            roles = roles,
            vehicle = vehicle,
            name = name,
            supervisedDarkstores = supervisedDarkstores,
            accountingProfileId = accountingProfileId,
            requisitionId = requisitionId
        )
    }

    @Step("Fill update user request")
    fun fillUpdateUserRequest(
        createUserRequest: CreateUserRequest,
        mobile: PhoneNumber = createUserRequest.mobile,
        email: Email? = createUserRequest.email,
        name: EmployeeName = createUserRequest.name,
        roles: List<ApiEnum<EmployeeRole, String>> = createUserRequest.roles,
        vehicle: Vehicle? = createUserRequest.vehicle,
        darkstoreId: UUID? = createUserRequest.darkstoreId,
        supervisedDarkstores: List<UUID>? = createUserRequest.supervisedDarkstores,
        staffPartnerId: UUID? = createUserRequest.staffPartnerId,
        version: Long = 1,
        accountingProfileId : String ? = createUserRequest.accountingProfileId
    ): UpdateUserRequest {
        return UpdateUserRequest(
            mobile = mobile,
            email = email,
            name = name,
            roles = roles,
            vehicle = vehicle,
            darkstoreId = darkstoreId,
            supervisedDarkstores = supervisedDarkstores,
            staffPartnerId = staffPartnerId,
            version = version,
            accountingProfileId = accountingProfileId
        )
    }

    @Step("Fill search requisitions request")
    fun fillSearchRequisitionsRequest(
        mobile: String? = null,
        name: String? = null,
        statuses: List<ApiEnum<UserRequisitionStatus, String>>? = null,
        types: List<ApiEnum<UserRequisitionType, String>>? = null,
        pageSize: Int? = null,
        pageMark: String? = null
    ): SearchUserRequisitionsRequest{
        return SearchUserRequisitionsRequest(
            mobile = mobile,
            name = name,
            statuses = statuses,
            types = types,
            pageSize = pageSize,
            pageMark = pageMark
        )
    }

    @Step("Fill create internship request")
    fun fillCreateInternshipRequest(
        darkstoreId: UUID,
        plannedDate: Instant
    ): CreateInternshipRequest {
        return CreateInternshipRequest(
            darkstoreId = darkstoreId,
            plannedDate = plannedDate
        )
    }

    @Step("Fill update internship request")
    fun fillUpdateInternshipRequest(
        darkstoreId: UUID,
        plannedDate: Instant,
        version: Long = 1L
    ): UpdateInternshipRequest {
        return UpdateInternshipRequest(
            darkstoreId = darkstoreId,
            plannedDate = plannedDate,
            version = version
        )
    }
}












