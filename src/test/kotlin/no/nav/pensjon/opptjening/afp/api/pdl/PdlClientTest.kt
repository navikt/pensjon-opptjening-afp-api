package no.nav.pensjon.opptjening.afp.api.pdl

import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.github.tomakehurst.wiremock.junit5.WireMockExtension
import no.nav.pensjon.opptjening.afp.api.Application
import no.nav.security.token.support.spring.test.EnableMockOAuth2Server
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
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
            val response = client.hentPerson("tjafs")!!
            assertThat(response.fnr).isEqualTo("12345678910")
            assertThat(response.fødselsÅr).isEqualTo(2000)
        }
    }
}