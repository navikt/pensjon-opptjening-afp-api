package no.nav.pensjon.opptjening.afp.api.popp.dto



data class DagpengerOpptjeningBelop(
    var dagpengerOpptjeningBelopId: Long? = null,
    var ar: Int? = null,
    var belopOrdinar: Double? = null,
    var belopFiskere: Double? = null,
    var dagpengerListe: List<Dagpenger>? = null,
    var changeStamp: ChangeStampDto? = null,
)
