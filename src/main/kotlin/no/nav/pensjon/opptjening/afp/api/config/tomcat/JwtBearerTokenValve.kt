package no.nav.pensjon.opptjening.afp.api.config.tomcat

import com.nimbusds.jwt.JWTParser
import net.minidev.json.JSONObject
import org.apache.catalina.connector.Request
import org.apache.catalina.connector.Response
import org.apache.catalina.valves.ValveBase
import org.slf4j.LoggerFactory.getLogger
import org.springframework.http.HttpHeaders

class JwtBearerTokenValve : ValveBase() {
    private val logger = getLogger(javaClass)

    override fun invoke(request: Request, response: Response) {
        request.getHeader(HttpHeaders.AUTHORIZATION)?.let { authHeader ->
            if (authHeader.startsWith(prefix = "bearer", ignoreCase = true)) {
                try {
                    val claimsSet = JWTParser.parse(authHeader.substring("bearer ".length)).jwtClaimsSet
                    request.setAttribute("token_audience", claimsSet.audience)
                    request.setAttribute("token_azp_name", claimsSet.getClaim("azp_name"))
                    (claimsSet.getClaim("consumer") as? JSONObject)
                        ?.also { request.setAttribute("token_consumer", it["ID"]) }
                    request.setAttribute("token_issuer", claimsSet.issuer)
                    request.setAttribute("token_roles", claimsSet.getClaim("roles"))
                    request.setAttribute("token_scope", claimsSet.getClaim("scope"))
                    request.setAttribute("token_subject", claimsSet.subject)
                } catch (e: RuntimeException) {
                    logger.info("Unable to parse token", e)
                }
            }
        }

        getNext().invoke(request, response)
    }
}