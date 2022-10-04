package ru.samokat.mysamokat.tests.tests.employee_profiles.`1CIntegration`

import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.samokat.my.domain.employee.EmployeeRole
import ru.samokat.my.rest.api.enum.ApiEnum
import ru.samokat.mysamokat.tests.checkers.EmployeeAssertion
import ru.samokat.mysamokat.tests.dataproviders.Constants
import ru.samokat.mysamokat.tests.dataproviders.StringAndPhoneNumberGenerator
import ru.samokat.mysamokat.tests.dataproviders.helpers.database.employee_profiles_backend.ProfileRequisition
import ru.samokat.mysamokat.tests.dataproviders.preconditions.CommonPreconditions
import ru.samokat.mysamokat.tests.dataproviders.preconditions.EmployeePreconditions
import ru.samokat.mysamokat.tests.helpers.actions.EmployeeActions
import java.util.*

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Tag("empro")
class InnerSourceEmployeeCreation {

    private lateinit var employeeAssertion: EmployeeAssertion

    @Autowired
    private lateinit var employeeActions: EmployeeActions

    private lateinit var employeePreconditions: EmployeePreconditions

    @Autowired
    private lateinit var commonPreconditions: CommonPreconditions

    @BeforeEach
    fun before() {
        employeePreconditions = EmployeePreconditions()
        employeeAssertion = EmployeeAssertion()
        employeeActions.deleteProfile(Constants.mobile1)
        employeeActions.deleteProfileByAccountingProfileId(Constants.accountingProfileIdForRequisitions.toString())
        employeeActions.deleteProfileByAccountingProfileId(Constants.accountingProfileIdForRequisitions2.toString())
        employeeActions.deleteProfileByAccountingProfileId(Constants.innForRequisitions)
        employeeActions.deleteProfileByAccountingProfileId(Constants.innForRequisitions2)
        employeeActions.deleteRequisitionFromDatabase(Constants.accountingProfileIdForRequisitions.toString())
        employeeActions.deleteRequisitionFromDatabase(Constants.accountingProfileIdForRequisitions2.toString())
        employeeActions.deleteRequisitionFromDatabase(Constants.innForRequisitions)
        employeeActions.deleteRequisitionFromDatabase(Constants.innForRequisitions2)
    }

    @AfterEach
    fun release() {
        employeeAssertion.assertAll()
        employeeActions.deleteProfile(Constants.mobile1)
        employeeActions.deleteProfileByAccountingProfileId(Constants.accountingProfileIdForRequisitions.toString())
        employeeActions.deleteProfileByAccountingProfileId(Constants.accountingProfileIdForRequisitions2.toString())
        employeeActions.deleteProfileByAccountingProfileId(Constants.innForRequisitions)
        employeeActions.deleteProfileByAccountingProfileId(Constants.innForRequisitions2)
        employeeActions.deleteRequisitionFromDatabase(Constants.accountingProfileIdForRequisitions.toString())
        employeeActions.deleteRequisitionFromDatabase(Constants.accountingProfileIdForRequisitions2.toString())
        employeeActions.deleteRequisitionFromDatabase(Constants.innForRequisitions)
        employeeActions.deleteRequisitionFromDatabase(Constants.innForRequisitions2)

    }

    @Test
    @Tags(Tag("kafka_produce"), Tag("smoke"))
    @DisplayName("New contract with proveden = true: create request")
    fun newContractProvedenTrueCreateRequestTest() {

        val event = employeePreconditions.fillPriemNaRabotyEvent(
            accountingProfileId = Constants.accountingProfileIdForRequisitions,
            inn = Constants.innForRequisitions
        )
        employeeActions.produceToPriemNaRabotuCFZ(event)

        val dbRequisition = employeeActions.getRequisitionFromDBByAccountingProfileId(Constants.innForRequisitions)!!
        val dbContractLog = employeeActions.getRequisitionLog(dbRequisition[ProfileRequisition.requestId])

        employeeAssertion
            .checkInnersourceRequisitionDB(event, dbRequisition)
            .checkRequisitionLogVersion(dbContractLog)
    }

    @Test
    @Tags(Tag("kafka_produce"), Tag("smoke"))
    @DisplayName("New contract with proveden = false: request not created")
    fun newContractProvedenFalseCreateRequestTest() {

        val event = employeePreconditions.fillPriemNaRabotyEvent(
            accountingProfileId = Constants.accountingProfileIdForRequisitions,
            inn = Constants.innForRequisitions,
            proveden = false
        )
        employeeActions.produceToPriemNaRabotuCFZ(event)

        val requisitionExistance = employeeActions.getRequisitionExistance(Constants.innForRequisitions)

        employeeAssertion.checkRequisitionNotExists(requisitionExistance)
    }

    @Test
    @Tags(Tag("kafka_produce"), Tag("smoke"))
    @DisplayName("Profile with accounting_profile_id disabled: create request")
    fun profileWithGuidDisabledCreateRequestTest() {

        val profileId = commonPreconditions.createProfileDarkstoreAdmin(
            accountingProfileId = Constants.innForRequisitions
        ).profileId
        employeeActions.deleteProfile(profileId)

        val event = employeePreconditions.fillPriemNaRabotyEvent(
            accountingProfileId = Constants.accountingProfileIdForRequisitions,
            inn = Constants.innForRequisitions
        )
        employeeActions.produceToPriemNaRabotuCFZ(event)

        val dbRequisition = employeeActions.getRequisitionFromDBByAccountingProfileId(Constants.innForRequisitions)!!
        val dbContractLog = employeeActions.getRequisitionLog(dbRequisition[ProfileRequisition.requestId])

        employeeAssertion
            .checkInnersourceRequisitionDB(event, dbRequisition)
            .checkRequisitionLogVersion(dbContractLog)
    }

    @Test
    @Tags(Tag("kafka_produce"), Tag("smoke"))
    @DisplayName("Profile with accounting_profile_id enabled: request not created")
    fun profileWithGuidEnabledCreateRequestTest() {

        commonPreconditions.createProfileDarkstoreAdmin(
            accountingProfileId = Constants.innForRequisitions
        )

        val event = employeePreconditions.fillPriemNaRabotyEvent(
            accountingProfileId = Constants.accountingProfileIdForRequisitions,
            inn = Constants.innForRequisitions
        )
        employeeActions.produceToPriemNaRabotuCFZ(event)

        val requisitionExistance = employeeActions.getRequisitionExistance(Constants.innForRequisitions)

        employeeAssertion.checkRequisitionNotExists(requisitionExistance)
    }

    @Test
    @Tags(Tag("kafka_produce"))
    @DisplayName("Request with NEW status exists: request not created")
    fun requestInNewStatusExistCreateRequestTest() {

        val event = employeePreconditions.fillPriemNaRabotyEvent(
            accountingProfileId = Constants.accountingProfileIdForRequisitions,
            inn = Constants.innForRequisitions
        )
        employeeActions.produceToPriemNaRabotuCFZ(event)

        val event2 = employeePreconditions.fillPriemNaRabotyEvent(
            accountingProfileId = Constants.accountingProfileIdForRequisitions2,
            inn = Constants.innForRequisitions
        )
        employeeActions.produceToPriemNaRabotuCFZ(event2)

        val requisitionsCount = employeeActions.getRequisitionsCount(Constants.innForRequisitions)
        val dbRequisition = employeeActions.getRequisitionFromDBByAccountingProfileId(Constants.innForRequisitions)!!
        val dbContractLog = employeeActions.getRequisitionLog(dbRequisition[ProfileRequisition.requestId])

        employeeAssertion
            .checkInnersourceRequisitionDB(event, dbRequisition)
            .checkRequisitionLogVersion(dbContractLog)
            .checkRequisitionsCount(requisitionsCount, 1)
    }

    @Test
    @Tags(Tag("kafka_produce"))
    @DisplayName("Request with DECLINED status exists: create request")
    fun requestInDeclinedStatusExistCreateRequestTest() {

        val event1 = employeePreconditions.fillPriemNaRabotyEvent(
            accountingProfileId = Constants.accountingProfileIdForRequisitions,
            inn = Constants.innForRequisitions
        )
        val event2 = employeePreconditions.fillPriemNaRabotyEvent(
            accountingProfileId = Constants.accountingProfileIdForRequisitions2,
            inn = Constants.innForRequisitions
        )
        val declineRequest = employeePreconditions.fillDeclineRequisitionRequest()

        employeeActions.produceToPriemNaRabotuCFZ(event1)
        val dbRequisition1 = employeeActions.getRequisitionFromDBByAccountingProfileId(Constants.innForRequisitions)!!
        employeeActions.declineProfileRequisition(dbRequisition1[ProfileRequisition.requestId], declineRequest)
        val dbRequisitionDeclined =
            employeeActions.getRequisitionFromDBById(dbRequisition1[ProfileRequisition.requestId])!!

        employeeActions.produceToPriemNaRabotuCFZ(event2)

        val dbRequisition =
            employeeActions.getRequisitionFromDBByAccountingProfileIdAndStatus(Constants.innForRequisitions, "NEW")!!

        employeeAssertion
            .checkInnersourceRequisitionDB(event2, dbRequisition)
            .checkInnersourceRequisitionDB(event1, dbRequisitionDeclined, "DECLINED", 2)
    }

    @Test
    @Tags(Tag("kafka_produce"))
    @DisplayName("Request with PROCESSED status exists: create request")
    fun requestInProcessedStatusExistCreateRequestTest() {

        val event = employeePreconditions.fillPriemNaRabotyEvent(
            accountingProfileId = Constants.accountingProfileIdForRequisitions,
            inn = Constants.innForRequisitions
        )
        val event2 = employeePreconditions.fillPriemNaRabotyEvent(
            accountingProfileId = Constants.accountingProfileIdForRequisitions2,
            inn = Constants.innForRequisitions
        )
        employeeActions.produceToPriemNaRabotuCFZ(event)
        val dbRequisition1 = employeeActions.getRequisitionFromDBByAccountingProfileId(Constants.innForRequisitions)!!

        val profileId = commonPreconditions.createProfileDarkstoreAdmin(
            requisitionId = dbRequisition1[ProfileRequisition.requestId]
        ).profileId
        employeeActions.updateAccountingProfileId(profileId, StringAndPhoneNumberGenerator.generateRandomInn())

        val dbRequisitionProcessed =
            employeeActions.getRequisitionFromDBById(dbRequisition1[ProfileRequisition.requestId])!!

        employeeActions.produceToPriemNaRabotuCFZ(event2)

        val dbRequisition =
            employeeActions.getRequisitionFromDBByAccountingProfileIdAndStatus(Constants.innForRequisitions, "NEW")!!

        employeeAssertion
            .checkInnersourceRequisitionDB(event2, dbRequisition)
            .checkInnersourceRequisitionDB(event, dbRequisitionProcessed, "PROCESSED", 2)
    }

    @Test
    @Tags(Tag("kafka_produce"))
    @DisplayName("Exist contract with proveden = false, request not exists: request not created")
    fun existContractProvedenFalseRequestNotExistsCreateRequestTest() {

        val eventTrue = employeePreconditions.fillPriemNaRabotyEvent(
            accountingProfileId = Constants.accountingProfileIdForRequisitions,
            inn = Constants.innForRequisitions,
            proveden = true
        )
        val eventFalse = employeePreconditions.fillPriemNaRabotyEvent(
            accountingProfileId = Constants.accountingProfileIdForRequisitions,
            inn = Constants.innForRequisitions,
            proveden = false
        )

        employeeActions.produceToPriemNaRabotuCFZ(eventTrue)
        employeeActions.deleteRequisitionFromDatabase(Constants.innForRequisitions)

        employeeActions.produceToPriemNaRabotuCFZ(eventFalse)

        val requisitionExistance = employeeActions.getRequisitionExistance(Constants.innForRequisitions)

        employeeAssertion.checkRequisitionNotExists(requisitionExistance)
    }

    @Test
    @Tags(Tag("kafka_produce"))
    @DisplayName("Exist contract with proveden = false, request exists: request was declined")
    fun existContractProvedenFalseRequestExistsCreateRequestTest() {

        val event = employeePreconditions.fillPriemNaRabotyEvent(
            accountingProfileId = Constants.accountingProfileIdForRequisitions,
            inn = Constants.innForRequisitions,
            proveden = true
        )

        employeeActions.produceToPriemNaRabotuCFZ(event)

        event.payload[0].proveden = false
        event.headers.dateInMilliseconds = System.currentTimeMillis().plus(123123)
        employeeActions.produceToPriemNaRabotuCFZ(event)

        val dbRequisition = employeeActions.getRequisitionFromDBByAccountingProfileIdAndStatus(
            Constants.innForRequisitions,
            "DECLINED"
        )!!

        employeeAssertion.checkInnersourceRequisitionDB(event, dbRequisition, "DECLINED", 2)

    }

    @Test
    @Tag("kafka_produce")
    @DisplayName("False before true: request not created")
    fun falseBeforeTrueTest() {

        val event = employeePreconditions.fillPriemNaRabotyEvent(
            accountingProfileId = Constants.accountingProfileIdForRequisitions,
            inn = Constants.innForRequisitions,
            proveden = false
        )
        employeeActions.produceToPriemNaRabotuCFZ(event)

        val requisitionExistance1 = employeeActions.getRequisitionExistance(Constants.innForRequisitions)

        event.payload[0].proveden = true
        event.headers.dateInMilliseconds = System.currentTimeMillis().minus(123123)

        employeeActions.produceToPriemNaRabotuCFZ(event)
        val requisitionExistance2 = employeeActions.getRequisitionExistance(Constants.innForRequisitions)

        employeeAssertion
            .checkRequisitionNotExists(requisitionExistance1)
            .checkRequisitionNotExists(requisitionExistance2)

    }

    @Test
    @Tag("kafka_produce")
    @DisplayName("False before true, update to true: create request")
    fun falseBeforeTrueUpdateToTrueTest() {

        val event = employeePreconditions.fillPriemNaRabotyEvent(
            accountingProfileId = Constants.accountingProfileIdForRequisitions,
            inn = Constants.innForRequisitions,
            proveden = false
        )
        employeeActions.produceToPriemNaRabotuCFZ(event)

        val requisitionExistance1 = employeeActions.getRequisitionExistance(Constants.innForRequisitions)

        event.payload[0].proveden = true
        event.headers.dateInMilliseconds = System.currentTimeMillis().plus(123123)

        employeeActions.produceToPriemNaRabotuCFZ(event)

        val dbRequisition = employeeActions.getRequisitionFromDBByAccountingProfileId(Constants.innForRequisitions)!!

        employeeAssertion
            .checkRequisitionNotExists(requisitionExistance1)
            .checkInnersourceRequisitionDB(event, dbRequisition)
    }

    @Test
    @Tag("kafka_produce")
    @DisplayName("Read from PriemNaRabotuSpiskomCFZ: create requests")
    fun createRequestsFromPriemNaRabotuSpiskomTest() {

        val event = employeePreconditions.fillPriemNaRabotySpiskomEvent(
            inn1 = Constants.innForRequisitions,
            accountingContractId1 = Constants.accountingProfileIdForRequisitions,
            inn2 = Constants.innForRequisitions2,
            accountingContractId2 = Constants.accountingProfileIdForRequisitions2,
        )
        employeeActions.produceToPriemNaRabotuSpiskomCFZ(event)

        val requisitionExistance1 = employeeActions.getRequisitionExistance(Constants.innForRequisitions)
        val requisitionExistance2 = employeeActions.getRequisitionExistance(Constants.innForRequisitions2)

        employeeAssertion
            .checkRequisitionExists(requisitionExistance1)
            .checkRequisitionExists(requisitionExistance2)

    }

    @Test
    @Tag("kafka_produce")
    @DisplayName("Read from PriemNaRabotuSpiskomCFZ: one request not created")
    fun createRequestsFromPriemNaRabotuSpiskomOneRequestNotCreatedTest() {

        val contractId = UUID.randomUUID()

        val event = employeePreconditions.fillPriemNaRabotyEvent(
            accountingProfileId = Constants.accountingProfileIdForRequisitions,
            accountingContractId = contractId,
            inn = Constants.innForRequisitions,
            proveden = false
        )
        employeeActions.produceToPriemNaRabotuCFZ(event)

        val eventSpiskom = employeePreconditions.fillPriemNaRabotySpiskomEvent(
            inn1 = Constants.innForRequisitions,
            accountingProfileId1 = Constants.accountingProfileIdForRequisitions,
            accountingContractId1 = contractId,
            inn2 = Constants.innForRequisitions2,
            accountingProfileId2 = Constants.accountingProfileIdForRequisitions2,
            dateInMilliseconds = System.currentTimeMillis().minus(123123123)
        )
        employeeActions.produceToPriemNaRabotuSpiskomCFZ(eventSpiskom)

        val requisitionExistance1 = employeeActions.getRequisitionExistance(Constants.innForRequisitions)
        val requisitionExistance2 = employeeActions.getRequisitionExistance(Constants.innForRequisitions2)

        employeeAssertion
            .checkRequisitionNotExists(requisitionExistance1)
            .checkRequisitionExists(requisitionExistance2)

    }

    @Test
    @Tags(Tag("kafka_produce"))
    @DisplayName("Create request: > 3 words in FIO")
    fun moreThreeWordsInFioTest() {

        val event = employeePreconditions.fillPriemNaRabotyEvent(
            accountingProfileId = Constants.accountingProfileIdForRequisitions,
            inn = Constants.innForRequisitions,
            naimenovanie = "ФИО Более Трех Слов"
        )
        employeeActions.produceToPriemNaRabotuCFZ(event)

        val dbRequisition = employeeActions.getRequisitionFromDBByAccountingProfileId(Constants.innForRequisitions)!!
        val dbContractLog = employeeActions.getRequisitionLog(dbRequisition[ProfileRequisition.requestId])

        employeeAssertion
            .checkInnersourceRequisitionDB(event, dbRequisition)
            .checkRequisitionLogVersion(dbContractLog)
    }

    @Test
    @Tags(Tag("kafka_produce"))
    @DisplayName("Create request: without middle name")
    fun withoutMiddleNameTest() {

        val event = employeePreconditions.fillPriemNaRabotyEvent(
            accountingProfileId = Constants.accountingProfileIdForRequisitions,
            inn = Constants.innForRequisitions,
            naimenovanie = "Фамилия Имя"
        )
        employeeActions.produceToPriemNaRabotuCFZ(event)

        val dbRequisition = employeeActions.getRequisitionFromDBByAccountingProfileId(Constants.innForRequisitions)!!
        val dbContractLog = employeeActions.getRequisitionLog(dbRequisition[ProfileRequisition.requestId])

        employeeAssertion
            .checkInnersourceRequisitionDB(event, dbRequisition)
            .checkRequisitionLogVersion(dbContractLog)
    }


    @Test
    @Tags(Tag("kafka_produce"))
    @DisplayName("Create request: FIO with numbers")
    fun fioWithNumbersTest() {

        val event = employeePreconditions.fillPriemNaRabotyEvent(
            accountingProfileId = Constants.accountingProfileIdForRequisitions,
            inn = Constants.innForRequisitions,
            naimenovanie = "Фамилия12 Имя34"
        )
        employeeActions.produceToPriemNaRabotuCFZ(event)

        val dbRequisition = employeeActions.getRequisitionFromDBByAccountingProfileId(Constants.innForRequisitions)!!
        val dbContractLog = employeeActions.getRequisitionLog(dbRequisition[ProfileRequisition.requestId])

        employeeAssertion
            .checkInnersourceRequisitionDB(event, dbRequisition)
            .checkRequisitionLogVersion(dbContractLog)
    }

    @Test
    @Tags(Tag("kafka_produce"))
    @DisplayName("Create request: empty FIO")
    fun emptyFIOTest() {

        val event = employeePreconditions.fillPriemNaRabotyEvent(
            accountingProfileId = Constants.accountingProfileIdForRequisitions,
            inn = Constants.innForRequisitions,
            naimenovanie = ""
        )
        employeeActions.produceToPriemNaRabotuCFZ(event)

        val dbRequisition = employeeActions.getRequisitionFromDBByAccountingProfileId(Constants.innForRequisitions)!!
        val dbContractLog = employeeActions.getRequisitionLog(dbRequisition[ProfileRequisition.requestId])

        employeeAssertion
            .checkInnersourceRequisitionDB(event, dbRequisition)
            .checkRequisitionLogVersion(dbContractLog)
    }

}