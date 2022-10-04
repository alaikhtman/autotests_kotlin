package ru.samokat.mysamokat.tests.dataproviders.apiModels.staffcave.staffpartners

data class CreatePartnerError (
    val message: String,
    val code: String?,
    val parameter: String?,
    val conflicts: List<StaffPartnerView>?
    )