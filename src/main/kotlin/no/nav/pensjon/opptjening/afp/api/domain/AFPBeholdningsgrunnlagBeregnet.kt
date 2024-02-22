package no.nav.pensjon.opptjening.afp.api.domain

import java.time.Instant
import java.time.LocalDate
import java.util.UUID

data class AFPBeholdningsgrunnlagBeregnet(
    val id: UUID = UUID.randomUUID(),
    val tidspunkt: Instant = Instant.now(),
    val fnr: String,
    val uttaksdato: LocalDate,
    val konsument: String? = null,
    val grunnlag: List<AFPBeholdningsgrunnlag>
)