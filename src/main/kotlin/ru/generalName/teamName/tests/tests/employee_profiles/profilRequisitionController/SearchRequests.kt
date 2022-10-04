package ru.samokat.mysamokat.tests.tests.employee_profiles.profilRequisitionController

import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.samokat.employeeprofiles.api.common.domain.EmployeeVehicleType
import ru.samokat.employeeprofiles.api.common.domain.Vehicle
import ru.samokat.employeeprofiles.api.common.filtering.MobileQueryFilter
import ru.samokat.employeeprofiles.api.common.paging.PagingFilter
import ru.samokat.employeeprofiles.api.profilerequisitions.domain.ProfileRequisitionStatus
import ru.samokat.employeeprofiles.api.profilerequisitions.domain.ProfileRequisitionType
import ru.samokat.my.domain.PhoneNumber
import ru.samokat.my.rest.api.enum.ApiEnum
import ru.samokat.mysamokat.tests.checkers.EmployeeAssertion
import ru.samokat.mysamokat.tests.dataproviders.Constants
import ru.samokat.mysamokat.tests.dataproviders.StringAndPhoneNumberGenerator
import ru.samokat.mysamokat.tests.dataproviders.helpers.database.employee_profiles_backend.ProfileRequisition
import ru.samokat.mysamokat.tests.dataproviders.preconditions.CommonPreconditions
import ru.samokat.mysamokat.tests.dataproviders.preconditions.EmployeePreconditions
import ru.samokat.mysamokat.tests.helpers.actions.EmployeeActions

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Tag("empro")
class SearchRequests {

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
        employeeActions.deleteProfile(Constants.mobile2)
        employeeActions.deleteProfileByAccountingProfileId(Constants.accountingProfileIdForRequisitions.toString())
        employeeActions.deleteProfileByAccountingProfileId(Constants.accountingProfileIdForRequisitions2.toString())
        employeeActions.deleteProfileByAccountingProfileId(Constants.accountingProfileIdForRequisitions3.toString())
        employeeActions.deleteRequisitionFromDatabase(Constants.accountingProfileIdForRequisitions.toString())
        employeeActions.deleteRequisitionFromDatabase(Constants.accountingProfileIdForRequisitions2.toString())
        employeeActions.deleteRequisitionFromDatabase(Constants.accountingProfileIdForRequisitions3.toString())
        employeeActions.deleteRequisitionFromDatabaseByMobile(Constants.mobile1.asStringWithoutPlus())
    }

    @AfterEach
    fun release() {
        employeeAssertion.assertAll()
        employeeActions.deleteProfile(Constants.mobile1)
        employeeActions.deleteProfile(Constants.mobile2)
        employeeActions.deleteProfileByAccountingProfileId(Constants.accountingProfileIdForRequisitions.toString())
        employeeActions.deleteProfileByAccountingProfileId(Constants.accountingProfileIdForRequisitions2.toString())
        employeeActions.deleteProfileByAccountingProfileId(Constants.accountingProfileIdForRequisitions3.toString())
        employeeActions.deleteRequisitionFromDatabase(Constants.accountingProfileIdForRequisitions.toString())
        employeeActions.deleteRequisitionFromDatabase(Constants.accountingProfileIdForRequisitions2.toString())
        employeeActions.deleteRequisitionFromDatabase(Constants.accountingProfileIdForRequisitions3.toString())

    }

    @Test
    @Tags(Tag("kafka_produce"), Tag("smoke"))
    @DisplayName("Search requests: by full name")
    fun searchRequestsByFullNameTest() {

        val event = employeePreconditions.fillOutsourseContractEvent(
            accountingProfileId = Constants.accountingProfileIdForRequisitions,
            naimenovanie = "Соколов Сергей Семёнович"
        )
        val event2 = employeePreconditions.fillOutsourseContractEvent(
            accountingProfileId = Constants.accountingProfileIdForRequisitions2,
            mobile = Constants.mobile2.asStringWithoutPlus(),
            naimenovanie = "Петров Петр Петрович"
        )
        val searchRequest = employeePreconditions.fillSearchRequisitionsRequest(nameLike = "Соколов Сергей Семёнович")
        employeeActions.produceToVneshnieSotrudniki(event)
        employeeActions.produceToVneshnieSotrudniki(event2)

        val requestId1 =
            employeeActions.getRequisitionFromDBByAccountingProfileId(Constants.accountingProfileIdForRequisitions.toString())!![ProfileRequisition.requestId]
        val requestId2 =
            employeeActions.getRequisitionFromDBByAccountingProfileId(Constants.accountingProfileIdForRequisitions2.toString())!![ProfileRequisition.requestId]

        val requests = employeeActions.searchRequisitions(searchRequest)

        employeeAssertion
            .checkRequisitionPresentInList(requests, requestId1)
            .checkRequisitionNotPresentInList(requests, requestId2)
            .checkRequisitionsFieldsInList(requests, requestId1, event)
    }

    @Test
    @Tags(Tag("kafka_produce"), Tag("smoke"))
    @DisplayName("Search requests: by full mobile")
    fun searchRequestsByFullMobileTest() {

        val event = employeePreconditions.fillOutsourseContractEvent(
            accountingProfileId = Constants.accountingProfileIdForRequisitions
        )
        val event2 = employeePreconditions.fillOutsourseContractEvent(
            accountingProfileId = Constants.accountingProfileIdForRequisitions2,
            mobile = Constants.mobile2.asStringWithoutPlus()
        )
        val searchRequest =
            employeePreconditions.fillSearchRequisitionsRequest(mobile = MobileQueryFilter.Exact(Constants.mobile1))
        employeeActions.produceToVneshnieSotrudniki(event)
        employeeActions.produceToVneshnieSotrudniki(event2)

        val requestId1 =
            employeeActions.getRequisitionFromDBByAccountingProfileId(Constants.accountingProfileIdForRequisitions.toString())!![ProfileRequisition.requestId]
        val requestId2 =
            employeeActions.getRequisitionFromDBByAccountingProfileId(Constants.accountingProfileIdForRequisitions2.toString())!![ProfileRequisition.requestId]

        val requests = employeeActions.searchRequisitions(searchRequest)

        employeeAssertion
            .checkRequisitionPresentInList(requests, requestId1)
            .checkRequisitionNotPresentInList(requests, requestId2)
            .checkRequisitionsFieldsInList(requests, requestId1, event)
    }

    @Test
    @Tags(Tag("kafka_produce"))
    @DisplayName("Search requests: by part name")
    fun searchRequestsByPartNameTest() {

        val event = employeePreconditions.fillOutsourseContractEvent(
            accountingProfileId = Constants.accountingProfileIdForRequisitions,
            naimenovanie = "Соколов Сергей Семёнович"
        )
        val event2 = employeePreconditions.fillOutsourseContractEvent(
            accountingProfileId = Constants.accountingProfileIdForRequisitions2,
            mobile = Constants.mobile2.asStringWithoutPlus(),
            naimenovanie = "Петров Петр Петрович"
        )
        val searchRequest = employeePreconditions.fillSearchRequisitionsRequest(nameLike = "Сергей Се")
        employeeActions.produceToVneshnieSotrudniki(event)
        employeeActions.produceToVneshnieSotrudniki(event2)

        val requestId1 =
            employeeActions.getRequisitionFromDBByAccountingProfileId(Constants.accountingProfileIdForRequisitions.toString())!![ProfileRequisition.requestId]
        val requestId2 =
            employeeActions.getRequisitionFromDBByAccountingProfileId(Constants.accountingProfileIdForRequisitions2.toString())!![ProfileRequisition.requestId]

        val requests = employeeActions.searchRequisitions(searchRequest)

        employeeAssertion
            .checkRequisitionPresentInList(requests, requestId1)
            .checkRequisitionNotPresentInList(requests, requestId2)
            .checkRequisitionsFieldsInList(requests, requestId1, event)
    }

    @Test
    @Tags(Tag("kafka_produce"))
    @DisplayName("Search requests: by part mobile")
    fun searchRequestsByPartMobileTest() {

        val event = employeePreconditions.fillOutsourseContractEvent(
            accountingProfileId = Constants.accountingProfileIdForRequisitions
        )
        val event2 = employeePreconditions.fillOutsourseContractEvent(
            accountingProfileId = Constants.accountingProfileIdForRequisitions2,
            mobile = Constants.mobile2.asStringWithoutPlus()
        )
        val searchRequest =
            employeePreconditions.fillSearchRequisitionsRequest(mobile = MobileQueryFilter.Like("700099977"))
        employeeActions.produceToVneshnieSotrudniki(event)
        employeeActions.produceToVneshnieSotrudniki(event2)

        val requestId1 =
            employeeActions.getRequisitionFromDBByAccountingProfileId(Constants.accountingProfileIdForRequisitions.toString())!![ProfileRequisition.requestId]
        val requestId2 =
            employeeActions.getRequisitionFromDBByAccountingProfileId(Constants.accountingProfileIdForRequisitions2.toString())!![ProfileRequisition.requestId]

        val requests = employeeActions.searchRequisitions(searchRequest)

        employeeAssertion
            .checkRequisitionPresentInList(requests, requestId1)
            .checkRequisitionPresentInList(requests, requestId2)
            .checkRequisitionsFieldsInList(requests, requestId1, event)
            .checkRequisitionsFieldsInList(requests, requestId2, event2)
    }

    @Test
    @Tags(Tag("kafka_produce"), Tag("smoke"))
    @DisplayName("Search requests: by full name latin")
    fun searchRequestsByFullNameLatinTest() {

        val event = employeePreconditions.fillOutsourseContractEvent(
            accountingProfileId = Constants.accountingProfileIdForRequisitions,
            naimenovanie = "Petrov Petr"
        )

        val searchRequest = employeePreconditions.fillSearchRequisitionsRequest(nameLike = "Petrov Petr")
        employeeActions.produceToVneshnieSotrudniki(event)

        val requestId =
            employeeActions.getRequisitionFromDBByAccountingProfileId(Constants.accountingProfileIdForRequisitions.toString())!![ProfileRequisition.requestId]

        val requests = employeeActions.searchRequisitions(searchRequest)

        employeeAssertion
            .checkRequisitionPresentInList(requests, requestId)
            .checkRequisitionsFieldsInList(requests, requestId, event)
    }

    @Test
    @Tags(Tag("kafka_produce"))
    @DisplayName("Search requests: by statuses")
    fun searchRequestsByStatusesTest() {

        val eventNew = employeePreconditions.fillOutsourseContractEvent(
            accountingProfileId = Constants.accountingProfileIdForRequisitions,
        )
        val eventProcessed = employeePreconditions.fillOutsourseContractEvent(
            accountingProfileId = Constants.accountingProfileIdForRequisitions2,
            mobile = Constants.mobile2.asStringWithoutPlus()
        )
        val eventDeclined = employeePreconditions.fillOutsourseContractEvent(
            accountingProfileId = Constants.accountingProfileIdForRequisitions3,
            mobile = Constants.mobile3.asStringWithoutPlus()
        )
        val declineRequest = employeePreconditions.fillDeclineRequisitionRequest()

        val searchRequestAll =
            employeePreconditions.fillSearchRequisitionsRequest(mobile = MobileQueryFilter.Like(Constants.searchMobilePart))
        val searchRequestNew = employeePreconditions.fillSearchRequisitionsRequest(
            mobile = MobileQueryFilter.Like(Constants.searchMobilePart),
            statuses = listOf(ApiEnum(ProfileRequisitionStatus.NEW))
        )
        val searchRequestProcessed =
            employeePreconditions.fillSearchRequisitionsRequest(
                mobile = MobileQueryFilter.Like(Constants.searchMobilePart),
                statuses = listOf(ApiEnum(ProfileRequisitionStatus.PROCESSED))
            )
        val searchRequestDeclined =
            employeePreconditions.fillSearchRequisitionsRequest(
                mobile = MobileQueryFilter.Like(Constants.searchMobilePart),
                statuses = listOf(ApiEnum(ProfileRequisitionStatus.DECLINED))
            )

        employeeActions.produceToVneshnieSotrudniki(eventNew)
        employeeActions.produceToVneshnieSotrudniki(eventProcessed)
        employeeActions.produceToVneshnieSotrudniki(eventDeclined)

        val requestIdNew =
            employeeActions.getRequisitionFromDBByAccountingProfileId(Constants.accountingProfileIdForRequisitions.toString())!![ProfileRequisition.requestId]
        val requestIdProcessed =
            employeeActions.getRequisitionFromDBByAccountingProfileId(Constants.accountingProfileIdForRequisitions2.toString())!![ProfileRequisition.requestId]
        val requestIdDeclined =
            employeeActions.getRequisitionFromDBByAccountingProfileId(Constants.accountingProfileIdForRequisitions3.toString())!![ProfileRequisition.requestId]
        employeeActions.declineProfileRequisition(requestIdDeclined, declineRequest)

        commonPreconditions.createProfileDeliveryman(
            vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
            requisitionId = requestIdProcessed
        )

        val requestsAll = employeeActions.searchRequisitions(searchRequestAll)
        val requestsProcessed = employeeActions.searchRequisitions(searchRequestProcessed)
        val requestsDeclined = employeeActions.searchRequisitions(searchRequestDeclined)
        val requestsNew = employeeActions.searchRequisitions(searchRequestNew)

        employeeAssertion
            .checkRequisitionPresentInList(requestsAll, requestIdNew)
            .checkRequisitionPresentInList(requestsAll, requestIdProcessed)
            .checkRequisitionPresentInList(requestsAll, requestIdDeclined)

            .checkRequisitionPresentInList(requestsNew, requestIdNew)
            .checkRequisitionNotPresentInList(requestsNew, requestIdProcessed)
            .checkRequisitionNotPresentInList(requestsNew, requestIdDeclined)
            .checkRequisitionsSatisfyStatusConditions(requestsNew, ApiEnum(ProfileRequisitionStatus.NEW))

            .checkRequisitionNotPresentInList(requestsProcessed, requestIdNew)
            .checkRequisitionPresentInList(requestsProcessed, requestIdProcessed)
            .checkRequisitionNotPresentInList(requestsProcessed, requestIdDeclined)
            .checkRequisitionsSatisfyStatusConditions(requestsProcessed, ApiEnum(ProfileRequisitionStatus.PROCESSED))

            .checkRequisitionNotPresentInList(requestsDeclined, requestIdNew)
            .checkRequisitionNotPresentInList(requestsDeclined, requestIdProcessed)
            .checkRequisitionPresentInList(requestsDeclined, requestIdDeclined)
            .checkRequisitionsSatisfyStatusConditions(requestsDeclined, ApiEnum(ProfileRequisitionStatus.DECLINED))

    }

    @Test
    @DisplayName("Search requests: empty result")
    fun searchRequestsEmptyResultTest() {

        val searchRequest = employeePreconditions.fillSearchRequisitionsRequest(
            statuses = listOf(ApiEnum(ProfileRequisitionStatus.NEW)),
            nameLike = StringAndPhoneNumberGenerator.generateRandomString(10),
            mobile = MobileQueryFilter.Exact(PhoneNumber(StringAndPhoneNumberGenerator.generateRandomPhoneNumber()))
        )

        val requests = employeeActions.searchRequisitions(searchRequest)

        employeeAssertion
            .checkListCount(requests.requisitions.size, 0)
    }

    @Test
    @DisplayName("Search requests: pagination")
    fun searchRequestsPаgination() {
        val event1 = employeePreconditions.fillOutsourseContractEvent(
            accountingProfileId = Constants.accountingProfileIdForRequisitions,
            naimenovanie = "Соколов Сергей Семёнович"
        )
        val event2 = employeePreconditions.fillOutsourseContractEvent(
            accountingProfileId = Constants.accountingProfileIdForRequisitions2,
            mobile = Constants.mobile2.asStringWithoutPlus(),
            naimenovanie = "Соколов Сергей Семёнович"
        )
        val event3 = employeePreconditions.fillOutsourseContractEvent(
            accountingProfileId = Constants.accountingProfileIdForRequisitions3,
            mobile = Constants.mobile3.asStringWithoutPlus(),
            naimenovanie = "Соколов Сергей Семёнович"
        )

        employeeActions.produceToVneshnieSotrudniki(event1)
        employeeActions.produceToVneshnieSotrudniki(event2)
        employeeActions.produceToVneshnieSotrudniki(event3)

        val requestId1 =
            employeeActions.getRequisitionFromDBByAccountingProfileId(Constants.accountingProfileIdForRequisitions.toString())!![ProfileRequisition.requestId]
        val requestId2 =
            employeeActions.getRequisitionFromDBByAccountingProfileId(Constants.accountingProfileIdForRequisitions2.toString())!![ProfileRequisition.requestId]
        val requestId3 =
            employeeActions.getRequisitionFromDBByAccountingProfileId(Constants.accountingProfileIdForRequisitions3.toString())!![ProfileRequisition.requestId]

        val searchRequestPage1 = employeePreconditions.fillSearchRequisitionsRequest(
            nameLike = "Соколов Сергей Семёнович",
            paging = PagingFilter(1, null)
        )
        val page1 = employeeActions.searchRequisitions(searchRequestPage1)

        val searchRequestPage2 = employeePreconditions.fillSearchRequisitionsRequest(
            nameLike = "Соколов Сергей Семёнович",
            paging = PagingFilter(1, page1.paging.nextPageMark)
        )
        val page2 = employeeActions.searchRequisitions(searchRequestPage2)

        val searchRequestPage3 = employeePreconditions.fillSearchRequisitionsRequest(
            nameLike = "Соколов Сергей Семёнович",
            paging = PagingFilter(1, page2.paging.nextPageMark)
        )
        val page3 = employeeActions.searchRequisitions(searchRequestPage3)

        employeeAssertion
            .checkRequisitionPresentInList(page1, requestId1)
            .checkRequisitionNotPresentInList(page1, requestId2)
            .checkRequisitionNotPresentInList(page1, requestId3)

            .checkRequisitionNotPresentInList(page2, requestId1)
            .checkRequisitionPresentInList(page2, requestId2)
            .checkRequisitionNotPresentInList(page2, requestId3)

            .checkRequisitionNotPresentInList(page3, requestId1)
            .checkRequisitionNotPresentInList(page3, requestId2)
            .checkRequisitionPresentInList(page3, requestId3)
    }

    @Test
    @DisplayName("Search requests: by type")
    fun searchRequestsByType() {

        val eventOutsourse = employeePreconditions.fillOutsourseContractEvent(
            accountingProfileId = Constants.accountingProfileIdForRequisitions,
            naimenovanie = "Соколов Сергей Семёнович"
        )

        val eventInner = employeePreconditions.fillPriemNaRabotyEvent(
            accountingProfileId = Constants.accountingProfileIdForRequisitions2,
            inn = Constants.innForRequisitions2
        )
        employeeActions.produceToPriemNaRabotuCFZ(eventInner)
        employeeActions.produceToVneshnieSotrudniki(eventOutsourse)

        val requisitionIdOut =
            employeeActions.getRequisitionFromDBByAccountingProfileId(Constants.accountingProfileIdForRequisitions.toString())!![ProfileRequisition.requestId]
        val requisitionIdInn =
            employeeActions.getRequisitionFromDBByAccountingProfileId(Constants.innForRequisitions2)!![ProfileRequisition.requestId]

        val searchRequestOut =
            employeePreconditions.fillSearchRequisitionsRequest(
                types = listOf(ApiEnum(ProfileRequisitionType.OUTSOURCE)), statuses = listOf(
                    ApiEnum(ProfileRequisitionStatus.NEW)
                )
            )
        val searchRequestInn =
            employeePreconditions.fillSearchRequisitionsRequest(
                types = listOf(ApiEnum(ProfileRequisitionType.INNER_SOURCE)), statuses = listOf(
                    ApiEnum(ProfileRequisitionStatus.NEW)))

        val requestsOut = employeeActions.searchRequisitions(searchRequestOut)
        val requestsInn = employeeActions.searchRequisitions(searchRequestInn)

        employeeAssertion
            .checkRequisitionPresentInList(requestsOut, requisitionIdOut)
            .checkRequisitionNotPresentInList(requestsOut, requisitionIdInn)
            .checkRequisitionNotPresentInList(requestsInn, requisitionIdOut)
            .checkRequisitionPresentInList(requestsInn, requisitionIdInn)
            .checkRequisitionsSatisfyTypeConditions(requestsOut, ApiEnum(ProfileRequisitionType.OUTSOURCE))
            .checkRequisitionsSatisfyTypeConditions(requestsInn, ApiEnum(ProfileRequisitionType.INNER_SOURCE))

    }
}
