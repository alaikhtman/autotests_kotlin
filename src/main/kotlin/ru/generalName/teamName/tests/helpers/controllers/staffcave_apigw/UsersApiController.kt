package ru.samokat.mysamokat.tests.helpers.controllers.staffcave_apigw

import io.restassured.RestAssured
import io.restassured.builder.RequestSpecBuilder
import io.restassured.filter.log.LogDetail
import io.restassured.http.ContentType
import io.restassured.module.jsv.JsonSchemaValidator
import org.apache.http.HttpStatus
import org.springframework.stereotype.Service
import ru.samokat.mysamokat.tests.SuiteBase
import ru.samokat.mysamokat.tests.dataproviders.ErrorView
import ru.samokat.mysamokat.tests.dataproviders.apiModels.staffcave.users.*
import ru.samokat.mysamokat.tests.endpoints.StaffCaveApiGatewayEndPoints
import ru.samokat.mysamokat.tests.endpoints.configuration.NetConfig
import java.util.*

@Service
class UsersApiController (restStaffCaveApigateway: NetConfig) {

    private val requestSpecification = RequestSpecBuilder()
        .setBaseUri(restStaffCaveApigateway.url)
        .setPort(restStaffCaveApigateway.port)
        .setAccept(ContentType.JSON)
        .setContentType(ContentType.JSON)
        .log(LogDetail.ALL)
        .build()

    fun createUser(accessToken: String, requestBody: CreateUserRequest): UserWithPasswordView? {

        return  RestAssured.given(this.requestSpecification)
            .header("Authorization", "Bearer $accessToken")
            ?.body(requestBody)
            ?.`when`()
            ?.post(StaffCaveApiGatewayEndPoints.USERS)
            ?.then()!!.log().all().statusCode(HttpStatus.SC_CREATED)
            .assertThat().body(
                JsonSchemaValidator.matchesJsonSchemaInClasspath(
                    "staff-cave-api-gateway/UserWithPasswordViewSchema.json"))
            .extract().`as`(UserWithPasswordView::class.java)
    }

    fun createUserError(accessToken: String, requestBody: CreateUserRequest, sc: Int): ErrorView? {

        val request = RestAssured.given(this.requestSpecification)
            .header("Authorization", "Bearer $accessToken")
            ?.body(requestBody)
            ?.`when`()
            ?.post(StaffCaveApiGatewayEndPoints.USERS)
            ?.then()!!.log().all().statusCode(sc)
            .assertThat().body(
                JsonSchemaValidator.matchesJsonSchemaInClasspath(
                    "staff-cave-api-gateway/ErrorViewSchema.json"))
            .extract().body().path<Any>("")

        return SuiteBase.jacksonObjectMapper.convertValue(request, ErrorView::class.java)
    }

    fun getUserById(accessToken: String, userId: UUID): UserByIdView? {
        return  RestAssured.given(this.requestSpecification)
            .header("Authorization", "Bearer $accessToken")
            ?.`when`()
            ?.get(StaffCaveApiGatewayEndPoints.USER.replace("{userId}", userId.toString()))
            ?.then()!!.log().all().spec(SuiteBase.commonSpecification)
            .assertThat().body(
                JsonSchemaValidator.matchesJsonSchemaInClasspath(
                    "staff-cave-api-gateway/UserWithVersionViewSchema.json"))
            .extract().`as`(UserByIdView::class.java)

    }

    fun getUserByIdError(accessToken: String, userId: UUID) {
        RestAssured.given(this.requestSpecification)
            .header("Authorization", "Bearer $accessToken")
            ?.`when`()
            ?.get(StaffCaveApiGatewayEndPoints.USER.replace("{userId}", userId.toString()))
            ?.then()!!.statusCode(HttpStatus.SC_NOT_FOUND)
    }

    fun updateUser(accessToken: String, requestBody: UpdateUserRequest, userId: UUID): UserView? {

        return  RestAssured.given(this.requestSpecification)
            .header("Authorization", "Bearer $accessToken")
            ?.body(requestBody)
            ?.`when`()
            ?.put(StaffCaveApiGatewayEndPoints.USER.replace("{userId}", userId.toString()))
            ?.then()!!.log().all().statusCode(HttpStatus.SC_OK)
            .assertThat().body(
                JsonSchemaValidator.matchesJsonSchemaInClasspath(
                    "staff-cave-api-gateway/UserViewSchema.json"))
            .extract().`as`(UserView::class.java)
    }

    fun updateUserWithError(accessToken: String, requestBody: UpdateUserRequest, userId: UUID, sc: Int): ErrorView? {

        return  RestAssured.given(this.requestSpecification)
            .header("Authorization", "Bearer $accessToken")
            ?.body(requestBody)
            ?.`when`()
            ?.put(StaffCaveApiGatewayEndPoints.USER.replace("{userId}", userId.toString()))
            ?.then()!!.log().all().statusCode(sc)
            .assertThat().body(
                JsonSchemaValidator.matchesJsonSchemaInClasspath(
                    "staff-cave-api-gateway/ErrorViewSchema.json"))
            .extract().`as`(ErrorView::class.java)
    }

    fun updateUserWithErrorEmptyResult(accessToken: String, requestBody: UpdateUserRequest, userId: UUID, sc: Int) {

       RestAssured.given(this.requestSpecification)
            .header("Authorization", "Bearer $accessToken")
            ?.body(requestBody)
            ?.`when`()
            ?.put(StaffCaveApiGatewayEndPoints.USER.replace("{userId}", userId.toString()))
            ?.then()!!.log().all().statusCode(sc)
    }

    fun deleteUser(accessToken: String, userId: UUID) {

        RestAssured.given(this.requestSpecification)
            .header("Authorization", "Bearer $accessToken")
            ?.`when`()
            ?.delete(StaffCaveApiGatewayEndPoints.USER.replace("{userId}", userId.toString()))
            ?.then()!!.log().all().statusCode(HttpStatus.SC_NO_CONTENT)
    }

    fun deleteUserWithErrorEmptyResult(accessToken: String, userId: UUID) {

        RestAssured.given(this.requestSpecification)
            .header("Authorization", "Bearer $accessToken")
            ?.`when`()
            ?.delete(StaffCaveApiGatewayEndPoints.USER.replace("{userId}", userId.toString()))
            ?.then()!!.log().all().statusCode(HttpStatus.SC_NOT_FOUND)
    }

    fun updatePassword(accessToken: String, userId: UUID): ChangedPasswordView? {

        return  RestAssured.given(this.requestSpecification)
            .header("Authorization", "Bearer $accessToken")
            ?.`when`()
            ?.put(StaffCaveApiGatewayEndPoints.PASSWORD.replace("{userId}", userId.toString()))
            ?.then()!!.spec(SuiteBase.commonSpecification)
            .assertThat().body(
                JsonSchemaValidator.matchesJsonSchemaInClasspath(
                    "staff-cave-api-gateway/ChangePasswordViewSchema.json"))
            .extract().`as`(ChangedPasswordView::class.java)
    }

    fun updatePasswordWithError(accessToken: String, userId: UUID) {

        RestAssured.given(this.requestSpecification)
            .header("Authorization", "Bearer $accessToken")
            ?.`when`()
            ?.put(StaffCaveApiGatewayEndPoints.PASSWORD.replace("{userId}", userId.toString()))
            ?.then()!!.log().all().statusCode(HttpStatus.SC_NOT_FOUND)
    }

    fun updatePasswordWithErrorAndMessage(accessToken: String, userId: UUID, sc: Int): ErrorView? {

        return RestAssured.given(this.requestSpecification)
            .header("Authorization", "Bearer $accessToken")
            ?.`when`()
            ?.put(StaffCaveApiGatewayEndPoints.PASSWORD.replace("{userId}", userId.toString()))
            ?.then()!!.log().all().statusCode(sc)
            .extract().`as`(ErrorView::class.java)
    }

    fun searchProfilesByMobileAndName(accessToken: String, mobile: String, name: String): UserListView? {

        val request =  RestAssured.given(this.requestSpecification)
            .header("Authorization", "Bearer $accessToken")
            ?.`when`()
            ?.queryParam("mobile", mobile)
            ?.queryParam("name", name)
            ?.get(StaffCaveApiGatewayEndPoints.USERS)
            ?.then()!!.spec(SuiteBase.commonSpecification)
            .assertThat().body(
                JsonSchemaValidator.matchesJsonSchemaInClasspath(
                    "staff-cave-api-gateway/UserListViewSchema.json"))

        return request.extract().`as`(UserListView::class.java)
    }

    fun searchProfilesByMobile(accessToken: String, mobile: String): UserListView? {

        val request =  RestAssured.given(this.requestSpecification)
            .header("Authorization", "Bearer $accessToken")
            ?.`when`()
            ?.queryParam("mobile", mobile)
            ?.get(StaffCaveApiGatewayEndPoints.USERS)
            ?.then()!!.spec(SuiteBase.commonSpecification)
            .assertThat().body(
                JsonSchemaValidator.matchesJsonSchemaInClasspath(
                    "staff-cave-api-gateway/UserListViewSchema.json"))
        return request.extract().`as`(UserListView::class.java)
    }

    fun searchProfilesByMobileWithPageSize(accessToken: String, mobile: String, pageSize: String): UserListView? {

        val request =  RestAssured.given(this.requestSpecification)
            .header("Authorization", "Bearer $accessToken")
            ?.`when`()
            ?.queryParam("mobile", mobile)
            ?.queryParam("pageSize", pageSize)
            ?.get(StaffCaveApiGatewayEndPoints.USERS)
            ?.then()!!.spec(SuiteBase.commonSpecification)
            .assertThat().body(
                JsonSchemaValidator.matchesJsonSchemaInClasspath(
                    "staff-cave-api-gateway/UserListViewSchema.json"))
        return request.extract().`as`(UserListView::class.java)
    }

    fun searchProfilesByMobileWithPageSizeAndMark(accessToken: String, mobile: String, pageSize: String, pageMark: String): UserListView? {

        val request =  RestAssured.given(this.requestSpecification)
            .header("Authorization", "Bearer $accessToken")
            ?.`when`()
            ?.queryParam("mobile", mobile)
            ?.queryParam("pageSize", pageSize)
            ?.queryParam("pageMark", pageMark)
            ?.get(StaffCaveApiGatewayEndPoints.USERS)
            ?.then()!!.spec(SuiteBase.commonSpecification)
            .assertThat().body(
                JsonSchemaValidator.matchesJsonSchemaInClasspath(
                    "staff-cave-api-gateway/UserListViewSchema.json"))
        return request.extract().`as`(UserListView::class.java)
    }

    fun searchProfilesWithoutParams(accessToken: String): UserListView? {

        val request =  RestAssured.given(this.requestSpecification)
            .header("Authorization", "Bearer $accessToken")
            ?.`when`()
            ?.get(StaffCaveApiGatewayEndPoints.USERS)
            ?.then()!!.spec(SuiteBase.commonSpecification)
            .assertThat().body(
                JsonSchemaValidator.matchesJsonSchemaInClasspath(
                    "staff-cave-api-gateway/UserListViewSchema.json"))
        return request.extract().`as`(UserListView::class.java)
    }

    fun searchProfilesByName(accessToken: String, name: String): UserListView? {

        val request =  RestAssured.given(this.requestSpecification)
            .header("Authorization", "Bearer $accessToken")
            ?.`when`()
            ?.queryParam("name", name)
            ?.get(StaffCaveApiGatewayEndPoints.USERS)
            ?.then()!!.spec(SuiteBase.commonSpecification)
            .assertThat().body(
                JsonSchemaValidator.matchesJsonSchemaInClasspath(
                    "staff-cave-api-gateway/UserListViewSchema.json"))
        return request.extract().`as`(UserListView::class.java)
    }

}