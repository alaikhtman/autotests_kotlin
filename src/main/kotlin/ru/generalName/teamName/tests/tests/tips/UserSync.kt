package ru.samokat.mysamokat.tests.tests.tips

import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.samokat.employeeprofiles.api.common.domain.EmployeeVehicleType
import ru.samokat.employeeprofiles.api.common.domain.Vehicle
import ru.samokat.my.domain.employee.EmployeeName
import ru.samokat.my.domain.employee.EmployeeRole

import ru.samokat.my.rest.api.enum.ApiEnum
import ru.samokat.mysamokat.tests.checkers.TipsAssertion
import ru.samokat.mysamokat.tests.dataproviders.Constants
import ru.samokat.mysamokat.tests.dataproviders.StringAndPhoneNumberGenerator
import ru.samokat.mysamokat.tests.dataproviders.preconditions.CommonPreconditions
import ru.samokat.mysamokat.tests.dataproviders.preconditions.EmployeePreconditions
import ru.samokat.mysamokat.tests.helpers.actions.EmployeeActions
import ru.samokat.mysamokat.tests.helpers.actions.TipsActions

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Tags(Tag("tips"), Tag("empro_integration"))
class UserSync {


    @Autowired
    private lateinit var tipsActions: TipsActions

    private lateinit var tipsAssertion: TipsAssertion

    @Autowired
    private lateinit var employeeActions: EmployeeActions

    @Autowired
    private lateinit var commonPreconditions: CommonPreconditions

    private lateinit var employeePreconditions: EmployeePreconditions

    @BeforeEach
    fun before() {
        employeePreconditions = EmployeePreconditions()
        tipsAssertion = TipsAssertion()
        employeeActions.deleteProfile(Constants.mobileChaChaCha1)
        employeeActions.deleteProfile(Constants.mobileChaChaCha2)
        employeeActions.deleteProfile(Constants.mobileChaChaCha3)
        tipsActions.deleteChachachayWorker(Constants.mobileChaChaCha1)
        tipsActions.deleteChachachayWorker(Constants.mobileChaChaCha2)
        tipsActions.deleteChachachayWorker(Constants.mobileChaChaCha3)
    }

    @AfterEach
    fun release() {
        tipsAssertion.assertAll()
        employeeActions.deleteProfile(Constants.mobileChaChaCha1)
        employeeActions.deleteProfile(Constants.mobileChaChaCha2)
        employeeActions.deleteProfile(Constants.mobileChaChaCha3)
    }

    @Test
    @DisplayName("User sync: adding user to tips table when create (deliveryman)")
    fun createUserInTipsTablesDeliveryman() {

        val profileId = commonPreconditions.createProfileDeliveryman(mobile = Constants.mobileChaChaCha1).profileId

        val worker = tipsActions.getChachachayWorkerFromDB(profileId)

        tipsAssertion.checkChachachayWorker(worker)
    }

    @Test
    @DisplayName("User sync: adding user to tips table when create (deliveryman-picker)")
    fun createUserInTipsTablesDeliverymanPicker() {

        val profileId = commonPreconditions.createProfileDeliverymanPicker(mobile = Constants.mobileChaChaCha1).profileId

        val worker = tipsActions.getChachachayWorkerFromDB(profileId)

        tipsAssertion.checkChachachayWorker(worker)
    }

    @Test
    @DisplayName("User sync: adding user to tips table when create (picker)")
    fun createUserInTipsTablesPicker() {

        val profileId = commonPreconditions.createProfilePicker(mobile = Constants.mobileChaChaCha1).profileId

        val workerExistance = tipsActions.getChachachayWorkerExistanceFromDB(profileId)

        tipsAssertion.getSoftAssertion().assertThat(workerExistance).isFalse
    }

    @Test
    @DisplayName("User sync: change user role to deliveryman")
    fun changeRoleToDeliveryman() {

        val profileId = commonPreconditions.createProfilePicker(mobile = Constants.mobileChaChaCha1).profileId

        employeePreconditions.setUpdateProfileRequest(
            employeePreconditions
                .fillCreateProfileRequest(
                    roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                    vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
                    email = null,
                    staffPartnerId = Constants.defaultStaffPartnerId,
                    darkstoreId = Constants.darkstoreId,
                    mobile = Constants.mobileChaChaCha1
                ),
            roles = listOf(ApiEnum(EmployeeRole.PICKER), ApiEnum(EmployeeRole.DELIVERYMAN)),
            vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR))
        )

        val workerExistance = tipsActions.getChachachayWorkerExistanceFromDB(profileId)

        employeeActions.updateProfile(
            profileId,
            employeePreconditions.updateProfileRequest()
        )

        val worker = tipsActions.getChachachayWorkerFromDB(profileId)

        tipsAssertion.getSoftAssertion().assertThat(workerExistance).isFalse
        tipsAssertion.checkChachachayWorker(worker)
    }

    @Test
    @Tag("smoke")
    @DisplayName("User sync: change user role from deliveryman")
    fun changeRoleFromDeliveryman() {

        val profileId = commonPreconditions.createProfileDeliveryman(mobile = Constants.mobileChaChaCha1).profileId

        employeePreconditions.setUpdateProfileRequest(
            employeePreconditions
                .fillCreateProfileRequest(
                    roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                    vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
                    email = null,
                    staffPartnerId = Constants.defaultStaffPartnerId,
                    darkstoreId = Constants.darkstoreId,
                    mobile = Constants.mobileChaChaCha1,
                ),
            roles = listOf(ApiEnum(EmployeeRole.PICKER)),
            vehicle = null
        )

        val worker = tipsActions.getChachachayWorkerFromDB(profileId)

        employeeActions.updateProfile(
            profileId,
            employeePreconditions.updateProfileRequest()
        )

        val workerExistance = tipsActions.getChachachayWorkerExistanceFromDB(profileId)

        tipsAssertion.getSoftAssertion().assertThat(workerExistance).isFalse
        tipsAssertion.checkChachachayWorker(worker)
    }

    @Test
    @Tag("smoke")
    @DisplayName("User sync: change phone number")
    fun changePhoneNumber() {

        val profileId = commonPreconditions.createProfileDeliveryman(mobile = Constants.mobileChaChaCha1).profileId

        employeePreconditions.setUpdateProfileRequest(
            employeePreconditions
                .fillCreateProfileRequest(
                    roles = listOf(ApiEnum(EmployeeRole.DELIVERYMAN)),
                    vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
                    email = null,
                    staffPartnerId = Constants.defaultStaffPartnerId,
                    darkstoreId = Constants.darkstoreId,
                    mobile = Constants.mobileChaChaCha1,
                ),
            mobile = Constants.mobileChaChaCha2
        )

        val worker1 = tipsActions.getChachachayWorkerFromDB(profileId)

        employeeActions.updateProfile(
            profileId,
            employeePreconditions.updateProfileRequest()
        )

        val worker2 = tipsActions.getChachachayWorkerFromDB(profileId)
        val workerExistance = tipsActions.getChachachayWorkerExistanceFromDBByMobile(Constants.mobileChaChaCha1)

        tipsAssertion.checkChachachayWorker(worker1, Constants.mobileChaChaCha1)
        tipsAssertion.checkChachachayWorker(worker2, Constants.mobileChaChaCha2)
        tipsAssertion.getSoftAssertion().assertThat(workerExistance).isFalse

    }

    @Test
    @Tag("smoke")
    @DisplayName("User sync: profile disable deliveryman")
    fun disableDeliverymanProfile() {
        val profileId = commonPreconditions.createProfileDeliveryman(mobile = Constants.mobileChaChaCha1).profileId

        val worker = tipsActions.getChachachayWorkerFromDB(profileId)

        employeeActions.deleteProfile(profileId)
        val workerExistance = tipsActions.getChachachayWorkerExistanceFromDB(profileId)

        tipsAssertion.getSoftAssertion().assertThat(workerExistance).isFalse
        tipsAssertion.checkChachachayWorker(worker)
    }

    @Test
    @Tag("smoke")
    @DisplayName("User sync: profile disable deliveryman-picker")
    fun disableDeliverymanPickerProfile() {
        val profileId = commonPreconditions.createProfileDeliverymanPicker(mobile = Constants.mobileChaChaCha1).profileId

        val worker = tipsActions.getChachachayWorkerFromDB(profileId)

        employeeActions.deleteProfile(profileId)
        val workerExistance = tipsActions.getChachachayWorkerExistanceFromDB(profileId)

        tipsAssertion.getSoftAssertion().assertThat(workerExistance).isFalse
        tipsAssertion.checkChachachayWorker(worker)
    }

    @Test
    @Tag("smoke")
    @DisplayName("User sync: profile disable, create new one")
    fun disableAndCreateProfile() {
        val profileId = commonPreconditions.createProfileDeliveryman(mobile = Constants.mobileChaChaCha1).profileId

        employeeActions.deleteProfile(profileId)

        val newProfileId = commonPreconditions.createProfileDeliveryman(
            mobile = Constants.mobileChaChaCha1
        ).profileId

        val worker = tipsActions.getChachachayWorkerFromDB(newProfileId)

        val workerExistance = tipsActions.getChachachayWorkerExistanceFromDB(newProfileId)

        tipsAssertion.getSoftAssertion().assertThat(workerExistance).isTrue
        tipsAssertion.checkChachachayWorker(worker)
    }


}