package no.nav.pensjon.opptjening.afp.api.domain

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.UUID

val zoneIdOslo: ZoneId = ZoneId.of("Europe/Oslo")

data class AFPBeholdningsgrunnlagBeregnet(
    val id: UUID = UUID.randomUUID(),
    val tidspunkt: ZonedDateTime = Instant.now().atZone(zoneIdOslo),
    val fnr: String,
    val uttaksdato: LocalDate,
    val konsument: String? = null,
    val grunnlag: List<AFPBeholdningsgrunnlag>
)