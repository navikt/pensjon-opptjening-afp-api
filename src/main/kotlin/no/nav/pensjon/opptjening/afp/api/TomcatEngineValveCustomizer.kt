package no.nav.pensjon.opptjening.afp.api

import ch.qos.logback.access.tomcat.LogbackValve
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory
import org.springframework.boot.web.server.WebServerFactoryCustomizer
import org.springframework.stereotype.Component

@Component
class TomcatEngineValveCustomizer : WebServerFactoryCustomizer<TomcatServletWebServerFactory> {
    override fun customize(factory: TomcatServletWebServerFactory) {
        factory.addEngineValves(
            TestValve(),
            LogbackValve().apply {
                name = "Logback Access"
                filename = "logback-access.xml"
            }
        )
    }
}