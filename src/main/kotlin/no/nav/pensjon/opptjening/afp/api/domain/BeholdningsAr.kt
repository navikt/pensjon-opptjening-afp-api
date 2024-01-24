package no.nav.pensjon.opptjening.afp.api.domain

/**
 * Representerer kalenderåret en beholdning gjelder for.
 */
@JvmInline
value class BeholdningsAr(val ar: Int) {
    /**
     * Fra Folketrygdloven §20-4 3. ledd.
     *
     * Pensjonsopptjeningen for et kalenderår oppreguleres med lønnsvekst og tilføres pensjonsbeholdningen
     * ved utløpet av året fastsettingen av formues- og inntekstskatt for det aktuelle året er ferdig.
     */
    fun opptjeningsAr(): OpptjeningsAr {
        return OpptjeningsAr(ar - 2)
    }
}