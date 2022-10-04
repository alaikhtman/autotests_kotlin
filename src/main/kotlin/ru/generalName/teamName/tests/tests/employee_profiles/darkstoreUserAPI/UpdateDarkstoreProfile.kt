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
import ru.samokat.mysamokat.tests.dataproviders.employee.DarkstoreUsersState
import ru.samokat.mysamokat.tests.dataproviders.preconditions.EmployeePreconditions
import ru.samokat.mysamokat.tests.helpers.actions.EmployeeActions
import java.time.Instant
import java.time.temporal.ChronoUnit

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Tag("empro")
class UpdateDarkstoreProfile {

    private lateinit var employeeAssertion: EmployeeAssertion

    @Autowired
    private lateinit var employeeActions: EmployeeActions

    private lateinit var employeePreconditions: EmployeePreconditions

    @BeforeEach
    fun before() {
        employeePreconditions = EmployeePreconditions()
        employeeAssertion = EmployeeAssertion()
        employeeActions.deleteProfile(Constants.mobile1)
    }

    @AfterEach
    fun release() {
        employeeAssertion.assertAll()
        employeeActions.deleteProfile(Constants.mobile1)
    }

    @Test
    @DisplayName("Update darkstore status: new - working")
    fun updateProfileStatusNewToWorking() {

        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
                email = null
            )

        val createdProfileId = employeeActions.createProfileId(createRequest)

        val updateDSBuilder = employeePreconditions.fillUpdateProfileStatusOnDarkstore(
            createdProfileId, ApiEnum(DarkstoreUserState.WORKING), null
        )

        employeeActions.updateProfileStatusOnDarkstore(
            createdProfileId,
            createRequest.darkstoreId!!,
            DarkstoreUserRole.DELIVERYMAN,
            updateDSBuilder
        )

        val darkstoreProfile = employeeActions.getDarkstoreUserById(
            createdProfileId,
            createRequest.darkstoreId!!,
            DarkstoreUserRole.DELIVERYMAN
        )
        val darkstoreProfileFromDB =
            employeeActions.getProfilesFromDSUserTable(createdProfileId, EmployeeRole.DELIVERYMAN)
        val darkstoreProfileLogFromDB =
            employeeActions.getProfilesFromDSUserLogTable(createdProfileId, EmployeeRole.DELIVERYMAN)

        employeeAssertion
            .checkDarkstoreUserProfile(
                dsProfile = darkstoreProfile,
                createRequest = createRequest,
                DarkstoreUserState.WORKING,
                version = 2
            )
            .checkProfileInDSUserTable(
                createRequest, darkstoreProfileFromDB, EmployeeRole.DELIVERYMAN, DarkstoreUsersState.WORKING.dbId
            )
            .checkProfileInDSUserLogTable(
                createRequest, darkstoreProfileLogFromDB, EmployeeRole.DELIVERYMAN
            )

    }

    @Test
    @DisplayName("Update darkstore status: new - not_working with inactivity reason employee_request")
    fun updateProfileStatusNewToNotWorkingEmployeeRequest() {
        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
                email = null
            )

        val createdProfileId = employeeActions.createProfileId(createRequest)

        val updateDSBuilderNotWorking = employeePreconditions.fillUpdateProfileStatusOnDarkstore(
            createdProfileId,
            ApiEnum(DarkstoreUserState.NOT_WORKING),
            "employee_request", 1
        )

        employeeActions.updateProfileStatusOnDarkstore(
            createdProfileId,
            createRequest.darkstoreId!!,
            DarkstoreUserRole.DELIVERYMAN,
            updateDSBuilderNotWorking
        )

        val darkstoreProfile = employeeActions.getDarkstoreUserById(
            createdProfileId,
            createRequest.darkstoreId!!,
            DarkstoreUserRole.DELIVERYMAN
        )
        val darkstoreProfileFromDB =
            employeeActions.getProfilesFromDSUserTable(createdProfileId, EmployeeRole.DELIVERYMAN)
        val darkstoreProfileLogFromDB =
            employeeActions.getProfilesFromDSUserLogTable(createdProfileId, EmployeeRole.DELIVERYMAN)

        employeeAssertion
            .checkDarkstoreUserProfile(
                dsProfile = darkstoreProfile,
                createRequest = createRequest,
                state = DarkstoreUserState.NOT_WORKING,
                inactivityReason = "employee_request",
                version = 2
            )
            .checkProfileInDSUserTable(
                createRequest, darkstoreProfileFromDB, EmployeeRole.DELIVERYMAN, DarkstoreUsersState.NOT_WORKING.dbId
            )
            .checkProfileInDSUserLogTable(
                createRequest, darkstoreProfileLogFromDB, EmployeeRole.DELIVERYMAN
            )
    }

    @Test
    @DisplayName("Update darkstore status: new - not_working with inactivity reason darkstore_admin_request")
    fun updateProfileStatusNewToNotWorkingDarkstoreAdminRequest() {
        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
                email = null
            )

        val createdProfileId = employeeActions.createProfileId(createRequest)

        val updateDSBuilderNotWorking = employeePreconditions.fillUpdateProfileStatusOnDarkstore(
            createdProfileId,
            ApiEnum(DarkstoreUserState.NOT_WORKING),
            "darkstore_admin_request", 1
        )

        employeeActions.updateProfileStatusOnDarkstore(
            createdProfileId,
            createRequest.darkstoreId!!,
            DarkstoreUserRole.DELIVERYMAN,
            updateDSBuilderNotWorking
        )

        val darkstoreProfile = employeeActions.getDarkstoreUserById(
            createdProfileId,
            createRequest.darkstoreId!!,
            DarkstoreUserRole.DELIVERYMAN
        )
        val darkstoreProfileFromDB =
            employeeActions.getProfilesFromDSUserTable(createdProfileId, EmployeeRole.DELIVERYMAN)
        val darkstoreProfileLogFromDB =
            employeeActions.getProfilesFromDSUserLogTable(createdProfileId, EmployeeRole.DELIVERYMAN)

        employeeAssertion
            .checkDarkstoreUserProfile(
                dsProfile = darkstoreProfile,
                createRequest = createRequest,
                state = DarkstoreUserState.NOT_WORKING,
            inactivityReason = "darkstore_admin_request",
                version = 2
            )
            .checkProfileInDSUserTable(
                createRequest, darkstoreProfileFromDB, EmployeeRole.DELIVERYMAN, DarkstoreUsersState.NOT_WORKING.dbId
            )
            .checkProfileInDSUserLogTable(
                createRequest, darkstoreProfileLogFromDB, EmployeeRole.DELIVERYMAN
            )
    }

    @Test
    @DisplayName("Update darkstore status: new - not_working with inactivity reason violation")
    fun updateProfileStatusNewToNotWorkingViolation() {
        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
                email = null
            )

        val createdProfileId = employeeActions.createProfileId(createRequest)

        val updateDSBuilderNotWorking = employeePreconditions.fillUpdateProfileStatusOnDarkstore(
            createdProfileId,
            ApiEnum(DarkstoreUserState.NOT_WORKING),
            "violation", 1
        )

        employeeActions.updateProfileStatusOnDarkstore(
            createdProfileId,
            createRequest.darkstoreId!!,
            DarkstoreUserRole.DELIVERYMAN,
            updateDSBuilderNotWorking
        )

        val darkstoreProfile = employeeActions.getDarkstoreUserById(
            createdProfileId,
            createRequest.darkstoreId!!,
            DarkstoreUserRole.DELIVERYMAN
        )
        val darkstoreProfileFromDB =
            employeeActions.getProfilesFromDSUserTable(createdProfileId, EmployeeRole.DELIVERYMAN)
        val darkstoreProfileLogFromDB =
            employeeActions.getProfilesFromDSUserLogTable(createdProfileId, EmployeeRole.DELIVERYMAN)

        employeeAssertion
            .checkDarkstoreUserProfile(
                dsProfile = darkstoreProfile,
                createRequest = createRequest,
                state = DarkstoreUserState.NOT_WORKING,
                inactivityReason = "violation",
                version = 2
            )
            .checkProfileInDSUserTable(
                createRequest, darkstoreProfileFromDB, EmployeeRole.DELIVERYMAN, DarkstoreUsersState.NOT_WORKING.dbId
            )
            .checkProfileInDSUserLogTable(
                createRequest, darkstoreProfileLogFromDB, EmployeeRole.DELIVERYMAN
            )
    }

    @Test
    @DisplayName("Update darkstore status: new - not_working with inactivity reason internship_failure")
    fun updateProfileStatusNewToNotWorkingInternshipFailure() {
        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
                email = null
            )

        val createdProfileId = employeeActions.createProfileId(createRequest)

        val updateDSBuilderNotWorking = employeePreconditions.fillUpdateProfileStatusOnDarkstore(
            createdProfileId,
            ApiEnum(DarkstoreUserState.NOT_WORKING),
            "internship_failure", 1
        )

        employeeActions.updateProfileStatusOnDarkstore(
            createdProfileId,
            createRequest.darkstoreId!!,
            DarkstoreUserRole.DELIVERYMAN,
            updateDSBuilderNotWorking
        )

        val darkstoreProfile = employeeActions.getDarkstoreUserById(
            createdProfileId,
            createRequest.darkstoreId!!,
            DarkstoreUserRole.DELIVERYMAN
        )
        val darkstoreProfileFromDB =
            employeeActions.getProfilesFromDSUserTable(createdProfileId, EmployeeRole.DELIVERYMAN)
        val darkstoreProfileLogFromDB =
            employeeActions.getProfilesFromDSUserLogTable(createdProfileId, EmployeeRole.DELIVERYMAN)

        employeeAssertion
            .checkDarkstoreUserProfile(
                dsProfile = darkstoreProfile,
                createRequest = createRequest,
                state = DarkstoreUserState.NOT_WORKING,
                inactivityReason = "internship_failure",
                version = 2
            )
            .checkProfileInDSUserTable(
                createRequest, darkstoreProfileFromDB, EmployeeRole.DELIVERYMAN, DarkstoreUsersState.NOT_WORKING.dbId
            )
            .checkProfileInDSUserLogTable(
                createRequest, darkstoreProfileLogFromDB, EmployeeRole.DELIVERYMAN
            )
    }

    @Test
    @DisplayName("Update darkstore status: new - not_working with inactivity reason automation_system_request")
    fun updateProfileStatusNewToNotWorkingAutomationSystemRequest() {
        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
                email = null
            )

        val createdProfileId = employeeActions.createProfileId(createRequest)

        val updateDSBuilderNotWorking = employeePreconditions.fillUpdateProfileStatusOnDarkstore(
            createdProfileId,
            ApiEnum(DarkstoreUserState.NOT_WORKING),
            "automation_system_request", 1
        )

        val errorMessage = employeeActions.updateProfileStatusOnDarkstoreWithError(
            createdProfileId,
            createRequest.darkstoreId!!,
            DarkstoreUserRole.DELIVERYMAN,
            updateDSBuilderNotWorking
        )

        val darkstoreProfile = employeeActions.getDarkstoreUserById(
            createdProfileId,
            createRequest.darkstoreId!!,
            DarkstoreUserRole.DELIVERYMAN
        )

        employeeAssertion
            .checkDarkstoreUserProfile(
                dsProfile = darkstoreProfile,
                createRequest = createRequest,
                state = DarkstoreUserState.NEW,
                inactivityReason = null,
                version = 1
            )
            .checkErrorMessage(errorMessage, "Forbidden inactivity reason")
    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Update darkstore status: working - not_working with inactivity reasonи employee_request")
    fun updateProfileStatusWorkingToNotWorkingEmployeeRequest() {
        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
                email = null
            )

        val createdProfileId = employeeActions.createProfileId(createRequest)

        val updateDSBuilderWorking = employeePreconditions.fillUpdateProfileStatusOnDarkstore(
            createdProfileId, ApiEnum(DarkstoreUserState.WORKING), null, 1
        )

        val updateDSBuilderNotWorking = employeePreconditions.fillUpdateProfileStatusOnDarkstore(
            createdProfileId,
            ApiEnum(DarkstoreUserState.NOT_WORKING),
            "employee_request", 2
        )

        employeeActions.updateProfileStatusOnDarkstore(
            createdProfileId,
            createRequest.darkstoreId!!,
            DarkstoreUserRole.DELIVERYMAN,
            updateDSBuilderWorking
        )
        employeeActions.updateProfileStatusOnDarkstore(
            createdProfileId,
            createRequest.darkstoreId!!,
            DarkstoreUserRole.DELIVERYMAN,
            updateDSBuilderNotWorking
        )

        val darkstoreProfile = employeeActions.getDarkstoreUserById(
            createdProfileId,
            createRequest.darkstoreId!!,
            DarkstoreUserRole.DELIVERYMAN
        )
        val darkstoreProfileFromDB =
            employeeActions.getProfilesFromDSUserTable(createdProfileId, EmployeeRole.DELIVERYMAN)
        val darkstoreProfileLogFromDB =
            employeeActions.getProfilesFromDSUserLogTable(createdProfileId, EmployeeRole.DELIVERYMAN)

        employeeAssertion
            .checkDarkstoreUserProfile(
                dsProfile = darkstoreProfile,
                createRequest = createRequest,
                state = DarkstoreUserState.NOT_WORKING,
                inactivityReason = "employee_request",
                version = 3
            )
            .checkProfileInDSUserTable(
                createRequest, darkstoreProfileFromDB, EmployeeRole.DELIVERYMAN, DarkstoreUsersState.NOT_WORKING.dbId
            )
            .checkProfileInDSUserLogTable(
                createRequest, darkstoreProfileLogFromDB, EmployeeRole.DELIVERYMAN
            )
    }

    @Test
    @DisplayName("Update darkstore status: working - not_working with inactivity reason darkstore_admin_request")
    fun updateProfileStatusWorkingToNotWorkingDarkstoreAdminRequest() {
        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
                email = null
            )

        val createdProfileId = employeeActions.createProfileId(createRequest)

        val updateDSBuilderWorking = employeePreconditions.fillUpdateProfileStatusOnDarkstore(
            createdProfileId, ApiEnum(DarkstoreUserState.WORKING), null, 1
        )

        val updateDSBuilderNotWorking = employeePreconditions.fillUpdateProfileStatusOnDarkstore(
            createdProfileId,
            ApiEnum(DarkstoreUserState.NOT_WORKING),
            "darkstore_admin_request", 2
        )

        employeeActions.updateProfileStatusOnDarkstore(
            createdProfileId,
            createRequest.darkstoreId!!,
            DarkstoreUserRole.DELIVERYMAN,
            updateDSBuilderWorking
        )
        employeeActions.updateProfileStatusOnDarkstore(
            createdProfileId,
            createRequest.darkstoreId!!,
            DarkstoreUserRole.DELIVERYMAN,
            updateDSBuilderNotWorking
        )

        val darkstoreProfile = employeeActions.getDarkstoreUserById(
            createdProfileId,
            createRequest.darkstoreId!!,
            DarkstoreUserRole.DELIVERYMAN
        )
        val darkstoreProfileFromDB =
            employeeActions.getProfilesFromDSUserTable(createdProfileId, EmployeeRole.DELIVERYMAN)
        val darkstoreProfileLogFromDB =
            employeeActions.getProfilesFromDSUserLogTable(createdProfileId, EmployeeRole.DELIVERYMAN)

        employeeAssertion
            .checkDarkstoreUserProfile(
                dsProfile = darkstoreProfile,
                createRequest = createRequest,
                state = DarkstoreUserState.NOT_WORKING,
                inactivityReason = "darkstore_admin_request",
                version = 3
            )
            .checkProfileInDSUserTable(
                createRequest, darkstoreProfileFromDB, EmployeeRole.DELIVERYMAN, DarkstoreUsersState.NOT_WORKING.dbId
            )
            .checkProfileInDSUserLogTable(
                createRequest, darkstoreProfileLogFromDB, EmployeeRole.DELIVERYMAN
            )
    }

    @Test
    @DisplayName("Update darkstore status: working - not_working with inactivity reason violation")
    fun updateProfileStatusWorkingToNotWorkingViolation() {
        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
                email = null
            )

        val createdProfileId = employeeActions.createProfileId(createRequest)

        val updateDSBuilderWorking = employeePreconditions.fillUpdateProfileStatusOnDarkstore(
            createdProfileId, ApiEnum(DarkstoreUserState.WORKING), null, 1
        )

        val updateDSBuilderNotWorking = employeePreconditions.fillUpdateProfileStatusOnDarkstore(
            createdProfileId,
            ApiEnum(DarkstoreUserState.NOT_WORKING),
            "violation", 2
        )

        employeeActions.updateProfileStatusOnDarkstore(
            createdProfileId,
            createRequest.darkstoreId!!,
            DarkstoreUserRole.DELIVERYMAN,
            updateDSBuilderWorking
        )
        employeeActions.updateProfileStatusOnDarkstore(
            createdProfileId,
            createRequest.darkstoreId!!,
            DarkstoreUserRole.DELIVERYMAN,
            updateDSBuilderNotWorking
        )

        val darkstoreProfile = employeeActions.getDarkstoreUserById(
            createdProfileId,
            createRequest.darkstoreId!!,
            DarkstoreUserRole.DELIVERYMAN
        )
        val darkstoreProfileFromDB =
            employeeActions.getProfilesFromDSUserTable(createdProfileId, EmployeeRole.DELIVERYMAN)
        val darkstoreProfileLogFromDB =
            employeeActions.getProfilesFromDSUserLogTable(createdProfileId, EmployeeRole.DELIVERYMAN)

        employeeAssertion
            .checkDarkstoreUserProfile(
                dsProfile = darkstoreProfile,
                createRequest = createRequest,
                state = DarkstoreUserState.NOT_WORKING,
                inactivityReason = "violation",
                version = 3
            )
            .checkProfileInDSUserTable(
                createRequest, darkstoreProfileFromDB, EmployeeRole.DELIVERYMAN, DarkstoreUsersState.NOT_WORKING.dbId
            )
            .checkProfileInDSUserLogTable(
                createRequest, darkstoreProfileLogFromDB, EmployeeRole.DELIVERYMAN
            )
    }

    @Test
    @DisplayName("Update darkstore status: working - not_working with inactivity reason internship_failure")
    fun updateProfileStatusWorkingToNotWorkingInternshipFailure() {
        // нельзя с такой причиной
        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
                email = null
            )

        val createdProfileId = employeeActions.createProfileId(createRequest)

        val updateDSBuilderWorking = employeePreconditions.fillUpdateProfileStatusOnDarkstore(
            createdProfileId, ApiEnum(DarkstoreUserState.WORKING), null, 1
        )

        val updateDSBuilderNotWorking = employeePreconditions.fillUpdateProfileStatusOnDarkstore(
            createdProfileId,
            ApiEnum(DarkstoreUserState.NOT_WORKING),
            "internship_failure", 2
        )

        employeeActions.updateProfileStatusOnDarkstore(
            createdProfileId,
            createRequest.darkstoreId!!,
            DarkstoreUserRole.DELIVERYMAN,
            updateDSBuilderWorking
        )

        val errorMessage = employeeActions.updateProfileStatusOnDarkstoreWithError(
            createdProfileId,
            createRequest.darkstoreId!!,
            DarkstoreUserRole.DELIVERYMAN,
            updateDSBuilderNotWorking
        )

        val darkstoreProfile = employeeActions.getDarkstoreUserById(
            createdProfileId,
            createRequest.darkstoreId!!,
            DarkstoreUserRole.DELIVERYMAN
        )

        employeeAssertion
            .checkDarkstoreUserProfile(
                dsProfile = darkstoreProfile,
                createRequest = createRequest,
                state = DarkstoreUserState.WORKING,
                inactivityReason = null,
                version = 2
            )
            .checkErrorMessage(errorMessage, "internship_failure reason is available only for new darkstore users")
    }

    @Test
    @DisplayName("Update darkstore status: working - not_working with inactivity reason automation_system_request")
    fun updateProfileStatusWorkingToNotWorkingAutomationSystemRequest() {
        // нельзя с такой причиной
        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
                email = null
            )

        val createdProfileId = employeeActions.createProfileId(createRequest)

        val updateDSBuilderWorking = employeePreconditions.fillUpdateProfileStatusOnDarkstore(
            createdProfileId, ApiEnum(DarkstoreUserState.WORKING), null, 1
        )

        val updateDSBuilderNotWorking = employeePreconditions.fillUpdateProfileStatusOnDarkstore(
            createdProfileId,
            ApiEnum(DarkstoreUserState.NOT_WORKING),
            "automation_system_request", 2
        )

        employeeActions.updateProfileStatusOnDarkstore(
            createdProfileId,
            createRequest.darkstoreId!!,
            DarkstoreUserRole.DELIVERYMAN,
            updateDSBuilderWorking
        )

        val errorMessage = employeeActions.updateProfileStatusOnDarkstoreWithError(
            createdProfileId,
            createRequest.darkstoreId!!,
            DarkstoreUserRole.DELIVERYMAN,
            updateDSBuilderNotWorking
        )

        val darkstoreProfile = employeeActions.getDarkstoreUserById(
            createdProfileId,
            createRequest.darkstoreId!!,
            DarkstoreUserRole.DELIVERYMAN
        )

        employeeAssertion
            .checkDarkstoreUserProfile(
                dsProfile = darkstoreProfile,
                createRequest = createRequest,
                state = DarkstoreUserState.WORKING,
                inactivityReason = null,
                version = 2
            )
            .checkErrorMessage(errorMessage, "Forbidden inactivity reason")

    }

    @Test
    @DisplayName("Update darkstore status: not_working - working with inactivity reason")
    fun updateProfileStatusNotWorkingToWorkingWithInactivityReason() {
        //not_working -> working: поле inactivityReason запрещено

        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
                email = null
            )

        val createdProfileId = employeeActions.createProfileId(createRequest)

        val updateDSBuilderNotWorking = employeePreconditions.fillUpdateProfileStatusOnDarkstore(
            createdProfileId,
            ApiEnum(DarkstoreUserState.NOT_WORKING),
            "employee_request", 1
        )

        val updateDSBuilderWorking = employeePreconditions.fillUpdateProfileStatusOnDarkstore(
            createdProfileId,
            ApiEnum(DarkstoreUserState.WORKING),
            "employee_request", 2
        )

        employeeActions.updateProfileStatusOnDarkstore(
            createdProfileId,
            createRequest.darkstoreId!!,
            DarkstoreUserRole.DELIVERYMAN,
            updateDSBuilderNotWorking
        )
        val errorMessage = employeeActions.updateProfileStatusOnDarkstoreWithError(
            createdProfileId,
            createRequest.darkstoreId!!,
            DarkstoreUserRole.DELIVERYMAN,
            updateDSBuilderWorking
        )

        val darkstoreProfile = employeeActions.getDarkstoreUserById(
            createdProfileId,
            createRequest.darkstoreId!!,
            DarkstoreUserRole.DELIVERYMAN
        )

        employeeAssertion
            .checkDarkstoreUserProfile(
                dsProfile = darkstoreProfile,
                createRequest = createRequest,
                state = DarkstoreUserState.NOT_WORKING,
                inactivityReason = "employee_request",
                version = 2
            )
            .checkErrorMessage(errorMessage, "Inactivity reason is available only for non-working darkstore users")
    }

    @Test
    @DisplayName("Update darkstore status: not_working - working")
    fun updateProfileStatusNotWorkingToWorking() {

        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
                email = null
            )

        val createdProfileId = employeeActions.createProfileId(createRequest)

        val updateDSBuilderNotWorking = employeePreconditions.fillUpdateProfileStatusOnDarkstore(
            createdProfileId,
            ApiEnum(DarkstoreUserState.NOT_WORKING),
            "employee_request", 1
        )

        val updateDSBuilderWorking = employeePreconditions.fillUpdateProfileStatusOnDarkstore(
            createdProfileId,
            ApiEnum(DarkstoreUserState.WORKING),
            null, 2
        )

        employeeActions.updateProfileStatusOnDarkstore(
            createdProfileId,
            createRequest.darkstoreId!!,
            DarkstoreUserRole.DELIVERYMAN,
            updateDSBuilderNotWorking
        )
        employeeActions.updateProfileStatusOnDarkstore(
            createdProfileId,
            createRequest.darkstoreId!!,
            DarkstoreUserRole.DELIVERYMAN,
            updateDSBuilderWorking
        )

        val darkstoreProfile = employeeActions.getDarkstoreUserById(
            createdProfileId,
            createRequest.darkstoreId!!,
            DarkstoreUserRole.DELIVERYMAN
        )
        val darkstoreProfileFromDB =
            employeeActions.getProfilesFromDSUserTable(createdProfileId, EmployeeRole.DELIVERYMAN)
        val darkstoreProfileLogFromDB =
            employeeActions.getProfilesFromDSUserLogTable(createdProfileId, EmployeeRole.DELIVERYMAN)

        employeeAssertion
            .checkDarkstoreUserProfile(
                dsProfile = darkstoreProfile,
                createRequest = createRequest,
                state = DarkstoreUserState.WORKING,
                inactivityReason = null,
                version = 3
            )
            .checkProfileInDSUserTable(
                createRequest, darkstoreProfileFromDB, EmployeeRole.DELIVERYMAN, DarkstoreUsersState.WORKING.dbId
            )
            .checkProfileInDSUserLogTable(
                createRequest, darkstoreProfileLogFromDB, EmployeeRole.DELIVERYMAN
            )


    }

    @Test
    @DisplayName("Update darkstore status: not_working - new")
    fun updateProfileStatusNotWorkingToNew() {
        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
                email = null
            )

        val createdProfileId = employeeActions.createProfileId(createRequest)

        val updateDSBuilderNotWorking = employeePreconditions.fillUpdateProfileStatusOnDarkstore(
            createdProfileId,
            ApiEnum(DarkstoreUserState.NOT_WORKING),
            "employee_request", 1
        )

        val updateDSBuilderWorking = employeePreconditions.fillUpdateProfileStatusOnDarkstore(
            createdProfileId,
            ApiEnum(DarkstoreUserState.NEW),
            null, 2
        )

        employeeActions.updateProfileStatusOnDarkstore(
            createdProfileId,
            createRequest.darkstoreId!!,
            DarkstoreUserRole.DELIVERYMAN,
            updateDSBuilderNotWorking
        )
        val errorMessage = employeeActions.updateProfileStatusOnDarkstoreWithError(
            createdProfileId,
            createRequest.darkstoreId!!,
            DarkstoreUserRole.DELIVERYMAN,
            updateDSBuilderWorking
        )

        val darkstoreProfile = employeeActions.getDarkstoreUserById(
            createdProfileId,
            createRequest.darkstoreId!!,
            DarkstoreUserRole.DELIVERYMAN
        )

        employeeAssertion
            .checkDarkstoreUserProfile(
                dsProfile = darkstoreProfile,
                createRequest = createRequest,
                state = DarkstoreUserState.NOT_WORKING,
                inactivityReason = "employee_request",
                version = 2
            )
            .checkErrorMessage(errorMessage, "Update to NEW darkstore state is forbidden")

    }

    @Test
    @DisplayName("Update darkstore status: working - new")
    fun updateProfileStatusWorkingToNewAutomationSystemRequest() {
        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
                email = null
            )

        val createdProfileId = employeeActions.createProfileId(createRequest)

        val updateDSBuilderWorking = employeePreconditions.fillUpdateProfileStatusOnDarkstore(
            createdProfileId,
            ApiEnum(DarkstoreUserState.WORKING),
            null, 1
        )

        val updateDSBuilderNew = employeePreconditions.fillUpdateProfileStatusOnDarkstore(
            createdProfileId,
            ApiEnum(DarkstoreUserState.NEW),
            null, 2
        )

        employeeActions.updateProfileStatusOnDarkstore(
            createdProfileId,
            createRequest.darkstoreId!!,
            DarkstoreUserRole.DELIVERYMAN,
            updateDSBuilderWorking
        )
        val errorMessage = employeeActions.updateProfileStatusOnDarkstoreWithError(
            createdProfileId,
            createRequest.darkstoreId!!,
            DarkstoreUserRole.DELIVERYMAN,
            updateDSBuilderNew
        )

        val darkstoreProfile = employeeActions.getDarkstoreUserById(
            createdProfileId,
            createRequest.darkstoreId!!,
            DarkstoreUserRole.DELIVERYMAN
        )

        employeeAssertion
            .checkDarkstoreUserProfile(
                dsProfile = darkstoreProfile,
                createRequest = createRequest,
                state = DarkstoreUserState.WORKING,
                inactivityReason = null,
                version = 2
            )
            .checkErrorMessage(errorMessage, "Update to NEW darkstore state is forbidden")

    }

    @Test
    @DisplayName("Update darkstore status: different inactivity reason for different roles")
    fun updateProfileStatusMultiroleBothNotWorking() {
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
            ApiEnum(DarkstoreUserState.NOT_WORKING),
            "employee_request", 1
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

        val darkstoreProfileDeliveryman = employeeActions.getDarkstoreUserById(
            createdProfileId,
            createRequest.darkstoreId!!,
            DarkstoreUserRole.DELIVERYMAN
        )

        val darkstoreProfilePicker = employeeActions.getDarkstoreUserById(
            createdProfileId,
            createRequest.darkstoreId!!,
            DarkstoreUserRole.PICKER
        )

        employeeAssertion
            .checkDarkstoreUserProfile(
                dsProfile = darkstoreProfileDeliveryman,
                createRequest = createRequest,
                state = DarkstoreUserState.NOT_WORKING,
                inactivityReason = "employee_request",
                version = 2
            )
            .checkDarkstoreUserProfile(
                dsProfile = darkstoreProfilePicker,
                createRequest = createRequest,
                state = DarkstoreUserState.NOT_WORKING,
                inactivityReason ="violation",
                version = 2
            )
    }

    @Test
    @DisplayName("Update darkstore status: only one role not working")
    fun updateProfileStatusMultiroleOneNotWorking() {
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

        employeeActions.updateProfileStatusOnDarkstore(
            createdProfileId,
            createRequest.darkstoreId!!,
            DarkstoreUserRole.PICKER,
            updateDSBuilderPicker
        )

        val darkstoreProfileDeliveryman = employeeActions.getDarkstoreUserById(
            createdProfileId,
            createRequest.darkstoreId!!,
            DarkstoreUserRole.DELIVERYMAN
        )

        val darkstoreProfilePicker = employeeActions.getDarkstoreUserById(
            createdProfileId,
            createRequest.darkstoreId!!,
            DarkstoreUserRole.PICKER
        )

        employeeAssertion
            .checkDarkstoreUserProfile(
                dsProfile = darkstoreProfileDeliveryman,
                createRequest = createRequest,
                state = DarkstoreUserState.NEW,
                inactivityReason = null,
                version = 1
            )
            .checkDarkstoreUserProfile(
                dsProfile = darkstoreProfilePicker,
                createRequest = createRequest,
                state = DarkstoreUserState.NOT_WORKING,
                inactivityReason = "violation",
                version = 2
            )
    }

    @Test
    @DisplayName("Update darkstore status: profile not exist on ds - 404")
    fun updateProfileStatusProfileNotExistOnDarkstore() {
        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
                email = null
            )
        val createdProfileId = employeeActions.createProfileId(createRequest)

        val updateDSBuilderPicker = employeePreconditions.fillUpdateProfileStatusOnDarkstore(
            createdProfileId,
            ApiEnum(DarkstoreUserState.NOT_WORKING),
            "violation", 1
        )

        val errorMessage = employeeActions.updateProfileStatusOnDarkstoreWithError(
            createdProfileId,
            Constants.updatedDarkstoreId,
            DarkstoreUserRole.DELIVERYMAN,
            updateDSBuilderPicker
        )

        employeeAssertion
            .checkErrorMessage(errorMessage, "Darkstore user was not found")
    }

    @Test
    @DisplayName("Update darkstore status: role not exist on ds - 404")
    fun updateProfileStatusProfileNotExistOnDarkstoreInThatRole() {
        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
                email = null
            )
        val createdProfileId = employeeActions.createProfileId(createRequest)

        val updateDSBuilderPicker = employeePreconditions.fillUpdateProfileStatusOnDarkstore(
            createdProfileId,
            ApiEnum(DarkstoreUserState.NOT_WORKING),
            "violation", 1
        )

        val errorMessage = employeeActions.updateProfileStatusOnDarkstoreWithError(
            createdProfileId,
            Constants.updatedDarkstoreId,
            DarkstoreUserRole.PICKER,
            updateDSBuilderPicker
        )

        employeeAssertion
            .checkErrorMessage(errorMessage, "Darkstore user was not found")
    }

    @Test
    @DisplayName("Update darkstore status: with wrong version -409")
    fun updateProfileStatusWithWrongVersion() {
        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
                email = null
            )
        val createdProfileId = employeeActions.createProfileId(createRequest)

        val updateDSBuilderPicker = employeePreconditions.fillUpdateProfileStatusOnDarkstore(
            createdProfileId,
            ApiEnum(DarkstoreUserState.NOT_WORKING),
            "violation", 3
        )

        val errorMessage = employeeActions.updateProfileStatusOnDarkstoreWithError(
            createdProfileId,
            createRequest.darkstoreId!!,
            DarkstoreUserRole.DELIVERYMAN,
            updateDSBuilderPicker
        )

        employeeAssertion
            .checkErrorMessage(errorMessage, "Concurrent update has been detected")
    }

    //TODO
    // нельзя сменить статус одной роли если есть стажировка для другой (при мультироли)

    @Test
    @DisplayName("Update darkstore status: with planned internship")
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

        val updateDSBuilder = employeePreconditions.fillUpdateProfileStatusOnDarkstore(
            createdProfileId,
            ApiEnum(DarkstoreUserState.NOT_WORKING),
            "violation", 1
        )
        employeeActions.createInternship(
            createdProfileId,
            DarkstoreUserRole.DELIVERYMAN,
            employeePreconditions.createInternshipRequest()
        )

        val errorMessage = employeeActions.updateProfileStatusOnDarkstoreWithError(
            createdProfileId,
            createRequest.darkstoreId!!,
            DarkstoreUserRole.DELIVERYMAN,
            updateDSBuilder
        )

        employeeAssertion
            .checkErrorMessage(errorMessage, "Internship status rejects the darkstore user state updating")
    }

    @Test
    @DisplayName("Update darkstore status: with canceled internship")
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
        val updateDSBuilder = employeePreconditions.fillUpdateProfileStatusOnDarkstore(
            createdProfileId,
            ApiEnum(DarkstoreUserState.NOT_WORKING),
            "violation", 1
        )
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

        val errorMessage = employeeActions.updateProfileStatusOnDarkstoreWithError(
            createdProfileId,
            createRequest.darkstoreId!!,
            DarkstoreUserRole.DELIVERYMAN,
            updateDSBuilder
        )

        employeeAssertion
            .checkErrorMessage(errorMessage, "Internship status rejects the darkstore user state updating")
    }

    @Test
    @DisplayName("Update darkstore status: with rejected internship")
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
        val updateDSBuilder = employeePreconditions.fillUpdateProfileStatusOnDarkstore(
            createdProfileId,
            ApiEnum(DarkstoreUserState.NOT_WORKING),
            "violation", 1
        )
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

        val errorMessage = employeeActions.updateProfileStatusOnDarkstoreWithError(
            createdProfileId,
            createRequest.darkstoreId!!,
            DarkstoreUserRole.DELIVERYMAN,
            updateDSBuilder
        )

        employeeAssertion
            .checkErrorMessage(errorMessage, "Internship status rejects the darkstore user state updating")
    }

    @Test
    @DisplayName("Update darkstore status: with failed internship")
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
        val updateDSBuilder = employeePreconditions.fillUpdateProfileStatusOnDarkstore(
            createdProfileId,
            ApiEnum(DarkstoreUserState.WORKING), null, 3
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
            updateDSBuilder
        )

        val darkstoreProfile = employeeActions.getDarkstoreUserById(
            createdProfileId,
            createRequest.darkstoreId!!,
            DarkstoreUserRole.DELIVERYMAN
        )
        employeeAssertion
            .checkDarkstoreUserProfile(
                dsProfile = darkstoreProfile,
                createRequest = createRequest,
                DarkstoreUserState.WORKING,
                version = 4
            )
    }

    @Test
    @DisplayName("Update darkstore status: with done internship")
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
        val updateDSBuilder = employeePreconditions.fillUpdateProfileStatusOnDarkstore(
            createdProfileId,
            ApiEnum(DarkstoreUserState.NOT_WORKING), null, 3
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
            updateDSBuilder
        )

        val darkstoreProfile = employeeActions.getDarkstoreUserById(
            createdProfileId,
            createRequest.darkstoreId!!,
            DarkstoreUserRole.DELIVERYMAN
        )
        employeeAssertion
            .checkDarkstoreUserProfile(
                dsProfile = darkstoreProfile,
                createRequest = createRequest,
                DarkstoreUserState.NOT_WORKING,
                version = 4
            )
    }


    @Test
    @DisplayName("Update darkstore status: with internship for other role")
    fun UpdateDarkstoreStatusWithInternshipForOtherRole() {
        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN), ApiEnum(EmployeeRole.PICKER)),
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
        val updateDSBuilder = employeePreconditions.fillUpdateProfileStatusOnDarkstore(
            createdProfileId,
            ApiEnum(DarkstoreUserState.NOT_WORKING),
            "violation", 1
        )
        employeeActions.createInternship(
            createdProfileId,
            DarkstoreUserRole.DELIVERYMAN,
            employeePreconditions.createInternshipRequest()
        )

        employeeActions.updateProfileStatusOnDarkstore(
            createdProfileId,
            createRequest.darkstoreId!!,
            DarkstoreUserRole.PICKER,
            updateDSBuilder
        )
        val darkstoreProfilePicker = employeeActions.getDarkstoreUserById(
            createdProfileId,
            createRequest.darkstoreId!!,
            DarkstoreUserRole.PICKER
        )
        val darkstoreProfileDeliveryman = employeeActions.getDarkstoreUserById(
            createdProfileId,
            createRequest.darkstoreId!!,
            DarkstoreUserRole.DELIVERYMAN
        )

        employeeAssertion
            .checkDarkstoreUserProfile(
                dsProfile = darkstoreProfilePicker,
                createRequest = createRequest,
                state = DarkstoreUserState.NOT_WORKING,
                inactivityReason = "violation",
                version = 2
            )
            .checkDarkstoreUserProfile(
                dsProfile = darkstoreProfileDeliveryman,
                createRequest = createRequest,
                state = DarkstoreUserState.NEW,
                inactivityReason = null,
                version = 2
            )
    }
}
