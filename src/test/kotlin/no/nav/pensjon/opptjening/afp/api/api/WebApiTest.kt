package no.nav.pensjon.opptjening.afp.api.api

import no.nav.pensjon.opptjening.afp.api.config.TokenScopeConfig
import no.nav.pensjon.opptjening.afp.api.domain.AFPBeholdningsgrunnlag
import no.nav.pensjon.opptjening.afp.api.domain.BeholdningException
import no.nav.pensjon.opptjening.afp.api.domain.person.PersonException
import no.nav.pensjon.opptjening.afp.api.service.AFPBeholdningsgrunnlagService
import no.nav.security.token.support.spring.test.EnableMockOAuth2Server
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.kotlin.any
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.HttpHeaders.CONTENT_TYPE
import org.springframework.http.MediaType.*
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDate
import java.time.Month

@SpringBootTest
@AutoConfigureMockMvc
@EnableMockOAuth2Server
class WebApiTest {

    @Autowired
    private lateinit var mvc: MockMvc

    @Autowired
    private lateinit var tokenIssuer: TestTokenIssuer

    @MockBean
    private lateinit var service: AFPBeholdningsgrunnlagService

    @Test
    fun `svarer med 404 for dersom person ikke eksister i PDL`() {
        given(
            service.beregnAFPBeholdingsgrunnlag(
                any(),
                any()
            )
        ).willThrow(PersonException.PersonIkkeFunnet("fant ikke"))

        mvc.perform(
            post("/api/beregn")
                .content(
                    """
                    {
                        "personId":"1234",
                        "fraOgMedDato":"2023-01-01"
                    }
                """.trimIndent()
                )
                .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .header(AUTHORIZATION, tokenIssuer.bearerToken(TokenScopeConfig.ISSUER_AZURE))
        )
            .andExpect(status().isNotFound)
            .andExpect(content().json("""{"message":"Fant ikke person"}"""))
    }

    @Test
    fun `svarer med 400 for dersom input til POPP er ugyldig`() {
        given(
            service.beregnAFPBeholdingsgrunnlag(
                any(),
                any()
            )
        ).willThrow(BeholdningException.UgyldigInput("ugyldig input"))

        mvc.perform(
            post("/api/beregn")
                .content(
                    """
                    {
                        "personId":"1234",
                        "fraOgMedDato":"2023-01-01"
                    }
                """.trimIndent()
                )
                .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .header(AUTHORIZATION, tokenIssuer.bearerToken(TokenScopeConfig.ISSUER_AZURE))
        )
            .andExpect(status().isBadRequest)
            .andExpect(content().json("""{"message":"Ugyldig input"}"""))
    }

    @Test
    fun `svarer med 400 for dersom input til API er ugyldig`() {
        mvc.perform(
            post("/api/simuler")
                .content(
                    """
                    {
                        "personId":"1234",
                        "fraOgMedDato":"2023-01-01",
                        "fremtidigInntektListe": [
                            {
                                "arligInntekt": 1000,
                                "fraOgMedDato": "2023-01-01"
                            },
                            {
                                "arligInntekt": 2000,
                                "fraOgMedDato": "2023-01-01"
                            }
                        ]
                    }
                """.trimIndent()
                )
                .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .header(AUTHORIZATION, tokenIssuer.bearerToken(TokenScopeConfig.ISSUER_AZURE))
        )
            .andExpect(status().isBadRequest)
            .andExpect(content().json("""{"message":"fremtidigInntektListe har flere verdier for fraOgMedDato: 2023-01-01"}"""))
    }

    @Test
    fun `svarer med 200 ok hvis alt går bra`() {
        val fom1 = LocalDate.of(2023, Month.JANUARY, 1)
        val tom1 = LocalDate.of(2023, Month.APRIL, 30)
        val fom2 = LocalDate.of(2023, Month.MAY, 1)
        val tom2 = LocalDate.of(2023, Month.DECEMBER, 31)

        given(service.beregnAFPBeholdingsgrunnlag(any(), any()))
            .willReturn(
                listOf(
                    AFPBeholdningsgrunnlag(
                        fraOgMedDato = fom1,
                        tilOgMedDato = tom1,
                        beholdning = 5000
                    ),
                    AFPBeholdningsgrunnlag(
                        fraOgMedDato = fom2,
                        tilOgMedDato = tom2,
                        beholdning = 5500
                    )
                )
            )

        val request = """
                    {
                        "personId":"1234",
                        "fraOgMedDato":"2023-01-01"
                    }
                """.trimIndent()

        val expected = """
                {  
                    "pensjonsBeholdningsPeriodeListe": [
                     {
                        "fraOgMedDato":"2023-01-01",
                        "pensjonsBeholdning":5000
                     },
                     {
                        "fraOgMedDato":"2023-05-01",
                        "pensjonsBeholdning":5500
                     }
                    ]                    
                }
            """.trimIndent()

        mvc.perform(
            post("/api/beregn")
                .content(request)
                .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .header(AUTHORIZATION, tokenIssuer.bearerToken(TokenScopeConfig.ISSUER_AZURE))
        )
            .andExpect(status().isOk)
            .andExpect(content().json(expected))
    }
}