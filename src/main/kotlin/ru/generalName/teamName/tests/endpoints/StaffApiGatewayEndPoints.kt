package ru.samokat.mysamokat.tests.endpoints

internal interface StaffApiGatewayEndPoints {
    companion object {
        const val AUTH_PASSWORD = "/oauth/token"
        const val TOKEN_REFRESH = "/oauth/token/refresh"
        const val OTP = "/oauth/otp"

        const val STAFF_BY_ID = "/staff/darkstore/user/{userId}/role/{roleId}"
        const val STAFF_BY_DARKSTORE = "/staff/darkstore/{darkstoreId}"

        const val EQUIPMENT_ISSUANCE = "/equipment/issuance"
        const val EQUIPMENT_ITEMS = "equipment/items/active"
        const val EQUIPMENT_ISSUANCE_BY_USER = "/equipment/issuance/user/{userId}"
        const val EQUIPMENT_ISSUANCE_RULES = "/equipment/issuance-rules"

        const val GLIDE_VIOLATIONS = "/glide/staff/darkstore/violations"
        const val GLIDE_STAFF = "/glide/staff/darkstore"

        const val INTERNSHIP = "/staff/darkstore/internships"
        const val INTERNSHIP_REJECT = "/staff/users/{userId}/roles/{role}/internships/reject"
        const val INTERNSHIP_FINISH = "/staff/users/{userId}/roles/{role}/internships/finish"
        const val INTERNSHIP_FAIL = "/staff/users/{userId}/roles/{role}/internships/fail"
        const val INTERNSHIP_CANCEL = "/staff/users/{userId}/roles/{role}/internships/cancel"

        const val WORKEDOUT_TIMESLOT_BY_ID = "/time-slots/worked-out/{workedOutTimeSlotId}"
        const val WORKEDOUT_TIMESLOT = "/time-slots/worked-out"

        const val METADATA = "/users/{userId}/metadata"
        const val CONTRACTS = "/users/contracts/search"
        const val ME = "/users/me"
        const val USERS = "/users/darkstore/{darkstoreId}"
        const val STATISTICS = "/users/darkstore/{darkstoreId}/statistics"
        const val ASSIGNEES = "/users/assignees"

        const val STAFF_PARTNERS = "/staff/partners"

        const val VIOLATIONS = "/staff/darkstore/users/{userId}/roles/{role}/violations"
        const val DICTIONARY = "/staff/violations/dictionary"

        const val TIMESHEET = "/timesheet/darkstore"
        const val SUBMIT_TIMESHEET = "/timesheet/{timesheetId}/submit"

        const val ACTIVE_SHIFTS = "/shifts/active/darkstore"
        const val WORKEDOUT_SHIFTS = "/shifts/worked-out/darkstore"
        const val SCHEDULE = "/shifts/schedule/darkstore/{darkstoreId}"
        const val ASSIGNMENTS = "/shifts/assignments/darkstore/{darkstoreId}"
        const val FOREIGN_ASSIGNMENTS = "/shifts/assignments/darkstore/{darkstoreId}/foreign"
        const val BATCH_ASSIGNMENTS = "/shifts/assignments/darkstore/{darkstoreId}/batch"
    }
}