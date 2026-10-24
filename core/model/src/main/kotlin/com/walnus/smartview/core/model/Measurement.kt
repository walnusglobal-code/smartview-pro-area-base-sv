package com.walnus.smartview.core.model

/**
 * A single recorded dimension with its provenance.
 *
 * A [Measurement] records what happened on site. Derived values live in the
 * estimation modules and are never written back as measurements.
 */
data class Measurement(
    val id: MeasurementId,
    /** The recorded value in [unit]. */
    val length: Length,
    /** How the value was obtained. */
    val provenance: Provenance,
    /** When the value was recorded (epoch milliseconds, UTC). */
    val recordedAtEpochMillis: Long,
    /** Optional free-text note from the surveyor. */
    val note: String? = null,
    /** Measurement this value was derived from, when provenance is not MEASURED. */
    val derivedFrom: MeasurementId? = null,
) {
    init {
        require(recordedAtEpochMillis >= 0) { "recordedAtEpochMillis must not be negative" }
        require(derivedFrom == null || derivedFrom != id) { "A measurement cannot derive from itself" }
        if (provenance == Provenance.INFERRED || provenance == Provenance.AI_SUGGESTED) {
            // Inferred or suggested values must state what they were derived from.
            require(derivedFrom != null) {
                "Inferred or AI-suggested measurements must reference a source measurement"
            }
        }
    }

    val metres: Double get() = length.metres
}
