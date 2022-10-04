package ru.samokat.mysamokat.tests.dataproviders.apiModels.staffcave.users

import ru.samokat.my.rest.api.paging.Paging


data class UserListView (
    val users: List<BriefUserView>,
    val paging: Paging
        )