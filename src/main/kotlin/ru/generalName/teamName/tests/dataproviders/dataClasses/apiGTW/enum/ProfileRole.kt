package ru.generalName.teamName.tests.dataproviders.dataClasses.apiGTW.enum



enum class ProfileRole(override val value: String) : ScalarRepresentation<String> {

    /**
     * Курьер.
     */
    DELIVERYMAN("deliveryman"),

    ;

    override fun toString(): String {
        return value
    }
}