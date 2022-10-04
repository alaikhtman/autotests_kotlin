package ru.samokat.mysamokat.tests.helpers.controllers.events.employee

data class FirstLoginEvent (
    val profileId: String,
    val firstLoginAt: String
        )