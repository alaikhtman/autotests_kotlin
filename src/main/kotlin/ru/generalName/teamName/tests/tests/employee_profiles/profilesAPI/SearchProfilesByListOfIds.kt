package ru.samokat.mysamokat.tests.tests.employee_profiles.profilesAPI

import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.samokat.employeeprofiles.api.common.domain.EmployeeProfileStatus
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
class SearchProfilesByListOfIds {

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
    @DisplayName("Get profiles by ID - several results")
    fun getSeveralProfilesByListOfIds() {

        val createRequest1 = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
                email = null,
                mobile = Constants.mobile1
            )
        val createRequest2 = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN),ApiEnum(EmployeeRole.PICKER)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
                email = null,
                mobile = Constants.mobile2
            )

        val createdProfileId1 = employeeActions.createProfileId(createRequest1)
        val createdProfileId2 = employeeActions.createProfileId(createRequest2)

        val searchBuilder = employeePreconditions.fillGetProfilesBuilder(mutableListOf(createdProfileId1, createdProfileId2))

        val searchResults = employeeActions.getProfilesByListOfIds(searchBuilder)

        employeeAssertion
            .checkListCount(searchResults.count(),2)
            .checkProfilePresentInList(searchResults, createdProfileId1)
            .checkProfilePresentInList(searchResults, createdProfileId2)
            .checkProfileFieldsInList(searchResults, createRequest1, createdProfileId1)
            .checkProfileFieldsInList(searchResults, createRequest2, createdProfileId2)
    }

    @Test
    @DisplayName("Get profiles by ID - more than 256 id")
    fun moreThan256Ids() {
        val searchBuilder = employeePreconditions
            .fillGetProfilesBuilder(
                profileIds = null,
                profilesCount = 257
            )
        var errorMessage = employeeActions.getProfilesByListOfIdsWithError(searchBuilder)
        employeeAssertion.checkErrorMessage(errorMessage, "Profile IDs list must contain from 1 to 256 items")
    }

    @Test
    @DisplayName("Get profiles by ID - by 0 id")
    fun emptyIdsList() {
        val searchBuilder = employeePreconditions
            .fillGetProfilesBuilder(
                profileIds = null,
                profilesCount = 0
            )
        val errorMessage = employeeActions.getProfilesByListOfIdsWithError(searchBuilder)
        employeeAssertion.checkErrorMessage(errorMessage, "Profile IDs list must contain from 1 to 256 items")

    }

    @Test
    @DisplayName("Get profiles by ID - user is blocked")
    fun userIsBlocked() {
        val createBuilder = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
                email = null
            )

        val createdProfileId = employeeActions.createProfileId(createBuilder)
        employeeActions.deleteProfile(createdProfileId)

        val searchBuilder = employeePreconditions
            .fillGetProfilesBuilder(
                profileIds = mutableListOf(createdProfileId)
            )

        val searchResults = employeeActions.getProfilesByListOfIds(searchBuilder)

        employeeAssertion
            .checkElementsSatisfyConditions(searchResults, "status", status = EmployeeProfileStatus.DISABLED)
    }

    @Test
    @DisplayName("Get profiles by ID - user does not exist")
    fun userNotExists() {
        val searchBuilder = employeePreconditions
            .fillGetProfilesBuilder(
                profileIds = mutableListOf(UUID.randomUUID())
            )
        val searchResults = employeeActions.getProfilesByListOfIds(searchBuilder)

        employeeAssertion
            .checkListCount(searchResults.count(), 0)
    }
}
