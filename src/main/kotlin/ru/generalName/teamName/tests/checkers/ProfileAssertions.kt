package ru.generalName.teamName.tests.checkers

import io.qameta.allure.Step
import org.assertj.core.api.SoftAssertions
import org.eclipse.jetty.http.HttpStatus
import org.jetbrains.exposed.sql.ResultRow
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*


@Service
class ProfileAssertions {

    private val softAssertion = SoftAssertions()
    fun getSoftAssertion(): SoftAssertions {
        return softAssertion
    }
    fun assertAll() {
        getSoftAssertion().assertAll()
    }

    // Status code checkers

    //Internship
    @Step("Check internship in table 'Internship'")
    fun checkInternshipFromDB(
        darkstoreId: UUID,
        role: String,
        status: String,
        internshipFromDB: ResultRow
    ): ProfileAssertions {
        getSoftAssertion().assertThat(internshipFromDB[Internship.darkstoreId])
            .isEqualTo(darkstoreId)
        getSoftAssertion().assertThat(internshipFromDB[Internship.role])
            .isEqualTo(role)
        getSoftAssertion().assertThat(internshipFromDB[Internship.status])
            .isEqualTo(status)

        return this
    }


    @Step("Check Internship in DB doesn't exist")
    fun checkInternshipNotInDatabase(internshipExists: Boolean): ProfileAssertions {
        getSoftAssertion().assertThat(internshipExists)
            .isFalse
        return this
    }


    @Step("Check Internship Log  in DB")
    fun checkInternshipLogFromDB(
        InternshipLogArray: MutableList<ResultRow>,
        amount: Int,
        index: Int,
        type: String
    ): ProfileAssertions {
        getSoftAssertion().assertThat(InternshipLogArray.count()).isEqualTo(amount)
        getSoftAssertion().assertThat(InternshipLogArray[index][InternshipLog.type])
            .isEqualTo(type)

        return this
    }

    @Step("Check Internship API response")
    fun checkInternshipAPIResponse(
        actualInternship: InternshipsView,
        expectedInternship: CreateInternshipRequest,
        status: ApiEnum<InternshipStatus, String>
    ): ProfileAssertions {
        for (i in 0 until actualInternship.internships.count()) {
            getSoftAssertion().assertThat(expectedInternship.darkstoreId)
                .isEqualTo(actualInternship.internships[i].darkstoreId)
            getSoftAssertion().assertThat(expectedInternship.plannedDate)
                .isEqualTo(actualInternship.internships[i].plannedDate)
            getSoftAssertion().assertThat(actualInternship.internships[i].status).isEqualTo(status)
        }
        return this
    }





}
