package no.nav.pensjon.opptjening.afp.api.domain

import java.time.LocalDate

data class AFPBeholdningsgrunnlag(
    val fraOgMedDato: LocalDate,
    val tilOgMedDato: LocalDate?,
    val beholdning: Int
)