package ru.samokat.mysamokat.tests.tests.employee_profiles.profilRequisitionController

import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.samokat.employeeprofiles.api.common.domain.EmployeeVehicleType
import ru.samokat.employeeprofiles.api.common.domain.Vehicle
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
class DeсlineRequest {

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
    @DisplayName("Decline an existing request")
    fun declineAnExistingRequestTest(){

        val event = employeePreconditions.fillOutsourseContractEvent(
            accountingProfileId = Constants.accountingProfileIdForRequisitions,
        )
        val declineRequest = employeePreconditions.fillDeclineRequisitionRequest()
        employeeActions.produceToVneshnieSotrudniki(event)

        val dbRequisition = employeeActions.getRequisitionFromDBByAccountingProfileId(Constants.accountingProfileIdForRequisitions.toString())!!
        employeeActions.declineProfileRequisition(dbRequisition[ProfileRequisition.requestId], declineRequest)

        val dbRequisition2 = employeeActions.getRequisitionFromDBByAccountingProfileIdAndStatus(Constants.accountingProfileIdForRequisitions.toString(), "DECLINED")!!
        val dbContractLog2 = employeeActions.getRequisitionLogByVersion(dbRequisition[ProfileRequisition.requestId], 2)

        employeeAssertion
            .checkOutsourceRequisitionDB(event, dbRequisition)
            .checkOutsourceRequisitionDB(event, dbRequisition2, "DECLINED", 2)
            .checkRequisitionLogVersion(dbContractLog2, 2)
    }

    @Test
    @DisplayName("Decline non-existent request")
    fun declineNonExistentRequestTest(){

        val declineRequest = employeePreconditions.fillDeclineRequisitionRequest()
        val error = employeeActions.declineProfileRequisitionWithError(UUID.randomUUID(), declineRequest)

        employeeAssertion.checkErrorMessage(error.message, "Requisition Not Found")
    }

    @Test
    @Tags(Tag("kafka_produce"))
    @DisplayName("Decline already declined request")
    fun declineAlreadyDeclinedRequestTest(){

        val event = employeePreconditions.fillOutsourseContractEvent(
            accountingProfileId = Constants.accountingProfileIdForRequisitions,
        )
        val declineRequest1 = employeePreconditions.fillDeclineRequisitionRequest()
        val declineRequest2 = employeePreconditions.fillDeclineRequisitionRequest(UUID.randomUUID())
        employeeActions.produceToVneshnieSotrudniki(event)

        val dbRequisition = employeeActions.getRequisitionFromDBByAccountingProfileId(Constants.accountingProfileIdForRequisitions.toString())!!
        employeeActions.declineProfileRequisition(dbRequisition[ProfileRequisition.requestId], declineRequest1)

        employeeActions.declineProfileRequisition(dbRequisition[ProfileRequisition.requestId], declineRequest2)

        val dbRequisition2 = employeeActions.getRequisitionFromDBByAccountingProfileId(Constants.accountingProfileIdForRequisitions.toString())!!
        val dbContractLog2 = employeeActions.getRequisitionLogByVersion(dbRequisition[ProfileRequisition.requestId], 2)

        employeeAssertion
            .checkOutsourceRequisitionDB(event, dbRequisition)
            .checkOutsourceRequisitionDB(event, dbRequisition2, "DECLINED", 2)
            .checkRequisitionLogVersion(dbContractLog2, 2)
    }

    @Test
    @DisplayName("Decline processed request")
    fun declineProcessedRequestTest(){

        val event = employeePreconditions.fillOutsourseContractEvent(
            accountingProfileId = Constants.accountingProfileIdForRequisitions,
        )
        employeeActions.produceToVneshnieSotrudniki(event)

        val dbRequisition = employeeActions.getRequisitionFromDBByAccountingProfileId(Constants.accountingProfileIdForRequisitions.toString())!!

        commonPreconditions.createProfileDeliveryman(requisitionId = dbRequisition[ProfileRequisition.requestId])

        val declineRequest = employeePreconditions.fillDeclineRequisitionRequest()
        val error = employeeActions.declineProfileRequisitionWithError(dbRequisition[ProfileRequisition.requestId], declineRequest)

        employeeAssertion.checkErrorMessage(error.message, "Illegal Requisition Status")
    }

}