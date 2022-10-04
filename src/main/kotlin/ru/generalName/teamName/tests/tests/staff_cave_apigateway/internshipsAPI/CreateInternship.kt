package ru.samokat.mysamokat.tests.tests.staff_cave_apigateway.internshipsAPI

import org.apache.http.HttpStatus
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.samokat.employeeprofiles.api.common.domain.EmployeeVehicleType
import ru.samokat.employeeprofiles.api.darkstoreusers.domain.DarkstoreUserRole
import ru.samokat.employeeprofiles.api.darkstoreusers.domain.DarkstoreUserState
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
class CreateInternship {
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
    @DisplayName("Create internship - deliveryman")
    fun createInternshipDeliverymanTest(){

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
    @DisplayName("Create internship - picker (by staff-manager)")
    fun createInternshipPickerTest(){
        getAuthToken(EmployeeRole.STAFF_MANAGER)
        val createUserRequest = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.PICKER)),
            darkstoreId = Constants.darkstoreId,
            staffPartnerId = Constants.staffPartnerId
        )
        val createInternshipRequest = scPreconditions.fillCreateInternshipRequest(
            darkstoreId = Constants.darkstoreId,
            plannedDate = Instant.now().plusSeconds(60).truncatedTo(ChronoUnit.SECONDS)
        )

        val createdUserId = scActions.createUser(token, createUserRequest)!!.user.userId
        scActions.createInternship(token, createInternshipRequest, createdUserId, "picker")


        val internship = scActions.getInternshipByUserId(token, createdUserId)!!

        scAssertion.checkUserInternship(internship, createInternshipRequest, role = EmployeeRole.PICKER)
    }

    @Test
    @DisplayName("Create internship - on foreign darkstore")
    fun createInternshipOnForeignDarkstoreTest(){

        val createUserRequest = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
            darkstoreId = Constants.darkstoreId,
            vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
            staffPartnerId = Constants.staffPartnerId
        )
        val createInternshipRequest = scPreconditions.fillCreateInternshipRequest(
            darkstoreId = Constants.updatedDarkstoreId,
            plannedDate = Instant.now().plusSeconds(60).truncatedTo(ChronoUnit.SECONDS)
        )

        val createdUserId = scActions.createUser(token, createUserRequest)!!.user.userId
        scActions.createInternship(token, createInternshipRequest, createdUserId, "deliveryman")


        val internship = scActions.getInternshipByUserId(token, createdUserId)!!

        scAssertion.checkUserInternship(internship, createInternshipRequest)
    }

    @Test
    @DisplayName("Create internship - for multirole (deliveryman")
    fun createInternshipMultiroleDeliverymanTest(){

        val createUserRequest = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN),ApiEnum(EmployeeRole.PICKER)),
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
    @DisplayName("Create internship - for multirole (picker")
    fun createInternshipMultirolePickerTest(){

        val createUserRequest = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN),ApiEnum(EmployeeRole.PICKER)),
            darkstoreId = Constants.darkstoreId,
            vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
            staffPartnerId = Constants.staffPartnerId
        )
        val createInternshipRequest = scPreconditions.fillCreateInternshipRequest(
            darkstoreId = Constants.darkstoreId,
            plannedDate = Instant.now().plusSeconds(60).truncatedTo(ChronoUnit.SECONDS)
        )

        val createdUserId = scActions.createUser(token, createUserRequest)!!.user.userId
        scActions.createInternship(token, createInternshipRequest, createdUserId, "picker")


        val internship = scActions.getInternshipByUserId(token, createdUserId)!!

        scAssertion.checkUserInternship(internship, createInternshipRequest, role = EmployeeRole.PICKER)
    }

    @Test
    @DisplayName("Create internship - with date in past")
    fun createInternshipWithDateInPastTest(){

        val createUserRequest = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
            darkstoreId = Constants.darkstoreId,
            vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
            staffPartnerId = Constants.staffPartnerId
        )
        val createInternshipRequest = scPreconditions.fillCreateInternshipRequest(
            darkstoreId = Constants.darkstoreId,
            plannedDate = Instant.now().minusSeconds(60).truncatedTo(ChronoUnit.SECONDS)
        )

        val createdUserId = scActions.createUser(token, createUserRequest)!!.user.userId
        val errors = scActions.createInternshipWithError(token, createInternshipRequest, createdUserId, "deliveryman", HttpStatus.SC_BAD_REQUEST)


        commonAssertions.checkErrorMessage(errors!!.code.toString(), "Incorrect")
            .checkErrorMessage(errors!!.message.toString(), "Internship planned date must be in the near future")
    }

    @Test
    @DisplayName("Create internship - with darkstore not exists")
    fun createInternshipWithDSNotExistsTest(){

        val createUserRequest = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
            darkstoreId = Constants.darkstoreId,
            vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
            staffPartnerId = Constants.staffPartnerId
        )
        val createInternshipRequest = scPreconditions.fillCreateInternshipRequest(
            darkstoreId = UUID.randomUUID(),
            plannedDate = Instant.now().plusSeconds(60).truncatedTo(ChronoUnit.SECONDS)
        )

        val createdUserId = scActions.createUser(token, createUserRequest)!!.user.userId
        val errors = scActions.createInternshipWithError(token, createInternshipRequest, createdUserId, "deliveryman", HttpStatus.SC_BAD_REQUEST)


        commonAssertions.checkErrorMessage(errors!!.code.toString(), "Incorrect")
            .checkErrorMessage(errors!!.message.toString(), "Darkstore was not found by ID")
    }

    @Test
    @DisplayName("Create internship - with wrong role ")
    fun createInternshipWithWrongRoleTest(){

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
        val errors = scActions.createInternshipWithError(token, createInternshipRequest, createdUserId, "darkstore_admin", HttpStatus.SC_BAD_REQUEST)


        commonAssertions.checkErrorMessage(errors!!.code.toString(), "Incorrect")
            .checkErrorMessage(errors!!.message.toString(), "Invalid internship user role")
    }

    @Test
    @DisplayName("Create internship - with invalid user role ")
    fun createInternshipWithInvalidRoleTest(){

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
        val errors = scActions.createInternshipWithError(token, createInternshipRequest, createdUserId, "picker", HttpStatus.SC_BAD_REQUEST)


        commonAssertions.checkErrorMessage(errors!!.code.toString(), "Incorrect")
            .checkErrorMessage(errors!!.message.toString(), "Darkstore user doesn't exist with provided role and userId")
    }

    @Test
    @DisplayName("Create internship - disabled profile ")
    fun createInternshipDisabledProfileTest(){

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
        employeeActions.deleteProfile(createdUserId)
        val errors = scActions.createInternshipWithError(token, createInternshipRequest, createdUserId, "deliveryman", HttpStatus.SC_BAD_REQUEST)


        commonAssertions.checkErrorMessage(errors!!.code.toString(), "Incorrect")
            .checkErrorMessage(errors!!.message.toString(), "Darkstore user doesn't exist with provided role and userId")
    }

    @Test
    @DisplayName("Create internship - profile not exists")
    fun createInternshipProfileNotExistsTest(){

        val createInternshipRequest = scPreconditions.fillCreateInternshipRequest(
            darkstoreId = Constants.darkstoreId,
            plannedDate = Instant.now().plusSeconds(60).truncatedTo(ChronoUnit.SECONDS)
        )

        val errors = scActions.createInternshipWithError(token, createInternshipRequest, UUID.randomUUID(), "deliveryman", HttpStatus.SC_BAD_REQUEST)


        commonAssertions.checkErrorMessage(errors!!.code.toString(), "Incorrect")
            .checkErrorMessage(errors!!.message.toString(), "Darkstore user doesn't exist with provided role and userId")
    }

    @Test
    @DisplayName("Create internship - internship already exists")
    fun createInternshipDeliverymanTwiceTest(){

        val createUserRequest = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
            darkstoreId = Constants.darkstoreId,
            vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
            staffPartnerId = Constants.staffPartnerId
        )
        val createInternshipRequest1 = scPreconditions.fillCreateInternshipRequest(
            darkstoreId = Constants.darkstoreId,
            plannedDate = Instant.now().plusSeconds(60).truncatedTo(ChronoUnit.SECONDS)
        )
        val createInternshipRequest2 = scPreconditions.fillCreateInternshipRequest(
            darkstoreId = Constants.updatedDarkstoreId,
            plannedDate = Instant.now().plusSeconds(60).truncatedTo(ChronoUnit.SECONDS)
        )

        val createdUserId = scActions.createUser(token, createUserRequest)!!.user.userId
        scActions.createInternship(token, createInternshipRequest1, createdUserId, "deliveryman")

        val errors = scActions.createInternshipWithError(token, createInternshipRequest2, createdUserId, "deliveryman", HttpStatus.SC_CONFLICT)

        commonAssertions.checkErrorMessage(errors!!.code.toString(), "ConcurrentModification")
            .checkErrorMessage(errors!!.message.toString(), "Concurrent modification has been detected")

    }

    @Test
    @DisplayName("Create internship - for working status")
    fun createInternshipForWorkingStatusTest(){

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

        val updateDSBuilder = employeePreconditions.fillUpdateProfileStatusOnDarkstore(
            createdUserId, ApiEnum(DarkstoreUserState.WORKING), null
        )

        employeeActions.updateProfileStatusOnDarkstore(
            createdUserId,
            createUserRequest.darkstoreId!!,
            DarkstoreUserRole.DELIVERYMAN,
            updateDSBuilder
        )


        val errors = scActions.createInternshipWithError(token, createInternshipRequest, createdUserId, "deliveryman", HttpStatus.SC_BAD_REQUEST)

        commonAssertions.checkErrorMessage(errors!!.code.toString(), "Incorrect")
            .checkErrorMessage(errors!!.message.toString(), "It's forbidden to create internship for the user")

    }

    @Test
    @DisplayName("Create internship - for not working status")
    fun createInternshipForNotWorkingStatusTest(){

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

        val updateDSBuilder = employeePreconditions.fillUpdateProfileStatusOnDarkstore(
            createdUserId, ApiEnum(DarkstoreUserState.NOT_WORKING), null
        )

        employeeActions.updateProfileStatusOnDarkstore(
            createdUserId,
            createUserRequest.darkstoreId!!,
            DarkstoreUserRole.DELIVERYMAN,
            updateDSBuilder
        )


        val errors = scActions.createInternshipWithError(token, createInternshipRequest, createdUserId, "deliveryman", HttpStatus.SC_BAD_REQUEST)

        commonAssertions.checkErrorMessage(errors!!.code.toString(), "Incorrect")
            .checkErrorMessage(errors!!.message.toString(), "It's forbidden to create internship for the user")

    }

    @Test
    @DisplayName("Create internship - for multirole (deliveryman), internship exists for other role")
    fun createInternshipMultiroleOneWithInternshipTest(){

        val createUserRequest = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN),ApiEnum(EmployeeRole.PICKER)),
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


        val errors = scActions.createInternshipWithError(token, createInternshipRequest, createdUserId, "picker", HttpStatus.SC_BAD_REQUEST)

        commonAssertions.checkErrorMessage(errors!!.code.toString(), "Incorrect")
            .checkErrorMessage(errors!!.message.toString(), "It's forbidden to create internship for the user")
    }

    @Test
    @DisplayName("Create internship - for not working status")
    fun createInternshipForNotWorkingMultiroleStatusTest(){

        val createUserRequest = scPreconditions.fillCreateUserRequest(
            roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN), ApiEnum(EmployeeRole.PICKER)),
            darkstoreId = Constants.darkstoreId,
            vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
            staffPartnerId = Constants.staffPartnerId
        )
        val createInternshipRequest = scPreconditions.fillCreateInternshipRequest(
            darkstoreId = Constants.darkstoreId,
            plannedDate = Instant.now().plusSeconds(60).truncatedTo(ChronoUnit.SECONDS)
        )
        val createdUserId = scActions.createUser(token, createUserRequest)!!.user.userId

        val updateDSBuilder = employeePreconditions.fillUpdateProfileStatusOnDarkstore(
            createdUserId, ApiEnum(DarkstoreUserState.NOT_WORKING), null
        )

        employeeActions.updateProfileStatusOnDarkstore(
            createdUserId,
            createUserRequest.darkstoreId!!,
            DarkstoreUserRole.DELIVERYMAN,
            updateDSBuilder
        )


        val errors = scActions.createInternshipWithError(token, createInternshipRequest, createdUserId, "picker", HttpStatus.SC_BAD_REQUEST)

        commonAssertions.checkErrorMessage(errors!!.code.toString(), "Incorrect")
            .checkErrorMessage(errors!!.message.toString(), "It's forbidden to create internship for the user")

    }
}

