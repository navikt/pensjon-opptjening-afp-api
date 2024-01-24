package no.nav.pensjon.opptjening.afp.api.domain

import no.nav.pensjon.opptjening.afp.api.domain.person.Ident
import no.nav.pensjon.opptjening.afp.api.domain.person.IdentHistorikk
import no.nav.pensjon.opptjening.afp.api.domain.person.Person
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class Aldersbegrensning61ÅrTest {

    private val person = Person(
        fødselsÅr = 2000,
        identhistorikk = IdentHistorikk(
            setOf(
                Ident.FolkeregisterIdent.Gjeldende("1")
            )
        )
    )

    @Test
    fun `returnerer år dersom det er mindre enn brukers 61 år`() {
        assertThat(Aldersbegrensning61År.begrens(person, 2030)).isEqualTo(2030)
    }

    @Test
    fun `returnerer brukers 61 år dersom år er senere enn brukers 61 år`() {
        assertThat(Aldersbegrensning61År.begrens(person, 2100)).isEqualTo(2061)
    }

    @Test
    fun `returnerer brukers 61 år dersom år dersom år er lik bruker 61 år`() {
        assertThat(Aldersbegrensning61År.begrens(person, 2061)).isEqualTo(2061)
    }
}