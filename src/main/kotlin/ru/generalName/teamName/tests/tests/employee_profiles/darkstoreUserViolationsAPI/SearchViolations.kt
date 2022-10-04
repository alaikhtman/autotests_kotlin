package ru.samokat.mysamokat.tests.tests.employee_profiles.darkstoreUserViolationsAPI

import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.samokat.employeeprofiles.api.common.domain.Vehicle
import ru.samokat.employeeprofiles.api.common.paging.PagingFilter
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

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Tag("empro")
class SearchViolations {

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
        employeeActions.deleteProfile(Constants.mobile2)
    }

    @Test
    @DisplayName("Search violation - search by one darkstore")
    fun searchViolationByOneDarkstore() {

        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN), ApiEnum(EmployeeRole.PICKER)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
                email = null,
                mobile = Constants.mobile1,
                darkstoreId = Constants.searchProfileDarkstore
            )

        val createdProfileId = employeeActions.createProfileFullResult(createRequest).profileId

        val storeViolationRequest1 = employeePreconditions
            .fillStoreDarkstoreUserViolationRequest(
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

        val searchRequest = employeePreconditions
            .fillSearchViolationsRequest(
                darkstoreIds = listOf(Constants.searchProfileDarkstore),
                darkstoreUserRoles = listOf(ApiEnum(DarkstoreUserRole.DELIVERYMAN), ApiEnum(DarkstoreUserRole.PICKER)),
                pagingFilter = PagingFilter(100, null)
            )

        val violationType1 = violationDictionary.first { it.code == ApiEnum(ViolationCode.V001) }
        val violationType2 = violationDictionary.first { it.code == ApiEnum(ViolationCode.V007) }

        val searchResults = employeeActions.searchViolations(searchRequest)


        employeeAssertion
            .checkViolationIsPresentInGlideList(searchResults, createdViolationId1)
            .checkViolationIsPresentInGlideList(searchResults, createdViolationId2)
            .checkViolationsFieldsInGlideList(
                searchResults,
                storeViolationRequest1,
                createRequest,
                createdViolationId1,
                createdProfileId,
                violationType1)
            .checkViolationsFieldsInGlideList(
                searchResults,
                storeViolationRequest2,
                createRequest,
                createdViolationId2,
                createdProfileId,
                violationType2)

        employeeActions.deleteViolationsFromDatabase(createdProfileId)

    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Search violation - search by deliverymen")
    fun searchViolationByDeliveryman() {

        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN), ApiEnum(EmployeeRole.PICKER)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
                email = null,
                mobile = Constants.mobile1,
                darkstoreId = Constants.searchProfileDarkstore
            )

        val createdProfileId = employeeActions.createProfileFullResult(createRequest).profileId

        val storeViolationRequest1 = employeePreconditions
            .fillStoreDarkstoreUserViolationRequest(
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

        val searchRequest = employeePreconditions
            .fillSearchViolationsRequest(
                darkstoreIds = listOf(Constants.searchProfileDarkstore),
                darkstoreUserRoles = listOf(ApiEnum(DarkstoreUserRole.DELIVERYMAN)),
            )

        val searchResults = employeeActions.searchViolations(searchRequest)


        employeeAssertion
            .checkViolationIsPresentInGlideList(searchResults, createdViolationId1)
            .checkViolationIsNotPresentInGlideList(searchResults, createdViolationId2)
            .checkElementsInGlideListSatisfyConditions(searchResults, "role", ApiEnum(EmployeeRole.DELIVERYMAN))
        employeeActions.deleteViolationsFromDatabase(createdProfileId)
    }

    @Test
    @DisplayName("Search violation - search by pickers")
    fun searchViolationByPicker() {
        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN), ApiEnum(EmployeeRole.PICKER)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
                email = null,
                mobile = Constants.mobile1,
                darkstoreId = Constants.searchProfileDarkstore
            )

        val createdProfileId = employeeActions.createProfileFullResult(createRequest).profileId

        val storeViolationRequest1 = employeePreconditions
            .fillStoreDarkstoreUserViolationRequest(
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

        val searchRequest = employeePreconditions
            .fillSearchViolationsRequest(
                darkstoreIds = listOf(Constants.searchProfileDarkstore),
                darkstoreUserRoles = listOf(ApiEnum(DarkstoreUserRole.PICKER)),
            )

        val searchResults = employeeActions.searchViolations(searchRequest)


        employeeAssertion
            .checkViolationIsNotPresentInGlideList(searchResults, createdViolationId1)
            .checkViolationIsPresentInGlideList(searchResults, createdViolationId2)
            .checkElementsInGlideListSatisfyConditions(searchResults, "role", ApiEnum(EmployeeRole.PICKER))
        employeeActions.deleteViolationsFromDatabase(createdProfileId)
    }

    @Test
    @DisplayName("Search violation - search by several darkstores")
    fun searchViolationBySeveralDarkstores() {
        val createRequest1 = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
                email = null,
                mobile = Constants.mobile1,
                darkstoreId = Constants.searchProfileDarkstore
            )
        val createRequest2 = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.PICKER)),
                email = null,
                mobile = Constants.mobile2,
                darkstoreId = Constants.searchContactsDarkstore,
                vehicle = null
            )

        val createdProfileId1 = employeeActions.createProfileFullResult(createRequest1).profileId
        val createdProfileId2 = employeeActions.createProfileFullResult(createRequest2).profileId

        val storeViolationRequest = employeePreconditions
            .fillStoreDarkstoreUserViolationRequest(
                violationCode = ViolationCode.V001
            )

        val createdViolationId1 = employeeActions.addViolation(
            createdProfileId1,
            createRequest1.darkstoreId!!,
            DarkstoreUserRole.DELIVERYMAN,
            storeViolationRequest
        ).violationId
        val createdViolationId2 = employeeActions.addViolation(
            createdProfileId2,
            createRequest2.darkstoreId!!,
            DarkstoreUserRole.PICKER,
            storeViolationRequest
        ).violationId

        val searchRequest = employeePreconditions
            .fillSearchViolationsRequest(
                darkstoreIds = listOf(Constants.searchProfileDarkstore, Constants.searchContactsDarkstore),
                darkstoreUserRoles = listOf(ApiEnum(DarkstoreUserRole.DELIVERYMAN), ApiEnum(DarkstoreUserRole.PICKER)),
            )

        val searchResults = employeeActions.searchViolations(searchRequest)

        employeeAssertion
            .checkViolationIsPresentInGlideList(searchResults, createdViolationId1)
            .checkViolationIsPresentInGlideList(searchResults, createdViolationId2)
        employeeActions.deleteViolationsFromDatabase(createdProfileId1)
        employeeActions.deleteViolationsFromDatabase(createdProfileId2)
    }

    // todo
    @Test
    @DisplayName("Search violation - pagination")
    fun searchViolationPagination() {}

}