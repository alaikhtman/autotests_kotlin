package ru.samokat.mysamokat.tests.dataproviders.employee

import ru.samokat.employeeprofiles.api.common.domain.Vehicle
import ru.samokat.employeeprofiles.api.profiles.update.UpdateProfileRequest
import ru.samokat.my.domain.PhoneNumber
import ru.samokat.my.domain.employee.EmployeeName
import ru.samokat.my.domain.employee.EmployeeRole
import ru.samokat.my.rest.api.enum.ApiEnum
import ru.samokat.mysamokat.tests.dataproviders.StringAndPhoneNumberGenerator
import java.util.*

class UpdateProfileRequestBuilder {

    private lateinit var mobile: PhoneNumber
    fun mobile(mobile: PhoneNumber) = apply { this.mobile = mobile }
    fun getMobile(): PhoneNumber {
        return mobile
    }

    fun randomMobile() =
        apply { mobile = PhoneNumber(value = StringAndPhoneNumberGenerator.generateRandomPhoneNumber()) }


    private var darkstoreId: UUID? = null
    fun darkstoreId(darkstoreId: UUID?) = apply { this.darkstoreId = darkstoreId }
    fun getDarkstoreId(): UUID {
        return darkstoreId!!
    }

    fun randomDarkstoreId() = apply { darkstoreId = UUID.randomUUID() }

    private var cityId: UUID? = null
    fun cityId(cityId: UUID?) = apply { this.cityId = cityId }
    fun cityId(): UUID? {
        return cityId!!
    }

    private var staffPartnerId: UUID? = null
    fun staffPartnerId(staffPartnerId: UUID?) = apply { this.staffPartnerId = staffPartnerId }
    fun getStaffPartnerId(): UUID {
        return staffPartnerId!!
    }

    fun randomStaffPartnerId() = apply { staffPartnerId = UUID.randomUUID() }


    private lateinit var issuerProfileId: UUID
    fun issuerProfileId(issuerProfileId: UUID) = apply { this.issuerProfileId = issuerProfileId }
    fun getIssuerProfileId(): UUID {
        return issuerProfileId
    }

    fun randomIssuerProfileId() = apply { issuerProfileId = UUID.randomUUID() }


    private var test: Boolean = true
    fun test(test: Boolean) = apply { this.test = test }
    fun getTest(): Boolean {
        return test
    }


    private lateinit var name: EmployeeName
    fun name(name: EmployeeName) = apply { this.name = name }
    fun getName(): EmployeeName {
        return name
    }

    fun randomName() = apply {
        name = EmployeeName(
            firstName = StringAndPhoneNumberGenerator.generateRandomString(10),
            middleName = StringAndPhoneNumberGenerator.generateRandomString(10),
            lastName = StringAndPhoneNumberGenerator.generateRandomString(10)
        )
    }


    private lateinit var roles: List<ApiEnum<EmployeeRole, String>>
    fun roles(roles: List<ApiEnum<EmployeeRole, String>>) = apply { this.roles = roles }
    fun getRoles(): List<ApiEnum<EmployeeRole, String>> {
        return roles
    }

    private var vehicle: Vehicle? = null
    fun vehicle(vehicle: Vehicle?) = apply { this.vehicle = vehicle }
    fun getVehicle(): Vehicle {
        return vehicle!!
    }

    private var supervisedDarkstore: MutableList<UUID>? = null
    fun supervisedDarkstore(supervisedDarkstore: MutableList<UUID>?) =
        apply { this.supervisedDarkstore = supervisedDarkstore }

    fun getSupervisedDarkstore(): MutableList<UUID> {
        return supervisedDarkstore!!
    }

    fun randomSupervisedDarkstore(supervisedDarkstoresCount: Int) = apply {
        supervisedDarkstore = mutableListOf()
        for (i in 0 until supervisedDarkstoresCount) {
            supervisedDarkstore!!.add(UUID.randomUUID())
        }
    }


    private var version: Long = 1
    fun version(version: Long) = apply { this.version = version }
    fun getVersion(): Long {
        return version
    }

    private var email: String? = null
    fun email(email: String?) = apply { this.email = email }
    fun getEmail(): String {
        return email!!
    }

    private var accountingProfileId: String? = null
    fun accountingProfileId(accountingProfileId: String?) = apply { this.accountingProfileId = accountingProfileId }
    fun getAccountingProfileId(): String{
        return accountingProfileId!!
    }

    fun build(): UpdateProfileRequest {
        return UpdateProfileRequest(
            mobile = mobile,
            name = name,
            roles = roles,
            darkstoreId = darkstoreId,
            staffPartnerId = staffPartnerId,
            vehicle = vehicle,
            test = test,
            issuerProfileId = issuerProfileId,
            supervisedDarkstores = supervisedDarkstore,
            version = version,
            email = email,
            accountingProfileId = accountingProfileId,
            cityId = cityId
        )
    }
}