package no.nav.pensjon.opptjening.afp.api.api.model

import no.nav.pensjon.opptjening.afp.api.domain.AFPBeholdningsgrunnlag
import no.nav.pensjon.opptjening.afp.api.domain.FremtidigInntekt
import no.nav.pensjon.opptjening.afp.api.domain.FremtidigeInntekter
import java.time.LocalDate
import java.time.YearMonth

data class SimulerAFPBeholdningsgrunnlagRequest(
    val personId: String,
    val uttaksDato: LocalDate,
    private val fremtidigInntektListe: List<FremtidigInntekter> = emptyList(),
) {
    init {
        fremtidigInntektListe.groupBy { it.fraOgMedDato }
            .forEach { (dato, inntekter) ->
                if (inntekter.size > 1) throw UgyldigApiRequestException("fremtidigInntektListe har flere verdier for fraOgMedDato: $dato")
            }
        fremtidigInntektListe.map { YearMonth.of(it.fraOgMedDato.year, it.fraOgMedDato.month) }.groupBy { it }
            .forEach { (årMåned, inntekter) ->
                if (inntekter.size > 1) throw UgyldigApiRequestException("fremtidigInntektListe har flere verdier for måned: $årMåned")
            }
    }

    val fremtidigeInntekter = FremtidigeInntekter(
        fremtidigInntektListe.map {
            FremtidigInntekt(
                måned = YearMonth.of(it.fraOgMedDato.year, it.fraOgMedDato.month),
                årligInntekt = it.arligInntekt,
            )
        })
}

data class SimulerAFPBeholdningsgrunnlagResponse(
    val afpBeholdningsgrunnlag: List<no.nav.pensjon.opptjening.afp.api.api.model.AFPBeholdningsgrunnlag>
) {
    companion object {
        fun of(afpBeholdningsgrunnlag: List<AFPBeholdningsgrunnlag>): SimulerAFPBeholdningsgrunnlagResponse {
            return SimulerAFPBeholdningsgrunnlagResponse(
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


data class FremtidigInntekter(
    val fraOgMedDato: LocalDate,
    val arligInntekt: Int,
)