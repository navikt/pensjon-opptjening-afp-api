package no.nav.pensjon.opptjening.afp.api.popp.dto


import java.util.Date


data class Beholdning(
    var beholdningId: Long? = null,
    var fnr: String? = null,
    var status: String? = null,
    var beholdningType: String? = null,
    var belop: Double? = null,
    var vedtakId: Long? = null,
    var fomDato: Date? = null,
    var tomDato: Date? = null,
    var beholdningGrunnlag: Double? = null,
    var beholdningGrunnlagAvkortet: Double? = null,
    var beholdningInnskudd: Double? = null,
    var beholdningInnskuddUtenOmsorg: Double? = null,
    var oppdateringArsak: String? = null,
    var lonnsvekstregulering: Lonnsvekstregulering? = null,
    var inntektOpptjeningBelop: InntektOpptjeningBelop? = null,
    var omsorgOpptjeningBelop: OmsorgOpptjeningBelop? = null,
    var dagpengerOpptjeningBelop: DagpengerOpptjeningBelop? = null,
    var forstegangstjenesteOpptjeningBelop: ForstegangstjenesteOpptjeningBelop? = null,
    var uforeOpptjeningBelop: UforeOpptjeningBelop? = null,
    var changeStamp: ChangeStampDto? = null,
)
