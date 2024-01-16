package no.nav.pensjon.opptjening.afp.api.api.model

import no.nav.pensjon.opptjening.afp.api.domain.AFPBeholdningsgrunnlag
import java.time.LocalDate

data class BeregnAFPBeholdningsgrunnlagRequest(
    val personId: String,
    val fraOgMedDato: LocalDate
)

data class BeregnAFPBeholdningsgrunnlagResponse(
    val personId: String,
    val afpGrunnlagBeholdninger: List<AFPBeholdningsgrunnlag>
)