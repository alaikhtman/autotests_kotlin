package ru.samokat.mysamokat.tests.endpoints

internal interface StaffCaveApiGatewayEndPoints {
    companion object {
        // oauth api
        const val TOKEN = "/oauth/token"
        const val TOKEN_REFRESH = "/oauth/token/refresh"

        //users api
        const val USER = "/users/{userId}"
        const val PASSWORD = "/users/{userId}/password"
        const val USERS = "/users"

        //staff-partners api
        const val STAFF_PARTNERS = "/staff-partners"

        //darkstores api
        const val DARKSTORES = "/darkstores"

        //internships api
        const val INTERNSHIP = "/users/{userId}/roles/{role}/internships"
        const val USER_INTERNSHIP = "/users/{userId}/internships"

        //user requisitions api
        const val  REQUESTS = "/users/requests/search"
        const val  REQUEST = "/users/requests/{requestId}"
    }
}