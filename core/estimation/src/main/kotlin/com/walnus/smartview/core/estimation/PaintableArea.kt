package com.walnus.smartview.core.estimation

import com.walnus.smartview.core.model.Provenance
import com.walnus.smartview.core.model.Surface

/**
 * Paintable area derived from survey surfaces.
 *
 * [grossAreaSquareMetres] counts every included, measurable surface; openings are then
 * deducted to reach [netAreaSquareMetres].
 *
 * [provenance] is the weakest provenance among the surfaces that contributed, or null
 * when nothing measurable contributed. It is never stronger than the inputs.
 */
data class PaintableArea(
    val grossAreaSquareMetres: Double,
    val deductedOpeningAreaSquareMetres: Double,
    val netAreaSquareMetres: Double,
    val contributingSurfaceCount: Int,
    val ignoredSurfaceCount: Int,
    val provenance: Provenance?,
    /** True when openings exceeded the gross area and the deduction was capped at 100%. */
    val deductionCapped: Boolean,
) {
    val hasUsableArea: Boolean get() = netAreaSquareMetres > 0.0
}

/**
 * Pure function object that turns survey surfaces into a paintable area.
 *
 * Kept separate from quantity calculation so plastering and screeding can reuse area
 * handling and so each rule is tested on its own.
 */
object SurfaceAreaCalculator {

    fun paintableArea(surfaces: List<Surface>): PaintableArea {
        val measurable = surfaces.filter { it.isMeasurable && it.includeInEstimate }

        val contributing = measurable.filter { it.countsTowardsArea }
        val openings = measurable.filter { it.isOpening }

        val gross = contributing.sumOf { it.areaSquareMetres ?: 0.0 }
        val openingTotal = openings.sumOf { it.areaSquareMetres ?: 0.0 }

        val net = (gross - openingTotal).coerceAtLeast(0.0)
        val capped = openingTotal > gross

        val provenances = contributing.map { it.provenance } + openings.map { it.provenance }
        val ignored = surfaces.size - measurable.size

        return PaintableArea(
            grossAreaSquareMetres = gross,
            deductedOpeningAreaSquareMetres = openingTotal,
            netAreaSquareMetres = net,
            contributingSurfaceCount = contributing.size,
            ignoredSurfaceCount = ignored,
            provenance = if (provenances.isEmpty()) null else Provenance.weakestOf(provenances),
            deductionCapped = capped,
        )
    }
}
