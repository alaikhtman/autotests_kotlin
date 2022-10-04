package ru.samokat.mysamokat.tests.tests.employee_profiles.darkstoreUserAPI

import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.samokat.employeeprofiles.api.common.domain.Vehicle
import ru.samokat.employeeprofiles.api.darkstoreusers.domain.DarkstoreUserRole
import ru.samokat.employeeprofiles.api.darkstoreusers.domain.DarkstoreUserState
import ru.samokat.employeeprofiles.api.darkstoreusers.internships.domain.FailureCode
import ru.samokat.employeeprofiles.api.darkstoreusers.internships.domain.InternshipStatus
import ru.samokat.employeeprofiles.api.darkstoreusers.internships.domain.RejectionCode
import ru.samokat.my.domain.employee.EmployeeRole
import ru.samokat.employeeprofiles.api.common.domain.EmployeeVehicleType
import ru.samokat.my.rest.api.enum.ApiEnum
import ru.samokat.mysamokat.tests.checkers.EmployeeAssertion
import ru.samokat.mysamokat.tests.dataproviders.Constants
import ru.samokat.mysamokat.tests.dataproviders.preconditions.EmployeePreconditions
import ru.samokat.mysamokat.tests.helpers.actions.EmployeeActions
import java.time.Instant
import java.time.temporal.ChronoUnit

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Tag("empro")
class FindDarkstoreProfilesById {

    private lateinit var employeeAssertion: EmployeeAssertion

    @Autowired
    private lateinit var employeeActions: EmployeeActions

    private lateinit var employeePreconditions: EmployeePreconditions

    @BeforeEach
    fun before() {
        employeePreconditions = EmployeePreconditions()
        employeeAssertion = EmployeeAssertion()
        employeeActions.deleteProfile(Constants.mobile1)
        employeeActions.deleteProfile(Constants.mobile2)
    }

    @AfterEach
    fun release() {
        employeeAssertion.assertAll()
        employeeActions.deleteProfile(Constants.mobile1)
        employeeActions.deleteProfile(Constants.mobile2)
    }

    @Test
    @DisplayName("Find profiles: by one profileId")
    fun getProfilesByOneProfileId() {

        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
                email = null,
                mobile = Constants.mobile1,
            )
        val createdProfileId = employeeActions.createProfileId(createRequest)

        val findRequest = employeePreconditions.fillGetDarkstoreUsersByProfileIdsRequest(listOf(createdProfileId))
        val findResults = employeeActions.findDSProfilesByIds(findRequest)

        employeeAssertion.checkDarkstoreUserProfile(
            dsProfile = findResults.get(createdProfileId)!!.get(0),
            createRequest = createRequest,
            state = DarkstoreUserState.NEW,
            version = 1
        )
            .checkIsInternFlag(findResults.get(createdProfileId)!!.get(0).isIntern, false)
    }

    @Test
    @DisplayName("Find profiles: by several profileId")
    fun getProfilesBySeveralProfileIds() {

        val createRequest1 = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
                email = null,
                mobile = Constants.mobile1,
            )
        val createRequest2 = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
                email = null,
                mobile = Constants.mobile2,
            )
        val createdProfileId1 = employeeActions.createProfileId(createRequest1)
        val createdProfileId2 = employeeActions.createProfileId(createRequest2)

        val findRequest =
            employeePreconditions.fillGetDarkstoreUsersByProfileIdsRequest(listOf(createdProfileId1, createdProfileId2))
        val findResults = employeeActions.findDSProfilesByIds(findRequest)

        employeeAssertion.checkDarkstoreUserProfile(
            dsProfile = findResults.get(createdProfileId1)!!.get(0),
            createRequest = createRequest1,
            state = DarkstoreUserState.NEW,
            version = 1
        )
            .checkDarkstoreUserProfile(
                dsProfile = findResults.get(createdProfileId2)!!.get(0),
                createRequest = createRequest2,
                state = DarkstoreUserState.NEW,
                version = 1
            )
            .checkIsInternFlag(findResults.get(createdProfileId1)!!.get(0).isIntern, false)
            .checkIsInternFlag(findResults.get(createdProfileId2)!!.get(0).isIntern, false)
    }

    @Test
    @DisplayName("Find profiles: multirole profile")
    fun getProfilesWithMultirole() {
        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN), ApiEnum(EmployeeRole.PICKER)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
                email = null
            )
        val createdProfileId = employeeActions.createProfileId(createRequest)

        val updateDSBuilderPicker = employeePreconditions.fillUpdateProfileStatusOnDarkstore(
            createdProfileId,
            ApiEnum(DarkstoreUserState.NOT_WORKING),
            "violation", 1
        )
        val updateDSBuilderDeliveryman = employeePreconditions.fillUpdateProfileStatusOnDarkstore(
            createdProfileId,
            ApiEnum(DarkstoreUserState.WORKING),
            inactivityReason = null,
            1
        )
        employeeActions.updateProfileStatusOnDarkstore(
            createdProfileId,
            createRequest.darkstoreId!!,
            DarkstoreUserRole.PICKER,
            updateDSBuilderPicker
        )
        employeeActions.updateProfileStatusOnDarkstore(
            createdProfileId,
            createRequest.darkstoreId!!,
            DarkstoreUserRole.DELIVERYMAN,
            updateDSBuilderDeliveryman
        )

        val findRequest =
            employeePreconditions.fillGetDarkstoreUsersByProfileIdsRequest(listOf(createdProfileId))
        val findResults = employeeActions.findDSProfilesByIds(findRequest)

        employeeAssertion
            .checkDarkstoreUserProfile(
                dsProfile = findResults.get(createdProfileId)!!
                    .filter { it.role.enumValue == DarkstoreUserRole.DELIVERYMAN }.first(),
                createRequest = createRequest,
                state = DarkstoreUserState.WORKING,
                version = 2
            )
            .checkDarkstoreUserProfile(
                dsProfile = findResults.get(createdProfileId)!!.filter { it.role.enumValue == DarkstoreUserRole.PICKER }
                    .first(),
                createRequest = createRequest,
                state = DarkstoreUserState.NOT_WORKING,
                inactivityReason = "violation",
                version = 2
            )
            .checkIsInternFlag(findResults.get(createdProfileId)!!.get(0).isIntern, false)
            .checkIsInternFlag(findResults.get(createdProfileId)!!.get(1).isIntern, false)


    }

    @Test
    @DisplayName("Find profiles: profile is disabled")
    fun getDisableProfile() {
        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
                email = null
            )

        val createdProfileId = employeeActions.createProfileId(createRequest)

        employeeActions.deleteProfile(createdProfileId)

        val findRequest =
            employeePreconditions.fillGetDarkstoreUsersByProfileIdsRequest(listOf(createdProfileId))
        val findResults = employeeActions.findDSProfilesByIds(findRequest)

        employeeAssertion
            .checkListCount(findResults.count(), 0)
    }


    @Test
    @DisplayName("Find profiles: check isIntern flag for new with planned internship")
    fun checkIsInternFlagWithForNewPlannedInternship() {
        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
                email = null,
                mobile = Constants.mobile1,
            )
        employeePreconditions.setCreateInternshipRequest(
            createRequest.darkstoreId!!,
            plannedDate = Instant.now().plusSeconds(60).truncatedTo(ChronoUnit.SECONDS),
            issuerProfileId = createRequest.issuerProfileId
        )

        val createdProfileId = employeeActions.createProfileId(createRequest)
        employeeActions.createInternship(
            createdProfileId,
            DarkstoreUserRole.DELIVERYMAN,
            employeePreconditions.createInternshipRequest()
        )

        val findRequest = employeePreconditions.fillGetDarkstoreUsersByProfileIdsRequest(listOf(createdProfileId))
        val findResults = employeeActions.findDSProfilesByIds(findRequest)

        employeeAssertion.checkDarkstoreUserProfile(
            dsProfile = findResults.get(createdProfileId)!!.get(0),
            createRequest = createRequest,
            state = DarkstoreUserState.NEW,
            version = 2
        )
            .checkIsInternFlag(findResults.get(createdProfileId)!!.get(0).isIntern, true)

    }

    @Test
    @DisplayName("Find profiles: check isIntern flag for not_working with canceled internship")
    fun checkIsInternFlagWithForNotWorkingCancelledInternship() {
        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
                email = null,
                mobile = Constants.mobile1,
            )
        employeePreconditions.setCreateInternshipRequest(
            createRequest.darkstoreId!!,
            plannedDate = Instant.now().plusSeconds(60).truncatedTo(ChronoUnit.SECONDS),
            issuerProfileId = createRequest.issuerProfileId
        )
            .setCancelInternshipRequest(issuerProfileId = createRequest.issuerProfileId)

        val createdProfileId = employeeActions.createProfileId(createRequest)
        employeeActions.createInternship(
            createdProfileId,
            DarkstoreUserRole.DELIVERYMAN,
            employeePreconditions.createInternshipRequest()
        )
            .changeInternshipDateInDatabase(createdProfileId, "2021-08-20 13:08:30+03")
            .cancelInternshipWithoutWaiting(
            employeePreconditions.createInternshipRequest().plannedDate,
            createdProfileId,
            DarkstoreUserRole.DELIVERYMAN,
            employeePreconditions.cancelInternshipRequest()
        )

        val findRequest = employeePreconditions.fillGetDarkstoreUsersByProfileIdsRequest(listOf(createdProfileId))
        val findResults = employeeActions.findDSProfilesByIds(findRequest)

        employeeAssertion.checkDarkstoreUserProfile(
            dsProfile = findResults.get(createdProfileId)!!.get(0),
            createRequest = createRequest,
            state = DarkstoreUserState.NOT_WORKING,
            inactivityReason = "internship_failure",
            version = 3
        )
            .checkIsInternFlag(findResults.get(createdProfileId)!!.get(0).isIntern, true)

    }

    @Test
    @DisplayName("Find profiles: check isIntern flag for not_working with rejected internship")
    fun checkIsInternFlagWithForNotWorkingRejectedInternship() {
        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
                email = null,
                mobile = Constants.mobile1,
            )
        employeePreconditions.setCreateInternshipRequest(
            createRequest.darkstoreId!!,
            plannedDate = Instant.now().plusSeconds(30).truncatedTo(ChronoUnit.SECONDS),
            issuerProfileId = createRequest.issuerProfileId
        )
            .setRejectInternshipRequest((ApiEnum(RejectionCode.R001)), issuerProfileId = createRequest.issuerProfileId)

        val createdProfileId = employeeActions.createProfileId(createRequest)
        employeeActions.createInternship(
            createdProfileId,
            DarkstoreUserRole.DELIVERYMAN,
            employeePreconditions.createInternshipRequest()
        )
            .changeInternshipDateInDatabase(createdProfileId, "2021-08-20 13:08:30+03")
            .rejectInternshipWithoutWaiting(
                employeePreconditions.createInternshipRequest().plannedDate,
                createdProfileId,
                DarkstoreUserRole.DELIVERYMAN,
                employeePreconditions.rejectInternshipRequest()
            )

        val findRequest = employeePreconditions.fillGetDarkstoreUsersByProfileIdsRequest(listOf(createdProfileId))
        val findResults = employeeActions.findDSProfilesByIds(findRequest)

        employeeAssertion.checkDarkstoreUserProfile(
            dsProfile = findResults.get(createdProfileId)!!.get(0),
            createRequest = createRequest,
            state = DarkstoreUserState.NOT_WORKING,
            version = 3,
            inactivityReason = "internship_failure"
        )
            .checkIsInternFlag(findResults.get(createdProfileId)!!.get(0).isIntern, true)

    }

    @Test
    @DisplayName("Find profiles: check isIntern flag for not_working with failed internship")
    fun checkIsInternFlagWithForNotWorkingFailedInternship() {
        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
                email = null,
                mobile = Constants.mobile1,
            )
        employeePreconditions.setCreateInternshipRequest(
            createRequest.darkstoreId!!,
            plannedDate = Instant.now().plusSeconds(30).truncatedTo(ChronoUnit.SECONDS),
            issuerProfileId = createRequest.issuerProfileId
        )
            .setCloseInternshipRequest(
                failureCode = (ApiEnum(FailureCode.F006)),
                status = (ApiEnum(InternshipStatus.FAILED)),
                issuerProfileId = createRequest.issuerProfileId
            )
        val createdProfileId = employeeActions.createProfileId(createRequest)
        employeeActions.createInternship(
            createdProfileId,
            DarkstoreUserRole.DELIVERYMAN,
            employeePreconditions.createInternshipRequest()
        )
            .changeInternshipDateInDatabase(createdProfileId, "2021-08-20 13:08:30+03")
            .closeInternshipWithoutWaiting(
                employeePreconditions.createInternshipRequest().plannedDate,
                createdProfileId,
                DarkstoreUserRole.DELIVERYMAN,
                employeePreconditions.closeInternshipRequest()
            )

        val findRequest = employeePreconditions.fillGetDarkstoreUsersByProfileIdsRequest(listOf(createdProfileId))
        val findResults = employeeActions.findDSProfilesByIds(findRequest)

        employeeAssertion.checkDarkstoreUserProfile(
            dsProfile = findResults.get(createdProfileId)!!.get(0),
            createRequest = createRequest,
            state = DarkstoreUserState.NOT_WORKING,
            version = 3,
            inactivityReason = "internship_failure"
        )
            .checkIsInternFlag(findResults.get(createdProfileId)!!.get(0).isIntern, false)

    }

    @Test
    @DisplayName("Find profiles: check isIntern flag for working with done internship")
    fun checkIsInternFlagWithForWorkingDoneInternship() {
        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
                email = null,
                mobile = Constants.mobile1,
            )
        employeePreconditions.setCreateInternshipRequest(
            createRequest.darkstoreId!!,
            plannedDate = Instant.now().plusSeconds(30).truncatedTo(ChronoUnit.SECONDS),
            issuerProfileId = createRequest.issuerProfileId
        )
            .setCloseInternshipRequest(
                status = (ApiEnum(InternshipStatus.DONE)),
                issuerProfileId = createRequest.issuerProfileId
            )
        val createdProfileId = employeeActions.createProfileId(createRequest)
        employeeActions.createInternship(
            createdProfileId,
            DarkstoreUserRole.DELIVERYMAN,
            employeePreconditions.createInternshipRequest()
        )
            .changeInternshipDateInDatabase(createdProfileId, "2021-08-20 13:08:30+03")
            .closeInternshipWithoutWaiting(
                employeePreconditions.createInternshipRequest().plannedDate,
                createdProfileId,
                DarkstoreUserRole.DELIVERYMAN,
                employeePreconditions.closeInternshipRequest()
            )

        val findRequest = employeePreconditions.fillGetDarkstoreUsersByProfileIdsRequest(listOf(createdProfileId))
        val findResults = employeeActions.findDSProfilesByIds(findRequest)

        employeeAssertion.checkDarkstoreUserProfile(
            dsProfile = findResults.get(createdProfileId)!!.get(0),
            createRequest = createRequest,
            state = DarkstoreUserState.WORKING,
            version = 3
        )
            .checkIsInternFlag(findResults.get(createdProfileId)!!.get(0).isIntern, false)
    }

    @Test
    @DisplayName("Find profiles: check isIntern flag for not_working with done internship")
    fun checkIsInternFlagWithForNotWorkingDoneInternship() {
        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
                email = null,
                mobile = Constants.mobile1,
            )
        employeePreconditions.setCreateInternshipRequest(
            createRequest.darkstoreId!!,
            plannedDate = Instant.now().plusSeconds(30).truncatedTo(ChronoUnit.SECONDS),
            issuerProfileId = createRequest.issuerProfileId
        )
            .setCloseInternshipRequest(
                status = (ApiEnum(InternshipStatus.DONE)),
                issuerProfileId = createRequest.issuerProfileId
            )

        val createdProfileId = employeeActions.createProfileId(createRequest)
        val updateDSBuilderNotWorking = employeePreconditions.fillUpdateProfileStatusOnDarkstore(
            createdProfileId,
            ApiEnum(DarkstoreUserState.NOT_WORKING),
            "employee_request", 3
        )
        employeeActions.createInternship(
            createdProfileId,
            DarkstoreUserRole.DELIVERYMAN,
            employeePreconditions.createInternshipRequest()
        )
            .changeInternshipDateInDatabase(createdProfileId, "2021-08-20 13:08:30+03")
            .closeInternshipWithoutWaiting(
                employeePreconditions.createInternshipRequest().plannedDate,
                createdProfileId,
                DarkstoreUserRole.DELIVERYMAN,
                employeePreconditions.closeInternshipRequest()
            )
        employeeActions.updateProfileStatusOnDarkstore(
            createdProfileId,
            createRequest.darkstoreId!!,
            DarkstoreUserRole.DELIVERYMAN,
            updateDSBuilderNotWorking
        )

        val findRequest = employeePreconditions.fillGetDarkstoreUsersByProfileIdsRequest(listOf(createdProfileId))
        val findResults = employeeActions.findDSProfilesByIds(findRequest)

        employeeAssertion.checkDarkstoreUserProfile(
            dsProfile = findResults.get(createdProfileId)!!.get(0),
            createRequest = createRequest,
            state = DarkstoreUserState.NOT_WORKING,
            version = 4,
            inactivityReason = "employee_request",
        )
            .checkIsInternFlag(findResults.get(createdProfileId)!!.get(0).isIntern, false)
    }
}