package ru.samokat.mysamokat.tests.tests.staff_metadata

import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.samokat.mysamokat.tests.checkers.CommonAssertion

import ru.samokat.mysamokat.tests.checkers.StaffMetadataAssertion
import ru.samokat.mysamokat.tests.dataproviders.Constants
import ru.samokat.mysamokat.tests.dataproviders.StringAndPhoneNumberGenerator
import ru.samokat.mysamokat.tests.dataproviders.preconditions.CommonPreconditions
import ru.samokat.mysamokat.tests.dataproviders.preconditions.StaffMetadataPreconditions
import ru.samokat.mysamokat.tests.helpers.actions.StaffMetadataActions
import ru.samokat.mysamokat.tests.helpers.controllers.database.StaffMetadataDatabaseController

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Tag("staffMetadata")
class Comment {

    private lateinit var staffMetadataAssertion: StaffMetadataAssertion

    @Autowired
    private lateinit var staffMetadataActions: StaffMetadataActions

    private lateinit var staffMetadataPreconditions: StaffMetadataPreconditions

    @Autowired
    private lateinit var databaseController: StaffMetadataDatabaseController

    @Autowired
    private lateinit var commonPreconditions: CommonPreconditions

    @Autowired
    private lateinit var commonAssertion: CommonAssertion

    @BeforeEach
    fun before() {
        staffMetadataPreconditions = StaffMetadataPreconditions()
        staffMetadataAssertion = StaffMetadataAssertion()
        staffMetadataActions.deleteUserMetadataInDb(Constants.uuidForComment1)
    }

    @AfterEach
    fun release() {
        commonAssertion.assertAll()
        staffMetadataAssertion.assertAll()
        staffMetadataActions.deleteUserMetadataInDb(Constants.uuidForComment1)
    }

    @Test
    @Tag("smoke")
    @DisplayName("Add comment")
    fun createComment() {
        val comment = StringAndPhoneNumberGenerator.generateRandomString(10)
        val getCommentRequest = staffMetadataPreconditions.setCreateCommentRequest(
            commentary=comment)
        staffMetadataActions.sendComment(getCommentRequest)

        val commentFromDb = staffMetadataActions.getCommentFromDB(Constants.uuidForComment1)

        staffMetadataAssertion
            .checkCommentInDatabase(commentFromDb, getCommentRequest)
    }

    @Test
    @DisplayName("Add comment 256 characters")
    fun createComment256Characters() {
        val comment = StringAndPhoneNumberGenerator.generateRandomString(256)
        val getCommentRequest = staffMetadataPreconditions.setCreateCommentRequest(
            commentary=comment)
        staffMetadataActions.sendComment(getCommentRequest)

        val commentFromDb = staffMetadataActions.getCommentFromDB(Constants.uuidForComment1)

        staffMetadataAssertion
            .checkCommentInDatabase(commentFromDb, getCommentRequest)
    }

    @Test
    @DisplayName("Add comment 1 character")
    fun createComment16Character() {
        val comment = StringAndPhoneNumberGenerator.generateRandomString(1)
        val getCommentRequest = staffMetadataPreconditions.setCreateCommentRequest(
            commentary=comment)
        staffMetadataActions.sendComment(getCommentRequest)

        val commentFromDb = staffMetadataActions.getCommentFromDB(Constants.uuidForComment1)

        staffMetadataAssertion
            .checkCommentInDatabase(commentFromDb, getCommentRequest)
    }

    @Test
    @DisplayName("Add empty comment")
    fun createEmptyComment() {
        val comment: String =  ""
        val getCommentRequest = staffMetadataPreconditions.setCreateCommentRequest(
            commentary=comment)
        staffMetadataActions.sendCommentWithError(getCommentRequest)

        val errorMessage = staffMetadataActions.sendCommentWithError(getCommentRequest).message

        commonAssertion.checkErrorMessage(errorMessage, "Commentary should consist of 1 to 256 characters")

        staffMetadataAssertion.getSoftAssertion()
            .assertThat(
                databaseController.checkMetadataExistById(Constants.uuidForComment1)
            )
            .isFalse

    }

    @Test
    @DisplayName("Add comment more 256 characters")
    fun createLongComment() {
        val comment = StringAndPhoneNumberGenerator.generateRandomString(257)
        val getCommentRequest = staffMetadataPreconditions.setCreateCommentRequest(
            commentary=comment)
        staffMetadataActions.sendCommentWithError(getCommentRequest)

        val errorMessage = staffMetadataActions.sendCommentWithError(getCommentRequest).message

        commonAssertion
            .checkErrorMessage(errorMessage, "Commentary should consist of 1 to 256 characters")

        staffMetadataAssertion.getSoftAssertion()
            .assertThat(
                databaseController.checkMetadataExistById(
                    Constants.uuidForComment1
                )
            )
            .isFalse
    }

    @Test
    @Tag("smoke")
    @DisplayName("Update comment")
    fun updateComment() {
        databaseController.setMetadataById(Constants.uuidForComment1, "Тест")
        val comment = StringAndPhoneNumberGenerator.generateRandomString(10)
        val getCommentRequest = staffMetadataPreconditions.setCreateCommentRequest(
            commentary=comment)
        staffMetadataActions.sendComment(getCommentRequest)

        val commentFromDb = staffMetadataActions.getCommentFromDB(Constants.uuidForComment1)

        staffMetadataAssertion
            .checkCommentInDatabase(commentFromDb, getCommentRequest)
    }

    @Test
    @Tag("smoke")
    @DisplayName("Delete comment")
    fun deleteComment() {
        databaseController.setMetadataById(Constants.uuidForComment1, "Тест")
        val comment = null
        val getCommentRequest = staffMetadataPreconditions.setCreateCommentRequest(
            commentary=comment)
        staffMetadataActions.sendComment(getCommentRequest)

        val commentFromDb = staffMetadataActions.getCommentFromDB(Constants.uuidForComment1)

        staffMetadataAssertion
            .checkCommentInDatabase(commentFromDb, getCommentRequest)
    }

}
