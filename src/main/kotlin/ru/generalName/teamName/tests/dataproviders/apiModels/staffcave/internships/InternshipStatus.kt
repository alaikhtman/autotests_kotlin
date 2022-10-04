package ru.samokat.mysamokat.tests.dataproviders.apiModels.staffcave.internships

import ru.samokat.my.enum.StringRepresentation

enum class InternshipStatus(override val value: String) : StringRepresentation {

    /**
     * Стажирока запланирована.
     */
    PLANNED("planned"),

    /**
     * Стажирока отменена по причине неявки стажера. (стажировка не проводилась)
     */
    CANCELED("canceled"),

    /**
     * Стажирока отклонена директором ЦФЗ. (стажировка не проводилась)
     */
    REJECTED("rejected"),

    /**
     * Стажировка завершена не удачно.
     */
    FAILED("failed"),

    /**
     * Стажировка успешна завершена.
     */
    DONE("done")
}