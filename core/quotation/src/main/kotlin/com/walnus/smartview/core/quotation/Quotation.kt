package com.walnus.smartview.core.quotation

/**
 * One priced line on a quotation.
 *
 * [unitPrice] is a commercial input from the user; the application never invents prices.
 */
data class QuoteLine(
    val description: String,
    val quantity: Double,
    val unitLabel: String,
    val unitPrice: Money,
    val taxable: Boolean = true,
) {
    init {
        require(description.isNotBlank()) { "Line description must not be blank" }
        require(unitLabel.isNotBlank()) { "Line unit must not be blank" }
        require(quantity.isFinite() && quantity > 0.0) { "Line quantity must be greater than zero" }
        require(unitPrice.minorUnits >= 0) { "Unit price must not be negative" }
    }

    /** quantity × unit price, rounded to whole minor units. */
    val lineTotal: Money get() = unitPrice.times(quantity)
}

/**
 * A quotation: priced lines, an optional discount and an optional tax rate.
 *
 * All figures are computed from the inputs; nothing is cached, so a quotation cannot
 * drift out of sync with its lines. No tax rate is assumed — [TAX_RATE_BASIS_POINTS_NONE]
 * means the user has not added tax.
 */
data class Quotation(
    val currency: CurrencyCode,
    val lines: List<QuoteLine>,
    val discount: Money = Money.ZERO,
    /** Tax rate in basis points (750 = 7.5%, 1500 = 15%). */
    val taxRateBasisPoints: Int = TAX_RATE_BASIS_POINTS_NONE,
) {
    init {
        require(discount.minorUnits >= 0) { "Discount must not be negative" }
        require(taxRateBasisPoints in 0..MAX_TAX_RATE_BASIS_POINTS) {
            "taxRateBasisPoints must be between 0 and $MAX_TAX_RATE_BASIS_POINTS"
        }
    }

    /** Sum of all line totals before discount and tax. */
    val subtotal: Money
        get() = lines.fold(Money.ZERO) { total, line -> total + line.lineTotal }

    /** Discount actually applied; cannot exceed the subtotal. */
    val discountApplied: Money
        get() = if (discount.minorUnits > subtotal.minorUnits) subtotal else discount

    /** Portion of the discount attributable to taxable lines, rounded half-up. */
    private val taxableDiscount: Money
        get() {
            val taxableSubtotal = taxableSubtotalMinorUnits
            val subtotalMinor = subtotal.minorUnits
            if (subtotalMinor == 0L || taxableSubtotal == 0L) return Money.ZERO
            val share = taxableSubtotal.toDouble() / subtotalMinor.toDouble()
            return Money(Money.roundToMinorUnits(discountApplied.minorUnits * share))
        }

    private val taxableSubtotalMinorUnits: Long
        get() = lines.filter { it.taxable }.fold(0L) { total, line -> total + line.lineTotal.minorUnits }

    /** Base that tax is calculated on. */
    val taxableBase: Money
        get() = Money((taxableSubtotalMinorUnits - taxableDiscount.minorUnits).coerceAtLeast(0))

    /** Tax due at the configured rate. */
    val tax: Money
        get() = Money(
            Money.roundToMinorUnits(
                taxableBase.minorUnits.toDouble() * taxRateBasisPoints.toDouble() / BASIS_POINTS_DIVISOR,
            ),
        )

    /** Amount payable. */
    val total: Money get() = subtotal - discountApplied + tax

    companion object {
        const val TAX_RATE_BASIS_POINTS_NONE: Int = 0
        const val MAX_TAX_RATE_BASIS_POINTS: Int = 10_000
        private const val BASIS_POINTS_DIVISOR: Double = 10_000.0
    }
}
