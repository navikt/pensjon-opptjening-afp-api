package no.nav.pensjon.opptjening.afp.api.popp.dto




data class Inntekt(
    var changeStamp: ChangeStampDto? = null,
    var inntektId: Long? = null,
    var fnr: String? = null,
    var inntektAr: Int? = null,
    var kilde: String? = null,
    var kommune: String? = null,
    var piMerke: String? = null,
    var inntektType: String? = null,
    var belop: Long? = null,
)
