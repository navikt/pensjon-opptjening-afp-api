package no.nav.pensjon.opptjening.afp.api

import org.apache.catalina.connector.Request
import org.apache.catalina.connector.Response
import org.apache.catalina.valves.ValveBase
import org.slf4j.LoggerFactory.getLogger

class TestValve : ValveBase() {
    private val logger = getLogger(javaClass)

    override fun invoke(request: Request, response: Response) {
        logger.info("Setting attribute in custom valve")
        request.setAttribute("customkey", "this is my custom value")
        getNext().invoke(request, response)
    }
}