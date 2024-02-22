package no.nav.pensjon.opptjening.afp.api.config

import com.nimbusds.jwt.JWT
import com.nimbusds.jwt.JWTParser
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.web.context.request.RequestAttributes
import org.springframework.web.context.request.ServletRequestAttributes

@Component
class RequestAttributeConsumerResolver(
    private val tokenResolver: JwtTokenIssuerSpecificConsumerResolver,
) {
    fun resolve(attributes: RequestAttributes): String {
        return (attributes as? ServletRequestAttributes)?.let {
            val authHeader = attributes.request.getHeader(HttpHeaders.AUTHORIZATION)
            val jwt = JWTParser.parse(authHeader.substring("bearer".length))
            tokenResolver.resolve(jwt)
        } ?: throw UnresolvableConsumerException("Could not resolve consumer due to unknown request attributes")
    }
}

@Component
class JwtTokenIssuerSpecificConsumerResolver(
    @Value("\${AZURE_OPENID_CONFIG_ISSUER}") var azureIssuer: String,
    @Value("\${MASKINPORTEN_ISSUER}") var maskinportenIssuer: String,
) {
    fun resolve(jwt: JWT): String {
        val issuer = jwt.jwtClaimsSet.issuer
        return when (issuer) {
            azureIssuer -> {
                jwt.jwtClaimsSet.getClaim("azp_name")?.toString()
                    ?: throw UnresolvableConsumerException("Could not find azp_name claim for azure issuer")
            }

            maskinportenIssuer -> {
                (jwt.jwtClaimsSet.getClaim("consumer") as? Map<String, String>)?.get("ID")
                    ?: throw UnresolvableConsumerException("Could not find consumer claim for issuer maskinporten")
            }

            else -> {
                throw UnresolvableConsumerException("Could not resolve consumer due to issuer being unknown")
            }
        }
    }
}

data class UnresolvableConsumerException(val msg: String) : RuntimeException(msg)