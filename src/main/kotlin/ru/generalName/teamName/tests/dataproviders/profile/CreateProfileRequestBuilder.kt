package ru.generalName.teamName.tests.dataproviders.profile


import java.util.*

class CreateProfileRequestBuilder {

    private var generatePassword: Boolean = false
    fun generatePassword(generatePassword: Boolean) = apply { this.generatePassword = generatePassword }
    fun isGeneratePassword(): Boolean {
        return generatePassword
    }

    private var darkstoreId: UUID? = null
    fun darkstoreId(darkstoreId: UUID?) = apply { this.darkstoreId = darkstoreId }
    fun getDarkstoreId(): UUID {
        return darkstoreId!!
    }


    private var cityId: UUID? = null
    fun cityId(cityId: UUID?) = apply { this.cityId = cityId }
    fun cityId(): UUID? {
        return cityId!!
    }

    fun randomDarkstoreId() = apply { darkstoreId = UUID.randomUUID() }

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

    private lateinit var mobile: PhoneNumber
    fun mobile(mobile: PhoneNumber) = apply { this.mobile = mobile }
    fun getMobile(): PhoneNumber {
        return mobile
    }

    fun randomMobile() =
        apply { mobile = PhoneNumber(value = StringAndPhoneNumberGenerator.generateRandomPhoneNumber()) }


    private lateinit var name: EmployeeName
    fun name(name: EmployeeName) = apply { this.name = name }
    fun getName(): EmployeeName {
        return name
    }

    fun randomFirstAndLastName() = apply {
        name = EmployeeName(
            firstName = StringAndPhoneNumberGenerator.generateRandomString(10),
            lastName = StringAndPhoneNumberGenerator.generateRandomString(10)
        )
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

    fun randomSupervisedDarkstore(supervisedDarkstoresCount: Int?) = apply {
        if (supervisedDarkstoresCount != 0) {
            supervisedDarkstore = mutableListOf()
            for (i in 0 until supervisedDarkstoresCount!!) {
                supervisedDarkstore!!.add(UUID.randomUUID())
            }
        }
    }

    private var email: String? = null
    fun email(email: String?) = apply { this.email = email }
    fun getEmail(): String {
        return email!!
    }

    private var accountingProfileId: String? = null
    fun accountingProfileId(accountingProfileId: String?) = apply { this.accountingProfileId = accountingProfileId }
    fun getAccountingProfileId(): String {
        return accountingProfileId!!
    }

    private var requisitionId: UUID? = null
    fun requisitionId(requisitionId: UUID?) = apply { this.requisitionId = requisitionId }
    fun getRequisitionId(): UUID {
        return requisitionId!!
    }

    fun build(): CreateProfileRequest {
        return CreateProfileRequest(
            mobile = mobile,
            name = name,
            roles = roles,
            darkstoreId = darkstoreId,
            staffPartnerId = staffPartnerId,
            vehicle = vehicle,
            test = test,
            generatePassword = generatePassword,
            issuerProfileId = issuerProfileId,
            supervisedDarkstores = supervisedDarkstore,
            email = email,
            accountingProfileId = accountingProfileId,
            requisitionId = requisitionId,
            cityId = cityId
        )
    }
}