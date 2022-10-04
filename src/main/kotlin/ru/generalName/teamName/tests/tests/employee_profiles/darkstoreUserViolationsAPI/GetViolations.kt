package ru.samokat.mysamokat.tests.tests.employee_profiles.darkstoreUserViolationsAPI

import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.samokat.employeeprofiles.api.common.domain.Vehicle
import ru.samokat.employeeprofiles.api.darkstoreusers.domain.DarkstoreUserRole
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

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Tag("empro")
class GetViolations {

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
    @DisplayName("Get violation - profile disabled")
    fun getViolationDisableProfile() {
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

        employeeActions.deleteProfile(createdProfileId)

        val violationFromApi =
            employeeActions.getViolation(createdProfileId, DarkstoreUserRole.DELIVERYMAN, createdViolationId)
        val violationType = violationDictionary.filter { it.code == ApiEnum(ViolationCode.V001) }.first()

        employeeAssertion
            .checkViolationsAreEquals(
                violationFromApi,
                storeViolationRequest,
                createRequest.darkstoreId!!,
                violationType,
                violationTime
            )

    }

    @Test
    @DisplayName("Get violation - profile role not exist")
    fun getViolationProfileRoleNotExist(){
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

        val violationFromApi =
            employeeActions.getViolations(createdProfileId, DarkstoreUserRole.PICKER)

        employeeAssertion.checkListCount(violationFromApi.count(), 0)
    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Get violation - several violations on different darkstore")
    fun getViolationSeveralViolationOnDifferentDarkstore(){
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
                violationComment = StringAndPhoneNumberGenerator.generateRandomString(10),
                violationCode = ViolationCode.V001
            )

        val createdViolationId1 = employeeActions.addViolation(
            createdProfileId,
            createRequest.darkstoreId!!,
            DarkstoreUserRole.DELIVERYMAN,
            storeViolationRequest1
        ).violationId

        val updateProfileRequest = employeePreconditions
            .setUpdateProfileRequest(
                createRequest,
                darkstoreId = Constants.updatedDarkstoreId
            )

        employeeActions.updateProfile(createdProfileId, employeePreconditions.updateProfileRequest())

        val storeViolationRequest2 = employeePreconditions
            .fillStoreDarkstoreUserViolationRequest(
                violationComment = StringAndPhoneNumberGenerator.generateRandomString(10),
                violationCode = ViolationCode.V007
            )

        val createdViolationId2 = employeeActions.addViolation(
            createdProfileId,
            Constants.updatedDarkstoreId,
            DarkstoreUserRole.DELIVERYMAN,
            storeViolationRequest2
        ).violationId

        val violationTime = Instant.now()

        val violationsFromApi =
            employeeActions.getViolations(createdProfileId, DarkstoreUserRole.DELIVERYMAN)
        val violationType1 = violationDictionary.filter { it.code == ApiEnum(ViolationCode.V001) }.first()
        val violationType2 = violationDictionary.filter { it.code == ApiEnum(ViolationCode.V007) }.first()

        employeeAssertion
            .checkViolationsAreEquals(
                violationsFromApi[0],
                storeViolationRequest2,
                Constants.updatedDarkstoreId,
                violationType2,
                violationTime
            )
            .checkViolationsAreEquals(
                violationsFromApi[1],
                storeViolationRequest1,
                createRequest.darkstoreId!!,
                violationType1,
                violationTime
            )
    }

    @Test
    @DisplayName("Get violation - violation for other role")
    fun getViolationForOtherRole(){
        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN),ApiEnum(EmployeeRole.PICKER)),
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

        val violationFromApi1 =
            employeeActions.getViolations(createdProfileId, DarkstoreUserRole.DELIVERYMAN)
        val violationFromApi2 =
            employeeActions.getViolations(createdProfileId, DarkstoreUserRole.PICKER)

        employeeAssertion
            .checkViolationIsPresentInList(violationFromApi1, createdViolationId1)
            .checkViolationIsPresentInList(violationFromApi2, createdViolationId2)
            .checkViolationIsNotPresentInList(violationFromApi2, createdViolationId1)
            .checkViolationIsNotPresentInList(violationFromApi1, createdViolationId2)
    }

}