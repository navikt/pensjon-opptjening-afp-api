package no.nav.pensjon.opptjening.afp.api.pdl

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import no.nav.pensjon.opptjening.afp.api.domain.Person
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.core.io.Resource
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.RequestEntity
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import pensjon.opptjening.azure.ad.client.TokenProvider
import java.net.URI

@Component
class PdlClient(
    @Value("\${PDL_URL}") private val pdlUrl: String,
    @Qualifier("pdlTokenProvider") private val tokenProvider: TokenProvider,
) {
    @Autowired
    private lateinit var graphqlQuery: GraphqlQuery
    private val restTemplate = RestTemplateBuilder().build()

    fun hentPerson(fnr: String): Person? {
        try {
        val entity = RequestEntity<PdlQuery>(
            PdlQuery(graphqlQuery.hentPersonQuery(), FnrVariables(ident = fnr)),
            HttpHeaders().apply {
                add("Nav-Call-Id", "TODO")
                add("Nav-Consumer-Id", "pensjon-opptjening-afp-api")
                add("Tema", "PEN")
                accept = listOf(MediaType.APPLICATION_JSON)
                contentType = MediaType.APPLICATION_JSON
                setBearerAuth(tokenProvider.getToken())
            },
            HttpMethod.POST,
            URI.create(pdlUrl)
        )

        val rr = restTemplate.exchange(
            entity,
            String::class.java
        )

        LoggerFactory.getLogger(this::class.java).error(rr.toString())
        LoggerFactory.getLogger(this::class.java).error(rr.body.toString())

            val response = jacksonObjectMapper().readValue<PdlResponse?>(rr.body.toString())

            response?.error?.extensions?.code?.also {
            if (it == PdlErrorCode.SERVER_ERROR) throw RuntimeException(response.error.toString())
        }
        return response?.data?.hentPerson?.toDomain()

        } catch (e: Exception){
            LoggerFactory.getLogger(this::class.java).error("$e", e)
            throw e
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
