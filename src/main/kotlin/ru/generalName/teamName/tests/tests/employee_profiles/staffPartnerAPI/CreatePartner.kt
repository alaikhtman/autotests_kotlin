package ru.samokat.mysamokat.tests.tests.employee_profiles.staffPartnerAPI

import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.samokat.employeeprofiles.api.common.domain.StaffPartnerType
import ru.samokat.my.rest.api.enum.ApiEnum
import ru.samokat.mysamokat.tests.checkers.EmployeeAssertion
import ru.samokat.mysamokat.tests.dataproviders.StringAndPhoneNumberGenerator
import ru.samokat.mysamokat.tests.dataproviders.preconditions.EmployeePreconditions
import ru.samokat.mysamokat.tests.helpers.actions.EmployeeActions
import ru.samokat.mysamokat.tests.helpers.controllers.database.EmployeeProfilesDatabaseController

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Tag("empro")
class CreatePartner {
    private lateinit var employeeAssertion: EmployeeAssertion

    @Autowired
    private lateinit var employeeActions: EmployeeActions

    private lateinit var employeePreconditions: EmployeePreconditions

    @Autowired
    private lateinit var databaseController: EmployeeProfilesDatabaseController

    @BeforeEach
    fun before() {
        employeePreconditions = EmployeePreconditions()
        employeeAssertion = EmployeeAssertion()
        employeeActions.deletePartner("TestPartner")
    }

    @AfterEach
    fun release() {
        employeeAssertion.assertAll()
        employeeActions.deletePartner("TestPartner")
    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Create outstaff partner")
    fun createOutstaffPartner() {

        val createPartnerRequest = employeePreconditions.fillCreatePartnerRequest(
            "TestPartner",
            "TestPartnerShortTitle",
            ApiEnum(StaffPartnerType.OUT_STAFF)
        )
        val getPartnersRequest = employeePreconditions.fillGetStaffPartnersRequest()

        val partnerId = employeeActions.createStaffPartner(createPartnerRequest).partnerId

        val partnerFromDB = employeeActions.getPartnerFromDB(partnerId)
        val partnerFromApi = employeeActions.getStaffPartnerById(getPartnersRequest, partnerId)

        employeeAssertion
            .checkPartnerFromApi(partnerFromApi, createPartnerRequest)
            .checkPartnerFromDB(partnerFromDB, createPartnerRequest)
    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Create outsource partner")
    fun createOutsourcePartner() {

        val createPartnerRequest = employeePreconditions.fillCreatePartnerRequest(
            "TestPartner",
            "TestPartnerShortTitle",
            ApiEnum(StaffPartnerType.OUT_SOURCE)
        )
        val getPartnersRequest = employeePreconditions.fillGetStaffPartnersRequest()

        val partnerId = employeeActions.createStaffPartner(createPartnerRequest).partnerId

        val partnerFromDB = employeeActions.getPartnerFromDB(partnerId)
        val partnerFromApi = employeeActions.getStaffPartnerById(getPartnersRequest, partnerId)

        employeeAssertion
            .checkPartnerFromApi(partnerFromApi, createPartnerRequest)
            .checkPartnerFromDB(partnerFromDB, createPartnerRequest)
    }

    @Test
    @DisplayName("Create partner with title > 128")
    fun createPartnerWithLongTitle() {
        val createPartnerRequest = employeePreconditions.fillCreatePartnerRequest(
            StringAndPhoneNumberGenerator.generateRandomString(129),
            "TestPartnerShortTitle",
            ApiEnum(StaffPartnerType.OUT_SOURCE)
        )
        val getPartnersRequest = employeePreconditions.fillGetStaffPartnersRequest()

        val errorMessage = employeeActions.createStaffPartnerWithError(createPartnerRequest).message

        employeeAssertion
            .checkErrorMessage(errorMessage, "Title should contain 1 to 128 characters")
    }

    @Test
    @DisplayName("Create partner with shortTitle > 128")
    fun createPartnerWithLongShortTitle() {
        val createPartnerRequest = employeePreconditions.fillCreatePartnerRequest(
            "TestPartnerTitle",
            StringAndPhoneNumberGenerator.generateRandomString(129),
            ApiEnum(StaffPartnerType.OUT_SOURCE)
        )
        val getPartnersRequest = employeePreconditions.fillGetStaffPartnersRequest()

        val errorMessage = employeeActions.createStaffPartnerWithError(createPartnerRequest).message

        employeeAssertion
            .checkErrorMessage(errorMessage, "Short Title should contain 1 to 128 characters")
    }

    @Test
    @DisplayName("Create partner with exist title")
    fun createPartnerWithExistTitle() {
        val createPartnerRequest1 = employeePreconditions.fillCreatePartnerRequest(
            "TestPartnerTitle",
            "TestPartnerShortTitle1",
            ApiEnum(StaffPartnerType.OUT_SOURCE)
        )
        val createPartnerRequest2 = employeePreconditions.fillCreatePartnerRequest(
            "TestPartnerTitle",
            "TestPartnerShortTitle1",
            ApiEnum(StaffPartnerType.OUT_STAFF)
        )
        val getPartnersRequest = employeePreconditions.fillGetStaffPartnersRequest()

        employeeActions.createStaffPartner(createPartnerRequest1)
        val errorMessage = employeeActions.createStaffPartnerWithError(createPartnerRequest2).message

        employeeAssertion
            .checkErrorMessage(errorMessage, "Partner is already exists")
    }

    @Test
    @DisplayName("Create partner with exist short title")
    fun createPartnerWithExistShortTitle() {
        val createPartnerRequest1 = employeePreconditions.fillCreatePartnerRequest(
            "TestPartnerTitle1",
            "TestPartnerShortTitle",
            ApiEnum(StaffPartnerType.OUT_SOURCE)
        )
        val createPartnerRequest2 = employeePreconditions.fillCreatePartnerRequest(
            "TestPartnerTitle2",
            "TestPartnerShortTitle",
            ApiEnum(StaffPartnerType.OUT_STAFF)
        )
        val getPartnersRequest = employeePreconditions.fillGetStaffPartnersRequest()

        employeeActions.createStaffPartner(createPartnerRequest1)
        val partnerId = employeeActions.createStaffPartner(createPartnerRequest2).partnerId

        val partnerFromDB = employeeActions.getPartnerFromDB(partnerId)
        val partnerFromApi = employeeActions.getStaffPartnerById(getPartnersRequest, partnerId)

        employeeAssertion
            .checkPartnerFromApi(partnerFromApi, createPartnerRequest2)
            .checkPartnerFromDB(partnerFromDB, createPartnerRequest2)
    }

}