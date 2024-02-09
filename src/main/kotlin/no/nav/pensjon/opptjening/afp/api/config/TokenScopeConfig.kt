package no.nav.pensjon.opptjening.afp.api.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration


/**
 * Validates that environment variables for scopes are equal to the static values configured for usage with annotations
 * on web-endpoints.
 */
@Configuration
data class TokenScopeConfig(
    @Value("\${SCOPE_BEREGN_READ}") var beregnScope: String,
    @Value("\${SCOPE_BEREGN_READ_EKSTERN}") var beregnScopeEkstern: String,
    @Value("\${SCOPE_SIMULER_READ}") var simulerScope: String,
    @Value("\${SCOPE_SIMULER_READ_EKSTERN}") var simulerScopeEkstern: String,
) {
    init {
        require(beregnScope == SCOPE_BEREGN_READ) { "Feil i konfigurasjon" }
        require(beregnScopeEkstern == SCOPE_BEREGN_READ_EKSTERN) { "Feil i konfigurasjon" }
        require(simulerScope == SCOPE_SIMULER_READ) { "Feil i konfigurasjon" }
        require(simulerScopeEkstern == SCOPE_SIMULER_READ_EKSTERN) { "Feil i konfigurasjon" }
    }

    companion object {
        const val ISSUER_AZURE = "azure"
        const val ISSUER_MASKINPORTEN = "maskinporten"
        const val SCOPE_BEREGN_READ = "afp.beholdningsgrunnlag.beregn.read"
        const val SCOPE_BEREGN_READ_EKSTERN = "ekstern.$SCOPE_BEREGN_READ"
        const val SCOPE_SIMULER_READ = "afp.beholdningsgrunnlag.simuler.read"
        const val SCOPE_SIMULER_READ_EKSTERN = "ekstern.$SCOPE_SIMULER_READ"
    }
}