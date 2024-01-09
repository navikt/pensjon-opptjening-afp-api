package no.nav.pensjon.opptjening.afp.api

import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

fun main(args: Array<String>) {
    runApplication<Application>(*args).also { LoggerFactory.getLogger(Application::class.java).info("Application started..") }
}

@SpringBootApplication
class Application