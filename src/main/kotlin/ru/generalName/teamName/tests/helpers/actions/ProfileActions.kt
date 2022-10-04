package ru.generalName.teamName.tests.helpers.actions

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
import java.time.Duration
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*

@Component
@Scope("prototype")
class ProfileActions(
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
    private lateinit var databaseController: ProfilesDatabaseController

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


    fun setProfile(profile: ResultRow): ProfileActions {
        profileFromDB = profile
        return this
    }

    fun setInternship(internship: ResultRow): ProfileActions {
        internshipFromDB = internship
        return this
    }


    fun getProfile(): ResultRow {
        return profileFromDB
    }

    fun getInternship(): ResultRow {
        return internshipFromDB
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


    fun getProfileExistanceByMobile(mobile: String): Boolean {
        return databaseController.checkActiveProfileExistsByMobile(mobile)
    }


    // requisitions
    fun getRequisitionFromDBByAccountingProfileId(accountingProfileId: String): ResultRow? {
        return databaseController.getRequisitionByAccountingProfileId(accountingProfileId)
    }


    @Step("Clear requisition in database")
    fun deleteRequisitionFromDatabase(accountingProfileId: String) {
        val requisition = getRequisitionFromDBByAccountingProfileId(accountingProfileId)

        if (requisition != null) {
            databaseController.deleteRequisitionLogByRequestId(requisition[ProfileRequisition.requestId])
            databaseController.deleteRequisitionByRequestId(requisition[ProfileRequisition.requestId])
        }
    }


    //Create
    @Step("send create profile request success")
    fun createProfile(createProfileRequest: CreateProfileRequest): ProfileActions {
        createProfile = employeeController.createProfile(createProfileRequest)!!
        createdProfileResponse = createProfile.asSuccess()
        return this
    }


    @Step("send create profile request with error")
    fun createProfileWithError(createProfileRequest: CreateProfileRequest): ProfileActions {
        responseCreateProfile = employeeController.createProfile(createProfileRequest)!!
        return this

    }

    @Step("send create profile request success")
    fun createProfileId(createProfileRequest: CreateProfileRequest): UUID {
        return employeeController.createProfile(createProfileRequest)!!.asSuccess().profileId
    }

    @Step("send create profile request with exist number")
    fun createProfileWithExistNumber(createRequest: CreateProfileRequest): ProfileActions {
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
    fun getProfileById(profileId: UUID): ProfileActions {
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
    fun deleteProfile(profileId: UUID): ProfileActions {
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
    fun updateProfile(profileId: UUID, updateProfileRequest: UpdateProfileRequest): ProfileActions {
        updateProfile = employeeController.updateProfile(profileId, updateProfileRequest)!!
        updateProfile.asSuccess()
        return this
    }

    @Step("update 1 profile several times success")
    fun updateProfileSeveralTimes(
        profileId: UUID,
        listUpdateProfileRequest: MutableList<UpdateProfileRequest>
    ): ProfileActions {
        for (i in 0 until listUpdateProfileRequest.count()) {
            employeeController.updateProfile(profileId, listUpdateProfileRequest[i])!!.asSuccess()
        }
        return this
    }


    @Step("update profile request unsuccessful")
    fun updateProfileUnsuccessful(profileId: UUID, updateProfileRequest: UpdateProfileRequest): ProfileActions {
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
    ): ProfileActions {
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
            ProfileActions {
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
    ): ProfileActions {
        createInternship =
            internshipController.createInternship(profileId, darkstoreUserRole, createInternshipRequest)!!
        return this
    }


    @Step("get created internship by profileId")
    fun getInternship(
        profileId: UUID
    ): ProfileActions {
        createdInternship =
            internshipController.getInternshipByProfileId(profileId)!!.asSuccess()
        return this
    }

    @Step("get created internship by darkstoreId")
    fun getDSInternship(
        darkstoreId: UUID,
        request: GetDarkstoreInternshipsRequest
    ): ProfileActions {
        dsInternship =
            internshipController.getInternshipByDarkstoreId(darkstoreId, request)!!.asSuccess()
        return this
    }


    @Step("update internship")
    fun updateInternship(
        profileId: UUID,
        darkstoreUserRole: DarkstoreUserRole,
        updateInternshipRequest: UpdateInternshipRequest
    ): ProfileActions {
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
    ): ProfileActions {
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
    ): ProfileActions {
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
    ): ProfileActions {
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
    ): ProfileActions {
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
    ): ProfileActions {
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
    ): ProfileActions {
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
    ): ProfileActions {
        waitInternshipPlannedDatePassed(plannedDate)
        rejectedInternship =
            internshipController.rejectInternship(profileId, darkstoreUserRole, rejectInternshipRequest)!!
        return this
    }




    @Step("close internship")
    fun closeInternship(
        plannedDate: Instant,
        profileId: UUID,
        darkstoreUserRole: DarkstoreUserRole,
        closeInternshipRequest: CloseInternshipRequest
    ): ProfileActions {
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
    ): ProfileActions {
        waitInternshipPlannedDatePassed(plannedDate)
        closedInternship =
            internshipController.closeInternship(profileId, darkstoreUserRole, closeInternshipRequest)!!
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




    // DS User
    // Darkstore users

    @Step("send update ds status request success")
    fun updateDSUserStatus(
        profileId: UUID,
        darkstoreId: UUID,
        darkstoreUserRole: DarkstoreUserRole,
        updateDSUserStatusRequest: UpdateDarkstoreUserRequest
    ): ProfileActions {
        updateDSUserProfile =
            dsUserController.updateProfileStatus(profileId, darkstoreId, darkstoreUserRole, updateDSUserStatusRequest)!!
        updateDSUserProfile.asSuccess()
        return this
    }


    @Step("get darkstore user by id")
    fun getDarkstoreUserById(profileId: UUID, darkstoreId: UUID, role: DarkstoreUserRole): DarkstoreUserView {
        return dsUserController.getDarkstoreUserById(profileId, darkstoreId, role)!!.asSuccess()
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
    fun changeInternshipDateInDatabase(profileId: UUID, newDate: String): ProfileActions {
        databaseController.updateInternshipPlanningDate(profileId, newDate)
        return this
    }


    @Step("delete partner (by title)")
    fun deletePartner(title: String): ProfileActions {
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