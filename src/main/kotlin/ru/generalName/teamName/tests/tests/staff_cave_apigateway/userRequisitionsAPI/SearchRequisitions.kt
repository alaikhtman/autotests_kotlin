package ru.samokat.mysamokat.tests.tests.staff_cave_apigateway.userRequisitionsAPI

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
import ru.samokat.mysamokat.tests.dataproviders.StringAndPhoneNumberGenerator
import ru.samokat.mysamokat.tests.dataproviders.apiModels.staffcave.requisitions.UserRequisitionStatus
import ru.samokat.mysamokat.tests.dataproviders.apiModels.staffcave.requisitions.UserRequisitionType
import ru.samokat.mysamokat.tests.dataproviders.helpers.database.employee_profiles_backend.ProfileRequisition
import ru.samokat.mysamokat.tests.dataproviders.preconditions.CommonPreconditions
import ru.samokat.mysamokat.tests.dataproviders.preconditions.EmployeePreconditions
import ru.samokat.mysamokat.tests.dataproviders.preconditions.StaffCaveApiGWPreconditions
import ru.samokat.mysamokat.tests.helpers.actions.EmployeeActions
import ru.samokat.mysamokat.tests.helpers.actions.StaffCaveApiGWActions


@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Tags(Tag("staff-cave-apigateway"))
class SearchRequisitions {

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
        employeeActions.deleteProfileByAccountingProfileId(Constants.accountingProfileIdForRequisitions2.toString())
        employeeActions.deleteProfileByAccountingProfileId(Constants.innForRequisitions)
        employeeActions.deleteRequisitionFromDatabase(Constants.accountingProfileIdForRequisitions.toString())
        employeeActions.deleteRequisitionFromDatabase(Constants.accountingProfileIdForRequisitions2.toString())
        employeeActions.deleteRequisitionFromDatabase(Constants.innForRequisitions)
        employeeActions.deleteContractFromDatabase(Constants.accountingProfileIdForRequisitions.toString())
        employeeActions.deleteContractFromDatabase(Constants.accountingProfileIdForRequisitions2.toString())
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
        employeeActions.deleteProfileByAccountingProfileId(Constants.accountingProfileIdForRequisitions2.toString())
        employeeActions.deleteProfileByAccountingProfileId(Constants.innForRequisitions)
        employeeActions.deleteRequisitionFromDatabase(Constants.accountingProfileIdForRequisitions.toString())
        employeeActions.deleteRequisitionFromDatabase(Constants.accountingProfileIdForRequisitions2.toString())
        employeeActions.deleteRequisitionFromDatabase(Constants.innForRequisitions)
        employeeActions.deleteContractFromDatabase(Constants.accountingProfileIdForRequisitions.toString())
        employeeActions.deleteContractFromDatabase(Constants.accountingProfileIdForRequisitions2.toString())
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
    @Tags(Tag("smoke"), Tag("kafka_produce"))
    @DisplayName("Search requisition: outsources")
    fun searchRequisitionsOutsourceTest() {

        val eventOutsource = employeePreconditions.fillOutsourseContractEvent(
            accountingProfileId = Constants.accountingProfileIdForRequisitions,
            mobile = Constants.mobile2.asStringWithoutPlus()
        )
        val eventInnersource = employeePreconditions.fillPriemNaRabotyEvent(
            accountingProfileId = Constants.accountingProfileIdForRequisitions,
            inn = Constants.innForRequisitions
        )
        employeeActions.produceToPriemNaRabotuCFZ(eventInnersource)
        employeeActions.produceToVneshnieSotrudniki(eventOutsource)
        val searchRequisitionsRequests = scPreconditions.fillSearchRequisitionsRequest(
            types = listOf(ApiEnum(UserRequisitionType.OUTSOURCE)),
            pageSize = 500
        )

        val requisitionIdOursource =
            employeeActions.getRequisitionFromDBByAccountingProfileId(Constants.accountingProfileIdForRequisitions.toString())!![ProfileRequisition.requestId]
        val requisitionIdInnersource =
            employeeActions.getRequisitionFromDBByAccountingProfileId(Constants.innForRequisitions)!![ProfileRequisition.requestId]


        val requisitions = scActions.searchRequisitions(token, searchRequisitionsRequests)!!

        scAssertion
            .checkRequisitionPresentInList(requisitions, requisitionIdOursource)
            .checkRequisitionNotPresentInList(requisitions, requisitionIdInnersource)
            .checkOutsourceRequisitionInList(requisitions, requisitionIdOursource, eventOutsource)
    }

    @Test
    @Tags(Tag("smoke"), Tag("kafka_produce"))
    @DisplayName("Search requisition: innersource (by staff-manager)")
    fun searchRequisitionsInnersourceTest() {
        getAuthToken(EmployeeRole.STAFF_MANAGER)
        val eventOutsource = employeePreconditions.fillOutsourseContractEvent(
            accountingProfileId = Constants.accountingProfileIdForRequisitions,
            mobile = Constants.mobile2.asStringWithoutPlus()
        )
        val eventInnersource = employeePreconditions.fillPriemNaRabotyEvent(
            accountingProfileId = Constants.accountingProfileIdForRequisitions,
            inn = Constants.innForRequisitions
        )
        employeeActions.produceToPriemNaRabotuCFZ(eventInnersource)
        employeeActions.produceToVneshnieSotrudniki(eventOutsource)
        val searchRequisitionsRequests = scPreconditions.fillSearchRequisitionsRequest(
            types = listOf(ApiEnum(UserRequisitionType.INNER_SOURCE)),
            pageSize = 500
        )

        val requisitionIdOursource =
            employeeActions.getRequisitionFromDBByAccountingProfileId(Constants.accountingProfileIdForRequisitions.toString())!![ProfileRequisition.requestId]
        val requisitionIdInnersource =
            employeeActions.getRequisitionFromDBByAccountingProfileId(Constants.innForRequisitions)!![ProfileRequisition.requestId]


        val requisitions = scActions.searchRequisitions(token, searchRequisitionsRequests)!!

        scAssertion
            .checkRequisitionNotPresentInList(requisitions, requisitionIdOursource)
            .checkRequisitionPresentInList(requisitions, requisitionIdInnersource)
            .checkInnersourceRequisitionInList(requisitions, requisitionIdInnersource, eventInnersource)
    }

    @Test
    @Tags(Tag("smoke"), Tag("kafka_produce"))
    @DisplayName("Search requisition: all types")
    fun searchRequisitionsAllTypesTest() {

        val eventOutsource = employeePreconditions.fillOutsourseContractEvent(
            accountingProfileId = Constants.accountingProfileIdForRequisitions,
            mobile = Constants.mobile2.asStringWithoutPlus()
        )
        val eventInnersource = employeePreconditions.fillPriemNaRabotyEvent(
            accountingProfileId = Constants.accountingProfileIdForRequisitions,
            inn = Constants.innForRequisitions
        )
        employeeActions.produceToPriemNaRabotuCFZ(eventInnersource)
        employeeActions.produceToVneshnieSotrudniki(eventOutsource)
        val searchRequisitionsRequests = scPreconditions.fillSearchRequisitionsRequest(
            types = listOf(ApiEnum(UserRequisitionType.INNER_SOURCE), ApiEnum(UserRequisitionType.OUTSOURCE)),
            pageSize = 500
        )

        val requisitionIdOursource =
            employeeActions.getRequisitionFromDBByAccountingProfileId(Constants.accountingProfileIdForRequisitions.toString())!![ProfileRequisition.requestId]
        val requisitionIdInnersource =
            employeeActions.getRequisitionFromDBByAccountingProfileId(Constants.innForRequisitions)!![ProfileRequisition.requestId]


        val requisitions = scActions.searchRequisitions(token, searchRequisitionsRequests)!!

        scAssertion
            .checkRequisitionPresentInList(requisitions, requisitionIdOursource)
            .checkRequisitionPresentInList(requisitions, requisitionIdInnersource)
    }

    @Test
    @Tags(Tag("smoke"), Tag("kafka_produce"))
    @DisplayName("Search requisition: by full mobile")
    fun searchRequisitionsByFullMobileTest() {

        val eventOutsource1 = employeePreconditions.fillOutsourseContractEvent(
            accountingProfileId = Constants.accountingProfileIdForRequisitions,
            mobile = Constants.mobile2.asStringWithoutPlus()
        )
        val eventOutsource2 = employeePreconditions.fillOutsourseContractEvent(
            accountingProfileId = Constants.accountingProfileIdForRequisitions2,
            mobile = Constants.mobile3.asStringWithoutPlus()
        )

        employeeActions.produceToVneshnieSotrudniki(eventOutsource1)
        employeeActions.produceToVneshnieSotrudniki(eventOutsource2)

        val searchRequisitionsRequests = scPreconditions.fillSearchRequisitionsRequest(
            mobile = Constants.mobile2.asStringWithoutPlus(), pageSize = 500, statuses = listOf(ApiEnum(UserRequisitionStatus.NEW))
        )

        val requisitionId1 =
            employeeActions.getRequisitionFromDBByAccountingProfileId(Constants.accountingProfileIdForRequisitions.toString())!![ProfileRequisition.requestId]
        val requisitionId2 =
            employeeActions.getRequisitionFromDBByAccountingProfileId(Constants.accountingProfileIdForRequisitions2.toString())!![ProfileRequisition.requestId]


        val requisitions = scActions.searchRequisitions(token, searchRequisitionsRequests)!!

        scAssertion
            .checkRequisitionPresentInList(requisitions, requisitionId1)
            .checkRequisitionNotPresentInList(requisitions, requisitionId2)
    }

    @Test
    @Tags(Tag("smoke"), Tag("kafka_produce"))
    @DisplayName("Search requisition: by mobile with +7")
    fun searchRequisitionsByMobileWithSevenTest() {

        val eventOutsource1 = employeePreconditions.fillOutsourseContractEvent(
            accountingProfileId = Constants.accountingProfileIdForRequisitions,
            mobile = Constants.mobile2.asStringWithoutPlus()
        )
        val eventOutsource2 = employeePreconditions.fillOutsourseContractEvent(
            accountingProfileId = Constants.accountingProfileIdForRequisitions2,
            mobile = Constants.mobile3.asStringWithoutPlus()
        )

        employeeActions.produceToVneshnieSotrudniki(eventOutsource1)
        employeeActions.produceToVneshnieSotrudniki(eventOutsource2)

        val searchRequisitionsRequests = scPreconditions.fillSearchRequisitionsRequest(
            mobile = Constants.mobile2.asStringWithPlus(), pageSize = 500, statuses = listOf(ApiEnum(UserRequisitionStatus.NEW))
        )

        val requisitionId1 =
            employeeActions.getRequisitionFromDBByAccountingProfileId(Constants.accountingProfileIdForRequisitions.toString())!![ProfileRequisition.requestId]
        val requisitionId2 =
            employeeActions.getRequisitionFromDBByAccountingProfileId(Constants.accountingProfileIdForRequisitions2.toString())!![ProfileRequisition.requestId]


        val requisitions = scActions.searchRequisitions(token, searchRequisitionsRequests)!!

        scAssertion
            .checkRequisitionPresentInList(requisitions, requisitionId1)
            .checkRequisitionNotPresentInList(requisitions, requisitionId2)
    }

    @Test
    @Tags(Tag("smoke"), Tag("kafka_produce"))
    @DisplayName("Search requisition: by part mobile")
    fun searchRequisitionsByPartMobileTest() {

        val eventOutsource1 = employeePreconditions.fillOutsourseContractEvent(
            accountingProfileId = Constants.accountingProfileIdForRequisitions,
            mobile = Constants.mobile2.asStringWithoutPlus()
        )
        val eventOutsource2 = employeePreconditions.fillOutsourseContractEvent(
            accountingProfileId = Constants.accountingProfileIdForRequisitions2,
            mobile = Constants.mobile3.asStringWithoutPlus()
        )

        employeeActions.produceToVneshnieSotrudniki(eventOutsource1)
        employeeActions.produceToVneshnieSotrudniki(eventOutsource2)

        val searchRequisitionsRequests = scPreconditions.fillSearchRequisitionsRequest(
            mobile = Constants.searchMobilePart, pageSize = 500, statuses = listOf(ApiEnum(UserRequisitionStatus.NEW))
        )

        val requisitionId1 =
            employeeActions.getRequisitionFromDBByAccountingProfileId(Constants.accountingProfileIdForRequisitions.toString())!![ProfileRequisition.requestId]
        val requisitionId2 =
            employeeActions.getRequisitionFromDBByAccountingProfileId(Constants.accountingProfileIdForRequisitions2.toString())!![ProfileRequisition.requestId]


        val requisitions = scActions.searchRequisitions(token, searchRequisitionsRequests)!!

        scAssertion
            .checkRequisitionPresentInList(requisitions, requisitionId1)
            .checkRequisitionPresentInList(requisitions, requisitionId2)
    }

    @Test
    @Tags(Tag("smoke"), Tag("kafka_produce"))
    @DisplayName("Search requisition: by full name")
    fun searchRequisitionsByFullNameTest() {

        val eventOutsource1 = employeePreconditions.fillOutsourseContractEvent(
            accountingProfileId = Constants.accountingProfileIdForRequisitions,
            mobile = Constants.mobile2.asStringWithoutPlus(),
            naimenovanie = "Иванов Иван Петрович",
        )
        val eventOutsource2 = employeePreconditions.fillOutsourseContractEvent(
            accountingProfileId = Constants.accountingProfileIdForRequisitions2,
            mobile = Constants.mobile3.asStringWithoutPlus(),
            naimenovanie = "Иванов Иван Сергеевич",
        )

        employeeActions.produceToVneshnieSotrudniki(eventOutsource1)
        employeeActions.produceToVneshnieSotrudniki(eventOutsource2)

        val searchRequisitionsRequests = scPreconditions.fillSearchRequisitionsRequest(
            name = "Иванов Иван Петрович"
        )

        val requisitionId1 =
            employeeActions.getRequisitionFromDBByAccountingProfileId(Constants.accountingProfileIdForRequisitions.toString())!![ProfileRequisition.requestId]
        val requisitionId2 =
            employeeActions.getRequisitionFromDBByAccountingProfileId(Constants.accountingProfileIdForRequisitions2.toString())!![ProfileRequisition.requestId]


        val requisitions = scActions.searchRequisitions(token, searchRequisitionsRequests)!!

        scAssertion
            .checkRequisitionPresentInList(requisitions, requisitionId1)
            .checkRequisitionNotPresentInList(requisitions, requisitionId2)
    }

    @Test
    @Tags(Tag("smoke"), Tag("kafka_produce"))
    @DisplayName("Search requisition: by part name")
    fun searchRequisitionsByPartNameTest() {

        val eventOutsource1 = employeePreconditions.fillOutsourseContractEvent(
            accountingProfileId = Constants.accountingProfileIdForRequisitions,
            mobile = Constants.mobile2.asStringWithoutPlus(),
            naimenovanie = "Иванов Иван Петрович",
        )
        val eventOutsource2 = employeePreconditions.fillOutsourseContractEvent(
            accountingProfileId = Constants.accountingProfileIdForRequisitions2,
            mobile = Constants.mobile3.asStringWithoutPlus(),
            naimenovanie = "Иванов Иван Сергеевич",
        )

        employeeActions.produceToVneshnieSotrudniki(eventOutsource1)
        employeeActions.produceToVneshnieSotrudniki(eventOutsource2)

        val searchRequisitionsRequests = scPreconditions.fillSearchRequisitionsRequest(
            name = "Иванов Иван"
        )

        val requisitionId1 =
            employeeActions.getRequisitionFromDBByAccountingProfileId(Constants.accountingProfileIdForRequisitions.toString())!![ProfileRequisition.requestId]
        val requisitionId2 =
            employeeActions.getRequisitionFromDBByAccountingProfileId(Constants.accountingProfileIdForRequisitions2.toString())!![ProfileRequisition.requestId]


        val requisitions = scActions.searchRequisitions(token, searchRequisitionsRequests)!!

        scAssertion
            .checkRequisitionPresentInList(requisitions, requisitionId1)
            .checkRequisitionPresentInList(requisitions, requisitionId2)
    }

    @Test
    @Tags(Tag("smoke"), Tag("kafka_produce"))
    @DisplayName("Search requisition: no search results")
    fun searchRequisitionsNoSearchResultsTest() {

        val searchRequisitionsRequests = scPreconditions.fillSearchRequisitionsRequest(
            name = StringAndPhoneNumberGenerator.generateRandomString(10)
        )

        val requisitions = scActions.searchRequisitions(token, searchRequisitionsRequests)!!

        scAssertion
            .checkRequisitionsListCount(requisitions, 0)
    }

    @Test
    @Tags(Tag("smoke"), Tag("kafka_produce"))
    @DisplayName("Search requisition: by status new")
    fun searchRequisitionsByStatusNewTest() {

        val eventOutsource1 = employeePreconditions.fillOutsourseContractEvent(
            accountingProfileId = Constants.accountingProfileIdForRequisitions,
            mobile = Constants.mobile2.asStringWithoutPlus()
        )
        val eventOutsource2 = employeePreconditions.fillOutsourseContractEvent(
            accountingProfileId = Constants.accountingProfileIdForRequisitions2,
            mobile = Constants.mobile3.asStringWithoutPlus()
        )
        val declineRequest = employeePreconditions.fillDeclineRequisitionRequest()

        employeeActions.produceToVneshnieSotrudniki(eventOutsource1)
        employeeActions.produceToVneshnieSotrudniki(eventOutsource2)

        val searchRequisitionsRequests = scPreconditions.fillSearchRequisitionsRequest(
            mobile = Constants.searchMobilePart, pageSize = 500, statuses = listOf(ApiEnum(UserRequisitionStatus.NEW))
        )

        val requisitionId1 =
            employeeActions.getRequisitionFromDBByAccountingProfileId(Constants.accountingProfileIdForRequisitions.toString())!![ProfileRequisition.requestId]
        val requisitionId2 =
            employeeActions.getRequisitionFromDBByAccountingProfileId(Constants.accountingProfileIdForRequisitions2.toString())!![ProfileRequisition.requestId]
        employeeActions.declineProfileRequisition(requisitionId1, declineRequest)

        val requisitions = scActions.searchRequisitions(token, searchRequisitionsRequests)!!

        scAssertion
            .checkRequisitionNotPresentInList(requisitions, requisitionId1)
            .checkRequisitionPresentInList(requisitions, requisitionId2)
    }

    @Test
    @Tags(Tag("smoke"), Tag("kafka_produce"))
    @DisplayName("Search requisition: by status declined")
    fun searchRequisitionsByStatusDeclinedTest() {

        val eventOutsource1 = employeePreconditions.fillOutsourseContractEvent(
            accountingProfileId = Constants.accountingProfileIdForRequisitions,
            mobile = Constants.mobile2.asStringWithoutPlus()
        )
        val eventOutsource2 = employeePreconditions.fillOutsourseContractEvent(
            accountingProfileId = Constants.accountingProfileIdForRequisitions2,
            mobile = Constants.mobile3.asStringWithoutPlus()
        )
        val declineRequest = employeePreconditions.fillDeclineRequisitionRequest()

        employeeActions.produceToVneshnieSotrudniki(eventOutsource1)
        employeeActions.produceToVneshnieSotrudniki(eventOutsource2)

        val searchRequisitionsRequests = scPreconditions.fillSearchRequisitionsRequest(
            mobile = Constants.searchMobilePart, pageSize = 500, statuses = listOf(ApiEnum(UserRequisitionStatus.DECLINED))
        )

        val requisitionId1 =
            employeeActions.getRequisitionFromDBByAccountingProfileId(Constants.accountingProfileIdForRequisitions.toString())!![ProfileRequisition.requestId]
        val requisitionId2 =
            employeeActions.getRequisitionFromDBByAccountingProfileId(Constants.accountingProfileIdForRequisitions2.toString())!![ProfileRequisition.requestId]
        employeeActions.declineProfileRequisition(requisitionId1, declineRequest)

        val requisitions = scActions.searchRequisitions(token, searchRequisitionsRequests)!!

        scAssertion
            .checkRequisitionPresentInList(requisitions, requisitionId1)
            .checkRequisitionNotPresentInList(requisitions, requisitionId2)
    }

    @Test
    @Tags(Tag("smoke"), Tag("kafka_produce"))
    @DisplayName("Search requisition: by status processed")
    fun searchRequisitionsByStatusProcessedTest() {

        val eventOutsource1 = employeePreconditions.fillOutsourseContractEvent(
            accountingProfileId = Constants.accountingProfileIdForRequisitions,
            mobile = Constants.mobile2.asStringWithoutPlus()
        )
        val eventOutsource2 = employeePreconditions.fillOutsourseContractEvent(
            accountingProfileId = Constants.accountingProfileIdForRequisitions2,
            mobile = Constants.mobile3.asStringWithoutPlus()
        )

        employeeActions.produceToVneshnieSotrudniki(eventOutsource1)
        employeeActions.produceToVneshnieSotrudniki(eventOutsource2)

        val searchRequisitionsRequests = scPreconditions.fillSearchRequisitionsRequest(
            mobile = Constants.searchMobilePart, pageSize = 500, statuses = listOf(ApiEnum(UserRequisitionStatus.PROCESSED))
        )

        val requisitionId1 =
            employeeActions.getRequisitionFromDBByAccountingProfileId(Constants.accountingProfileIdForRequisitions.toString())!![ProfileRequisition.requestId]
        val requisitionId2 =
            employeeActions.getRequisitionFromDBByAccountingProfileId(Constants.accountingProfileIdForRequisitions2.toString())!![ProfileRequisition.requestId]
        commonPreconditions.createProfileDeliveryman(
            requisitionId = requisitionId1,
            mobile = Constants.mobile2
        ).profileId

        val requisitions = scActions.searchRequisitions(token, searchRequisitionsRequests)!!

        scAssertion
            .checkRequisitionPresentInList(requisitions, requisitionId1)
            .checkRequisitionNotPresentInList(requisitions, requisitionId2)
    }
}