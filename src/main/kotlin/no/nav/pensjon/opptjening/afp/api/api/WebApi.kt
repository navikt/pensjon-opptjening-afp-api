package no.nav.pensjon.opptjening.afp.api.api

import no.nav.pensjon.opptjening.afp.api.api.model.BeregnAFPBeholdningsgrunnlagRequest
import no.nav.pensjon.opptjening.afp.api.api.model.BeregnAFPBeholdningsgrunnlagResponse
import no.nav.pensjon.opptjening.afp.api.api.model.SimulerAFPBeholdningsgrunnlagRequest
import no.nav.pensjon.opptjening.afp.api.api.model.SimulerAFPBeholdningsgrunnlagResponse
import no.nav.pensjon.opptjening.afp.api.service.AFPBeholdningsgrunnlagService
import no.nav.security.token.support.core.api.Protected
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
@Protected
class WebApi(
    private val service: AFPBeholdningsgrunnlagService
) {

    @PostMapping("/beregn")
    fun beregn(
        @RequestBody request: BeregnAFPBeholdningsgrunnlagRequest
    ): ResponseEntity<BeregnAFPBeholdningsgrunnlagResponse> {
        //TODO hvordan håndtere ugyldig input - f.eks inntekter etter 61 år?
        return ResponseEntity.ok(
            BeregnAFPBeholdningsgrunnlagResponse(
                personId = request.personId,
                afpGrunnlagBeholdninger = service.beregnAFPBeholdingsgrunnlag(
                    fnr = request.personId,
                    beholdningFraOgMed = request.fraOgMedDato
                )
            )
        )
    }

    @PostMapping("/simuler")
    fun simuler(
        @RequestBody request: SimulerAFPBeholdningsgrunnlagRequest
    ): ResponseEntity<SimulerAFPBeholdningsgrunnlagResponse> {
        //TODO hvordan håndtere ugyldig input - f.eks inntekter etter 61 år?
        return ResponseEntity.ok(
            SimulerAFPBeholdningsgrunnlagResponse(
                personId = request.personId,
                afpGrunnlagBeholdninger = service.simulerAFPBeholdningsgrunnlag(
                    fnr = request.personId,
                    beholdningFraOgMed = request.fraOgMedDato,
                    inntekter = request.inntekter,
                )
            )
        )
    }
}