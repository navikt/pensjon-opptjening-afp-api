package no.nav.pensjon.opptjening.afp.api.service

import no.nav.pensjon.opptjening.afp.api.domain.AFPBeholdningsgrunnlag
import no.nav.pensjon.opptjening.afp.api.domain.Inntekt
import no.nav.pensjon.opptjening.afp.api.domain.person.Person
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
        fraOgMed: LocalDate,
    ): List<AFPBeholdningsgrunnlag> {
        val tilOgMed = hentPerson(fnr).sekstiFørsteÅr
        return poppClient.beregnPensjonsbeholdning(
            fnr = fnr,
            fraOgMed = fraOgMed.year,
            tilOgMed = tilOgMed,
        )
    }

    fun simulerAFPBeholdningsgrunnlag(
        fnr: String,
        fraOgMed: LocalDate,
        inntekter: List<Inntekt>
    ): List<AFPBeholdningsgrunnlag> {
        val tilOgMed = hentPerson(fnr).sekstiFørsteÅr
        return poppClient.simulerPensjonsbeholdning(
            fnr = fnr,
            fraOgMed = fraOgMed.year,
            tilOgMed = tilOgMed,
            inntekter = inntekter
        )
    }

    private fun hentPerson(fnr: String): Person {
        return pdlClient.hentPerson(fnr) ?: throw RuntimeException("Fant ikke person")
    }
}