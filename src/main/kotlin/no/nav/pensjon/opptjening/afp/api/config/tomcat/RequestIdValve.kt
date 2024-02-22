package no.nav.pensjon.opptjening.afp.api.config.tomcat

import org.apache.catalina.connector.Request
import org.apache.catalina.connector.Response
import org.apache.catalina.valves.ValveBase
import java.util.UUID

class RequestIdValve : ValveBase() {

    override fun invoke(request: Request, response: Response) {
        val header = request.getHeader("x-request-id")

        if (!header.isNullOrEmpty()) {
            request.setAttribute("request_id", header)
            request.setAttribute("request_id_type", "header")
        } else {
            request.setAttribute("request_id", UUID.randomUUID().toString())
            request.setAttribute("request_id_type", "generated")
        }

        getNext().invoke(request, response)
    }
}