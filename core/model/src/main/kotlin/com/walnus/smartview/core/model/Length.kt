package com.walnus.smartview.core.model

/**
 * Length units supported by the survey workflow.
 *
 * Conversion factors are exact by definition (SI + international yard agreement),
 * so converting does not silently introduce rounding error into a measurement.
 */
enum class LengthUnit(
    val label: String,
    /** Exact size of one unit in metres. */
    val metresPerUnit: Double,
) {
    MILLIMETRE("mm", 0.001),
    CENTIMETRE("cm", 0.01),
    METRE("m", 1.0),
    INCH("in", 0.0254),
    FOOT("ft", 0.3048),
    ;

    /** Converts [value] expressed in this unit to metres. */
    fun toMetres(value: Double): Double = value * metresPerUnit

    /** Converts a length in metres to this unit. */
    fun fromMetres(metres: Double): Double = metres / metresPerUnit

    companion object {
        /** Metric units offered first, as the default field workflow in the target market. */
        val metricOrder: List<LengthUnit> = listOf(MILLIMETRE, CENTIMETRE, METRE)
    }
}

/**
 * A physical length recorded during a survey.
 *
 * The original value and unit are preserved exactly as entered; conversion happens
 * only where a calculation needs metres.
 */
data class Length(
    val value: Double,
    val unit: LengthUnit,
) {
    init {
        require(value.isFinite()) { "Length must be finite" }
        require(value > 0.0) { "Length must be greater than zero" }
    }

    val metres: Double get() = unit.toMetres(value)

    /** Returns this length expressed in [target], preserving exact conversion factors. */
    fun convertedTo(target: LengthUnit): Length = Length(target.fromMetres(metres), target)

    override fun toString(): String = "${formatTrimmed(value)} ${unit.label}"

    private fun formatTrimmed(raw: Double): String =
        if (raw % 1.0 == 0.0) raw.toLong().toString() else raw.toString()
}
