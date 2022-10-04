package ru.samokat.mysamokat.tests.helpers.controllers.database

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.stereotype.Service
import ru.samokat.my.domain.PhoneNumber
import ru.samokat.mysamokat.tests.dataproviders.helpers.database.DarkstoreUser
import ru.samokat.mysamokat.tests.dataproviders.helpers.database.employee_profiles_backend.*
import java.util.*

@Service
class EmployeeProfilesDatabaseController(
    private val employeeDatabase: Database
) {

    fun getStaffPartnerId(): UUID {
        var uuid: UUID? = null
        transaction(employeeDatabase) {
            addLogger(StdOutSqlLogger)
            uuid = StaffPartner.selectAll().limit(1).firstOrNull()?.get(StaffPartner.partnerId)
        }
        return uuid!!
    }

    fun getStaffPartner(partnerId: UUID): ResultRow {
        var partner: ResultRow? = null
        transaction(employeeDatabase) {
            addLogger(StdOutSqlLogger)
            partner = StaffPartner.select { StaffPartner.partnerId eq partnerId }.single()
        }
        return partner!!
    }

    fun getAllStaffPartner(): MutableList<ResultRow> {
        val partner: MutableList<ResultRow> = mutableListOf()
        transaction(employeeDatabase) {
            addLogger(StdOutSqlLogger)
            val query = StaffPartner.selectAll()
            (query as Query).forEach {
                partner.add(it)
            }
        }
        partner.sortBy { it[StaffPartner.id] }
        return partner
    }

    fun getProfileIdByMobile(mobile: PhoneNumber): UUID? {
        var uuid: UUID? = null
        transaction(employeeDatabase) {
            addLogger(StdOutSqlLogger)
            uuid = Profile.select { (Profile.mobile eq mobile.asStringWithoutPlus()) and (Profile.status eq "enabled") }
                .firstOrNull()?.get(Profile.profileId)
        }
        return uuid
    }

    fun getProfileIdByAccountingProfileId(accountingProfileId: String): UUID? {
        var uuid: UUID? = null
        transaction(employeeDatabase) {
            addLogger(StdOutSqlLogger)
            uuid =
                Profile.select { (Profile.accountingProfileId eq accountingProfileId.toString()) and (Profile.status eq "enabled") }
                    .firstOrNull()?.get(Profile.profileId)
        }
        return uuid
    }

    fun getProfile(profileId: UUID): ResultRow {
        var profile: ResultRow? = null
        transaction(employeeDatabase) {
            addLogger(StdOutSqlLogger)
            profile = Profile.select { Profile.profileId eq profileId }.single()
        }
        return profile!!
    }

    fun getContract(accountingProfileId: String): ResultRow {
        var contract: ResultRow? = null
        transaction(employeeDatabase) {
            addLogger(StdOutSqlLogger)
            contract = Contract.select { (Contract.accountingProfileId eq accountingProfileId) }.single()
        }
        return contract!!
    }

    fun getAllContractsByAccountingProfileId(accountingProfileId: String): MutableList<ResultRow> {
        var contracts: MutableList<ResultRow> = mutableListOf()
        transaction(employeeDatabase) {
            addLogger(StdOutSqlLogger)
            var query = Contract.select { (Contract.accountingProfileId eq accountingProfileId) }
            (query as Query).forEach {
                contracts.add(it)
            }
        }
        return contracts
    }


    fun getContractByContractId(accountingContractId: String): ResultRow {
        var contract: ResultRow? = null
        transaction(employeeDatabase) {
            addLogger(StdOutSqlLogger)
            contract = Contract.select { Contract.accountingContractId eq accountingContractId }.single()
        }
        return contract!!
    }

    fun getTask(type: String, attempts: Int): MutableList<ResultRow> {
        var tasks: MutableList<ResultRow> = mutableListOf()
        transaction(employeeDatabase) {
            addLogger(StdOutSqlLogger)
            var query =
                Task.select { (Task.type eq type) and (Task.attempts lessEq attempts) }
            (query as Query).forEach {
                tasks.add(it)
            }
        }
        return tasks
    }

    fun getTaskByCorrelationId(correlationId: String): ResultRow {
        var task: ResultRow? = null
        transaction(employeeDatabase) {
            addLogger(StdOutSqlLogger)
            task = Task.select { Task.correlationId eq correlationId }.single()
        }
        return task!!
    }

    fun checkTaskExistsByCorrelationId(correlationId: String): Boolean {
        var exists = false
        transaction(employeeDatabase) {
            addLogger(StdOutSqlLogger)
            if (Task.select { Task.correlationId eq correlationId }.count() > 0) {
                exists = true
            }
        }
        return exists
    }

    fun getContractLog(accountingContractId: String): ResultRow {
        var contractLog: ResultRow? = null
        transaction(employeeDatabase) {
            addLogger(StdOutSqlLogger)
            contractLog = ContractLog.select { ContractLog.accountingContractId eq accountingContractId }.single()
        }
        return contractLog!!
    }

    fun getContractLogByType(accountingContractId: String, type: String): ResultRow {
        var contractLog: ResultRow? = null
        transaction(employeeDatabase) {
            addLogger(StdOutSqlLogger)
            contractLog =
                ContractLog.select { (ContractLog.accountingContractId eq accountingContractId) and (ContractLog.type eq type) }
                    .single()
        }
        return contractLog!!
    }

    fun checkAContractExistsById(accountingContractId: String): Boolean {
        var exists = false
        transaction(employeeDatabase) {
            addLogger(StdOutSqlLogger)
            if (Contract.select { Contract.accountingContractId eq accountingContractId }.count() > 0) {
                exists = true
            }
        }
        return exists
    }

    fun getProfileSupervisedDarkstores(profileId: UUID): MutableList<UUID> {
        var darkstores: MutableList<UUID>? = mutableListOf()
        transaction(employeeDatabase) {
            addLogger(StdOutSqlLogger)
            var query = SupervisedDarkstores.select { SupervisedDarkstores.profileId eq profileId }
            (query as Query).forEach {
                darkstores?.add(it[SupervisedDarkstores.darkstoreId])
            }
        }
        return darkstores!!
    }

    fun getProfileVehicle(profileId: UUID): String {

        var vehicle: String? = null
        transaction(employeeDatabase) {
            addLogger(StdOutSqlLogger)

            if (!Vehicle.select { Vehicle.profileId eq profileId }.empty()) {
                vehicle = Vehicle.select { Vehicle.profileId eq profileId }.single()[Vehicle.type].toString()
            } else vehicle = "none"
        }
        return vehicle!!
    }

    fun checkActiveProfileExistsByMobile(mobile: String): Boolean {
        var exists = false
        transaction(employeeDatabase) {
            addLogger(StdOutSqlLogger)
            if (Profile.select { (Profile.mobile eq mobile) and (Profile.status eq "enabled") }.count() > 0) {
                exists = true
            }
        }
        return exists
    }

    fun checkProfilePassExists(profileId: UUID): Boolean {
        var exists = false
        transaction(employeeDatabase) {
            addLogger(StdOutSqlLogger)
            if (ProfilePassword.select { ProfilePassword.profileId eq profileId }.count() > 0) {
                exists = true
            }
        }
        return exists
    }

    // DS_users
    fun getDSUserProfile(profileId: UUID, role: String): ResultRow {
        var dsUserProfile: ResultRow? = null
        transaction(employeeDatabase) {
            addLogger(StdOutSqlLogger)
            dsUserProfile =
                DarkstoreUser.select { (DarkstoreUser.profileId eq profileId) and (DarkstoreUser.role eq role) }
                    .single()
        }
        return dsUserProfile!!
    }

    fun checkDarkstoreUserExists(profileId: UUID): Boolean {
        var exists = false
        transaction(employeeDatabase) {
            addLogger(StdOutSqlLogger)
            if (DarkstoreUser.select { DarkstoreUser.profileId eq profileId }.count() > 0) {
                exists = true
            }
        }
        return exists
    }

    fun checkDarkstoreUserLogExists(profileId: UUID): Boolean {
        var exists = false
        transaction(employeeDatabase) {
            addLogger(StdOutSqlLogger)
            if (DarkstoreUser.select { DarkstoreUser.profileId eq profileId }.count() > 0) {
                exists = true
            }
        }
        return exists
    }

    fun checkDarkstoreUserActivityExists(profileId: UUID): Boolean {
        var exists = false
        transaction(employeeDatabase) {
            addLogger(StdOutSqlLogger)
            if (DarkstoreUser.select { DarkstoreUser.profileId eq profileId }.count() > 0) {
                exists = true
            }
        }
        return exists
    }

    fun getDSUserActivity(profileId: UUID, role: String): ResultRow {
        var dsUserActivity: ResultRow? = null
        transaction(employeeDatabase) {
            addLogger(StdOutSqlLogger)
            dsUserActivity =
                DarkstoreUserActivity.select { (DarkstoreUserActivity.profileId eq profileId) and (DarkstoreUserActivity.role eq role) }
                    .single()
        }
        return dsUserActivity!!
    }

    fun getDSUserLogArray(profileId: UUID, role: String): MutableList<ResultRow> {
        var dsUserLogArray: MutableList<ResultRow> = mutableListOf()
        transaction(employeeDatabase) {
            addLogger(StdOutSqlLogger)
            var query =
                DarkstoreUserLog.select { (DarkstoreUserLog.profileId eq profileId) and (DarkstoreUserLog.role eq role) }
            (query as Query).forEach {
                dsUserLogArray.add(it)
            }
        }
        dsUserLogArray.sortBy { it[DarkstoreUserLog.id] }

        return dsUserLogArray
    }

    fun getDarkstoreUserByRole(profileId: UUID, role: String): ResultRow {
        var profile: ResultRow? = null
        transaction(employeeDatabase) {
            addLogger(StdOutSqlLogger)
            profile = DarkstoreUser.select { (DarkstoreUser.profileId eq profileId) and (DarkstoreUser.role eq role) }
                .single()
        }
        return profile!!
    }

    fun getDarkstoreUserLogsByRole(profileId: UUID, role: String): MutableList<ResultRow> {
        var profiles: MutableList<ResultRow> = mutableListOf()
        transaction(employeeDatabase) {
            addLogger(StdOutSqlLogger)
            var query =
                DarkstoreUserLog.select { (DarkstoreUserLog.profileId eq profileId) and (DarkstoreUserLog.role eq role) }
            (query as Query).forEach {
                profiles.add(it)
            }
        }
        return profiles
    }


    fun getDarkstoreUserActivityByRole(profileId: UUID, role: String): ResultRow {
        var profile: ResultRow? = null
        transaction(employeeDatabase) {
            addLogger(StdOutSqlLogger)
            profile =
                DarkstoreUserActivity.select { (DarkstoreUserActivity.profileId eq profileId) and (DarkstoreUserActivity.role eq role) }
                    .single()
        }
        return profile!!
    }

    fun checkDarkstoreUserWithRoleExists(profileId: UUID, role: String): Boolean {
        var exists = false
        transaction(employeeDatabase) {
            addLogger(StdOutSqlLogger)
            if (DarkstoreUser.select { (DarkstoreUser.profileId eq profileId) and (DarkstoreUser.role eq role) }
                    .count() > 0) {
                exists = true
            }
        }
        return exists
    }

    fun checkDarkstoreUserWithRoleActivityExists(profileId: UUID, role: String): Boolean {
        var exists = false
        transaction(employeeDatabase) {
            addLogger(StdOutSqlLogger)
            if (DarkstoreUserActivity.select { (DarkstoreUserActivity.profileId eq profileId) and (DarkstoreUserActivity.role eq role) }
                    .count() > 0) {
                exists = true
            }
        }
        return exists
    }


    //Profile_log DB

    fun getProfileLogArray(profileId: UUID): MutableList<ResultRow> {
        var profileLogArray: MutableList<ResultRow>? = mutableListOf()
        transaction(employeeDatabase) {
            addLogger(StdOutSqlLogger)
            var query = ProfileLog.select { ProfileLog.profileId eq profileId }
            (query as Query).forEach {
                profileLogArray?.add(it)
            }
        }

        return profileLogArray!!
    }

    fun getProfilesLogUsers(profileId: UUID): MutableList<ResultRow> {

        var profiles: MutableList<ResultRow>? = mutableListOf()
        transaction(employeeDatabase) {
            addLogger(StdOutSqlLogger)
            var query = ProfileLog.select { ProfileLog.profileId eq profileId }

            (query as Query).forEach {
                profiles?.add(it)
            }
        }
        return profiles!!

    }

    fun checkProfilePasswordLogRowsCount(profileId: UUID, type: String): Int {
        var count = 0
        transaction(employeeDatabase) {
            addLogger(StdOutSqlLogger)
            count =
                ProfilePasswordLog.select { (ProfilePasswordLog.profileId eq profileId) and (ProfilePasswordLog.type eq type) }
                    .count().toInt()
        }
        return count
    }

    fun getDarkstoreUserViolationByViolationId(violationId: UUID): ResultRow {
        var violation: ResultRow? = null
        transaction(employeeDatabase) {
            addLogger(StdOutSqlLogger)
            violation = DarkstoreUserViolation.select { (DarkstoreUserViolation.violationId eq violationId) }
                .single()
        }
        return violation!!
    }

    fun getDarkstoreUserViolationLogByViolationId(violationId: UUID): ResultRow {
        var violation: ResultRow? = null
        transaction(employeeDatabase) {
            addLogger(StdOutSqlLogger)
            violation = DarkstoreUserViolationLog.select { (DarkstoreUserViolationLog.violationId eq violationId) }
                .single()
        }
        return violation!!
    }

    fun deleteViolationsByProfileId(profileId: UUID) {
        transaction(employeeDatabase) {
            addLogger(StdOutSqlLogger)
            DarkstoreUserViolation.deleteWhere { DarkstoreUserViolation.profileId eq profileId }
        }
    }

    //Internship
    fun getInternship(profileId: UUID): ResultRow {
        var internship: ResultRow? = null
        transaction(employeeDatabase) {
            addLogger(StdOutSqlLogger)
            internship = Internship.select { (Internship.profileId eq profileId) }.single()
        }
        return internship!!
    }

    fun getInternshipLogArray(profileId: UUID): MutableList<ResultRow> {
        var internshipLogArray: MutableList<ResultRow> = mutableListOf()
        transaction(employeeDatabase) {
            addLogger(StdOutSqlLogger)
            var query =
                InternshipLog.select { (InternshipLog.profileId eq profileId) }
            (query as Query).forEach {
                internshipLogArray.add(it)
            }
        }
        internshipLogArray.sortBy { it[InternshipLog.id] }

        return internshipLogArray
    }

    fun checkInternshipExists(profileId: UUID): Boolean {
        var exists = false
        transaction(employeeDatabase) {
            addLogger(StdOutSqlLogger)
            if (Internship.select { (Internship.profileId eq profileId) }
                    .count() > 0) {
                exists = true
            }
        }
        return exists
    }

    fun updateInternshipPlanningDate(profileId: UUID, newDate: String) {
        transaction(employeeDatabase) {
            addLogger(StdOutSqlLogger)
            Internship.update({ Internship.profileId eq profileId }) {
                it[date] = newDate
            }
        }

    }

    fun deletePartnerByTitle(title: String) {
        transaction(employeeDatabase) {
            addLogger(StdOutSqlLogger)
            StaffPartner.deleteWhere { StaffPartner.partnerTitle eq title }
        }
    }

    // requisitions
    fun getRequisitionByAccountingProfileId(accountingProfileId: String): ResultRow? {
        var requisition: ResultRow? = null
        transaction(employeeDatabase) {
            addLogger(StdOutSqlLogger)
            requisition =
                ProfileRequisition.select { ProfileRequisition.accountingProfileId eq accountingProfileId }
                    .firstOrNull()
        }
        return requisition
    }

    fun getRequisitionById(requisitionId: UUID): ResultRow? {
        var requisition: ResultRow? = null
        transaction(employeeDatabase) {
            addLogger(StdOutSqlLogger)
            requisition =
                ProfileRequisition.select { ProfileRequisition.requestId eq requisitionId }
                    .firstOrNull()
        }
        return requisition
    }

    fun getAllRequisitionsByMobile(mobile: String): MutableList<ResultRow> {
        var requisitions: MutableList<ResultRow> = mutableListOf()
        transaction(employeeDatabase) {
            addLogger(StdOutSqlLogger)
            var query =
                ProfileRequisition.select { (ProfileRequisition.mobile eq mobile) }
            (query as Query).forEach {
                requisitions.add(it)
            }
        }
        return requisitions
    }

    fun getRequisitionByAccountingProfileIdAndStatus(accountingProfileId: String, status: String): ResultRow? {
        var requisition: ResultRow? = null
        transaction(employeeDatabase) {
            addLogger(StdOutSqlLogger)
            requisition =
                ProfileRequisition.select { (ProfileRequisition.accountingProfileId eq accountingProfileId) and (ProfileRequisition.status eq status) }
                    .firstOrNull()
        }
        return requisition
    }

    fun getRequisitionLog(requestId: UUID): ResultRow {
        var requisition: ResultRow? = null
        transaction(employeeDatabase) {
            addLogger(StdOutSqlLogger)
            requisition = ProfileRequisitionLog.select { ProfileRequisitionLog.requestId eq requestId }.single()
        }
        return requisition!!
    }

    fun getRequisitionLogByVersion(requestId: UUID, version: Int): ResultRow {
        var requisition: ResultRow? = null
        transaction(employeeDatabase) {
            addLogger(StdOutSqlLogger)
            requisition =
                ProfileRequisitionLog.select { (ProfileRequisitionLog.requestId eq requestId) and (ProfileRequisitionLog.version eq version) }
                    .single()
        }
        return requisition!!
    }

    fun deleteRequisitionLogByRequestId(requestId: UUID) {
        transaction(employeeDatabase) {
            addLogger(StdOutSqlLogger)
            ProfileRequisitionLog.deleteWhere { ProfileRequisitionLog.requestId eq requestId }
        }
    }

    fun deleteRequisitionByRequestId(requestId: UUID) {
        transaction(employeeDatabase) {
            addLogger(StdOutSqlLogger)
            ProfileRequisition.deleteWhere { ProfileRequisition.requestId eq requestId }
        }
    }


    fun deleteContractByRequestId(accountingProfileId: String) {
        transaction(employeeDatabase) {
            addLogger(StdOutSqlLogger)
            Contract.deleteWhere { Contract.accountingProfileId eq accountingProfileId }
        }
    }

    fun checkRequisitionExists(accountingProfileId: String): Boolean {
        var exists = false
        transaction(employeeDatabase) {
            addLogger(StdOutSqlLogger)
            if (ProfileRequisition.select { (ProfileRequisition.accountingProfileId eq accountingProfileId) }
                    .count() > 0) {
                exists = true
            }
        }
        return exists
    }


    fun getRequisitionsCount(accountingProfileId: String): Int {
        var count = 0
        transaction(employeeDatabase) {
            addLogger(StdOutSqlLogger)
            count =
                ProfileRequisition.select { (ProfileRequisition.accountingProfileId eq accountingProfileId) and (ProfileRequisition.status eq "NEW")}.count()
                    .toInt()
        }
        return count
    }


    fun updateAccountingProfileId(profileId: UUID, newAccountingProfileId: String) {
        transaction(employeeDatabase) {
            addLogger(StdOutSqlLogger)
            Profile.update({ Profile.profileId eq profileId }) {
                it[accountingProfileId] = newAccountingProfileId
            }
        }

    }
}
