package ru.samokat.mysamokat.tests.tests.employee_profiles.`1CIntegration`

import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.samokat.mysamokat.tests.checkers.EmployeeAssertion
import ru.samokat.mysamokat.tests.dataproviders.preconditions.EmployeePreconditions
import ru.samokat.mysamokat.tests.helpers.actions.EmployeeActions

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Tag("empro")
class OutsourceContracts {

    private lateinit var employeeAssertion: EmployeeAssertion

    @Autowired
    private lateinit var employeeActions: EmployeeActions

    private lateinit var employeePreconditions: EmployeePreconditions

    @BeforeEach
    fun before() {
        employeePreconditions = EmployeePreconditions()
        employeeAssertion = EmployeeAssertion()
    }

    @AfterEach
    fun release() {
        employeeAssertion.assertAll()
    }

    @Test
    @Tags(Tag("kafka_produce"), Tag("smoke"))
    @DisplayName("Save event from VneshnieSotrudniki")
    fun produceToVneshnieSotrudniki(){
        val event = employeePreconditions.fillOutsourseContractEvent()
        employeeActions.produceToVneshnieSotrudniki(event)

        val dbContract = employeeActions.getContractFromDB(event.payload[0].fizicheskoeLitso.guid.toString())
        val dbContractLog = employeeActions.getContractLogFromDB(event.payload[0].guid)

        employeeAssertion.checkContractDB(event, dbContract)
        employeeAssertion.checkContractLogTypeDB("new", dbContractLog)
    }

    @Test
    @Tags(Tag("kafka_produce"))
    @DisplayName("Save event from VneshnieSotrudniki (several contracts)")
    fun produceToVneshnieSotrudnikiSeveralContracts(){
        val event1 = employeePreconditions.fillOutsourseContractEvent()
        employeeActions.produceToVneshnieSotrudniki(event1)

        val event2 = employeePreconditions.fillOutsourseContractEvent(accountingProfileId = event1.payload[0].fizicheskoeLitso.guid)
        employeeActions.produceToVneshnieSotrudniki(event2)

        val dbContract1 = employeeActions.getContractFromDBByContractId(event1.payload[0].guid)
        val dbContractLog1 = employeeActions.getContractLogFromDB(event1.payload[0].guid)

        val dbContract2 = employeeActions.getContractFromDBByContractId(event2.payload[0].guid)
        val dbContractLog2 = employeeActions.getContractLogFromDB(event2.payload[0].guid)

        employeeAssertion.checkContractDB(event1, dbContract1)
        employeeAssertion.checkContractLogTypeDB("new", dbContractLog1)
        employeeAssertion.checkContractDB(event2, dbContract2)
        employeeAssertion.checkContractLogTypeDB("new", dbContractLog2)
    }

    @Test
    @Tags(Tag("kafka_produce"))
    @DisplayName("Save event from VneshnieSotrudniki (outdated event)")
    fun produceToVneshnieSotrudnikiOutdated(){
        val event1 = employeePreconditions.fillOutsourseContractEvent(
            dateInMilliseconds = System.currentTimeMillis().minus(10000L)
        )
        employeeActions.produceToVneshnieSotrudniki(event1)

        val event2 = employeePreconditions.fillOutsourseContractEvent(
            dateInMilliseconds = System.currentTimeMillis().minus(20000L),
            accountingProfileId = event1.payload[0].fizicheskoeLitso.guid,
            accountingContractId = event1.payload[0].guid
        )
        employeeActions.produceToVneshnieSotrudniki(event2)

        val dbContract = employeeActions.getContractFromDBByContractId(event1.payload[0].guid)
        val dbContractLog = employeeActions.getContractLogFromDB(event1.payload[0].guid)

        employeeAssertion.checkContractDB(event1, dbContract)
        employeeAssertion.checkContractLogTypeDB("new", dbContractLog)
    }

    @Test
    @Tags(Tag("kafka_produce"))
    @DisplayName("Save event from VneshnieSotrudniki (update contract)")
    fun produceToVneshnieSotrudnikiUpdateEvent(){
        val event1 = employeePreconditions.fillOutsourseContractEvent(
            dateInMilliseconds = System.currentTimeMillis().minus(20000L)
        )
        employeeActions.produceToVneshnieSotrudniki(event1)

        val event2 = employeePreconditions.fillOutsourseContractEvent(
            dateInMilliseconds = System.currentTimeMillis().minus(10000L),
            accountingProfileId = event1.payload[0].fizicheskoeLitso.guid,
            accountingContractId = event1.payload[0].guid
        )
        employeeActions.produceToVneshnieSotrudniki(event2)

        val dbContract = employeeActions.getContractFromDBByContractId(event1.payload[0].guid)
        val dbContractLog = employeeActions.getContractLogFromDbByType(event1.payload[0].guid, "updated")

        employeeAssertion.checkContractDB(event2, dbContract)
        employeeAssertion.checkContractLogTypeDB("updated", dbContractLog)
    }
    
}