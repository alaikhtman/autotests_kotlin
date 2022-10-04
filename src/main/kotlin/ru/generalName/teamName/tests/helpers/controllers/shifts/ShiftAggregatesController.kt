package ru.samokat.mysamokat.tests.helpers.controllers.shifts

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.samokat.my.rest.client.RestResult
import ru.samokat.shifts.api.aggregates.statistics.GetShiftAggregatedStatisticError
import ru.samokat.shifts.api.aggregates.statistics.GetShiftAggregatedStatisticRequest
import ru.samokat.shifts.api.aggregates.statistics.ShiftAggregatedStatisticView
import ru.samokat.shifts.client.ShiftAggregatesClient

@Service
class ShiftAggregatesController {

    @Autowired
    lateinit var shiftStat: ShiftAggregatesClient

    fun getAgregateStatistics(request: GetShiftAggregatedStatisticRequest): RestResult<ShiftAggregatedStatisticView, GetShiftAggregatedStatisticError>? {
        return try {
            shiftStat.getStatistics(request).get()
        } catch (e: InterruptedException) {
            e.printStackTrace()
            null
        }
    }

}