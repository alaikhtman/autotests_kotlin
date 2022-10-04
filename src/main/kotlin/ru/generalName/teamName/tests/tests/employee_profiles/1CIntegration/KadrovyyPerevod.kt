package ru.samokat.mysamokat.tests.tests.employee_profiles.`1CIntegration`

import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.samokat.mysamokat.tests.checkers.EmployeeAssertion
import ru.samokat.mysamokat.tests.dataproviders.preconditions.EmployeePreconditions
import ru.samokat.mysamokat.tests.helpers.actions.EmployeeActions

import java.time.Instant
import java.time.temporal.ChronoUnit

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Tag("empro")
class KadrovyyPerevod {

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
    @DisplayName("Save event from KadrovyyPerevodCFZ (proveden = true)")
    fun produceToKadrovyyPerevodCFZCFZKafkaProveden() {

        val eventPriemNaRaboty = employeePreconditions.fillPriemNaRabotyEvent()
        val eventKadrovyyPerevod = employeePreconditions.fillKadrovyyPerevodEvent(
            accountingContractId = eventPriemNaRaboty.payload[0].sotrudnik.guid,
            accountingProfileId = eventPriemNaRaboty.payload[0].fizicheskoeLitso.guid,
            naimenovanie = "Петров Сергей Федорович",
        )
        employeeActions.produceToPriemNaRabotuCFZ(eventPriemNaRaboty)
        employeeActions.produceToKadrovyyPerevodCFZ(eventKadrovyyPerevod)

        val dbContract = employeeActions.getContractFromDB(eventKadrovyyPerevod.payload[0].fizicheskoeLitso!!.inn!!)
        val dbContractLog = employeeActions.getContractLogFromDbByType(
            eventKadrovyyPerevod.payload[0].sotrudnik!!.guid.toString(),
            "updated"
        )
        val task = employeeActions.getTaskExistanceByCorrelationId(eventKadrovyyPerevod.headers.id.toString())

        employeeAssertion.checkContractDB(eventKadrovyyPerevod, dbContract)
        employeeAssertion.checkContractLogTypeDB("updated", dbContractLog)
        employeeAssertion.getSoftAssertion().assertThat(task).isFalse

    }

    @Test
    @Tags(Tag("kafka_produce"))
    @DisplayName("Save event from KadrovyyPerevodSpiskomCFZ (proveden = true)")
    fun produceToKadrovyyPerevodSpiskomCFZKafkaProveden() {

        val eventPriemSpiskom = employeePreconditions.fillPriemNaRabotySpiskomEvent()
        val eventPerevodSpiskom = employeePreconditions.fillKadrovyyPerevodSpiskomEvent(
            accountingContractId1 = eventPriemSpiskom.payload[0].sotrudniki[0].sotrudnik.guid,
            accountingProfileId1 = eventPriemSpiskom.payload[0].sotrudniki[0].fizicheskoeLitso.guid,
            accountingContractId2 = eventPriemSpiskom.payload[0].sotrudniki[1].sotrudnik.guid,
            accountingProfileId2 = eventPriemSpiskom.payload[0].sotrudniki[1].fizicheskoeLitso.guid,

            )
        employeeActions.produceToPriemNaRabotuSpiskomCFZ(eventPriemSpiskom)
        employeeActions.produceToKadrovyyPerevodSpiskomCFZ(eventPerevodSpiskom)

        val dbContract1 =
            employeeActions.getContractFromDB(eventPerevodSpiskom.payload[0].sotrudniki[0].fizicheskoeLitso.inn!!)
        val dbContractLog1 = employeeActions.getContractLogFromDbByType(
            eventPerevodSpiskom.payload[0].sotrudniki[0].sotrudnik.guid.toString(),
            "updated"
        )

        val dbContract2 =
            employeeActions.getContractFromDB(eventPerevodSpiskom.payload[0].sotrudniki[1].fizicheskoeLitso.inn!!)
        val dbContractLog2 = employeeActions.getContractLogFromDbByType(
            eventPerevodSpiskom.payload[0].sotrudniki[1].sotrudnik.guid.toString(),
            "updated"
        )


        employeeAssertion.checkContractDB(eventPerevodSpiskom.payload[0].sotrudniki[0], true, dbContract1)
        employeeAssertion.checkContractLogTypeDB("updated", dbContractLog1)

        employeeAssertion.checkContractDB(eventPerevodSpiskom.payload[0].sotrudniki[1], true, dbContract2)
        employeeAssertion.checkContractLogTypeDB("updated", dbContractLog2)
    }

    @Test
    @Tags(Tag("kafka_produce"))
    @DisplayName("Save event from KadrovyyPerevodCFZ (contract not exists)")
    fun produceToKadrovyyPerevodCFZCFZKafkaContractNotExists() {

        val eventKadrovyyPerevod = employeePreconditions.fillKadrovyyPerevodEvent()

        employeeActions.produceToKadrovyyPerevodCFZ(eventKadrovyyPerevod)
        val dbFailedEvent =
            employeeActions.getPerevodTaskFromDB(eventKadrovyyPerevod.payload[0].sotrudnik!!.guid.toString())

        employeeAssertion.checkTwoEventsAreEqual(eventKadrovyyPerevod, dbFailedEvent!!)
    }

    @Test
    @Tags(Tag("kafka_produce"))
    @DisplayName("Save event from KadrovyyPerevodCFZ (update contract proveden = true)")
    fun produceToKadrovyyPerevodCFZCFZKafkaProvedenDogovorFalse() {

        val eventPriemNaRaboty = employeePreconditions.fillPriemNaRabotyEvent(proveden = false)
        val eventKadrovyyPerevod = employeePreconditions.fillKadrovyyPerevodEvent(
            accountingContractId = eventPriemNaRaboty.payload[0].sotrudnik.guid,
            accountingProfileId = eventPriemNaRaboty.payload[0].fizicheskoeLitso.guid,
            naimenovanie = "Петров Сергей Федорович",
        )
        employeeActions.produceToPriemNaRabotuCFZ(eventPriemNaRaboty)
        employeeActions.produceToKadrovyyPerevodCFZ(eventKadrovyyPerevod)

        val dbContract = employeeActions.getContractFromDB(eventKadrovyyPerevod.payload[0].fizicheskoeLitso!!.inn!!)
        val dbContractLog = employeeActions.getContractLogFromDbByType(
            eventKadrovyyPerevod.payload[0].sotrudnik!!.guid.toString(),
            "updated"
        )

        employeeAssertion.checkContractDB(eventKadrovyyPerevod, dbContract)
        employeeAssertion.checkContractLogTypeDB("updated", dbContractLog)
    }

    @Test
    @Tags(Tag("kafka_produce"))
    @DisplayName("Save event from KadrovyyPerevodCFZ (several transactions)")
    fun produceToKadrovyyPerevodCFZCFZKafkaSeveralTransactions() {

        val eventPriemNaRaboty = employeePreconditions.fillPriemNaRabotyEvent()
        val eventKadrovyyPerevod1 = employeePreconditions.fillKadrovyyPerevodEvent(
            accountingContractId = eventPriemNaRaboty.payload[0].sotrudnik.guid,
            accountingProfileId = eventPriemNaRaboty.payload[0].fizicheskoeLitso.guid,
            naimenovanie = "Петров Сергей Федорович",
            dataNachala = "2021-10-05T21:00:00Z",
            dataOkonchaniya = "0001-01-01T00:00:00Z"
        )
        val eventKadrovyyPerevod2 = employeePreconditions.fillKadrovyyPerevodEvent(
            accountingContractId = eventPriemNaRaboty.payload[0].sotrudnik.guid,
            accountingProfileId = eventPriemNaRaboty.payload[0].fizicheskoeLitso.guid,
            naimenovanie = "Сидоров Федор Иванович",
            dataNachala = "2021-11-05T21:00:00Z",
            dataOkonchaniya = "0001-01-01T00:00:00Z"
        )
        employeeActions.produceToPriemNaRabotuCFZ(eventPriemNaRaboty)

        employeeActions.produceToKadrovyyPerevodCFZ(eventKadrovyyPerevod1)
        employeeActions.produceToKadrovyyPerevodCFZ(eventKadrovyyPerevod2)


        val dbContract = employeeActions.getContractFromDB(eventKadrovyyPerevod2.payload[0].fizicheskoeLitso!!.inn!!)

        employeeAssertion.checkContractDB(eventKadrovyyPerevod2, dbContract)
    }

    @Test
    @Tags(Tag("kafka_produce"))
    @DisplayName("Save event from KadrovyyPerevodCFZ (dataNachala in the future)")
    fun produceToKadrovyyPerevodCFZCFZKafkaWithFutureDate() {

        val eventPriemNaRaboty = employeePreconditions.fillPriemNaRabotyEvent()
        val eventKadrovyyPerevod = employeePreconditions.fillKadrovyyPerevodEvent(
            accountingContractId = eventPriemNaRaboty.payload[0].sotrudnik.guid,
            accountingProfileId = eventPriemNaRaboty.payload[0].fizicheskoeLitso.guid,
            naimenovanie = "Петров Сергей Федорович",
            dataNachala = Instant.now().plusSeconds(3600).truncatedTo(ChronoUnit.SECONDS).toString()
        )

        employeeActions.produceToPriemNaRabotuCFZ(eventPriemNaRaboty)
        employeeActions.produceToKadrovyyPerevodCFZ(eventKadrovyyPerevod)

        val task = employeeActions.getTaskFromDBByCorrelationId(eventKadrovyyPerevod.headers.id.toString())
        val dbContract = employeeActions.getContractFromDB(eventPriemNaRaboty.payload[0].fizicheskoeLitso.inn!!)

        employeeAssertion.checkScheduledTransferTask(task, eventKadrovyyPerevod)
        employeeAssertion.checkContractDB(eventPriemNaRaboty, dbContract)
    }

    @Test
    @Tags(Tag("kafka_produce"))
    @DisplayName("Save event from KadrovyyPerevodCFZ (cancell transfer)")
    fun produceToKadrovyyPerevodCFZCFZKafkaCancellTransfer() {

        val eventPriemNaRaboty = employeePreconditions.fillPriemNaRabotyEvent()
        val eventKadrovyyPerevodTrue = employeePreconditions.fillKadrovyyPerevodEvent(
            accountingContractId = eventPriemNaRaboty.payload[0].sotrudnik.guid,
            accountingProfileId = eventPriemNaRaboty.payload[0].fizicheskoeLitso.guid,
            naimenovanie = "Петров Сергей Федорович",
            proveden = true,
            dateInMilliseconds = System.currentTimeMillis().minus(10000L)
        )

        employeeActions.produceToPriemNaRabotuCFZ(eventPriemNaRaboty)
        employeeActions.produceToKadrovyyPerevodCFZ(eventKadrovyyPerevodTrue)


        val eventKadrovyyPerevodFalse = employeePreconditions.fillKadrovyyPerevodEvent(
            accountingContractId = eventPriemNaRaboty.payload[0].sotrudnik.guid,
            accountingProfileId = eventPriemNaRaboty.payload[0].fizicheskoeLitso.guid,
            naimenovanie = eventKadrovyyPerevodTrue.payload[0].fizicheskoeLitso!!.naimenovanie,
            proveden = false,
            dateInMilliseconds = System.currentTimeMillis().minus(10L),
            payloadGuid = eventKadrovyyPerevodTrue.payload[0].guid!!,
            inn = eventKadrovyyPerevodTrue.payload[0].fizicheskoeLitso!!.inn!!,
            dataNachala = eventKadrovyyPerevodTrue.payload[0].dataNachala!!,
            dataOkonchaniya = eventKadrovyyPerevodTrue.payload[0].dataOkonchaniya!!,
            dolzhnostGuid = eventKadrovyyPerevodTrue.payload[0].dolzhnost!!.guid,
            dolzhnostNaimenovanie = eventKadrovyyPerevodTrue.payload[0].dolzhnost!!.naimenovanie
        )
        employeeActions.produceToKadrovyyPerevodCFZ(eventKadrovyyPerevodFalse)

        val dbContract = employeeActions.getContractFromDB(eventPriemNaRaboty.payload[0].fizicheskoeLitso.inn!!)

        employeeAssertion.checkContractDB(eventPriemNaRaboty, dbContract)
    }

    @Test
    @Tags(Tag("kafka_produce"))
    @DisplayName("Save event from KadrovyyPerevodCFZ (with end date)")
    fun produceToKadrovyyPerevodCFZCFZKafkaWithEndDate() {

        val eventPriemNaRaboty = employeePreconditions.fillPriemNaRabotyEvent()
        val eventKadrovyyPerevod = employeePreconditions.fillKadrovyyPerevodEvent(
            accountingContractId = eventPriemNaRaboty.payload[0].sotrudnik.guid,
            accountingProfileId = eventPriemNaRaboty.payload[0].fizicheskoeLitso.guid,
            naimenovanie = "Петров Сергей Федорович",
            dataOkonchaniya = Instant.now().plusSeconds(3600).truncatedTo(ChronoUnit.SECONDS).toString()
        )
        employeeActions.produceToPriemNaRabotuCFZ(eventPriemNaRaboty)
        employeeActions.produceToKadrovyyPerevodCFZ(eventKadrovyyPerevod)

        val dbContract = employeeActions.getContractFromDB(eventKadrovyyPerevod.payload[0].fizicheskoeLitso!!.inn!!)
        val dbContractLog = employeeActions.getContractLogFromDbByType(
            eventKadrovyyPerevod.payload[0].sotrudnik!!.guid.toString(),
            "updated"
        )
        val task = employeeActions.getTaskFromDBByCorrelationId(eventKadrovyyPerevod.headers.id.toString())

        employeeAssertion.checkContractDB(eventKadrovyyPerevod, dbContract)
        employeeAssertion.checkContractLogTypeDB("updated", dbContractLog)

        employeeAssertion.checkScheduledTransferTask(task!!, eventKadrovyyPerevod, "innerSourceEmployeeTransferRollback")

    }

    @Test
    @Tags(Tag("kafka_produce"))
    @DisplayName("Save event from KadrovyyPerevodCFZ (proveden = false before true)")
    fun produceToKadrovyyPerevodCFZCFZKafkaProvedenFalseBeforeTrue() {

        val eventPriemNaRaboty = employeePreconditions.fillPriemNaRabotyEvent()
        val eventKadrovyyPerevod = employeePreconditions.fillKadrovyyPerevodEvent(
            accountingContractId = eventPriemNaRaboty.payload[0].sotrudnik.guid,
            accountingProfileId = eventPriemNaRaboty.payload[0].fizicheskoeLitso.guid,
            naimenovanie = "Петров Сергей Федорович",
            proveden = false
        )
        employeeActions.produceToPriemNaRabotuCFZ(eventPriemNaRaboty)
        employeeActions.produceToKadrovyyPerevodCFZ(eventKadrovyyPerevod)

        val dbContract = employeeActions.getContractFromDB(eventPriemNaRaboty.payload[0].fizicheskoeLitso.inn!!)
        val task = employeeActions.getPerevodTaskFromDB(eventKadrovyyPerevod.payload[0].sotrudnik!!.guid.toString())

        employeeAssertion.checkContractDB(eventPriemNaRaboty, dbContract)
        employeeAssertion.checkTwoEventsAreEqual(eventKadrovyyPerevod, task!!)
    }

    @Test
    @Tags(Tag("kafka_produce"))
    @DisplayName("Save event from KadrovyyPerevodCFZ (outdated event)")
    fun produceToKadrovyyPerevodCFZCFZKafkaOutdatedEvent() {

        val eventPriemNaRaboty = employeePreconditions.fillPriemNaRabotyEvent()
        val eventKadrovyyPerevod = employeePreconditions.fillKadrovyyPerevodEvent(
            accountingContractId = eventPriemNaRaboty.payload[0].sotrudnik.guid,
            accountingProfileId = eventPriemNaRaboty.payload[0].fizicheskoeLitso.guid,
            naimenovanie = "Петров Сергей Федорович",
            dataNachala = "2021-10-05T21:00:00Z",
            dataOkonchaniya = "2021-10-06T21:00:00Z",
        )
        employeeActions.produceToPriemNaRabotuCFZ(eventPriemNaRaboty)
        employeeActions.produceToKadrovyyPerevodCFZ(eventKadrovyyPerevod)

        val dbContract = employeeActions.getContractFromDB(eventPriemNaRaboty.payload[0].fizicheskoeLitso.inn!!)

        employeeAssertion.checkContractDB(eventPriemNaRaboty, dbContract)
    }

}
