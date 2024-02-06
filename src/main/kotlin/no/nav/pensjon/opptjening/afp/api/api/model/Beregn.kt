package no.nav.pensjon.opptjening.afp.api.api.model

import no.nav.pensjon.opptjening.afp.api.domain.AFPBeholdningsgrunnlag
import java.time.LocalDate

data class BeregnAFPBeholdningsgrunnlagRequest(
    val personId: String,
    val fraOgMedDato: LocalDate
)

data class BeregnAFPBeholdningsgrunnlagResponse(
    val pensjonsBeholdningsPeriodeListe: List<PensjonsBeholdningsPeriode>
) {
    companion object {
        fun of(afpBeholdningsgrunnlag: List<AFPBeholdningsgrunnlag>): BeregnAFPBeholdningsgrunnlagResponse {
            return BeregnAFPBeholdningsgrunnlagResponse(
                afpBeholdningsgrunnlag.map {
                    PensjonsBeholdningsPeriode(
                        fraOgMedDato = it.fraOgMedDato,
                        pensjonsBeholdning = it.beholdning,
                    )
                }
            )
        }
    }
}