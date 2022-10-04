package ru.samokat.mysamokat.tests.dataproviders.dataClasses.staffApiGTW.enum

import ru.samokat.my.enum.ScalarRepresentation

enum class StafferState (override val value: String) : ScalarRepresentation<String> {

    /**
     * Новый сотрудник.
     */
    NEW("new"),

    /**
     * Работающий сотрудник.
     */
    WORKING("working"),

    /**
     * Неработающий сотрудник.
     */
    NOT_WORKING("not_working")
    ;

    override fun toString(): String {
        return value
    }
}