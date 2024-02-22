package no.nav.pensjon.opptjening.afp.api.api

import no.nav.pensjon.opptjening.afp.api.api.model.BeregnAFPBeholdningsgrunnlagRequest
import no.nav.pensjon.opptjening.afp.api.api.model.BeregnAFPBeholdningsgrunnlagResponse
import no.nav.pensjon.opptjening.afp.api.api.model.SimulerAFPBeholdningsgrunnlagRequest
import no.nav.pensjon.opptjening.afp.api.api.model.SimulerAFPBeholdningsgrunnlagResponse
import no.nav.pensjon.opptjening.afp.api.config.RequestAttributeConsumerResolver
import no.nav.pensjon.opptjening.afp.api.config.TokenScopeConfig.Companion.AZURE_CONFIG_ALIAS
import no.nav.pensjon.opptjening.afp.api.config.TokenScopeConfig.Companion.MASKINPORTEN_CONFIG_ALIAS
import no.nav.pensjon.opptjening.afp.api.config.TokenScopeConfig.Companion.SCOPE_BEREGN_READ
import no.nav.pensjon.opptjening.afp.api.config.TokenScopeConfig.Companion.SCOPE_BEREGN_READ_EKSTERN
import no.nav.pensjon.opptjening.afp.api.config.TokenScopeConfig.Companion.SCOPE_SIMULER_READ
import no.nav.pensjon.opptjening.afp.api.config.TokenScopeConfig.Companion.SCOPE_SIMULER_READ_EKSTERN
import no.nav.pensjon.opptjening.afp.api.service.AFPBeholdningsgrunnlagService
import no.nav.security.token.support.core.api.ProtectedWithClaims
import no.nav.security.token.support.core.api.RequiredIssuers
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.context.request.RequestContextHolder

@RestController
@RequestMapping("/api")
class WebApi(
    private val service: AFPBeholdningsgrunnlagService,
    private val consumerResolver: RequestAttributeConsumerResolver,
) {
    @PostMapping("/beregn")
    @RequiredIssuers(
        value = [
            ProtectedWithClaims(issuer = AZURE_CONFIG_ALIAS, claimMap = ["roles=$SCOPE_BEREGN_READ"]),
            ProtectedWithClaims(issuer = MASKINPORTEN_CONFIG_ALIAS, claimMap = ["scope=$SCOPE_BEREGN_READ_EKSTERN"]),
        ]
    )
    fun beregn(
        @RequestBody request: BeregnAFPBeholdningsgrunnlagRequest
    ): ResponseEntity<BeregnAFPBeholdningsgrunnlagResponse> {
        return ResponseEntity.ok(
            BeregnAFPBeholdningsgrunnlagResponse.of(
                service.beregnAFPBeholdingsgrunnlag(
                    fnr = request.personId,
                    uttaksDato = request.uttaksDato,
                    konsument = consumerResolver.resolve(RequestContextHolder.currentRequestAttributes())
                )
            )
        )
    }

    @PostMapping("/simuler")
    @RequiredIssuers(
        value = [
            ProtectedWithClaims(issuer = AZURE_CONFIG_ALIAS, claimMap = ["roles=$SCOPE_SIMULER_READ"]),
            ProtectedWithClaims(issuer = MASKINPORTEN_CONFIG_ALIAS, claimMap = ["scope=$SCOPE_SIMULER_READ_EKSTERN"]),
        ]
    )
    fun simuler(
        @RequestBody request: SimulerAFPBeholdningsgrunnlagRequest
    ): ResponseEntity<SimulerAFPBeholdningsgrunnlagResponse> {
        return ResponseEntity.ok(
            SimulerAFPBeholdningsgrunnlagResponse.of(
                service.simulerAFPBeholdningsgrunnlag(
                    fnr = request.personId,
                    uttaksDato = request.uttaksDato,
                    fremtidigeInntekter = request.fremtidigeInntekter,
                )
            )
        )
    }
}