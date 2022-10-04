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
class PriemNaRaboty {

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
    @Tags(Tag("kafka_produce"), Tag("Smoke"))
    @DisplayName("Save event from PriemNaRabotuCFZ (proveden = true)")
    fun produceToPriemNaRabotuCFZKafkaProveden(){
        val event = employeePreconditions.fillPriemNaRabotyEvent()
        employeeActions.produceToPriemNaRabotuCFZ(event)

        val dbContract = employeeActions.getContractFromDB(event.payload[0].fizicheskoeLitso.inn!!)
        val dbContractLog = employeeActions.getContractLogFromDB(event.payload[0].sotrudnik.guid.toString())

        employeeAssertion.checkContractDB(event, dbContract)
        employeeAssertion.checkContractLogTypeDB("new", dbContractLog)
    }

    @Test
    @Tags(Tag("kafka_produce"))
    @DisplayName("Save event from PriemNaRabotuCFZ (proveden = false)")
    fun produceToPriemNaRabotuCFZKafkaNeProveden(){
        val event = employeePreconditions.fillPriemNaRabotyEvent(
            proveden =  false
        )
        employeeActions.produceToPriemNaRabotuCFZ(event)

        val dbContract = employeeActions.getContractFromDB(event.payload[0].fizicheskoeLitso.inn!!)
        val dbContractLog = employeeActions.getContractLogFromDB(event.payload[0].sotrudnik.guid.toString())

        employeeAssertion.checkContractDB(event, dbContract)
        employeeAssertion.checkContractLogTypeDB("new", dbContractLog)
    }

    @Test
    @Tags(Tag("kafka_produce"))
    @DisplayName("Save event from PriemNaRabotuSpiskomCFZ (proveden = true)")
    fun produceToPriemNaRabotuSpiskomCFZKafkaProveden(){
        val event = employeePreconditions.fillPriemNaRabotySpiskomEvent()
        employeeActions.produceToPriemNaRabotuSpiskomCFZ(event)

        val dbContract1 = employeeActions.getContractFromDB(event.payload[0].sotrudniki[0].fizicheskoeLitso.inn!!)
        val dbContractLog1 = employeeActions.getContractLogFromDB(event.payload[0].sotrudniki[0].sotrudnik.guid.toString())

        val dbContract2 = employeeActions.getContractFromDB(event.payload[0].sotrudniki[1].fizicheskoeLitso.inn!!)
        val dbContractLog2 = employeeActions.getContractLogFromDB(event.payload[0].sotrudniki[1].sotrudnik.guid.toString())


        employeeAssertion.checkContractDB(event.payload[0].sotrudniki[0], true, dbContract1)
        employeeAssertion.checkContractLogTypeDB("new", dbContractLog1)
        employeeAssertion.checkContractDB(event.payload[0].sotrudniki[1], true, dbContract2)
        employeeAssertion.checkContractLogTypeDB("new", dbContractLog2)
    }

    @Test
    @Tags(Tag("kafka_produce"))
    @DisplayName("Save failed event from PriemNaRabotuCFZ")
    fun produceToPriemNaRabotuCFZKafkaFail(){
        val event = employeePreconditions.fillPriemNaRabotyEvent(dateInMilliseconds = null)
        employeeActions.produceToPriemNaRabotuCFZ(event)

        val dbFailedEvent = employeeActions.getTaskFromDB(event.payload[0].sotrudnik.guid.toString())

        employeeAssertion.checkTwoEventsAreEqual(event, dbFailedEvent!!)
    }

    @Test
    @Tags(Tag("kafka_produce"))
    @DisplayName("Save event from PriemNaRabotuCFZ (update to proveden = false)")
    fun produceToPriemNaRabotuCFZKafkaUpdateToProvedenFalse(){
        val event = employeePreconditions.fillPriemNaRabotyEvent()
        employeeActions.produceToPriemNaRabotuCFZ(event)

        val eventFalse = employeePreconditions.fillPriemNaRabotyEvent(
            accountingContractId = event.payload[0].sotrudnik.guid,
            inn = event.payload[0].fizicheskoeLitso.inn!!,
            proveden = false
        )
        employeeActions.produceToPriemNaRabotuCFZ(eventFalse)

        val contractExists = employeeActions.getContractExistanse(event.payload[0].sotrudnik.guid.toString())
        val dbContractLog = employeeActions.getContractLogFromDbByType(event.payload[0].sotrudnik.guid.toString(), "removed")

        employeeAssertion.checkContractLogTypeDB("removed", dbContractLog)
            .getSoftAssertion().assertThat(contractExists).isFalse
    }

    @Test
    @Tags(Tag("kafka_produce"))
    @DisplayName("Save event from PriemNaRabotuCFZ (update to proveden = true)")
    fun produceToPriemNaRabotuCFZKafkaUpdateToProvedenTrue(){
        val event = employeePreconditions.fillPriemNaRabotyEvent(
            proveden = false
        )
        employeeActions.produceToPriemNaRabotuCFZ(event)

        val eventTrue = employeePreconditions.fillPriemNaRabotyEvent(
            accountingContractId = event.payload[0].sotrudnik.guid,
            inn = event.payload[0].fizicheskoeLitso.inn!!,
            proveden = true
        )
        employeeActions.produceToPriemNaRabotuCFZ(eventTrue)

        val dbContract = employeeActions.getContractFromDB(event.payload[0].fizicheskoeLitso.inn!!)

        employeeAssertion.checkContractDB(eventTrue, dbContract)
    }

    @Test
    @Tags(Tag("kafka_produce"))
    @DisplayName("Update event data from PriemNaRabotuCFZ ")
    fun produceToPriemNaRabotuCFZKafkaUpdateEventData(){
        val event1 = employeePreconditions.fillPriemNaRabotyEvent(
            proveden = true
        )
        employeeActions.produceToPriemNaRabotuCFZ(event1)

        val event2 = employeePreconditions.fillPriemNaRabotyEvent(
            accountingContractId = event1.payload[0].sotrudnik.guid,
            inn = event1.payload[0].fizicheskoeLitso.inn!!,
            naimenovanie = "Иванов Иван Иванович",
            proveden = true
        )
        employeeActions.produceToPriemNaRabotuCFZ(event2)

        val dbContract = employeeActions.getContractFromDB(event1.payload[0].fizicheskoeLitso.inn!!)

        employeeAssertion.checkContractDB(event2, dbContract)
    }
}