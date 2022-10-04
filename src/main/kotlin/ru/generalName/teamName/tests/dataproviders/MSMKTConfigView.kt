package ru.samokat.mysamokat.tests.dataproviders

data class MSMKTConfigView(
    val updateDateTime: String,
    val config: Config
)

data class Config(
    val tips_info: TipsInfo,
    val vacancies: Vacancies,
    val promo_offers: PromoOffers,
    val promo_referal: Promo,
    val couriers_codex: Codex,
    val promo_insurance: PromoInsurance,
    val tech_support_faq: TechSupportFaq,
    val list_schedule_max_days: Int,
    val shifts_schedule_min_hours: Int,
    val internship_feedback_cities: String,
    val enable_feat_expectations_profile: Boolean,
    val enable_feat_expectations_onboarding: Boolean
)

data class TipsInfo(
    val text: String,
    val buttonMain: ButtonMain
)

data class ButtonMain(
    val kind: String?,
    val close: Boolean?,
    val title: String,
    val url: String?
)

data class Vacancies(
    val tabs: List<Tabs>
)

data class Tabs(
    val key: String,
    val title: String,
    val content: TabContent
)

data class TabContent(
    val text: String,
    val buttonMain: ButtonMain
)

data class PromoOffers(
    val header: String,
    val offers: List<PromoOffer>
)

data class PromoOffer(
    val id: String,
    val title: String,
    val buttons: List<Button>,
    val titleColor: String?,
    val description: String
)

data class Button(
    val url: String,
    val title: String
)

data class Promo(
    val text: String,
    val buttonMain: Button
)

data class PromoInsurance(
    val deliveryman: Promo
)

data class TechSupportFaq(
    val links: List<Button>,
    val topics: List<Topic>
)

data class Topic(
    val title: String,
    val content: List<Content>
)

data class Content(
    val text: String?,
    val type: String,
    val url: String?,
    val description: String?,
    val wh: Wh?
)

data class Wh(
    val h: Int,
    val w: Int
)

data class Codex(
    val deliveryman: Text
)

data class Text(
    val text: String
)