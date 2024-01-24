package no.nav.pensjon.opptjening.afp.api.domain

import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.Month
import kotlin.test.assertEquals

class VelgAFPBeholdningsgrunnlagTest {
    @Test
    fun `tar kun med siste grunnlag dersom fra og med er samme dato som siste grunnlag`() {
        val actual = VelgAFPBeholdningsgrunnlag(
            fraOgMed = LocalDate.of(2020, Month.JANUARY, 1),
            beholdninger = lagAFPBeholdningsgrunnlag(2000..2020)
        ).get()

        val expected = listOf(
            AFPBeholdningsgrunnlag(
                fraOgMedDato = LocalDate.of(2020, Month.JANUARY, 1),
                tilOgMedDato = null,
                beholdning = 202000
            ),
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `tar med grunnlag med overlappende tidsintervall og senere til og med dato`() {
        val actual = VelgAFPBeholdningsgrunnlag(
            fraOgMed = LocalDate.of(2019, Month.MARCH, 12),
            beholdninger = lagAFPBeholdningsgrunnlag(2000..2020)
        ).get()

        val expected = listOf(
            AFPBeholdningsgrunnlag(
                fraOgMedDato = LocalDate.of(2019, Month.JANUARY, 1),
                tilOgMedDato = LocalDate.of(2019, Month.APRIL, 30),
                beholdning = 201900
            ),
            AFPBeholdningsgrunnlag(
                fraOgMedDato = LocalDate.of(2019, Month.MAY, 1),
                tilOgMedDato = LocalDate.of(2019, Month.DECEMBER, 31),
                beholdning = 201900
            ),
            AFPBeholdningsgrunnlag(
                fraOgMedDato = LocalDate.of(2020, Month.JANUARY, 1),
                tilOgMedDato = null,
                beholdning = 202000
            ),
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `tar kun med grunnlag med åpen til og med dato dersom ingen grunnlag har senere til og med dato`() {
        val actual = VelgAFPBeholdningsgrunnlag(
            fraOgMed = LocalDate.of(2050, Month.MAY, 19),
            beholdninger = lagAFPBeholdningsgrunnlag(2000..2020)
        ).get()

        val expected = listOf(
            AFPBeholdningsgrunnlag(
                fraOgMedDato = LocalDate.of(2020, Month.JANUARY, 1),
                tilOgMedDato = null,
                beholdning = 202000
            ),
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `tar med alle grunnlag dersom fra og med er tidligere enn tidligste til og med dato`() {
        val grunnlag = lagAFPBeholdningsgrunnlag(2000..2020)
        val actual = VelgAFPBeholdningsgrunnlag(
            fraOgMed = LocalDate.of(1990, Month.MAY, 19),
            beholdninger = grunnlag
        ).get()
        val expected = grunnlag
        assertEquals(expected, actual)
    }

    private fun lagAFPBeholdningsgrunnlag(intRange: IntRange): List<AFPBeholdningsgrunnlag> {
        return intRange.fold(emptyList()) { acc, i ->
            when (i == intRange.last) {
                true -> {
                    acc.plus(
                        listOf(
                            AFPBeholdningsgrunnlag(
                                fraOgMedDato = LocalDate.of(i, Month.JANUARY, 1),
                                tilOgMedDato = null,
                                beholdning = i * 100
                            ),
                        )
                    )
                }

                false -> {
                    acc.plus(
                        listOf(
                            AFPBeholdningsgrunnlag(
                                fraOgMedDato = LocalDate.of(i, Month.JANUARY, 1),
                                tilOgMedDato = LocalDate.of(i, Month.APRIL, 30),
                                beholdning = i * 100
                            ),
                            AFPBeholdningsgrunnlag(
                                fraOgMedDato = LocalDate.of(i, Month.MAY, 1),
                                tilOgMedDato = LocalDate.of(i, Month.DECEMBER, 31),
                                beholdning = i * 100
                            )
                        )
                    )
                }
            }
        }
    }
}