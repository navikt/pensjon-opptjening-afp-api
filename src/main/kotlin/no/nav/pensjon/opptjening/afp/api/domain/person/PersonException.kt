package no.nav.pensjon.opptjening.afp.api.domain.person

sealed class PersonException(msg :String?) : RuntimeException(msg) {
    data class PersonIkkeFunnet(val msg: String) : PersonException(msg)
    data class TekniskFeil(val msg: String) : PersonException(msg)
    data class FantIkkeFødselsinformasjon(val msg :String): PersonException(msg)
}