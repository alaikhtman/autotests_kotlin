package ru.samokat.mysamokat.tests.dataproviders.apiModels.staffcave.users


data class UserWithPasswordView (
    val user: UserView,
    val generatedPassword: CharArray
    )