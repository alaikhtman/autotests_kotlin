package ru.samokat.mysamokat.tests.tests.staff_apigateway.usersAPI

import org.apache.http.HttpStatus
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.samokat.employeeprofiles.api.common.domain.EmployeeVehicleType
import ru.samokat.employeeprofiles.api.common.domain.Vehicle
import ru.samokat.my.domain.employee.EmployeeName
import ru.samokat.my.domain.employee.EmployeeRole
import ru.samokat.my.rest.api.enum.ApiEnum
import ru.samokat.mysamokat.tests.checkers.CommonAssertion
import ru.samokat.mysamokat.tests.checkers.StaffApiGWAssertions
import ru.samokat.mysamokat.tests.dataproviders.Constants
import ru.samokat.mysamokat.tests.dataproviders.StringAndPhoneNumberGenerator
import ru.samokat.mysamokat.tests.dataproviders.preconditions.CommonPreconditions
import ru.samokat.mysamokat.tests.dataproviders.preconditions.EmployeePreconditions
import ru.samokat.mysamokat.tests.dataproviders.preconditions.StaffApiGWPreconditions
import ru.samokat.mysamokat.tests.helpers.actions.EmployeeActions
import ru.samokat.mysamokat.tests.helpers.actions.StaffApiGWActions
import java.time.Instant
import java.util.*


@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Tags(Tag("staff_apigateway"))
class Contracts {

    private lateinit var employeePreconditions: EmployeePreconditions

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
        employeePreconditions = EmployeePreconditions()
        staffApiGWAssertions = StaffApiGWAssertions()
        commonAssertions = CommonAssertion()
        employeeActions.deleteProfile(Constants.mobile1)
        employeeActions.deleteProfile(Constants.mobile2)
        employeeActions.deleteProfile(Constants.mobile3)
        employeeActions.deleteProfile(Constants.mobile4)
        employeeActions.deleteProfile(Constants.mobile6)
    }

    @AfterEach
    fun release() {
        staffApiGWAssertions.assertAll()
        commonAssertions.assertAll()
        employeeActions.deleteProfile(Constants.mobile1)
        employeeActions.deleteProfile(Constants.mobile2)
        employeeActions.deleteProfile(Constants.mobile3)
        employeeActions.deleteProfile(Constants.mobile4)
        employeeActions.deleteProfile(Constants.mobile6)

    }

    @Test
    @Tags(Tag("smoke"), Tag("emproIntegration"))
    @DisplayName("Get contracts for 1 user by darkstore_admin")
    fun getContractOneUser() {

        val tokens = commonPreconditions
            .createAndAuthorizeStaffUser(
                roles = listOf(ApiEnum(EmployeeRole.DARKSTORE_ADMIN)),
                vehicle = null,
                staffPartnerId = null
            )

        val createDeliveryman = commonPreconditions.createProfileDeliveryman(
            mobile = Constants.mobile2,
            vehicle = Vehicle(ApiEnum(EmployeeVehicleType.BICYCLE)),
            accountingProfileId = Constants.accountingProfileIdForTimesheet1
        ).profileId

        staffApiGWPreconditions.fillContractsSearchRequest(userIds = mutableListOf(createDeliveryman))

        val contracts = staffApiGWActions.getContracts(tokens!!.accessToken, staffApiGWPreconditions.contractRequest())
        val contractFromDB =
            mutableListOf(employeeActions.getSeveralContractFromDB(Constants.accountingProfileIdForTimesheet1))

        staffApiGWAssertions.checkContractsResponse(
            contracts,
            mutableListOf(createDeliveryman),
            contractFromDB
        )


    }

    @Test
    @Tags(Tag("smoke"), Tag("emproIntegration"))
    @DisplayName("Get contracts for 1 foreign user")
    fun getContractForeignUser() {
        val tokens = commonPreconditions
            .createAndAuthorizeStaffUser(
                roles = listOf(ApiEnum(EmployeeRole.DARKSTORE_ADMIN)),
                vehicle = null,
                staffPartnerId = null
            )

        val createDeliveryman = commonPreconditions.createProfileDeliveryman(
            mobile = Constants.mobile2,
            darkstoreId = Constants.darkstoreIdWithNotMoscowTimezone,
            vehicle = Vehicle(ApiEnum(EmployeeVehicleType.BICYCLE)),
            accountingProfileId = Constants.accountingProfileIdForTimesheet1
        ).profileId
        staffApiGWPreconditions.fillContractsSearchRequest(userIds = mutableListOf(createDeliveryman))

        val contracts = staffApiGWActions.getContracts(tokens!!.accessToken, staffApiGWPreconditions.contractRequest())
        val contractFromDB =
            mutableListOf(employeeActions.getSeveralContractFromDB(Constants.accountingProfileIdForTimesheet1))

        staffApiGWAssertions.checkContractsResponse(
            contracts,
            mutableListOf(createDeliveryman),
            contractFromDB
        )
    }


    @Test
    @Tags(Tag("smoke"), Tag("emproIntegration"))
    @DisplayName("Get contracts for >1 user by goods_manager")
    fun getContractSeveralUsers() {

        val tokens = commonPreconditions
            .createAndAuthorizeStaffUser(
                roles = listOf(ApiEnum(EmployeeRole.GOODS_MANAGER)),
                vehicle = null,
                staffPartnerId = null
            )

        val createDeliverymenAndPickers = commonPreconditions.createListOfProfiles(
            listOfRoles = mutableListOf(
                listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                listOf(ApiEnum(EmployeeRole.PICKER)),
                listOf(ApiEnum(EmployeeRole.DELIVERYMAN), ApiEnum(EmployeeRole.PICKER))
            ),
            listOfVehicle = mutableListOf(
                Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
                null,
                Vehicle(ApiEnum(EmployeeVehicleType.BICYCLE))
            ),
            listOfStaffPartner = mutableListOf(
                Constants.defaultStaffPartnerId,
                Constants.defaultStaffPartnerId,
                Constants.defaultStaffPartnerId
            ),
            listOfDarkstore = mutableListOf(
                Constants.darkstoreId,
                Constants.darkstoreId,
                Constants.darkstoreId
            ),
            listOfMobile = mutableListOf(
                Constants.mobile2,
                Constants.mobile3,
                Constants.mobile4
            ),
            listOfName = mutableListOf(
                EmployeeName(
                    (StringAndPhoneNumberGenerator.generateRandomString(10)),
                    (StringAndPhoneNumberGenerator.generateRandomString(10)),
                    (StringAndPhoneNumberGenerator.generateRandomString(10))
                ),
                EmployeeName(
                    (StringAndPhoneNumberGenerator.generateRandomString(10)),
                    (StringAndPhoneNumberGenerator.generateRandomString(10)),
                    (StringAndPhoneNumberGenerator.generateRandomString(10))
                ),
                EmployeeName(
                    (StringAndPhoneNumberGenerator.generateRandomString(10)),
                    (StringAndPhoneNumberGenerator.generateRandomString(10)),
                    (StringAndPhoneNumberGenerator.generateRandomString(10))
                )
            ),
            listOfAccountingProfileIds = mutableListOf(
                Constants.accountingProfileIdForTimesheet1,
                Constants.accountingProfileIdForTimesheet2,
                Constants.accountingProfileIdForTimesheet3
            ),
            listOfRequisitionsId = mutableListOf(null, null, null),
            amount = 3
        )

        staffApiGWPreconditions.fillContractsSearchRequest(userIds = createDeliverymenAndPickers)

        val contracts = staffApiGWActions.getContracts(tokens!!.accessToken, staffApiGWPreconditions.contractRequest())
        val contractsFromDB = mutableListOf(
            employeeActions.getSeveralContractFromDB(Constants.accountingProfileIdForTimesheet1),
            employeeActions.getSeveralContractFromDB(Constants.accountingProfileIdForTimesheet2),
            employeeActions.getSeveralContractFromDB(Constants.accountingProfileIdForTimesheet3)
        )

        staffApiGWAssertions.checkContractsResponse(
            contracts,
            createDeliverymenAndPickers,
            contractsFromDB
        )
    }

    @Test
    @DisplayName("Get contracts for more than max users is impossible")
    fun getContractMaxUsers() {

        val tokens = commonPreconditions
            .createAndAuthorizeStaffUser(
                roles = listOf(ApiEnum(EmployeeRole.GOODS_MANAGER)),
                vehicle = null,
                staffPartnerId = null
            )

        val userIds: MutableList<UUID> = mutableListOf()
        for (i in 0 until 1025) {
            userIds.add(UUID.randomUUID())
        }

        staffApiGWPreconditions.fillContractsSearchRequest(userIds)

        staffApiGWActions.getContractsWithError(
            tokens!!.accessToken,
            staffApiGWPreconditions.contractRequest(),
            HttpStatus.SC_BAD_REQUEST
        )

    }


    @Test
    @Tags(Tag("smoke"), Tag("emproIntegration"))
    @DisplayName("Get contracts for user with >1 contract")
    fun getTwoContract() {
        val tokens = commonPreconditions
            .createAndAuthorizeStaffUser(
                roles = listOf(ApiEnum(EmployeeRole.DARKSTORE_ADMIN)),
                vehicle = null,
                staffPartnerId = null
            )

        val createDeliveryman = commonPreconditions.createProfileDeliveryman(
            mobile = Constants.mobile2,
            darkstoreId = Constants.darkstoreId,
            vehicle = Vehicle(ApiEnum(EmployeeVehicleType.BICYCLE)),
            accountingProfileId = Constants.accountingProfileIdWithTwoContracts
        ).profileId
        staffApiGWPreconditions.fillContractsSearchRequest(userIds = mutableListOf(createDeliveryman))

        val contracts = staffApiGWActions.getContracts(tokens!!.accessToken, staffApiGWPreconditions.contractRequest())
        val contractFromDB =
            mutableListOf(employeeActions.getSeveralContractFromDB(Constants.accountingProfileIdWithTwoContracts))

        staffApiGWAssertions.checkContractsResponse(
            contracts,
            mutableListOf(createDeliveryman),
            contractFromDB
        )


    }

    @Test
    @Tags(Tag("emproIntegration"))
    @DisplayName("Get contracts for user with 0 contract")
    fun getNullContract() {
        val tokens = commonPreconditions
            .createAndAuthorizeStaffUser(
                roles = listOf(ApiEnum(EmployeeRole.DARKSTORE_ADMIN)),
                vehicle = null,
                staffPartnerId = null
            )

        val createDeliveryman = commonPreconditions.createProfileDeliveryman(
            mobile = Constants.mobile2,
            darkstoreId = Constants.darkstoreId,
            vehicle = Vehicle(ApiEnum(EmployeeVehicleType.BICYCLE)),
            accountingProfileId = null
        ).profileId
        staffApiGWPreconditions.fillContractsSearchRequest(userIds = mutableListOf(createDeliveryman))

        val contracts = staffApiGWActions.getContracts(tokens!!.accessToken, staffApiGWPreconditions.contractRequest())

        staffApiGWAssertions.checkEmptyContractResponse(
            contracts,
            mutableListOf(createDeliveryman)
        )


    }


    @Test
    @Tags(Tag("emproIntegration"))
    @DisplayName("Get contracts for disabled user")
    fun getDisabledUser() {
        val tokens = commonPreconditions
            .createAndAuthorizeStaffUser(
                roles = listOf(ApiEnum(EmployeeRole.DARKSTORE_ADMIN)),
                vehicle = null,
                staffPartnerId = null
            )

        val createDeliveryman = commonPreconditions.createProfileDeliveryman(
            mobile = Constants.mobile2,
            darkstoreId = Constants.darkstoreId,
            vehicle = Vehicle(ApiEnum(EmployeeVehicleType.BICYCLE)),
            accountingProfileId = Constants.accountingProfileIdForTimesheet1
        ).profileId

        employeeActions.deleteProfile(createDeliveryman)
        staffApiGWPreconditions.fillContractsSearchRequest(userIds = mutableListOf(createDeliveryman))

        val contracts = staffApiGWActions.getContracts(tokens!!.accessToken, staffApiGWPreconditions.contractRequest())
        val contractFromDB =
            mutableListOf(employeeActions.getSeveralContractFromDB(Constants.accountingProfileIdForTimesheet1))

        staffApiGWAssertions.checkContractsResponse(
            contracts,
            mutableListOf(createDeliveryman),
            contractFromDB
        )


    }

    @Test
    @Tags(Tag("smoke"), Tag("emproIntegration"))
    @DisplayName("Check retirementsData")
    fun getInactiveContractsWithoutActiveUntil() {
        val event1 = employeePreconditions.fillOutsourseContractEvent(
            accountingProfileId = UUID.randomUUID(),
            dataUvolneniya = Instant.now().toString()
        )
        employeeActions.produceToVneshnieSotrudniki(event1)

        val event2 = employeePreconditions.fillOutsourseContractEvent(
            accountingProfileId = event1.payload[0].fizicheskoeLitso.guid,
            dataUvolneniya = Instant.now().minusSeconds(84000).toString()
        )

        employeeActions.produceToVneshnieSotrudniki(event2)

        val event3 = employeePreconditions.fillOutsourseContractEvent(
            accountingProfileId = event1.payload[0].fizicheskoeLitso.guid,
            dataUvolneniya = Instant.now().plusSeconds(84000).toString()
        )

        employeeActions.produceToVneshnieSotrudniki(event3)

        val event4 = employeePreconditions.fillOutsourseContractEvent(
            accountingProfileId = event1.payload[0].fizicheskoeLitso.guid
        )
        employeeActions.produceToVneshnieSotrudniki(event4)

        val createDeliveryman = commonPreconditions.createProfileDeliveryman(
            mobile = Constants.mobile2,
            darkstoreId = Constants.darkstoreIdWithNotMoscowTimezone,
            vehicle = Vehicle(ApiEnum(EmployeeVehicleType.BICYCLE)),
            accountingProfileId = event1.payload[0].fizicheskoeLitso.guid.toString(
            )
        ).profileId

        val tokens = commonPreconditions
            .createAndAuthorizeStaffUser(
                roles = listOf(ApiEnum(EmployeeRole.DARKSTORE_ADMIN)),
                vehicle = null,
                staffPartnerId = null
            )
        staffApiGWPreconditions.fillContractsSearchRequest(
            userIds = mutableListOf(createDeliveryman)
        )

        val contracts = staffApiGWActions.getContracts(tokens!!.accessToken, staffApiGWPreconditions.contractRequest())
        val contractFromDB =
            mutableListOf(employeeActions.getSeveralContractFromDB(event1.payload[0].fizicheskoeLitso.guid.toString()))

        staffApiGWAssertions.checkContractsResponse(
            contracts,
            mutableListOf(createDeliveryman),
            contractFromDB

        )


    }

    @Test
    @Tags(Tag("smoke"), Tag("emproIntegration"))
    @DisplayName("Get active contracts: inactive contracts are not received")
    fun getInactiveContract() {
        val tokens = commonPreconditions
            .createAndAuthorizeStaffUser(
                roles = listOf(ApiEnum(EmployeeRole.DARKSTORE_ADMIN)),
                vehicle = null,
                staffPartnerId = null
            )

        val createDeliveryman = commonPreconditions.createProfileDeliveryman(
            mobile = Constants.mobile2,
            darkstoreId = Constants.darkstoreIdWithNotMoscowTimezone,
            vehicle = Vehicle(ApiEnum(EmployeeVehicleType.BICYCLE)),
            accountingProfileId = Constants.accountingProfileIdWithInactiveContract
        ).profileId

        staffApiGWPreconditions.fillContractsSearchRequest(
            userIds = mutableListOf(createDeliveryman),
            activeUntil = Instant.now()
        )

        val contracts = staffApiGWActions.getContracts(tokens!!.accessToken, staffApiGWPreconditions.contractRequest())

        staffApiGWAssertions.checkEmptyContractResponse(
            contracts,
            mutableListOf(createDeliveryman)
        )

    }

    @Test
    @Tags(Tag("emproIntegration"))
    @DisplayName("Get active contracts: inactive and active contracts")
    fun getInactiveAndActiveContract() {

        val tokens = commonPreconditions
            .createAndAuthorizeStaffUser(
                roles = listOf(ApiEnum(EmployeeRole.DARKSTORE_ADMIN)),
                vehicle = null,
                staffPartnerId = null
            )

        val createDeliveryman = commonPreconditions.createProfileDeliveryman(
            mobile = Constants.mobile2,
            darkstoreId = Constants.darkstoreIdWithNotMoscowTimezone,
            vehicle = Vehicle(ApiEnum(EmployeeVehicleType.BICYCLE)),
            accountingProfileId = Constants.accountingProfileIdWithActiveAndInactiveContracts
        ).profileId

        staffApiGWPreconditions.fillContractsSearchRequest(
            userIds = mutableListOf(createDeliveryman),
            activeUntil = Instant.now()
        )

        val contracts = staffApiGWActions.getContracts(tokens!!.accessToken, staffApiGWPreconditions.contractRequest())
        val contractFromDB =
            mutableListOf(listOf(employeeActions.getContractFromDBByContractId(Constants.activeContract3.toString())))

        staffApiGWAssertions.checkContractsResponse(
            contracts,
            mutableListOf(createDeliveryman),
            contractFromDB
        )

    }

    @Test
    @Tags(Tag("smoke"), Tag("emproIntegration"))
    @DisplayName("Get active contracts: inactive from today contracts are received")
    fun getInactiveFromTodayContract() {

        val event = employeePreconditions.fillOutsourseContractEvent(
            accountingProfileId = UUID.randomUUID(),
            dataUvolneniya = Instant.now().toString()
        )
        employeeActions.produceToVneshnieSotrudniki(event)

        val createDeliveryman = commonPreconditions.createProfileDeliveryman(
            mobile = Constants.mobile2,
            darkstoreId = Constants.darkstoreIdWithNotMoscowTimezone,
            vehicle = Vehicle(ApiEnum(EmployeeVehicleType.BICYCLE)),
            accountingProfileId = event.payload[0].fizicheskoeLitso.guid.toString(
            )
        ).profileId

        val tokens = commonPreconditions
            .createAndAuthorizeStaffUser(
                roles = listOf(ApiEnum(EmployeeRole.DARKSTORE_ADMIN)),
                vehicle = null,
                staffPartnerId = null
            )
        staffApiGWPreconditions.fillContractsSearchRequest(
            userIds = mutableListOf(createDeliveryman),
            activeUntil = Instant.now().minusSeconds(10800)
        )

        val contracts = staffApiGWActions.getContracts(tokens!!.accessToken, staffApiGWPreconditions.contractRequest())
        val contractFromDB =
            mutableListOf(employeeActions.getSeveralContractFromDB(event.payload[0].fizicheskoeLitso.guid.toString()))

        staffApiGWAssertions.checkContractsResponse(
            contracts,
            mutableListOf(createDeliveryman),
            contractFromDB
        )


    }


    @Test
    @DisplayName("Get contracts by disabled user is impossible")
    fun getContractByDisabledUser() {
        val tokens = commonPreconditions
            .createAndAuthorizeStaffUser(
                roles = listOf(ApiEnum(EmployeeRole.DARKSTORE_ADMIN)),
                vehicle = null,
                staffPartnerId = null
            )

        val createDeliveryman = commonPreconditions.createProfileDeliveryman(
            mobile = Constants.mobile2,
            darkstoreId = Constants.darkstoreIdWithNotMoscowTimezone,
            vehicle = Vehicle(ApiEnum(EmployeeVehicleType.BICYCLE)),
            accountingProfileId = Constants.accountingProfileIdForTimesheet1
        ).profileId

        staffApiGWPreconditions.fillContractsSearchRequest(userIds = mutableListOf(createDeliveryman))

        employeeActions.deleteProfile(Constants.mobile1)

        staffApiGWActions.getContractsWithError(
            tokens!!.accessToken,
            staffApiGWPreconditions.contractRequest(),
            HttpStatus.SC_FORBIDDEN
        )

    }

    @Test
    @DisplayName("Get contracts by coordinator is impossible")
    fun getContractByCoordinator() {
        val tokens = commonPreconditions
            .createAndAuthorizeStaffUser(
                roles = listOf(ApiEnum(EmployeeRole.COORDINATOR)),
                vehicle = null,
                staffPartnerId = null,
                darkstoreId = null,
                supervisedDarkstores = Constants.supervisedDarkstores,
                cityId = null
            )


        val createDeliveryman = commonPreconditions.createProfileDeliveryman(
            mobile = Constants.mobile2,
            darkstoreId = Constants.darkstoreIdWithNotMoscowTimezone,
            vehicle = Vehicle(ApiEnum(EmployeeVehicleType.BICYCLE)),
            accountingProfileId = Constants.accountingProfileIdForTimesheet1
        ).profileId

        staffApiGWPreconditions.fillContractsSearchRequest(userIds = mutableListOf(createDeliveryman))

        staffApiGWActions.getContractsWithError(
            tokens!!.accessToken,
            staffApiGWPreconditions.contractRequest(),
            HttpStatus.SC_FORBIDDEN
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

        staffApiGWActions.getContractsWithError(
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

        val contracts = staffApiGWActions.getContracts(tokens!!.accessToken, staffApiGWPreconditions.contractRequest())

        staffApiGWAssertions.checkEmptyContractResponse(
            contracts,
            mutableListOf(createGoodsManger)
        )

    }


}