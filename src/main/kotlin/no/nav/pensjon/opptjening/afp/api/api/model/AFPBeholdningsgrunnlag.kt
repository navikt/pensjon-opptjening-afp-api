package no.nav.pensjon.opptjening.afp.api.api.model

import java.time.LocalDate

data class PensjonsBeholdningsPeriode(
    val fraOgMedDato: LocalDate,
    val pensjonsBeholdning: Int
)