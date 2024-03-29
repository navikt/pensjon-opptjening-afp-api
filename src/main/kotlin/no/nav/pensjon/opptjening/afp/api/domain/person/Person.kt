package no.nav.pensjon.opptjening.afp.api.domain.person

class Person(
    val fødselsÅr: Int,
    val identhistorikk: IdentHistorikk,
) {
    val fnr = identhistorikk.gjeldende().ident
    val sekstiFørsteÅr = fødselsÅr.plus(61)
}