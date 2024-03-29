package no.nav.pensjon.opptjening.afp.api.service

import no.nav.pensjon.opptjening.afp.api.domain.AFPBeholdiningsgrunnlagBeregnetRepo
import no.nav.pensjon.opptjening.afp.api.domain.AFPBeholdningsgrunnlag
import no.nav.pensjon.opptjening.afp.api.domain.FremtidigInntekt
import no.nav.pensjon.opptjening.afp.api.domain.FremtidigeInntekter
import no.nav.pensjon.opptjening.afp.api.domain.Pensjonsbeholdning
import no.nav.pensjon.opptjening.afp.api.domain.person.Ident
import no.nav.pensjon.opptjening.afp.api.domain.person.IdentHistorikk
import no.nav.pensjon.opptjening.afp.api.domain.person.Person
import no.nav.pensjon.opptjening.afp.api.domain.person.PersonException
import no.nav.pensjon.opptjening.afp.api.domain.person.Personoppslag
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.LocalDate
import java.time.Month
import java.time.YearMonth

class AFPBeholdningsgrunnlagServiceTest {

    private val pensjonsbeholdning: Pensjonsbeholdning = mock()
    private val personoppslag: Personoppslag = mock()
    private val repo: AFPBeholdiningsgrunnlagBeregnetRepo = mock()
    private val service: AFPBeholdningsgrunnlagService = AFPBeholdningsgrunnlagService(
        pensjonsbeholdning = pensjonsbeholdning,
        personoppslag = personoppslag,
        repo = repo,
    )

    @Test
    fun `kaster exception dersom person ikke eksisterer`() {
        assertThrows<PersonException.PersonIkkeFunnet> {
            service.beregnAFPBeholdingsgrunnlag(
                fnr = "1",
                uttaksDato = LocalDate.now(),
                konsument = "k"
            )
        }
    }

    @Test
    fun `beregner pensjonsbeholdning fra og med fødsel til og brukers 61e opptjeningsår (alder 63)`() {
        mockPerson()
        mockTomPensjonsbeholdningResponse()

        service.beregnAFPBeholdingsgrunnlag(
            fnr = "1",
            uttaksDato = LocalDate.of(2070, Month.JANUARY, 1),
            konsument = "k",
        )

        verify(personoppslag).hent("1")
        verify(pensjonsbeholdning).beregn(
            fnr = eq("12345678910"),
            fraOgMed = eq(2000),
            tilOgMed = eq(2061)
        )
    }

    @Test
    fun `simulerer pensjonsbeholdning fra og med fødsel til og brukers 61e opptjeningsår (alder 63)`() {
        mockPerson()
        mockTomPensjonsbeholdningResponse()

        val fremtidigInntekt = FremtidigeInntekter(
            listOf(
                FremtidigInntekt(YearMonth.of(2020, Month.JANUARY), 100_000)
            )
        )


        service.simulerAFPBeholdningsgrunnlag(
            fnr = "1",
            uttaksDato = LocalDate.of(2070, Month.JANUARY, 1),
            fremtidigeInntekter = fremtidigInntekt
        )

        verify(personoppslag).hent("1")
        verify(pensjonsbeholdning).simuler(
            fnr = eq("12345678910"),
            fraOgMed = eq(2000),
            tilOgMed = eq(2061),
            inntekter = eq(fremtidigInntekt.årligInntekt(2061))
        )
    }

    @Test
    fun `beregn inkluderer bare pensjonsbeholdninger gjeldende på fraOgMed dato eller senere`() {
        val a = AFPBeholdningsgrunnlag(
            fraOgMedDato = LocalDate.of(2040, Month.JANUARY, 1),
            tilOgMedDato = LocalDate.of(2049, Month.DECEMBER, 31),
            beholdning = 1000
        )
        val b = AFPBeholdningsgrunnlag(
            fraOgMedDato = LocalDate.of(2050, Month.JANUARY, 1),
            tilOgMedDato = LocalDate.of(2054, Month.DECEMBER, 31),
            beholdning = 2000
        )
        val c = AFPBeholdningsgrunnlag(
            fraOgMedDato = LocalDate.of(2055, Month.JANUARY, 1),
            tilOgMedDato = null,
            beholdning = 2000
        )

        mockPerson()
        whenever(pensjonsbeholdning.beregn(any(), any(), any())).thenReturn(listOf(a, b, c))

        val uttaksdato2052 = LocalDate.of(2052, Month.JANUARY, 1)
        val actual2052 = service.beregnAFPBeholdingsgrunnlag(
            fnr = "1",
            uttaksDato = uttaksdato2052,
            konsument = "k",
        )

        val expected2052 = listOf(b.copy(fraOgMedDato = uttaksdato2052), c)

        val uttaksdato2060 = LocalDate.of(2060, Month.JANUARY, 1)
        val actual2060 = service.beregnAFPBeholdingsgrunnlag(
            fnr = "1",
            uttaksDato = uttaksdato2060,
            konsument = "k",
        )

        val expected2060 = listOf(c.copy(fraOgMedDato = uttaksdato2060))

        assertThat(actual2052).isEqualTo(expected2052)
        assertThat(actual2060).isEqualTo(expected2060)
    }

    @Test
    fun `simuler inkluderer bare pensjonsbeholdninger gjeldende på fraOgMed dato eller senere`() {
        val a = AFPBeholdningsgrunnlag(
            fraOgMedDato = LocalDate.of(2040, Month.JANUARY, 1),
            tilOgMedDato = LocalDate.of(2049, Month.DECEMBER, 31),
            beholdning = 1000
        )
        val b = AFPBeholdningsgrunnlag(
            fraOgMedDato = LocalDate.of(2050, Month.JANUARY, 1),
            tilOgMedDato = LocalDate.of(2054, Month.DECEMBER, 31),
            beholdning = 2000
        )
        val c = AFPBeholdningsgrunnlag(
            fraOgMedDato = LocalDate.of(2055, Month.JANUARY, 1),
            tilOgMedDato = null,
            beholdning = 2000
        )

        mockPerson()
        whenever(pensjonsbeholdning.simuler(any(), any(), any(), any())).thenReturn(listOf(a, b, c))

        val uttaksdato2052 = LocalDate.of(2052, Month.JANUARY, 1)
        val actual2052 = service.simulerAFPBeholdningsgrunnlag(
            fnr = "1",
            uttaksDato = uttaksdato2052,
            fremtidigeInntekter = FremtidigeInntekter(emptyList())
        )

        val expected2052 = listOf(b.copy(fraOgMedDato = uttaksdato2052), c)

        val uttaksDato2060 = LocalDate.of(2060, Month.JANUARY, 1)
        val actual2060 = service.simulerAFPBeholdningsgrunnlag(
            fnr = "1",
            uttaksDato = uttaksDato2060,
            fremtidigeInntekter = FremtidigeInntekter(emptyList())
        )

        val expected2060 = listOf(c.copy(fraOgMedDato = uttaksDato2060))

        assertThat(actual2052).isEqualTo(expected2052)
        assertThat(actual2060).isEqualTo(expected2060)
    }

    private fun mockPerson() {
        whenever(personoppslag.hent(any())).thenReturn(
            Person(
                fødselsÅr = 2000,
                identhistorikk = IdentHistorikk(
                    identer = setOf(
                        Ident.FolkeregisterIdent.Gjeldende("12345678910")
                    )
                )
            )
        )
    }

    private fun mockTomPensjonsbeholdningResponse() {
        whenever(pensjonsbeholdning.beregn(any(), any(), any())).thenReturn(emptyList())
    }
}
