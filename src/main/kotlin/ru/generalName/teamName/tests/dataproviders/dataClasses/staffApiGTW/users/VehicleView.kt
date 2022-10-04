package ru.samokat.mysamokat.tests.dataproviders.dataClasses.staffApiGTW.users

import ru.samokat.my.rest.api.enum.ApiEnum
import ru.samokat.mysamokat.tests.dataproviders.dataClasses.staffApiGTW.enum.VehicleType


data class VehicleView(
    val type: ApiEnum<VehicleType, String>
)
