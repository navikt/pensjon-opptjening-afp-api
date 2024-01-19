package no.nav.pensjon.opptjening.afp.api.popp

import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.github.tomakehurst.wiremock.junit5.WireMockExtension
import no.nav.pensjon.opptjening.afp.api.Application
import no.nav.pensjon.opptjening.afp.api.domain.BeholdningException
import no.nav.security.token.support.spring.test.EnableMockOAuth2Server
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.RegisterExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import java.time.LocalDate
import java.time.Month

@SpringBootTest
@EnableMockOAuth2Server
class PoppClientTest {

    @Autowired
    private lateinit var client: PoppClient

    companion object {
        @JvmField
        @RegisterExtension
        val wiremock = WireMockExtension.newInstance()
            .options(WireMockConfiguration.wireMockConfig().port(9991))
            .build()!!
    }

    @Test
    fun `kaller POPP med beregnUtenUttakAP = true`() {
        wiremock.givenThat(
            WireMock.post(WireMock.urlPathEqualTo("/api/beholdning/beregn"))
                .willReturn(
                    WireMock.ok()
                        .withBodyFile("beregnpensjonsbeholdning.json")
                        .withHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                )
        )

        client.beregnPensjonsbeholdning("tjafs", 2023, 2023)

        val expected = """{"fnr":"tjafs","fraOgMed":2023,"tilOgMed":2023,"beregnUtenUttakAP":true}"""

        assertThat(wiremock.serveEvents.requests.single().request.bodyAsString).isEqualTo(expected)
    }

    @Test
    fun `kan deserialsiere respons`() {
        wiremock.givenThat(
            WireMock.post(WireMock.urlPathEqualTo("/api/beholdning/beregn"))
                .willReturn(
                    WireMock.ok()
                        .withBodyFile("beregnpensjonsbeholdning.json")
                        .withHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                )
        )

        assertDoesNotThrow {
            val response = client.beregnPensjonsbeholdning("tjafs", 2023, 2023)
            assertThat(response).hasSize(19)
        }
    }

    @Test
    fun `mapper timestamp-date til riktig localdate for beholdning`() {
        wiremock.givenThat(
            WireMock.post(WireMock.urlPathEqualTo("/api/beholdning/beregn"))
                .willReturn(
                    WireMock.ok()
                        .withBodyFile("beregnpensjonsbeholdning.json")
                        .withHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                )
        )

        assertDoesNotThrow {
            val response = client.beregnPensjonsbeholdning("tjafs", 2023, 2023)
            response[0].let {
                assertThat(it.fraOgMedDato).isEqualTo(LocalDate.of(2015, Month.JANUARY, 1))
                assertThat(it.tilOgMedDato).isEqualTo(LocalDate.of(2015, Month.APRIL, 30))
            }
            response[1].let {
                assertThat(it.fraOgMedDato).isEqualTo(LocalDate.of(2015, Month.MAY, 1))
                assertThat(it.tilOgMedDato).isEqualTo(LocalDate.of(2015, Month.DECEMBER, 31))
            }
            response[18].let {
                assertThat(it.fraOgMedDato).isEqualTo(LocalDate.of(2024, Month.JANUARY, 1))
                assertThat(it.tilOgMedDato).isNull()
            }
        }
    }

    @Test
    fun `kaster person ikke funnet hvis POPP svarer med 404`(){
        wiremock.givenThat(
            WireMock.post(WireMock.urlPathEqualTo("/api/beholdning/beregn"))
                .willReturn(
                    WireMock.notFound()
                        .withBody("""
                            {
                                "message":"Fant ikke person"
                            }
                        """.trimIndent())
                        .withHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                )
        )

        assertThrows<BeholdningException.PersonIkkeFunnet> {
            client.beregnPensjonsbeholdning("tjafs", 2023, 2023)
        }
    }

    @Test
    fun `kaster ugyldig input hvis POPP svarer med 400`(){
        wiremock.givenThat(
            WireMock.post(WireMock.urlPathEqualTo("/api/beholdning/beregn"))
                .willReturn(
                    WireMock.badRequest()
                        .withBody("""
                            {
                                "message":"Ugyldig input"
                            }
                        """.trimIndent())
                        .withHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                )
        )

        assertThrows<BeholdningException.UgyldigInput> {
            client.beregnPensjonsbeholdning("tjafs", 2023, 2023)
        }
    }

    @Test
    fun `kaster teknisk feil hvis POPP svarer med 500`(){
        wiremock.givenThat(
            WireMock.post(WireMock.urlPathEqualTo("/api/beholdning/beregn"))
                .willReturn(
                    WireMock.serverError()
                        .withBody("""
                            {
                                "message":"Ugyldig input"
                            }
                        """.trimIndent())
                        .withHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                )
        )

        assertThrows<BeholdningException.TekniskFeil> {
            client.beregnPensjonsbeholdning("tjafs", 2023, 2023)
        }
    }
}