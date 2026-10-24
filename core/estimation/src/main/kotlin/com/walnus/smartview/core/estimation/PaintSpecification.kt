package com.walnus.smartview.core.estimation

/**
 * The assumptions a paint estimate is built on.
 *
 * Coverage and wastage are site/product assumptions, not measurements. Defaults are
 * common practice for interior emulsion and must be edited by the user or replaced with
 * a manufacturer's datasheet before a quotation is issued.
 */
data class PaintSpecification(
    /** Number of coats to apply. */
    val coats: Int = DEFAULT_COATS,
    /** Square metres covered by one litre for a single coat. */
    val coverageSquareMetresPerLitre: Double = DEFAULT_COVERAGE,
    /** Extra allowance for rollers, spray loss, texture and touch-ups, as a percentage. */
    val wastagePercent: Double = DEFAULT_WASTAGE_PERCENT,
    /** Primer coverage per litre, or null when no primer is quoted. */
    val primerCoverageSquareMetresPerLitre: Double? = null,
    /** Retail pack sizes available, in litres. Order does not matter. */
    val packageSizesLitres: List<Double> = DEFAULT_PACKAGE_SIZES,
) {
    init {
        require(coats in 1..MAX_COATS) { "coats must be between 1 and $MAX_COATS" }
        require(coverageSquareMetresPerLitre.isFinite() && coverageSquareMetresPerLitre > 0.0) {
            "coverageSquareMetresPerLitre must be greater than zero"
        }
        require(wastagePercent.isFinite() && wastagePercent >= 0.0 && wastagePercent <= 50.0) {
            "wastagePercent must be between 0 and 50"
        }
        require(
            primerCoverageSquareMetresPerLitre == null ||
                (primerCoverageSquareMetresPerLitre.isFinite() &&
                    primerCoverageSquareMetresPerLitre > 0.0),
        ) { "primerCoverageSquareMetresPerLitre must be greater than zero when set" }

        require(packageSizesLitres.isNotEmpty()) { "At least one package size is required" }
        require(packageSizesLitres.all { it.isFinite() && it > 0.0 }) {
            "Package sizes must all be greater than zero"
        }
        require(packageSizesLitres.toSet().size == packageSizesLitres.size) {
            "Package sizes must be distinct"
        }
    }

    /** Package sizes in descending order, ready for the purchase-breakdown algorithm. */
    val packageSizesDescending: List<Double> get() = packageSizesLitres.sortedDescending()

    /** The smallest pack that can be bought. */
    val smallestPackageLitres: Double get() = packageSizesLitres.min()

    companion object {
        const val DEFAULT_COATS: Int = 2

        /**
         * Typical interior emulsion coverage.
         * An assumption for planning; manufacturers' datasheets take precedence.
         */
        const val DEFAULT_COVERAGE: Double = 10.0

        /** Typical site allowance for loss and touch-ups. */
        const val DEFAULT_WASTAGE_PERCENT: Double = 5.0

        /** Common Nigerian retail pack sizes. Ordering is irrelevant. */
        val DEFAULT_PACKAGE_SIZES: List<Double> = listOf(20.0, 10.0, 4.0, 1.0)

        const val MAX_COATS: Int = 6
    }
}
