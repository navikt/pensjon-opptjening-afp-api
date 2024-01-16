package no.nav.pensjon.opptjening.afp.api.popp.dto


import java.util.*

data class Forstegangstjeneste(
    var forstegangstjenesteId: Long? = null,
    var fnr: String? = null,
    var kilde: String? = null,
    var rapportType: String? = null,
    var tjenestestartDato: Date? = null,
    var dimitteringDato: Date? = null,
    var forstegangstjenestePeriodeListe: List<ForstegangstjenestePeriode>? = null,
    var changeStamp: ChangeStampDto? = null
)
