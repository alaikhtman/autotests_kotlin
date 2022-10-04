package ru.samokat.mysamokat.tests.tests.staff_cave_apigateway.staffPartnersAPI

import org.apache.http.HttpStatus
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.samokat.my.domain.employee.EmployeeRole
import ru.samokat.my.rest.api.enum.ApiEnum
import ru.samokat.mysamokat.tests.checkers.CommonAssertion
import ru.samokat.mysamokat.tests.checkers.StaffCaveApiGWAssertions
import ru.samokat.mysamokat.tests.dataproviders.Constants
import ru.samokat.mysamokat.tests.dataproviders.StringAndPhoneNumberGenerator
import ru.samokat.mysamokat.tests.dataproviders.apiModels.staffcave.staffpartners.StaffPartnerType
import ru.samokat.mysamokat.tests.dataproviders.preconditions.CommonPreconditions
import ru.samokat.mysamokat.tests.dataproviders.preconditions.EmployeePreconditions
import ru.samokat.mysamokat.tests.dataproviders.preconditions.StaffCaveApiGWPreconditions
import ru.samokat.mysamokat.tests.helpers.actions.EmployeeActions
import ru.samokat.mysamokat.tests.helpers.actions.StaffCaveApiGWActions

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Tags(Tag("staff-cave-apigateway"))
class CreateStaffPartner {
    @Autowired
    private lateinit var scActions: StaffCaveApiGWActions

    @Autowired
    private lateinit var employeeActions: EmployeeActions

    @Autowired
    private lateinit var commonPreconditions: CommonPreconditions

    private lateinit var commonAssertions: CommonAssertion

    private lateinit var scPreconditions: StaffCaveApiGWPreconditions

    private lateinit var employeePreconditions: EmployeePreconditions

    private lateinit var scAssertion: StaffCaveApiGWAssertions

    private lateinit var token: String


    @BeforeEach
    fun before() {
        scPreconditions = StaffCaveApiGWPreconditions()
        employeePreconditions = EmployeePreconditions()
        scAssertion = StaffCaveApiGWAssertions()
        commonAssertions = CommonAssertion()
        employeeActions.deleteProfile(Constants.mobile1)
        employeeActions.deletePartner("TestPartner")
        employeeActions.deletePartner("TestPartner1")
        employeeActions.deletePartner("TestPartner2")
        getAuthToken()
    }

    @AfterEach
    fun release() {
        scAssertion.assertAll()
        commonAssertions.assertAll()
        employeeActions.deleteProfile(Constants.mobile1)
        employeeActions.deletePartner("TestPartner")
        employeeActions.deletePartner("TestPartner1")
        employeeActions.deletePartner("TestPartner2")
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
    @DisplayName("Create out source partner test (tech-support)")
    fun createOutSourcePartnerTest(){

        val getPartnersRequest = employeePreconditions.fillGetStaffPartnersRequest()
        val createStaffPartnerRequest = scPreconditions.fillCreateStaffPartnerRequest(
            title = "TestPartner",
            shortTitle = StringAndPhoneNumberGenerator.generateRandomString(5),
            type = ApiEnum(StaffPartnerType.OUT_SOURCE)
        )

        scActions.createStaffPartner(token, createStaffPartnerRequest)

        val partners = employeeActions.getStaffPartners(getPartnersRequest)

        scAssertion.checkStaffPartnerInList(createStaffPartnerRequest, partners)
    }

    @Test
    @DisplayName("Create out source partner test (staff-manager)")
    fun createOutSourcePartnerStaffManagerTest(){

        getAuthToken(EmployeeRole.STAFF_MANAGER)
        val getPartnersRequest = employeePreconditions.fillGetStaffPartnersRequest()
        val createStaffPartnerRequest = scPreconditions.fillCreateStaffPartnerRequest(
            title = "TestPartner",
            shortTitle = StringAndPhoneNumberGenerator.generateRandomString(5),
            type = ApiEnum(StaffPartnerType.OUT_SOURCE)
        )

        scActions.createStaffPartner(token, createStaffPartnerRequest)

        val partners = employeeActions.getStaffPartners(getPartnersRequest)

        scAssertion.checkStaffPartnerInList(createStaffPartnerRequest, partners)
    }

    @Test
    @DisplayName("Create out staff partner test")
    fun createOutStaffPartnerTest(){

        val getPartnersRequest = employeePreconditions.fillGetStaffPartnersRequest()
        val createStaffPartnerRequest = scPreconditions.fillCreateStaffPartnerRequest(
            title = "TestPartner",
            shortTitle = StringAndPhoneNumberGenerator.generateRandomString(5),
            type = ApiEnum(StaffPartnerType.OUT_SOURCE)
        )
        scActions.createStaffPartner(token, createStaffPartnerRequest)

        val partners = employeeActions.getStaffPartners(getPartnersRequest)

        scAssertion.checkStaffPartnerInList(createStaffPartnerRequest, partners)
    }

    @Test
    @DisplayName("Create partner with existed title")
    fun createPartnerWithExistedTitleTest(){

        val createStaffPartnerRequest1 = scPreconditions.fillCreateStaffPartnerRequest(
            title = "TestPartner",
            shortTitle = "TestPartner1",
            type = ApiEnum(StaffPartnerType.OUT_SOURCE)
        )
        val createStaffPartnerRequest2 = scPreconditions.fillCreateStaffPartnerRequest(
            title = "TestPartner",
            shortTitle = "TestPartner2",
            type = ApiEnum(StaffPartnerType.OUT_SOURCE)
        )

        scActions.createStaffPartner(token, createStaffPartnerRequest1)

        val errors = scActions.createStaffPartnerWithError(token, createStaffPartnerRequest2, HttpStatus.SC_CONFLICT)

        commonAssertions.checkErrorMessage(errors!!.code.toString(), "AlreadyExists")
            .checkErrorMessage(errors.message, "Partner already exists")
    }

    @Test
    @DisplayName("Create partner with long title")
    fun createPartnerWithLongTitleTest(){

        val createStaffPartnerRequest = scPreconditions.fillCreateStaffPartnerRequest(
            title = StringAndPhoneNumberGenerator.generateRandomString(129),
            shortTitle = "TestPartner1",
            type = ApiEnum(StaffPartnerType.OUT_SOURCE)
        )

        val errors = scActions.createStaffPartnerWithError(token, createStaffPartnerRequest, HttpStatus.SC_BAD_REQUEST)

        commonAssertions.checkErrorMessage(errors!!.code.toString(), "Incorrect")
            .checkErrorMessage(errors!!.message.toString(), "Title should contain 1 to 128 characters")
            .checkErrorMessage(errors!!.parameter.toString(), "title")
    }

    @Test
    @DisplayName("Create partner with long shorttTitle")
    fun createPartnerWithLongShortTitleTest(){

        val createStaffPartnerRequest = scPreconditions.fillCreateStaffPartnerRequest(
            title = "TestPartner",
            shortTitle = StringAndPhoneNumberGenerator.generateRandomString(129),
            type = ApiEnum(StaffPartnerType.OUT_SOURCE)
        )

        val errors = scActions.createStaffPartnerWithError(token, createStaffPartnerRequest, HttpStatus.SC_BAD_REQUEST)

        commonAssertions.checkErrorMessage(errors!!.code.toString(), "Incorrect")
            .checkErrorMessage(errors!!.message.toString(), "Short Title should contain 1 to 128 characters")
            .checkErrorMessage(errors!!.parameter.toString(), "shortTitle")
    }

    @Test
    @DisplayName("Create partner with existed short title")
    fun createPartnerWithExistedShortTitleTest(){

        val getPartnersRequest = employeePreconditions.fillGetStaffPartnersRequest()
        val createStaffPartnerRequest1 = scPreconditions.fillCreateStaffPartnerRequest(
            title = "TestPartner1",
            shortTitle = "TestPartner",
            type = ApiEnum(StaffPartnerType.OUT_SOURCE)
        )
        val createStaffPartnerRequest2 = scPreconditions.fillCreateStaffPartnerRequest(
            title = "TestPartner2",
            shortTitle = "TestPartner",
            type = ApiEnum(StaffPartnerType.OUT_SOURCE)
        )

        scActions.createStaffPartner(token, createStaffPartnerRequest1)
        scActions.createStaffPartner(token, createStaffPartnerRequest2)

        val partners = employeeActions.getStaffPartners(getPartnersRequest)

        scAssertion.checkStaffPartnerInList(createStaffPartnerRequest1, partners)
        scAssertion.checkStaffPartnerInList(createStaffPartnerRequest2, partners)
    }

}