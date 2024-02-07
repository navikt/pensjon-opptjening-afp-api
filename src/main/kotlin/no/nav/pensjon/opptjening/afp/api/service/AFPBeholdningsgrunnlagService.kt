package no.nav.pensjon.opptjening.afp.api.service

import no.nav.pensjon.opptjening.afp.api.domain.AFPBeholdningsgrunnlag
import no.nav.pensjon.opptjening.afp.api.domain.Aldersbegrensning61År
import no.nav.pensjon.opptjening.afp.api.domain.BeholdningsAr
import no.nav.pensjon.opptjening.afp.api.domain.FremtidigeInntekter
import no.nav.pensjon.opptjening.afp.api.domain.Pensjonsbeholdning
import no.nav.pensjon.opptjening.afp.api.domain.person.Personoppslag
import no.nav.pensjon.opptjening.afp.api.domain.VelgAFPBeholdningsgrunnlag
import no.nav.pensjon.opptjening.afp.api.domain.person.Person
import no.nav.pensjon.opptjening.afp.api.domain.person.PersonException
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class AFPBeholdningsgrunnlagService(
    private val pensjonsbeholdning: Pensjonsbeholdning,
    private val personoppslag: Personoppslag,
) {
    fun beregnAFPBeholdingsgrunnlag(
        fnr: String,
        beholdningFraOgMed: LocalDate,
    ): List<AFPBeholdningsgrunnlag> {
        val person = hentPerson(fnr)
        val tilOgMed = begrensTil61ÅrOpptjening(beholdningFraOgMed, person)
        val beholdninger = pensjonsbeholdning.beregn(
            fnr = person.fnr,
            fraOgMed = person.fødselsÅr,
            tilOgMed = tilOgMed,
        )
        return VelgAFPBeholdningsgrunnlag(
            fraOgMed = beholdningFraOgMed,
            beholdninger = beholdninger
        ).get()
    }

    fun simulerAFPBeholdningsgrunnlag(
        fnr: String,
        beholdningFraOgMed: LocalDate,
        fremtidigeInntekter: FremtidigeInntekter
    ): List<AFPBeholdningsgrunnlag> {
        val person = hentPerson(fnr)
        val tilOgMed = begrensTil61ÅrOpptjening(beholdningFraOgMed, person)
        val årligInntekt = fremtidigeInntekter.årligInntekt(tilOgMed = tilOgMed)
        val beholdninger = pensjonsbeholdning.simuler(
            fnr = person.fnr,
            fraOgMed = person.fødselsÅr,
            tilOgMed = tilOgMed,
            inntekter = årligInntekt,
        )
        return VelgAFPBeholdningsgrunnlag(
            fraOgMed = beholdningFraOgMed,
            beholdninger = beholdninger
        ).get()
    }

    private fun begrensTil61ÅrOpptjening(
        beholdningFraOgMed: LocalDate,
        person: Person
    ): Int {
        val beholdningÅr = BeholdningsAr(beholdningFraOgMed.year)
        val opptjeningsÅr = beholdningÅr.opptjeningsAr()
        val tilOgMed = Aldersbegrensning61År.begrens(person, opptjeningsÅr.ar)
        return tilOgMed
    }

    private fun hentPerson(fnr: String): Person {
        return personoppslag.hent(fnr) ?: throw PersonException.PersonIkkeFunnet("Fant ikke person")
    }
}