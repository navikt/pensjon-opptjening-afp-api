package no.nav.pensjon.opptjening.afp.api.api.model

import no.nav.pensjon.opptjening.afp.api.domain.AFPBeholdningsgrunnlag
import no.nav.pensjon.opptjening.afp.api.domain.Inntekt
import java.time.LocalDate

data class SimulerAFPBeholdningsgrunnlagRequest(
    val personId: String,
    val fraOgMedDato: LocalDate,
    val inntekter: List<Inntekt>
)

data class SimulerAFPBeholdningsgrunnlagResponse(
    val personId: String,
    val afpGrunnlagBeholdninger: List<AFPBeholdningsgrunnlag>
)