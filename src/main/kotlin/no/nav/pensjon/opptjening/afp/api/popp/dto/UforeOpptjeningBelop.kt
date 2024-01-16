package no.nav.pensjon.opptjening.afp.api.popp.dto



data class UforeOpptjeningBelop(
    var uforeOpptjeningBelopId: Long? = null,
    var ar: Int? = null,
    var belop: Double? = null,
    var proRataBeregnetUp: Boolean? = null,
    var poengtall: Double? = null,
    var uforegrad: Int? = null,
    var antattInntekt: Double? = null,
    var antattInntektProRata: Double? = null,
    var andelProrata: Double? = null,
    var poengarTellerProRata: Int? = null,
    var poengarNevnerProRata: Int? = null,
    var antFremtidigArProRata: Int? = null,
    var poengAntattArligInntekt: Double? = null,
    var yrkesskadegrad: Int? = null,
    var antattInntektYrke: Double? = null,
    var uforear: Boolean? = null,
    var konvertertUFT: Boolean? = null,
    var veietGrunnbelop: Int? = null,
    var uforetrygd: Boolean? = null,
    var yrkesskade: Boolean? = null,
    var changeStamp: ChangeStampDto? = null,
) {

    
}
