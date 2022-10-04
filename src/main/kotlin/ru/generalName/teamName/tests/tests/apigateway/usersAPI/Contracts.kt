package ru.generalName.teamName.tests.tests.apigateway.usersAPI

import org.apache.http.HttpStatus
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.util.*


@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Tags(Tag("staff_apigateway"))
class Contracts {

    private lateinit var employeePreconditions: EmployeePreconditions

    private lateinit var staffApiGWPreconditions: StaffApiGWPreconditions

    private lateinit var apiGWAssertions: ApiGWAssertions
    private lateinit var commonAssertions: CommonAssertion

    @Autowired
    private lateinit var apiGWActions: ApiGWActions

    @Autowired
    private lateinit var profileActions: ProfileActions

    @Autowired
    private lateinit var commonPreconditions: CommonPreconditions

    @BeforeEach
    fun before() {
        staffApiGWPreconditions = StaffApiGWPreconditions()
        employeePreconditions = EmployeePreconditions()
        apiGWAssertions = ApiGWAssertions()
        commonAssertions = CommonAssertion()
        profileActions.deleteProfile(Constants.mobile1)

    }

    @AfterEach
    fun release() {
        apiGWAssertions.assertAll()
        commonAssertions.assertAll()
        profileActions.deleteProfile(Constants.mobile1)


    }

    @Test
    @Tags(Tag("smoke"), Tag("integration"))
    @DisplayName("Get contracts for 1 user by darkstore_admin")
    fun getContractOneUser() {

        val tokens = commonPreconditions
            .createAndAuthorizeStaffUser(
                roles = listO(ApiEnum(EmployeeRole.DARKSTORE_ADMIN)),
                vehicle = null,
                staffPartnerId = null
            )

        val createDeliveryman = commonPreconditions.createProfileDeliveryman(
            mobile = Constants.mobile2,
            vehicle = Vehicle(ApiEnum(EmployeeVehicleType.BICYCLE)),
            accountingProfileId = Constants.accountingProfileIdForTimesheet1
        ).profileId

        staffApiGWPreconditions.fillContractsSearchRequest(userIds = mutableListOf(createDeliveryman))

        val contracts = apiGWActions.getContracts(tokens!!.accessToken, staffApiGWPreconditions.contractRequest())
        val contractFromDB =
            mutableListOf(profileActions.getSeveralContractFromDB(Constants.accountingProfileIdForTimesheet1))

        apiGWAssertions.checkContractsResponse(
            contracts,
            mutableListOf(createDeliveryman),
            contractFromDB
        )


    }



    @Test
    @DisplayName("Get contracts by supervisor is impossible")
    fun getContractBySupervisor() {
        val tokens = commonPreconditions
            .createAndAuthorizeStaffUser(
                roles = listOf(ApiEnum(EmployeeRole.SUPERVISOR)),
                vehicle = null,
                staffPartnerId = null,
                darkstoreId = null,
                cityId = null
            )


        val createDeliveryman = commonPreconditions.createProfileDeliveryman(
            mobile = Constants.mobile2,
            darkstoreId = Constants.darkstoreIdWithNotMoscowTimezone,
            vehicle = Vehicle(ApiEnum(EmployeeVehicleType.BICYCLE)),
            accountingProfileId = Constants.accountingProfileIdForTimesheet1
        ).profileId

        staffApiGWPreconditions.fillContractsSearchRequest(userIds = mutableListOf(createDeliveryman))

        apiGWActions.getContractsWithError(
            tokens!!.accessToken,
            staffApiGWPreconditions.contractRequest(),
            HttpStatus.SC_FORBIDDEN
        )
    }


    @Test
    @DisplayName("Get contracts of not outsource employee")
    fun getContractForNotDeliverymanUser() {

        val tokens = commonPreconditions
            .createAndAuthorizeStaffUser(
                roles = listOf(ApiEnum(EmployeeRole.DARKSTORE_ADMIN)),
                vehicle = null,
                staffPartnerId = null
            )

        val createGoodsManger = commonPreconditions.createProfileGoodsManager(
            mobile = Constants.mobile2,
            darkstoreId = Constants.darkstoreIdWithNotMoscowTimezone
        ).profileId

        staffApiGWPreconditions.fillContractsSearchRequest(userIds = mutableListOf(createGoodsManger))

        val contracts = apiGWActions.getContracts(tokens!!.accessToken, staffApiGWPreconditions.contractRequest())

        apiGWAssertions.checkEmptyContractResponse(
            contracts,
            mutableListOf(createGoodsManger)
        )

    }


}