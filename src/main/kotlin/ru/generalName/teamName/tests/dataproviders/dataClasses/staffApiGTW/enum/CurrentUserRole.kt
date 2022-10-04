package ru.samokat.mysamokat.tests.dataproviders.dataClasses.staffApiGTW.enum

import ru.samokat.my.enum.ScalarRepresentation

enum class CurrentUserRole(override val value: String) : ScalarRepresentation<String> {

    /**
     * Директор ЦФЗ.
     */
    DARKSTORE_ADMIN("darkstore_admin"),

    /**
     * Супервайзер.
     */
    SUPERVISOR("supervisor"),

    /**
     * Территориальный менеджер.
     */
    TERRITORIAL_MANAGER("territorial_manager"),

    /**
     * Координатор ЦФЗ.
     */
    COORDINATOR("coordinator"),

    /**
     * Товаровед.
     */
    GOODS_MANAGER("goods_manager")
    ;

    override fun toString(): String {
        return value
    }
}