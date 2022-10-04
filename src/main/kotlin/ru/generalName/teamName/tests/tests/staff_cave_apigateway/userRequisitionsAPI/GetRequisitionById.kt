package ru.samokat.mysamokat.tests.tests.staff_cave_apigateway.userRequisitionsAPI

import org.apache.http.HttpStatus
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.samokat.employeeprofiles.api.common.domain.EmployeeVehicleType
import ru.samokat.employeeprofiles.api.common.domain.Vehicle
import ru.samokat.my.domain.employee.EmployeeRole
import ru.samokat.my.rest.api.enum.ApiEnum
import ru.samokat.mysamokat.tests.checkers.CommonAssertion
import ru.samokat.mysamokat.tests.checkers.StaffCaveApiGWAssertions
import ru.samokat.mysamokat.tests.dataproviders.Constants
import ru.samokat.mysamokat.tests.dataproviders.apiModels.staffcave.requisitions.UserRequisitionStatus
import ru.samokat.mysamokat.tests.dataproviders.helpers.database.employee_profiles_backend.ProfileRequisition
import ru.samokat.mysamokat.tests.dataproviders.preconditions.CommonPreconditions
import ru.samokat.mysamokat.tests.dataproviders.preconditions.EmployeePreconditions
import ru.samokat.mysamokat.tests.dataproviders.preconditions.StaffCaveApiGWPreconditions
import ru.samokat.mysamokat.tests.helpers.actions.EmployeeActions
import ru.samokat.mysamokat.tests.helpers.actions.StaffCaveApiGWActions
import java.util.*

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Tags(Tag("staff-cave-apigateway"))
class GetRequisitionById {

    @Autowired
    private lateinit var scActions: StaffCaveApiGWActions

    @Autowired
    private lateinit var employeeActions: EmployeeActions

    @Autowired
    private lateinit var commonPreconditions: CommonPreconditions

    private lateinit var commonAssertions: CommonAssertion

    private lateinit var scPreconditions: StaffCaveApiGWPreconditions

    private lateinit var scAssertion: StaffCaveApiGWAssertions

    private lateinit var token: String

    private lateinit var employeePreconditions: EmployeePreconditions

    @BeforeEach
    fun before() {
        scPreconditions = StaffCaveApiGWPreconditions()
        employeePreconditions = EmployeePreconditions()
        scAssertion = StaffCaveApiGWAssertions()
        commonAssertions = CommonAssertion()
        employeeActions.deleteProfile(Constants.mobile1)
        employeeActions.deleteProfile(Constants.mobile2)
        employeeActions.deleteProfileByAccountingProfileId(Constants.accountingProfileIdForRequisitions.toString())
        employeeActions.deleteRequisitionFromDatabase(Constants.accountingProfileIdForRequisitions.toString())
        employeeActions.deleteContractFromDatabase(Constants.accountingProfileIdForRequisitions.toString())
        employeeActions.deleteContractFromDatabase(Constants.innForRequisitions)
        getAuthToken()
    }

    @AfterEach
    fun release() {
        scAssertion.assertAll()
        commonAssertions.assertAll()
        employeeActions.deleteProfile(Constants.mobile1)
        employeeActions.deleteProfile(Constants.mobile2)
        employeeActions.deleteProfileByAccountingProfileId(Constants.accountingProfileIdForRequisitions.toString())
        employeeActions.deleteRequisitionFromDatabase(Constants.accountingProfileIdForRequisitions.toString())
        employeeActions.deleteContractFromDatabase(Constants.accountingProfileIdForRequisitions.toString())
        employeeActions.deleteContractFromDatabase(Constants.innForRequisitions)
    }

    fun getAuthToken(role: EmployeeRole = EmployeeRole.TECH_SUPPORT) {
        employeeActions.deleteProfile(Constants.mobile1)
        val profile = commonPreconditions.createProfile(
            roles = listOf(ApiEnum(role))
        )
        val authRequest = scPreconditions.fillAuthRequest(password = profile.generatedPassword!!)
        token = scActions.authProfilePassword(authRequest).accessToken

    }

    @Test
    @Tags(Tag("kafka_produce"), Tag("smoke"))
    @DisplayName("Get requisition: outsource (new status)")
    fun getNewOutSourceRequisition() {

        val event = employeePreconditions.fillOutsourseContractEvent(
            accountingProfileId = Constants.accountingProfileIdForRequisitions,
            mobile = Constants.mobile2.asStringWithoutPlus()
        )
        employeeActions.produceToVneshnieSotrudniki(event)

        val requisitionId = employeeActions.getRequisitionFromDBByAccountingProfileId(Constants.accountingProfileIdForRequisitions.toString())!![ProfileRequisition.requestId]

        val requisition = scActions.getRequisitionById(token, requisitionId)!!

        scAssertion
            .checkOutsourceRequisition(requisition, event)
    }

    @Test
    @Tags(Tag("kafka_produce"), Tag("smoke"))
    @DisplayName("Get requisition: innersource (new status) by staff-manager")
    fun getNewInnerSourceRequisition() {
        getAuthToken(EmployeeRole.STAFF_MANAGER)
        val event = employeePreconditions.fillPriemNaRabotyEvent(
            accountingProfileId = Constants.accountingProfileIdForRequisitions,
            inn = Constants.innForRequisitions
        )
        employeeActions.produceToPriemNaRabotuCFZ(event)

        val requisitionId = employeeActions.getRequisitionFromDBByAccountingProfileId(Constants.innForRequisitions)!![ProfileRequisition.requestId]

        val requisition = scActions.getRequisitionById(token, requisitionId)!!

        scAssertion
            .checkInnersourceRequisition(requisition, event)
    }


    @Test
    @Tags(Tag("kafka_produce"))
    @DisplayName("Get requisition: decline status")
    fun getDeclinedRequisition() {

        val event = employeePreconditions.fillOutsourseContractEvent(
            accountingProfileId = Constants.accountingProfileIdForRequisitions,
            mobile = Constants.mobile2.asStringWithoutPlus()
        )
        val declineRequest = employeePreconditions.fillDeclineRequisitionRequest()
        employeeActions.produceToVneshnieSotrudniki(event)

        val requisitionId = employeeActions.getRequisitionFromDBByAccountingProfileId(Constants.accountingProfileIdForRequisitions.toString())!![ProfileRequisition.requestId]
        employeeActions.declineProfileRequisition(requisitionId, declineRequest)

        val requisition = scActions.getRequisitionById(token, requisitionId)!!

        scAssertion
            .checkOutsourceRequisition(requisition, event, ApiEnum(UserRequisitionStatus.DECLINED))
    }

    @Test
    @Tags(Tag("kafka_produce"))
    @DisplayName("Get requisition: processed status")
    fun getProcessedRequisition() {

        val event = employeePreconditions.fillOutsourseContractEvent(
            accountingProfileId = Constants.accountingProfileIdForRequisitions,
            mobile = Constants.mobile2.asStringWithoutPlus()
        )
        employeeActions.produceToVneshnieSotrudniki(event)

        val requisitionId = employeeActions.getRequisitionFromDBByAccountingProfileId(Constants.accountingProfileIdForRequisitions.toString())!![ProfileRequisition.requestId]
        commonPreconditions.createProfileDeliveryman(
            requisitionId = requisitionId,
            mobile = Constants.mobile2
        ).profileId

        val requisition = scActions.getRequisitionById(token, requisitionId)!!

        scAssertion
            .checkOutsourceRequisition(requisition, event, ApiEnum(UserRequisitionStatus.PROCESSED))
    }

    @Test
    @Tags(Tag("kafka_produce"))
    @DisplayName("Get requisition: not exist")
    fun getNotExistRequisition() {
        scActions.getRequisitionByIdWithError(token, UUID.randomUUID(), HttpStatus.SC_NOT_FOUND)!!

    }
}