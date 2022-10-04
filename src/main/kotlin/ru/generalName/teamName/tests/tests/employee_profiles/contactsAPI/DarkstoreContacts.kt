package ru.samokat.mysamokat.tests.tests.employee_profiles.contactsAPI

import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.samokat.employeeprofiles.api.common.domain.EmployeeVehicleType
import ru.samokat.employeeprofiles.api.common.domain.Vehicle
import ru.samokat.my.domain.employee.EmployeeRole

import ru.samokat.my.rest.api.enum.ApiEnum
import ru.samokat.mysamokat.tests.checkers.EmployeeAssertion
import ru.samokat.mysamokat.tests.dataproviders.Constants
import ru.samokat.mysamokat.tests.dataproviders.preconditions.EmployeePreconditions
import ru.samokat.mysamokat.tests.helpers.actions.EmployeeActions


@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Tag("empro")
class DarkstoreContacts {

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
    }

    @AfterEach
    fun release() {
        employeeAssertion.assertAll()
        employeeActions.deleteProfile(Constants.mobile1)
        employeeActions.deleteProfile(Constants.mobile2)
    }

    @Test
    @DisplayName("Get DS contacts: no contacts")
    fun getEmptyContactsList() {
        val contacts = employeeActions.getDarkstoreContacts(Constants.searchContactsDarkstore)
        employeeAssertion.checkDarkstoreContactsListCount(contacts, 0)

    }

    @Test
    @DisplayName("Get DS contacts: >1 contacts")
    fun getMoreThanOneContactsList() {
        val createAdminRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DARKSTORE_ADMIN)),
                vehicle = null,
                email = null,
                staffPartnerId = null,
                darkstoreId = Constants.searchContactsDarkstore,
                mobile = Constants.mobile1
            )
        val createCoordinatorRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.COORDINATOR)),
                vehicle = null,
                email = null,
                supervisedDarkstores = mutableListOf(Constants.searchContactsDarkstore),
                staffPartnerId = null,
                darkstoreId = null,
                mobile = Constants.mobile2
            )

        val coordinatorProfileId = employeeActions.createProfileId(createCoordinatorRequest)
        val adminProfileId = employeeActions.createProfileId(createAdminRequest)

        val contacts = employeeActions.getDarkstoreContacts(Constants.searchContactsDarkstore)

        employeeAssertion
            .checkDarkstoreContactsListCount(contacts, 2)
            .checkDarkstoreContactInList(contacts, adminProfileId, createAdminRequest)
            .checkDarkstoreContactInList(contacts, coordinatorProfileId, createCoordinatorRequest)
    }

    @Test
    @DisplayName("Get DS contacts: only darkstore_admin")
    fun getOnlyAdminsContactsList() {
        val createAdminRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DARKSTORE_ADMIN)),
                vehicle = null,
                email = null,
                staffPartnerId = null,
                darkstoreId = Constants.searchContactsDarkstore,
                mobile = Constants.mobile1
            )
        val adminProfileId = employeeActions.createProfileId(createAdminRequest)

        val contacts = employeeActions.getDarkstoreContacts(Constants.searchContactsDarkstore)

        employeeAssertion
            .checkDarkstoreContactsListCount(contacts, 1)
            .checkDarkstoreContactInList(contacts, adminProfileId, createAdminRequest)
    }

    @Test
    @DisplayName("Get DS contacts: only coordinator")
    fun getOnlyCoordinatorsContactsList() {
        val createCoordinatorRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.COORDINATOR)),
                vehicle = null,
                email = null,
                supervisedDarkstores = mutableListOf(Constants.searchContactsDarkstore),
                staffPartnerId = null,
                darkstoreId = null,
                mobile = Constants.mobile1
            )
        val coordinatorProfileId = employeeActions.createProfileId(createCoordinatorRequest)

        val contacts = employeeActions.getDarkstoreContacts(Constants.searchContactsDarkstore)

        employeeAssertion
            .checkDarkstoreContactsListCount(contacts, 1)
            .checkDarkstoreContactInList(contacts, coordinatorProfileId, createCoordinatorRequest)
    }

    @Test
    @DisplayName("Get DS contacts: change DS by darkstre_admin")
    fun getContactsListAdminChangeDS() {
        val createAdminRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DARKSTORE_ADMIN)),
                vehicle = null,
                email = null,
                staffPartnerId = null,
                darkstoreId = Constants.searchContactsDarkstore,
                mobile = Constants.mobile1
            )
        val adminProfileId = employeeActions.createProfileId(createAdminRequest)

        employeePreconditions.setUpdateProfileRequest(
            createAdminRequest,
            staffPartnerId = null,
            vehicle = null,
            supervisedDarkstores = null,
            darkstoreId = Constants.darkstoreId)
        employeeActions
            .updateProfile(
                adminProfileId,
                employeePreconditions.updateProfileRequest()
            )

        val contactsA = employeeActions.getDarkstoreContacts(Constants.searchContactsDarkstore)
        val contactsB = employeeActions.getDarkstoreContacts(Constants.darkstoreId)

        employeeAssertion
            .checkDarkstoreContactNotInList(contactsA, adminProfileId)
            .checkDarkstoreContactInList(contactsB, adminProfileId, createAdminRequest)
    }

   @Test
    @DisplayName("Get DS contacts: change DS by coordinator")
    fun getContactsListCoordinatorChangeDS() {
        val createCoordinatorRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.COORDINATOR)),
                vehicle = null,
                email = null,
                supervisedDarkstores = mutableListOf(Constants.searchContactsDarkstore),
                staffPartnerId = null,
                darkstoreId = null,
                mobile = Constants.mobile1
            )
        val coordinatorProfileId = employeeActions.createProfileId(createCoordinatorRequest)

        employeePreconditions.setUpdateProfileRequest(
            createCoordinatorRequest,
            staffPartnerId = null,
            vehicle = null,
            supervisedDarkstores = mutableListOf(Constants.darkstoreId),
            darkstoreId = null)
        employeeActions
            .updateProfile(
                coordinatorProfileId,
                employeePreconditions.updateProfileRequest()
            )

        val contactsA = employeeActions.getDarkstoreContacts(Constants.searchContactsDarkstore)
        val contactsB = employeeActions.getDarkstoreContacts(Constants.darkstoreId)

        employeeAssertion
            .checkDarkstoreContactNotInList(contactsA, coordinatorProfileId)
            .checkDarkstoreContactInList(contactsB, coordinatorProfileId, createCoordinatorRequest)
    }

    @Test
    @DisplayName("Get DS contacts: admin is blocked")
    fun getContactsListAdminBlocked() {

        val createAdminRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DARKSTORE_ADMIN)),
                vehicle = null,
                email = null,
                staffPartnerId = null,
                darkstoreId = Constants.searchContactsDarkstore,
                mobile = Constants.mobile1
            )
        val adminProfileId = employeeActions.createProfileId(createAdminRequest)
        employeeActions.deleteProfile(adminProfileId)

        val contacts = employeeActions.getDarkstoreContacts(Constants.searchContactsDarkstore)

        employeeAssertion
            .checkDarkstoreContactNotInList(contacts, adminProfileId)
    }

    @Test
    @DisplayName("Get DS contacts: coordinator is blocked")
    fun getContactsListCoordinatorBlocked() {

        val createCoordinatorRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.COORDINATOR)),
                vehicle = null,
                email = null,
                supervisedDarkstores = mutableListOf(Constants.searchContactsDarkstore),
                staffPartnerId = null,
                darkstoreId = null,
                mobile = Constants.mobile1
            )
        val coordinatorProfileId = employeeActions.createProfileId(createCoordinatorRequest)
        employeeActions.deleteProfile(coordinatorProfileId)

        val contacts = employeeActions.getDarkstoreContacts(Constants.searchContactsDarkstore)

        employeeAssertion
            .checkDarkstoreContactNotInList(contacts, coordinatorProfileId)
    }

    @Test
    @DisplayName("Get DS contacts: role change (admin -> coordinator)")
    fun getContactsListRoleChangedAdminToCoordinator() {
        val createAdminRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DARKSTORE_ADMIN)),
                vehicle = null,
                email = null,
                staffPartnerId = null,
                darkstoreId = Constants.searchContactsDarkstore,
                mobile = Constants.mobile1
            )
        val adminProfileId = employeeActions.createProfileId(createAdminRequest)

        employeePreconditions.setUpdateProfileRequest(
            createAdminRequest,
            staffPartnerId = null,
            vehicle = null,
            darkstoreId = null,
            supervisedDarkstores = mutableListOf(Constants.searchContactsDarkstore),
            roles = listOf(ApiEnum(EmployeeRole.COORDINATOR))
        )
        employeeActions
            .updateProfile(
                adminProfileId,
                employeePreconditions.updateProfileRequest()
            )

        val contacts = employeeActions.getDarkstoreContacts(Constants.searchContactsDarkstore)

        employeeAssertion
            .checkDarkstoreContactInList(contacts, adminProfileId, createAdminRequest, listOf(ApiEnum(EmployeeRole.COORDINATOR)))

    }

    @Test
    @DisplayName("Get DS contacts: role change (coordinator -> admin)")
    fun getContactsListRoleChangedCoordinatorToAdmin() {

        val createCoordinatorRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.COORDINATOR)),
                vehicle = null,
                email = null,
                supervisedDarkstores = mutableListOf(Constants.searchContactsDarkstore),
                staffPartnerId = null,
                darkstoreId = null,
                mobile = Constants.mobile1
            )
        val coordinatorProfileId = employeeActions.createProfileId(createCoordinatorRequest)

        employeePreconditions.setUpdateProfileRequest(
            createCoordinatorRequest,
            staffPartnerId = null,
            vehicle = null,
            darkstoreId = Constants.searchContactsDarkstore,
            supervisedDarkstores = null,
            roles = listOf(ApiEnum(EmployeeRole.DARKSTORE_ADMIN))
        )
        employeeActions
            .updateProfile(
                coordinatorProfileId,
                employeePreconditions.updateProfileRequest()
            )

        val contacts = employeeActions.getDarkstoreContacts(Constants.searchContactsDarkstore)

        employeeAssertion
            .checkDarkstoreContactInList(contacts, coordinatorProfileId, createCoordinatorRequest, listOf(ApiEnum(EmployeeRole.DARKSTORE_ADMIN)))

    }

    @Test
    @DisplayName("Get DS contacts: multirole")
    fun getContactsListMultirole() {

        val createAdminRequest = employeePreconditions
            .fillCreateProfileRequest(
                roles = listOf(ApiEnum(EmployeeRole.DARKSTORE_ADMIN), ApiEnum(EmployeeRole.DELIVERYMAN)),
                vehicle = Vehicle(ApiEnum(EmployeeVehicleType.COMPANY_BICYCLE)),
                email = null,
                darkstoreId = Constants.searchContactsDarkstore,
                mobile = Constants.mobile1
            )
        val adminProfileId = employeeActions.createProfileId(createAdminRequest)

        val contacts = employeeActions.getDarkstoreContacts(Constants.searchContactsDarkstore)

        employeeAssertion
            .checkDarkstoreContactInList(contacts, adminProfileId, createAdminRequest)

    }


}
