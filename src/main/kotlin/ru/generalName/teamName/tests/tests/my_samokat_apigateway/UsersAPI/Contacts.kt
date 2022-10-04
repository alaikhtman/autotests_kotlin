package ru.samokat.mysamokat.tests.tests.my_samokat_apigateway.UsersAPI

import org.apache.http.HttpStatus
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.samokat.employeeprofiles.api.common.domain.EmployeeVehicleType
import ru.samokat.employeeprofiles.api.common.domain.Vehicle
import ru.samokat.my.domain.employee.EmployeeName
import ru.samokat.my.domain.employee.EmployeeRole
import ru.samokat.my.rest.api.enum.ApiEnum
import ru.samokat.mysamokat.tests.checkers.CommonAssertion
import ru.samokat.mysamokat.tests.checkers.MySamokatApiGWAssertions
import ru.samokat.mysamokat.tests.dataproviders.Constants
import ru.samokat.mysamokat.tests.dataproviders.preconditions.CommonPreconditions
import ru.samokat.mysamokat.tests.dataproviders.preconditions.MySamokatApiGWPreconditions
import ru.samokat.mysamokat.tests.helpers.actions.EmployeeActions
import ru.samokat.mysamokat.tests.helpers.actions.MySamokatApiGWActions

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Tag("msmkt-apigateway")
class Contacts {

    private lateinit var msmktPreconditions: MySamokatApiGWPreconditions

    private lateinit var commonAssertion: CommonAssertion

    @Autowired
    private lateinit var employeeActions: EmployeeActions

    @Autowired
    private lateinit var msmktActions: MySamokatApiGWActions

    private lateinit var msmktAssertion: MySamokatApiGWAssertions

    @Autowired
    private lateinit var commonPreconditions: CommonPreconditions

    @BeforeEach
    fun before() {
        msmktPreconditions = MySamokatApiGWPreconditions()
        msmktAssertion = MySamokatApiGWAssertions()
        commonAssertion = CommonAssertion()
        employeeActions.deleteProfile(Constants.mobile1)
        employeeActions.deleteProfile(Constants.mobile2)
        employeeActions.deleteProfile(Constants.mobile3)
    }

    @AfterEach
    fun release() {
        msmktAssertion.assertAll()
        employeeActions.deleteProfile(Constants.mobile1)
        employeeActions.deleteProfile(Constants.mobile2)
        employeeActions.deleteProfile(Constants.mobile3)
    }

    // тесты могут работать нестабильно при повторном запуске, так как на ручке получения контактов есть кеш 5 минут
    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Contacts: get contacts for deliveryman (only admin)")
    fun getContactsForDeliveryman() {

        val admin = commonPreconditions.createProfileDarkstoreAdmin(
            darkstoreId = Constants.searchContactsDarkstore,
            mobile = Constants.mobile2,
            name = EmployeeName("Ivanov", "Ivan", "Ivanovich")
        ).profileId

        val profile = commonPreconditions.createProfileDeliveryman(
            darkstoreId = Constants.searchContactsDarkstore,
        )
        val authRequest = msmktPreconditions.fillAuthRequest(password = profile.generatedPassword!!)

        val token = msmktActions.authProfilePassword(authRequest)!!.accessToken
        val contacts = msmktActions.getContacts(token)

        msmktAssertion.checkDarkstoreContactsListCount(contacts, 1)
        msmktAssertion.checkDarkstoreContactInList(
            contacts,
            admin,
            Constants.mobile2,
            EmployeeName("Ivanov", "Ivan", "Ivanovich"), listOf(ApiEnum(EmployeeRole.DARKSTORE_ADMIN))
        )
    }

    @Test
    @DisplayName("Contacts: get contacts for picker (only coordinator)")
    fun getContactsForPicker() {

        val coordinator = commonPreconditions.createProfile(
            roles = listOf(ApiEnum(EmployeeRole.COORDINATOR)),
            supervisedDarkstores = mutableListOf(Constants.searchContactsDarkstore2),
            mobile = Constants.mobile2,
            name = EmployeeName("Ivanov", "Ivan", "Ivanovich")
        ).profileId

        val profile = commonPreconditions.createProfilePicker(
            darkstoreId = Constants.searchContactsDarkstore2, roles = listOf(
                ApiEnum(EmployeeRole.PICKER)
            )
        )

        val authRequest = msmktPreconditions.fillAuthRequest(password = profile.generatedPassword!!)

        val token = msmktActions.authProfilePassword(authRequest)!!.accessToken
        val contacts = msmktActions.getContacts(token)

        msmktAssertion.checkDarkstoreContactsListCount(contacts, 1)
        msmktAssertion.checkDarkstoreContactInList(
            contacts,
            coordinator,
            Constants.mobile2,
            EmployeeName("Ivanov", "Ivan", "Ivanovich"), listOf(ApiEnum(EmployeeRole.COORDINATOR))
        )
    }

    @Test
    @DisplayName("Contacts: get contacts for admin (several contacts)")
    fun getContactsForAdmin() {

        val coordinator = commonPreconditions.createProfile(
            roles = listOf(ApiEnum(EmployeeRole.COORDINATOR)),
            supervisedDarkstores = mutableListOf(Constants.searchContactsDarkstore3),
            mobile = Constants.mobile2,
            name = EmployeeName("Ivanov", "Ivan", "Ivanovich")
        ).profileId

        val admin = commonPreconditions.createProfileDarkstoreAdmin(
            darkstoreId = Constants.searchContactsDarkstore3,
            mobile = Constants.mobile3,
            name = EmployeeName("Ivanov", "Ivan", "Ivanovich")
        ).profileId

        val profile = commonPreconditions.createProfileDarkstoreAdmin(
            darkstoreId = Constants.searchContactsDarkstore3)

        val authRequest = msmktPreconditions.fillAuthRequest(password = profile.generatedPassword!!)

        val token = msmktActions.authProfilePassword(authRequest)!!.accessToken
        val contacts = msmktActions.getContacts(token)

        msmktAssertion.checkDarkstoreContactsListCount(contacts, 2)
        msmktAssertion.checkDarkstoreContactsPresentInList(contacts, coordinator)
        msmktAssertion.checkDarkstoreContactsPresentInList(contacts, admin)
        msmktAssertion.checkDarkstoreContactsRole(contacts.contacts[0], EmployeeRole.DARKSTORE_ADMIN)
        msmktAssertion.checkDarkstoreContactsRole(contacts.contacts[1], EmployeeRole.COORDINATOR)
    }

    @Test
    @DisplayName("Contacts: no contacts")
    fun getContactsEmptyResult() {

        val profile = commonPreconditions.createProfileDeliveryman(
            vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
            darkstoreId = Constants.searchContactsDarkstore4,
        )
        val authRequest = msmktPreconditions.fillAuthRequest(password = profile.generatedPassword!!)

        val token = msmktActions.authProfilePassword(authRequest)!!.accessToken
        val contacts = msmktActions.getContacts(token)

        msmktAssertion.checkDarkstoreContactsListCount(contacts, 0)
    }

    @Test
    @Tag("darkstore_integration")
    @DisplayName("Contacts: inactive darkstore")
    fun getContactsInactiveDarkstore() {

        val admin = commonPreconditions.createProfileDarkstoreAdmin(
            darkstoreId = Constants.inactiveDarkstore,
            mobile = Constants.mobile2,
            name = EmployeeName("Ivanov", "Ivan", "Ivanovich")
        ).profileId

        val profile = commonPreconditions.createProfileDeliveryman(
            darkstoreId = Constants.inactiveDarkstore,
        )
        val authRequest = msmktPreconditions.fillAuthRequest(password = profile.generatedPassword!!)

        val token = msmktActions.authProfilePassword(authRequest)!!.accessToken
        val contacts = msmktActions.getContacts(token)

        msmktAssertion.checkDarkstoreContactsListCount(contacts, 1)
    }

    @Test
    @DisplayName("Contacts: profile disabled")
    fun getContactsProfileDisabled() {

        val profile = commonPreconditions.createProfileDeliveryman(
            darkstoreId = Constants.searchContactsDarkstore
        )
        val authRequest = msmktPreconditions.fillAuthRequest(password = profile.generatedPassword!!)

        val token = msmktActions.authProfilePassword(authRequest)!!.accessToken
        employeeActions.deleteProfile(profile.profileId)
        val contacts = msmktActions.getContactsError(token, HttpStatus.SC_INTERNAL_SERVER_ERROR)

    }
}
