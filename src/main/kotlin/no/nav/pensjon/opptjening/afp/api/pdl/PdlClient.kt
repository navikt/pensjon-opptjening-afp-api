package no.nav.pensjon.opptjening.afp.api.pdl

import no.nav.pensjon.opptjening.afp.api.domain.person.Person
import no.nav.pensjon.opptjening.afp.api.domain.person.PersonException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.core.io.Resource
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.RequestEntity
import org.springframework.stereotype.Component
import pensjon.opptjening.azure.ad.client.TokenProvider
import java.net.URI
import java.util.UUID

@Component
class PdlClient(
    @Value("\${PDL_URL}") private val pdlUrl: String,
    @Qualifier("pdlTokenProvider") private val tokenProvider: TokenProvider,
) {
    @Autowired
    private lateinit var graphqlQuery: GraphqlQuery
    private val restTemplate = RestTemplateBuilder().build()

    fun hentPerson(fnr: String): Person? {
        return try {
            val entity = RequestEntity<PdlQuery>(
                PdlQuery(graphqlQuery.hentPersonQuery(), FnrVariables(ident = fnr)),
                HttpHeaders().apply {
                    add("Nav-Call-Id", "${UUID.randomUUID()}")
                    add("Nav-Consumer-Id", "pensjon-opptjening-afp-api")
                    add("Tema", "PEN")
                    accept = listOf(MediaType.APPLICATION_JSON)
                    contentType = MediaType.APPLICATION_JSON
                    setBearerAuth(tokenProvider.getToken())
                },
                HttpMethod.POST,
                URI.create(pdlUrl)
            )

            val response = restTemplate.exchange(
                entity,
                PdlResponse::class.java
            ).body

            response?.error?.extensions?.code?.also {
                throw when (it) {
                    PdlErrorCode.NOT_FOUND -> PersonException.PersonIkkeFunnet(response.error.toString())
                    PdlErrorCode.SERVER_ERROR -> PersonException.TekniskFeil(response.error.toString())
                    PdlErrorCode.UNAUTHENTICATED -> PersonException.TekniskFeil(response.error.toString())
                    PdlErrorCode.UNAUTHORIZED -> PersonException.TekniskFeil(response.error.toString())
                    PdlErrorCode.BAD_REQUEST -> PersonException.TekniskFeil(response.error.toString())
                }
            }
            response?.data?.hentPerson?.toDomain()
        } catch (e: PersonException) {
            throw e
        } catch (e: Throwable) {
            throw PersonException.TekniskFeil("${e.message}")
        }
    }
}

@Component
internal class GraphqlQuery(
    @Value("classpath:pdl/folkeregisteridentifikator.graphql")
    private val hentPersonQuery: Resource,
) {
    fun hentPersonQuery(): String {
        return String(hentPersonQuery.inputStream.readBytes()).replace("[\n\r]", "")
    }
}

private data class PdlQuery(val query: String, val variables: FnrVariables)

private data class FnrVariables(val ident: String)
