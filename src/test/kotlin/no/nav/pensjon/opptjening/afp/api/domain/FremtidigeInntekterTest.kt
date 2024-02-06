package no.nav.pensjon.opptjening.afp.api.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.Month
import java.time.YearMonth

class FremtidigeInntekterTest {

    @Test
    fun `ingen fremtidige inntekter gir ingen årlige inntekter`() {
        val actual = FremtidigeInntekter(emptyList()).årligInntekt()
        val expected = emptyList<ÅrligInntekt>()
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `en fremtidig inntekt blir ingen årlige inntekter dersom tilOgMed er tidligere enn fremtidig inntekts fraOgMed`() {
        val actual = FremtidigeInntekter(
            listOf(
                FremtidigInntekt(måned = YearMonth.of(2022, Month.JANUARY), årligInntekt = 12000)
            )
        ).årligInntekt(tilOgMed = 2015)
        val expected = emptyList<ÅrligInntekt>()
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `en fremtidig inntekt blir en årlig inntekt dersom tilOgMed er lik fremtidig inntekts fraOgMed`() {
        val actual = FremtidigeInntekter(
            listOf(
                FremtidigInntekt(måned = YearMonth.of(2022, Month.JANUARY), årligInntekt = 12000)
            )
        ).årligInntekt(tilOgMed = 2022)

        val expected = listOf(
            ÅrligInntekt(år = 2022, inntekt = 12000)
        )
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `en fremtidig inntekt blir justert for antall måneder i året dersom den har fraOgMed senere enn 1 januar`() {
        val actual = FremtidigeInntekter(
            listOf(
                FremtidigInntekt(måned = YearMonth.of(2022, Month.JULY), årligInntekt = 12000)
            )
        ).årligInntekt(tilOgMed = 2022)

        val expected = listOf(
            ÅrligInntekt(år = 2022, inntekt = 6000)
        )
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `en fremtidig inntekt blir flere årlige inntekter dersom tilOgMed er større enn fremtidig inntekts fraOgMed`() {
        val actual = FremtidigeInntekter(
            listOf(
                FremtidigInntekt(måned = YearMonth.of(2022, Month.JANUARY), årligInntekt = 12000)
            )
        ).årligInntekt(tilOgMed = 2025)

        val expected = listOf(
            ÅrligInntekt(år = 2022, inntekt = 12000),
            ÅrligInntekt(år = 2023, inntekt = 12000),
            ÅrligInntekt(år = 2024, inntekt = 12000),
            ÅrligInntekt(år = 2025, inntekt = 12000)
        )
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `en fremtidig inntekt avløses av en ny fremtidig inntekt når ny fraOgMed nåes`() {
        val actual = FremtidigeInntekter(
            listOf(
                FremtidigInntekt(måned = YearMonth.of(2022, Month.JANUARY), årligInntekt = 12000),
                FremtidigInntekt(måned = YearMonth.of(2024, Month.MAY), årligInntekt = 24000),
            )
        ).årligInntekt(tilOgMed = 2025)

        val expected = listOf(
            ÅrligInntekt(år = 2022, inntekt = 12000),
            ÅrligInntekt(år = 2023, inntekt = 12000),
            ÅrligInntekt(år = 2024, inntekt = 20000),
            ÅrligInntekt(år = 2025, inntekt = 24000)
        )
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `en fremtidig inntekt avløses av en ny fremtidig inntekt når ny fraOgMed nåes for flere inntekter i samme år`() {
        val actual = FremtidigeInntekter(
            listOf(
                FremtidigInntekt(måned = YearMonth.of(2022, Month.JANUARY), årligInntekt = 12000),
                FremtidigInntekt(måned = YearMonth.of(2022, Month.MAY), årligInntekt = 24000),
                FremtidigInntekt(måned = YearMonth.of(2022, Month.DECEMBER), årligInntekt = 36000),
            )
        ).årligInntekt(tilOgMed = 2022)

        val expected = listOf(
            ÅrligInntekt(år = 2022, inntekt = 21000),
        )
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `den siste fremtidige inntekten benyttes fram til tilOgMed nåes`() {
        val actual = FremtidigeInntekter(
            listOf(
                FremtidigInntekt(måned = YearMonth.of(2022, Month.JANUARY), årligInntekt = 12000),
                FremtidigInntekt(måned = YearMonth.of(2023, Month.MAY), årligInntekt = 24000),
                FremtidigInntekt(måned = YearMonth.of(2024, Month.NOVEMBER), årligInntekt = 36000)
            )
        ).årligInntekt(tilOgMed = 2025)

        val expected = listOf(
            ÅrligInntekt(år = 2022, inntekt = 12000),
            ÅrligInntekt(år = 2023, inntekt = 20000),
            ÅrligInntekt(år = 2024, inntekt = 26000),
            ÅrligInntekt(år = 2025, inntekt = 36000)
        )
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `dersom ny fremtidig fraOgMed er senere enn tilOgMed blir den aldri benyttet`() {
        val actual = FremtidigeInntekter(
            listOf(
                FremtidigInntekt(måned = YearMonth.of(2022, Month.JANUARY), årligInntekt = 12000),
                FremtidigInntekt(måned = YearMonth.of(2030, Month.MAY), årligInntekt = 24000),
            )
        ).årligInntekt(tilOgMed = 2024)

        val expected = listOf(
            ÅrligInntekt(år = 2022, inntekt = 12000),
            ÅrligInntekt(år = 2023, inntekt = 12000),
            ÅrligInntekt(år = 2024, inntekt = 12000),
        )
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `avrunding skjer med ikke så alt for store feil`() {
        val actual = FremtidigeInntekter(
            listOf(
                FremtidigInntekt(måned = YearMonth.of(2022, Month.JANUARY), årligInntekt = 1700),
                FremtidigInntekt(måned = YearMonth.of(2023, Month.JANUARY), årligInntekt = 33977),
                FremtidigInntekt(måned = YearMonth.of(2024, Month.JANUARY), årligInntekt = 10),
                FremtidigInntekt(måned = YearMonth.of(2025, Month.JANUARY), årligInntekt = 797313),
            )
        ).årligInntekt(tilOgMed = 2025)

        val expected = listOf(
            ÅrligInntekt(år = 2022, inntekt = 1700),
            ÅrligInntekt(år = 2023, inntekt = 33977),
            ÅrligInntekt(år = 2024, inntekt = 9), //ubetydlig feil på ubetydelig beløp
            ÅrligInntekt(år = 2025, inntekt = 797313),
        )
        assertThat(actual).isEqualTo(expected)
    }
}