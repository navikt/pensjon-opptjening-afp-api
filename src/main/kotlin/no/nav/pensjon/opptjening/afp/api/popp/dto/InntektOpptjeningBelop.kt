package no.nav.pensjon.opptjening.afp.api.popp.dto



data class InntektOpptjeningBelop(
    var inntektOpptjeningBelopId: Long? = null,
    var ar: Int? = null,
    var belop: Double? = null,
    var sumPensjonsgivendeInntekt: Inntekt? = null,
    var inntektListe: List<Inntekt>? = null,
    var changeStamp: ChangeStampDto? = null,
)
