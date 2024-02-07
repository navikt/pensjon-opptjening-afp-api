package no.nav.pensjon.opptjening.afp.api.domain.person

import no.nav.pensjon.opptjening.afp.api.domain.person.Person

interface Personoppslag {
    fun hent(fnr: String): Person?
}