package no.nav.pensjon.opptjening.afp.api.repo

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonMapperBuilder
import com.fasterxml.jackson.module.kotlin.readValue
import no.nav.pensjon.opptjening.afp.api.domain.AFPBeholdiningsgrunnlagBeregnetRepo
import no.nav.pensjon.opptjening.afp.api.domain.AFPBeholdningsgrunnlag
import no.nav.pensjon.opptjening.afp.api.domain.AFPBeholdningsgrunnlagBeregnet
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import java.sql.Timestamp
import java.time.LocalDate
import java.util.UUID

@Repository
class AFPBeholdiningsgrunnlagBeregnetRepoRepo(
    private val jdbc: NamedParameterJdbcOperations
) : AFPBeholdiningsgrunnlagBeregnetRepo {

    private val objectMapper = jacksonMapperBuilder().addModules(JavaTimeModule()).build()

    override fun lagre(grunnlag: AFPBeholdningsgrunnlagBeregnet) {

        jdbc.update(
            "insert into afp_beholdningsgrunnlag (id, tidspunkt, fnr, uttaksdato, konsument, grunnlag) values (:id, :tidspunkt, :fnr, :uttaksdato, :konsument, to_jsonb(:grunnlag::jsonb))",
            MapSqlParameterSource(
                mapOf(
                    "id" to grunnlag.id,
                    "tidspunkt" to Timestamp.from(grunnlag.tidspunkt),
                    "fnr" to grunnlag.fnr,
                    "uttaksdato" to grunnlag.uttaksdato,
                    "konsument" to grunnlag.konsument,
                    "grunnlag" to grunnlag.grunnlag.toDb(),
                )
            )
        )
    }

    override fun hent(fnr: String): List<AFPBeholdningsgrunnlagBeregnet> {
        return jdbc.query(
            "select * from afp_beholdningsgrunnlag where fnr = :fnr",
            mapOf("fnr" to fnr),
            AFPBeholdningsgrunnlagDokumentasjonRowMapper()
        )
    }

    data class AFPBeholdningsgrunnlagDb(
        val fraOgMedDato: LocalDate,
        val tilOgMedDato: LocalDate?,
        val beholdning: Int
    )

    private fun List<AFPBeholdningsgrunnlag>.toDb(): String {
        return objectMapper.writeValueAsString(map { it.toDb() })
    }

    private fun AFPBeholdningsgrunnlag.toDb(): AFPBeholdningsgrunnlagDb {
        return AFPBeholdningsgrunnlagDb(
            fraOgMedDato = fraOgMedDato,
            tilOgMedDato = tilOgMedDato,
            beholdning = beholdning
        )
    }

    internal fun String.deserialize(): List<AFPBeholdningsgrunnlag> {
        return objectMapper.readValue<(List<AFPBeholdningsgrunnlagDb>)>(this)
            .map {
                AFPBeholdningsgrunnlag(
                    fraOgMedDato = it.fraOgMedDato,
                    tilOgMedDato = it.tilOgMedDato,
                    beholdning = it.beholdning
                )
            }
    }

    inner class AFPBeholdningsgrunnlagDokumentasjonRowMapper : RowMapper<AFPBeholdningsgrunnlagBeregnet> {
        override fun mapRow(rs: ResultSet, rowNum: Int): AFPBeholdningsgrunnlagBeregnet {
            return AFPBeholdningsgrunnlagBeregnet(
                id = UUID.fromString(rs.getString("id")),
                tidspunkt = rs.getTimestamp("tidspunkt").toInstant(),
                fnr = rs.getString("fnr"),
                uttaksdato = rs.getTimestamp("uttaksdato").toLocalDateTime().toLocalDate(),
                konsument = rs.getString("konsument"),
                grunnlag = rs.getString("grunnlag").deserialize()
            )
        }
    }
}