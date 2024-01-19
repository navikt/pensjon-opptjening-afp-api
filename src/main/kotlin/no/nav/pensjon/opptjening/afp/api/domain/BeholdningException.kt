package no.nav.pensjon.opptjening.afp.api.domain

sealed class BeholdningException(msg: String?) : RuntimeException(msg){
    data class PersonIkkeFunnet(val msg: String?): BeholdningException(msg)
    data class UgyldigInput(val msg: String?): BeholdningException(msg)
    data class TekniskFeil(val msg: String?): BeholdningException(msg)
}