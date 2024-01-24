package no.nav.pensjon.opptjening.afp.api.domain

import no.nav.pensjon.opptjening.afp.api.domain.person.Person
import kotlin.math.min

object Aldersbegrensning61År {
    fun begrens(person: Person, år: Int): Int {
        return min(person.sekstiFørsteÅr, år)
    }
}