package ru.samokat.mysamokat.tests.checkers

import io.qameta.allure.Step
import org.assertj.core.api.SoftAssertions
import org.jetbrains.exposed.sql.ResultRow
import org.springframework.stereotype.Service
import ru.samokat.employeeprofiles.api.common.domain.Vehicle
import ru.samokat.employeeprofiles.api.profiles.create.CreateProfileRequest
import ru.samokat.my.rest.api.enum.ApiEnum
import ru.samokat.mysamokat.tests.SuiteBase
import ru.samokat.mysamokat.tests.dataproviders.dataClasses.staffApiGTW.enum.CurrentUserRole
import ru.samokat.mysamokat.tests.dataproviders.dataClasses.staffApiGTW.enum.EmployeeUserRole
import ru.samokat.mysamokat.tests.dataproviders.dataClasses.staffApiGTW.enum.StafferRole
import ru.samokat.mysamokat.tests.dataproviders.dataClasses.staffApiGTW.enum.StafferState
import ru.samokat.mysamokat.tests.dataproviders.dataClasses.staffApiGTW.oauth.OAuthTokenView
import ru.samokat.mysamokat.tests.dataproviders.dataClasses.staffApiGTW.staffPartner.GetStaffPartnersView
import ru.samokat.mysamokat.tests.dataproviders.dataClasses.staffApiGTW.users.*
import ru.samokat.mysamokat.tests.dataproviders.helpers.database.employee_profiles_backend.Contract
import ru.samokat.mysamokat.tests.dataproviders.helpers.database.employee_profiles_backend.StaffPartner
import ru.samokat.mysamokat.tests.helpers.controllers.events.DataVneshnieSotrudniki
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.*


@Service
class StaffApiGWAssertions {

    private val softAssertion = SoftAssertions()
    fun getSoftAssertion(): SoftAssertions {
        return softAssertion
    }

    fun assertAll() {
        getSoftAssertion().assertAll()
    }

    // authorization
    @Step("check oauth tokens exists")
    fun checkOauthTokensExists(tokens: OAuthTokenView) {
        getSoftAssertion().assertThat(tokens.accessToken).isNotNull
        getSoftAssertion().assertThat(tokens.refreshToken).isNotNull
    }

    //users
    @Step("check user data")
    fun checkUserData(
        actualView: CurrentUserView,
        expectedRequest: CreateProfileRequest,
        profileId: UUID,
        city: String? = null,
        timeZone: ZoneId? = null,
        roles: MutableList<ApiEnum<CurrentUserRole, String>>,
        supervisedDarkstoresCity: MutableList<String>? = null,
        supervisedDarkstoresTimeZone: MutableList<ZoneId>? = null
    ) {
        getSoftAssertion().assertThat(actualView.id).isEqualTo(profileId)
        getSoftAssertion().assertThat(actualView.mobile).isEqualTo(expectedRequest.mobile.asStringWithPlus())
        getSoftAssertion().assertThat(actualView.name).isEqualTo(
            ProfileName(
                expectedRequest.name.firstName,
                expectedRequest.name.lastName,
                expectedRequest.name.middleName
            )
        )
        getSoftAssertion().assertThat(actualView.roles).isEqualTo(roles)

        if (expectedRequest.darkstoreId != null) {
            getSoftAssertion().assertThat(actualView.darkstore!!.id).isEqualTo(expectedRequest.darkstoreId)
            getSoftAssertion().assertThat(actualView.darkstore!!.cityCode).isEqualTo(city)
            getSoftAssertion().assertThat(actualView.darkstore!!.timezone).isEqualTo(timeZone)
        }

        if (expectedRequest.supervisedDarkstores != null) {
            for (i in 0 until actualView.supervisedDarkstores!!.count()) {
                getSoftAssertion().assertThat(actualView.supervisedDarkstores[i].id)
                    .isEqualTo(expectedRequest.supervisedDarkstores!![i])
                getSoftAssertion().assertThat(actualView.supervisedDarkstores[i].cityCode)
                    .isEqualTo(supervisedDarkstoresCity!![i])
                getSoftAssertion().assertThat(actualView.supervisedDarkstores[i].timezone)
                    .isEqualTo(supervisedDarkstoresTimeZone!![i])
            }
        }

    }

    @Step("Check user's comment")
    fun checkUsersComment(actualComment: String?, expectedComment: String?): StaffApiGWAssertions {
        getSoftAssertion().assertThat(actualComment).isEqualTo(expectedComment)
        return this
    }

    @Step("Check staff")
    fun checkStaff(
        staff: EmployeeListView,
        profileIds: List<UUID>,
        darkstoreIds: List<UUID>,
        roles: List<List<ApiEnum<EmployeeUserRole, String>>>,
        vehicle: List<Vehicle?>?,
        status: List<List<ApiEnum<StafferState, String>>>,
        stafferRoles: List<List<ApiEnum<StafferRole, String>>>
    ): StaffApiGWAssertions {
        for (i in 0 until profileIds.count()) {
            val listElement = staff.users.filter { it.id == profileIds[i] }[0]
            getSoftAssertion().assertThat(listElement.darkstore!!.id).isEqualTo(darkstoreIds[i])
            getSoftAssertion().assertThat(listElement.roles).isEqualTo(roles[i])

            if (vehicle != null && vehicle[i] != null) {
                getSoftAssertion().assertThat(listElement.vehicle!!.type.value).isEqualTo(vehicle[i]!!.type.value)
            }
            for (y in 0 until stafferRoles[i].count()) {
                getSoftAssertion().assertThat(listElement.staffers[y].state).isEqualTo(status[i][y])
                getSoftAssertion().assertThat(listElement.staffers[y].role).isEqualTo(stafferRoles[i][y])
            }
        }
        return this
    }

    @Step("Check group of users is absent")
    fun checkGroupOfUserIsAbsent(
        staff: EmployeeListView,
        profileIds: List<UUID>,

        ): StaffApiGWAssertions {
        for (i in 0 until profileIds.count()) {
            getSoftAssertion().assertThat(staff.users.filter { it.id == profileIds[i] }).isEmpty()

        }
        return this
    }


    @Step("Check contracts response")
    fun checkContractsResponse(
        contracts: SearchUsersContractsView,
        profileIds: MutableList<UUID>,
        contractsDB: MutableList<List<ResultRow>>

    ): StaffApiGWAssertions {

        for (i in 0 until profileIds.count()) {
            val actualContracts = contracts.usersContracts.getValue(profileIds[i]).map { it.accountingContractId }
            val actualTitles = contracts.usersContracts.getValue(profileIds[i]).map { it.title }
            val retirementsData: List<Instant?> =
                contracts.usersContracts.getValue(profileIds[i]).map { it.retirementDate }

            for (y in 0 until contractsDB[i].count()) {
                getSoftAssertion().assertThat(actualContracts[y])
                    .isEqualTo(UUID.fromString(contractsDB[i][y][Contract.accountingContractId]))

                val data =
                    SuiteBase.jacksonObjectMapper.readValue(
                        contractsDB[i][y][Contract.data],
                        DataVneshnieSotrudniki::class.java
                    )

                getSoftAssertion().assertThat(actualTitles[y])
                    .isEqualTo(data.outsourceContractType.title)

                if (retirementsData.count() > 0 && retirementsData[y] != null) {

                    getSoftAssertion().assertThat(retirementsData[y]!!.truncatedTo(ChronoUnit.SECONDS))
                        .isEqualTo(Instant.parse(data.retirementDate).truncatedTo(ChronoUnit.SECONDS))
                }

            }

        }
        return this
    }

    @Step("Check contracts response")
    fun checkEmptyContractResponse(
        contracts: SearchUsersContractsView,
        profileIds: MutableList<UUID>
    ) {
        for (i in 0 until profileIds.count()) {
            val contracts = contracts.usersContracts.getValue(profileIds[i]).count()
            getSoftAssertion().assertThat(contracts).isEqualTo(0)
        }
    }

    @Step("Check assignee response")
    fun checkAssigneeResponse(
        assignees: AssigneeListView,
        profileRequest: MutableList<CreateProfileRequest>,
        availability: List<Boolean>,
        stafferState: List<List<StafferState>>,
        internList: List<List<Boolean>>,
        commentList: List<String?> = listOf(),
        assignmentsList: List<List<UUID?>> = listOf()
    ): StaffApiGWAssertions {

        val sortAssignees = assignees.assignees.sortedBy { it.assignee.mobile }
        getSoftAssertion().assertThat(sortAssignees.count()).isEqualTo(
            profileRequest.count()
        )
        for (i in 0 until profileRequest.count()) {
            getSoftAssertion().assertThat(sortAssignees[i].assignee.darkstore!!.id)
                .isEqualTo(profileRequest[i].darkstoreId)

            if (profileRequest[i].vehicle != null) {
                getSoftAssertion().assertThat(sortAssignees[i].assignee.vehicle!!.type.value)
                    .isEqualTo(profileRequest[i].vehicle!!.type.value)
            }

            getSoftAssertion().assertThat(sortAssignees[i].assignee.mobile)
                .isEqualTo(profileRequest[i].mobile.asStringWithPlus())
            getSoftAssertion().assertThat(sortAssignees[i].assignee.name.firstName)
                .isEqualTo(profileRequest[i].name.firstName)
            getSoftAssertion().assertThat(sortAssignees[i].assignee.name.lastName)
                .isEqualTo(profileRequest[i].name.lastName)
            getSoftAssertion().assertThat(sortAssignees[i].assignee.name.middleName)
                .isEqualTo(profileRequest[i].name.middleName)

            if (commentList.count() > 0 && commentList[i] != null) {
                getSoftAssertion().assertThat(sortAssignees[i].assignee.comment)
                    .isEqualTo(commentList[i])

            }
            getSoftAssertion().assertThat(sortAssignees[i].availability.available).isEqualTo(availability[i])

            if (!availability[i]) {
                for (a in 0 until assignmentsList[i].count()) {
                    getSoftAssertion().assertThat(sortAssignees[i].availability.conflictingShiftAssignments!![a].id)
                        .isEqualTo(assignmentsList[i][a])
                }
            }

            for (y in 0 until profileRequest[i].roles.count()) {
                getSoftAssertion().assertThat(sortAssignees[i].assignee.roles[y].value)
                    .isEqualTo(profileRequest[i].roles[y].value)

                getSoftAssertion().assertThat(sortAssignees[i].assignee.staffers[y].role.value)
                    .isEqualTo(profileRequest[i].roles[y].value)
                getSoftAssertion().assertThat(sortAssignees[i].assignee.staffers[y].state.value)
                    .isEqualTo(stafferState[i][y].toString())
                getSoftAssertion().assertThat(sortAssignees[i].assignee.staffers[y].isIntern)
                    .isEqualTo(internList[i][y])

            }
        }
        return this
    }

    @Step("Check employee is absent in response")
    fun checkEmployeeIsAbsentInAssigneeResponse(
        assignees: AssigneeListView,
        profileId: UUID
    ): StaffApiGWAssertions {
        val assigneesIds: MutableList<UUID> = mutableListOf()
        for (i in 0 until assignees.assignees.count()) {
            if (assignees.assignees[i].assignee.id == profileId) {
                assigneesIds.add(assignees.assignees[i].assignee.id)
            }
            getSoftAssertion().assertThat(assigneesIds).isEmpty()
        }

        return this

    }

    @Step("Check assignee response")
    fun checkAssigneeResponseIsEmpty(
        assignees: AssigneeListView
    ) {
        getSoftAssertion().assertThat(assignees.assignees.count()).isEqualTo(0)
    }

    @Step("Check statistics")
    fun checkStatistic(
        statistic: UsersStatisticsView,
        profileIds: MutableList<UUID>,
        scheduleDuration: Long,
        assignmentsDuration: Long,
        assignmentsCount: Int,
        mistakenAssignmentsCount: Int,
        cancelledByIssuerAssignmentsCount: Int,
        cancelledByAssigneeAssignmentsCount: Int,
        absenceAssignmentsCount: Int,
        workedOutShiftsCount: Int,
        workedOutShiftsDuration: Long
    ): StaffApiGWAssertions {
        for (i in 0 until profileIds.count()) {
            getSoftAssertion().assertThat((statistic.statistics.getValue(profileIds[i]).schedule.duration.toHours()))
                .isEqualTo(scheduleDuration)
            getSoftAssertion().assertThat((statistic.statistics.getValue(profileIds[i]).assignments.duration.toHours()))
                .isEqualTo(assignmentsDuration)
            getSoftAssertion().assertThat((statistic.statistics.getValue(profileIds[i]).assignments.count.assigned))
                .isEqualTo(assignmentsCount)
            getSoftAssertion().assertThat((statistic.statistics.getValue(profileIds[i]).assignments.count.canceled.mistaken))
                .isEqualTo(mistakenAssignmentsCount)
            getSoftAssertion().assertThat((statistic.statistics.getValue(profileIds[i]).assignments.count.canceled.byIssuer))
                .isEqualTo(cancelledByIssuerAssignmentsCount)
            getSoftAssertion().assertThat((statistic.statistics.getValue(profileIds[i]).assignments.count.canceled.byAssignee))
                .isEqualTo(cancelledByAssigneeAssignmentsCount)
            getSoftAssertion().assertThat((statistic.statistics.getValue(profileIds[i]).assignments.count.canceled.absence))
                .isEqualTo(absenceAssignmentsCount)
            getSoftAssertion().assertThat((statistic.statistics.getValue(profileIds[i]).workedOutShifts.duration.toHours()))
                .isEqualTo(workedOutShiftsDuration)
            getSoftAssertion().assertThat((statistic.statistics.getValue(profileIds[i]).workedOutShifts.count))
                .isEqualTo(workedOutShiftsCount)

        }
        return this
    }

    @Step("Check employee is absent statistics")
    fun checkEmployeeAbsentInStatistic(
        statistic: UsersStatisticsView,
        profileId: UUID
    ) {
        val profilesStat = statistic.statistics.map { it.key }

        for (i in 0 until profilesStat.count()) {
            getSoftAssertion().assertThat(profilesStat[i]).isNotEqualTo(profileId)

        }
    }

    @Step("Check staff partners")
    fun checkStaffPartners(expectedStaffPartners: MutableList<ResultRow>, actualStaffPartners: GetStaffPartnersView) {
        getSoftAssertion().assertThat(actualStaffPartners.partners.count())
            .isEqualTo(expectedStaffPartners.count())
        for (i in 0 until expectedStaffPartners.count()) {
            getSoftAssertion().assertThat(actualStaffPartners.partners[i].partnerId)
                .isEqualTo(expectedStaffPartners[i][StaffPartner.partnerId].toString())
            getSoftAssertion().assertThat(actualStaffPartners.partners[i].title)
                .isEqualTo(expectedStaffPartners[i][StaffPartner.partnerTitle])
            getSoftAssertion().assertThat(actualStaffPartners.partners[i].shortTitle)
                .isEqualTo(expectedStaffPartners[i][StaffPartner.partnerShortTitle])
            getSoftAssertion().assertThat(actualStaffPartners.partners[i].type)
                .isEqualTo(expectedStaffPartners[i][StaffPartner.partnerType])
        }
    }

}



