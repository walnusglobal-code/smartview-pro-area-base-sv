package com.walnus.smartview.core.estimation

import com.walnus.smartview.core.model.Provenance

/** A number of identical retail packs. */
data class PaintPackage(
    val sizeLitres: Double,
    val count: Int,
) {
    init {
        require(sizeLitres > 0.0) { "Package size must be greater than zero" }
        require(count > 0) { "Package count must be greater than zero" }
    }

    val totalLitres: Double get() = sizeLitres * count
}

/**
 * Result of a paint estimate.
 *
 * Every field is derived, so [provenance] can never be stronger than
 * [Provenance.ESTIMATED] even when all survey inputs were physically measured.
 */
data class PaintEstimate(
    val area: PaintableArea,
    val specification: PaintSpecification,
    /** Litres for one coat over the net area. */
    val litresPerCoat: Double,
    /** Litres for all coats before wastage. */
    val totalLitresRequired: Double,
    /** Litres after the wastage allowance. */
    val litresWithWastage: Double,
    /** Retail packs to purchase, largest first. */
    val paintPackages: List<PaintPackage>,
    /** Litres that will actually be bought (always >= [litresWithWastage]). */
    val litresToPurchase: Double,
    /** Primer packs, or empty when no primer was quoted. */
    val primerPackages: List<PaintPackage>,
    /** Litres of primer to purchase, or null when no primer was quoted. */
    val primerLitresToPurchase: Double?,
    val provenance: Provenance,
    /** Human-readable assumptions to print alongside the numbers. */
    val assumptions: List<String>,
) {
    val hasEstimate: Boolean get() = area.hasUsableArea
}
