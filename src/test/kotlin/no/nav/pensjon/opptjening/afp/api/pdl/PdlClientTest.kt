package no.nav.pensjon.opptjening.afp.api.pdl

import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.github.tomakehurst.wiremock.junit5.WireMockExtension
import no.nav.pensjon.opptjening.afp.api.Application
import no.nav.pensjon.opptjening.afp.api.domain.person.PersonException
import no.nav.security.token.support.spring.test.EnableMockOAuth2Server
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.RegisterExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders

@SpringBootTest
@EnableMockOAuth2Server
class PdlClientTest {

    @Autowired
    private lateinit var client: PdlClient

    companion object {
        @JvmField
        @RegisterExtension
        val wiremock = WireMockExtension.newInstance()
            .options(WireMockConfiguration.wireMockConfig().port(9991))
            .build()!!
    }

    @Test
    fun `kan deserialsiere respons`() {
        wiremock.givenThat(
            WireMock.post(WireMock.urlPathEqualTo("/graphql"))
                .willReturn(
                    WireMock.ok()
                        .withBodyFile("simple_pdl_response.json")
                        .withHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                )
        )

        assertDoesNotThrow {
            val response = client.hent("tjafs")!!
            assertThat(response.fnr).isEqualTo("12345678910")
            assertThat(response.fødselsÅr).isEqualTo(2000)
        }
    }

    @Test
    fun `kaster person ikke funnet exception hvis person ikke fins`() {
        wiremock.givenThat(
            WireMock.post(WireMock.urlPathEqualTo("/graphql"))
                .willReturn(
                    WireMock.ok()
                        .withBody(
                            """
                            {
                                "data": {
                                    "hentPerson":null
                                },
                                "errors": [
                                    {
                                        "message":"Fant ikke person",
                                        "extensions": {
                                            "code":"not_found"
                                        }
                                    }
                                ]
                            }
                        """.trimIndent()
                        )
                        .withHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                )
        )

        assertThrows<PersonException.PersonIkkeFunnet> {
            client.hent("tjafs")
        }
    }

    @Test
    fun `kaster teknisk feil dersom ukjent feil returneres fra PDL`() {
        wiremock.givenThat(
            WireMock.post(WireMock.urlPathEqualTo("/graphql"))
                .willReturn(
                    WireMock.ok()
                        .withBody(
                            """
                            {
                                "data": {
                                    "hentPerson":null
                                },
                                "errors": [
                                    {
                                        "message":"Some technical error",
                                        "extensions": {
                                            "code":"server_error"
                                        }
                                    }
                                ]
                            }
                        """.trimIndent()
                        )
                        .withHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                )
        )

        assertThrows<PersonException.TekniskFeil> {
            client.hent("tjafs")
        }
    }

    @Test
    fun `kaster fant ikke fødsel feil dersom fødselsinformasjon ikke eksisterer i PDL`() {
        wiremock.givenThat(
            WireMock.post(WireMock.urlPathEqualTo("/graphql"))
                .willReturn(
                    WireMock.ok()
                        .withBody(
                            """
                            {
                                "data": {
                                    "hentPerson": {
                                        "folkeregisteridentifikator": [],
                                        "foedsel": []
                                    }
                                }
                            }
                        """.trimIndent()
                        )
                        .withHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                )
        )

        assertThrows<PersonException.FantIkkeFødselsinformasjon> {
            client.hent("tjafs")
        }
    }
}
