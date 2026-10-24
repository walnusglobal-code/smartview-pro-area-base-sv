package com.walnus.smartview.core.quotation

import java.math.BigDecimal
import java.math.RoundingMode

/**
 * An amount in the smallest unit of a currency (kobo, cents, ...).
 *
 * Integer minor units avoid the classic floating-point money bug where two lines of
 * ₦0.10 add up to ₦0.20 but total ₦0.19999999999999998.
 */
@JvmInline
value class Money(val minorUnits: Long) {

    operator fun plus(other: Money): Money = Money(minorUnits + other.minorUnits)

    operator fun minus(other: Money): Money = Money(minorUnits - other.minorUnits)

    /** Multiplies by [factor] and rounds half-up to whole minor units. */
    fun times(factor: Double): Money = Money(roundToMinorUnits(factor * minorUnits.toDouble()))

    override fun toString(): String = "$minorUnits minor units"

    companion object {
        val ZERO: Money = Money(0)

        /** Rounds a fractional minor-unit amount half-up, matching invoice conventions. */
        fun roundToMinorUnits(value: Double): Long {
            require(value.isFinite()) { "Money value must be finite" }
            return BigDecimal(value).setScale(0, RoundingMode.HALF_UP).toLong()
        }
    }
}

/** ISO 4217 alpha-3 currency code, validated at construction. */
@JvmInline
value class CurrencyCode(val value: String) {
    init {
        require(PATTERN.matches(value)) { "Currency code must be an ISO 4217 alpha-3 value" }
    }

    override fun toString(): String = value

    private companion object {
        val PATTERN = Regex("^[A-Z]{3}$")
    }
}
