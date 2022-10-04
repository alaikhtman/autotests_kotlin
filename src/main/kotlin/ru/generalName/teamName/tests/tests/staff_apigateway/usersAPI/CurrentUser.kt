package ru.samokat.mysamokat.tests.tests.staff_apigateway.usersAPI

import org.apache.http.HttpStatus
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.samokat.my.domain.employee.EmployeeRole
import ru.samokat.my.rest.api.enum.ApiEnum
import ru.samokat.mysamokat.tests.checkers.CommonAssertion
import ru.samokat.mysamokat.tests.checkers.StaffApiGWAssertions
import ru.samokat.mysamokat.tests.dataproviders.Constants
import ru.samokat.mysamokat.tests.dataproviders.dataClasses.staffApiGTW.enum.CurrentUserRole
import ru.samokat.mysamokat.tests.dataproviders.preconditions.CommonPreconditions
import ru.samokat.mysamokat.tests.dataproviders.preconditions.StaffApiGWPreconditions
import ru.samokat.mysamokat.tests.helpers.actions.EmployeeActions
import ru.samokat.mysamokat.tests.helpers.actions.StaffApiGWActions
import java.time.ZoneId

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Tags(Tag("staff_apigateway"), Tag("emproIntegration"), Tag("darkstoreIntegration"))
class CurrentUser {

    private lateinit var staffApiGWPreconditions: StaffApiGWPreconditions

    private lateinit var staffApiGWAssertions: StaffApiGWAssertions
    private lateinit var commonAssertions: CommonAssertion

    @Autowired
    private lateinit var staffApiGWActions: StaffApiGWActions

    @Autowired
    private lateinit var employeeActions: EmployeeActions

    @Autowired
    private lateinit var commonPreconditions: CommonPreconditions

    @BeforeEach
    fun before() {
        staffApiGWPreconditions = StaffApiGWPreconditions()
        staffApiGWAssertions = StaffApiGWAssertions()
        commonAssertions = CommonAssertion()
        employeeActions.deleteProfile(Constants.mobile1)

    }

    @AfterEach
    fun release() {
        staffApiGWAssertions.assertAll()
        commonAssertions.assertAll()
        employeeActions.deleteProfile(Constants.mobile1)

    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Current User: darkstore_admin")
    fun currentUserDarkstoreAdmin() {
        val tokens = commonPreconditions
            .createAndAuthorizeStaffUser(
                roles = listOf(ApiEnum(EmployeeRole.DARKSTORE_ADMIN)),
                vehicle = null,
                staffPartnerId = null
            )

        val currentUser = staffApiGWActions.getMe(tokens!!.accessToken)

        staffApiGWAssertions.checkUserData(
            currentUser,
            commonPreconditions.createProfileRequest(),
            profileId = commonPreconditions.profileResult().profileId,
            roles = mutableListOf(ApiEnum(CurrentUserRole.DARKSTORE_ADMIN)),
            city = "SPB",
            timeZone = ZoneId.of("Europe/Moscow")
        )

    }


    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Current User: goods_manager")
    fun currentUserGoodsManager() {
        val tokens = commonPreconditions
            .createAndAuthorizeStaffUser(
                roles = listOf(ApiEnum(EmployeeRole.GOODS_MANAGER)),
                vehicle = null,
                staffPartnerId = null
            )

        val currentUser = staffApiGWActions.getMe(tokens!!.accessToken)

        staffApiGWAssertions.checkUserData(
            currentUser,
            commonPreconditions.createProfileRequest(),
            profileId = commonPreconditions.profileResult().profileId,
            roles = mutableListOf(ApiEnum(CurrentUserRole.GOODS_MANAGER)),
            city = "SPB",
            timeZone = ZoneId.of("Europe/Moscow")
        )


    }


    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Current User: coordinator")
    fun currentUserCoordinator() {
        val tokens = commonPreconditions
            .createAndAuthorizeStaffUser(
                roles = listOf(ApiEnum(EmployeeRole.COORDINATOR)),
                vehicle = null,
                staffPartnerId = null,
                darkstoreId = null,
                supervisedDarkstores = Constants.supervisedDarkstores,
                cityId = null
            )

        val currentUser = staffApiGWActions.getMe(tokens!!.accessToken)

        staffApiGWAssertions.checkUserData(
            currentUser,
            commonPreconditions.createProfileRequest(),
            profileId = commonPreconditions.profileResult().profileId,
            roles = mutableListOf(ApiEnum(CurrentUserRole.COORDINATOR)),
            supervisedDarkstoresCity = mutableListOf("SPB", "MSK"),
            supervisedDarkstoresTimeZone = mutableListOf(ZoneId.of("Europe/Moscow"), ZoneId.of("Europe/Moscow"))
        )

    }


    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Current User: supervisor")
    fun currentUserSupervisor() {
        val tokens = commonPreconditions
            .createAndAuthorizeStaffUser(
                roles = listOf(ApiEnum(EmployeeRole.SUPERVISOR)),
                vehicle = null,
                staffPartnerId = null,
                darkstoreId = null,
                cityId = null
            )

        val currentUser = staffApiGWActions.getMe(tokens!!.accessToken)

        staffApiGWAssertions.checkUserData(
            currentUser,
            commonPreconditions.createProfileRequest(),
            profileId = commonPreconditions.profileResult().profileId,
            roles = mutableListOf(ApiEnum(CurrentUserRole.SUPERVISOR)),

            )


    }

    @Test
    @DisplayName("Current User: darkstore_admin-goods_manager")
    fun currentUserTwoRoles() {

        val tokens = commonPreconditions
            .createAndAuthorizeStaffUser(
                roles = listOf(ApiEnum(EmployeeRole.DARKSTORE_ADMIN), ApiEnum(EmployeeRole.GOODS_MANAGER)),
                vehicle = null,
                staffPartnerId = null
            )

        val currentUser = staffApiGWActions.getMe(tokens!!.accessToken)

        staffApiGWAssertions.checkUserData(
            currentUser,
            commonPreconditions.createProfileRequest(),
            profileId = commonPreconditions.profileResult().profileId,
            roles = mutableListOf(ApiEnum(CurrentUserRole.DARKSTORE_ADMIN), ApiEnum(CurrentUserRole.GOODS_MANAGER)),
            city = "SPB",
            timeZone = ZoneId.of("Europe/Moscow")
        )

    }


    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Current User: not moscow timezone")
    fun currentUserNotMoscowTimezoneCheck() {
        val tokens = commonPreconditions
            .createAndAuthorizeStaffUser(
                roles = listOf(ApiEnum(EmployeeRole.DARKSTORE_ADMIN)),
                vehicle = null,
                staffPartnerId = null,
                darkstoreId = Constants.darkstoreIdWithNotMoscowTimezone
            )

        val currentUser = staffApiGWActions.getMe(tokens!!.accessToken)

        staffApiGWAssertions.checkUserData(
            currentUser,
            commonPreconditions.createProfileRequest(),
            profileId = commonPreconditions.profileResult().profileId,
            roles = mutableListOf(ApiEnum(CurrentUserRole.DARKSTORE_ADMIN)),
            city = "EKB",
            timeZone = ZoneId.of("Asia/Yekaterinburg")
        )

    }


    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Current User: disabled user")
    fun currentUserDDisabledUser() {
        val tokens = commonPreconditions
            .createAndAuthorizeStaffUser(
                roles = listOf(ApiEnum(EmployeeRole.GOODS_MANAGER)),
                vehicle = null,
                staffPartnerId = null
            )
        employeeActions.deleteProfile(commonPreconditions.profileResult().profileId)

        staffApiGWActions.getMeWithError(tokens!!.accessToken, HttpStatus.SC_FORBIDDEN)


    }


}