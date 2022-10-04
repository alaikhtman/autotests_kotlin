package ru.generalName.teamName.tests.checkers

import io.qameta.allure.Step
import org.assertj.core.api.SoftAssertions
import org.jetbrains.exposed.sql.ResultRow
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.*


@Service
class ApiGWAssertions {

    private val softAssertion = SoftAssertions()
    fun getSoftAssertion(): SoftAssertions {
        return softAssertion
    }

    fun assertAll() {
        getSoftAssertion().assertAll()
    }



    //users
    @Step("check user data")
    fun checkUserData(
        actualView: CurrentUserView,
        expectedRequest: CreateProfileRequest,
        profileId: UUID,
        city: String? = null,
        timeZone: ZoneId? = null,
        roles: MutableList<ApiEnum<CurrentUserRole, String>>,
        supervisedDarkstoresCity: MutableList<String>? = null,
        supervisedDarkstoresTimeZone: MutableList<ZoneId>? = null
    ) {
        getSoftAssertion().assertThat(actualView.id).isEqualTo(profileId)
        getSoftAssertion().assertThat(actualView.mobile).isEqualTo(expectedRequest.mobile.asStringWithPlus())
        getSoftAssertion().assertThat(actualView.name).isEqualTo(
            ProfileName(
                expectedRequest.name.firstName,
                expectedRequest.name.lastName,
                expectedRequest.name.middleName
            )
        )
        getSoftAssertion().assertThat(actualView.roles).isEqualTo(roles)

        if (expectedRequest.darkstoreId != null) {
            getSoftAssertion().assertThat(actualView.darkstore!!.id).isEqualTo(expectedRequest.darkstoreId)
            getSoftAssertion().assertThat(actualView.darkstore!!.cityCode).isEqualTo(city)
            getSoftAssertion().assertThat(actualView.darkstore!!.timezone).isEqualTo(timeZone)
        }

        if (expectedRequest.supervisedDarkstores != null) {
            for (i in 0 until actualView.supervisedDarkstores!!.count()) {
                getSoftAssertion().assertThat(actualView.supervisedDarkstores[i].id)
                    .isEqualTo(expectedRequest.supervisedDarkstores!![i])
                getSoftAssertion().assertThat(actualView.supervisedDarkstores[i].cityCode)
                    .isEqualTo(supervisedDarkstoresCity!![i])
                getSoftAssertion().assertThat(actualView.supervisedDarkstores[i].timezone)
                    .isEqualTo(supervisedDarkstoresTimeZone!![i])
            }
        }

    }




}



