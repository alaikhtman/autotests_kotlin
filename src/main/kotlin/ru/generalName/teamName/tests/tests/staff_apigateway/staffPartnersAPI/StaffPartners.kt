package ru.samokat.mysamokat.tests.tests.staff_apigateway.staffPartnersAPI

import org.apache.http.HttpStatus
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.samokat.my.domain.employee.EmployeeRole
import ru.samokat.my.rest.api.enum.ApiEnum
import ru.samokat.mysamokat.tests.checkers.CommonAssertion
import ru.samokat.mysamokat.tests.checkers.StaffApiGWAssertions
import ru.samokat.mysamokat.tests.dataproviders.Constants
import ru.samokat.mysamokat.tests.dataproviders.preconditions.CommonPreconditions
import ru.samokat.mysamokat.tests.dataproviders.preconditions.StaffApiGWPreconditions
import ru.samokat.mysamokat.tests.helpers.actions.EmployeeActions
import ru.samokat.mysamokat.tests.helpers.actions.StaffApiGWActions


@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Tags(Tag("staff_apigateway"))
class StaffPartners {
    private lateinit var staffApiGWPreconditions: StaffApiGWPreconditions

    private lateinit var staffApiGWAssertions: StaffApiGWAssertions
    private lateinit var commonAssertions: CommonAssertion

    @Autowired
    private lateinit var staffApiGWActions: StaffApiGWActions

    @Autowired
    private lateinit var employeeActions: EmployeeActions

    @Autowired
    private lateinit var commonPreconditions: CommonPreconditions

    @BeforeEach
    fun before() {
        staffApiGWPreconditions = StaffApiGWPreconditions()
        staffApiGWAssertions = StaffApiGWAssertions()
        commonAssertions = CommonAssertion()
        employeeActions.deleteProfile(Constants.mobile1)

    }

    @AfterEach
    fun release() {
        staffApiGWAssertions.assertAll()
        commonAssertions.assertAll()
        employeeActions.deleteProfile(Constants.mobile1)

    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Get staff partners by darkstore_admin")
    fun getStaffPartnerByDarkstoreAdmin() {
        val tokens = commonPreconditions
            .createAndAuthorizeStaffUser(
                roles = listOf(ApiEnum(EmployeeRole.DARKSTORE_ADMIN)),
                vehicle = null,
                staffPartnerId = null
            )

        val staffPartners = staffApiGWActions.getStaffPartners(tokens!!.accessToken)

        staffApiGWAssertions.checkStaffPartners(
            expectedStaffPartners = employeeActions.getAllStaffPartnerFromDB(), actualStaffPartners = staffPartners
        )

    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Get staff partners by goods_manager")
    fun getStaffPartnerByGoodsManager() {
        val tokens = commonPreconditions
            .createAndAuthorizeStaffUser(
                roles = listOf(ApiEnum(EmployeeRole.GOODS_MANAGER)),
                vehicle = null,
                staffPartnerId = null
            )

        val staffPartners = staffApiGWActions.getStaffPartners(tokens!!.accessToken)

        staffApiGWAssertions.checkStaffPartners(
            expectedStaffPartners = employeeActions.getAllStaffPartnerFromDB(), actualStaffPartners = staffPartners
        )

    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Get staff partner by coordinator is impossible")
    fun getStaffPartnerCoordinator() {
        val tokens = commonPreconditions
            .createAndAuthorizeStaffUser(
                roles = listOf(ApiEnum(EmployeeRole.COORDINATOR)),
                vehicle = null,
                staffPartnerId = null,
                darkstoreId = null,
                supervisedDarkstores = Constants.supervisedDarkstores,
                cityId = null
            )


        staffApiGWActions.getStaffPartnersWithError(tokens!!.accessToken, HttpStatus.SC_FORBIDDEN)

    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Get staff partner by supervisor is impossible")
    fun getStaffPartnerBySupervisor() {
        val tokens = commonPreconditions
            .createAndAuthorizeStaffUser(
                roles = listOf(ApiEnum(EmployeeRole.SUPERVISOR)),
                vehicle = null,
                staffPartnerId = null,
                darkstoreId = null,
                cityId = null
            )

        staffApiGWActions.getStaffPartnersWithError(tokens!!.accessToken, HttpStatus.SC_FORBIDDEN)


    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Get staff partner by disabled darkstore_admin is impossible")
    fun getStaffPartnerByDisabledDarkstoreAdmin() {
        val tokens = commonPreconditions
            .createAndAuthorizeStaffUser(
                roles = listOf(ApiEnum(EmployeeRole.DARKSTORE_ADMIN)),
                vehicle = null,
                staffPartnerId = null
            )

        employeeActions.deleteProfile(Constants.mobile1)

        staffApiGWActions.getStaffPartnersWithError(tokens!!.accessToken, HttpStatus.SC_FORBIDDEN)


    }


}