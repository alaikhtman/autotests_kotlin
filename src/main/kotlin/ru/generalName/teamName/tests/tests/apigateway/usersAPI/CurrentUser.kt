package ru.generalName.teamName.tests.tests.apigateway.usersAPI

import org.apache.http.HttpStatus
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.samokat.my.domain.employee.EmployeeRole
import ru.samokat.my.rest.api.enum.ApiEnum
import ru.generalName.teamName.tests.checkers.CommonAssertion
import ru.generalName.teamName.tests.checkers.ApiGWAssertions
import ru.generalName.teamName.tests.dataproviders.Constants
import ru.generalName.teamName.tests.dataproviders.dataClasses.apiGTW.enum.UserRole
import ru.generalName.teamName.tests.dataproviders.preconditions.CommonPreconditions
import ru.generalName.teamName.tests.dataproviders.preconditions.ApiGWPreconditions
import ru.generalName.teamName.tests.helpers.actions.ProfileActions
import ru.generalName.teamName.tests.helpers.actions.ApiGWActions
import java.time.ZoneId

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Tags(Tag("staff_apigateway"), Tag("emproIntegration"), Tag("darkstoreIntegration"))
class CurrentUser {

    private lateinit var apiGWPreconditions: ApiGWPreconditions

    private lateinit var apiGWAssertions: ApiGWAssertions
    private lateinit var commonAssertions: CommonAssertion

    @Autowired
    private lateinit var apiGWActions: ApiGWActions

    @Autowired
    private lateinit var profileActions: ProfileActions

    @Autowired
    private lateinit var commonPreconditions: CommonPreconditions

    @BeforeEach
    fun before() {
        apiGWPreconditions = ApiGWPreconditions()
        apiGWAssertions = ApiGWAssertions()
        commonAssertions = CommonAssertion()
        profileActions.deleteProfile(Constants.mobile1)

    }

    @AfterEach
    fun release() {
        apiGWAssertions.assertAll()
        commonAssertions.assertAll()
        profileActions.deleteProfile(Constants.mobile1)

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

        val currentUser = apiGWActions.getMe(tokens!!.accessToken)

        apiGWAssertions.checkUserData(
            currentUser,
            commonPreconditions.createProfileRequest(),
            profileId = commonPreconditions.profileResult().profileId,
            roles = mutableListOf(ApiEnum(UserRole.DARKSTORE_ADMIN)),
            city = "SPB",
            timeZone = ZoneId.of("Europe/Moscow")
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
        profileActions.deleteProfile(commonPreconditions.profileResult().profileId)

        apiGWActions.getMeWithError(tokens!!.accessToken, HttpStatus.SC_FORBIDDEN)


    }


}