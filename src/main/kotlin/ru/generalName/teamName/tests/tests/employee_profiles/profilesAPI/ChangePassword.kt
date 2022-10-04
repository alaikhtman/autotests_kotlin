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
import ru.samokat.mysamokat.tests.dataproviders.preconditions.EmployeePreconditions
import ru.samokat.mysamokat.tests.helpers.actions.EmployeeActions
import java.util.*


@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Tag("empro")
class ChangePassword {


    private lateinit var employeeAssertion: EmployeeAssertion

    @Autowired
    lateinit var employeeActions: EmployeeActions

    private lateinit var employeePreconditions: EmployeePreconditions

    @BeforeEach
    fun setUp() {
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
    @Tags(Tag("smoke"), Tag("kafka_consume"))
    @DisplayName("Change profile password")
    fun changeProfilePassword() {

        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
                email = null
            )

        val createdProfile = employeeActions.createProfileFullResult(createRequest)
        val newPassword = employeeActions.changeProfilePassword(createdProfile.profileId)
        val profileFromDB = employeeActions.getProfileFromDB(createdProfile.profileId)

        employeeAssertion
            .checkPasswordHashWasUpdated(newPassword, createdProfile.generatedPassword.toString())
            .checkProfileVersion(1, profileFromDB)
            .checkEmployeeProfileChangePasswordExistsKafka(
                employeeActions.getMessageFromKafkaPasswordChanged(
                    createdProfile.profileId
                )
            )
    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Change profile password (profile disabled)")
    fun changePasswordDisableProfile() {
        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
                email = null
            )

        val createdProfile = employeeActions.createProfileFullResult(createRequest)
        employeeActions.deleteProfile(createdProfile.profileId)
        val errorMessage = employeeActions.changeProfilePasswordError(createdProfile.profileId).message

        employeeAssertion.checkErrorMessage(errorMessage, "Found profile is disabled")
    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Change profile password (profile not exists)")
    fun changePasswordWithInvalidProfileId() {

        val errorMessage = employeeActions.changeProfilePasswordError(UUID.randomUUID()).message
        employeeAssertion.checkErrorMessage(errorMessage, "Profile was not found")

    }
}
