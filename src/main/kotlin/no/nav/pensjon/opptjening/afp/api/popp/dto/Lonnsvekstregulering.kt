package no.nav.pensjon.opptjening.afp.api.popp.dto

import java.util.*

data class Lonnsvekstregulering(
    var lonnsvekstreguleringId: Long? = null,
    var reguleringsbelop: Double? = null,
    var reguleringsDato: Date? = null,
    var changeStamp: ChangeStampDto? = null,
)