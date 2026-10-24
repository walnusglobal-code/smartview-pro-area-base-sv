package com.walnus.smartview.core.entitlements

/**
 * Time source for entitlement decisions.
 *
 * Injected rather than read from the system directly so expiry, grace and clock-skew
 * rules can be tested deterministically.
 */
fun interface Clock {
    fun nowEpochMillis(): Long
}

/** Production clock. */
object SystemClock : Clock {
    override fun nowEpochMillis(): Long = System.currentTimeMillis()
}

/**
 * Test/preview clock that only moves when told to.
 *
 * Deliberately not wired into the app's production path: entitlement expiry must never
 * be driven by a user-adjustable value.
 */
class ManualClock(private var currentEpochMillis: Long) : Clock {
    override fun nowEpochMillis(): Long = currentEpochMillis

    fun advanceBy(millis: Long) {
        require(millis >= 0) { "A manual clock cannot move backwards" }
        currentEpochMillis += millis
    }

    fun set(epochMillis: Long) {
        currentEpochMillis = epochMillis
    }
}

/** Millisecond helpers used by the entitlement policy. */
object Durations {
    const val MINUTE_MILLIS: Long = 60_000L
    const val HOUR_MILLIS: Long = 60 * MINUTE_MILLIS
    const val DAY_MILLIS: Long = 24 * HOUR_MILLIS
}
