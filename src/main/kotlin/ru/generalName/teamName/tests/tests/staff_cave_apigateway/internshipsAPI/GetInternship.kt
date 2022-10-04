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
import ru.samokat.mysamokat.tests.dataproviders.apiModels.staffcave.internships.InternshipStatus
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
class GetInternship {

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

    fun getAuthToken() {
        val profile = commonPreconditions.createProfile(
            roles = listOf(ApiEnum(EmployeeRole.TECH_SUPPORT))
        )
        val authRequest = scPreconditions.fillAuthRequest(password = profile.generatedPassword!!)
        token = scActions.authProfilePassword(authRequest).accessToken

    }

    @Test
    @DisplayName("Get internship - planned status")
    fun getInternshipPlannedStatusTest(){

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

        val createdUserId = scActions.createUser(token, createUserRequest)!!.user.userId
        scActions.createInternship(token, createInternshipRequest, createdUserId, "deliveryman")


        val internship = scActions.getInternshipByUserId(token, createdUserId)!!

        scAssertion.checkUserInternship(internship, createInternshipRequest)
    }

    @Test
    @DisplayName("Get internship - rejected status")
    fun getInternshipRejectedStatusTest(){

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
        val createdUserId = scActions.createUser(token, createUserRequest)!!.user.userId
        scActions.createInternship(token, createInternshipRequest, createdUserId, "deliveryman")

        employeeActions.rejectInternship(createInternshipRequest.plannedDate, createdUserId, DarkstoreUserRole.DELIVERYMAN,
            employeePreconditions.rejectInternshipRequest()
        )


        val internship = scActions.getInternshipByUserId(token, createdUserId)!!

        scAssertion.checkUserInternship(internship, createInternshipRequest, version = 2L, status = InternshipStatus.REJECTED, isEditable = true)
    }

    @Test
    @DisplayName("Get internship - failed status")
    fun getInternshipFailedStatusTest(){

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
        val createdUserId = scActions.createUser(token, createUserRequest)!!.user.userId
        scActions.createInternship(token, createInternshipRequest, createdUserId, "deliveryman")

        employeeActions.closeInternship(createInternshipRequest.plannedDate, createdUserId, DarkstoreUserRole.DELIVERYMAN,
            employeePreconditions.closeInternshipRequest()
        )

        val internship = scActions.getInternshipByUserId(token, createdUserId)!!

        scAssertion.checkUserInternship(internship, createInternshipRequest, version = 2L, status = InternshipStatus.FAILED, isEditable = false)
    }

    @Test
    @DisplayName("Get internship - done status")
    fun getInternshipDoneStatusTest(){

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
        employeePreconditions.setCloseInternshipRequest(issuerProfileId = UUID.randomUUID(), status = (ApiEnum(ru.samokat.employeeprofiles.api.darkstoreusers.internships.domain.InternshipStatus.DONE))
        )
        val createdUserId = scActions.createUser(token, createUserRequest)!!.user.userId
        scActions.createInternship(token, createInternshipRequest, createdUserId, "deliveryman")

        employeeActions.closeInternship(createInternshipRequest.plannedDate, createdUserId, DarkstoreUserRole.DELIVERYMAN,
            employeePreconditions.closeInternshipRequest()
        )

        val internship = scActions.getInternshipByUserId(token, createdUserId)!!

        scAssertion.checkUserInternship(internship, createInternshipRequest, version = 2L, status = InternshipStatus.DONE, isEditable = false)
    }

    @Test
    @DisplayName("Get internship - canceled status")
    fun getInternshipCanceledStatusTest(){

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
        val createdUserId = scActions.createUser(token, createUserRequest)!!.user.userId
        scActions.createInternship(token, createInternshipRequest, createdUserId, "deliveryman")

        employeeActions.cancelInternship(
            createInternshipRequest.plannedDate,createdUserId, DarkstoreUserRole.DELIVERYMAN,
            employeePreconditions.cancelInternshipRequest()
        )

        val internship = scActions.getInternshipByUserId(token, createdUserId)!!

        scAssertion.checkUserInternship(internship, createInternshipRequest, version = 2L, status = InternshipStatus.CANCELED, isEditable = true)
    }

    @Test
    @DisplayName("Get internship - user not exist")
    fun getInternshipUserNotExistTest(){

        val errors = scActions.getInternshipByUserIdWithError(token, UUID.randomUUID(), HttpStatus.SC_INTERNAL_SERVER_ERROR)!!

        commonAssertions.checkErrorMessage(errors!!.code.toString(), "InternalServerError")
            .checkErrorMessage(errors!!.message.toString(), "Failed to fetch darkstore users")

    }

    @Test
    @DisplayName("Get internship - disabled profile")
    fun getInternshipDisabledProfileTest(){

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

        val createdUserId = scActions.createUser(token, createUserRequest)!!.user.userId
        scActions.createInternship(token, createInternshipRequest, createdUserId, "deliveryman")
        employeeActions.deleteProfile(createdUserId)

        val errors = scActions.getInternshipByUserIdWithError(token, UUID.randomUUID(), HttpStatus.SC_INTERNAL_SERVER_ERROR)!!

        commonAssertions.checkErrorMessage(errors!!.code.toString(), "InternalServerError")
            .checkErrorMessage(errors!!.message.toString(), "Failed to fetch darkstore users")
    }

    @Test
    @DisplayName("Get internship - internship not exist")
    fun getInternshipNotExistTest(){

        val createUserRequest = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
            darkstoreId = Constants.darkstoreId,
            vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
            staffPartnerId = Constants.staffPartnerId
        )
        val createdUserId = scActions.createUser(token, createUserRequest)!!.user.userId

        val internships = scActions.getInternshipByUserId(token, createdUserId)!!

        scAssertion.checkInternshipsCount(internships, 0)
    }
}