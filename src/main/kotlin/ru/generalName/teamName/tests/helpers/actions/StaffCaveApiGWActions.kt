package ru.samokat.mysamokat.tests.helpers.actions

import io.qameta.allure.Step
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import ru.samokat.mysamokat.tests.dataproviders.ErrorView
import ru.samokat.mysamokat.tests.dataproviders.apiModels.staffcave.DarkstoreListView
import ru.samokat.mysamokat.tests.dataproviders.apiModels.staffcave.internships.CreateInternshipRequest
import ru.samokat.mysamokat.tests.dataproviders.apiModels.staffcave.internships.InternshipsView
import ru.samokat.mysamokat.tests.dataproviders.apiModels.staffcave.internships.UpdateInternshipRequest
import ru.samokat.mysamokat.tests.dataproviders.apiModels.staffcave.oauth.GetOAuthTokenRequest
import ru.samokat.mysamokat.tests.dataproviders.apiModels.staffcave.oauth.OAuthTokenView
import ru.samokat.mysamokat.tests.dataproviders.apiModels.staffcave.oauth.RefreshAccessTokenRequest
import ru.samokat.mysamokat.tests.dataproviders.apiModels.staffcave.requisitions.GetUserRequisitionView
import ru.samokat.mysamokat.tests.dataproviders.apiModels.staffcave.requisitions.SearchUserRequisitionsRequest
import ru.samokat.mysamokat.tests.dataproviders.apiModels.staffcave.requisitions.SearchUserRequisitionsView
import ru.samokat.mysamokat.tests.dataproviders.apiModels.staffcave.staffpartners.CreatePartnerError
import ru.samokat.mysamokat.tests.dataproviders.apiModels.staffcave.staffpartners.CreatePartnerRequest
import ru.samokat.mysamokat.tests.dataproviders.apiModels.staffcave.staffpartners.StaffPartnersListView
import ru.samokat.mysamokat.tests.dataproviders.apiModels.staffcave.users.*
import ru.samokat.mysamokat.tests.helpers.controllers.staffcave_apigw.*

import java.util.*


@Component
@Scope("prototype")
class StaffCaveApiGWActions {

    @Autowired
    private lateinit var authController: SCAuthController

    @Autowired
    private lateinit var darkstoresController: DarkstoresApiController

    @Autowired
    lateinit var staffPartnersController: StaffPartnersApiController

    @Autowired
    lateinit var usersController: UsersApiController

    @Autowired
    lateinit var requisitionApi: RequisitionApiController

    @Autowired
    lateinit var internshipApi: InternshipsApiController

    // auth
    @Step("Authenticate profile with password (contract)")
    fun authProfilePasswordContract(request: String): String {
        return authController.authenticateProfile(request)
    }

    @Step("Authenticate profile with password")
    fun authProfilePassword(request: GetOAuthTokenRequest): OAuthTokenView {
        return authController.authenticateProfile(request)
    }

    @Step("Authenticate profile with password with error")
    fun authProfilePasswordError(request: GetOAuthTokenRequest, expectedSC: Int): ErrorView? {
        return authController.authenticateProfileWithError(request, expectedSC)
    }

    @Step("Refresh token")
    fun refreshToken(request: RefreshAccessTokenRequest): OAuthTokenView? {
        return authController.refreshToken(request)
    }

    @Step("Delete token")
    fun deleteToken(accessToken: String, sc: Int) {
        authController.deleteToken(accessToken, sc)
    }

    // darkstores
    @Step("Get darkstores list")
    fun getDarkStoresList(accessToken: String): DarkstoreListView? {
        return darkstoresController.getDarkStoresList(accessToken)
    }

    // staff-partners
    @Step("Get staff-partners list")
    fun getStaffPartnersList(accessToken: String): StaffPartnersListView? {
        return staffPartnersController.getStaffPartnersList(accessToken)
    }

    @Step("Create staff-partner")
    fun createStaffPartner(token: String, request: CreatePartnerRequest){
        staffPartnersController.createStaffPartnersList(token, request)
    }

    @Step("Create staff-partner")
    fun createStaffPartnerWithError(token: String, request: CreatePartnerRequest, sc: Int): CreatePartnerError? {
        return staffPartnersController.createStaffPartnerWithError(token, request, sc)
    }

    // users
    @Step("Create user")
    fun createUser(accessToken: String, request: CreateUserRequest): UserWithPasswordView? {
        return usersController.createUser(accessToken, request)
    }

    @Step("Create user")
    fun createUserError(accessToken: String, request: CreateUserRequest, sc: Int): ErrorView? {
        return usersController.createUserError(accessToken, request, sc)
    }

    @Step("Get user")
    fun getUserByProfileId(accessToken: String, userId: UUID): UserByIdView? {
        return usersController.getUserById(accessToken, userId)
    }


    @Step("Get user")
    fun getUserByProfileIdError(accessToken: String, userId: UUID) {
        usersController.getUserByIdError(accessToken, userId)
    }

    @Step("Update user")
    fun updateUser(accessToken: String, request: UpdateUserRequest, userId: UUID): UserView? {
        return usersController.updateUser(accessToken, request, userId)
    }

    @Step("Update user")
    fun updateUserWithError(accessToken: String, request: UpdateUserRequest, userId: UUID, sc: Int): ErrorView? {
        return usersController.updateUserWithError(accessToken, request, userId, sc)
    }

    @Step("Update user")
    fun updateUserWithErrorEmptyResult(accessToken: String, request: UpdateUserRequest, userId: UUID, sc: Int) {
        usersController.updateUserWithErrorEmptyResult(accessToken, request, userId, sc)
    }

    @Step("Delete user")
    fun deleteUser(accessToken: String, profileId: UUID) {
        usersController.deleteUser(accessToken, profileId)
    }

    @Step("Delete user")
    fun deleteUserWithErrorEmptyResult(accessToken: String, profileId: UUID) {
        usersController.deleteUserWithErrorEmptyResult(accessToken, profileId)
    }

    @Step("Update profile password")
    fun updateProfilePassword(token: String, userId: UUID): ChangedPasswordView? {
        return usersController.updatePassword(token, userId)
    }

    @Step("Update profile password")
    fun updateProfilePasswordWithError(token: String, userId: UUID){
        usersController.updatePasswordWithError(token, userId)
    }

    @Step("Update profile password")
    fun updateProfilePasswordWithErrorAndMessage(token: String, userId: UUID, sc: Int): ErrorView? {
        return usersController.updatePasswordWithErrorAndMessage(token, userId, sc)
    }

    @Step("Search profiles")
    fun searchProfilesByMobile(token: String, mobile: String): UserListView? {
        return usersController.searchProfilesByMobile(token, mobile)
    }

    @Step("Search profiles")
    fun searchProfilesByMobileWithPageSize(token: String, mobile: String, pageSize: Int): UserListView? {
        return usersController.searchProfilesByMobileWithPageSize(token, mobile, pageSize.toString())
    }

    @Step("Search profiles")
    fun searchProfilesByMobileWithPageSizeAndMark(token: String, mobile: String, pageSize: Int, pageMark: String): UserListView? {
        return usersController.searchProfilesByMobileWithPageSizeAndMark(token, mobile, pageSize.toString(), pageMark)
    }

    @Step("Search profiles without parameters")
    fun searchProfilesWithoutParameters(token: String): UserListView? {
        return usersController.searchProfilesWithoutParams(token)
    }

    @Step("Search profiles")
    fun searchProfilesByName(token: String, name: String): UserListView? {
        return usersController.searchProfilesByName(token, name)
    }

    @Step("Search profiles")
    fun searchProfilesByMobileAndName(token: String, mobile: String, name: String): UserListView? {
        return usersController.searchProfilesByMobileAndName(token, mobile, name)
    }

    // requisition api
    @Step("Get requisition by id")
    fun getRequisitionById(token: String, requsitionId: UUID): GetUserRequisitionView? {
        return requisitionApi.getRequisitionById(token, requsitionId)
    }

    @Step("Get requisition by id")
    fun getRequisitionByIdWithError(token: String, requsitionId: UUID, sc: Int) {
        requisitionApi.getRequisitionByIdWithError(token, requsitionId, sc)
    }

    @Step("Decline requisitions")
    fun declineRequisitions(token: String, requsitionId: UUID, sc: Int){
        requisitionApi.declineRequisition(token, requsitionId, sc)
    }

    @Step("Decline requisitions")
    fun declineRequisitionWithError(token: String, requsitionId: UUID, sc: Int): ErrorView{
        return requisitionApi.declineRequisitionWithError(token, requsitionId, sc)
    }

    @Step("Search requisitions")
    fun searchRequisitions(token: String, request: SearchUserRequisitionsRequest): SearchUserRequisitionsView? {
        return requisitionApi.searchRequisitions(token, request)
    }

    @Step("Create internship")
    fun createInternship(token: String, request: CreateInternshipRequest, userId: UUID, role: String){
        internshipApi.createInternship(token, request, userId, role)
    }

    @Step("Create internship")
    fun createInternshipWithError(token: String, request: CreateInternshipRequest, userId: UUID, role: String, sc: Int): ErrorView? {
        return internshipApi.createInternshipWithError(token, request, userId, role, sc)
    }

    @Step("Get internship by id")
    fun getInternshipByUserId(token: String, userId: UUID): InternshipsView {
        return internshipApi.getInternshipByUserId(token, userId)
    }

    @Step("Get internship by id")
    fun getInternshipByUserIdWithError(token: String, userId: UUID, sc: Int): ErrorView {
        return internshipApi.getInternshipByUserIdWithError(token, userId, sc)
    }


    @Step("Create internship")
    fun updateInternship(token: String, request: UpdateInternshipRequest, userId: UUID, role: String){
        internshipApi.updateInternship(token, request, userId, role)
    }

    @Step("Create internship")
    fun updateInternshipWithError(token: String, request: UpdateInternshipRequest, userId: UUID, role: String, sc: Int): ErrorView? {
        return internshipApi.updateInternshipWithError(token, request, userId, role, sc)
    }

}