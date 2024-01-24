package no.nav.pensjon.opptjening.afp.api.domain

/**
 * Representerer kalender�ret hvor arbeidet for opptjeningen ble utf�rt.
 */
@JvmInline
value class OpptjeningsAr(val ar: Int) {

    /**
     * Fra Folketrygdloven �20-4 3. ledd.
     *
     * Pensjonsopptjeningen for et kalender�r oppreguleres med l�nnsvekst og tilf�res pensjonsbeholdningen
     * ved utl�pet av �ret fastsettingen av formues- og inntekstskatt for det aktuelle �ret er ferdig.
     */
    fun beholdningAr(): BeholdningsAr {
        return BeholdningsAr(ar + 2)
    }
}