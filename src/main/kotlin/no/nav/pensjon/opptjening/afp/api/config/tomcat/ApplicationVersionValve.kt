package no.nav.pensjon.opptjening.afp.api.config.tomcat

import org.apache.catalina.connector.Request
import org.apache.catalina.connector.Response
import org.apache.catalina.valves.ValveBase

class ApplicationVersionValve : ValveBase() {

    override fun invoke(request: Request, response: Response) {
        request.setAttribute("application_version", System.getenv("NAIS_APP_IMAGE") ?: "")

        getNext().invoke(request, response)
    }
}