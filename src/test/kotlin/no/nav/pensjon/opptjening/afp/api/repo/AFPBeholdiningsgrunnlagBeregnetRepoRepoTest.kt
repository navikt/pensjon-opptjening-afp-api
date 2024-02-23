package no.nav.pensjon.opptjening.afp.api.repo

import no.nav.pensjon.opptjening.afp.api.domain.AFPBeholdiningsgrunnlagBeregnetRepo
import no.nav.pensjon.opptjening.afp.api.domain.AFPBeholdningsgrunnlag
import no.nav.pensjon.opptjening.afp.api.domain.AFPBeholdningsgrunnlagBeregnet
import no.nav.security.token.support.spring.test.EnableMockOAuth2Server
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDate
import java.time.Month
import java.time.ZoneOffset
import java.util.UUID

@SpringBootTest
@AutoConfigureMockMvc
@EnableMockOAuth2Server
class AFPBeholdiningsgrunnlagBeregnetRepoRepoTest {

    @Autowired
    private lateinit var repo: AFPBeholdiningsgrunnlagBeregnetRepo

    @Test
    fun `lagre og hente`() {
        val expected = AFPBeholdningsgrunnlagBeregnet(
            id = UUID.randomUUID(),
            tidspunkt = LocalDate.of(2024, Month.FEBRUARY, 22).atStartOfDay(ZoneOffset.UTC).toInstant(),
            fnr = "12345678910",
            uttaksdato = LocalDate.of(2024, Month.FEBRUARY, 22),
            konsument = "TPO",
            grunnlag = listOf(
                AFPBeholdningsgrunnlag(
                    fraOgMedDato = LocalDate.of(2020, Month.JANUARY, 1),
                    tilOgMedDato = LocalDate.of(2023, Month.DECEMBER, 31),
                    beholdning = 1000
                ),
                AFPBeholdningsgrunnlag(
                    fraOgMedDato = LocalDate.of(2024, Month.JANUARY, 1),
                    tilOgMedDato = null,
                    beholdning = 2000
                )
            )

        )


        repo.lagre(expected)

        val actual = repo.hent("12345678910")
        assertThat(actual).isEqualTo(listOf(expected))
    }
}