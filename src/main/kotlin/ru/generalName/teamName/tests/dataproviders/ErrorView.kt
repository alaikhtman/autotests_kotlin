package ru.samokat.mysamokat.tests.dataproviders

import java.util.*

data class ErrorView(
    val message: String?,
    val code: String?,
    val parameter: String?,
    val darkstores: List<UUID>?
)