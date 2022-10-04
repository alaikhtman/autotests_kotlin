package ru.samokat.mysamokat.tests.tests.staff_cave_apigateway.internshipsAPI

import org.apache.http.HttpStatus
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.samokat.employeeprofiles.api.common.domain.EmployeeVehicleType
import ru.samokat.employeeprofiles.api.darkstoreusers.domain.DarkstoreUserRole
import ru.samokat.employeeprofiles.api.darkstoreusers.internships.domain.FailureCode
import ru.samokat.employeeprofiles.api.darkstoreusers.internships.domain.RejectionCode
import ru.samokat.my.domain.employee.EmployeeRole
import ru.samokat.my.rest.api.enum.ApiEnum
import ru.samokat.mysamokat.tests.checkers.CommonAssertion
import ru.samokat.mysamokat.tests.checkers.StaffCaveApiGWAssertions
import ru.samokat.mysamokat.tests.dataproviders.Constants
import ru.samokat.mysamokat.tests.dataproviders.apiModels.staffcave.users.Vehicle
import ru.samokat.mysamokat.tests.dataproviders.preconditions.CommonPreconditions
import ru.samokat.mysamokat.tests.dataproviders.preconditions.EmployeePreconditions
import ru.samokat.mysamokat.tests.dataproviders.preconditions.StaffCaveApiGWPreconditions
import ru.samokat.mysamokat.tests.helpers.actions.EmployeeActions
import ru.samokat.mysamokat.tests.helpers.actions.StaffCaveApiGWActions
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Tags(Tag("staff-cave-apigateway"))
class UpdateInternship {

    @Autowired
    private lateinit var scActions: StaffCaveApiGWActions

    @Autowired
    private lateinit var employeeActions: EmployeeActions

    @Autowired
    private lateinit var commonPreconditions: CommonPreconditions

    private lateinit var commonAssertions: CommonAssertion

    private lateinit var scPreconditions: StaffCaveApiGWPreconditions

    private lateinit var scAssertion: StaffCaveApiGWAssertions

    private lateinit var token: String

    private lateinit var employeePreconditions: EmployeePreconditions

    @BeforeEach
    fun before() {
        scPreconditions = StaffCaveApiGWPreconditions()
        employeePreconditions = EmployeePreconditions()
        scAssertion = StaffCaveApiGWAssertions()
        commonAssertions = CommonAssertion()
        employeeActions.deleteProfile(Constants.mobile1)
        employeeActions.deleteProfile(Constants.mobile2)
        employeeActions.deleteProfileByAccountingProfileId(Constants.accountingProfileIdForRequisitions.toString())
        employeeActions.deleteProfileByAccountingProfileId(Constants.accountingProfileIdForRequisitions2.toString())
        employeeActions.deleteProfileByAccountingProfileId(Constants.innForRequisitions)
        employeeActions.deleteRequisitionFromDatabase(Constants.accountingProfileIdForRequisitions.toString())
        employeeActions.deleteRequisitionFromDatabase(Constants.accountingProfileIdForRequisitions2.toString())
        employeeActions.deleteRequisitionFromDatabase(Constants.innForRequisitions)
        employeeActions.deleteContractFromDatabase(Constants.accountingProfileIdForRequisitions.toString())
        employeeActions.deleteContractFromDatabase(Constants.accountingProfileIdForRequisitions2.toString())
        employeeActions.deleteContractFromDatabase(Constants.innForRequisitions)
        getAuthToken()
    }

    @AfterEach
    fun release() {
        scAssertion.assertAll()
        commonAssertions.assertAll()
        employeeActions.deleteProfile(Constants.mobile1)
        employeeActions.deleteProfile(Constants.mobile2)
        employeeActions.deleteProfileByAccountingProfileId(Constants.accountingProfileIdForRequisitions.toString())
        employeeActions.deleteProfileByAccountingProfileId(Constants.accountingProfileIdForRequisitions2.toString())
        employeeActions.deleteProfileByAccountingProfileId(Constants.innForRequisitions)
        employeeActions.deleteRequisitionFromDatabase(Constants.accountingProfileIdForRequisitions.toString())
        employeeActions.deleteRequisitionFromDatabase(Constants.accountingProfileIdForRequisitions2.toString())
        employeeActions.deleteRequisitionFromDatabase(Constants.innForRequisitions)
        employeeActions.deleteContractFromDatabase(Constants.accountingProfileIdForRequisitions.toString())
        employeeActions.deleteContractFromDatabase(Constants.accountingProfileIdForRequisitions2.toString())
        employeeActions.deleteContractFromDatabase(Constants.innForRequisitions)
    }

    fun getAuthToken(role: EmployeeRole = EmployeeRole.TECH_SUPPORT) {
        employeeActions.deleteProfile(Constants.mobile1)
        val profile = commonPreconditions.createProfile(
            roles = listOf(ApiEnum(role))
        )
        val authRequest = scPreconditions.fillAuthRequest(password = profile.generatedPassword!!)
        token = scActions.authProfilePassword(authRequest).accessToken

    }

    @Test
    @Tags(Tag("smoke"), Tag("emproIntegration"))
    @DisplayName("Update internship - change darkstore")
    fun updateInternshipChangeDarkstoreTest(){

        val createUserRequest = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
            darkstoreId = Constants.darkstoreId,
            vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
            staffPartnerId = Constants.staffPartnerId
        )
        val createInternshipRequest = scPreconditions.fillCreateInternshipRequest(
            darkstoreId = Constants.darkstoreId,
            plannedDate = Instant.now().plusSeconds(60).truncatedTo(ChronoUnit.SECONDS)
        )
        val updateInternshipRequest = scPreconditions.fillUpdateInternshipRequest(
            darkstoreId = Constants.updatedDarkstoreId,
            plannedDate = Instant.now().plusSeconds(60).truncatedTo(ChronoUnit.SECONDS)
        )

        val createdUserId = scActions.createUser(token, createUserRequest)!!.user.userId
        scActions.createInternship(token, createInternshipRequest, createdUserId, "deliveryman")
        scActions.updateInternship(token, updateInternshipRequest, createdUserId, "deliveryman")


        val internship = scActions.getInternshipByUserId(token, createdUserId)!!

        scAssertion.checkUserInternship(internship, updateInternshipRequest)
    }

    @Test
    @DisplayName("Update internship - update rejected internship date (by staff-manager)")
    fun updateInternshipRejectedDateTest(){

        getAuthToken(EmployeeRole.STAFF_MANAGER)
        val createUserRequest = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
            darkstoreId = Constants.darkstoreId,
            vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
            staffPartnerId = Constants.staffPartnerId
        )
        val createInternshipRequest = scPreconditions.fillCreateInternshipRequest(
            darkstoreId = Constants.darkstoreId,
            plannedDate = Instant.now().plusSeconds(3).truncatedTo(ChronoUnit.SECONDS)
        )
        employeePreconditions.setRejectInternshipRequest((ApiEnum(RejectionCode.R008)), issuerProfileId = UUID.randomUUID())

        val updateInternshipRequest = scPreconditions.fillUpdateInternshipRequest(
            darkstoreId = Constants.darkstoreId,
            plannedDate = Instant.now().plusSeconds(86400).truncatedTo(ChronoUnit.SECONDS), 2L
        )

        val createdUserId = scActions.createUser(token, createUserRequest)!!.user.userId
        scActions.createInternship(token, createInternshipRequest, createdUserId, "deliveryman")
        employeeActions.rejectInternship(createInternshipRequest.plannedDate, createdUserId, DarkstoreUserRole.DELIVERYMAN,
            employeePreconditions.rejectInternshipRequest()
        )
        scActions.updateInternship(token, updateInternshipRequest, createdUserId, "deliveryman")


        val internship = scActions.getInternshipByUserId(token, createdUserId)!!

        scAssertion.checkUserInternship(internship, updateInternshipRequest, version = 3L)
    }

    @Test
    @DisplayName("Update internship - update planned internship date")
    fun updateInternshipPlannedDateTest(){

        val createUserRequest = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
            darkstoreId = Constants.darkstoreId,
            vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
            staffPartnerId = Constants.staffPartnerId
        )
        val createInternshipRequest = scPreconditions.fillCreateInternshipRequest(
            darkstoreId = Constants.darkstoreId,
            plannedDate = Instant.now().plusSeconds(60).truncatedTo(ChronoUnit.SECONDS)
        )

        val updateInternshipRequest = scPreconditions.fillUpdateInternshipRequest(
            darkstoreId = Constants.darkstoreId,
            plannedDate = Instant.now().plusSeconds(86400).truncatedTo(ChronoUnit.SECONDS)
        )

        val createdUserId = scActions.createUser(token, createUserRequest)!!.user.userId
        scActions.createInternship(token, createInternshipRequest, createdUserId, "deliveryman")
        scActions.updateInternship(token, updateInternshipRequest, createdUserId, "deliveryman")


        val internship = scActions.getInternshipByUserId(token, createdUserId)!!

        scAssertion.checkUserInternship(internship, updateInternshipRequest)
    }

    @Test
    @DisplayName("Update internship - update cancelled internship date")
    fun updateInternshipCancelledDateTest(){

        val createUserRequest = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
            darkstoreId = Constants.darkstoreId,
            vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
            staffPartnerId = Constants.staffPartnerId
        )
        val createInternshipRequest = scPreconditions.fillCreateInternshipRequest(
            darkstoreId = Constants.darkstoreId,
            plannedDate = Instant.now().plusSeconds(3).truncatedTo(ChronoUnit.SECONDS)
        )
        employeePreconditions.setCancelInternshipRequest(issuerProfileId = UUID.randomUUID())
        val updateInternshipRequest = scPreconditions.fillUpdateInternshipRequest(
            darkstoreId = Constants.darkstoreId,
            plannedDate = Instant.now().plusSeconds(86400).truncatedTo(ChronoUnit.SECONDS), 2L
        )

        val createdUserId = scActions.createUser(token, createUserRequest)!!.user.userId
        scActions.createInternship(token, createInternshipRequest, createdUserId, "deliveryman")
        employeeActions.cancelInternship(
            createInternshipRequest.plannedDate,createdUserId, DarkstoreUserRole.DELIVERYMAN,
            employeePreconditions.cancelInternshipRequest()
        )

        scActions.updateInternship(token, updateInternshipRequest, createdUserId, "deliveryman")


        val internship = scActions.getInternshipByUserId(token, createdUserId)!!

        scAssertion.checkUserInternship(internship, updateInternshipRequest, version = 3L)
    }

    @Test
    @DisplayName("Update internship - update cancelled internship time")
    fun updateInternshipCancelledTimeTest(){

        val createUserRequest = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
            darkstoreId = Constants.darkstoreId,
            vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
            staffPartnerId = Constants.staffPartnerId
        )
        val createInternshipRequest = scPreconditions.fillCreateInternshipRequest(
            darkstoreId = Constants.darkstoreId,
            plannedDate = Instant.now().plusSeconds(3).truncatedTo(ChronoUnit.SECONDS)
        )
        employeePreconditions.setCancelInternshipRequest(issuerProfileId = UUID.randomUUID())
        val updateInternshipRequest = scPreconditions.fillUpdateInternshipRequest(
            darkstoreId = Constants.darkstoreId,
            plannedDate = Instant.now().plusSeconds(200).truncatedTo(ChronoUnit.SECONDS), 2L
        )

        val createdUserId = scActions.createUser(token, createUserRequest)!!.user.userId
        scActions.createInternship(token, createInternshipRequest, createdUserId, "deliveryman")
        employeeActions.cancelInternship(
            createInternshipRequest.plannedDate,createdUserId, DarkstoreUserRole.DELIVERYMAN,
            employeePreconditions.cancelInternshipRequest()
        )

        scActions.updateInternship(token, updateInternshipRequest, createdUserId, "deliveryman")


        val internship = scActions.getInternshipByUserId(token, createdUserId)!!

        scAssertion.checkUserInternship(internship, updateInternshipRequest, version = 3L)
    }

    @Test
    @DisplayName("Update internship - update rejected internship time")
    fun updateInternshipRejectedTimeTest(){

        val createUserRequest = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
            darkstoreId = Constants.darkstoreId,
            vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
            staffPartnerId = Constants.staffPartnerId
        )
        val createInternshipRequest = scPreconditions.fillCreateInternshipRequest(
            darkstoreId = Constants.darkstoreId,
            plannedDate = Instant.now().plusSeconds(3).truncatedTo(ChronoUnit.SECONDS)
        )
        employeePreconditions.setRejectInternshipRequest((ApiEnum(RejectionCode.R008)), issuerProfileId = UUID.randomUUID())

        val updateInternshipRequest = scPreconditions.fillUpdateInternshipRequest(
            darkstoreId = Constants.darkstoreId,
            plannedDate = Instant.now().plusSeconds(200).truncatedTo(ChronoUnit.SECONDS), 2L
        )

        val createdUserId = scActions.createUser(token, createUserRequest)!!.user.userId
        scActions.createInternship(token, createInternshipRequest, createdUserId, "deliveryman")
        employeeActions.rejectInternship(createInternshipRequest.plannedDate, createdUserId, DarkstoreUserRole.DELIVERYMAN,
            employeePreconditions.rejectInternshipRequest()
        )
        scActions.updateInternship(token, updateInternshipRequest, createdUserId, "deliveryman")


        val internship = scActions.getInternshipByUserId(token, createdUserId)!!

        scAssertion.checkUserInternship(internship, updateInternshipRequest, version = 3L)
    }

    @Test
    @DisplayName("Update internship - update planned internship time")
    fun updateInternshipPlannedTimeTest(){

        val createUserRequest = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
            darkstoreId = Constants.darkstoreId,
            vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
            staffPartnerId = Constants.staffPartnerId
        )
        val createInternshipRequest = scPreconditions.fillCreateInternshipRequest(
            darkstoreId = Constants.darkstoreId,
            plannedDate = Instant.now().plusSeconds(60).truncatedTo(ChronoUnit.SECONDS)
        )

        val updateInternshipRequest = scPreconditions.fillUpdateInternshipRequest(
            darkstoreId = Constants.darkstoreId,
            plannedDate = Instant.now().plusSeconds(200).truncatedTo(ChronoUnit.SECONDS)
        )

        val createdUserId = scActions.createUser(token, createUserRequest)!!.user.userId
        scActions.createInternship(token, createInternshipRequest, createdUserId, "deliveryman")
        scActions.updateInternship(token, updateInternshipRequest, createdUserId, "deliveryman")


        val internship = scActions.getInternshipByUserId(token, createdUserId)!!

        scAssertion.checkUserInternship(internship, updateInternshipRequest)
    }

    @Test
    @DisplayName("Update internship - with wrong version")
    fun updateInternshipWrongVersionTest(){

        val createUserRequest = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
            darkstoreId = Constants.darkstoreId,
            vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
            staffPartnerId = Constants.staffPartnerId
        )
        val createInternshipRequest = scPreconditions.fillCreateInternshipRequest(
            darkstoreId = Constants.darkstoreId,
            plannedDate = Instant.now().plusSeconds(60).truncatedTo(ChronoUnit.SECONDS)
        )
        val updateInternshipRequest = scPreconditions.fillUpdateInternshipRequest(
            darkstoreId = Constants.updatedDarkstoreId,
            plannedDate = Instant.now().plusSeconds(60).truncatedTo(ChronoUnit.SECONDS),
            version = 10L
        )

        val createdUserId = scActions.createUser(token, createUserRequest)!!.user.userId
        scActions.createInternship(token, createInternshipRequest, createdUserId, "deliveryman")
        val errors = scActions.updateInternshipWithError(token, updateInternshipRequest, createdUserId, "deliveryman", HttpStatus.SC_CONFLICT)

        commonAssertions.checkErrorMessage(errors!!.code.toString(), "ConcurrentModification")
            .checkErrorMessage(errors!!.message.toString(), "Concurrent modification has been detected")
    }

    @Test
    @DisplayName("Update internship - update role")
    fun updateInternshipRoleUpdateTest(){

        val createUserRequest = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
            darkstoreId = Constants.darkstoreId,
            vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
            staffPartnerId = Constants.staffPartnerId
        )
        val createInternshipRequest = scPreconditions.fillCreateInternshipRequest(
            darkstoreId = Constants.darkstoreId,
            plannedDate = Instant.now().plusSeconds(60).truncatedTo(ChronoUnit.SECONDS)
        )
        val updateInternshipRequest = scPreconditions.fillUpdateInternshipRequest(
            darkstoreId = Constants.updatedDarkstoreId,
            plannedDate = Instant.now().plusSeconds(60).truncatedTo(ChronoUnit.SECONDS)
        )

        val createdUserId = scActions.createUser(token, createUserRequest)!!.user.userId
        scActions.createInternship(token, createInternshipRequest, createdUserId, "deliveryman")
        val errors = scActions.updateInternshipWithError(token, updateInternshipRequest, createdUserId, "picker", HttpStatus.SC_BAD_REQUEST)

        commonAssertions.checkErrorMessage(errors!!.code.toString(), "Incorrect")
            .checkErrorMessage(errors!!.message.toString(), "Darkstore user doesn't exist with provided role and userId")
    }

    @Test
    @DisplayName("Update internship - with time to past")
    fun updateInternshipUpdateTimeToPastTest(){

        val createUserRequest = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
            darkstoreId = Constants.darkstoreId,
            vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
            staffPartnerId = Constants.staffPartnerId
        )
        val createInternshipRequest = scPreconditions.fillCreateInternshipRequest(
            darkstoreId = Constants.darkstoreId,
            plannedDate = Instant.now().plusSeconds(60).truncatedTo(ChronoUnit.SECONDS)
        )
        val updateInternshipRequest = scPreconditions.fillUpdateInternshipRequest(
            darkstoreId = Constants.updatedDarkstoreId,
            plannedDate = Instant.now().minusSeconds(600).truncatedTo(ChronoUnit.SECONDS)
        )

        val createdUserId = scActions.createUser(token, createUserRequest)!!.user.userId
        scActions.createInternship(token, createInternshipRequest, createdUserId, "deliveryman")
        val errors = scActions.updateInternshipWithError(token, updateInternshipRequest, createdUserId, "deliveryman", HttpStatus.SC_BAD_REQUEST)

        commonAssertions.checkErrorMessage(errors!!.code.toString(), "Incorrect")
            .checkErrorMessage(errors!!.message.toString(), "Internship planned date must be in the near future")
    }

    @Test
    @DisplayName("Update internship - update rejected internship darkstore without date")
    fun updateInternshipRejectedDarkstoreTest(){

        val createUserRequest = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
            darkstoreId = Constants.darkstoreId,
            vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
            staffPartnerId = Constants.staffPartnerId
        )
        val createInternshipRequest = scPreconditions.fillCreateInternshipRequest(
            darkstoreId = Constants.darkstoreId,
            plannedDate = Instant.now().plusSeconds(3).truncatedTo(ChronoUnit.SECONDS)
        )
        employeePreconditions.setRejectInternshipRequest((ApiEnum(RejectionCode.R008)), issuerProfileId = UUID.randomUUID())

        val updateInternshipRequest = scPreconditions.fillUpdateInternshipRequest(
            darkstoreId = Constants.updatedDarkstoreId,
            plannedDate = createInternshipRequest.plannedDate
        )

        val createdUserId = scActions.createUser(token, createUserRequest)!!.user.userId
        scActions.createInternship(token, createInternshipRequest, createdUserId, "deliveryman")
        employeeActions.rejectInternship(createInternshipRequest.plannedDate, createdUserId, DarkstoreUserRole.DELIVERYMAN,
            employeePreconditions.rejectInternshipRequest()
        )
        val errors = scActions.updateInternshipWithError(token, updateInternshipRequest, createdUserId, "deliveryman", HttpStatus.SC_BAD_REQUEST)

        commonAssertions.checkErrorMessage(errors!!.code.toString(), "Incorrect")
            .checkErrorMessage(errors!!.message.toString(), "Internship planned date must be in the near future")
    }

    @Test
    @DisplayName("Update internship - update canceled internship darkstore without date")
    fun updateInternshipCancelledDarkstoreTest(){

        val createUserRequest = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
            darkstoreId = Constants.darkstoreId,
            vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
            staffPartnerId = Constants.staffPartnerId
        )
        val createInternshipRequest = scPreconditions.fillCreateInternshipRequest(
            darkstoreId = Constants.darkstoreId,
            plannedDate = Instant.now().plusSeconds(3).truncatedTo(ChronoUnit.SECONDS)
        )
        employeePreconditions.setCancelInternshipRequest(issuerProfileId = UUID.randomUUID())
        val updateInternshipRequest = scPreconditions.fillUpdateInternshipRequest(
            darkstoreId = Constants.updatedDarkstoreId,
            plannedDate = createInternshipRequest.plannedDate
        )

        val createdUserId = scActions.createUser(token, createUserRequest)!!.user.userId
        scActions.createInternship(token, createInternshipRequest, createdUserId, "deliveryman")
        employeeActions.cancelInternship(
            createInternshipRequest.plannedDate,createdUserId, DarkstoreUserRole.DELIVERYMAN,
            employeePreconditions.cancelInternshipRequest()
        )

        val errors = scActions.updateInternshipWithError(token, updateInternshipRequest, createdUserId, "deliveryman", HttpStatus.SC_BAD_REQUEST)

        commonAssertions.checkErrorMessage(errors!!.code.toString(), "Incorrect")
            .checkErrorMessage(errors!!.message.toString(), "Internship planned date must be in the near future")
    }

    @Test
    @DisplayName("Update internship - other user role")
    fun updateInternshipOtherUserRoleTest(){

        val createUserRequest = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.DARKSTORE_ADMIN)),
            darkstoreId = Constants.darkstoreId
        )

        val updateInternshipRequest = scPreconditions.fillUpdateInternshipRequest(
            darkstoreId = Constants.updatedDarkstoreId,
            plannedDate = Instant.now().plusSeconds(60).truncatedTo(ChronoUnit.SECONDS),
            version = 1L
        )

        val createdUserId = scActions.createUser(token, createUserRequest)!!.user.userId
        val errors = scActions.updateInternshipWithError(token, updateInternshipRequest, createdUserId, "deliveryman", HttpStatus.SC_BAD_REQUEST)

        commonAssertions.checkErrorMessage(errors!!.code.toString(), "Incorrect")
            .checkErrorMessage(errors!!.message.toString(), "Darkstore user doesn't exist with provided role and userId")
    }

    @Test
    @DisplayName("Update internship - for disabled profile")
    fun updateInternshipForDisabledProfileTest(){

        val createUserRequest = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
            darkstoreId = Constants.darkstoreId,
            vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
            staffPartnerId = Constants.staffPartnerId
        )
        val createInternshipRequest = scPreconditions.fillCreateInternshipRequest(
            darkstoreId = Constants.darkstoreId,
            plannedDate = Instant.now().plusSeconds(60).truncatedTo(ChronoUnit.SECONDS)
        )
        val updateInternshipRequest = scPreconditions.fillUpdateInternshipRequest(
            darkstoreId = Constants.updatedDarkstoreId,
            plannedDate = Instant.now().plusSeconds(60).truncatedTo(ChronoUnit.SECONDS),
            version = 1L
        )

        val createdUserId = scActions.createUser(token, createUserRequest)!!.user.userId
        scActions.createInternship(token, createInternshipRequest, createdUserId, "deliveryman")
        employeeActions.deleteProfile(createdUserId)
        val errors = scActions.updateInternshipWithError(token, updateInternshipRequest, createdUserId, "deliveryman", HttpStatus.SC_BAD_REQUEST)

        commonAssertions.checkErrorMessage(errors!!.code.toString(), "Incorrect")
            .checkErrorMessage(errors!!.message.toString(), "Darkstore user doesn't exist with provided role and userId")
    }

    @Test
    @DisplayName("Update internship - profile not exists")
    fun updateInternshipProfileNotExistTest(){

        val updateInternshipRequest = scPreconditions.fillUpdateInternshipRequest(
            darkstoreId = Constants.updatedDarkstoreId,
            plannedDate = Instant.now().plusSeconds(60).truncatedTo(ChronoUnit.SECONDS),
            version = 1L
        )

        val errors = scActions.updateInternshipWithError(token, updateInternshipRequest, UUID.randomUUID(), "deliveryman", HttpStatus.SC_BAD_REQUEST)

        commonAssertions.checkErrorMessage(errors!!.code.toString(), "Incorrect")
            .checkErrorMessage(errors!!.message.toString(), "Darkstore user doesn't exist with provided role and userId")
    }

    @Test
    @DisplayName("Update internship - update failed internship")
    fun updateInternshipFailedTest(){

        val createUserRequest = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
            darkstoreId = Constants.darkstoreId,
            vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
            staffPartnerId = Constants.staffPartnerId
        )
        val createInternshipRequest = scPreconditions.fillCreateInternshipRequest(
            darkstoreId = Constants.darkstoreId,
            plannedDate = Instant.now().plusSeconds(3).truncatedTo(ChronoUnit.SECONDS)
        )
        employeePreconditions.setCloseInternshipRequest(failureCode = (ApiEnum(FailureCode.F009)),
            status = (ApiEnum(ru.samokat.employeeprofiles.api.darkstoreusers.internships.domain.InternshipStatus.FAILED)),
            issuerProfileId = UUID.randomUUID()
        )

        val updateInternshipRequest = scPreconditions.fillUpdateInternshipRequest(
            darkstoreId = Constants.darkstoreId,
            plannedDate = Instant.now().plusSeconds(86400).truncatedTo(ChronoUnit.SECONDS)
        )

        val createdUserId = scActions.createUser(token, createUserRequest)!!.user.userId
        scActions.createInternship(token, createInternshipRequest, createdUserId, "deliveryman")
        employeeActions.closeInternship(createInternshipRequest.plannedDate, createdUserId, DarkstoreUserRole.DELIVERYMAN,
            employeePreconditions.closeInternshipRequest()
        )

        val errors = scActions.updateInternshipWithError(token, updateInternshipRequest, createdUserId, "deliveryman", HttpStatus.SC_BAD_REQUEST)

        commonAssertions.checkErrorMessage(errors!!.code.toString(), "Incorrect")
            .checkErrorMessage(errors!!.message.toString(), "It's forbidden to update the internship")

    }

    @Test
    @DisplayName("Update internship - update done internship")
    fun updateInternshipDoneTest(){

        val createUserRequest = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
            darkstoreId = Constants.darkstoreId,
            vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
            staffPartnerId = Constants.staffPartnerId
        )
        val createInternshipRequest = scPreconditions.fillCreateInternshipRequest(
            darkstoreId = Constants.darkstoreId,
            plannedDate = Instant.now().plusSeconds(3).truncatedTo(ChronoUnit.SECONDS)
        )
        employeePreconditions.setCloseInternshipRequest(
            status = (ApiEnum(ru.samokat.employeeprofiles.api.darkstoreusers.internships.domain.InternshipStatus.DONE)),
            issuerProfileId = UUID.randomUUID()
        )

        val updateInternshipRequest = scPreconditions.fillUpdateInternshipRequest(
            darkstoreId = Constants.darkstoreId,
            plannedDate = Instant.now().plusSeconds(86400).truncatedTo(ChronoUnit.SECONDS)
        )

        val createdUserId = scActions.createUser(token, createUserRequest)!!.user.userId
        scActions.createInternship(token, createInternshipRequest, createdUserId, "deliveryman")
        employeeActions.closeInternship(createInternshipRequest.plannedDate, createdUserId, DarkstoreUserRole.DELIVERYMAN,
            employeePreconditions.closeInternshipRequest()
        )

        val errors = scActions.updateInternshipWithError(token, updateInternshipRequest, createdUserId, "deliveryman", HttpStatus.SC_BAD_REQUEST)

        commonAssertions.checkErrorMessage(errors!!.code.toString(), "Incorrect")
            .checkErrorMessage(errors!!.message.toString(), "It's forbidden to update the internship")

    }

}