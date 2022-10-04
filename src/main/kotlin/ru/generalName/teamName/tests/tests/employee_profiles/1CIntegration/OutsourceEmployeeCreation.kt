package ru.samokat.mysamokat.tests.tests.employee_profiles.`1CIntegration`

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
class OutsourceEmployeeCreation {

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
    @DisplayName("Profile with accounting_profile_id not exists: create request")
    fun profileWithAccountingProfileIdNotExistsTest(){

        val event = employeePreconditions.fillOutsourseContractEvent(
            accountingProfileId = Constants.accountingProfileIdForRequisitions,
        )
        employeeActions.produceToVneshnieSotrudniki(event)

        val dbRequisition = employeeActions.getRequisitionFromDBByAccountingProfileId(Constants.accountingProfileIdForRequisitions.toString())!!
        val dbContractLog = employeeActions.getRequisitionLog(dbRequisition[ProfileRequisition.requestId])

        employeeAssertion.checkOutsourceRequisitionDB(event, dbRequisition)
        employeeAssertion.checkRequisitionLogVersion(dbContractLog)
    }

    @Test
    @Tags(Tag("kafka_produce"))
    @DisplayName("Profile with accounting_profile_id already exists: request not created")
    fun profileWithAccountingProfileIdExistsTest(){

        commonPreconditions.createProfileDeliveryman(
            accountingProfileId = Constants.accountingProfileIdForRequisitions.toString()
        )

        val event = employeePreconditions.fillOutsourseContractEvent(
            accountingProfileId = Constants.accountingProfileIdForRequisitions,
        )
        employeeActions.produceToVneshnieSotrudniki(event)

        val requisitionExistance = employeeActions.getRequisitionExistance(Constants.accountingProfileIdForRequisitions.toString())

        employeeAssertion.checkRequisitionNotExists(requisitionExistance)
    }

    @Test
    @Tags(Tag("kafka_produce"))
    @DisplayName("Profile with mobile exists: update accounting_profile_id")
    fun profileWithMobileExistsUpdateAccountingProfileIdTest(){

        val oldId = UUID.randomUUID().toString()
        val profileId = commonPreconditions.createProfileDeliveryman(
            accountingProfileId = oldId
        ).profileId

        val event = employeePreconditions.fillOutsourseContractEvent(
            accountingProfileId = Constants.accountingProfileIdForRequisitions,
        )
        employeeActions.produceToVneshnieSotrudniki(event)

        val dbRequisition = employeeActions.getRequisitionFromDBByAccountingProfileId(Constants.accountingProfileIdForRequisitions.toString())!!
        val profileFromDB = employeeActions.getProfileFromDB(profileId)

        employeeAssertion
            .checkOutsourceRequisitionDB(event, dbRequisition)
            .checkProfileAccountingIdInDatabase(oldId, profileFromDB)
    }

    @Test
    @Tags(Tag("kafka_produce"))
    @DisplayName("Profile with mobile exists: add accounting_profile_id")
    fun profileWithMobileExistsAddAccountingProfileIdTest(){

        val profileId = commonPreconditions.createProfileDeliveryman().profileId

        val event = employeePreconditions.fillOutsourseContractEvent(
            accountingProfileId = Constants.accountingProfileIdForRequisitions,
        )
        employeeActions.produceToVneshnieSotrudniki(event)

        val requisitionExistance = employeeActions.getRequisitionExistance(Constants.accountingProfileIdForRequisitions.toString())
        val profileFromDB = employeeActions.getProfileFromDB(profileId)

        employeeAssertion.checkRequisitionNotExists(requisitionExistance)
            .checkProfileAccountingIdInDatabase(Constants.accountingProfileIdForRequisitions.toString(), profileFromDB)
    }

    @Test
    @Tags(Tag("kafka_produce"), Tag("smoke"))
    @DisplayName("Profile with accounting_profile_id not exists, request already exists: new request not created")
    fun requestAlreadyExistsTest(){

        val event1 = employeePreconditions.fillOutsourseContractEvent(
            accountingProfileId = Constants.accountingProfileIdForRequisitions,
        )
        val event2 = employeePreconditions.fillOutsourseContractEvent(
            accountingProfileId = Constants.accountingProfileIdForRequisitions, mobile = Constants.mobile2.asStringWithoutPlus()
        )
        employeeActions.produceToVneshnieSotrudniki(event1)
        employeeActions.produceToVneshnieSotrudniki(event2)


        val dbRequisition = employeeActions.getRequisitionFromDBByAccountingProfileId(Constants.accountingProfileIdForRequisitions.toString())!!
        val dbContractLog = employeeActions.getRequisitionLog(dbRequisition[ProfileRequisition.requestId])

        employeeAssertion.checkOutsourceRequisitionDB(event1, dbRequisition)
        employeeAssertion.checkRequisitionLogVersion(dbContractLog)
    }

    @Test
    @Tags(Tag("kafka_produce"))
    @DisplayName("Request is processed, create new request")
    fun requestIsProcessedCreateNewRequestTest(){

        val event = employeePreconditions.fillOutsourseContractEvent(
            accountingProfileId = Constants.accountingProfileIdForRequisitions,
        )
        employeeActions.produceToVneshnieSotrudniki(event)

        val dbRequisition = employeeActions.getRequisitionFromDBByAccountingProfileId(Constants.accountingProfileIdForRequisitions.toString())!!

        val profileId = commonPreconditions.createProfileDeliveryman(requisitionId = dbRequisition[ProfileRequisition.requestId]).profileId
        employeeActions.deleteProfile(Constants.mobile1)

        employeeActions.produceToVneshnieSotrudniki(event)

        val dbRequisitionNew = employeeActions.getRequisitionFromDBByAccountingProfileIdAndStatus(Constants.accountingProfileIdForRequisitions.toString(), "NEW")!!
        val dbContractLog = employeeActions.getRequisitionLog(dbRequisitionNew[ProfileRequisition.requestId])

        employeeAssertion.checkOutsourceRequisitionDB(event, dbRequisitionNew, "NEW")
        employeeAssertion.checkRequisitionLogVersion(dbContractLog)
    }

    @Test
    @Tags(Tag("kafka_produce"))
    @DisplayName("Request is declined, create new request")
    fun requestIsDeclinedCreateNewRequestTest(){

        val event1 = employeePreconditions.fillOutsourseContractEvent(
            accountingProfileId = Constants.accountingProfileIdForRequisitions,
            mobile = Constants.mobile2.asStringWithoutPlus()
        )
        val declineRequest = employeePreconditions.fillDeclineRequisitionRequest()
        employeeActions.produceToVneshnieSotrudniki(event1)
        val dbRequisition = employeeActions.getRequisitionFromDBByAccountingProfileId(Constants.accountingProfileIdForRequisitions.toString())!!
        employeeActions.declineProfileRequisition(dbRequisition[ProfileRequisition.requestId], declineRequest)

        val event2 = employeePreconditions.fillOutsourseContractEvent(
            accountingProfileId = Constants.accountingProfileIdForRequisitions,
            mobile = Constants.mobile1.asStringWithoutPlus()
        )

        employeeActions.produceToVneshnieSotrudniki(event2)

        val dbRequisitionNew = employeeActions.getRequisitionFromDBByAccountingProfileIdAndStatus(Constants.accountingProfileIdForRequisitions.toString(), "NEW")!!
        val dbContractLog = employeeActions.getRequisitionLog(dbRequisitionNew[ProfileRequisition.requestId])

        employeeAssertion.checkOutsourceRequisitionDB(event2, dbRequisitionNew, "NEW")
        employeeAssertion.checkRequisitionLogVersion(dbContractLog)
    }

    @Test
    @Tags(Tag("kafka_produce"))
    @DisplayName("Request is processed, profile exists, update accounting profile id")
    fun requestIsProcessedUpdateAccountingProfileIdTest(){

        val event = employeePreconditions.fillOutsourseContractEvent(
            accountingProfileId = Constants.accountingProfileIdForRequisitions,
        )
        employeeActions.produceToVneshnieSotrudniki(event)

        val dbRequisition = employeeActions.getRequisitionFromDBByAccountingProfileId(Constants.accountingProfileIdForRequisitions.toString())!!

        val profileId = commonPreconditions.createProfileDeliveryman(requisitionId = dbRequisition[ProfileRequisition.requestId]).profileId
        val oldId = UUID.randomUUID().toString()
        employeeActions.updateAccountingProfileId(profileId, oldId)

        employeeActions.produceToVneshnieSotrudniki(event)

        val profileFromDB = employeeActions.getProfileFromDB(profileId)

        employeeAssertion
            .checkProfileAccountingIdInDatabase(oldId.toString(), profileFromDB)

    }

    @Test
    @Tags(Tag("kafka_produce"))
    @DisplayName("Request is declined, profile exists, update accounting profile id")
    fun requestIsDeclinedUpdateAccountingProfileIdTest(){

        val event = employeePreconditions.fillOutsourseContractEvent(
            accountingProfileId = Constants.accountingProfileIdForRequisitions,
        )
        val declineRequest = employeePreconditions.fillDeclineRequisitionRequest()

        employeeActions.produceToVneshnieSotrudniki(event)
        val dbRequisition = employeeActions.getRequisitionFromDBByAccountingProfileId(Constants.accountingProfileIdForRequisitions.toString())!!
        employeeActions.declineProfileRequisition(dbRequisition[ProfileRequisition.requestId], declineRequest)
        val profileId = commonPreconditions.createProfileDeliveryman().profileId

        employeeActions.produceToVneshnieSotrudniki(event)

        val profileFromDB = employeeActions.getProfileFromDB(profileId)

        employeeAssertion
            .checkProfileAccountingIdInDatabase(Constants.accountingProfileIdForRequisitions.toString(), profileFromDB)

    }

}