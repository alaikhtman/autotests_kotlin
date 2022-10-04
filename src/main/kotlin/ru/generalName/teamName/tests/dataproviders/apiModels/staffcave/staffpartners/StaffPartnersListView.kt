package ru.samokat.mysamokat.tests.dataproviders.apiModels.staffcave.staffpartners

import java.util.*


data class StaffPartnersListView (
    val staffPartners: List<StaffPartnerView>)

data class StaffPartnerView(
    val id: UUID,
    val title: String,
    val shortTitle: String
)