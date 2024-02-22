package no.nav.pensjon.opptjening.afp.api.domain

interface AFPBeholdiningsgrunnlagBeregnetRepo {
    fun lagre(grunnlag: AFPBeholdningsgrunnlagBeregnet)
    fun hent(fnr: String): List<AFPBeholdningsgrunnlagBeregnet>
}