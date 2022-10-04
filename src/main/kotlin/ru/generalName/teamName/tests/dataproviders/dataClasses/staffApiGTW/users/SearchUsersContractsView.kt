package ru.samokat.mysamokat.tests.dataproviders.dataClasses.staffApiGTW.users


import java.time.Instant
import java.util.*

data class SearchUsersContractsView(

    val usersContracts: Map<UUID, List<Contract>>
)

data class Contract(
    val accountingContractId: UUID,
    val title: String,
    val retirementDate: Instant?
)
