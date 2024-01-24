package no.nav.pensjon.opptjening.afp.api.service

import no.nav.pensjon.opptjening.afp.api.domain.AFPBeholdningsgrunnlag
import no.nav.pensjon.opptjening.afp.api.domain.Inntekt
import no.nav.pensjon.opptjening.afp.api.domain.person.Person
import no.nav.pensjon.opptjening.afp.api.domain.person.PersonException
import no.nav.pensjon.opptjening.afp.api.domain.Aldersbegrensning61År
import no.nav.pensjon.opptjening.afp.api.domain.BeholdningsAr
import no.nav.pensjon.opptjening.afp.api.domain.VelgAFPBeholdningsgrunnlag
import no.nav.pensjon.opptjening.afp.api.pdl.PdlClient
import no.nav.pensjon.opptjening.afp.api.popp.PoppClient
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class AFPBeholdningsgrunnlagService(
    private val poppClient: PoppClient,
    private val pdlClient: PdlClient,
) {
    fun beregnAFPBeholdingsgrunnlag(
        fnr: String,
        beholdningFraOgMed: LocalDate,
    ): List<AFPBeholdningsgrunnlag> {
        val person = hentPerson(fnr)
        val tilOgMed = begrensTil61ÅrOpptjening(beholdningFraOgMed, person)
        val beholdninger = poppClient.beregnPensjonsbeholdning(
            fnr = fnr,
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
        inntekter: List<Inntekt>
    ): List<AFPBeholdningsgrunnlag> {
        val person = hentPerson(fnr)
        val tilOgMed = begrensTil61ÅrOpptjening(beholdningFraOgMed, person)
        val beholdninger = poppClient.simulerPensjonsbeholdning(
            fnr = fnr,
            fraOgMed = person.fødselsÅr,
            tilOgMed = tilOgMed,
            inntekter = inntekter
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
        return pdlClient.hentPerson(fnr) ?: throw PersonException.PersonIkkeFunnet("Fant ikke person")
    }
}