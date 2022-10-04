package ru.generalName.teamName.tests.dataproviders.dataClasses.apiGTW.enum


enum class UserRole(override val value: String) : ScalarRepresentation<String> {

    /**
     * Директор ЦФЗ.
     */
    DARKSTORE_ADMIN("darkstore_admin"),


    ;

    override fun toString(): String {
        return value
    }
}