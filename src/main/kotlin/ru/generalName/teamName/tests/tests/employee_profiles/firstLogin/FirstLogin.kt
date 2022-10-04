package ru.samokat.mysamokat.tests.tests.employee_profiles.firstLogin

import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.samokat.my.domain.employee.EmployeeName
import ru.samokat.my.domain.employee.EmployeeRole
import ru.samokat.my.rest.api.enum.ApiEnum
import ru.samokat.mysamokat.tests.checkers.EmployeeAssertion
import ru.samokat.mysamokat.tests.dataproviders.Constants
import ru.samokat.mysamokat.tests.dataproviders.helpers.database.employee_profiles_backend.Profile
import ru.samokat.mysamokat.tests.dataproviders.preconditions.EmployeePreconditions
import ru.samokat.mysamokat.tests.helpers.actions.EmployeeActions
import java.time.Instant
import java.time.temporal.ChronoUnit

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Tag("empro")
class FirstLogin {

    private lateinit var employeeAssertion: EmployeeAssertion

    @Autowired
    private lateinit var employeeActions: EmployeeActions

    private lateinit var employeePreconditions: EmployeePreconditions

    @BeforeEach
    fun before() {
        employeePreconditions = EmployeePreconditions()
        employeeAssertion = EmployeeAssertion()
        employeeActions.deleteProfile(Constants.mobile1)
        employeeActions.deleteProfile(Constants.mobile2)
        employeeActions.deleteProfile(Constants.mobile3)
    }

    @AfterEach
    fun release() {
        employeeAssertion.assertAll()
        employeeActions.deleteProfile(Constants.mobile1)
        employeeActions.deleteProfile(Constants.mobile2)
        employeeActions.deleteProfile(Constants.mobile3)
    }

    @Test
    @Tags(Tag("kafka_consume"), Tag("smoke"))
    @DisplayName("Get first login event - save date to database (deliveryman)")
    fun getFirstLoginEventDeliveryman() {

        val firstLogin = Instant.now().truncatedTo(ChronoUnit.MILLIS)

        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                staffPartnerId = Constants.staffPartnerId,
                email = null,
                name = EmployeeName("Test", "Test", "Test")
            )

        val createdProfileId = employeeActions.createProfileId(createRequest)

        val firstLoginAtBeforeEvent = employeeActions.getProfileFromDB(createdProfileId)

        val event = employeePreconditions.fillFirstLoginEvent(createdProfileId, firstLogin)
        employeeActions.produceToFirstLogin(event)

        val firstLoginAtAfterEvent = employeeActions.getProfileFromDB(createdProfileId)
        val profileFromApi = employeeActions.getApiProfileById(createdProfileId)

        employeeAssertion
            .checkProfileFirstLoginAtIsNull(firstLoginAtBeforeEvent)
            .checkProfileFirstLoginAt(firstLoginAtAfterEvent[Profile.firstLoginAt].toString(), firstLogin.toString())
            .checkProfileFirstLoginAt(profileFromApi.firstLoginAt.toString(), firstLogin.truncatedTo(ChronoUnit.SECONDS).toString())

    }

    @Test
    @Tags(Tag("kafka_consume"))
    @DisplayName("Get first login event - save date to database (picker)")
    fun getFirstLoginEventPicker() {

        val firstLogin = Instant.now().truncatedTo(ChronoUnit.MILLIS)
        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.PICKER)),
                staffPartnerId = Constants.staffPartnerId,
                email = null,
                vehicle = null,
                name = EmployeeName("Test", "Test", "Test")
            )

        val createdProfileId = employeeActions.createProfileId(createRequest)

        val firstLoginAtBeforeEvent = employeeActions.getProfileFromDB(createdProfileId)

        val event = employeePreconditions.fillFirstLoginEvent(createdProfileId, firstLogin)
        employeeActions.produceToFirstLogin(event)

        val firstLoginAtAfterEvent = employeeActions.getProfileFromDB(createdProfileId)
        val profileFromApi = employeeActions.getApiProfileById(createdProfileId)

        employeeAssertion
            .checkProfileFirstLoginAtIsNull(firstLoginAtBeforeEvent)
            .checkProfileFirstLoginAt(firstLoginAtAfterEvent[Profile.firstLoginAt].toString(), firstLogin.toString())
            .checkProfileFirstLoginAt(profileFromApi.firstLoginAt.toString(), firstLogin.truncatedTo(ChronoUnit.SECONDS).toString())

    }

    @Test
    @Tags(Tag("kafka_consume"))
    @DisplayName("Get first login event - save date to database (admin)")
    fun getFirstLoginEventAdmin() {

        val firstLogin = Instant.now().truncatedTo(ChronoUnit.MILLIS)
        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DARKSTORE_ADMIN)),
                staffPartnerId = null,
                email = null,
                vehicle = null,
                name = EmployeeName("Test", "Test", "Test")
            )

        val createdProfileId = employeeActions.createProfileId(createRequest)

        val firstLoginAtBeforeEvent = employeeActions.getProfileFromDB(createdProfileId)

        val event = employeePreconditions.fillFirstLoginEvent(createdProfileId, firstLogin)
        employeeActions.produceToFirstLogin(event)

        val firstLoginAtAfterEvent = employeeActions.getProfileFromDB(createdProfileId)
        val profileFromApi = employeeActions.getApiProfileById(createdProfileId)

        employeeAssertion
            .checkProfileFirstLoginAtIsNull(firstLoginAtBeforeEvent)
            .checkProfileFirstLoginAt(firstLoginAtAfterEvent[Profile.firstLoginAt].toString(), firstLogin.toString())
            .checkProfileFirstLoginAt(profileFromApi.firstLoginAt.toString(), firstLogin.truncatedTo(ChronoUnit.SECONDS).toString())

    }

    @Tags(Tag("kafka_consume"))
    @DisplayName("Get first login event - save date to database (deliveryman-picker)")
    fun getFirstLoginEventDeliverymanPicker() {

        val firstLogin = Instant.now().truncatedTo(ChronoUnit.MILLIS)
        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN), ApiEnum(EmployeeRole.PICKER)),
                staffPartnerId = Constants.staffPartnerId,
                email = null,
                name = EmployeeName("Test", "Test", "Test")
            )

        val createdProfileId = employeeActions.createProfileId(createRequest)

        val firstLoginAtBeforeEvent = employeeActions.getProfileFromDB(createdProfileId)

        val event = employeePreconditions.fillFirstLoginEvent(createdProfileId, firstLogin)
        employeeActions.produceToFirstLogin(event)

        val firstLoginAtAfterEvent = employeeActions.getProfileFromDB(createdProfileId)
        val profileFromApi = employeeActions.getApiProfileById(createdProfileId)

        employeeAssertion
            .checkProfileFirstLoginAtIsNull(firstLoginAtBeforeEvent)
            .checkProfileFirstLoginAt(firstLoginAtAfterEvent[Profile.firstLoginAt].toString(), firstLogin.toString())
            .checkProfileFirstLoginAt(profileFromApi.firstLoginAt.toString(), firstLogin.truncatedTo(ChronoUnit.SECONDS).toString())

    }

    @Test
    @Tags(Tag("kafka_consume"))
    @DisplayName("Get first login event - repeated event")
    fun getFirstLoginEventRepeated() {

        val firstLogin1 = Instant.now().minusSeconds(5000).truncatedTo(ChronoUnit.MILLIS)
        val firstLogin2 = Instant.now().truncatedTo(ChronoUnit.MILLIS)
        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                staffPartnerId = Constants.staffPartnerId,
                email = null,
                name = EmployeeName("Test", "Test", "Test")
            )

        val createdProfileId = employeeActions.createProfileId(createRequest)

        val event1 = employeePreconditions.fillFirstLoginEvent(createdProfileId, firstLogin1)
        employeeActions.produceToFirstLogin(event1)

        val event2 = employeePreconditions.fillFirstLoginEvent(createdProfileId, firstLogin2)
        employeeActions.produceToFirstLogin(event2)

        val firstLoginAtAfterEvent = employeeActions.getProfileFromDB(createdProfileId)
        val profileFromApi = employeeActions.getApiProfileById(createdProfileId)

        employeeAssertion
            .checkProfileFirstLoginAt(firstLoginAtAfterEvent[Profile.firstLoginAt].toString(), firstLogin1.toString())
            .checkProfileFirstLoginAt(profileFromApi.firstLoginAt.toString(), firstLogin1.truncatedTo(ChronoUnit.SECONDS).toString())

    }

    @Test
    @Tags(Tag("kafka_consume"))
    @DisplayName("Get first login event - profile disabled")
    fun getFirstLoginEventProfileDisabled() {

        val firstLogin = Instant.now().truncatedTo(ChronoUnit.MILLIS)
        val createRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                staffPartnerId = Constants.staffPartnerId,
                email = null,
                name = EmployeeName("Test", "Test", "Test")
            )

        val createdProfileId = employeeActions.createProfileId(createRequest)
        employeeActions.deleteProfile(createdProfileId)

        val event = employeePreconditions.fillFirstLoginEvent(createdProfileId, firstLogin)
        employeeActions.produceToFirstLogin(event)

        val firstLoginAtAfterEvent = employeeActions.getProfileFromDB(createdProfileId)

        employeeAssertion
            .checkProfileFirstLoginAtIsNull(firstLoginAtAfterEvent)
    }

    @Test
    @Tags(Tag("kafka_consume"))
    @DisplayName("Get first login event - several events for several employee")
    fun getFirstLoginSeveralevents() {

        val firstLogin = Instant.now().truncatedTo(ChronoUnit.MILLIS)
        employeePreconditions
            .setListOfCreatedProfileRequest(amount = 2)

        employeeActions
            .createSeveralProfiles(employeePreconditions.listCreateProfileRequest())

        val event1 = employeePreconditions.fillFirstLoginEvent(employeeActions.listCreatedProfileResponses()[0].profileId, firstLogin)
        val event2 = employeePreconditions.fillFirstLoginEvent(employeeActions.listCreatedProfileResponses()[1].profileId, firstLogin)
        employeeActions.produceToFirstLogin(event1)
        employeeActions.produceToFirstLogin(event2)

        val firstLoginAtAfterEvent1 = employeeActions.getProfileFromDB(employeeActions.listCreatedProfileResponses()[0].profileId)
        val firstLoginAtAfterEvent2 = employeeActions.getProfileFromDB(employeeActions.listCreatedProfileResponses()[1].profileId)

        employeeAssertion
            .checkProfileFirstLoginAt(firstLoginAtAfterEvent1[Profile.firstLoginAt].toString(), firstLogin.toString())
            .checkProfileFirstLoginAt(firstLoginAtAfterEvent2[Profile.firstLoginAt].toString(), firstLogin.toString())

        employeeActions.deleteSeveralProfileByProfileId(employeeActions.listCreatedProfileResponses())

    }
}




