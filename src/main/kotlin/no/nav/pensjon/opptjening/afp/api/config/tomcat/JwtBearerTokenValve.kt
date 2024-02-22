package no.nav.pensjon.opptjening.afp.api.config.tomcat

import com.nimbusds.jwt.JWTParser
import org.apache.catalina.connector.Request
import org.apache.catalina.connector.Response
import org.apache.catalina.valves.ValveBase
import org.slf4j.LoggerFactory.getLogger
import org.springframework.http.HttpHeaders

class JwtBearerTokenValve : ValveBase() {
    private val logger = getLogger(javaClass)

    override fun invoke(request: Request, response: Response) {
        request.getHeader(HttpHeaders.AUTHORIZATION)?.let {
            if (it.startsWith(prefix = "bearer", ignoreCase = true)) {
                try {
                    val claimsSet = JWTParser.parse(it.substring("bearer ".length)).jwtClaimsSet
                    request.setAttribute("token_audience", claimsSet.audience)
                    request.setAttribute("token_azp_name", claimsSet.getClaim("azp_name"))
                    request.setAttribute("token_scope", claimsSet.getClaim("scope"))
                    request.setAttribute("token_issuer", claimsSet.issuer)
                    request.setAttribute("token_subject", claimsSet.subject)
                } catch (e: RuntimeException) {
                    logger.info("Unable to parse token", e)
                }
            }
        }

        getNext().invoke(request, response)
    }
}