package ru.samokat.mysamokat.tests.dataproviders.dataClasses.staffApiGTW.enum

import ru.samokat.my.enum.ScalarRepresentation

enum class EmployeeUserRole(override val value: String) : ScalarRepresentation<String> {

    /**
     * Курьер.
     */
    DELIVERYMAN("deliveryman"),

    /**
     * Сборщик.
     */
    PICKER("picker")
    ;

    override fun toString(): String {
        return value
    }
}