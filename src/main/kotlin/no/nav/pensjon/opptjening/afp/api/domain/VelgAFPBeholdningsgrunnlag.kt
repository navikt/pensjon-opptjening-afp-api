package no.nav.pensjon.opptjening.afp.api.domain

import java.time.LocalDate

class VelgAFPBeholdningsgrunnlag(
    private val fraOgMed: LocalDate,
    beholdninger: List<AFPBeholdningsgrunnlag>
) {
    private val sorted = beholdninger.sortedBy { it.fraOgMedDato }

    fun get(): List<AFPBeholdningsgrunnlag>{
        return sorted.filter { it.tilOgMedDato == null || it.tilOgMedDato >= fraOgMed }
    }
}