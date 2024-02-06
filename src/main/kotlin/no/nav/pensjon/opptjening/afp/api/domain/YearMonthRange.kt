package no.nav.pensjon.opptjening.afp.api.domain

import java.time.YearMonth

operator fun YearMonth.rangeTo(other: YearMonth) = YearMonthProgression(this, other)