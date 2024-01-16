package no.nav.pensjon.opptjening.afp.api.popp.dto



data class ForstegangstjenesteOpptjeningBelop(
    var forstegangstjenesteOpptjeningBelopId: Long? = null,
    var belop: Double? = null,
    var ar: Int? = null,
    var forstegangstjeneste: Forstegangstjeneste? = null,
    var anvendtForstegangstjenestePeriodeListe: List<ForstegangstjenestePeriode>? = null,
    var changeStamp: ChangeStampDto? = null,
)
