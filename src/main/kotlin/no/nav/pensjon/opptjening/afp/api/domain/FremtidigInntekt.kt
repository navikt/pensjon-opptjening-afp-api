package no.nav.pensjon.opptjening.afp.api.domain

import java.time.YearMonth

data class FremtidigInntekt(
    val måned: YearMonth,
    val årligInntekt: Int
) {
     fun år(): Int = måned.year
}