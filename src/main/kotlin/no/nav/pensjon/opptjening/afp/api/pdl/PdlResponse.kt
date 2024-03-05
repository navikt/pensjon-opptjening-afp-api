package no.nav.pensjon.opptjening.afp.api.pdl

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import no.nav.pensjon.opptjening.afp.api.domain.person.Ident
import no.nav.pensjon.opptjening.afp.api.domain.person.IdentHistorikk
import no.nav.pensjon.opptjening.afp.api.domain.person.Person
import no.nav.pensjon.opptjening.afp.api.domain.person.PersonException
import java.time.LocalDateTime

@JsonIgnoreProperties(ignoreUnknown = true)
internal data class PdlResponse(
    val data: PdlData,
    private val errors: List<PdlError>? = null
) {
    val error: PdlError? = errors?.firstOrNull()
}

internal data class PdlData(
    val hentPerson: HentPersonQueryResponse?
)

@JsonIgnoreProperties(ignoreUnknown = true)
internal data class HentPersonQueryResponse(
    val folkeregisteridentifikator: List<Folkeregisteridentifikator>,
    val foedsel: List<Foedsel>,
) {
    private fun identhistorikk(): IdentHistorikk {
        return folkeregisteridentifikator.identhistorikk()
    }

    private fun fødselsår(): Int {
        return when (foedsel.size) {
            0 -> {
                throw PersonException.FantIkkeFødselsinformasjon("Fødselsår finnes ikke i respons fra pdl")
            }

            1 -> {
                foedsel.first().foedselsaar
            }

            else -> {
                foedsel.avklarFoedsel()?.foedselsaar ?: throw PersonException.FantIkkeFødselsinformasjon("Fødselsår finnes ikke i respons fra pdl")
            }
        }
    }

    private fun List<Folkeregisteridentifikator>.identhistorikk(): IdentHistorikk {
        return IdentHistorikk(
            map {
                when (it.status) {
                    Folkeregisteridentifikator.Status.I_BRUK -> {
                        Ident.FolkeregisterIdent.Gjeldende(it.identifikasjonsnummer)
                    }

                    Folkeregisteridentifikator.Status.OPPHOERT -> {
                        Ident.FolkeregisterIdent.Historisk(it.identifikasjonsnummer)
                    }
                }
            }.toSet()
        )
    }

    fun toDomain(): Person {
        return Person(
            fødselsÅr = fødselsår(),
            identhistorikk = identhistorikk(),
        )
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
internal data class Folkeregisteridentifikator(
    val identifikasjonsnummer: String,
    val status: Status,
    val type: Type,
    val metadata: Metadata,
    val folkeregistermetadata: Folkeregistermetadata? = null,
) {
    fun erGjeldende(): Boolean {
        return status == Status.I_BRUK
    }

    enum class Status { I_BRUK, OPPHOERT }
    enum class Type { FNR, DNR }
}

@JsonIgnoreProperties(ignoreUnknown = true)
internal data class Foedsel(
    val foedselsaar: Int,
    val metadata: Metadata,
    val folkeregistermetadata: Folkeregistermetadata? = null,
)

@JsonIgnoreProperties(ignoreUnknown = true)
internal data class Metadata(
    val historisk: Boolean,
    val master: String,
    val endringer: List<Endring> = emptyList()
)

@JsonIgnoreProperties(ignoreUnknown = true)
internal data class Folkeregistermetadata(
    val ajourholdstidspunkt: LocalDateTime? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
internal data class Endring(
    val registrert: LocalDateTime
)