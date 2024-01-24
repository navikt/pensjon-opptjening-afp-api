package no.nav.pensjon.opptjening.afp.api.domain

/**
 * Representerer kalenderåret hvor arbeidet for opptjeningen ble utført.
 */
@JvmInline
value class OpptjeningsAr(val ar: Int) {

    /**
     * Fra Folketrygdloven §20-4 3. ledd.
     *
     * Pensjonsopptjeningen for et kalenderår oppreguleres med lønnsvekst og tilføres pensjonsbeholdningen
     * ved utløpet av året fastsettingen av formues- og inntekstskatt for det aktuelle året er ferdig.
     */
    fun beholdningAr(): BeholdningsAr {
        return BeholdningsAr(ar + 2)
    }
}