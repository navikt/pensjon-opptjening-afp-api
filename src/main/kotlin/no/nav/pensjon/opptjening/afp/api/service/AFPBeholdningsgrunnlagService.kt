package no.nav.pensjon.opptjening.afp.api.service

import no.nav.pensjon.opptjening.afp.api.domain.AFPBeholdiningsgrunnlagBeregnetRepo
import no.nav.pensjon.opptjening.afp.api.domain.AFPBeholdningsgrunnlag
import no.nav.pensjon.opptjening.afp.api.domain.AFPBeholdningsgrunnlagBeregnet
import no.nav.pensjon.opptjening.afp.api.domain.FremtidigeInntekter
import no.nav.pensjon.opptjening.afp.api.domain.Pensjonsbeholdning
import no.nav.pensjon.opptjening.afp.api.domain.VelgAFPBeholdningsgrunnlag
import no.nav.pensjon.opptjening.afp.api.domain.person.Person
import no.nav.pensjon.opptjening.afp.api.domain.person.PersonException
import no.nav.pensjon.opptjening.afp.api.domain.person.Personoppslag
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class AFPBeholdningsgrunnlagService(
    private val pensjonsbeholdning: Pensjonsbeholdning,
    private val personoppslag: Personoppslag,
    private val repo: AFPBeholdiningsgrunnlagBeregnetRepo,
) {
    fun beregnAFPBeholdingsgrunnlag(
        fnr: String,
        uttaksDato: LocalDate,
        konsument: String,
    ): List<AFPBeholdningsgrunnlag> {
        val person = hentPerson(fnr)
        val beholdninger = pensjonsbeholdning.beregn(
            fnr = person.fnr,
            fraOgMed = person.fødselsÅr,
            tilOgMed = person.sekstiFørsteÅr,
        )
        val grunnlag = VelgAFPBeholdningsgrunnlag(
            fraOgMed = uttaksDato,
            beholdninger = beholdninger
        ).get()

        val beregnet = AFPBeholdningsgrunnlagBeregnet(
            fnr = fnr,
            uttaksdato = uttaksDato,
            konsument = konsument,
            grunnlag = grunnlag,
        )

        repo.lagre(beregnet)

        return beregnet.grunnlag
    }

    fun simulerAFPBeholdningsgrunnlag(
        fnr: String,
        uttaksDato: LocalDate,
        fremtidigeInntekter: FremtidigeInntekter
    ): List<AFPBeholdningsgrunnlag> {
        val person = hentPerson(fnr)
        val tilOgMed = person.sekstiFørsteÅr
        val årligInntekt = fremtidigeInntekter.årligInntekt(tilOgMed = tilOgMed)
        val beholdninger = pensjonsbeholdning.simuler(
            fnr = person.fnr,
            fraOgMed = person.fødselsÅr,
            tilOgMed = tilOgMed,
            inntekter = årligInntekt,
        )
        return VelgAFPBeholdningsgrunnlag(
            fraOgMed = uttaksDato,
            beholdninger = beholdninger
        ).get()
    }

    private fun hentPerson(fnr: String): Person {
        return personoppslag.hent(fnr) ?: throw PersonException.PersonIkkeFunnet("Fant ikke person")
    }
}