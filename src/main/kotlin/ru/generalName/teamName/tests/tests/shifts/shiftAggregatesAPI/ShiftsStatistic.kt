package ru.samokat.mysamokat.tests.tests.shifts.shiftAggregatesAPI

import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.samokat.employeeprofiles.api.common.domain.Vehicle
import ru.samokat.employeeprofiles.api.common.domain.EmployeeVehicleType
import ru.samokat.my.domain.shifts.TimeRange
import ru.samokat.my.rest.api.enum.ApiEnum
import ru.samokat.mysamokat.tests.checkers.ShiftAssertion
import ru.samokat.mysamokat.tests.dataproviders.Constants
import ru.samokat.mysamokat.tests.dataproviders.preconditions.CommonPreconditions
import ru.samokat.mysamokat.tests.dataproviders.preconditions.EmployeePreconditions
import ru.samokat.mysamokat.tests.dataproviders.preconditions.ShiftsPreconditions
import ru.samokat.mysamokat.tests.helpers.actions.EmployeeActions
import ru.samokat.mysamokat.tests.helpers.actions.ShiftsActions
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneOffset

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Tag("shifts")
class ShiftsStatistic {

    private lateinit var shiftsPreconditions: ShiftsPreconditions

    @Autowired
    private lateinit var employeeActions: EmployeeActions

    private lateinit var employeePreconditions: EmployeePreconditions

    @Autowired
    private lateinit var shiftsActions: ShiftsActions

    private lateinit var shiftsAssertion: ShiftAssertion

    @Autowired
    private lateinit var commonPreconditions: CommonPreconditions

    @BeforeEach
    fun before() {
        this.date = commonPreconditions.getTomorrowsDate()
        this.range = commonPreconditions.getTomorrowsFullDayRange()
        shiftsPreconditions = ShiftsPreconditions()
        employeePreconditions = EmployeePreconditions()
        shiftsAssertion = ShiftAssertion()
        employeeActions.deleteProfile(Constants.mobile1)
        employeeActions.deleteProfile(Constants.mobile2)
        commonPreconditions.clearAssignmentsFromDatabase(range)
    }

    @AfterEach
    fun release() {
        shiftsAssertion.assertAll()
        employeeActions.deleteProfile(Constants.mobile1)
        employeeActions.deleteProfile(Constants.mobile2)
        commonPreconditions.clearAssignmentsFromDatabase(range)
    }

    private lateinit var range: TimeRange
    fun range(range: TimeRange) = apply { this.range = range }
    fun getRange(): TimeRange {
        return range
    }

    private lateinit var date: LocalDate
    fun date(date: LocalDate) = apply { this.date = date }
    fun getDate(): LocalDate {
        return date
    }

    @Test
    @Tags(Tag("smoke"))
    @DisplayName("Agregate statistics")
    fun agregateStatistics() {

        val profileId = commonPreconditions.createProfileDeliveryman().profileId

        val statRequest = shiftsPreconditions.fillGetStatisticsBuilder(
            TimeRange(date.minusDays(2).atStartOfDay().toInstant(ZoneOffset.UTC), range.endingAt),
            listOf(profileId))
        val stat1 = shiftsActions.getStatistics(statRequest).statistics.get(profileId)!!

        val shift = commonPreconditions.startAndStopShift(profileId)
        val assignmentId = commonPreconditions.createAssignment(
            profileId, range, TimeRange(
                startingAt = date.atTime(LocalTime.parse("09:00")).toInstant(ZoneOffset.UTC),
                endingAt = date.atTime(LocalTime.parse("17:00")).toInstant(ZoneOffset.UTC)
            )
        )
        val storeRequest = shiftsPreconditions.fillStoreScheduleRequest(
            profileId,
            timeRange = range,
            schedule = listOf(
                TimeRange(
                    startingAt = date.atTime(LocalTime.parse("09:00")).toInstant(ZoneOffset.UTC),
                    endingAt = date.atTime(LocalTime.parse("17:00")).toInstant(ZoneOffset.UTC)
                ),
                TimeRange(
                    startingAt = date.atTime(LocalTime.parse("18:00")).toInstant(ZoneOffset.UTC),
                    endingAt = date.atTime(LocalTime.parse("22:00")).toInstant(ZoneOffset.UTC)
                )
            )
        )
        shiftsActions.postSchedules(storeRequest)

        val stat2 = shiftsActions.getStatistics(statRequest).statistics.get(profileId)!!

        shiftsAssertion
            .checkStats(stat1, 0L, 0L, 0, 0)
            .checkStats(stat2, 12L, 8L, 1, 1)
    }

    @Test
    @DisplayName("Agregate statistics - for several employees")
    fun agregateStatisticsSeveralEmployees() {

        val profileId1 = commonPreconditions.createProfileDeliveryman().profileId
        val profileId2 = commonPreconditions.createProfileDeliveryman(mobile = Constants.mobile2).profileId

        val shift = commonPreconditions.startAndStopShift(profileId1)
        val assignmentId = commonPreconditions.createAssignment(
            profileId2, range, TimeRange(
                startingAt = date.atTime(LocalTime.parse("09:00")).toInstant(ZoneOffset.UTC),
                endingAt = date.atTime(LocalTime.parse("17:00")).toInstant(ZoneOffset.UTC)
            )
        )

        val statRequest = shiftsPreconditions.fillGetStatisticsBuilder(
            TimeRange(date.minusDays(2).atStartOfDay().toInstant(ZoneOffset.UTC), range.endingAt),
            listOf(profileId1, profileId2))
        val stat = shiftsActions.getStatistics(statRequest).statistics

        shiftsAssertion
            .checkStats(stat.get(profileId1)!!, 0L, 0L, 0, 1)
            .checkStats(stat.get(profileId2)!!, 0L, 8L, 1, 0)
    }

    @Test
    @DisplayName("Agregate statistics - disable employee")
    fun agregateStatisticsDisableEmployee() {

        val profileId = commonPreconditions.createProfileDeliveryman().profileId

        val statRequest = shiftsPreconditions.fillGetStatisticsBuilder(
            TimeRange(date.minusDays(2).atStartOfDay().toInstant(ZoneOffset.UTC), range.endingAt),
            listOf(profileId))

        val shift = commonPreconditions.startAndStopShift(profileId)
        val assignmentId = commonPreconditions.createAssignment(
            profileId, range, TimeRange(
                startingAt = date.atTime(LocalTime.parse("09:00")).toInstant(ZoneOffset.UTC),
                endingAt = date.atTime(LocalTime.parse("17:00")).toInstant(ZoneOffset.UTC)
            )
        )
        val storeRequest = shiftsPreconditions.fillStoreScheduleRequest(
            profileId,
            timeRange = range,
            schedule = listOf(
                TimeRange(
                    startingAt = date.atTime(LocalTime.parse("09:00")).toInstant(ZoneOffset.UTC),
                    endingAt = date.atTime(LocalTime.parse("17:00")).toInstant(ZoneOffset.UTC)
                ),
                TimeRange(
                    startingAt = date.atTime(LocalTime.parse("18:00")).toInstant(ZoneOffset.UTC),
                    endingAt = date.atTime(LocalTime.parse("22:00")).toInstant(ZoneOffset.UTC)
                )
            )
        )
        shiftsActions.postSchedules(storeRequest)
        employeeActions.deleteProfile(profileId)
        Thread.sleep(3_000)

        val stat = shiftsActions.getStatistics(statRequest).statistics.get(profileId)!!

        shiftsAssertion
            .checkStats(stat, 12L, 0L, 0, 1)

    }
}