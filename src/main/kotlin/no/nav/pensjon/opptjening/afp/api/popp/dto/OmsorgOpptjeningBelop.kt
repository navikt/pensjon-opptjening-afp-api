package no.nav.pensjon.opptjening.afp.api.popp.dto



data class OmsorgOpptjeningBelop(
    var omsorgOpptjeningBelopId: Long? = null,
    var ar: Int? = null,
    var belop: Double? = null,
    var omsorgOpptjeningInnskudd: Double? = null,
    var omsorgListe: List<Omsorg>? = null,
    var changeStamp: ChangeStampDto? = null,
)