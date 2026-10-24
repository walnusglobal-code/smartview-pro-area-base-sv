package com.walnus.smartview.core.model

/**
 * Where a value came from, and therefore how much trust it carries.
 *
 * SmartView Pro must never present a derived or suggested value as a physically
 * verified measurement. Every measurement, area or quantity carries one of these
 * labels through the domain layer so the UI and reports can show it honestly.
 *
 * Declaration order encodes strength: [MEASURED] is strongest, [AI_SUGGESTED] weakest.
 * Use [weakestOf] when combining values.
 */
enum class Provenance(
    /** Short, user-facing label. */
    val label: String,
    /** True only for values produced by a physical measurement or an explicit user confirmation. */
    val isPhysicallyVerified: Boolean,
) {
    /** Taken with a tape measure or other physical instrument on site. */
    MEASURED("Measured", true),

    /** Entered or corrected by the user, who takes responsibility for the value. */
    USER_CONFIRMED("User confirmed", true),

    /** Derived from other values by a documented rule (for example room perimeter from walls). */
    INFERRED("Inferred", false),

    /** A planning figure based on assumptions such as coverage rates. */
    ESTIMATED("Estimated", false),

    /** Produced by an AI service. Never treated as factual until a human confirms it. */
    AI_SUGGESTED("AI suggested", false),
    ;

    /** Position in the strength ordering; higher is stronger. */
    val strength: Int get() = ordinalStrength(ordinal)

    companion object {
        private fun ordinalStrength(ordinal: Int): Int = entries.size - ordinal

        /** Returns the weakest (least trustworthy) provenance in [values]. */
        fun weakestOf(vararg values: Provenance): Provenance {
            require(values.isNotEmpty()) { "At least one provenance is required" }
            return values.minBy { it.strength }
        }

        /** Returns the weakest (least trustworthy) provenance in [values]. */
        fun weakestOf(values: Iterable<Provenance>): Provenance {
            val list = values.toList()
            require(list.isNotEmpty()) { "At least one provenance is required" }
            return list.minBy { it.strength }
        }
    }
}

/**
 * A value together with its provenance, so trust cannot be lost by accident.
 *
 * Used for quantities that are computed rather than directly entered.
 */
data class ProvValue<T : Any>(
    val value: T,
    val provenance: Provenance,
) {
    /** Maps the value while preserving provenance. */
    fun <R : Any> map(transform: (T) -> R): ProvValue<R> = ProvValue(transform(value), provenance)

    /** Combines with another provenance-tagged value, keeping the weaker trust level. */
    fun <R : Any> combine(other: ProvValue<*>, transform: (T, Any?) -> R): ProvValue<R> =
        ProvValue(transform(value, other.value), Provenance.weakestOf(provenance, other.provenance))
}
