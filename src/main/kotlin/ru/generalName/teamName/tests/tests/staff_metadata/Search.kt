package ru.samokat.mysamokat.tests.tests.staff_metadata

import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.samokat.mysamokat.tests.checkers.CommonAssertion
import ru.samokat.mysamokat.tests.checkers.StaffMetadataAssertion
import ru.samokat.mysamokat.tests.dataproviders.Constants
import ru.samokat.mysamokat.tests.dataproviders.StringAndPhoneNumberGenerator
import ru.samokat.mysamokat.tests.dataproviders.preconditions.StaffMetadataPreconditions
import ru.samokat.mysamokat.tests.helpers.actions.StaffMetadataActions

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Tag("staffMetadata")
class Search {

    private lateinit var staffMetadataAssertion: StaffMetadataAssertion

    @Autowired
    private lateinit var staffMetadataActions: StaffMetadataActions

    private lateinit var staffMetadataPreconditions: StaffMetadataPreconditions

    @Autowired
    private lateinit var commonAssertion: CommonAssertion

    @BeforeEach
    fun before() {
        staffMetadataPreconditions = StaffMetadataPreconditions()
        staffMetadataAssertion = StaffMetadataAssertion()
        staffMetadataActions.deleteUserMetadataInDb(Constants.uuidForComment1)
        staffMetadataActions.deleteUserMetadataInDb(Constants.uuidForComment2)
        staffMetadataActions.deleteUserMetadataInDb(Constants.uuidForComment3)
    }

    @AfterEach
    fun release() {
        commonAssertion.assertAll()
        staffMetadataAssertion.assertAll()
        staffMetadataActions.deleteUserMetadataInDb(Constants.uuidForComment1)
        staffMetadataActions.deleteUserMetadataInDb(Constants.uuidForComment2)
        staffMetadataActions.deleteUserMetadataInDb(Constants.uuidForComment3)
    }

    @Test
    @Tag("smoke")
    @DisplayName("Search 1 user metadata")
    fun searchOneUserMetadata() {
        val comment1 = StringAndPhoneNumberGenerator.generateRandomString(10)
        staffMetadataActions.setMetadataById(Constants.uuidForComment1, comment1)
        val userIds = mutableListOf(Constants.uuidForComment1)

        val searchBuilder = staffMetadataPreconditions.setSearchCommentBuilder(userIds)

        val responseComment1 = staffMetadataActions.getResponseCommentById(searchBuilder, Constants.uuidForComment1)
        staffMetadataAssertion.checkCommentInSearchResponse(responseComment1, comment1)
    }

    @Test
    @Tag("smoke")
    @DisplayName("Search 3 user metadata")
    fun searchManyUserMetadata() {
        val userIds = mutableListOf(Constants.uuidForComment1, Constants.uuidForComment2, Constants.uuidForComment3)

        val comment1 = StringAndPhoneNumberGenerator.generateRandomString(10)
        val comment2 = StringAndPhoneNumberGenerator.generateRandomString(1)
        val comment3 = StringAndPhoneNumberGenerator.generateRandomString(256)
        val comments = mutableListOf(comment1, comment2, comment3)

        staffMetadataActions.setManyMetadataById(userIds, comments)

        val searchBuilder = staffMetadataPreconditions.setSearchCommentBuilder(userIds)

        val responseComment1 = staffMetadataActions.getResponseCommentById(searchBuilder, Constants.uuidForComment1)
        val responseComment2 = staffMetadataActions.getResponseCommentById(searchBuilder, Constants.uuidForComment2)
        val responseComment3 = staffMetadataActions.getResponseCommentById(searchBuilder, Constants.uuidForComment3)
        val responseComments = mutableListOf(responseComment1, responseComment2, responseComment3)

        staffMetadataAssertion.checkCommentsInSearchResponse(responseComments,comments)
    }


    @Test
    @DisplayName("No metadata in DB")
    fun noMetadataInDB() {
        val searchBuilder =
            staffMetadataPreconditions.setSearchCommentBuilder(userIds = mutableListOf(Constants.uuidForComment1))

        val responseComment1 = staffMetadataActions.getMetadataByListOfIds(searchBuilder)
        staffMetadataAssertion.checkEmptyMetadata(responseComment1)

    }

    @Test
    @DisplayName("Search metadata by ID - more than 128 id")
    fun moreThan128Ids() {
        val searchBuilder = staffMetadataPreconditions
            .setSearchCommentBuilder(
                userIds = null,
                usersCount = 129
            )
        val errorMessage = staffMetadataActions.getMetadataByListOfIdsWithError(searchBuilder).message

        commonAssertion.checkErrorMessage(errorMessage, "User IDs list must contain from 1 to 128 items")

    }

    @Test
    @DisplayName("Search metadata by ID - 0 id")
    fun emptyIdsList() {
        val searchBuilder = staffMetadataPreconditions
            .setSearchCommentBuilder(
                userIds = null,
                usersCount = 0
            )
        val errorMessage = staffMetadataActions.getMetadataByListOfIdsWithError(searchBuilder).message
        commonAssertion.checkErrorMessage(errorMessage, "User IDs list must contain from 1 to 128 items")
    }
}