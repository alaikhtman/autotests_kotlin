package ru.samokat.mysamokat.tests.tests.shifts.activeShiftsAPI

import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.samokat.employeeprofiles.api.common.domain.Vehicle
import ru.samokat.my.domain.employee.EmployeeRole
import ru.samokat.employeeprofiles.api.common.domain.EmployeeVehicleType
import ru.samokat.my.rest.api.enum.ApiEnum
import ru.samokat.mysamokat.tests.checkers.ShiftAssertion
import ru.samokat.mysamokat.tests.dataproviders.Constants
import ru.samokat.mysamokat.tests.dataproviders.preconditions.CommonPreconditions
import ru.samokat.mysamokat.tests.dataproviders.preconditions.EmployeePreconditions
import ru.samokat.mysamokat.tests.dataproviders.preconditions.ShiftsPreconditions
import ru.samokat.mysamokat.tests.helpers.actions.EmployeeActions
import ru.samokat.mysamokat.tests.helpers.actions.ShiftsActions
import ru.samokat.shifts.api.common.domain.DeliveryMethod
import ru.samokat.shifts.api.common.domain.ShiftUserPermission
import ru.samokat.shifts.api.common.domain.ShiftUserRole
import java.time.Instant
import java.util.*

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Tag("shifts")
class OpenShift {

    private lateinit var shiftsPreconditions: ShiftsPreconditions

    @Autowired
    private lateinit var employeeActions: EmployeeActions

    private lateinit var employeePreconditions: EmployeePreconditions

    @Autowired
    private lateinit var shiftsActions: ShiftsActions


    @Autowired
    private lateinit var commonPreconditions: CommonPreconditions

    private lateinit var shiftsAssertion: ShiftAssertion

    @BeforeEach
    fun before() {
        shiftsPreconditions = ShiftsPreconditions()
        employeePreconditions = EmployeePreconditions()
        shiftsAssertion = ShiftAssertion()
        employeeActions.deleteProfile(Constants.mobile1)
    }

    @AfterEach
    fun release() {
        shiftsAssertion.assertAll()
        employeeActions.deleteProfile(Constants.mobile1)
    }

    @Test
    @Tags(Tag("smoke"), Tag("kafka_consume"), Tag("darkstore_integration"))
    @DisplayName("Open deliveryman shift")
    fun openDeliverymanShift() {

        val profileId = commonPreconditions.createProfileDeliveryman().profileId
        val openShiftRequest = shiftsPreconditions.fillStartShiftRequest(
            userId = profileId,
            deliveryMethod = null
        )
        val shift = shiftsActions.startShift(openShiftRequest)
        val kafkaEvent = shiftsActions.getMessageFromKafkaActiveShiftsLogLog(shift.shiftId)

        shiftsAssertion
            .checkStartShiftInfo(openShiftRequest, shift, null, Instant.now())
            .checkUserPermissions(shift.userPermissions, listOf(ApiEnum(ShiftUserPermission.DELIVERY)))
            .checkActiveShiftsLogMessage(shift, kafkaEvent!!)
    }

    @Test
    @Tags(Tag("smoke"), Tag("kafka_consume"))
    @DisplayName("Open deliveryman shift by car on darkstore")
    fun openDeliverymanShiftByCarOnDarkstore() {

        val profileId = commonPreconditions.createProfileDeliveryman().profileId
        val openShiftRequest = shiftsPreconditions.fillStartShiftRequest(
            userId = profileId,
            deliveryMethod = DeliveryMethod.BY_CAR
        )
        val shift = shiftsActions.startShift(openShiftRequest)
        val kafkaEvent = shiftsActions.getMessageFromKafkaActiveShiftsLogLog(shift.shiftId)

        shiftsAssertion
            .checkStartShiftInfo(openShiftRequest, shift, ApiEnum(DeliveryMethod.BY_CAR), Instant.now())
            .checkUserPermissions(shift.userPermissions, listOf(ApiEnum(ShiftUserPermission.DELIVERY)))
            .checkActiveShiftsLogMessage(shift, kafkaEvent!!)
    }

    @Test
    @Tags(Tag("smoke"), Tag("kafka_consume"))
    @DisplayName("Open deliveryman shift by car on hub")
    fun openDeliverymanShiftByCarOnHub() {

        val profileId = commonPreconditions.createProfileDeliveryman().profileId
        val openShiftRequest = shiftsPreconditions.fillStartShiftRequest(
            userId = profileId,
            deliveryMethod = DeliveryMethod.BY_CAR,
            darkstoreId = Constants.hubId
        )
        val shift = shiftsActions.startShift(openShiftRequest)
        val kafkaEvent = shiftsActions.getMessageFromKafkaActiveShiftsLogLog(shift.shiftId)

        shiftsAssertion
            .checkStartShiftInfo(openShiftRequest, shift, ApiEnum(DeliveryMethod.BY_CAR), Instant.now())
            .checkUserPermissions(
                shift.userPermissions, listOf(
                    ApiEnum(ShiftUserPermission.DELIVERY),
                    ApiEnum(ShiftUserPermission.HUB_DELIVERY)
                )
            )
            .checkActiveShiftsLogMessage(shift, kafkaEvent!!)
    }

    @Test
    @Tags(Tag("smoke"), Tag("kafka_consume"))
    @DisplayName("Open picker shift")
    fun openPickerShift() {

        val profileId = commonPreconditions.createProfilePicker().profileId
        val openShiftRequest = shiftsPreconditions.fillStartShiftRequest(
            userId = profileId, userRole = ShiftUserRole.PICKER
        )
        val shift = shiftsActions.startShift(openShiftRequest)
        val kafkaEvent = shiftsActions.getMessageFromKafkaActiveShiftsLogLog(shift.shiftId)

        shiftsAssertion
            .checkStartShiftInfo(openShiftRequest, shift, null, Instant.now())
            .checkUserPermissions(shift.userPermissions, listOf(ApiEnum(ShiftUserPermission.PICKING)))
            .checkActiveShiftsLogMessage(shift, kafkaEvent!!)
    }

    @Test
    @Tags(Tag("smoke"), Tag("kafka_consume"))
    @DisplayName("Open receiving clerk shift")
    fun openReceivingClerkShift() {

        val profileId = commonPreconditions.createProfile(
            roles = listOf(ApiEnum(EmployeeRole.RECEIVING_CLERK)), darkstoreId = Constants.darkstoreId,
            cityId = Constants.cityId
        ).profileId
        val openShiftRequest = shiftsPreconditions.fillStartShiftRequest(
            userId = profileId,
            userRole = ShiftUserRole.RECEIVING_CLERK
        )
        val shift = shiftsActions.startShift(openShiftRequest)
        val kafkaEvent = shiftsActions.getMessageFromKafkaActiveShiftsLogLog(shift.shiftId)

        shiftsAssertion
            .checkStartShiftInfo(openShiftRequest, shift, null, Instant.now())
            .checkUserPermissions(shift.userPermissions, listOf(ApiEnum(ShiftUserPermission.RECEIVING)))
            .checkActiveShiftsLogMessage(shift, kafkaEvent!!)
    }

    @Test
    @Tags(Tag("smoke"), Tag("kafka_consume"))
    @DisplayName("Open goods manager shift on darkstore")
    fun openGoodsManagerShiftOnDarkstore() {

        val profileId = commonPreconditions.createProfileGoodsManager().profileId
        val openShiftRequest = shiftsPreconditions.fillStartShiftRequest(
            userId = profileId,
            userRole = ShiftUserRole.GOODS_MANAGER
        )
        val shift = shiftsActions.startShift(openShiftRequest)
        val kafkaEvent = shiftsActions.getMessageFromKafkaActiveShiftsLogLog(shift.shiftId)

        shiftsAssertion
            .checkStartShiftInfo(openShiftRequest, shift, null, Instant.now())
            .checkUserPermissions(
                shift.userPermissions, listOf(
                    ApiEnum(ShiftUserPermission.PICKING),
                    ApiEnum(ShiftUserPermission.RECEIVING),
                    ApiEnum(ShiftUserPermission.GOODS_OPERATIONS),
                    ApiEnum(ShiftUserPermission.MANAGEMENT),
                    ApiEnum(ShiftUserPermission.DELIVERY)
                )
            )
            .checkActiveShiftsLogMessage(shift, kafkaEvent!!)
    }

    @Test
    @Tag("kafka_consume")
    @DisplayName("Open goods manager shift on hub")
    fun openGoodsManagerShiftOnHub() {

        val profileId = commonPreconditions.createProfileGoodsManager().profileId
        val openShiftRequest = shiftsPreconditions.fillStartShiftRequest(
            userId = profileId,
            darkstoreId = Constants.hubId,
            userRole = ShiftUserRole.GOODS_MANAGER
        )
        val shift = shiftsActions.startShift(openShiftRequest)
        val kafkaEvent = shiftsActions.getMessageFromKafkaActiveShiftsLogLog(shift.shiftId)

        shiftsAssertion
            .checkStartShiftInfo(openShiftRequest, shift, null, Instant.now())
            .checkUserPermissions(
                shift.userPermissions, listOf(
                    ApiEnum(ShiftUserPermission.PICKING),
                    ApiEnum(ShiftUserPermission.RECEIVING),
                    ApiEnum(ShiftUserPermission.GOODS_OPERATIONS),
                    ApiEnum(ShiftUserPermission.MANAGEMENT),
                    ApiEnum(ShiftUserPermission.DELIVERY),
                    ApiEnum(ShiftUserPermission.HUB_DELIVERY)
                )
            )
            .checkActiveShiftsLogMessage(shift, kafkaEvent!!)
    }

    @Test
    @Tags(Tag("smoke"), Tag("kafka_consume"))
    @DisplayName("Open darkstore admin shift on darkstore")
    fun openDarkstoreAdminShiftOnDarkstore() {

        val profileId = commonPreconditions.createProfileDarkstoreAdmin().profileId

        val openShiftRequest = shiftsPreconditions.fillStartShiftRequest(
            userId = profileId,
            userRole = ShiftUserRole.DARKSTORE_ADMIN
        )
        val shift = shiftsActions.startShift(openShiftRequest)
        val kafkaEvent = shiftsActions.getMessageFromKafkaActiveShiftsLogLog(shift.shiftId)

        shiftsAssertion
            .checkStartShiftInfo(openShiftRequest, shift, null, Instant.now())
            .checkUserPermissions(
                shift.userPermissions, listOf(
                    ApiEnum(ShiftUserPermission.PICKING),
                    ApiEnum(ShiftUserPermission.RECEIVING),
                    ApiEnum(ShiftUserPermission.GOODS_OPERATIONS),
                    ApiEnum(ShiftUserPermission.MANAGEMENT),
                    ApiEnum(ShiftUserPermission.DELIVERY)
                )
            )
            .checkActiveShiftsLogMessage(shift, kafkaEvent!!)
    }

    @Test
    @Tag("kafka_consume")
    @DisplayName("Open darkstore admin shift on hub")
    fun openDarkstoreAdminShiftOnHub() {

        val profileId = commonPreconditions.createProfileDarkstoreAdmin().profileId
        val openShiftRequest = shiftsPreconditions.fillStartShiftRequest(
            userId = profileId,
            darkstoreId = Constants.hubId,
            userRole = ShiftUserRole.DARKSTORE_ADMIN
        )
        val shift = shiftsActions.startShift(openShiftRequest)
        val kafkaEvent = shiftsActions.getMessageFromKafkaActiveShiftsLogLog(shift.shiftId)

        shiftsAssertion
            .checkStartShiftInfo(openShiftRequest, shift, null, Instant.now())
            .checkUserPermissions(
                shift.userPermissions, listOf(
                    ApiEnum(ShiftUserPermission.PICKING),
                    ApiEnum(ShiftUserPermission.RECEIVING),
                    ApiEnum(ShiftUserPermission.GOODS_OPERATIONS),
                    ApiEnum(ShiftUserPermission.MANAGEMENT),
                    ApiEnum(ShiftUserPermission.DELIVERY),
                    ApiEnum(ShiftUserPermission.HUB_DELIVERY)
                )
            )
            .checkActiveShiftsLogMessage(shift, kafkaEvent!!)
    }

    @Test
    @Tags(Tag("smoke"), Tag("kafka_consume"))
    @DisplayName("Open forwarder shift on hub by car")
    fun openForwarderShiftOnHubByCar() {

        val profileId = commonPreconditions.createProfile(
            roles = listOf(ApiEnum(EmployeeRole.FORWARDER)),
            vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
            darkstoreId = Constants.darkstoreId,
            cityId = Constants.cityId
        ).profileId
        val openShiftRequest = shiftsPreconditions.fillStartShiftRequest(
            userId = profileId,
            darkstoreId = Constants.hubId,
            deliveryMethod = DeliveryMethod.BY_CAR,
            userRole = ShiftUserRole.FORWARDER
        )
        val shift = shiftsActions.startShift(openShiftRequest)
        val kafkaEvent = shiftsActions.getMessageFromKafkaActiveShiftsLogLog(shift.shiftId)

        shiftsAssertion
            .checkStartShiftInfo(openShiftRequest, shift, null, Instant.now())
            .checkUserPermissions(
                shift.userPermissions, listOf(
                    ApiEnum(ShiftUserPermission.HUB_DELIVERY)
                )
            )
            .checkActiveShiftsLogMessage(shift, kafkaEvent!!)
    }

    @Test
    @Tag("kafka_consume")
    @DisplayName("Open forwarder shift on hub")
    fun openForwarderShiftOnHub() {

        val profileId = commonPreconditions.createProfile(
            roles = listOf(ApiEnum(EmployeeRole.FORWARDER)),
            vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
            darkstoreId = Constants.darkstoreId,
            cityId = Constants.cityId
        ).profileId
        val openShiftRequest = shiftsPreconditions.fillStartShiftRequest(
            userId = profileId,
            darkstoreId = Constants.hubId,
            userRole = ShiftUserRole.FORWARDER
        )
        val shift = shiftsActions.startShift(openShiftRequest)
        val kafkaEvent = shiftsActions.getMessageFromKafkaActiveShiftsLogLog(shift.shiftId)

        shiftsAssertion
            .checkStartShiftInfo(openShiftRequest, shift, null, Instant.now())
            .checkUserPermissions(
                shift.userPermissions, listOf(
                    ApiEnum(ShiftUserPermission.HUB_DELIVERY)
                )
            )
            .checkActiveShiftsLogMessage(shift, kafkaEvent!!)
    }

    @Test
    @DisplayName("Open forwarder shift on darkstore")
    fun openForwarderShiftOnDarkstore() {

        val profileId = commonPreconditions.createProfile(
            roles = listOf(ApiEnum(EmployeeRole.FORWARDER)),
            vehicle = Vehicle(ApiEnum(EmployeeVehicleType.CAR)),
            darkstoreId = Constants.darkstoreId,
            cityId = Constants.cityId
        ).profileId
        val openShiftRequest = shiftsPreconditions.fillStartShiftRequest(
            userId = profileId,
            userRole = ShiftUserRole.FORWARDER
        )
        val errorMessage = shiftsActions.startShiftWithError(openShiftRequest).message

        shiftsAssertion
            .checkErrorMessage(errorMessage, "Forwarders are not allowed to start shifts on non-hub darkstores")
    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Open shift with other roles")
    fun openShiftWithOtherRole() {

        val profileId = commonPreconditions.createProfilePicker().profileId
        val openShiftRequest = shiftsPreconditions.fillStartShiftRequest(
            userId = profileId, userRole = ShiftUserRole.DELIVERYMAN
        )
        val errorMessage = shiftsActions.startShiftWithError(openShiftRequest).message

        shiftsAssertion
            .checkErrorMessage(errorMessage, "User does not have the role with which shift would be started")
    }

    @Test
    @Tag("empro_integration")
    @DisplayName("Open shift with blocked user")
    fun openShiftWithBlockedUser() {

        val profileId = commonPreconditions.createProfilePicker().profileId
        employeeActions.deleteProfile(profileId)
        val openShiftRequest = shiftsPreconditions.fillStartShiftRequest(
            userId = profileId, userRole = ShiftUserRole.DELIVERYMAN
        )
        val errorMessage = shiftsActions.startShiftWithError(openShiftRequest).message

        shiftsAssertion
            .checkErrorMessage(errorMessage, "User was not found")
    }

    @Test
    @DisplayName("Open shift with invalid darkstoreId")
    fun openShiftWithInvalidDarkstoreId() {

        val profileId = commonPreconditions.createProfilePicker().profileId
        val openShiftRequest = shiftsPreconditions.fillStartShiftRequest(
            userId = profileId, userRole = ShiftUserRole.PICKER,
            darkstoreId = UUID.randomUUID()
        )
        val errorMessage = shiftsActions.startShiftWithError(openShiftRequest).message

        shiftsAssertion
            .checkErrorMessage(errorMessage, "Darkstore was not found")
    }

    @Test
    @DisplayName("Open shift twice")
    fun openShiftTwice() {

        val profileId = commonPreconditions.createProfilePicker().profileId
        val openShiftRequest = shiftsPreconditions.fillStartShiftRequest(
            userId = profileId, userRole = ShiftUserRole.PICKER
        )
        val shift1 = shiftsActions.startShift(openShiftRequest)
        val shift2 = shiftsActions.startShift(openShiftRequest)

        shiftsAssertion
            .checkStartShiftInfo(openShiftRequest, shift2, null, Instant.now())
            .checkUserPermissions(shift2.userPermissions, listOf(ApiEnum(ShiftUserPermission.PICKING)))
    }

    @Test
    @Tags(Tag("smoke"), Tag("kafka_consume"))
    @DisplayName("Open other shift (multirole)")
    fun openOtherShiftMultirole() {

        val profileId = commonPreconditions.createProfileDeliverymanPicker().profileId
        val openShiftPickerRequest = shiftsPreconditions.fillStartShiftRequest(
            userId = profileId, userRole = ShiftUserRole.PICKER
        )
        val openShiftDeliverymanRequest = shiftsPreconditions.fillStartShiftRequest(
            userId = profileId, userRole = ShiftUserRole.PICKER
        )
        val shiftPicker = shiftsActions.startShift(openShiftPickerRequest)
        val shiftDeliveryman = shiftsActions.startShift(openShiftDeliverymanRequest)
        val kafkaEvent = shiftsActions.getMessageFromKafkaActiveShiftsLogLog(shiftPicker.shiftId)

        shiftsAssertion
            .checkStartShiftInfo(openShiftPickerRequest, shiftPicker, null, Instant.now())
            .checkUserPermissions(shiftPicker.userPermissions, listOf(ApiEnum(ShiftUserPermission.PICKING)))
            .checkActiveShiftsLogMessage(shiftPicker, kafkaEvent!!)
    }

    @Test
    @DisplayName("Open shift - darkstore not active")
    fun openShiftWithInactiveDarkstoreId() {

        val profileId = commonPreconditions.createProfilePicker(darkstoreId = Constants.inactiveDarkstore).profileId
        val openShiftRequest = shiftsPreconditions.fillStartShiftRequest(
            userId = profileId, userRole = ShiftUserRole.PICKER,
            darkstoreId = Constants.inactiveDarkstore
        )
        val errorMessage = shiftsActions.startShiftWithError(openShiftRequest).message

        shiftsAssertion
            .checkErrorMessage(errorMessage, "Darkstore was not found")
    }
}

