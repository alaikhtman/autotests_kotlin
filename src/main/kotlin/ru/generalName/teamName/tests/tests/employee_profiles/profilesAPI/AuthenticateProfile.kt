package ru.samokat.mysamokat.tests.tests.employee_profiles.profilesAPI

import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.samokat.employeeprofiles.api.common.domain.EmployeeVehicleType
import ru.samokat.employeeprofiles.api.common.domain.Vehicle
import ru.samokat.my.domain.employee.EmployeeRole
import ru.samokat.my.rest.api.enum.ApiEnum
import ru.samokat.mysamokat.tests.checkers.EmployeeAssertion
import ru.samokat.mysamokat.tests.dataproviders.Constants
import ru.samokat.mysamokat.tests.dataproviders.StringAndPhoneNumberGenerator
import ru.samokat.mysamokat.tests.dataproviders.helpers.database.employee_profiles_backend.Profile
import ru.samokat.mysamokat.tests.dataproviders.preconditions.EmployeePreconditions
import ru.samokat.mysamokat.tests.helpers.actions.EmployeeActions

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Tag("empro")
class AuthenticateProfile {

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
    @Tags(Tag("smoke"))
    @DisplayName("Authenticate Profile")
    fun authenticateProfile() {

        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
                email = null,
                mobile = Constants.mobile1
            )

        val createdProfile = employeeActions.createProfileFullResult(createRequest)
        val profileFromDB = employeeActions.getProfileFromDB(createdProfile.profileId)

        val authBuilder = employeePreconditions
            .fillAuthProfileBuilder(
                mobile = Constants.mobile1,
                password = createdProfile.generatedPassword!!
            )
        val authResult = employeeActions.authenticateProfile(authBuilder.build())

        employeeAssertion
            .checkProfilesAreEquals(authResult, createRequest)
            .checkTwoDatesAreEqual(authResult.createdAt, profileFromDB[Profile.createdAt])
            .checkTwoDatesAreEqual(authResult.updatedAt, profileFromDB[Profile.updatedAt])
    }

    @Test
    @DisplayName("Authenticate Profile: empty password")
    fun authenticateProfileWithEmptyPassword() {

        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
                email = null
            )
        employeeActions.createProfileFullResult(createRequest)

        val authBuilder = employeePreconditions
            .fillAuthProfileBuilder(
                mobile = Constants.mobile1,
                password = "".toCharArray()
            )
        val errorMessage = employeeActions.authenticateProfileWithError(authBuilder.build())

        employeeAssertion.checkErrorMessage(errorMessage, "Password must not be empty")

    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Authenticate Profile: incorrect credentials")
    fun authenticateProfileWithWrongPassword() {

        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
                email = null
            )
        employeeActions.createProfileFullResult(createRequest)

        val authBuilder = employeePreconditions
            .fillAuthProfileBuilder(
                mobile = Constants.mobile1,
                password = StringAndPhoneNumberGenerator.generateRandomString(6).toCharArray()
            )
        val errorMessage = employeeActions.authenticateProfileWithError(authBuilder.build())

        employeeAssertion.checkErrorMessage(errorMessage, "Correct credentials must be provided")
    }
}
