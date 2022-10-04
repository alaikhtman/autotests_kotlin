package ru.samokat.mysamokat.tests.helpers.actions

import io.qameta.allure.Step
import org.awaitility.Awaitility
import org.awaitility.kotlin.atMost
import org.awaitility.kotlin.withPollInterval
import org.eclipse.jetty.http.HttpStatus
import org.jetbrains.exposed.sql.ResultRow
import org.junit.jupiter.api.Assertions
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import ru.samokat.employeeprofiles.api.common.domain.EmployeeProfileView
import ru.samokat.employeeprofiles.api.common.domain.StaffPartnerView
import ru.samokat.employeeprofiles.api.contacts.EmployeeProfileContactView
import ru.samokat.employeeprofiles.api.contracts.search.*
import ru.samokat.employeeprofiles.api.darkstoreusers.domain.DarkstoreUserRole
import ru.samokat.employeeprofiles.api.darkstoreusers.domain.DarkstoreUserView
import ru.samokat.employeeprofiles.api.darkstoreusers.getbyprofileids.GetDarkstoreUsersByProfileIdsRequest
import ru.samokat.employeeprofiles.api.darkstoreusers.internships.cancel.CancelInternshipError
import ru.samokat.employeeprofiles.api.darkstoreusers.internships.cancel.CancelInternshipRequest
import ru.samokat.employeeprofiles.api.darkstoreusers.internships.close.CloseInternshipError
import ru.samokat.employeeprofiles.api.darkstoreusers.internships.close.CloseInternshipRequest
import ru.samokat.employeeprofiles.api.darkstoreusers.internships.create.CreateInternshipError
import ru.samokat.employeeprofiles.api.darkstoreusers.internships.create.CreateInternshipRequest
import ru.samokat.employeeprofiles.api.darkstoreusers.internships.get.GetDarkstoreInternshipsRequest
import ru.samokat.employeeprofiles.api.darkstoreusers.internships.get.InternshipsView
import ru.samokat.employeeprofiles.api.darkstoreusers.internships.reject.RejectInternshipError
import ru.samokat.employeeprofiles.api.darkstoreusers.internships.reject.RejectInternshipRequest
import ru.samokat.employeeprofiles.api.darkstoreusers.internships.update.UpdateInternshipError
import ru.samokat.employeeprofiles.api.darkstoreusers.internships.update.UpdateInternshipRequest
import ru.samokat.employeeprofiles.api.darkstoreusers.search.SearchDarkstoreUsersRequest
import ru.samokat.employeeprofiles.api.darkstoreusers.search.SearchDarkstoreUsersView
import ru.samokat.employeeprofiles.api.darkstoreusers.update.UpdateDarkstoreUserError
import ru.samokat.employeeprofiles.api.darkstoreusers.update.UpdateDarkstoreUserRequest
import ru.samokat.employeeprofiles.api.darkstoreusers.violations.dictionary.get.ViolationDictionaryView
import ru.samokat.employeeprofiles.api.darkstoreusers.violations.get.DarkstoreUserViolationView
import ru.samokat.employeeprofiles.api.darkstoreusers.violations.get.GetDarkstoreUserViolationsError
import ru.samokat.employeeprofiles.api.darkstoreusers.violations.search.ExtendedDarkstoreUserViolationView
import ru.samokat.employeeprofiles.api.darkstoreusers.violations.search.SearchDarkstoreUsersViolationsRequest
import ru.samokat.employeeprofiles.api.darkstoreusers.violations.store.StoreDarkstoreUserViolationError
import ru.samokat.employeeprofiles.api.darkstoreusers.violations.store.StoreDarkstoreUserViolationRequest
import ru.samokat.employeeprofiles.api.darkstoreusers.violations.store.StoreDarkstoreUserViolationView
import ru.samokat.employeeprofiles.api.profilerequisitions.decline.DeclineProfileRequisitionError
import ru.samokat.employeeprofiles.api.profilerequisitions.decline.DeclineProfileRequisitionRequest
import ru.samokat.employeeprofiles.api.profilerequisitions.get.GetProfileRequisitionError
import ru.samokat.employeeprofiles.api.profilerequisitions.get.GetProfileRequisitionView
import ru.samokat.employeeprofiles.api.profilerequisitions.search.SearchProfileRequisitionsRequest
import ru.samokat.employeeprofiles.api.profilerequisitions.search.SearchProfileRequisitionsView
import ru.samokat.employeeprofiles.api.profiles.authenticate.AuthenticateProfileRequest
import ru.samokat.employeeprofiles.api.profiles.changepassword.ChangePasswordError
import ru.samokat.employeeprofiles.api.profiles.changepassword.ChangedPasswordView
import ru.samokat.employeeprofiles.api.profiles.create.CreateProfileError
import ru.samokat.employeeprofiles.api.profiles.create.CreateProfileRequest
import ru.samokat.employeeprofiles.api.profiles.create.CreatedProfileView
import ru.samokat.employeeprofiles.api.profiles.getprofiles.GetProfilesRequest
import ru.samokat.employeeprofiles.api.profiles.getprofiles.GetProfilesView
import ru.samokat.employeeprofiles.api.profiles.update.UpdateProfileError
import ru.samokat.employeeprofiles.api.profiles.update.UpdateProfileRequest
import ru.samokat.employeeprofiles.api.signatures.getbyprofileid.EmployeeSignatureView
import ru.samokat.employeeprofiles.api.signatures.getbyprofileid.GetSignatureByProfileIdError
import ru.samokat.employeeprofiles.api.staffpartners.createpartners.CreatePartnerError
import ru.samokat.employeeprofiles.api.staffpartners.createpartners.CreatePartnerRequest
import ru.samokat.employeeprofiles.api.staffpartners.getpartners.GetPartnersRequest
import ru.samokat.logistics.tests.dataproviders.employee.GetProfilesByIdsRequestBuilder
import ru.samokat.my.domain.PhoneNumber
import ru.samokat.my.domain.employee.EmployeeRole
import ru.samokat.my.rest.client.RestResult
import ru.samokat.mysamokat.tests.SuiteBase.Companion.jacksonObjectMapper
import ru.samokat.mysamokat.tests.dataproviders.helpers.database.employee_profiles_backend.Contract
import ru.samokat.mysamokat.tests.dataproviders.helpers.database.employee_profiles_backend.ProfileRequisition
import ru.samokat.mysamokat.tests.dataproviders.helpers.database.employee_profiles_backend.Task
import ru.samokat.mysamokat.tests.helpers.controllers.KafkaController
import ru.samokat.mysamokat.tests.helpers.controllers.asClientError
import ru.samokat.mysamokat.tests.helpers.controllers.asSuccess
import ru.samokat.mysamokat.tests.helpers.controllers.database.EmployeeProfilesDatabaseController
import ru.samokat.mysamokat.tests.helpers.controllers.employeeprofiles.*
import ru.samokat.mysamokat.tests.helpers.controllers.events.DataVneshnieSotrudniki
import ru.samokat.mysamokat.tests.helpers.controllers.events.KadrovyyPerevodCFZ
import ru.samokat.mysamokat.tests.helpers.controllers.events.KadrovyyPerevodSpiskomCFZ
import ru.samokat.mysamokat.tests.helpers.controllers.events.VneshnieSotrudniki
import ru.samokat.mysamokat.tests.helpers.controllers.events.employee.*
import java.time.Duration
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*

@Component
@Scope("prototype")
class EmployeeActions(
    private val kafkaEmployeeLog: KafkaController,
    private val kafkaEmployeeCreated: KafkaController,
    private val kafkaEmployeeChanged: KafkaController,
    private val kafkaEmployeeDisabled: KafkaController,
    private val kafkaEmployeePassChanged: KafkaController,
    private val kafkaKadrovyyPerevodCFZ: KafkaController,
    private val kafkaKadrovyyPerevodSpiskomCFZ: KafkaController,
    private val kafkaPriemNaRabotuCFZ: KafkaController,
    private val kafkaPriemNaRabotuSpiskomCFZ: KafkaController,
    private val kafkaVneshnieSotrudniki: KafkaController,
    private val kafkaFirstLogin: KafkaController
) {

    @Autowired
    lateinit var employeeController: EmployeeProfileController

    @Autowired
    lateinit var contractsController: EmployeeContractsController

    @Autowired
    lateinit var darkstoreContactsController: EmployeeContactsController

    @Autowired
    lateinit var dsUserController: DarkstoreUserController

    @Autowired
    lateinit var dsViolationsController: DarkstoreUserViolationsController

    @Autowired
    lateinit var internshipController: InternshipController

    @Autowired
    lateinit var signatureController: EmployeeSignaturesController

    @Autowired
    lateinit var staffPartnersController: StaffPartnersController

    @Autowired
    lateinit var requisitionsController: ProfileRequisitionController

    @Autowired
    private lateinit var databaseController: EmployeeProfilesDatabaseController

    private lateinit var profileFromApi: EmployeeProfileView
    fun profileFromApi(profile: EmployeeProfileView) = apply { this.profileFromApi = profile }
    fun getProfileFromApi(): EmployeeProfileView {
        return profileFromApi
    }

    private lateinit var createProfile: RestResult<CreatedProfileView, CreateProfileError>
    fun createProfileResult(): RestResult<CreatedProfileView, CreateProfileError> {
        return createProfile
    }

    private lateinit var createdProfileResponse: CreatedProfileView
    fun createdProfileResponse(): CreatedProfileView {
        return createdProfileResponse

    }

    private lateinit var listCreatedProfileResponses: MutableList<CreatedProfileView>
    fun listCreatedProfileResponses(): MutableList<CreatedProfileView> {
        return listCreatedProfileResponses

    }


    private lateinit var createInternship: RestResult<Unit, CreateInternshipError>
    fun getCreateInternshipResponse(): RestResult<Unit, CreateInternshipError> {
        return createInternship
    }

    private lateinit var createdInternship: InternshipsView
    fun getCreatedInternship(): InternshipsView {
        return createdInternship
    }

    private lateinit var updatedInternship:
            RestResult<Unit, UpdateInternshipError>

    fun getUpdatedInternship():
            RestResult<Unit, UpdateInternshipError> {
        return updatedInternship
    }

    private lateinit var cancelledInternship:
            RestResult<Unit, CancelInternshipError>

    fun getCancelledInternship():
            RestResult<Unit, CancelInternshipError> {
        return cancelledInternship
    }

    private lateinit var rejectedInternship:
            RestResult<Unit, RejectInternshipError>

    fun getRejectedInternship():
            RestResult<Unit, RejectInternshipError> {
        return rejectedInternship
    }

    private lateinit var closedInternship:
            RestResult<Unit, CloseInternshipError>

    fun getClosedInternship():
            RestResult<Unit, CloseInternshipError> {
        return closedInternship
    }


    private lateinit var dsInternship: InternshipsView
    fun getCreatedDsInternship(): InternshipsView {
        return dsInternship
    }


    private lateinit var updateProfile: RestResult<Unit, UpdateProfileError>
    private lateinit var updateDSUserProfile: RestResult<Unit, UpdateDarkstoreUserError>

    private lateinit var changePasswordResponse: RestResult<ChangedPasswordView, ChangePasswordError>
    fun getChangePasswordResponse(): RestResult<ChangedPasswordView, ChangePasswordError> {
        return changePasswordResponse
    }

    private lateinit var conflictProfile: EmployeeProfileView
    fun getConflictProfile(): EmployeeProfileView {
        return conflictProfile
    }

    private lateinit var responseCreateProfile: RestResult<CreatedProfileView, CreateProfileError>
    fun getResponseCreateProfile(): RestResult<CreatedProfileView, CreateProfileError> {
        return responseCreateProfile
    }


    //DB rows
    private lateinit var profileFromDB: ResultRow
    private lateinit var internshipFromDB: ResultRow
    private lateinit var profileLogArray: MutableList<ResultRow>
    private lateinit var dsUserProfileFromDB: ResultRow
    private lateinit var dsUserActivityFromDB: ResultRow
    private lateinit var dsUserLogArray: MutableList<ResultRow>


    fun setProfile(profile: ResultRow): EmployeeActions {
        profileFromDB = profile
        return this
    }

    fun setInternship(internship: ResultRow): EmployeeActions {
        internshipFromDB = internship
        return this
    }


    fun getProfile(): ResultRow {
        return profileFromDB
    }

    fun getInternship(): ResultRow {
        return internshipFromDB
    }

    fun setDSUserProfile(dsUserProfile: ResultRow): EmployeeActions {
        dsUserProfileFromDB = dsUserProfile
        return this
    }

    fun setDSUserActivity(dsUserActivity: ResultRow): EmployeeActions {
        dsUserActivityFromDB = dsUserActivity
        return this
    }

    fun setProfileLogArray(profileLog: MutableList<ResultRow>): EmployeeActions {
        profileLogArray = profileLog
        return this
    }

    fun setDSUserLogArray(dsUserLog: MutableList<ResultRow>): EmployeeActions {
        dsUserLogArray = dsUserLog
        return this
    }

    fun getProfileFromDB(profileId: UUID): ResultRow {
        return databaseController.getProfile(profileId)
    }

    fun getProfilePasswordLogRowsCount(profileId: UUID, type: String): Int {
        return databaseController.checkProfilePasswordLogRowsCount(profileId, type)
    }

    fun getVehicleFromDatabase(profileId: UUID): String {
        val vehicle = databaseController.getProfileVehicle(profileId)
        return vehicle
    }

    fun getProfilesFromProfilesLogTable(profileId: UUID): MutableList<ResultRow> {
        val profiles = databaseController.getProfilesLogUsers(profileId)
        return profiles
    }

    fun getProfilesFromDSUserTable(profileId: UUID, role: EmployeeRole): ResultRow {
        val profile = databaseController.getDarkstoreUserByRole(profileId, role.toString())
        return profile
    }

    fun getProfilesFromDSUserLogTable(profileId: UUID, role: EmployeeRole): MutableList<ResultRow> {
        return databaseController.getDarkstoreUserLogsByRole(profileId, role.toString())
    }

    fun getProfilesFromDSActivityTable(profileId: UUID, role: EmployeeRole): ResultRow {
        val profile = databaseController.getDarkstoreUserActivityByRole(profileId, role.toString())
        return profile
    }

    fun getSupervisedDarkstoresFromDB(profileId: UUID): MutableList<UUID> {
        val darkstores = databaseController.getProfileSupervisedDarkstores(profileId)
        return darkstores
    }

    fun getContractFromDB(accountingProfileId: String): ResultRow {
        return databaseController.getContract(accountingProfileId)
    }

    fun getSeveralContractFromDB(accountingProfileId: String): List<ResultRow> {
        return databaseController.getAllContractsByAccountingProfileId(accountingProfileId)
    }

    fun getContractIdFromDB(accountingProfileId: String): String {
        return databaseController.getContract(accountingProfileId)[Contract.accountingContractId]
    }

    fun getActiveContractIdWithoutRetirementDateFromDB(accountingProfileId: String): MutableList<String> {
        val contracts: MutableList<ResultRow> =
            databaseController.getAllContractsByAccountingProfileId(accountingProfileId)
        val activeContracts: MutableList<String> = mutableListOf()
        for (i in 0 until contracts.count()) {
            val data = jacksonObjectMapper.readValue(contracts[i][Contract.data], DataVneshnieSotrudniki::class.java)
            if (data.retirementDate == null) {
                activeContracts.add(contracts[i][Contract.accountingContractId])
            }
        }
        return activeContracts
    }


    fun getContractFromDBByContractId(accountingContractId: String): ResultRow {
        return databaseController.getContractByContractId(accountingContractId)
    }

    fun getPartnerFromDB(partnerId: UUID): ResultRow {
        return databaseController.getStaffPartner(partnerId)
    }

    fun getTaskFromDB(accountingContractId: String): PriemNaRabotuCFZ? {
        val tasks = databaseController.getTask(
            "innerSourceEmployeeCreationEventRetry",
            1
        )

        tasks.forEach {
            val payload = jacksonObjectMapper.readValue(it[Task.payload], PriemNaRabotuCFZ::class.java)
            if (payload.payload[0].sotrudnik.guid.toString() == accountingContractId)
                return payload
        }
        return null
    }

    fun getPerevodTaskFromDB(
        accountingContractId: String,
        type: String = "innerSourceEmployeeTransferEventRetry",
        attempts: Int = 1
    ): KadrovyyPerevodCFZ? {
        val tasks = databaseController.getTask(type, attempts)

        tasks.forEach {
            val payload = jacksonObjectMapper.readValue(it[Task.payload], KadrovyyPerevodCFZ::class.java)
            if (payload.payload[0].sotrudnik!!.guid.toString() == accountingContractId)
                return payload
        }
        return null
    }

    fun getTaskFromDBByCorrelationId(correlationId: String): ResultRow {
        return databaseController.getTaskByCorrelationId(correlationId)
    }

    fun getTaskExistanceByCorrelationId(correlationId: String): Boolean {
        return databaseController.checkTaskExistsByCorrelationId(correlationId)
    }

    fun getProfileExistanceByMobile(mobile: String): Boolean {
        return databaseController.checkActiveProfileExistsByMobile(mobile)
    }


    fun getContractLogFromDB(accountingContractId: String): ResultRow {
        return databaseController.getContractLog(accountingContractId)
    }

    fun getContractLogFromDbByType(accountingContractId: String, type: String): ResultRow {
        return databaseController.getContractLogByType(accountingContractId, type)
    }

    fun getContractExistanse(accountingContractId: String): Boolean {
        return databaseController.checkAContractExistsById(accountingContractId)
    }

    // requisitions
    fun getRequisitionFromDBByAccountingProfileId(accountingProfileId: String): ResultRow? {
        return databaseController.getRequisitionByAccountingProfileId(accountingProfileId)
    }

    fun getRequisitionFromDBByAccountingProfileIdAndStatus(accountingProfileId: String, status: String): ResultRow? {
        return databaseController.getRequisitionByAccountingProfileIdAndStatus(accountingProfileId, status)
    }

    fun getRequisitionLog(requestId: UUID): ResultRow {
        return databaseController.getRequisitionLog(requestId)
    }

    fun getRequisitionLogByVersion(requestId: UUID, version: Int = 2): ResultRow {
        return databaseController.getRequisitionLogByVersion(requestId, version)
    }

    fun getRequisitionFromDBById(requisitionId: UUID): ResultRow? {
        return databaseController.getRequisitionById(requisitionId)
    }

    @Step("Clear requisition in database")
    fun deleteRequisitionFromDatabase(accountingProfileId: String) {
        val requisition = getRequisitionFromDBByAccountingProfileId(accountingProfileId)

        if (requisition != null) {
            databaseController.deleteRequisitionLogByRequestId(requisition[ProfileRequisition.requestId])
            databaseController.deleteRequisitionByRequestId(requisition[ProfileRequisition.requestId])
        }
    }

    @Step("Clear contract in database")
    fun deleteContractFromDatabase(accountingProfileId: String) {
        databaseController.deleteContractByRequestId(accountingProfileId)
    }

    @Step("Clear requisition in database by mobile")
    fun deleteRequisitionFromDatabaseByMobile(mobile: String) {
        val requistions = databaseController.getAllRequisitionsByMobile(mobile)

        requistions.forEach {
            databaseController.deleteRequisitionLogByRequestId(it[ProfileRequisition.requestId])
            databaseController.deleteRequisitionByRequestId(it[ProfileRequisition.requestId])
        }

    }

    @Step("Get requisition existance")
    fun getRequisitionExistance(accountingProfileId: String): Boolean {
        return databaseController.checkRequisitionExists(accountingProfileId)
    }

    @Step("Get requisitions count")
    fun getRequisitionsCount(accountingProfileId: String): Int {
        return databaseController.getRequisitionsCount(accountingProfileId)
    }

    @Step("decline profile requisition")
    fun declineProfileRequisition(requisitionId: UUID, request: DeclineProfileRequisitionRequest) {
        requisitionsController.deleteRequisition(requisitionId, request)
        Thread.sleep(5_000)
    }

    @Step("decline profile requisition")
    fun declineProfileRequisitionWithError(
        requisitionId: UUID,
        request: DeclineProfileRequisitionRequest
    ): DeclineProfileRequisitionError {
        return requisitionsController.deleteRequisition(requisitionId, request)!!.asClientError()
    }

    @Step("get requisition by id")
    fun getRequisitionById(requisitionId: UUID): GetProfileRequisitionView {
        return requisitionsController.getRequisitionById(requisitionId)!!.asSuccess()
    }

    @Step("get requisition by id")
    fun getRequisitionByIdWithError(requisitionId: UUID): GetProfileRequisitionError {
        return requisitionsController.getRequisitionById(requisitionId)!!.asClientError()
    }

    @Step("search requisitions")
    fun searchRequisitions(request: SearchProfileRequisitionsRequest): SearchProfileRequisitionsView {
        return requisitionsController.searchRequisitions(request)!!.asSuccess()
    }

    fun updateAccountingProfileId(profileId: UUID, accountingProfileId: String) {
        databaseController.updateAccountingProfileId(profileId, accountingProfileId)
    }

    //Create
    @Step("send create profile request success")
    fun createProfile(createProfileRequest: CreateProfileRequest): EmployeeActions {
        createProfile = employeeController.createProfile(createProfileRequest)!!
        createdProfileResponse = createProfile.asSuccess()
        return this
    }

    @Step("send several create profile request success")
    fun createSeveralProfiles(listCreateProfileRequest: MutableList<CreateProfileRequest>): EmployeeActions {
        val listCreatedProfile: MutableList<RestResult<CreatedProfileView, CreateProfileError>> = mutableListOf()
        listCreatedProfileResponses = mutableListOf()
        for (i in 0 until listCreateProfileRequest.count()) {
            listCreatedProfile.add(employeeController.createProfile(listCreateProfileRequest[i])!!)
            listCreatedProfileResponses.add(listCreatedProfile[i].asSuccess())
        }
        return this
    }


    @Step("send create profile request with error")
    fun createProfileWithError(createProfileRequest: CreateProfileRequest): EmployeeActions {
        responseCreateProfile = employeeController.createProfile(createProfileRequest)!!
        return this

    }

    @Step("send create profile request success")
    fun createProfileId(createProfileRequest: CreateProfileRequest): UUID {
        return employeeController.createProfile(createProfileRequest)!!.asSuccess().profileId
    }

    @Step("send create profile request with exist number")
    fun createProfileWithExistNumber(createRequest: CreateProfileRequest): EmployeeActions {
        responseCreateProfile = employeeController.createProfile(createRequest)!!
        conflictProfile = (responseCreateProfile.asClientError() as CreateProfileError.InconsistentRequest).conflicts[0]
        return this

    }

    @Step("send create profile request success (full result)")
    fun createProfileFullResult(createRequest: CreateProfileRequest): CreatedProfileView {
        return employeeController.createProfile(createRequest)!!.asSuccess()
    }

    //Get profile by id
    @Step("get profile by profileId via API")
    fun getProfileById(profileId: UUID): EmployeeActions {
        profileFromApi = employeeController.getProfileById(profileId)?.asSuccess()!!
        return this
    }

    @Step("get profile by profileId via API")
    fun getApiProfileById(profileId: UUID): EmployeeProfileView {
        return employeeController.getProfileById(profileId)?.asSuccess()!!
    }


    // Change Password
    @Step("change profile password")
    fun changeProfilePassword(profileId: UUID): String {
        return employeeController.updateProfilePassword(profileId)!!.asSuccess().generatedPassword.toString()
    }

    @Step("change profile password with error")
    fun changeProfilePasswordError(profileId: UUID): ChangePasswordError {
        return employeeController.updateProfilePassword(profileId)!!.asClientError()
    }

    //Delete
    @Step("delete profile (by UUID)")
    fun deleteProfile(profileId: UUID): EmployeeActions {
        val deleteRequestStatus = employeeController.deleteProfile(profileId)?.statusCode
        Assertions.assertEquals(HttpStatus.NO_CONTENT_204, deleteRequestStatus)
        return this
    }

    @Step("delete profile (by mobile)")
    fun deleteProfile(mobile: PhoneNumber) {
        employeeController.deleteProfileByMobileIfExists(mobile)
    }

    @Step("delete profile (by mobile)")
    fun deleteProfileByAccountingProfileId(accountingProfileId: String) {
        employeeController.deleteProfileByAccountingProfileIdIfExists(accountingProfileId)
    }

    @Step("delete several profiles by profileId")
    fun deleteSeveralProfileByProfileId(listCreateProfileResponses: MutableList<CreatedProfileView>) {
        for (i in 0 until listCreateProfileResponses.count()) {
            employeeController.deleteProfile(listCreateProfileResponses[i].profileId)!!
        }
    }

    @Step("delete profile error")
    fun deleteProfileError(profileId: UUID): String {
        return employeeController.deleteProfile(profileId)!!.asClientError().message
    }


    //Update
    @Step("update profile request success")
    fun updateProfile(profileId: UUID, updateProfileRequest: UpdateProfileRequest): EmployeeActions {
        updateProfile = employeeController.updateProfile(profileId, updateProfileRequest)!!
        updateProfile.asSuccess()
        return this
    }

    @Step("update 1 profile several times success")
    fun updateProfileSeveralTimes(
        profileId: UUID,
        listUpdateProfileRequest: MutableList<UpdateProfileRequest>
    ): EmployeeActions {
        for (i in 0 until listUpdateProfileRequest.count()) {
            employeeController.updateProfile(profileId, listUpdateProfileRequest[i])!!.asSuccess()
        }
        return this
    }

    @Step("update profile request with exist number")
    fun updateProfileWithExistNumber(profileId: UUID, updateProfileRequest: UpdateProfileRequest): EmployeeActions {
        updateProfile = employeeController.updateProfile(profileId, updateProfileRequest)!!
        conflictProfile = (updateProfile.asClientError() as UpdateProfileError.ProfileAlreadyExists).conflicts[0]
        return this

    }

    @Step("update profile request unsuccessful")
    fun updateProfileUnsuccessful(profileId: UUID, updateProfileRequest: UpdateProfileRequest): EmployeeActions {
        updateProfile = employeeController.updateProfile(profileId, updateProfileRequest)!!
        return this

    }

    fun getUpdateResponse(): RestResult<Unit, UpdateProfileError> {
        return updateProfile
    }


    //Get profiles

    @Step("get profiles")
    fun getProfiles(getBuilder: GetProfilesRequest): GetProfilesView {
        return employeeController.getProfiles(getBuilder)!!.asSuccess()
    }

    //Get staffPartners
    @Step("get staff partners")
    fun getStaffPartners(request: GetPartnersRequest): List<StaffPartnerView> {
        return staffPartnersController.getStaffPartners(request)!!.asSuccess().partners
    }

    @Step("get staff partner by partner id")
    fun getStaffPartnerById(request: GetPartnersRequest, partnerId: UUID): StaffPartnerView {
        val partners = staffPartnersController.getStaffPartners(request)!!.asSuccess().partners
        return partners.filter { it.partnerId == partnerId }.first()
    }

    @Step("get all staff partners from db")
    fun getAllStaffPartnerFromDB(): MutableList<ResultRow> {
       return databaseController.getAllStaffPartner()
    }

    @Step("create staff partner")
    fun createStaffPartner(request: CreatePartnerRequest): StaffPartnerView {
        return staffPartnersController.createStaffPartner(request)!!.asSuccess()
    }

    @Step("create staff partner with error")
    fun createStaffPartnerWithError(request: CreatePartnerRequest): CreatePartnerError {
        return staffPartnersController.createStaffPartner(request)!!.asClientError()
    }

    // Authenticate profile
    @Step("authenticate profile")
    fun authenticateProfile(authBuilder: AuthenticateProfileRequest): EmployeeProfileView {
        return employeeController.authenticateProfile(authBuilder)!!.asSuccess()
    }

    @Step("authenticate profile with error")
    fun authenticateProfileWithError(authBuilder: AuthenticateProfileRequest): String {
        return employeeController.authenticateProfile(authBuilder)!!.asClientError().message
    }

    // Get Profiles By list IDS
    @Step("get profiles by list of ids")
    fun getProfilesByListOfIds(getBuilder: GetProfilesByIdsRequestBuilder): List<EmployeeProfileView> {
        val result = employeeController.getProfilesByIdsList(getBuilder.build())!!.asSuccess().profiles
        return result.values.toList()
    }

    @Step("get profiles by list of ids")
    fun getProfilesByListOfIdsWithError(getBuilder: GetProfilesByIdsRequestBuilder): String {
        return employeeController.getProfilesByIdsList(getBuilder.build())!!.asClientError().message
    }

    //Internship
    @Step("send create internship request success")
    fun createInternship(
        profileId: UUID,
        darkstoreUserRole: DarkstoreUserRole,
        createInternshipRequest: CreateInternshipRequest
    ): EmployeeActions {
        createInternship =
            internshipController.createInternship(profileId, darkstoreUserRole, createInternshipRequest)!!
        createInternship.asSuccess()
        return this
    }

    @Step("send several create internship request success")
    fun createSeveralInternships(
        listCreateProfileResponses: MutableList<CreatedProfileView>,
        darkstoreUserRole: DarkstoreUserRole,
        listCreateInternshipRequest: MutableList<CreateInternshipRequest>
    ):
            EmployeeActions {
        for (i in 0 until listCreateInternshipRequest.count()) {
            internshipController.createInternship(
                listCreateProfileResponses[i].profileId,
                darkstoreUserRole,
                listCreateInternshipRequest[i]
            )!!.asSuccess()

        }
        return this
    }


    @Step("send create internship request unsuccessful")
    fun createInternshipUnsuccessful(
        profileId: UUID,
        darkstoreUserRole: DarkstoreUserRole,
        createInternshipRequest: CreateInternshipRequest
    ): EmployeeActions {
        createInternship =
            internshipController.createInternship(profileId, darkstoreUserRole, createInternshipRequest)!!
        return this
    }


    @Step("get created internship by profileId")
    fun getInternship(
        profileId: UUID
    ): EmployeeActions {
        createdInternship =
            internshipController.getInternshipByProfileId(profileId)!!.asSuccess()
        return this
    }

    @Step("get created internship by darkstoreId")
    fun getDSInternship(
        darkstoreId: UUID,
        request: GetDarkstoreInternshipsRequest
    ): EmployeeActions {
        dsInternship =
            internshipController.getInternshipByDarkstoreId(darkstoreId, request)!!.asSuccess()
        return this
    }


    @Step("update internship")
    fun updateInternship(
        profileId: UUID,
        darkstoreUserRole: DarkstoreUserRole,
        updateInternshipRequest: UpdateInternshipRequest
    ): EmployeeActions {
        updatedInternship =
            internshipController.updateInternship(profileId, darkstoreUserRole, updateInternshipRequest)!!
        updatedInternship.asSuccess()
        return this
    }

    @Step("update 1 internship several times")
    fun updateInternshipSeveralTimes(
        profileId: UUID,
        darkstoreUserRole: DarkstoreUserRole,
        listUpdateInternshipRequest: MutableList<UpdateInternshipRequest>
    ): EmployeeActions {
        val listUpdatedInternship: MutableList<RestResult<Unit, UpdateInternshipError>> = mutableListOf()
        val listUpdatedInternshipResponses: MutableList<Unit> = mutableListOf()
        for (i in 0 until listUpdateInternshipRequest.count()) {
            listUpdatedInternship.add(
                internshipController.updateInternship(
                    profileId,
                    darkstoreUserRole,
                    listUpdateInternshipRequest[i]
                )!!
            )
            listUpdatedInternshipResponses.add(listUpdatedInternship[i].asSuccess())
        }

        return this
    }


    @Step("update internship unsuccessful")
    fun updateInternshipUnsuccessful(
        profileId: UUID,
        darkstoreUserRole: DarkstoreUserRole,
        updateInternshipRequest: UpdateInternshipRequest
    ): EmployeeActions {
        updatedInternship =
            internshipController.updateInternship(profileId, darkstoreUserRole, updateInternshipRequest)!!
        return this
    }

    @Step("cancel internship")
    fun cancelInternship(
        plannedDate: Instant,
        profileId: UUID,
        darkstoreUserRole: DarkstoreUserRole,
        cancelInternshipRequest: CancelInternshipRequest
    ): EmployeeActions {
        waitInternshipPlannedDatePassed(plannedDate)
        cancelledInternship =
            internshipController.cancelInternship(profileId, darkstoreUserRole, cancelInternshipRequest)!!
        cancelledInternship.asSuccess()
        return this
    }

    @Step("cancel internship unsuccessful")
    fun cancelInternshipUnsuccessful(
        plannedDate: Instant,
        profileId: UUID,
        darkstoreUserRole: DarkstoreUserRole,
        cancelInternshipRequest: CancelInternshipRequest
    ): EmployeeActions {
        waitInternshipPlannedDatePassed(plannedDate)
        cancelledInternship =
            internshipController.cancelInternship(profileId, darkstoreUserRole, cancelInternshipRequest)!!
        return this
    }


    @Step("cancel internship")
    fun cancelInternshipWithoutWaiting(
        plannedDate: Instant,
        profileId: UUID,
        darkstoreUserRole: DarkstoreUserRole,
        cancelInternshipRequest: CancelInternshipRequest
    ): EmployeeActions {
        cancelledInternship =
            internshipController.cancelInternship(profileId, darkstoreUserRole, cancelInternshipRequest)!!
        cancelledInternship.asSuccess()
        return this
    }


    @Step("reject internship")
    fun rejectInternship(
        plannedDate: Instant,
        profileId: UUID,
        darkstoreUserRole: DarkstoreUserRole,
        rejectInternshipRequest: RejectInternshipRequest
    ): EmployeeActions {
        waitInternshipPlannedDatePassed(plannedDate)
        rejectedInternship =
            internshipController.rejectInternship(profileId, darkstoreUserRole, rejectInternshipRequest)!!
        rejectedInternship.asSuccess()
        return this
    }

    @Step("reject internship unsuccessful")
    fun rejectInternshipUnsuccessful(
        plannedDate: Instant,
        profileId: UUID,
        darkstoreUserRole: DarkstoreUserRole,
        rejectInternshipRequest: RejectInternshipRequest
    ): EmployeeActions {
        waitInternshipPlannedDatePassed(plannedDate)
        rejectedInternship =
            internshipController.rejectInternship(profileId, darkstoreUserRole, rejectInternshipRequest)!!
        return this
    }


    @Step("reject internship")
    fun rejectInternshipWithoutWaiting(
        plannedDate: Instant,
        profileId: UUID,
        darkstoreUserRole: DarkstoreUserRole,
        rejectInternshipRequest: RejectInternshipRequest
    ): EmployeeActions {
        rejectedInternship =
            internshipController.rejectInternship(profileId, darkstoreUserRole, rejectInternshipRequest)!!
        rejectedInternship.asSuccess()
        return this
    }

    @Step("close internship")
    fun closeInternship(
        plannedDate: Instant,
        profileId: UUID,
        darkstoreUserRole: DarkstoreUserRole,
        closeInternshipRequest: CloseInternshipRequest
    ): EmployeeActions {
        waitInternshipPlannedDatePassed(plannedDate)
        closedInternship =
            internshipController.closeInternship(profileId, darkstoreUserRole, closeInternshipRequest)!!
        closedInternship.asSuccess()
        return this
    }

    @Step("close internship unsuccessful")
    fun closeInternshipUnsuccessful(
        plannedDate: Instant,
        profileId: UUID,
        darkstoreUserRole: DarkstoreUserRole,
        closeInternshipRequest: CloseInternshipRequest
    ): EmployeeActions {
        waitInternshipPlannedDatePassed(plannedDate)
        closedInternship =
            internshipController.closeInternship(profileId, darkstoreUserRole, closeInternshipRequest)!!
        return this
    }


    @Step("close internship")
    fun closeInternshipWithoutWaiting(
        plannedDate: Instant,
        profileId: UUID,
        darkstoreUserRole: DarkstoreUserRole,
        closeInternshipRequest: CloseInternshipRequest
    ): EmployeeActions {
        closedInternship =
            internshipController.closeInternship(profileId, darkstoreUserRole, closeInternshipRequest)!!
        closedInternship.asSuccess()
        return this
    }


// Kafka

    @Step("Get all message from employee_profile_log")
    fun getAllMessagesFromKafkaLogById(profileId: UUID): MutableList<EmployeeProfileLog> {
        val records = kafkaEmployeeLog.consumeAll()
        val result = mutableListOf<EmployeeProfileLog>()
        records!!.forEach {
            val key = it.key()
            if (key.toString() == profileId.toString()) {
                result.add(jacksonObjectMapper.convertValue(it.value(), EmployeeProfileLog::class.java))
            }
        }
        return result
    }

    @Step("Get message from employeeprofiles_profile_created")
    fun getMessageFromKafkaCreated(profileId: UUID): EmployeeProfileLog {
        val answer =
            kafkaEmployeeCreated.consume(profileId.toString())!!.value()
        return jacksonObjectMapper.convertValue(answer, EmployeeProfileLog::class.java)
    }

    @Step("Get message from employeeprofiles_profile_changed")
    fun getMessageFromKafkaUpdate(profileId: UUID): EmployeeProfilesProfileChanged {
        val answer =
            kafkaEmployeeChanged.consume(profileId.toString())!!.value()
        val result = jacksonObjectMapper.convertValue(answer, EmployeeProfilesProfileChanged::class.java)
        return result
    }


    @Step("Get all message from employeeprofiles_profile_changed")
    fun getAllMessagesFromKafkaChangeLogById(profileId: UUID): MutableList<EmployeeProfilesProfileChanged> {
        Thread.sleep(5_000)
        val records = kafkaEmployeeChanged.consumeAll()
        val results = mutableListOf<Any?>()
        for (record in records!!) {
            val key = record.key()
            if (key.toString() == profileId.toString()) {
                results.add(record.value())
            }
        }
        val result2 = mutableListOf<EmployeeProfilesProfileChanged>()
        for (result in results) {
            result2.add(jacksonObjectMapper.convertValue(result, EmployeeProfilesProfileChanged::class.java))
        }
        return result2
    }


    @Step("Get message from employeeprofiles_profile_disabled")
    fun getMessageFromKafkaDisable(profileId: UUID): EmployeeProfileLog {
        val answer =
            kafkaEmployeeDisabled.consume(profileId.toString())!!.value()
        val result = jacksonObjectMapper.convertValue(answer, EmployeeProfileLog::class.java)
        return result
    }

    @Step("Get message from employeeprofiles_profile_disabled")
    fun getMessageFromKafkaPasswordChanged(profileId: UUID): EmployeeProfilesProfilePasswordChanged {
        val answer =
            kafkaEmployeePassChanged.consume(profileId.toString())!!.value()
        val result = jacksonObjectMapper.convertValue(answer, EmployeeProfilesProfilePasswordChanged::class.java)
        return result
    }

    @Step("Produce message to kafka KadrovyyPerevodCFZ")
    fun produceToKadrovyyPerevodCFZ(event: KadrovyyPerevodCFZ) {
        val byteEvent = jacksonObjectMapper.writeValueAsBytes(event)
        val key = UUID.randomUUID()
        kafkaKadrovyyPerevodCFZ.sendMessage(byteEvent, 1, key)
        Thread.sleep(2_000)
    }

    @Step("Produce message to kafka KadrovyyPerevodSpiskomCFZ")
    fun produceToKadrovyyPerevodSpiskomCFZ(event: KadrovyyPerevodSpiskomCFZ) {
        val byteEvent = jacksonObjectMapper.writeValueAsBytes(event)
        val key = UUID.randomUUID()
        kafkaKadrovyyPerevodSpiskomCFZ.sendMessage(byteEvent, 1, key)
        Thread.sleep(2_000)
    }


    // Darkstore Contacts

    @Step("get darkstore contacts")
    fun getDarkstoreContacts(darkstoreId: UUID): List<EmployeeProfileContactView> {
        return darkstoreContactsController.getDarkstoreContacts(darkstoreId)!!.contacts
    }

    @Step("delete all contacts from DarkStore")
    fun deleteAllDarkstoreContacts(darkstoreId: UUID) {

        val profiles = darkstoreContactsController.getDarkstoreContacts(darkstoreId)
        profiles!!.contacts.forEach {
            deleteProfile(it.profileId)
        }

    }

    // DS User
    // Darkstore users

    @Step("send update ds status request success")
    fun updateDSUserStatus(
        profileId: UUID,
        darkstoreId: UUID,
        darkstoreUserRole: DarkstoreUserRole,
        updateDSUserStatusRequest: UpdateDarkstoreUserRequest
    ): EmployeeActions {
        updateDSUserProfile =
            dsUserController.updateProfileStatus(profileId, darkstoreId, darkstoreUserRole, updateDSUserStatusRequest)!!
        updateDSUserProfile.asSuccess()
        return this
    }


    @Step("get darkstore user by id")
    fun getDarkstoreUserById(profileId: UUID, darkstoreId: UUID, role: DarkstoreUserRole): DarkstoreUserView {
        return dsUserController.getDarkstoreUserById(profileId, darkstoreId, role)!!.asSuccess()
    }

    @Step("get darkstore user by id error")
    fun getDarkstoreUserByIdError(profileId: UUID, darkstoreId: UUID, role: DarkstoreUserRole): String {
        return dsUserController.getDarkstoreUserById(profileId, darkstoreId, role)!!.asClientError().message
    }

    @Step("get darkstore user by id error")
    fun searchDarkstoreProfiles(request: SearchDarkstoreUsersRequest): SearchDarkstoreUsersView {
        return dsUserController.searchDarkstoreProfiles(request)!!.asSuccess()
    }

    @Step("Update darkstore status")
    fun updateProfileStatusOnDarkstore(
        profileId: UUID,
        darkstoreId: UUID,
        role: DarkstoreUserRole,
        request: UpdateDarkstoreUserRequest
    ) {
        return dsUserController.updateProfileStatus(profileId, darkstoreId, role, request)!!.asSuccess()
    }

    @Step("Update darkstore status with error")
    fun updateProfileStatusOnDarkstoreWithError(
        profileId: UUID,
        darkstoreId: UUID,
        role: DarkstoreUserRole,
        request: UpdateDarkstoreUserRequest
    ): String {
        return dsUserController.updateProfileStatus(profileId, darkstoreId, role, request)!!.asClientError().message
    }

    @Step("Find darkstore profiles by ids")
    fun findDSProfilesByIds(request: GetDarkstoreUsersByProfileIdsRequest): Map<UUID, List<DarkstoreUserView>> {
        return dsUserController.findProfiles(request)!!.asSuccess().darkstoreUsers
    }

    // Violations

    @Step("Violations dictionary")
    fun getViolationsDictionary(): ViolationDictionaryView {
        return dsViolationsController.getViolationsDictionary()!!.asSuccess()
    }

    @Step("Add violation")
    fun addViolation(
        profileId: UUID,
        darkstoreId: UUID,
        role: DarkstoreUserRole,
        request: StoreDarkstoreUserViolationRequest
    ): StoreDarkstoreUserViolationView {
        return dsViolationsController.addViolation(profileId, darkstoreId, role, request)!!.asSuccess()
    }

    @Step("Add violations")
    fun addViolations(
        profileId: UUID,
        darkstoreId: UUID,
        role: DarkstoreUserRole,
        requests: List<StoreDarkstoreUserViolationRequest>
    ): List<StoreDarkstoreUserViolationView> {
        val violations = mutableListOf<StoreDarkstoreUserViolationView>()
        requests.forEach {
            violations.add(dsViolationsController.addViolation(profileId, darkstoreId, role, it)!!.asSuccess())
        }
        return violations
    }


    @Step("Add violation with error")
    fun addViolationWithError(
        profileId: UUID,
        darkstoreId: UUID,
        role: DarkstoreUserRole,
        request: StoreDarkstoreUserViolationRequest
    ): StoreDarkstoreUserViolationError {
        return dsViolationsController.addViolation(profileId, darkstoreId, role, request)!!.asClientError()
    }

    @Step("Get violation")
    fun getViolation(profileId: UUID, role: DarkstoreUserRole, violationId: UUID): DarkstoreUserViolationView {
        return dsViolationsController.getViolation(profileId, role)!!
            .asSuccess().violations.filter { it.violationId == violationId }.first()
    }

    @Step("Get violations")
    fun getViolations(profileId: UUID, role: DarkstoreUserRole): List<DarkstoreUserViolationView> {
        return dsViolationsController.getViolation(profileId, role)!!.asSuccess().violations
    }

    @Step("Get violation with error")
    fun getViolationWithError(
        profileId: UUID,
        role: DarkstoreUserRole,
        violationId: UUID
    ): GetDarkstoreUserViolationsError {
        return dsViolationsController.getViolation(profileId, role)!!.asClientError()
    }

    @Step("Get violation from DB")
    fun getViolationFromDB(violationId: UUID): ResultRow {
        return databaseController.getDarkstoreUserViolationByViolationId(violationId)
    }

    @Step("Get violation log from DB")
    fun getViolationLogFromDB(violationId: UUID): ResultRow {
        return databaseController.getDarkstoreUserViolationLogByViolationId(violationId)
    }

    @Step("Search violations")
    fun searchViolations(request: SearchDarkstoreUsersViolationsRequest): List<ExtendedDarkstoreUserViolationView> {
        return dsViolationsController.searchViolation(request)!!.asSuccess().violations
    }

    @Step("Clear violations in database")
    fun deleteViolationsFromDatabase(profileId: UUID) {
        databaseController.deleteViolationsByProfileId(profileId)
    }

    // 1c integration
    @Step("Produce message to kafka PriemNaRabotuCFZ")
    fun produceToPriemNaRabotuCFZ(event: PriemNaRabotuCFZ) {
        val byteEvent = jacksonObjectMapper.writeValueAsBytes(event)
        val key = UUID.randomUUID()
        kafkaPriemNaRabotuCFZ.sendMessage(byteEvent, 1, key)
        Thread.sleep(2_000)
    }

    @Step("Produce message to kafka PriemNaRabotuSpiskomCFZ")
    fun produceToPriemNaRabotuSpiskomCFZ(event: PriemNaRabotuSpiskomCFZ) {
        val byteEvent = jacksonObjectMapper.writeValueAsBytes(event)
        val key = UUID.randomUUID()
        kafkaPriemNaRabotuSpiskomCFZ.sendMessage(byteEvent, 1, key)
    }

    @Step("Produce message to kafka VneshnieSotrudniki")
    fun produceToVneshnieSotrudniki(event: VneshnieSotrudniki) {
        val byteEvent = jacksonObjectMapper.writeValueAsBytes(event)
        val key = UUID.randomUUID()
        kafkaVneshnieSotrudniki.sendMessage(byteEvent, 1, key)
        Thread.sleep(2_000)
    }

    @Step("Get profile signature")
    fun getProfileSignature(profileId: UUID): EmployeeSignatureView {
        return signatureController.getSignature(profileId)!!.asSuccess()
    }

    @Step("Get profile signature with error")
    fun getProfileSignatureWithError(profileId: UUID): GetSignatureByProfileIdError {
        return signatureController.getSignature(profileId)!!.asClientError()
    }

    @Step("Get profile contract")
    fun getProfileContract(request: SearchUsersContractsRequest): SearchUsersContractsView {
        return contractsController.getContracts(request)!!.asSuccess()
    }

    @Step("Get profile contract by accounting profile id")
    fun getProfileContractByAccountingProfileId(request: SearchAccountingProfileContractsRequest): SearchAccountingProfileContractsView {
        return contractsController.getContractsByAccountingProfileIds(request)!!.asSuccess()
    }

    @Step("Get profile contract")
    fun getProfileContractWithError(request: SearchUsersContractsRequest): SearchUsersContractsError {
        return contractsController.getContracts(request)!!.asClientError()
    }

    @Step("Wait  intership's planned_date is passed (ignore seconds)")
    fun waitInternshipPlannedDatePassed(
        plannedDate: Instant
    ) {
        Awaitility.await()
            .atMost(duration = Duration.ofSeconds(30)).withPollInterval(Duration.ofSeconds(5)).until {
                Instant.now().truncatedTo(
                    ChronoUnit.SECONDS
                ).isAfter(
                    plannedDate.truncatedTo(
                        ChronoUnit.SECONDS
                    )
                )
            }

    }

    @Step("Change internship's planned date in database")
    fun changeInternshipDateInDatabase(profileId: UUID, newDate: String): EmployeeActions {
        databaseController.updateInternshipPlanningDate(profileId, newDate)
        return this
    }


    @Step("delete partner (by title)")
    fun deletePartner(title: String): EmployeeActions {
        databaseController.deletePartnerByTitle(title)
        return this
    }

    // first login

    @Step("Produce message to FirstLogin")
    fun produceToFirstLogin(event: FirstLoginEvent) {
        val byteEvent = jacksonObjectMapper.writeValueAsBytes(event)
        val key = UUID.randomUUID()
        kafkaFirstLogin.sendMessage(byteEvent, 1, key)
        Thread.sleep(2_000)
    }

}