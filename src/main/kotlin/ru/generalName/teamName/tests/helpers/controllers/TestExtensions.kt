package ru.samokat.mysamokat.tests.helpers.controllers

import org.junit.jupiter.api.Assertions
import ru.samokat.my.rest.api.error.RestError
import ru.samokat.my.rest.client.RestResult

fun <R : Any> RestResult<R, *>.asSuccess(): R {
    return when (this) {
        is RestResult.Success -> result
        is RestResult.ClientError -> Assertions.fail("Success result was expected but found: statusCode=$statusCode, body=$error")
        is RestResult.ServerError -> Assertions.fail("Success result was expected but found: statusCode=$statusCode, body=$error")
    }
}

fun <E : RestError> RestResult<*, E>.asClientError(): E {
    return when (this) {
        is RestResult.Success -> Assertions.fail("Client error was expected but found: statusCode=$statusCode, body=$result")
        is RestResult.ClientError -> error
        is RestResult.ServerError -> Assertions.fail("Client error was expected but found: statusCode=$statusCode, body=$error")
    }
}
