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

        fun logRequest(request: HttpServletRequest) {
            log.info("Request: ${request.method}, url: ${request.requestURL}, host: ${request.remoteHost}")
        }

        fun logResponse(response: HttpServletResponse) {
            log.info("Response: ${response.status}")
        }
    }
}