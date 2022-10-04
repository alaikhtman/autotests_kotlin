package ru.samokat.mysamokat.tests.checkers

import io.qameta.allure.Step
import org.assertj.core.api.SoftAssertions
import org.eclipse.jetty.http.HttpStatus
import org.jetbrains.exposed.sql.ResultRow
import org.springframework.stereotype.Service
import ru.samokat.employeeprofiles.api.common.domain.EmployeeProfileStatus
import ru.samokat.employeeprofiles.api.common.domain.EmployeeProfileView
import ru.samokat.employeeprofiles.api.common.domain.EmployeeVehicleType
import ru.samokat.employeeprofiles.api.common.domain.StaffPartnerView
import ru.samokat.employeeprofiles.api.contacts.EmployeeProfileContactView
import ru.samokat.employeeprofiles.api.darkstoreusers.domain.DarkstoreUserExtendedView
import ru.samokat.employeeprofiles.api.darkstoreusers.domain.DarkstoreUserRole
import ru.samokat.employeeprofiles.api.darkstoreusers.domain.DarkstoreUserState
import ru.samokat.employeeprofiles.api.darkstoreusers.domain.DarkstoreUserView
import ru.samokat.employeeprofiles.api.darkstoreusers.internships.create.CreateInternshipRequest
import ru.samokat.employeeprofiles.api.darkstoreusers.internships.domain.InternshipStatus
import ru.samokat.employeeprofiles.api.darkstoreusers.internships.get.InternshipsView
import ru.samokat.employeeprofiles.api.darkstoreusers.internships.update.UpdateInternshipRequest
import ru.samokat.employeeprofiles.api.darkstoreusers.violations.dictionary.get.ViolationView
import ru.samokat.employeeprofiles.api.darkstoreusers.violations.domain.ViolationCode
import ru.samokat.employeeprofiles.api.darkstoreusers.violations.domain.ViolationSeverity
import ru.samokat.employeeprofiles.api.darkstoreusers.violations.get.DarkstoreUserViolationView
import ru.samokat.employeeprofiles.api.darkstoreusers.violations.search.ExtendedDarkstoreUserViolationView
import ru.samokat.employeeprofiles.api.darkstoreusers.violations.store.StoreDarkstoreUserViolationRequest
import ru.samokat.employeeprofiles.api.profilerequisitions.domain.ProfileRequisitionStatus
import ru.samokat.employeeprofiles.api.profilerequisitions.domain.ProfileRequisitionType
import ru.samokat.employeeprofiles.api.profilerequisitions.get.GetProfileRequisitionView
import ru.samokat.employeeprofiles.api.profilerequisitions.search.SearchProfileRequisitionsView
import ru.samokat.employeeprofiles.api.profiles.create.CreateProfileRequest
import ru.samokat.employeeprofiles.api.profiles.getprofiles.GetProfilesView
import ru.samokat.employeeprofiles.api.profiles.update.UpdateProfileRequest
import ru.samokat.employeeprofiles.api.signatures.getbyprofileid.EmployeeSignatureView
import ru.samokat.employeeprofiles.api.staffpartners.createpartners.CreatePartnerRequest
import ru.samokat.my.domain.PhoneNumber
import ru.samokat.my.domain.employee.EmployeeRole
import ru.samokat.my.rest.api.enum.ApiEnum
import ru.samokat.mysamokat.tests.SuiteBase.Companion.jacksonObjectMapper
import ru.samokat.mysamokat.tests.dataproviders.employee.CreateProfileRequestBuilder
import ru.samokat.mysamokat.tests.dataproviders.employee.DarkstoreUsersState
import ru.samokat.mysamokat.tests.dataproviders.helpers.database.DarkstoreUser
import ru.samokat.mysamokat.tests.dataproviders.helpers.database.employee_profiles_backend.*
import ru.samokat.mysamokat.tests.dataproviders.helpers.database.employee_profiles_backend.Contract
import ru.samokat.mysamokat.tests.helpers.controllers.events.*
import ru.samokat.mysamokat.tests.helpers.controllers.events.employee.*
import java.time.Duration
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*


@Service
class EmployeeAssertion {

    private val softAssertion = SoftAssertions()
    fun getSoftAssertion(): SoftAssertions {
        return softAssertion
    }

    // Status code checkers

    @Step("Check status code 409")
    fun checkStatusCodeConflict(responseCode: Int): EmployeeAssertion {
        getSoftAssertion().assertThat(responseCode).isEqualTo(HttpStatus.CONFLICT_409)
        return this
    }

    @Step("Check status code 404")
    fun checkStatusNotFound(responseCode: Int): EmployeeAssertion {
        getSoftAssertion().assertThat(responseCode).isEqualTo(HttpStatus.NOT_FOUND_404)
        return this
    }

    @Step("Check status code 400")
    fun checkStatusBadRequest(responseCode: Int): EmployeeAssertion {
        getSoftAssertion().assertThat(responseCode).isEqualTo(HttpStatus.BAD_REQUEST_400)
        return this
    }

    // Assertions in DB
    @Step("Check profile darkstore in DB")
    fun checkDarkstoreIdInDatabase(darkstoreId: UUID, profileFromDB: ResultRow): EmployeeAssertion {
        getSoftAssertion().assertThat(profileFromDB[Profile.darkstoreId])
            .isEqualTo(darkstoreId)
        return this
    }

    @Step("Check profile city in DB")
    fun checkCityIdInDatabase(cityId: UUID? = null, profileFromDB: ResultRow): EmployeeAssertion {
        getSoftAssertion().assertThat(profileFromDB[Profile.cityId])
            .isEqualTo(cityId)
        return this
    }


    @Step("Check profile first name in DB")
    fun checkFirstNameInDatabase(firstName: String, profileFromDB: ResultRow): EmployeeAssertion {
        getSoftAssertion().assertThat(profileFromDB[Profile.firstName])
            .isEqualTo(firstName)
        return this
    }

    @Step("Check profile last name in DB")
    fun checkLastNameInDatabase(lastName: String, profileFromDB: ResultRow): EmployeeAssertion {
        getSoftAssertion().assertThat(profileFromDB[Profile.lastName])
            .isEqualTo(lastName)
        return this
    }

    @Step("Check profile middle name in DB")
    fun checkMiddleNameInDatabase(middleName: String, profileFromDB: ResultRow): EmployeeAssertion {
        getSoftAssertion().assertThat(profileFromDB[Profile.middleName])
            .isEqualTo(middleName)
        return this
    }

    @Step("Check mobile in DB")
    fun checkMobileInDatabase(mobile: String, profileFromDB: ResultRow): EmployeeAssertion {
        getSoftAssertion().assertThat(profileFromDB[Profile.mobile])
            .isEqualTo(mobile)
        return this
    }

    @Step("Check profile partnerId in DB")
    fun checkPartnerIdInDatabase(partnerId: UUID, profileFromDB: ResultRow): EmployeeAssertion {
        getSoftAssertion().assertThat(profileFromDB[Profile.partnerId])
            .isEqualTo(partnerId)
        return this
    }

    @Step("Check profile email in DB")
    fun checkEmailInDatabase(email: String?, profileFromDB: ResultRow): EmployeeAssertion {
        getSoftAssertion().assertThat(profileFromDB[Profile.email])
            .isEqualTo(email)
        return this
    }

    @Step("Check profile vehicle in DB")
    fun checkVehicleInDatabase(vehicleFromDB: String, vehicle: String): EmployeeAssertion {
        getSoftAssertion().assertThat(vehicleFromDB)
            .isEqualTo(vehicle)
        return this
    }

    @Step("Check profile contract in DB")
    fun checkProfileAccountingIdInDatabase(accountingId: String, profileFromDB: ResultRow): EmployeeAssertion {
        getSoftAssertion().assertThat(profileFromDB[Profile.accountingProfileId])
            .isEqualTo(accountingId)
        return this
    }

    @Step("Check profile role in DB")
    fun checkProfileRolesInDatabase(
        profileFromDB: ResultRow,
        roles: List<ApiEnum<EmployeeRole, String>>
    ): EmployeeAssertion {
        if (profileFromDB[Profile.roles].size == roles.size) {
            for (i in roles.indices) {
                //  getSoftAssertion().assertThat(profileFromDB[Profile.roles][i].toString()).isEqualTo(roles[i].value.toString())
                getSoftAssertion().assertThat(profileFromDB[Profile.roles].containsAll(roles.map { it.value }))
                    .isTrue
            }
        } else {
            getSoftAssertion().fail("Roles' amount are not equal")
        }
        return this
    }

    @Step("Check profile supervised darkstores in DB")
    fun checkProfileSupervisedDarkstoreInDatabase(
        supervisedDarkstores: List<UUID>?,
        supervisedDarkstoresFromDB: MutableList<UUID>
    ): EmployeeAssertion {
        getSoftAssertion().assertThat(supervisedDarkstoresFromDB.containsAll(supervisedDarkstores!!))
            .isTrue
        return this
    }

    @Step("Check profile supervised darkstores not in DB")
    fun checkProfileSupervisedDarkstoreNotInDatabase(
        supervisedDarkstores: List<UUID>?,
        supervisedDarkstoresFromDB: MutableList<UUID>
    ): EmployeeAssertion {
        getSoftAssertion().assertThat(supervisedDarkstoresFromDB.containsAll(supervisedDarkstores!!))
            .isFalse
        return this
    }

    @Step("Check profile status is disabled in DB")
    fun checkProfileStatusDisable(status: String): EmployeeAssertion {
        getSoftAssertion().assertThat(status).isEqualTo("disabled")
        return this
    }

    @Step("Check profile version in DB")
    fun checkProfileVersion(version: Long, profileFromDB: ResultRow): EmployeeAssertion {
        getSoftAssertion().assertThat(profileFromDB[Profile.version]).isEqualTo(version)
        return this
    }

    @Step("Check profile in table 'Profile'")
    fun checkProfileFromProfileDB(
        createRequest: CreateProfileRequest,
        profileFromDB: ResultRow
    ): EmployeeAssertion {

        val createdRoles = createRequest.roles.map { it.value }
        getSoftAssertion().assertThat(profileFromDB[Profile.firstName])
            .isEqualTo(createRequest.name.firstName)
        getSoftAssertion().assertThat(profileFromDB[Profile.lastName])
            .isEqualTo(createRequest.name.lastName)
        getSoftAssertion().assertThat(profileFromDB[Profile.roles].count())
            .isEqualTo(createRequest.roles.count())
        getSoftAssertion().assertThat(profileFromDB[Profile.cityId])
            .isEqualTo(createRequest.cityId)
        getSoftAssertion().assertThat(profileFromDB[Profile.roles].containsAll(createdRoles)).isTrue

        when {
            createdRoles.contains(EmployeeRole.DELIVERYMAN.value) -> {
                getSoftAssertion().assertThat(profileFromDB[Profile.darkstoreId])
                    .isEqualTo(createRequest.darkstoreId)
                getSoftAssertion().assertThat(profileFromDB[Profile.partnerId])
                    .isEqualTo(createRequest.staffPartnerId)
            }
            createdRoles.contains(EmployeeRole.PICKER.value) -> {
                getSoftAssertion().assertThat(profileFromDB[Profile.darkstoreId])
                    .isEqualTo(createRequest.darkstoreId)
                getSoftAssertion().assertThat(profileFromDB[Profile.partnerId])
                    .isEqualTo(createRequest.staffPartnerId)
            }
            createdRoles.contains(EmployeeRole.FORWARDER.value) -> {
                getSoftAssertion().assertThat(profileFromDB[Profile.darkstoreId])
                    .isEqualTo(createRequest.darkstoreId)
            }
            createdRoles.contains(EmployeeRole.DARKSTORE_ADMIN.value) -> {
                getSoftAssertion().assertThat(profileFromDB[Profile.darkstoreId])
                    .isEqualTo(createRequest.darkstoreId)
            }
            createdRoles.contains(EmployeeRole.GOODS_MANAGER.value) -> {
                getSoftAssertion().assertThat(profileFromDB[Profile.darkstoreId])
                    .isEqualTo(createRequest.darkstoreId)
            }
        }

        return this
    }

    @Step("Check deliveryman vehicle in DB is empty")
    fun checkVehicleIsEmpty(profileVehicleFromDB: String): EmployeeAssertion {
        getSoftAssertion().assertThat(profileVehicleFromDB).isEqualTo("none")
        return this
    }

    @Step("Check deliveryman vehicle in Database")
    fun checkDeliverymanVehicleInDB(expectedVehicle: String, profileVehicleFromDB: String): EmployeeAssertion {
        getSoftAssertion().assertThat(profileVehicleFromDB).isEqualTo(expectedVehicle)
        return this
    }

    @Step("Check profile middle name is null in DB")
    fun checkMiddleNameIsNull(profileFromDB: ResultRow): EmployeeAssertion {
        getSoftAssertion().assertThat(profileFromDB[Profile.middleName])
            .isNull()
        return this
    }

    @Step("Check supervisedDarkstores in DB")
    fun checkSupervisedDarkstoresInDatabase(
        expectedProfile: CreateProfileRequest,
        supervisedDarkstoresFromDB: MutableList<UUID>
    ): EmployeeAssertion {
        getSoftAssertion()
            .assertThat(supervisedDarkstoresFromDB.containsAll(expectedProfile.supervisedDarkstores!!))
            .isTrue

        return this
    }

    // DS User check in DB
    @Step("Check DS Users in DB exists")
    fun checkDsUserExistsInDatabase(dsUserExists: Boolean): EmployeeAssertion {
        getSoftAssertion().assertThat(dsUserExists)
            .isTrue
        return this
    }

    @Step("Check full DS User Info in DB")
    fun checkDSUserInDatabase(
        dsUserProfile: ResultRow,
        darkstoreId: UUID,
        state: Int
    ): EmployeeAssertion {
        getSoftAssertion().assertThat(dsUserProfile[DarkstoreUser.darkstoreId])
            .isEqualTo(darkstoreId)
        getSoftAssertion().assertThat(dsUserProfile[DarkstoreUser.state])
            .isEqualTo(state)
        return this
    }

    @Step("Check intern status for DS user in API")
    fun checkInternStatusForDSUser(
        darkstoreUserView: DarkstoreUserView,
        internStatus: Boolean
    ): EmployeeAssertion {
        getSoftAssertion().assertThat(darkstoreUserView.isIntern).isEqualTo(internStatus)
        return this
    }


    @Step("Check DS Users in DB doesn't exist")
    fun checkDsUserNotInDatabase(dsUserExists: Boolean): EmployeeAssertion {
        getSoftAssertion().assertThat(dsUserExists)
            .isFalse
        return this
    }

    fun checkDsUserActivityNotInDatabase(dsUserActivityExists: Boolean): EmployeeAssertion {
        getSoftAssertion().assertThat(dsUserActivityExists)
            .isFalse
        return this
    }

    @Step("Check full DS User Activity Info in DB")
    fun checkDSUserActivityInDatabase(
        dsUserActivity: ResultRow,
        darkstoreId: UUID,
        status: String
    ): EmployeeAssertion {
        getSoftAssertion().assertThat(dsUserActivity[DarkstoreUserActivity.darkstoreId])
            .isEqualTo(darkstoreId)
        getSoftAssertion().assertThat(dsUserActivity[DarkstoreUserActivity.status])
            .isEqualTo(status)

        return this
    }

    @Step("Check full DS User Log Info in DB")
    fun checkDSUserLogInDatabase(
        dsUserLog: MutableList<ResultRow>,
        amount: Int,
        index: Int,
        type: String
    ): EmployeeAssertion {
        getSoftAssertion().assertThat(dsUserLog.count()).isEqualTo(amount)
        getSoftAssertion().assertThat(dsUserLog[index][DarkstoreUserLog.type])
            .isEqualTo(type)

        return this
    }

    @Step("Check new profile in darkstore_users tables")
    fun checkProfileInDSUserTable(
        expectedProfile: CreateProfileRequest,
        profileFromDarkstoreTable: ResultRow,
        role: EmployeeRole,
        dsState: Int
    ): EmployeeAssertion {
        getSoftAssertion().assertThat(profileFromDarkstoreTable[DarkstoreUser.darkstoreId])
            .isEqualTo(expectedProfile.darkstoreId!!)
        getSoftAssertion().assertThat(profileFromDarkstoreTable[DarkstoreUser.state])
            .isEqualTo(dsState)
        getSoftAssertion().assertThat(profileFromDarkstoreTable[DarkstoreUser.role])
            .isEqualTo(role.toString())
        return this
    }

    @Step("Check profile in darkstore_users_log tables")
    fun checkNewProfileInDSUserLogTable(
        expectedProfile: CreateProfileRequestBuilder,
        profileFromDarkstoreLogTable: ResultRow,
        role: EmployeeRole
    ): EmployeeAssertion {
        getSoftAssertion().assertThat(profileFromDarkstoreLogTable[DarkstoreUserLog.darkstoreId])
            .isEqualTo(expectedProfile.getDarkstoreId())
        getSoftAssertion().assertThat(profileFromDarkstoreLogTable[DarkstoreUserLog.type])
            .isEqualTo("new")
        getSoftAssertion().assertThat(profileFromDarkstoreLogTable[DarkstoreUserLog.role])
            .isEqualTo(role.toString())
        return this
    }

    @Step("Check profile in darkstore_users_log tables")
    fun checkProfileInDSUserLogTable(
        expectedProfile: CreateProfileRequest,
        profileFromDarkstoreLogTable: MutableList<ResultRow>,
        role: EmployeeRole
    ): EmployeeAssertion {

        val newRow = profileFromDarkstoreLogTable.filter { it[DarkstoreUserLog.type] == "new" }.first()
        val updateRow = profileFromDarkstoreLogTable.filter { it[DarkstoreUserLog.type] == "updated" }.first()

        getSoftAssertion().assertThat(newRow[DarkstoreUserLog.darkstoreId])
            .isEqualTo(expectedProfile.darkstoreId!!)
        getSoftAssertion().assertThat(newRow[DarkstoreUserLog.type])
            .isEqualTo("new")
        getSoftAssertion().assertThat(newRow[DarkstoreUserLog.role])
            .isEqualTo(role.toString())

        getSoftAssertion().assertThat(updateRow[DarkstoreUserLog.darkstoreId])
            .isEqualTo(expectedProfile.darkstoreId!!)
        getSoftAssertion().assertThat(updateRow[DarkstoreUserLog.type])
            .isEqualTo("updated")
        getSoftAssertion().assertThat(updateRow[DarkstoreUserLog.role])
            .isEqualTo(role.toString())

        return this
    }

    @Step("Check new profile in darkstore_users tables")
    fun checkNewProfileFromDSUserTable(
        expectedProfile: CreateProfileRequest,
        actualProfile: ResultRow,
        role: EmployeeRole
    ): EmployeeAssertion {
        getSoftAssertion().assertThat(actualProfile[DarkstoreUser.darkstoreId])
            .isEqualTo(expectedProfile.darkstoreId)
        getSoftAssertion().assertThat(actualProfile[DarkstoreUser.state])
            .isEqualTo(DarkstoreUsersState.NEW.dbId)
            .isEqualTo(DarkstoreUsersState.NEW.dbId)
        getSoftAssertion().assertThat(actualProfile[DarkstoreUser.role])
            .isEqualTo(role.toString())
        return this
    }

    @Step("Check profile in darkstore_users_log tables")
    fun checkNewProfileFromDSUserLogTable(
        expectedProfile: CreateProfileRequest,
        actualProfile: ResultRow,
        role: EmployeeRole
    ): EmployeeAssertion {
        getSoftAssertion().assertThat(actualProfile[DarkstoreUserLog.darkstoreId])
            .isEqualTo(expectedProfile.darkstoreId)
        getSoftAssertion().assertThat(actualProfile[DarkstoreUserLog.type])
            .isEqualTo("new")
        getSoftAssertion().assertThat(actualProfile[DarkstoreUserLog.role])
            .isEqualTo(role.toString())
        return this
    }

    @Step("Check profile  in darkstore_users_activity tables")
    fun checkNewProfileFromDSUserActivityTable(
        expectedProfile: CreateProfileRequest,
        actualProfile: ResultRow,
        role: EmployeeRole
    ): EmployeeAssertion {
        getSoftAssertion().assertThat(actualProfile[DarkstoreUserActivity.darkstoreId])
            .isEqualTo(expectedProfile.darkstoreId)
        getSoftAssertion().assertThat(actualProfile[DarkstoreUserActivity.status])
            .isEqualTo("waiting_for_fetch")
        getSoftAssertion().assertThat(actualProfile[DarkstoreUserActivity.role])
            .isEqualTo(role.toString())
        return this
    }

    //Internship
    @Step("Check internship in table 'Internship'")
    fun checkInternshipFromDB(
        darkstoreId: UUID,
        role: String,
        status: String,
        internshipFromDB: ResultRow
    ): EmployeeAssertion {
        getSoftAssertion().assertThat(internshipFromDB[Internship.darkstoreId])
            .isEqualTo(darkstoreId)
        getSoftAssertion().assertThat(internshipFromDB[Internship.role])
            .isEqualTo(role)
        getSoftAssertion().assertThat(internshipFromDB[Internship.status])
            .isEqualTo(status)

        return this
    }


    @Step("Check Internship in DB doesn't exist")
    fun checkInternshipNotInDatabase(internshipExists: Boolean): EmployeeAssertion {
        getSoftAssertion().assertThat(internshipExists)
            .isFalse
        return this
    }


    @Step("Check Internship Log  in DB")
    fun checkInternshipLogFromDB(
        InternshipLogArray: MutableList<ResultRow>,
        amount: Int,
        index: Int,
        type: String
    ): EmployeeAssertion {
        getSoftAssertion().assertThat(InternshipLogArray.count()).isEqualTo(amount)
        getSoftAssertion().assertThat(InternshipLogArray[index][InternshipLog.type])
            .isEqualTo(type)

        return this
    }

    @Step("Check Internship API response")
    fun checkInternshipAPIResponse(
        actualInternship: InternshipsView,
        expectedInternship: CreateInternshipRequest,
        status: ApiEnum<InternshipStatus, String>
    ): EmployeeAssertion {
        for (i in 0 until actualInternship.internships.count()) {
            getSoftAssertion().assertThat(expectedInternship.darkstoreId)
                .isEqualTo(actualInternship.internships[i].darkstoreId)
            getSoftAssertion().assertThat(expectedInternship.plannedDate)
                .isEqualTo(actualInternship.internships[i].plannedDate)
            getSoftAssertion().assertThat(actualInternship.internships[i].status).isEqualTo(status)
        }
        return this
    }


    @Step("Check several Internship API response")
    fun checkSeveralInternshipAPIResponse(
        actualInternship: InternshipsView,
        expectedInternship: MutableList<CreateInternshipRequest>,
        status: ApiEnum<InternshipStatus, String>
    ): EmployeeAssertion {
        for (i in 0 until expectedInternship.count()) {
            checkInternshipAPIResponse(actualInternship, expectedInternship[i], status)
        }
        return this
    }

    @Step("Check Updated internship API response")
    fun checkUpdatedInternshipAPIResponse(
        actualInternship: InternshipsView, expectedInternship: UpdateInternshipRequest
    ): EmployeeAssertion {

        getSoftAssertion().assertThat(expectedInternship.darkstoreId)
            .isEqualTo(actualInternship.internships.first().darkstoreId)
        getSoftAssertion().assertThat(expectedInternship.plannedDate)
            .isEqualTo(actualInternship.internships.first().plannedDate)

        return this
    }

    @Step("Check Empty Internship API response")
    fun checkInternshipAPIisEmptyResponse(
        expectedInternship: InternshipsView
    ): EmployeeAssertion {

        getSoftAssertion().assertThat(expectedInternship.internships.size)
            .isEqualTo(0)


        return this
    }


    // Password check
    @Step("Check password was updated")
    fun checkPasswordHashWasUpdated(newPassword: String, oldPassword: String): EmployeeAssertion {
        getSoftAssertion().assertThat(newPassword).isNotEqualTo(oldPassword)
        return this
    }

    //Logging
    @Step("Check full Profile Log Info in DB")
    fun checkProfileLogInDatabase(
        profileLog: MutableList<ResultRow>,
        amount: Int,
        index: Int,
        type: String,
        issuerId: UUID,
        version: Long
    ): EmployeeAssertion {
        getSoftAssertion().assertThat(profileLog.count()).isEqualTo(amount)
        getSoftAssertion().assertThat(profileLog[index][ProfileLog.type]).isEqualTo(type)
        getSoftAssertion().assertThat(profileLog[index][ProfileLog.issuerId]).isEqualTo(issuerId)
        getSoftAssertion().assertThat(profileLog[index][ProfileLog.version]).isEqualTo(version)
        return this
    }

    @Step("Check user in profile_log table")
    fun checkNewUserInProfileLogTable(
        profilesFromProfileLogDB: MutableList<ResultRow>
    ): EmployeeAssertion {

        getSoftAssertion().assertThat(profilesFromProfileLogDB.count()).isEqualTo(1)
        getSoftAssertion().assertThat(profilesFromProfileLogDB.get(0)[ProfileLog.version]).isEqualTo(1)
        getSoftAssertion().assertThat(profilesFromProfileLogDB.get(0)[ProfileLog.type]).isEqualTo("creation")
        return this
    }

    @Step("Check user in profile_log table")
    fun checkDisabledUserInProfileLogTable(
        profilesFromProfileLogDB: MutableList<ResultRow>
    ): EmployeeAssertion {

        getSoftAssertion().assertThat(profilesFromProfileLogDB.count()).isEqualTo(2)
        getSoftAssertion().assertThat(profilesFromProfileLogDB[0][ProfileLog.version]).isEqualTo(1)
        getSoftAssertion().assertThat(profilesFromProfileLogDB[0][ProfileLog.type]).isEqualTo("creation")
        getSoftAssertion().assertThat(profilesFromProfileLogDB[1][ProfileLog.version]).isEqualTo(2)
        getSoftAssertion().assertThat(profilesFromProfileLogDB[1][ProfileLog.type]).isEqualTo("disabling")

        return this
    }

    // Table profile_password_log
    @Step("Check profile_password_log exist by type")
    fun checkProfilePasswordLogExistByType(cnt: Int): EmployeeAssertion {
        getSoftAssertion().assertThat(cnt).isEqualTo(1)
        return this
    }

    // 1C
    @Step("Check contract")
    fun checkContractDB(
        event: PriemNaRabotuCFZ,
        contract: ResultRow
    ): EmployeeAssertion {

        getSoftAssertion().assertThat(event.payload[0].sotrudnik.guid.toString())
            .isEqualTo(contract[Contract.accountingContractId])
        getSoftAssertion().assertThat(event.payload[0].fizicheskoeLitso.inn)
            .isEqualTo(contract[Contract.accountingProfileId])

        val data = jacksonObjectMapper.readValue(contract[Contract.data], Data::class.java)

        getSoftAssertion().assertThat(data.individualId).isEqualTo(event.payload[0].fizicheskoeLitso.guid.toString())
        getSoftAssertion().assertThat(data.approved).isEqualTo(event.payload[0].proveden)
        getSoftAssertion().assertThat(data.fullName).isEqualTo(event.payload[0].fizicheskoeLitso.naimenovanie)
        getSoftAssertion().assertThat(data.jobFunction.title).isEqualTo(event.payload[0].dolzhnost.naimenovanie)
        getSoftAssertion().assertThat(data.jobFunction.jobFunctionId)
            .isEqualTo(event.payload[0].dolzhnost.guid.toString())

        return this
    }

    @Step("Check contract")
    fun checkContractDB(
        event: KadrovyyPerevodCFZ,
        contract: ResultRow
    ): EmployeeAssertion {

        getSoftAssertion().assertThat(event.payload[0].sotrudnik!!.guid.toString())
            .isEqualTo(contract[Contract.accountingContractId])
        getSoftAssertion().assertThat(event.payload[0].fizicheskoeLitso!!.inn)
            .isEqualTo(contract[Contract.accountingProfileId])

        val data = jacksonObjectMapper.readValue(contract[Contract.data], Data::class.java)

        getSoftAssertion().assertThat(data.individualId).isEqualTo(event.payload[0].fizicheskoeLitso!!.guid.toString())
        getSoftAssertion().assertThat(data.approved).isEqualTo(event.payload[0].proveden)
        getSoftAssertion().assertThat(data.fullName).isEqualTo(event.payload[0].fizicheskoeLitso!!.naimenovanie)
        getSoftAssertion().assertThat(data.jobFunction.title).isEqualTo(event.payload[0].dolzhnost!!.naimenovanie)
        getSoftAssertion().assertThat(data.jobFunction.jobFunctionId)
            .isEqualTo(event.payload[0].dolzhnost!!.guid.toString())

        return this
    }

    @Step("Check contract")
    fun checkContractDB(
        event: VneshnieSotrudniki,
        contract: ResultRow
    ): EmployeeAssertion {

        getSoftAssertion().assertThat(event.payload[0].guid).isEqualTo(contract[Contract.accountingContractId])
        getSoftAssertion().assertThat(event.payload[0].fizicheskoeLitso.guid.toString())
            .isEqualTo(contract[Contract.accountingProfileId])

        val data = jacksonObjectMapper.readValue(contract[Contract.data], DataVneshnieSotrudniki::class.java)

        getSoftAssertion().assertThat(event.payload[0].telefon).isEqualTo(data.mobile)
        getSoftAssertion().assertThat(event.payload[0].partner.naimenovanie).isEqualTo(data.partner.title)
        getSoftAssertion().assertThat(event.payload[0].partner.guid).isEqualTo(data.partner.partnerId)
        getSoftAssertion().assertThat(event.payload[0].fizicheskoeLitso.naimenovanie).isEqualTo(data.fullName)
        getSoftAssertion().assertThat(event.payload[0].dolzhnost.guid.toString())
            .isEqualTo(data.jobFunction.jobFunctionId)
        getSoftAssertion().assertThat(event.payload[0].dolzhnost.naimenovanie).isEqualTo(data.jobFunction.title)
        getSoftAssertion().assertThat(event.payload[0].fizicheskoeLitso.guid.toString()).isEqualTo(data.individualId)
        getSoftAssertion().assertThat(event.payload[0].dataOformleniya).isEqualTo(data.employmentDate)

        if (data.retirementDate == null)
            getSoftAssertion().assertThat(event.payload[0].dataUvolneniya).isEqualTo("0001-01-01T00:00:00Z")
        else
            getSoftAssertion().assertThat(event.payload[0].dataUvolneniya).isEqualTo(data.retirementDate)
        getSoftAssertion().assertThat(event.payload[0].vidDogovora.naimenovanie)
            .isEqualTo(data.outsourceContractType.title)
        getSoftAssertion().assertThat(event.payload[0].vidDogovora.guid)
            .isEqualTo(data.outsourceContractType.outsourceContractTypeId)

        return this
    }

    @Step("Check two events are equal")
    fun checkTwoEventsAreEqual(actual: PriemNaRabotuCFZ, expected: PriemNaRabotuCFZ) {
        getSoftAssertion().assertThat(actual).isEqualTo(expected)
    }

    @Step("Check two events are equal")
    fun checkTwoEventsAreEqual(actual: KadrovyyPerevodCFZ, expected: KadrovyyPerevodCFZ) {
        getSoftAssertion().assertThat(actual).isEqualTo(expected)
    }


    @Step("Check contract")
    fun checkContractDB(
        event: Sotrudniki,
        proveden: Boolean,
        contract: ResultRow
    ): EmployeeAssertion {

        getSoftAssertion().assertThat(event.sotrudnik.guid.toString())
            .isEqualTo(contract[Contract.accountingContractId])
        getSoftAssertion().assertThat(event.fizicheskoeLitso.inn)
            .isEqualTo(contract[Contract.accountingProfileId])

        val data = jacksonObjectMapper.readValue(contract[Contract.data], Data::class.java)

        getSoftAssertion().assertThat(data.individualId).isEqualTo(event.fizicheskoeLitso.guid.toString())
        getSoftAssertion().assertThat(data.approved).isEqualTo(proveden)
        getSoftAssertion().assertThat(data.fullName).isEqualTo(event.fizicheskoeLitso.naimenovanie)
        getSoftAssertion().assertThat(data.jobFunction.title).isEqualTo(event.dolzhnost.naimenovanie)
        getSoftAssertion().assertThat(data.jobFunction.jobFunctionId).isEqualTo(event.dolzhnost.guid.toString())

        return this
    }

    @Step("Check contract")
    fun checkContractDB(
        event: SotrudnikiPerevod,
        proveden: Boolean,
        contract: ResultRow
    ): EmployeeAssertion {

        getSoftAssertion().assertThat(event.sotrudnik.guid.toString())
            .isEqualTo(contract[Contract.accountingContractId])
        getSoftAssertion().assertThat(event.fizicheskoeLitso.inn)
            .isEqualTo(contract[Contract.accountingProfileId])

        val data = jacksonObjectMapper.readValue(contract[Contract.data], Data::class.java)

        getSoftAssertion().assertThat(data.individualId).isEqualTo(event.fizicheskoeLitso.guid.toString())
        getSoftAssertion().assertThat(data.approved).isEqualTo(proveden)
        getSoftAssertion().assertThat(data.fullName).isEqualTo(event.fizicheskoeLitso.naimenovanie)
        getSoftAssertion().assertThat(data.jobFunction.title).isEqualTo(event.dolzhnost.naimenovanie)
        getSoftAssertion().assertThat(data.jobFunction.jobFunctionId).isEqualTo(event.dolzhnost.guid.toString())

        return this
    }

    @Step("Check scheduled task for employee transfer")
    fun checkScheduledTransferTask(
        task: ResultRow, eventKadrovyyPerevod: KadrovyyPerevodCFZ,
        type: String = "innerSourceEmployeeTransfer"
    ) {

        val data = jacksonObjectMapper.readValue(task[Task.payload], ScheduledTask::class.java)

        getSoftAssertion().assertThat(data.contract.accountingContractId)
            .isEqualTo(eventKadrovyyPerevod.payload[0].sotrudnik?.guid.toString())
        getSoftAssertion().assertThat(data.contract.accountingProfileId)
            .isEqualTo(eventKadrovyyPerevod.payload[0].fizicheskoeLitso?.inn)
        getSoftAssertion().assertThat(data.contract.data.approved).isEqualTo(eventKadrovyyPerevod.payload[0].proveden)
        getSoftAssertion().assertThat(data.contract.data.individualId)
            .isEqualTo(eventKadrovyyPerevod.payload[0].fizicheskoeLitso?.guid.toString())
        getSoftAssertion().assertThat(data.contract.data.fullName)
            .isEqualTo(eventKadrovyyPerevod.payload[0].fizicheskoeLitso?.naimenovanie)
        getSoftAssertion().assertThat(data.contract.data.jobFunction.title)
            .isEqualTo(eventKadrovyyPerevod.payload[0].dolzhnost?.naimenovanie)
        getSoftAssertion().assertThat(data.contract.data.jobFunction.jobFunctionId)
            .isEqualTo(eventKadrovyyPerevod.payload[0].dolzhnost?.guid.toString())
        getSoftAssertion().assertThat(data.contract.type).isEqualTo("INNER_SOURCE_EMPLOYEE")


        getSoftAssertion().assertThat(task[Task.type]).isEqualTo(type)

        if (type == "innerSourceEmployeeTransfer") {
            getSoftAssertion().assertThat(task[Task.scheduledAt].toString())
                .isEqualTo(eventKadrovyyPerevod.payload[0].dataNachala)
            getSoftAssertion().assertThat(data.endDateTime!!).isEqualTo(eventKadrovyyPerevod.payload[0].dataOkonchaniya)
        }

        if (type == "innerSourceEmployeeTransferRollback")
            getSoftAssertion().assertThat(task[Task.scheduledAt].toString())
                .isEqualTo(eventKadrovyyPerevod.payload[0].dataOkonchaniya)

    }

    @Step("Check contract_log type")
    fun checkContractLogTypeDB(
        type: String,
        contractLog: ResultRow
    ): EmployeeAssertion {
        getSoftAssertion().assertThat(contractLog[ContractLog.type]).isEqualTo(type)
        return this
    }


    // Assertions in API
    @Step("Check to profiles are equals")
    fun checkProfilesAreEquals(
        actualProfile: EmployeeProfileView, expectedProfile: CreateProfileRequest
    ): EmployeeAssertion {

        getSoftAssertion().assertThat(actualProfile.mobile).isEqualTo(expectedProfile.mobile)

        getSoftAssertion().assertThat(actualProfile.name.firstName)
            .isEqualTo(expectedProfile.name.firstName)
        getSoftAssertion().assertThat(actualProfile.name.lastName)
            .isEqualTo(expectedProfile.name.lastName)
        getSoftAssertion().assertThat(actualProfile.name.middleName)
            .isEqualTo(expectedProfile.name.middleName)

        getSoftAssertion().assertThat(actualProfile.roles.count())
            .isEqualTo(expectedProfile.roles.count())
        getSoftAssertion().assertThat(actualProfile.roles.containsAll(expectedProfile.roles))

        val expectedRoles = expectedProfile.roles.map { it.value }

        when {
            expectedRoles.contains(EmployeeRole.DELIVERYMAN.value) -> {
                getSoftAssertion().assertThat(actualProfile.vehicle).isEqualTo(expectedProfile.vehicle)
                getSoftAssertion().assertThat(actualProfile.staffPartner?.partnerId)
                    .isEqualTo(expectedProfile.staffPartnerId)
                getSoftAssertion().assertThat(actualProfile.darkstoreId)
                    .isEqualTo(expectedProfile.darkstoreId)
            }
            expectedRoles.contains(EmployeeRole.PICKER.value) -> {
                getSoftAssertion().assertThat(actualProfile.staffPartner?.partnerId)
                    .isEqualTo(expectedProfile.staffPartnerId)
                getSoftAssertion().assertThat(actualProfile.darkstoreId)
                    .isEqualTo(expectedProfile.darkstoreId)
            }
            expectedRoles.contains(EmployeeRole.FORWARDER.value) -> {
                getSoftAssertion().assertThat(actualProfile.vehicle).isEqualTo(expectedProfile.vehicle)
                getSoftAssertion().assertThat(actualProfile.darkstoreId)
                    .isEqualTo(expectedProfile.darkstoreId)
            }
            expectedRoles.contains(EmployeeRole.DARKSTORE_ADMIN.value) -> {
                getSoftAssertion().assertThat(actualProfile.darkstoreId)
                    .isEqualTo(expectedProfile.darkstoreId)
            }
            expectedRoles.contains(EmployeeRole.GOODS_MANAGER.value) -> {
                getSoftAssertion().assertThat(actualProfile.darkstoreId)
                    .isEqualTo(expectedProfile.darkstoreId)
            }
            expectedRoles.contains(EmployeeRole.COUNTERPARTY.value) -> {
                getSoftAssertion().assertThat(actualProfile.email).isEqualTo(expectedProfile.email)
            }
            expectedRoles.contains(EmployeeRole.COORDINATOR.value) -> {
                getSoftAssertion()
                    .assertThat(actualProfile.supervisedDarkstores?.containsAll(expectedProfile.supervisedDarkstores!!))
                    .isTrue()
            }
        }
        return this
    }

    @Step("Check profileAccountingId")
    fun checkProfileAccountingId(expectedId: String, actualId: String): EmployeeAssertion {
        getSoftAssertion().assertThat(expectedId).isEqualTo(actualId)
        return this
    }

    @Step("Check status are enabled")
    fun checkProfileStatusIsEnabled(actualProfile: EmployeeProfileView): EmployeeAssertion {
        getSoftAssertion().assertThat(actualProfile.status.value)
            .isEqualTo(EmployeeProfileStatus.ENABLED.toString())
        return this
    }

    @Step("Check supervisedDarkstores are equals")
    fun checkSupervisedDarkstoresAreEquals(
        actualProfile: EmployeeProfileView,
        expectedProfile: CreateProfileRequest
    ): EmployeeAssertion {
        getSoftAssertion()
            .assertThat(actualProfile.supervisedDarkstores?.containsAll(expectedProfile.supervisedDarkstores!!))
            .isTrue
        return this
    }

    @Step("Check updated profiles are equals")
    fun checkUpdatedProfilesAreEquals(
        actualProfile: EmployeeProfileView,
        expectedProfile: UpdateProfileRequest
    ): EmployeeAssertion {

        getSoftAssertion().assertThat(actualProfile.mobile).isEqualTo(expectedProfile.mobile)
        getSoftAssertion().assertThat(actualProfile.name.firstName)
            .isEqualTo(expectedProfile.name.firstName)
        getSoftAssertion().assertThat(actualProfile.name.lastName)
            .isEqualTo(expectedProfile.name.lastName)
        getSoftAssertion().assertThat(actualProfile.name.middleName)
            .isEqualTo(expectedProfile.name.middleName)
        getSoftAssertion().assertThat(actualProfile.roles.size).isEqualTo(expectedProfile.roles.size)
        for (i in 0 until actualProfile.roles.size) {
            getSoftAssertion().assertThat(actualProfile.roles[i].value)
                .isEqualTo(expectedProfile.roles[i].value)
        }
        getSoftAssertion().assertThat(actualProfile.roles).isEqualTo(expectedProfile.roles)
        getSoftAssertion().assertThat(actualProfile.darkstoreId)
            .isEqualTo(expectedProfile.darkstoreId)
        getSoftAssertion().assertThat(actualProfile.status.value).isEqualTo("enabled")
        getSoftAssertion().assertThat(actualProfile.staffPartner?.partnerId)
            .isEqualTo(expectedProfile.staffPartnerId)
        getSoftAssertion().assertThat(actualProfile.vehicle)
            .isEqualTo(expectedProfile.vehicle)
        getSoftAssertion().assertThat(actualProfile.supervisedDarkstores)
            .isEqualTo(expectedProfile.supervisedDarkstores)
        getSoftAssertion().assertThat(actualProfile.email)
            .isEqualTo(expectedProfile.email)
        return this
    }

    @Step("Check error message")
    fun checkErrorMessage(actual: String, expected: String): EmployeeAssertion {
        getSoftAssertion().assertThat(actual).isEqualTo(expected)
        return this
    }

    // assertions in profilesList

    @Step("Check profiles list count")
    fun checkListCount(profilesCount: Int, expectedCount: Int): EmployeeAssertion {
        getSoftAssertion().assertThat(profilesCount).isEqualTo(expectedCount)
        return this
    }

    @Step("Check page mark is null")
    fun checkPageMarkIsNull(result: GetProfilesView): EmployeeAssertion {
        getSoftAssertion().assertThat(result.paging.nextPageMark).isNull()
        return this
    }

    @Step("Check profile present in profiles list")
    fun checkProfilePresentInList(profiles: List<EmployeeProfileView>, profileId: UUID): EmployeeAssertion {
        val filtered = profiles.filter { it.profileId == profileId }
        getSoftAssertion().assertThat(filtered.size).isNotEqualTo(0)
        return this
    }

    @Step("Check profile present in profiles list")
    fun checkProfileIsNotPresentInList(profiles: List<EmployeeProfileView>, profileId: UUID): EmployeeAssertion {
        val filtered = profiles.filter { it.profileId == profileId }
        getSoftAssertion().assertThat(filtered.size).isEqualTo(0)
        return this
    }

    @Step("Check list element")
    fun checkProfileFieldsInList(
        profiles: List<EmployeeProfileView>,
        request: CreateProfileRequest,
        profileId: UUID
    ): EmployeeAssertion {
        val listElement = profiles.filter { it.profileId == profileId }[0]
        getSoftAssertion().assertThat(listElement.mobile).isEqualTo(request.mobile)
        getSoftAssertion().assertThat(listElement.name.firstName).isEqualTo(request.name.firstName)
        getSoftAssertion().assertThat(listElement.name.middleName).isEqualTo(request.name.middleName)
        getSoftAssertion().assertThat(listElement.name.lastName).isEqualTo(request.name.lastName)
        getSoftAssertion().assertThat(listElement.roles.containsAll(request.roles)).isTrue

        when {
            request.roles.contains(ApiEnum(EmployeeRole.DELIVERYMAN)) -> {
                if (request.vehicle!!.type.value == "none") {
                    getSoftAssertion().assertThat(listElement.vehicle!!.type.value).isEqualTo("none")
                } else {
                    getSoftAssertion().assertThat(listElement.vehicle!!.type.value)
                        .isEqualTo(request.vehicle!!.type.value)
                }
                getSoftAssertion().assertThat(listElement.staffPartner!!.partnerId).isEqualTo(request.staffPartnerId)
                getSoftAssertion().assertThat(listElement.darkstoreId).isEqualTo(request.darkstoreId)
            }
            request.roles.contains(ApiEnum(EmployeeRole.PICKER)) -> {
                getSoftAssertion().assertThat(listElement.staffPartner!!.partnerId).isEqualTo(request.staffPartnerId)
                getSoftAssertion().assertThat(listElement.darkstoreId).isEqualTo(request.darkstoreId)
            }
            request.roles.contains(ApiEnum(EmployeeRole.FORWARDER)) -> {
                if (request.vehicle!!.type.value == "none") {
                    getSoftAssertion().assertThat(listElement.vehicle!!.type.value).isEqualTo("none")
                } else {
                    getSoftAssertion().assertThat(listElement.vehicle!!.type.value)
                        .isEqualTo(request.vehicle!!.type.value)
                }
                getSoftAssertion().assertThat(listElement.darkstoreId).isEqualTo(request.darkstoreId)
            }
            request.roles.contains(ApiEnum(EmployeeRole.DARKSTORE_ADMIN)) -> {
                getSoftAssertion().assertThat(listElement.darkstoreId).isEqualTo(request.darkstoreId)
            }
            request.roles.contains(ApiEnum(EmployeeRole.DARKSTORE_ADMIN)) -> {
                getSoftAssertion().assertThat(
                    request.supervisedDarkstores!!.containsAll(listElement.supervisedDarkstores!!)
                ).isTrue
            }
            request.roles.contains(ApiEnum(EmployeeRole.COUNTERPARTY)) -> {
                getSoftAssertion().assertThat(listElement.email).isEqualTo(request.email)
            }
        }
        return this
    }

    @Step("Check that all elements in list satisfy the condition")
    fun checkElementsSatisfyConditions(
        profiles: List<EmployeeProfileView>,
        condition: String,
        status: EmployeeProfileStatus? = null,
        roles: List<ApiEnum<EmployeeRole, String>>? = null
    ): EmployeeAssertion {

        when (condition) {
            "status" -> {
                profiles.forEach {
                    getSoftAssertion().assertThat(it.status.value).isEqualTo(status.toString())
                }
            }
            "role" -> {
                profiles.forEach {
                    getSoftAssertion().assertThat(it.roles.containsAll(roles!!)).isTrue
                }
            }
        }
        return this
    }

    // assertions in darkstore contacts list
    @Step("assert darkstore contacts list count")
    fun checkDarkstoreContactsListCount(
        contacts: List<EmployeeProfileContactView>,
        expectedCount: Int
    ): EmployeeAssertion {
        getSoftAssertion().assertThat(contacts.count()).isEqualTo(expectedCount)
        return this
    }

    @Step("assert darkstore contacts element in list")
    fun checkDarkstoreContactInList(
        contacts: List<EmployeeProfileContactView>,
        profileId: UUID,
        request: CreateProfileRequest
    ): EmployeeAssertion {
        val listElement = contacts.filter { it.profileId == profileId }[0]
        getSoftAssertion().assertThat(listElement.mobile).isEqualTo(request.mobile)
        getSoftAssertion().assertThat(listElement.name.firstName).isEqualTo(request.name.firstName)
        getSoftAssertion().assertThat(listElement.name.middleName).isEqualTo(request.name.middleName)
        getSoftAssertion().assertThat(listElement.name.lastName).isEqualTo(request.name.lastName)
        getSoftAssertion().assertThat(listElement.roles.containsAll(request.roles)).isTrue
        return this
    }

    @Step("assert darkstore contacts element in list with role")
    fun checkDarkstoreContactInList(
        contacts: List<EmployeeProfileContactView>,
        profileId: UUID,
        request: CreateProfileRequest,
        roles: List<ApiEnum<EmployeeRole, String>>
    ): EmployeeAssertion {
        val listElement = contacts.filter { it.profileId == profileId }[0]
        getSoftAssertion().assertThat(listElement.mobile).isEqualTo(request.mobile)
        getSoftAssertion().assertThat(listElement.name.firstName).isEqualTo(request.name.firstName)
        getSoftAssertion().assertThat(listElement.name.middleName).isEqualTo(request.name.middleName)
        getSoftAssertion().assertThat(listElement.name.lastName).isEqualTo(request.name.lastName)
        getSoftAssertion().assertThat(listElement.roles.containsAll(roles)).isTrue
        return this
    }

    @Step("assert darkstore contacts element not in list")
    fun checkDarkstoreContactNotInList(contacts: List<EmployeeProfileContactView>, profileId: UUID): EmployeeAssertion {
        val listElement = contacts.filter { it.profileId == profileId }
        getSoftAssertion().assertThat(listElement.count()).isEqualTo(0)
        return this
    }

    @Step("Check profile present in darkstore users list")
    fun checkProfilePresentInDSUsersList(
        profiles: List<DarkstoreUserExtendedView>,
        mobile: PhoneNumber
    ): EmployeeAssertion {
        val filtered = profiles.filter { it.mobile == mobile }
        getSoftAssertion().assertThat(filtered.size).isNotEqualTo(0)
        return this
    }

    @Step("Check profile not present in darkstore users list")
    fun checkProfileNotPresentInDSUsersList(
        profiles: List<DarkstoreUserExtendedView>,
        mobile: PhoneNumber
    ): EmployeeAssertion {
        val filtered = profiles.filter { it.mobile == mobile }
        getSoftAssertion().assertThat(filtered.size).isEqualTo(0)
        return this
    }

    @Step("Check that all elements in list satisfy the condition")
    fun checkDSUserSatisfyConditions(
        dsUsersList: List<DarkstoreUserExtendedView>,
        condition: String,
        darkstoreId: UUID? = null,
        state: DarkstoreUserState? = null,
        role: ApiEnum<DarkstoreUserRole, String>? = null,
        vehicle: List<ApiEnum<EmployeeVehicleType, String>>? = null,
        staffPartners: List<UUID>? = null
    ): EmployeeAssertion {

        when (condition) {
            "darkstoreId" -> {
                dsUsersList.forEach {
                    getSoftAssertion().assertThat(it.darkstoreUser.darkstoreId).isEqualTo(darkstoreId)
                }
            }
            "status" -> {
                dsUsersList.forEach {
                    getSoftAssertion().assertThat(it.darkstoreUser.state.value).isEqualTo(state!!.value)
                }
            }
            "role" -> {
                dsUsersList.forEach {
                    getSoftAssertion().assertThat(it.darkstoreUser.role).isEqualTo(role)
                }
            }
            "vehicle" -> {
                dsUsersList.forEach {
                    //getSoftAssertion().assertThat(vehicle).contains(it.vehicle?.type)
                }
            }
            "staffPartner" -> {
                dsUsersList.forEach {
                    getSoftAssertion().assertThat(staffPartners).contains(it.staffPartner?.partnerId)
                }
            }
        }
        return this
    }

    @Step("Check DS profile list sorting")
    fun checkDSUserListSorting(
        dsUsersList: List<DarkstoreUserExtendedView>,
        sortingMobiles: List<PhoneNumber>
    ): EmployeeAssertion {
        for (i in 0 until dsUsersList.count()) {
            getSoftAssertion().assertThat(dsUsersList[i].mobile).isEqualTo(sortingMobiles[i])
        }
        return this
    }

    @Step("Check DS profile list element")
    fun checkDSProfileFieldsInList(
        profiles: List<DarkstoreUserExtendedView>,
        expected: CreateProfileRequest,
        state: DarkstoreUserState,
        version: Int,
        isIntern: Boolean,
        role: DarkstoreUserRole
    ): EmployeeAssertion {
        val listElement =
            profiles.filter { it.mobile == expected.mobile && it.darkstoreUser.role.value == role.value }[0]

        getSoftAssertion().assertThat(listElement.darkstoreUser.darkstoreId).isEqualTo(expected.darkstoreId)
        getSoftAssertion().assertThat(listElement.darkstoreUser.state.value).isEqualTo(state.value)
        getSoftAssertion().assertThat(listElement.darkstoreUser.version.toInt()).isEqualTo(version)
        //getSoftAssertion().assertThat(listElement.darkstoreUser.isIntern).isEqualTo(isIntern)
        getSoftAssertion().assertThat(listElement.darkstoreUser.profileId).isNotNull()
        getSoftAssertion().assertThat(listElement.darkstoreUser.lastModifiedAt).isNotNull()

        getSoftAssertion().assertThat(listElement.name.firstName).isEqualTo(expected.name.firstName)
        getSoftAssertion().assertThat(listElement.name.lastName).isEqualTo(expected.name.lastName)
        getSoftAssertion().assertThat(listElement.name.middleName).isEqualTo(expected.name.middleName)

        getSoftAssertion().assertThat(listElement.staffPartner?.partnerId).isEqualTo(expected.staffPartnerId)
        getSoftAssertion().assertThat(listElement.profileCreatedAt).isNotNull()

        if (listElement.darkstoreUser.role == ApiEnum(DarkstoreUserRole.DELIVERYMAN)) {
            getSoftAssertion().assertThat(listElement.vehicle!!.type.value)
                .isEqualTo(expected.vehicle!!.type.value)
        } else
            getSoftAssertion().assertThat(listElement.vehicle).isNull()

        return this
    }

    // Kafka


    @Step("Check all message in employeeprofiles_profile_changed topic")
    fun checkSeveralTimesChangedEmployeeProfilesKafka(
        actualMessages: MutableList<EmployeeProfilesProfileChanged>,
        request: UpdateProfileRequest,
        updateVersion: Int,
        count: Int
    ): EmployeeAssertion {

        getSoftAssertion().assertThat(actualMessages.count()).isEqualTo((count))
        getSoftAssertion().assertThat(actualMessages.last().version)
            .isEqualTo(updateVersion)
        getSoftAssertion().assertThat(actualMessages.last().snapshot.name.firstName)
            .isEqualTo(request.name.firstName)
        getSoftAssertion().assertThat(actualMessages.last().snapshot.name.middleName)
            .isEqualTo(request.name.middleName)
        getSoftAssertion().assertThat(actualMessages.last().snapshot.name.lastName)
            .isEqualTo(request.name.lastName)
        return this
    }

    @Step("Check message in employeeprofiles_profile_changed topic")
    fun checkEmployeeProfileChangedKafka(
        message: EmployeeProfilesProfileChanged,
        request: UpdateProfileRequest,
        updateVersion: Int
    ): EmployeeAssertion {

        getSoftAssertion().assertThat(message.snapshot.mobile).isEqualTo(request.mobile.asStringWithPlus())
        getSoftAssertion().assertThat(message.snapshot.name.firstName).isEqualTo(request.name.firstName)
        getSoftAssertion().assertThat(message.snapshot.name.middleName).isEqualTo(request.name.middleName)
        getSoftAssertion().assertThat(message.snapshot.name.lastName).isEqualTo(request.name.lastName)

        val buildRoles = request.roles.map { it.value }
        getSoftAssertion().assertThat(message.snapshot.roles.count()).isEqualTo(buildRoles.count())
        getSoftAssertion().assertThat(buildRoles.containsAll(message.snapshot.roles)).isTrue

        getSoftAssertion().assertThat(message.version).isEqualTo(updateVersion)

        when {
            buildRoles.contains(EmployeeRole.DELIVERYMAN.value) -> {
                if (request.vehicle?.type?.value == "none") {
                    getSoftAssertion().assertThat(message.snapshot.vehicle).isNull()
                } else {
                    getSoftAssertion().assertThat(message.snapshot.vehicle!!.type)
                        .isEqualTo(request.vehicle?.type?.value)
                }
                getSoftAssertion().assertThat(message.snapshot.staffPartnerId).isEqualTo(request.staffPartnerId)
                getSoftAssertion().assertThat(message.snapshot.darkstoreId).isEqualTo(request.darkstoreId)
            }
            buildRoles.contains(EmployeeRole.PICKER.value) -> {
                getSoftAssertion().assertThat(message.snapshot.staffPartnerId).isEqualTo(request.staffPartnerId)
                getSoftAssertion().assertThat(message.snapshot.darkstoreId).isEqualTo(request.darkstoreId)
            }
            buildRoles.contains(EmployeeRole.FORWARDER.value) -> {
                if (request.vehicle?.type?.value == "none") {
                    getSoftAssertion().assertThat(message.snapshot.vehicle).isNull()
                } else {
                    getSoftAssertion().assertThat(message.snapshot.vehicle!!.type)
                        .isEqualTo(request.vehicle?.type?.value)
                }
                getSoftAssertion().assertThat(message.snapshot.darkstoreId).isEqualTo(request.darkstoreId)
            }
            buildRoles.contains(EmployeeRole.PICKER.value) -> {
                getSoftAssertion().assertThat(message.snapshot.darkstoreId).isEqualTo(request.darkstoreId)
            }
            buildRoles.contains(EmployeeRole.COORDINATOR.value) -> {
                getSoftAssertion()
                    .assertThat(request.supervisedDarkstores?.containsAll(message.snapshot.supervisedDarkstores!!))
                    .isTrue
            }
            buildRoles.contains(EmployeeRole.COUNTERPARTY.value) -> {
                getSoftAssertion().assertThat(message.snapshot.email).isEqualTo(request.email)
            }
        }
        return this
    }

    @Step("Check updatedName in employeeprofiles_profile_changed topic")
    fun checkEmployeeProfileChangedKafkaUpdatedName(
        message: EmployeeProfilesProfileChanged,
        request: UpdateProfileRequest

    ): EmployeeAssertion {
        getSoftAssertion().assertThat(message.changes.name?.newValue?.firstName).isEqualTo(request.name.firstName)
        getSoftAssertion().assertThat(message.changes.name?.newValue?.middleName).isEqualTo(request.name.middleName)
        getSoftAssertion().assertThat(message.changes.name?.newValue?.lastName).isEqualTo(request.name.lastName)

        return this
    }

    @Step("Check updatedMobile in employeeprofiles_profile_changed topic")
    fun checkEmployeeProfileChangedKafkaUpdatedMobile(
        message: EmployeeProfilesProfileChanged,
        request: UpdateProfileRequest

    ): EmployeeAssertion {
        getSoftAssertion().assertThat(message.changes.mobile?.newValue).isEqualTo(request.mobile.asStringWithPlus())

        return this
    }

    @Step("Check updatedDarkstore in employeeprofiles_profile_changed topic")
    fun checkEmployeeProfileChangedKafkaUpdatedDarkstoreAndCity(
        message: EmployeeProfilesProfileChanged,
        request: UpdateProfileRequest

    ): EmployeeAssertion {
        getSoftAssertion().assertThat(message.changes.darkstoreId?.newValue).isEqualTo(request.darkstoreId)
        getSoftAssertion().assertThat(message.changes.cityId?.newValue).isEqualTo(request.cityId)
        return this
    }

    @Step("Check updatedRole in employeeprofiles_profile_changed topic")
    fun checkEmployeeProfileChangedKafkaUpdatedRole(
        message: EmployeeProfilesProfileChanged,
        request: UpdateProfileRequest

    ): EmployeeAssertion {
        val buildRoles = request.roles.map { it.value }
        getSoftAssertion().assertThat(buildRoles.containsAll(message.changes.roles!!.newValue)).isTrue
        return this
    }

    @Step("Check updatedVehicle in employeeprofiles_profile_changed topic")
    fun checkEmployeeProfileChangedKafkaUpdatedVehicle(
        message: EmployeeProfilesProfileChanged,
        request: UpdateProfileRequest

    ): EmployeeAssertion {
        getSoftAssertion().assertThat(message.changes.vehicle?.newValue?.type)
            .isEqualTo(request.vehicle?.type?.value)

        return this
    }

    @Step("Check updatedStaffPartner in employeeprofiles_profile_changed topic")
    fun checkEmployeeProfileChangedKafkaUpdatedStaffPartner(
        message: EmployeeProfilesProfileChanged,
        request: UpdateProfileRequest

    ): EmployeeAssertion {
        getSoftAssertion().assertThat(message.changes.staffPartnerId?.newValue).isEqualTo(request.staffPartnerId)

        return this
    }

    @Step("Check updatedEmail in employeeprofiles_profile_changed topic")
    fun checkEmployeeProfileChangedKafkaUpdatedEmail(
        message: EmployeeProfilesProfileChanged,
        request: UpdateProfileRequest

    ): EmployeeAssertion {
        getSoftAssertion().assertThat(message.changes.email?.newValue).isEqualTo(request.email)

        return this
    }

    @Step("Check updatedSupervisedDS in employeeprofiles_profile_changed topic")
    fun checkEmployeeProfileChangedKafkaUpdatedSupervisedDS(
        message: EmployeeProfilesProfileChanged,
        request: UpdateProfileRequest

    ): EmployeeAssertion {
        getSoftAssertion().assertThat(message.changes.supervisedDarkstores?.newValue)
            .isEqualTo(request.supervisedDarkstores)

        return this
    }

    @Step("Check message in employee_profile_disable topic")
    fun checkEmployeeProfileDisableKafka(message: EmployeeProfileLog): EmployeeAssertion {
        getSoftAssertion().assertThat(message.version).isEqualTo(2)
        return this
    }

    @Step("Check message in employee_profile_change_password topic")
    fun checkEmployeeProfileChangePasswordExistsKafka(message: EmployeeProfilesProfilePasswordChanged?): EmployeeAssertion {
        getSoftAssertion().assertThat(message).isNotEqualTo(null)
        return this
    }

    @Step("Check message in employee_profile_log topic")
    fun checkEmployeeProfileLogKafka(
        message: EmployeeProfileLog,
        expected: CreateProfileRequest
    ): EmployeeAssertion {

        getSoftAssertion().assertThat(message.mobile).isEqualTo(expected.mobile.asStringWithPlus())
        getSoftAssertion().assertThat(message.name.firstName).isEqualTo(expected.name.firstName)
        getSoftAssertion().assertThat(message.name.middleName).isEqualTo(expected.name.middleName)
        getSoftAssertion().assertThat(message.name.lastName).isEqualTo(expected.name.lastName)

        val buildRoles = expected.roles.map { it.value }
        getSoftAssertion().assertThat(message.roles.count()).isEqualTo(buildRoles.count())
        getSoftAssertion().assertThat(buildRoles.containsAll(message.roles)).isTrue

        getSoftAssertion().assertThat(message.status).isEqualTo(EmployeeProfileStatus.ENABLED.toString())
        getSoftAssertion().assertThat(message.passwordChanged).isFalse
        getSoftAssertion().assertThat(message.version).isEqualTo(1)

        when {
            message.roles.contains(EmployeeRole.DELIVERYMAN.value) -> {
                if (expected.vehicle!!.type.value == "none") {
                    getSoftAssertion().assertThat(message.vehicle).isNull()
                } else {
                    getSoftAssertion().assertThat(message.vehicle!!.type)
                        .isEqualTo(expected.vehicle!!.type.value)
                }
                getSoftAssertion().assertThat(message.staffPartnerId).isEqualTo(expected.staffPartnerId)
                getSoftAssertion().assertThat(message.darkstoreId).isEqualTo(expected.darkstoreId)
            }
            message.roles.contains(EmployeeRole.PICKER.value) -> {
                getSoftAssertion().assertThat(message.staffPartnerId).isEqualTo(expected.staffPartnerId)
                getSoftAssertion().assertThat(message.darkstoreId).isEqualTo(expected.darkstoreId)
            }
            message.roles.contains(EmployeeRole.FORWARDER.value) -> {
                if (expected.vehicle!!.type.value == "none") {
                    getSoftAssertion().assertThat(message.vehicle).isNull()
                } else {
                    getSoftAssertion().assertThat(message.vehicle!!.type)
                        .isEqualTo(expected.vehicle!!.type.value)
                }
                getSoftAssertion().assertThat(message.darkstoreId).isEqualTo(expected.darkstoreId)
            }
            message.roles.contains(EmployeeRole.DARKSTORE_ADMIN.value) -> {
                getSoftAssertion().assertThat(message.darkstoreId).isEqualTo(expected.darkstoreId)
            }
            message.roles.contains(EmployeeRole.GOODS_MANAGER.value) -> {
                getSoftAssertion().assertThat(message.darkstoreId).isEqualTo(expected.darkstoreId)
            }
            message.roles.contains(EmployeeRole.COUNTERPARTY.value) -> {
                getSoftAssertion().assertThat(message.email).isEqualTo(expected.email)
            }
            message.roles.contains(EmployeeRole.COORDINATOR.value) -> {
                getSoftAssertion()
                getSoftAssertion()
                    .assertThat(expected.supervisedDarkstores!!.containsAll(message.supervisedDarkstores!!))
                    .isTrue
            }
        }
        return this
    }

    @Step("Check message in employee_profile_created topic")
    fun checkEmployeeProfileCreatedKafka(
        message: EmployeeProfileLog,
        expected: CreateProfileRequest
    ): EmployeeAssertion {

        getSoftAssertion().assertThat(message.mobile).isEqualTo(expected.mobile.asStringWithPlus())
        getSoftAssertion().assertThat(message.name.firstName).isEqualTo(expected.name.firstName)
        getSoftAssertion().assertThat(message.name.middleName).isEqualTo(expected.name.middleName)
        getSoftAssertion().assertThat(message.name.lastName).isEqualTo(expected.name.lastName)

        val buildRoles = expected.roles.map { it.value }
        getSoftAssertion().assertThat(message.roles.count()).isEqualTo(buildRoles.count())
        getSoftAssertion().assertThat(buildRoles.containsAll(message.roles)).isTrue

        getSoftAssertion().assertThat(message.passwordChanged).isFalse
        getSoftAssertion().assertThat(message.version).isEqualTo(1)

        when {
            message.roles.contains(EmployeeRole.DELIVERYMAN.value) -> {
                if (expected.vehicle!!.type.value == "none") {
                    getSoftAssertion().assertThat(message.vehicle).isNull()
                } else {
                    getSoftAssertion().assertThat(message.vehicle!!.type)
                        .isEqualTo(expected.vehicle!!.type.value)
                }
                getSoftAssertion().assertThat(message.staffPartnerId).isEqualTo(expected.staffPartnerId)
                getSoftAssertion().assertThat(message.darkstoreId).isEqualTo(expected.darkstoreId)
            }
            message.roles.contains(EmployeeRole.PICKER.value) -> {
                getSoftAssertion().assertThat(message.staffPartnerId).isEqualTo(expected.staffPartnerId)
                getSoftAssertion().assertThat(message.darkstoreId).isEqualTo(expected.darkstoreId)
            }
            message.roles.contains(EmployeeRole.FORWARDER.value) -> {
                if (expected.vehicle!!.type.value == "none") {
                    getSoftAssertion().assertThat(message.vehicle).isNull()
                } else {
                    getSoftAssertion().assertThat(message.vehicle!!.type)
                        .isEqualTo(expected.vehicle!!.type.value)
                }
                getSoftAssertion().assertThat(message.darkstoreId).isEqualTo(expected.darkstoreId)
            }
            message.roles.contains(EmployeeRole.DARKSTORE_ADMIN.value) -> {
                getSoftAssertion().assertThat(message.darkstoreId).isEqualTo(expected.darkstoreId)
            }
            message.roles.contains(EmployeeRole.GOODS_MANAGER.value) -> {
                getSoftAssertion().assertThat(message.darkstoreId).isEqualTo(expected.darkstoreId)
            }
            message.roles.contains(EmployeeRole.COUNTERPARTY.value) -> {
                getSoftAssertion().assertThat(message.email).isEqualTo(expected.email)
            }
            message.roles.contains(EmployeeRole.COORDINATOR.value) -> {
                getSoftAssertion()
                getSoftAssertion()
                    .assertThat(expected.supervisedDarkstores!!.containsAll(message.supervisedDarkstores!!))
                    .isTrue
            }
        }

        return this
    }


    // Команда
    @Step("check darkstore user profile")
    fun checkDarkstoreUserProfile(
        dsProfile: DarkstoreUserView,
        createRequest: CreateProfileRequest,
        state: DarkstoreUserState,
        inactivityReason: String? = null,
        version: Long
    ): EmployeeAssertion {

        getSoftAssertion().assertThat(dsProfile.darkstoreId).isEqualTo(createRequest.darkstoreId)
        getSoftAssertion().assertThat(createRequest.roles.map { it.value }.contains(dsProfile.role.value))
            .isTrue
        getSoftAssertion().assertThat(dsProfile.state.value).isEqualTo(state.value)
        getSoftAssertion().assertThat(dsProfile.version).isEqualTo(version)
        getSoftAssertion().assertThat(dsProfile.inactivityReason).isEqualTo(inactivityReason)
        return this
    }

    @Step("check isIntern flag is true")
    fun checkIsInternFlag(actual: Boolean, expected: Boolean): EmployeeAssertion {
        getSoftAssertion().assertThat(actual).isEqualTo(expected)
        return this
    }

    // Нарушения
    @Step("Check violations from api")
    fun checkViolationsAreEquals(
        actualViolation: DarkstoreUserViolationView,
        expectedViolation: StoreDarkstoreUserViolationRequest,
        darkstoreId: UUID, violationType: ViolationView,
        violationTime: Instant
    ): EmployeeAssertion {
        getSoftAssertion().assertThat(actualViolation.violationCode).isEqualTo(expectedViolation.violationCode)
        getSoftAssertion().assertThat(actualViolation.violationTitle).isEqualTo(violationType.title)
        getSoftAssertion().assertThat(actualViolation.violationSeverity).isEqualTo(violationType.severity)
        getSoftAssertion().assertThat(Duration.between(violationTime, actualViolation.violationDate).toMinutes() < 2)
            .isTrue
        getSoftAssertion().assertThat(actualViolation.violationComment).isEqualTo(expectedViolation.violationComment)
        getSoftAssertion().assertThat(actualViolation.darkstoreId).isEqualTo(darkstoreId)
        return this
    }

    @Step("Check list of violations from api")
    fun checkListOfViolationsFromApi(
        actualViolations: List<DarkstoreUserViolationView>,
        expectedViolations: List<StoreDarkstoreUserViolationRequest>,
        darkstoreId: UUID, violationTypes: List<ViolationView>
    ): EmployeeAssertion{

        for (i in 0 until actualViolations.count()){

            val violationType = violationTypes.filter { it.code == actualViolations[i].violationCode }[0]
            val expectedViolation = expectedViolations.filter { it.violationCode == actualViolations[i].violationCode } [0]

            getSoftAssertion().assertThat(actualViolations[i].violationCode).isEqualTo(expectedViolation.violationCode)
            getSoftAssertion().assertThat(actualViolations[i].violationTitle).isEqualTo(violationType.title)
            getSoftAssertion().assertThat(actualViolations[i].violationSeverity).isEqualTo(violationType.severity)
            getSoftAssertion().assertThat(actualViolations[i].violationComment).isEqualTo(expectedViolation.violationComment)
            getSoftAssertion().assertThat(actualViolations[i].darkstoreId).isEqualTo(darkstoreId)



        }

        return this
    }

    @Step("Check violations in DB")
    fun checkViolationsFromDatabase(
        actualViolation: ResultRow,
        expectedViolation: StoreDarkstoreUserViolationRequest,
        darkstoreId: UUID, role: DarkstoreUserRole, version: Long
    ): EmployeeAssertion {
        getSoftAssertion().assertThat(actualViolation[DarkstoreUserViolation.violation_code])
            .isEqualTo(expectedViolation.violationCode.value)
        getSoftAssertion().assertThat(actualViolation[DarkstoreUserViolation.comment])
            .isEqualTo(expectedViolation.violationComment)
        getSoftAssertion().assertThat(actualViolation[DarkstoreUserViolation.darkstoreId]).isEqualTo(darkstoreId)
        getSoftAssertion().assertThat(actualViolation[DarkstoreUserViolation.role]).isEqualTo(role.toString())
        getSoftAssertion().assertThat(actualViolation[DarkstoreUserViolation.version]).isEqualTo(version)
        return this
    }

    @Step("Check violations log in DB")
    fun checkViolationsLogFromDatabase(
        actualViolationLog: ResultRow,
        darkstoreId: UUID, profileId: UUID, role: DarkstoreUserRole, type: String, version: Long
    ): EmployeeAssertion {
        getSoftAssertion().assertThat(actualViolationLog[DarkstoreUserViolationLog.darkstoreId]).isEqualTo(darkstoreId)
        getSoftAssertion().assertThat(actualViolationLog[DarkstoreUserViolationLog.profileId]).isEqualTo(profileId)
        getSoftAssertion().assertThat(actualViolationLog[DarkstoreUserViolationLog.role]).isEqualTo(role.toString())
        getSoftAssertion().assertThat(actualViolationLog[DarkstoreUserViolationLog.type]).isEqualTo(type)
        getSoftAssertion().assertThat(actualViolationLog[DarkstoreUserViolationLog.version]).isEqualTo(version)
        return this
    }

    @Step("Check violations id not equals")
    fun checkViolationsIdNotEquals(violationId1: UUID, violationId2: UUID): EmployeeAssertion {
        getSoftAssertion().assertThat(violationId1).isNotEqualTo(violationId2)
        return this
    }

    @Step("Check violation present in violations list")
    fun checkViolationIsPresentInList(
        violations: List<DarkstoreUserViolationView>,
        violationId: UUID
    ): EmployeeAssertion {
        val filtered = violations.filter { it.violationId == violationId }
        getSoftAssertion().assertThat(filtered.size).isEqualTo(1)
        return this
    }

    @Step("Check violation not present in violations list")
    fun checkViolationIsNotPresentInList(
        violations: List<DarkstoreUserViolationView>,
        violationId: UUID
    ): EmployeeAssertion {
        val filtered = violations.filter { it.violationId == violationId }
        getSoftAssertion().assertThat(filtered.size).isEqualTo(0)
        return this
    }

    @Step("Check violation present in violations list")
    fun checkViolationIsPresentInGlideList(
        violations: List<ExtendedDarkstoreUserViolationView>,
        violationId: UUID
    ): EmployeeAssertion {
        val filtered = violations.filter { it.violationId == violationId }
        getSoftAssertion().assertThat(filtered.size).isEqualTo(1)
        return this
    }

    @Step("Check violation not present in violations list")
    fun checkViolationIsNotPresentInGlideList(
        violations: List<ExtendedDarkstoreUserViolationView>,
        violationId: UUID
    ): EmployeeAssertion {
        val filtered = violations.filter { it.violationId == violationId }
        getSoftAssertion().assertThat(filtered.size).isEqualTo(0)
        return this
    }

    @Step("Check violations fields in glide list")
    fun checkViolationsFieldsInGlideList(
        violations: List<ExtendedDarkstoreUserViolationView>,
        expectedViolation: StoreDarkstoreUserViolationRequest,
        expectedProfile: CreateProfileRequest,
        violationId: UUID,
        profileId: UUID,
        violationType: ViolationView
    ): EmployeeAssertion {

        val violation = violations.filter { it.violationId == violationId }[0]
        getSoftAssertion().assertThat(violation.violationId).isEqualTo(violationId)
        getSoftAssertion().assertThat(violation.violationIssuer).isEqualTo(expectedViolation.issuerProfileId)
        getSoftAssertion().assertThat(violation.violationCode).isEqualTo(expectedViolation.violationCode)
        getSoftAssertion().assertThat(violation.violationTitle).isEqualTo(violationType.title)
        getSoftAssertion().assertThat(violation.violationSeverity).isEqualTo(violationType.severity)
        getSoftAssertion().assertThat(violation.violationComment).isEqualTo(expectedViolation.violationComment)
        getSoftAssertion().assertThat(violation.darkstoreId).isEqualTo(expectedProfile.darkstoreId)
        getSoftAssertion().assertThat(violation.profileId).isEqualTo(profileId)
        getSoftAssertion().assertThat(violation.mobile).isEqualTo(expectedProfile.mobile)
        getSoftAssertion().assertThat(violation.staffPartner?.partnerId).isEqualTo(expectedProfile.staffPartnerId)
        getSoftAssertion().assertThat(violation.fullName)
            .isEqualTo(expectedProfile.name.lastName + " " + expectedProfile.name.firstName + " " + expectedProfile.name.middleName)
        getSoftAssertion().assertThat(violation.roles.count())
            .isEqualTo(expectedProfile.roles.count())
        getSoftAssertion().assertThat(violation.roles.containsAll(expectedProfile.roles))
        return this
    }

    @Step("Check violations dictionary")
    fun checkViolationsDictionary(violationDictionary: List<ViolationView>) {

        val expectedViolations = mutableListOf<ViolationView>()
        expectedViolations.add(ViolationView(ApiEnum(ViolationCode.V001), "Не вышел на смену", ApiEnum(ViolationSeverity.CRITICAL)))
        expectedViolations.add(ViolationView(ApiEnum(ViolationCode.V002), "Не закрывает заявку в заказе", ApiEnum(ViolationSeverity.CRITICAL)))
        expectedViolations.add(ViolationView(ApiEnum(ViolationCode.V003), "Закрывает заявку до доставки", ApiEnum(ViolationSeverity.CRITICAL)))
        expectedViolations.add(ViolationView(ApiEnum(ViolationCode.V004), "Вышел без велосипеда", ApiEnum(ViolationSeverity.CRITICAL)))
        expectedViolations.add(ViolationView(ApiEnum(ViolationCode.V005), "Жалобы от клиентов", ApiEnum(ViolationSeverity.CRITICAL)))
        expectedViolations.add(ViolationView(ApiEnum(ViolationCode.V006), "Воровство, криминал", ApiEnum(ViolationSeverity.CRITICAL)))
        expectedViolations.add(ViolationView(ApiEnum(ViolationCode.V007), "Не умеет ездить на велосипеде", ApiEnum(ViolationSeverity.MEDIUM)))
        expectedViolations.add(ViolationView(ApiEnum(ViolationCode.V008), "Плохо ориентируется на местности", ApiEnum(ViolationSeverity.MEDIUM)))
        expectedViolations.add(ViolationView(ApiEnum(ViolationCode.V009), "Медленно работает", ApiEnum(ViolationSeverity.MEDIUM)))
        expectedViolations.add(ViolationView(ApiEnum(ViolationCode.V010), "Не вышел на смену без предупреждения", ApiEnum(ViolationSeverity.MEDIUM)))
        expectedViolations.add(ViolationView(ApiEnum(ViolationCode.V011), "Не закрывает заявку в заказе", ApiEnum(ViolationSeverity.MEDIUM)))
        expectedViolations.add(ViolationView(ApiEnum(ViolationCode.V012), "Вышел без велосипеда", ApiEnum(ViolationSeverity.MEDIUM)))
        expectedViolations.add(ViolationView(ApiEnum(ViolationCode.V013), "Жалоба от клиента", ApiEnum(ViolationSeverity.MEDIUM)))
        expectedViolations.add(ViolationView(ApiEnum(ViolationCode.V014), "Опоздание на смену", ApiEnum(ViolationSeverity.MEDIUM)))
        expectedViolations.add(ViolationView(ApiEnum(ViolationCode.V015), "Уход со смены без уважительной причины", ApiEnum(ViolationSeverity.CRITICAL)))
        expectedViolations.add(ViolationView(ApiEnum(ViolationCode.V016), "Партнёр не выдал шлем", ApiEnum(ViolationSeverity.MEDIUM)))
        expectedViolations.add(ViolationView(ApiEnum(ViolationCode.V017), "Нет мед.книжки после 40 часов смен", ApiEnum(ViolationSeverity.CRITICAL)))

        getSoftAssertion().assertThat(expectedViolations.containsAll(violationDictionary)).isTrue

    }

    fun assertAll() {
        getSoftAssertion().assertAll()
    }

    @Step("Check that all elements in list satisfy the condition")
    fun checkElementsInGlideListSatisfyConditions(
        violations: List<ExtendedDarkstoreUserViolationView>,
        condition: String,
        role: ApiEnum<EmployeeRole, String>? = null
    ): EmployeeAssertion {

        when (condition) {
            "role" -> {
                violations.forEach {
                    getSoftAssertion().assertThat(it.roles.contains(role!!)).isTrue
                }
            }
        }
        return this
    }

    @Step("Check profile signature")
    fun stepCheckProfileSignature(event: PriemNaRabotuCFZ, signature: EmployeeSignatureView): EmployeeAssertion {
        getSoftAssertion().assertThat(signature.signature!!.inn)
            .isEqualTo(event.payload[0].fizicheskoeLitso.inn)
        getSoftAssertion().assertThat(signature.signature!!.fullName)
            .isEqualTo(event.payload[0].fizicheskoeLitso.naimenovanie)
        getSoftAssertion().assertThat(signature.signature!!.jobFunction)
            .isEqualTo(event.payload[0].dolzhnost.naimenovanie)
        getSoftAssertion().assertThat(signature.signature!!.inn).isEqualTo(event.payload[0].fizicheskoeLitso.inn)

        getSoftAssertion().assertThat(signature.canSign).isTrue
        return this
    }

    @Step("Check two dates are equal")
    fun checkTwoDatesAreEqual(date1: Instant, date2: Instant): EmployeeAssertion {

        getSoftAssertion().assertThat(date1.truncatedTo(ChronoUnit.SECONDS).toEpochMilli())
            .isEqualTo(date2.truncatedTo(ChronoUnit.SECONDS).toEpochMilli())
        return this
    }

    @Step("Check two dates are not equal")
    fun checkUpdatedDatesLaterThanCreate(createDate: Instant, updateDate: Instant): EmployeeAssertion {

        getSoftAssertion().assertThat(createDate.toEpochMilli() < updateDate.toEpochMilli()).isTrue
        return this
    }

    @Step("Check partner data from API")
    fun checkPartnerFromApi(actual: StaffPartnerView, expected: CreatePartnerRequest): EmployeeAssertion {
        getSoftAssertion().assertThat(actual.title).isEqualTo(expected.title)
        getSoftAssertion().assertThat(actual.shortTitle).isEqualTo(expected.shortTitle)
        getSoftAssertion().assertThat(actual.type).isEqualTo(expected.type)
        return this
    }

    @Step("Check partner data from API")
    fun checkPartnerFromDB(actual: ResultRow, expected: CreatePartnerRequest): EmployeeAssertion {
        getSoftAssertion().assertThat(actual[StaffPartner.partnerTitle]).isEqualTo(expected.title)
        getSoftAssertion().assertThat(actual[StaffPartner.partnerShortTitle]).isEqualTo(expected.shortTitle)
        getSoftAssertion().assertThat(actual[StaffPartner.partnerType]).isEqualTo(expected.type.value)
        return this
    }

    @Step("Check profile contract")
    fun checkProfileContract(
        event: VneshnieSotrudniki,
        contracts: Map<UUID, List<ru.samokat.employeeprofiles.api.contracts.domain.Contract>>,
        profileId: UUID
    ): EmployeeAssertion {

        val contract = contracts[profileId]!!.first()

        getSoftAssertion().assertThat(contract.accountingContractId.toString()).isEqualTo(event.payload[0].guid)
        getSoftAssertion().assertThat(contract.accountingProfileId)
            .isEqualTo(event.payload[0].fizicheskoeLitso.guid.toString())

        getSoftAssertion().assertThat(event.payload[0].telefon.toString())
            .isEqualTo(contract.data.get("mobile").textValue())
        getSoftAssertion().assertThat(event.payload[0].partner.naimenovanie.toString())
            .isEqualTo(contract.data.get("partner").get("title").textValue())
        getSoftAssertion().assertThat(event.payload[0].partner.guid.toString())
            .isEqualTo(contract.data.get("partner").get("partnerId").textValue())
        getSoftAssertion().assertThat(event.payload[0].fizicheskoeLitso.naimenovanie.toString())
            .isEqualTo(contract.data.get("fullName").textValue())
        getSoftAssertion().assertThat(event.payload[0].dolzhnost.guid.toString())
            .isEqualTo(contract.data.get("jobFunction").get("jobFunctionId").textValue())
        getSoftAssertion().assertThat(event.payload[0].dolzhnost.naimenovanie.toString())
            .isEqualTo(contract.data.get("jobFunction").get("title").textValue())
        getSoftAssertion().assertThat(event.payload[0].fizicheskoeLitso.guid.toString())
            .isEqualTo(contract.data.get("individualId").textValue())
        getSoftAssertion().assertThat(event.payload[0].dataOformleniya.toString())
            .isEqualTo(contract.data.get("employmentDate").textValue())
        getSoftAssertion().assertThat(event.payload[0].vidDogovora.naimenovanie.toString())
            .isEqualTo(contract.data.get("outsourceContractType").get("title").textValue())
        getSoftAssertion().assertThat(event.payload[0].vidDogovora.guid.toString())
            .isEqualTo(contract.data.get("outsourceContractType").get("outsourceContractTypeId").textValue())

        if (event.payload[0].dataUvolneniya != "0001-01-01T00:00:00Z") {
            getSoftAssertion().assertThat(event.payload[0].dataUvolneniya.toString())
                .isEqualTo(contract.data.get("retirementDate").textValue())
        } else
        {
            getSoftAssertion().assertThat (contract.data.get("retirementDate")).isNull()
        }

        return this
    }

    @Step("Check profile contract")
    fun checkProfileContractByAccountingProfileId(
        event: VneshnieSotrudniki,
        contracts: Map<String, List<ru.samokat.employeeprofiles.api.contracts.domain.Contract>>,
        accountingProfileId: UUID
    ): EmployeeAssertion {

        val contract = contracts[accountingProfileId.toString()]!!.first()

        getSoftAssertion().assertThat(contract.accountingContractId.toString()).isEqualTo(event.payload[0].guid)
        getSoftAssertion().assertThat(contract.accountingProfileId)
            .isEqualTo(event.payload[0].fizicheskoeLitso.guid.toString())

        getSoftAssertion().assertThat(event.payload[0].telefon.toString())
            .isEqualTo(contract.data.get("mobile").textValue())
        getSoftAssertion().assertThat(event.payload[0].partner.naimenovanie.toString())
            .isEqualTo(contract.data.get("partner").get("title").textValue())
        getSoftAssertion().assertThat(event.payload[0].partner.guid.toString())
            .isEqualTo(contract.data.get("partner").get("partnerId").textValue())
        getSoftAssertion().assertThat(event.payload[0].fizicheskoeLitso.naimenovanie.toString())
            .isEqualTo(contract.data.get("fullName").textValue())
        getSoftAssertion().assertThat(event.payload[0].dolzhnost.guid.toString())
            .isEqualTo(contract.data.get("jobFunction").get("jobFunctionId").textValue())
        getSoftAssertion().assertThat(event.payload[0].dolzhnost.naimenovanie.toString())
            .isEqualTo(contract.data.get("jobFunction").get("title").textValue())
        getSoftAssertion().assertThat(event.payload[0].fizicheskoeLitso.guid.toString())
            .isEqualTo(contract.data.get("individualId").textValue())
        getSoftAssertion().assertThat(event.payload[0].dataOformleniya.toString())
            .isEqualTo(contract.data.get("employmentDate").textValue())
        getSoftAssertion().assertThat(event.payload[0].vidDogovora.naimenovanie.toString())
            .isEqualTo(contract.data.get("outsourceContractType").get("title").textValue())
        getSoftAssertion().assertThat(event.payload[0].vidDogovora.guid.toString())
            .isEqualTo(contract.data.get("outsourceContractType").get("outsourceContractTypeId").textValue())

        if (event.payload[0].dataUvolneniya != "0001-01-01T00:00:00Z") {
            getSoftAssertion().assertThat(event.payload[0].dataUvolneniya.toString())
                .isEqualTo(contract.data.get("retirementDate").textValue())
        } else
        {
            getSoftAssertion().assertThat (contract.data.get("retirementDate")).isNull()
        }

        return this
    }

    @Step("Check profile contract")
    fun checkProfileContractCount(
        contracts: Map<UUID, List<ru.samokat.employeeprofiles.api.contracts.domain.Contract>>,
        profileId: UUID, expectedCount: Int
    ): EmployeeAssertion {
        val contract = contracts[profileId]!!.count()
        getSoftAssertion().assertThat(contract).isEqualTo(expectedCount)

        return this
    }

    @Step("Check profile first login at")
    fun checkProfileFirstLoginAt(actual: String?, expected: String?): EmployeeAssertion {
        getSoftAssertion().assertThat(actual).isEqualTo(expected)
        return this
    }

    @Step("Check profile first login at")
    fun checkProfileFirstLoginAtIsNull(actual: ResultRow): EmployeeAssertion {
        getSoftAssertion().assertThat(actual[Profile.firstLoginAt]).isNull()
        return this
    }

    // requisitions
    @Step("Check requisition")
    fun checkOutsourceRequisitionDB(
        event: VneshnieSotrudniki,
        requisition: ResultRow,
        status: String = "NEW",
        version: Int = 1
    ): EmployeeAssertion {

        getSoftAssertion().assertThat(requisition[ProfileRequisition.accountingProfileId])
            .isEqualTo(event.payload[0].fizicheskoeLitso.guid.toString())
        getSoftAssertion().assertThat(requisition[ProfileRequisition.fullName])
            .isEqualTo(event.payload[0].fizicheskoeLitso.naimenovanie)
        getSoftAssertion().assertThat(requisition[ProfileRequisition.mobile]).isEqualTo(event.payload[0].telefon)
        getSoftAssertion().assertThat(requisition[ProfileRequisition.status]).isEqualTo(status)
        getSoftAssertion().assertThat(requisition[ProfileRequisition.version]).isEqualTo(version)
        getSoftAssertion().assertThat(requisition[ProfileRequisition.type]).isEqualTo("OUTSOURCE")

        return this
    }

    @Step("Check requisition")
    fun checkInnersourceRequisitionDB(
        event: PriemNaRabotuCFZ,
        requisition: ResultRow,
        status: String = "NEW",
        version: Int = 1
    ): EmployeeAssertion {

        getSoftAssertion().assertThat(requisition[ProfileRequisition.accountingProfileId])
            .isEqualTo(event.payload[0].fizicheskoeLitso.inn)
        getSoftAssertion().assertThat(requisition[ProfileRequisition.fullName])
            .isEqualTo(event.payload[0].fizicheskoeLitso.naimenovanie)
        getSoftAssertion().assertThat(requisition[ProfileRequisition.status]).isEqualTo(status)
        getSoftAssertion().assertThat(requisition[ProfileRequisition.version]).isEqualTo(version)
        getSoftAssertion().assertThat(requisition[ProfileRequisition.type]).isEqualTo("INNER_SOURCE")

        return this
    }

    @Step("Check requisition")
    fun checkRequisitionApi(
        event: VneshnieSotrudniki,
        requisition: GetProfileRequisitionView,
        status: ApiEnum<ProfileRequisitionStatus, String> = ApiEnum(ProfileRequisitionStatus.NEW)
    ): EmployeeAssertion {

        getSoftAssertion().assertThat(requisition.accountingProfileId)
            .isEqualTo(event.payload[0].fizicheskoeLitso.guid.toString())
        getSoftAssertion().assertThat(requisition.fullName).isEqualTo(event.payload[0].fizicheskoeLitso.naimenovanie)
        getSoftAssertion().assertThat(requisition.mobile).isEqualTo(event.payload[0].telefon)
        getSoftAssertion().assertThat(requisition.status).isEqualTo(status)

        return this
    }

    @Step("Check requisition")
    fun checkInnersourceRequisitionApi(
        event: PriemNaRabotuCFZ,
        requisition: GetProfileRequisitionView,
        status: ApiEnum<ProfileRequisitionStatus, String> = ApiEnum(ProfileRequisitionStatus.NEW)
    ): EmployeeAssertion {

        getSoftAssertion().assertThat(requisition.accountingProfileId)
            .isEqualTo(event.payload[0].fizicheskoeLitso.inn)
        getSoftAssertion().assertThat(requisition.fullName).isEqualTo(event.payload[0].fizicheskoeLitso.naimenovanie)
        getSoftAssertion().assertThat(requisition.mobile).isEqualTo("")
        getSoftAssertion().assertThat(requisition.status).isEqualTo(status)

        return this
    }

    @Step("Check requisition log version")
    fun checkRequisitionLogVersion(requisitionLog: ResultRow, version: Int = 1): EmployeeAssertion {
        getSoftAssertion().assertThat(requisitionLog[ProfileRequisitionLog.version]).isEqualTo(version)
        return this
    }

    @Step("Check requisitions count")
    fun checkRequisitionsCount(expectedCount: Int, actualCount: Int): EmployeeAssertion {
        getSoftAssertion().assertThat(expectedCount).isEqualTo(actualCount)
        return this
    }

    @Step("Check requisition not exist")
    fun checkRequisitionNotExists(requisition: Boolean): EmployeeAssertion {
        getSoftAssertion().assertThat(requisition).isFalse
        return this
    }

    @Step("Check requisition not exist")
    fun checkRequisitionExists(requisition: Boolean): EmployeeAssertion {
        getSoftAssertion().assertThat(requisition).isTrue
        return this
    }

    @Step("Check profile not exist")
    fun checkProfileNotExists(profile: Boolean): EmployeeAssertion {
        getSoftAssertion().assertThat(profile).isFalse
        return this
    }


    @Step("Check requisition present in requisitions list")
    fun checkRequisitionPresentInList(
        requisitionsList: SearchProfileRequisitionsView,
        requestId: UUID
    ): EmployeeAssertion {
        val filtered = requisitionsList.requisitions.filter { it.requisitionId == requestId }
        getSoftAssertion().assertThat(filtered.size).isNotEqualTo(0)
        return this
    }

    @Step("Check requisition present in requisitions list")
    fun checkRequisitionNotPresentInList(
        requisitionsList: SearchProfileRequisitionsView,
        requestId: UUID
    ): EmployeeAssertion {
        val filtered = requisitionsList.requisitions.filter { it.requisitionId == requestId }
        getSoftAssertion().assertThat(filtered.size).isEqualTo(0)
        return this
    }

    @Step("Check requisitions fields in list")
    fun checkRequisitionsFieldsInList(
        requisitionsList: SearchProfileRequisitionsView,
        requestId: UUID,
        event: VneshnieSotrudniki,
        status: ApiEnum<ProfileRequisitionStatus, String> = ApiEnum(ProfileRequisitionStatus.NEW)
    ): EmployeeAssertion {
        val requisition = requisitionsList.requisitions.filter { it.requisitionId == requestId }.first()
        getSoftAssertion().assertThat(requisition.accountingProfileId)
            .isEqualTo(event.payload[0].fizicheskoeLitso.guid.toString())
        getSoftAssertion().assertThat(requisition.fullName).isEqualTo(event.payload[0].fizicheskoeLitso.naimenovanie)
        getSoftAssertion().assertThat(requisition.mobile).isEqualTo(event.payload[0].telefon)
        getSoftAssertion().assertThat(requisition.status).isEqualTo(status)

        return this

    }

    @Step("Check that all requisitions in list satisfy the status condition")
    fun checkRequisitionsSatisfyStatusConditions(
        requisitionsList: SearchProfileRequisitionsView,
        status: ApiEnum<ProfileRequisitionStatus, String> = ApiEnum(ProfileRequisitionStatus.NEW)
    ): EmployeeAssertion {

        requisitionsList.requisitions.forEach {
            getSoftAssertion().assertThat(it.status).isEqualTo(status)
        }
        return this
    }

    @Step("Check that all requisitions in list satisfy the status condition")
    fun checkRequisitionsSatisfyTypeConditions(
        requisitionsList: SearchProfileRequisitionsView,
        type: ApiEnum<ProfileRequisitionType, String> = ApiEnum(ProfileRequisitionType.INNER_SOURCE)
    ): EmployeeAssertion {

        requisitionsList.requisitions.forEach {
            getSoftAssertion().assertThat(it.type).isEqualTo(type)
        }
        return this
    }
}
