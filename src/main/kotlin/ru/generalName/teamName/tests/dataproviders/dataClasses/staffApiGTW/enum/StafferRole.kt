package ru.samokat.mysamokat.tests.dataproviders.dataClasses.staffApiGTW.enum

import ru.samokat.my.enum.ScalarRepresentation


enum class StafferRole (val role: EmployeeUserRole) : ScalarRepresentation<String> {

    /**
     * Курьер.
     */
    DELIVERYMAN(EmployeeUserRole.DELIVERYMAN),

    /**
     * Сборщик.
     */
    PICKER(EmployeeUserRole.PICKER)
    ;

    override val value: String
    get() = role.value

    override fun toString(): String {
        return value
    }
}