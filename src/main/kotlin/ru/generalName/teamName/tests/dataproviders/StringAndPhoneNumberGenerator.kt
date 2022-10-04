package ru.generalName.teamName.tests.dataproviders

import java.util.concurrent.ThreadLocalRandom
import kotlin.random.Random
import kotlin.streams.asSequence

interface StringAndPhoneNumberGenerator {
    companion object {
        fun generateRandomString(stringLength: Int): String {
            val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
            return ThreadLocalRandom.current()
                .ints(stringLength.toLong(), 0, charPool.size)
                .asSequence()
                .map(charPool::get)
                .joinToString("")
        }

        fun generateRandomPhoneNumber(): String {
            var phoneNumber = "7"
            for (i in 0..9) {
                phoneNumber += Random.nextInt(0, 9).toString()
            }
            return phoneNumber
        }

        fun generateRandomInn(): String{
            var inn = ""
            for (i in 0..12) {
                inn += Random.nextInt(0, 9).toString()
            }
            return inn
        }
    }
}
