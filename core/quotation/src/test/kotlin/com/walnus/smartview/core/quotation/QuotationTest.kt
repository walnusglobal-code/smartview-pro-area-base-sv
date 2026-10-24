package com.walnus.smartview.core.quotation

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class MoneyTest {

    @Test
    fun roundsHalfUpToWholeMinorUnits() {
        assertEquals(1, Money.roundToMinorUnits(0.5))
        assertEquals(3, Money.roundToMinorUnits(2.5))
        assertEquals(0, Money.roundToMinorUnits(0.4999))
        assertEquals(-1, Money.roundToMinorUnits(-0.5))
    }

    @Test
    fun multiplicationRoundsLineValues() {
        assertEquals(Money(500), Money(333).times(1.5))
        assertEquals(Money.ZERO, Money(100).times(0.0))
    }

    @Test
    fun additionAndSubtractionAreExactInMinorUnits() {
        val cents = Money(10) + Money(20)
        assertEquals(Money(30), cents)
        assertEquals(Money(10), cents - Money(20))
    }

    @Test
    fun currencyCodeIsValidated() {
        assertEquals("NGN", CurrencyCode("NGN").value)
        assertFailsWith<IllegalArgumentException> { CurrencyCode("Naira") }
        assertFailsWith<IllegalArgumentException> { CurrencyCode("NG") }
    }
}

class QuotationTest {

    private val ngn = CurrencyCode("NGN")

    private val paintLine = QuoteLine(
        description = "Emulsion paint, 20 L",
        quantity = 2.0,
        unitLabel = "pack",
        unitPrice = Money(4_500_000),
    )

    private val labourLine = QuoteLine(
        description = "Painting labour",
        quantity = 1.0,
        unitLabel = "job",
        unitPrice = Money(15_000_000),
    )

    @Test
    fun subtotalAddsLineTotals() {
        val quotation = Quotation(ngn, listOf(paintLine, labourLine))
        assertEquals(Money(24_000_000), quotation.subtotal)
        assertEquals(Money.ZERO, quotation.tax)
        assertEquals(Money(24_000_000), quotation.total)
    }

    @Test
    fun taxIsAppliedAtTheConfiguredRate() {
        val quotation = Quotation(ngn, listOf(paintLine, labourLine), taxRateBasisPoints = 750)
        assertEquals(Money(1_800_000), quotation.tax)
        assertEquals(Money(25_800_000), quotation.total)
    }

    @Test
    fun discountReducesTheTaxableBase() {
        val quotation = Quotation(
            currency = ngn,
            lines = listOf(paintLine, labourLine),
            discount = Money(1_000_000),
            taxRateBasisPoints = 750,
        )
        assertEquals(Money(23_000_000), quotation.taxableBase)
        assertEquals(Money(1_725_000), quotation.tax)
        assertEquals(Money(24_725_000), quotation.total)
    }

    @Test
    fun discountCannotExceedSubtotal() {
        val quotation = Quotation(ngn, listOf(paintLine), discount = Money(90_000_000))
        assertEquals(Money(9_000_000), quotation.discountApplied)
        assertEquals(Money.ZERO, quotation.taxableBase)
        assertEquals(Money.ZERO, quotation.total)
    }

    @Test
    fun discountIsSharedAcrossTaxableAndNonTaxableLines() {
        val taxable = QuoteLine("Paint", 1.0, "job", Money(10_000_000))
        val exempt = QuoteLine("Scaffold hire", 1.0, "job", Money(10_000_000), taxable = false)
        val quotation = Quotation(
            currency = ngn,
            lines = listOf(taxable, exempt),
            discount = Money(2_000_000),
            taxRateBasisPoints = 750,
        )
        assertEquals(Money(9_000_000), quotation.taxableBase)
        assertEquals(Money(675_000), quotation.tax)
        assertEquals(Money(18_675_000), quotation.total)
    }

    @Test
    fun emptyQuotationIsZeroRatherThanFailing() {
        val quotation = Quotation(ngn, emptyList(), taxRateBasisPoints = 750)
        assertEquals(Money.ZERO, quotation.subtotal)
        assertEquals(Money.ZERO, quotation.tax)
        assertEquals(Money.ZERO, quotation.total)
    }

    @Test
    fun invalidLineInputsAreRejected() {
        assertFailsWith<IllegalArgumentException> { QuoteLine("", 1.0, "job", Money(1)) }
        assertFailsWith<IllegalArgumentException> { QuoteLine("Paint", 0.0, "job", Money(1)) }
        assertFailsWith<IllegalArgumentException> { QuoteLine("Paint", 1.0, " ", Money(1)) }
        assertFailsWith<IllegalArgumentException> { QuoteLine("Paint", 1.0, "job", Money(-1)) }
    }

    @Test
    fun invalidQuotationInputsAreRejected() {
        assertFailsWith<IllegalArgumentException> {
            Quotation(ngn, listOf(paintLine), discount = Money(-1))
        }
        assertFailsWith<IllegalArgumentException> {
            Quotation(ngn, listOf(paintLine), taxRateBasisPoints = -1)
        }
        assertFailsWith<IllegalArgumentException> {
            Quotation(ngn, listOf(paintLine), taxRateBasisPoints = 10_001)
        }
    }
}
