package ru.samokat.mysamokat.tests.dataproviders.helpers.database.employee_profiles_backend

import org.jetbrains.exposed.sql.Table

object SupervisedDarkstores : Table(name = "supervised_darkstores") {
    val darkstoreId = uuid("darkstore_id")
    val profileId = uuid("profile_id")

}
