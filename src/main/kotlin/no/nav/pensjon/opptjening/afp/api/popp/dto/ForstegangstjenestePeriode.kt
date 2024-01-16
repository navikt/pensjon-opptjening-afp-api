package no.nav.pensjon.opptjening.afp.api.popp.dto


import java.util.*

data class ForstegangstjenestePeriode(
    var forstegangstjenestePeriodeId: Long? = null,
    var periodeType: String? = null,
    var tjenesteType: String? = null,
    var fomDato: Date? = null,
    var tomDato: Date? = null,
    var changeStamp: ChangeStampDto? = null,
)