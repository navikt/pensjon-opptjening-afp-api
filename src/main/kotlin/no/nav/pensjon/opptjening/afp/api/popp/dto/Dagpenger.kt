package no.nav.pensjon.opptjening.afp.api.popp.dto




data class Dagpenger(
    var dagpengerId: Long? = null,
    var fnr: String? = null,
    var dagpengerType: String? = null,
    var rapportType: String? = null,
    var kilde: String? = null,
    var ar: Int? = null,
    var utbetalteDagpenger: Int? = null,
    var uavkortetDagpengegrunnlag: Int? = null,
    var ferietillegg: Int? = null,
    var barnetillegg: Int? = null,
    var changeStamp: ChangeStampDto? = null,
)
