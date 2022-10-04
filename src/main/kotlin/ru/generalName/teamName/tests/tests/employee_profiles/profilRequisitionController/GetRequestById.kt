package ru.samokat.mysamokat.tests.tests.employee_profiles.profilRequisitionController

import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.samokat.employeeprofiles.api.common.domain.EmployeeVehicleType
import ru.samokat.employeeprofiles.api.common.domain.Vehicle
import ru.samokat.employeeprofiles.api.profilerequisitions.domain.ProfileRequisitionStatus
import ru.samokat.my.rest.api.enum.ApiEnum
import ru.samokat.mysamokat.tests.checkers.EmployeeAssertion
import ru.samokat.mysamokat.tests.dataproviders.Constants
import ru.samokat.mysamokat.tests.dataproviders.helpers.database.employee_profiles_backend.ProfileRequisition
import ru.samokat.mysamokat.tests.dataproviders.preconditions.CommonPreconditions
import ru.samokat.mysamokat.tests.dataproviders.preconditions.EmployeePreconditions
import ru.samokat.mysamokat.tests.helpers.actions.EmployeeActions
import java.util.*

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Tag("empro")
class GetRequestById {

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
        employeeActions.deleteRequisitionFromDatabase(Constants.accountingProfileIdForRequisitions.toString())
    }

    @AfterEach
    fun release() {
        employeeAssertion.assertAll()
        employeeActions.deleteProfile(Constants.mobile1)
        employeeActions.deleteProfileByAccountingProfileId(Constants.accountingProfileIdForRequisitions.toString())
        employeeActions.deleteRequisitionFromDatabase(Constants.accountingProfileIdForRequisitions.toString())

    }

    @Test
    @Tags(Tag("kafka_produce"), Tag("smoke"))
    @DisplayName("Get new outsource request by id")
    fun getNewOutsourceRequestByIdTest(){

        val event = employeePreconditions.fillOutsourseContractEvent(
            accountingProfileId = Constants.accountingProfileIdForRequisitions,
        )
        employeeActions.produceToVneshnieSotrudniki(event)

        val dbRequisition = employeeActions.getRequisitionFromDBByAccountingProfileId(Constants.accountingProfileIdForRequisitions.toString())!!

        val apiRequisition = employeeActions.getRequisitionById(dbRequisition[ProfileRequisition.requestId])

        employeeAssertion
            .checkOutsourceRequisitionDB(event, dbRequisition)
            .checkRequisitionApi(event, apiRequisition)
    }

    @Test
    @Tags(Tag("kafka_produce"), Tag("smoke"))
    @DisplayName("Get new innersource request by id")
    fun getNewInnersourceRequestByIdTest(){

        val event = employeePreconditions.fillPriemNaRabotyEvent(
            accountingProfileId = Constants.accountingProfileIdForRequisitions,
            inn = Constants.innForRequisitions
        )
        employeeActions.produceToPriemNaRabotuCFZ(event)

        val dbRequisition = employeeActions.getRequisitionFromDBByAccountingProfileId(Constants.innForRequisitions)!!

        val apiRequisition = employeeActions.getRequisitionById(dbRequisition[ProfileRequisition.requestId])

        employeeAssertion
            .checkInnersourceRequisitionDB(event, dbRequisition)
            .checkInnersourceRequisitionApi(event, apiRequisition)
    }

    @Test
    @Tags(Tag("kafka_produce"))
    @DisplayName("Get declined request by id")
    fun getDeclinedRequestByIdTest(){

        val event = employeePreconditions.fillOutsourseContractEvent(
            accountingProfileId = Constants.accountingProfileIdForRequisitions,
        )
        val declineRequest = employeePreconditions.fillDeclineRequisitionRequest()
        employeeActions.produceToVneshnieSotrudniki(event)

        val dbRequisition = employeeActions.getRequisitionFromDBByAccountingProfileId(Constants.accountingProfileIdForRequisitions.toString())!!
        employeeActions.declineProfileRequisition(dbRequisition[ProfileRequisition.requestId], declineRequest)

        val apiRequisition = employeeActions.getRequisitionById(dbRequisition[ProfileRequisition.requestId])

        employeeAssertion
            .checkOutsourceRequisitionDB(event, dbRequisition)
            .checkRequisitionApi(event, apiRequisition, ApiEnum(ProfileRequisitionStatus.DECLINED))
    }

    @Test
    @Tags(Tag("kafka_produce"))
    @DisplayName("Get processed request by id")
    fun getProcessedRequestByIdTest(){

        val event = employeePreconditions.fillOutsourseContractEvent(
            accountingProfileId = Constants.accountingProfileIdForRequisitions,
        )
        employeeActions.produceToVneshnieSotrudniki(event)

        val dbRequisition = employeeActions.getRequisitionFromDBByAccountingProfileId(Constants.accountingProfileIdForRequisitions.toString())!!

        commonPreconditions.createProfileDeliveryman(requisitionId = dbRequisition[ProfileRequisition.requestId]).profileId

        val apiRequisition = employeeActions.getRequisitionById(dbRequisition[ProfileRequisition.requestId])

        employeeAssertion
            .checkOutsourceRequisitionDB(event, dbRequisition)
            .checkRequisitionApi(event, apiRequisition, ApiEnum(ProfileRequisitionStatus.PROCESSED))
    }

    @Test
    @DisplayName("Get non-existant request by id")
    fun getNonExistantRequestByIdTest(){

        val error = employeeActions.getRequisitionByIdWithError(UUID.randomUUID())

        employeeAssertion.checkErrorMessage(error.message, "Requisition Not Found")
    }

    @Test
    @Tags(Tag("kafka_produce"))
    @DisplayName("Get request by id - without mobile")
    fun getNewRequestByIdWithoutMobileTest(){

        val event = employeePreconditions.fillOutsourseContractEvent(
            accountingProfileId = Constants.accountingProfileIdForRequisitions, mobile = ""
        )
        employeeActions.produceToVneshnieSotrudniki(event)

        val dbRequisition = employeeActions.getRequisitionFromDBByAccountingProfileId(Constants.accountingProfileIdForRequisitions.toString())!!
        val apiRequisition = employeeActions.getRequisitionById(dbRequisition[ProfileRequisition.requestId])

        employeeAssertion
            .checkOutsourceRequisitionDB(event, dbRequisition)
            .checkRequisitionApi(event, apiRequisition)
    }

    @Test
    @Tags(Tag("kafka_produce"))
    @DisplayName("Get request by id - without name")
    fun getNewRequestByIdWithoutNameTest(){

        val event = employeePreconditions.fillOutsourseContractEvent(
            accountingProfileId = Constants.accountingProfileIdForRequisitions, naimenovanie = ""
        )
        employeeActions.produceToVneshnieSotrudniki(event)

        val dbRequisition = employeeActions.getRequisitionFromDBByAccountingProfileId(Constants.accountingProfileIdForRequisitions.toString())!!
        val apiRequisition = employeeActions.getRequisitionById(dbRequisition[ProfileRequisition.requestId])

        employeeAssertion
            .checkOutsourceRequisitionDB(event, dbRequisition)
            .checkRequisitionApi(event, apiRequisition)
    }
}
