package com.walnus.smartview.core.estimation

import com.walnus.smartview.core.model.Length
import com.walnus.smartview.core.model.LengthUnit
import com.walnus.smartview.core.model.Provenance
import com.walnus.smartview.core.model.Surface
import com.walnus.smartview.core.model.SurfaceExtent
import com.walnus.smartview.core.model.SurfaceId
import com.walnus.smartview.core.model.SurfaceKind
import com.walnus.smartview.core.trade.TradeIds
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

internal fun wall(
    widthMetres: Double,
    heightMetres: Double,
    provenance: Provenance = Provenance.MEASURED,
    include: Boolean = true,
): Surface = Surface.rectangular(
    label = "Wall ${widthMetres}x$heightMetres",
    kind = SurfaceKind.WALL,
    width = Length(widthMetres, LengthUnit.METRE),
    height = Length(heightMetres, LengthUnit.METRE),
    provenance = provenance,
    includeInEstimate = include,
)

internal fun opening(
    kind: SurfaceKind,
    widthMetres: Double,
    heightMetres: Double,
    provenance: Provenance = Provenance.MEASURED,
): Surface = Surface.rectangular(
    label = kind.label,
    kind = kind,
    width = Length(widthMetres, LengthUnit.METRE),
    height = Length(heightMetres, LengthUnit.METRE),
    provenance = provenance,
)

class SurfaceAreaCalculatorTest {

    @Test
    fun subtractsDoorsAndWindowsFromWalls() {
        val area = SurfaceAreaCalculator.paintableArea(
            listOf(
                wall(4.0, 3.0),
                wall(5.0, 3.0),
                opening(SurfaceKind.DOOR, 0.9, 2.1),
                opening(SurfaceKind.WINDOW, 1.2, 1.2),
            ),
        )
        assertEquals(27.0, area.grossAreaSquareMetres, 1e-9)
        assertEquals(3.33, area.deductedOpeningAreaSquareMetres, 1e-9)
        assertEquals(23.67, area.netAreaSquareMetres, 1e-9)
        assertEquals(2, area.contributingSurfaceCount)
        assertEquals(0, area.ignoredSurfaceCount)
        assertFalse(area.deductionCapped)
    }

    @Test
    fun provenanceIsWeakestOfContributors() {
        val area = SurfaceAreaCalculator.paintableArea(
            listOf(
                wall(4.0, 3.0, Provenance.MEASURED),
                opening(SurfaceKind.WINDOW, 1.0, 1.0, Provenance.AI_SUGGESTED),
            ),
        )
        assertEquals(Provenance.AI_SUGGESTED, area.provenance)
    }

    @Test
    fun unmeasuredAndExcludedSurfacesAreReportedAsIgnored() {
        val unmeasured = Surface(
            id = SurfaceId.new(),
            label = "Unmeasured wall",
            kind = SurfaceKind.WALL,
            extent = SurfaceExtent.Unknown,
            provenance = Provenance.MEASURED,
        )
        val area = SurfaceAreaCalculator.paintableArea(
            listOf(wall(2.0, 2.0), unmeasured, wall(3.0, 3.0, include = false)),
        )
        assertEquals(4.0, area.grossAreaSquareMetres, 1e-9)
        assertEquals(2, area.ignoredSurfaceCount)
        assertEquals(1, area.contributingSurfaceCount)
    }

    @Test
    fun emptySurveyHasNoProvenanceAndNoArea() {
        val area = SurfaceAreaCalculator.paintableArea(emptyList())
        assertNull(area.provenance)
        assertEquals(0.0, area.netAreaSquareMetres, 1e-9)
        assertFalse(area.hasUsableArea)
    }

    @Test
    fun oversizedDeductionIsCappedAndFlagged() {
        val area = SurfaceAreaCalculator.paintableArea(
            listOf(wall(2.0, 2.0), opening(SurfaceKind.WINDOW, 10.0, 10.0)),
        )
        assertEquals(0.0, area.netAreaSquareMetres, 1e-9)
        assertTrue(area.deductionCapped)
        assertTrue(area.deductedOpeningAreaSquareMetres > area.grossAreaSquareMetres)
    }

    @Test
    fun trimCountsTowardsPaintableArea() {
        val area = SurfaceAreaCalculator.paintableArea(
            listOf(
                wall(4.0, 3.0),
                Surface.rectangular(
                    label = "Skirting",
                    kind = SurfaceKind.TRIM,
                    width = Length(12.0, LengthUnit.METRE),
                    height = Length(0.1, LengthUnit.METRE),
                    provenance = Provenance.USER_CONFIRMED,
                ),
            ),
        )
        assertEquals(13.2, area.netAreaSquareMetres, 1e-9)
        assertEquals(Provenance.USER_CONFIRMED, area.provenance)
    }
}

class PaintCalculatorTest {

    private val spec = PaintSpecification(
        coats = 2,
        coverageSquareMetresPerLitre = 10.0,
        wastagePercent = 5.0,
    )

    @Test
    fun estimatesLitresAndRetailPacksForASmallRoom() {
        val estimate = PaintCalculator.estimate(
            surfaces = listOf(
                wall(4.0, 3.0),
                wall(5.0, 3.0),
                opening(SurfaceKind.DOOR, 0.9, 2.1),
            ),
            specification = spec,
        )

        assertEquals(25.11, estimate.area.netAreaSquareMetres, 1e-9)
        assertEquals(2.51, estimate.litresPerCoat, 1e-9)
        assertEquals(5.02, estimate.totalLitresRequired, 1e-9)
        assertEquals(5.27, estimate.litresWithWastage, 1e-9)
        assertEquals(6.0, estimate.litresToPurchase, 1e-9)
        assertEquals(listOf(4.0, 1.0), estimate.paintPackages.map { it.sizeLitres })
        assertEquals(listOf(1, 2), estimate.paintPackages.map { it.count })
        assertTrue(estimate.litresToPurchase >= estimate.litresWithWastage)
        assertTrue(estimate.primerPackages.isEmpty())
        assertNull(estimate.primerLitresToPurchase)
    }

    @Test
    fun derivedQuantityIsNeverStrongerThanEstimated() {
        val estimate = PaintCalculator.estimate(listOf(wall(4.0, 3.0)), spec)
        assertEquals(Provenance.ESTIMATED, estimate.provenance)
        assertFalse(estimate.provenance.isPhysicallyVerified)
    }

    @Test
    fun aiSuggestedInputWeakensTheResult() {
        val estimate = PaintCalculator.estimate(
            surfaces = listOf(wall(4.0, 3.0, Provenance.AI_SUGGESTED)),
            specification = spec,
        )
        assertEquals(Provenance.AI_SUGGESTED, estimate.provenance)
    }

    @Test
    fun exactPackMultipleNeedsNoExtraPack() {
        val packages = PaintCalculator.packagesFor(40.0, spec)
        assertEquals(listOf(20.0), packages.map { it.sizeLitres })
        assertEquals(listOf(2), packages.map { it.count })
    }

    @Test
    fun packsAlwaysRoundUp() {
        val packages = PaintCalculator.packagesFor(45.4, spec)
        assertEquals(listOf(20.0, 4.0, 1.0), packages.map { it.sizeLitres })
        assertEquals(listOf(2, 1, 2), packages.map { it.count })
        assertEquals(46.0, packages.sumOf { it.totalLitres }, 1e-9)
    }

    @Test
    fun quantityBelowSmallestPackBuysOnePack() {
        val packages = PaintCalculator.packagesFor(0.4, spec)
        assertEquals(listOf(1.0), packages.map { it.sizeLitres })
        assertEquals(listOf(1), packages.map { it.count })
    }

    @Test
    fun rejectsNonPositiveLitres() {
        assertFailsWith<IllegalArgumentException> { PaintCalculator.packagesFor(0.0, spec) }
    }

    @Test
    fun primerIsIncludedOnlyWhenRequestedAndConfigured() {
        val withoutPrimerConfig = PaintCalculator.estimate(listOf(wall(4.0, 3.0)), spec, includePrimer = true)
        assertTrue(withoutPrimerConfig.primerPackages.isEmpty())

        val withPrimer = spec.copy(primerCoverageSquareMetresPerLitre = 8.0)
        val estimate = PaintCalculator.estimate(listOf(wall(5.0, 3.0)), withPrimer, includePrimer = true)
        // 15 m² / 8 m² per litre × 1.05 = 1.96875 L → two 1 L packs
        assertEquals(2.0, estimate.primerLitresToPurchase!!, 1e-9)
        assertEquals(listOf(1.0), estimate.primerPackages.map { it.sizeLitres })
        assertEquals(listOf(2), estimate.primerPackages.map { it.count })
    }

    @Test
    fun singleCoatWithoutWastageIsExact() {
        val estimate = PaintCalculator.estimate(
            surfaces = listOf(wall(4.0, 5.0)),
            specification = PaintSpecification(coats = 1, coverageSquareMetresPerLitre = 10.0, wastagePercent = 0.0),
        )
        assertEquals(2.0, estimate.litresPerCoat, 1e-9)
        assertEquals(2.0, estimate.litresToPurchase, 1e-9)
    }

    @Test
    fun rejectsSurveyWithoutMeasurableArea() {
        val exception = assertFailsWith<IllegalArgumentException> {
            PaintCalculator.estimate(emptyList(), spec)
        }
        assertTrue(exception.message!!.contains("measurable", ignoreCase = true))
    }

    @Test
    fun assumptionsExplainEveryEstimate() {
        val estimate = PaintCalculator.estimate(listOf(wall(4.0, 3.0)), spec)
        assertTrue(estimate.assumptions.size >= 5)
        assertTrue(estimate.assumptions.first().contains("estimate", ignoreCase = true))
        assertTrue(estimate.assumptions.any { it.contains("m² per litre") })
    }

    @Test
    fun specificationValidatesItsAssumptions() {
        assertFailsWith<IllegalArgumentException> { PaintSpecification(coats = 0) }
        assertFailsWith<IllegalArgumentException> { PaintSpecification(coats = 7) }
        assertFailsWith<IllegalArgumentException> { PaintSpecification(coverageSquareMetresPerLitre = 0.0) }
        assertFailsWith<IllegalArgumentException> { PaintSpecification(wastagePercent = -1.0) }
        assertFailsWith<IllegalArgumentException> { PaintSpecification(packageSizesLitres = emptyList()) }
        assertFailsWith<IllegalArgumentException> {
            PaintSpecification(packageSizesLitres = listOf(20.0, 20.0))
        }
    }

    @Test
    fun sameInputsAlwaysProduceSameEstimate() {
        val surfaces = listOf(wall(4.0, 3.0), opening(SurfaceKind.WINDOW, 1.0, 1.0))
        assertEquals(
            PaintCalculator.estimate(surfaces, spec),
            PaintCalculator.estimate(surfaces, spec),
        )
    }
}

class PaintingTradeTest {

    @Test
    fun paintingAdvertisesACapabilityTheShellCanList() {
        assertEquals(TradeIds.PAINTING, PaintingTrade.id)
        assertEquals("Painting", PaintingTrade.displayName)
        assertTrue(PaintingTrade.summary.isNotBlank())
    }

    @Test
    fun paintingRequiresTheCoreEstimationFeature() {
        assertEquals("paint_estimation", PaintingTrade.requiredFeature?.value)
    }
}
