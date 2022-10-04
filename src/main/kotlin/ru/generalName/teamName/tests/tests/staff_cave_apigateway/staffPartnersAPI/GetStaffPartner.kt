package ru.samokat.mysamokat.tests.tests.staff_cave_apigateway.staffPartnersAPI

import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.samokat.my.domain.employee.EmployeeRole
import ru.samokat.my.rest.api.enum.ApiEnum
import ru.samokat.mysamokat.tests.checkers.CommonAssertion
import ru.samokat.mysamokat.tests.checkers.StaffCaveApiGWAssertions
import ru.samokat.mysamokat.tests.dataproviders.Constants
import ru.samokat.mysamokat.tests.dataproviders.preconditions.CommonPreconditions
import ru.samokat.mysamokat.tests.dataproviders.preconditions.StaffCaveApiGWPreconditions
import ru.samokat.mysamokat.tests.helpers.actions.EmployeeActions
import ru.samokat.mysamokat.tests.helpers.actions.StaffCaveApiGWActions

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Tags(Tag("staff-cave-apigateway"))
class GetStaffPartner {

    @Autowired
    private lateinit var scActions: StaffCaveApiGWActions

    @Autowired
    private lateinit var employeeActions: EmployeeActions

    @Autowired
    private lateinit var commonPreconditions: CommonPreconditions

    private lateinit var commonAssertions: CommonAssertion

    private lateinit var scPreconditions: StaffCaveApiGWPreconditions

    private lateinit var scAssertion: StaffCaveApiGWAssertions

    @BeforeEach
    fun before() {
        scPreconditions = StaffCaveApiGWPreconditions()
        scAssertion = StaffCaveApiGWAssertions()
        commonAssertions = CommonAssertion()
        employeeActions.deleteProfile(Constants.mobile1)
    }

    @AfterEach
    fun release() {
        scAssertion.assertAll()
        commonAssertions.assertAll()
        employeeActions.deleteProfile(Constants.mobile1)
    }

    @Test
    @Tags(Tag("contract"))
    @DisplayName("Check staffPartners list contract")
    fun checkStaffPartnerListContract(){
        val profile = commonPreconditions.createProfile(
            roles = listOf(ApiEnum(EmployeeRole.TECH_SUPPORT))
        )
        val authRequest = scPreconditions.fillAuthRequest(password = profile.generatedPassword!!)

        val tokens = scActions.authProfilePassword(authRequest)
        scActions.getStaffPartnersList(tokens.accessToken)!!
    }


}