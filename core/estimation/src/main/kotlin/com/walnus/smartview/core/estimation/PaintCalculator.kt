package com.walnus.smartview.core.estimation

import com.walnus.smartview.core.model.Provenance
import com.walnus.smartview.core.model.Surface
import kotlin.math.floor
import kotlin.math.round

/**
 * Deterministic paint quantity estimation.
 *
 * The calculator is a pure function object: the same surfaces and specification always
 * produce the same result, which makes the output testable and reproducible in a
 * quotation. It performs no rounding to "nice" numbers other than pack rounding.
 */
object PaintCalculator {

    private const val EPSILON = 1e-6

    /**
     * Estimates paint for [surfaces].
     *
     * @throws IllegalArgumentException when the survey has no measurable paintable area.
     * Callers should validate the survey first and show the missing dimensions.
     */
    fun estimate(
        surfaces: List<Surface>,
        specification: PaintSpecification = PaintSpecification(),
        includePrimer: Boolean = false,
    ): PaintEstimate {
        val area = SurfaceAreaCalculator.paintableArea(surfaces)
        require(area.contributingSurfaceCount > 0 && area.hasUsableArea) {
            "No measurable paintable area. Add at least one measured wall, ceiling or trim surface."
        }

        val litresPerCoat = area.netAreaSquareMetres / specification.coverageSquareMetresPerLitre
        val totalLitres = litresPerCoat * specification.coats
        val litresWithWastage = totalLitres * (1.0 + specification.wastagePercent / 100.0)

        val paintPackages = packagesFor(litresWithWastage, specification)

        val primerLitres = if (includePrimer) {
            specification.primerCoverageSquareMetresPerLitre?.let { coverage ->
                area.netAreaSquareMetres / coverage *
                    (1.0 + specification.wastagePercent / 100.0)
            }
        } else {
            null
        }
        val primerPackages = primerLitres?.let { packagesFor(it, specification) }.orEmpty()

        return PaintEstimate(
            area = area,
            specification = specification,
            litresPerCoat = round2(litresPerCoat),
            totalLitresRequired = round2(totalLitres),
            litresWithWastage = round2(litresWithWastage),
            paintPackages = paintPackages,
            litresToPurchase = round2(paintPackages.sumOf { it.totalLitres }),
            primerPackages = primerPackages,
            primerLitresToPurchase = primerPackages.takeIf { it.isNotEmpty() }
                ?.let { round2(it.sumOf { pack -> pack.totalLitres }) },
            provenance = estimateProvenance(area),
            assumptions = assumptions(area, specification, primerLitres != null),
        )
    }

    /**
     * Breaks [litres] into retail packs, largest first, always rounding up.
     *
     * Example: 45.4 L with 20/10/4/1 L packs gives 2×20, 1×4, 2×1 (46 L).
     */
    fun packagesFor(litres: Double, specification: PaintSpecification): List<PaintPackage> {
        require(litres.isFinite() && litres > 0.0) { "Litres to purchase must be greater than zero" }

        val packages = mutableListOf<PaintPackage>()
        var remaining = litres

        for (size in specification.packageSizesDescending) {
            val count = floor(remaining / size + EPSILON).toInt()
            if (count > 0) {
                packages += PaintPackage(sizeLitres = size, count = count)
                remaining -= size * count
            }
            if (remaining <= EPSILON) break
        }

        if (remaining > EPSILON) {
            // Anything left over needs at least the smallest available pack.
            val smallest = specification.smallestPackageLitres
            val index = packages.indexOfFirst { it.sizeLitres == smallest }
            if (index >= 0) {
                packages[index] = packages[index].copy(count = packages[index].count + 1)
            } else {
                packages += PaintPackage(sizeLitres = smallest, count = 1)
            }
            packages.sortByDescending { it.sizeLitres }
        }

        return packages
    }

    /**
     * A derived quantity is at best an estimate, so trust is capped at [Provenance.ESTIMATED]
     * and inherited weaknesses (AI suggestions, inferences) are preserved.
     */
    private fun estimateProvenance(area: PaintableArea): Provenance =
        Provenance.weakestOf(area.provenance ?: Provenance.ESTIMATED, Provenance.ESTIMATED)

    private fun assumptions(
        area: PaintableArea,
        specification: PaintSpecification,
        primerIncluded: Boolean,
    ): List<String> = buildList {
        add("Quantities are estimates derived from the survey — not measured paint volumes.")
        add(
            "Coverage assumed at ${round2(specification.coverageSquareMetresPerLitre)} m² per litre " +
                "for one coat; confirm against the product datasheet.",
        )
        add("${specification.coats} coat(s) applied.")
        add(
            "A ${round2(specification.wastagePercent)}% wastage allowance is included for rollers, " +
                "texture and touch-ups.",
        )
        add(
            "Packs rounded up to available sizes: " +
                specification.packageSizesDescending.joinToString(", ") { "${round2(it)} L" } + ".",
        )
        add("${round2(area.deductedOpeningAreaSquareMetres)} m² of doors and windows deducted.")
        if (area.deductionCapped) {
            add("Openings exceeded the measured wall area; the deduction was capped. Re-check the survey.")
        }
        if (area.ignoredSurfaceCount > 0) {
            add("${area.ignoredSurfaceCount} surface(s) ignored: dimensions incomplete or excluded by the user.")
        }
        if (primerIncluded) {
            add("Primer is quoted as a single coat over the net area.")
        } else {
            add("Primer is not included.")
        }
    }

    private fun round2(value: Double): Double = round(value * 100.0) / 100.0
}
