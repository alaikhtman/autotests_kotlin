package ru.samokat.mysamokat.tests.dataproviders.apiModels.staffcave.users

import java.time.Instant

data class UserWithVersionView(
    val user: UserView,
    val signature: SignatureView?,
    val createdAt: Instant,
    val updatedAt: Instant,
    val version: Long
)