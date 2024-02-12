package no.nav.pensjon.opptjening.afp.api.api.model

import java.time.LocalDate

data class AFPBeholdningsgrunnlag(
    val fraOgMedDato: LocalDate,
    val belop: Int
)