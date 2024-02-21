package no.nav.pensjon.opptjening.afp.api.api.filter

import com.nimbusds.jwt.JWTParser
import jakarta.servlet.annotation.WebFilter
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import net.logstash.logback.marker.Markers
import net.minidev.json.JSONObject
import org.apache.commons.lang3.StringUtils.startsWith
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import java.time.Instant
import java.util.*


@Order(value = Ordered.HIGHEST_PRECEDENCE)
@Component
@WebFilter(filterName = "RequestLoggingFilter", urlPatterns = ["/api/*"])
class LoggingInterceptor : HandlerInterceptor {

    companion object {
        private val log: Logger = LoggerFactory.getLogger("RequestLoggingFilter")
    }

    override fun preHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any
    ): Boolean {
        val id = request.getHeader("Nav-Call-Id") ?: request.getHeader("x-request-id") ?: UUID.randomUUID().toString()

        request.setAttribute("id", id)
        request.setAttribute("startTime", Instant.now())

        return super.preHandle(request, response, handler)
    }

    override fun afterCompletion(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
        ex: Exception?
    ) {
        val timestamp = Instant.now()
        val authHeader = request.getHeader(HttpHeaders.AUTHORIZATION)
        val tokenData = if (authHeader.startsWith("bearer ", ignoreCase = true)) {
            try {
                JWTParser.parse(authHeader.substring("bearer ".length)).jwtClaimsSet
            } catch (e: RuntimeException) {
                log.info("Unable to parse bearer token", e)
                null
            }
        } else {
            null
        }

        val logData = mapOf(
            "id" to request.getAttribute("id"),
            "version" to (System.getenv("NAIS_APP_IMAGE") ?: ""),
            "method" to request.method,
            "protocol" to request.protocol,
            "statusCode" to response.status,
            "requestedUrl" to request.requestURL.toString(),
            "requestedUri" to request.requestURI,
            "remoteHost" to request.remoteHost,
            "contentLength" to request.contentLength.toString(),
            "elapsedTime" to (timestamp.toEpochMilli() - (request.getAttribute("startTime") as Instant).toEpochMilli()).toString() + "ms",
            "threadName" to Thread.currentThread().name,
            "token_audience" to tokenData?.audience,
            "token_azp_name" to tokenData?.getClaim("azp_name"),
            "token_scope" to tokenData?.getClaim("scope"),
            "token_issuer" to tokenData?.issuer,
            "token_subject" to tokenData?.subject,
        )

        val markers = Markers.appendEntries(logData)

        log.info(
            markers,
            "id: {}, {}, {}, {}, {}",
            logData["id"],
            logData["protocol"],
            logData["requestedUri"],
            logData["statusCode"],
            logData["elapsedTime"]
        )

        super.afterCompletion(request, response, handler, ex)
    }
}