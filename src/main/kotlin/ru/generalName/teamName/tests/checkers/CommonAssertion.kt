package ru.generalName.teamName.tests.checkers

import io.qameta.allure.Step
import org.assertj.core.api.SoftAssertions
import org.eclipse.jetty.http.HttpStatus
import org.springframework.stereotype.Service


@Service
class CommonAssertion {

    private val softAssertion = SoftAssertions()
    fun getSoftAssertion(): SoftAssertions {
        return softAssertion
    }

    fun assertAll() {
        getSoftAssertion().assertAll()
    }

    @Step("Check status code 409")
    fun checkStatusCodeConflict(responseCode: Int): CommonAssertion {
        getSoftAssertion().assertThat(responseCode).isEqualTo(HttpStatus.CONFLICT_409)
        return this
    }

    @Step("Check status code 404")
    fun checkStatusNotFound(responseCode: Int): CommonAssertion {
        getSoftAssertion().assertThat(responseCode).isEqualTo(HttpStatus.NOT_FOUND_404)
        return this
    }

    @Step("Check status code 400")
    fun checkStatusBadRequest(responseCode: Int): CommonAssertion {
        getSoftAssertion().assertThat(responseCode).isEqualTo(HttpStatus.BAD_REQUEST_400)
        return this
    }

    @Step("Check error message")
    fun checkErrorMessage(actual: String, expected: String): CommonAssertion {
        getSoftAssertion().assertThat(actual).isEqualTo(expected)
        return this
    }

}