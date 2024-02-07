package no.nav.pensjon.opptjening.afp.api.domain

interface Pensjonsbeholdning {
    fun beregn(
        fnr: String,
        fraOgMed: Int,
        tilOgMed: Int,
    ): List<AFPBeholdningsgrunnlag>

    fun simuler(
        fnr: String,
        fraOgMed: Int,
        tilOgMed: Int,
        inntekter: List<ÅrligInntekt>
    ): List<AFPBeholdningsgrunnlag>
}