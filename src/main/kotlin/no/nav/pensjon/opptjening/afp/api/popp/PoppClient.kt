package no.nav.pensjon.opptjening.afp.api.popp

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import no.nav.pensjon.opptjening.afp.api.domain.AFPBeholdningsgrunnlag
import no.nav.pensjon.opptjening.afp.api.domain.BeholdningException
import no.nav.pensjon.opptjening.afp.api.domain.Pensjonsbeholdning
import no.nav.pensjon.opptjening.afp.api.domain.ÅrligInntekt
import no.nav.pensjon.opptjening.afp.api.popp.dto.Beholdning
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.client.HttpClientErrorException
import pensjon.opptjening.azure.ad.client.TokenProvider
import java.net.URI
import java.time.LocalDate
import java.time.ZoneId
import no.nav.pensjon.opptjening.afp.api.popp.dto.Inntekt as PoppInntekt

@Component
internal class PoppClient(
    @Value("\${POPP_URL}") private val baseUrl: String,
    @Qualifier("poppTokenProvider") private val tokenProvider: TokenProvider,
    private val objectMapper: ObjectMapper,
): Pensjonsbeholdning {
    private val restTemplate = RestTemplateBuilder()
        .rootUri(baseUrl)
        .build()

    override fun beregn(
        fnr: String,
        fraOgMed: Int,
        tilOgMed: Int,
    ): List<AFPBeholdningsgrunnlag> {
        return try {
            val content = BeregnPensjonsbeholdningRequest(
                fnr = fnr,
                fraOgMed = fraOgMed,
                tilOgMed = tilOgMed,
            )
            val response = restTemplate.exchange(
                URI.create("$baseUrl/beholdning/beregn"),
                HttpMethod.POST,
                HttpEntity(
                    objectMapper.writeValueAsString(content),
                    HttpHeaders().apply {
                        add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        add(HttpHeaders.AUTHORIZATION, "Bearer ${tokenProvider.getToken()}")
                    }
                ),
                String::class.java
            )

            val mapped: List<Beholdning> = objectMapper.readValue(response.body!!)

            mapped.map { pensjonsbeholdning -> pensjonsbeholdning.toDomain() }
        } catch (e: HttpClientErrorException) {
            throw handleHttpClientErrors(e)
        } catch (e: Throwable) {
            throw BeholdningException.TekniskFeil(e.message)
        }
    }

    override fun simuler(
        fnr: String,
        fraOgMed: Int,
        tilOgMed: Int,
        inntekter: List<ÅrligInntekt>
    ): List<AFPBeholdningsgrunnlag> {
        return try {
            val content = SimulerPensjonsbeholdningRequest(
                fnr = fnr,
                fraOgMed = fraOgMed,
                tilOgMed = tilOgMed,
                inntekter = inntekter.toDto(fnr),
            )
            val response = restTemplate.exchange(
                URI.create("$baseUrl/beholdning/simuler"),
                HttpMethod.POST,
                HttpEntity(
                    objectMapper.writeValueAsString(content),
                    HttpHeaders().apply {
                        add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        add(HttpHeaders.AUTHORIZATION, "Bearer ${tokenProvider.getToken()}")
                    }
                ),
                String::class.java
            )

            val mapped: List<Beholdning> = objectMapper.readValue(response.body!!)

            mapped.map { pensjonsbeholdning -> pensjonsbeholdning.toDomain() }
        } catch (e: HttpClientErrorException) {
            throw handleHttpClientErrors(e)
        } catch (e: Throwable) {
            throw BeholdningException.TekniskFeil(e.message)
        }
    }

    private fun handleHttpClientErrors(e: HttpClientErrorException): BeholdningException {
        return when (e.statusCode) {
            HttpStatus.NOT_FOUND -> {
                BeholdningException.PersonIkkeFunnet(e.message)
            }

            HttpStatus.BAD_REQUEST -> {
                BeholdningException.UgyldigInput(e.message)
            }

            else -> {
                BeholdningException.TekniskFeil(e.message)
            }
        }
    }
}

private data class BeregnPensjonsbeholdningRequest(
    val fnr: String,
    val fraOgMed: Int,
    val tilOgMed: Int,
) {
    val beregnUtenUttakAP = true
}

private data class SimulerPensjonsbeholdningRequest(
    val fnr: String,
    val fraOgMed: Int,
    val tilOgMed: Int,
    val inntekter: List<PoppInntekt>,
)

private fun List<ÅrligInntekt>.toDto(fnr: String): List<PoppInntekt> {
    return map { it.toDto(fnr) }
}

private fun ÅrligInntekt.toDto(fnr: String): PoppInntekt {
    return PoppInntekt(
        changeStamp = null,
        inntektId = null,
        fnr = fnr,
        inntektAr = år,
        kilde = "NAV",
        kommune = null,
        piMerke = null,
        inntektType = "SUM_PI",
        belop = inntekt.toLong()
    )
}


private fun Beholdning.toDomain(): AFPBeholdningsgrunnlag {
    val tz = ZoneId.of("Europe/Oslo")
    return AFPBeholdningsgrunnlag(
        fraOgMedDato = LocalDate.ofInstant(fomDato!!.toInstant(), tz),
        tilOgMedDato = tomDato?.let { LocalDate.ofInstant(it.toInstant(), tz) },
        beholdning = belop?.toInt() ?: 0
    )
}