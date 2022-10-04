package ru.samokat.mysamokat.tests.dataproviders.apiModels.staffcave.requisitions

import ru.samokat.my.rest.api.paging.Paging

data class SearchUserRequisitionsView (
    val requisitions: List<UserRequisitionView>,
    val paging: Paging
        )
