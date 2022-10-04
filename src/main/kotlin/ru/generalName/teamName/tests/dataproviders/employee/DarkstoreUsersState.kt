package ru.samokat.mysamokat.tests.dataproviders.employee

enum class DarkstoreUsersState(val dbId: Int) {
    NEW(dbId = 4),
    NOT_WORKING(dbId = 5),
    WORKING(dbId = 6)
}