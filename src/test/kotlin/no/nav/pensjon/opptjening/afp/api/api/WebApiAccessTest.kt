package no.nav.pensjon.opptjening.afp.api.api

import no.nav.pensjon.opptjening.afp.api.config.TokenScopeConfig
import no.nav.pensjon.opptjening.afp.api.config.TokenScopeConfig.Companion.SCOPE_BEREGN_READ_EKSTERN
import no.nav.pensjon.opptjening.afp.api.service.AFPBeholdningsgrunnlagService
import no.nav.security.token.support.spring.test.EnableMockOAuth2Server
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.HttpHeaders.CONTENT_TYPE
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
@EnableMockOAuth2Server
class WebApiAccessTest {

    @Autowired
    private lateinit var mvc: MockMvc

    @Autowired
    private lateinit var tokenIssuer: TestTokenIssuer

    @MockBean
    private lateinit var service: AFPBeholdningsgrunnlagService

    @Test
    fun `svarer med 401 for ukjent issuer`() {
        mvc.perform(
            MockMvcRequestBuilders.post("/api/beregn")
                .content("""{}""")
                .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .header(AUTHORIZATION, tokenIssuer.bearerToken("okta"))
        )
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `svarer med 401 for ugyldig audience`() {
        mvc.perform(
            MockMvcRequestBuilders.post("/api/beregn")
                .content("""{}""")
                .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .header(AUTHORIZATION, tokenIssuer.bearerToken(TokenScopeConfig.AZURE_CONFIG_ALIAS, "bogus"))
        )
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `svarer med 200 for issuer azure og gyldig token`() {
        mvc.perform(
            MockMvcRequestBuilders.post("/api/beregn")
                .content("""{"personId":"12345","uttaksDato":"2024-01-01"}""")
                .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .header(AUTHORIZATION, tokenIssuer.bearerToken(TokenScopeConfig.AZURE_CONFIG_ALIAS))
        )
            .andExpect(status().isOk)

        mvc.perform(
            MockMvcRequestBuilders.post("/api/simuler")
                .content("""{"personId":"12345","uttaksDato":"2024-01-01"}""")
                .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .header(AUTHORIZATION, tokenIssuer.bearerToken(TokenScopeConfig.AZURE_CONFIG_ALIAS))
        )
            .andExpect(status().isOk)
    }

    @Test
    fun `svarer med 200 for issuer maskinporten`() {
        mvc.perform(
            MockMvcRequestBuilders.post("/api/beregn")
                .content("""{"personId":"12345","uttaksDato":"2024-01-01"}""")
                .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .header(AUTHORIZATION, tokenIssuer.bearerToken(TokenScopeConfig.MASKINPORTEN_CONFIG_ALIAS))
        )
            .andExpect(status().isOk)

        mvc.perform(
            MockMvcRequestBuilders.post("/api/simuler")
                .content("""{"personId":"12345","uttaksDato":"2024-01-01"}""")
                .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .header(AUTHORIZATION, tokenIssuer.bearerToken(TokenScopeConfig.MASKINPORTEN_CONFIG_ALIAS))
        )
            .andExpect(status().isOk)
    }

    @Test
    fun `svarer med 403 dersom scope i token ikke matcher påkrevet verdi`() {
        mvc.perform(
            MockMvcRequestBuilders.post("/api/beregn")
                .content("""{"personId":"12345","uttaksDato":"2024-01-01"}""")
                .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .header(
                    AUTHORIZATION,
                    tokenIssuer.bearerToken(issuerId = TokenScopeConfig.MASKINPORTEN_CONFIG_ALIAS, scopes = listOf("baluba"))
                )
        )
            .andExpect(status().isForbidden)

        mvc.perform(
            MockMvcRequestBuilders.post("/api/simuler")
                .content("""{"personId":"12345","uttaksDato":"2024-01-01"}""")
                .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .header(
                    AUTHORIZATION,
                    tokenIssuer.bearerToken(issuerId = TokenScopeConfig.MASKINPORTEN_CONFIG_ALIAS, scopes = listOf("baluba"))
                )
        )
            .andExpect(status().isForbidden)
    }

    @Test
    fun `svarer med 403 dersom role i token ikke matcher påkrevet verdi`() {
        mvc.perform(
            MockMvcRequestBuilders.post("/api/beregn")
                .content("""{"personId":"12345","uttaksDato":"2024-01-01"}""")
                .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .header(
                    AUTHORIZATION,
                    tokenIssuer.bearerToken(issuerId = TokenScopeConfig.AZURE_CONFIG_ALIAS, roles = listOf("baluba"))
                )
        )
            .andExpect(status().isForbidden)

        mvc.perform(
            MockMvcRequestBuilders.post("/api/simuler")
                .content("""{"personId":"12345","uttaksDato":"2024-01-01"}""")
                .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .header(
                    AUTHORIZATION,
                    tokenIssuer.bearerToken(issuerId = TokenScopeConfig.AZURE_CONFIG_ALIAS, roles = listOf("baluba"))
                )
        )
            .andExpect(status().isForbidden)
    }

    @Test
    fun `svarer med 200 dersom token har flere scopes, og et av dem er gyldig for endepunktet`() {
        mvc.perform(
            MockMvcRequestBuilders.post("/api/beregn")
                .content("""{"personId":"12345","uttaksDato":"2024-01-01"}""")
                .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .header(
                    AUTHORIZATION,
                    tokenIssuer.bearerToken(
                        issuerId = TokenScopeConfig.MASKINPORTEN_CONFIG_ALIAS,
                        scopes = listOf(SCOPE_BEREGN_READ_EKSTERN, "bogus")
                    )
                )
        )
            .andExpect(status().isOk)
    }

    @Test
    fun `kan kalle acutator uten token`() {
        mvc.perform(
            MockMvcRequestBuilders.get("/actuator/health")
        )
            .andExpect(status().isOk)
    }
}