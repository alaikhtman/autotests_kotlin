package ru.generalName.teamName.tests.helpers.controllers.apiController

import io.restassured.RestAssured
import io.restassured.builder.RequestSpecBuilder
import io.restassured.filter.log.LogDetail
import io.restassured.http.ContentType
import io.restassured.module.jsv.JsonSchemaValidator
import org.apache.http.HttpStatus
import org.springframework.stereotype.Service



@Service
class StaffUsersApiController(restStaffApigateway: NetConfig) {
    private val requestSpecification = RequestSpecBuilder()
        .setBaseUri(restStaffApigateway.url)
        .setPort(restStaffApigateway.port)
        .setAccept(ContentType.JSON)
        .setContentType(ContentType.JSON)
        .log(LogDetail.ALL)
        .build()

    fun addMetadata(accessToken: String, requestBody: StoreUserMetadataRequest, userId: String) {
        val request = RestAssured.given(this.requestSpecification)
            .header("Authorization", "Bearer $accessToken")
            ?.body(requestBody)
            ?.`when`()
            ?.post(ApiGatewayEndPoints.METADATA.replace("{userId}", userId))
            ?.then()!!.statusCode(HttpStatus.SC_NO_CONTENT)

    }

    fun addMetadataWithError(accessToken: String, requestBody: StoreUserMetadataRequest, userId: String, sc: Int) {
        val request = RestAssured.given(this.requestSpecification)
            .header("Authorization", "Bearer $accessToken")
            ?.body(requestBody)
            ?.`when`()
            ?.post(ApiGatewayEndPoints.METADATA.replace("{userId}", userId))
            ?.then()!!.statusCode(sc).log().all()


    }

    fun searchContract(accessToken: String, requestBody: SearchUsersContractsRequest): SearchUsersContractsView {
        val request = RestAssured.given(this.requestSpecification)
            .header("Authorization", "Bearer $accessToken")
            ?.body(requestBody)
            ?.`when`()
            ?.post(ApiGatewayEndPoints.CONTRACTS)
            ?.then()!!.spec(SuiteBase.commonSpecification)
            .assertThat().body(
                JsonSchemaValidator.matchesJsonSchemaInClasspath(
                    "api-gateway/SearchUserContractSchema.json"
                )
            )
            .extract().body().path<Any>("")

        return SuiteBase.jacksonObjectMapper.convertValue(
            request,
            SearchUsersContractsView::class.java
        )
    }

    fun searchContractWithError(accessToken: String, requestBody: SearchUsersContractsRequest, sc: Int): ErrorView {
        val request = RestAssured.given(this.requestSpecification)
            .header("Authorization", "Bearer $accessToken")
            ?.body(requestBody)
            ?.`when`()
            ?.post(ApiGatewayEndPoints.CONTRACTS)
            ?.then()!!.statusCode(sc).log().all()
            .assertThat().body(
                JsonSchemaValidator.matchesJsonSchemaInClasspath(
                    "api-gateway/ErrorViewSchema.json"
                )
            )
            .extract().body().path<Any>("")

        return SuiteBase.jacksonObjectMapper.convertValue(request, ErrorView::class.java)

    }


    fun getMe(accessToken: String): CurrentUserView {
        val request = RestAssured.given(this.requestSpecification)
            .header("Authorization", "Bearer $accessToken")
            ?.`when`()
            ?.get(ApiGatewayEndPoints.ME)
            ?.then()!!.spec(SuiteBase.commonSpecification)
            .assertThat().body(
                JsonSchemaValidator.matchesJsonSchemaInClasspath(
                    "api-gateway/CurrentUserSchema.json"
                )
            )
            .extract().body().path<Any>("")

        return SuiteBase.jacksonObjectMapper.convertValue(
            request,
            CurrentUserView::class.java
        )
    }

    fun getMeError(accessToken: String, sc: Int): ErrorView {
        val request = RestAssured.given(this.requestSpecification)
            .header("Authorization", "Bearer $accessToken")
            ?.`when`()
            ?.get(ApiGatewayEndPoints.ME)
            ?.then()!!.statusCode(sc).log().all()
            .assertThat().body(
                JsonSchemaValidator.matchesJsonSchemaInClasspath(
                    "api-gateway/ErrorViewSchema.json"
                )
            )
            .extract().body().path<Any>("")

        return SuiteBase.jacksonObjectMapper.convertValue(request, ErrorView::class.java)

    }

    fun getUsers(accessToken: String, darkstoreId: String, userRoles: MutableList<String>): EmployeeListView {
        val request = RestAssured.given(this.requestSpecification)
            .header("Authorization", "Bearer $accessToken")
            ?.queryParam("userRoles", userRoles)
            ?.`when`()
            ?.get(ApiGatewayEndPoints.USERS.replace("{darkstoreId}", darkstoreId))
            ?.then()!!.spec(SuiteBase.commonSpecification)
            .assertThat().body(
                JsonSchemaValidator.matchesJsonSchemaInClasspath(
                    "api-gateway/EmployeeListSchema.json"
                )
            )
            .extract().body().path<Any>("")


        return SuiteBase.jacksonObjectMapper.convertValue(
            request,
            EmployeeListView::class.java
        )
    }

    fun getUsersWithError(
        accessToken: String,
        darkstoreId: String,
        userRoles: MutableList<String>,
        sc: Int
    ): ErrorView {
        val request = RestAssured.given(this.requestSpecification)
            .header("Authorization", "Bearer $accessToken")
            ?.queryParam("userRoles", userRoles)
            ?.`when`()
            ?.get(ApiGatewayEndPoints.USERS.replace("{darkstoreId}", darkstoreId))
            ?.then()!!.statusCode(sc).log().all()
            .assertThat().body(
                JsonSchemaValidator.matchesJsonSchemaInClasspath(
                    "api-gateway/ErrorViewSchema.json"
                )
            )
            .extract().body().path<Any>("")

        return SuiteBase.jacksonObjectMapper.convertValue(request, ErrorView::class.java)

    }

    fun getStatistics(
        accessToken: String,
        darkstoreId: String,
        userRoles: MutableList<String>,
        from: Long,
        to: Long
    ): UsersStatisticsView {
        val request = RestAssured.given(this.requestSpecification)
            .header("Authorization", "Bearer $accessToken")
            ?.queryParam("from", from)
            ?.queryParam("to", to)
            ?.queryParam("userRoles", userRoles)
            ?.`when`()
            ?.get(ApiGatewayEndPoints.STATISTICS.replace("{darkstoreId}", darkstoreId))
            ?.then()!!.spec(SuiteBase.commonSpecification)
            .assertThat().body(
                JsonSchemaValidator.matchesJsonSchemaInClasspath(
                    "api-gateway/UsersStatisticsSchema.json"
                )
            )
            .extract().body().path<Any>("")


        return SuiteBase.jacksonObjectMapper.convertValue(
            request,
            UsersStatisticsView::class.java
        )
    }

    fun getStatisticsWithError(
        accessToken: String,
        darkstoreId: String,
        userRoles: MutableList<String>,
        from: Long,
        to: Long,
        sc: Int
    ): ErrorView {
        val request = RestAssured.given(this.requestSpecification)
            .header("Authorization", "Bearer $accessToken")
            ?.queryParam("from", from)
            ?.queryParam("to", to)
            ?.queryParam("userRoles", userRoles)
            ?.`when`()
            ?.get(ApiGatewayEndPoints.STATISTICS.replace("{darkstoreId}", darkstoreId))
            ?.then()!!.statusCode(sc).log().all()
            .assertThat().body(
                JsonSchemaValidator.matchesJsonSchemaInClasspath(
                    "api-gateway/ErrorViewSchema.json"
                )
            )
            .extract().body().path<Any>("")


        return SuiteBase.jacksonObjectMapper.convertValue(
            request,
            ErrorView::class.java
        )
    }

    fun getAssignees(
        accessToken: String,
        name: String,
        userRoles: MutableList<String>,
        searchFrom: Long,
        searchTo: Long,
        assignTo: Long,
        assignFrom: Long
    ): AssigneeListView {
        val request = RestAssured.given(this.requestSpecification)
            .header("Authorization", "Bearer $accessToken")
            ?.queryParam("name", name)
            ?.queryParam("searchFrom", searchFrom)
            ?.queryParam("searchTo", searchTo)
            ?.queryParam("assignFrom", assignFrom)
            ?.queryParam("assignTo", assignTo)
            ?.queryParam("userRoles", userRoles)
            ?.`when`()
            ?.get(ApiGatewayEndPoints.ASSIGNEES)
            ?.then()!!.spec(SuiteBase.commonSpecification)
            .assertThat().body(
                JsonSchemaValidator.matchesJsonSchemaInClasspath(
                    "api-gateway/AssigneeListSchema.json"
                )
            )
            .extract().body().path<Any>("")


        return SuiteBase.jacksonObjectMapper.convertValue(
            request,
            AssigneeListView::class.java
        )
    }

    fun getAssigneesWithError(
        accessToken: String,
        name: String,
        userRoles: MutableList<String>,
        searchFrom: Long,
        searchTo: Long,
        assignTo: Long,
        assignFrom: Long,
        sc: Int
    ): ErrorView {
        val request = RestAssured.given(this.requestSpecification)
            .header("Authorization", "Bearer $accessToken")
            ?.queryParam("name", name)
            ?.queryParam("searchFrom", searchFrom)
            ?.queryParam("searchTo", searchTo)
            ?.queryParam("assignFrom", assignFrom)
            ?.queryParam("assignTo", assignTo)
            ?.queryParam("userRoles", userRoles)
            ?.`when`()
            ?.get(ApiGatewayEndPoints.ASSIGNEES)
            ?.then()!!.statusCode(sc).log().all()
            .assertThat().body(
                JsonSchemaValidator.matchesJsonSchemaInClasspath(
                    "api-gateway/ErrorViewSchema.json"
                )
            )
            .extract().body().path<Any>("")

        return SuiteBase.jacksonObjectMapper.convertValue(request, ErrorView::class.java)

    }

}