package no.nav.pensjon.opptjening.afp.api.api.model

import no.nav.pensjon.opptjening.afp.api.domain.AFPBeholdningsgrunnlag
import java.time.LocalDate

data class BeregnAFPBeholdningsgrunnlagRequest(
    val personId: String,
    val fraOgMedDato: LocalDate
)

data class BeregnAFPBeholdningsgrunnlagResponse(
    val afpBeholdningsgrunnlag: List<no.nav.pensjon.opptjening.afp.api.api.model.AFPBeholdningsgrunnlag>
) {
    companion object {
        fun of(afpBeholdningsgrunnlag: List<AFPBeholdningsgrunnlag>): BeregnAFPBeholdningsgrunnlagResponse {
            return BeregnAFPBeholdningsgrunnlagResponse(
                afpBeholdningsgrunnlag.map {
                    AFPBeholdningsgrunnlag(
                        fraOgMedDato = it.fraOgMedDato,
                        belop = it.beholdning,
                    )
                }
            )
        }
    }
}