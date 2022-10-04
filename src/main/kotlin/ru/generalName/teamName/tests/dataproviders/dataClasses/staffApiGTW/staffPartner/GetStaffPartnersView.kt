package ru.samokat.mysamokat.tests.dataproviders.dataClasses.staffApiGTW.staffPartner

data class GetStaffPartnersView(
    val partners: List<StaffPartnerView>

)

data class StaffPartnerView(
    val partnerId: String,
    val title: String,
    val shortTitle: String,
    val type: String
)