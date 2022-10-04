package ru.samokat.mysamokat.tests.checkers

import io.qameta.allure.Step
import org.assertj.core.api.SoftAssertions
import org.springframework.stereotype.Service
import ru.samokat.employeeprofiles.api.common.domain.StaffPartnerView
import ru.samokat.my.domain.employee.EmployeeRole
import ru.samokat.my.rest.api.enum.ApiEnum
import ru.samokat.mysamokat.tests.dataproviders.apiModels.staffcave.internships.CreateInternshipRequest
import ru.samokat.mysamokat.tests.dataproviders.apiModels.staffcave.internships.InternshipStatus
import ru.samokat.mysamokat.tests.dataproviders.apiModels.staffcave.internships.InternshipsView
import ru.samokat.mysamokat.tests.dataproviders.apiModels.staffcave.internships.UpdateInternshipRequest
import ru.samokat.mysamokat.tests.dataproviders.apiModels.staffcave.oauth.OAuthTokenView
import ru.samokat.mysamokat.tests.dataproviders.apiModels.staffcave.requisitions.GetUserRequisitionView
import ru.samokat.mysamokat.tests.dataproviders.apiModels.staffcave.requisitions.SearchUserRequisitionsView
import ru.samokat.mysamokat.tests.dataproviders.apiModels.staffcave.requisitions.UserRequisitionStatus
import ru.samokat.mysamokat.tests.dataproviders.apiModels.staffcave.staffpartners.CreatePartnerRequest
import ru.samokat.mysamokat.tests.dataproviders.apiModels.staffcave.users.*
import ru.samokat.mysamokat.tests.helpers.controllers.events.VneshnieSotrudniki
import ru.samokat.mysamokat.tests.helpers.controllers.events.employee.PriemNaRabotuCFZ
import java.nio.file.Files
import java.nio.file.Path
import java.util.*


@Service
class StaffCaveApiGWAssertions {

    private val softAssertion = SoftAssertions()
    fun getSoftAssertion(): SoftAssertions {
        return softAssertion
    }

    fun assertAll() {
        getSoftAssertion().assertAll()
    }

    // authorization
    @Step("check oauth tokens contract")
    fun checkOauthTokensContract(tokens: String) {

        val expected = Files
            .readString(
                Path
                    .of("src/test/kotlin/ru/samokat/mysamokat/tests/dataproviders/resources/GetOAuthTokenResponse.json")
            )
            .replace("{authToken}", tokens.substringAfter("accessToken\":\"").substringBefore("\",\"refreshToken\""))
            .replace("{refreshToken}", tokens.substringAfter("refreshToken\":\"").substringBefore("\"}"))

        getSoftAssertion().assertThat(tokens).isEqualTo(expected)
    }

    @Step("check oauth tokens exist")
    fun checkOauthTokensExists(tokens: OAuthTokenView) {
        getSoftAssertion().assertThat(tokens.accessToken).isNotNull
        getSoftAssertion().assertThat(tokens.refreshToken).isNotNull
    }


    // staff-partners
    @Step("check staffpartner present in list")
    fun checkStaffPartnerInList(createStaffPartnerRequest: CreatePartnerRequest, partners: List<StaffPartnerView>) {
        val partner = partners.filter { it.title == createStaffPartnerRequest.title }
        getSoftAssertion().assertThat(partner[0].shortTitle).isEqualTo(createStaffPartnerRequest.shortTitle)
        getSoftAssertion().assertThat(partner[0].title).isEqualTo(createStaffPartnerRequest.title)
    }

    // user
    @Step("check user data")
    fun checkUserData(
        user: UserByIdView,
        createUserRequest: CreateUserRequest,
        version: Int = 1
    ): StaffCaveApiGWAssertions {
        val createdRoles = createUserRequest.roles.map { it.value }

        getSoftAssertion().assertThat(user.mobile).isEqualTo(createUserRequest.mobile)
        getSoftAssertion().assertThat(user.name).isEqualTo(createUserRequest.name)
        getSoftAssertion().assertThat(user.roles).isEqualTo(createUserRequest.roles)
        getSoftAssertion().assertThat(user.version).isEqualTo(version)

        when {
            createdRoles.contains(EmployeeRole.DELIVERYMAN.value) -> {
                getSoftAssertion().assertThat(user.darkstore!!.darkstoreId).isEqualTo(createUserRequest.darkstoreId)
                getSoftAssertion().assertThat(user.staffPartner!!.id).isEqualTo(createUserRequest.staffPartnerId)
                getSoftAssertion().assertThat(user.vehicle!!.type.value)
                    .isEqualTo(createUserRequest.vehicle!!.type.value)
            }
            createdRoles.contains(EmployeeRole.PICKER.value) -> {
                getSoftAssertion().assertThat(user.darkstore!!.darkstoreId).isEqualTo(createUserRequest.darkstoreId)
                getSoftAssertion().assertThat(user.staffPartner!!.id).isEqualTo(createUserRequest.staffPartnerId)
            }
            createdRoles.contains(EmployeeRole.FORWARDER.value) -> {
                getSoftAssertion().assertThat(user.darkstore!!.darkstoreId).isEqualTo(createUserRequest.darkstoreId)
                getSoftAssertion().assertThat(user.vehicle!!.type.value)
                    .isEqualTo(createUserRequest.vehicle!!.type.value)
            }
            createdRoles.contains(EmployeeRole.DARKSTORE_ADMIN.value) -> {
                getSoftAssertion().assertThat(user.darkstore!!.darkstoreId).isEqualTo(createUserRequest.darkstoreId)
            }
            createdRoles.contains(EmployeeRole.GOODS_MANAGER.value) -> {
                getSoftAssertion().assertThat(user.darkstore!!.darkstoreId).isEqualTo(createUserRequest.darkstoreId)
            }
            createdRoles.contains(EmployeeRole.COUNTERPARTY.value) -> {
                getSoftAssertion().assertThat(user.email).isEqualTo(createUserRequest.email)
            }
            createdRoles.contains(EmployeeRole.COORDINATOR.value) -> {
                getSoftAssertion().assertThat(
                    (user.supervisedDarkstores!!.map { it.darkstoreId }).containsAll(
                        createUserRequest.supervisedDarkstores!!
                    )
                ).isTrue
            }

        }
        return this
    }

    @Step("check user signature")
    fun checkUserSignature(user: UserByIdView, canSign: Boolean): StaffCaveApiGWAssertions {
        getSoftAssertion().assertThat(user.signature!!.canSign).isEqualTo(canSign)
        return this
    }

    @Step("check password is new")
    fun checkPassIsNew(oldPass: String, newPass: String): StaffCaveApiGWAssertions {
        getSoftAssertion().assertThat(oldPass).isNotEqualTo(newPass)
        return this
    }


    @Step("check user data")
    fun checkUserData(
        user: UserByIdView,
        createUserRequest: UpdateUserRequest,
        version: Int = 1
    ): StaffCaveApiGWAssertions {
        val createdRoles = createUserRequest.roles.map { it.value }

        getSoftAssertion().assertThat(user.mobile).isEqualTo(createUserRequest.mobile)
        getSoftAssertion().assertThat(user.name).isEqualTo(createUserRequest.name)
        getSoftAssertion().assertThat(user.roles).isEqualTo(createUserRequest.roles)
        getSoftAssertion().assertThat(user.version).isEqualTo(version)

        when {
            createdRoles.contains(EmployeeRole.DELIVERYMAN.value) -> {
                getSoftAssertion().assertThat(user.darkstore!!.darkstoreId).isEqualTo(createUserRequest.darkstoreId)
                getSoftAssertion().assertThat(user.staffPartner!!.id).isEqualTo(createUserRequest.staffPartnerId)
                getSoftAssertion().assertThat(user.vehicle!!.type.value)
                    .isEqualTo(createUserRequest.vehicle!!.type.value)
            }
            createdRoles.contains(EmployeeRole.PICKER.value) -> {
                getSoftAssertion().assertThat(user.darkstore!!.darkstoreId).isEqualTo(createUserRequest.darkstoreId)
                getSoftAssertion().assertThat(user.staffPartner!!.id).isEqualTo(createUserRequest.staffPartnerId)
            }
            createdRoles.contains(EmployeeRole.FORWARDER.value) -> {
                getSoftAssertion().assertThat(user.darkstore!!.darkstoreId).isEqualTo(createUserRequest.darkstoreId)
                getSoftAssertion().assertThat(user.vehicle!!.type.value)
                    .isEqualTo(createUserRequest.vehicle!!.type.value)
            }
            createdRoles.contains(EmployeeRole.DARKSTORE_ADMIN.value) -> {
                getSoftAssertion().assertThat(user.darkstore!!.darkstoreId).isEqualTo(createUserRequest.darkstoreId)
            }
            createdRoles.contains(EmployeeRole.GOODS_MANAGER.value) -> {
                getSoftAssertion().assertThat(user.darkstore!!.darkstoreId).isEqualTo(createUserRequest.darkstoreId)
            }
            createdRoles.contains(EmployeeRole.COUNTERPARTY.value) -> {
                getSoftAssertion().assertThat(user.email).isEqualTo(createUserRequest.email)
            }
            createdRoles.contains(EmployeeRole.COORDINATOR.value) -> {
                getSoftAssertion().assertThat(
                    (user.supervisedDarkstores!!.map { it.darkstoreId }).containsAll(
                        createUserRequest.supervisedDarkstores!!
                    )
                ).isTrue
            }

        }
        return this
    }

    @Step("check accounting profile id")
    fun checkAccountingProfileId(user: UserByIdView, accountingProfileId: String): StaffCaveApiGWAssertions {
        getSoftAssertion().assertThat(user.accountingProfileId).isEqualTo(accountingProfileId)
        return this
    }

    @Step("check user data in search results list")
    fun checkUserDataInSearchResults(
        createUserRequest: CreateUserRequest,
        createdUser: UserWithPasswordView,
        searchResults: UserListView
    ): StaffCaveApiGWAssertions {

        val user = searchResults.users.filter { it.userId == createdUser.user.userId }[0]
        val createdRoles = createUserRequest.roles.map { it.value }

        getSoftAssertion().assertThat(user.mobile).isEqualTo(createUserRequest.mobile)
        getSoftAssertion().assertThat(user.name).isEqualTo(createUserRequest.name)
        getSoftAssertion().assertThat(user.roles).isEqualTo(createUserRequest.roles)

        when {
            createdRoles.contains(EmployeeRole.DELIVERYMAN.value) -> {
                getSoftAssertion().assertThat(user.darkstore!!.darkstoreId).isEqualTo(createUserRequest.darkstoreId)
                getSoftAssertion().assertThat(user.staffPartner!!.id).isEqualTo(createUserRequest.staffPartnerId)
                getSoftAssertion().assertThat(user.vehicle!!.type.value)
                    .isEqualTo(createUserRequest.vehicle!!.type.value)
            }
            createdRoles.contains(EmployeeRole.PICKER.value) -> {
                getSoftAssertion().assertThat(user.darkstore!!.darkstoreId).isEqualTo(createUserRequest.darkstoreId)
                getSoftAssertion().assertThat(user.staffPartner!!.id).isEqualTo(createUserRequest.staffPartnerId)
            }
            createdRoles.contains(EmployeeRole.FORWARDER.value) -> {
                getSoftAssertion().assertThat(user.darkstore!!.darkstoreId).isEqualTo(createUserRequest.darkstoreId)
                getSoftAssertion().assertThat(user.vehicle!!.type.value)
                    .isEqualTo(createUserRequest.vehicle!!.type.value)
            }
            createdRoles.contains(EmployeeRole.DARKSTORE_ADMIN.value) -> {
                getSoftAssertion().assertThat(user.darkstore!!.darkstoreId).isEqualTo(createUserRequest.darkstoreId)
            }
            createdRoles.contains(EmployeeRole.GOODS_MANAGER.value) -> {
                getSoftAssertion().assertThat(user.darkstore!!.darkstoreId).isEqualTo(createUserRequest.darkstoreId)
            }
            createdRoles.contains(EmployeeRole.COUNTERPARTY.value) -> {
                getSoftAssertion().assertThat(user.email).isEqualTo(createUserRequest.email)
            }
            createdRoles.contains(EmployeeRole.COORDINATOR.value) -> {
                getSoftAssertion().assertThat(
                    (user.supervisedDarkstores!!.map { it.darkstoreId }).containsAll(
                        createUserRequest.supervisedDarkstores!!
                    )
                ).isTrue
            }
        }

        return this
    }

    @Step("check user list count")
    fun checkUsersListCount(searchResults: UserListView, expectedCount: Int): StaffCaveApiGWAssertions {
        getSoftAssertion().assertThat(searchResults.users.count()).isEqualTo(expectedCount)
        return this
    }

    @Step("check user in list")
    fun checkUserPresentInList(
        searchResults: UserListView,
        createdUser: UserWithPasswordView
    ): StaffCaveApiGWAssertions {
        getSoftAssertion().assertThat(searchResults.users.filter { it.userId == createdUser.user.userId }.count())
            .isEqualTo(1)
        return this
    }

    @Step("check user in list")
    fun checkUserNotPresentInList(
        searchResults: UserListView,
        createdUser: UserWithPasswordView
    ): StaffCaveApiGWAssertions {
        getSoftAssertion().assertThat(searchResults.users.filter { it.userId == createdUser.user.userId }.count())
            .isEqualTo(0)
        return this
    }

    // requisitions

    @Step("check requisition")
    fun checkOutsourceRequisition(
        requisition: GetUserRequisitionView,
        event: VneshnieSotrudniki,
        status: ApiEnum<UserRequisitionStatus, String> = ApiEnum(
            UserRequisitionStatus.NEW
        )
    ): StaffCaveApiGWAssertions {
        getSoftAssertion().assertThat(requisition.accountingProfileId)
            .isEqualTo(event.payload[0].fizicheskoeLitso.guid.toString())
        getSoftAssertion().assertThat(requisition.metadata.fullName)
            .isEqualTo(event.payload[0].fizicheskoeLitso.naimenovanie)
        getSoftAssertion().assertThat(requisition.metadata.mobile).isEqualTo(event.payload[0].telefon)
        getSoftAssertion().assertThat(requisition.metadata.jobTitle).isEqualTo(event.payload[0].dolzhnost.naimenovanie)
        getSoftAssertion().assertThat(requisition.metadata.partner).isEqualTo(event.payload[0].partner.naimenovanie)
        getSoftAssertion().assertThat(requisition.status).isEqualTo(status)

        return this
    }

    @Step("check requisition")
    fun checkOutsourceRequisitionInList(
        requisitions: SearchUserRequisitionsView,
        requisitionId: UUID,
        event: VneshnieSotrudniki,
        status: ApiEnum<UserRequisitionStatus, String> = ApiEnum(
            UserRequisitionStatus.NEW
        )
    ): StaffCaveApiGWAssertions {

        val requisition = requisitions.requisitions.filter { it.requisitionId == requisitionId }[0]

        getSoftAssertion().assertThat(requisition.accountingProfileId)
            .isEqualTo(event.payload[0].fizicheskoeLitso.guid.toString())
        getSoftAssertion().assertThat(requisition.fullName).isEqualTo(event.payload[0].fizicheskoeLitso.naimenovanie)
        getSoftAssertion().assertThat(requisition.mobile).isEqualTo(event.payload[0].telefon)
        getSoftAssertion().assertThat(requisition.status).isEqualTo(status)

        return this
    }

    @Step("check requisition")
    fun checkInnersourceRequisition(
        requisition: GetUserRequisitionView,
        event: PriemNaRabotuCFZ,
        status: ApiEnum<UserRequisitionStatus, String> = ApiEnum(
            UserRequisitionStatus.NEW
        )
    ): StaffCaveApiGWAssertions {

        getSoftAssertion().assertThat(requisition.accountingProfileId)
            .isEqualTo(event.payload[0].fizicheskoeLitso.inn)
        getSoftAssertion().assertThat(requisition.metadata.fullName)
            .isEqualTo(event.payload[0].fizicheskoeLitso.naimenovanie)
        getSoftAssertion().assertThat(requisition.metadata.mobile).isEqualTo("")
        getSoftAssertion().assertThat(requisition.metadata.jobTitle).isEqualTo(event.payload[0].dolzhnost.naimenovanie)
        getSoftAssertion().assertThat(requisition.status).isEqualTo(status)

        return this
    }

    @Step("check requisition")
    fun checkInnersourceRequisitionInList(
        requisitions: SearchUserRequisitionsView,
        requisitionId: UUID,
        event: PriemNaRabotuCFZ,
        status: ApiEnum<UserRequisitionStatus, String> = ApiEnum(
            UserRequisitionStatus.NEW
        )
    ): StaffCaveApiGWAssertions {

        val requisition = requisitions.requisitions.filter { it.requisitionId == requisitionId }[0]

        getSoftAssertion().assertThat(requisition.accountingProfileId)
            .isEqualTo(event.payload[0].fizicheskoeLitso.inn)
        getSoftAssertion().assertThat(requisition.fullName).isEqualTo(event.payload[0].fizicheskoeLitso.naimenovanie)
        getSoftAssertion().assertThat(requisition.mobile).isEqualTo("")
        getSoftAssertion().assertThat(requisition.status).isEqualTo(status)

        return this
    }

    @Step("check requisition not present in list")
    fun checkRequisitionNotPresentInList(
        requisitions: SearchUserRequisitionsView,
        requisitionId: UUID
    ): StaffCaveApiGWAssertions {
        getSoftAssertion().assertThat(requisitions.requisitions.filter { it.requisitionId == requisitionId }.count())
            .isEqualTo(0)
        return this
    }

    @Step("check requisition present in list")
    fun checkRequisitionPresentInList(
        requisitions: SearchUserRequisitionsView,
        requisitionId: UUID
    ): StaffCaveApiGWAssertions {
        getSoftAssertion().assertThat(requisitions.requisitions.filter { it.requisitionId == requisitionId }.count())
            .isEqualTo(1)
        return this
    }

    @Step("check requisitions list count")
    fun checkRequisitionsListCount(
        searchResults: SearchUserRequisitionsView,
        expectedCount: Int
    ): StaffCaveApiGWAssertions {
        getSoftAssertion().assertThat(searchResults.requisitions.count()).isEqualTo(expectedCount)
        return this
    }

    // internships

    @Step("check user internship")
    fun checkUserInternship(
        internships: InternshipsView,
        createInternshipRequest: CreateInternshipRequest,
        role: EmployeeRole = EmployeeRole.DELIVERYMAN,
        status: InternshipStatus = InternshipStatus.PLANNED,
        isEditable: Boolean = true,
        version: Long = 1L
    ): StaffCaveApiGWAssertions {
        getSoftAssertion().assertThat(internships.internships[0].darkstore.darkstoreId)
            .isEqualTo(createInternshipRequest.darkstoreId)
        getSoftAssertion().assertThat(internships.internships[0].plannedDate)
            .isEqualTo(createInternshipRequest.plannedDate)
        getSoftAssertion().assertThat(internships.internships[0].role.value).isEqualTo(role.value)
        getSoftAssertion().assertThat(internships.internships[0].status.value).isEqualTo(status.value)
        getSoftAssertion().assertThat(internships.internships[0].isEditable).isEqualTo(isEditable)
        getSoftAssertion().assertThat(internships.internships[0].version).isEqualTo(version)
        return this
    }

    @Step("check user internship")
    fun checkUserInternship(
        internships: InternshipsView,
        createInternshipRequest: UpdateInternshipRequest,
        role: EmployeeRole = EmployeeRole.DELIVERYMAN,
        status: InternshipStatus = InternshipStatus.PLANNED,
        isEditable: Boolean = true,
        version: Long = 2L
    ): StaffCaveApiGWAssertions {
        getSoftAssertion().assertThat(internships.internships[0].darkstore.darkstoreId)
            .isEqualTo(createInternshipRequest.darkstoreId)
        getSoftAssertion().assertThat(internships.internships[0].plannedDate)
            .isEqualTo(createInternshipRequest.plannedDate)
        getSoftAssertion().assertThat(internships.internships[0].role.value).isEqualTo(role.value)
        getSoftAssertion().assertThat(internships.internships[0].status.value).isEqualTo(status.value)
        getSoftAssertion().assertThat(internships.internships[0].isEditable).isEqualTo(isEditable)
        getSoftAssertion().assertThat(internships.internships[0].version).isEqualTo(version)
        return this
    }

    @Step("Check internship count")
    fun checkInternshipsCount(internships: InternshipsView, expectedCount: Int): StaffCaveApiGWAssertions {
        getSoftAssertion().assertThat(internships.internships.count()).isEqualTo(expectedCount)
        return this
    }

}