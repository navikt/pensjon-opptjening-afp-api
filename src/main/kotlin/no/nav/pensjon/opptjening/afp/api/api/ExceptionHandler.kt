package no.nav.pensjon.opptjening.afp.api.api

import no.nav.pensjon.opptjening.afp.api.api.model.UgyldigApiRequestException
import no.nav.pensjon.opptjening.afp.api.domain.BeholdningException
import no.nav.pensjon.opptjening.afp.api.domain.person.PersonException
import no.nav.security.token.support.core.exceptions.JwtTokenInvalidClaimException
import no.nav.security.token.support.spring.validation.interceptor.JwtTokenUnauthorizedException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@ControllerAdvice
class ExceptionHandler : ResponseEntityExceptionHandler() {

    private val FNR_REGEX = "(\\d{6})\\d{5}".toRegex()
    private val log: Logger = LoggerFactory.getLogger(this::class.java)

    data class ExceptionBody(val message: String)

    @ExceptionHandler(value = [PersonException::class])
    fun handlePersonException(ex: PersonException, request: WebRequest): ResponseEntity<Any>? {
        logExeption(ex)
        return when (ex) {
            is PersonException.FantIkkeFødselsinformasjon -> {
                handleExceptionInternal(
                    ex,
                    ExceptionBody("Fant ikke tilstrekkelige personopplysninger for person"),
                    HttpHeaders().apply { contentType = MediaType.APPLICATION_JSON },
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    request
                )
            }

            is PersonException.PersonIkkeFunnet -> {
                handleExceptionInternal(
                    ex,
                    ExceptionBody("Fant ikke person"),
                    HttpHeaders().apply { contentType = MediaType.APPLICATION_JSON },
                    HttpStatus.NOT_FOUND,
                    request
                )
            }

            is PersonException.TekniskFeil -> {
                handleExceptionInternal(
                    ex,
                    ExceptionBody("Ukjent teknisk feil"),
                    HttpHeaders().apply { contentType = MediaType.APPLICATION_JSON },
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    request
                )
            }
        }
    }

    @ExceptionHandler(value = [BeholdningException::class])
    fun handlePersonException(ex: BeholdningException, request: WebRequest): ResponseEntity<Any>? {
        logExeption(ex)
        return when (ex) {
            is BeholdningException.PersonIkkeFunnet -> {
                handleExceptionInternal(
                    ex,
                    ExceptionBody("Fant ikke person"),
                    HttpHeaders().apply { contentType = MediaType.APPLICATION_JSON },
                    HttpStatus.NOT_FOUND,
                    request
                )
            }

            is BeholdningException.TekniskFeil -> {
                handleExceptionInternal(
                    ex,
                    ExceptionBody("Ukjent teknisk feil"),
                    HttpHeaders().apply { contentType = MediaType.APPLICATION_JSON },
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    request
                )
            }

            is BeholdningException.UgyldigInput -> {
                handleExceptionInternal(
                    ex,
                    ExceptionBody("Ugyldig input"),
                    HttpHeaders().apply { contentType = MediaType.APPLICATION_JSON },
                    HttpStatus.BAD_REQUEST,
                    request
                )
            }
        }
    }

    @ExceptionHandler(value = [UgyldigApiRequestException::class])
    fun handle(ex: UgyldigApiRequestException, request: WebRequest): ResponseEntity<Any>? {
        logExeption(ex)
        return handleExceptionInternal(
            ex,
            ExceptionBody(ex.message!!),
            HttpHeaders().apply { contentType = MediaType.APPLICATION_JSON },
            HttpStatus.BAD_REQUEST,
            request
        )
    }

    /**
     * Override slik at requester kan bygge inn sin egen validering i init-funksjonen.
     */
    override fun handleHttpMessageNotReadable(
        ex: HttpMessageNotReadableException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any>? {
        return if (ex.rootCause is UgyldigApiRequestException) {
            handle(ex.rootCause as UgyldigApiRequestException, request)
        } else {
            logExeption(ex)
            super.handleHttpMessageNotReadable(ex, headers, status, request)
        }
    }

    @ExceptionHandler(value = [JwtTokenUnauthorizedException::class])
    fun handle(ex: JwtTokenUnauthorizedException, request: WebRequest): ResponseEntity<Any>? {
        return if (ex.cause is JwtTokenInvalidClaimException) {
            logExeption(ex)
            handleExceptionInternal(
                ex,
                ExceptionBody("${ex.message}"),
                HttpHeaders().apply { contentType = MediaType.APPLICATION_JSON },
                HttpStatus.FORBIDDEN,
                request
            )
        } else {
            logAndHandleException(ex, request)
        }
    }


    @ExceptionHandler(value = [Exception::class])
    fun logAndHandleException(ex: Exception, request: WebRequest): ResponseEntity<Any>? {
        logExeption(ex)
        return handleException(ex, request)
    }

    private fun logExeption(ex: Exception) {
        log.warn(ex.message?.removeFnr(), ex)
    }

    private fun String.removeFnr(): String = FNR_REGEX.replace(this, "\$1*****")

}