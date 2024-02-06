package no.nav.pensjon.opptjening.afp.api.domain

import java.math.BigDecimal
import java.math.BigInteger
import java.math.MathContext
import java.time.LocalDate
import java.time.Month
import java.time.YearMonth
import java.util.LinkedList
import java.util.Queue

data class FremtidigeInntekter(
    private val inntekter: List<FremtidigInntekt>
) {
    private val sortedByFom = inntekter.sortedBy { it.måned }

    init {
        require(inntekter.groupBy { it.måned }.values.none { it.size > 1 }) { "Fremtidige inntekter kan ikke ha flere verdier for samme måned" }
    }

    fun årligInntekt(
        tilOgMed: Int = LocalDate.now().year
    ): List<ÅrligInntekt> {
        val tilOgMedMåned = LocalDate.now().let { YearMonth.of(tilOgMed, Month.DECEMBER) }
        val månedlig = transformerTilInntektPerMåned(
            input = LinkedList(sortedByFom.filter { it.måned <= tilOgMedMåned }),
            result = emptyMap(),
            tilOgMed = tilOgMedMåned
        )
        return månedlig.entries.groupBy { it.key.year }.entries.map { (år, beløpPerMåned) ->
            ÅrligInntekt(
                år = år,
                inntekt = beløpPerMåned.årsinntektJustertForAntallMånederMedInntekt().sum()
            )
        }
    }


    private fun List<Map.Entry<YearMonth, BigDecimal>>.årsinntektJustertForAntallMånederMedInntekt(): List<Int> {
        return groupBy { it.value }.map {
            it.key.times(BigDecimal(it.value.count())).toInt()
        }
    }


    private fun transformerTilInntektPerMåned(
        input: Queue<FremtidigInntekt>,
        result: Map<YearMonth, BigDecimal>,
        tilOgMed: YearMonth,
    ): Map<YearMonth, BigDecimal> {
        if (input.isEmpty()) {
            return result
        }
        val map = mutableMapOf<YearMonth, BigDecimal>()

        while (input.isNotEmpty()) {
            val current = input.poll()
            val slutt = if (input.isNotEmpty()) {
                input.peek().måned
            } else {
                tilOgMed
            }

            val månedligInntekt =
                BigDecimal(BigInteger.valueOf(current.årligInntekt.toLong()), MathContext.DECIMAL32)
                    .divide(BigDecimal.valueOf(12), MathContext.DECIMAL32)

            (current.måned..slutt).forEach {
                map[it] = månedligInntekt
            }
        }
        return transformerTilInntektPerMåned(input, result + map, tilOgMed)
    }
}