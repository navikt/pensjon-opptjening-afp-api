package no.nav.pensjon.opptjening.afp.api.api.model

data class UgyldigApiRequestException(val msg: String): RuntimeException(msg)