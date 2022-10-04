package ru.samokat.mysamokat.tests.tests.employee_profiles.darkstoreUserViolationsAPI

import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.samokat.employeeprofiles.api.common.domain.Vehicle
import ru.samokat.employeeprofiles.api.darkstoreusers.domain.DarkstoreUserRole
import ru.samokat.employeeprofiles.api.darkstoreusers.internships.domain.FailureCode
import ru.samokat.employeeprofiles.api.darkstoreusers.internships.domain.InternshipStatus
import ru.samokat.employeeprofiles.api.darkstoreusers.internships.domain.RejectionCode
import ru.samokat.employeeprofiles.api.darkstoreusers.violations.dictionary.get.ViolationView
import ru.samokat.employeeprofiles.api.darkstoreusers.violations.domain.ViolationCode
import ru.samokat.my.domain.employee.EmployeeRole
import ru.samokat.employeeprofiles.api.common.domain.EmployeeVehicleType
import ru.samokat.my.rest.api.enum.ApiEnum
import ru.samokat.mysamokat.tests.checkers.EmployeeAssertion
import ru.samokat.mysamokat.tests.dataproviders.Constants
import ru.samokat.mysamokat.tests.dataproviders.StringAndPhoneNumberGenerator
import ru.samokat.mysamokat.tests.dataproviders.preconditions.EmployeePreconditions
import ru.samokat.mysamokat.tests.helpers.actions.EmployeeActions
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Tag("empro")
class CreateViolations {

    private lateinit var employeeAssertion: EmployeeAssertion

    @Autowired
    private lateinit var employeeActions: EmployeeActions

    private lateinit var employeePreconditions: EmployeePreconditions


    private var violationDictionary: List<ViolationView> = mutableListOf()

    @BeforeEach
    fun before() {
        employeePreconditions = EmployeePreconditions()
        employeeAssertion = EmployeeAssertion()
        employeeActions.deleteProfile(Constants.mobile1)
        violationDictionary = employeeActions.getViolationsDictionary().violations
    }

    @AfterEach
    fun release() {
        employeeAssertion.assertAll()
        employeeActions.deleteProfile(Constants.mobile1)
    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Create violation with comment - critical")
    fun createCriticalViolationWithComment() {
        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
                email = null,
                mobile = Constants.mobile1
            )

        val createdProfileId = employeeActions.createProfileFullResult(createRequest).profileId

        val storeViolationRequest = employeePreconditions
            .fillStoreDarkstoreUserViolationRequest(
                violationComment = StringAndPhoneNumberGenerator.generateRandomString(10),
                violationCode = ViolationCode.V001
            )

        val createdViolationId = employeeActions.addViolation(
            createdProfileId,
            createRequest.darkstoreId!!,
            DarkstoreUserRole.DELIVERYMAN,
            storeViolationRequest
        ).violationId
        val violationTime = Instant.now()

        val violationFromApi =
            employeeActions.getViolation(createdProfileId, DarkstoreUserRole.DELIVERYMAN, createdViolationId)
        val violationFromDB = employeeActions.getViolationFromDB(createdViolationId)
        val violationLogFromDB = employeeActions.getViolationLogFromDB(createdViolationId)
        val violationType = violationDictionary.filter { it.code == ApiEnum(ViolationCode.V001) }.first()

        employeeAssertion
            .checkViolationsAreEquals(
                violationFromApi,
                storeViolationRequest,
                createRequest.darkstoreId!!,
                violationType, violationTime
            )
            .checkViolationsFromDatabase(
                violationFromDB,
                storeViolationRequest,
                createRequest.darkstoreId!!,
                DarkstoreUserRole.DELIVERYMAN,
                1L
            )
            .checkViolationsLogFromDatabase(
                violationLogFromDB,
                createRequest.darkstoreId!!,
                createdProfileId,
                DarkstoreUserRole.DELIVERYMAN,
                "created",
                1L
            )
    }

    @Test
    @DisplayName("Create violation without comment - medium")
    fun createMediumViolationWithoutComment() {
        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
                email = null,
                mobile = Constants.mobile1
            )

        val createdProfileId = employeeActions.createProfileFullResult(createRequest).profileId

        val storeViolationRequest = employeePreconditions
            .fillStoreDarkstoreUserViolationRequest(
                violationCode = ViolationCode.V007
            )

        val createdViolationId = employeeActions.addViolation(
            createdProfileId,
            createRequest.darkstoreId!!,
            DarkstoreUserRole.DELIVERYMAN,
            storeViolationRequest
        ).violationId
        val violationTime = Instant.now()

        val violationFromApi =
            employeeActions.getViolation(createdProfileId, DarkstoreUserRole.DELIVERYMAN, createdViolationId)
        val violationFromDB = employeeActions.getViolationFromDB(createdViolationId)
        val violationLogFromDB = employeeActions.getViolationLogFromDB(createdViolationId)
        val violationType = violationDictionary.filter { it.code == ApiEnum(ViolationCode.V007) }.first()

        employeeAssertion
            .checkViolationsAreEquals(
                violationFromApi,
                storeViolationRequest,
                createRequest.darkstoreId!!,
                violationType,
                violationTime
            )
            .checkViolationsFromDatabase(
                violationFromDB,
                storeViolationRequest,
                createRequest.darkstoreId!!,
                DarkstoreUserRole.DELIVERYMAN,
                1L
            )
            .checkViolationsLogFromDatabase(
                violationLogFromDB,
                createRequest.darkstoreId!!,
                createdProfileId,
                DarkstoreUserRole.DELIVERYMAN,
                "created",
                1L
            )
    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Create several different violations ")
    fun createSeveralViolations() {
        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
                email = null,
                mobile = Constants.mobile1
            )

        val createdProfileId = employeeActions.createProfileFullResult(createRequest).profileId

        val storeViolationRequest1 = employeePreconditions
            .fillStoreDarkstoreUserViolationRequest(
                violationCode = ViolationCode.V007
            )

        val storeViolationRequest2 = employeePreconditions
            .fillStoreDarkstoreUserViolationRequest(
                violationCode = ViolationCode.V001,
                violationComment = StringAndPhoneNumberGenerator.generateRandomString(10)
            )

        val createdViolationId1 = employeeActions.addViolation(
            createdProfileId,
            createRequest.darkstoreId!!,
            DarkstoreUserRole.DELIVERYMAN,
            storeViolationRequest1
        ).violationId

        val createdViolationId2 = employeeActions.addViolation(
            createdProfileId,
            createRequest.darkstoreId!!,
            DarkstoreUserRole.DELIVERYMAN,
            storeViolationRequest2
        ).violationId

        val violationTime = Instant.now()

        val violationFromApi1 =
            employeeActions.getViolation(createdProfileId, DarkstoreUserRole.DELIVERYMAN, createdViolationId1)
        val violationFromDB1 = employeeActions.getViolationFromDB(createdViolationId1)
        val violationLogFromDB1 = employeeActions.getViolationLogFromDB(createdViolationId1)
        val violationType1 = violationDictionary.filter { it.code == ApiEnum(ViolationCode.V007) }.first()

        val violationFromApi2 =
            employeeActions.getViolation(createdProfileId, DarkstoreUserRole.DELIVERYMAN, createdViolationId2)
        val violationFromDB2 = employeeActions.getViolationFromDB(createdViolationId2)
        val violationLogFromDB2 = employeeActions.getViolationLogFromDB(createdViolationId2)
        val violationType2 = violationDictionary.filter { it.code == ApiEnum(ViolationCode.V001) }.first()

        employeeAssertion
            .checkViolationsAreEquals(
                violationFromApi1,
                storeViolationRequest1,
                createRequest.darkstoreId!!,
                violationType1,
                violationTime
            )
            .checkViolationsFromDatabase(
                violationFromDB1,
                storeViolationRequest1,
                createRequest.darkstoreId!!,
                DarkstoreUserRole.DELIVERYMAN,
                1L
            )
            .checkViolationsLogFromDatabase(
                violationLogFromDB1,
                createRequest.darkstoreId!!,
                createdProfileId,
                DarkstoreUserRole.DELIVERYMAN,
                "created",
                1L
            )
            .checkViolationsAreEquals(
                violationFromApi2,
                storeViolationRequest2,
                createRequest.darkstoreId!!,
                violationType2,
                violationTime
            )
            .checkViolationsFromDatabase(
                violationFromDB2,
                storeViolationRequest2,
                createRequest.darkstoreId!!,
                DarkstoreUserRole.DELIVERYMAN,
                1L
            )
            .checkViolationsLogFromDatabase(
                violationLogFromDB2,
                createRequest.darkstoreId!!,
                createdProfileId,
                DarkstoreUserRole.DELIVERYMAN,
                "created",
                1L
            )
    }

    @Test
    @DisplayName("Create the same violations ")
    fun createTheSameViolations() {
        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
                email = null,
                mobile = Constants.mobile1
            )

        val createdProfileId = employeeActions.createProfileFullResult(createRequest).profileId

        val storeViolationRequest = employeePreconditions
            .fillStoreDarkstoreUserViolationRequest(
                violationComment = StringAndPhoneNumberGenerator.generateRandomString(10),
                violationCode = ViolationCode.V001
            )

        val createdViolationId1 = employeeActions.addViolation(
            createdProfileId,
            createRequest.darkstoreId!!,
            DarkstoreUserRole.DELIVERYMAN,
            storeViolationRequest
        ).violationId
        val createdViolationId2 = employeeActions.addViolation(
            createdProfileId,
            createRequest.darkstoreId!!,
            DarkstoreUserRole.DELIVERYMAN,
            storeViolationRequest
        ).violationId
        val violationTime = Instant.now()

        val violationFromApi1 =
            employeeActions.getViolation(createdProfileId, DarkstoreUserRole.DELIVERYMAN, createdViolationId1)
        val violationFromDB1 = employeeActions.getViolationFromDB(createdViolationId1)
        val violationLogFromDB1 = employeeActions.getViolationLogFromDB(createdViolationId1)
        val violationFromApi2 =
            employeeActions.getViolation(createdProfileId, DarkstoreUserRole.DELIVERYMAN, createdViolationId2)
        val violationFromDB2 = employeeActions.getViolationFromDB(createdViolationId2)
        val violationLogFromDB2 = employeeActions.getViolationLogFromDB(createdViolationId2)
        val violationType = violationDictionary.filter { it.code == ApiEnum(ViolationCode.V001) }.first()

        employeeAssertion
            .checkViolationsIdNotEquals(createdViolationId1, createdViolationId2)
            .checkViolationsAreEquals(
                violationFromApi1,
                storeViolationRequest,
                createRequest.darkstoreId!!,
                violationType,
                violationTime
            )
            .checkViolationsFromDatabase(
                violationFromDB1,
                storeViolationRequest,
                createRequest.darkstoreId!!,
                DarkstoreUserRole.DELIVERYMAN,
                1L
            )
            .checkViolationsLogFromDatabase(
                violationLogFromDB1,
                createRequest.darkstoreId!!,
                createdProfileId,
                DarkstoreUserRole.DELIVERYMAN,
                "created",
                1L
            ).checkViolationsAreEquals(
                violationFromApi2,
                storeViolationRequest,
                createRequest.darkstoreId!!,
                violationType,
                violationTime
            )
            .checkViolationsFromDatabase(
                violationFromDB2,
                storeViolationRequest,
                createRequest.darkstoreId!!,
                DarkstoreUserRole.DELIVERYMAN,
                1L
            )
            .checkViolationsLogFromDatabase(
                violationLogFromDB2,
                createRequest.darkstoreId!!,
                createdProfileId,
                DarkstoreUserRole.DELIVERYMAN,
                "created",
                1L
            )
    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Create violations for different roles")
    fun createViolationsFotDifferentRoles() {
        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN), ApiEnum(EmployeeRole.PICKER)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
                email = null,
                mobile = Constants.mobile1
            )

        val createdProfileId = employeeActions.createProfileFullResult(createRequest).profileId

        val storeViolationRequest1 = employeePreconditions
            .fillStoreDarkstoreUserViolationRequest(
                violationComment = StringAndPhoneNumberGenerator.generateRandomString(10),
                violationCode = ViolationCode.V001
            )

        val storeViolationRequest2 = employeePreconditions
            .fillStoreDarkstoreUserViolationRequest(
                violationComment = StringAndPhoneNumberGenerator.generateRandomString(10),
                violationCode = ViolationCode.V007
            )

        val createdViolationId1 = employeeActions.addViolation(
            createdProfileId,
            createRequest.darkstoreId!!,
            DarkstoreUserRole.DELIVERYMAN,
            storeViolationRequest1
        ).violationId

        val createdViolationId2 = employeeActions.addViolation(
            createdProfileId,
            createRequest.darkstoreId!!,
            DarkstoreUserRole.PICKER,
            storeViolationRequest2
        ).violationId

        val violationTime = Instant.now()

        val violationFromApi1 =
            employeeActions.getViolation(createdProfileId, DarkstoreUserRole.DELIVERYMAN, createdViolationId1)
        val violationFromDB1 = employeeActions.getViolationFromDB(createdViolationId1)
        val violationLogFromDB1 = employeeActions.getViolationLogFromDB(createdViolationId1)
        val violationType1 = violationDictionary.filter { it.code == ApiEnum(ViolationCode.V001) }.first()

        val violationFromApi2 =
            employeeActions.getViolation(createdProfileId, DarkstoreUserRole.PICKER, createdViolationId2)
        val violationFromDB2 = employeeActions.getViolationFromDB(createdViolationId2)
        val violationLogFromDB2 = employeeActions.getViolationLogFromDB(createdViolationId2)
        val violationType2 = violationDictionary.filter { it.code == ApiEnum(ViolationCode.V007) }.first()

        employeeAssertion
            .checkViolationsAreEquals(
                violationFromApi1,
                storeViolationRequest1,
                createRequest.darkstoreId!!,
                violationType1,
                violationTime
            )
            .checkViolationsFromDatabase(
                violationFromDB1,
                storeViolationRequest1,
                createRequest.darkstoreId!!,
                DarkstoreUserRole.DELIVERYMAN,
                1L
            )
            .checkViolationsLogFromDatabase(
                violationLogFromDB1,
                createRequest.darkstoreId!!,
                createdProfileId,
                DarkstoreUserRole.DELIVERYMAN,
                "created",
                1L
            )
            .checkViolationsAreEquals(
                violationFromApi2,
                storeViolationRequest2,
                createRequest.darkstoreId!!,
                violationType2,
                violationTime
            )
            .checkViolationsFromDatabase(
                violationFromDB2,
                storeViolationRequest2,
                createRequest.darkstoreId!!,
                DarkstoreUserRole.PICKER,
                1L
            )
            .checkViolationsLogFromDatabase(
                violationLogFromDB2,
                createRequest.darkstoreId!!,
                createdProfileId,
                DarkstoreUserRole.PICKER,
                "created",
                1L
            )
    }

    @Test
    @DisplayName("Create violation - profile not exist")
    fun createViolationProfileNotExist() {
        val storeViolationRequest = employeePreconditions
            .fillStoreDarkstoreUserViolationRequest(
                violationComment = StringAndPhoneNumberGenerator.generateRandomString(10),
                violationCode = ViolationCode.V001
            )

        val createdViolationErrorMessage = employeeActions.addViolationWithError(
            UUID.randomUUID(),
            Constants.darkstoreId,
            DarkstoreUserRole.DELIVERYMAN,
            storeViolationRequest
        ).message

        employeeAssertion.checkErrorMessage(createdViolationErrorMessage, "Darkstore user was not found")
    }

    @Test
    @DisplayName("Create violation - profile disable")
    fun createViolationProfileDisable() {
        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
                email = null,
                mobile = Constants.mobile1
            )

        val createdProfileId = employeeActions.createProfileFullResult(createRequest).profileId
        employeeActions.deleteProfile(createdProfileId)

        val storeViolationRequest = employeePreconditions
            .fillStoreDarkstoreUserViolationRequest(
                violationComment = StringAndPhoneNumberGenerator.generateRandomString(10),
                violationCode = ViolationCode.V001
            )

        val createdViolationErrorMessage = employeeActions.addViolationWithError(
            createdProfileId,
            createRequest.darkstoreId!!,
            DarkstoreUserRole.DELIVERYMAN,
            storeViolationRequest
        ).message

        employeeAssertion.checkErrorMessage(createdViolationErrorMessage, "Darkstore user was not found")
    }

    @Test
    @DisplayName("Create violation - profile not exist on DS")
    fun createViolationProfileNotExistsOnDS() {
        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
                email = null,
                mobile = Constants.mobile1
            )

        val createdProfileId = employeeActions.createProfileFullResult(createRequest).profileId
        employeeActions.deleteProfile(createdProfileId)

        val storeViolationRequest = employeePreconditions
            .fillStoreDarkstoreUserViolationRequest(
                violationComment = StringAndPhoneNumberGenerator.generateRandomString(10),
                violationCode = ViolationCode.V001
            )

        val createdViolationErrorMessage = employeeActions.addViolationWithError(
            createdProfileId,
            Constants.searchContactsDarkstore,
            DarkstoreUserRole.DELIVERYMAN,
            storeViolationRequest
        ).message

        employeeAssertion.checkErrorMessage(createdViolationErrorMessage, "Darkstore user was not found")
    }

    @Test
    @DisplayName("Create violation - profile role not exists")
    fun createViolationProfileRoleNotExists() {
        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
                email = null,
                mobile = Constants.mobile1
            )

        val createdProfileId = employeeActions.createProfileFullResult(createRequest).profileId
        employeeActions.deleteProfile(createdProfileId)

        val storeViolationRequest = employeePreconditions
            .fillStoreDarkstoreUserViolationRequest(
                violationComment = StringAndPhoneNumberGenerator.generateRandomString(10),
                violationCode = ViolationCode.V001
            )

        val createdViolationErrorMessage = employeeActions.addViolationWithError(
            createdProfileId,
            Constants.searchContactsDarkstore,
            DarkstoreUserRole.PICKER,
            storeViolationRequest
        ).message

        employeeAssertion.checkErrorMessage(createdViolationErrorMessage, "Darkstore user was not found")

    }

    @Test
    @DisplayName("Create violation if internship in planned status exists")
    fun createViolationWithInternshipPlanned() {
        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
                email = null,
                mobile = Constants.mobile1
            )
        employeePreconditions.setCreateInternshipRequest(
            createRequest.darkstoreId!!,
            plannedDate = Instant.now().plusSeconds(60).truncatedTo(ChronoUnit.SECONDS),
            issuerProfileId = createRequest.issuerProfileId
        )
        val storeViolationRequest = employeePreconditions
            .fillStoreDarkstoreUserViolationRequest(
                violationComment = StringAndPhoneNumberGenerator.generateRandomString(10),
                violationCode = ViolationCode.V001
            )

        val createdProfileId = employeeActions.createProfileFullResult(createRequest).profileId
        employeeActions.createInternship(
            createdProfileId,
            DarkstoreUserRole.DELIVERYMAN,
            employeePreconditions.createInternshipRequest()
        )

        val createdViolationErrorMessage = employeeActions.addViolationWithError(
            createdProfileId,
            Constants.darkstoreId,
            DarkstoreUserRole.DELIVERYMAN,
            storeViolationRequest
        ).message

        employeeAssertion.checkErrorMessage(
            createdViolationErrorMessage,
            "Internship status rejects the creation of a violation for the darkstore user"
        )
    }

    @Test
    @DisplayName("Create violation if internship in rejected status exists")
    fun createViolationWithInternshipRejected() {

        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
                email = null,
                mobile = Constants.mobile1
            )
        val storeViolationRequest = employeePreconditions
            .fillStoreDarkstoreUserViolationRequest(
                violationComment = StringAndPhoneNumberGenerator.generateRandomString(10),
                violationCode = ViolationCode.V001
            )
        employeePreconditions
            .setCreateInternshipRequest(
                createRequest.darkstoreId!!,
                plannedDate = Instant.now().plusSeconds(8).truncatedTo(ChronoUnit.SECONDS),
                issuerProfileId = createRequest.issuerProfileId
            )
            .setRejectInternshipRequest((ApiEnum(RejectionCode.R001)), issuerProfileId = createRequest.issuerProfileId)


        val createdProfileId = employeeActions.createProfileFullResult(createRequest).profileId
        employeeActions
            .createInternship(
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
        val createdViolationErrorMessage = employeeActions.addViolationWithError(
            createdProfileId,
            Constants.darkstoreId,
            DarkstoreUserRole.DELIVERYMAN,
            storeViolationRequest
        ).message

        employeeAssertion.checkErrorMessage(
            createdViolationErrorMessage,
            "Internship status rejects the creation of a violation for the darkstore user"
        )
    }

    @Test
    @DisplayName("Create violation if internship in canceled status exists")
    fun createViolationWithInternshipCanceled() {
        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
                email = null,
                mobile = Constants.mobile1
            )
        val storeViolationRequest = employeePreconditions
            .fillStoreDarkstoreUserViolationRequest(
                violationComment = StringAndPhoneNumberGenerator.generateRandomString(10),
                violationCode = ViolationCode.V001
            )
        employeePreconditions
            .setCreateInternshipRequest(
                createRequest.darkstoreId!!,
                plannedDate = Instant.now().plusSeconds(8).truncatedTo(ChronoUnit.SECONDS),
                issuerProfileId = createRequest.issuerProfileId
            )
            .setCancelInternshipRequest(issuerProfileId = createRequest.issuerProfileId)


        val createdProfileId = employeeActions.createProfileFullResult(createRequest).profileId
        employeeActions
            .createInternship(
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
        val createdViolationErrorMessage = employeeActions.addViolationWithError(
            createdProfileId,
            Constants.darkstoreId,
            DarkstoreUserRole.DELIVERYMAN,
            storeViolationRequest
        ).message

        employeeAssertion.checkErrorMessage(
            createdViolationErrorMessage,
            "Internship status rejects the creation of a violation for the darkstore user"
        )

    }

    @Test
    @DisplayName("Create violation if internship in failed status exists")
    fun createViolationWithInternshipFailed() {

        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
                email = null,
                mobile = Constants.mobile1
            )
        val storeViolationRequest = employeePreconditions
            .fillStoreDarkstoreUserViolationRequest(
                violationComment = StringAndPhoneNumberGenerator.generateRandomString(10),
                violationCode = ViolationCode.V001
            )
        employeePreconditions
            .setCreateInternshipRequest(
                createRequest.darkstoreId!!,
                plannedDate = Instant.now().plusSeconds(8).truncatedTo(ChronoUnit.SECONDS),
                issuerProfileId = createRequest.issuerProfileId
            )
            .setCloseInternshipRequest(
                failureCode = (ApiEnum(FailureCode.F006)),
                status = (ApiEnum(InternshipStatus.FAILED)),
                issuerProfileId = createRequest.issuerProfileId
            )

        val createdProfileId = employeeActions.createProfileFullResult(createRequest).profileId
        employeeActions
            .createInternship(
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


        val createdViolationId = employeeActions.addViolation(
            createdProfileId,
            createRequest.darkstoreId!!,
            DarkstoreUserRole.DELIVERYMAN,
            storeViolationRequest
        ).violationId
        val violationTime = Instant.now()

        val violationFromApi =
            employeeActions.getViolation(createdProfileId, DarkstoreUserRole.DELIVERYMAN, createdViolationId)
        val violationType = violationDictionary.filter { it.code == ApiEnum(ViolationCode.V001) }.first()

        employeeAssertion
            .checkViolationsAreEquals(
                violationFromApi,
                storeViolationRequest,
                createRequest.darkstoreId!!,
                violationType, violationTime
            )
    }

    @Test
    @DisplayName("Create violation if internship done done status exists")
    fun createViolationWithInternshipDone() {

        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
                email = null,
                mobile = Constants.mobile1
            )
        val storeViolationRequest = employeePreconditions
            .fillStoreDarkstoreUserViolationRequest(
                violationComment = StringAndPhoneNumberGenerator.generateRandomString(10),
                violationCode = ViolationCode.V001
            )
        employeePreconditions
            .setCreateInternshipRequest(
                createRequest.darkstoreId!!,
                plannedDate = Instant.now().plusSeconds(8).truncatedTo(ChronoUnit.SECONDS),
                issuerProfileId = createRequest.issuerProfileId
            )
            .setCloseInternshipRequest(
                status = (ApiEnum(InternshipStatus.DONE)),
                issuerProfileId = createRequest.issuerProfileId
            )

        val createdProfileId = employeeActions.createProfileFullResult(createRequest).profileId
        employeeActions
            .createInternship(
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

        val createdViolationId = employeeActions.addViolation(
            createdProfileId,
            createRequest.darkstoreId!!,
            DarkstoreUserRole.DELIVERYMAN,
            storeViolationRequest
        ).violationId
        val violationTime = Instant.now()

        val violationFromApi =
            employeeActions.getViolation(createdProfileId, DarkstoreUserRole.DELIVERYMAN, createdViolationId)
        val violationType = violationDictionary.filter { it.code == ApiEnum(ViolationCode.V001) }.first()

        employeeAssertion
            .checkViolationsAreEquals(
                violationFromApi,
                storeViolationRequest,
                createRequest.darkstoreId!!,
                violationType, violationTime
            )

    }


    @Test
    @DisplayName("Check violations dictionary ")
    fun checkViolationsDictionary() {
        employeeAssertion.checkViolationsDictionary(violationDictionary)
    }

    @Test
    @DisplayName("Create violations all actual types")
    fun createViolationsAllTypes() {
        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
                email = null,
                mobile = Constants.mobile1
            )

        val createdProfileId = employeeActions.createProfileFullResult(createRequest).profileId

        val storeViolationRequests = employeePreconditions
            .fillStoreDarkstoreUserViolationRequests(
                violationComment = StringAndPhoneNumberGenerator.generateRandomString(10),
                violationCode = listOf(
                    ViolationCode.V010,
                    ViolationCode.V011,
                    ViolationCode.V012,
                    ViolationCode.V013,
                    ViolationCode.V014,
                    ViolationCode.V015,
                    ViolationCode.V016,
                    ViolationCode.V017
                )
            )

        val createdViolationIds = employeeActions.addViolations(
            createdProfileId,
            createRequest.darkstoreId!!,
            DarkstoreUserRole.DELIVERYMAN,
            storeViolationRequests
        )

        val violationsFromApi =
            employeeActions.getViolations(createdProfileId, DarkstoreUserRole.DELIVERYMAN)


        employeeAssertion
            .checkListOfViolationsFromApi(
                violationsFromApi,
                storeViolationRequests,
                createRequest.darkstoreId!!,
                violationDictionary
            )
    }

}