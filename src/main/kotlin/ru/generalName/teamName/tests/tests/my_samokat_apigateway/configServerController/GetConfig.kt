package ru.samokat.mysamokat.tests.tests.my_samokat_apigateway.configServerController

import org.apache.http.HttpStatus
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.samokat.employeeprofiles.api.common.domain.EmployeeVehicleType
import ru.samokat.employeeprofiles.api.common.domain.Vehicle
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
@Tags(Tag("msmkt-apigateway"))
class GetConfig {

    private lateinit var msmktPreconditions: MySamokatApiGWPreconditions

    @Autowired
    private lateinit var msmktActions: MySamokatApiGWActions

    private lateinit var msmktAssertion: MySamokatApiGWAssertions

    @BeforeEach
    fun before() {
        msmktPreconditions = MySamokatApiGWPreconditions()
        msmktAssertion = MySamokatApiGWAssertions()
    }

    @AfterEach
    fun release() {
        msmktAssertion.assertAll()
    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Get config fot mobile")
    fun getMobileConfigTest() {
        val config = msmktActions.getConfig("mobile")
        msmktAssertion.checkConfig(config!!)
    }

    @Test
    @DisplayName("Get config: not found")
    fun getConfigNotExistsTest(){
        msmktActions.getConfigWithError("some", HttpStatus.SC_NOT_FOUND)
    }

}