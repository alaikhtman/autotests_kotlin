package ru.samokat.mysamokat.tests.tests.staff_apigateway.usersAPI

import org.apache.http.HttpStatus
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.samokat.employeeprofiles.api.common.domain.EmployeeVehicleType
import ru.samokat.employeeprofiles.api.common.domain.Vehicle
import ru.samokat.my.domain.employee.EmployeeRole
import ru.samokat.my.rest.api.enum.ApiEnum
import ru.samokat.mysamokat.tests.checkers.CommonAssertion
import ru.samokat.mysamokat.tests.checkers.StaffApiGWAssertions
import ru.samokat.mysamokat.tests.dataproviders.Constants
import ru.samokat.mysamokat.tests.dataproviders.StringAndPhoneNumberGenerator
import ru.samokat.mysamokat.tests.dataproviders.preconditions.CommonPreconditions
import ru.samokat.mysamokat.tests.dataproviders.preconditions.StaffApiGWPreconditions
import ru.samokat.mysamokat.tests.helpers.actions.EmployeeActions
import ru.samokat.mysamokat.tests.helpers.actions.StaffApiGWActions


@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Tags(Tag("staff_apigateway"), Tag("staffMetadataIntegration"))
class Metadata {

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
        employeeActions.deleteProfile(Constants.mobile2)

    }

    @AfterEach
    fun release() {
        staffApiGWAssertions.assertAll()
        commonAssertions.assertAll()
        employeeActions.deleteProfile(Constants.mobile1)
        employeeActions.deleteProfile(Constants.mobile2)

    }


    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Add comment deliveryman by darkstore_admin")
    fun addCommentDeliverymanByDarkstoreAdmin() {
        val tokens = commonPreconditions
            .createAndAuthorizeStaffUser(
                roles = listOf(ApiEnum(EmployeeRole.DARKSTORE_ADMIN)),
                vehicle = null,
                staffPartnerId = null
            )
        staffApiGWPreconditions.fillCommentRequest("Лучший сотрудник месяца")

        val user = commonPreconditions.createProfileDeliveryman(mobile = Constants.mobile2).profileId

        staffApiGWActions.addComment(
            tokens!!.accessToken,
            staffApiGWPreconditions.commentRequest(),
            user.toString()
        )

        staffApiGWAssertions.checkUsersComment(
            actualComment = staffApiGWActions.getUsersByDarkstore(
                tokens.accessToken,
                commonPreconditions.createProfileRequest().darkstoreId.toString(),
                mutableListOf(EmployeeRole.DELIVERYMAN.value)
            ).users.filter { it.id == user }[0].comment,
            expectedComment = staffApiGWPreconditions.commentRequest().comment
        )


    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Add comment picker by goods_manager")
    fun addCommentPickerByGoodsManager() {
        val tokens = commonPreconditions
            .createAndAuthorizeStaffUser(
                roles = listOf(ApiEnum(EmployeeRole.GOODS_MANAGER)),
                vehicle = null,
                staffPartnerId = null
            )
        staffApiGWPreconditions.fillCommentRequest("Лучший сотрудник месяца!")

        val user = commonPreconditions.createProfilePicker(mobile = Constants.mobile2).profileId

        staffApiGWActions.addComment(
            tokens!!.accessToken,
            staffApiGWPreconditions.commentRequest(),
            user.toString()
        )

        staffApiGWAssertions.checkUsersComment(
            actualComment = staffApiGWActions.getUsersByDarkstore(
                tokens.accessToken,
                commonPreconditions.createProfileRequest().darkstoreId.toString(),
                mutableListOf(EmployeeRole.PICKER.value)
            ).users.filter { it.id == user }[0].comment,
            expectedComment = staffApiGWPreconditions.commentRequest().comment
        )

    }


    @Test
    @DisplayName("Add comment = 1 symbols")
    fun addMinComment() {
        val tokens = commonPreconditions
            .createAndAuthorizeStaffUser(
                roles = listOf(ApiEnum(EmployeeRole.GOODS_MANAGER)),
                vehicle = null,
                staffPartnerId = null
            )
        staffApiGWPreconditions.fillCommentRequest("1")

        val user = commonPreconditions.createProfileDeliveryman(mobile = Constants.mobile2).profileId

        staffApiGWActions.addComment(
            tokens!!.accessToken,
            staffApiGWPreconditions.commentRequest(),
            user.toString()
        )

        staffApiGWAssertions.checkUsersComment(
            actualComment = staffApiGWActions.getUsersByDarkstore(
                tokens.accessToken,
                commonPreconditions.createProfileRequest().darkstoreId.toString(),
                mutableListOf(EmployeeRole.DELIVERYMAN.value)
            ).users.filter { it.id == user }[0].comment,
            expectedComment = staffApiGWPreconditions.commentRequest().comment
        )

    }

    @Test
    @DisplayName("Add comment = 256 symbols by coordinator")
    fun addMaxCommentByCoordinator() {
        val tokens = commonPreconditions
            .createAndAuthorizeStaffUser(
                roles = listOf(ApiEnum(EmployeeRole.COORDINATOR)),
                vehicle = null,
                staffPartnerId = null,
                darkstoreId = null,
                supervisedDarkstores = Constants.supervisedDarkstores,
                cityId = null
            )
        staffApiGWPreconditions.fillCommentRequest(StringAndPhoneNumberGenerator.generateRandomString(256))

        val user = commonPreconditions.createProfileDeliveryman(
            mobile = Constants.mobile2,
            darkstoreId = commonPreconditions.createProfileRequest().supervisedDarkstores!!.first()
        ).profileId

        staffApiGWActions.addComment(
            tokens!!.accessToken,
            staffApiGWPreconditions.commentRequest(),
            user.toString()
        )

        staffApiGWAssertions.checkUsersComment(
            actualComment = staffApiGWActions.getUsersByDarkstore(
                tokens.accessToken,
                commonPreconditions.createProfileRequest().darkstoreId.toString(),
                mutableListOf(EmployeeRole.DELIVERYMAN.value)
            ).users.filter { it.id == user }[0].comment,
            expectedComment = staffApiGWPreconditions.commentRequest().comment
        )

    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Edit comment")
    fun editComment() {
        val tokens = commonPreconditions
            .createAndAuthorizeStaffUser(
                roles = listOf(ApiEnum(EmployeeRole.COORDINATOR)),
                vehicle = null,
                staffPartnerId = null,
                darkstoreId = null,
                supervisedDarkstores = Constants.supervisedDarkstores
            )
        staffApiGWPreconditions.fillCommentRequest(StringAndPhoneNumberGenerator.generateRandomString(256))

        val user = commonPreconditions.createProfileDeliveryman(
            mobile = Constants.mobile2,
            darkstoreId = Constants.supervisedDarkstores.first()
        ).profileId

        staffApiGWActions.addComment(
            tokens!!.accessToken,
            staffApiGWPreconditions.commentRequest(),
            user.toString()
        )

        staffApiGWPreconditions.fillCommentRequest(StringAndPhoneNumberGenerator.generateRandomString(37))

        staffApiGWActions.addComment(
            tokens.accessToken,
            staffApiGWPreconditions.commentRequest(),
            user.toString()
        )


        staffApiGWAssertions.checkUsersComment(
            actualComment = staffApiGWActions.getUsersByDarkstore(
                tokens.accessToken,
                commonPreconditions.createProfileRequest().darkstoreId.toString(),
                mutableListOf(EmployeeRole.DELIVERYMAN.value)
            ).users.filter { it.id == user }[0].comment,
            expectedComment = staffApiGWPreconditions.commentRequest().comment
        )

    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Delete comment")
    fun deleteComment() {

        val tokens = commonPreconditions
            .createAndAuthorizeStaffUser(
                roles = listOf(ApiEnum(EmployeeRole.DARKSTORE_ADMIN)),
                vehicle = null,
                staffPartnerId = null
            )
        staffApiGWPreconditions.fillCommentRequest("Лучший сотрудник месяца")

        val user = commonPreconditions.createProfileDeliveryman(mobile = Constants.mobile2).profileId

        staffApiGWActions.addComment(
            tokens!!.accessToken,
            staffApiGWPreconditions.commentRequest(),
            user.toString()
        )
        staffApiGWActions.deleteComment(tokens.accessToken, user.toString())

        staffApiGWAssertions.checkUsersComment(
            actualComment = staffApiGWActions.getUsersByDarkstore(
                tokens.accessToken,
                commonPreconditions.createProfileRequest().darkstoreId.toString(),
                mutableListOf(EmployeeRole.DELIVERYMAN.value)
            ).users.filter { it.id == user }[0].comment,
            expectedComment = null
        )


    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Add comment for foreign user is impossible")
    fun addCommentForeignUser() {
        val tokens = commonPreconditions
            .createAndAuthorizeStaffUser(
                roles = listOf(ApiEnum(EmployeeRole.DARKSTORE_ADMIN)),
                vehicle = null,
                staffPartnerId = null
            )
        val user = commonPreconditions.createProfileDeliveryman(
            mobile = Constants.mobile2,
            darkstoreId = Constants.updatedDarkstoreId,
        ).profileId

        staffApiGWPreconditions.fillCommentRequest("Лучший сотрудник месяца")

        staffApiGWActions.addCommentWithError(
            tokens!!.accessToken,
            staffApiGWPreconditions.commentRequest(),
            user.toString(), HttpStatus.SC_FORBIDDEN
        )
    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Add comment for disabled user is impossible")
    fun addCommentDisabledUser() {

        val tokens = commonPreconditions
            .createAndAuthorizeStaffUser(
                roles = listOf(ApiEnum(EmployeeRole.DARKSTORE_ADMIN)),
                vehicle = null,
                staffPartnerId = null
            )
        val user = commonPreconditions.createProfileDeliveryman(
            mobile = Constants.mobile2,
            darkstoreId = Constants.updatedDarkstoreId,
        ).profileId

        employeeActions.deleteProfile(user)
        staffApiGWPreconditions.fillCommentRequest("Лучший сотрудник месяца")

        staffApiGWActions.addCommentWithError(
            tokens!!.accessToken,
            staffApiGWPreconditions.commentRequest(),
            user.toString(), HttpStatus.SC_NOT_FOUND
        )
    }

    @Test
    @DisplayName("Add comment by supervisor is impossible")
    fun addCommentBySupervisor() {
        val tokens = commonPreconditions
            .createAndAuthorizeStaffUser(
                roles = listOf(ApiEnum(EmployeeRole.SUPERVISOR)),
                vehicle = null,
                staffPartnerId = null,
                darkstoreId = null
            )
        val user = commonPreconditions.createProfileDeliveryman(
            mobile = Constants.mobile2,
            darkstoreId = Constants.updatedDarkstoreId,
        ).profileId

        staffApiGWPreconditions.fillCommentRequest("Лучший сотрудник месяца")

        staffApiGWActions.addCommentWithError(
            tokens!!.accessToken,
            staffApiGWPreconditions.commentRequest(),
            user.toString(), HttpStatus.SC_FORBIDDEN
        )

    }


    @Test
    @DisplayName("Add  more than 256 symbols in comment is impossible")
    fun addTooLongComment() {
        val tokens = commonPreconditions
            .createAndAuthorizeStaffUser(
                roles = listOf(ApiEnum(EmployeeRole.COORDINATOR)),
                vehicle = null,
                staffPartnerId = null,
                darkstoreId = null,
                supervisedDarkstores = Constants.supervisedDarkstores
            )
        staffApiGWPreconditions.fillCommentRequest(StringAndPhoneNumberGenerator.generateRandomString(257))

        val user = commonPreconditions.createProfileDeliveryman(mobile = Constants.mobile2).profileId

        staffApiGWActions.addCommentWithError(
            tokens!!.accessToken,
            staffApiGWPreconditions.commentRequest(),
            user.toString(), HttpStatus.SC_BAD_REQUEST
        )
    }

    @Test
    @DisplayName("Add empty comment is impossible")
    fun addEmptyComment() {
        val tokens = commonPreconditions
            .createAndAuthorizeStaffUser(
                roles = listOf(ApiEnum(EmployeeRole.DARKSTORE_ADMIN)),
                vehicle = null,
                staffPartnerId = null
            )
        staffApiGWPreconditions.fillCommentRequest("")

        val user = commonPreconditions.createProfileDeliveryman(mobile = Constants.mobile2).profileId

        staffApiGWActions.addCommentWithError(
            tokens!!.accessToken,
            staffApiGWPreconditions.commentRequest(),
            user.toString(), HttpStatus.SC_BAD_REQUEST
        )

    }


}