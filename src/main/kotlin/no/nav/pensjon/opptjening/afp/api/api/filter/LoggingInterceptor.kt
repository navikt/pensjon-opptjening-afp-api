package no.nav.pensjon.opptjening.afp.api.api.filter

import jakarta.servlet.annotation.WebFilter
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import java.util.*


@Order(value = Ordered.HIGHEST_PRECEDENCE)
@Component
@WebFilter(filterName = "RequestLoggingFilter", urlPatterns = ["/api/*"])
class LoggingInterceptor : HandlerInterceptor {

    private val logger: RequestResponseLogger = RequestResponseLogger()
    override fun preHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any
    ): Boolean {
        logger.logRequest(request)
        return super.preHandle(request, response, handler)
    }

    override fun afterCompletion(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
        ex: Exception?
    ) {
        logger.logResponse(response)
        super.afterCompletion(request, response, handler, ex)
    }

    inner class RequestResponseLogger {
        private val log: Logger = LoggerFactory.getLogger(this::class.java)
        private val secureLog: Logger = LoggerFactory.getLogger("secure")

        fun logRequest(request: HttpServletRequest) {
            val corr = UUID.randomUUID().toString()
            log.info("Request: ${request.method}, url: ${request.requestURL}, host: ${request.remoteHost}")
            val headerNames = request.headerNames.toList().joinToString(",")
            secureLog.info("Correlation id: $corr (for incoming request logging only)")
            secureLog.info("Request($corr): ${request.method}, url: ${request.requestURL}, host: ${request.remoteHost}")
            secureLog.debug("Request($corr) header names: $headerNames")

        }

        fun logResponse(response: HttpServletResponse) {
            val corr = UUID.randomUUID().toString()
            val headerNames = response.headerNames.toList().joinToString(",")
            log.info("Response: ${response.status}")
            secureLog.info("Correlation id: $corr (for outgoing response only)")
            secureLog.info("Response ($corr) status: ${response.status} content type:${response.contentType}")
            secureLog.info("Response ($corr) headers: $headerNames")
        }
    }
}