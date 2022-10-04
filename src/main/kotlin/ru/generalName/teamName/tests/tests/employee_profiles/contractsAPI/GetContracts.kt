package ru.samokat.mysamokat.tests.tests.employee_profiles.contractsAPI

import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.samokat.my.domain.employee.EmployeeName
import ru.samokat.my.domain.employee.EmployeeRole
import ru.samokat.my.rest.api.enum.ApiEnum
import ru.samokat.mysamokat.tests.checkers.EmployeeAssertion
import ru.samokat.mysamokat.tests.dataproviders.Constants
import ru.samokat.mysamokat.tests.dataproviders.preconditions.EmployeePreconditions
import ru.samokat.mysamokat.tests.helpers.actions.EmployeeActions
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Tag("empro")
class GetContracts {

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
        employeeActions.deleteProfile(Constants.mobile3)
    }

    @AfterEach
    fun release() {
        employeeAssertion.assertAll()
        employeeActions.deleteProfile(Constants.mobile1)
        employeeActions.deleteProfile(Constants.mobile2)
        employeeActions.deleteProfile(Constants.mobile3)
    }

    @Test
    @Tags(Tag("kafka_produce"), Tag("smoke"))
    @DisplayName("Get employee contract")
    fun getEmployeeContract() {

        val event = employeePreconditions.fillOutsourseContractEvent()
        employeeActions.produceToVneshnieSotrudniki(event)

        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.PICKER)),
                vehicle = null,
                staffPartnerId = Constants.staffPartnerId,
                email = null,
                name = EmployeeName("Test", "Test", "Test"),
                accountingProfileId = event.payload[0].fizicheskoeLitso.guid.toString()
            )

        val createdProfileId = employeeActions.createProfileId(createRequest)

        val searchContractRequest = employeePreconditions.fillSearchContractsRequest(listOf(createdProfileId))

        val contracts = employeeActions.getProfileContract(searchContractRequest).contracts

        employeeAssertion.checkProfileContract(event, contracts, createdProfileId)
    }

    @Test
    @DisplayName("Get employee contract (contractNotExists)")
    fun getEmployeeContractNotExist() {

        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.PICKER)),
                vehicle = null,
                staffPartnerId = Constants.staffPartnerId,
                email = null,
                name = EmployeeName("Test", "Test", "Test"),
                accountingProfileId = null
            )

        val createdProfileId = employeeActions.createProfileId(createRequest)

        val searchContractRequest = employeePreconditions.fillSearchContractsRequest(listOf(createdProfileId))

        val contracts = employeeActions.getProfileContract(searchContractRequest).contracts

        employeeAssertion.checkProfileContractCount(contracts, createdProfileId, 0)
    }

    @Test
    @Tags(Tag("kafka_produce"))
    @DisplayName("Get employee contract (several contracts)")
    fun getEmployeeContractSeveralContracts() {

        val event1 = employeePreconditions.fillOutsourseContractEvent()
        employeeActions.produceToVneshnieSotrudniki(event1)

        val event2 =
            employeePreconditions.fillOutsourseContractEvent(accountingProfileId = event1.payload[0].fizicheskoeLitso.guid)
        employeeActions.produceToVneshnieSotrudniki(event2)

        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.PICKER)),
                vehicle = null,
                staffPartnerId = Constants.staffPartnerId,
                email = null,
                name = EmployeeName("Test", "Test", "Test"),
                accountingProfileId = event1.payload[0].fizicheskoeLitso.guid.toString()
            )

        val createdProfileId = employeeActions.createProfileId(createRequest)

        val searchContractRequest = employeePreconditions.fillSearchContractsRequest(listOf(createdProfileId))

        val contracts = employeeActions.getProfileContract(searchContractRequest).contracts

        employeeAssertion.checkProfileContractCount(contracts, createdProfileId, 2)
    }

    @Test
    @Tags(Tag("kafka_produce"))
    @DisplayName("Get employee contract (profile disabled)")
    fun getEmployeeContractProfileDisabled() {

        val event = employeePreconditions.fillOutsourseContractEvent()
        employeeActions.produceToVneshnieSotrudniki(event)

        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.PICKER)),
                vehicle = null,
                staffPartnerId = Constants.staffPartnerId,
                email = null,
                name = EmployeeName("Test", "Test", "Test"),
                accountingProfileId = event.payload[0].fizicheskoeLitso.guid.toString()
            )

        val createdProfileId = employeeActions.createProfileId(createRequest)
        employeeActions.deleteProfile(createdProfileId)

        val searchContractRequest = employeePreconditions.fillSearchContractsRequest(listOf(createdProfileId))

        val contracts = employeeActions.getProfileContract(searchContractRequest).contracts

        employeeAssertion.checkProfileContract(event, contracts, createdProfileId)
    }

    @Test
    @DisplayName("Get employee contract (empty ids)")
    fun getEmployeeContractEmptyIds() {

        val searchContractRequest = employeePreconditions.fillSearchContractsRequest(listOf())

        val message = employeeActions.getProfileContractWithError(searchContractRequest).message

        employeeAssertion.checkErrorMessage(message, "Profile IDs list must contain from 1 to 1024 items")
    }

    @Test
    @DisplayName("Get employee contract (more than 1024 ids)")
    fun getEmployeeContractTooMuchIds() {

        val idsList = mutableListOf<UUID>()

        for (i in 0 until 1025) {
            idsList.add(UUID.randomUUID())
        }
        val searchContractRequest = employeePreconditions.fillSearchContractsRequest(idsList)

        val message = employeeActions.getProfileContractWithError(searchContractRequest).message

        employeeAssertion.checkErrorMessage(message, "Profile IDs list must contain from 1 to 1024 items")
    }

    @Test
    @DisplayName("Get employee contract (repeated ids)")
    fun getEmployeeContractSameIds() {

        val id = UUID.randomUUID()
        val searchContractRequest = employeePreconditions.fillSearchContractsRequest(listOf(id, id))

        val message = employeeActions.getProfileContractWithError(searchContractRequest).message

        employeeAssertion.checkErrorMessage(message, "Only unique profile IDs are allowed")
    }

    @Test
    @Tags(Tag("kafka_produce"))
    @DisplayName("Get employee contract (other role)")
    fun getEmployeeContractOtherRole() {

        val event = employeePreconditions.fillOutsourseContractEvent()
        employeeActions.produceToVneshnieSotrudniki(event)

        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DARKSTORE_ADMIN)),
                vehicle = null,
                staffPartnerId = null,
                email = null,
                name = EmployeeName("Test", "Test", "Test"),
                accountingProfileId = event.payload[0].fizicheskoeLitso.guid.toString()
            )

        val createdProfileId = employeeActions.createProfileId(createRequest)

        val searchContractRequest = employeePreconditions.fillSearchContractsRequest(listOf(createdProfileId))

        val contracts = employeeActions.getProfileContract(searchContractRequest).contracts

        employeeAssertion.checkProfileContract(event, contracts, createdProfileId)
    }

    @Test
    @Tags(Tag("kafka_produce"))
    @DisplayName("Get employee contract (by several employees)")
    fun getEmployeeContractBySeveralEmployees() {

        val event1 = employeePreconditions.fillOutsourseContractEvent()
        employeeActions.produceToVneshnieSotrudniki(event1)
        val event2 = employeePreconditions.fillOutsourseContractEvent(mobile = Constants.mobile2.asStringWithoutPlus())
        employeeActions.produceToVneshnieSotrudniki(event2)

        val createRequest1 = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.PICKER)),
                vehicle = null,
                staffPartnerId = Constants.staffPartnerId,
                email = null,
                name = EmployeeName("Test", "Test", "Test"),
                accountingProfileId = event1.payload[0].fizicheskoeLitso.guid.toString()
            )
        val createRequest2 = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.PICKER)),
                mobile = Constants.mobile2,
                vehicle = null,
                staffPartnerId = Constants.staffPartnerId,
                email = null,
                name = EmployeeName("Test", "Test", "Test"),
                accountingProfileId = event2.payload[0].fizicheskoeLitso.guid.toString()
            )
        val createRequest3 = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.PICKER)),
                mobile = Constants.mobile3,
                vehicle = null,
                staffPartnerId = Constants.staffPartnerId,
                email = null,
                name = EmployeeName("Test", "Test", "Test"),
                accountingProfileId = null
            )

        val createdProfileId1 = employeeActions.createProfileId(createRequest1)
        val createdProfileId2 = employeeActions.createProfileId(createRequest2)
        val createdProfileId3 = employeeActions.createProfileId(createRequest3)

        val searchContractRequest =
            employeePreconditions.fillSearchContractsRequest(
                listOf(
                    createdProfileId1,
                    createdProfileId2,
                    createdProfileId3
                )
            )

        val contracts = employeeActions.getProfileContract(searchContractRequest).contracts

        employeeAssertion.checkProfileContract(event1, contracts, createdProfileId1)
        employeeAssertion.checkProfileContract(event2, contracts, createdProfileId2)
        employeeAssertion.checkProfileContractCount(contracts, createdProfileId3, 0)
    }

    @Test
    @Tags(Tag("kafka_produce"), Tag("smoke"))
    @DisplayName("Get employee contract by accounting profile id")
    fun getEmployeeContractByAccountingProfileId() {

        val event = employeePreconditions.fillOutsourseContractEvent()
        employeeActions.produceToVneshnieSotrudniki(event)

        val searchContractRequest =
            employeePreconditions.fillSearchContractsRequestByAccountingProfileId(listOf(event.payload[0].fizicheskoeLitso.guid.toString()))

        val contracts = employeeActions.getProfileContractByAccountingProfileId(searchContractRequest).contracts

        employeeAssertion.checkProfileContractByAccountingProfileId(
            event,
            contracts,
            event.payload[0].fizicheskoeLitso.guid
        )
    }

    @Test
    @Tags(Tag("kafka_produce"), Tag("smoke"))
    @DisplayName("Get active employee contract: active and inactive contracts")
    fun getActiveEmployeeContractWithFutureRetirementDate() {

        val event1 = employeePreconditions.fillOutsourseContractEvent(
            dataUvolneniya = "2022-03-10T21:00:00Z"
        )
        employeeActions.produceToVneshnieSotrudniki(event1)

        val event2 =
            employeePreconditions.fillOutsourseContractEvent(
                accountingProfileId = event1.payload[0].fizicheskoeLitso.guid,
                dataUvolneniya = Instant.now().plusSeconds(172800).truncatedTo(ChronoUnit.SECONDS).toString()
            )
        employeeActions.produceToVneshnieSotrudniki(event2)

        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.PICKER)),
                vehicle = null,
                staffPartnerId = Constants.staffPartnerId,
                email = null,
                name = EmployeeName("Test", "Test", "Test"),
                accountingProfileId = event1.payload[0].fizicheskoeLitso.guid.toString()
            )

        val createdProfileId = employeeActions.createProfileId(createRequest)

        val searchContractRequest =
            employeePreconditions.fillSearchContractsRequest(
                listOf(createdProfileId),
                activeUntil = Instant.now().truncatedTo(ChronoUnit.SECONDS)
            )

        val contracts = employeeActions.getProfileContract(searchContractRequest).contracts

        employeeAssertion
            .checkProfileContractCount(contracts, createdProfileId, 1)
            .checkProfileContract(event2, contracts, createdProfileId)
    }

    @Test
    @Tags(Tag("kafka_produce"), Tag("smoke"))
    @DisplayName("Get active employee contract: from today inactive contract is received")
    fun getTodayInactiveEmployeeContract() {

        val today = Instant.now().truncatedTo(ChronoUnit.SECONDS)
        val event = employeePreconditions.fillOutsourseContractEvent(
            dataUvolneniya = today.toString()
        )
        employeeActions.produceToVneshnieSotrudniki(event)


        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.PICKER)),
                vehicle = null,
                staffPartnerId = Constants.staffPartnerId,
                email = null,
                name = EmployeeName("Test", "Test", "Test"),
                accountingProfileId = event.payload[0].fizicheskoeLitso.guid.toString()
            )

        val createdProfileId = employeeActions.createProfileId(createRequest)

        val searchContractRequest =
            employeePreconditions.fillSearchContractsRequest(
                listOf(createdProfileId),
                activeUntil = today
            )

        val contracts = employeeActions.getProfileContract(searchContractRequest).contracts

        employeeAssertion
            .checkProfileContract(event, contracts, createdProfileId)
    }


    @Test
    @Tags(Tag("kafka_produce"), Tag("smoke"))
    @DisplayName("Get active employee contract: 1 inactive contracts is not received")
    fun getInactiveEmployeeContract() {

        val event = employeePreconditions.fillOutsourseContractEvent(
            dataUvolneniya = "2022-03-10T21:00:00Z"
        )
        employeeActions.produceToVneshnieSotrudniki(event)


        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.PICKER)),
                vehicle = null,
                staffPartnerId = Constants.staffPartnerId,
                email = null,
                name = EmployeeName("Test", "Test", "Test"),
                accountingProfileId = event.payload[0].fizicheskoeLitso.guid.toString()
            )

        val createdProfileId = employeeActions.createProfileId(createRequest)

        val searchContractRequest =
            employeePreconditions.fillSearchContractsRequest(
                listOf(createdProfileId),
                activeUntil = Instant.now().minusSeconds(10).truncatedTo(ChronoUnit.SECONDS)
            )

        val contracts = employeeActions.getProfileContract(searchContractRequest).contracts

        employeeAssertion
            .checkProfileContractCount(contracts, createdProfileId, 0)
    }

    @Test
    @Tags(Tag("kafka_produce"))
    @DisplayName("Get active employee contract: 2 inactive contracts are not received")
    fun getSeveralInactiveEmployeeContract() {

        val event = employeePreconditions.fillOutsourseContractEvent(
            dataUvolneniya = "2022-03-10T21:00:00Z"
        )
        employeeActions.produceToVneshnieSotrudniki(event)

        val event2 =
            employeePreconditions.fillOutsourseContractEvent(
                accountingProfileId = event.payload[0].fizicheskoeLitso.guid,
                dataUvolneniya = "2022-03-11T21:00:00Z"
            )
        employeeActions.produceToVneshnieSotrudniki(event2)

        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.PICKER)),
                vehicle = null,
                staffPartnerId = Constants.staffPartnerId,
                email = null,
                name = EmployeeName("Test", "Test", "Test"),
                accountingProfileId = event.payload[0].fizicheskoeLitso.guid.toString()
            )

        val createdProfileId = employeeActions.createProfileId(createRequest)

        val searchContractRequest =
            employeePreconditions.fillSearchContractsRequest(
                listOf(createdProfileId),
                activeUntil = Instant.now().truncatedTo(ChronoUnit.SECONDS)
            )

        val contracts = employeeActions.getProfileContract(searchContractRequest).contracts

        employeeAssertion
            .checkProfileContractCount(contracts, createdProfileId, 0)

    }


    @Test
    @Tags(Tag("kafka_produce"), Tag("smoke"))
    @DisplayName("Get active employee contract: active contract without retirementData")
    fun getActiveEmployeeContractWithoutRetirementDate() {

        val event = employeePreconditions.fillOutsourseContractEvent()
        employeeActions.produceToVneshnieSotrudniki(event)

        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.PICKER)),
                vehicle = null,
                staffPartnerId = Constants.staffPartnerId,
                email = null,
                name = EmployeeName("Test", "Test", "Test"),
                accountingProfileId = event.payload[0].fizicheskoeLitso.guid.toString()
            )

        val createdProfileId = employeeActions.createProfileId(createRequest)

        val searchContractRequest =
            employeePreconditions.fillSearchContractsRequest(
                listOf(createdProfileId),
                activeUntil = Instant.now().truncatedTo(ChronoUnit.SECONDS)
            )

        val contracts = employeeActions.getProfileContract(searchContractRequest).contracts

        employeeAssertion
            .checkProfileContractCount(contracts, createdProfileId, 1)

    }


}