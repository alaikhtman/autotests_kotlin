package ru.samokat.mysamokat.tests.dataproviders.dataClasses.staffApiGTW.enum

import ru.samokat.my.enum.StringRepresentation

enum class VehicleType(override val value: String) : StringRepresentation {

    /**
     * Корпоративный велосипед.
     */
    COMPANY_BICYCLE("company_bicycle"),

    /**
     * Личный велосипед.
     */
    PERSONAL_BICYCLE("personal_bicycle"),

    /**
     * Автомобиль.
     */
    CAR("car"),

    /**
     * Транспорт отсутствует (Пеший).
     */
    NONE("none"),

    /**
     * Мотоцикл
     */
    MOTOCYCLE("motocycle"),

    /**
     * Велосипед
     */
    BICYCLE("bicycle"),

    /**
     * Электровелосипед
     */
    ELECTRIC_BICYCLE("electric_bicycle")
    ;

    override fun toString(): String {
        return value
    }
}