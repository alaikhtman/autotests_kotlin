package ru.samokat.mysamokat.tests.dataproviders.preconditions

import io.qameta.allure.Step
import ru.samokat.employeeprofiles.api.common.domain.EmployeeProfileStatus
import ru.samokat.employeeprofiles.api.common.domain.EmployeeVehicleType
import ru.samokat.employeeprofiles.api.common.domain.StaffPartnerType
import ru.samokat.employeeprofiles.api.common.domain.Vehicle
import ru.samokat.employeeprofiles.api.common.filtering.MobileQueryFilter
import ru.samokat.employeeprofiles.api.common.paging.PagingFilter
import ru.samokat.employeeprofiles.api.contracts.search.SearchAccountingProfileContractsRequest
import ru.samokat.employeeprofiles.api.contracts.search.SearchUsersContractsRequest
import ru.samokat.employeeprofiles.api.darkstoreusers.domain.DarkstoreUserRole
import ru.samokat.employeeprofiles.api.darkstoreusers.domain.DarkstoreUserState
import ru.samokat.employeeprofiles.api.darkstoreusers.getbyprofileids.GetDarkstoreUsersByProfileIdsRequest
import ru.samokat.employeeprofiles.api.darkstoreusers.internships.cancel.CancelInternshipRequest
import ru.samokat.employeeprofiles.api.darkstoreusers.internships.close.CloseInternshipRequest
import ru.samokat.employeeprofiles.api.darkstoreusers.internships.create.CreateInternshipRequest
import ru.samokat.employeeprofiles.api.darkstoreusers.internships.domain.FailureCode
import ru.samokat.employeeprofiles.api.darkstoreusers.internships.domain.InternshipStatus
import ru.samokat.employeeprofiles.api.darkstoreusers.internships.domain.RejectionCode
import ru.samokat.employeeprofiles.api.darkstoreusers.internships.get.GetDarkstoreInternshipsRequest
import ru.samokat.employeeprofiles.api.darkstoreusers.internships.reject.RejectInternshipRequest
import ru.samokat.employeeprofiles.api.darkstoreusers.internships.update.UpdateInternshipRequest
import ru.samokat.employeeprofiles.api.darkstoreusers.search.SearchDarkstoreUsersRequest
import ru.samokat.employeeprofiles.api.darkstoreusers.search.SearchDarkstoreUsersSortingMode
import ru.samokat.employeeprofiles.api.darkstoreusers.update.UpdateDarkstoreUserRequest
import ru.samokat.employeeprofiles.api.darkstoreusers.violations.domain.ViolationCode
import ru.samokat.employeeprofiles.api.darkstoreusers.violations.search.SearchDarkstoreUsersViolationsRequest
import ru.samokat.employeeprofiles.api.darkstoreusers.violations.store.StoreDarkstoreUserViolationRequest
import ru.samokat.employeeprofiles.api.profilerequisitions.decline.DeclineProfileRequisitionRequest
import ru.samokat.employeeprofiles.api.profilerequisitions.domain.ProfileRequisitionStatus
import ru.samokat.employeeprofiles.api.profilerequisitions.domain.ProfileRequisitionType
import ru.samokat.employeeprofiles.api.profilerequisitions.search.SearchProfileRequisitionsRequest
import ru.samokat.employeeprofiles.api.profiles.create.CreateProfileRequest
import ru.samokat.employeeprofiles.api.profiles.getprofiles.PagingQueryFilter
import ru.samokat.employeeprofiles.api.profiles.update.UpdateProfileRequest
import ru.samokat.employeeprofiles.api.staffpartners.createpartners.CreatePartnerRequest
import ru.samokat.employeeprofiles.api.staffpartners.getpartners.GetPartnersRequest
import ru.samokat.logistics.tests.dataproviders.employee.AuthenticateProfileRequestBuilder
import ru.samokat.logistics.tests.dataproviders.employee.GetProfilesByIdsRequestBuilder
import ru.samokat.logistics.tests.dataproviders.employee.GetProfilesRequestBuilder
import ru.samokat.my.domain.PhoneNumber
import ru.samokat.my.domain.employee.EmployeeName
import ru.samokat.my.domain.employee.EmployeeRole
import ru.samokat.my.rest.api.enum.ApiEnum
import ru.samokat.mysamokat.tests.dataproviders.Constants
import ru.samokat.mysamokat.tests.dataproviders.StringAndPhoneNumberGenerator
import ru.samokat.mysamokat.tests.dataproviders.employee.*
import ru.samokat.mysamokat.tests.helpers.controllers.events.*
import ru.samokat.mysamokat.tests.helpers.controllers.events.employee.*
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*


class EmployeePreconditions {

    private lateinit var createProfileRequest: CreateProfileRequest
    fun createProfileRequest(): CreateProfileRequest {
        return createProfileRequest
    }

    private var listCreateProfileRequest: MutableList<CreateProfileRequest> = mutableListOf()
    fun listCreateProfileRequest(): MutableList<CreateProfileRequest> {
        return listCreateProfileRequest
    }

    private lateinit var updateProfileRequest: UpdateProfileRequest
    fun updateProfileRequest(): UpdateProfileRequest {
        return updateProfileRequest
    }

    private lateinit var listUpdateProfileRequest: MutableList<UpdateProfileRequest>
    fun listUpdateProfileRequest(): MutableList<UpdateProfileRequest> {
        return listUpdateProfileRequest
    }


    private lateinit var updateDSUserStatusRequest: UpdateDarkstoreUserRequest
    fun updateDSUserStatusRequest(): UpdateDarkstoreUserRequest {
        return updateDSUserStatusRequest
    }

    private lateinit var createInternshipRequest: CreateInternshipRequest
    fun createInternshipRequest(): CreateInternshipRequest {
        return createInternshipRequest
    }

    private var listCreateInternshipRequest: MutableList<CreateInternshipRequest> = mutableListOf()
    fun listCreateInternshipRequest(): MutableList<CreateInternshipRequest> {
        return listCreateInternshipRequest
    }

    private lateinit var createInternshipTwiceRequest: CreateInternshipRequest
    fun createInternshipTwiceRequest(): CreateInternshipRequest {
        return createInternshipTwiceRequest
    }

    private lateinit var getInternshipDSRequest: GetDarkstoreInternshipsRequest
    fun getInternshipDSRequest(): GetDarkstoreInternshipsRequest {
        return getInternshipDSRequest
    }

    private lateinit var updateInternshipRequest: UpdateInternshipRequest
    fun updateInternshipRequest(): UpdateInternshipRequest {
        return updateInternshipRequest
    }

    private lateinit var listUpdateInternshipRequest: MutableList<UpdateInternshipRequest>
    fun listUpdateInternshipRequest(): MutableList<UpdateInternshipRequest> {
        return listUpdateInternshipRequest
    }

    private lateinit var cancelInternshipRequest: CancelInternshipRequest
    fun cancelInternshipRequest(): CancelInternshipRequest {
        return cancelInternshipRequest
    }

    private lateinit var rejectInternshipRequest: RejectInternshipRequest
    fun rejectInternshipRequest(): RejectInternshipRequest {
        return rejectInternshipRequest
    }

    private lateinit var closeInternshipRequest: CloseInternshipRequest
    fun closeInternshipRequest(): CloseInternshipRequest {
        return closeInternshipRequest
    }


    //Create Profile
    @Step("Set create profile request")
    fun setCreateProfileRequest(
        mobile: PhoneNumber = Constants.mobile1,
        generatePassword: Boolean = true,
        issuerProfileId: UUID = UUID.randomUUID(),
        roles: List<ApiEnum<EmployeeRole, String>>,
        name: EmployeeName = EmployeeName(
            StringAndPhoneNumberGenerator.generateRandomString(10),
            StringAndPhoneNumberGenerator.generateRandomString(10),
            StringAndPhoneNumberGenerator.generateRandomString(10)
        ),
        darkstoreId: UUID? = Constants.darkstoreId,
        email: String? = null,
        staffPartnerId: UUID? = Constants.defaultStaffPartnerId,
        supervisedDarkstores: MutableList<UUID>? = null,
        vehicle: Vehicle? = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
        accountingProfileId: String? = null,
        cityId: UUID? = Constants.cityId
    ) = apply {
        this.createProfileRequest = CreateProfileRequestBuilder()
            .name(name)
            .issuerProfileId(issuerProfileId)
            .mobile(mobile)
            .staffPartnerId(staffPartnerId)
            .darkstoreId(darkstoreId)
            .roles(roles)
            .vehicle(vehicle)
            .generatePassword(generatePassword)
            .supervisedDarkstore(supervisedDarkstores)
            .email(email)
            .accountingProfileId(accountingProfileId)
            .cityId(cityId)
            .build()
    }

    @Step("fill createProfile builder")
    fun fillCreateProfileRequest(
        mobile: PhoneNumber = Constants.mobile1,
        generatePassword: Boolean = true,
        issuerProfileId: UUID = UUID.randomUUID(),
        roles: List<ApiEnum<EmployeeRole, String>> = (listOf(ApiEnum(EmployeeRole.DELIVERYMAN))),
        name: EmployeeName = EmployeeName(
            StringAndPhoneNumberGenerator.generateRandomString(10),
            StringAndPhoneNumberGenerator.generateRandomString(10),
            StringAndPhoneNumberGenerator.generateRandomString(10)
        ),
        darkstoreId: UUID? = Constants.darkstoreId,
        email: String? = Constants.defaultEmail,
        staffPartnerId: UUID? = Constants.defaultStaffPartnerId,
        supervisedDarkstoresCount: Int = 0,
        supervisedDarkstores: MutableList<UUID>? = null,
        vehicle: Vehicle? = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
        accountingProfileId: String? = null,
        requisitionId: UUID? = null,
        cityId: UUID? = Constants.cityId
    ): CreateProfileRequest {
        return CreateProfileRequestBuilder()
            .mobile(mobile)
            .darkstoreId(darkstoreId)
            .generatePassword(generatePassword)
            .issuerProfileId(issuerProfileId)
            .email(email)
            .staffPartnerId(staffPartnerId)
            .roles(roles)
            .vehicle(vehicle)
            .name(name)
            .supervisedDarkstore(supervisedDarkstores)
            .randomSupervisedDarkstore(supervisedDarkstoresCount)
            .accountingProfileId(accountingProfileId)
            .requisitionId(requisitionId)
            .cityId(cityId)
            .build()
    }

    @Step("Fill het staff partners request")
    fun fillGetStaffPartnersRequest(
        pageMark: String? = null,
        pageSize: Int? = null
    ): GetPartnersRequest {
        return GetStaffPartnersRequestBuilder()
            .pageMark(pageMark)
            .pageSize(pageSize)
            .build()
    }

    @Step("Set list of profile request")
    fun setListOfCreatedProfileRequest(
        amount: Int
    ): EmployeePreconditions {
        for (i in 0..amount) {
            this.listCreateProfileRequest.add(
                fillCreateProfileRequest(
                    mobile = PhoneNumber(
                        StringAndPhoneNumberGenerator.generateRandomPhoneNumber()
                    ), email = null
                )
            )
        }
        return this
    }

    //Update Profile
    @Step("Set profile for update")
    fun setUpdateProfileRequest(
        createProfileRequest: CreateProfileRequest,
        name: EmployeeName = createProfileRequest.name,
        issuerProfileId: UUID = createProfileRequest.issuerProfileId,
        mobile: PhoneNumber = createProfileRequest.mobile,
        staffPartnerId: UUID? = createProfileRequest.staffPartnerId,
        darkstoreId: UUID? = createProfileRequest.darkstoreId,
        roles: List<ApiEnum<EmployeeRole, String>> = createProfileRequest.roles,
        vehicle: Vehicle? = createProfileRequest.vehicle,
        email: String? = createProfileRequest.email,
        supervisedDarkstores: MutableList<UUID>? = null,
        accountingProfileId: String? = createProfileRequest.accountingProfileId,
        cityId: UUID? = Constants.cityId
    ) = apply {
        this.updateProfileRequest = UpdateProfileRequestBuilder()
            .name(name)
            .issuerProfileId(issuerProfileId)
            .mobile(mobile)
            .staffPartnerId(staffPartnerId)
            .darkstoreId(darkstoreId)
            .roles(roles)
            .supervisedDarkstore(supervisedDarkstores)
            .vehicle(vehicle)
            .email(email)
            .accountingProfileId(accountingProfileId)
            .cityId(cityId)
            .build()
    }


    @Step("Set list of update name for one profile")
    fun setListUpdateNameProfileRequest(
        amount: Int,
        createProfileRequest: CreateProfileRequest
    ): EmployeePreconditions {
        this.listUpdateProfileRequest = mutableListOf()
        for (i in 0 until amount) {
            var version = i.toLong()
            version++
            this.listUpdateProfileRequest.add(
                UpdateProfileRequestBuilder()
                    .randomName()
                    .issuerProfileId(createProfileRequest.issuerProfileId)
                    .mobile(createProfileRequest.mobile)
                    .staffPartnerId(createProfileRequest.staffPartnerId)
                    .darkstoreId(createProfileRequest.darkstoreId)
                    .roles(createProfileRequest.roles)
                    .vehicle(createProfileRequest.vehicle)
                    .cityId(createProfileRequest.cityId)
                    .accountingProfileId(createProfileRequest.accountingProfileId)
                    .version(version)
                    .build()
            )
        }
        return this

    }

    // Set DS User Update
    @Step("Set ds user for status update")
    fun setUpdateDSUserProfile(
        issuerProfileId: UUID = createProfileRequest.issuerProfileId,
        state: ApiEnum<DarkstoreUserState, String>,
        inactivityReason: String? = null,
        version: Long = 1
    ) = apply {
        this.updateDSUserStatusRequest = UpdateDSUserStatusBuilder()
            .issuerProfileId(issuerProfileId)
            .state(state)
            .inactivityReason(inactivityReason)
            .version(version)
            .build()

    }


    fun fillUpdateProfileStatusOnDarkstore(
        issuerProfileId: UUID = createProfileRequest.issuerProfileId,
        state: ApiEnum<DarkstoreUserState, String>,
        inactivityReason: String? = null,
        version: Long = 1
    ): UpdateDarkstoreUserRequest {
        val request = UpdateDSUserStatusBuilder()
            .issuerProfileId(issuerProfileId)
            .state(state)
            .inactivityReason(inactivityReason)
            .version(version)
            .build()
        return request
    }

    // Search profile
    fun fillGetProfilesRequest(
        mobile: MobileQueryFilter? = null,
        nameLike: String? = null,
        darkstoreId: UUID? = null,
        statuses: List<ApiEnum<EmployeeProfileStatus, String>>? = null,
        roles: List<ApiEnum<EmployeeRole, String>>? = null,
        paging: PagingQueryFilter = PagingQueryFilter(pageSize = 1000)
    ): GetProfilesRequestBuilder {
        val getRequest = GetProfilesRequestBuilder()
        getRequest
            .mobileLike(mobile)
            .nameLike(nameLike)
            .darkstoreId(darkstoreId)
            .statuses(statuses)
            .roles(roles)
            .paging(paging)
        return getRequest
    }

    // AuthenticateProfile
    fun fillAuthProfileBuilder(
        mobile: PhoneNumber,
        password: CharArray
    ): AuthenticateProfileRequestBuilder {
        val authRequest = AuthenticateProfileRequestBuilder()
        authRequest
            .mobile(mobile)
            .password(password)
        return authRequest
    }

    fun fillGetProfilesBuilder(
        profileIds: MutableList<UUID>?,
        profilesCount: Int? = null
    ): GetProfilesByIdsRequestBuilder {
        val request = GetProfilesByIdsRequestBuilder()

        if (profilesCount != null)
            request
                .randomProfilesIds(profilesCount)
        if (profileIds != null)
            request.profileIds(profileIds!!)

        request.build()
        return request
    }

    fun fillSearchDSProfilesRequest(
        darkstoreIds: List<UUID>,
        darkstoreUserStates: List<ApiEnum<DarkstoreUserState, String>>? = null,
        nameLike: String? = null,
        mobileLike: String? = null,
        darkstoreUserRoles: List<ApiEnum<DarkstoreUserRole, String>>? = null,
        staffPartnerIds: List<UUID>? = null,
        vehicleTypes: List<ApiEnum<EmployeeVehicleType, String>>? = null,
        sortingMode: ApiEnum<SearchDarkstoreUsersSortingMode, String>? = null,
        pagingFilter: PagingFilter = PagingFilter(100, null)
    ): SearchDarkstoreUsersRequest {
        return GetDarkstoreUsersRequestBuilder()
            .darkstoreIds(darkstoreIds)
            .darkstoreUserStates(darkstoreUserStates)
            .nameLike(nameLike)
            .mobileLike(mobileLike)
            .darkstoreUserRoles(darkstoreUserRoles)
            .staffPartnerIds(staffPartnerIds)
            .vehicleTypes(vehicleTypes)
            .sortingMode(sortingMode)
            .pagingFilter(pagingFilter)
            .build()
    }

    // Violations
    fun fillStoreDarkstoreUserViolationRequest(
        violationCode: ViolationCode,
        issuerProfileId: UUID = UUID.randomUUID(),
        violationComment: String? = null
    ): StoreDarkstoreUserViolationRequest {
        return StoreDarkstoreUserViolationRequestBuilder()
            .issuerProfileId(issuerProfileId)
            .violationCode(violationCode)
            .violationComment(violationComment)
            .build()
    }

    fun fillStoreDarkstoreUserViolationRequests(
        violationCode: List<ViolationCode>,
        issuerProfileId: UUID = UUID.randomUUID(),
        violationComment: String? = null
    ): List<StoreDarkstoreUserViolationRequest> {
        val requests = mutableListOf<StoreDarkstoreUserViolationRequest>()
        violationCode.forEach {
            requests.add(
                StoreDarkstoreUserViolationRequestBuilder()
                    .issuerProfileId(issuerProfileId)
                    .violationCode(it)
                    .violationComment(violationComment)
                    .build()
            )
        }
        return requests
    }

    fun fillSearchViolationsRequest(
        darkstoreIds: List<UUID>,
        darkstoreUserRoles: List<ApiEnum<DarkstoreUserRole, String>>,
        pagingFilter: PagingFilter = PagingFilter(100, null)
    ): SearchDarkstoreUsersViolationsRequest {
        return SearchDarkstoreUsersViolationsRequestBuilder()
            .darkstoreIds(darkstoreIds)
            .darkstoreUserRoles(darkstoreUserRoles)
            .pagingFilter(pagingFilter)
            .build()
    }

    fun fillGetDarkstoreUsersByProfileIdsRequest(
        profileIds: List<UUID>
    ): GetDarkstoreUsersByProfileIdsRequest {
        return GetDarkstoreUsersByProfileIdsRequestBuilder()
            .profileIds(profileIds)
            .build()
    }

    //Set Internship
    @Step("Set internship")
    fun setCreateInternshipRequest(
        darkstoreId: UUID,
        plannedDate: Instant,
        issuerProfileId: UUID = UUID.randomUUID()

    ) = apply {
        this.createInternshipRequest = CreateInternshipRequestBuilder()
            .darkstoreId(darkstoreId)
            .plannedDate(plannedDate)
            .issuerProfileId(issuerProfileId)
            .build()
    }

    @Step("Set list of internship request")
    fun setListInternshipOfCreatedProfileRequest(
        amount: Int,
        plannedDate: Instant
    ): EmployeePreconditions {
        for (i in 0..amount) {
            this.listCreateInternshipRequest.add(
                CreateInternshipRequestBuilder()
                    .darkstoreId(listCreateProfileRequest()[i].darkstoreId!!)
                    .plannedDate(plannedDate)
                    .issuerProfileId(listCreateProfileRequest()[i].issuerProfileId).build()
            )
        }
        return this
    }

    @Step("Set internship twice")
    fun setCreateInternshipTwiceRequest(
        darkstoreId: UUID,
        plannedDate: Instant,
        issuerProfileId: UUID = createProfileRequest.issuerProfileId

    ) = apply {
        this.createInternshipTwiceRequest = CreateInternshipRequestBuilder()
            .darkstoreId(darkstoreId)
            .plannedDate(plannedDate)
            .issuerProfileId(issuerProfileId)
            .build()
    }

    @Step("Set internship DS request")
    fun setGetInternshipRequest(
        from: Instant,
        to: Instant
    ) = apply {
        this.getInternshipDSRequest = GetDSInternshipRequestBuilder()
            .from(from)
            .to(to)
            .build()
    }

    @Step("Set update internship request")
    fun setUpdateInternshipRequest(
        darkstoreId: UUID,
        plannedDate: Instant,
        issuerProfileId: UUID = createProfileRequest.issuerProfileId,
        version: Long = 1
    ) = apply {
        this.updateInternshipRequest = UpdateInternshipRequestBuilder()
            .darkstoreId(darkstoreId)
            .plannedDate(plannedDate)
            .issuerProfileId(issuerProfileId)
            .version(version)
            .build()
    }

    @Step("Set list of update request for 1 internship")
    fun setListUpdateInternshipRequest(
        amount: Int,
        createProfileRequest: CreateProfileRequest,
        darkstoreId: UUID
    ): EmployeePreconditions {
        this.listUpdateInternshipRequest = mutableListOf()
        for (i in 0 until amount) {
            var version = i.toLong()
            version++
            var seconds: Long = 120
            seconds *= version
            this.listUpdateInternshipRequest.add(
                UpdateInternshipRequestBuilder()
                    .darkstoreId(darkstoreId)
                    .plannedDate(Instant.now().plusSeconds(seconds).truncatedTo(ChronoUnit.SECONDS))
                    .issuerProfileId(createProfileRequest.issuerProfileId)
                    .version(version)
                    .build()
            )
        }
        return this

    }


    @Step("Set cancel internship request")
    fun setCancelInternshipRequest(
        issuerProfileId: UUID = createProfileRequest.issuerProfileId,
    ) = apply {
        this.cancelInternshipRequest = CancelInternshipRequestBuilder()
            .issuerProfileId(issuerProfileId)
            .build()
    }

    @Step("Set reject internship request")
    fun setRejectInternshipRequest(
        rejectionCode: ApiEnum<RejectionCode, String>,
        issuerProfileId: UUID = createProfileRequest.issuerProfileId,
    ) = apply {
        this.rejectInternshipRequest = RejectInternshipRequestBuilder()
            .issuerProfileId(issuerProfileId)
            .rejectionCode(rejectionCode)
            .build()
    }

    @Step("Set close internship request")
    fun setCloseInternshipRequest(
        failureCode: ApiEnum<FailureCode, String>? = null,
        issuerProfileId: UUID = createProfileRequest.issuerProfileId,
        status: ApiEnum<InternshipStatus, String>
    ) = apply {
        this.closeInternshipRequest = CloseInternshipRequestBuilder()
            .issuerProfileId(issuerProfileId)
            .failureCode(failureCode)
            .status(status)
            .build()
    }

    // Kafka 1C

    fun fillPriemNaRabotyEvent(
        accountingContractId: UUID = UUID.randomUUID(),
        accountingProfileId: UUID = UUID.randomUUID(),
        inn: String = StringAndPhoneNumberGenerator.generateRandomInn(),
        proveden: Boolean = true,
        dateInMilliseconds: Long? = System.currentTimeMillis(),
        naimenovanie: String = "Курилко Станислав Юрьевич"
    ): PriemNaRabotuCFZ {
        return PriemNaRabotuCFZ(
            headers = set1CEventHeaders(dateInMilliseconds),
            payload = listOf(
                setPriemNaRabotyPayload(proveden, accountingContractId, inn, accountingProfileId, naimenovanie)
            )
        )
    }


    fun fillPriemNaRabotySpiskomEvent(
        accountingContractId1: UUID = UUID.randomUUID(),
        inn1: String = StringAndPhoneNumberGenerator.generateRandomInn(),
        accountingProfileId1: UUID = UUID.randomUUID(),
        accountingContractId2: UUID = UUID.randomUUID(),
        inn2: String = StringAndPhoneNumberGenerator.generateRandomInn(),
        accountingProfileId2: UUID = UUID.randomUUID(),
        proveden: Boolean = true,
        dateInMilliseconds: Long? = System.currentTimeMillis(),
    ): PriemNaRabotuSpiskomCFZ {
        return PriemNaRabotuSpiskomCFZ(
            headers = set1CEventHeaders(dateInMilliseconds),
            payload = listOf(
                PayloadList(
                    objectName = "PriemNaRabotuSpiskom",
                    proveden = proveden,
                    guid = UUID.randomUUID(),
                    sotrudniki = listOf(
                        setPriemNaRabotuSpiskomSotrudniki(accountingContractId1, inn1),
                        setPriemNaRabotuSpiskomSotrudniki(accountingContractId2, inn2),
                    )
                )
            )
        )
    }


    fun set1CEventHeaders(
        dateInMilliseconds: Long? = System.currentTimeMillis(),
        event: String = "ПриемНаРаботу"
    ): Headers {
        return Headers(
            id = UUID.randomUUID(),
            date = "2021-10-13T14:53:50Z",
            event = event,
            dateInMilliseconds = dateInMilliseconds,
            multithreadingAnalytics = "",
            multithreadingDate = "2021-10-05T13:38:47Z",
            singleThreaded = false,
            addressForResult = "http://hw-011011005.samokat.io/base4_sobolev_01/hs/RequestAPI"
        )
    }

    fun setPriemNaRabotyPayload(
        proveden: Boolean = true,
        accountingContractId: UUID = UUID.randomUUID(),
        inn: String = StringAndPhoneNumberGenerator.generateRandomInn(),
        accountingProfileId: UUID = UUID.randomUUID(),
        naimenovanie: String = "Курилко Станислав Юрьевич"
    ): Payload {
        return Payload(
            objectName = "PriemNaRabotu",
            proveden = proveden,
            guid = UUID.randomUUID(),
            sotrudnik = setSotrudnik(accountingContractId),
            fizicheskoeLitso = setFizLitso(inn, naimenovanie, accountingProfileId),
            dolzhnost = setDolzhnost(),
            dataPriema = "2021-10-04T21:00:00Z",
            dataZaversheniyaTrudovogoDogovora = "0001-01-01T00:00:00Z"

        )
    }

    fun setPriemNaRabotuSpiskomSotrudniki(
        accountingContractId: UUID,
        inn: String,
        accountingProfileId: UUID = UUID.randomUUID()
    ): Sotrudniki {
        return Sotrudniki(
            sotrudnik = setSotrudnik(accountingContractId),
            fizicheskoeLitso = setFizLitso(inn, guid = accountingProfileId),
            dolzhnost = setDolzhnost(),
            dataPriema = "2021-09-30T21:00:00Z",
            dataZaversheniyaTrudovogoDogovora = "0001-01-01T00:00:00Z"
        )
    }

    fun setSotrudnik(accountingContractId: UUID): Sotrudnik {
        return Sotrudnik(
            objectName = "Sotrudniki",
            guid = accountingContractId
        )
    }

    fun setFizLitso(
        inn: String?,
        naimenovanie: String = StringAndPhoneNumberGenerator.generateRandomString(15),
        guid: UUID = UUID.randomUUID()
    ): FizicheskoeLitso {
        return FizicheskoeLitso(
            objectName = "FizicheskieLitsa",
            guid = guid,
            inn = inn,
            naimenovanie = naimenovanie
        )
    }

    fun setDolzhnost(
        guid: UUID = UUID.randomUUID(),
        naimenovanie: String = StringAndPhoneNumberGenerator.generateRandomString(10)
    ): Dolzhnost {
        return Dolzhnost(
            objectName = "Dolzhnosti",
            guid = guid,
            naimenovanie = naimenovanie
        )
    }

    fun setKadrovyyPerevodPayload(
        proveden: Boolean = true,
        accountingContractId: UUID = UUID.randomUUID(),
        inn: String = StringAndPhoneNumberGenerator.generateRandomInn(),
        naimenovanie: String = "Курилко Станислав Юрьевич",
        accountingProfileId: UUID = UUID.randomUUID(),
        dataNachala: String = "2021-10-05T21:00:00Z",
        dataOkonchaniya: String = "0001-01-01T00:00:00Z",
        payloadGuid: UUID = UUID.randomUUID(),
        dolzhnostGuid: UUID = UUID.randomUUID(),
        dolzhnostNaimenovanie: String = StringAndPhoneNumberGenerator.generateRandomString(10)
    ): PayloadPerevod {
        return PayloadPerevod(
            objectName = "KadrovyyPerevod",
            proveden = proveden,
            guid = payloadGuid,
            sotrudnik = setSotrudnik(accountingContractId),
            fizicheskoeLitso = setFizLitso(inn, guid = accountingProfileId, naimenovanie = naimenovanie),
            dolzhnost = setDolzhnost(dolzhnostGuid, dolzhnostNaimenovanie),
            dataNachala = dataNachala,
            dataOkonchaniya = dataOkonchaniya
        )
    }

    fun fillKadrovyyPerevodEvent(
        accountingContractId: UUID = UUID.randomUUID(),
        accountingProfileId: UUID = UUID.randomUUID(),
        inn: String = StringAndPhoneNumberGenerator.generateRandomInn(),
        proveden: Boolean = true,
        dateInMilliseconds: Long? = System.currentTimeMillis(),
        naimenovanie: String = "Курилко Станислав Юрьевич",
        dataNachala: String = "2021-10-05T21:00:00Z",
        dataOkonchaniya: String = "0001-01-01T00:00:00Z",
        payloadGuid: UUID = UUID.randomUUID(),
        dolzhnostGuid: UUID = UUID.randomUUID(),
        dolzhnostNaimenovanie: String = StringAndPhoneNumberGenerator.generateRandomString(10)
    ): KadrovyyPerevodCFZ {
        return KadrovyyPerevodCFZ(
            headers = set1CEventHeaders(dateInMilliseconds, "КадровыйПеревод"),
            payload = listOf(
                setKadrovyyPerevodPayload(
                    payloadGuid = payloadGuid,
                    proveden = proveden,
                    accountingContractId = accountingContractId,
                    inn = inn,
                    naimenovanie = naimenovanie,
                    accountingProfileId = accountingProfileId,
                    dataNachala = dataNachala,
                    dataOkonchaniya = dataOkonchaniya,
                    dolzhnostGuid = dolzhnostGuid,
                    dolzhnostNaimenovanie = dolzhnostNaimenovanie
                )
            )
        )
    }

    fun fillKadrovyyPerevodSotrudnikiList(
        accountingContractId: UUID,
        inn: String,
        accountingProfileId: UUID,
        fio: String = "Скворцов2 Ринат Ильнурович",
        dolzhnost: String,
        dataNachala: String = "2021-10-05T21:00:00Z",
        dataOkonchaniya: String = "0001-01-01T00:00:00Z"
    ): SotrudnikiPerevod {
        return SotrudnikiPerevod(
            sotrudnik = Sotrudnik(
                objectName = "Sotrudniki",
                guid = accountingContractId
            ),
            fizicheskoeLitso = setFizLitso(inn, guid = accountingProfileId, naimenovanie = fio),
            dolzhnost = Dolzhnost(
                objectName = "Dolzhnosti",
                guid = UUID.randomUUID(),
                naimenovanie = dolzhnost
            ),
            dataNachala = dataNachala,
            dataOkonchaniya = dataOkonchaniya
        )
    }

    fun fillKadrovyyPerevodSpiskomEvent(
        accountingContractId1: UUID = UUID.randomUUID(),
        accountingProfileId1: UUID = UUID.randomUUID(),
        accountingContractId2: UUID = UUID.randomUUID(),
        accountingProfileId2: UUID = UUID.randomUUID(),
        inn1: String = StringAndPhoneNumberGenerator.generateRandomInn(),
        inn2: String = StringAndPhoneNumberGenerator.generateRandomInn(),
        naimenovanie1: String = "Товаровед1",
        naimenovanie2: String = "Товаровед2",
        proveden: Boolean = true,
        dataNachala: String = "2021-10-05T21:00:00Z",
        dataOkonchaniya: String = "0001-01-01T00:00:00Z"
    ): KadrovyyPerevodSpiskomCFZ {
        return KadrovyyPerevodSpiskomCFZ(
            headers = set1CEventHeaders(System.currentTimeMillis(), "КадровыйПереводСписком"),
            payload = listOf(
                PayloadPerevodList(
                    objectName = "KadrovyyPerevodSpiskom",
                    proveden = proveden,
                    guid = UUID.randomUUID(),
                    sotrudniki = listOf(
                        fillKadrovyyPerevodSotrudnikiList(
                            accountingContractId1,
                            inn1,
                            accountingProfileId1,
                            "Скворцов2 Ринат Ильнурович",
                            naimenovanie1,
                            dataNachala,
                            dataOkonchaniya
                        ),
                        fillKadrovyyPerevodSotrudnikiList(
                            accountingContractId2,
                            inn2,
                            accountingProfileId2,
                            "Соловьев2 Заур Исябагович",
                            naimenovanie2,
                            dataNachala,
                            dataOkonchaniya
                        )
                    )
                )
            )
        )
    }

    fun fillOutsourseContractEvent(
        dateInMilliseconds: Long? = System.currentTimeMillis(),
        mobile: String? = Constants.mobile1.asStringWithoutPlus(),
        event: String = "ВнешниеСотрудники",
        naimenovanie: String = "Курилко Станислав Юрьевич",
        accountingProfileId: UUID = UUID.randomUUID(),
        accountingContractId: String = UUID.randomUUID().toString(),
        dataUvolneniya: String? = "0001-01-01T00:00:00Z"
    ): VneshnieSotrudniki {
        return VneshnieSotrudniki(
            headers = set1CEventHeaders(dateInMilliseconds, event),
            payload = listOf(
                VneshnieSotrudnikiPayload(
                    objectName = "VneshnieSotrudniki",
                    guid = accountingContractId,
                    naimenovanie = naimenovanie,
                    dolzhnost = setDolzhnost(),
                    vidDogovora = setVidDogovora(),
                    dataOformleniya = "2021-07-06T21:00:00Z",
                    dataUvolneniya = dataUvolneniya,
                    partner = setPartner(),
                    telefon = mobile,
                    fizicheskoeLitso = setFizLitso(
                        guid = accountingProfileId,
                        inn = null,
                        naimenovanie = naimenovanie
                    )
                )
            )
        )
    }

    fun setVidDogovora(
        guid: UUID = UUID.randomUUID(),
        naimenovanie: String = StringAndPhoneNumberGenerator.generateRandomString(10)
    ): VidDogovora {
        return VidDogovora(
            objectName = "VidyDogovorov",
            guid = guid.toString(),
            naimenovanie = naimenovanie
        )
    }

    fun setPartner(
        guid: UUID = UUID.randomUUID(),
        naimenovanie: String = StringAndPhoneNumberGenerator.generateRandomString(10)
    ): Partner {
        return Partner(
            objectName = "Partnery",
            guid = guid.toString(),
            naimenovanie = naimenovanie
        )
    }

    //Create partner
    @Step("Fill create partner request")
    fun fillCreatePartnerRequest(
        title: String,
        shortTitle: String,
        type: ApiEnum<StaffPartnerType, String> = ApiEnum(StaffPartnerType.OUT_STAFF)
    ): CreatePartnerRequest {
        return CreatePartnerRequestBuilder()
            .title(title)
            .shortTitle(shortTitle)
            .type(type)
            .build()
    }

    @Step("Fill search contracts request")
    fun fillSearchContractsRequest(
        profileIds: List<UUID>,
        activeUntil: Instant? = null
    ): SearchUsersContractsRequest {
        return SearchUsersContractsRequest(
            profileIds = profileIds,
            activeUntil = activeUntil
        )
    }

    @Step("Fill first login event")
    fun fillFirstLoginEvent(
        profileId: UUID,
        firstLoginAt: Instant = Instant.now()
    ): FirstLoginEvent {
        return FirstLoginEvent(
            profileId = profileId.toString(),
            firstLoginAt = firstLoginAt.toString()
        )
    }

    @Step("Fill decline requisition request")
    fun fillDeclineRequisitionRequest(issuerId: UUID = UUID.randomUUID()): DeclineProfileRequisitionRequest {
        return DeclineProfileRequisitionRequest(issuerProfileId = issuerId)
    }

    @Step("Fill search requisitions request")
    fun fillSearchRequisitionsRequest(
        mobile: MobileQueryFilter? = null,
        nameLike: String? = null,
        statuses: List<ApiEnum<ProfileRequisitionStatus, String>>? = null,
        paging: PagingFilter = PagingFilter(pageSize = 100, pageMark = null),
        types: List<ApiEnum<ProfileRequisitionType, String>>? = null
    ): SearchProfileRequisitionsRequest {
        return SearchProfileRequisitionsRequestBuilder()
            .mobile(mobile)
            .nameLike(nameLike)
            .statuses(statuses)
            .pagingFilter(paging)
            .types(types)
            .build()
    }

    @Step("Fill search contracts request")
    fun fillSearchContractsRequestByAccountingProfileId(
        accountingProfileIds: List<String>
    ): SearchAccountingProfileContractsRequest {
        return SearchAccountingProfileContractsRequest(
            accountingProfileIds = accountingProfileIds
        )
    }
}