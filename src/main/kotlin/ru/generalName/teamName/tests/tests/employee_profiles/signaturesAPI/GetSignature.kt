package ru.samokat.mysamokat.tests.tests.employee_profiles.signaturesAPI

import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.samokat.my.domain.employee.EmployeeName
import ru.samokat.my.domain.employee.EmployeeRole
import ru.samokat.my.rest.api.enum.ApiEnum
import ru.samokat.mysamokat.tests.checkers.EmployeeAssertion
import ru.samokat.mysamokat.tests.dataproviders.Constants
import ru.samokat.mysamokat.tests.dataproviders.preconditions.EmployeePreconditions
import ru.samokat.mysamokat.tests.helpers.actions.EmployeeActions
import java.util.*

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Tag("empro")
class GetSignature {
    private lateinit var employeeAssertion: EmployeeAssertion

    @Autowired
    private lateinit var employeeActions: EmployeeActions

    private lateinit var employeePreconditions: EmployeePreconditions

    @BeforeEach
    fun before() {
        employeePreconditions = EmployeePreconditions()
        employeeAssertion = EmployeeAssertion()
        employeeActions.deleteProfile(Constants.mobile1)
    }

    @AfterEach
    fun release() {
        employeeAssertion.assertAll()
        employeeActions.deleteProfile(Constants.mobile1)
    }

    @Test
    @Tags(Tag("kafka_produce"), Tag("smoke"))
    @DisplayName("Get darkstore admin signature")
    fun getDarkstoreAdminSignature(){

        val event = employeePreconditions.fillPriemNaRabotyEvent()
        employeeActions.produceToPriemNaRabotuCFZ(event)

        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DARKSTORE_ADMIN)),
                vehicle = null,
                staffPartnerId = null,
                email = null,
                name = EmployeeName("Test", "Test", "Test"),
                accountingProfileId = event.payload[0].fizicheskoeLitso.inn
            )

        val createdProfileId = employeeActions.createProfileId(createRequest)

        val signature = employeeActions.getProfileSignature(createdProfileId)

        employeeAssertion.stepCheckProfileSignature(event, signature)
    }

    @Test
    @Tags(Tag("kafka_produce"))
    @DisplayName("Get goods manager signature")
    fun getGoodsManagerSignature(){

        val event = employeePreconditions.fillPriemNaRabotyEvent()
        employeeActions.produceToPriemNaRabotuCFZ(event)

        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.GOODS_MANAGER)),
                vehicle = null,
                staffPartnerId = null,
                email = null,
                name = EmployeeName("Test", "Test", "Test"),
                accountingProfileId = event.payload[0].fizicheskoeLitso.inn
            )

        val createdProfileId = employeeActions.createProfileId(createRequest)

        val signature = employeeActions.getProfileSignature(createdProfileId)

        employeeAssertion.stepCheckProfileSignature(event, signature)
    }

    @Test
    @Tags(Tag("kafka_produce"))
    @DisplayName("Get signature by other role")
    fun getSignatureByOtherRole(){

        val event = employeePreconditions.fillPriemNaRabotyEvent()
        employeeActions.produceToPriemNaRabotuCFZ(event)

        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                email = null,
                name = EmployeeName("Test", "Test", "Test"),
                accountingProfileId = event.payload[0].fizicheskoeLitso.inn
            )

        val createdProfileId = employeeActions.createProfileId(createRequest)

        val signature = employeeActions.getProfileSignature(createdProfileId)

        employeeAssertion.getSoftAssertion().assertThat(signature.canSign).isFalse
    }

    @Test
    @DisplayName("Get darkstore admin signature - no contract")
    fun getDarkstoreAdminSignatureNoContract(){

        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DARKSTORE_ADMIN)),
                vehicle = null,
                staffPartnerId = null,
                email = null,
                name = EmployeeName("Test", "Test", "Test"),
                accountingProfileId = UUID.randomUUID().toString()
            )

        val createdProfileId = employeeActions.createProfileId(createRequest)

        val signature = employeeActions.getProfileSignature(createdProfileId)

        employeeAssertion.getSoftAssertion().assertThat(signature.canSign).isFalse
    }

    @Test
    @DisplayName("Get darkstore admin signature - user is disabled")
    fun getDarkstoreAdminSignatureUserIsDisabled(){

        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DARKSTORE_ADMIN)),
                vehicle = null,
                staffPartnerId = null,
                email = null,
                name = EmployeeName("Test", "Test", "Test"),
                accountingProfileId = UUID.randomUUID().toString()
            )

        val createdProfileId = employeeActions.createProfileId(createRequest)
        employeeActions.deleteProfile(createdProfileId)

        val signature = employeeActions.getProfileSignatureWithError(createdProfileId)

        employeeAssertion.checkErrorMessage(signature.message, "Employee profile was not found")
    }

    @Test
    @DisplayName("Get darkstore admin signature - user not exists")
    fun getDarkstoreAdminSignatureUserNotExists(){

        val signature = employeeActions.getProfileSignatureWithError(UUID.randomUUID())
        employeeAssertion.checkErrorMessage(signature.message, "Employee profile was not found")
    }

    @Test
    @Tags(Tag("kafka_produce"))
    @DisplayName("Get darkstore admin signature (approved = false)")
    fun getDarkstoreAdminSignatureCotractNotApproved(){

        val event = employeePreconditions.fillPriemNaRabotyEvent(proveden = false)
        employeeActions.produceToPriemNaRabotuCFZ(event)

        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DARKSTORE_ADMIN)),
                vehicle = null,
                staffPartnerId = null,
                email = null,
                name = EmployeeName("Test", "Test", "Test"),
                accountingProfileId = event.payload[0].fizicheskoeLitso.inn
            )

        val createdProfileId = employeeActions.createProfileId(createRequest)

        val signature = employeeActions.getProfileSignature(createdProfileId)

        employeeAssertion.getSoftAssertion().assertThat(signature.canSign).isFalse
    }

    @Test
    @Tags(Tag("kafka_produce"))
    @DisplayName("Get darkstore admin signature (several approved contracts)")
    fun getDarkstoreAdminSignatureSeveralContracts(){

        val event = employeePreconditions.fillPriemNaRabotyEvent()
        val event2 = employeePreconditions.fillPriemNaRabotyEvent(
            inn = event.payload[0].fizicheskoeLitso.inn!!)
        employeeActions.produceToPriemNaRabotuCFZ(event)
        employeeActions.produceToPriemNaRabotuCFZ(event2)

        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DARKSTORE_ADMIN)),
                vehicle = null,
                staffPartnerId = null,
                email = null,
                name = EmployeeName("Test", "Test", "Test"),
                accountingProfileId = event.payload[0].fizicheskoeLitso.inn
            )

        val createdProfileId = employeeActions.createProfileId(createRequest)

        val signature = employeeActions.getProfileSignature(createdProfileId)

        employeeAssertion.getSoftAssertion().assertThat(signature.canSign).isFalse
    }

    @Test
    @Tags(Tag("kafka_produce"))
    @DisplayName("Get darkstore admin signature (several contracts)")
    fun getDarkstoreAdminSignatureSeveralContractsInDifferentStatuses(){

        val event = employeePreconditions.fillPriemNaRabotyEvent(proveden = false)
        val event2 = employeePreconditions.fillPriemNaRabotyEvent(
            inn = event.payload[0].fizicheskoeLitso.inn!!,
            proveden = true)
        employeeActions.produceToPriemNaRabotuCFZ(event)
        employeeActions.produceToPriemNaRabotuCFZ(event2)

        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DARKSTORE_ADMIN)),
                vehicle = null,
                staffPartnerId = null,
                email = null,
                name = EmployeeName("Test", "Test", "Test"),
                accountingProfileId = event.payload[0].fizicheskoeLitso.inn
            )

        val createdProfileId = employeeActions.createProfileId(createRequest)

        val signature = employeeActions.getProfileSignature(createdProfileId)

        employeeAssertion.stepCheckProfileSignature(event2, signature)
    }
}