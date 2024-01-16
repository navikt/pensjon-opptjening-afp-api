package no.nav.pensjon.opptjening.afp.api.popp.dto



data class Omsorg(
    var omsorgId: Long? = null,
    var fnr: String? = null,
    var fnrOmsorgFor: String? = null,
    var omsorgType: String? = null,
    var kilde: String? = null,
    var ar: Int? = null,
    var changeStamp: ChangeStampDto? = null,
)
