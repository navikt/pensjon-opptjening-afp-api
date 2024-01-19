package no.nav.pensjon.opptjening.afp.api.service

import no.nav.pensjon.opptjening.afp.api.domain.AFPBeholdningsgrunnlag
import no.nav.pensjon.opptjening.afp.api.domain.Inntekt
import no.nav.pensjon.opptjening.afp.api.domain.person.Person
import no.nav.pensjon.opptjening.afp.api.domain.person.PersonException
import no.nav.pensjon.opptjening.afp.api.pdl.PdlClient
import no.nav.pensjon.opptjening.afp.api.popp.PoppClient
import org.springframework.stereotype.Service
import java.time.LocalDate
import kotlin.math.min

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
        val opptjeningsÅr = beholdningFraOgMed.year - 2
        //kun ta med opptjening tom 61 år
        val tilOgMed = min(person.sekstiFørsteÅr, opptjeningsÅr)
        return poppClient.beregnPensjonsbeholdning(
            fnr = fnr,
            fraOgMed = person.fødselsÅr,
            tilOgMed = tilOgMed,
        )
    }

    fun simulerAFPBeholdningsgrunnlag(
        fnr: String,
        beholdningFraOgMed: LocalDate,
        inntekter: List<Inntekt>
    ): List<AFPBeholdningsgrunnlag> {
        val person = hentPerson(fnr)
        //kun ta med opptjening tom 61 år
        val opptjeningsÅr = beholdningFraOgMed.year - 2
        val tilOgMed = min(person.sekstiFørsteÅr, opptjeningsÅr)
        //TODO alle inntekter må være tidlgiere eller lik tilOgMed, hvis ikke vil de ikke ha noen effekt
        return poppClient.simulerPensjonsbeholdning(
            fnr = fnr,
            fraOgMed = person.fødselsÅr,
            tilOgMed = tilOgMed,
            inntekter = inntekter
        )
    }

    private fun hentPerson(fnr: String): Person {
        return pdlClient.hentPerson(fnr) ?: throw PersonException.PersonIkkeFunnet("Fant ikke person")
    }
}