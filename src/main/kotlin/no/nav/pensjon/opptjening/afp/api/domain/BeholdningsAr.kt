package no.nav.pensjon.opptjening.afp.api.domain

/**
 * Representerer kalender�ret en beholdning gjelder for.
 */
@JvmInline
value class BeholdningsAr(val ar: Int) {
    /**
     * Fra Folketrygdloven �20-4 3. ledd.
     *
     * Pensjonsopptjeningen for et kalender�r oppreguleres med l�nnsvekst og tilf�res pensjonsbeholdningen
     * ved utl�pet av �ret fastsettingen av formues- og inntekstskatt for det aktuelle �ret er ferdig.
     */
    fun opptjeningsAr(): OpptjeningsAr {
        return OpptjeningsAr(ar - 2)
    }
}