package ru.samokat.mysamokat.tests.dataproviders

import ru.samokat.my.domain.PhoneNumber
import java.util.*

object Constants {
    val mobile1 = PhoneNumber("70009997711")
    val mobile2 = PhoneNumber("70009997722")
    val mobile3 = PhoneNumber("70009997733")
    val mobile4 = PhoneNumber("70009997744")
    val mobile5 = PhoneNumber("70009997755")
    val mobile6 = PhoneNumber("70009997766")
    val mobile7 = PhoneNumber("70009997777")
    val mobile8 = PhoneNumber("79999997711")
    val mobile9 = PhoneNumber("79299997711")
    val mobile10 = PhoneNumber("79399997711")
    val mobileChaChaCha1 = PhoneNumber("79049999771")
    val mobileChaChaCha2 = PhoneNumber("79219999771")
    val mobileChaChaCha3 = PhoneNumber("79049999771")
    val mobileChaChaCha4 = PhoneNumber("79069999771")
    val searchMobilePart = "700099977"

    val cityId: UUID = UUID.fromString("b89dac1e-b169-44b8-bac1-949f0fd38419")
    val updatedCityId: UUID = UUID.fromString("779d6467-0b5a-4c96-b605-99629f935023")

    val darkstoreId: UUID = UUID.fromString("10976eb9-a945-11ea-9e08-0050560306e1")
    val hubId: UUID = UUID.fromString("5a802da2-3e2b-11ea-b77f-2e728ce88125")
    val updatedDarkstoreId: UUID = UUID.fromString("51977a00-5d38-11ea-a7b3-0050560306e1")
    val darkstoreIdWithNotMoscowTimezone: UUID = UUID.fromString("48b717b7-6a1b-11eb-85a3-1c34dae33151")
    val inactiveDarkstore: UUID = UUID.fromString("aed7c793-5d40-11ea-a7b3-0050560306e1")
    val planningDarkstore: UUID = UUID.fromString("522ec083-7107-11eb-85a3-1c34dae33151")
    val defaultEmail = "email@mail.ru"
    val defaultStaffPartnerId = UUID.fromString("ebd4837d-f36d-4314-89d3-dc0b72efea70")
    val staffPartnerId = UUID.fromString("ebd4837d-f36d-4314-89d3-dc0b72efea51")
    var searchProfileDarkstore: UUID = UUID.fromString("288665b1-6a12-11eb-85a3-1c34dae33151")

    var searchContactsDarkstore: UUID = UUID.fromString("a896627d-3012-11eb-859a-1c34dae33151")
    var searchContactsDarkstore2: UUID = UUID.fromString("f598e423-3731-11ec-a0ee-ec0d9a21b021")
    var searchContactsDarkstore3: UUID = UUID.fromString("d4972086-c83c-11eb-85ac-1c34dae33151")
    var searchContactsDarkstore4: UUID = UUID.fromString("ad6695ad-387a-11eb-859a-1c34dae33151")

    val existedMobile = PhoneNumber("79046098780")


    val supervisedDarkstores: MutableList<UUID> = mutableListOf(
        UUID.fromString("10976eb9-a945-11ea-9e08-0050560306e1"),
        UUID.fromString("51977a00-5d38-11ea-a7b3-0050560306e1")
    )

    val uuidForComment1: UUID = UUID.fromString("0acb0026-4333-46ad-a104-02fb2cda5387")
    val uuidForComment2: UUID = UUID.fromString("e197de79-0f2f-457d-9fd1-1c82bb29a6c1")
    val uuidForComment3: UUID = UUID.fromString("8143f72b-3ce5-4fe6-80b1-134b62d875a6")

    val chachachayUsersCompleted: Map<UUID, Long> = mapOf(
        UUID.fromString("c517a831-bf8b-4379-bed2-13ab440d1b0f") to 1589905L,
        UUID.fromString("32bfffd5-60b4-4565-ab85-a720a2b3bcb1") to -1000L,
        UUID.fromString("62914eec-548a-4595-906a-d6dd2226d481") to 10099L,
        UUID.fromString("1ac563eb-cf67-4e15-95f1-cda901415721") to 999999999L,
        UUID.fromString("5f269827-54a7-4024-9baa-c61d05d0e817") to 74800L,
        UUID.fromString("a742683e-3855-470f-843c-bb0986a2b893") to 48950L,
    )

    val chachachayPositiveBalanceMobile = PhoneNumber("79000000924")
    val chachachayPositiveBalancePassword = "53221450"
    val chachachayNegativeBalanceMobile = PhoneNumber("79000000921")
    val chachachayNegativeBalancePassword = "28279638"
    val chachachayMaxBalanceMobile = PhoneNumber("79000000923")
    val chachachayMaxBalancePassword = "61115561"
    val chachachayInProgressMobile = PhoneNumber("79056575087")
    val chachachayInProgressPassword = "64920072"

    val chachachayFailedToSyncMobile = PhoneNumber("70000011223")
    val chachachayFailedToSyncPassword = "22905393"


    val chachachayInProgress = UUID.fromString("b5c497d8-b607-48b5-ac51-84f6050724ce")
    val chachachayFailedToSync = UUID.fromString("d95d1cf5-dadd-4964-868a-c76f2eac1b22")
    val orderId = UUID.fromString("8fd91c14-b05c-48e7-9f71-2e85bafbebb0")
    val chachachayToken = "aLE5usE5egakYbUwYxE7ArA7"
    val restId = "19475"
    val chachachayUrl = "https://chachachay.me/sp.php"
    val clientPhone = "79313089157"
    val deliverymanPhone = "79313089155"

    val darkstoreForTimesheet = UUID.fromString("176b57b2-d18d-11ea-b7de-0050560306e1")
    val accountingProfileIdForTimesheet1: String = "1113edd3-74ec-46d0-b4ae-6bcf8a8271a8"
    val accountingProfileIdForTimesheet2: String = "02febb1d-1712-4356-9e22-41f062285b2e"
    val accountingProfileIdForTimesheet3: String = "f6d5315f-97e8-45da-a9a4-73d57f2c46c9"
    val accountingProfileIdWithTwoContracts: String = "25f3edd3-74ec-46d0-b4ae-6bcf8a8271a6"
    val accountingProfileIdWithInactiveContract: String = "f4035e54-f1ba-45dd-be0c-ca7ba71bb1a5"
    val accountingProfileIdWithActiveAndInactiveContracts: String = "a05b847b-3c9d-11ec-add3-043f72d11777"
    val accountingProfileIdWith2InactiveContracts: String = "40fb9cf2-970e-4252-b4ed-9d968e029763"
    val absentIssuerId = UUID.fromString("00000000-0000-0000-0000-000000000000")
    val activeContract1 = UUID.fromString("4649a1ab-24f1-11ec-9189-ec0d9ab1c881")
    val activeContract2 = UUID.fromString("4689a1ab-24f1-11ec-9189-ec0d9ab1c881")
    val activeContract3 = UUID.fromString ("a6e2169c-df4e-11eb-85ad-1c34dae11999")


    val accountingProfileIdForRequisitions = UUID.fromString("096cdc23-dadf-46f4-9864-074a7d0927fb")
    val accountingProfileIdForRequisitions2 = UUID.fromString("096cdc23-dadf-46f4-9864-074a7d0937fb")
    val accountingProfileIdForRequisitions3 = UUID.fromString("096cdc23-dadf-46f4-9864-074a7d0947fb")

    val innForRequisitions = "027565019853"
    val innForRequisitions2= "546432475643"
    val profileWithTimeSlots = UUID.fromString("6bfaacf8-8cc6-4e68-b208-d969f81cf8f3")


    val innFL = "215526944267"
    val innUL = "7176709756"
    // mobile config
    val techSupportFaqMobileTitle = "Позвонить на Линию заботы"
    val techSupportFaqMobile = "tel:+78005050588"
    val listScheduleMaxDays = 30
    val shiftScheduleMinHours = 2

    // hrp_map
    val hrpTestCityId = UUID.fromString("03512854-b983-44fb-928e-7788a590f09b")
    val hrpTestCityId1 = UUID.fromString("13512854-b983-44fb-928e-7788a590f09b")
    val hrpTestCityId2 = UUID.fromString("23512854-b983-44fb-928e-7788a590f09b")
    val hrpTestDarktoreId1 = UUID.fromString("83512854-b983-44fb-928e-7788a590f09b")
    val hrpTestDarktoreId2 = UUID.fromString("93512854-b983-44fb-928e-7788a590f09b")
    val hrpTestDarktoreId3 = UUID.fromString("73512854-b983-44fb-928e-7788a590f09b")
    val hrpTestDarktoreId4 = UUID.fromString("f541fcf7-e198-11e8-ac06-00155d462103")//darkstore not exist
    //val hrpFinishedToOperate = "2022-05-18 09:25:51.864742 +00:00"

}

